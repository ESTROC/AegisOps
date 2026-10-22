package dev.aegisops.incidents.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aegisops.incidents.api.*;
import dev.aegisops.incidents.domain.*;
import dev.aegisops.incidents.repository.*;
import jakarta.persistence.OptimisticLockException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class TicketService {
 private final TicketRepository tickets;private final AuditRepository audits;private final CommentRepository comments;private final OutboxRepository outbox;private final ObjectMapper mapper;
 public TicketService(TicketRepository tickets,AuditRepository audits,CommentRepository comments,OutboxRepository outbox,ObjectMapper mapper){this.tickets=tickets;this.audits=audits;this.comments=comments;this.outbox=outbox;this.mapper=mapper;}
 @Transactional
 public TicketResponse create(TicketRequests.Create r,String actor){
  Ticket t=tickets.save(new Ticket(r.type(),r.title().trim(),r.description().trim(),r.serviceName().trim(),r.priority(),actor,Instant.now()));
  audits.save(new AuditEntry(t.getId(),actor,"CREATE","Opened "+t.getType()+" with priority "+t.getPriority()));
  enqueue(t,"ticket.created",actor);return TicketResponse.from(t);
 }
 @Transactional(readOnly=true)
 public TicketResponse get(Long id){return TicketResponse.from(load(id));}
 @Transactional(readOnly=true)
 public Page<TicketResponse> list(TicketStatus status,Priority priority,TicketType type,String query,int page,int size){
  Specification<Ticket> spec=(root,q,cb)->cb.conjunction();
  if(status!=null)spec=spec.and((root,q,cb)->cb.equal(root.get("status"),status));
  if(priority!=null)spec=spec.and((root,q,cb)->cb.equal(root.get("priority"),priority));
  if(type!=null)spec=spec.and((root,q,cb)->cb.equal(root.get("type"),type));
  if(query!=null&&!query.isBlank()){
   String term="%"+query.trim().toLowerCase()+"%";
   spec=spec.and((root,q,cb)->cb.or(cb.like(cb.lower(root.get("title")),term),cb.like(cb.lower(root.get("serviceName")),term)));
  }
  return tickets.findAll(spec,PageRequest.of(Math.max(0,page),Math.min(100,Math.max(1,size)),Sort.by(Sort.Direction.DESC,"createdAt"))).map(TicketResponse::from);
 }
 @Transactional
 public TicketResponse transition(Long id,TicketRequests.Transition r,String actor){
  Ticket t=load(id);
  if(t.getVersion()!=r.version())throw new ResponseStatusException(HttpStatus.CONFLICT,"Ticket changed; reload and retry");
  TicketStatus old=t.getStatus();
  try{t.transition(r.status(),r.assignee(),Instant.now());}catch(IllegalArgumentException ex){throw new ResponseStatusException(HttpStatus.CONFLICT,ex.getMessage());}
  audits.save(new AuditEntry(id,actor,"STATUS_CHANGE",old+" -> "+r.status()));
  tickets.saveAndFlush(t);enqueue(t,"ticket.status_changed",actor);return TicketResponse.from(t);
 }
 @Transactional
 public TicketComment comment(Long id,TicketRequests.Comment req,String actor){
  load(id);TicketComment c=comments.save(new TicketComment(id,actor,req.message().trim()));
  audits.save(new AuditEntry(id,actor,"COMMENT","Comment added"));return c;
 }
 @Transactional(readOnly=true)
 public List<TicketComment> comments(Long id){load(id);return comments.findByTicketIdOrderByCreatedAtAsc(id);}
 @Transactional(readOnly=true)
 public List<AuditEntry> audit(Long id){load(id);return audits.findByTicketIdOrderByCreatedAtDesc(id);}
 private Ticket load(Long id){return tickets.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Ticket not found"));}
 private void enqueue(Ticket t,String routingKey,String actor){
  try{String json=mapper.writeValueAsString(Map.of("ticketId",t.getId(),"type",t.getType().name(),"priority",t.getPriority().name(),"status",t.getStatus().name(),"actor",actor,"eventTime",Instant.now().toString()));
  outbox.save(new OutboxEvent(routingKey,json));}catch(JsonProcessingException ex){throw new IllegalStateException("Cannot serialize event",ex);}
 }
}

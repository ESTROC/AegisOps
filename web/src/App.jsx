import React,{useCallback,useEffect,useMemo,useState} from 'react';
import {Activity,AlertTriangle,ArrowRight,BarChart3,Check,ChevronDown,Clock,Command,Filter,Layers,LogOut,Plus,RefreshCw,Search,Shield,ShieldCheck,Terminal,Ticket,Wifi} from 'lucide-react';
const PRIORITIES=['P1','P2','P3','P4'],TYPES=['INCIDENT','PROBLEM','CHANGE'],STATUSES=['OPEN','INVESTIGATING','RESOLVED','CLOSED'];
const nowFormat=v=>v?new Date(v).toLocaleString(undefined,{dateStyle:'medium',timeStyle:'short'}):'—';
function request(path,token,options={}){
 return fetch('/api'+path,{...options,headers:{'Content-Type':'application/json',...(token?{Authorization:`Bearer ${token}`}:{ }),...(options.headers||{})}}).then(async r=>{
  const text=await r.text();let data;try{data=text?JSON.parse(text):null}catch{data=text}
  if(!r.ok)throw new Error(data?.message||data?.detail||data?.error||`${r.status} ${r.statusText}`);return data;
 });
}
function SignIn({onLogin}){
 const [username,setUsername]=useState('admin'),[password,setPassword]=useState(''),[busy,setBusy]=useState(false),[error,setError]=useState('');
 async function submit(e){e.preventDefault();setBusy(true);setError('');try{onLogin(await request('/auth/login',null,{method:'POST',body:JSON.stringify({username,password})}));}catch(e){setError(e.message)}finally{setBusy(false)}}
 return <div className="login-page"><div className="login-brand"><span className="logo-mark"><ShieldCheck size={20}/></span> AEGIS<span>OPS</span></div>
  <div className="login-card"><div className="intro-icon"><Shield size={26}/></div><span className="eyebrow">SECURE ACCESS PORTAL</span><h1>Operations, in control.</h1><p>Sign in to access incident, problem and change workflows.</p>
  <form onSubmit={submit}><label>USERNAME<input value={username} autoComplete="username" onChange={e=>setUsername(e.target.value)} required/></label>
  <label>PASSWORD<input type="password" value={password} autoComplete="current-password" onChange={e=>setPassword(e.target.value)} required/></label>
  {error&&<div role="alert" className="error">{error}</div>}<button className="primary" disabled={busy}>{busy?'Authenticating...':'Sign in securely'}<ArrowRight size={17}/></button></form>
  <small>Independent engineering portfolio demo · No institutional affiliation</small></div>
  <footer>JAVA 21 · SPRING BOOT · OAUTH2 JWT · MYSQL · RABBITMQ</footer></div>
}
function Sidebar({tab,setTab,user,signout}){
 return <aside className="sidebar"><div className="brand"><span className="logo-mark"><ShieldCheck size={21}/></span> AEGIS<span>OPS</span><small>OPERATIONS CLOUD</small></div>
 <div className="side-label">WORKSPACE</div>
 <button className={'nav '+(tab==='tickets'?'active':'')} onClick={()=>setTab('tickets')}><Layers size={18}/> Tickets <span className="nav-pill">LIVE</span></button>
 <button className={'nav '+(tab==='overview'?'active':'')} onClick={()=>setTab('overview')}><BarChart3 size={18}/> Overview</button>
 <div className="side-spacer"/><div className="system-box"><span className="system-dot"/> Infrastructure demo <small>Containerized microservices</small></div>
 <div className="profile"><div className="avatar">{(user.username||'A')[0].toUpperCase()}</div><div><strong>{user.username}</strong><small>{user.role} ACCOUNT</small></div><button title="Sign out" onClick={signout}><LogOut size={17}/></button></div>
 </aside>
}
function Chip({status,priority}){return <span className={'chip '+(status?'status-'+status.toLowerCase():'priority-'+priority.toLowerCase())}>{status||priority}</span>}
function TicketModal({onClose,onSave,busy}){
 const [draft,setDraft]=useState({type:'INCIDENT',priority:'P2',title:'',description:'',serviceName:''});
 const [error,setError]=useState('');
 function change(k,value){setDraft(x=>({...x,[k]:value}));}
 async function save(e){e.preventDefault();setError('');try{await onSave(draft)}catch(e){setError(e.message)}}
 return <div className="scrim" onMouseDown={e=>{if(e.target===e.currentTarget)onClose()}}><section className="modal" role="dialog" aria-modal="true" aria-label="Create ticket">
 <header><div><span className="eyebrow">SERVICE MANAGEMENT</span><h2>Create new ticket</h2></div><button className="ghost" onClick={onClose}>✕</button></header>
 <form onSubmit={save}><div className="two-col"><label>TYPE<select value={draft.type} onChange={e=>change('type',e.target.value)}>{TYPES.map(v=><option key={v}>{v}</option>)}</select></label>
 <label>PRIORITY<select value={draft.priority} onChange={e=>change('priority',e.target.value)}>{PRIORITIES.map(v=><option key={v}>{v}</option>)}</select></label></div>
 <label>TITLE<input required maxLength={180} placeholder="API gateway response degradation" value={draft.title} onChange={e=>change('title',e.target.value)}/></label>
 <label>SERVICE / SYSTEM<input required maxLength={120} placeholder="payments-api" value={draft.serviceName} onChange={e=>change('serviceName',e.target.value)}/></label>
 <label>DESCRIPTION<textarea required maxLength={5000} rows={4} placeholder="What happened, user impact and initial findings..." value={draft.description} onChange={e=>change('description',e.target.value)}/></label>
 {error&&<p className="error">{error}</p>}<div className="actions"><button type="button" className="secondary" onClick={onClose}>Cancel</button><button className="primary" disabled={busy}>{busy?'Creating...':'Create ticket'}<Plus size={16}/></button></div></form></section></div>
}
function Details({ticket,onClose,onUpdate,token}){
 const [status,setStatus]=useState(ticket.status),[assignee,setAssignee]=useState(ticket.assignee||''),[comments,setComments]=useState([]),[audit,setAudit]=useState([]),[msg,setMsg]=useState(''),[error,setError]=useState(''),[loading,setLoading]=useState(false);
 const refresh=useCallback(()=>{Promise.all([request(`/tickets/${ticket.id}/comments`,token),request(`/tickets/${ticket.id}/audit`,token)]).then(([c,a])=>{setComments(c);setAudit(a)}).catch(e=>setError(e.message))},[ticket.id,token]);
 useEffect(()=>{refresh()},[refresh]);
 async function transition(){setLoading(true);setError('');try{await onUpdate(ticket.id,{status,assignee,version:ticket.version});onClose()}catch(e){setError(e.message)}finally{setLoading(false)}}
 async function addComment(e){e.preventDefault();setLoading(true);try{await request(`/tickets/${ticket.id}/comments`,token,{method:'POST',body:JSON.stringify({message:msg})});setMsg('');refresh()}catch(e){setError(e.message)}finally{setLoading(false)}}
 return <div className="scrim details-scrim" onMouseDown={e=>{if(e.target===e.currentTarget)onClose()}}><section className="details" role="dialog" aria-modal="true" aria-label="Ticket details">
 <header><div><span className="eyebrow">TICKET #{String(ticket.id).padStart(6,'0')}</span><h2>{ticket.title}</h2></div><button className="ghost" onClick={onClose}>✕</button></header>
 <div className="detail-tags"><Chip status={ticket.status}/><Chip priority={ticket.priority}/><span className="pill-muted">{ticket.type}</span></div>
 <div className="detail-grid"><div><small>SERVICE</small><strong>{ticket.serviceName}</strong></div><div><small>CREATED BY</small><strong>{ticket.createdBy}</strong></div><div><small>CREATED</small><strong>{nowFormat(ticket.createdAt)}</strong></div><div><small>SLA DEADLINE</small><strong className={ticket.overdue?'danger-text':''}>{nowFormat(ticket.slaDeadline)}</strong></div></div>
 <h3>Description</h3><p className="desc-text">{ticket.description}</p><h3>Workflow update</h3>
 <div className="two-col"><label>STATUS<select value={status} onChange={e=>setStatus(e.target.value)}>{STATUSES.map(v=><option key={v}>{v}</option>)}</select></label><label>ASSIGNEE<input value={assignee} onChange={e=>setAssignee(e.target.value)} placeholder="Engineer name"/></label></div>
 <button className="primary" onClick={transition} disabled={loading||status===ticket.status}>Update status</button>
 {error&&<p className="error">{error}</p>}
 <h3>Comments</h3><div className="notes">{comments.length?comments.map(c=><div className="note" key={c.id}><strong>{c.author}</strong><small>{nowFormat(c.createdAt)}</small><p>{c.message}</p></div>):<p className="muted">No comments yet.</p>}</div>
 <form className="comment-form" onSubmit={addComment}><input required value={msg} onChange={e=>setMsg(e.target.value)} placeholder="Add investigation note..."/><button disabled={loading}>Add</button></form>
 <h3>Immutable-style audit history</h3><div className="audit">{audit.map(a=><div key={a.id}><span>•</span><strong>{a.action}</strong><small>{a.actor} · {nowFormat(a.createdAt)}</small><p>{a.detail}</p></div>)}</div>
 </section></div>
}
export default function App(){
 const [session,setSession]=useState(()=>{try{const s=sessionStorage.getItem('aegisops-session');return s?JSON.parse(s):null}catch{return null}});
 const [tab,setTab]=useState('tickets'),[tickets,setTickets]=useState([]),[total,setTotal]=useState(0),[page,setPage]=useState(0),[search,setSearch]=useState(''),[filter,setFilter]=useState(''),[loading,setLoading]=useState(false),[error,setError]=useState(''),[showCreate,setShowCreate]=useState(false),[selected,setSelected]=useState(null),[saving,setSaving]=useState(false);
 const token=session?.accessToken;
 const load=useCallback(async()=>{
  if(!token)return;setLoading(true);setError('');try{let url=`/tickets?size=50&page=${page}`;
   if(search)url+='&q='+encodeURIComponent(search);if(filter)url+='&status='+encodeURIComponent(filter);
   const data=await request(url,token);setTickets(data.content||[]);setTotal(data.totalElements||0);
  }catch(e){setError(e.message);if(/401|expired/i.test(e.message)){sessionStorage.removeItem('aegisops-session');setSession(null)}}finally{setLoading(false)};
 },[token,search,filter,page]);
 useEffect(()=>{load()},[load]);
 const stats=useMemo(()=>({open:tickets.filter(t=>t.status==='OPEN').length,active:tickets.filter(t=>t.status==='INVESTIGATING').length,overdue:tickets.filter(t=>t.overdue).length,resolved:tickets.filter(t=>['RESOLVED','CLOSED'].includes(t.status)).length}),[tickets]);
 function signIn(s){setSession(s);sessionStorage.setItem('aegisops-session',JSON.stringify(s))}
 function signOut(){sessionStorage.removeItem('aegisops-session');setSession(null)}
 async function create(payload){setSaving(true);try{await request('/tickets',token,{method:'POST',body:JSON.stringify(payload)});setShowCreate(false);setPage(0);await load()}finally{setSaving(false)}}
 async function update(id,payload){await request(`/tickets/${id}/status`,token,{method:'PATCH',body:JSON.stringify(payload)});await load()}
 if(!session)return <SignIn onLogin={signIn}/>;
 return <div className="app"><Sidebar tab={tab} setTab={setTab} user={session} signout={signOut}/><main className="main">
 <div className="topbar"><div className="breadcrumb">Workspace <span>/</span> {tab==='tickets'?'Incident operations':'Overview'}</div><div className="top-meta"><span className="connection"><Wifi size={14}/> Secure session</span><div className="top-avatar">{session.username?.[0].toUpperCase()}</div></div></div>
 <div className="page-head"><div><div className="eyebrow">ENGINEERING OPERATIONS / CONTROL CENTER</div><h1>{tab==='tickets'?'Incident operations':'System overview'}</h1><p>Manage incidents, problems, and changes across distributed services.</p></div><button className="primary create-btn" onClick={()=>setShowCreate(true)}><Plus size={18}/> New ticket</button></div>
 <div className="stats"><div className="stat"><span><Ticket size={16}/> Open tickets</span><strong>{stats.open}</strong><small>Awaiting triage</small></div><div className="stat"><span><Activity size={16}/> Investigating</span><strong>{stats.active}</strong><small>Under active review</small></div><div className="stat"><span><AlertTriangle size={16}/> SLA overdue</span><strong className="danger-text">{stats.overdue}</strong><small>Based on loaded page</small></div><div className="stat"><span><ShieldCheck size={16}/> Resolved / closed</span><strong>{stats.resolved}</strong><small>Based on loaded page</small></div></div>
 {tab==='overview'&&<div className="overview-strip"><div><span className="eyebrow">SYSTEM ARCHITECTURE</span><h2>Designed for production workflows.</h2><p>Java 21 · Spring Boot · Hibernate/JPA · MySQL · RabbitMQ · JWT/RBAC · Docker · CI/CD · REST</p></div><div className="sys-icon"><Terminal size={28}/></div></div>}
 <section className="table-section"><div className="section-top"><div><h2>Ticket queue <span className="total-pill">{total}</span></h2><p>All incident, problem and change requests.</p></div><button className="refresh" onClick={load} aria-label="Refresh"><RefreshCw size={17}/></button></div>
 <div className="filters"><div className="search"><Search size={18}/><input value={search} onChange={e=>{setSearch(e.target.value);setPage(0)}} placeholder="Search title or service..."/></div>
 <div className="select-filter"><Filter size={16}/><select value={filter} onChange={e=>{setFilter(e.target.value);setPage(0)}}><option value="">All statuses</option>{STATUSES.map(s=><option key={s}>{s}</option>)}</select><ChevronDown size={15}/></div></div>
 <div className="table-wrap"><table><thead><tr><th>TICKET</th><th>TYPE / SERVICE</th><th>PRIORITY</th><th>STATUS</th><th>CREATED</th><th>ASSIGNEE</th><th></th></tr></thead>
 <tbody>{tickets.map(t=><tr onClick={()=>setSelected(t)} key={t.id} tabIndex={0} onKeyDown={e=>{if(e.key==='Enter')setSelected(t)}}>
 <td><span className="ticket-id">#{String(t.id).padStart(6,'0')}</span><strong className="ticket-name">{t.title}</strong></td><td><span className="category">{t.type}</span><small className="service">{t.serviceName}</small></td><td><Chip priority={t.priority}/></td><td><Chip status={t.status}/></td><td className="time-cell">{nowFormat(t.createdAt)}</td><td className="time-cell">{t.assignee||'Unassigned'}</td><td className="open-arrow"><ArrowRight size={16}/></td></tr>)}
 {!tickets.length&&<tr className="empty-row"><td colSpan={7}>{loading?'Loading tickets...':error||'No tickets found. Create one to start tracking operational work.'}</td></tr>}</tbody></table></div>
 <div className="table-footer"><small>Displaying {tickets.length} of {total} · page {page+1}</small><div className="pager"><button disabled={page===0} onClick={()=>setPage(p=>Math.max(0,p-1))}>Previous</button><button disabled={(page+1)*50>=total} onClick={()=>setPage(p=>p+1)}>Next</button></div></div></section>
 <footer className="app-footer"><span><span className="status-beacon"/> AEGISOPS SYSTEMS</span><span>Independent portfolio project · Not affiliated with any government agency</span><span>v1.0</span></footer>
 </main>{showCreate&&<TicketModal onClose={()=>setShowCreate(false)} onSave={create} busy={saving}/>}{selected&&<Details ticket={selected} onClose={()=>setSelected(null)} onUpdate={update} token={token}/>}</div>
}

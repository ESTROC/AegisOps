package dev.aegisops.incidents.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Correlate HTTP errors to ECS JSON logs without logging tokens or request bodies. */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestCorrelationFilter extends OncePerRequestFilter {
 @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,
    FilterChain chain)throws ServletException,IOException{
  String incoming=request.getHeader("X-Request-ID");
  String id=incoming!=null&&incoming.matches("[A-Za-z0-9._-]{1,64}")
    ?incoming:UUID.randomUUID().toString();
  response.setHeader("X-Request-ID",id);
  try{
   MDC.put("requestId",id);
   chain.doFilter(request,response);
  }finally{MDC.remove("requestId");}
 }
}

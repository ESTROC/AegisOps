package dev.aegisops.legacy;
import java.time.Instant;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
@Endpoint
public class ServiceHealthEndpoint {
 public static final String NS="https://aegisops.dev/legacy";
 @PayloadRoot(namespace=NS,localPart="GetServiceHealthRequest")
 @ResponsePayload
 public Element status(@RequestPayload Element request)throws Exception{
  Node node=request.getElementsByTagNameNS(NS,"serviceName").item(0);
  if(node==null)throw new IllegalArgumentException("serviceName is required");
  String serviceName=node.getTextContent().trim();
  if(serviceName.isEmpty()||serviceName.length()>120)throw new IllegalArgumentException("Invalid serviceName");
  // Deterministic, explicitly synthetic legacy integration; no real infrastructure status is fetched.
  String status=serviceName.toLowerCase().contains("degraded")?"DEGRADED":"NOMINAL";
  Document doc=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
  Element response=doc.createElementNS(NS,"ns:GetServiceHealthResponse");doc.appendChild(response);
  add(doc,response,"serviceName",serviceName);add(doc,response,"healthStatus",status);add(doc,response,"observedAt",Instant.now().toString());
  return response;
 }
 private static void add(Document d,Element root,String key,String val){Element e=d.createElementNS(NS,"ns:"+key);e.setTextContent(val);root.appendChild(e);}
}

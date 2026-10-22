package dev.aegisops.incidents.api;
import dev.aegisops.incidents.service.TicketService;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Document;
@RestController
public class LegacySoapController {
 private final TicketService tickets;private final RestClient client;private final String url;
 public LegacySoapController(TicketService tickets,RestClient.Builder client,@Value("${app.legacy-soap-url:http://localhost:8083/ws}")String url){this.tickets=tickets;this.client=client.build();this.url=url;}
 @GetMapping("/api/tickets/{id}/legacy-health")
 public Map<String,String> legacyStatus(@PathVariable Long id){
  String service=tickets.get(id).serviceName();
  String escaped=service.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace(""","&quot;");
  String envelope="""
   <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:leg="https://aegisops.dev/legacy">
   <soapenv:Header/><soapenv:Body><leg:GetServiceHealthRequest><leg:serviceName>%s</leg:serviceName></leg:GetServiceHealthRequest></soapenv:Body>
   </soapenv:Envelope>
   """.formatted(escaped);
  try{
   String xml=client.post().uri(url).contentType(MediaType.TEXT_XML).body(envelope).retrieve().body(String.class);
   DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();factory.setNamespaceAware(true);
   factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);
   factory.setFeature("http://xml.org/sax/features/external-general-entities",false);
   factory.setFeature("http://xml.org/sax/features/external-parameter-entities",false);
   factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING,true);
   factory.setXIncludeAware(false);factory.setExpandEntityReferences(false);
   Document doc=factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
   String health=doc.getElementsByTagNameNS("https://aegisops.dev/legacy","healthStatus").item(0).getTextContent();
   return Map.of("serviceName",service,"healthStatus",health,"source","SYNTHETIC_LEGACY_SOAP_ADAPTER");
  }catch(Exception ex){throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"Legacy SOAP service unavailable");}
 }
}

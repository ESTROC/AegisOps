package dev.aegisops.legacy;
import static org.junit.jupiter.api.Assertions.*;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
class ServiceHealthEndpointTest {
 @Test void simulatedLegacyResponseIsDeterministic()throws Exception{
  Document doc=DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
  Element request=doc.createElementNS(ServiceHealthEndpoint.NS,"ns:GetServiceHealthRequest");
  doc.appendChild(request);
  Element name=doc.createElementNS(ServiceHealthEndpoint.NS,"ns:serviceName");
  name.setTextContent("degraded-demo-service");request.appendChild(name);
  Element response=new ServiceHealthEndpoint().status(request);
  assertEquals("DEGRADED",response.getElementsByTagNameNS(ServiceHealthEndpoint.NS,"healthStatus").item(0).getTextContent());
 }
}

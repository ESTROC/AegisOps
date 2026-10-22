package dev.aegisops.legacy;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;
@Configuration
public class SoapConfiguration {
 @Bean ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext context){
  MessageDispatcherServlet servlet=new MessageDispatcherServlet();
  servlet.setApplicationContext(context);servlet.setTransformWsdlLocations(true);
  return new ServletRegistrationBean<>(servlet,"/ws/*");
 }
 @Bean XsdSchema serviceHealthSchema(){return new SimpleXsdSchema(new ClassPathResource("service-health.xsd"));}
 @Bean(name="legacy") DefaultWsdl11Definition legacyWsdl(XsdSchema serviceHealthSchema){
  DefaultWsdl11Definition wsdl=new DefaultWsdl11Definition();
  wsdl.setPortTypeName("ServiceHealthPort");wsdl.setLocationUri("/ws");
  wsdl.setTargetNamespace("https://aegisops.dev/legacy");wsdl.setSchema(serviceHealthSchema);return wsdl;
 }
}

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import javax.jms.ConnectionFactory;

public class ResponseSender {
    public static CamelContext camelContext;
    public static ConnectionFactory connectionFactory;
    public static void Send(String nim, Boolean status) throws Exception {
        camelContext = new DefaultCamelContext();
        connectionFactory = new ActiveMQConnectionFactory();
        camelContext.addComponent("JMS", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:response")
                        .to("activemq:message_queue")
                        .end();
            }
        });
        camelContext.start();
        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        producerTemplate.sendBody("direct:response", status + " " + nim);
        Thread.sleep(5000);
        camelContext.stop();
    }
}

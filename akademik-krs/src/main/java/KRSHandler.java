import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import javax.jms.ConnectionFactory;
import java.util.Scanner;

public class KRSHandler {
    public static void main(String[] args) throws Exception{
        Scanner sc = new Scanner(System.in);
        CamelContext camelContext = new DefaultCamelContext();
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        camelContext.addComponent("JMS", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));

        String nim = sc.nextLine();
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:send")
                        .to("activemq:request")
                        .end();
            }
        });
        camelContext.start();
        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        producerTemplate.sendBody("direct:send", nim);

        ResponseKeuanganHandler.Recieve();
        Thread.sleep(5000);
        camelContext.stop();
    }
}

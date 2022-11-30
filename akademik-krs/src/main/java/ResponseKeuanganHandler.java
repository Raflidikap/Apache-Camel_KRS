import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import javax.jms.ConnectionFactory;

public class ResponseKeuanganHandler {
    public static CamelContext camelContext;
    public static ConnectionFactory connectionFactory;

    public static void Recieve() throws Exception {
        camelContext = new DefaultCamelContext();
        connectionFactory = new ActiveMQConnectionFactory();
        camelContext.addComponent("JMS", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("activemq:message_queue")
                        .to("seda:receive")
                        .end();
            }
        });
        camelContext.start();
        ConsumerTemplate consumerTemplate = camelContext.createConsumerTemplate();
        Object status = consumerTemplate.receiveBody("seda:receive", Object.class);
        String[] request = status.toString().split(" ", 2);
        Boolean status_response = Boolean.parseBoolean(request[0]);
        String nim = request[1];

        if (!nim.isEmpty()) {
            if (status_response) {
                System.out.println("Berhasil");
                KRSSender.SendKrs(nim);
            } else
                System.out.println("Belum bayar");
            Thread.sleep(5000);
            camelContext.stop();
        } else {
            System.out.println("mahasiswa tidak terdaftar");
        }

    }
}

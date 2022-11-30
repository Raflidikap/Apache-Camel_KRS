import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

import javax.jms.ConnectionFactory;
import java.util.HashMap;

public class VerifikasiKeuanganMahasiswa {
    public static void main(String[] args) throws Exception {
        HashMap<String, Boolean> hashMap;
        hashMap = new HashMap<>();
        hashMap.put("2010511042", true);
        hashMap.put("2010511048", true);
        hashMap.put("2010511045", true);
        hashMap.put("2010511038", false);

        CamelContext camelContext = new DefaultCamelContext();
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        camelContext.addComponent("JMS", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
        while (true){
            camelContext.addRoutes(new RouteBuilder() {
                @Override
                public void configure() throws Exception {
                    from("activemq:request")
                            .to("seda:request")
                            .end();
                }
            });
            camelContext.start();
            ConsumerTemplate consumerTemplate = camelContext.createConsumerTemplate();
            String request = consumerTemplate.receiveBody("seda:request", String.class);
            if (hashMap.containsKey(request)) {
                System.out.println("Send Response To Akademik");
                ResponseSender.Send(request, hashMap.get(request));
            } else{
                ResponseSender.Send("", null);
                System.out.println("tidak terdaftar");
            }
            Thread.sleep(5000);
            camelContext.stop();
        }

    }
}

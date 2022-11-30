import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class KRSSender {
    public static void SendKrs(String nim) throws Exception {
        String path = "KRS_Storage/" + nim;
        String path_target = nim + ".txt";
        CamelContext camelContext = new DefaultCamelContext();
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:transferFile")
                        .to("file:client_storage")
                        .end();
            }
        });

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        camelContext.start();
        try {
            String fileText = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
            Map<String, Object> headerMap = new HashMap<String, Object>();
            headerMap.put(Exchange.FILE_NAME, path_target);
            producerTemplate.sendBodyAndHeaders("direct:transferFile", fileText, headerMap);
        } catch (NoSuchFieldError e) {
            System.out.println("file krs tidak ditemukan");
        }
        Thread.sleep(5000);
        camelContext.stop();
    }
}

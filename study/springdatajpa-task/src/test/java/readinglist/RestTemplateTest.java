package readinglist;

import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLServerSocket;
import java.net.ServerSocket;

public class RestTemplateTest {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject("https://ipple1986.github.io/",String.class);
        System.out.println(response);

    }
}

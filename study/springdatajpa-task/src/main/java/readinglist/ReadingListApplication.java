package readinglist;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import sun.misc.Version;

@EnableScheduling
@SpringBootApplication
public class ReadingListApplication {

	public static void main(String[] args) {

		BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("",""));
		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultCredentialsProvider(credsProvider)
				.setProxy(new org.apache.http.HttpHost("",8080))
				.setProxyAuthenticationStrategy(ProxyAuthenticationStrategy.INSTANCE)
				.build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);

		RestTemplate restTemplate = new RestTemplate(requestFactory);


		SpringApplication.run(ReadingListApplication.class, args);
	}

}

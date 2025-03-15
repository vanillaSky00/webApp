package com.vanillasky.springbootquickstart;

import com.vanillasky.springbootquickstart.user.User;
import com.vanillasky.springbootquickstart.user.UserHttpClient;
import com.vanillasky.springbootquickstart.user.UserRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.List;

//啟動類別
@SpringBootApplication
public class Application {
    private static final Logger log = LoggerFactory.getLogger((Appendable.class));
    public static void main(String[] args) {
        SpringApplication.run (Application.class, args);
        }

    @Bean
    UserHttpClient userHttpClient(RestClient.Builder builder) {
        RestClient restClient = RestClient.create("https://jsonplaceholder.typicode.com/");
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
        return factory.createClient(UserHttpClient.class);
    }

    @Bean
    CommandLineRunner runner(UserHttpClient client){
        return args -> {

            List<User> users = client.findAll();
            System.out.println (users);

            User user = client.findById(1);
            System.out.println (user);
        };



    }

//    @Bean
//    CommandLineRunner runner(RunRepository runRepository) {
//        return args -> {
//            //funcitonal interface can used by lambda rather than creating a class
//            Run run = new Run(1, "First Run", LocalDateTime.now(), LocalDateTime.now().plus(1, ChronoUnit.HOURS), 5, Location.OUTDOOR);
//            //log.info("Run: " + run);
//            runRepository.create(run);
//        };
//    }
}

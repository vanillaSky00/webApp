package com.vanillasky.springbootquickstart;

import com.vanillasky.springbootquickstart.run.Location;
import com.vanillasky.springbootquickstart.run.Run;
import com.vanillasky.springbootquickstart.run.RunRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

//啟動類別
@SpringBootApplication
public class Application {
    private static final Logger log = LoggerFactory.getLogger((Appendable.class));
    public static void main(String[] args) {
        SpringApplication.run (Application.class, args);

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

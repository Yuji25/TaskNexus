package org.example.tasknexus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TaskNexusApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskNexusApplication.class, args);
    }

}

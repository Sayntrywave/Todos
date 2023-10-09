package com.korotkov.todo;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;


//@EnableWebSecurity(debug = true)
@SpringBootApplication
@EnableCaching
public class TodoApplication {


    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

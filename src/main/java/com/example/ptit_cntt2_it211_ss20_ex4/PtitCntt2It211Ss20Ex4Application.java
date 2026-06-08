package com.example.ptit_cntt2_it211_ss20_ex4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PtitCntt2It211Ss20Ex4Application {

    public static void main(String[] args) {
        SpringApplication.run(PtitCntt2It211Ss20Ex4Application.class, args);
    }

}

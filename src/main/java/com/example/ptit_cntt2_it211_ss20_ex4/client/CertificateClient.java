package com.example.ptit_cntt2_it211_ss20_ex4.client;

import com.example.ptit_cntt2_it211_ss20_ex4.dto.ClaimCertificateResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "certificateClient", url = "${mock-api.url}")
public interface CertificateClient {

    @PostMapping("/certificates")
    ClaimCertificateResponse claimCertificate(@RequestBody Map<String, String> payload);
}

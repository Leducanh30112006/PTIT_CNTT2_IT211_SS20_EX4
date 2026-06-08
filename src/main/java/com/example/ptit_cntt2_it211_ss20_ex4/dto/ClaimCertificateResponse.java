package com.example.ptit_cntt2_it211_ss20_ex4.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaimCertificateResponse {
    private String message;
    private String certificateUrl;
}

package com.ecommerce.webapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LoginDTO {

    private String username;

    private String password;

    @Default
    private Boolean adminLogin = false;


}

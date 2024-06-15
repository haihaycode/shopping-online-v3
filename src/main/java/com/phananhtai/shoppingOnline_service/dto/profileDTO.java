package com.phananhtai.shoppingOnline_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class profileDTO {

    @NotBlank(message = "{NotBlank.userDTO.username}")
    private String username;
    private String fullname;

    @NotBlank(message = "{NotBlank.userDTO.email}")
    @Email(message = "{Email.userDTO.email}")
    private String email;

    private Integer phone;
    private String address;

}

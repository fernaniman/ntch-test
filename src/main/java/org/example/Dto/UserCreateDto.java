package org.example.Dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class UserCreateDto {

    @NotNull(message = "Parameter username tidak boleh kosong")
    private String username;

    @NotNull(message = "Parameter password tidak boleh kosong")
    private String password;

    @Email(message = "Paramter email tidak sesuai format")
    private String email;

    private String fullname;

    private String profilePic;

}

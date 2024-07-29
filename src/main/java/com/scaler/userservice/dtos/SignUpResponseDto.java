package com.scaler.userservice.dtos;

import com.scaler.userservice.models.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpResponseDto {
    private User user;
    private ResponseStatus responseStatus;
}

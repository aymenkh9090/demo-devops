package com.iteam.Exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAlreadyExistsExceptions extends RuntimeException {
    private String message;
}

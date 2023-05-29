package com.akvamarin.clientappfriends.api;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse implements Serializable {
    private int statusCode;
    private String message;
    private String endpoint;
}

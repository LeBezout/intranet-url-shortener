package com.github.lebezout.urlshortener.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    public enum ErrorType { CLIENT, SERVER }

    private ErrorType type;
    private String errorMessage;
    private String requestURI;
}

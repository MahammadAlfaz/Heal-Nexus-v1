package com.healnexus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class APIResponse {
    String message;
    boolean success;
}

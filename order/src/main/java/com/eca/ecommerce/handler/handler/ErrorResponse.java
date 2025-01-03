package com.eca.ecommerce.handler.handler;

import java.util.Map;

public record ErrorResponse(
    Map<String, String> errors
) {

}

package com.eca.ecommerce.handler.customer;

public record CustomerResponse(
    String id,
    String firstname,
    String lastname,
    String email
) {

}

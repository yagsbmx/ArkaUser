package com.example.arkauser.infraestructure.client;

import com.example.arkauser.config.FeignConfig;
import com.example.arkauser.infraestructure.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service",
        path = "/api/product-service/products",
        configuration = FeignConfig.class
)
public interface ProductClient {

    @GetMapping("/{id}")
    ProductDto getProductById(@PathVariable("id") Long id);
}

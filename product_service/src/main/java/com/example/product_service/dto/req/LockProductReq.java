package com.example.product_service.dto.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LockProductReq {
    private String orderId;

    @NotEmpty
    private List<LockProductItem> items;
}
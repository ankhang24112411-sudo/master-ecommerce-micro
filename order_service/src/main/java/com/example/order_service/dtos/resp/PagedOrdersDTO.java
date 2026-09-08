package com.example.order_service.dtos.resp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagedOrdersDTO {
    private List<OrderDTO> items;
    private Integer nextCursor;
    private boolean hasMore;
}

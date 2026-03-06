package com.seowon.coding.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

public record OrderRequest(
	@NotBlank String customerName,
	@NotBlank String customerEmail,
	@NotBlank List<OrderProductResponse> product
) {
	public record OrderProductResponse(
		Long productId,
		Integer quantity
	){}
	public List<Long> getProductIds(List<OrderProductResponse> products) {
		return products.stream()
			.map(OrderRequest.OrderProductResponse::productId)
			.toList();
	}
	public List<Integer> getQuantitys(List<OrderProductResponse> products) {
		return products.stream()
			.map(OrderRequest.OrderProductResponse::quantity)
			.toList();
	}
}

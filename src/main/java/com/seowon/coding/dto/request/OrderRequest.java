package com.seowon.coding.dto.request;

import java.util.List;

import lombok.NonNull;

public record OrderRequest(
	@NonNull String customerName,
	@NonNull String customerEmail,
	@NonNull List<OrderProductResponse> products
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

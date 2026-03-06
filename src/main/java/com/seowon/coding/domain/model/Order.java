package com.seowon.coding.domain.model;

import static com.seowon.coding.domain.model.Order.OrderStatus.*;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // "order" is a reserved keyword in SQL
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String customerName;
    
    private String customerEmail;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    private LocalDateTime orderDate;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
    
    private BigDecimal totalAmount;

    public void calTotalAmount(BigDecimal subtotal,String couponCode,BigDecimal total) {
        BigDecimal shipping = subtotal.compareTo(new BigDecimal("100.00")) >= 0 ? BigDecimal.ZERO : new BigDecimal("5.00");
        BigDecimal discount = (couponCode != null && couponCode.startsWith("SALE")) ? new BigDecimal("10.00") : BigDecimal.ZERO;

        total= total.add(subtotal.add(shipping).subtract(discount));
        this.totalAmount = total;
    }

    public static Order createOrder(String customerName, String customerEmail, BigDecimal totalAmount) {
        if (customerName == null || customerEmail == null) {
            throw new IllegalArgumentException("customer info required");
        }
        return Order.builder()
            .customerName(customerName)
            .customerEmail(customerEmail)
            .totalAmount(totalAmount)
            .orderDate(LocalDateTime.now())
            .status(PENDING)
            .build();
    }
    public static Order create(String customerName, String customerEmail) {
        if (customerName == null || customerEmail == null) {
            throw new IllegalArgumentException("customer info required");
        }

        return Order.builder()
            .customerName(customerName)
            .customerEmail(customerEmail)
            .status(Order.OrderStatus.PENDING)
            .orderDate(LocalDateTime.now())
            .items(new ArrayList<>())
            .totalAmount(BigDecimal.ZERO)
            .build();
    }
    
    // Business logic
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
        recalculateTotalAmount();
    }
    
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
        recalculateTotalAmount();
    }
    
    public void recalculateTotalAmount() {
        this.totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public void markAsProcessing() {
        this.status = OrderStatus.PROCESSING;
    }
    
    public void markAsShipped() {
        this.status = OrderStatus.SHIPPED;
    }
    
    public void markAsDelivered() {
        this.status = OrderStatus.DELIVERED;
    }
    
    public void markAsCancelled() {
        this.status = OrderStatus.CANCELLED;
    }
    
    public enum OrderStatus {
        PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }

}
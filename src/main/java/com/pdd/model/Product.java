package com.pdd.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
    private Long id;
    private String goodId;
    private String name;
    private BigDecimal cost;
    private BigDecimal price;
    private Long sales;
    private String category;
    private String state;
    private String image;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 构造方法
    public Product() {}

    public Product(String goodId, String name, BigDecimal cost, BigDecimal price, Long sales, 
                  String category, String state, String image, String description) {
        this.goodId = goodId;
        this.name = name;
        this.cost = cost;
        this.price = price;
        this.sales = sales;
        this.category = category;
        this.state = state;
        this.image = image;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoodId() {
        return goodId;
    }

    public void setGoodId(String goodId) {
        this.goodId = goodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getSales() {
        return sales;
    }

    public void setSales(Long sales) {
        this.sales = sales;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", goodId='" + goodId + '\'' +
                ", name='" + name + '\'' +
                ", cost=" + cost +
                ", price=" + price +
                ", sales=" + sales +
                ", category='" + category + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
} 
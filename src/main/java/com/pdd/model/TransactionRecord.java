package com.pdd.model;

import java.math.BigDecimal;

public class TransactionRecord {
    
    private Long id; // 自增主键
    
    private String goodId; // 商品ID
    
    private String name;
    
    private BigDecimal cost;
    
    private BigDecimal price;
    
    private String category;
    
    private String state;
    
    private String image;
    
    private String description;
    
    private Integer sales;
    
    // 构造函数
    public TransactionRecord() {}
    
    public TransactionRecord(String goodId, String name, BigDecimal cost, BigDecimal price, 
                           String category, String state, String image, String description, Integer sales) {
        this.goodId = goodId;
        this.name = name;
        this.cost = cost;
        this.price = price;
        this.category = category;
        this.state = state;
        this.image = image;
        this.description = description;
        this.sales = sales;
    }
    
    public TransactionRecord(Long id, String goodId, String name, BigDecimal cost, BigDecimal price, 
                           String category, String state, String image, String description, Integer sales) {
        this.id = id;
        this.goodId = goodId;
        this.name = name;
        this.cost = cost;
        this.price = price;
        this.category = category;
        this.state = state;
        this.image = image;
        this.description = description;
        this.sales = sales;
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
    
    public Integer getSales() {
        return sales;
    }
    
    public void setSales(Integer sales) {
        this.sales = sales;
    }
    
    @Override
    public String toString() {
        return "TransactionRecord{" +
                "id=" + id +
                ", goodId='" + goodId + '\'' +
                ", name='" + name + '\'' +
                ", cost=" + cost +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", state='" + state + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                ", sales=" + sales +
                '}';
    }
} 
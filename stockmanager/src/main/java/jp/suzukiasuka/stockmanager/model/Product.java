package jp.suzukiasuka.stockmanager.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int quantity;

    @Column
    private String category;

    @Column(name = "user_id")
    private Long userId;

    // 画像データ（bytea型用）※ @Lob は不要、columnDefinition で指定
    @Column(columnDefinition = "bytea")
    private byte[] image;

    @Column(length = 1000)
    private String description;
}
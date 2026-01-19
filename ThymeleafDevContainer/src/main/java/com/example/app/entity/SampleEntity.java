package com.example.app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * サンプルエンティティクラス
 * sample_tableテーブルに対応するJPAエンティティ
 */
@Entity
@Table(name = "sample_table")
public class SampleEntity {
    
    /**
     * ID（主キー）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 名前
     */
    @Column(name = "name")
    private String name;
    
    /**
     * 作成日時
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    /**
     * 更新日時
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * デフォルトコンストラクタ
     */
    public SampleEntity() {
    }
    
    /**
     * コンストラクタ
     * 
     * @param id ID
     * @param name 名前
     * @param createdAt 作成日時
     * @param updatedAt 更新日時
     */
    public SampleEntity(Long id, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getter/Setter
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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
}

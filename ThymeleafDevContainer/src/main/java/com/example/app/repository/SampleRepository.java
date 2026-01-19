package com.example.app.repository;

import com.example.app.entity.SampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * サンプルリポジトリインターフェース
 * sample_tableテーブルへのデータアクセスを提供するJPAリポジトリ
 */
@Repository
public interface SampleRepository extends JpaRepository<SampleEntity, Long> {
    
    /**
     * 名前でエンティティを検索
     * 
     * @param name 名前
     * @return エンティティのリスト
     */
    @Query("SELECT s FROM SampleEntity s WHERE s.name = :name")
    List<SampleEntity> findByName(@Param("name") String name);
}

package com.example.app.repository;

import com.example.app.entity.SampleEntity;
import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.Delete;
import org.seasar.doma.boot.ConfigAutowireable;

import java.util.List;
import java.util.Optional;

/**
 * サンプルリポジトリインターフェース
 * sample_tableテーブルへのデータアクセスを提供するDoma DAO
 */
@Dao
@ConfigAutowireable
public interface SampleRepository {
    
    /**
     * 全てのエンティティを取得
     * 
     * @return エンティティのリスト
     */
    @Select
    List<SampleEntity> findAll();
    
    /**
     * IDでエンティティを取得
     * 
     * @param id ID
     * @return エンティティ（存在しない場合はOptional.empty()）
     */
    @Select
    Optional<SampleEntity> findById(Long id);
    
    /**
     * 名前でエンティティを検索
     * 
     * @param name 名前
     * @return エンティティのリスト
     */
    @Select
    List<SampleEntity> findByName(String name);
    
    /**
     * エンティティを挿入
     * 
     * @param entity 挿入するエンティティ
     * @return 挿入された行数
     */
    @Insert
    int insert(SampleEntity entity);
    
    /**
     * エンティティを更新
     * 
     * @param entity 更新するエンティティ
     * @return 更新された行数
     */
    @Update
    int update(SampleEntity entity);
    
    /**
     * エンティティを削除
     * 
     * @param entity 削除するエンティティ
     * @return 削除された行数
     */
    @Delete
    int delete(SampleEntity entity);
}

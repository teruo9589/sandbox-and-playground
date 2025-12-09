package com.example.app.service;

import com.example.app.entity.SampleEntity;
import com.example.app.repository.SampleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * サンプルサービスクラス
 * ビジネスロジックを実装し、トランザクション管理を提供
 */
@Service
public class SampleService {
    
    private static final Logger log = LoggerFactory.getLogger(SampleService.class);
    
    private final SampleRepository sampleRepository;
    
    /**
     * コンストラクタ
     * 
     * @param sampleRepository サンプルリポジトリ
     */
    public SampleService(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }
    
    /**
     * 全てのエンティティを取得
     * 
     * @return エンティティのリスト
     */
    @Transactional(readOnly = true)
    public List<SampleEntity> findAll() {
        log.debug("全てのエンティティを取得");
        return sampleRepository.findAll();
    }
    
    /**
     * IDでエンティティを取得
     * 
     * @param id ID
     * @return エンティティ（存在しない場合はOptional.empty()）
     */
    @Transactional(readOnly = true)
    public Optional<SampleEntity> findById(Long id) {
        log.debug("IDでエンティティを取得: id={}", id);
        return sampleRepository.findById(id);
    }
    
    /**
     * 名前でエンティティを検索
     * 
     * @param name 名前
     * @return エンティティのリスト
     */
    @Transactional(readOnly = true)
    public List<SampleEntity> findByName(String name) {
        log.debug("名前でエンティティを検索: name={}", name);
        return sampleRepository.findByName(name);
    }
    
    /**
     * 新しいエンティティを作成
     * 
     * @param name 名前
     * @return 作成されたエンティティ
     */
    @Transactional
    public SampleEntity create(String name) {
        log.info("新しいエンティティを作成: name={}", name);
        
        // ビジネスロジック: 名前の検証
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("名前は必須です");
        }
        
        // エンティティの作成
        SampleEntity entity = new SampleEntity();
        entity.setName(name.trim());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        
        // データベースに挿入
        int result = sampleRepository.insert(entity);
        
        if (result != 1) {
            throw new RuntimeException("エンティティの作成に失敗しました");
        }
        
        log.info("エンティティを作成しました: id={}", entity.getId());
        return entity;
    }
    
    /**
     * エンティティを更新
     * 
     * @param id ID
     * @param name 新しい名前
     * @return 更新されたエンティティ
     */
    @Transactional
    public SampleEntity update(Long id, String name) {
        log.info("エンティティを更新: id={}, name={}", id, name);
        
        // ビジネスロジック: 名前の検証
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("名前は必須です");
        }
        
        // 既存エンティティの取得
        SampleEntity entity = sampleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("エンティティが見つかりません: id=" + id));
        
        // エンティティの更新
        entity.setName(name.trim());
        entity.setUpdatedAt(LocalDateTime.now());
        
        // データベースに更新
        int result = sampleRepository.update(entity);
        
        if (result != 1) {
            throw new RuntimeException("エンティティの更新に失敗しました");
        }
        
        log.info("エンティティを更新しました: id={}", entity.getId());
        return entity;
    }
    
    /**
     * エンティティを削除
     * 
     * @param id ID
     */
    @Transactional
    public void delete(Long id) {
        log.info("エンティティを削除: id={}", id);
        
        // 既存エンティティの取得
        SampleEntity entity = sampleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("エンティティが見つかりません: id=" + id));
        
        // データベースから削除
        int result = sampleRepository.delete(entity);
        
        if (result != 1) {
            throw new RuntimeException("エンティティの削除に失敗しました");
        }
        
        log.info("エンティティを削除しました: id={}", id);
    }
    
    /**
     * ビジネスロジックの実行例
     * 複数の操作を1つのトランザクションで実行
     * 
     * @return 処理結果のリスト
     */
    @Transactional
    public List<SampleEntity> processBusinessLogic() {
        log.info("ビジネスロジックを実行");
        
        // 例: 全てのエンティティを取得して処理
        List<SampleEntity> entities = sampleRepository.findAll();
        
        // ビジネスロジックの実装例
        // 実際のビジネス要件に応じて実装
        
        return entities;
    }
}

package com.example.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * データベースヘルスチェックコンポーネント
 * アプリケーション起動時にデータベース接続を確認
 */
@Component
public class DatabaseHealthCheck {
    
    private static final Logger log = LoggerFactory.getLogger(DatabaseHealthCheck.class);
    
    @Autowired
    private DataSource dataSource;
    
    /**
     * アプリケーション起動時にデータベース接続を確認
     */
    @PostConstruct
    public void checkConnection() {
        try (Connection conn = dataSource.getConnection()) {
            log.info("データベース接続成功");
            log.info("データベースURL: {}", conn.getMetaData().getURL());
            log.info("データベース製品名: {}", conn.getMetaData().getDatabaseProductName());
            log.info("データベースバージョン: {}", conn.getMetaData().getDatabaseProductVersion());
        } catch (SQLException e) {
            log.error("データベース接続失敗: {}", e.getMessage(), e);
            throw new RuntimeException("データベースに接続できません。データベースコンテナが起動しているか確認してください。", e);
        }
    }
}

#!/bin/bash

# Docker環境ヘルスチェックスクリプト
# Git Bash / Linux / macOS用

set -e  # エラー時にスクリプトを停止

# 色付きの出力用の定数
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== Docker環境ヘルスチェック開始 ===${NC}"

# 1. Dockerコンテナの状態確認
echo -e "\n${YELLOW}1. Dockerコンテナの状態確認${NC}"
if docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" 2>/dev/null; then
    echo -e "${GREEN}✓ Dockerコンテナの状態確認完了${NC}"
else
    echo -e "${RED}✗ エラー: Dockerコンテナの状態確認に失敗しました${NC}"
    exit 1
fi

# 2. アプリケーションコンテナのヘルスチェック
echo -e "\n${YELLOW}2. アプリケーションコンテナのヘルスチェック${NC}"
if APP_STATUS=$(docker inspect spring-boot-app --format='{{.State.Status}}' 2>/dev/null); then
    echo "アプリケーションコンテナ状態: $APP_STATUS"
    
    if [ "$APP_STATUS" != "running" ]; then
        echo -e "${RED}警告: アプリケーションコンテナが実行中ではありません${NC}"
    else
        echo -e "${GREEN}✓ アプリケーションコンテナが正常に動作中${NC}"
    fi
else
    echo -e "${RED}✗ エラー: アプリケーションコンテナの確認に失敗しました${NC}"
fi

# 3. データベースコンテナのヘルスチェック
echo -e "\n${YELLOW}3. データベースコンテナのヘルスチェック${NC}"
if DB_STATUS=$(docker inspect postgres-db --format='{{.State.Status}}' 2>/dev/null); then
    DB_HEALTH=$(docker inspect postgres-db --format='{{.State.Health.Status}}' 2>/dev/null || echo "no-health-check")
    echo "データベースコンテナ状態: $DB_STATUS"
    echo "データベースヘルス状態: $DB_HEALTH"
    
    if [ "$DB_STATUS" != "running" ] || [ "$DB_HEALTH" != "healthy" ]; then
        echo -e "${RED}警告: データベースコンテナが正常に動作していません${NC}"
    else
        echo -e "${GREEN}✓ データベースコンテナが正常に動作中${NC}"
    fi
else
    echo -e "${RED}✗ エラー: データベースコンテナの確認に失敗しました${NC}"
fi

# 4. ポート接続確認
echo -e "\n${YELLOW}4. ポート接続確認${NC}"

# アプリケーションポート（8080）の確認
if command -v nc >/dev/null 2>&1; then
    # netcatが利用可能な場合
    if nc -z localhost 8080 2>/dev/null; then
        echo -e "${GREEN}✓ アプリケーションポート 8080 は接続可能です${NC}"
    else
        echo -e "${RED}✗ アプリケーションポート 8080 に接続できません${NC}"
    fi
    
    # データベースポート（5432）の確認
    if nc -z localhost 5432 2>/dev/null; then
        echo -e "${GREEN}✓ データベースポート 5432 は接続可能です${NC}"
    else
        echo -e "${RED}✗ データベースポート 5432 に接続できません${NC}"
    fi
elif command -v telnet >/dev/null 2>&1; then
    # telnetが利用可能な場合（Windows Git Bash）
    if timeout 3 bash -c "</dev/tcp/localhost/8080" 2>/dev/null; then
        echo -e "${GREEN}✓ アプリケーションポート 8080 は接続可能です${NC}"
    else
        echo -e "${RED}✗ アプリケーションポート 8080 に接続できません${NC}"
    fi
    
    if timeout 3 bash -c "</dev/tcp/localhost/5432" 2>/dev/null; then
        echo -e "${GREEN}✓ データベースポート 5432 は接続可能です${NC}"
    else
        echo -e "${RED}✗ データベースポート 5432 に接続できません${NC}"
    fi
else
    echo -e "${YELLOW}注意: ポート接続確認ツール（nc または telnet）が見つかりません${NC}"
    echo "手動でブラウザからアクセスして確認してください"
fi

# 5. HTTP接続確認
echo -e "\n${YELLOW}5. HTTP接続確認${NC}"
if command -v curl >/dev/null 2>&1; then
    # curlが利用可能な場合
    if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080 | grep -q "200"; then
        echo -e "${GREEN}✓ アプリケーションHTTP接続が正常です${NC}"
    else
        echo -e "${RED}✗ アプリケーションHTTP接続に問題があります${NC}"
    fi
    
    # ヘルスチェックエンドポイントの確認
    if HEALTH_RESPONSE=$(curl -s http://localhost:8080/actuator/health 2>/dev/null); then
        if echo "$HEALTH_RESPONSE" | grep -q '"status":"UP"'; then
            echo -e "${GREEN}✓ ヘルスチェックエンドポイントが正常に動作しています${NC}"
            echo "  レスポンス: $HEALTH_RESPONSE"
        else
            echo -e "${RED}✗ ヘルスチェックでエラーが検出されました${NC}"
            echo "  レスポンス: $HEALTH_RESPONSE"
        fi
    else
        echo -e "${RED}✗ ヘルスチェックエンドポイントにアクセスできません${NC}"
    fi
elif command -v wget >/dev/null 2>&1; then
    # wgetが利用可能な場合
    if wget -q --spider http://localhost:8080 2>/dev/null; then
        echo -e "${GREEN}✓ アプリケーションHTTP接続が正常です${NC}"
    else
        echo -e "${RED}✗ アプリケーションHTTP接続に問題があります${NC}"
    fi
else
    echo -e "${YELLOW}注意: HTTP接続確認ツール（curl または wget）が見つかりません${NC}"
    echo "手動でブラウザからアクセスして確認してください"
fi

# 6. アプリケーションログの確認
echo -e "\n${YELLOW}6. アプリケーションログの確認（最新10行）${NC}"
if docker logs spring-boot-app --tail 10 2>/dev/null; then
    echo -e "${GREEN}✓ アプリケーションログの取得完了${NC}"
else
    echo -e "${RED}✗ エラー: アプリケーションログの取得に失敗しました${NC}"
fi

echo -e "\n${GREEN}=== Docker環境ヘルスチェック完了 ===${NC}"
echo -e "${CYAN}アプリケーションURL: http://localhost:8080${NC}"
echo -e "${CYAN}ヘルスチェックURL: http://localhost:8080/actuator/health${NC}"
echo -e "${CYAN}データベース接続: localhost:5432${NC}"

# 最終的な結果サマリー
echo -e "\n${YELLOW}=== 結果サマリー ===${NC}"
echo "1. ブラウザで http://localhost:8080 にアクセスしてアプリケーションを確認してください"
echo "2. http://localhost:8080/actuator/health でヘルスチェックを確認してください"
echo "3. 問題がある場合は、docker-compose logs でログを確認してください"
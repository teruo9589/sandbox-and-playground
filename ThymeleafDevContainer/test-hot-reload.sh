#!/bin/bash

# ホットリロード機能の動作テストスクリプト

echo "=== ホットリロード機能の動作テスト ==="
echo ""

# テスト用の一時ファイル
TEMPLATE_FILE="src/main/resources/templates/index.html"
BACKUP_FILE="src/main/resources/templates/index.html.backup"
TEST_MARKER="<!-- HOT_RELOAD_TEST_MARKER -->"

# 1. コンテナが起動しているか確認
echo "1. Dockerコンテナの状態を確認中..."
if ! docker ps | grep -q "spring-boot-app"; then
    echo "   ✗ アプリケーションコンテナが起動していません"
    echo "   docker-compose up -d を実行してください"
    exit 1
fi
echo "   ✓ アプリケーションコンテナが起動しています"
echo ""

# 2. アプリケーションが応答するか確認
echo "2. アプリケーションの応答を確認中..."
if ! curl -s -o /dev/null -w "%{http_code}" http://localhost:8080 | grep -q "200"; then
    echo "   ✗ アプリケーションが応答していません"
    echo "   コンテナのログを確認してください: docker logs spring-boot-app"
    exit 1
fi
echo "   ✓ アプリケーションが正常に応答しています"
echo ""

# 3. テンプレートファイルのバックアップ
echo "3. テンプレートファイルをバックアップ中..."
if [ -f "$TEMPLATE_FILE" ]; then
    cp "$TEMPLATE_FILE" "$BACKUP_FILE"
    echo "   ✓ バックアップ完了: $BACKUP_FILE"
else
    echo "   ✗ テンプレートファイルが見つかりません: $TEMPLATE_FILE"
    exit 1
fi
echo ""

# 4. テンプレートファイルを変更
echo "4. テンプレートファイルを変更中..."
if grep -q "$TEST_MARKER" "$TEMPLATE_FILE"; then
    # すでにマーカーが存在する場合は削除
    sed -i.tmp "/$TEST_MARKER/d" "$TEMPLATE_FILE"
    rm -f "${TEMPLATE_FILE}.tmp"
fi
# マーカーを追加
sed -i.tmp "/<\/body>/i\\
$TEST_MARKER" "$TEMPLATE_FILE"
rm -f "${TEMPLATE_FILE}.tmp"
echo "   ✓ テストマーカーを追加しました"
echo ""

# 5. 変更が反映されるまで待機
echo "5. 変更の反映を待機中（最大30秒）..."
COUNTER=0
MAX_WAIT=30
while [ $COUNTER -lt $MAX_WAIT ]; do
    if curl -s http://localhost:8080 | grep -q "$TEST_MARKER"; then
        echo "   ✓ 変更が反映されました（${COUNTER}秒後）"
        RELOAD_SUCCESS=true
        break
    fi
    sleep 1
    COUNTER=$((COUNTER + 1))
    echo -n "."
done
echo ""

# 6. テンプレートファイルを復元
echo "6. テンプレートファイルを復元中..."
mv "$BACKUP_FILE" "$TEMPLATE_FILE"
echo "   ✓ 復元完了"
echo ""

# 7. 結果の表示
echo "=== テスト結果 ==="
if [ "$RELOAD_SUCCESS" = true ]; then
    echo "✓ ホットリロード機能が正常に動作しています"
    echo ""
    echo "テンプレートファイルの変更が自動的に反映されることを確認しました。"
    exit 0
else
    echo "✗ ホットリロード機能が動作していません"
    echo ""
    echo "トラブルシューティング："
    echo "1. コンテナのログを確認: docker logs spring-boot-app"
    echo "2. DevTools設定を確認: src/main/resources/application-dev.yml"
    echo "3. ボリュームマウントを確認: docker-compose.yml"
    exit 1
fi

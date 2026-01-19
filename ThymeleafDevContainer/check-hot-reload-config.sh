#!/bin/bash

# ホットリロード機能の設定確認スクリプト

echo "=== ホットリロード機能の設定確認 ==="
echo ""

# 1. Spring Boot DevToolsの依存関係確認
echo "1. Spring Boot DevToolsの依存関係を確認中..."
if grep -q "spring-boot-devtools" build.gradle; then
    echo "   ✓ Spring Boot DevToolsが設定されています"
else
    echo "   ✗ Spring Boot DevToolsが見つかりません"
    exit 1
fi
echo ""

# 2. Thymeleafキャッシュ無効化の確認
echo "2. Thymeleafキャッシュ無効化を確認中..."
if grep -q "cache: false" src/main/resources/application.yml; then
    echo "   ✓ Thymeleafキャッシュが無効化されています"
else
    echo "   ✗ Thymeleafキャッシュの無効化設定が見つかりません"
    exit 1
fi
echo ""

# 3. DevTools設定の確認
echo "3. DevTools設定を確認中..."
if grep -q "devtools:" src/main/resources/application-dev.yml; then
    echo "   ✓ DevTools設定が存在します"
    if grep -q "restart:" src/main/resources/application-dev.yml; then
        echo "   ✓ 再起動設定が有効です"
    fi
    if grep -q "livereload:" src/main/resources/application-dev.yml; then
        echo "   ✓ LiveReload設定が有効です"
    fi
else
    echo "   ✗ DevTools設定が見つかりません"
    exit 1
fi
echo ""

# 4. ボリュームマウント設定の確認
echo "4. ボリュームマウント設定を確認中..."
if grep -q "./src:/app/src" docker-compose.yml; then
    echo "   ✓ ソースコードのボリュームマウントが設定されています"
else
    echo "   ✗ ボリュームマウント設定が見つかりません"
    exit 1
fi
echo ""

# 5. LiveReloadポートの確認
echo "5. LiveReloadポートの公開を確認中..."
if grep -q "35729:35729" docker-compose.yml; then
    echo "   ✓ LiveReloadポート(35729)が公開されています"
else
    echo "   ✗ LiveReloadポートの公開設定が見つかりません"
    exit 1
fi
echo ""

echo "=== すべての設定確認が完了しました ==="
echo ""
echo "ホットリロード機能を使用するには："
echo "1. docker-compose up -d でコンテナを起動"
echo "2. src/main/resources/templates/ 内のHTMLファイルを編集"
echo "3. ブラウザで http://localhost:8080 にアクセスして変更を確認"
echo ""

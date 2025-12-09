# Docker環境ガイド

## 概要

このプロジェクトは、Docker Composeを使用してSpring BootアプリケーションとPostgreSQLデータベースをコンテナ化しています。

## 前提条件

- Docker Desktop（Windows/Mac）またはDocker Engine（Linux）がインストールされていること
- Docker Composeがインストールされていること（Docker Desktop には含まれています）

## 開発環境の起動

### 1. 初回起動

```bash
# すべてのコンテナを起動（バックグラウンド）
docker-compose up -d

# ログを確認
docker-compose logs -f
```

### 2. アプリケーションへのアクセス

ブラウザで以下のURLにアクセス：
- アプリケーション: http://localhost:8080
- データベース: localhost:5432（PostgreSQLクライアントから接続可能）

### 3. 環境の停止

```bash
# コンテナを停止
docker-compose down

# コンテナとボリュームを削除（データも削除）
docker-compose down -v
```

## よく使うコマンド

### コンテナの状態確認

```bash
# 実行中のコンテナを確認
docker-compose ps

# コンテナのログを確認
docker-compose logs app
docker-compose logs postgres
```

### コンテナの再起動

```bash
# すべてのコンテナを再起動
docker-compose restart

# 特定のコンテナのみ再起動
docker-compose restart app
```

### コンテナ内でコマンドを実行

```bash
# アプリケーションコンテナに入る
docker-compose exec app bash

# データベースコンテナに入る
docker-compose exec postgres psql -U appuser -d appdb
```

### ビルドの再実行

```bash
# イメージを再ビルドして起動
docker-compose up -d --build

# キャッシュを使わずに再ビルド
docker-compose build --no-cache
```

## 本番環境のビルドとデプロイ

### 本番用Dockerイメージのビルド

```bash
# 本番用イメージをビルド
docker build -t spring-boot-app:latest .

# イメージの確認
docker images | grep spring-boot-app
```

### 本番環境用Docker Composeの使用

```bash
# 環境変数を設定
export DATABASE_URL=jdbc:postgresql://your-rds-endpoint:5432/appdb
export DB_USERNAME=your-username
export DB_PASSWORD=your-password

# 本番環境用の設定で起動
docker-compose -f docker-compose.prod.yml up -d
```

## トラブルシューティング

### ポートが既に使用されている

エラー: `Bind for 0.0.0.0:8080 failed: port is already allocated`

**解決方法**:
```bash
# ポートを使用しているプロセスを確認（Windows）
netstat -ano | findstr :8080

# ポートを使用しているプロセスを確認（Mac/Linux）
lsof -i :8080

# docker-compose.ymlのポート番号を変更
ports:
  - "8081:8080"  # ホスト側のポートを変更
```

### データベース接続エラー

エラー: `Connection refused` または `could not connect to server`

**解決方法**:
```bash
# データベースコンテナの状態を確認
docker-compose ps postgres

# データベースのログを確認
docker-compose logs postgres

# ヘルスチェックの状態を確認
docker-compose ps

# データベースが起動するまで待機してから再起動
docker-compose restart app
```

### ボリュームのクリーンアップ

データベースの状態をリセットしたい場合：

```bash
# すべてのコンテナとボリュームを削除
docker-compose down -v

# 再度起動（初期化スクリプトが実行される）
docker-compose up -d
```

### Gradleキャッシュの問題

ビルドエラーが発生する場合：

```bash
# Gradleキャッシュボリュームを削除
docker volume rm spring-boot-thymeleaf-dev-env_gradle-cache

# イメージを再ビルド
docker-compose up -d --build
```

### コンテナのリソース不足

メモリ不足エラーが発生する場合：

1. Docker Desktopの設定を開く
2. Resources > Advanced
3. メモリを4GB以上に増やす
4. Apply & Restart

## ファイル構成

```
.
├── docker-compose.yml          # 開発環境用Docker Compose設定
├── docker-compose.prod.yml     # 本番環境用Docker Compose設定
├── Dockerfile.dev              # 開発環境用Dockerfile
├── Dockerfile                  # 本番環境用Dockerfile
├── .dockerignore               # Dockerビルドから除外するファイル
├── .env.example                # 環境変数のサンプル
└── docker/
    └── init.sql                # データベース初期化スクリプト
```

## ホットリロード

開発環境では、ソースコードの変更が自動的に反映されます：

- `src/main/resources/templates/` 配下のThymeleafテンプレート
- `src/main/resources/static/` 配下の静的ファイル（CSS、JS、画像）

Javaコードを変更した場合は、Spring Boot DevToolsが自動的にアプリケーションを再起動します。

## セキュリティに関する注意事項

1. **本番環境では必ず環境変数を変更してください**
   - デフォルトのパスワード（`devpassword`）は開発環境専用です
   - 本番環境ではAWS Secrets Managerなどを使用してください

2. **.envファイルをGitにコミットしないでください**
   - `.gitignore`に`.env`が含まれていることを確認してください

3. **本番環境ではHTTPSを使用してください**
   - ALBやNginxでSSL/TLS終端を設定してください

## 参考リンク

- [Docker公式ドキュメント](https://docs.docker.com/)
- [Docker Compose公式ドキュメント](https://docs.docker.com/compose/)
- [Spring Boot Docker公式ガイド](https://spring.io/guides/gs/spring-boot-docker/)

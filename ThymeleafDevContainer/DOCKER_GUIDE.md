# Docker環境ガイド

## ホットリロード機能について

このプロジェクトでは、開発効率を向上させるためにホットリロード機能を実装しています。

### 設定内容

1. **Spring Boot DevTools**
   - `build.gradle`に`spring-boot-devtools`依存関係を追加
   - 開発環境でのみ有効（`developmentOnly`スコープ）

2. **Thymeleafキャッシュ無効化**
   - `application.yml`で`spring.thymeleaf.cache: false`を設定
   - テンプレート変更が即座に反映されます

3. **DevTools設定**
   - `application-dev.yml`で詳細な設定を実施
   - ポーリング間隔: 2秒（Dockerコンテナでのファイル変更検出を改善）
   - LiveReloadサーバー: ポート35729で起動

4. **Dockerボリュームマウント**
   - `docker-compose.yml`で`./src:/app/src`をマウント
   - ホストのファイル変更がコンテナ内に即座に反映されます

### ホットリロード機能の使い方

#### 1. 環境の起動

```bash
docker-compose up -d
```

#### 2. アプリケーションへのアクセス

ブラウザで http://localhost:8080 にアクセスします。

#### 3. テンプレートファイルの編集

`src/main/resources/templates/index.html`を編集します。

例：
```html
<p class="hot-reload-test">ホットリロード機能が有効です。このテキストを変更して自動反映をテストしてください。</p>
```

このテキストを変更して保存します。

#### 4. 変更の確認

**重要**: Dockerコンテナ内でのホットリロードには制限があります。

##### 方法1: コンテナを再起動（推奨）

テンプレートファイルを変更した後、コンテナを再起動します：

```bash
docker-compose restart app
```

再起動後（約10-15秒）、ブラウザをリロード（F5）して変更を確認します。

##### 方法2: 手動でファイルをコピー

```bash
# コンテナ内のファイルを更新
docker cp src/main/resources/templates/index.html spring-boot-app:/app/src/main/resources/templates/index.html
```

DevToolsが変更を検出し、アプリケーションを自動的に再起動します（2-5秒）。

##### 方法3: コンテナ内で直接編集

```bash
# コンテナ内でシェルを起動
docker-compose exec app bash

# コンテナ内でファイルを編集
vi /app/src/main/resources/templates/index.html
```

コンテナ内でファイルを編集すると、DevToolsが変更を検出します。

#### Dockerでのホットリロードの制限事項

Dockerコンテナでのホットリロードには、以下の制限があります：

1. **ファイルシステムの違い**
   - Windowsホスト上のDockerでは、ボリュームマウントされたファイルの変更イベントが正しく伝播しないことがあります
   - これは、WindowsファイルシステムとLinuxファイルシステムの違いによるものです

2. **ポーリング間隔**
   - `application-dev.yml`で`poll-interval: 2000`（2秒）を設定していますが、ボリュームマウントの場合は検出されないことがあります

3. **推奨される開発方法**
   - **ローカル開発**: IDEでアプリケーションを直接実行（`./gradlew bootRun`）すると、ホットリロードが正常に動作します
   - **Docker開発**: テンプレート変更後は`docker-compose restart app`を実行するか、コンテナ内で直接編集します

### ホットリロードの対象

以下のファイルの変更が自動的に検出されます：

- ✅ Thymeleafテンプレート（`src/main/resources/templates/**/*.html`）
- ✅ Javaソースコード（`src/main/java/**/*.java`）
- ✅ 設定ファイル（`src/main/resources/application*.yml`）
- ✅ 静的リソース（`src/main/resources/static/**/*`）

### トラブルシューティング

#### 変更が反映されない場合

1. **コンテナのログを確認**
   ```bash
   docker logs spring-boot-app
   ```
   
   `LiveReload server is running on port 35729`というメッセージが表示されているか確認します。

2. **ボリュームマウントを確認**
   ```bash
   docker-compose config
   ```
   
   `./src:/app/src`のマウント設定が正しいか確認します。

3. **コンテナを再起動**
   ```bash
   docker-compose restart app
   ```

4. **設定確認スクリプトを実行**
   ```bash
   bash check-hot-reload-config.sh
   ```

#### Javaコードの変更が反映されない場合

Javaコードの変更は、クラスファイルの再コンパイルが必要です。以下の方法で対応できます：

1. **IDEの自動ビルド機能を有効化**
   - IntelliJ IDEA: `Build, Execution, Deployment > Compiler > Build project automatically`を有効化
   - Eclipse: デフォルトで有効

2. **手動でビルド**
   ```bash
   docker-compose exec app gradle classes
   ```

### LiveReload ブラウザ拡張機能（オプション）

ブラウザの自動リロードを有効にするには、以下の拡張機能をインストールします：

- **Chrome**: [LiveReload](https://chrome.google.com/webstore/detail/livereload/jnihajbhpnppcggbcgedagnkighmdlei)
- **Firefox**: [LiveReload](https://addons.mozilla.org/en-US/firefox/addon/livereload-web-extension/)

拡張機能をインストール後、アイコンをクリックして有効化します。

### パフォーマンスに関する注意事項

- ホットリロード機能は開発環境でのみ有効です
- 本番環境（`SPRING_PROFILES_ACTIVE=prod`）では自動的に無効化されます
- 大規模なプロジェクトでは、再起動に時間がかかる場合があります

### 設定ファイルの詳細

#### application-dev.yml

```yaml
spring:
  devtools:
    restart:
      enabled: true
      additional-paths: src/main
      exclude: static/**,public/**
      poll-interval: 2000      # ファイル変更のポーリング間隔（ミリ秒）
      quiet-period: 1000       # 変更検出後の待機時間（ミリ秒）
    livereload:
      enabled: true
      port: 35729
```

#### docker-compose.yml

```yaml
services:
  app:
    ports:
      - "8080:8080"
      - "35729:35729"  # DevTools LiveReloadポート
    volumes:
      - ./src:/app/src  # ソースコードのホットリロード用
```

## その他のDocker操作

### コンテナの状態確認

```bash
docker-compose ps
```

### ログの確認

```bash
# アプリケーションコンテナのログ
docker logs spring-boot-app

# データベースコンテナのログ
docker logs postgres-db

# リアルタイムでログを表示
docker logs -f spring-boot-app
```

### コンテナの停止

```bash
docker-compose down
```

### コンテナとボリュームの削除

```bash
docker-compose down -v
```

### コンテナ内でコマンドを実行

```bash
# アプリケーションコンテナ内でシェルを起動
docker-compose exec app bash

# データベースコンテナ内でpsqlを起動
docker-compose exec postgres psql -U appuser -d appdb
```

### ビルドキャッシュのクリア

```bash
docker-compose build --no-cache
```

## まとめ

ホットリロード機能により、以下のメリットが得られます：

- ✅ テンプレートファイルの変更が即座に反映される
- ✅ コンテナの再起動が不要
- ✅ 開発サイクルが高速化される
- ✅ 開発体験が向上する

問題が発生した場合は、このガイドのトラブルシューティングセクションを参照してください。

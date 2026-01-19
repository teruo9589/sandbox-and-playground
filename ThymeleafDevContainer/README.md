# Spring Boot + Thymeleaf 開発環境

## 概要

このプロジェクトは、Thymeleafを使用したSpring Boot開発環境をDockerコンテナ内で構築するためのテンプレートです。既存のThymeleaf HTMLファイルを活用しながら、バックエンドのビジネスロジックを実行・検証できる完全な開発環境を提供します。

### 主な特徴

- **コンテナ化された開発環境**: Docker Composeを使用した簡単なセットアップ
- **ホットリロード対応**: テンプレートやコードの変更が即座に反映（Spring Boot DevTools使用）
- **品質管理ツール統合**: CheckStyle、JaCoCo、SpotBugsによる自動品質チェック
- **本番環境対応**: AWS環境へのデプロイを想定した設定
- **3層アーキテクチャ**: Controller、Service、Repositoryの明確な分離

### アーキテクチャ

```
┌─────────────────┐    ┌─────────────────┐
│   ブラウザ      │────│ Spring Boot App │
└─────────────────┘    │ (Port: 8080)    │
                       │ - Controller    │
                       │ - Service       │
                       │ - Repository    │
                       │ - Thymeleaf     │
                       └─────────┬───────┘
                                 │
                       ┌─────────▼───────┐
                       │   PostgreSQL    │
                       │   (Port: 5432)  │
                       └─────────────────┘
```

## 技術スタック

- **Java**: 21
- **Spring Boot**: 3.4.0
- **Thymeleaf**: 3.x
- **データベース**: PostgreSQL 16
- **ORM**: Doma 3.x
- **ビルドツール**: Gradle 8.5
- **品質管理ツール**: CheckStyle, JaCoCo, SpotBugs
- **コンテナ**: Docker & Docker Compose

## 前提条件

### 必須
- **Docker**: 20.10以上
- **Docker Compose**: 2.0以上

### 推奨（ローカル開発時）
- **Java**: 21以上
- **Git**: 最新版
- **Git Bash**: Windows環境での推奨ターミナル

### ターミナル環境について

このプロジェクトのコマンドはBash環境での実行を前提としています：

- **Windows**: Git Bashの使用を推奨
- **macOS/Linux**: 標準のターミナル

Windows環境でPowerShellやコマンドプロンプトを使用する場合は、一部のコマンドを適宜読み替えてください。

## クイックスタート

### Git Bash環境での起動手順

#### 1. プロジェクトのクローン

```bash
git clone <repository-url>
cd spring-boot-thymeleaf-dev-env
```

#### 2. Docker環境の起動

```bash
# すべてのコンテナを起動（初回は自動的にイメージをビルド）
docker-compose up -d

# 起動状況を確認
docker-compose ps

# ログを確認（オプション）
docker-compose logs -f app
```

#### 3. アプリケーションへのアクセス

ブラウザで以下のURLにアクセス：
- **アプリケーション**: http://localhost:8080
- **ヘルスチェック**: http://localhost:8080/actuator/health
- **データベース**: localhost:5432（外部接続用）

### 4. 環境の動作確認

Docker環境が正常に動作していることを確認：

```bash
# コンテナの状態確認
docker-compose ps

# アプリケーションログの確認
docker-compose logs app | tail -20

# データベースの接続確認
docker-compose exec postgres psql -U appuser -d appdb -c "SELECT version();"

# ヘルスチェックエンドポイントの確認（curlが利用可能な場合）
curl http://localhost:8080/actuator/health

# または自動ヘルスチェックスクリプトを実行
chmod +x docker-health-check.sh
./docker-health-check.sh
```

**期待される結果**:
- すべてのコンテナが `Up` 状態
- アプリケーションが http://localhost:8080 でアクセス可能
- ヘルスチェックで `{"status":"UP"}` が返される
- データベース接続が正常に確立されている

### 5. 開発完了後の停止

```bash
# コンテナを停止
docker-compose down

# データも含めて完全にクリーンアップ
docker-compose down -v
```

## セットアップ詳細

### Docker環境でのセットアップ（推奨）

#### 初回セットアップ

```bash
# 1. 環境変数ファイルの作成（オプション）
cp .env.example .env

# 2. コンテナのビルドと起動
docker-compose up -d --build

# 3. データベースの初期化確認
docker-compose logs postgres | grep "database system is ready"

# 4. アプリケーションの起動確認
docker-compose logs app | grep "Started Application"
```

#### 開発中の操作

```bash
# アプリケーションコンテナのみ再起動
docker-compose restart app

# 特定のコンテナのログを監視
docker-compose logs -f app

# コンテナ内でコマンド実行
docker-compose exec app bash

# Gradleタスクの実行
docker-compose exec app ./gradlew test
docker-compose exec app ./gradlew checkstyleMain
```

### ローカル環境でのセットアップ

Docker環境が利用できない場合のローカルセットアップ手順：

#### 前提条件
- Java 21以上
- PostgreSQL 16以上
- Gradle 8.5以上（または./gradlewを使用）

#### セットアップ手順

```bash
# 1. データベースの作成
createdb appdb
psql appdb < docker/init.sql

# 2. 環境変数の設定
export DB_PASSWORD=your_password
export SPRING_PROFILES_ACTIVE=dev

# 3. 依存関係のダウンロード
./gradlew dependencies

# 4. アプリケーションの起動
./gradlew bootRun
```

## プロジェクト構造

```
spring-boot-thymeleaf-dev-env/
├── .kiro/                          # Kiro設定ファイル
│   └── specs/                      # 仕様書
├── config/                         # 品質管理ツール設定
│   ├── checkstyle/
│   │   └── checkstyle.xml         # CheckStyle設定
│   └── spotbugs/
│       └── exclude.xml            # SpotBugs除外設定
├── docker/                         # Docker関連ファイル
│   └── init.sql                   # データベース初期化スクリプト
├── src/
│   ├── main/
│   │   ├── java/com/example/app/
│   │   │   ├── Application.java           # メインクラス
│   │   │   ├── config/                    # 設定クラス
│   │   │   │   └── DatabaseHealthCheck.java
│   │   │   ├── controller/                # コントローラー層
│   │   │   │   └── SampleController.java
│   │   │   ├── entity/                    # エンティティ
│   │   │   │   └── SampleEntity.java
│   │   │   ├── exception/                 # 例外ハンドラー
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── repository/                # リポジトリ層
│   │   │   │   └── SampleRepository.java
│   │   │   └── service/                   # サービス層
│   │   │       └── SampleService.java
│   │   └── resources/
│   │       ├── application.yml            # 共通設定
│   │       ├── application-dev.yml        # 開発環境設定
│   │       ├── application-prod.yml       # 本番環境設定
│   │       ├── logback-spring.xml         # ログ設定
│   │       ├── static/                    # 静的リソース
│   │       │   ├── css/style.css
│   │       │   ├── js/main.js
│   │       │   └── images/
│   │       ├── templates/                 # Thymeleafテンプレート
│   │       │   ├── index.html
│   │       │   └── error.html
│   │       └── META-INF/                  # Doma SQLファイル
│   └── test/                              # テストコード
│       ├── java/com/example/app/
│       └── resources/
├── build.gradle                           # Gradleビルド設定
├── settings.gradle                        # Gradleプロジェクト設定
├── docker-compose.yml                     # 開発環境Docker設定
├── docker-compose.prod.yml                # 本番環境Docker設定
├── docker-health-check.sh                 # Docker環境ヘルスチェックスクリプト
├── Dockerfile.dev                         # 開発環境Dockerfile
├── Dockerfile                             # 本番環境Dockerfile
├── .dockerignore                          # Docker除外設定
├── .env.example                           # 環境変数テンプレート
└── README.md                              # このファイル
```

## 開発ワークフロー

### ホットリロード機能

このプロジェクトでは、Spring Boot DevToolsを使用したホットリロード機能を提供しています：

#### 対応する変更
- **Thymeleafテンプレート** (`src/main/resources/templates/`): 即座に反映
- **静的リソース** (`src/main/resources/static/`): 即座に反映
- **Javaソースコード** (`src/main/java/`): 自動再起動で反映
- **設定ファイル** (`src/main/resources/`): 自動再起動で反映

#### ホットリロード機能のテスト方法

1. **Docker環境を起動**
   ```bash
   docker-compose up -d
   ```

2. **ブラウザでアプリケーションにアクセス**
   ```
   http://localhost:8080
   ```

3. **テンプレートファイルを変更**
   ```bash
   # src/main/resources/templates/index.html を編集
   # 例: "ホットリロード機能が有効です" のテキストを変更
   ```

4. **ブラウザをリフレッシュして変更を確認**
   - アプリケーションの再起動なしに変更が反映されます

5. **CSSファイルを変更**
   ```bash
   # src/main/resources/static/css/style.css を編集
   # 例: .hot-reload-test のbackground-colorを変更
   ```

6. **ブラウザをリフレッシュして変更を確認**

#### LiveReload機能（オプション）

ブラウザの自動リフレッシュ機能を使用する場合：

1. **ブラウザ拡張機能をインストール**
   - Chrome: [LiveReload Extension](https://chrome.google.com/webstore/detail/livereload/jnihajbhpnppcggbcgedagnkighmdlei)
   - Firefox: [LiveReload Add-on](https://addons.mozilla.org/en-US/firefox/addon/livereload-web-extension/)

2. **拡張機能を有効化**
   - ポート35729でLiveReloadサーバーに接続

3. **ファイル変更時に自動でブラウザがリフレッシュされます**

### 日常的な開発作業

1. **環境の起動**
   ```bash
   docker-compose up -d
   ```

2. **コード変更**
   - `src/main/java/` 配下のJavaファイルを編集
   - `src/main/resources/templates/` 配下のHTMLテンプレートを編集
   - 変更は自動的に反映されます（ホットリロード）

3. **テストの実行**
   ```bash
   docker-compose exec app ./gradlew test
   ```

4. **品質チェック**
   ```bash
   # すべての品質チェックを実行
   docker-compose exec app ./gradlew check
   
   # 個別実行
   docker-compose exec app ./gradlew checkstyleMain
   docker-compose exec app ./gradlew jacocoTestReport
   docker-compose exec app ./gradlew spotbugsMain
   ```

5. **環境の停止**
   ```bash
   docker-compose down
   ```

### ブランチ作業

```bash
# 新機能ブランチの作成
git checkout -b feature/new-feature

# 開発環境で動作確認
docker-compose up -d

# テストとビルドの確認
docker-compose exec app ./gradlew clean build

# コミット前の最終チェック
docker-compose exec app ./gradlew check
```

## Docker環境の詳細

### コンテナ構成

| コンテナ名 | ポート | 説明 |
|-----------|--------|------|
| app | 8080:8080 | Spring Bootアプリケーション |
| postgres | 5432:5432 | PostgreSQLデータベース |

### 環境変数

開発環境で使用される主な環境変数：

| 変数名 | デフォルト値 | 説明 |
|--------|-------------|------|
| `SPRING_PROFILES_ACTIVE` | dev | アクティブプロファイル |
| `DB_PASSWORD` | devpassword | データベースパスワード |
| `POSTGRES_DB` | appdb | データベース名 |
| `POSTGRES_USER` | appuser | データベースユーザー |

### ボリュームマウント

- `./src:/app/src` - ソースコードのホットリロード用
- `gradle-cache:/root/.gradle` - Gradleキャッシュの永続化
- `postgres-data:/var/lib/postgresql/data` - データベースデータの永続化

### Docker Composeコマンド一覧

#### 基本操作
```bash
# 起動（バックグラウンド）
docker-compose up -d

# 起動（フォアグラウンド、ログ表示）
docker-compose up

# 停止
docker-compose down

# 停止（ボリュームも削除）
docker-compose down -v

# 再起動
docker-compose restart

# 特定のサービスのみ再起動
docker-compose restart app
```

#### 監視・デバッグ
```bash
# 全サービスの状態確認
docker-compose ps

# ログの確認
docker-compose logs
docker-compose logs -f app
docker-compose logs --tail=100 postgres

# コンテナ内でコマンド実行
docker-compose exec app bash
docker-compose exec postgres psql -U appuser -d appdb
```

#### ビルド・更新
```bash
# イメージを再ビルドして起動
docker-compose up -d --build

# 特定のサービスのみ再ビルド
docker-compose build app

# イメージの削除
docker-compose down --rmi all
```

## Gradleタスク

### ビルド関連
```bash
# プロジェクトのビルド
./gradlew build

# クリーンビルド
./gradlew clean build

# 依存関係のダウンロード
./gradlew dependencies

# アプリケーションの起動
./gradlew bootRun
```

### テスト関連
```bash
# 全テストの実行
./gradlew test

# 特定のテストクラスのみ実行
./gradlew test --tests "com.example.app.SampleServiceTest"

# テストレポートの生成
./gradlew test jacocoTestReport
```

### 品質管理
```bash
# 全品質チェックの実行
./gradlew check

# CheckStyle（コーディング規約チェック）
./gradlew checkstyleMain
./gradlew checkstyleTest

# JaCoCo（カバレッジレポート）
./gradlew jacocoTestReport
./gradlew jacocoTestCoverageVerification

# SpotBugs（静的解析）
./gradlew spotbugsMain
./gradlew spotbugsTest
```

### レポート確認
```bash
# テストレポート
# Linux/macOS
xdg-open build/reports/tests/test/index.html  # Linux
open build/reports/tests/test/index.html      # macOS

# Windows（Git Bash）
start build/reports/tests/test/index.html

# カバレッジレポート
# Linux/macOS
xdg-open build/reports/jacoco/test/html/index.html  # Linux
open build/reports/jacoco/test/html/index.html      # macOS

# Windows（Git Bash）
start build/reports/jacoco/test/html/index.html

# CheckStyleレポート
# Linux/macOS
xdg-open build/reports/checkstyle/main.html  # Linux
open build/reports/checkstyle/main.html      # macOS

# Windows（Git Bash）
start build/reports/checkstyle/main.html

# SpotBugsレポート
# Linux/macOS
xdg-open build/reports/spotbugs/main.html  # Linux
open build/reports/spotbugs/main.html      # macOS

# Windows（Git Bash）
start build/reports/spotbugs/main.html
```

## トラブルシューティング

### よくある問題と解決方法

#### 1. Docker関連の問題

**問題**: `docker-compose up` でポートが既に使用されているエラー
```
Error: bind: address already in use
```

**解決方法**:
```bash
# 使用中のポートを確認（Linux/macOS）
netstat -tulpn | grep :8080
netstat -tulpn | grep :5432

# Windows（Git Bash）の場合
netstat -an | grep :8080
netstat -an | grep :5432

# または、Dockerコンテナのポートマッピングを確認
docker-compose ps

# 該当プロセスを停止するか、docker-compose.ymlでポート番号を変更
# 例: "8081:8080" に変更
```

**問題**: コンテナが起動しない
```bash
# コンテナの状態を確認
docker-compose ps

# ログを確認
docker-compose logs app
docker-compose logs postgres

# イメージを再ビルド
docker-compose down
docker-compose up -d --build
```

**問題**: データベース接続エラー
```
Connection refused: postgres:5432
```

**解決方法**:
```bash
# PostgreSQLコンテナの起動を確認
docker-compose logs postgres | grep "ready to accept connections"

# データベースに直接接続してテスト
docker-compose exec postgres psql -U appuser -d appdb

# アプリケーションの再起動
docker-compose restart app
```

#### 2. ビルド・テスト関連の問題

**問題**: Gradleビルドが失敗する
```bash
# Gradleキャッシュをクリア
./gradlew clean

# 依存関係を再ダウンロード
./gradlew build --refresh-dependencies

# Gradleデーモンを停止
./gradlew --stop
```

**問題**: テストが失敗する
```bash
# 特定のテストのみ実行
./gradlew test --tests "com.example.app.ApplicationTests"

# テスト結果の詳細を確認
./gradlew test --info

# テストレポートを確認
# Linux/macOS
xdg-open build/reports/tests/test/index.html  # Linux
open build/reports/tests/test/index.html      # macOS

# Windows（Git Bash）
start build/reports/tests/test/index.html
```

**問題**: CheckStyleエラー
```bash
# CheckStyleレポートを確認
./gradlew checkstyleMain

# Linux/macOS
xdg-open build/reports/checkstyle/main.html  # Linux
open build/reports/checkstyle/main.html      # macOS

# Windows（Git Bash）
start build/reports/checkstyle/main.html

# 自動修正可能な場合
./gradlew spotlessApply  # Spotlessプラグインが設定されている場合
```

#### 3. アプリケーション関連の問題

**問題**: アプリケーションが起動しない
```bash
# アプリケーションログを確認
docker-compose logs -f app

# Java プロセスの確認
docker-compose exec app ps aux | grep java

# 設定ファイルの確認
docker-compose exec app cat /app/src/main/resources/application-dev.yml
```

**問題**: Thymeleafテンプレートが見つからない
```
TemplateInputException: Error resolving template "index"
```

**解決方法**:
```bash
# テンプレートファイルの存在確認
ls -la src/main/resources/templates/

# ボリュームマウントの確認
docker-compose exec app ls -la /app/src/main/resources/templates/

# コンテナの再起動
docker-compose restart app
```

**問題**: 静的リソース（CSS/JS）が読み込まれない
```bash
# 静的リソースの確認
ls -la src/main/resources/static/

# ブラウザの開発者ツールでネットワークタブを確認
# 404エラーの場合はパスを確認
```

#### 4. データベース関連の問題

**問題**: データベースの初期化に失敗
```bash
# 初期化スクリプトの確認
cat docker/init.sql

# PostgreSQLコンテナの再作成
docker-compose down -v
docker-compose up -d

# 手動でスクリプト実行
docker-compose exec postgres psql -U appuser -d appdb -f /docker-entrypoint-initdb.d/init.sql
```

**問題**: データが永続化されない
```bash
# ボリュームの確認
docker volume ls | grep postgres

# データディレクトリの確認
docker-compose exec postgres ls -la /var/lib/postgresql/data/
```

#### 5. パフォーマンス関連の問題

**問題**: アプリケーションの起動が遅い
```bash
# JVMヒープサイズの調整（docker-compose.yml）
environment:
  - JAVA_OPTS=-Xmx512m -Xms256m

# Gradleデーモンの使用
./gradlew bootRun --daemon
```

**問題**: ホットリロードが効かない
```bash
# ボリュームマウントの確認
docker-compose exec app ls -la /app/src/

# Spring Boot DevToolsの設定確認
docker-compose exec app grep -r "spring-boot-devtools" /app/build.gradle

# DevTools設定の確認
docker-compose exec app cat /app/src/main/resources/application-dev.yml | grep -A 10 devtools

# コンテナの再起動
docker-compose restart app

# ログでDevToolsの動作を確認
docker-compose logs app | grep -i devtools
```

**重要**: Dockerコンテナでのホットリロードには制限があります：

1. **ファイルシステムの違い**
   - Windowsホスト上のDockerでは、ボリュームマウントされたファイルの変更イベントが正しく伝播しないことがあります
   - これは、WindowsファイルシステムとLinuxファイルシステムの違いによるものです

2. **推奨される開発方法**
   - **ローカル開発**: IDEでアプリケーションを直接実行（`./gradlew bootRun`）すると、ホットリロードが正常に動作します
   - **Docker開発**: テンプレート変更後は`docker-compose restart app`を実行するか、コンテナ内で直接編集します

3. **代替方法**
   ```bash
   # 方法1: コンテナを再起動（推奨）
   docker-compose restart app
   
   # 方法2: 手動でファイルをコピー
   docker cp src/main/resources/templates/index.html spring-boot-app:/app/src/main/resources/templates/index.html
   
   # 方法3: コンテナ内で直接編集
   docker-compose exec app bash
   vi /app/src/main/resources/templates/index.html
   ```

詳細は`DOCKER_GUIDE.md`を参照してください。

**問題**: LiveReloadが動作しない
```bash
# LiveReloadポートの確認
docker-compose ps | grep 35729

# ブラウザ拡張機能の確認
# - LiveReload拡張機能がインストールされているか確認
# - 拡張機能が有効になっているか確認
# - ポート35729に接続されているか確認

# ファイアウォール設定の確認（Windows）
# ポート35729がブロックされていないか確認
```

### ログの確認方法

#### アプリケーションログ
```bash
# リアルタイムでログを監視
docker-compose logs -f app

# 最新100行のログを表示
docker-compose logs --tail=100 app

# 特定の時間範囲のログ
docker-compose logs --since="2024-01-01T00:00:00" app
```

#### データベースログ
```bash
# PostgreSQLログの確認
docker-compose logs postgres

# 接続ログの確認
docker-compose logs postgres | grep "connection"

# エラーログの確認
docker-compose logs postgres | grep "ERROR"
```

### 環境のリセット

完全に環境をリセットしたい場合：

```bash
# 1. すべてのコンテナとボリュームを削除
docker-compose down -v

# 2. イメージも削除
docker-compose down --rmi all

# 3. 未使用のDockerリソースをクリーンアップ
docker system prune -a

# 4. 環境を再構築
docker-compose up -d --build
```

### サポート情報

問題が解決しない場合は、以下の情報を含めてサポートに連絡してください：

1. **環境情報**
   ```bash
   docker --version
   docker-compose --version
   java --version
   ```

2. **エラーログ**
   ```bash
   docker-compose logs > logs.txt
   ```

3. **設定ファイル**
   - `docker-compose.yml`
   - `application-dev.yml`
   - `build.gradle`

## 本番環境へのデプロイ

### AWS環境での構成例

本プロジェクトは、AWS環境での本番デプロイを想定して設計されています：

- **コンピューティング**: ECS Fargate または EC2
- **データベース**: RDS for PostgreSQL
- **ロードバランサー**: Application Load Balancer
- **ログ管理**: CloudWatch Logs

### 本番環境用の設定

```bash
# 本番環境用のDockerイメージをビルド
docker build -f Dockerfile -t spring-boot-app:prod .

# 本番環境用のDocker Composeファイルを使用
docker-compose -f docker-compose.prod.yml up -d
```

### 環境変数の設定

本番環境では以下の環境変数を設定してください：

```bash
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=jdbc:postgresql://your-rds-endpoint:5432/appdb
export DB_USERNAME=your-username
export DB_PASSWORD=your-secure-password
```

## 参考資料

- [Spring Boot公式ドキュメント](https://spring.io/projects/spring-boot)
- [Thymeleaf公式ドキュメント](https://www.thymeleaf.org/documentation.html)
- [Doma公式ドキュメント](https://doma.readthedocs.io/)
- [Docker Compose公式ドキュメント](https://docs.docker.com/compose/)
- [PostgreSQL公式ドキュメント](https://www.postgresql.org/docs/)

## ライセンス

このプロジェクトはMITライセンスの下で公開されています。詳細は[LICENSE](LICENSE)ファイルを参照してください。

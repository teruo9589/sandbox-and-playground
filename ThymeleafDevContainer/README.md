# Spring Boot + Thymeleaf 開発環境

Thymeleafを使用したSpring Boot開発環境をDockerコンテナ内で構築するプロジェクトです。

## 技術スタック

- **Java**: 21
- **Spring Boot**: 3.4.0
- **Thymeleaf**: 3.x
- **データベース**: PostgreSQL 16
- **ORM**: Doma 3.x
- **ビルドツール**: Gradle 8.5
- **品質管理ツール**: CheckStyle, JaCoCo, SpotBugs

## 前提条件

- Java 21以上がインストールされていること
- Dockerがインストールされていること
- Docker Composeがインストールされていること

## セットアップ

### 1. Gradleラッパーの初期化

プロジェクトを初めて使用する場合、Gradleラッパーを初期化する必要があります。

システムにGradleがインストールされている場合:

```bash
gradle wrapper --gradle-version 8.5
```

Gradleがインストールされていない場合は、[Gradle公式サイト](https://gradle.org/install/)からインストールしてください。

### 2. ビルドの実行

```bash
# Windows
.\gradlew.bat build

# Linux/Mac
./gradlew build
```

### 3. テストの実行

```bash
# Windows
.\gradlew.bat test

# Linux/Mac
./gradlew test
```

### 4. 品質チェックの実行

```bash
# CheckStyle
.\gradlew.bat checkstyleMain

# JaCoCo (カバレッジレポート)
.\gradlew.bat jacocoTestReport

# SpotBugs
.\gradlew.bat spotbugsMain
```

## プロジェクト構造

```
.
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/app/
│   │   │       └── Application.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── templates/
│   └── test/
│       └── java/
│           └── com/example/app/
│               └── ApplicationTests.java
├── config/
│   └── checkstyle/
│       └── checkstyle.xml
├── build.gradle
├── settings.gradle
└── README.md
```

## Docker環境での起動

### クイックスタート

```bash
# すべてのコンテナを起動
docker-compose up -d

# ログを確認
docker-compose logs -f

# ブラウザでアクセス
# http://localhost:8080
```

### 環境の停止

```bash
# コンテナを停止
docker-compose down

# コンテナとボリュームを削除（データも削除）
docker-compose down -v
```

### よく使うコマンド

```bash
# コンテナの状態確認
docker-compose ps

# 特定のコンテナのログを確認
docker-compose logs app
docker-compose logs postgres

# コンテナを再起動
docker-compose restart

# イメージを再ビルドして起動
docker-compose up -d --build
```

詳細なDocker環境の使い方については、[DOCKER_GUIDE.md](DOCKER_GUIDE.md)を参照してください。

## ローカル環境での起動（Dockerを使わない場合）

### 前提条件

- PostgreSQLがローカルにインストールされていること
- データベース `appdb` が作成されていること

### 起動方法

```bash
# 開発環境プロファイルで起動
.\gradlew.bat bootRun --args='--spring.profiles.active=dev'

# または
.\gradlew.bat bootRun
```

## ライセンス

このプロジェクトはMITライセンスの下で公開されています。

# 要件定義書

## はじめに

本ドキュメントは、既存のSpring Boot + Thymeleafアプリケーションに対して、Visual Studio CodeのDevContainer機能を使用した開発環境を構築するための要件を定義します。既存のDocker環境（docker-compose.yml、Dockerfile.dev）との整合性を保ちながら、開発者がコンテナ内で直接開発できる環境を提供します。

## 用語集

- **DevContainer**: Visual Studio CodeのRemote - Containers拡張機能を使用して、コンテナ内で開発を行うための環境
- **System**: VS Code DevContainer環境全体を指す
- **App_Container**: アプリケーションが実行されるDockerコンテナ
- **DB_Container**: PostgreSQLデータベースが実行されるDockerコンテナ
- **VS_Code**: Visual Studio Codeエディタ
- **Hot_Reload**: ソースコード変更時に自動的にアプリケーションを再起動する機能
- **Docker_Compose**: 複数のDockerコンテナを定義・実行するためのツール

## 要件

### 要件1: DevContainer設定ファイルの作成

**ユーザーストーリー:** 開発者として、VS CodeでDevContainerを使用した開発環境を起動できるようにしたい。そうすることで、ローカル環境に依存せずに一貫した開発環境を利用できる。

#### 受入基準

1. WHEN 開発者がプロジェクトをVS Codeで開く THEN THE System SHALL .devcontainer/devcontainer.jsonファイルを検出し、DevContainerとして開くオプションを提示する
2. THE System SHALL devcontainer.jsonにコンテナ名、イメージ、ポートマッピング、環境変数を定義する
3. THE System SHALL 既存のdocker-compose.ymlを参照してサービスを起動する設定を含む
4. THE System SHALL VS Code拡張機能のインストール設定を含む
5. WHERE DevContainerが起動される THE System SHALL PostgreSQLコンテナも同時に起動する

### 要件2: 開発環境の一貫性

**ユーザーストーリー:** 開発者として、DevContainer環境と既存のDocker環境で同じ動作を保証したい。そうすることで、環境の違いによる問題を防ぐことができる。

#### 受入基準

1. THE System SHALL 既存のDockerfile.devと同じベースイメージ（gradle:8.5-jdk21）を使用する
2. THE System SHALL 既存のdocker-compose.ymlで定義されたポート（8080、35729、5432）を同じようにマッピングする
3. THE System SHALL 既存の環境変数（SPRING_PROFILES_ACTIVE、DB_PASSWORD）を同じように設定する
4. THE System SHALL Gradleキャッシュボリュームを既存の設定と同じように永続化する
5. THE System SHALL PostgreSQLのヘルスチェック設定を既存の設定と同じように適用する

### 要件3: ホットリロード機能の維持

**ユーザーストーリー:** 開発者として、DevContainer環境でもホットリロード機能を使用したい。そうすることで、コード変更を即座に確認できる。

#### 受入基準

1. THE System SHALL ソースコードディレクトリ（./src）をコンテナにマウントする
2. WHEN ソースコードが変更される THEN THE System SHALL Spring Boot DevToolsによる自動再起動を実行する
3. THE System SHALL LiveReloadポート（35729）を公開し、ブラウザの自動リフレッシュを可能にする
4. THE System SHALL Gradleのビルドキャッシュを保持し、再ビルド時間を最小化する

### 要件4: VS Code統合機能

**ユーザーストーリー:** 開発者として、VS Code内でシームレスに開発作業を行いたい。そうすることで、生産性を向上させることができる。

#### 受入基準

1. THE System SHALL Java開発に必要なVS Code拡張機能を自動的にインストールする
2. THE System SHALL Spring Boot開発に必要なVS Code拡張機能を自動的にインストールする
3. THE System SHALL Docker関連のVS Code拡張機能を自動的にインストールする
4. THE System SHALL コンテナ内のターミナルをVS Codeで直接使用できるようにする
5. THE System SHALL VS Codeのデバッガーがコンテナ内のJavaプロセスにアタッチできるようにする

### 要件5: データベース接続設定

**ユーザーストーリー:** 開発者として、DevContainer環境からPostgreSQLデータベースに接続できるようにしたい。そうすることで、データベースを使用した開発とテストが可能になる。

#### 受入基準

1. THE System SHALL PostgreSQLコンテナをDevContainerと同じネットワークに配置する
2. WHEN アプリケーションが起動する THEN THE System SHALL データベース接続を確立する
3. THE System SHALL データベースの初期化スクリプト（docker/init.sql）を実行する
4. THE System SHALL データベースデータを永続化ボリュームに保存する
5. WHEN PostgreSQLコンテナが起動していない THEN THE System SHALL アプリケーションコンテナの起動を待機する

### 要件6: 開発者体験の向上

**ユーザーストーリー:** 開発者として、DevContainer環境で快適に開発作業を行いたい。そうすることで、開発効率を最大化できる。

#### 受入基準

1. THE System SHALL コンテナ起動後に自動的にGradle依存関係をダウンロードする
2. THE System SHALL Git設定をホストからコンテナに引き継ぐ
3. THE System SHALL SSH認証情報をホストからコンテナに引き継ぐ
4. THE System SHALL コンテナ起動時にカスタム初期化スクリプトを実行できるようにする
5. THE System SHALL コンテナ内でGradleコマンドを直接実行できるようにする

### 要件7: ドキュメントとガイド

**ユーザーストーリー:** 開発者として、DevContainer環境のセットアップと使用方法を理解したい。そうすることで、スムーズに開発を開始できる。

#### 受入基準

1. THE System SHALL DevContainerのセットアップ手順を記載したドキュメントを提供する
2. THE System SHALL 必要なVS Code拡張機能のリストを記載したドキュメントを提供する
3. THE System SHALL トラブルシューティングガイドを提供する
4. THE System SHALL 既存のDocker環境との違いを説明したドキュメントを提供する
5. THE System SHALL よくある質問（FAQ）セクションを含むドキュメントを提供する

### 要件8: 既存環境との共存

**ユーザーストーリー:** 開発者として、DevContainer環境と既存のDocker環境を両方使用できるようにしたい。そうすることで、状況に応じて適切な環境を選択できる。

#### 受入基準

1. THE System SHALL DevContainer設定ファイルを追加しても既存のdocker-compose.ymlの動作に影響を与えない
2. THE System SHALL .gitignoreファイルを更新し、DevContainer固有の一時ファイルを除外する
3. THE System SHALL 既存のDockerfile.devを変更せずに使用する
4. THE System SHALL 既存のビルドスクリプトやテストスクリプトをそのまま使用できるようにする
5. THE System SHALL 既存のDOCKER_GUIDE.mdドキュメントを補完する形でDevContainerガイドを提供する

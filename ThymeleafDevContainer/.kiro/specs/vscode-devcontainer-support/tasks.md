# 実装計画: VS Code DevContainer対応

## 概要

既存のSpring Boot + Thymeleafアプリケーションに対して、Visual Studio CodeのDevContainer機能を統合します。既存のDocker環境を活用しながら、VS Code特有の設定を追加することで、開発者がコンテナ内でシームレスに開発できる環境を提供します。

## タスク

- [x] 1. DevContainer設定ファイルの作成
  - .devcontainer/devcontainer.jsonファイルを作成
  - 基本設定（name、dockerComposeFile、service、workspaceFolder）を定義
  - ポート転送設定（forwardPorts: 8080, 35729, 5432）を追加
  - コンテナ作成後コマンド（postCreateCommand）を設定
  - リモートユーザー（remoteUser: gradle）を設定
  - _要件: 1.1, 1.2, 1.3, 2.2, 3.3, 6.1_

- [x] 2. VS Code拡張機能の設定
  - [x] 2.1 Java開発用拡張機能を設定
    - vscjava.vscode-java-pack（Java開発基本パック）
    - vscjava.vscode-java-debug（Javaデバッガー）
    - vscjava.vscode-java-test（Javaテストランナー）
    - vscjava.vscode-maven（Maven/Gradle統合）
    - vscjava.vscode-gradle（Gradle統合）
    - _要件: 4.1_
  
  - [x] 2.2 Spring Boot開発用拡張機能を設定
    - vmware.vscode-spring-boot（Spring Boot拡張機能）
    - vscjava.vscode-spring-initializr（Spring Initializr統合）
    - vscjava.vscode-spring-boot-dashboard（Spring Bootダッシュボード）
    - _要件: 4.2_
  
  - [x] 2.3 フロントエンド開発用拡張機能を設定
    - ecmel.vscode-html-css（HTML/CSS統合）
    - dbaeumer.vscode-eslint（ESLint）
    - esbenp.prettier-vscode（Prettier）
    - formulahendry.auto-rename-tag（HTMLタグ自動リネーム）
    - zignd.html-css-class-completion（CSSクラス名補完）
    - xabikos.JavaScriptSnippets（JavaScript Snippets）
    - _要件: 4.1_
  
  - [x] 2.4 開発者体験向上用拡張機能を設定
    - streetsidesoftware.code-spell-checker（スペルチェッカー）
    - usernamehw.errorlens（Error Lens）
    - christian-kohler.path-intellisense（パス補完）
    - _要件: 4.1_
  
  - [x] 2.5 Docker/コンテナ用拡張機能を設定
    - ms-azuretools.vscode-docker（Docker統合）
    - _要件: 4.3_
  
  - [x] 2.6 コード品質用拡張機能を設定
    - sonarsource.sonarlint-vscode（SonarLint）
    - shengchen.vscode-checkstyle（CheckStyle統合）
    - _要件: 4.3_

- [x] 3. VS Code設定のカスタマイズ
  - customizations.vscode.settingsセクションを追加
  - Java言語サーバーの設定を追加
  - フォーマッター設定を追加（Prettier、Java）
  - _要件: 4.1_

- [x] 4. .gitignore設定の更新
  - DevContainer固有の一時ファイルパターンを追加
  - .devcontainer/.vscodeディレクトリの除外設定を確認
  - _要件: 8.2_

- [x] 5. チェックポイント - 設定ファイルの検証
  - 全ての設定ファイルが正しく作成されていることを確認
  - 質問があればユーザーに確認

- [ ] 6. DevContainerドキュメントの作成
  - [x] 6.1 .devcontainer/README.mdファイルを作成
    - DevContainerの概要を記載
    - _要件: 7.1_
  
  - [x] 6.2 セットアップ手順セクションを追加
    - 必要な前提条件（Docker Desktop、VS Code、Remote - Containers拡張機能）
    - DevContainerの起動手順
    - 初回起動時の注意事項
    - _要件: 7.1_
  
  - [x] 6.3 拡張機能リストセクションを追加
    - 自動インストールされる拡張機能の一覧
    - 各拡張機能の役割説明
    - _要件: 7.2_
  
  - [x] 6.4 トラブルシューティングセクションを追加
    - よくある問題と解決方法
    - Dockerデーモンが起動していない場合
    - ポートが既に使用されている場合
    - ボリュームマウントの権限エラー
    - 拡張機能のインストール失敗
    - _要件: 7.3_
  
  - [x] 6.5 既存Docker環境との違いセクションを追加
    - DevContainerと通常のdocker-compose upの違い
    - 使い分けのガイドライン
    - _要件: 7.4_
  
  - [x] 6.6 FAQセクションを追加
    - よくある質問と回答
    - ホットリロードは動作するか
    - データベースデータは永続化されるか
    - Git設定は引き継がれるか
    - _要件: 7.5, 8.5_

- [x] 7. チェックポイント - ドキュメントの確認
  - 全てのドキュメントが作成されていることを確認
  - 質問があればユーザーに確認

- [ ] 8. 設定ファイル検証テストの作成
  - [x] 8.1 devcontainer.json検証テストを作成
    - **プロパティ1: DevContainer設定ファイルの完全性**
    - **検証要件: 1.2, 1.3, 1.4, 2.2, 3.3, 6.1**
  
  - [~] 8.2 VS Code拡張機能検証テストを作成
    - **プロパティ2: VS Code拡張機能の完全性**
    - **検証要件: 4.1, 4.2, 4.3**
  
  - [x] 8.3 docker-compose.yml検証テストを作成
    - **プロパティ3: Docker Compose設定の整合性**
    - **検証要件: 2.1, 2.3, 3.1**
  
  - [~] 8.4 PostgreSQL設定検証テストを作成
    - **プロパティ4: PostgreSQL設定の完全性**
    - **検証要件: 5.1, 5.3, 5.4, 5.5**
  
  - [~] 8.5 .gitignore検証テストを作成
    - **プロパティ5: .gitignore設定の完全性**
    - **検証要件: 8.2**
  
  - [~] 8.6 ドキュメント検証テストを作成
    - **プロパティ6: ドキュメントの完全性**
    - **検証要件: 7.1, 7.2, 7.3, 7.4, 7.5, 8.5**

- [~] 9. 最終チェックポイント
  - 全ての設定ファイルとドキュメントが完成していることを確認
  - 質問があればユーザーに確認

## 注意事項

- 各タスクは特定の要件を参照しており、トレーサビリティを確保しています
- チェックポイントは段階的な検証を保証します
- プロパティテストは普遍的な正確性プロパティを検証します
- ユニットテストは特定の例とエッジケースを検証します

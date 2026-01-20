# VS Code DevContainer ガイド

## 概要

このプロジェクトは、Visual Studio CodeのDevContainer機能を使用して、コンテナ内で直接開発できる環境を提供します。DevContainerを使用することで、ローカル環境に依存せず、一貫した開発環境を利用できます。

### DevContainerとは

DevContainerは、Visual Studio CodeのRemote - Containers拡張機能を使用して、Dockerコンテナ内で開発を行うための環境です。以下の特徴があります：

- **一貫した開発環境**: チーム全体で同じ開発環境を共有できます
- **環境構築の簡素化**: Dockerコンテナを起動するだけで、必要なツールや拡張機能が自動的にセットアップされます
- **ローカル環境の汚染防止**: 開発に必要なツールやライブラリをコンテナ内に閉じ込めることができます
- **VS Codeとのシームレスな統合**: VS Code内でコンテナ内のファイルを直接編集、デバッグ、ターミナル操作が可能です

### このプロジェクトのDevContainer環境

このプロジェクトのDevContainer環境は、以下のコンポーネントで構成されています：

- **アプリケーションコンテナ**: Spring Boot + Thymeleafアプリケーションが実行されるコンテナ（Gradle 8.5 + JDK 21）
- **データベースコンテナ**: PostgreSQL 16データベースが実行されるコンテナ
- **VS Code拡張機能**: Java、Spring Boot、フロントエンド開発に必要な拡張機能が自動的にインストールされます
- **ホットリロード機能**: ソースコード変更時に自動的にアプリケーションが再起動されます

### 既存のDocker環境との関係

このDevContainer環境は、既存の`docker-compose.yml`と`Dockerfile.dev`を活用しています。そのため、以下の特徴があります：

- **既存環境との互換性**: 既存のDocker環境（`docker-compose up`）と同じ動作を保証します
- **共存可能**: DevContainer環境と既存のDocker環境を両方使用できます
- **設定の一元管理**: ポート、環境変数、ボリュームなどの設定は既存の`docker-compose.yml`で管理されます

### DevContainerの利点

DevContainerを使用することで、以下の利点が得られます：

1. **VS Code内で完結**: コンテナ内のファイルをVS Codeで直接編集できます
2. **拡張機能の自動インストール**: Java、Spring Boot、フロントエンド開発に必要な拡張機能が自動的にインストールされます
3. **デバッグ機能**: VS Codeのデバッガーがコンテナ内のJavaプロセスにアタッチできます
4. **ターミナル統合**: コンテナ内のターミナルをVS Codeで直接使用できます
5. **Git/SSH統合**: ホストのGit設定とSSH認証情報がコンテナに自動的に引き継がれます

### 次のステップ

DevContainer環境を使用するには、以下のセクションを参照してください：

- **セットアップ手順**: DevContainer環境の起動方法
- **拡張機能リスト**: 自動インストールされる拡張機能の一覧
- **トラブルシューティング**: よくある問題と解決方法
- **既存Docker環境との違い**: DevContainerと通常の`docker-compose up`の違い
- **FAQ**: よくある質問と回答

---

## セットアップ手順

このセクションでは、DevContainer環境を起動するための手順を説明します。

### 前提条件

DevContainer環境を使用するには、以下のソフトウェアがインストールされている必要があります：

#### 1. Docker Desktop

DevContainerはDockerコンテナを使用するため、Docker Desktopが必要です。

- **Windows/Mac**: [Docker Desktop](https://www.docker.com/products/docker-desktop)をダウンロードしてインストールしてください
- **Linux**: Docker EngineとDocker Composeをインストールしてください

**インストール確認**:
```bash
docker --version
docker-compose --version
```

**重要**: Docker Desktopが起動していることを確認してください。タスクバー（Windows）またはメニューバー（Mac）にDockerアイコンが表示されていれば、起動しています。

#### 2. Visual Studio Code

DevContainerはVS Codeの機能であるため、VS Codeが必要です。

- [Visual Studio Code](https://code.visualstudio.com/)をダウンロードしてインストールしてください

#### 3. Remote - Containers拡張機能

VS CodeでDevContainerを使用するには、Remote - Containers拡張機能が必要です。

**インストール方法**:
1. VS Codeを起動します
2. 拡張機能ビュー（`Ctrl+Shift+X` または `Cmd+Shift+X`）を開きます
3. "Remote - Containers"を検索します
4. "Dev Containers"（Microsoft製）をインストールします

または、以下のコマンドでインストールできます：
```bash
code --install-extension ms-vscode-remote.remote-containers
```

### DevContainerの起動手順

前提条件が満たされたら、以下の手順でDevContainer環境を起動します。

#### ステップ1: プロジェクトを開く

1. VS Codeを起動します
2. `ファイル` > `フォルダーを開く` を選択します
3. このプロジェクトのルートディレクトリを選択します

#### ステップ2: DevContainerで再度開く

プロジェクトを開くと、VS Codeが`.devcontainer/devcontainer.json`ファイルを検出し、右下に通知が表示されます：

```
フォルダーに Dev Container 構成ファイルが含まれています。
コンテナーで再度開きますか?
```

以下のいずれかの方法でDevContainerを起動します：

**方法1: 通知から起動**
- 通知の`コンテナーで再度開く`ボタンをクリックします

**方法2: コマンドパレットから起動**
1. コマンドパレット（`Ctrl+Shift+P` または `Cmd+Shift+P`）を開きます
2. "Dev Containers: Reopen in Container"を選択します

**方法3: 左下のアイコンから起動**
1. VS Codeの左下にある緑色のアイコン（`><`）をクリックします
2. "Reopen in Container"を選択します

#### ステップ3: コンテナのビルドと起動

初回起動時は、以下の処理が自動的に実行されます：

1. **Dockerイメージのビルド**: `Dockerfile.dev`を使用してアプリケーションコンテナのイメージをビルドします（数分かかる場合があります）
2. **コンテナの起動**: アプリケーションコンテナとPostgreSQLコンテナを起動します
3. **VS Code拡張機能のインストール**: Java、Spring Boot、フロントエンド開発に必要な拡張機能を自動的にインストールします
4. **Gradle依存関係のダウンロード**: `gradle dependencies --no-daemon`コマンドを実行し、依存関係を事前にダウンロードします

進行状況は、VS Codeの右下に表示されます。完了すると、コンテナ内のVS Code環境が開きます。

#### ステップ4: 起動確認

DevContainerが正常に起動したことを確認します：

1. **左下のアイコン**: VS Codeの左下に`Dev Container: Spring Boot DevContainer`と表示されていることを確認します
2. **ターミナル**: ターミナル（`Ctrl+` または `Cmd+`）を開き、コンテナ内のシェルが表示されることを確認します
3. **拡張機能**: 拡張機能ビューを開き、Java、Spring Boot関連の拡張機能がインストールされていることを確認します

### 初回起動時の注意事項

初回起動時には、以下の点に注意してください：

#### 1. 起動時間

初回起動時は、以下の処理が実行されるため、通常よりも時間がかかります（5〜10分程度）：

- Dockerイメージのビルド
- VS Code拡張機能のインストール
- Gradle依存関係のダウンロード

2回目以降の起動は、キャッシュが使用されるため、より高速になります（1〜2分程度）。

#### 2. ネットワーク接続

初回起動時は、以下のダウンロードが発生するため、安定したネットワーク接続が必要です：

- Dockerベースイメージ（gradle:8.5-jdk21）
- PostgreSQLイメージ（postgres:16）
- VS Code拡張機能
- Gradle依存関係（JARファイル）

#### 3. ディスク容量

DevContainer環境は、以下のディスク容量を使用します：

- Dockerイメージ: 約2〜3GB
- Gradleキャッシュ: 約500MB〜1GB
- PostgreSQLデータ: 約100MB〜（データ量に応じて増加）

十分なディスク容量があることを確認してください。

#### 4. メモリ使用量

DevContainer環境は、以下のメモリを使用します：

- アプリケーションコンテナ: 約1〜2GB
- PostgreSQLコンテナ: 約100〜200MB

Docker Desktopのメモリ設定が十分であることを確認してください（推奨: 4GB以上）。

#### 5. ポートの競合

DevContainer環境は、以下のポートを使用します：

- `8080`: Spring Bootアプリケーション
- `35729`: LiveReload（ホットリロード）
- `5432`: PostgreSQLデータベース

これらのポートが既に使用されている場合、コンテナの起動に失敗します。使用中のアプリケーションを停止するか、`docker-compose.yml`でポート番号を変更してください。

#### 6. 初回ビルドの失敗

初回ビルド時にネットワークエラーやタイムアウトが発生した場合は、以下を試してください：

1. Docker Desktopを再起動します
2. VS Codeを再起動します
3. コマンドパレットから"Dev Containers: Rebuild Container"を実行します

### アプリケーションの起動

DevContainer環境が起動したら、以下のコマンドでアプリケーションを起動できます：

```bash
./gradlew bootRun
```

アプリケーションが起動したら、ブラウザで以下のURLにアクセスできます：

- アプリケーション: http://localhost:8080

ソースコードを変更すると、Spring Boot DevToolsによって自動的にアプリケーションが再起動されます（ホットリロード）。

### DevContainerの停止

DevContainer環境を停止するには、以下のいずれかの方法を使用します：

**方法1: VS Codeを閉じる**
- VS Codeを閉じると、コンテナは自動的に停止します

**方法2: コマンドパレットから停止**
1. コマンドパレット（`Ctrl+Shift+P` または `Cmd+Shift+P`）を開きます
2. "Dev Containers: Reopen Folder Locally"を選択します

**方法3: 左下のアイコンから停止**
1. VS Codeの左下にある緑色のアイコン（`Dev Container: Spring Boot DevContainer`）をクリックします
2. "Reopen Folder Locally"を選択します

### 次のステップ

DevContainer環境が正常に起動したら、以下のセクションを参照してください：

- **拡張機能リスト**: 自動インストールされる拡張機能の詳細
- **トラブルシューティング**: 問題が発生した場合の解決方法
- **FAQ**: よくある質問と回答

---

## 拡張機能リスト

このセクションでは、DevContainer環境で自動的にインストールされるVS Code拡張機能の一覧と、各拡張機能の役割を説明します。

### 概要

DevContainer環境では、Java、Spring Boot、フロントエンド開発に必要な拡張機能が自動的にインストールされます。これにより、開発者は手動で拡張機能をインストールする必要がなく、すぐに開発を開始できます。

拡張機能は、`.devcontainer/devcontainer.json`ファイルの`customizations.vscode.extensions`セクションで定義されています。

### Java開発用拡張機能

Java開発に必要な基本的な拡張機能です。

#### 1. Extension Pack for Java (`vscjava.vscode-java-pack`)

**役割**: Java開発に必要な拡張機能をまとめたパックです。以下の拡張機能が含まれています：
- Language Support for Java(TM) by Red Hat
- Debugger for Java
- Test Runner for Java
- Maven for Java
- Project Manager for Java
- Visual Studio IntelliCode

**主な機能**:
- Javaコードの構文ハイライト
- コード補完（IntelliSense）
- リファクタリング機能
- コードナビゲーション（定義へジャンプ、参照の検索など）

#### 2. Debugger for Java (`vscjava.vscode-java-debug`)

**役割**: Javaアプリケーションのデバッグ機能を提供します。

**主な機能**:
- ブレークポイントの設定
- ステップ実行（ステップイン、ステップオーバー、ステップアウト）
- 変数の監視
- コールスタックの表示
- 条件付きブレークポイント

#### 3. Test Runner for Java (`vscjava.vscode-java-test`)

**役割**: JUnit、TestNGなどのJavaテストフレームワークをサポートします。

**主な機能**:
- テストの実行と結果表示
- テストエクスプローラーでのテスト一覧表示
- 個別テストまたはテストクラス全体の実行
- テストのデバッグ
- テストカバレッジの表示

#### 4. Maven for Java (`vscjava.vscode-maven`)

**役割**: Maven/Gradleプロジェクトの統合を提供します。

**主な機能**:
- Mavenプロジェクトの認識と管理
- 依存関係の管理
- Mavenコマンドの実行
- pom.xmlの編集サポート

#### 5. Gradle for Java (`vscjava.vscode-gradle`)

**役割**: Gradleプロジェクトの統合を提供します。

**主な機能**:
- Gradleプロジェクトの認識と管理
- Gradleタスクの実行
- 依存関係の管理
- build.gradleの編集サポート
- Gradleデーモンの管理

### Spring Boot開発用拡張機能

Spring Bootアプリケーション開発に特化した拡張機能です。

#### 6. Spring Boot Extension Pack (`vmware.vscode-spring-boot`)

**役割**: Spring Bootアプリケーション開発をサポートします。

**主な機能**:
- Spring Bootプロパティファイル（application.properties、application.yml）の編集サポート
- Spring Bootアノテーションの補完
- Spring Beanの定義へのナビゲーション
- Spring Boot設定のバリデーション
- ライブホバー情報（実行中のアプリケーションの情報表示）

#### 7. Spring Initializr Java Support (`vscjava.vscode-spring-initializr`)

**役割**: Spring Initializrを使用して新しいSpring Bootプロジェクトを作成できます。

**主な機能**:
- VS Code内からSpring Bootプロジェクトの生成
- 依存関係の選択
- プロジェクト設定のカスタマイズ
- Gradle/Mavenの選択

#### 8. Spring Boot Dashboard (`vscjava.vscode-spring-boot-dashboard`)

**役割**: Spring Bootアプリケーションの管理ダッシュボードを提供します。

**主な機能**:
- Spring Bootアプリケーションの起動/停止
- 実行中のアプリケーションの一覧表示
- アプリケーションログの表示
- デバッグモードでの起動
- 複数のSpring Bootアプリケーションの管理

### フロントエンド開発用拡張機能

Thymeleaf、HTML、CSS、JavaScript開発に必要な拡張機能です。

#### 9. HTML CSS Support (`ecmel.vscode-html-css`)

**役割**: HTMLとCSSの統合サポートを提供します。

**主な機能**:
- HTMLファイル内でのCSSクラス名の補完
- CSSセレクターの検証
- HTMLタグの補完
- CSSファイルへのナビゲーション

#### 10. ESLint (`dbaeumer.vscode-eslint`)

**役割**: JavaScriptコードの静的解析を行います。

**主な機能**:
- JavaScriptコードの構文エラー検出
- コーディング規約違反の検出
- 自動修正機能
- カスタムルールの設定

#### 11. Prettier - Code formatter (`esbenp.prettier-vscode`)

**役割**: コードの自動フォーマッターです。

**主な機能**:
- JavaScript、HTML、CSS、JSONなどのフォーマット
- 保存時の自動フォーマット
- 一貫したコードスタイルの維持
- カスタマイズ可能なフォーマット設定

#### 12. Auto Rename Tag (`formulahendry.auto-rename-tag`)

**役割**: HTMLタグの開始タグと終了タグを自動的に同期します。

**主な機能**:
- 開始タグを変更すると終了タグも自動的に変更
- 終了タグを変更すると開始タグも自動的に変更
- Thymeleafテンプレートでも動作

#### 13. IntelliSense for CSS class names in HTML (`zignd.html-css-class-completion`)

**役割**: HTMLファイル内でCSSクラス名の自動補完を提供します。

**主な機能**:
- プロジェクト内のCSSファイルからクラス名を抽出
- HTMLファイル内でクラス名の補完
- Thymeleafテンプレートでも動作

#### 14. JavaScript (ES6) code snippets (`xabikos.JavaScriptSnippets`)

**役割**: JavaScriptのコードスニペットを提供します。

**主な機能**:
- ES6+の構文スニペット
- よく使うパターンのスニペット
- 高速なコード入力
- カスタムスニペットの追加

### 開発者体験向上用拡張機能

開発効率を向上させる拡張機能です。

#### 15. Code Spell Checker (`streetsidesoftware.code-spell-checker`)

**役割**: コード内のスペルミスを検出します。

**主な機能**:
- コメント、文字列、変数名のスペルチェック
- カスタム辞書の追加
- 複数言語のサポート
- スペルミスの自動修正候補

#### 16. Error Lens (`usernamehw.errorlens`)

**役割**: エラーと警告をコードの横に直接表示します。

**主な機能**:
- エラーメッセージのインライン表示
- 警告メッセージのインライン表示
- エラーの視認性向上
- カスタマイズ可能な表示スタイル

#### 17. Path Intellisense (`christian-kohler.path-intellisense`)

**役割**: ファイルパスの自動補完を提供します。

**主な機能**:
- ファイルパスの自動補完
- 相対パスと絶対パスのサポート
- インポート文でのパス補完
- Thymeleafテンプレートでのパス補完

### Docker/コンテナ用拡張機能

Docker環境の管理に必要な拡張機能です。

#### 18. Docker (`ms-azuretools.vscode-docker`)

**役割**: DockerコンテナとイメージをVS Code内で管理できます。

**主な機能**:
- Dockerコンテナの一覧表示と管理
- Dockerイメージの一覧表示と管理
- Dockerfileの編集サポート
- docker-compose.ymlの編集サポート
- コンテナのログ表示
- コンテナ内のシェルへのアクセス

### コード品質用拡張機能

コード品質を向上させる拡張機能です。

#### 19. SonarLint (`sonarsource.sonarlint-vscode`)

**役割**: コードの静的解析を行い、バグ、脆弱性、コードスメルを検出します。

**主な機能**:
- リアルタイムのコード解析
- セキュリティ脆弱性の検出
- コード品質の問題検出
- 修正方法の提案
- SonarQubeとの連携

#### 20. Checkstyle for Java (`shengchen.vscode-checkstyle`)

**役割**: Javaコードのコーディング規約チェックを行います。

**主な機能**:
- Checkstyleルールの適用
- コーディング規約違反の検出
- カスタムCheckstyle設定のサポート
- リアルタイムのチェック
- 修正方法の提案

### 拡張機能の確認方法

DevContainer環境で拡張機能が正しくインストールされているか確認するには、以下の手順を実行します：

1. VS Codeの拡張機能ビュー（`Ctrl+Shift+X` または `Cmd+Shift+X`）を開きます
2. 検索ボックスに`@installed`と入力します
3. インストールされている拡張機能の一覧が表示されます

### 追加の拡張機能

上記の拡張機能に加えて、個人的な好みに応じて追加の拡張機能をインストールすることもできます。ただし、DevContainer環境で自動的にインストールされる拡張機能は、`.devcontainer/devcontainer.json`ファイルで定義されたものに限られます。

個人的な拡張機能をインストールする場合は、以下の方法があります：

1. **ローカルにインストール**: ホストのVS Codeにインストールします（DevContainer環境では使用できません）
2. **DevContainer設定に追加**: `.devcontainer/devcontainer.json`の`extensions`配列に拡張機能IDを追加します（チーム全体で共有されます）

### 次のステップ

拡張機能の詳細を確認したら、以下のセクションを参照してください：

- **トラブルシューティング**: 拡張機能のインストールに問題が発生した場合の解決方法
- **FAQ**: よくある質問と回答

---

## トラブルシューティング

このセクションでは、DevContainer環境で発生する可能性のある問題と、その解決方法を説明します。

### 概要

DevContainer環境は、Docker、VS Code、複数のコンテナを組み合わせた複雑なシステムです。そのため、様々な問題が発生する可能性があります。このセクションでは、よくある問題とその解決方法を説明します。

問題が発生した場合は、以下の順序で確認してください：

1. **Dockerデーモンの状態**: Dockerが起動しているか確認します
2. **ポートの競合**: 必要なポートが使用可能か確認します
3. **ボリュームマウントの権限**: ファイルシステムのアクセス権限を確認します
4. **拡張機能のインストール**: 拡張機能が正しくインストールされているか確認します
5. **ログの確認**: VS CodeとDockerのログを確認します

### よくある問題と解決方法

#### 問題1: DevContainerが起動しない

**症状**:
- VS Codeで"Reopen in Container"を選択しても、コンテナが起動しない
- エラーメッセージが表示される
- 起動プロセスが途中で停止する

**原因**:
- Dockerデーモンが起動していない
- Docker Desktopのリソース不足
- ネットワーク接続の問題
- 設定ファイルの構文エラー

**解決方法**:

1. **Dockerデーモンの確認**:
   ```bash
   docker ps
   ```
   このコマンドが正常に実行されない場合は、Docker Desktopを起動してください。

2. **Docker Desktopの再起動**:
   - Docker Desktopを完全に終了します
   - Docker Desktopを再起動します
   - タスクバー（Windows）またはメニューバー（Mac）にDockerアイコンが表示されるまで待ちます

3. **VS Codeの再起動**:
   - VS Codeを完全に終了します
   - VS Codeを再起動します
   - プロジェクトを再度開きます

4. **コンテナの再ビルド**:
   - コマンドパレット（`Ctrl+Shift+P` または `Cmd+Shift+P`）を開きます
   - "Dev Containers: Rebuild Container"を選択します
   - コンテナが再ビルドされるまで待ちます

5. **ログの確認**:
   - コマンドパレットから"Dev Containers: Show Container Log"を選択します
   - エラーメッセージを確認し、具体的な問題を特定します

#### 問題2: Dockerデーモンが起動していない

**症状**:
- "Cannot connect to the Docker daemon"というエラーメッセージが表示される
- "Is the docker daemon running?"というメッセージが表示される
- `docker ps`コマンドが失敗する

**原因**:
- Docker Desktopが起動していない
- Docker Desktopのサービスが停止している
- Dockerのインストールが不完全

**解決方法**:

1. **Docker Desktopの起動確認**:
   - **Windows**: タスクバーにDockerアイコンが表示されているか確認します
   - **Mac**: メニューバーにDockerアイコンが表示されているか確認します
   - **Linux**: `systemctl status docker`コマンドでDockerサービスの状態を確認します

2. **Docker Desktopの起動**:
   - **Windows**: スタートメニューから"Docker Desktop"を起動します
   - **Mac**: アプリケーションフォルダから"Docker Desktop"を起動します
   - **Linux**: `sudo systemctl start docker`コマンドでDockerサービスを起動します

3. **Docker Desktopの完全な起動を待つ**:
   - Docker Desktopが完全に起動するまで、1〜2分かかる場合があります
   - Dockerアイコンが緑色になるまで待ちます
   - `docker ps`コマンドが正常に実行されることを確認します

4. **Docker Desktopの再インストール**:
   - 上記の方法で解決しない場合は、Docker Desktopを再インストールします
   - [Docker Desktop](https://www.docker.com/products/docker-desktop)から最新版をダウンロードします
   - 既存のDocker Desktopをアンインストールしてから、新しいバージョンをインストールします

5. **Linuxの場合の追加手順**:
   ```bash
   # Dockerサービスの有効化
   sudo systemctl enable docker
   
   # Dockerサービスの起動
   sudo systemctl start docker
   
   # 現在のユーザーをdockerグループに追加
   sudo usermod -aG docker $USER
   
   # ログアウトして再ログイン（グループ変更を反映）
   ```

#### 問題3: ポートが既に使用されている

**症状**:
- "port is already allocated"というエラーメッセージが表示される
- "Bind for 0.0.0.0:8080 failed: port is already allocated"というメッセージが表示される
- コンテナが起動するが、アプリケーションにアクセスできない

**原因**:
- 必要なポート（8080、35729、5432）が既に他のアプリケーションやコンテナで使用されている
- 以前のコンテナが正常に停止していない

**解決方法**:

1. **使用中のポートの確認**:
   ```bash
   # Windows (PowerShell)
   netstat -ano | findstr :8080
   netstat -ano | findstr :35729
   netstat -ano | findstr :5432
   
   # Mac/Linux
   lsof -i :8080
   lsof -i :35729
   lsof -i :5432
   ```

2. **既存のコンテナの停止**:
   ```bash
   # 実行中のコンテナを確認
   docker ps
   
   # 特定のコンテナを停止
   docker stop <コンテナID>
   
   # 全てのコンテナを停止
   docker stop $(docker ps -q)
   ```

3. **docker-composeで起動したコンテナの停止**:
   ```bash
   # プロジェクトルートディレクトリで実行
   docker-compose down
   ```

4. **使用中のプロセスの終了**:
   - **Windows**: タスクマネージャーを開き、ポートを使用しているプロセスを終了します
   - **Mac/Linux**: `kill -9 <プロセスID>`コマンドでプロセスを終了します

5. **ポート番号の変更**（最終手段）:
   - `docker-compose.yml`ファイルを編集します
   - ポートマッピングを変更します（例: `8080:8080` → `8081:8080`）
   - `.devcontainer/devcontainer.json`の`forwardPorts`も同様に変更します
   - コンテナを再ビルドします

6. **DevContainerの再起動**:
   - コマンドパレットから"Dev Containers: Rebuild Container"を選択します
   - コンテナが再ビルドされ、ポートが再割り当てされます

#### 問題4: ボリュームマウントの権限エラー

**症状**:
- "Permission denied"というエラーメッセージが表示される
- ファイルの読み書きができない
- Gradleビルドが失敗する
- コンテナ内でファイルを作成できない

**原因**:
- ホストとコンテナ間のファイルシステムの権限が一致していない
- Dockerのファイル共有設定が正しくない
- SELinux（Linux）の制限

**解決方法**:

1. **Dockerのファイル共有設定の確認**:
   - **Windows/Mac**: Docker Desktopの設定を開きます
   - "Resources" > "File Sharing"を選択します
   - プロジェクトディレクトリが共有対象に含まれているか確認します
   - 含まれていない場合は、追加して"Apply & Restart"をクリックします

2. **ファイルの権限確認**:
   ```bash
   # ホスト側でファイルの権限を確認
   ls -la ./src
   
   # コンテナ内でファイルの権限を確認
   # DevContainer内のターミナルで実行
   ls -la /app/src
   ```

3. **ファイルの所有者変更**（Linuxの場合）:
   ```bash
   # ホスト側でファイルの所有者を変更
   sudo chown -R $USER:$USER ./src
   
   # ファイルの権限を変更
   chmod -R 755 ./src
   ```

4. **SELinuxの設定変更**（Linuxの場合）:
   ```bash
   # SELinuxのコンテキストを確認
   ls -Z ./src
   
   # SELinuxのコンテキストを変更
   sudo chcon -Rt svirt_sandbox_file_t ./src
   
   # または、docker-compose.ymlでボリュームマウントに:zオプションを追加
   # volumes:
   #   - ./src:/app/src:z
   ```

5. **Gradleキャッシュの権限問題**:
   ```bash
   # Gradleキャッシュボリュームを削除して再作成
   docker volume rm spring-boot-thymeleaf-dev-env_gradle-cache
   
   # コンテナを再ビルド
   # コマンドパレットから"Dev Containers: Rebuild Container"を選択
   ```

6. **WSL2の場合の追加手順**（Windows）:
   - プロジェクトをWSL2のファイルシステム内に配置します（例: `/home/username/projects/`）
   - Windowsのファイルシステム（`/mnt/c/`）ではなく、WSL2のネイティブファイルシステムを使用します
   - これにより、パフォーマンスと権限の問題が解決されます

#### 問題5: 拡張機能のインストール失敗

**症状**:
- 拡張機能が自動的にインストールされない
- "Failed to install extension"というエラーメッセージが表示される
- 一部の拡張機能のみがインストールされる
- 拡張機能の機能が動作しない

**原因**:
- ネットワーク接続の問題
- VS Code Marketplaceへのアクセス制限
- 拡張機能の互換性問題
- コンテナ内のディスク容量不足

**解決方法**:

1. **拡張機能のインストール状態確認**:
   - 拡張機能ビュー（`Ctrl+Shift+X` または `Cmd+Shift+X`）を開きます
   - 検索ボックスに`@installed`と入力します
   - インストールされている拡張機能を確認します

2. **手動での拡張機能インストール**:
   - 拡張機能ビューで、インストールされていない拡張機能を検索します
   - "Install in Dev Container"ボタンをクリックします
   - 例: "Extension Pack for Java"を検索してインストール

3. **コンテナの再ビルド**:
   - コマンドパレット（`Ctrl+Shift+P` または `Cmd+Shift+P`）を開きます
   - "Dev Containers: Rebuild Container"を選択します
   - 拡張機能が再度自動的にインストールされます

4. **ネットワーク接続の確認**:
   ```bash
   # コンテナ内のターミナルで実行
   ping -c 4 marketplace.visualstudio.com
   
   # プロキシ設定が必要な場合は、環境変数を設定
   export HTTP_PROXY=http://proxy.example.com:8080
   export HTTPS_PROXY=http://proxy.example.com:8080
   ```

5. **VS Codeのログ確認**:
   - コマンドパレットから"Developer: Show Logs"を選択します
   - "Extension Host"を選択します
   - エラーメッセージを確認し、具体的な問題を特定します

6. **拡張機能の互換性確認**:
   - 一部の拡張機能は、コンテナ環境で動作しない場合があります
   - 拡張機能のドキュメントを確認し、コンテナ環境でのサポート状況を確認します
   - 代替の拡張機能を検討します

7. **ディスク容量の確認**:
   ```bash
   # コンテナ内のディスク使用量を確認
   df -h
   
   # Dockerのディスク使用量を確認
   docker system df
   
   # 不要なイメージやコンテナを削除
   docker system prune -a
   ```

8. **devcontainer.jsonの確認**:
   - `.devcontainer/devcontainer.json`ファイルを開きます
   - `customizations.vscode.extensions`配列に拡張機能IDが正しく記載されているか確認します
   - JSON構文エラーがないか確認します

### その他の問題

#### 問題6: Gradleビルドが失敗する

**症状**:
- `./gradlew bootRun`コマンドが失敗する
- "Could not resolve dependencies"というエラーメッセージが表示される
- ビルドが途中で停止する

**解決方法**:

1. **Gradle依存関係の再ダウンロード**:
   ```bash
   # Gradleキャッシュをクリア
   ./gradlew clean --no-daemon
   
   # 依存関係を再ダウンロード
   ./gradlew dependencies --refresh-dependencies --no-daemon
   ```

2. **Gradleデーモンの停止**:
   ```bash
   ./gradlew --stop
   ```

3. **Gradleキャッシュボリュームの削除**:
   ```bash
   # コンテナを停止
   docker-compose down
   
   # Gradleキャッシュボリュームを削除
   docker volume rm spring-boot-thymeleaf-dev-env_gradle-cache
   
   # コンテナを再起動
   # VS Codeで"Reopen in Container"を実行
   ```

#### 問題7: データベース接続エラー

**症状**:
- "Connection refused"というエラーメッセージが表示される
- アプリケーションがデータベースに接続できない
- "FATAL: password authentication failed"というエラーメッセージが表示される

**解決方法**:

1. **PostgreSQLコンテナの状態確認**:
   ```bash
   docker ps
   ```
   `postgres`コンテナが実行中であることを確認します。

2. **PostgreSQLのヘルスチェック確認**:
   ```bash
   docker-compose ps
   ```
   `postgres`サービスの状態が"healthy"であることを確認します。

3. **データベース接続設定の確認**:
   - `src/main/resources/application-dev.properties`ファイルを確認します
   - データベースのホスト名、ポート、ユーザー名、パスワードが正しいか確認します

4. **PostgreSQLコンテナの再起動**:
   ```bash
   docker-compose restart postgres
   ```

5. **PostgreSQLのログ確認**:
   ```bash
   docker-compose logs postgres
   ```

#### 問題8: ホットリロードが動作しない

**症状**:
- ソースコードを変更しても、アプリケーションが再起動されない
- ブラウザが自動的にリフレッシュされない
- 変更が反映されない

**解決方法**:

1. **Spring Boot DevToolsの確認**:
   - `build.gradle`ファイルを確認します
   - `developmentOnly 'org.springframework.boot:spring-boot-devtools'`が含まれているか確認します

2. **ボリュームマウントの確認**:
   - `docker-compose.yml`ファイルを確認します
   - `./src:/app/src`のボリュームマウントが正しく設定されているか確認します

3. **LiveReloadポートの確認**:
   - ブラウザの開発者ツールを開きます
   - コンソールに"LiveReload enabled"というメッセージが表示されているか確認します
   - ポート35729が正しく転送されているか確認します

4. **アプリケーションの再起動**:
   ```bash
   # アプリケーションを停止（Ctrl+C）
   # アプリケーションを再起動
   ./gradlew bootRun
   ```

### ログの確認方法

問題の原因を特定するために、以下のログを確認できます：

1. **VS Codeのログ**:
   - コマンドパレットから"Developer: Show Logs"を選択します
   - "Extension Host"、"Window"、"Shared"などのログを確認します

2. **DevContainerのログ**:
   - コマンドパレットから"Dev Containers: Show Container Log"を選択します
   - コンテナのビルドと起動のログを確認します

3. **Dockerのログ**:
   ```bash
   # 特定のコンテナのログを確認
   docker logs <コンテナID>
   
   # docker-composeのログを確認
   docker-compose logs
   
   # 特定のサービスのログを確認
   docker-compose logs app
   docker-compose logs postgres
   ```

4. **アプリケーションのログ**:
   - DevContainer内のターミナルで`./gradlew bootRun`を実行した際のログを確認します
   - Spring Bootのログレベルを変更して詳細なログを出力します

### サポートとヘルプ

上記の解決方法で問題が解決しない場合は、以下のリソースを参照してください：

1. **VS Code DevContainerドキュメント**:
   - [VS Code Remote - Containers](https://code.visualstudio.com/docs/remote/containers)
   - [DevContainer仕様](https://containers.dev/)

2. **Dockerドキュメント**:
   - [Docker Desktop](https://docs.docker.com/desktop/)
   - [Docker Compose](https://docs.docker.com/compose/)

3. **Spring Bootドキュメント**:
   - [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.devtools)

4. **プロジェクト固有のドキュメント**:
   - `DOCKER_GUIDE.md`: 既存のDocker環境のガイド
   - `README.md`: プロジェクトの概要

### 次のステップ

問題が解決したら、以下のセクションを参照してください：

- **既存Docker環境との違い**: DevContainerと通常の`docker-compose up`の違い
- **FAQ**: よくある質問と回答

---

## 既存Docker環境との違い

このセクションでは、DevContainer環境と既存のDocker環境（`docker-compose up`）の違いを説明し、それぞれの使い分けのガイドラインを提供します。

### 概要

このプロジェクトには、2つの開発環境が用意されています：

1. **DevContainer環境**: VS CodeのRemote - Containers拡張機能を使用した開発環境
2. **既存Docker環境**: `docker-compose up`コマンドを使用した従来の開発環境

両方の環境は、同じ`docker-compose.yml`と`Dockerfile.dev`を使用しているため、**動作は完全に同じ**です。違いは、開発者がどのようにコンテナと対話するかという点にあります。

### 主な違い

#### 1. VS Codeの統合

**DevContainer環境**:
- VS Codeがコンテナ内で直接動作します
- コンテナ内のファイルをVS Codeで直接編集できます
- VS Codeの拡張機能がコンテナ内で動作します
- デバッガー、ターミナル、Git統合などがコンテナ内で動作します

**既存Docker環境**:
- VS Codeはホストマシンで動作します
- コンテナ内のファイルはボリュームマウントを通じて編集します
- VS Codeの拡張機能はホストマシンで動作します
- コンテナ内での作業には`docker exec`コマンドを使用します

#### 2. 拡張機能のインストール

**DevContainer環境**:
- Java、Spring Boot、フロントエンド開発に必要な拡張機能が**自動的にインストール**されます
- 拡張機能はコンテナ内で動作するため、コンテナ内のファイルやプロセスに直接アクセスできます
- チーム全体で同じ拡張機能を共有できます

**既存Docker環境**:
- 拡張機能は**手動でインストール**する必要があります
- 拡張機能はホストマシンで動作するため、コンテナ内のファイルやプロセスへのアクセスが制限される場合があります
- 各開発者が個別に拡張機能を管理します

#### 3. ターミナルとコマンド実行

**DevContainer環境**:
- VS Codeのターミナルは**コンテナ内のシェル**に直接接続されます
- `./gradlew bootRun`などのコマンドを直接実行できます
- コンテナ内のファイルシステムに直接アクセスできます

**既存Docker環境**:
- ホストマシンのターミナルから`docker exec`コマンドを使用してコンテナ内でコマンドを実行します
- 例: `docker exec -it <コンテナID> ./gradlew bootRun`
- または、`docker-compose exec app ./gradlew bootRun`

#### 4. デバッグ機能

**DevContainer環境**:
- VS Codeのデバッガーが**コンテナ内のJavaプロセスに直接アタッチ**できます
- ブレークポイント、ステップ実行、変数の監視などが簡単に使用できます
- デバッグ設定が自動的に構成されます

**既存Docker環境**:
- デバッグポートを公開し、リモートデバッグ設定を手動で構成する必要があります
- VS Codeからコンテナ内のJavaプロセスにリモート接続します
- 設定が複雑になる場合があります

#### 5. Git統合

**DevContainer環境**:
- ホストのGit設定とSSH認証情報が**自動的にコンテナに引き継がれます**
- VS Code内でGit操作（コミット、プッシュ、プルなど）を直接実行できます
- Git履歴、差分表示、ブランチ管理などがVS Code内で利用できます

**既存Docker環境**:
- Git操作は**ホストマシンで実行**します
- コンテナ内でGit操作を行う場合は、Git設定とSSH認証情報を手動で設定する必要があります

#### 6. 起動方法

**DevContainer環境**:
```
1. VS Codeでプロジェクトを開く
2. "Reopen in Container"を選択
3. 自動的にコンテナがビルド・起動される
```

**既存Docker環境**:
```bash
# ターミナルで実行
docker-compose up -d

# アプリケーションを起動
docker-compose exec app ./gradlew bootRun
```

#### 7. ファイル編集

**DevContainer環境**:
- VS Codeがコンテナ内で動作するため、**ファイルアクセスが高速**です
- ファイルの変更が即座にコンテナ内に反映されます
- ファイルシステムの権限問題が発生しにくいです

**既存Docker環境**:
- ボリュームマウントを通じてファイルを編集します
- ファイルシステムの同期に若干の遅延が発生する場合があります（特にWindows/Mac）
- ファイルシステムの権限問題が発生する可能性があります

### 比較表

以下の表は、DevContainer環境と既存Docker環境の主な違いをまとめたものです：

| 項目 | DevContainer環境 | 既存Docker環境 |
|------|------------------|----------------|
| **VS Code統合** | コンテナ内で動作 | ホストで動作 |
| **拡張機能** | 自動インストール | 手動インストール |
| **ターミナル** | コンテナ内のシェル | ホストのシェル + `docker exec` |
| **デバッグ** | 直接アタッチ | リモートデバッグ設定が必要 |
| **Git統合** | 自動的に引き継ぎ | ホストで実行 |
| **起動方法** | VS Codeから | `docker-compose up` |
| **ファイルアクセス** | 高速（コンテナ内） | ボリュームマウント経由 |
| **学習コスト** | 低（VS Codeに統合） | 中（Dockerコマンドの知識が必要） |
| **セットアップ** | 簡単（自動化） | 手動設定が必要 |
| **チーム共有** | 設定を共有可能 | 各自で設定 |

### 使い分けのガイドライン

どちらの環境を使用するかは、開発者の好みや状況に応じて選択できます。以下のガイドラインを参考にしてください。

#### DevContainer環境を推奨する場合

以下の場合は、DevContainer環境の使用を推奨します：

1. **VS Codeを主に使用する開発者**:
   - VS Code内で完結する開発環境を好む場合
   - VS Codeの拡張機能を活用したい場合

2. **チーム開発**:
   - チーム全体で同じ開発環境を共有したい場合
   - 拡張機能やVS Code設定を統一したい場合

3. **初心者や新規参加者**:
   - Dockerコマンドに不慣れな開発者
   - 環境構築を簡単にしたい場合

4. **デバッグを頻繁に使用する場合**:
   - VS Codeのデバッガーを活用したい場合
   - ブレークポイントやステップ実行を頻繁に使用する場合

5. **Git操作をVS Code内で行いたい場合**:
   - VS CodeのGit統合機能を活用したい場合
   - コミット、プッシュ、プルなどをVS Code内で行いたい場合

6. **ファイルアクセスのパフォーマンスを重視する場合**:
   - 大量のファイルを扱う場合
   - ファイルアクセスの遅延を最小化したい場合

#### 既存Docker環境を推奨する場合

以下の場合は、既存Docker環境の使用を推奨します：

1. **他のエディタを使用する開発者**:
   - IntelliJ IDEA、Eclipse、Vimなど、VS Code以外のエディタを使用する場合
   - エディタに依存しない開発環境を好む場合

2. **Dockerコマンドに慣れている開発者**:
   - `docker-compose`コマンドを直接使用したい場合
   - コンテナの管理を細かく制御したい場合

3. **CI/CD環境との一貫性**:
   - CI/CDパイプラインと同じ方法でコンテナを起動したい場合
   - 本番環境に近い環境で開発したい場合

4. **複数のプロジェクトを同時に開発する場合**:
   - 複数のプロジェクトのコンテナを同時に起動したい場合
   - リソース管理を細かく制御したい場合

5. **軽量な環境を好む場合**:
   - VS Codeの拡張機能のオーバーヘッドを避けたい場合
   - 最小限のリソースで開発したい場合

6. **カスタマイズを重視する場合**:
   - Docker環境を自由にカスタマイズしたい場合
   - DevContainerの制約を受けたくない場合

### 両方の環境を併用する

DevContainer環境と既存Docker環境は**共存可能**です。以下のように併用することもできます：

1. **通常の開発**: DevContainer環境を使用
2. **CI/CDのテスト**: 既存Docker環境を使用
3. **パフォーマンステスト**: 既存Docker環境を使用
4. **デバッグ**: DevContainer環境を使用

両方の環境は同じ`docker-compose.yml`と`Dockerfile.dev`を使用しているため、動作は完全に同じです。開発者は、状況に応じて適切な環境を選択できます。

### 環境の切り替え

#### DevContainer環境から既存Docker環境への切り替え

1. VS Codeの左下にある緑色のアイコン（`Dev Container: Spring Boot DevContainer`）をクリックします
2. "Reopen Folder Locally"を選択します
3. ターミナルで`docker-compose up -d`を実行します

#### 既存Docker環境からDevContainer環境への切り替え

1. ターミナルで`docker-compose down`を実行します
2. VS Codeでプロジェクトを開きます
3. コマンドパレットから"Dev Containers: Reopen in Container"を選択します

### 共通の設定ファイル

両方の環境は、以下の設定ファイルを共有しています：

- **docker-compose.yml**: コンテナの定義、ポートマッピング、環境変数、ボリュームなど
- **Dockerfile.dev**: アプリケーションコンテナのイメージ定義
- **src/**: ソースコード（ボリュームマウント）
- **build.gradle**: Gradleビルド設定
- **application-dev.properties**: Spring Boot設定

これらのファイルを変更すると、両方の環境に影響します。

### ホットリロード機能

**両方の環境でホットリロード機能が動作します**：

- ソースコードを変更すると、Spring Boot DevToolsによって自動的にアプリケーションが再起動されます
- LiveReload（ポート35729）によって、ブラウザが自動的にリフレッシュされます

DevContainer環境でも既存Docker環境でも、ホットリロード機能の動作は同じです。

### データベースデータの永続化

**両方の環境でデータベースデータが永続化されます**：

- PostgreSQLのデータは`postgres-data`ボリュームに保存されます
- コンテナを停止しても、データは保持されます
- 両方の環境で同じデータベースデータを共有します

### Gradleキャッシュの共有

**両方の環境でGradleキャッシュが共有されます**：

- Gradleの依存関係は`gradle-cache`ボリュームに保存されます
- 一度ダウンロードした依存関係は、両方の環境で再利用されます
- ビルド時間が短縮されます

### まとめ

- **DevContainer環境**: VS Code統合、自動化、初心者向け、チーム開発に最適
- **既存Docker環境**: エディタ非依存、柔軟性、CI/CD一貫性、上級者向け
- **両方の環境は共存可能**: 状況に応じて使い分けることができます
- **動作は完全に同じ**: 同じ設定ファイルを使用しているため、動作に違いはありません

どちらの環境を選択しても、同じ開発体験を得ることができます。開発者の好みや状況に応じて、最適な環境を選択してください。

### 次のステップ

既存Docker環境との違いを理解したら、以下のセクションを参照してください：

- **FAQ**: よくある質問と回答
- **DOCKER_GUIDE.md**: 既存Docker環境の詳細なガイド

---

## FAQ（よくある質問）

このセクションでは、DevContainer環境に関するよくある質問と回答を提供します。

### 概要

DevContainer環境を使用する際に、開発者から頻繁に寄せられる質問をまとめました。問題が発生した場合や、DevContainer環境の動作について疑問がある場合は、まずこのセクションを確認してください。

より詳細なトラブルシューティング情報については、**トラブルシューティング**セクションを参照してください。

---

### Q1: ホットリロードは動作しますか？

**A: はい、DevContainer環境でもホットリロード機能は完全に動作します。**

DevContainer環境は、既存のDocker環境と同じ設定を使用しているため、ホットリロード機能も同じように動作します。

#### ホットリロードの仕組み

1. **ソースコードのマウント**: `./src`ディレクトリがコンテナ内の`/app/src`にマウントされています
2. **Spring Boot DevTools**: ソースコードの変更を検知し、自動的にアプリケーションを再起動します
3. **LiveReload**: ポート35729を通じて、ブラウザに変更を通知し、自動的にリフレッシュします

#### 動作確認方法

1. DevContainer環境でアプリケーションを起動します：
   ```bash
   ./gradlew bootRun
   ```

2. ブラウザで http://localhost:8080 にアクセスします

3. VS Code内でJavaファイルまたはThymeleafテンプレートを編集します

4. ファイルを保存すると、以下が自動的に実行されます：
   - アプリケーションが再起動される（数秒かかります）
   - ブラウザが自動的にリフレッシュされる

#### ホットリロードが動作しない場合

ホットリロードが動作しない場合は、以下を確認してください：

1. **Spring Boot DevToolsの確認**:
   ```bash
   # build.gradleに以下が含まれているか確認
   developmentOnly 'org.springframework.boot:spring-boot-devtools'
   ```

2. **ボリュームマウントの確認**:
   ```bash
   # docker-compose.ymlに以下が含まれているか確認
   volumes:
     - ./src:/app/src
   ```

3. **LiveReloadポートの確認**:
   - ブラウザの開発者ツールを開きます
   - コンソールに"LiveReload enabled"というメッセージが表示されているか確認します
   - ポート35729が正しく転送されているか確認します

4. **アプリケーションの再起動**:
   ```bash
   # アプリケーションを停止（Ctrl+C）
   # アプリケーションを再起動
   ./gradlew bootRun
   ```

詳細については、**トラブルシューティング**セクションの「問題8: ホットリロードが動作しない」を参照してください。

---

### Q2: データベースデータは永続化されますか？

**A: はい、データベースデータは永続化されます。コンテナを停止しても、データは保持されます。**

PostgreSQLのデータは、Dockerボリューム（`postgres-data`）に保存されるため、コンテナを停止したり削除したりしても、データは失われません。

#### データ永続化の仕組み

1. **Dockerボリューム**: `docker-compose.yml`で`postgres-data`という名前のボリュームが定義されています
2. **ボリュームマウント**: PostgreSQLコンテナの`/var/lib/postgresql/data`ディレクトリが、このボリュームにマウントされています
3. **データの保持**: コンテナを停止しても、ボリューム内のデータは保持されます

#### データの確認方法

1. **ボリュームの確認**:
   ```bash
   docker volume ls
   ```
   `spring-boot-thymeleaf-dev-env_postgres-data`というボリュームが表示されます。

2. **データベースへの接続**:
   ```bash
   # DevContainer内のターミナルで実行
   docker exec -it <postgresコンテナID> psql -U appuser -d appdb
   
   # または、docker-composeを使用
   docker-compose exec postgres psql -U appuser -d appdb
   ```

3. **テーブルの確認**:
   ```sql
   -- データベース内のテーブル一覧を表示
   \dt
   
   -- 特定のテーブルのデータを確認
   SELECT * FROM your_table_name;
   ```

#### データの削除方法

データベースデータを完全に削除したい場合（例: 初期状態に戻したい場合）は、以下の手順を実行します：

1. **コンテナの停止**:
   ```bash
   docker-compose down
   ```

2. **ボリュームの削除**:
   ```bash
   docker volume rm spring-boot-thymeleaf-dev-env_postgres-data
   ```

3. **コンテナの再起動**:
   - VS Codeで"Dev Containers: Rebuild Container"を実行します
   - または、`docker-compose up -d`を実行します

4. **初期化スクリプトの実行**:
   - PostgreSQLコンテナが起動すると、`docker/init.sql`スクリプトが自動的に実行されます
   - データベースとテーブルが初期状態で作成されます

#### DevContainer環境と既存Docker環境でのデータ共有

DevContainer環境と既存Docker環境（`docker-compose up`）は、**同じボリュームを使用します**。そのため、以下の特徴があります：

- DevContainer環境で作成したデータは、既存Docker環境でも参照できます
- 既存Docker環境で作成したデータは、DevContainer環境でも参照できます
- 両方の環境で同じデータベースデータを共有します

---

### Q3: Git設定は引き継がれますか？

**A: はい、ホストのGit設定とSSH認証情報は自動的にコンテナに引き継がれます。**

VS CodeのDevContainer拡張機能は、デフォルトでホストのGit設定とSSH認証情報をコンテナに自動的に共有します。そのため、追加の設定は不要です。

#### 引き継がれる設定

1. **Git設定（.gitconfig）**:
   - ユーザー名（`user.name`）
   - メールアドレス（`user.email`）
   - その他のGit設定（エイリアス、デフォルトブランチなど）

2. **SSH認証情報**:
   - SSH秘密鍵（`~/.ssh/id_rsa`、`~/.ssh/id_ed25519`など）
   - SSH設定（`~/.ssh/config`）
   - SSH認証エージェント

3. **Git認証情報ヘルパー**:
   - HTTPS認証情報（Git Credential Manager）
   - トークンベースの認証

#### 動作確認方法

1. **Git設定の確認**:
   ```bash
   # DevContainer内のターミナルで実行
   git config --list
   
   # ユーザー名とメールアドレスの確認
   git config user.name
   git config user.email
   ```

2. **SSH接続の確認**:
   ```bash
   # GitHubへのSSH接続テスト
   ssh -T git@github.com
   
   # 成功すると以下のようなメッセージが表示されます
   # Hi username! You've successfully authenticated, but GitHub does not provide shell access.
   ```

3. **Git操作の実行**:
   ```bash
   # ブランチの作成
   git checkout -b feature/new-feature
   
   # 変更のコミット
   git add .
   git commit -m "Add new feature"
   
   # リモートへのプッシュ
   git push origin feature/new-feature
   ```

#### VS Code内でのGit操作

DevContainer環境では、VS CodeのGit統合機能を使用して、以下の操作を実行できます：

1. **ソース管理ビュー**:
   - `Ctrl+Shift+G`（または`Cmd+Shift+G`）でソース管理ビューを開きます
   - 変更されたファイルの一覧が表示されます

2. **変更のステージング**:
   - ファイルの横にある`+`アイコンをクリックして、変更をステージングします

3. **コミット**:
   - コミットメッセージを入力します
   - `Ctrl+Enter`（または`Cmd+Enter`）でコミットします

4. **プッシュ/プル**:
   - ソース管理ビューの`...`メニューから"Push"または"Pull"を選択します

5. **ブランチ管理**:
   - VS Codeの左下にあるブランチ名をクリックします
   - ブランチの切り替え、作成、削除などが実行できます

#### Git設定が引き継がれない場合

Git設定が正しく引き継がれない場合は、以下を確認してください：

1. **ホストのGit設定の確認**:
   ```bash
   # ホストマシンのターミナルで実行
   git config --list
   ```
   ホストマシンでGit設定が正しく構成されているか確認します。

2. **SSH認証エージェントの確認**:
   ```bash
   # ホストマシンのターミナルで実行
   ssh-add -l
   ```
   SSH秘密鍵が認証エージェントに追加されているか確認します。

3. **DevContainerの再起動**:
   - コマンドパレットから"Dev Containers: Rebuild Container"を選択します
   - Git設定が再度引き継がれます

4. **手動でのGit設定**:
   Git設定が自動的に引き継がれない場合は、コンテナ内で手動で設定できます：
   ```bash
   # DevContainer内のターミナルで実行
   git config --global user.name "Your Name"
   git config --global user.email "your.email@example.com"
   ```

5. **SSH秘密鍵の手動コピー**:
   SSH認証情報が自動的に引き継がれない場合は、手動でコピーできます：
   ```bash
   # ホストマシンからコンテナにSSH秘密鍵をコピー
   # （通常は自動的に処理されるため、この手順は不要です）
   ```

#### セキュリティに関する注意事項

- **SSH秘密鍵**: VS CodeのDevContainer拡張機能は、SSH認証エージェントを使用して秘密鍵を安全に共有します。秘密鍵自体はコンテナにコピーされません。
- **Git認証情報**: HTTPS認証情報は、Git Credential Managerを通じて安全に共有されます。
- **コンテナの削除**: コンテナを削除しても、ホストのGit設定やSSH認証情報には影響しません。

---

### Q4: DevContainer環境と既存Docker環境の違いは何ですか？

**A: 両方の環境は同じ設定を使用しているため、動作は完全に同じです。違いは、開発者がどのようにコンテナと対話するかという点にあります。**

詳細については、**既存Docker環境との違い**セクションを参照してください。

主な違いは以下の通りです：

- **VS Code統合**: DevContainer環境では、VS Codeがコンテナ内で動作します
- **拡張機能**: DevContainer環境では、拡張機能が自動的にインストールされます
- **ターミナル**: DevContainer環境では、ターミナルがコンテナ内のシェルに直接接続されます
- **デバッグ**: DevContainer環境では、デバッガーがコンテナ内のJavaプロセスに直接アタッチできます

---

### Q5: DevContainer環境の起動に時間がかかるのはなぜですか？

**A: 初回起動時は、Dockerイメージのビルド、拡張機能のインストール、Gradle依存関係のダウンロードが実行されるため、5〜10分程度かかります。**

2回目以降の起動は、キャッシュが使用されるため、より高速になります（1〜2分程度）。

#### 初回起動時の処理

1. **Dockerイメージのビルド**: `Dockerfile.dev`を使用してアプリケーションコンテナのイメージをビルドします（数分）
2. **VS Code拡張機能のインストール**: Java、Spring Boot、フロントエンド開発に必要な拡張機能をインストールします（数分）
3. **Gradle依存関係のダウンロード**: `gradle dependencies --no-daemon`コマンドを実行し、依存関係をダウンロードします（数分）

#### 起動時間の短縮方法

1. **キャッシュの活用**: 2回目以降の起動では、キャッシュが使用されるため、起動時間が大幅に短縮されます

2. **ネットワーク接続**: 安定した高速なネットワーク接続を使用することで、ダウンロード時間を短縮できます

3. **Docker Desktopのリソース設定**: Docker Desktopに十分なCPUとメモリを割り当てることで、ビルド時間を短縮できます
   - 推奨設定: CPU 4コア以上、メモリ 4GB以上

4. **SSDの使用**: SSDを使用することで、ディスクI/Oが高速化され、ビルド時間が短縮されます

---

### Q6: DevContainer環境でデバッグはできますか？

**A: はい、VS Codeのデバッガーがコンテナ内のJavaプロセスに直接アタッチできます。**

DevContainer環境では、VS Codeのデバッグ機能を使用して、ブレークポイント、ステップ実行、変数の監視などを実行できます。

#### デバッグの開始方法

1. **デバッグ設定の作成**:
   - `.vscode/launch.json`ファイルを作成します
   - Spring Bootアプリケーション用のデバッグ設定を追加します

2. **ブレークポイントの設定**:
   - デバッグしたいコードの行番号の左側をクリックします
   - 赤い丸（ブレークポイント）が表示されます

3. **デバッグの開始**:
   - `F5`キーを押すか、デバッグビューから"Start Debugging"を選択します
   - アプリケーションがデバッグモードで起動します

4. **デバッグ操作**:
   - ブレークポイントで実行が停止します
   - ステップイン（`F11`）、ステップオーバー（`F10`）、ステップアウト（`Shift+F11`）などの操作が実行できます
   - 変数の値を確認したり、式を評価したりできます

詳細については、VS Codeのデバッグドキュメントを参照してください。

---

### Q7: DevContainer環境でGradleコマンドを実行できますか？

**A: はい、DevContainer内のターミナルで直接Gradleコマンドを実行できます。**

DevContainer環境では、VS Codeのターミナルがコンテナ内のシェルに直接接続されているため、`./gradlew`コマンドを直接実行できます。

#### Gradleコマンドの例

```bash
# アプリケーションの起動
./gradlew bootRun

# ビルド
./gradlew build

# テストの実行
./gradlew test

# クリーン
./gradlew clean

# 依存関係の確認
./gradlew dependencies

# タスクの一覧表示
./gradlew tasks
```

#### Gradle Wrapper

このプロジェクトでは、Gradle Wrapper（`./gradlew`）を使用しています。Gradle Wrapperを使用することで、以下の利点があります：

- プロジェクトごとに異なるGradleバージョンを使用できます
- Gradleをシステムにインストールする必要がありません
- チーム全体で同じGradleバージョンを使用できます

---

### Q8: DevContainer環境でポートを変更できますか？

**A: はい、`docker-compose.yml`と`.devcontainer/devcontainer.json`を編集することで、ポートを変更できます。**

#### ポート変更の手順

1. **docker-compose.ymlの編集**:
   ```yaml
   services:
     app:
       ports:
         - "8081:8080"  # ホストポート8081をコンテナポート8080にマッピング
         - "35730:35729"  # LiveReloadポートも変更
   ```

2. **.devcontainer/devcontainer.jsonの編集**:
   ```json
   {
     "forwardPorts": [8081, 35730, 5432]
   }
   ```

3. **コンテナの再ビルド**:
   - コマンドパレットから"Dev Containers: Rebuild Container"を選択します

4. **アプリケーションへのアクセス**:
   - ブラウザで http://localhost:8081 にアクセスします

---

### Q9: DevContainer環境で複数のプロジェクトを同時に開発できますか？

**A: はい、複数のDevContainer環境を同時に起動できます。ただし、ポートの競合に注意してください。**

複数のプロジェクトを同時に開発する場合は、各プロジェクトで異なるポート番号を使用する必要があります。

#### 複数プロジェクトの開発方法

1. **各プロジェクトで異なるポートを使用**:
   - プロジェクトAは8080ポートを使用
   - プロジェクトBは8081ポートを使用
   - プロジェクトCは8082ポートを使用

2. **VS Codeで複数のウィンドウを開く**:
   - 各プロジェクトを別々のVS Codeウィンドウで開きます
   - 各ウィンドウでDevContainer環境を起動します

3. **リソースの管理**:
   - 複数のコンテナを同時に起動すると、CPUとメモリの使用量が増加します
   - Docker Desktopのリソース設定を適切に調整してください

---

### Q10: DevContainer環境を削除するにはどうすればよいですか？

**A: コンテナ、イメージ、ボリュームを削除することで、DevContainer環境を完全に削除できます。**

#### 完全削除の手順

1. **コンテナの停止と削除**:
   ```bash
   docker-compose down
   ```

2. **イメージの削除**:
   ```bash
   # イメージの一覧表示
   docker images
   
   # 特定のイメージを削除
   docker rmi <イメージID>
   
   # または、プロジェクト関連のイメージを全て削除
   docker rmi $(docker images -q spring-boot-thymeleaf-dev-env*)
   ```

3. **ボリュームの削除**:
   ```bash
   # ボリュームの一覧表示
   docker volume ls
   
   # 特定のボリュームを削除
   docker volume rm spring-boot-thymeleaf-dev-env_gradle-cache
   docker volume rm spring-boot-thymeleaf-dev-env_postgres-data
   ```

4. **未使用のリソースの削除**:
   ```bash
   # 未使用のコンテナ、イメージ、ボリュームを全て削除
   docker system prune -a --volumes
   ```

**注意**: ボリュームを削除すると、データベースデータとGradleキャッシュも削除されます。必要に応じてバックアップを取ってください。

---

### その他の質問

上記の質問で解決しない場合は、以下のリソースを参照してください：

- **トラブルシューティング**: より詳細な問題解決方法
- **既存Docker環境との違い**: DevContainerと既存Docker環境の比較
- **DOCKER_GUIDE.md**: 既存Docker環境の詳細なガイド
- **VS Code DevContainerドキュメント**: [VS Code Remote - Containers](https://code.visualstudio.com/docs/remote/containers)

---

## まとめ

このガイドでは、VS Code DevContainer環境のセットアップ、使用方法、トラブルシューティング、既存Docker環境との違い、よくある質問について説明しました。

### 主なポイント

1. **DevContainer環境**: VS Code内で完結する開発環境を提供します
2. **既存環境との互換性**: 既存のDocker環境と同じ動作を保証します
3. **自動化**: 拡張機能のインストール、Git設定の引き継ぎなどが自動化されています
4. **ホットリロード**: ソースコード変更時に自動的にアプリケーションが再起動されます
5. **データ永続化**: データベースデータとGradleキャッシュが永続化されます

### 次のステップ

1. **DevContainer環境の起動**: セットアップ手順に従って、DevContainer環境を起動します
2. **アプリケーションの開発**: VS Code内でソースコードを編集し、アプリケーションを開発します
3. **デバッグ**: VS Codeのデバッガーを使用して、アプリケーションをデバッグします
4. **Git操作**: VS CodeのGit統合機能を使用して、バージョン管理を行います

### サポート

問題が発生した場合や質問がある場合は、以下のリソースを参照してください：

- **トラブルシューティング**: よくある問題と解決方法
- **FAQ**: よくある質問と回答
- **VS Code DevContainerドキュメント**: [VS Code Remote - Containers](https://code.visualstudio.com/docs/remote/containers)
- **Dockerドキュメント**: [Docker Desktop](https://docs.docker.com/desktop/)
- **Spring Bootドキュメント**: [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.devtools)

Happy coding! 🚀

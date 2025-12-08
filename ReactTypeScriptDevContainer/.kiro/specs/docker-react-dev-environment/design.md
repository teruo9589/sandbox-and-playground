# Design Document

## Overview

本システムは、Dockerコンテナベースの完全に隔離されたTypeScript + React開発環境を提供します。Visual Studio CodeのDev Container機能を中核として、ローカル環境に一切の依存関係をインストールすることなく、JSXファイルの閲覧・編集・Hot Reloadを実現します。

主要な設計目標：
- ホストマシンの環境を一切変更しない完全なコンテナ化
- Viteを使用した高速な開発サーバーとHot Module Replacement (HMR)
- Dev Containerによるシームレスな開発体験
- 既存のJSXファイルを即座に利用可能な柔軟な構造

## Architecture

システムは以下の3つの主要レイヤーで構成されます：

```
┌─────────────────────────────────────────┐
│     Host Machine (Windows/Mac/Linux)    │
│  ┌───────────────────────────────────┐  │
│  │   Visual Studio Code (Host)       │  │
│  │   - Dev Container Extension       │  │
│  │   - Remote Container Connection   │  │
│  └───────────────────────────────────┘  │
│              ↕ (Docker API)             │
│  ┌───────────────────────────────────┐  │
│  │   Docker Container                │  │
│  │  ┌─────────────────────────────┐  │  │
│  │  │  Node.js Runtime            │  │  │
│  │  │  - npm/yarn                 │  │  │
│  │  │  - TypeScript Compiler      │  │  │
│  │  │  - Vite Dev Server          │  │  │
│  │  └─────────────────────────────┘  │  │
│  │  ┌─────────────────────────────┐  │  │
│  │  │  VS Code Server             │  │  │
│  │  │  - Extensions (ESLint, etc) │  │  │
│  │  │  - Language Services        │  │  │
│  │  └─────────────────────────────┘  │  │
│  │  ┌─────────────────────────────┐  │  │
│  │  │  Mounted Workspace          │  │  │
│  │  │  /workspace                 │  │  │
│  │  │  - src/                     │  │  │
│  │  │  - public/                  │  │  │
│  │  │  - config files             │  │  │
│  │  └─────────────────────────────┘  │  │
│  └───────────────────────────────────┘  │
│              ↕ (Port Forwarding)        │
│  ┌───────────────────────────────────┐  │
│  │   Browser (Host)                  │  │
│  │   http://localhost:5173           │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

### Design Decisions

1. **Vite over Create React App**: Viteを選択した理由は、より高速な起動時間、優れたHMR、TypeScriptのネイティブサポート、そして軽量な設定です。

2. **Volume Mounting Strategy**: ソースコードはホストからコンテナにマウントし、node_modulesはコンテナ内の名前付きボリュームに配置することで、ホストとコンテナ間のファイルシステムパフォーマンスの問題を回避します。

3. **Dev Container as Primary Interface**: Dev Containerを使用することで、Dockerの複雑さを隠蔽し、開発者にシームレスな体験を提供します。

## Components and Interfaces

### 1. Dev Container Configuration (`.devcontainer/devcontainer.json`)

Dev Containerの設定を定義します。

**責務:**
- コンテナイメージの指定
- ポートフォワーディングの設定
- VS Code拡張機能のインストール指定
- コンテナ起動後のコマンド実行

**インターフェース:**
```json
{
  "name": "React TypeScript Dev Container",
  "dockerFile": "Dockerfile",
  "forwardPorts": [5173],
  "postCreateCommand": "npm install",
  "postStartCommand": "npm run dev",
  "customizations": {
    "vscode": {
      "extensions": [...]
    }
  }
}
```

### 2. Dockerfile (`.devcontainer/Dockerfile`)

コンテナイメージのビルド定義。

**責務:**
- Node.js環境のセットアップ
- 必要なシステムパッケージのインストール
- 作業ディレクトリの設定
- ユーザー権限の設定

**ベースイメージ:** `node:22-bullseye` (最新LTS版、Debian系)

### 3. Vite Configuration (`vite.config.ts`)

開発サーバーとビルドツールの設定。

**責務:**
- React pluginの設定
- 開発サーバーのホストとポート設定
- HMRの設定
- TypeScriptパスエイリアスの解決

**主要設定:**
```typescript
export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0', // コンテナ外からのアクセスを許可
    port: 5173,
    watch: {
      usePolling: true // Dockerボリュームでのファイル監視
    }
  }
})
```

### 4. TypeScript Configuration (`tsconfig.json`)

TypeScriptコンパイラの設定。

**責務:**
- JSX変換の設定（React 18の新しいJSX transform）
- モジュール解決戦略
- 型チェックの厳密性
- 出力ディレクトリの指定

**主要設定:**
- `"jsx": "react-jsx"` - 新しいJSX transform
- `"moduleResolution": "bundler"` - Vite互換
- `"strict": true` - 厳密な型チェック

### 5. Package Configuration (`package.json`)

プロジェクトの依存関係とスクリプト定義。

**主要依存関係:**
- `react@^19.2.1`, `react-dom@^19.2.1` - Reactコアライブラリ（最新版）
- `typescript@^5.6.0` - TypeScriptコンパイラ（最新版）
- `vite@^7.2.6` - ビルドツールと開発サーバー（最新版）
- `@vitejs/plugin-react@^4.3.0` - ViteのReactプラグイン（最新版）
- `@types/react@^19.0.0`, `@types/react-dom@^19.0.0` - 型定義（最新版）
- `tailwindcss@^3.4.0` - ユーティリティファーストCSSフレームワーク（最新版）
- `postcss@^8.4.0` - CSS変換ツール
- `autoprefixer@^10.4.0` - CSSベンダープレフィックス自動付与

**スクリプト:**
- `dev` - 開発サーバー起動
- `build` - プロダクションビルド
- `preview` - ビルド結果のプレビュー
- `lint` - ESLintによるコード検証

### 6. Tailwind CSS Configuration

Tailwind CSSの設定とViteとの統合。

**責務:**
- Tailwind CSSのコンテンツパス設定
- PostCSSプラグインの設定
- グローバルスタイルへのTailwindディレクティブの追加

**tailwind.config.js:**
```javascript
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {},
  },
  plugins: [],
}
```

**postcss.config.js:**
```javascript
export default {
  plugins: {
    tailwindcss: {},
    autoprefixer: {},
  },
}
```

**src/index.css:**
```css
@tailwind base;
@tailwind components;
@tailwind utilities;
```

### 7. Project Structure

```
project-root/
├── .devcontainer/
│   ├── devcontainer.json    # Dev Container設定
│   └── Dockerfile            # コンテナイメージ定義
├── src/
│   ├── main.tsx             # エントリーポイント
│   ├── App.tsx              # ルートコンポーネント
│   ├── index.css            # グローバルスタイル（Tailwind directives）
│   ├── vite-env.d.ts        # Vite型定義
│   └── [existing jsx files] # 既存のJSXファイル
├── public/                   # 静的アセット
├── index.html               # HTMLエントリーポイント
├── package.json             # 依存関係定義
├── tsconfig.json            # TypeScript設定
├── vite.config.ts           # Vite設定
├── tailwind.config.js       # Tailwind CSS設定
├── postcss.config.js        # PostCSS設定
└── .gitignore               # Git除外設定
```

## Data Models

このプロジェクトは主に設定ファイルとインフラストラクチャに焦点を当てているため、複雑なデータモデルはありません。ただし、以下の設定構造を定義します：

### Dev Container Configuration Schema

```typescript
interface DevContainerConfig {
  name: string;
  dockerFile: string;
  forwardPorts: number[];
  postCreateCommand?: string;
  postStartCommand?: string;
  customizations: {
    vscode: {
      extensions: string[];
      settings?: Record<string, any>;
    };
  };
  mounts?: string[];
}
```

### Vite Configuration Schema

```typescript
interface ViteConfig {
  plugins: Plugin[];
  server: {
    host: string;
    port: number;
    watch: {
      usePolling: boolean;
    };
    hmr?: {
      overlay: boolean;
    };
  };
  build?: {
    outDir: string;
    sourcemap: boolean;
  };
}
```


## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system-essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

このプロジェクトは主にインフラストラクチャと設定に焦点を当てているため、多くの検証は具体的な設定例の確認となります。以下のプロパティは、システムが正しく構成され、期待通りに動作することを保証します。

### Configuration File Properties

Property 1: Dev Container configuration completeness
*For any* valid Dev Container setup, the `.devcontainer/devcontainer.json` file should exist and contain all required fields: name, dockerFile, forwardPorts, and customizations.vscode.extensions
**Validates: Requirements 2.1**

Property 2: Required VS Code extensions specification
*For any* Dev Container configuration, the extensions list should include ESLint, Prettier, and TypeScript/React language support extensions
**Validates: Requirements 6.1, 6.2, 6.3**

Property 3: TypeScript configuration validity
*For any* TypeScript project setup, the `tsconfig.json` file should exist and include jsx mode set to "react-jsx" and moduleResolution compatible with Vite
**Validates: Requirements 5.2**

Property 4: Package dependencies completeness
*For any* React TypeScript project, the `package.json` should include react, react-dom, typescript, vite, @vitejs/plugin-react, tailwindcss, postcss, and autoprefixer as dependencies
**Validates: Requirements 5.3**

Property 5: Dockerfile base image specification
*For any* containerized Node.js environment, the Dockerfile should specify a Node.js base image (node:22 or later for latest LTS)
**Validates: Requirements 5.4**

### Runtime Behavior Properties

Property 6: Container isolation verification
*For any* running Dev Container, executing `node --version`, `npm --version`, and `tsc --version` inside the container should succeed, while these commands on the host should either fail or return different versions
**Validates: Requirements 1.1, 1.2**

Property 7: Development server port accessibility
*For any* running Vite development server in the container, an HTTP request to `http://localhost:5173` from the host should return a successful response (status 200)
**Validates: Requirements 3.1, 3.4**

Property 8: JSX compilation and rendering
*For any* valid JSX file placed in the src directory, accessing the development server should return HTML content that includes the compiled JavaScript
**Validates: Requirements 3.2, 3.3, 3.5**

Property 9: File change detection responsiveness
*For any* JSX file modification, the development server should detect the change and trigger a recompilation within 2 seconds
**Validates: Requirements 4.1, 4.2**

Property 10: Hot Module Replacement functionality
*For any* file change in a running development environment, the browser should receive an HMR update without requiring a manual page refresh
**Validates: Requirements 4.3**

Property 11: Error overlay display
*For any* compilation error introduced in the code, the development server should display an error overlay in the browser with the error message
**Validates: Requirements 4.5**

### Data Persistence Properties

Property 12: Host file system persistence
*For any* file modification made within the Dev Container, stopping and restarting the container should preserve all changes on the host file system
**Validates: Requirements 7.4**

Property 13: Container cleanup verification
*For any* Dev Container removal, the host machine should not contain any node_modules directories or npm cache artifacts in the project directory
**Validates: Requirements 1.3**

Property 14: Dependency isolation
*For any* npm install operation within the container, the node_modules directory should exist only within the container's file system, not on the host
**Validates: Requirements 1.4**

### Integration Properties

Property 15: Project structure completeness
*For any* initialized project, the following directories and files should exist: src/, public/, index.html, package.json, tsconfig.json, vite.config.ts, tailwind.config.js, postcss.config.js, .devcontainer/devcontainer.json, .devcontainer/Dockerfile
**Validates: Requirements 5.1, 5.5**

Property 16: Automatic server startup
*For any* Dev Container that completes its build process, the postStartCommand should automatically execute and start the Vite development server
**Validates: Requirements 7.2**

## Error Handling

### Container Build Failures

**Scenario:** Dockerfileのビルドが失敗する場合
- **Detection:** Docker build processのexit codeが非ゼロ
- **Response:** VS Codeがエラーメッセージを表示し、ビルドログを提供
- **Recovery:** ユーザーがDockerfileを修正し、"Rebuild Container"コマンドを実行

### Port Conflicts

**Scenario:** ポート5173が既に使用されている場合
- **Detection:** Viteサーバーの起動時にEADDRINUSEエラー
- **Response:** Viteが自動的に次の利用可能なポート（5174等）を使用
- **Logging:** コンソールに新しいポート番号を表示

### File System Permission Issues

**Scenario:** コンテナ内でファイルの読み書き権限がない場合
- **Detection:** EACCES or EPERM errors
- **Response:** Dockerfileでユーザー権限を適切に設定（node userの使用）
- **Prevention:** devcontainer.jsonで`"remoteUser": "node"`を指定

### Missing Dependencies

**Scenario:** package.jsonの依存関係がインストールされていない場合
- **Detection:** モジュールが見つからないエラー
- **Response:** postCreateCommandで`npm install`を自動実行
- **Recovery:** 手動で`npm install`を実行可能

### TypeScript Compilation Errors

**Scenario:** TypeScriptの型エラーや構文エラー
- **Detection:** Viteのビルドプロセスでエラー検出
- **Response:** ブラウザにエラーオーバーレイを表示
- **Details:** エラーメッセージ、ファイル名、行番号を含む詳細情報
- **Recovery:** コードを修正すると自動的に再コンパイル

### Hot Reload Failures

**Scenario:** HMRが正常に動作しない場合
- **Detection:** ファイル変更が反映されない
- **Response:** Viteのwatch設定で`usePolling: true`を使用（Docker環境用）
- **Fallback:** 完全なページリロードにフォールバック

### Network Connectivity Issues

**Scenario:** ホストからコンテナへの接続が失敗する場合
- **Detection:** Connection refused errors
- **Response:** Vite serverの`host: '0.0.0.0'`設定を確認
- **Verification:** `docker ps`でポートマッピングを確認

## Testing Strategy

このプロジェクトは主にインフラストラクチャと設定に関するものであるため、テスト戦略は以下の2つのアプローチを組み合わせます：

### 1. Configuration Validation Tests (Unit Tests)

設定ファイルの存在と内容を検証する自動テスト。

**テストツール:** Node.jsのテストフレームワーク（Vitest）

**テスト対象:**
- devcontainer.jsonの構造と必須フィールド
- tsconfig.jsonのコンパイラオプション
- package.jsonの依存関係リスト
- vite.config.tsのサーバー設定
- Dockerfileのベースイメージとコマンド

**実装方法:**
```typescript
// 例：devcontainer.jsonの検証
describe('Dev Container Configuration', () => {
  it('should have required fields', () => {
    const config = JSON.parse(fs.readFileSync('.devcontainer/devcontainer.json'));
    expect(config.name).toBeDefined();
    expect(config.dockerFile).toBeDefined();
    expect(config.forwardPorts).toContain(5173);
  });
});
```

### 2. Integration Tests

実際のコンテナ環境での動作を検証するテスト。

**テストツール:** シェルスクリプトまたはNode.jsスクリプト

**テスト対象:**
- コンテナのビルドと起動
- 開発サーバーへのHTTPアクセス
- ファイル変更の検知
- ポートフォワーディング
- ボリュームマウント

**実装方法:**
```bash
# 例：開発サーバーのアクセステスト
#!/bin/bash
# コンテナ起動を待機
sleep 5
# HTTPリクエストを送信
response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:5173)
if [ $response -eq 200 ]; then
  echo "✓ Development server is accessible"
else
  echo "✗ Development server is not accessible"
  exit 1
fi
```

### 3. Manual Verification Checklist

自動化が困難な項目の手動確認リスト。

**確認項目:**
- [ ] VS CodeでDev Containerを開く際にプロンプトが表示される
- [ ] IntelliSenseがTypeScriptコードで正常に動作する
- [ ] ブラウザでReactコンポーネントが正しくレンダリングされる
- [ ] ファイル編集時にHMRが動作し、ブラウザが自動更新される
- [ ] エラーを含むコードを書いた際にエラーオーバーレイが表示される
- [ ] コンテナを停止・再起動してもファイル変更が保持される

### Testing Workflow

1. **開発時:** Configuration Validation Testsを実行して設定ファイルの整合性を確認
2. **コンテナビルド後:** Integration Testsを実行して実際の動作を確認
3. **最終確認:** Manual Verification Checklistで全体的な動作を確認

### Property-Based Testing

このプロジェクトでは、設定ファイルの検証に対してプロパティベーステストを適用できます。

**テストライブラリ:** fast-check (TypeScript/JavaScript用のプロパティベーステストライブラリ)

**適用例:**
- 各設定ファイルが存在し、有効なJSON/TypeScriptであることを検証
- 必須フィールドが常に存在することを検証
- ポート番号が有効な範囲内であることを検証

**実装要件:**
- 各プロパティベーステストは最低100回の反復を実行
- 各テストには設計書のプロパティ番号を明示的に参照するコメントを含める
- コメント形式: `// Feature: docker-react-dev-environment, Property X: [property text]`

**例:**
```typescript
import fc from 'fast-check';

// Feature: docker-react-dev-environment, Property 1: Dev Container configuration completeness
describe('Property: Dev Container configuration completeness', () => {
  it('should always have required fields', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        const config = JSON.parse(
          fs.readFileSync('.devcontainer/devcontainer.json', 'utf-8')
        );
        return (
          config.name !== undefined &&
          config.dockerFile !== undefined &&
          Array.isArray(config.forwardPorts) &&
          config.customizations?.vscode?.extensions !== undefined
        );
      }),
      { numRuns: 100 }
    );
  });
});
```

この戦略により、設定の正確性と実際の動作の両方を包括的に検証できます。

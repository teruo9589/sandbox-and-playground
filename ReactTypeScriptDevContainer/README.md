# React TypeScript 開発環境 (Dev Container)

このプロジェクトは、Dockerコンテナ内で完結するTypeScript + React開発環境です。ローカル環境にNode.jsやnpmをインストールすることなく、すぐに開発を始められます。

## 前提条件

以下のツールがインストールされている必要があります：

1. **Docker Desktop**
   - Windows: [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop)
   - Mac: [Docker Desktop for Mac](https://www.docker.com/products/docker-desktop)
   - Linux: [Docker Engine](https://docs.docker.com/engine/install/)

2. **Visual Studio Code**
   - [VS Code公式サイト](https://code.visualstudio.com/)からダウンロード

3. **Dev Containers拡張機能**
   - VS Codeの拡張機能マーケットプレイスから「Dev Containers」をインストール
   - または、VS Codeで`ms-vscode-remote.remote-containers`を検索してインストール

## セットアップ手順

### 1. プロジェクトを開く

```bash
# プロジェクトディレクトリに移動
cd /path/to/project

# VS Codeで開く
code .
```

### 2. Dev Containerで開く

VS Codeでプロジェクトを開くと、右下に通知が表示されます：

```
Folder contains a Dev Container configuration file. Reopen folder to develop in a container.
```

**「Reopen in Container」**をクリックします。

または、コマンドパレット（`Ctrl+Shift+P` / `Cmd+Shift+P`）を開いて：
```
Dev Containers: Reopen in Container
```
を選択します。

### 3. 初回ビルド

初回起動時は、Dockerイメージのビルドと依存関係のインストールが自動的に実行されます。
これには数分かかる場合があります。

ビルドが完了すると、開発サーバーが自動的に起動します。

### 4. ブラウザでアクセス

開発サーバーが起動したら、ブラウザで以下のURLにアクセスします：

```
http://localhost:5173
```

Reactアプリケーションが表示されます。

## 利用可能なnpmスクリプト

コンテナ内のターミナルで以下のコマンドを実行できます：

### `npm run dev`
開発サーバーを起動します（通常は自動起動されます）。
- ポート: 5173
- Hot Module Replacement (HMR) 有効

### `npm run build`
プロダクション用にアプリケーションをビルドします。
- 出力先: `dist/` ディレクトリ

### `npm run preview`
ビルドしたアプリケーションをプレビューします。

### `npm run lint`
ESLintでコードをチェックします。

### `npm test`
テストを実行します（Vitestを使用）。

## プロジェクト構造

```
.
├── .devcontainer/          # Dev Container設定
│   ├── devcontainer.json   # コンテナ設定
│   └── Dockerfile          # Dockerイメージ定義
├── src/                    # ソースコード
│   ├── main.tsx           # エントリーポイント
│   ├── App.tsx            # ルートコンポーネント
│   └── index.css          # グローバルスタイル
├── public/                 # 静的アセット
├── tests/                  # テストファイル
├── index.html             # HTMLエントリーポイント
├── package.json           # 依存関係定義
├── tsconfig.json          # TypeScript設定
├── vite.config.ts         # Vite設定
├── tailwind.config.js     # Tailwind CSS設定
└── postcss.config.js      # PostCSS設定
```

## 既存のJSXファイルを画面として表示する方法

既にお持ちのJSXファイルを画面として表示するには、以下の手順に従ってください。

### 方法1: App.tsxを置き換える（最も簡単）

1. **既存のJSXファイルを配置**
   ```bash
   # 既存のJSXファイルを src/ ディレクトリにコピー
   # 例: MyComponent.jsx → src/MyComponent.tsx
   ```

2. **ファイル名を変更（必要に応じて）**
   - `.jsx` → `.tsx` に変更（TypeScript対応）
   - または `.jsx` のまま使用可能

3. **src/App.tsx を編集して既存コンポーネントをインポート**
   ```tsx
   import React from 'react'
   import MyComponent from './MyComponent'  // 既存のJSXファイル
   
   function App() {
     return (
       <div>
         <MyComponent />
       </div>
     )
   }
   
   export default App
   ```

4. **ブラウザで確認**
   - `http://localhost:5173` にアクセス
   - 既存のJSXファイルの内容が表示されます

### 方法2: 複数のJSXファイルを表示する

複数のJSXファイルを同時に表示したい場合：

```tsx
// src/App.tsx
import React from 'react'
import ComponentA from './ComponentA'
import ComponentB from './ComponentB'
import ComponentC from './ComponentC'

function App() {
  return (
    <div>
      <ComponentA />
      <ComponentB />
      <ComponentC />
    </div>
  )
}

export default App
```

### 方法3: ルーティングを使用して複数ページを作成

異なるURLで異なるJSXファイルを表示したい場合は、React Routerを使用します：

1. **React Routerをインストール**
   ```bash
   npm install react-router-dom
   ```

2. **src/App.tsx を編集**
   ```tsx
   import React from 'react'
   import { BrowserRouter, Routes, Route, Link } from 'react-router-dom'
   import PageA from './PageA'
   import PageB from './PageB'
   
   function App() {
     return (
       <BrowserRouter>
         <nav>
           <Link to="/">ページA</Link>
           <Link to="/page-b">ページB</Link>
         </nav>
         
         <Routes>
           <Route path="/" element={<PageA />} />
           <Route path="/page-b" element={<PageB />} />
         </Routes>
       </BrowserRouter>
     )
   }
   
   export default App
   ```

### JSXファイルの変換が必要な場合

既存のJSXファイルがReact 19やTypeScriptと互換性がない場合、以下の修正が必要になることがあります：

1. **Reactのインポートを追加（古いJSXの場合）**
   ```tsx
   import React from 'react'
   ```

2. **型定義を追加（TypeScriptの場合）**
   ```tsx
   interface Props {
     title: string;
     count: number;
   }
   
   function MyComponent({ title, count }: Props) {
     // ...
   }
   ```

3. **export文を確認**
   ```tsx
   export default MyComponent  // デフォルトエクスポート
   // または
   export { MyComponent }      // 名前付きエクスポート
   ```

### 未導入のライブラリがある場合

既存のJSXファイルが外部ライブラリを使用している場合、以下の手順でインストールします。

#### 1. エラーメッセージを確認

ブラウザまたはVS Codeのターミナルに以下のようなエラーが表示されます：

```
Cannot find module 'axios'
Cannot find module '@mui/material'
Module not found: Can't resolve 'lodash'
```

#### 2. コンテナ内のターミナルでライブラリをインストール

VS Codeのターミナル（Dev Container内）で以下のコマンドを実行します：

```bash
# 単一のライブラリをインストール
npm install axios

# 複数のライブラリを同時にインストール
npm install axios lodash moment

# 特定のバージョンをインストール
npm install react-router-dom@6.20.0

# 開発用の依存関係としてインストール
npm install --save-dev @types/lodash
```

#### 3. TypeScript型定義をインストール（必要に応じて）

TypeScriptを使用している場合、型定義が必要なライブラリもあります：

```bash
# 型定義をインストール
npm install --save-dev @types/react-router-dom
npm install --save-dev @types/lodash
npm install --save-dev @types/node
```

**注意**: 多くの最新ライブラリは型定義が組み込まれているため、別途インストール不要です。

#### 4. 開発サーバーを再起動（必要に応じて）

通常、npmインストール後は自動的に反映されますが、反映されない場合は：

```bash
# Ctrl+C で開発サーバーを停止
npm run dev
```

#### よく使われるライブラリのインストール例

```bash
# ルーティング
npm install react-router-dom

# 状態管理
npm install zustand
npm install redux react-redux @reduxjs/toolkit

# UIライブラリ
npm install @mui/material @emotion/react @emotion/styled
npm install antd

# HTTPクライアント
npm install axios

# フォーム管理
npm install react-hook-form
npm install formik yup

# ユーティリティ
npm install lodash
npm install date-fns
npm install clsx

# アイコン
npm install react-icons
npm install @mui/icons-material

# CSS-in-JS
npm install styled-components
npm install @emotion/react @emotion/styled
```

#### package.jsonの確認

インストール後、`package.json`の`dependencies`または`devDependencies`にライブラリが追加されていることを確認できます：

```json
{
  "dependencies": {
    "react": "^19.2.1",
    "react-dom": "^19.2.1",
    "axios": "^1.6.0",  // 新しく追加されたライブラリ
    "lodash": "^4.17.21"
  }
}
```

#### ライブラリのアンインストール

不要になったライブラリを削除する場合：

```bash
npm uninstall axios
npm uninstall lodash moment
```

### トラブルシューティング

**エラー: `Cannot find module './MyComponent'`**
- ファイルパスが正しいか確認
- ファイル拡張子（.tsx, .jsx）を確認
- インポート文で拡張子を省略していることを確認

**エラー: `Cannot find module 'some-library'`**
- ライブラリがインストールされているか確認: `npm list some-library`
- インストールされていない場合: `npm install some-library`
- node_modulesを再インストール: `rm -rf node_modules && npm install`

**エラー: `JSX element type does not have any construct or call signatures`**
- コンポーネントが正しくエクスポートされているか確認
- インポート文が正しいか確認（default export vs named export）

**エラー: TypeScript型エラー（例: `Property 'xxx' does not exist on type 'yyy'`）**
- 型定義をインストール: `npm install --save-dev @types/library-name`
- 型定義が存在しない場合は、`src/`に`.d.ts`ファイルを作成して型を定義

**スタイルが適用されない**
- CSSファイルがインポートされているか確認
- Tailwind CSSを使用する場合は、クラス名を確認
- UIライブラリのCSSをインポートしているか確認（例: `import 'antd/dist/reset.css'`）

## 開発ワークフロー

1. **ファイルを編集**: `src/`ディレクトリ内のファイルを編集
2. **自動リロード**: 保存すると自動的にブラウザが更新されます（HMR）
3. **エラー確認**: コンパイルエラーがある場合、ブラウザにオーバーレイで表示されます

## トラブルシューティング

### 開発サーバーが起動しない

**症状**: ブラウザで`localhost:5173`にアクセスできない

**解決方法**:
1. VS Codeのターミナルで開発サーバーが起動しているか確認
2. 起動していない場合、手動で起動:
   ```bash
   npm run dev
   ```
3. ポート5173が他のプロセスで使用されていないか確認

### ファイル変更が反映されない

**症状**: ファイルを保存してもブラウザが更新されない

**解決方法**:
1. `vite.config.ts`で`usePolling: true`が設定されているか確認
2. 開発サーバーを再起動:
   ```bash
   # Ctrl+C で停止
   npm run dev
   ```
3. ブラウザのキャッシュをクリア

### コンテナのビルドが失敗する

**症状**: Dev Containerの起動時にエラーが発生

**解決方法**:
1. Docker Desktopが起動しているか確認
2. コンテナを再ビルド:
   - コマンドパレット → `Dev Containers: Rebuild Container`
3. Dockerのログを確認:
   ```bash
   docker logs <container-id>
   ```

### 依存関係のインストールエラー

**症状**: `npm install`が失敗する

**解決方法**:
1. `node_modules`を削除して再インストール:
   ```bash
   rm -rf node_modules
   npm install
   ```
2. npmキャッシュをクリア:
   ```bash
   npm cache clean --force
   npm install
   ```

### ポート競合エラー

**症状**: `EADDRINUSE: address already in use :::5173`

**解決方法**:
1. ポート5173を使用しているプロセスを確認:
   ```bash
   # Windows
   netstat -ano | findstr :5173
   
   # Mac/Linux
   lsof -i :5173
   ```
2. 該当プロセスを停止するか、`vite.config.ts`で別のポートを指定

### VS Code拡張機能が動作しない

**症状**: ESLintやPrettierが機能しない

**解決方法**:
1. コンテナ内に拡張機能がインストールされているか確認
2. `.devcontainer/devcontainer.json`の`extensions`リストを確認
3. コンテナを再ビルド

### ファイルの権限エラー

**症状**: ファイルの作成や編集ができない

**解決方法**:
1. `.devcontainer/devcontainer.json`で`remoteUser`が`node`に設定されているか確認
2. Dockerfileで適切なユーザー権限が設定されているか確認

## コンテナの停止と再起動

### コンテナを停止
VS Codeを閉じるか、コマンドパレットから：
```
Dev Containers: Close Remote Connection
```

### コンテナを再起動
プロジェクトをVS Codeで開き、再度「Reopen in Container」を選択

### コンテナを完全に削除
```bash
docker ps -a  # コンテナIDを確認
docker rm <container-id>
docker volume prune  # 不要なボリュームを削除
```

## 技術スタック

- **React**: 19.2.1
- **TypeScript**: 5.6.0
- **Vite**: 7.2.6
- **Tailwind CSS**: 3.4.0
- **Node.js**: 22 (LTS)
- **Vitest**: テストフレームワーク

## ライセンス

このプロジェクトのライセンスについては、プロジェクトオーナーにお問い合わせください。

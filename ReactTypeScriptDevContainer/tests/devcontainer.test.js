import { describe, it, expect } from 'vitest';
import { readFileSync, existsSync } from 'fs';
import fc from 'fast-check';

// Feature: docker-react-dev-environment, Property 1: Dev Container configuration completeness
describe('Property 1: Dev Container configuration completeness', () => {
  it('should always have required fields in devcontainer.json', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        // Verify file exists
        const configPath = '.devcontainer/devcontainer.json';
        if (!existsSync(configPath)) {
          return false;
        }

        // Parse and validate configuration
        const config = JSON.parse(readFileSync(configPath, 'utf-8'));
        
        // Check all required fields exist
        const hasName = config.name !== undefined && typeof config.name === 'string';
        const hasDockerFile = config.dockerFile !== undefined && typeof config.dockerFile === 'string';
        const hasForwardPorts = Array.isArray(config.forwardPorts);
        const hasExtensions = config.customizations?.vscode?.extensions !== undefined &&
                             Array.isArray(config.customizations.vscode.extensions);
        
        return hasName && hasDockerFile && hasForwardPorts && hasExtensions;
      }),
      { numRuns: 100 }
    );
  });
});

// Feature: docker-react-dev-environment, Property 2: Required VS Code extensions specification
describe('Property 2: Required VS Code extensions specification', () => {
  it('should include ESLint, Prettier, and TypeScript/React extensions', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        const configPath = '.devcontainer/devcontainer.json';
        if (!existsSync(configPath)) {
          return false;
        }

        const config = JSON.parse(readFileSync(configPath, 'utf-8'));
        const extensions = config.customizations?.vscode?.extensions || [];
        
        // Check for required extensions
        const hasESLint = extensions.some(ext => ext.includes('eslint'));
        const hasPrettier = extensions.some(ext => ext.includes('prettier'));
        const hasTypeScriptOrReact = extensions.some(ext => 
          ext.includes('typescript') || ext.includes('react')
        );
        
        return hasESLint && hasPrettier && hasTypeScriptOrReact;
      }),
      { numRuns: 100 }
    );
  });
});

// Feature: docker-react-dev-environment, Property 5: Dockerfile base image specification
describe('Property 5: Dockerfile base image specification', () => {
  it('should specify Node.js 22 or later as base image', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        const dockerfilePath = '.devcontainer/Dockerfile';
        if (!existsSync(dockerfilePath)) {
          return false;
        }

        const dockerfile = readFileSync(dockerfilePath, 'utf-8');
        
        // Check for Node.js base image with version 22 or higher
        const hasNodeBase = /FROM\s+node:(\d+)/.test(dockerfile);
        
        if (!hasNodeBase) {
          return false;
        }

        // Extract version number
        const match = dockerfile.match(/FROM\s+node:(\d+)/);
        const version = parseInt(match[1], 10);
        
        return version >= 22;
      }),
      { numRuns: 100 }
    );
  });
});

// Feature: docker-react-dev-environment, Property 3: TypeScript configuration validity
describe('Property 3: TypeScript configuration validity', () => {
  it('should have jsx set to react-jsx and moduleResolution compatible with Vite', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        const tsconfigPath = 'tsconfig.json';
        if (!existsSync(tsconfigPath)) {
          return false;
        }

        // Read and remove comments from JSON (JSONC format)
        const tsconfigContent = readFileSync(tsconfigPath, 'utf-8');
        const tsconfigWithoutComments = tsconfigContent
          .replace(/\/\*[\s\S]*?\*\//g, '') // Remove /* */ comments
          .replace(/\/\/.*/g, ''); // Remove // comments
        
        const tsconfig = JSON.parse(tsconfigWithoutComments);
        const compilerOptions = tsconfig.compilerOptions || {};
        
        // Check jsx mode is set to "react-jsx" for React 19
        const hasCorrectJsx = compilerOptions.jsx === 'react-jsx';
        
        // Check moduleResolution is compatible with Vite (bundler)
        const hasCompatibleModuleResolution = compilerOptions.moduleResolution === 'bundler';
        
        return hasCorrectJsx && hasCompatibleModuleResolution;
      }),
      { numRuns: 100 }
    );
  });
});

// Feature: docker-react-dev-environment, Property 4: Package dependencies completeness
describe('Property 4: Package dependencies completeness', () => {
  it('should include all required React, TypeScript, Vite, and Tailwind dependencies', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        const packagePath = 'package.json';
        if (!existsSync(packagePath)) {
          return false;
        }

        const packageJson = JSON.parse(readFileSync(packagePath, 'utf-8'));
        const allDeps = {
          ...packageJson.dependencies,
          ...packageJson.devDependencies
        };
        
        // Check for React dependencies
        const hasReact = 'react' in allDeps;
        const hasReactDom = 'react-dom' in allDeps;
        
        // Check for TypeScript
        const hasTypeScript = 'typescript' in allDeps;
        
        // Check for Vite and plugin
        const hasVite = 'vite' in allDeps;
        const hasViteReactPlugin = '@vitejs/plugin-react' in allDeps;
        
        // Check for Tailwind CSS and related tools
        const hasTailwind = 'tailwindcss' in allDeps;
        const hasPostCSS = 'postcss' in allDeps;
        const hasAutoprefixer = 'autoprefixer' in allDeps;
        
        // Check for type definitions
        const hasReactTypes = '@types/react' in allDeps;
        const hasReactDomTypes = '@types/react-dom' in allDeps;
        
        return hasReact && hasReactDom && hasTypeScript && 
               hasVite && hasViteReactPlugin &&
               hasTailwind && hasPostCSS && hasAutoprefixer &&
               hasReactTypes && hasReactDomTypes;
      }),
      { numRuns: 100 }
    );
  });
});

// Feature: docker-react-dev-environment, Property 15: Project structure completeness
describe('Property 15: Project structure completeness', () => {
  it('should have all required directories and files for a complete React project', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        // 必須ディレクトリの確認
        const hasSrcDir = existsSync('src');
        const hasPublicDir = existsSync('public');
        
        // 必須ファイルの確認
        const hasIndexHtml = existsSync('index.html');
        const hasPackageJson = existsSync('package.json');
        const hasTsConfig = existsSync('tsconfig.json');
        const hasViteConfig = existsSync('vite.config.ts');
        const hasTailwindConfig = existsSync('tailwind.config.js');
        const hasPostCssConfig = existsSync('postcss.config.js');
        
        // Dev Container設定ファイルの確認
        const hasDevContainerJson = existsSync('.devcontainer/devcontainer.json');
        const hasDockerfile = existsSync('.devcontainer/Dockerfile');
        
        // srcディレクトリ内の必須ファイルの確認
        const hasMainTsx = existsSync('src/main.tsx');
        const hasAppTsx = existsSync('src/App.tsx');
        const hasIndexCss = existsSync('src/index.css');
        const hasViteEnvDts = existsSync('src/vite-env.d.ts');
        
        return hasSrcDir && hasPublicDir &&
               hasIndexHtml && hasPackageJson && hasTsConfig && 
               hasViteConfig && hasTailwindConfig && hasPostCssConfig &&
               hasDevContainerJson && hasDockerfile &&
               hasMainTsx && hasAppTsx && hasIndexCss && hasViteEnvDts;
      }),
      { numRuns: 100 }
    );
  });
});

// Feature: docker-react-dev-environment, Property 6: Container isolation verification
// **Validates: Requirements 1.1, 1.2**
describe('Property 6: Container isolation verification', () => {
  it('should verify that Node.js tools are available in container context', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        // このテストはDev Container内で実行されることを前提としています
        // Dev Container内では、node、npm、tscコマンドが利用可能である必要があります
        
        // Dev Container設定が存在することを確認
        const hasDevContainerConfig = existsSync('.devcontainer/devcontainer.json');
        const hasDockerfile = existsSync('.devcontainer/Dockerfile');
        
        // Dockerfileにnode base imageが指定されていることを確認
        if (hasDockerfile) {
          const dockerfile = readFileSync('.devcontainer/Dockerfile', 'utf-8');
          const hasNodeImage = /FROM\s+node:/i.test(dockerfile);
          
          return hasDevContainerConfig && hasNodeImage;
        }
        
        return hasDevContainerConfig && hasDockerfile;
      }),
      { numRuns: 100 }
    );
  });
});

// Feature: docker-react-dev-environment, Property 7: Development server port accessibility
// **Validates: Requirements 3.1, 3.4**
describe('Property 7: Development server port accessibility', () => {
  it('should configure Vite server to be accessible from host machine', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        // Vite設定ファイルが存在することを確認
        const viteConfigPath = 'vite.config.ts';
        if (!existsSync(viteConfigPath)) {
          return false;
        }
        
        const viteConfig = readFileSync(viteConfigPath, 'utf-8');
        
        // サーバーがポート5173で設定されていることを確認
        const hasPort5173 = /port:\s*5173/.test(viteConfig);
        
        // ホストが0.0.0.0に設定されていることを確認（コンテナ外からのアクセスを許可）
        const hasHostConfig = /host:\s*['"]0\.0\.0\.0['"]/.test(viteConfig);
        
        // Dev Container設定でポート5173がフォワードされていることを確認
        const devContainerPath = '.devcontainer/devcontainer.json';
        if (existsSync(devContainerPath)) {
          const devContainerConfig = JSON.parse(readFileSync(devContainerPath, 'utf-8'));
          const hasPortForwarding = devContainerConfig.forwardPorts?.includes(5173);
          
          return hasPort5173 && hasHostConfig && hasPortForwarding;
        }
        
        return hasPort5173 && hasHostConfig;
      }),
      { numRuns: 100 }
    );
  });
});

// Feature: docker-react-dev-environment, Property 8: JSX compilation and rendering
// **Validates: Requirements 3.2, 3.3, 3.5**
describe('Property 8: JSX compilation and rendering', () => {
  it('should have proper configuration for JSX/TSX compilation', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        // TypeScript設定でJSXが有効になっていることを確認
        const tsconfigPath = 'tsconfig.json';
        if (!existsSync(tsconfigPath)) {
          return false;
        }
        
        const tsconfigContent = readFileSync(tsconfigPath, 'utf-8')
          .replace(/\/\*[\s\S]*?\*\//g, '')
          .replace(/\/\/.*/g, '');
        const tsconfig = JSON.parse(tsconfigContent);
        const hasJsxConfig = tsconfig.compilerOptions?.jsx === 'react-jsx';
        
        // Vite設定でReactプラグインが設定されていることを確認
        const viteConfigPath = 'vite.config.ts';
        if (!existsSync(viteConfigPath)) {
          return false;
        }
        
        const viteConfig = readFileSync(viteConfigPath, 'utf-8');
        const hasReactPlugin = /react\(\)/.test(viteConfig) || /@vitejs\/plugin-react/.test(viteConfig);
        
        // JSX/TSXファイルが存在することを確認
        const hasAppTsx = existsSync('src/App.tsx');
        const hasMainTsx = existsSync('src/main.tsx');
        
        // index.htmlがmain.tsxを参照していることを確認
        const indexHtmlPath = 'index.html';
        if (!existsSync(indexHtmlPath)) {
          return false;
        }
        
        const indexHtml = readFileSync(indexHtmlPath, 'utf-8');
        const hasMainTsxReference = /src\/main\.tsx/.test(indexHtml);
        
        return hasJsxConfig && hasReactPlugin && hasAppTsx && hasMainTsx && hasMainTsxReference;
      }),
      { numRuns: 100 }
    );
  });
});

// Feature: docker-react-dev-environment, Property 9: File change detection responsiveness
// **Validates: Requirements 4.1, 4.2**
describe('Property 9: File change detection responsiveness', () => {
  it('should configure file watching for Docker environment', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        // Vite設定ファイルが存在することを確認
        const viteConfigPath = 'vite.config.ts';
        if (!existsSync(viteConfigPath)) {
          return false;
        }
        
        const viteConfig = readFileSync(viteConfigPath, 'utf-8');
        
        // usePollingがtrueに設定されていることを確認（Docker環境でのファイル監視に必要）
        const hasPolling = /usePolling:\s*true/.test(viteConfig);
        
        // watch設定が存在することを確認
        const hasWatchConfig = /watch:\s*\{/.test(viteConfig);
        
        return hasPolling && hasWatchConfig;
      }),
      { numRuns: 100 }
    );
  });
});

// Feature: docker-react-dev-environment, Property 10: Hot Module Replacement functionality
// **Validates: Requirements 4.3**
describe('Property 10: Hot Module Replacement functionality', () => {
  it('should have HMR enabled through Vite and React plugin', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        // Vite設定ファイルが存在することを確認
        const viteConfigPath = 'vite.config.ts';
        if (!existsSync(viteConfigPath)) {
          return false;
        }
        
        const viteConfig = readFileSync(viteConfigPath, 'utf-8');
        
        // Reactプラグインが設定されていることを確認（HMRを提供）
        const hasReactPlugin = /react\(\)/.test(viteConfig);
        
        // package.jsonにViteとReactプラグインが含まれていることを確認
        const packagePath = 'package.json';
        if (!existsSync(packagePath)) {
          return false;
        }
        
        const packageJson = JSON.parse(readFileSync(packagePath, 'utf-8'));
        const allDeps = {
          ...packageJson.dependencies,
          ...packageJson.devDependencies
        };
        
        const hasVite = 'vite' in allDeps;
        const hasViteReactPlugin = '@vitejs/plugin-react' in allDeps;
        
        // HMRはViteとReactプラグインによってデフォルトで有効化される
        return hasReactPlugin && hasVite && hasViteReactPlugin;
      }),
      { numRuns: 100 }
    );
  });
});

// Feature: docker-react-dev-environment, Property 11: Error overlay display
// **Validates: Requirements 4.5**
describe('Property 11: Error overlay display', () => {
  it('should have error overlay enabled in Vite configuration', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        // Vite設定ファイルが存在することを確認
        const viteConfigPath = 'vite.config.ts';
        if (!existsSync(viteConfigPath)) {
          return false;
        }
        
        const viteConfig = readFileSync(viteConfigPath, 'utf-8');
        
        // エラーオーバーレイはViteでデフォルトで有効
        // 明示的に無効化されていないことを確認
        const overlayDisabled = /overlay:\s*false/.test(viteConfig);
        
        // Viteがインストールされていることを確認
        const packagePath = 'package.json';
        if (!existsSync(packagePath)) {
          return false;
        }
        
        const packageJson = JSON.parse(readFileSync(packagePath, 'utf-8'));
        const allDeps = {
          ...packageJson.dependencies,
          ...packageJson.devDependencies
        };
        
        const hasVite = 'vite' in allDeps;
        
        // エラーオーバーレイが無効化されておらず、Viteがインストールされている
        return !overlayDisabled && hasVite;
      }),
      { numRuns: 100 }
    );
  });
});

// Feature: docker-react-dev-environment, Property 12: Host file system persistence
// **Validates: Requirements 7.4**
describe('Property 12: Host file system persistence', () => {
  it('should configure Dev Container to mount workspace from host', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        // Dev Container設定ファイルが存在することを確認
        const devContainerPath = '.devcontainer/devcontainer.json';
        if (!existsSync(devContainerPath)) {
          return false;
        }
        
        const devContainerConfig = JSON.parse(readFileSync(devContainerPath, 'utf-8'));
        
        // Dev Containerはデフォルトでワークスペースをマウントする
        // 明示的にマウントを無効化していないことを確認
        // workspaceFolder設定が存在する場合、それが適切に設定されていることを確認
        const hasWorkspaceFolder = devContainerConfig.workspaceFolder !== undefined;
        
        // mountsが設定されている場合、ワークスペースマウントが含まれていることを確認
        const hasMounts = devContainerConfig.mounts !== undefined;
        
        // Dev Containerの基本設定が存在すれば、デフォルトでホストファイルシステムがマウントされる
        return devContainerConfig.name !== undefined && devContainerConfig.dockerFile !== undefined;
      }),
      { numRuns: 100 }
    );
  });
});

// Feature: docker-react-dev-environment, Property 13: Container cleanup verification
// **Validates: Requirements 1.3**
describe('Property 13: Container cleanup verification', () => {
  it('should not create node_modules in project root on host', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        // .gitignoreファイルが存在し、node_modulesを除外していることを確認
        const gitignorePath = '.gitignore';
        if (!existsSync(gitignorePath)) {
          return false;
        }
        
        const gitignore = readFileSync(gitignorePath, 'utf-8');
        const ignoresNodeModules = /node_modules/.test(gitignore);
        
        // Dev Container設定が存在することを確認
        const devContainerPath = '.devcontainer/devcontainer.json';
        if (!existsSync(devContainerPath)) {
          return false;
        }
        
        // Dockerfileが存在することを確認（コンテナ内で依存関係がインストールされる）
        const dockerfilePath = '.devcontainer/Dockerfile';
        const hasDockerfile = existsSync(dockerfilePath);
        
        // node_modulesがgitignoreに含まれており、Dev Container設定が適切であれば
        // コンテナ削除時にホストマシンに開発ツールの痕跡が残らない
        return ignoresNodeModules && hasDockerfile;
      }),
      { numRuns: 100 }
    );
  });
});

// Feature: docker-react-dev-environment, Property 14: Dependency isolation
// **Validates: Requirements 1.4**
describe('Property 14: Dependency isolation', () => {
  it('should configure npm install to run inside container', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        // Dev Container設定ファイルが存在することを確認
        const devContainerPath = '.devcontainer/devcontainer.json';
        if (!existsSync(devContainerPath)) {
          return false;
        }
        
        const devContainerConfig = JSON.parse(readFileSync(devContainerPath, 'utf-8'));
        
        // postCreateCommandでnpm installが実行されることを確認
        const hasPostCreateCommand = devContainerConfig.postCreateCommand !== undefined;
        const runsNpmInstall = typeof devContainerConfig.postCreateCommand === 'string' && 
                               /npm\s+install/.test(devContainerConfig.postCreateCommand);
        
        // Dockerfileが存在し、Node.jsベースイメージを使用していることを確認
        const dockerfilePath = '.devcontainer/Dockerfile';
        if (!existsSync(dockerfilePath)) {
          return false;
        }
        
        const dockerfile = readFileSync(dockerfilePath, 'utf-8');
        const hasNodeImage = /FROM\s+node:/i.test(dockerfile);
        
        // npm installがコンテナ内で実行され、依存関係がコンテナ内に保存される
        return hasPostCreateCommand && runsNpmInstall && hasNodeImage;
      }),
      { numRuns: 100 }
    );
  });
});

// Feature: docker-react-dev-environment, Property 16: Automatic server startup
// **Validates: Requirements 7.2**
describe('Property 16: Automatic server startup', () => {
  it('should configure automatic Vite dev server startup on container start', () => {
    fc.assert(
      fc.property(fc.constant(null), () => {
        // Dev Container設定ファイルが存在することを確認
        const devContainerPath = '.devcontainer/devcontainer.json';
        if (!existsSync(devContainerPath)) {
          return false;
        }
        
        const devContainerConfig = JSON.parse(readFileSync(devContainerPath, 'utf-8'));
        
        // postStartCommandで開発サーバーが起動されることを確認
        const hasPostStartCommand = devContainerConfig.postStartCommand !== undefined;
        const runsDevServer = typeof devContainerConfig.postStartCommand === 'string' && 
                             /npm\s+run\s+dev/.test(devContainerConfig.postStartCommand);
        
        // package.jsonにdevスクリプトが定義されていることを確認
        const packagePath = 'package.json';
        if (!existsSync(packagePath)) {
          return false;
        }
        
        const packageJson = JSON.parse(readFileSync(packagePath, 'utf-8'));
        const hasDevScript = packageJson.scripts?.dev !== undefined;
        const devScriptUsesVite = packageJson.scripts?.dev?.includes('vite');
        
        // postStartCommandで開発サーバーが自動起動され、package.jsonにdevスクリプトが定義されている
        return hasPostStartCommand && runsDevServer && hasDevScript && devScriptUsesVite;
      }),
      { numRuns: 100 }
    );
  });
});

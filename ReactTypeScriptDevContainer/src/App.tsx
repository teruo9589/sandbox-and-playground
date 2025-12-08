import React from 'react'

function App() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
      <div className="bg-white rounded-lg shadow-xl p-8 max-w-md w-full">
        <h1 className="text-3xl font-bold text-gray-800 mb-4">
          React + TypeScript + Vite へようこそ
        </h1>
        <p className="text-gray-600 mb-6">
          このアプリケーションはDev Container内で動作しています。
          JSXコンパイルとTailwind CSSが正常に機能していることを確認できます。
        </p>
        <div className="bg-indigo-50 border-l-4 border-indigo-500 p-4 rounded">
          <p className="text-sm text-indigo-700">
            ✨ ファイルを編集してHot Module Replacementを体験してください！
          </p>
        </div>
      </div>
    </div>
  )
}

export default App

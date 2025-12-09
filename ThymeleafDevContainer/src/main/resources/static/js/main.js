// メインJavaScriptファイル

// DOMの読み込み完了後に実行
document.addEventListener('DOMContentLoaded', function() {
    console.log('Spring Boot + Thymeleaf アプリケーションが読み込まれました');
    
    // 現在時刻の更新（デモ用）
    updateCurrentTime();
    
    // テーブルの行にホバー効果を追加
    addTableRowEffects();
    
    // ページ読み込み完了メッセージ
    showWelcomeMessage();
});

/**
 * 現在時刻を更新する関数
 */
function updateCurrentTime() {
    const timeElements = document.querySelectorAll('.current-time');
    if (timeElements.length > 0) {
        setInterval(function() {
            const now = new Date();
            const formattedTime = now.toLocaleString('ja-JP', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            });
            timeElements.forEach(function(element) {
                element.textContent = formattedTime;
            });
        }, 1000);
    }
}

/**
 * テーブルの行にインタラクティブな効果を追加
 */
function addTableRowEffects() {
    const tableRows = document.querySelectorAll('table tbody tr');
    tableRows.forEach(function(row) {
        row.addEventListener('click', function() {
            // 行がクリックされたときの処理（必要に応じて実装）
            console.log('行がクリックされました:', this);
        });
    });
}

/**
 * ウェルカムメッセージを表示
 */
function showWelcomeMessage() {
    console.log('=================================');
    console.log('Spring Boot + Thymeleaf 開発環境');
    console.log('Docker環境で正常に動作しています');
    console.log('=================================');
}

/**
 * エラーハンドリング用のグローバル関数
 */
window.addEventListener('error', function(event) {
    console.error('エラーが発生しました:', event.error);
});

/**
 * 未処理のPromise拒否をキャッチ
 */
window.addEventListener('unhandledrejection', function(event) {
    console.error('未処理のPromise拒否:', event.reason);
});

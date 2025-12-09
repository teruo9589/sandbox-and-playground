package com.example.app.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;

import java.sql.SQLException;

/**
 * グローバル例外ハンドラー
 * アプリケーション全体の例外を一元的に処理
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * データベース接続エラーのハンドリング
     * 
     * @param e データアクセス例外
     * @param model モデル
     * @return エラーページ
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDataAccessException(DataAccessException e, Model model) {
        log.error("データベースアクセスエラー: {}", e.getMessage(), e);
        
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", "データベースエラー");
        model.addAttribute("message", "データベースへのアクセス中にエラーが発生しました。しばらく時間をおいてから再度お試しください。");
        
        return "error";
    }
    
    /**
     * SQL例外のハンドリング
     * 
     * @param e SQL例外
     * @param model モデル
     * @return エラーページ
     */
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleSQLException(SQLException e, Model model) {
        log.error("SQLエラー: {}", e.getMessage(), e);
        
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", "データベースエラー");
        model.addAttribute("message", "データベース操作中にエラーが発生しました。");
        
        return "error";
    }
    
    /**
     * Thymeleafテンプレート入力エラーのハンドリング
     * 
     * @param e テンプレート入力例外
     * @param model モデル
     * @return エラーページ
     */
    @ExceptionHandler(TemplateInputException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleTemplateInputException(TemplateInputException e, Model model) {
        log.error("テンプレートエラー: {}", e.getMessage(), e);
        
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", "テンプレートエラー");
        model.addAttribute("message", "ページの表示中にエラーが発生しました。");
        
        return "error";
    }
    
    /**
     * Thymeleafテンプレート処理エラーのハンドリング
     * 
     * @param e テンプレート処理例外
     * @param model モデル
     * @return エラーページ
     */
    @ExceptionHandler(TemplateProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleTemplateProcessingException(TemplateProcessingException e, Model model) {
        log.error("テンプレート処理エラー: {}", e.getMessage(), e);
        
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", "テンプレート処理エラー");
        model.addAttribute("message", "ページの処理中にエラーが発生しました。");
        
        return "error";
    }
    
    /**
     * 404エラー（ページが見つからない）のハンドリング
     * 
     * @param e NoHandlerFoundException
     * @param model モデル
     * @return エラーページ
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NoHandlerFoundException e, Model model) {
        log.warn("ページが見つかりません: {}", e.getRequestURL());
        
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", "ページが見つかりません");
        model.addAttribute("message", "お探しのページは見つかりませんでした。");
        
        return "error";
    }
    
    /**
     * IllegalArgumentException（不正な引数）のハンドリング
     * 
     * @param e IllegalArgumentException
     * @param model モデル
     * @return エラーページ
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException e, Model model) {
        log.warn("不正な引数: {}", e.getMessage());
        
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("error", "不正なリクエスト");
        model.addAttribute("message", e.getMessage());
        
        return "error";
    }
    
    /**
     * RuntimeException（実行時例外）のハンドリング
     * 
     * @param e RuntimeException
     * @param model モデル
     * @return エラーページ
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeException(RuntimeException e, Model model) {
        log.error("実行時エラー: {}", e.getMessage(), e);
        
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", "内部サーバーエラー");
        model.addAttribute("message", "予期しないエラーが発生しました。");
        
        return "error";
    }
    
    /**
     * その他の例外のハンドリング
     * 
     * @param e Exception
     * @param model モデル
     * @return エラーページ
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception e, Model model) {
        log.error("予期しないエラー: {}", e.getMessage(), e);
        
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("error", "エラー");
        model.addAttribute("message", "予期しないエラーが発生しました。しばらく時間をおいてから再度お試しください。");
        
        return "error";
    }
}

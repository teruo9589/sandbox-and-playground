package com.example.app.controller;

import com.example.app.entity.SampleEntity;
import com.example.app.service.SampleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * サンプルコントローラークラス
 * HTTPリクエストを処理し、Thymeleafテンプレートを返却
 */
@Controller
public class SampleController {
    
    private static final Logger log = LoggerFactory.getLogger(SampleController.class);
    
    private final SampleService sampleService;
    private final Environment environment;
    
    @Value("${spring.application.name:spring-boot-thymeleaf-app}")
    private String applicationName;
    
    /**
     * コンストラクタ
     * 
     * @param sampleService サンプルサービス
     * @param environment 環境情報
     */
    public SampleController(SampleService sampleService, Environment environment) {
        this.sampleService = sampleService;
        this.environment = environment;
    }
    
    /**
     * インデックスページを表示
     * 全てのエンティティを取得してモデルに設定
     * 
     * @param model モデル
     * @return テンプレート名
     */
    @GetMapping("/")
    public String index(Model model) {
        log.info("インデックスページにアクセス");
        
        // ビジネスロジックの実行
        List<SampleEntity> entities = sampleService.findAll();
        
        // アクティブプロファイルの取得
        String activeProfile = Arrays.stream(environment.getActiveProfiles())
            .findFirst()
            .orElse("default");
        
        // モデルへのデータ設定
        model.addAttribute("samples", entities);
        model.addAttribute("title", "サンプルアプリケーション");
        model.addAttribute("activeProfile", activeProfile);
        model.addAttribute("applicationName", applicationName);
        model.addAttribute("currentTime", LocalDateTime.now());
        
        // Thymeleafテンプレート名を返却
        return "index";
    }
    
    /**
     * 詳細ページを表示
     * 指定されたIDのエンティティを取得してモデルに設定
     * 
     * @param id エンティティID
     * @param model モデル
     * @return テンプレート名
     */
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        log.info("詳細ページにアクセス: id={}", id);
        
        // ビジネスロジックの実行
        SampleEntity entity = sampleService.findById(id)
            .orElseThrow(() -> new RuntimeException("エンティティが見つかりません: id=" + id));
        
        // モデルへのデータ設定
        model.addAttribute("entity", entity);
        model.addAttribute("title", "詳細 - " + entity.getName());
        
        // Thymeleafテンプレート名を返却
        return "detail";
    }
    
    /**
     * 作成フォームページを表示
     * 
     * @param model モデル
     * @return テンプレート名
     */
    @GetMapping("/create")
    public String createForm(Model model) {
        log.info("作成フォームページにアクセス");
        
        // モデルへのデータ設定
        model.addAttribute("title", "新規作成");
        
        // Thymeleafテンプレート名を返却
        return "create";
    }
    
    /**
     * エンティティを作成
     * 
     * @param name 名前
     * @param redirectAttributes リダイレクト属性
     * @return リダイレクト先
     */
    @PostMapping("/create")
    public String create(@RequestParam String name, RedirectAttributes redirectAttributes) {
        log.info("エンティティを作成: name={}", name);
        
        try {
            // ビジネスロジックの実行
            SampleEntity entity = sampleService.create(name);
            
            // 成功メッセージを設定
            redirectAttributes.addFlashAttribute("message", "エンティティを作成しました: " + entity.getName());
            redirectAttributes.addFlashAttribute("messageType", "success");
            
            // インデックスページにリダイレクト
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            log.warn("エンティティの作成に失敗: {}", e.getMessage());
            
            // エラーメッセージを設定
            redirectAttributes.addFlashAttribute("message", "エラー: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
            
            // 作成フォームにリダイレクト
            return "redirect:/create";
        }
    }
    
    /**
     * 編集フォームページを表示
     * 
     * @param id エンティティID
     * @param model モデル
     * @return テンプレート名
     */
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        log.info("編集フォームページにアクセス: id={}", id);
        
        // ビジネスロジックの実行
        SampleEntity entity = sampleService.findById(id)
            .orElseThrow(() -> new RuntimeException("エンティティが見つかりません: id=" + id));
        
        // モデルへのデータ設定
        model.addAttribute("entity", entity);
        model.addAttribute("title", "編集 - " + entity.getName());
        
        // Thymeleafテンプレート名を返却
        return "edit";
    }
    
    /**
     * エンティティを更新
     * 
     * @param id エンティティID
     * @param name 新しい名前
     * @param redirectAttributes リダイレクト属性
     * @return リダイレクト先
     */
    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @RequestParam String name, RedirectAttributes redirectAttributes) {
        log.info("エンティティを更新: id={}, name={}", id, name);
        
        try {
            // ビジネスロジックの実行
            SampleEntity entity = sampleService.update(id, name);
            
            // 成功メッセージを設定
            redirectAttributes.addFlashAttribute("message", "エンティティを更新しました: " + entity.getName());
            redirectAttributes.addFlashAttribute("messageType", "success");
            
            // インデックスページにリダイレクト
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            log.warn("エンティティの更新に失敗: {}", e.getMessage());
            
            // エラーメッセージを設定
            redirectAttributes.addFlashAttribute("message", "エラー: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
            
            // 編集フォームにリダイレクト
            return "redirect:/edit/" + id;
        }
    }
    
    /**
     * エンティティを削除
     * 
     * @param id エンティティID
     * @param redirectAttributes リダイレクト属性
     * @return リダイレクト先
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("エンティティを削除: id={}", id);
        
        try {
            // ビジネスロジックの実行
            sampleService.delete(id);
            
            // 成功メッセージを設定
            redirectAttributes.addFlashAttribute("message", "エンティティを削除しました");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (RuntimeException e) {
            log.warn("エンティティの削除に失敗: {}", e.getMessage());
            
            // エラーメッセージを設定
            redirectAttributes.addFlashAttribute("message", "エラー: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        
        // インデックスページにリダイレクト
        return "redirect:/";
    }
}

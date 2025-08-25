package jp.suzukiasuka.stockmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // ログイン画面の表示
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // login.html を表示
    }

    // Spring Securityの機能でPOST /login は処理されるので、ここには不要！

    // ログアウト処理（SecurityConfig で自動的に処理される）
    @GetMapping("/logout-success")
    public String logoutSuccess() {
        return "redirect:/login?logout";
    }
}
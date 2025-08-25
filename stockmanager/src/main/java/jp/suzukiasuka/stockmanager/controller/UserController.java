package jp.suzukiasuka.stockmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jp.suzukiasuka.stockmanager.model.User;
import jp.suzukiasuka.stockmanager.service.UserService;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    // 新規登録フォーム表示
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";  // register.html に対応
    }

    // 新規登録処理
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        // メールアドレスを正規化（小文字＋トリム）
        String email = user.getEmail().trim().toLowerCase();
        user.setEmail(email);

        // role が null なら USER を設定
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }

        boolean success = userService.registerUser(user);
        if (!success) {
            model.addAttribute("error", "そのメールアドレスはすでに使われています。");
            return "register";
        }

        return "redirect:/register-success";
    }

    // 登録完了画面
    @GetMapping("/register-success")
    public String showRegisterSuccess() {
        return "register-success";
    }
}
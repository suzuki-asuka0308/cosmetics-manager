package jp.suzukiasuka.stockmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import jp.suzukiasuka.stockmanager.model.User;
import jp.suzukiasuka.stockmanager.service.UserService;

@Controller
public class MenuController {

    @Autowired
    private UserService userService;

    // メニュー画面の表示
    @GetMapping("/menu")
    public String showMenu(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        User user = userService.findById(userId);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        String role = user.getRole() != null ? user.getRole().trim().toUpperCase() : "";

        System.out.println("【DEBUG】メニュー画面へ: userId=" + userId + ", role=" + role);

        if ("ADMIN".equals(role)) {
            return "menu"; // 管理者メニュー
        } else {
            return "user-menu"; // 一般ユーザーメニュー
        }
    }

    // CSV出力ページの表示
    @GetMapping("/csv-export")
    public String showCsvExportPage(HttpSession session) {
        String role = (String) session.getAttribute("userRole");
        if (!"ADMIN".equals(role)) {
            return "redirect:/products/list";
        }
        return "csv-export"; // ← templates/csv-export.html を表示
    }
}
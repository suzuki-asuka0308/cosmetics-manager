package jp.suzukiasuka.stockmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ItemController {

    @GetMapping("/items")
    public String showItemList() {
        return "item/list"; // ← 修正！ファイル名と一致させる
    }
}
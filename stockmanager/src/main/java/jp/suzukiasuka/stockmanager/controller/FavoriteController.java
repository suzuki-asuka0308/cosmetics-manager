package jp.suzukiasuka.stockmanager.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import jp.suzukiasuka.stockmanager.model.Favorite;
import jp.suzukiasuka.stockmanager.model.Product;
import jp.suzukiasuka.stockmanager.repository.FavoriteRepository;
import jp.suzukiasuka.stockmanager.repository.ProductRepository;

@Controller
public class FavoriteController {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private HttpSession session;

    // お気に入り一覧表示
    @GetMapping("/favorites")
    public String showFavorites(Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        List<Long> productIds = favorites.stream()
                .map(Favorite::getProductId)
                .collect(Collectors.toList());

        List<Product> favoriteProducts = productRepository.findAllById(productIds);
        model.addAttribute("favoriteProducts", favoriteProducts);
        return "favorite-list";
    }

    // ✅ お気に入り追加（非同期・画面に何も表示しない）
    @PostMapping("/favorites/add/{id}")
    @ResponseBody
    public ResponseEntity<Void> addFavorite(@PathVariable("id") Long productId) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // すでに登録済みでなければ追加
        boolean alreadyFavorite = favoriteRepository.existsByUserIdAndProductId(userId, productId);
        if (!alreadyFavorite) {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setProductId(productId);
            favoriteRepository.save(favorite);
        }

        return ResponseEntity.ok().build(); // メッセージなしでOKステータス返す
    }

    // ✅ お気に入り削除（非同期・画面に何も表示しない）
    @DeleteMapping("/favorites/remove/{id}")
    @ResponseBody
    public ResponseEntity<Void> removeFavorite(@PathVariable("id") Long productId) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        for (Favorite fav : favorites) {
            if (fav.getProductId().equals(productId)) {
                favoriteRepository.delete(fav);
                return ResponseEntity.ok().build(); // 削除成功
            }
        }

        return ResponseEntity.notFound().build(); // お気に入りが見つからなければ404
    }
}
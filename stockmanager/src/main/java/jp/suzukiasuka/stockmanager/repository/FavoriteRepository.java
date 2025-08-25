package jp.suzukiasuka.stockmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jp.suzukiasuka.stockmanager.model.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // お気に入り商品のID一覧取得（一覧画面で表示制御に使用）
    @Query("SELECT f.productId FROM Favorite f WHERE f.userId = :userId")
    List<Long> findFavoriteProductIdsByUserId(@Param("userId") Long userId);

    // ユーザーのお気に入り一覧取得
    List<Favorite> findByUserId(Long userId);

    // すでに登録済みか確認（重複登録防止用）★追加に必要
    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
package jp.suzukiasuka.stockmanager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.suzukiasuka.stockmanager.model.Product;
import jp.suzukiasuka.stockmanager.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // 商品一覧取得
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    // 商品をIDで検索
    public Product findById(long id) {
        return productRepository.findById(id).orElse(null);
    }

    // 商品を保存
    public void save(Product product) {
        productRepository.save(product);
    }

    // 商品を削除
    public void deleteById(long id) {
        productRepository.deleteById(id);
    }
}
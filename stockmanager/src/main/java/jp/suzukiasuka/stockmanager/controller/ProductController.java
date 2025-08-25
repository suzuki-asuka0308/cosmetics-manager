package jp.suzukiasuka.stockmanager.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jp.suzukiasuka.stockmanager.model.Product;
import jp.suzukiasuka.stockmanager.repository.FavoriteRepository;
import jp.suzukiasuka.stockmanager.repository.ProductRepository;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private HttpSession session;

    // 商品一覧
    @GetMapping
    public String showProductList(Model model) {
        model.addAttribute("products", productRepository.findAll());

        String role = (String) session.getAttribute("userRole");
        Long userId = (Long) session.getAttribute("userId");

        boolean isAdmin = "ADMIN".equals(role);
        model.addAttribute("isAdmin", isAdmin);

        if (!isAdmin && userId != null) {
            Set<Long> favoriteProductIds = new HashSet<>(favoriteRepository.findFavoriteProductIdsByUserId(userId));
            model.addAttribute("favoriteProductIds", favoriteProductIds);
        }

        return "products/product-list";
    }

    @GetMapping("/list")
    public String redirectToList() {
        return "redirect:/products";
    }

    // 商品詳細
    @GetMapping("/{id:\\d+}")
    public String showProductDetail(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/products";
        }
        model.addAttribute("product", product);
        return "products/product-detail";
    }

    // 編集フォーム表示
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/products";
        }
        model.addAttribute("product", product);
        return "products/product-edit";
    }

    // 編集保存
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product) {
        product.setId(id);
        productRepository.save(product);
        return "redirect:/products";
    }

    // 削除確認
    @GetMapping("/delete-confirm/{id}")
    public String showDeleteConfirm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/products";
        }
        model.addAttribute("product", product);
        return "products/product-delete-confirm";
    }

    // 商品削除
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        }
        return "redirect:/products";
    }

    // 登録フォーム表示（修正：productFormを追加）
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("productForm", new Product());
        return "products/product-create";
    }

    // 登録処理（画像あり対応）
    @PostMapping("/create")
    public String createProductViaCreate(
        @RequestParam("name") String name,
        @RequestParam("price") int price,
        @RequestParam("quantity") int quantity,
        @RequestParam(value = "category", required = false) String category,
        @RequestParam(value = "description", required = false) String description,
        @RequestParam(value = "image", required = false) MultipartFile imageFile,
        HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setCategory(category);
        product.setDescription(description);
        product.setUserId(userId);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                byte[] imageBytes = imageFile.getBytes();
                product.setImage(imageBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        productRepository.save(product);
        return "redirect:/products";
    }

    // CSV出力
    @GetMapping("/export")
    public void exportCsv(HttpServletResponse response) throws IOException {
        List<Product> products = productRepository.findAll();

        String filename = URLEncoder.encode("products.csv", StandardCharsets.UTF_8);
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + filename);

        PrintWriter writer = response.getWriter();
        writer.println("ID,商品名,価格,在庫数,カテゴリ,ユーザーID");

        for (Product product : products) {
            writer.printf("%d,%s,%d,%d,%s,%s%n",
                    product.getId(),
                    escapeCsv(product.getName()),
                    product.getPrice(),
                    product.getQuantity(),
                    escapeCsv(product.getCategory()),
                    product.getUserId() != null ? product.getUserId().toString() : ""
            );
        }

        writer.flush();
    }

    // CSVエスケープ
    private String escapeCsv(String input) {
        if (input == null) return "";
        String escaped = input.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
package jp.suzukiasuka.stockmanager.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jp.suzukiasuka.stockmanager.model.User;
import jp.suzukiasuka.stockmanager.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ログイン処理
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email.trim().toLowerCase());
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    // 新規登録処理
    public boolean registerUser(User user) {
        String email = user.getEmail().trim().toLowerCase();
        user.setEmail(email); // emailを正規化してから登録

        if (userRepository.findByEmail(email) != null) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 特定のメールアドレスのみ管理者、それ以外は一般ユーザー
        if ("suzuki.asuka0308@gmail.com".equals(email)) {
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }

        userRepository.save(user);
        return true;
    }

    // Emailでユーザー取得
    public User findByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase());
    }

    // IDでユーザー取得
    public User findById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElse(null);
    }
}
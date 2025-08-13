package com.example.springboot_ch_1.repository;

import com.example.springboot_ch_1.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/21
 * Time: 16:43
 * Description:
 */
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor {
    User findByUserAccount(String userAccount);

    User findByUserAccountAndPassword(String userAccount,String password);
}

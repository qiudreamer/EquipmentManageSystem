package com.example.springboot_ch_1.repository;

import com.example.springboot_ch_1.entity.UserRoot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created with IntelliJ IDEA
 * User: llbj
 * Date: 2025/7/26
 * Time: 5:23
 * Description:
 */
public interface UserRootRepository extends JpaRepository<UserRoot, String>, JpaSpecificationExecutor {
    UserRoot findByUserAccountAndUserPassword(String userAccount,String userPassword);
    UserRoot findByUserAccount(String userAccount);

    List<UserRoot> findAllByRootType(String rootType);
}

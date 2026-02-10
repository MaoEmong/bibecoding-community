package com.example.bibecoding01.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmployeeNo(String employeeNo);

    List<User> findAll();

    List<User> findPage(UserQueryCondition condition);

    long count(UserQueryCondition condition);

    void delete(User user);
}

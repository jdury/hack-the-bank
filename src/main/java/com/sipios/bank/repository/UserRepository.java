package com.sipios.bank.repository;

import com.sipios.bank.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByIban(String iban);
}

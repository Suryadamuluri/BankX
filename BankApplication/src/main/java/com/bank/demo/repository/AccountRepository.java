package com.bank.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.demo.entities.Account;
import java.util.Optional;

//public interface AccountRepository extends JpaRepository<Account , Long>{
//	Optional<Account> findByUserName(String username);

	public interface AccountRepository extends JpaRepository<Account, Long> {
	    Optional<Account> findByUsername(String username); // Update method name
	}



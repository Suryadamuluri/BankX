package com.bank.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.bank.demo.entities.Transaction;
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
	List<Transaction> findByAccountId(long accountId);
}

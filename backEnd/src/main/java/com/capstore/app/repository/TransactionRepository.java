package com.capstore.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.capstore.app.models.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

}

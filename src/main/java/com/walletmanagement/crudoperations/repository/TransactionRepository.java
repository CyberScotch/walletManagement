package com.walletmanagement.crudoperations.repository;

import com.walletmanagement.crudoperations.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Integer>, PagingAndSortingRepository<Transaction,Integer> {
    public Transaction findByTransactionId(Integer transactionId);
    public List<Transaction> findByFromUser(String fromUser);
    public List<Transaction> findByToUser(String toUser);

    public Page<Transaction> findByFromUser(String fromUser, Pageable page);

}

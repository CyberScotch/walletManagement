package com.walletmanagement.crudoperations.repository;

import com.walletmanagement.crudoperations.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Integer> {
    public Transaction findByTransactionId(Integer transactionId);
    public List<Transaction> findByFromUser(String fromUser);
    public List<Transaction> findByToUser(String toUser);

    /*public static final String FIND_TRANSACTIONS = "SELECT * FROM transaction WHERE ";
    @Query(value = FIND_TRANSACTIONS, nativeQuery = true)
    public List<Object[]> findProjects();*/

    public List<Transaction> findByFromUser(String fromUser, Pageable pageable);
    public List<Transaction> findByToUser(String toUser, Pageable pageable);
    //public List<Transaction> findPaginated(int pageNo,int pageSize);

    public List<Transaction> findByFromUserAndToUser(String fromUser,String toUser,Pageable pageable);

}

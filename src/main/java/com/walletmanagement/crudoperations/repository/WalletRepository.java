package com.walletmanagement.crudoperations.repository;

import com.walletmanagement.crudoperations.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Integer> {
        public Wallet findByWalletId(Integer walletId);
        public Wallet findByUserId(Integer userId);
}

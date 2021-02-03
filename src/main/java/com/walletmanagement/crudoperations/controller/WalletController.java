package com.walletmanagement.crudoperations.controller;

import com.walletmanagement.crudoperations.model.GeneralResponse;
import com.walletmanagement.crudoperations.model.Transaction;
import com.walletmanagement.crudoperations.model.User;
import com.walletmanagement.crudoperations.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class WalletController {

    @Autowired
    WalletService walletService;

    Logger logger= LoggerFactory.getLogger(WalletController.class);

    //To add a user
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public GeneralResponse insertUser(@RequestBody User user) {
        return walletService.addUser(user);
    }

    //Creating wallet for user
    @RequestMapping(value = "/wallet", method = RequestMethod.POST)
    public GeneralResponse createWallet(@RequestParam(value="phoneNumber") String phoneNo) {
        return walletService.addWallet(phoneNo);
    }

    //Updating balance in wallet
    @RequestMapping(value = "/addToWallet", method = RequestMethod.POST)
    public GeneralResponse addBalanceInWallet(@RequestParam(value="amount") Integer amount, @RequestParam(value="userId") Integer userId)
    {
        return walletService.updateBalance(amount,userId);
    }

    //Transferring balance from one wallet to other
    @RequestMapping(value = "/transaction", method = RequestMethod.POST)
    public GeneralResponse transferBalanceFromWallet(@RequestParam(value="payer") String user1, @RequestParam(value="payee") String user2 , @RequestParam(value="amount") Integer amount)
    {
        return walletService.transferBalance(user1,user2,amount);
    }

    //getting transaction status
    @RequestMapping(value = "/transactions", method = RequestMethod.GET)
    public GeneralResponse getTransactionStatus(@RequestParam(value="txnId") Integer userId) {
        return walletService.getStatus(userId);
    }

    //getting Transaction summary
    @RequestMapping(value = "/transaction", method = RequestMethod.GET)
    public Page<Transaction> getTransactionSummary(@RequestParam(value="userId") Integer userId) {
        return walletService.getSummary(userId);
    }
}

package com.walletmanagement.crudoperations.service;

import com.walletmanagement.crudoperations.model.GeneralResponse;
import com.walletmanagement.crudoperations.model.Transaction;
import com.walletmanagement.crudoperations.model.User;
import com.walletmanagement.crudoperations.model.Wallet;
import com.walletmanagement.crudoperations.repository.TransactionRepository;
import com.walletmanagement.crudoperations.repository.UserRepository;
import com.walletmanagement.crudoperations.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.ws.ServiceMode;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class WalletService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    WalletRepository walletRepository;
    @Autowired
    TransactionRepository transactionRepository;

    //For adding log traces
    Logger logger= LoggerFactory.getLogger(WalletService.class);

    //To add a user
    @Transactional
    public GeneralResponse addUser(User user) {
        GeneralResponse gr=new GeneralResponse();
        gr.setStatus("FAILED");
        try {
            String phoneNumber = user.getPhoneNumber();
            logger.trace("Adding user");
            //adding user if no duplicate data
           if(userRepository.findByPhoneNumber(phoneNumber) != null) {
               gr.setMessage("Phone Number exists");
               logger.trace("Existing Phone Number");
           }
            else{
                //validating phone number
                if(!Pattern.matches("^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}$",phoneNumber))
                {
                    gr.setMessage("Invalid PhoneNumber");
                    logger.trace(" Invalid PhoneNumber");
                    return gr;
                }
                //saving user details
                userRepository.save(user);
                gr.setMessage("User saved");
                gr.setStatus("STATUS: OK");
                logger.trace("User Saved");
            }
            //returning responseObject
            return gr;
        }
        catch(Exception e)
        {
            String str=e.getMessage();
            gr.setMessage("Exception Occurred "+ str);
            logger.trace(" Exception occurred");
            return gr;
        }
    }

    @Transactional
    public GeneralResponse addWallet(String phoneNo) {
        GeneralResponse gr = new GeneralResponse();
        gr.setStatus("FAILED");
        try {
            //checking if user is present

            if (userRepository.findByPhoneNumber(phoneNo) !=null) {

                User callUser=userRepository.findByPhoneNumber(phoneNo);

                //creating wallet with 0 balance
                Wallet wallet = new Wallet();
                wallet.setUserId(callUser.getId());
                wallet.setBalance(0);
                walletRepository.save(wallet);

                gr.setMessage("Wallet Created");
                gr.setStatus("Status: OK");
                logger.trace("Wallet Created");
                return gr;
            }
            gr.setMessage("User with phoneNumber not exists");
            logger.trace(" Non existing user");
            return gr;
        }
        catch (Exception e) {
            String str = e.getMessage();
            gr.setMessage("Exception Occurred " + str);
            return gr;
        }
    }

    @Transactional
    public GeneralResponse updateBalance(Integer balance,Integer userId) {
        GeneralResponse gr = new GeneralResponse();
        gr.setStatus("FAILED");
        try {
            //checking if user is present
            if (walletRepository.findByUserId(userId) !=null) {

                //creating wallet with 0 balance
                Wallet wallet = walletRepository.findByUserId(userId);
                wallet.setBalance(balance);
                walletRepository.save(wallet);

                gr.setMessage("Wallet Updated");
                gr.setStatus("Status: OK");
                logger.trace("Wallet updated");
                return gr;
            }
            gr.setMessage("User with wallet not exists");
            logger.trace(" Non existing user");
            return gr;
        }
        catch (Exception e) {
            String str = e.getMessage();
            gr.setMessage("Exception Occurred " + str);
            return gr;
        }
    }

    @Transactional
    public GeneralResponse transferBalance(String user1,String user2,Integer amount) {
        GeneralResponse gr = new GeneralResponse();
        gr.setStatus("FAILED");
        try {
            //checking if user is present
            if(userRepository.findByPhoneNumber(user1) == null)
            {
                gr.setMessage("Payer Not exists");
                logger.trace("Payer Not exists");
                return gr;
            }
            else if(userRepository.findByPhoneNumber(user2) == null)
            {
                gr.setMessage("Payee Not exists");
                logger.trace("Payee Not exists");
                return gr;
            }
            else {
                User callUser1=userRepository.findByPhoneNumber(user1);
                User callUser2=userRepository.findByPhoneNumber(user2);
                Integer currentPayerId=callUser1.getId();
                Integer currentPayeeId=callUser2.getId();

                Wallet payerWallet=walletRepository.findByUserId(currentPayerId);
                Wallet payeeWallet=walletRepository.findByUserId(currentPayeeId);

                Integer payerBalance=payerWallet.getBalance();
                Integer payeeBalance=payeeWallet.getBalance();

                if(payerBalance<amount)
                {
                    gr.setMessage("Not enough balance");
                    logger.trace("Not enough balance");
                    return gr;
                }
                else {
                    //make transfer
                    payerBalance = payerBalance - amount;
                    payeeBalance = payeeBalance + amount;
                    payerWallet.setBalance(payerBalance);
                    payeeWallet.setBalance(payeeBalance);
                    walletRepository.save(payerWallet);
                    walletRepository.save(payeeWallet);

                    gr.setMessage("Transaction complete");
                    gr.setStatus("Status: OK");
                    logger.trace("Transaction complete");

                    //make an entry in transaction table
                    addTransactionSummary(user1,user2,amount);
                    return gr;
                }
            }
        }
        catch (Exception e) {
            String str = e.getMessage();
            gr.setMessage("Exception Occurred " + str);
            return gr;
        }
    }

    @Transactional
    public void addTransactionSummary(String user1,String user2,Integer amount)
    {
        java.util.Date date= new java.util.Date();
        Timestamp timestamp=new Timestamp(date.getTime());

        logger.trace("Adding transaction entry to table");
        Transaction transaction=new Transaction();
        transaction.setFromUser(user1);
        transaction.setToUser(user2);
        transaction.setAmount(amount);
        transaction.setStatus("SUCCESS");
        transaction.setTime(timestamp);
        transactionRepository.save(transaction);
        logger.trace("Transaction entry added to table");

    }


    @Transactional
    public GeneralResponse getStatus(Integer txnId) {
        Optional<Transaction> temp=transactionRepository.findById(txnId);
        GeneralResponse gr=new GeneralResponse();
        if(temp.isPresent())
        {
            Transaction callTransaction=temp.get();
            String status=callTransaction.getStatus();
            gr.setMessage(status);
            gr.setStatus("STATUS: OK");
            logger.trace("Providing status of particular transaction "+ txnId );
            return gr;
        }
        else {
            gr.setMessage("NOT SUCCESSFUL");
            gr.setStatus("STATUS: FAILED");
            logger.trace(" Transaction has not occurred ");
            return gr;
        }
    }

    public Page<Transaction> getSummary(Integer userId) {

        Page<Transaction> transactionPages;
        Optional<User> temp = userRepository.findById(userId);
        if (temp.isPresent()) {
            User callUser = temp.get();
            String phoneNumber = callUser.getPhoneNumber();
                transactionPages=transactionRepository.findByFromUser(phoneNumber,PageRequest.of(1,3));
                return transactionPages;
        }
        else
            return null;
    }
}


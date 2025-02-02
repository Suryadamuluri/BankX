package com.bank.demo.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bank.demo.entities.Account;
import com.bank.demo.entities.Transaction;
import com.bank.demo.repository.AccountRepository;
import com.bank.demo.repository.TransactionRepository;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    TransactionRepository transactionRepository;
    
    // Register account with password encoding
    public Account registerAccount(String username, String password) {
        if (accountRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User Already exists");
        }
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(password)); // Encode the password
        account.setBalance(BigDecimal.ZERO);
        return accountRepository.save(account); // Save account to DB
    }

    // Load user by username
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = findAccountByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException(username);
        }
        // Return a UserDetails implementation (User) with the username, encoded password, and authorities
        return new User(account.getUsername(), account.getPassword(), authorities());
    }

    // Find account by username
    public Account findAccountByUsername(String username) {
        return accountRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Account Not Found"));
    }

    // Deposit amount
    public void deposit(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        Transaction transaction = new Transaction(amount, "Deposit", LocalDateTime.now(), account);
        transactionRepository.save(transaction);
    }

    // Withdraw amount
    public void withdraw(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient Balance Please try Later");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        Transaction transaction = new Transaction(amount, "Withdraw", LocalDateTime.now(), account);
      //  System.out.println(t;
        transactionRepository.save(transaction);
    }

    // Get transaction history
    public List<Transaction> getTransactionHistory(Account account) {
        return transactionRepository.findByAccountId(account.getId());
    }

    // Authorities method (can be extended to use roles)
    private Collection<? extends GrantedAuthority> authorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // Transfer amount
    public void transferAmount(Account fromAccount, String toUsername, BigDecimal amount) {
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient Funds");
        }
        System.out.println(toUsername);
        Account toAccount = accountRepository.findByUsername(toUsername)
                .orElseThrow(() -> new RuntimeException("Recipient Account Not Found"));
        
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        accountRepository.save(fromAccount);
        System.out.println(fromAccount.getUsername());
        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountRepository.save(toAccount);

        // Create debit transaction record
        Transaction debitTransaction = new Transaction(amount, "Transfer out to " + toAccount.getUsername(), LocalDateTime.now(), fromAccount);
        transactionRepository.save(debitTransaction);

        // Create credit transaction record
        Transaction creditTransaction = new Transaction(amount, "Transfer into " + fromAccount.getUsername(), LocalDateTime.now(), toAccount);
        transactionRepository.save(creditTransaction);
    }
}

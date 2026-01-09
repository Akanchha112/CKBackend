package com.example.cloudBalance.cloudBalance.service;

import com.example.cloudBalance.cloudBalance.DTO.AccountResponse;
import com.example.cloudBalance.cloudBalance.model.Account;
import com.example.cloudBalance.cloudBalance.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public List<AccountResponse> getAllAccounts() {

        return accountRepository.findAll()
                .stream()
                .map(a -> new AccountResponse(
                        a.getId(),
                        a.getAccountId(),
                        a.getAccountName(),
                        a.getArn()
                ))
                .toList();
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Account updateAccount(Long id, Account accountDetails) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));

        account.setAccountId(accountDetails.getAccountId());
        account.setArn(accountDetails.getArn());
        account.setAccountName(accountDetails.getAccountName());

        return accountRepository.save(account);
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }
}

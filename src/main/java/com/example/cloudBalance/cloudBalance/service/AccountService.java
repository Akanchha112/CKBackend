package com.example.cloudBalance.cloudBalance.service;

import com.example.cloudBalance.cloudBalance.DTO.AccountRequest;
import com.example.cloudBalance.cloudBalance.DTO.AccountResponse;
import com.example.cloudBalance.cloudBalance.entity.Account;
import com.example.cloudBalance.cloudBalance.entity.RoleType;
import com.example.cloudBalance.cloudBalance.entity.User;
import com.example.cloudBalance.cloudBalance.exception.ApiException;
import com.example.cloudBalance.cloudBalance.exception.ErrorCode;
import com.example.cloudBalance.cloudBalance.repository.AccountRepository;
import com.example.cloudBalance.cloudBalance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

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

    public AccountResponse  getAccountById(Long id) {
        Account account= accountRepository.findById(id)
                .orElseThrow(() -> new ApiException("Account not found with id: " + id,HttpStatus.NOT_FOUND,ErrorCode.NOT_FOUND));
        AccountResponse res=new AccountResponse(
                account.getId(),
                account.getAccountId(),
                account.getAccountName(),
                account.getArn());
        return res;


    }

    public AccountResponse createAccount(AccountRequest account) {
        if (accountRepository.findByAccountId(account.accountId()).isPresent()) {
            throw new ApiException(
                    "Account already exists",
                    HttpStatus.CONFLICT,
                    ErrorCode.ACCOUNT_ALREADY_EXISTS
            );
        }
        Account acc=Account.builder()
                .accountName(account.accountName())
                .accountId(account.accountId())
                .arn(account.arn())
                .build();

        Account newAcc= accountRepository.save(acc);
        return new AccountResponse(
                newAcc.getId(),
                newAcc.getAccountId(),
                newAcc.getAccountName(),
                newAcc.getArn()
        );
    }

    public Account updateAccount(Long id, Account accountDetails) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ApiException("Account not found with id: " + accountDetails.getAccountId(),HttpStatus.NOT_FOUND,ErrorCode.NOT_FOUND));

        account.setAccountId(accountDetails.getAccountId());
        account.setArn(accountDetails.getArn());
        account.setAccountName(accountDetails.getAccountName());

        return accountRepository.save(account);
    }

    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }

    public List<AccountResponse> getAccountsForUser(String email) {

        User user = userRepository.findByEmailId(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ADMIN and READONLY can see all accounts
        if (user.getRole() == RoleType.ADMIN || user.getRole() == RoleType.READONLY) {
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

        // CUSTOMER can only see assigned accounts
        return user.getAccounts()
                .stream()
                .map(a -> new AccountResponse(
                        a.getId(),
                        a.getAccountId(),
                        a.getAccountName(),
                        a.getArn()
                ))
                .toList();
    }



    public List<Account> validateAndFetchAccounts(List<Long> accountIds) {

        if (accountIds == null || accountIds.isEmpty()) {
            return List.of();
        }

        List<Account> fetchedAccounts =
                accountRepository.findByAccountIdIn(accountIds);

        if (fetchedAccounts.size() != accountIds.size()) {

            Set<Long> fetchedIds = fetchedAccounts.stream()
                    .map(Account::getAccountId)
                    .map(Long::valueOf)
                    .collect(Collectors.toSet());

            List<Long> missingAccounts = accountIds.stream()
                    .filter(id -> !fetchedIds.contains(id))
                    .toList();

            throw new ApiException(
                    "Account(s) not found: " + missingAccounts,
                    HttpStatus.NOT_FOUND,
                    ErrorCode.ACCOUNT_NOT_FOUND
            );
        }

        return fetchedAccounts;
    }

}

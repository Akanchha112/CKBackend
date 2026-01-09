package com.example.cloudBalance.cloudBalance.repository;

import com.example.cloudBalance.cloudBalance.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import java.lang.ScopedValue;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountId(String accountId);

    List<Account> findByAccountIdIn(List<Long> accountIds);

}


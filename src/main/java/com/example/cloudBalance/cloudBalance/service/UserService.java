package com.example.cloudBalance.cloudBalance.service;

import com.example.cloudBalance.cloudBalance.DTO.UserRequest;
import com.example.cloudBalance.cloudBalance.DTO.ApiResponse;
import com.example.cloudBalance.cloudBalance.DTO.UpdateUserRequest;
import com.example.cloudBalance.cloudBalance.DTO.UserResponse;
import com.example.cloudBalance.cloudBalance.exception.ApiException;
import com.example.cloudBalance.cloudBalance.exception.ErrorCode;
import com.example.cloudBalance.cloudBalance.entity.Account;
import com.example.cloudBalance.cloudBalance.entity.RoleType;
import com.example.cloudBalance.cloudBalance.entity.User;
import com.example.cloudBalance.cloudBalance.repository.AccountRepository;
import com.example.cloudBalance.cloudBalance.repository.UserRepository;
import com.example.cloudBalance.cloudBalance.utils.DTOtoEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    public final UserRepository userrepo;
    public final PasswordEncoder passwordEncoder;
    public final DTOtoEntityMapper dtOtoEntityMapper;
    public final AccountRepository accountRepository;
    public final AccountService accountService;

    public UserResponse addUser(UserRequest userRequest){
        if (userrepo.findByEmailId(userRequest.emailId()).isPresent()) {
            throw new ApiException(
                    "Email already exists",
                    HttpStatus.CONFLICT,
                    ErrorCode.USER_ALREADY_EXISTS
            );
        }

        RoleType role = userRequest.role();

        if (role == null) {
            throw new ApiException(
                    "Role is required",
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.INVALID_ROLE
            );
        }

        User user=dtOtoEntityMapper.mapToEntity(userRequest);

        //save account when ROLE is CUSTOMER
        if(role.equals(RoleType.CUSTOMER)){
            List<Account> fetchedaccount=accountService.validateAndFetchAccounts(userRequest.accountIds());
            user.setAccounts(fetchedaccount);
        }

        userrepo.save(user);
        return dtOtoEntityMapper.mapToResponse(user);
    }

    public List<UserResponse> getAllUser(){
        List<UserResponse> users = userrepo.findAll()
                .stream()
                .map(dtOtoEntityMapper::mapToResponse)
                .toList();

        return users;
    }

    public UserResponse editUser(Long id, UpdateUserRequest req) {

        User user = userrepo.findById(id)
                .orElseThrow(() -> new ApiException(
                        "User does not exist",
                        HttpStatus.NOT_FOUND,
                        ErrorCode.USER_NOT_FOUND
                ));

        dtOtoEntityMapper.updateEntity(user, req, passwordEncoder);

        RoleType newRole=req.role();

        //save account when ROLE is CUSTOMER
        if (newRole == RoleType.CUSTOMER) {

            List<Long> accountIds = req.accountIds();
            if (accountIds != null && !accountIds.isEmpty()) {
                List<Account> fetchedAccounts =accountService.validateAndFetchAccounts(accountIds);
                user.setAccounts(fetchedAccounts);
            } else {
                // CUSTOMER with no accounts clear explicitly
                user.getAccounts().clear();
            }
        } else {
            // ADMIN / READONLY must not have accounts
            user.getAccounts().clear();
        }

        userrepo.save(user);

        return dtOtoEntityMapper.mapToResponse(user);
    }

    public UserResponse findUserById(Long id) {
        User user = userrepo.findById(id)
                .orElseThrow(() -> new ApiException(
                        "User does not exist",
                        HttpStatus.NOT_FOUND,
                        ErrorCode.USER_NOT_FOUND
                ));
        return dtOtoEntityMapper.mapToResponse(user);
    }
}

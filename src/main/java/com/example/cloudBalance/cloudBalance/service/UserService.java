package com.example.cloudBalance.cloudBalance.service;

import com.example.cloudBalance.cloudBalance.DTO.UserRequest;
import com.example.cloudBalance.cloudBalance.DTO.ApiResponse;
import com.example.cloudBalance.cloudBalance.DTO.UpdateUserRequest;
import com.example.cloudBalance.cloudBalance.DTO.UserResponse;
import com.example.cloudBalance.cloudBalance.exception.ApiException;
import com.example.cloudBalance.cloudBalance.exception.ErrorCode;
import com.example.cloudBalance.cloudBalance.model.Account;
import com.example.cloudBalance.cloudBalance.model.RoleType;
import com.example.cloudBalance.cloudBalance.model.User;
import com.example.cloudBalance.cloudBalance.repository.AccountRepository;
import com.example.cloudBalance.cloudBalance.repository.UserRepository;
import com.example.cloudBalance.cloudBalance.utils.DTOtoEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    public final UserRepository userrepo;
    public final PasswordEncoder passwordEncoder;
    public final DTOtoEntityMapper dtOtoEntityMapper;
    public final AccountRepository accountRepository;

    public ApiResponse<?> addUser(UserRequest userRequest){
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
            List<Long> account=userRequest.accountIds();
            if (!account.isEmpty()) {
                List<Account> fetchedaccount = accountRepository.findByAccountIdIn(account);
                user.setAccounts(fetchedaccount);
            }
        }

        userrepo.save(user);
        return ApiResponse.success(
                "User created successfully",
                dtOtoEntityMapper.mapToResponse(user),
                HttpStatus.CREATED.value()
        );
    }

    public ApiResponse<?> getAllUser(){
        List<UserResponse> users = userrepo.findAll()
                .stream()
                .map(dtOtoEntityMapper::mapToResponse)
                .toList();

        return ApiResponse.success(
                "Users fetched successfully",
                users,
                200
        );
    }

    public ApiResponse<?> editUser(Long id, UpdateUserRequest req) {

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
                List<Account> fetchedAccounts =
                        accountRepository.findByAccountIdIn(accountIds);
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

        return ApiResponse.success(
                "User updated successfully",
                dtOtoEntityMapper.mapToResponse(user),
                HttpStatus.OK.value()
        );
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

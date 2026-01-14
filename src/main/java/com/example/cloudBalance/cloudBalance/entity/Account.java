package com.example.cloudBalance.cloudBalance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String accountId;

    @Column(nullable = false)
    private String arn;

    @Column(nullable = false)
    private String accountName;

    @ManyToMany(mappedBy = "accounts", fetch = FetchType.LAZY)
    private List<User> users= new ArrayList<>();
}


package com.example.cloudBalance.cloudBalance.service;

import com.example.cloudBalance.cloudBalance.entity.Account;
import com.example.cloudBalance.cloudBalance.exception.ApiException;
import com.example.cloudBalance.cloudBalance.exception.ErrorCode;
import com.example.cloudBalance.cloudBalance.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CostExplorerService {

    private final JdbcTemplate snowflakeJdbcTemplate;
    public final AccountRepository accountRepository;

    public CostExplorerService(@Qualifier("snowflakeJdbcTemplate") JdbcTemplate jdbcTemplate, AccountRepository accountRepository) {
        this.snowflakeJdbcTemplate = jdbcTemplate;
        this.accountRepository = accountRepository;
    }

    public List<Map<String, Object>> getCostData(
            String accountId,
            LocalDate startDate,
            LocalDate endDate,
            String groupBy,
            List<String> services,
            List<String> instanceTypes,
            List<String> usageTypes,
            List<String> platforms,
            List<String> regions,
            List<String> usageTypeGroups
    ) {
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ApiException("Account not found with id: " + accountId,HttpStatus.NOT_FOUND,ErrorCode.NOT_FOUND));
        System.out.println("groupby"+groupBy);
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        if (groupBy != null && !groupBy.isEmpty()) {
            // Grouped query
            sql.append("SELECT ");
            sql.append(groupBy.toUpperCase()).append(", ");
            sql.append("DATE_TRUNC('MONTH', BILL_DATE) as MONTH, ");
            sql.append("SUM(COST) as TOTAL_COST ");
            sql.append("FROM costreport ");
            sql.append("WHERE ACCOUNT_ID = ? ");
            params.add(accountId);

            sql.append("AND BILL_DATE BETWEEN ? AND ? ");
            params.add(java.sql.Date.valueOf(startDate));
            params.add(java.sql.Date.valueOf(endDate));

            addFilters(sql, params, services, instanceTypes, usageTypes, platforms, regions, usageTypeGroups);

            sql.append("GROUP BY ").append(groupBy.toUpperCase()).append(", MONTH ");
            sql.append("ORDER BY MONTH, TOTAL_COST DESC");
        } else {
            // Non-grouped query - monthly totals
            sql.append("SELECT DATE_TRUNC('MONTH', BILL_DATE) as MONTH, ");
            sql.append("SUM(COST) as TOTAL_COST ");
            sql.append("FROM costreport ");
            sql.append("WHERE ACCOUNT_ID = ? ");
            params.add(accountId);

            sql.append("AND BILL_DATE BETWEEN ? AND ? ");
            params.add(java.sql.Date.valueOf(startDate));
            params.add(java.sql.Date.valueOf(endDate));

            addFilters(sql, params, services, instanceTypes, usageTypes, platforms, regions, usageTypeGroups);

            sql.append("GROUP BY MONTH ");
            sql.append("ORDER BY MONTH");
        }

        return snowflakeJdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    private void addFilters(StringBuilder sql, List<Object> params,
                            List<String> services, List<String> instanceTypes,
                            List<String> usageTypes, List<String> platforms,
                            List<String> regions, List<String> usageTypeGroups) {

        if (services != null && !services.isEmpty()) {
            sql.append("AND SERVICE IN (");
            sql.append(String.join(",", services.stream().map(s -> "?").toList()));
            sql.append(") ");
            params.addAll(services);
        }

        if (instanceTypes != null && !instanceTypes.isEmpty()) {
            sql.append("AND INSTANCE_TYPE IN (");
            sql.append(String.join(",", instanceTypes.stream().map(s -> "?").toList()));
            sql.append(") ");
            params.addAll(instanceTypes);
        }

        if (usageTypes != null && !usageTypes.isEmpty()) {
            sql.append("AND USAGE_TYPE IN (");
            sql.append(String.join(",", usageTypes.stream().map(s -> "?").toList()));
            sql.append(") ");
            params.addAll(usageTypes);
        }

        if (platforms != null && !platforms.isEmpty()) {
            sql.append("AND PLATFORM IN (");
            sql.append(String.join(",", platforms.stream().map(s -> "?").toList()));
            sql.append(") ");
            params.addAll(platforms);
        }

        if (regions != null && !regions.isEmpty()) {
            sql.append("AND REGION IN (");
            sql.append(String.join(",", regions.stream().map(s -> "?").toList()));
            sql.append(") ");
            params.addAll(regions);
        }

        if (usageTypeGroups != null && !usageTypeGroups.isEmpty()) {
            sql.append("AND USAGE_TYPE_GROUP IN (");
            sql.append(String.join(",", usageTypeGroups.stream().map(s -> "?").toList()));
            sql.append(") ");
            params.addAll(usageTypeGroups);
        }
    }

    public List<String> getFilterValues(String filterType, String accountId) {
        Account account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ApiException("Account not found with id: " + accountId,HttpStatus.NOT_FOUND,ErrorCode.NOT_FOUND));

        String column = switch (filterType.toUpperCase()) {
            case "SERVICE" -> "SERVICE";
            case "INSTANCE_TYPE" -> "INSTANCE_TYPE";
            case "USAGE_TYPE" -> "USAGE_TYPE";
            case "PLATFORM" -> "PLATFORM";
            case "REGION" -> "REGION";
            case "USAGE_TYPE_GROUP" -> "USAGE_TYPE_GROUP";
            case "PURCHASE_OPTION" -> "PURCHASE_OPTION";
            case "API_OPERATION" -> "API_OPERATION";
            default -> throw new ApiException("Invalid filter type: " + filterType, HttpStatus.NOT_FOUND, ErrorCode.FILTER_NOT_FOUND);
        };

        String sql = String.format(
                "SELECT DISTINCT %s FROM costreport WHERE ACCOUNT_ID = ? AND %s IS NOT NULL ORDER BY %s",
                column, column, column
        );

        return snowflakeJdbcTemplate.queryForList(sql, String.class, accountId);
    }
}
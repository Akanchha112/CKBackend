package com.example.cloudBalance.cloudBalance.controller;

import com.example.cloudBalance.cloudBalance.DTO.ApiResponse;
import com.example.cloudBalance.cloudBalance.service.CostExplorerService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cost-explorer")
public class CostExplorerController {

    private CostExplorerService costExplorerService;

    public CostExplorerController(CostExplorerService costExplorerService) {
        this.costExplorerService = costExplorerService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'READONLY', 'CUSTOMER')")
    @GetMapping("/data")
    public ApiResponse<List<Map<String, Object>>> getCostData(
            @RequestParam String accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String groupBy,
            @RequestParam(required = false) List<String> services,
            @RequestParam(required = false) List<String> instanceTypes,
            @RequestParam(required = false) List<String> usageTypes,
            @RequestParam(required = false) List<String> platforms,
            @RequestParam(required = false) List<String> regions,
            @RequestParam(required = false) List<String> usageTypeGroups
    ) {
        List<Map<String, Object>> data = costExplorerService.getCostData(
                accountId, startDate, endDate, groupBy,
                services, instanceTypes, usageTypes, platforms, regions, usageTypeGroups
        );
        return ApiResponse.success("Cost data fetched successfully", data, 200);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'READONLY', 'CUSTOMER')")
    @GetMapping("/filters/{filterType}")
    public ApiResponse<List<String>> getFilterValues(
            @PathVariable String filterType,
            @RequestParam String accountId
    ) {
        List<String> values = costExplorerService.getFilterValues(filterType, accountId);
        return ApiResponse.success("Filter values fetched successfully", values, 200);
    }
}

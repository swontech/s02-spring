package com.swontech.s02.domain.spec.s022;

import org.springframework.http.ResponseEntity;

public interface S0221A0070Spec {
    ResponseEntity<?> selectCostReqList(String eventCode, Integer memberId, String fromDate, String toDate);
}

package com.swontech.s02.domain.logic.s022;

import com.swontech.s02.domain.dto.comm.CustomResponse;
import com.swontech.s02.domain.dto.s022.S0221A0060Dto;
import com.swontech.s02.domain.spec.s022.S0221A0060Spec;
import com.swontech.s02.domain.store.s022.S0221A0060Store;
import com.swontech.s02.domain.vo.s022.S0221A0060Vo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public class S0221A0060Logic implements S0221A0060Spec {
    private final S0221A0060Store s0221A0060Store;
    private final CustomResponse response;
    public S0221A0060Logic(S0221A0060Store s0221A0060Store, CustomResponse response) {
        this.s0221A0060Store = s0221A0060Store;
        this.response = response;
    }

    @Override
    public ResponseEntity<?> selectEventCost(Integer eventUseId) {
        return response.success(s0221A0060Store.selectEventCost(eventUseId));
    }

    @Override
    public ResponseEntity<?> updateEventCost(S0221A0060Dto.UpdateEventCostDto eventCostDto) {
        int result = s0221A0060Store.updateEventCost(S0221A0060Vo.UpdateEventCostVo
                .builder()
                        .eventId(eventCostDto.getEventId())
                        .eventUserId(eventCostDto.getEventUserId())
                        .usedDate(eventCostDto.getUsedDate())
                        .useAmount(eventCostDto.getUseAmount())
                        .useComment(eventCostDto.getUseComment())
                        .useReceiptId(eventCostDto.getUseReceiptId())
                        .useSubject(eventCostDto.getUseSubject())
                        .eventUseId(eventCostDto.getEventUseId())
                .build());
        if(result > 0) {
            return response.success("행사비용등록 수정에 성공했습니다.");
        }
        return response.fail("행사비용이 정상적으로 수정되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> insertEventCost(S0221A0060Dto.InsertEventCostDto eventCostDto) {
        String useProStatus = "";
        Integer payCurrentStep = null;
        Integer eventPayDept = 0;

        Map<String, Object> payInfo = s0221A0060Store.selectPayInfo(eventCostDto.getEventId());
        if(payInfo == null) {
            return response.fail("비용요청등록이 가능한 event가 아니거나 등록된 event가 없습니다.", HttpStatus.BAD_REQUEST);
        }


        eventPayDept = (int)payInfo.get("EVENT_PAY_DEPT");
        String payFlag = (String)payInfo.get("PAY_FLAG");
        if("N".equals(payFlag)) {
            useProStatus = "C";
            payCurrentStep = null;
        } else if("Y".equals(payFlag)) {
            useProStatus = "A";
            payCurrentStep = 1;
        }

        int result = s0221A0060Store.insertEventCost(S0221A0060Vo.InsertEventCostVo
                .builder()
                        .eventId(eventCostDto.getEventId())
                        .eventUserId(eventCostDto.getEventUserId())
                        .usedDate(eventCostDto.getUsedDate())
                        .useAmount(eventCostDto.getUseAmount())
                        .useComment(eventCostDto.getUseComment())
                        .useReceiptId(eventCostDto.getUseReceiptId())
                        .useReceiptName(eventCostDto.getUseReceiptName())
                        .useSubject(eventCostDto.getUseSubject())
                        .useProStatus(useProStatus)
                        .payStepCnt(eventPayDept)
                        .payCurrentStep(payCurrentStep)
                .build()
        );
        if(result > 0) {
            return response.success("행사 비용이 정상적으로 등록되었습니다.");
        }
        return response.fail("행시비용이 정상적으로 등록되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> deleteEventCost(List<Integer> eventUseIdList) {
        eventUseIdList.forEach(e -> s0221A0060Store.deleteEventCost(e));
        return response.success("행사비용이 정상적으로 삭제되었습니다.");

    }
}

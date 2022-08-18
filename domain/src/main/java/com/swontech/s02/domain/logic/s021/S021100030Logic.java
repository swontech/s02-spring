package com.swontech.s02.domain.logic.s021;

import com.swontech.s02.domain.dto.comm.CustomResponse;
import com.swontech.s02.domain.dto.s021.S021100030Dto;
import com.swontech.s02.domain.spec.s021.S021100030Spec;
import com.swontech.s02.domain.store.s021.S021100030Store;
import com.swontech.s02.domain.vo.s021.S021100030Vo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class S021100030Logic implements S021100030Spec {
    private final CustomResponse response;
    private final S021100030Store s021100030Store;

    public S021100030Logic(CustomResponse response, S021100030Store s021100030Store) {
        this.response = response;
        this.s021100030Store = s021100030Store;
    }

    @Override
    public ResponseEntity<?> selectMemberList(S021100030Dto.RetriveMemberList reqDto) {
        return response.success(s021100030Store.selectMemberList(
                S021100030Vo.SelectMemberListVo
                        .builder()
                            .orgId(reqDto.getOrgId())
                            .fromDt(reqDto.getFromDt())
                            .toDt(reqDto.getToDt())
                            .memberTp(reqDto.getMemberTp())
                            .memberName(reqDto.getMemberName())
                        .build()
                )
        );
    }

    @Override
    public ResponseEntity<?> deleteMember(S021100030Dto.DeleteMember reqDto) {
        int result = s021100030Store.deleteMember(reqDto.getMemberId());
        if(result > 0) {
            return response.success("회원정보를 정상적으로 삭제했습니다.");
        }
        return response.fail("회원 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<?> updateMember(S021100030Dto.UpdateMemberTp reqDto) {
        int result = s021100030Store.updateMemberTp(reqDto.getMemberId());
        if(result > 0) {
            return response.success("회원구분을 정상적으로 update했습니다.");
        }
        return response.fail("회원구분을 update하는데 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

package com.swontech.s02.domain.logic.s021;

import com.swontech.s02.domain.dto.comm.CustomResponse;
import com.swontech.s02.domain.dto.s021.S021100020Dto;
import com.swontech.s02.domain.spec.s021.S021100020Spec;
import com.swontech.s02.domain.store.s021.S021100020Store;
import com.swontech.s02.domain.vo.s021.S021100020Vo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
public class S021100020Logic implements S021100020Spec {
    private final S021100020Store s021100020Store;
    private final PasswordEncoder passwordEncoder;
    private final CustomResponse responseDto;
    public S021100020Logic(S021100020Store s021100020Store, PasswordEncoder passwordEncoder, CustomResponse responseDto) {
        this.s021100020Store = s021100020Store;
        this.passwordEncoder = passwordEncoder;
        this.responseDto = responseDto;
    }
    /**
     * 이메일 중복 체크
     * @param
     */
    @Override
    public ResponseEntity<?> duplicationCheckEmail(String email) {
        if(s021100020Store.selectMemberEmail(email) == null) {
            return responseDto.success(true, "사용 가능한 이메일 주소입니다.", HttpStatus.OK);
        }
        return responseDto.success(false, "이미 사용중인 이메일 주소입니다.", HttpStatus.OK);
    }

    /* kjy 2022.10.18 : for 단체명 중복체크*/
    @Override
    public ResponseEntity<?> duplicationCheckOrgName(String orgName) {
        if(s021100020Store.selectOrgName(orgName) == null) {
            return responseDto.success(true, "사용 가능한 단체명입니다.", HttpStatus.OK);
        }
        return responseDto.success(false, "이미 사용중인 단체명입니다.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> retrieveOrg(int orgId) {
        return responseDto.success(s021100020Store.selectOrg(orgId));
    }

    /**
     * 단체 등록 메소드
     * @param "S021100020Dto.RegisterOrg"
     */
    @Override
    public ResponseEntity<?> registerOrg(S021100020Dto.RegisterOrgReqDto reqDto) {
        try {
            /**
             * 단체 등록 메소드
             * 컨트롤러로부터 전달받은 request dto parameter를 InsertOrgVo에 담아 DML처리한다.
             * Mybatis의 selectKey를 이용해 발번된 orgId를 insertOrgVo의 String orgId에 자동으로 할당한다.
             */
            S021100020Vo.InsertOrgVo insertOrgVo = S021100020Vo.InsertOrgVo
                    .builder()
                    .orgName(reqDto.getOrgName())               // 단체이름
                    .zipCode(reqDto.getZipCode())               // 단체 우편번호
                    .address(reqDto.getAddress())               // 단체 주소
                    .detailAddress(reqDto.getDetailAddress())   // 단체 상세주소
                    .firstTelNo(reqDto.getFirstTelNo())         // 단체 연락처
                    .middleTelNo(reqDto.getMiddleTelNo())       // 단체 연락처
                    .lastTelNo(reqDto.getLastTelNo())           // 단체 연락처
                    .ceoName(reqDto.getCeoName())       /*2022.10.26 kjy 대표자명*/
                    .build();
            s021100020Store.insertOrg(insertOrgVo);

            int result = 0;
            /**
             *  insertOrgVo Mapper(S02100020Mapper.xml)에서 데이터 입력 후, 시퀀스를 통해 자동발번된 org_id를 리턴, insertOrgVo에 담아준다.
             *  컨트롤러로부터 전달받은 request dto 중 대표자정보와 insertOrgVo.orgId 및 전화번호를 초기 패스워드로 암호화 인코딩하여 InsertMemberVo에 담는다.
             */
            S021100020Vo.InsertMemberVo insertMemberVo = S021100020Vo.InsertMemberVo
                    .builder()
                    .orgId(insertOrgVo.getOrgId())          // 단체 id(insertOrg DML이후 VO에 담긴 orgId)
                    .memberName(reqDto.getMemberName())
                    .firstHpNo(reqDto.getFirstHpNo())
                    .middleHpNo(reqDto.getMiddleHpNo())
                    .lastHpNo(reqDto.getLastHpNo())
                    .email(reqDto.getEmail())
                    .pwd(passwordEncoder.encode(reqDto.getPwd()))   // 인코딩된 패스워드
                    .build();
            result = s021100020Store.insertMember(insertMemberVo);

            /* 2022.10.26 kjy
             * 단체 신규 등록시 발번된 memberId 로 org010.created_object_id 추가 update
             */
            if(result > 0) {
                int newMemberId = insertMemberVo.getMemberId();
                log.info("[S021100020] 단체신규등록된 memberId : " + newMemberId);

                result = 0;
                if( newMemberId > 0) {
                    result = s021100020Store.updateOrgAudit(S021100020Vo.UpdateOrgVo.builder()
                            .orgId(insertOrgVo.getOrgId())
                            .memberId(insertMemberVo.getMemberId()).build()
                        );
                    log.info("[S021100020] 단체신규등록 audit update 처리결과 : " + result);
                }
            }

            return responseDto.success(insertOrgVo.getOrgId(), "단체 신규등록에 성공했습니다.", HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }
    /**
     * 단체 상세정보 수정 메소드
     */
    @Override
    public ResponseEntity<?> patchOrgDetail(S021100020Dto.PatchOrgReqDto reqDto) {
        try {
            s021100020Store.updateOrg(S021100020Vo.UpdateOrgVo
                    .builder()
                    .orgId(reqDto.getOrgId())
                    .orgName(reqDto.getOrgName())
                    .zipCode(reqDto.getZipCode())
                    .address(reqDto.getAddress())
                    .detailAddress(reqDto.getDetailAddress())
                    .firstTelNo(reqDto.getFirstTelNo())
                    .middleTelNo(reqDto.getMiddleTelNo())
                    .lastTelNo(reqDto.getLastTelNo())
                    .firstHpNo(reqDto.getFirstHpNo())
                    .middleHpNo(reqDto.getMiddleHpNo())
                    .lastHpNo(reqDto.getLastHpNo())
                    .email(reqDto.getEmail())
                    .memberName(reqDto.getMemberName())
                    .firstHpNo(reqDto.getFirstHpNo())
                    .middleHpNo(reqDto.getMiddleHpNo())
                    .lastHpNo(reqDto.getLastHpNo())
                    .pwd(passwordEncoder.encode(reqDto.getPwd()))
                    .memberId(reqDto.getMemberId()) /*2022.10.26 kjy 로그인 memberId*/
                    .ceoName(reqDto.getCeoName())   /*2022.10.26 kjy 대표자명*/
                    .build());
            return responseDto.success("단체 상세 정보 수정에 성공했습니다.");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }
}

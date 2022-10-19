package com.swontech.s02.client.service.s022;

import com.swontech.s02.domain.dto.comm.CustomResponse;
import com.swontech.s02.domain.logic.s022.S022300010Logic;
import com.swontech.s02.domain.store.s022.S022300010Store;
import org.springframework.stereotype.Service;

@Service
public class S022300010Service extends S022300010Logic {

    public S022300010Service(CustomResponse response, S022300010Store s022300010Store) {
        super(response,s022300010Store);
    }
}

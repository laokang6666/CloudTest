package com.car.borrowservice.service;

import com.car.borrowservice.dto.BorrowResponse;
import com.car.borrowservice.dto.CreateBorrowRequest;

public interface BorrowApplicationService {

    BorrowResponse createBorrow(CreateBorrowRequest request);

    BorrowResponse getBorrow(Long id);
}

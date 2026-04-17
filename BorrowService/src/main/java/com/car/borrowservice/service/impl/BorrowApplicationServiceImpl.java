package com.car.borrowservice.service.impl;

import com.car.borrowservice.client.BookFeignClient;
import com.car.borrowservice.client.UserFeignClient;
import com.car.borrowservice.dto.BorrowResponse;
import com.car.borrowservice.dto.CreateBorrowRequest;
import com.car.borrowservice.entity.Borrow;
import com.car.borrowservice.enums.BorrowErrorCode;
import com.car.borrowservice.exception.BorrowDomainException;
import com.car.borrowservice.repository.BorrowRepository;
import com.car.borrowservice.service.BorrowApplicationService;
import com.car.common.api.R;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;

@Service
public class BorrowApplicationServiceImpl implements BorrowApplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BorrowApplicationServiceImpl.class);

    private final UserFeignClient userFeignClient;
    private final BookFeignClient bookFeignClient;
    private final BorrowRepository borrowRepository;

    public BorrowApplicationServiceImpl(
            UserFeignClient userFeignClient,
            BookFeignClient bookFeignClient,
            BorrowRepository borrowRepository) {
        this.userFeignClient = userFeignClient;
        this.bookFeignClient = bookFeignClient;
        this.borrowRepository = borrowRepository;
    }

    @Override
    @Transactional
    public BorrowResponse createBorrow(CreateBorrowRequest request) {
        LOGGER.info("borrow 断点时间：{}",new Date());
        Long userId = request.getUserId();
        Long bookId = request.getBookId();

        try {
            assertRemoteSuccess(userFeignClient.getUser(userId), BorrowErrorCode.REMOTE_USER_ERROR);
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new BorrowDomainException(
                        HttpStatus.NOT_FOUND,
                        BorrowErrorCode.USER_NOT_FOUND.getCode(),
                        BorrowErrorCode.USER_NOT_FOUND.getMessage());
            }
            throw new BorrowDomainException(
                    HttpStatus.BAD_GATEWAY,
                    BorrowErrorCode.REMOTE_USER_ERROR.getCode(),
                    BorrowErrorCode.REMOTE_USER_ERROR.getMessage() + ": " + e.getMessage());
        }

        try {
            assertRemoteSuccess(bookFeignClient.borrowOne(bookId, Collections.emptyMap()), BorrowErrorCode.REMOTE_BOOK_ERROR);
        } catch (FeignException e) {
            int status = e.status();
            if (status == 404) {
                throw new BorrowDomainException(
                        HttpStatus.NOT_FOUND,
                        BorrowErrorCode.BOOK_NOT_FOUND.getCode(),
                        BorrowErrorCode.BOOK_NOT_FOUND.getMessage());
            }
            if (status == 409) {
                throw new BorrowDomainException(
                        HttpStatus.CONFLICT,
                        BorrowErrorCode.STOCK_INSUFFICIENT.getCode(),
                        BorrowErrorCode.STOCK_INSUFFICIENT.getMessage());
            }
            throw new BorrowDomainException(
                    HttpStatus.BAD_GATEWAY,
                    BorrowErrorCode.REMOTE_BOOK_ERROR.getCode(),
                    BorrowErrorCode.REMOTE_BOOK_ERROR.getMessage() + ": " + e.getMessage());
        }

        Borrow borrow = new Borrow();
        borrow.setUserId(userId);
        borrow.setBookId(bookId);
        borrow.setQuantity(1);
        Borrow saved = borrowRepository.save(borrow);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowResponse getBorrow(Long id) {
        Borrow borrow = borrowRepository.findById(id)
                .orElseThrow(() -> new BorrowDomainException(
                        HttpStatus.NOT_FOUND,
                        BorrowErrorCode.BORROW_NOT_FOUND.getCode(),
                        BorrowErrorCode.BORROW_NOT_FOUND.getMessage()));
        return toResponse(borrow);
    }

    @Override
    @Transactional
    public BorrowResponse returnBorrow(Long borrowId) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new BorrowDomainException(
                        HttpStatus.NOT_FOUND,
                        BorrowErrorCode.BORROW_NOT_FOUND.getCode(),
                        BorrowErrorCode.BORROW_NOT_FOUND.getMessage()));
        if (borrow.getReturnedAt() != null) {
            throw new BorrowDomainException(
                    HttpStatus.CONFLICT,
                    BorrowErrorCode.BORROW_ALREADY_RETURNED.getCode(),
                    BorrowErrorCode.BORROW_ALREADY_RETURNED.getMessage());
        }

        try {
            assertRemoteSuccess(
                    bookFeignClient.returnOne(borrow.getBookId(), Collections.emptyMap()),
                    BorrowErrorCode.REMOTE_BOOK_ERROR);
        } catch (FeignException e) {
            int status = e.status();
            if (status == 404) {
                throw new BorrowDomainException(
                        HttpStatus.NOT_FOUND,
                        BorrowErrorCode.BOOK_NOT_FOUND.getCode(),
                        BorrowErrorCode.BOOK_NOT_FOUND.getMessage());
            }
            if (status == 409) {
                throw new BorrowDomainException(
                        HttpStatus.CONFLICT,
                        BorrowErrorCode.STOCK_AT_CAPACITY.getCode(),
                        BorrowErrorCode.STOCK_AT_CAPACITY.getMessage());
            }
            throw new BorrowDomainException(
                    HttpStatus.BAD_GATEWAY,
                    BorrowErrorCode.REMOTE_BOOK_ERROR.getCode(),
                    BorrowErrorCode.REMOTE_BOOK_ERROR.getMessage() + ": " + e.getMessage());
        }

        borrow.setReturnedAt(LocalDateTime.now());
        Borrow saved = borrowRepository.save(borrow);
        return toResponse(saved);
    }

    private static void assertRemoteSuccess(R r, BorrowErrorCode remoteFailureFallback) {
        if (r == null || !r.isSuccess()) {
            String msg = r != null && r.getMessage() != null ? r.getMessage() : remoteFailureFallback.getMessage();
            String code = r != null && r.getCode() != null ? r.getCode() : remoteFailureFallback.getCode();
            throw new BorrowDomainException(HttpStatus.BAD_GATEWAY, code, msg);
        }
    }

    private BorrowResponse toResponse(Borrow borrow) {
        return new BorrowResponse(
                borrow.getId(),
                borrow.getUserId(),
                borrow.getBookId(),
                borrow.getQuantity(),
                borrow.getCreatedAt(),
                borrow.getReturnedAt()
        );
    }
}

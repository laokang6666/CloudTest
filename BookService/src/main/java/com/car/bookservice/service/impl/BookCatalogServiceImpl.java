package com.car.bookservice.service.impl;

import com.car.bookservice.dto.BookResponse;
import com.car.bookservice.dto.CreateBookRequest;
import com.car.bookservice.entity.Book;
import com.car.bookservice.exception.DomainException;
import com.car.bookservice.repository.BookRepository;
import com.car.bookservice.service.BookCatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookCatalogServiceImpl implements BookCatalogService {

    private final BookRepository bookRepository;

    public BookCatalogServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        int total = request.getTotalStock();
        Book book = new Book();
        book.setTitle(request.getTitle().trim());
        book.setTotalStock(total);
        book.setAvailableStock(total);
        Book saved = bookRepository.save(book);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new DomainException(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND", "图书不存在"));
        return toResponse(book);
    }

    @Override
    @Transactional
    public BookResponse borrowOne(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new DomainException(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND", "图书不存在");
        }
        int updated = bookRepository.decreaseAvailableStock(bookId);
        if (updated == 0) {
            throw new DomainException(HttpStatus.CONFLICT, "STOCK_INSUFFICIENT", "库存不足");
        }
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new DomainException(HttpStatus.NOT_FOUND, "BOOK_NOT_FOUND", "图书不存在"));
        return toResponse(book);
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getTotalStock(),
                book.getAvailableStock(),
                book.getCreatedAt()
        );
    }
}

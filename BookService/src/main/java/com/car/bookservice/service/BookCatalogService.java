package com.car.bookservice.service;

import com.car.bookservice.dto.BookResponse;
import com.car.bookservice.dto.CreateBookRequest;

public interface BookCatalogService {

    BookResponse createBook(CreateBookRequest request);

    BookResponse getBook(Long id);

    BookResponse borrowOne(Long bookId);
}

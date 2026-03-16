package com.ptk.demo.service;

import com.ptk.demo.dto.request.BookRequest;
import com.ptk.demo.dto.response.BookResponse;

public interface BookService {

	BookResponse addBook(BookRequest request);

	BookResponse getBookByIsbn(String isbn);

	BookResponse deleteBookByIsbn(String testIsbn);

}

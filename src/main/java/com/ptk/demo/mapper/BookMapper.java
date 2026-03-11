package com.ptk.demo.mapper;

import org.springframework.stereotype.Component;


import com.ptk.demo.dto.request.BookRequest;
import com.ptk.demo.dto.response.BookResponse;
import com.ptk.demo.model.Book;

@Component 
public class BookMapper 
{
	// Convert BookRequest to Book entity
    public Book toEntity(BookRequest request)
    {
        Book book = new Book();
        book.setIsbn(request.getIsbn());
        book.setName(request.getName());
        book.setAuthor(request.getAuthor());
        return book;
    }

    // Convert Book entity to BookResponse
    public BookResponse toResponse(Book book)
    {
        return new BookResponse(
                book.getId(),
                book.getIsbn(),
                book.getName(),
                book.getAuthor()
        );
    }
}

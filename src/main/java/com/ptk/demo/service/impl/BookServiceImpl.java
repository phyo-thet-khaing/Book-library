package com.ptk.demo.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ptk.demo.dto.request.BookRequest;
import com.ptk.demo.dto.response.BookResponse;
import com.ptk.demo.mapper.BookMapper;
import com.ptk.demo.model.Book;
import com.ptk.demo.repository.BookRepository;
import com.ptk.demo.service.BookService;

@Service
public class BookServiceImpl implements BookService 
{
	@Autowired 
	private BookRepository bookRepository;
	
	@Autowired
	private BookMapper bookMapper;
	
	@Override
	public BookResponse addBook(BookRequest request) 
	{
		// TODO Auto-generated method stub
		// Map request DTO to entity
		Book book = bookMapper.toEntity(request);
		
		//Save Database
		Book savedBook = bookRepository.save(book);
		return bookMapper.toResponse(savedBook);
	}

	@Override
	public BookResponse getBookByIsbn(String isbn) 
	{
		// TODO Auto-generated method stub
		Optional<Book> optionalBook = bookRepository.findByIsbn(isbn);

        // Return mapped response or null if not found
        return optionalBook.map(bookMapper::toResponse).orElse(null);
	}

	@Override
	public BookResponse deleteBookByIsbn(String isbn) {
	    // Find the book by ISBN
	    Book book = bookRepository.findByIsbn(isbn)
	            .orElseThrow(() -> new RuntimeException("Book not found with ISBN: " + isbn));

	    // Delete the book
	    bookRepository.delete(book);

	    // Convert the deleted book to BookResponse using mapper
	    return bookMapper.toResponse(book);
	}

}

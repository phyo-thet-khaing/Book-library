package com.ptk.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ptk.demo.model.Book;
import com.ptk.demo.repository.BookRepository;



@Service
public class BookService
{
	@Autowired
	private BookRepository bookRepository;

	public Book addBook(Book book)
    {
        return bookRepository.save(book);
    }

	public Book getBookByIsbn(String isbn) 
	{
        return bookRepository.findByIsbn(isbn).orElse(null); // Convert Optional to Book or null
    }
}

package com.ptk.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ptk.demo.dto.request.BookRequest;
import com.ptk.demo.dto.response.BookResponse;
import com.ptk.demo.model.Book;
import com.ptk.demo.service.BookService;

@RestController
@RequestMapping("/books")
public class BookController 
{
	
	
	@Autowired
    private BookService bookService;

	@PostMapping
	public ResponseEntity<BookResponse> addBook(@RequestBody BookRequest request) 
	{
	    BookResponse response = bookService.addBook(request);
	    return ResponseEntity.ok(response);
	}

	@GetMapping("/{isbn}")
	public ResponseEntity<BookResponse> getBook(@PathVariable String isbn) 
	{
	    BookResponse response = bookService.getBookByIsbn(isbn);

	    if (response == null) {
	        return ResponseEntity.notFound().build();
	    }

	    return ResponseEntity.ok(response);
	}
}

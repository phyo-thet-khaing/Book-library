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

import com.ptk.demo.model.Book;
import com.ptk.demo.service.BookService;

@RestController
@RequestMapping("/books")
public class BookController 
{
	
	
	@Autowired
    private BookService bookService;

    @PostMapping
    public Book addBook(@RequestBody Book book) {
        return bookService.addBook(book);
    }

    @GetMapping("/{isbn}")
    public Book getBook(@PathVariable String isbn) 
    {
        return bookService.getBookByIsbn(isbn); // Returns null if not found
    }
}

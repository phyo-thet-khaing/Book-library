package com.ptk.demo.unit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import com.ptk.demo.controller.BookController;
import com.ptk.demo.dto.request.BookRequest;
import com.ptk.demo.dto.response.BookResponse;
import com.ptk.demo.service.BookService;

class BookControllerTest {

    @InjectMocks
    private BookController bookController;

    @Mock
    private BookService bookService;

    private BookRequest bookRequest;
    private BookResponse bookResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Sample request
        bookRequest = new BookRequest();
        bookRequest.setIsbn("12345");
        bookRequest.setName("Test Book");
        bookRequest.setAuthor("John Doe");

        // Sample response
        bookResponse = new BookResponse(1, "12345", "Test Book", "John Doe");
    }

    @Test
    void testAddBook() {
        when(bookService.addBook(any(BookRequest.class))).thenReturn(bookResponse);

        ResponseEntity<BookResponse> response = bookController.addBook(bookRequest);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("12345", response.getBody().getIsbn());

        verify(bookService, times(1)).addBook(any(BookRequest.class));
    }

    @Test
    void testGetBook_Found() {
        when(bookService.getBookByIsbn("12345")).thenReturn(bookResponse);

        ResponseEntity<BookResponse> response = bookController.getBook("12345");

        assertEquals(OK, response.getStatusCode());
        assertEquals("Test Book", response.getBody().getName());
        verify(bookService, times(1)).getBookByIsbn("12345");
    }

    @Test
    void testGetBook_NotFound() {
        when(bookService.getBookByIsbn("99999")).thenReturn(null);

        ResponseEntity<BookResponse> response = bookController.getBook("99999");

        assertEquals(NOT_FOUND, response.getStatusCode());
        verify(bookService, times(1)).getBookByIsbn("99999");
    }

    @Test
    void testDeleteBook_Found() {
        when(bookService.deleteBookByIsbn("12345")).thenReturn(bookResponse);

        ResponseEntity<BookResponse> response = bookController.deleteBook("12345");

        assertEquals(OK, response.getStatusCode());
        assertEquals("12345", response.getBody().getIsbn());
        verify(bookService, times(1)).deleteBookByIsbn("12345");
    }

    @Test
    void testDeleteBook_NotFound() {
        when(bookService.deleteBookByIsbn("99999")).thenThrow(new RuntimeException("Book not found"));

        ResponseEntity<BookResponse> response = bookController.deleteBook("99999");

        assertEquals(NOT_FOUND, response.getStatusCode());
        verify(bookService, times(1)).deleteBookByIsbn("99999");
    }
}
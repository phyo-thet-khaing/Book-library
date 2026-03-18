package com.ptk.demo.acceptance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ptk.demo.dto.request.BookRequest;
import com.ptk.demo.dto.response.BookResponse;
import com.ptk.demo.service.BookService;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LibrarySteps 
{

    @Autowired
    private BookService bookService;

    private BookRequest bookRequest;
    private BookResponse retrievedBook;
    
    // ✅ This runs before each scenario
    @Before
    public void cleanUp() 
    {
        // Example: remove a book with the test ISBN if it exists
        String testIsbn = "0395974682"; // or whatever ISBN you use in the scenario
        BookResponse existing = bookService.getBookByIsbn(testIsbn);
        if (existing != null) {
            bookService.deleteBookByIsbn(testIsbn); // you need to implement this method
        }
    }

    @Given("Book {string} by {string} with ISBN number {string}")
    public void createBook(String name, String author, String isbn) 
    {

        bookRequest = new BookRequest();
        bookRequest.setName(name);
        bookRequest.setAuthor(author);
        bookRequest.setIsbn(isbn);

    }

    @When("I store the book in library")
    public void storeBook()
    {

        bookService.addBook(bookRequest);

    }

    @Then("I am able to retrieve the book by the ISBN number")
    public void retrieveBook() 
    {

        retrievedBook = bookService.getBookByIsbn(bookRequest.getIsbn());

        assertNotNull(retrievedBook);
        assertEquals(bookRequest.getName(), retrievedBook.getName());
        assertEquals(bookRequest.getAuthor(), retrievedBook.getAuthor());
        assertEquals(bookRequest.getIsbn(), retrievedBook.getIsbn());

    }
}
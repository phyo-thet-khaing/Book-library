package com.ptk.demo.acceptance;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import com.ptk.demo.model.Book;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LibrarySteps
{
	
	
	 private Book book;
	    private Book retrievedBook;

	    // In-memory storage for BDD test
	    private static Map<String, Book> bookStore = new HashMap<>();

	    @Given("Book {string} by {string} with ISBN number {string}")
	    public void createBook(String name, String author, String isbn) {
	        book = new Book();
	        book.setName(name);
	        book.setAuthor(author);
	        book.setIsbn(isbn);
	    }

	    @When("I store the book in library")
	    public void storeBook() {
	        bookStore.put(book.getIsbn(), book);
	    }

	    @Then("I am able to retrieve the book by the ISBN number")
	    public void retrieveBook() {
	        retrievedBook = bookStore.get(book.getIsbn());

	        assertNotNull(retrievedBook);
	        assertEquals(book.getName(), retrievedBook.getName());
	        assertEquals(book.getAuthor(), retrievedBook.getAuthor());
	        assertEquals(book.getIsbn(), retrievedBook.getIsbn());
	    }
}

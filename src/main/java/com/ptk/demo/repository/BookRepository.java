package com.ptk.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ptk.demo.model.Book;

public interface BookRepository extends JpaRepository<Book, Integer>
{

	Optional<Book> findByIsbn(String isbn);

}

package com.ptk.demo.dto.request;

import lombok.*;

@Getter
@Setter
public class BookRequest 
{
	private String isbn;
    private String name;
    private String author;
}

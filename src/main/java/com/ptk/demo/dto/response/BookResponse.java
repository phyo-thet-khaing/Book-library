package com.ptk.demo.dto.response;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
public class BookResponse 
{
	private Integer id;
    private String isbn;
    private String name;
    private String author;
}

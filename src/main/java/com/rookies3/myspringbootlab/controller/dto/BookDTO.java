package com.rookies3.myspringbootlab.controller.dto;

import com.rookies3.myspringbootlab.entity.Book;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

public class BookDTO {

    @Getter
    @Setter
    public static class BookCreateRequest {
        @NotBlank(message = "Title cannot be blank")
        private String title;

        @NotBlank(message = "Author cannot be blank")
        private String author;

        @NotBlank(message = "ISBN cannot be blank")
        @Pattern(regexp = "^[0-9]{10,13}$", message = "ISBN must be 10-13 digits")
        private String isbn;

        @Positive(message = "Price must be positive")
        private Integer price;

        @PastOrPresent(message = "Publish date cannot be in the future")
        private LocalDate publishDate;

        public Book toEntity() {
            Book book = new Book();
            book.setTitle(this.title);
            book.setAuthor(this.author);
            book.setIsbn(this.isbn);
            book.setPrice(this.price);
            book.setPublishDate(this.publishDate);
            return book;
        }
    }

    @Getter
    @Setter
    public static class BookUpdateRequest {
        @NotBlank(message = "Title cannot be blank")
        private String title;

        @NotBlank(message = "Author cannot be blank")
        private String author;

        @Positive(message = "Price must be positive")
        private Integer price;

        @PastOrPresent(message = "Publish date cannot be in the future")
        private LocalDate publishDate;
    }

    @Getter
    @Setter
    public static class BookResponse {
        private Long id;
        private String title;
        private String author;
        private String isbn;
        private Integer price;
        private LocalDate publishDate;

        public static BookResponse from(Book book) {
            BookResponse response = new BookResponse();
            response.setId(book.getId());
            response.setTitle(book.getTitle());
            response.setAuthor(book.getAuthor());
            response.setIsbn(book.getIsbn());
            response.setPrice(book.getPrice());
            response.setPublishDate(book.getPublishDate());
            return response;
        }
    }
}
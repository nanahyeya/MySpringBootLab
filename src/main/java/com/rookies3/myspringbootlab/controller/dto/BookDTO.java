package com.rookies3.myspringbootlab.controller.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.rookies3.myspringbootlab.entity.Book;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

public class BookDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "제목은 필수 입력 값입니다.")
        private String title;

        @NotBlank(message = "작가는 필수 입력 값입니다.")
        private String author;

        @NotBlank(message = "ISBN은 필수 입력 값입니다.")
        private String isbn;

        @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
        private Integer price;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate publishDate;

        private BookDetailDTO.Request detail;
        private Long publisherId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String title;
        private String author;
        private String isbn;
        private Integer price;
        private LocalDate publishDate;
        private BookDetailDTO.Response detail;
        private PublisherDTO.SimpleResponse publisher;

        public static Response fromEntity(Book book) {
            BookDetailDTO.Response detailResponse = null;
            if (book.getBookDetail() != null) {
                detailResponse = BookDetailDTO.Response.builder()
                        .id(book.getBookDetail().getId())
                        .description(book.getBookDetail().getDescription())
                        .language(book.getBookDetail().getLanguage())
                        .pageCount(book.getBookDetail().getPageCount())
                        .publisher(book.getBookDetail().getPublisher())
                        .coverImageUrl(book.getBookDetail().getCoverImageUrl())
                        .edition(book.getBookDetail().getEdition())
                        .build();
            }

            PublisherDTO.SimpleResponse publisherResponse = null;
            if (book.getPublisher() != null) {
                publisherResponse = PublisherDTO.SimpleResponse.fromEntityWithCount(
                        book.getPublisher(),
                        book.getPublisher().getBooks() != null ? (long) book.getPublisher().getBooks().size() : 0L
                );
            }

            return Response.builder()
                    .id(book.getId())
                    .title(book.getTitle())
                    .author(book.getAuthor())
                    .isbn(book.getIsbn())
                    .price(book.getPrice())
                    .publishDate(book.getPublishDate())
                    .detail(detailResponse)
                    .publisher(publisherResponse)
                    .build();
        }
    }
}
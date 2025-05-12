package com.rookies3.myspringbootlab.controller;

import com.rookies3.myspringbootlab.controller.dto.BookDTO;
import com.rookies3.myspringbootlab.service.BookService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // 모든 도서 조회
    @GetMapping
    public ResponseEntity<List<BookDTO.BookResponse>> getAllBooks() {
        List<BookDTO.BookResponse> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    // ID로 도서 조회
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO.BookResponse> getBookById(@PathVariable Long id) {
        BookDTO.BookResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    // ISBN으로 도서 조회
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDTO.BookResponse> getBookByIsbn(@PathVariable String isbn) {
        BookDTO.BookResponse book = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(book);
    }

    // 저자명으로 도서 조회
    @GetMapping("/author/{author}")
    public ResponseEntity<List<BookDTO.BookResponse>> getBooksByAuthor(@PathVariable String author) {
        List<BookDTO.BookResponse> books = bookService.getBooksByAuthor(author);
        return ResponseEntity.ok(books);
    }

    // 도서 등록
    @PostMapping
    public ResponseEntity<BookDTO.BookResponse> createBook(@Valid @RequestBody BookDTO.BookCreateRequest request) {
        BookDTO.BookResponse createdBook = bookService.createBook(request);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    // 도서 정보 수정
    @PatchMapping("/{id}")
    public ResponseEntity<BookDTO.BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookDTO.BookUpdateRequest request) {
        BookDTO.BookResponse updatedBook = bookService.updateBook(id, request);
        return ResponseEntity.ok(updatedBook);
    }

    // 도서 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
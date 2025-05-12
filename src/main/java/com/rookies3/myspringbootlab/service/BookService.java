package com.rookies3.myspringbootlab.service;

import com.rookies3.myspringbootlab.controller.dto.BookDTO;
import com.rookies3.myspringbootlab.entity.Book;
import com.rookies3.myspringbootlab.exception.BusinessException;
import com.rookies3.myspringbootlab.repository.BookRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    public List<BookDTO.BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                //.map(book -> BookDTO.BookResponse.from(book))
                .map(BookDTO.BookResponse::from)
                .toList();  //Stream<BookResponse> => List<BookResponse>
    }

    public BookDTO.BookResponse getBookById(Long id) {
        Book book = findBookById(id);
        return BookDTO.BookResponse.from(book);
    }

    public BookDTO.BookResponse getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BusinessException("Book Not Found with ISBN: "
                        + isbn, HttpStatus.NOT_FOUND));
        return BookDTO.BookResponse.from(book);
    }

    public List<BookDTO.BookResponse> getBooksByAuthor(String author) {
        return bookRepository.findByAuthor(author).stream()
                .map(BookDTO.BookResponse::from)
                .toList();
    }

    @Transactional
    public BookDTO.BookResponse createBook(BookDTO.BookCreateRequest request) {
        // ISBN 중복 검사
        bookRepository.findByIsbn(request.getIsbn()) //Optional<Book>
                .ifPresent(book -> {
                    throw new BusinessException("Book with this ISBN already exists", HttpStatus.CONFLICT);
                });
        // BookCreateRequest => Entity 변환
        Book book = request.toEntity();
        // 등록 처리
        Book savedBook = bookRepository.save(book);
        // Book => BookResponse 변환
        return BookDTO.BookResponse.from(savedBook);
    }

    @Transactional
    public BookDTO.BookResponse updateBook(Long id, BookDTO.BookUpdateRequest request) {
        Book existingBook = findBookById(id);

        // 변경이 필요한 필드만 업데이트
        if (request.getPrice() != null) {
            existingBook.setPrice(request.getPrice());
        }

        // 확장성을 위한 추가 필드 업데이트
        if (request.getTitle() != null) {
            existingBook.setTitle(request.getTitle());
        }

        if (request.getAuthor() != null) {
            existingBook.setAuthor(request.getAuthor());
        }

        if (request.getPublishDate() != null) {
            existingBook.setPublishDate(request.getPublishDate());
        }

        Book updatedBook = bookRepository.save(existingBook);
        return BookDTO.BookResponse.from(updatedBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BusinessException("Book Not Found with ID: " + id, HttpStatus.NOT_FOUND);
        }
        bookRepository.deleteById(id);
    }

    // 내부 헬퍼 메서드
    private Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Book Not Found with ID: " + id, HttpStatus.NOT_FOUND));
    }
}
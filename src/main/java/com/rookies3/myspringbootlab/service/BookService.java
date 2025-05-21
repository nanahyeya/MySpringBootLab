package com.rookies3.myspringbootlab.service;

import com.rookies3.myspringbootlab.controller.dto.BookDTO;
import com.rookies3.myspringbootlab.controller.dto.PublisherDTO;
import com.rookies3.myspringbootlab.entity.Book;
import com.rookies3.myspringbootlab.entity.BookDetail;
import com.rookies3.myspringbootlab.entity.Publisher;
import com.rookies3.myspringbootlab.exception.BusinessException;
import com.rookies3.myspringbootlab.exception.ErrorCode;
import com.rookies3.myspringbootlab.repository.BookDetailRepository;
import com.rookies3.myspringbootlab.repository.BookRepository;

import com.rookies3.myspringbootlab.repository.PublisherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;
    private final BookDetailService bookDetailService;

    @Transactional(readOnly = true)
    public List<BookDTO.Response> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookDTO.Response getBookById(Long id) {
        Book book = bookRepository.findByIdWithAllDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("도서를 찾을 수 없습니다. ID: " + id));
        return convertToResponse(book);
    }

    @Transactional(readOnly = true)
    public BookDTO.Response getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbnWithBookDetail(isbn)
                .orElseThrow(() -> new EntityNotFoundException("도서를 찾을 수 없습니다. ISBN: " + isbn));
        return convertToResponse(book);
    }

    @Transactional(readOnly = true)
    public List<BookDTO.Response> getBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookDTO.Response> getBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookDTO.Response> getBooksByPublisherId(Long publisherId) {
        return bookRepository.findByPublisherId(publisherId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookDTO.Response createBook(BookDTO.Request request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateIsbnException("이미 존재하는 ISBN입니다: " + request.getIsbn());
        }

        Publisher publisher = null;
        if (request.getPublisherId() != null) {
            publisher = publisherRepository.findById(request.getPublisherId())
                    .orElseThrow(() -> new EntityNotFoundException("출판사를 찾을 수 없습니다. ID: " + request.getPublisherId()));
        }

        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .publishDate(request.getPublishDate())
                .price(request.getPrice())
                .publisher(publisher)
                .build();

        Book savedBook = bookRepository.save(book);

        // BookDetail 처리
        if (request.getBookDetail() != null) {
            BookDetailDTO.Request detailRequest = request.getBookDetail();
            BookDetailDTO.Response bookDetail = bookDetailService.createBookDetail(
                    savedBook.getId(), detailRequest);
            savedBook.setBookDetail(bookDetailService.getBookDetailEntity(bookDetail.getId()));
        }

        return convertToResponse(savedBook);
    }

    @Transactional
    public BookDTO.Response updateBook(Long id, BookDTO.Request request) {
        Book book = bookRepository.findByIdWithAllDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("도서를 찾을 수 없습니다. ID: " + id));

        if (!book.getIsbn().equals(request.getIsbn()) &&
                bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateIsbnException("이미 존재하는 ISBN입니다: " + request.getIsbn());
        }

        Publisher publisher = null;
        if (request.getPublisherId() != null) {
            publisher = publisherRepository.findById(request.getPublisherId())
                    .orElseThrow(() -> new EntityNotFoundException("출판사를 찾을 수 없습니다. ID: " + request.getPublisherId()));
        }

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPublishDate(request.getPublishDate());
        book.setPrice(request.getPrice());
        book.setPublisher(publisher);

        // BookDetail 처리
        if (request.getBookDetail() != null) {
            if (book.getBookDetail() != null) {
                bookDetailService.updateBookDetail(book.getBookDetail().getId(), request.getBookDetail());
            } else {
                BookDetailDTO.Response bookDetail = bookDetailService.createBookDetail(
                        book.getId(), request.getBookDetail());
                book.setBookDetail(bookDetailService.getBookDetailEntity(bookDetail.getId()));
            }
        }

        Book updatedBook = bookRepository.save(book);
        return convertToResponse(updatedBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("도서를 찾을 수 없습니다. ID: " + id));
        bookRepository.delete(book);
    }

    private BookDTO.Response convertToResponse(Book book) {
        PublisherDTO.Response publisherResponse = null;
        if (book.getPublisher() != null) {
            publisherResponse = PublisherDTO.Response.builder()
                    .id(book.getPublisher().getId())
                    .name(book.getPublisher().getName())
                    .establishedDate(book.getPublisher().getEstablishedDate())
                    .address(book.getPublisher().getAddress())
                    .build();
        }

        BookDetailDTO.Response bookDetailResponse = null;
        if (book.getBookDetail() != null) {
            bookDetailResponse = BookDetailDTO.Response.builder()
                    .id(book.getBookDetail().getId())
                    .description(book.getBookDetail().getDescription())
                    .pageCount(book.getBookDetail().getPageCount())
                    .language(book.getBookDetail().getLanguage())
                    .build();
        }

        return BookDTO.Response.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .publishDate(book.getPublishDate())
                .price(book.getPrice())
                .publisher(publisherResponse)
                .bookDetail(bookDetailResponse)
                .build();
    }
}
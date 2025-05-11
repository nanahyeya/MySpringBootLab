package com.rookies3.myspringbootlab.repository;

import com.rookies3.myspringbootlab.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    // ISBN으로 책 존재 여부 확인
    boolean existsByIsbn(String isbn);

    // ISBN으로 책 조회
    Optional<Book> findByIsbn(String isbn);

    // 저자명으로 책 목록 조회 (기존 메서드)
    List<Book> findByAuthor(String author);
}
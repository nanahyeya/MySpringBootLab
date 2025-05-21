package com.rookies3.myspringbootlab.service;

import com.rookies3.myspringbootlab.controller.dto.BookDTO;
import com.rookies3.myspringbootlab.controller.dto.PublisherDTO;
import com.rookies3.myspringbootlab.controller.dto.PublisherDTO.*;
import com.rookies3.myspringbootlab.entity.Publisher;
import com.rookies3.myspringbootlab.repository.BookRepository;
import com.rookies3.myspringbootlab.repository.PublisherRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublisherService {

    private final PublisherRepository publisherRepository;
    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public List<PublisherDTO.Response> getAllPublishers() {
        return publisherRepository.findAll().stream()
                .map(publisher -> {
                    Long bookCount = bookRepository.countByPublisherId(publisher.getId());
                    return convertToResponse(publisher, bookCount);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PublisherDTO.DetailResponse getPublisherById(Long id) {
        Publisher publisher = publisherRepository.findByIdWithBooks(id)
                .orElseThrow(() -> new EntityNotFoundException("출판사를 찾을 수 없습니다. ID: " + id));

        return convertToDetailResponse(publisher);
    }

    @Transactional(readOnly = true)
    public PublisherDTO.Response getPublisherByName(String name) {
        Publisher publisher = publisherRepository.findByName(name);
        if (publisher == null) {
            throw new EntityNotFoundException("출판사를 찾을 수 없습니다. Name: " + name);
        }

        Long bookCount = bookRepository.countByPublisherId(publisher.getId());
        return convertToResponse(publisher, bookCount);
    }

    @Transactional
    public PublisherDTO.Response createPublisher(PublisherDTO.Request request) {
        if (publisherRepository.existsByName(request.getName())) {
            throw new DuplicateNameException("이미 존재하는 출판사 이름입니다: " + request.getName());
        }

        Publisher publisher = Publisher.builder()
                .name(request.getName())
                .establishedDate(request.getEstablishedDate())
                .address(request.getAddress())
                .build();

        Publisher savedPublisher = publisherRepository.save(publisher);
        return convertToResponse(savedPublisher, 0L);
    }

    @Transactional
    public PublisherDTO.Response updatePublisher(Long id, PublisherDTO.Request request) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("출판사를 찾을 수 없습니다. ID: " + id));

        if (!publisher.getName().equals(request.getName()) &&
                publisherRepository.existsByName(request.getName())) {
            throw new DuplicateNameException("이미 존재하는 출판사 이름입니다: " + request.getName());
        }

        publisher.setName(request.getName());
        publisher.setEstablishedDate(request.getEstablishedDate());
        publisher.setAddress(request.getAddress());

        Publisher updatedPublisher = publisherRepository.save(publisher);
        Long bookCount = bookRepository.countByPublisherId(updatedPublisher.getId());
        return convertToResponse(updatedPublisher, bookCount);
    }

    @Transactional
    public void deletePublisher(Long id) {
        Publisher publisher = publisherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("출판사를 찾을 수 없습니다. ID: " + id));

        if (bookRepository.countByPublisherId(publisher.getId()) > 0) {
            throw new PublisherHasBooksException("출판사에 등록된 도서가 있어 삭제할 수 없습니다.");
        }

        publisherRepository.delete(publisher);
    }

    private PublisherDTO.Response convertToResponse(Publisher publisher, Long bookCount) {
        return PublisherDTO.Response.builder()
                .id(publisher.getId())
                .name(publisher.getName())
                .establishedDate(publisher.getEstablishedDate())
                .address(publisher.getAddress())
                .bookCount(bookCount)
                .build();
    }

    private PublisherDTO.DetailResponse convertToDetailResponse(Publisher publisher) {
        List<BookDTO.Response> books = publisher.getBooks().stream()
                .map(book -> BookDTO.Response.builder()
                        .id(book.getId())
                        .title(book.getTitle())
                        .author(book.getAuthor())
                        .isbn(book.getIsbn())
                        .publishDate(book.getPublishDate())
                        .price(book.getPrice())
                        .bookDetail(null) // 간략한 정보만 제공
                        .build())
                .collect(Collectors.toList());

        return PublisherDTO.DetailResponse.builder()
                .id(publisher.getId())
                .name(publisher.getName())
                .establishedDate(publisher.getEstablishedDate())
                .address(publisher.getAddress())
                .books(books)
                .build();
    }
    }
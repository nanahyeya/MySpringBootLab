package com.rookies3.myspringbootlab.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rookies3.myspringbootlab.entity.Publisher;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
public class PublisherDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SimpleResponse {
        private Long id;
        private String name;
        private Long bookCount;

        public static SimpleResponse fromEntityWithCount(Publisher publisher, Long count) {
            return SimpleResponse.builder()
                    .id(publisher.getId())
                    .name(publisher.getName())
                    .bookCount(count)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private LocalDate establishedDate;
        private String address;
        private List<BookDTO.Response> books;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private String name;
        private LocalDate establishedDate;
        private String address;
    }
}
package com.polarbookshop.catalog_service.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter // setter 연습용
@AllArgsConstructor
@NoArgsConstructor
public class Book {
        @Id @GeneratedValue(strategy = IDENTITY)
        private Long id;

        @NotBlank(message = "The book ISBN must be defined.")
        @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "The ISBN format must be valid.")
        private String isbn;

        @NotBlank(message = "The book title must be defined.")
        private String title;

        @NotBlank(message = "The book author must be defined.")
        private String author;

        @NotNull(message = "The book price must be defined.")
        @Positive(message = "The book price must be greater than zero.") // 필드는 null이 아니고 0보다 큰값이여야한다.
        private Double price;

        private String publisher;

        @CreatedDate
        private LocalDateTime createdDate;

        @LastModifiedDate
        private LocalDateTime lastModifiedDate;

        @Version
        private int version;

        public static Book of(String isbn, String title, String author, Double price, String publisher) {
                return new Book(
                        null,
                        isbn,
                        title,
                        author,
                        price,
                        publisher,
                        null,
                        null,
                        0
                );
        }

        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Book book = (Book) o;
                return version == book.version &&
                        id.equals(book.id) &&
                        isbn.equals(book.isbn) &&
                        title.equals(book.title) &&
                        author.equals(book.author) &&
                        price.equals(book.price) &&
                        Objects.equals(publisher, book.publisher) &&
                        createdDate.equals(book.createdDate) &&
                        lastModifiedDate.equals(book.lastModifiedDate);
        }

        @Override
        public int hashCode() {
                return Objects.hash(id, isbn, title, author, price, publisher, createdDate, lastModifiedDate, version);
        }
}



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
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
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
        @Column(updatable = false)
        private LocalDateTime createdDate;

        @LastModifiedDate
        private LocalDateTime lastModifiedDate;

        @CreatedBy
        @Column(name = "created_by", updatable = false)
        private String createdBy;

        @LastModifiedBy
        @Column(name = "last_modified_by")
        private String lastModifiedBy;

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

                if (id != null && book.id != null) {
                        return id.equals(book.id);
                }

                return Objects.equals(isbn, book.isbn);
        }

        @Override
        public int hashCode() {
                return id != null ? Objects.hash(id) : Objects.hash(isbn);
        }

        public void updateDetail(Book updatedBook) {
                this.title = updatedBook.getTitle();
                this.author = updatedBook.getAuthor();
                this.price = updatedBook.getPrice();
                this.publisher = updatedBook.getPublisher();
                this.lastModifiedDate = updatedBook.getLastModifiedDate();
                this.lastModifiedBy = updatedBook.getLastModifiedBy();
                this.version = updatedBook.getVersion();
        }
}



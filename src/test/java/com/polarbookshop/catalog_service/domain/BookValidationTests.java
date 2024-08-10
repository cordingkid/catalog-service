package com.polarbookshop.catalog_service.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 테스트 코드 실행
 * ./gradlew test --tests 클래스이름
 */
public class BookValidationTests {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsCorrectThenValidationSucceeds() throws Exception{
        var book = new Book("1234567890", "Title", "Author", 9.90);

        Set<ConstraintViolation<Book>> validations = validator.validate(book);
        assertThat(validations).isEmpty();
    }


    @Test
    void whenIsbnNotDefinedThenValidationFails() {
        var book = new Book("", "Title", "Author", 9.90);

        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(2);

        List<String> constraintViolationMessages = violations
                .stream()
                .map(ConstraintViolation::getMessage).collect(Collectors.toList());

        assertThat(constraintViolationMessages)
                .contains("The book ISBN must be defined.")
                .contains("The ISBN format must be valid.");
    }
}

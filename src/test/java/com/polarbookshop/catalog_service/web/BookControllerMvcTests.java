package com.polarbookshop.catalog_service.web;

import com.polarbookshop.catalog_service.domain.BookNotFoundException;
import com.polarbookshop.catalog_service.domain.BookService;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(BookController.class)
public class BookControllerMvcTests {

    // 모의 환경에서 웹 계층을 테스트하기 위한 유틸리티 클래스
    // 톰캣 같은 서버를 로드하지 않고도 웹 엔드포인트 테스트가능
    @Autowired
    private MockMvc mockMvc;

    // 스프링 애플리케이션 콘텍스트에 모의 객체 추가
    @MockBean
    private BookService bookService;

    @Test
    void whenGetBookBotExistingThenShouldReturn404() throws Exception {
        String isbn = "73737313940";

        BDDMockito.given(bookService.viewBookDetails(isbn))
                .willThrow(BookNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/books/" + isbn))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}

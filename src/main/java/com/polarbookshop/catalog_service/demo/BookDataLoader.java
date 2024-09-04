package com.polarbookshop.catalog_service.demo;

import com.polarbookshop.catalog_service.domain.Book;
import com.polarbookshop.catalog_service.domain.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("testdata") // 이 클래스는 testdata 프로파일 활성화일때만 로드됨
@RequiredArgsConstructor
public class BookDataLoader {

    private final BookRepository bookRepository;

    /*
        @EventListener(ApplicationReadyEvent.class)
         - ApplicationReadyEvent가 발생하면
         - 해당 이벤트가 애플리케이션 시작 단계가 완료되면 발생한다.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void loadBookTestData() {
        bookRepository.deleteAll();
        var book1 = Book.of("1234567891", "Northern Lights", "Lyra Silvertongue", 9.90, "Polarsophia");
        var book2 = Book.of("1234567892", "Polar Journey", "Iorek Polarson", 12.90, "Polarsophia");
        bookRepository.saveAll(List.of(book1, book2));
    }
}

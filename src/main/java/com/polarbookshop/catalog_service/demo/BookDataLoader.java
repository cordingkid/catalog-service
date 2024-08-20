package com.polarbookshop.catalog_service.demo;

import com.polarbookshop.catalog_service.domain.Book;
import com.polarbookshop.catalog_service.domain.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

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
        var book1 = new Book("1234567891", "Northern Lights", "Lyra Silverstar", 9.90);
        var book2 = new Book("1234567892", "Polar Journey", "Iorek Polarson", 12.90);
        bookRepository.save(book1);
        bookRepository.save(book2);
    }
}

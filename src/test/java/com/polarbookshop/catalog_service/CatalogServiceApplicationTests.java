package com.polarbookshop.catalog_service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.polarbookshop.catalog_service.domain.Book;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
		// 완전한 스프링 웹 애플리케이션 콘텍스트와 임의의 포트를 듣는 서블릿 컨테이너를 로드
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("integration")
@Testcontainers
class CatalogServiceApplicationTests {

	// Customer
	private static KeycloakToken bjornTokens;
	// Customer and employee
	private static KeycloakToken isabelleTokens;

	// 테스트를 위해 REST 엔드포인트를 호출할 유틸리티
	@Autowired
	private WebTestClient webTestClient;

	// 키클록 컨테이너 정의
	@Container
	private static final KeycloakContainer ketCloakContainer =
			new KeycloakContainer("quay.io/keycloak/keycloak:24.0")
					.withRealmImportFile("/test-realm-config.json");

	// 키클록 발행자 URI가 테스트 키클록 인스턴스를 가리키도록 변경
	@DynamicPropertySource
	static void dynamicProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
				() -> ketCloakContainer.getAuthServerUrl() + "/realms/PolarBookshop");
	}

	@BeforeAll
	static void generateAccessTokens() {
		WebClient webClient = WebClient.builder()	// 키클록 호출을 위한 WebClient
				.baseUrl(ketCloakContainer.getAuthServerUrl() + "/realms/PolarBookshop/protocol/openid-connect/token")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.build();

		isabelleTokens = authenticateWith("isabelle", "password", webClient);
		bjornTokens = authenticateWith("bjorn", "password", webClient);
	}

	@Test
	void whenGetRequestWithIdThenBookReturned() {
		var bookIsbn = "1231231230";
		var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, "Polarsophia");
		Book expectedBook = webTestClient.post()
				.uri("/books")
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Book.class).value(book -> assertThat(book).isNotNull())
				.returnResult().getResponseBody();

		webTestClient
				.get()
				.uri("/books/" + bookIsbn)
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(Book.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.getIsbn()).isEqualTo(expectedBook.getIsbn());
				});
	}

	@Test
	void whenPostRequestThenBookCreated() {
		var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Polarsophia");

		webTestClient.post()
				.uri("/books")
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken))
				.bodyValue(expectedBook) // 요청 본문에 Book 객체 추가
				.exchange() //요청 전송
				.expectStatus().isCreated() // HTTP 응답코드 201 상태 확인
				.expectBody(Book.class)
				.value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.getIsbn()).isEqualTo(expectedBook.getIsbn());
				});
	}

	@Test
	void whenPostRequestUnauthenticatedThen401() {
		var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Polarsophia");

		webTestClient.post()
				.uri("/books")
				.bodyValue(expectedBook)
				.exchange()
				.expectStatus().isUnauthorized();
	}

	@Test
	void whenPostRequestUnauthorizedThen403() {
		var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Polarsophia");

		webTestClient.post()
				.uri("/books")
				.headers(headers -> headers.setBearerAuth(bjornTokens.accessToken()))
				.bodyValue(expectedBook)
				.exchange()
				.expectStatus().isForbidden();
	}


	@Test
	void whenPutRequestThenBookUpdated() {
		var bookIsbn = "1231231232";
		var bookToCreate = Book.of(bookIsbn, "Title", "Author", 7.95, "Polarsophia");
		Book createdBook = webTestClient.post()
				.uri("/books")
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Book.class).value(book -> assertThat(book).isNotNull())
				.returnResult().getResponseBody();
		var bookToUpdate = new Book(
				createdBook.getId(), createdBook.getIsbn(),
				createdBook.getTitle(), createdBook.getAuthor(),
				7.95, createdBook.getPublisher(),
				createdBook.getCreatedDate(), createdBook.getLastModifiedDate(),
				createdBook.getVersion()
		);

		webTestClient
				.put()
				.uri("/books/" + bookIsbn)
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
				.bodyValue(bookToUpdate)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Book.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.getPrice()).isEqualTo(createdBook.getPrice());
				});
	}

	@Test
	void whenDeleteRequestThenBookDeleted() {
		var bookIsbn = "1231231233";
		var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, "Polarsophia");
		webTestClient.post()
				.uri("/books")
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated();

		webTestClient.delete()
				.uri("/books/" + bookIsbn)
				.headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
				.exchange()
				.expectStatus().isNoContent();

		webTestClient
				.get()
				.uri("/books/" + bookIsbn)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody(String.class).value(errorMessage ->
						assertThat(errorMessage).isEqualTo("The book with ISBN " + bookIsbn + " was not found.")
				);
	}

	private static KeycloakToken authenticateWith(String username, String password, WebClient webClient) {
		return webClient.post()
				.body(BodyInserters.fromFormData("grant_type", "password")
						.with("client_id", "polar-test")
						.with("username", username)
						.with("password", password)
				)
				.retrieve()
				.bodyToMono(KeycloakToken.class)
				.block();
	}

	private record KeycloakToken(String accessToken) {
		// KeycloakToken 객체로 역직렬화
		@JsonCreator
		private KeycloakToken(@JsonProperty("access_token") final String accessToken) {
			this.accessToken = accessToken;
		}
	}

}

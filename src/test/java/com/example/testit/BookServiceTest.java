package com.example.testit;

import com.example.testit.adapter.mail.MailService;
import com.example.testit.model.Book;
import com.example.testit.model.Member;
import com.example.testit.model.Status;
import com.example.testit.repository.BookRepository;
import com.example.testit.repository.MemberRepository;
import com.example.testit.service.BookService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

public class BookServiceTest {

	private static final Long BOOK_ID = 1L;
	private static final Long MEMBER_ID = 2L;

	private BookService bookService;
	private BookRepository bookRepositoryMock;
	private MemberRepository memberRepositoryMock;
	private MailService mailServiceMock;

	@BeforeEach
	void setUp() {
		this.bookRepositoryMock = Mockito.mock(BookRepository.class);
		this.memberRepositoryMock = Mockito.mock(MemberRepository.class);
		this.mailServiceMock = Mockito.mock(MailService.class);
		this.bookService = new BookService(bookRepositoryMock, memberRepositoryMock, mailServiceMock);
	}

	// Tests de la première méthode (borrowBook)

	@Test
	public void testBorrowBookNotExist() {
		Mockito.when(bookRepositoryMock.findById(BOOK_ID)).thenReturn(Optional.empty());

		Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.borrowBook(BOOK_ID, MEMBER_ID));

		Mockito.verify(bookRepositoryMock, Mockito.never()).save(Mockito.any());
	}

	@Test
	public void testBorrowBookExistUserNotExist() {
		Book book = new Book();
		book.setStatus(Status.DISPONIBLE);
		Mockito.when(bookRepositoryMock.findById(BOOK_ID)).thenReturn(Optional.of(book));
		Mockito.when(memberRepositoryMock.findById(MEMBER_ID)).thenReturn(Optional.empty());

		Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.borrowBook(BOOK_ID, MEMBER_ID));

		Mockito.verify(bookRepositoryMock, Mockito.never()).save(Mockito.any());
	}

	@Test
	public void testBorrowBookNotAvailable() {
		Book book = new Book();
		book.setStatus(Status.EMPRUNTE);
		Mockito.when(bookRepositoryMock.findById(BOOK_ID)).thenReturn(Optional.of(book));
		Mockito.when(memberRepositoryMock.findById(MEMBER_ID)).thenReturn(Optional.of(new Member()));

		Assertions.assertThrows(IllegalStateException.class, () -> bookService.borrowBook(BOOK_ID, MEMBER_ID));

		Mockito.verify(bookRepositoryMock, Mockito.never()).save(Mockito.any());
	}

	@Test
	public void testBorrowBookUserHasOngoingLoans() {
		Book book = new Book();
		book.setStatus(Status.DISPONIBLE);
		Book otherBook = new Book();
		otherBook.setStatus(Status.EMPRUNTE);

		Mockito.when(bookRepositoryMock.findById(BOOK_ID)).thenReturn(Optional.of(book));
		Mockito.when(memberRepositoryMock.findById(MEMBER_ID)).thenReturn(Optional.of(new Member()));
		Mockito.when(bookRepositoryMock.findByMemberAndStatus(MEMBER_ID, Status.EMPRUNTE))
				.thenReturn(List.of(otherBook));

		Assertions.assertThrows(IllegalStateException.class, () -> bookService.borrowBook(BOOK_ID, MEMBER_ID));

		Assertions.assertEquals(Status.DISPONIBLE, book.getStatus());
		Mockito.verify(bookRepositoryMock, Mockito.never()).save(Mockito.any());
	}

	@Test
	public void testBorrowBook() {
		Member member = new Member();
		Book book = new Book();
		book.setStatus(Status.DISPONIBLE);

		Mockito.when(bookRepositoryMock.findById(BOOK_ID)).thenReturn(Optional.of(book));
		Mockito.when(memberRepositoryMock.findById(MEMBER_ID)).thenReturn(Optional.of(member));
		Mockito.when(bookRepositoryMock.findByMemberAndStatus(MEMBER_ID, Status.EMPRUNTE))
				.thenReturn(List.of());
		Mockito.when(bookRepositoryMock.save(book)).thenReturn(book);

		Book result = bookService.borrowBook(BOOK_ID, MEMBER_ID);

		Assertions.assertEquals(Status.EMPRUNTE, result.getStatus());
		Assertions.assertEquals(member, result.getBorrower());
		Mockito.verify(bookRepositoryMock).save(book);
	}
}

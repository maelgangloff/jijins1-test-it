package com.example.testit;

import com.example.testit.adapter.mail.MailService;
import com.example.testit.model.Book;
import com.example.testit.model.Member;
import com.example.testit.model.Status;
import com.example.testit.repository.BookRepository;
import com.example.testit.repository.MemberRepository;
import com.example.testit.service.BookService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DataJpaTest
@Import(BookService.class)
class BookServiceIT {

	@Autowired
	private BookService bookService;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private MemberRepository memberRepository;

	@MockitoBean
	private MailService mailService;

	@Test
	public void borrowAndReturnTheBook() {
		// Création d'un book dans la db
		Book book = new Book();
		book.setStatus(Status.DISPONIBLE);
		book.setTitle("Mon aventure en Junior-Entreprise");
		book.setAuthor("Maël Gangloff");
		Book savedBook = bookRepository.save(book);

		// Création d'un member dans la db
		Member member = new Member();
		member.setUsername("maelgangloff");
		Member savedMember = memberRepository.save(member);

		// Le nouvel utilisateur emprunte le nouveau livre
		this.bookService.borrowBook(savedBook.getId(), savedMember.getId());

		// On cherche le livre emprunté dans la DB pour procéder à des tests
		Book updatedBook = this.bookRepository.findById(savedBook.getId()).orElseThrow();

		Assertions.assertNotNull(updatedBook.getBorrower());
		Assertions.assertEquals(savedMember.getId(), updatedBook.getBorrower().getId());
		Assertions.assertEquals(Status.EMPRUNTE, updatedBook.getStatus());
	}
}

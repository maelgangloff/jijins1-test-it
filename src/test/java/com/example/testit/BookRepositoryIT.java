package com.example.testit;

import com.example.testit.model.Book;
import com.example.testit.model.Member;
import com.example.testit.model.Status;
import com.example.testit.repository.BookRepository;
import com.example.testit.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

@DataJpaTest
public class BookRepositoryIT {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Test
	public void testFindByMemberAndStatus() {
		Member member = new Member();
		Member savedMember = this.memberRepository.save(member);

		Book book = new Book();
		book.setStatus(Status.EMPRUNTE);
		book.setBorrower(savedMember);
		Book savedBook = this.bookRepository.save(book);

		List<Book> byMemberAndStatus = this.bookRepository.findByMemberAndStatus(savedMember.getId(), Status.EMPRUNTE);
		Assertions.assertTrue(byMemberAndStatus.contains(savedBook));
	}
}
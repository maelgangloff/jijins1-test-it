package com.example.testit;

import com.example.testit.adapter.member.CurrentMemberServiceFake;
import com.example.testit.model.Book;
import com.example.testit.model.Member;
import com.example.testit.repository.MemberRepository;
import com.example.testit.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BookService bookService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private CurrentMemberServiceFake currentMemberService;

	@Test
	void testBorrowAndReturnBook() throws Exception {
		Member member = new Member();
		member.setUsername("mael.gangloff");
		member = memberRepository.save(member);

		currentMemberService.setCurrent(member.getId());

		Book savedBook = bookService.createBook("Mon aventure en cours de FullStack <3", "Maël Gangloff");

		mockMvc.perform(post("/books/{id}/borrow", savedBook.getId()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(savedBook.getId()))
				.andExpect(jsonPath("$.status").value("EMPRUNTE"));

		mockMvc.perform(post("/books/{id}/return", savedBook.getId()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("RENDU"));
	}
}

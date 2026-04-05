package com.example.application.controllers;


import com.example.application.dto.AuthorDTO;
import com.example.application.dto.BookDTO;
import com.example.application.model.*;
import com.example.application.model.types.BookSorting;
import com.example.application.model.types.BookStatus;
import com.example.application.model.types.BookTypes;
import com.example.application.model.types.LongLiedBookSorting;
import com.example.application.security.JwtCheckerFilter;
import com.example.application.security.JwtService;
import com.example.application.security.UserDetailsService;
import com.example.application.services.BookService;
import io.jsonwebtoken.Claims;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookControllerTest {


    @Mock
    BookService bookService;

    private BookController bookController;
    MockMvc mockMvc;

    @InjectMocks
    JwtCheckerFilter jwtCheckerFilter;


    @Mock
    JwtService jwtService;

    @Mock
    UserDetailsService userDetailsService;
    @Mock
    Claims claims;



    @BeforeEach
    void setUp() {
        // без него может не очищатьсяи тогда loadUserByusername будет излишним
        SecurityContextHolder.clearContext();
        bookController = new BookController(bookService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(bookController)
                .addFilters(jwtCheckerFilter)
                // для него нужен контекст и не будет работать @MockExtension
//                .apply(springSecurity())
                .build();

    }

    private List<BookDTO> getListBooks(){
        AuthorDTO basicAuthor = new AuthorDTO("Name", "Surname", "Paternal");

        return List.of(
                new BookDTO(BookStatus.OUT_OF_STOCK,"Book", 2026,12D, BookTypes.FANTASY, basicAuthor, LocalDate.now(), LocalDate.now()),
                new BookDTO(BookStatus.IN_STOCK,"Book1", 2025,11D, BookTypes.FANTASY, basicAuthor, LocalDate.now(), LocalDate.now())
        );
    }

    private UserDetails admin(){
        Role role= new Role("ADMIN");
        role.setPrivilegeList(List.of(new Privilege(1L, "get_models")));
        return new UserSecured(
                new User("admin", "smth", role)
        );
    }

    private UserDetails watcher(){
        return new UserSecured(
                new User("user_watcher", "smth", new Role("USER_WATCHER"))
        );
    }

    private UserDetails full(){
        return new UserSecured(
                new User("user_full", "smth", new Role("USER_FULL"))
        );
    }

    @Test
    @Tag("positive")
    @DisplayName("displayAllBooksIfRoleIsValid")
    public void displayAllBooksIfRoleIsValid() throws Exception {
        //настраиваем поведение, что как будто бы заходит администратор
        when(claims.getSubject()).thenReturn("admin");
        when(jwtService.parseToken("token")).thenReturn(claims);
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(admin());

        when(bookService.getAllBooks(any(Logger.class))).thenReturn(getListBooks());

        mockMvc.perform(get("/books")
                        .header("Authorization", "Bearer token")
                )

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                // почему не работает getTitle()
                .andExpect(jsonPath("$[0].title").value("Book"));

    }

    @Test
    @Tag("negative")
    @Tag("401")
    @DisplayName("displayAllBooksIfTokenIsInvalid")
    public void displayAllBooksIfTokenIsInvalid() throws Exception {
        // неправильный токен
        when(claims.getSubject()).thenReturn("admin");
        when(jwtService.parseToken("token")).thenReturn(claims);
        when(userDetailsService.loadUserByUsername(
                argThat(username -> List.of("admin", "user_full", "user_watcher").contains(username))
        )).thenReturn(admin());

        when(bookService.getAllBooks(any(Logger.class))).thenReturn(getListBooks());

        mockMvc.perform(get("/books")
                        .header("Authorization", "Bearer nonValidToken"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @Tag("positive")
    @DisplayName("displaySortedBooksIfTokenIsValid")
    public void displaySortedBooksIfTokenIsValid() throws Exception {

        when(claims.getSubject()).thenReturn("admin");
        when(jwtService.parseToken("token")).thenReturn(claims);
        when(userDetailsService.loadUserByUsername(
                argThat(username -> List.of("admin", "user_full", "user_watcher").contains(username))
        )).thenReturn(admin());

        when(bookService.getSortedBooks(any(BookSorting.class), any(Logger.class))).thenReturn(getListBooks());

        mockMvc.perform(get("/books/sorted")
                        .param("type", "TITLE")
                        .header("Authorization", "Bearer token")
                )

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                // почему не работает getTitle()
                .andExpect(jsonPath("$[0].title").value("Book"));

    }

    @Test
    @Tag("negative")
    @DisplayName("displaySortedBooksIfNoParametersInRequest")
    public void displaySortedBooksIfNoParametersInRequest() throws Exception {
        // нет параметров
        mockMvc.perform(get("/books/sorted")
                        .header("Authorization", "Bearer token")
                )
                .andExpect(status().is4xxClientError());

    }

    @Test
    @Tag("positive")
    @DisplayName("displayLonLiedBooksIfAllParametersOccured")
    public void displayLonLiedBooksIfAllParametersOccured() throws Exception {
        when(claims.getSubject()).thenReturn("admin");
        when(jwtService.parseToken("token")).thenReturn(claims);
        when(userDetailsService.loadUserByUsername(
                argThat(username -> List.of("admin", "user_full", "user_watcher").contains(username))
        )).thenReturn(admin());
        when(bookService.getLongLiedBooks(any(LongLiedBookSorting.class), anyInt(), any(Logger.class)))
                .thenReturn(getListBooks());

        mockMvc.perform(get("/books/longLied")
                .header("Authorization", "Bearer token")
                .param("type", "Title")
                .param("numberOfMonth", "1")
        ).andExpect(status().isOk());
    }

    @Test
    @Tag("negative")
    @DisplayName("displayLonLiedBooksIfNotCorrectUser")
    public void displayLonLiedBooksIfNotCorrectUser() throws Exception {
        // не найден такой user
        when(claims.getSubject()).thenReturn("admin");
        when(jwtService.parseToken("token")).thenReturn(claims);
        when(userDetailsService.loadUserByUsername(
                argThat(username -> List.of("admin", "user_full", "user_watcher").contains(username))
        )).thenReturn(null);


        mockMvc.perform(get("/books/longLied")
                .header("Authorization", "Bearer token")
                .param("type", "Title")
                .param("numberOfMonth", "1")
        ).andExpect(status().isUnauthorized());
    }


    @Test
    @Tag("positive")
    @DisplayName("getDescriptionIfUsernameIsFound")
    public void getDescriptionIfUsernameIsFound() throws Exception {
        when(claims.getSubject()).thenReturn("admin");
        when(jwtService.parseToken("token")).thenReturn(claims);
        when(userDetailsService.loadUserByUsername(
                argThat(username -> List.of("admin", "user_full", "user_watcher").contains(username))
        )).thenReturn(admin());

        when(bookService.getBookDescription(anyInt(), any(Logger.class))).thenReturn("Description");

        mockMvc.perform(get("/books/description/1")
                .header("Authorization", "Bearer token")
        ).andExpect(status().isOk());
    }

    @Test
    @Tag("negative")
    @DisplayName("getDescriptionIfUsernameNotFound")
    public void getDescriptionIfUsernameNotFound() throws Exception {
        // не найдено username
        when(claims.getSubject()).thenReturn("someBody");
        when(jwtService.parseToken("token")).thenReturn(claims);
        when(userDetailsService.loadUserByUsername(
                argThat(username -> List.of("admin", "user_full", "user_watcher").contains(username))
        )).thenReturn(admin());


        when(bookService.getBookDescription(anyInt(), any(Logger.class))).thenReturn("Description");

        mockMvc.perform(get("/books/description/1")
                .header("Authorization", "Bearer token")
        ).andExpect(status().isUnauthorized());
    }


    @Test
    @Tag("positive")
    @DisplayName("checkBookIfUsernameIsFound")
    public void checkBookIfUsernameIsFound() throws Exception {
        when(claims.getSubject()).thenReturn("admin");
        when(jwtService.parseToken("token")).thenReturn(claims);
        when(userDetailsService.loadUserByUsername(
                argThat(username -> List.of("admin", "user_full", "user_watcher").contains(username))
        )).thenReturn(admin());

        when(bookService.checkBook(anyInt(), any(Logger.class))).thenReturn(true);

        mockMvc.perform(get("/books/check/1")
                .header("Authorization", "Bearer token")
        ).andExpect(status().isOk());
    }

    @Test
    @Tag("negative")
    @DisplayName("checkBookIfUsernameNotFound")
    public void checkBookIfUsernameNotFound() throws Exception {
        // не найдено username
        when(claims.getSubject()).thenReturn("someBody");
        when(jwtService.parseToken("token")).thenReturn(claims);
        when(userDetailsService.loadUserByUsername(
                argThat(username -> List.of("admin", "user_full", "user_watcher").contains(username))
        )).thenReturn(admin());


        when(bookService.checkBook(anyInt(), any(Logger.class))).thenReturn(true);

        mockMvc.perform(get("/books/check/1")
                .header("Authorization", "Bearer token")
        ).andExpect(status().isUnauthorized());
    }

}


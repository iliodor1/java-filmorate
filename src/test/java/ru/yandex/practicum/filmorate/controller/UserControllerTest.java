package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserController controller;

    @Test
    public void test_shouldReturnOkStatus() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"login\": \"login\",\n" +
                                "  \"name\": \"Name\",\n" +
                                "  \"email\": \"mail@mail.ru\",\n" +
                                "  \"birthday\": \"2005-03-20\"\n" +
                                "}"))
                .andExpect(status().isOk());
    }

    @Test
    public void test_emptyLoginShouldReturn400CodeStatus() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"mail@mail.ru\"," +
                                "\"login\": \"\", \"name\": \"Ivan\"" +
                                ", \"birthday\": \"2000-02-02\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test_blankLoginShouldReturn400CodeStatus() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"mail@mail.ru\"," +
                                "\"login\": \" \", \"name\": \"Ivan\"" +
                                ", \"birthday\": \"2000-02-02\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test_emptyNameShouldReturnOkStatusAndPutLoginInName() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"mail1@mail.ru\"," +
                                "\"login\": \"John1\", \"name\": \"\"" +
                                ", \"birthday\": \"2000-02-02\"}"))
                .andExpect(content().json("{\"email\": \"mail1@mail.ru\"," +
                        "\"login\": \"John1\", \"name\": \"John1\"" +
                        ", \"birthday\": \"2000-02-02\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void test_emptyEmailShouldReturn400CodeStatus() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"\"," +
                                "\"login\": \"John\", \"name\": \"Ivan\"" +
                                ", \"birthday\": \"2000-02-02\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test_emailWithoutAtShouldReturn400CodeStatus() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"mail_mail.ru\"," +
                                "\"login\": \"John\", \"name\": \"Ivan\"" +
                                ", \"birthday\": \"2000-02-02\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test_birthdayInFutureReturn400CodeStatus() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"mail@mail.ru\"," +
                                "\"login\": \"John\", \"name\": \"Ivan\"" +
                                ", \"birthday\": \"2025-02-02\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test_addUserWithSameLoginAndEmailShouldReturn400CodeStatus() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"login\": \"login2\",\n" +
                                "  \"name\": \"Name2\",\n" +
                                "  \"email\": \"mail@mail.ru2\",\n" +
                                "  \"birthday\": \"2005-03-20\"\n" +
                                "}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"login\": \"login2\",\n" +
                                "  \"name\": \"Name2\",\n" +
                                "  \"email\": \"mail@mail.ru2\",\n" +
                                "  \"birthday\": \"2005-03-20\"\n" +
                                "}"))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"login\": \"login3\",\n" +
                                "  \"name\": \"Name3\",\n" +
                                "  \"email\": \"mail@mail.ru3\",\n" +
                                "  \"birthday\": \"2005-03-20\"\n" +
                                "}"))
                .andExpect(status().isOk());
    }

    /*@Test
    public void test_updateUsersIdShouldBeInMap() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1,\"email\": \"mail@mail.ru\"," +
                                "\"login\": \"John1\", \"name\": \"Ivan\"" +
                                ", \"birthday\": \"2000-02-02\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 400,\"email\": \"mail@yandex.ru\"," +
                                "\"login\": \"John\", \"name\": \"Ivan\"" +
                                ", \"birthday\": \"2000-02-02\"}"))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1,\"email\": \"mail@yandex.ru\"," +
                                "\"login\": \"John\", \"name\": \"Ivan\"" +
                                ", \"birthday\": \"2001-02-02\"}"))
                .andExpect(status().isOk());
    }*/
}

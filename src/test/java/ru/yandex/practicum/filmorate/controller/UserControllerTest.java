package ru.yandex.practicum.filmorate.controller;

import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private String getUserToJson(String email,
                                 String login,
                                 String name,
                                 LocalDate birthday) {
        User user = new User(-10L, email, login, name, birthday);
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new UserControllerTest.LocalDateAdapter())
                .create();

        return gson.toJson(user);
    }

    @Test
    public void test_shouldReturnOkStatus() throws Exception {
        String json = getUserToJson(
                "mail@mail.ru",
                "login",
                "Name",
                LocalDate.of(2000, 3, 20));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void test_emptyLoginShouldReturn400CodeStatus() throws Exception {
        String json = getUserToJson(
                "mail1@mail.ru",
                "",
                "Ivan",
                LocalDate.of(2000, 3, 20));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test_blankLoginShouldReturn400CodeStatus() throws Exception {
        String json = getUserToJson(
                "mail2@mail.ru",
                " ",
                "Ivan",
                LocalDate.of(2000, 3, 20));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test_emptyNameShouldReturnOkStatusAndPutLoginInName() throws Exception {
        String json = getUserToJson(
                "mail3@mail.ru",
                "John1",
                "",
                LocalDate.of(2000, 3, 20));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(content().json("{\"email\": \"mail3@mail.ru\"," +
                        "\"login\": \"John1\", \"name\": \"John1\"" +
                        ", \"birthday\": \"2000-03-20\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void test_emptyEmailShouldReturn400CodeStatus() throws Exception {
        String json = getUserToJson(
                "John",
                "Ivan",
                "",
                LocalDate.of(2000, 3, 20));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test_emailWithoutAtShouldReturn400CodeStatus() throws Exception {
        String json = getUserToJson(
                "mail3mail.ru",
                "John5",
                "",
                LocalDate.of(2000, 3, 20));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test_birthdayInFutureReturn400CodeStatus() throws Exception {
        String json = getUserToJson(
                "mail3@mail.ru",
                "John6",
                "",
                LocalDate.of(2025, 3, 20));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test_addUserWithSameLoginAndEmailShouldReturn400CodeStatus() throws Exception {
        String json = getUserToJson(
                "mail4@mail.ru",
                "John7",
                "Name7",
                LocalDate.of(2000, 3, 20));

        String jsonPost = getUserToJson(
                "mail8@mail.ru",
                "John8",
                "Name8",
                LocalDate.of(2000, 3, 20));

        String jsonFalsePost = getUserToJson(
                "mail4@mail.ru",
                "John7",
                "Name7",
                LocalDate.of(2000, 3, 20));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonFalsePost))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonPost))
                .andExpect(status().isOk());
    }

    private static class LocalDateAdapter implements JsonSerializer<LocalDate> {
        @Override
        public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE)); // "yyyy-mm-dd"
        }

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

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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private String getFilmToJson(Long id,
                                 String name,
                                 String description,
                                 LocalDate releaseDate,
                                 int duration, int mpaId) {
        Film film = new Film(id,
                name,
                description,
                releaseDate,
                duration,
                new Mpa(mpaId, null),
                null);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();

        return gson.toJson(film);
    }

    @Test
    public void test_shouldReturnOkStatus() throws Exception {
        String json = getFilmToJson(-20L,
                "name",
                "description",
                LocalDate.of(1974, 4, 17),
                100,
                1);

        System.out.println(json);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void test_emptyNameShouldReturn400CodeStatus() throws Exception {
        String json = getFilmToJson(-20L,
                "",
                "description",
                LocalDate.of(1967, 4, 17),
                120,
                4);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test_tooLongDescriptionShouldReturn400CodeStatus() throws Exception {
        String json = getFilmToJson(-20L,
                "name",
                "ttttttttttttttttTtttttttttttttttttttt" +
                        "ooooooooooooooooooOoooooooooooooooooooo" +
                        "ooooooooooooooooooOoooooooooooooooooooo" +
                        "llllllllllllllllllLllllllllllllllllllll" +
                        "ooooooooooooooooooOoooooooooooooooooooo" +
                        "nnnnnnnnnnnnnnnnnnNnnnnnnnnnnnnnnnnnnnn" +
                        "ggggggggggggggggggGgggggggggggggggg",
                LocalDate.of(1967, 4, 17),
                100,
                1);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test_releaseDateIsBeforeThanOldestFilmShouldReturn400CodeStatus() throws Exception {
        String json = getFilmToJson(-20L,
                "once upon a time",
                "description",
                LocalDate.of(1895, 12, 27),
                100,
                1);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test_negativeDurationShouldReturn400CodeStatus() throws Exception {
        String json = getFilmToJson(-20L,
                "once upon a time",
                "description",
                LocalDate.of(1955, 12, 27),
                -100,
                1);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void test_addFilmWithSameNameAndReleaseDateShouldReturn400CodeStatus() throws Exception {
        String json = getFilmToJson(-12L,
                "name12",
                "description",
                LocalDate.of(1967, 12, 27),
                100,
                1);

        String jsonFalseUpdate = getFilmToJson(-12L,
                "name12",
                "description",
                LocalDate.of(1967, 12, 27),
                100,
                1);
        String jsonUpdate = getFilmToJson(-12L,
                "name18",
                "description",
                LocalDate.of(1967, 10, 27),
                100,
                1);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonFalseUpdate))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(jsonUpdate))
                .andExpect(status().isOk());
    }

    private static class LocalDateAdapter implements JsonSerializer<LocalDate> {
        @Override
        public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE)); // "yyyy-mm-dd"
        }

    }

    /*@Test
    public void test_updateFilmWithDifferentIdShouldReturn400CodeStatus() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"once upon a time\",\"description\": \"film description\","
                                + "\"releaseDate\": \"1895-12-28\", \"duration\": 100}"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 1, \"name\": \"once upon a time\", "
                                + "\"description\": \"Different film description\","
                                + "\"releaseDate\": \"2000-12-28\", \"duration\": 90}"))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"id\": 0, \"name\": \"once upon a time\", "
                                + "\"description\": \"Different film description\","
                                + "\"releaseDate\": \"2000-12-28\", \"duration\": 90}"))
                .andExpect(status().isOk());
    }*/
}

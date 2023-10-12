package com.korotkov.todo.controller;

import com.korotkov.todo.security.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class UserControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JWTUtil jwtUtil;

    @Test
    void getInfo() throws Exception {


        String token = getToken();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/me")
                .header("Authorization", token);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                    {
                                        "id": 1,
                                        "login": "nikitos",
                                        "role": "ADMIN",
                                        "isInBan": false
                                    }
                                """)
                );
    }

    @Test
    void getTodosCount() throws Exception {
        String token = getToken();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/todos-count")
                .header("Authorization", token);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                    {
                                        "count": 194
                                    }
                                """)
                );
    }

    @Test
    void getTodos() {

    }

    @Test
    void createTodo() {
    }

    @Test
    void getTodo() {
    }

    @Test
    void updateTodo() {
    }

    @Test
    void deleteTodo() {
    }

    @Test
    void setPrivileges() {
    }

    @Test
    void acceptPrivilege() {
    }

    @Test
    void getTodoRequests() {
    }

    private String getToken() {
        return jwtUtil.generateToken("nikitos");
    }
}
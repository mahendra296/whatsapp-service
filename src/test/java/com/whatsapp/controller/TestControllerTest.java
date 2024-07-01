package com.whatsapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TestControllerTest {

    public MockMvc mockMvc;

    @Autowired
    public TestController testController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(testController).build();
    }

    @Test
    void saveStudent() throws Exception {
        Student student = Student.builder()
                .firstName("firstName")
                .lastName("lastName")
                .age(27)
                .totalSubject(5)
                .build();

        mockMvc.perform(post("/api/v1/student").contentType("application/json").content(objectMapper.writeValueAsString(student))).andExpect(status().is(200));
    }

    /*@Test
    void getStudent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/student").contentType("application/json")).andExpect(status().is(200));
    }*/
}
package com.irsyad.springmysqlredis.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irsyad.springmysqlredis.SpringMysqlRedisApplication;
import com.irsyad.springmysqlredis.model.Employee;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.regex.Pattern;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class EmployeeRESTTest {

    private static final String RESOURCE_LOCATION_PATTERN = "http://localhost/api/employees/[0-9]+";

    @Autowired
    WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void initTests() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void shouldCreateRetrieveDelete() throws Exception {
        Employee p1 = mockEmployee("shouldCreateRetrieveDelete");
        byte[] r1Json = toJson(p1);

        // CREATE
        MvcResult result = mvc.perform(post("/api/employees")
                .content(r1Json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern())
                .andReturn();
        long id = getResourceIdFromUrl(result.getResponse().getRedirectedUrl());

        // RETRIEVE
        mvc.perform(get("/api/employees/" + id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) id)))
                .andExpect(jsonPath("$.age", is(p1.getAge())))
                .andExpect(jsonPath("$.name", is(p1.getName())))
                .andExpect(jsonPath("$.salary", is(p1.getSalary())));

        // DELETE
        mvc.perform(delete("/api/employees/" + id))
                .andExpect(status().isNoContent());
    }


    @Test
    public void shouldCreateAndUpdateAndDelete() throws Exception {
        Employee p1 = mockEmployee("shouldCreateAndUpdate");
        byte[] r1Json = toJson(p1);
        //CREATE
        MvcResult result = mvc.perform(post("/api/employees")
                .content(r1Json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern())
                .andReturn();
        long id = getResourceIdFromUrl(result.getResponse().getRedirectedUrl());

        Employee p2 = mockEmployee("shouldCreateAndUpdate");
        p2.setId(id);
        byte[] r2Json = toJson(p2);

        // UPDATE
        result = mvc.perform(put("/api/employees/" + id)
                .content(r2Json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();

        // RETRIEVE updated
        mvc.perform(get("/api/employees/" + id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) id)))
                .andExpect(jsonPath("$.name", is(p2.getName())))
                .andExpect(jsonPath("$.age", is(p2.getAge())))
                .andExpect(jsonPath("$.salary", is(p2.getSalary())));

        // DELETE
        mvc.perform(delete("/api/employees/" + id))
                .andExpect(status().isNoContent());
    }

    // ========================================================== //

    private long getResourceIdFromUrl(String locationUrl) {
        String[] parts = locationUrl.split("/");
        return Long.valueOf(parts[parts.length - 1]);
    }

    private Employee mockEmployee(String prefix) {
        Employee employee = new Employee();
        employee.setSalary(1000.00);
        employee.setAge(10);
        employee.setName(prefix + "_name");
        return employee;
    }

    private byte[] toJson(Object r) throws Exception {
        ObjectMapper map = new ObjectMapper();
        return map.writeValueAsString(r).getBytes();
    }

    // match redirect header URL (aka Location header)
    private static ResultMatcher redirectedUrlPattern() {
        return result -> {
            Pattern pattern = Pattern.compile("\\A" + EmployeeRESTTest.RESOURCE_LOCATION_PATTERN + "\\z");
            assertTrue(pattern.matcher(result.getResponse().getRedirectedUrl()).find());
        };
    }
}
package se.webcv.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import se.webcv.UnitTest;
import se.webcv.model.*;
import se.webcv.repository.CVRepository;
import se.webcv.repository.EmployeeRepository;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EmployeeControllerTest extends UnitTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    CVRepository cvRepository;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void postShouldCreateEmployee() throws Exception {
        String id = createEmployee();
        Employee e = employeeRepository.findActiveEmployee(id);
        assertThat(e, notNullValue());
        assertThat(e.getId(), is(id));
        assertThat(e.getName(), is("name"));
        assertThat(e.getRole(), is("role"));
        CV cv = cvRepository.findActiveCV(id, "SE");
        assertThat(cv.getEmployeeId(), is(id));
        assertThat(cv.getLang(), is("SE"));
        assertThat(cv.getIntroduction(), is("intro"));
        assertThat(cv.getFramework(), is("frame"));
        assertThat(cv.getLanguage(), is("lang"));
        assertThat(cv.getMethod(), is("method"));
        assertThat(cv.getDynamicSections().size(), is(1));
        assertThat(cv.getAssignments().size(), is(1));
        assertThat(cv.getAssignments().get(0).getCustomer(), is("Cust"));
        assertThat(cv.getAssignments().get(0).getDate(), is("date"));
        assertThat(cv.getAssignments().get(0).getDescription(), is("desc"));
        assertThat(cv.getAssignments().get(0).getRole(), is("Role"));
        assertThat(cv.getAssignments().get(0).getTechniques(), is("Tech"));
    }

    private String createEmployee() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEmployee(null))))
                .andExpect(status().isCreated())
                .andExpect(header().string("X-createdId", notNullValue()))
                .andExpect(header().string("location", notNullValue()))
                .andReturn();
        return mvcResult.getResponse().getHeader("X-createdId");
    }

    @Test
    public void putWithWrongIdShouldFail() throws Exception {
        String id = createEmployee();
        mockMvc.perform(put("/employees/" + id + "doesNotExist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEmployee(id, "newname"))))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void putShouldUpdateEmployee() throws Exception {
        String id = createEmployee();
        mockMvc.perform(put("/employees/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newEmployee(id, "newname"))))
                .andExpect(status().isNoContent());
        Employee e = employeeRepository.findActiveEmployee(id);
        assertThat(e, notNullValue());
        assertThat(e.getId(), is(id));
        assertThat(e.getName(), is("newname"));
        assertThat(e.getRole(), is("role"));
        CV cv = cvRepository.findActiveCV(id, "SE");
        assertThat(cv.getEmployeeId(), is(id));
        assertThat(cv.getLang(), is("SE"));
        assertThat(cv.getIntroduction(), is("intro"));
        assertThat(cv.getFramework(), is("frame"));
        assertThat(cv.getLanguage(), is("lang"));
        assertThat(cv.getMethod(), is("method"));
        assertThat(cv.getDynamicSections().size(), is(1));
        assertThat(cv.getAssignments().size(), is(1));
        assertThat(cv.getAssignments().get(0).getCustomer(), is("Cust"));
        assertThat(cv.getAssignments().get(0).getDate(), is("date"));
        assertThat(cv.getAssignments().get(0).getDescription(), is("desc"));
        assertThat(cv.getAssignments().get(0).getRole(), is("Role"));
        assertThat(cv.getAssignments().get(0).getTechniques(), is("Tech"));
    }

    @Test
    public void getWithIdShouldReturnEmployee() throws Exception {
        String id = createEmployee();
        MvcResult mvcResult = mockMvc.perform(get("/employees/" + id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        EmployeeDto e = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), EmployeeDto.class);
        assertThat(e, notNullValue());
        assertThat(e.id, is(id));
        assertThat(e.name, is("name"));
        assertThat(e.role, is("role"));
        CVDto cv = e.cvs.get("SE");
        assertThat(cv.id, is(notNullValue()));
        assertThat(cv.introduction, is("intro"));
        assertThat(cv.framework, is("frame"));
        assertThat(cv.language, is("lang"));
        assertThat(cv.method, is("method"));
        assertThat(cv.dynamicSections.size(), is(1));
        assertThat(cv.assignments.size(), is(1));
        assertThat(cv.assignments.get(0).getCustomer(), is("Cust"));
        assertThat(cv.assignments.get(0).getDate(), is("date"));
        assertThat(cv.assignments.get(0).getDescription(), is("desc"));
        assertThat(cv.assignments.get(0).getRole(), is("Role"));
        assertThat(cv.assignments.get(0).getTechniques(), is("Tech"));
    }

    @Test
    public void getShouldSearchEmptySearchParamShouldReturnAll() throws Exception {
        final String id = createEmployee();
        MvcResult mvcResult = mockMvc.perform(get("/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<EmployeeSearchResultDto> results = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, EmployeeSearchResultDto.class));
        assertTrue(results.size() > 0);
        assertTrue(Iterables.find(results, new Predicate<EmployeeSearchResultDto>() {
            @Override
            public boolean apply(EmployeeSearchResultDto input) {
                return input.getId().equals(id)
                        && input.getName().equals("name");
            }
        }) != null);

    }

    @Test
    public void getShouldSearchSearchParamShouldReturnMatching() throws Exception {
        final String id = createEmployee();
        MvcResult mvcResult = mockMvc.perform(get("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .param("searchText", "a"))
                .andExpect(status().isOk())
                .andReturn();
        List<EmployeeSearchResultDto> results = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, EmployeeSearchResultDto.class));
        assertTrue(results.size() > 0);
        assertTrue(Iterables.find(results, new Predicate<EmployeeSearchResultDto>() {
            @Override
            public boolean apply(EmployeeSearchResultDto input) {
                return input.getId().equals(id)
                        && input.getName().equals("name");
            }
        }) != null);
    }

    @Test
    public void getWithUnknownIdShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/employees/cannotBeThere")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteWithUnknownIdShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/employees/cannotBeThere")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteWithShouldReturnNoContentAndArchiveEmp() throws Exception {
        final String id = createEmployee();
        mockMvc.perform(delete("/employees/" + id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        Employee e = employeeRepository.findActiveEmployee(id);
        assertThat(e, nullValue());

        mockMvc.perform(get("/employees/" + id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private CVDto newCV() {
        CVDto cv = new CVDto("1", "intro", "lang", "frame", "method", null,
                Arrays.asList(new DynamicSection(HeadLines.COURSES)),
                Arrays.asList(newAssignment()));
        return cv;
    }

    private Assignment newAssignment() {
        Assignment a = new Assignment();
        a.setCustomer("Cust");
        a.setDate("date");
        a.setDescription("desc");
        a.setRole("Role");
        a.setTechniques("Tech");
        return a;
    }

    private EmployeeDto newEmployee(String id, String name) {
        return new EmployeeDto(id, name, null, null, "role", null, null,
                ImmutableMap.of("SE", newCV()));
    }

    private EmployeeDto newEmployee(String id) {
        return newEmployee(id, "name");
    }

}
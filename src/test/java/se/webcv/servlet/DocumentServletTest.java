package se.webcv.servlet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import se.webcv.repository.CVRepository;
import se.webcv.repository.EmployeeRepository;
import se.webcv.model.Assignment;
import se.webcv.model.CV;
import se.webcv.model.Employee;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DocumentServletTest {

    final DocumentServlet documentServlet = new DocumentServlet();
    final CVRepository cvRepository = mock(CVRepository.class);
    final EmployeeRepository employeeRepository = mock(EmployeeRepository.class);
    final HttpServletRequest request = mock(HttpServletRequest.class);
    final HttpServletResponse response = mock(HttpServletResponse.class);

    @Before
    public void setUp() throws Exception {
        documentServlet.cvRepository = cvRepository;
        documentServlet.employeeRepository = employeeRepository;
    }

    @Test
    public void noEmployeeShouldReturn404() throws Exception {
        when(request.getParameter("employeeid")).thenReturn("1");
        when(request.getParameter("gentype")).thenReturn("docx");
        when(request.getParameter("lang")).thenReturn("EN");
        documentServlet.doGet(request, response);
        verify(response).sendError(404);
    }

    @Test
    public void validEmployeeShouldGenerateDocument() throws Exception {
        when(request.getParameter("employeeid")).thenReturn("1");
        when(request.getParameter("gentype")).thenReturn("docx");
        when(request.getParameter("lang")).thenReturn("EN");
        when(employeeRepository.findActiveEmployee("1")).thenReturn(newEmployee("1"));
        when(cvRepository.findActiveCV("1", "EN")).thenReturn(newCV("1", "EN"));
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(8192);
        when(response.getOutputStream()).thenReturn(new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                bos.write(b);
            }
        });
        documentServlet.doGet(request, response);
        Assert.assertTrue(bos.size() > 0);
    }

    private CV newCV(String employeeId, String lang) {
        CV cv = new CV(employeeId, lang);
        cv = cv.createDynamicSectionsIfNotFound();
        cv.setIntroduction("intro");
        cv.setLanguage("Language");
        cv.getAssignments().add(newAssignment());
        cv.getAssignments().add(newAssignment());
        cv.getAssignments().add(newAssignment());
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

    private Employee newEmployee(String id) {
        Employee e = new Employee("test");
        e.setId(id);
        e.setRole("role");
        return e;
    }
}
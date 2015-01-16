package se.webcv.rest;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.webcv.db.EmployeeRepository;
import se.webcv.model.Employee;
import se.webcv.model.EmployeeDto;

import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @RequestMapping(produces = "application/json", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<EmployeeDto> findEmployees(@RequestParam(required = false) String searchText) {
        return employeeRepository.findActiveEmployeesNoImage(nullIfEmpty(searchText));
    }

    private String nullIfEmpty(String searchText) {
        return searchText == null || searchText.trim().length() == 0 ? null : searchText.trim();
    }

    @RequestMapping(value = "/{id}", produces = "application/json", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public Employee findEmployee(@PathVariable String id) {
        Employee e = employeeRepository.findActiveEmployee(id);
        if (e == null) {
            throw new ResourceNotFoundException();
        }
        return e;
    }

    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public HttpHeaders createEmployee(@RequestBody Employee employee) {
        Employee saved = employeeRepository.saveEmployee(employee);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("location", "/rest/employees/" + saved.getId());
        httpHeaders.add("X-createdId", saved.getId());
        return httpHeaders;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable String id) {
        Employee employee = employeeRepository.findActiveEmployee(id);
        if (employee == null) {
            throw new ResourceNotFoundException();
        }
        employeeRepository.saveEmployee(employee.archive(DateTime.now()));
    }

}

package se.webcv.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import se.webcv.db.EmployeeRepository;
import se.webcv.model.Employee;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @RequestMapping(produces = "application/json", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<Employee> getEmployees(@RequestParam(required = false) String searchText) {
        return employeeRepository.getEmployees(asOptional(searchText));
    }

    private Optional<String> asOptional(String searchText) {
        return Optional.ofNullable(searchText == null || searchText.trim().length() == 0 ? null : searchText.trim());
    }

    @RequestMapping(value = "/{id}", produces = "application/json", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public Employee getEmployee(@PathVariable String id) {
        return employeeRepository.getEmployee(id);
    }

    @RequestMapping(produces = "application/json", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public HttpHeaders createEmployee(@RequestBody Employee employee) {
        Employee saved = employeeRepository.saveEmployee(employee);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("location", "/app/employees/" + saved.getId());
        httpHeaders.add("X-createdId", saved.getId());
        return httpHeaders;
    }

}

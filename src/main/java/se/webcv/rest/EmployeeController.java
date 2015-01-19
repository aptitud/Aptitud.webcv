package se.webcv.rest;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.*;
import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.webcv.db.CVRepository;
import se.webcv.db.EmployeeRepository;
import se.webcv.model.CV;
import se.webcv.model.Employee;
import se.webcv.model.EmployeeDto;
import se.webcv.model.EmployeeSearchResultDto;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/employees")
public class EmployeeController {
    private static final Collection<String> langs = ImmutableList.of("SE", "EN");

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CVRepository cvRepository;

    @RequestMapping(produces = "application/json", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public List<EmployeeSearchResultDto> findEmployees(@RequestParam(required = false) String searchText) {
        return employeeRepository.findActiveEmployeesNoImage(nullIfEmpty(searchText));
    }

    private String nullIfEmpty(String searchText) {
        return searchText == null || searchText.trim().length() == 0 ? null : searchText.trim();
    }

    @RequestMapping(value = "/{id}", produces = "application/json", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public EmployeeDto findEmployee(@PathVariable String id) {
        Employee e = employeeRepository.findActiveEmployee(id);
        if (e == null) {
            throw new ResourceNotFoundException();
        }
        return EmployeeDto.from(e, cvsFrom(e));
    }

    private Collection<CV> cvsFrom(final Employee e) {
        List<CV> cvs = cvRepository.findActiveCVs(e.getId());
        final Set<String> currentLangs = Sets.newHashSet(Iterables.transform(cvs, new Function<CV, String>() {
            @Override
            public String apply(CV input) {
                return input.getLang();
            }
        }));
        Collection<String> langsToAdd = Collections2.filter(langs, new Predicate<String>() {
            @Override
            public boolean apply(String lang) {
                return !currentLangs.contains(lang);
            }
        });
        if (langsToAdd.isEmpty()) {
            return cvs;
        }

        return Lists.newArrayList(Iterables.concat(cvs, Collections2.transform(langsToAdd, new Function<String, CV>() {
            @Override
            public CV apply(String lang) {
                return new CV(e.getId(), lang).createDynamicSectionsIfNotFound();
            }
        })));
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public HttpHeaders createEmployee(@RequestBody EmployeeDto employeeDto) {
        Employee saved = saveEmployeeInner(employeeDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("location", "/rest/employees/" + saved.getId());
        httpHeaders.add("X-createdId", saved.getId());
        return httpHeaders;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void saveEmployee(@PathVariable String id, @RequestBody EmployeeDto employeeDto) {
        Validate.isTrue(id.equals(employeeDto.id));
        saveEmployeeInner(employeeDto);
    }

    private Employee saveEmployeeInner(@RequestBody EmployeeDto employeeDto) {
        Employee saved = employeeRepository.saveEmployee(employeeDto.toEmployee());
        for (CV cv : employeeDto.toCVs(saved.getId())) {
            cvRepository.saveCV(cv);
        }
        return saved;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable String id) {
        Employee employee = employeeRepository.findActiveEmployee(id);
        if (employee == null) {
            throw new ResourceNotFoundException();
        }
        DateTime now = DateTime.now();
        employeeRepository.saveEmployee(employee.archive(now));
        List<CV> cvs = cvRepository.findActiveCVs(id);
        for (CV cv : cvs) {
            cvRepository.saveCV(cv.archive(now));
        }
    }

}

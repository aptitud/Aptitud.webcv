package se.webcv.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class EmployeeDto {

    public final String name;
    public final String mail;
    public final String id;
    public final String phonenr;
    public final String role;
    public final String img;
    public final DateTime archivedAt;

    public final Map<String, CVDto> cvs;

    @JsonCreator
    public EmployeeDto(@JsonProperty("id") String id,
                       @JsonProperty("name") String name,
                       @JsonProperty("mail") String mail,
                       @JsonProperty("phonenr") String phonenr,
                       @JsonProperty("role") String role,
                       @JsonProperty("img") String img,
                       @JsonProperty("archivedAt") DateTime archivedAt,
                       @JsonProperty("cvs") Map<String, CVDto> cvs) {
        this.name = name;
        this.mail = mail;
        this.id = id;
        this.phonenr = phonenr;
        this.role = role;
        this.img = img;
        this.archivedAt = archivedAt;
        this.cvs = cvs != null ? Collections.unmodifiableMap(cvs) : Collections.<String, CVDto>emptyMap();
    }

    public static EmployeeDto from(Employee e, Collection<CV> cvs) {
        ImmutableMap<String, CV> perLang = Maps.uniqueIndex(cvs, new Function<CV, String>() {
            @Override
            public String apply(CV cv) {
                return cv.getLang();
            }
        });
        return new EmployeeDto(e.getId(), e.getName(), e.getMail(), e.getPhonenr(), e.getRole(), e.getImg(), e.getArchivedAt(),
                Maps.transformValues(perLang, new Function<CV, CVDto>() {
                    @Override
                    public CVDto apply(CV cv) {
                        return CVDto.from(cv);
                    }
                }));
    }

    public Employee toEmployee() {
        Employee e = new Employee(name);
        if (id != null) {
            e.setId(id);
        }
        e.setImg(img);
        e.setMail(mail);
        e.setPhonenr(phonenr);
        e.setRole(role);
        return e;
    }

    public Collection<CV> toCVs(final String employeeId) {
        return Collections2.transform(cvs.entrySet(), new Function<Map.Entry<String, CVDto>, CV>() {
            @Override
            public CV apply(Map.Entry<String, CVDto> entry) {
                CVDto dto = entry.getValue();
                CV cv = new CV(employeeId, entry.getKey());
                if (dto.id != null) {
                    cv.setId(dto.id);
                }
                cv.setAssignments(dto.assignments);
                cv.setDynamicSections(dto.dynamicSections);
                cv.setFramework(dto.framework);
                cv.setIntroduction(dto.introduction);
                cv.setLanguage(dto.language);
                cv.setMethod(dto.method);
                return cv;
            }
        });
    }
}

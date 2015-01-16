package se.webcv.model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;

public class CV {

    @Id
    private String id;
    private String lang;
    private String employeeId;
    private String introduction;
    private String language;
    private String framework;
    private String method;
    private DateTime modified;
    private List<DynamicSection> dynamicSections = new ArrayList<DynamicSection>();
    private List<Assignment> assignments = new ArrayList<Assignment>();
    private DateTime archivedAt;

    public String getId() {
        return id;
    }

    public DateTime getModified() {
        return modified;
    }

    public void setModified(DateTime modified) {
        this.modified = modified;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public List<DynamicSection> getDynamicSections() {
        return dynamicSections;
    }

    public void setDynamicSections(List<DynamicSection> dynamicSections) {
        this.dynamicSections = dynamicSections;
    }

    public void setArchivedAt(DateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    public DateTime getArchivedAt() {
        return archivedAt;
    }

    public CV archive(DateTime at) {
        this.archivedAt = at;
        return this;
    }

    public CV createDynamicSectionsIfNotFound() {
        if (getDynamicSections().isEmpty()) {
            getDynamicSections().add(new DynamicSection(HeadLines.EDUCATION));
            getDynamicSections().add(new DynamicSection(HeadLines.COURSES));
            getDynamicSections().add(new DynamicSection(HeadLines.LECTIURES));
            getDynamicSections().add(new DynamicSection(HeadLines.OTHER));
        }
        return this;
    }
}

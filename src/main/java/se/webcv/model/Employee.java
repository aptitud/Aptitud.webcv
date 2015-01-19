package se.webcv.model;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "employees")
public class Employee {

    @Id
    private String id;
    private String name;
    private String mail;
    private String phonenr;
    private String role;
    private String img;
    private DateTime archivedAt;

    public Employee(String name) {
        setName(name);
    }

    public Employee() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Employee name can not be null");
        }
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhonenr() {
        return phonenr;
    }

    public void setPhonenr(String phonenr) {
        this.phonenr = phonenr;
    }

    @Override
    public String toString() {
        return id + ":" + mail + ":" + name + ":" + phonenr;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public DateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(DateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    public Employee archive(DateTime at) {
        this.archivedAt = at;
        return this;
    }

    public void setId(String id) {
        Validate.notEmpty(id);
        this.id = id;
    }
}

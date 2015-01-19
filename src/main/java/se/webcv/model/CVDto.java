package se.webcv.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CVDto {
    public final String id;
    public final String introduction;
    public final String language;
    public final String framework;
    public final String method;
    public final DateTime modified;
    public final List<DynamicSection> dynamicSections;
    public final List<Assignment> assignments;

    @JsonCreator
    public CVDto(@JsonProperty("id") String id,
                 @JsonProperty("introduction") String introduction,
                 @JsonProperty("language") String language,
                 @JsonProperty("framework") String framework,
                 @JsonProperty("method") String method,
                 @JsonProperty("modified") DateTime modified,
                 @JsonProperty("dynamicSections") List<DynamicSection> dynamicSections,
                 @JsonProperty("assignments") List<Assignment> assignments) {
        this.id = id;
        this.introduction = introduction;
        this.language = language;
        this.framework = framework;
        this.method = method;
        this.modified = modified;
        this.dynamicSections = dynamicSections != null ? Collections.unmodifiableList(dynamicSections) : Collections.<DynamicSection>emptyList();
        this.assignments = assignments != null ? Collections.unmodifiableList(assignments) : Collections.<Assignment>emptyList();
    }

    public static CVDto from(CV cv) {
        return new CVDto(cv.getId(), cv.getIntroduction(), cv.getLanguage(), cv.getFramework(), cv.getMethod(),
                cv.getModified(), cv.getDynamicSections(), cv.getAssignments());
    }
}

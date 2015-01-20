package se.webcv.repository.inmem;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import se.webcv.model.CV;
import se.webcv.repository.CVRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by marcus on 20/01/15.
 */
@Repository
@Profile("in-mem")
public class InMemoryCVRepository extends AbstractInMemoryRepository<String, CV> implements CVRepository {
    @Override
    public List<CV> findActiveCVs(final String employeeId) {
        return Lists.newArrayList(Iterables.filter(findAll(), new Predicate<CV>() {
            @Override
            public boolean apply(CV input) {
                return input.getEmployeeId().equals(employeeId)
                        && input.getArchivedAt() == null;
            }
        }));
    }

    @Override
    public CV findActiveCV(final String employeeId, final String lang) {
        return Iterables.find(findAll(), new Predicate<CV>() {
            @Override
            public boolean apply(CV input) {
                return input.getEmployeeId().equals(employeeId)
                        && input.getLang().equals(lang)
                        && input.getArchivedAt() == null;
            }
        }, null);
    }

    @Override
    public CV saveCV(CV cv) {
        if (cv.getId() == null) {
            cv.setId(UUID.randomUUID().toString());
        }
        return save(cv.getId(), cv);
    }
}

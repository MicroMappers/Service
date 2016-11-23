package qa.qcri.mm.trainer.pybossa.dao.impl;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import qa.qcri.mm.trainer.pybossa.dao.ProjectPybossaDao;
import qa.qcri.mm.trainer.pybossa.entityForPybossa.Project;

/**
 * User: Kushal
 */
@Repository
public class ProjectPybossaDaoImpl extends AbstractDaoImplForPybossa<Project, Integer> implements ProjectPybossaDao {

    protected ProjectPybossaDaoImpl(){
        super(Project.class);
    }

	@Override
	public Project getProjectByShortName(String shortName) {
		return findByCriterionID(Restrictions.eq("shortName",shortName));
	}
	
	@Override
    public Project saveProject(Project project) {
    	
        if(StringUtils.isEmpty(project.getCreated()) || StringUtils.isEmpty(project.getUpdated())) {
        		String date = new Date().toString();
				project.setCreated(date);
        		project.setUpdated(date);
        }
        save(project);
        return project;
    }

    @Override
    public Project updateProject(Project project) {
		String date = new Date().toString();
		if(StringUtils.isEmpty(project.getCreated())) {
			project.setCreated(date);
		}
		project.setUpdated(date);
		saveOrUpdate(project);
		return project;
    }
}

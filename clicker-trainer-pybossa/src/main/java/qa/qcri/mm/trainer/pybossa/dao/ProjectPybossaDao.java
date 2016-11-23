package qa.qcri.mm.trainer.pybossa.dao;

import qa.qcri.mm.trainer.pybossa.entityForPybossa.Project;

public interface ProjectPybossaDao extends AbstractDao<Project, Integer>{
	
	Project getProjectByShortName(String shortName);

	Project saveProject(Project project);

	Project updateProject(Project project);
}
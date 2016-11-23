package qa.qcri.mm.trainer.pybossa.service;

import qa.qcri.mm.trainer.pybossa.entityForPybossa.Project;

public interface ProjectPybossaService {
	
	public Project getProjectByShortName(String shortName);
	
	public Project createProject(Project project);

	public Project updateProject(Project project);
}

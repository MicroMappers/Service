package qa.qcri.mm.trainer.pybossa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qa.qcri.mm.trainer.pybossa.dao.ProjectPybossaDao;
import qa.qcri.mm.trainer.pybossa.entityForPybossa.Project;
import qa.qcri.mm.trainer.pybossa.service.ProjectPybossaService;

@Service("ProjectPybossaService")
@Transactional(readOnly = true)
public class ProjectPybossaServiceImpl implements ProjectPybossaService {

    @Autowired
    private ProjectPybossaDao projectPybossaDao;
    
	@Override
	public Project getProjectByShortName(String shortName) {
		return projectPybossaDao.getProjectByShortName(shortName);
	}

	@Override
	public Project createProject(Project project) {
		return projectPybossaDao.saveProject(project);
	}
	
	@Override
	public Project updateProject(Project project) {
		return projectPybossaDao.updateProject(project);
	}
}

package com.askrida.web.service.service;

import com.askrida.web.service.exception.ResourceNotFoundException;
import com.askrida.web.service.model.ProjectModel;
import com.askrida.web.service.model.TaskItem;
import com.askrida.web.service.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic untuk modul Project & Task management.
 */
@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public List<ProjectModel> getAllProjects()      { return projectRepository.getAllProjects(); }

    public ProjectModel getProjectById(int id) {
        ProjectModel p = projectRepository.getProjectById(id);
        if (p == null) throw new ResourceNotFoundException("Project", id);
        return p;
    }

    @Transactional
    public void addProject(ProjectModel p) throws Exception    { projectRepository.addProject(p); }

    @Transactional
    public void updateProject(ProjectModel p) throws Exception {
        getProjectById(p.getIdProject());
        projectRepository.updateProject(p);
    }

    @Transactional
    public void deleteProject(int id) throws Exception {
        getProjectById(id);
        projectRepository.deleteProject(id);
    }

    public List<TaskItem> getTasksByProject(int projectId) { return projectRepository.getTasksByProject(projectId); }
    public List<TaskItem> getAllTasks()                     { return projectRepository.getAllTasks(); }

    @Transactional
    public void addTask(TaskItem task) throws Exception            { projectRepository.addTask(task); }

    @Transactional
    public void updateTask(TaskItem task) throws Exception         { projectRepository.updateTask(task); }

    @Transactional
    public void updateTaskStatus(int taskId, String status) throws Exception {
        projectRepository.updateTaskStatus(taskId, status);
    }

    @Transactional
    public void deleteTask(int id) throws Exception                { projectRepository.deleteTask(id); }

    public int countProjects()                         { return projectRepository.countProjects(); }
    public int countProjectsByStatus(String status)    { return projectRepository.countProjectsByStatus(status); }
    public int countTasks()                            { return projectRepository.countTasks(); }
    public int countTasksByStatus(String status)       { return projectRepository.countTasksByStatus(status); }
}

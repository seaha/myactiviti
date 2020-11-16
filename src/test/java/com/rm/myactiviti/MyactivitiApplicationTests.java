package com.rm.myactiviti;

import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

//@SpringBootTest
public class MyactivitiApplicationTests {

    //Activiti6

    /**
     * 初始化数据库
     */
    @Test
    public void init() {
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml.bak");

        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        System.out.println(processEngine);
    }

    /**
     * 部署请假流程
     */
    @Test
    public void deployLeave() {
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml.bak");

        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        //获取RepositoryService
        RepositoryService repositoryService =
                processEngine.getRepositoryService();
        //部署
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("processes/leave.bpmn")
                .name("请假申请流程")
                .deploy();
        System.out.println("请假流程id=" + deployment.getId() + "\n" + "请假流程名称=" + deployment.getName());
    }

    /**
     * 启动请假流程
     */
    @Test
    public void startProcessLeave() {
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml.bak");

        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        //获取RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //根据key启动流程
        ProcessInstance leaveProcessInstance =
                runtimeService.startProcessInstanceByKey("leave");
        //获取流程实例相关信息
        String processDefinitionId = leaveProcessInstance.getProcessDefinitionId();
        String deploymentId = leaveProcessInstance.getDeploymentId();
        String activityId = leaveProcessInstance.getActivityId();
        String id = leaveProcessInstance.getId();
        System.out.println("流程id=" + id + "\n流程定义id=" + processDefinitionId + "\n流程部署id=" + deploymentId + "\n流程当前活动id=" + activityId);
    }

    /**
     * 任务查询
     */
    @Test
    public void taskQuery() {
        //待办任务的人
        String assignee = "请假人";
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml.bak");
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        //获取taskservice
        TaskService leaveTaskService = processEngine.getTaskService();
        //查询任务列表
        List<Task> leaveTaskList =
                leaveTaskService.createTaskQuery().processDefinitionKey("leave").taskAssignee(assignee).list();
        //遍历任务列表
        for (Task task : leaveTaskList) {
            String processDefinitionId = task.getProcessDefinitionId();
            String processInstanceId = task.getProcessInstanceId();
            String assignee1 = task.getAssignee();
            String id = task.getId();
            String name = task.getName();

            System.out.println("流程定义id = " + processDefinitionId + "\n流程实例id = " +
                    processInstanceId + "\n任务负责人 = " + assignee1 + "\n任务id=" + id + "\n任务名称 = " + name);
        }
    }

    /**
     * 完成任务处理
     */
    @Test
    public  void leaveTaskComplete(){
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml.bak");
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();

        //待办人
        String assignee= "请假人";
        //获取taskservice
        TaskService taskService = processEngine.getTaskService();
        //查询任务列表
        List<Task> leaveTaskList = taskService.createTaskQuery().processDefinitionKey("leave").taskAssignee(assignee).list();
        //遍历任务列表
        for (Task task : leaveTaskList) {
            String id = task.getId();
            //完成任务
            taskService.complete(id);
        }
    }

    //Activiti7
    /**
     * 流程定义相关操作
     */
    @Autowired
    private ProcessRuntime processRuntime;

    /**
     * 任务相关操作
     */
    @Autowired
    private TaskRuntime taskRuntime;

}

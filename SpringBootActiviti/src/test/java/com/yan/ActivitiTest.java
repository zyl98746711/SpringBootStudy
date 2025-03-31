package com.yan;


import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ActivitiTest {

    private static final Logger LOG = LoggerFactory.getLogger(ActivitiTest.class);

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    /**
     * 部署流程
     */
    @Test
    void testDeploy() {
        DeploymentBuilder builder = repositoryService.createDeployment();
        builder.addClasspathResource("bpmn/test/test.bpmn");
        builder.addClasspathResource("bpmn/test/test.png");
        builder.tenantId("123");//区分同一个流程所属系统，该值主要用于记录启动的流程实例归属于哪个系统。
        builder.name("请假流程");
        builder.key("test1");
        Deployment deployment = builder.deploy();
        Assertions.assertNotNull(deployment);
        //输出部署信息
        System.out.println("流程部署id：" + deployment.getId());
        System.out.println("流程部署名称：" + deployment.getName());
    }

    @Test
    void testListProcess() {
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        Assertions.assertNotNull(list);
        list.stream().forEach(System.out::println);
    }

    @Test
    void testStartProcess() {
        Map<String, Object> map = new HashMap<>();
        map.put("reason", "生病");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKeyAndTenantId("test.bpmn.20", "1", map, "123");
        Assertions.assertNotNull(processInstance);
        LOG.info("{}", processInstance);
    }

    @Test
    void testGetTask() {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey("1", "test.bpmn.20").singleResult();
        Assertions.assertNotNull(processInstance);
        LOG.info("{}", processInstance);
    }

    @Test
    void testListTask() {
        List<Task> list = taskService.createTaskQuery().processInstanceBusinessKey("1").includeTaskLocalVariables().list();
        for (Task task : list) {
            Map<String, Object> taskLocalVariables = task.getTaskLocalVariables();
            System.out.println(taskLocalVariables);
        }
        list.stream().forEach(System.out::println);
    }
}

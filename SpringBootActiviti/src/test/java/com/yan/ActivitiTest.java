package com.yan;


import com.yan.tool.JsonUtil;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.DelegationState;
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
import java.util.Objects;
import java.util.stream.Collectors;

@SpringBootTest
class ActivitiTest {

    private static final Logger LOG = LoggerFactory.getLogger(ActivitiTest.class);

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;

    private static Map<String, List<String>> ROLE_PERSON_MAP = new HashMap<>();

    static {
        ROLE_PERSON_MAP.put("173474798000001", List.of("zhangsan", "lisi"));
        ROLE_PERSON_MAP.put("173474798000002", List.of("wangwu", "zhaoliu"));
        ROLE_PERSON_MAP.put("173474798000003", List.of("lily", "lucy"));
        ROLE_PERSON_MAP.put("173474767400001", List.of("lilei", "hanmeimei"));
    }

    /**
     * 部署流程
     */
    @Test
    void testDeploy() {
        DeploymentBuilder builder = repositoryService.createDeployment();
        builder.addClasspathResource("bpmn/test/limit_carry.bpmn");
        builder.name("请假流程");
        Deployment deployment = builder.deploy();
        Assertions.assertNotNull(deployment);
        //输出部署信息
        System.out.println("流程部署id：" + deployment.getId());
        System.out.println("流程部署名称：" + deployment.getName());
    }

    @Test
    void testListProcess() {
        List<ProcessDefinition> definitionList = repositoryService.createProcessDefinitionQuery()
                .latestVersion() // 只查询最新版本
                .list()
                .stream()
                .distinct() // 去重
                .collect(Collectors.toList());
        for (ProcessDefinition processDefinition : definitionList) {
            System.out.println(processDefinition.getKey());
        }
    }

    @Test
    void testStartProcess() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", "张三");
        variables.put("reason", "生病");
        // overweight
        // variables.put("applyRole", "173474798000002");
        // goodsItem
        // variables.put("applyRole", "173474798000001");
        // variables.put("roleList", List.of("173474798000002", "173474798000003"));

        // overweight_carry
        // variables.put("applyRole", "173474798000002");
        // limit_carry
        variables.put("applyRole", "173474798000001");
        variables.put("regionRole", "173474798000002");
        variables.put("regionManagerRole", "173474798000003");
        variables.put("explosive", 1);
        variables.put("white", 1);


        String businessKey = "1";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("limt_carry", businessKey, variables);
        String id = processInstance.getId();
        System.out.println(id);
        System.out.println(processInstance.getProcessInstanceId());
        // cbafbeba-c000-11ef-a193-6a489d4b725d
        // 20a30ddd-c009-11ef-8a31-de8eda465ada
        // 7035a936-c00c-11ef-a864-de8eda465ada
        // 1ecd3f55-c00d-11ef-bcd3-de8eda465ada
        // c982d1ce-c013-11ef-a092-0291d84cea55

        // 47dfc243-c014-11ef-8314-0291d84cea55
        // a4cf4316-c014-11ef-aa39-0291d84cea55
    }

    /**
     * 查询进行的流程参数
     */
    @Test
    void testGetProcessVariables() {
        //
        Map<String, Object> variables = runtimeService.getVariables("238e6388-bf69-11ef-8677-72933e81e180");
        System.out.println(JsonUtil.toJsonString(variables));
    }

    /**
     * 查询流程已完成列表
     */
    @Test
    void testListTask() {
        List<Task> list = taskService.createTaskQuery().processInstanceId("238e6388-bf69-11ef-8677-72933e81e180").includeProcessVariables().includeTaskLocalVariables().list();
        list.forEach(e -> {
            Map<String, Object> variables = e.getProcessVariables();
            Map<String, Object> taskLocalVariables = e.getTaskLocalVariables();
            System.out.println(e.getAssignee() + "执行:" + e.getName() + "参数:" + JsonUtil.toJsonString(variables) + " " + JsonUtil.toJsonString(taskLocalVariables));
        });
    }

    /**
     * 查询个人任务
     */
    @Test
    void testGetTestByAssignee() {
        String name = "lily";
        String role = getRole(name);
        // 查询指定用户的个人任务
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(role) // 设置任务处理人
                .list();
        for (Task task : tasks) {
            System.out.println(task.getProcessInstanceId() + "下的任务id:" + task.getId() + " 名字：" + task.getName());
        }
    }

    @Test
    void testCompleteTask() {
        String taskId = "c48f2963-c014-11ef-8a9f-0291d84cea55";
        Map<String, Object> var = Map.of("say", "同意");
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (Objects.isNull(task)) {
            return;
        }
        if (task.getDelegationState() != null && task.getDelegationState().equals(DelegationState.PENDING)) {
            taskService.resolveTask(taskId);
        } else {
            taskService.complete(taskId, var);
        }
    }

    /**
     * 加签
     */
    @Test
    void testAddTask() {
        taskService.delegateTask("75e94da3-bf69-11ef-a426-72933e81e180", "wangwu");
    }

    /**
     * 查询完成的历史任务
     */
    @Test
    void testListProcessTask() {
        // 查询已完成的流程实例
        List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId("a4cf4316-c014-11ef-aa39-0291d84cea55")
                .finished()  // 只查询已完成的任务
                .orderByHistoricTaskInstanceEndTime().asc()  // 按结束时间排序
                .list();

        // 输出查询到的已完成历史任务信息
        for (HistoricTaskInstance task : tasks) {
            System.out.println("Task ID: " + task.getId());
            System.out.println("Task Name: " + task.getName());
            System.out.println("Assignee: " + task.getAssignee());
            System.out.println("Start Time: " + task.getStartTime());
            System.out.println("End Time: " + task.getEndTime());
            System.out.println("Duration: " + task.getDurationInMillis());
            System.out.println("------------------------------------------------");
        }
    }

    @Test
    void testTrans() {
        String taskId = "a1e638d4-bf6b-11ef-9b60-72933e81e180";
        taskService.setAssignee(taskId, "wangwu");
    }

    @Test
    void testProcessFinish() {
        String processInstanceId = "8df73943-bf6b-11ef-8b42-72933e81e180";
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        if (processInstance != null) {
            // 流程仍在运行
            System.out.println("false");
            return;
        }
        // 如果运行时不存在，查询历史数据确认是否已结束
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        if (historicProcessInstance == null) {
            throw new IllegalArgumentException("未找到流程实例: " + processInstanceId);
        }
        // 如果历史记录中 endTime 不为 null，说明流程已结束
        System.out.println(historicProcessInstance.getEndTime() != null);
    }

    private String getRole(String name) {
        for (Map.Entry<String, List<String>> entry : ROLE_PERSON_MAP.entrySet()) {
            if (entry.getValue().contains(name)) {
                return entry.getKey();
            }
        }
        throw new RuntimeException("未配置角色任务");
    }


}

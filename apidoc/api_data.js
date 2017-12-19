define({ "api": [  {    "type": "delete",    "url": "/v1/list/{uuid}",    "title": "删除列表",    "version": "0.1.0",    "group": "list",    "filename": "src/main/java/wowjoy/fruits/ms/controller/ListController.java",    "groupTitle": "list",    "name": "DeleteV1ListUuid"  },  {    "type": "post",    "url": "/v1/list/project",    "title": "添加【项目】列表",    "version": "0.1.0",    "group": "list",    "examples": [      {        "title": "项目添加示例",        "content": "\n{\n    \"title\":\"测试列表添加日志记录功能\",\n    \"description\":\"测试列表添加日志记录功能\",\n    \"projectRelation\":{\"ADD\":[\"e41e0c03ee704b31b56f2ec1076609b5\"]}\n}",        "type": "json"      }    ],    "filename": "src/main/java/wowjoy/fruits/ms/controller/ListController.java",    "groupTitle": "list",    "name": "PostV1ListProject"  },  {    "type": "put",    "url": "/v1/list/{uuid}",    "title": "修改列表",    "version": "0.1.0",    "group": "list",    "filename": "src/main/java/wowjoy/fruits/ms/controller/ListController.java",    "groupTitle": "list",    "name": "PutV1ListUuid"  },  {    "type": "delete",    "url": "/v1/notepad/{uuid}",    "title": "删除日报",    "version": "0.1.0",    "group": "notepad",    "filename": "src/main/java/wowjoy/fruits/ms/controller/NotepadController.java",    "groupTitle": "notepad",    "name": "DeleteV1NotepadUuid"  },  {    "type": "get",    "url": "/v1/notepad",    "title": "查看日报（当前登录用户）",    "version": "0.1.0",    "group": "notepad",    "filename": "src/main/java/wowjoy/fruits/ms/controller/NotepadController.java",    "groupTitle": "notepad",    "name": "GetV1Notepad"  },  {    "type": "get",    "url": "/v1/notepad/team/{teamId}",    "title": "查看日报（团队视角）",    "version": "0.1.0",    "group": "notepad",    "filename": "src/main/java/wowjoy/fruits/ms/controller/NotepadController.java",    "groupTitle": "notepad",    "name": "GetV1NotepadTeamTeamid"  },  {    "type": "post",    "url": "/v1/notepad",    "title": "添加日报",    "version": "0.1.0",    "group": "notepad",    "examples": [      {        "title": "添加日报",        "content": "{\n        \"content\":\"今天我很生气，我要吃鸡\",\n        \"notepadDate\":\"2017-10-20\"\n        }",        "type": "json"      }    ],    "filename": "src/main/java/wowjoy/fruits/ms/controller/NotepadController.java",    "groupTitle": "notepad",    "name": "PostV1Notepad"  },  {    "type": "put",    "url": "/v1/notepad/{uuid}",    "title": "修改日报",    "version": "0.1.0",    "group": "notepad",    "filename": "src/main/java/wowjoy/fruits/ms/controller/NotepadController.java",    "groupTitle": "notepad",    "name": "PutV1NotepadUuid"  },  {    "type": "get",    "url": "/v1/plan/project/{uuid}",    "title": "计划综合查询【项目查询】",    "version": "0.1.0",    "group": "plan",    "filename": "src/main/java/wowjoy/fruits/ms/controller/PlanController.java",    "groupTitle": "plan",    "name": "GetV1PlanProjectUuid"  },  {    "type": "get",    "url": "/v1/plan/{uuid}",    "title": "查询计划详情",    "version": "0.1.0",    "group": "plan",    "filename": "src/main/java/wowjoy/fruits/ms/controller/PlanController.java",    "groupTitle": "plan",    "name": "GetV1PlanUuid"  },  {    "type": "get",    "url": "/v1/plan/{year}",    "title": "查询某年的年周对照表",    "version": "0.1.0",    "group": "plan",    "filename": "src/main/java/wowjoy/fruits/ms/controller/PlanController.java",    "groupTitle": "plan",    "name": "GetV1PlanYear"  },  {    "type": "post",    "url": "/v1/plan/project/{uuid}",    "title": "【项目】计划添加",    "version": "0.1.0",    "group": "plan",    "filename": "src/main/java/wowjoy/fruits/ms/controller/PlanController.java",    "groupTitle": "plan",    "name": "PostV1PlanProjectUuid"  },  {    "type": "put",    "url": "/v1/plan/complete/{uuid}",    "title": "修改状态【完成】",    "version": "0.1.0",    "group": "plan",    "filename": "src/main/java/wowjoy/fruits/ms/controller/PlanController.java",    "groupTitle": "plan",    "name": "PutV1PlanCompleteUuid"  },  {    "type": "put",    "url": "/v1/plan/end/{uuid}",    "title": "修改状态【终止】",    "version": "0.1.0",    "group": "plan",    "filename": "src/main/java/wowjoy/fruits/ms/controller/PlanController.java",    "groupTitle": "plan",    "name": "PutV1PlanEndUuid"  },  {    "type": "put",    "url": "/v1/plan/summary/{uuid}",    "title": "添加进度小结",    "version": "0.1.0",    "group": "plan",    "filename": "src/main/java/wowjoy/fruits/ms/controller/PlanController.java",    "groupTitle": "plan",    "name": "PutV1PlanSummaryUuid"  },  {    "type": "put",    "url": "/v1/plan/{uuid}",    "title": "计划修改接口",    "version": "0.1.0",    "group": "plan",    "filename": "src/main/java/wowjoy/fruits/ms/controller/PlanController.java",    "groupTitle": "plan",    "name": "PutV1PlanUuid"  },  {    "type": "put",    "url": "/v1/plan/{uuid}",    "title": "删除计划",    "version": "0.1.0",    "group": "plan",    "filename": "src/main/java/wowjoy/fruits/ms/controller/PlanController.java",    "groupTitle": "plan",    "name": "PutV1PlanUuid"  },  {    "type": "delete",    "url": "/v1/project/{uuid}",    "title": "删除项目",    "version": "0.1.0",    "group": "project",    "filename": "src/main/java/wowjoy/fruits/ms/controller/ProjectController.java",    "groupTitle": "project",    "name": "DeleteV1ProjectUuid"  },  {    "type": "get",    "url": "/v1/project/current",    "title": "项目查询【列表，当前用户关联项目】",    "version": "0.1.0",    "group": "project",    "filename": "src/main/java/wowjoy/fruits/ms/controller/ProjectController.java",    "groupTitle": "project",    "name": "GetV1ProjectCurrent"  },  {    "type": "get",    "url": "/v1/project/relation",    "title": "项目查询【列表】",    "version": "0.1.0",    "group": "project",    "filename": "src/main/java/wowjoy/fruits/ms/controller/ProjectController.java",    "groupTitle": "project",    "name": "GetV1ProjectRelation"  },  {    "type": "get",    "url": "/v1/project/user/{uuid}",    "title": "根据项目id，查询用户信息",    "version": "0.1.0",    "group": "project",    "filename": "src/main/java/wowjoy/fruits/ms/controller/ProjectController.java",    "groupTitle": "project",    "name": "GetV1ProjectUserUuid"  },  {    "type": "get",    "url": "/v1/project/{uuid}",    "title": "项目查询【详情】",    "version": "0.1.0",    "group": "project",    "filename": "src/main/java/wowjoy/fruits/ms/controller/ProjectController.java",    "groupTitle": "project",    "name": "GetV1ProjectUuid"  },  {    "type": "post",    "url": "/v1/project",    "title": "项目添加",    "version": "0.1.0",    "group": "project",    "filename": "src/main/java/wowjoy/fruits/ms/controller/ProjectController.java",    "groupTitle": "project",    "name": "PostV1Project"  },  {    "type": "put",    "url": "/v1/project/complete/{uuid}",    "title": "项目状态【完成】",    "version": "0.1.0",    "group": "project",    "filename": "src/main/java/wowjoy/fruits/ms/controller/ProjectController.java",    "groupTitle": "project",    "name": "PutV1ProjectCompleteUuid"  },  {    "type": "put",    "url": "/v1/project/{uuid}",    "title": "项目修改",    "version": "0.1.0",    "group": "project",    "filename": "src/main/java/wowjoy/fruits/ms/controller/ProjectController.java",    "groupTitle": "project",    "name": "PutV1ProjectUuid"  },  {    "type": "delete",    "url": "/v1/task/{uuid}",    "title": "删除任务",    "version": "0.1.0",    "group": "task",    "description": "<p>1、完成删除任务 2、完成删除关联用户 3、完成删除关联计划 4、完成删除关联项目</p>",    "filename": "src/main/java/wowjoy/fruits/ms/controller/TaskController.java",    "groupTitle": "task",    "name": "DeleteV1TaskUuid"  },  {    "type": "get",    "url": "/v1/task/current",    "title": "查询当前登录用户的所有任务列表",    "version": "0.1.0",    "group": "task",    "filename": "src/main/java/wowjoy/fruits/ms/controller/TaskController.java",    "groupTitle": "task",    "name": "GetV1TaskCurrent"  },  {    "type": "get",    "url": "/v1/task/current_create",    "title": "查询当前登录用户创建的任务",    "version": "0.1.0",    "group": "task",    "filename": "src/main/java/wowjoy/fruits/ms/controller/TaskController.java",    "groupTitle": "task",    "name": "GetV1TaskCurrent_create"  },  {    "type": "get",    "url": "/v1/task/list/{uuid}",    "title": "根据指定列表id，查询对应的任务列表下所有任务",    "version": "0.1.0",    "group": "task",    "filename": "src/main/java/wowjoy/fruits/ms/controller/TaskController.java",    "groupTitle": "task",    "name": "GetV1TaskListUuid"  },  {    "type": "get",    "url": "/v1/task/plan/{uuid}",    "title": "根据指定的计划id，查询计划对应的任务",    "version": "0.1.0",    "group": "task",    "filename": "src/main/java/wowjoy/fruits/ms/controller/TaskController.java",    "groupTitle": "task",    "name": "GetV1TaskPlanUuid"  },  {    "type": "get",    "url": "/v1/task/project/{uuid}",    "title": "根据指定项目id查询对应的任务列表",    "version": "0.1.0",    "group": "task",    "filename": "src/main/java/wowjoy/fruits/ms/controller/TaskController.java",    "groupTitle": "task",    "name": "GetV1TaskProjectUuid"  },  {    "type": "post",    "url": "/v1/task",    "title": "添加任务",    "version": "0.1.0",    "group": "task",    "parameter": {      "examples": [        {          "title": "关联计划:",          "content": "{\n\"description\":\"2017年11月15日10:35:55：测试任务添加-计划\",\n\"estimatedEndDate\":\"2017-11-15\",\n\"title\":\"测试任务添加-计划\",\n\"taskLevel\":\"LOW\",\n\"userRelation\":{\n\"ADD\":[{\n\"userId\":\"fbdebd622b75404a9258e6ddd0c13a79\"\n}]\n},\n\"listRelation\":{\n\"ADD\":[{\n\"listId\":\"6c59f8d69a27406c835f7a8f0d44a71f\"\n}]\n},\"planRelation\":{\n\"ADD\":[{\n\"planId\":\"963b729b7677406bbc3aa7eac2f58b19\"\n}]\n}\n}",          "type": "json"        },        {          "title": "关联项目:",          "content": "{\n\"description\":\"2017年11月15日10:35:55：测试任务添加-计划\",\n\"estimatedEndDate\":\"2017-11-15\",\n\"title\":\"测试任务添加-计划\",\n\"taskLevel\":\"LOW\",\n\"userRelation\":{\n\"ADD\":[{\n\"userId\":\"fbdebd622b75404a9258e6ddd0c13a79\"\n}]\n},\n\"listRelation\":{\n\"ADD\":[{\n\"listId\":\"6c59f8d69a27406c835f7a8f0d44a71f\"\n}]\n},\"projectRelation\":{\n\"ADD\":[{\n\"projectId\":\"5db11c2ee68e49208c368a9a670a7bbb\"\n}]\n}\n}",          "type": "json"        }      ]    },    "filename": "src/main/java/wowjoy/fruits/ms/controller/TaskController.java",    "groupTitle": "task",    "name": "PostV1Task"  },  {    "type": "put",    "url": "/v1/task/end/{uuid}",    "title": "变更任务状态【结束】",    "version": "0.1.0",    "group": "task",    "filename": "src/main/java/wowjoy/fruits/ms/controller/TaskController.java",    "groupTitle": "task",    "name": "PutV1TaskEndUuid"  },  {    "type": "put",    "url": "/v1/task/list/{uuid}",    "title": "改变当前任务所在列表",    "version": "0.1.0",    "group": "task",    "filename": "src/main/java/wowjoy/fruits/ms/controller/TaskController.java",    "groupTitle": "task",    "name": "PutV1TaskListUuid"  },  {    "type": "put",    "url": "/v1/task/start/{uuid}",    "title": "变更任务状态【开始】",    "version": "0.1.0",    "group": "task",    "filename": "src/main/java/wowjoy/fruits/ms/controller/TaskController.java",    "groupTitle": "task",    "name": "PutV1TaskStartUuid"  },  {    "type": "put",    "url": "/v1/task/{uuid}",    "title": "修改任务",    "version": "0.1.0",    "group": "task",    "filename": "src/main/java/wowjoy/fruits/ms/controller/TaskController.java",    "groupTitle": "task",    "name": "PutV1TaskUuid"  },  {    "type": "delete",    "url": "/v1/team/{uuid}",    "title": "团队删除",    "version": "0.1.0",    "group": "team",    "filename": "src/main/java/wowjoy/fruits/ms/controller/TeamController.java",    "groupTitle": "team",    "name": "DeleteV1TeamUuid"  },  {    "type": "get",    "url": "/v1/team/current",    "title": "团队信息查询【当前用户】",    "version": "0.1.0",    "group": "team",    "filename": "src/main/java/wowjoy/fruits/ms/controller/TeamController.java",    "groupTitle": "team",    "name": "GetV1TeamCurrent"  },  {    "type": "get",    "url": "/v1/team/relation",    "title": "团队信息查询",    "version": "0.1.0",    "group": "team",    "filename": "src/main/java/wowjoy/fruits/ms/controller/TeamController.java",    "groupTitle": "team",    "name": "GetV1TeamRelation"  },  {    "type": "get",    "url": "/v1/team/{uuid}",    "title": "团队详情查询",    "version": "0.1.0",    "group": "team",    "filename": "src/main/java/wowjoy/fruits/ms/controller/TeamController.java",    "groupTitle": "team",    "name": "GetV1TeamUuid"  },  {    "type": "post",    "url": "/v1/team",    "title": "团队添加",    "version": "0.1.0",    "group": "team",    "filename": "src/main/java/wowjoy/fruits/ms/controller/TeamController.java",    "groupTitle": "team",    "name": "PostV1Team"  },  {    "type": "put",    "url": "/v1/team/{uuid}",    "title": "团队修改",    "version": "0.1.0",    "group": "team",    "filename": "src/main/java/wowjoy/fruits/ms/controller/TeamController.java",    "groupTitle": "team",    "name": "PutV1TeamUuid"  }] });

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>厦门大学嘉庚学院专升本备考计划 - 甘特图</title>
    <script src="https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js"></script>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', 'Microsoft YaHei', 'PingFang SC', sans-serif;
            background: linear-gradient(135deg, #0f1b3a 0%, #2c3e50 50%, #4a6491 100%);
            min-height: 100vh;
            padding: 20px;
            color: #e0e7ff;
        }
        
        .container {
            max-width: 1400px;
            margin: 0 auto;
            background: rgba(255, 255, 255, 0.08);
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
            border-radius: 20px;
            border: 1px solid rgba(255, 255, 255, 0.18);
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.25);
            overflow: hidden;
        }
        
        .header {
            background: linear-gradient(135deg, rgba(52, 152, 219, 0.3) 0%, rgba(41, 128, 185, 0.3) 100%);
            color: white;
            padding: 30px;
            text-align: center;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }
        
        .title {
            font-size: 32px;
            font-weight: 600;
            margin-bottom: 10px;
            text-shadow: 0 2px 4px rgba(0,0,0,0.2);
        }
        
        .subtitle {
            font-size: 16px;
            opacity: 0.85;
            letter-spacing: 0.5px;
        }
        
        .stats-container {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            padding: 30px;
            background: rgba(255, 255, 255, 0.05);
        }
        
        .stat-card {
            background: rgba(255, 255, 255, 0.1);
            border-radius: 15px;
            padding: 25px;
            text-align: center;
            transition: all 0.3s ease;
            border: 1px solid rgba(255, 255, 255, 0.1);
            box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
        }
        
        .stat-card:hover {
            transform: translateY(-5px);
            background: rgba(255, 255, 255, 0.15);
            box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
        }
        
        .stat-value {
            font-size: 36px;
            font-weight: 700;
            margin-bottom: 5px;
            color: #fff;
            text-shadow: 0 2px 4px rgba(0,0,0,0.2);
        }
        
        .stat-label {
            font-size: 14px;
            color: #b8c5d6;
            letter-spacing: 0.5px;
        }
        
        .chart-container {
            padding: 30px;
            position: relative;
        }
        
        #ganttChart {
            width: 100%;
            height: 700px;
            border-radius: 10px;
        }
        
        .controls {
            display: flex;
            justify-content: center;
            gap: 15px;
            margin-bottom: 20px;
            flex-wrap: wrap;
        }
        
        .control-btn {
            padding: 10px 20px;
            border: 1px solid rgba(74, 100, 145, 0.5);
            background: rgba(74, 100, 145, 0.3);
            border-radius: 25px;
            cursor: pointer;
            transition: all 0.3s ease;
            font-size: 14px;
            color: #e0e7ff;
            font-weight: 500;
        }
        
        .control-btn.active {
            background: rgba(41, 128, 185, 0.6);
            color: white;
            border-color: rgba(52, 152, 219, 0.8);
            box-shadow: 0 0 15px rgba(52, 152, 219, 0.4);
        }
        
        .control-btn:hover {
            background: rgba(41, 128, 185, 0.5);
            transform: translateY(-2px);
            color: white;
        }
        
        .legend {
            display: flex;
            justify-content: center;
            gap: 30px;
            margin-top: 20px;
            padding: 20px;
            background: rgba(255, 255, 255, 0.05);
            border-radius: 10px;
            flex-wrap: wrap;
        }
        
        .legend-item {
            display: flex;
            align-items: center;
            gap: 8px;
        }
        
        .legend-color {
            width: 20px;
            height: 12px;
            border-radius: 3px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.2);
        }
        
        .subject-colors {
            display: flex;
            justify-content: center;
            gap: 20px;
            margin: 20px 0;
            flex-wrap: wrap;
        }
        
        .subject-item {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 8px 15px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 20px;
            border: 1px solid rgba(255, 255, 255, 0.1);
        }
        
        .subject-color {
            width: 12px;
            height: 12px;
            border-radius: 50%;
        }
        
        .progress-info {
            text-align: center;
            margin: 20px 0;
            font-size: 16px;
            color: #b8c5d6;
        }
        
        .progress-bar {
            width: 100%;
            height: 8px;
            background: rgba(255,255,255,0.1);
            border-radius: 4px;
            overflow: hidden;
            margin-top: 10px;
        }
        
        .progress-fill {
            height: 100%;
            background: linear-gradient(90deg, #3498db, #2ecc71);
            border-radius: 4px;
            transition: width 0.5s ease;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1 class="title">📚 厦门大学嘉庚学院专升本备考计划</h1>
            <p class="subtitle">可视化学习进度管理系统</p>
        </div>
        
        <div class="stats-container">
            <div class="stat-card">
                <div class="stat-value">366</div>
                <div class="stat-label">总天数</div>
            </div>
            <div class="stat-card">
                <div class="stat-value">7</div>
                <div class="stat-label">每日学习(小时)</div>
            </div>
            <div class="stat-card">
                <div class="stat-value">364</div>
                <div class="stat-label">剩余天数</div>
            </div>
            <div class="stat-card">
                <div class="stat-value">1.5%</div>
                <div class="stat-label">当前进度</div>
            </div>
        </div>
        
        <div class="chart-container">
            <div class="subject-colors">
                <div class="subject-item">
                    <div class="subject-color" style="background: #e74c3c;"></div>
                    <span>思政理论</span>
                </div>
                <div class="subject-item">
                    <div class="subject-color" style="background: #3498db;"></div>
                    <span>信息技术基础</span>
                </div>
                <div class="subject-item">
                    <div class="subject-color" style="background: #2ecc71;"></div>
                    <span>大学英语</span>
                </div>
                <div class="subject-item">
                    <div class="subject-color" style="background: #f39c12;"></div>
                    <span>高等数学</span>
                </div>
            </div>
            
            <div class="controls">
                <button class="control-btn active" data-filter="all">全部科目</button>
                <button class="control-btn" data-filter="思政理论">思政理论</button>
                <button class="control-btn" data-filter="信息技术基础">信息技术基础</button>
                <button class="control-btn" data-filter="大学英语">大学英语</button>
                <button class="control-btn" data-filter="高等数学">高等数学</button>
            </div>
            
            <div class="progress-info">
                <div>总体进度：<strong>1.5%</strong> (第1-69天数据)</div>
                <div class="progress-bar">
                    <div class="progress-fill" style="width: 1.5%;"></div>
                </div>
            </div>
            
            <div id="ganttChart"></div>
            
            <div class="legend">
                <div class="legend-item">
                    <div class="legend-color" style="background: #27ae60;"></div>
                    <span>✅ 已完成</span>
                </div>
                <div class="legend-item">
                    <div class="legend-color" style="background: #3498db;"></div>
                    <span>📝 进行中</span>
                </div>
                <div class="legend-item">
                    <div class="legend-color" style="background: #95a5a6;"></div>
                    <span>⏳ 未开始</span>
                </div>
            </div>
        </div>
    </div>

    <script>
        // 原始数据解析
        const rawData = `1	1	思政理论	担当复兴大任 成就时代新人	100%	已完成
1	2	信息技术基础	WPS文字高级排版	50%	已完成
2	1	大学英语	议论文写作模板与句型	50%	已完成
2	2	大学英语	议论文写作模板与句型	67%	未开始
3	1	大学英语	议论文写作模板与句型	75%	未开始
3	2	高等数学	函数的概念与性质	50%	未开始
4	1	思政理论	复习:担当复兴大任 成就时代新人	0%	未开始
4	2	信息技术基础	WPS文字高级排版	100%	未开始
4	3	思政理论	遵守道德规范 锤炼道德品格	50%	未开始
5	1	高等数学	函数的概念与性质	100%	未开始
5	2	思政理论	遵守道德规范 锤炼道德品格	100%	未开始
5	3	大学英语	议论文写作模板与句型	100%	未开始
6	1	大学英语	英译汉基本方法与技巧	50%	未开始
6	2	大学英语	英译汉基本方法与技巧	67%	未开始
7	1	信息技术基础	复习:WPS文字高级排版	0%	未开始
7	2	大学英语	英译汉基本方法与技巧	100%	未开始
7	3	信息技术基础	Python基本语法与数据类型	50%	未开始
8	1	思政理论	复习:担当复兴大任 成就时代新人	0%	未开始
8	2	信息技术基础	Python基本语法与数据类型	67%	未开始
8	3	信息技术基础	Python基本语法与数据类型	75%	未开始
9	1	高等数学	复习:函数的概念与性质	0%	未开始
9	2	信息技术基础	Python基本语法与数据类型	80%	未开始
10	1	大学英语	复习:英译汉基本方法与技巧	0%	未开始
10	2	高等数学	导数与微分的概念与计算	50%	未开始
11	1	信息技术基础	复习:WPS文字高级排版	0%	未开始
11	2	信息技术基础	Python基本语法与数据类型	100%	未开始
11	3	信息技术基础	计算机硬件组成与工作原理	50%	未开始
12	1	高等数学	复习:函数的概念与性质	0%	未开始
12	2	信息技术基础	计算机硬件组成与工作原理	67%	未开始
13	1	思政理论	复习:遵守道德规范 锤炼道德品格	0%	未开始
13	2	高等数学	导数与微分的概念与计算	67%	未开始
14	1	大学英语	复习:英译汉基本方法与技巧	0%	未开始
14	2	信息技术基础	计算机硬件组成与工作原理	100%	未开始
14	3	信息技术基础	操作系统基本功能	50%	未开始
15	1	思政理论	复习:担当复兴大任 成就时代新人	0%	未开始
15	2	信息技术基础	操作系统基本功能	67%	未开始
15	3	高等数学	导数与微分的概念与计算	75%	未开始
16	1	信息技术基础	复习:Python基本语法与数据类型	0%	未开始
16	2	信息技术基础	操作系统基本功能	100%	未开始
17	1	信息技术基础	复习:计算机硬件组成与工作原理	0%	未开始
17	2	信息技术基础	WPS表格数据处理与分析	50%	未开始
18	1	信息技术基础	复习:WPS文字高级排版	0%	未开始
18	2	信息技术基础	WPS表格数据处理与分析	67%	未开始
19	1	高等数学	复习:函数的概念与性质	0%	未开始
19	2	高等数学	导数与微分的概念与计算	100%	未开始
19	3	信息技术基础	WPS表格数据处理与分析	75%	未开始
20	1	思政理论	复习:遵守道德规范 锤炼道德品格	0%	未开始
20	2	信息技术基础	WPS表格数据处理与分析	100%	未开始
20	3	信息技术基础	简单算法设计与实现	50%	未开始
21	1	大学英语	复习:英译汉基本方法与技巧	0%	未开始
21	2	高等数学	不定积分的概念与计算	50%	未开始
22	1	高等数学	复习:导数与微分的概念与计算	0%	未开始
22	2	信息技术基础	简单算法设计与实现	67%	未开始
23	1	信息技术基础	复习:操作系统基本功能	0%	未开始
23	2	信息技术基础	简单算法设计与实现	75%	未开始
24	1	信息技术基础	复习:WPS表格数据处理与分析	0%	未开始
24	2	信息技术基础	简单算法设计与实现	80%	未开始
25	1	信息技术基础	复习:Python基本语法与数据类型	0%	未开始
25	2	高等数学	不定积分的概念与计算	67%	未开始
26	1	高等数学	复习:导数与微分的概念与计算	0%	未开始
26	2	信息技术基础	简单算法设计与实现	100%	未开始
26	3	高等数学	不定积分的概念与计算	75%	未开始
27	1	信息技术基础	复习:WPS表格数据处理与分析	0%	未开始
27	2	高等数学	不定积分的概念与计算	100%	未开始
27	3	高等数学	极限的计算方法	50%	未开始
28	1	信息技术基础	复习:计算机硬件组成与工作原理	0%	未开始
28	2	高等数学	极限的计算方法	67%	未开始
29	1	信息技术基础	复习:简单算法设计与实现	0%	未开始
29	2	高等数学	极限的计算方法	75%	未开始
30	1	信息技术基础	复习:操作系统基本功能	0%	未开始
30	2	高等数学	极限的计算方法	100%	未开始
30	3	高等数学	微分中值定理与导数的应用	50%	未开始
31	1	思政理论	复习:担当复兴大任 成就时代新人	0%	未开始
31	2	高等数学	微分中值定理与导数的应用	67%	未开始
31	3	高等数学	微分中值定理与导数的应用	75%	未开始
32	1	高等数学	复习:不定积分的概念与计算	0%	未开始
32	2	高等数学	微分中值定理与导数的应用	80%	未开始
33	1	高等数学	复习:导数与微分的概念与计算	0%	未开始
33	2	高等数学	微分中值定理与导数的应用	100%	未开始
33	3	高等数学	定积分的概念与应用	50%	未开始
34	1	信息技术基础	复习:WPS文字高级排版	0%	未开始
34	2	高等数学	定积分的概念与应用	67%	未开始
35	1	高等数学	复习:函数的概念与性质	0%	未开始
35	2	高等数学	定积分的概念与应用	75%	未开始
36	1	高等数学	复习:微分中值定理与导数的应用	0%	未开始
36	2	高等数学	定积分的概念与应用	100%	未开始
37	1	大学英语	复习:英译汉基本方法与技巧	0%	未开始
38	1	高等数学	复习:极限的计算方法	0%	未开始
39	1	高等数学	复习:定积分的概念与应用	0%	未开始
40	1	信息技术基础	复习:简单算法设计与实现	0%	未开始
41	1	信息技术基础	复习:Python基本语法与数据类型	0%	未开始
42	1	高等数学	复习:不定积分的概念与计算	0%	未开始
43	1	高等数学	复习:定积分的概念与应用	0%	未开始
44	1	信息技术基础	复习:计算机硬件组成与工作原理	0%	未开始
45	1	高等数学	复习:极限的计算方法	0%	未开始
46	1	信息技术基础	复习:操作系统基本功能	0%	未开始
47	1	高等数学	复习:微分中值定理与导数的应用	0%	未开始
48	1	高等数学	复习:微分中值定理与导数的应用	0%	未开始
49	1	高等数学	复习:导数与微分的概念与计算	0%	未开始
50	1	信息技术基础	复习:WPS表格数据处理与分析	0%	未开始
51	1	高等数学	复习:定积分的概念与应用	0%	未开始
52	1	思政理论	复习:遵守道德规范 锤炼道德品格	0%	未开始
53	1	大学英语	复习:议论文写作模板与句型	0%	未开始
54	1	信息技术基础	复习:WPS表格数据处理与分析	0%	未开始
55	1	高等数学	复习:不定积分的概念与计算	0%	未开始
56	1	信息技术基础	复习:简单算法设计与实现	0%	未开始
57	1	高等数学	复习:不定积分的概念与计算	0%	未开始
58	1	信息技术基础	复习:简单算法设计与实现	0%	未开始
59	1	高等数学	复习:极限的计算方法	0%	未开始
60	1	高等数学	复习:极限的计算方法	0%	未开始
61	1	信息技术基础	复习:计算机硬件组成与工作原理	0%	未开始
62	1	大学英语	复习:议论文写作模板与句型	0%	未开始
63	1	高等数学	复习:微分中值定理与导数的应用	0%	未开始
64	1	信息技术基础	复习:操作系统基本功能	0%	未开始
65	1	信息技术基础	复习:Python基本语法与数据类型	0%	未开始
66	1	高等数学	复习:定积分的概念与应用	0%	未开始
67	1	大学英语	复习:议论文写作模板与句型	0%	未开始
68	1	思政理论	复习:遵守道德规范 锤炼道德品格	0%	未开始
69	1	大学英语	复习:议论文写作模板与句型	0%	未开始`;

        // 科目颜色映射 - 更新为更鲜艳的色调
        const subjectColors = {
            '思政理论': '#e74c3c',    // 红色
            '信息技术基础': '#3498db', // 蓝色
            '大学英语': '#2ecc71',     // 绿色
            '高等数学': '#f39c12'      // 橙色
        };

        // 状态颜色映射 - 保持原样
        const statusColors = {
            '已完成': '#27ae60',
            '进行中': '#3498db',
            '未开始': '#95a5a6'
        };

        // 解析数据
        function parseData() {
            const lines = rawData.trim().split('\n');
            const tasks = [];
            
            lines.forEach(line => {
                const parts = line.split('\t');
                if (parts.length >= 6) {
                    const day = parseInt(parts[0]);
                    const subject = parts[2];
                    const knowledge = parts[3];
                    const progress = parts[4];
                    const status = parts[5];
                    
                    // 确定状态
                    let taskStatus = status;
                    if (status === '未开始' && progress !== '0%' && progress !== '100%') {
                        taskStatus = '进行中';
                    } else if (status === '未开始' && progress === '100%') {
                        taskStatus = '已完成';
                    }
                    
                    tasks.push({
                        day: day,
                        subject: subject,
                        knowledge: knowledge,
                        progress: progress,
                        status: taskStatus,
                        start: day - 0.5,
                        end: day + 0.5,
                        itemStyle: {
                            color: taskStatus === '已完成' ? statusColors['已完成'] :
                                   taskStatus === '进行中' ? statusColors['进行中'] :
                                   statusColors['未开始']
                        }
                    });
                }
            });
            
            return tasks;
        }

        // 初始化图表
        const chart = echarts.init(document.getElementById('ganttChart'));
        let allTasks = parseData();
        let currentFilter = 'all';

        // 获取唯一科目列表
        const subjects = [...new Set(allTasks.map(task => task.subject))];

        // 创建系列数据
        function createSeriesData(filter = 'all') {
            const filteredTasks = filter === 'all' 
                ? allTasks 
                : allTasks.filter(task => task.subject === filter);
            
            const series = [];
            
            subjects.forEach((subject, subjectIndex) => {
                const subjectTasks = filteredTasks.filter(task => task.subject === subject);
                if (subjectTasks.length > 0) {
                    series.push({
                        name: subject,
                        type: 'custom',
                        renderItem: function(params, api) {
                            const categoryIndex = api.value(0);
                            const start = api.coord([api.value(1), categoryIndex]);
                            const end = api.coord([api.value(2), categoryIndex]);
                            const height = api.size([0, 1])[1] * 0.6;
                            
                            const rectShape = echarts.graphic.clipRectByRect({
                                x: start[0],
                                y: start[1] - height / 2,
                                width: end[0] - start[0],
                                height: height
                            }, {
                                x: params.coordSys.x,
                                y: params.coordSys.y,
                                width: params.coordSys.width,
                                height: params.coordSys.height
                            });
                            
                            return rectShape && {
                                type: 'rect',
                                shape: rectShape,
                                style: api.style({
                                    fill: api.visual('color'),
                                    stroke: subjectColors[subject],
                                    lineWidth: 2
                                }),
                                emphasis: {
                                    style: {
                                        shadowBlur: 10,
                                        shadowColor: 'rgba(0,0,0,0.3)'
                                    }
                                }
                            };
                        },
                        encode: {
                            x: [1, 2],
                            y: 0
                        },
                        data: subjectTasks.map(task => ({
                            value: [
                                subjectIndex,
                                task.start,
                                task.end,
                                task.knowledge
                            ],
                            itemStyle: {
                                color: task.status === '已完成' ? statusColors['已完成'] :
                                       task.status === '进行中' ? statusColors['进行中'] :
                                       statusColors['未开始']
                            },
                            taskData: task
                        }))
                    });
                }
            });
            
            return series;
        }

        // 图表配置
        function getOption(filter = 'all') {
            const filteredTasks = filter === 'all' 
                ? allTasks 
                : allTasks.filter(task => task.subject === filter);
            
            const filteredSubjects = [...new Set(filteredTasks.map(task => task.subject))];
            
            return {
                backgroundColor: 'transparent',
                title: {
                    text: '学习进度甘特图',
                    subtext: `显示第1-69天计划 (${filteredTasks.length}个任务)`,
                    left: 'center',
                    textStyle: {
                        color: '#fff',
                        fontSize: 20,
                        fontWeight: 'bold'
                    },
                    subtextStyle: {
                        color: '#b8c5d6',
                        fontSize: 14
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(30, 40, 60, 0.9)',
                    borderColor: 'rgba(74, 100, 145, 0.5)',
                    borderWidth: 1,
                    textStyle: {
                        color: '#e0e7ff',
                        fontSize: 13
                    },
                    formatter: function(params) {
                        const task = params.data.taskData;
                        return `
                            <div style="padding: 10px; max-width: 300px;">
                                <div style="font-weight: bold; margin-bottom: 8px; font-size: 14px; color: #fff;">
                                    ${task.knowledge}
                                </div>
                                <div style="margin-bottom: 4px;">
                                    <span style="color: #b8c5d6;">科目：</span>
                                    <span style="color: ${subjectColors[task.subject]}; font-weight: 500;">${task.subject}</span>
                                </div>
                                <div style="margin-bottom: 4px;">
                                    <span style="color: #b8c5d6;">天数：</span>
                                    <span style="color: #fff;">第${task.day}天</span>
                                </div>
                                <div style="margin-bottom: 4px;">
                                    <span style="color: #b8c5d6;">进度：</span>
                                    <span style="color: #fff;">${task.progress}</span>
                                </div>
                                <div style="margin-bottom: 4px;">
                                    <span style="color: #b8c5d6;">状态：</span>
                                    <span style="color: ${task.status === '已完成' ? '#2ecc71' : task.status === '进行中' ? '#3498db' : '#95a5a6'}; font-weight: 500;">
                                        ${task.status}
                                    </span>
                                </div>
                            </div>
                        `;
                    }
                },
                grid: {
                    left: '15%',
                    right: '5%',
                    top: '15%',
                    bottom: '15%'
                },
                xAxis: {
                    type: 'value',
                    name: '天数',
                    nameLocation: 'middle',
                    nameGap: 30,
                    min: 1,
                    max: 70,
                    interval: 5,
                    axisLine: {
                        lineStyle: {
                            color: 'rgba(255, 255, 255, 0.3)'
                        }
                    },
                    axisLabel: {
                        color: '#b8c5d6',
                        formatter: '第{value}天'
                    },
                    nameTextStyle: {
                        color: '#b8c5d6'
                    },
                    splitLine: {
                        lineStyle: {
                            color: 'rgba(255, 255, 255, 0.1)'
                        }
                    }
                },
                yAxis: {
                    type: 'category',
                    data: filteredSubjects,
                    axisLine: {
                        lineStyle: {
                            color: 'rgba(255, 255, 255, 0.3)'
                        }
                    },
                    axisLabel: {
                        color: '#b8c5d6',
                        formatter: function(value) {
                            return value;
                        }
                    },
                    axisTick: {
                        alignWithLabel: true,
                        lineStyle: {
                            color: 'rgba(255, 255, 255, 0.1)'
                        }
                    }
                },
                series: createSeriesData(filter),
                dataZoom: [
                    {
                        type: 'slider',
                        xAxisIndex: 0,
                        start: 0,
                        end: 100,
                        height: 30,
                        bottom: 30,
                        handleStyle: {
                            color: '#3498db'
                        },
                        backgroundColor: 'rgba(255,255,255,0.1)',
                        borderColor: 'rgba(255,255,255,0.1)',
                        fillerColor: 'rgba(52, 152, 219, 0.3)',
                        textStyle: {
                            color: '#e0e7ff'
                        }
                    },
                    {
                        type: 'inside',
                        xAxisIndex: 0,
                        start: 0,
                        end: 100
                    }
                ],
                animation: true,
                animationDuration: 1000,
                animationEasing: 'cubicOut'
            };
        }

        // 设置图表选项
        chart.setOption(getOption());

        // 响应式处理
        window.addEventListener('resize', function() {
            chart.resize();
        });

        // 筛选按钮事件
        document.querySelectorAll('.control-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                document.querySelectorAll('.control-btn').forEach(b => b.classList.remove('active'));
                this.classList.add('active');
                
                currentFilter = this.dataset.filter;
                chart.setOption(getOption(currentFilter));
            });
        });

        // 图表点击事件
        chart.on('click', function(params) {
            if (params.data && params.data.taskData) {
                const task = params.data.taskData;
                // 可以添加更多交互功能，比如标记完成、添加笔记等
                console.log('点击了任务：', task);
            }
        });
    </script>
</body>
</html>
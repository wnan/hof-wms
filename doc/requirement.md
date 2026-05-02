# 需求文档
开发一个系统包含进，销，存，数据对接，报表模块
## 需求列表
进，销，存：
设计成通用的模块，实现进销存的基本功能
数据对接模块：
实现对接外部系统的数据，实现数据的同步。要支持增量和全量同步，并且要支持定时同步和手动同步两种方式
报表模块：
实现报表的生成，并提供下载功能，能实现和AI的对接，提供智能分析和预测功能

# 技术选型
## 技术选型
技术选型：
1. 前端：Vue3 + Vite + TypeScript + Element Plus
2. 后端：java，springboot，springcloud，mybatis-plus
3. 数据库：Postgres
4. 部署：Docker
5. 测试：junit
6. 缓存：redis
7. 全文索引：elasticsearch

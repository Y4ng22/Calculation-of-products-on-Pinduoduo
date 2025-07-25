# 表数据操作功能使用指南

## 概述

`MetadataManagement.vue` 页面现在支持对数据库表数据进行完整的增删改查操作。用户可以直接在界面上查看、编辑、删除和新增表记录。

## 功能特性

### 1. 数据查看
- 分页显示表数据
- 支持自定义每页显示数量（10/20/50/100条）
- 实时显示总记录数和当前页信息
- 支持数据刷新功能

### 2. 数据编辑
- 点击编辑按钮打开编辑模态框
- 支持修改所有字段（除主键外）
- 主键字段（如id）为只读状态
- 实时保存修改结果

### 3. 数据删除
- 点击删除按钮触发删除操作
- 删除前会显示确认对话框
- 显示要删除的记录详情
- 删除后自动刷新数据列表

### 4. 数据新增
- 点击"新增记录"按钮打开新增模态框
- 支持填写所有字段
- 必填字段会有相应提示
- 新增成功后自动刷新数据列表

## 操作流程

### 查看表数据
1. 进入元数据管理页面
2. 点击表名右侧的"数据"按钮
3. 在数据模态框中查看表数据
4. 使用分页控件浏览更多数据

### 编辑记录
1. 在数据表格中找到要编辑的记录
2. 点击该记录右侧的"编辑"按钮（铅笔图标）
3. 在编辑模态框中修改字段值
4. 点击"保存"按钮提交修改
5. 系统会显示操作结果提示

### 删除记录
1. 在数据表格中找到要删除的记录
2. 点击该记录右侧的"删除"按钮（垃圾桶图标）
3. 在确认对话框中查看记录详情
4. 点击"确定"确认删除
5. 系统会显示操作结果提示

### 新增记录
1. 在数据模态框中点击"新增记录"按钮
2. 在新增模态框中填写字段值
3. 必填字段会有红色星号标识
4. 点击"新增"按钮提交数据
5. 系统会显示操作结果提示

## 技术实现

### 前端实现
- 使用Vue.js 3 Composition API
- 模态框组件用于编辑和新增操作
- 动态表单生成，支持任意表结构
- 完整的错误处理和用户反馈

### 后端接口
- `POST /api/database/table/{tableName}/add` - 新增记录
- `POST /api/database/table/{tableName}/update` - 更新记录
- `POST /api/database/table/{tableName}/delete` - 删除记录

### 数据库操作
- 使用JdbcTemplate执行动态SQL
- 自动构建INSERT、UPDATE、DELETE语句
- 支持参数化查询，防止SQL注入
- 完整的异常处理和事务管理

## 安全特性

### 数据验证
- 前端表单验证
- 后端数据校验
- 必填字段检查
- 数据类型验证

### 权限控制
- 操作确认机制
- 删除前二次确认
- 操作结果反馈
- 错误信息提示

### SQL注入防护
- 使用参数化查询
- 动态SQL构建
- 输入数据过滤
- 异常处理机制

## 使用注意事项

### 数据备份
- 在进行大量数据操作前，建议先备份数据库
- 删除操作不可逆，请谨慎操作
- 建议在测试环境中先验证操作

### 性能考虑
- 大量数据操作可能影响性能
- 建议分批处理大量数据
- 定期清理无用数据

### 字段限制
- 主键字段不可修改
- 必填字段不能为空
- 字段长度和类型需要符合数据库定义

## 故障排除

### 常见问题
1. **编辑失败**：检查字段值是否符合数据库约束
2. **删除失败**：确认记录存在且没有外键约束
3. **新增失败**：检查必填字段是否已填写
4. **页面无响应**：检查网络连接和后端服务状态

### 调试方法
1. 查看浏览器控制台错误信息
2. 检查后端日志输出
3. 验证数据库连接状态
4. 确认API接口响应

## 扩展功能

### 未来计划
- 批量操作功能
- 数据导入导出
- 高级搜索过滤
- 数据变更历史记录
- 用户操作日志

### 自定义配置
- 字段显示配置
- 操作权限控制
- 界面主题定制
- 多语言支持 
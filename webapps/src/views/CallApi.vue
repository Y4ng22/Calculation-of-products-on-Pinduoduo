<template>
  <div class="metrics-center">
    <div class="header-section">
      <div class="header-content">
        <h1 class="text-2xl font-bold text-gray-900">指标中心</h1>
        <p class="text-gray-600 mt-2">管理API数据拉取和数据库查询指标</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="showAddMetricDialog" size="large">
          <el-icon><Plus /></el-icon>
          新增指标
        </el-button>
      </div>
    </div>

    <!-- 指标统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon api-icon">
          <el-icon><Connection /></el-icon>
          </div>
        <div class="stat-content">
          <div class="stat-value">{{ apiMetricsCount }}</div>
          <div class="stat-label">API指标</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon db-icon">
          <el-icon><Coin /></el-icon>
            </div>
        <div class="stat-content">
          <div class="stat-value">{{ dbMetricsCount }}</div>
          <div class="stat-label">数据库指标</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon active-icon">
          <el-icon><Check /></el-icon>
          </div>
        <div class="stat-content">
          <div class="stat-value">{{ activeMetricsCount }}</div>
          <div class="stat-label">启用中</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon warning-icon">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ warningMetricsCount }}</div>
          <div class="stat-label">异常指标</div>
        </div>
      </div>
            </div>

    <!-- 指标列表 -->
    <el-card class="metrics-list-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon><DocumentCopy /></el-icon>
            <span>指标列表</span>
          </div>
          <div class="header-filters">
            <el-select v-model="filterType" placeholder="指标类型" clearable style="width: 150px">
              <el-option label="API指标" value="api" />
              <el-option label="数据库指标" value="database" />
            </el-select>
            <el-select v-model="filterStatus" placeholder="状态" clearable style="width: 120px">
              <el-option label="启用" value="enabled" />
              <el-option label="禁用" value="disabled" />
            </el-select>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索指标名称"
              clearable
              style="width: 200px"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
                    </div>
                  </div>
      </template>

      <el-table
        :data="filteredMetrics"
        v-loading="loading"
        style="width: 100%"
        empty-text="暂无指标数据"
      >
        <el-table-column prop="name" label="指标名称" min-width="180">
          <template #default="{ row }">
            <div class="metric-name">
              <el-icon v-if="row.type === 'api'" class="type-icon api"><Connection /></el-icon>
              <el-icon v-else class="type-icon db"><Coin /></el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'api' ? 'primary' : 'success'" size="small">
              {{ row.type === 'api' ? 'API' : '数据库' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="schedule" label="调度周期" width="120">
          <template #default="{ row }">
            <span class="schedule-text">{{ getScheduleText(row.schedule) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.enabled"
              @change="toggleMetricStatus(row)"
              :loading="row.statusLoading"
            />
          </template>
        </el-table-column>
        <el-table-column prop="lastRun" label="最后运行" width="160">
          <template #default="{ row }">
            <span v-if="row.lastRun" class="last-run">
              {{ formatTime(row.lastRun) }}
            </span>
            <span v-else class="text-gray-400">未运行</span>
          </template>
        </el-table-column>
        <el-table-column prop="result" label="运行结果" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.lastResult === 'success'" type="success" size="small">成功</el-tag>
            <el-tag v-else-if="row.lastResult === 'error'" type="danger" size="small">失败</el-tag>
            <el-tag v-else-if="row.lastResult === 'warning'" type="warning" size="small">警告</el-tag>
            <span v-else class="text-gray-400">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="scheduleStatus" label="调度状态" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.scheduled" type="success" size="small">
              <el-icon><Timer /></el-icon>
              调度中
            </el-tag>
            <el-tag v-else type="info" size="small">未调度</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="360" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewMetricDetail(row)">详情</el-button>
            <el-button size="small" @click="editMetric(row)">编辑</el-button>
            <el-button 
              size="small" 
              type="primary" 
              @click="runMetricNow(row)"
              :loading="row.runLoading"
            >
              运行
            </el-button>
            <el-button 
              v-if="row.scheduled"
              size="small" 
              type="warning" 
              @click="stopSchedule(row)"
              :loading="row.scheduleLoading"
            >
              停止调度
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="deleteMetric(row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑指标对话框 -->
    <el-dialog
      v-model="metricDialogVisible"
      :title="isEditMode ? '编辑指标' : '新增指标'"
      width="800px"
      :before-close="handleCloseMetricDialog"
    >
      <el-form :model="metricForm" :rules="metricRules" ref="metricFormRef" label-width="120px">
        <el-form-item label="指标名称" prop="name">
          <el-input v-model="metricForm.name" placeholder="请输入指标名称" />
        </el-form-item>
        
        <el-form-item label="指标类型" prop="type">
          <el-radio-group v-model="metricForm.type" @change="handleTypeChange">
            <el-radio value="api">API数据拉取</el-radio>
            <el-radio value="database">数据库查询</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="调度周期" prop="schedule">
          <el-select v-model="metricForm.schedule" placeholder="选择调度周期">
            <el-option label="每5分钟" value="5min" />
            <el-option label="每15分钟" value="15min" />
            <el-option label="每30分钟" value="30min" />
            <el-option label="每小时" value="1hour" />
            <el-option label="每天" value="1day" />
            <el-option label="每周" value="1week" />
          </el-select>
        </el-form-item>

        <!-- API类型配置 -->
        <div v-if="metricForm.type === 'api'">
          <el-divider content-position="left">API配置</el-divider>
          
          <el-form-item label="API地址" prop="apiUrl" required>
            <el-input v-model="metricForm.apiUrl" placeholder="请输入PDD API URL（必填）" />
          </el-form-item>
          
          <el-form-item label="CLIENT_ID" prop="clientId">
            <el-input v-model="metricForm.clientId" placeholder="请输入CLIENT_ID" />
          </el-form-item>
          
          <el-form-item label="CLIENT_SECRET" prop="clientSecret">
            <el-input v-model="metricForm.clientSecret" placeholder="请输入CLIENT_SECRET" type="password" show-password />
          </el-form-item>
          
          <el-form-item label="ACCESS_TOKEN" prop="accessToken">
            <el-input v-model="metricForm.accessToken" placeholder="请输入ACCESS_TOKEN" type="password" show-password />
          </el-form-item>

          <el-divider content-position="left">数据写入配置</el-divider>
          
          <el-form-item label="数据库地址" prop="targetDbHost">
            <el-input v-model="metricForm.targetDbHost" placeholder="请输入数据库地址" />
          </el-form-item>
          
          <el-form-item label="数据库名称" prop="targetDbName">
            <el-input v-model="metricForm.targetDbName" placeholder="请输入数据库名称" />
          </el-form-item>
          
          <el-form-item label="表名称" prop="targetTableName">
            <el-input v-model="metricForm.targetTableName" placeholder="请输入表名称" />
          </el-form-item>
        </div>

        <!-- 数据库类型配置 -->
        <div v-if="metricForm.type === 'database'">
          <el-divider content-position="left">数据库查询配置</el-divider>
          
          <el-form-item label="数据库地址" prop="sourceDbHost">
            <el-input v-model="metricForm.sourceDbHost" placeholder="请输入数据库地址" />
          </el-form-item>
          
          <el-form-item label="数据库名称" prop="sourceDbName">
            <el-input v-model="metricForm.sourceDbName" placeholder="请输入数据库名称" />
          </el-form-item>
          
          <el-form-item label="表名称" prop="sourceTableName">
            <el-input v-model="metricForm.sourceTableName" placeholder="请输入表名称" />
          </el-form-item>
          
          <el-form-item label="字段名" prop="fieldName">
            <el-input v-model="metricForm.fieldName" placeholder="请输入要统计的字段名" />
          </el-form-item>
          
          <el-form-item label="统计方式" prop="aggregateMethod">
            <el-select v-model="metricForm.aggregateMethod" placeholder="选择统计方式">
              <el-option label="求和(SUM)" value="SUM" />
              <el-option label="计数(COUNT)" value="COUNT" />
              <el-option label="平均值(AVG)" value="AVG" />
              <el-option label="最大值(MAX)" value="MAX" />
              <el-option label="最小值(MIN)" value="MIN" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="自定义SQL" prop="customSql">
            <el-input
              v-model="metricForm.customSql"
              type="textarea"
              :rows="4"
              placeholder="可选：输入自定义SQL查询语句（将覆盖上述配置）"
            />
          </el-form-item>

          <el-divider content-position="left">利润率监控</el-divider>
          
          <el-form-item label="启用利润率监控">
            <el-switch v-model="metricForm.enableProfitAlert" />
          </el-form-item>
          
          <el-form-item v-if="metricForm.enableProfitAlert" label="利润率阈值(%)" prop="profitThreshold">
            <el-input-number 
              v-model="metricForm.profitThreshold" 
              :min="0" 
              :max="100"
              :precision="2"
              controls-position="right"
            />
          </el-form-item>
        </div>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="metricForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入指标描述"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseMetricDialog">取消</el-button>
          <el-button type="primary" @click="saveMetric" :loading="saving">
            {{ isEditMode ? '更新' : '创建' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 指标详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="指标详情"
      width="700px"
      :before-close="handleCloseDetailDialog"
    >
      <div v-if="selectedMetric" class="metric-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="指标名称">{{ selectedMetric.name }}</el-descriptions-item>
          <el-descriptions-item label="指标类型">
            <el-tag :type="selectedMetric.type === 'api' ? 'primary' : 'success'">
              {{ selectedMetric.type === 'api' ? 'API指标' : '数据库指标' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="调度周期">{{ getScheduleText(selectedMetric.schedule) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="selectedMetric.enabled ? 'success' : 'info'">
              {{ selectedMetric.enabled ? '启用' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatTime(selectedMetric.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="最后运行">
            {{ selectedMetric.lastRun ? formatTime(selectedMetric.lastRun) : '未运行' }}
          </el-descriptions-item>
          <el-descriptions-item label="调度状态">
            <el-tag v-if="selectedMetric.scheduled" type="success" size="small">
              <el-icon><Timer /></el-icon>
              调度中
            </el-tag>
            <el-tag v-else type="info" size="small">未调度</el-tag>
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedMetric.scheduleInfo?.nextExecutionTime" label="下次执行">
            {{ formatTime(selectedMetric.scheduleInfo.nextExecutionTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ selectedMetric.description || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div v-if="selectedMetric.type === 'api'" class="config-section">
          <h4>API配置</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="API地址">{{ selectedMetric.apiUrl }}</el-descriptions-item>
            <el-descriptions-item label="CLIENT_ID">{{ selectedMetric.clientId || '-' }}</el-descriptions-item>
            <el-descriptions-item label="目标数据库">{{ selectedMetric.targetDbHost }}/{{ selectedMetric.targetDbName }}</el-descriptions-item>
            <el-descriptions-item label="目标表名">{{ selectedMetric.targetTableName }}</el-descriptions-item>
          </el-descriptions>
              </div>

        <div v-if="selectedMetric.type === 'database'" class="config-section">
          <h4>数据库查询配置</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="数据库">{{ selectedMetric.sourceDbHost }}/{{ selectedMetric.sourceDbName }}</el-descriptions-item>
            <el-descriptions-item label="表名">{{ selectedMetric.sourceTableName }}</el-descriptions-item>
            <el-descriptions-item label="字段名">{{ selectedMetric.fieldName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="统计方式">{{ selectedMetric.aggregateMethod || '-' }}</el-descriptions-item>
            <el-descriptions-item v-if="selectedMetric.customSql" label="自定义SQL">
              <pre class="sql-code">{{ selectedMetric.customSql }}</pre>
            </el-descriptions-item>
            <el-descriptions-item label="执行SQL预览">
              <div class="sql-preview-container">
                <pre class="sql-code sql-preview">{{ getExecutedSql(selectedMetric) }}</pre>
                <el-button 
                  size="small" 
                  type="text" 
                  @click="copyToClipboard(getExecutedSql(selectedMetric))"
                  class="copy-sql-btn"
                >
                  <el-icon><DocumentCopy /></el-icon>
                  复制SQL
                </el-button>
              </div>
            </el-descriptions-item>
            <el-descriptions-item v-if="selectedMetric.enableProfitAlert" label="利润率监控">
              启用，阈值: {{ selectedMetric.profitThreshold }}%
            </el-descriptions-item>
          </el-descriptions>
              </div>

        <div v-if="selectedMetric.lastResult" class="result-section">
          <h4>最近运行结果</h4>
          <div class="result-content">
            <div class="result-status">
              <el-tag :type="getResultType(selectedMetric.lastResult)" size="large">
                {{ getResultText(selectedMetric.lastResult) }}
              </el-tag>
              <div v-if="selectedMetric.lastRun" class="run-time">
                运行时间: {{ formatTime(selectedMetric.lastRun) }}
              </div>
            </div>
            
            <!-- 显示SQL查询结果值 -->
            <div v-if="selectedMetric.lastExecutionResult && selectedMetric.type === 'database'" class="sql-result-display">
              <div class="result-value-card">
                <div class="result-label">SQL查询结果</div>
                <div class="result-value">{{ formatQueryResult(selectedMetric.lastExecutionResult.query_result) }}</div>
              </div>
              
              <div v-if="selectedMetric.lastExecutionResult.sql_executed" class="executed-sql-card">
                <div class="result-label">执行的SQL</div>
                <pre class="executed-sql">{{ selectedMetric.lastExecutionResult.sql_executed }}</pre>
              </div>
              
              <div v-if="selectedMetric.lastExecutionResult.execution_time" class="execution-time-card">
                <div class="result-label">查询耗时</div>
                <div class="result-value">{{ formatTime(selectedMetric.lastExecutionResult.execution_time) }}</div>
              </div>
            </div>
            
            <!-- 显示API执行结果 -->
            <div v-if="selectedMetric.lastExecutionResult && selectedMetric.type === 'api'" class="api-result-display">
              <div class="result-value-card">
                <div class="result-label">拉取记录数</div>
                <div class="result-value">{{ selectedMetric.lastExecutionResult.records_fetched || 0 }}</div>
              </div>
              
              <div v-if="selectedMetric.lastExecutionResult.records_saved" class="result-value-card">
                <div class="result-label">保存记录数</div>
                <div class="result-value">{{ selectedMetric.lastExecutionResult.records_saved }}</div>
              </div>
            </div>
            
            <div v-if="selectedMetric.lastError" class="error-message">
              {{ selectedMetric.lastError }}
            </div>
          </div>
        </div>

        <!-- 运行历史记录 -->
        <div class="history-section">
          <div class="history-header">
            <h4>运行历史</h4>
            <el-button size="small" @click="loadMetricHistory">刷新</el-button>
          </div>
          
          <div v-if="metricHistoryLoading" class="history-loading">
            <el-icon class="is-loading"><Loading /></el-icon>
            加载中...
          </div>
          
          <div v-else-if="metricHistory.length === 0" class="no-history">
            暂无运行历史
          </div>
          
          <div v-else class="history-list">
            <div 
              v-for="record in metricHistory" 
              :key="record.id"
              class="history-item"
              :class="{ 'error': record.result === 'error', 'warning': record.result === 'warning' }"
            >
              <div class="history-main">
                <div class="history-status">
                  <el-tag 
                    :type="getResultType(record.result)" 
                    size="small"
                  >
                    {{ getResultText(record.result) }}
                  </el-tag>
                  <span class="history-time">{{ formatTime(record.executeTime) }}</span>
                  <span v-if="record.executionDuration" class="execution-time">
                    耗时: {{ record.executionDuration }}ms
                  </span>
                </div>
                <div class="history-summary">
                  {{ record.summary || '执行完成' }}
                </div>
                <div v-if="record.errorMessage" class="history-error">
                  错误信息: {{ record.errorMessage }}
                </div>
              </div>
              <div v-if="record.detailData" class="history-detail">
                <el-button 
                  type="text" 
                  size="small" 
                  @click="toggleHistoryDetail(record)"
                >
                  {{ record.showDetail ? '收起' : '查看详情' }}
                </el-button>
              </div>
              
              <!-- 详细数据展开 -->
              <div v-if="record.showDetail && record.detailData" class="detail-data">
                <div class="detail-grid">
                  <div v-for="(value, key) in record.detailData" :key="key" class="detail-item">
                    <label>{{ formatDetailKey(key) }}:</label>
                    <span>{{ formatDetailValue(value) }}</span>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 分页 -->
            <div v-if="historyPagination.total > historyPagination.size" class="history-pagination">
              <el-pagination
                v-model:current-page="historyPagination.page"
                :page-size="historyPagination.size"
                :total="historyPagination.total"
                layout="prev, pager, next"
                @current-change="handleHistoryPageChange"
                small
              />
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseDetailDialog">关闭</el-button>
          <el-button 
            v-if="!selectedMetric?.scheduled" 
            type="success" 
            @click="startScheduleFromDetail"
            :loading="selectedMetric?.scheduleLoading"
          >
            启动调度
          </el-button>
          <el-button 
            v-if="selectedMetric?.scheduled" 
            type="warning" 
            @click="stopScheduleFromDetail"
            :loading="selectedMetric?.scheduleLoading"
          >
            停止调度
          </el-button>
          <el-button type="primary" @click="editMetric(selectedMetric)">编辑</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { 
  Plus, Connection, Coin, Check, Warning, DocumentCopy, 
  Search, Picture, Delete, Loading, Timer 
} from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'MetricsCenter',
  components: { 
    Plus, Connection, Coin, Check, Warning, DocumentCopy, 
    Search, Picture, Delete, Loading, Timer 
  },
  data() {
    return {
      loading: false,
      saving: false,
      metrics: [],
      
      // 过滤器
      filterType: '',
      filterStatus: '',
      searchKeyword: '',
      
      // 分页
      pagination: {
        page: 1,
        size: 10,
        total: 0
      },
      
      // 对话框控制
      metricDialogVisible: false,
      detailDialogVisible: false,
      isEditMode: false,
      selectedMetric: null,
      
      // 历史记录
      metricHistory: [],
      metricHistoryLoading: false,
      historyPagination: {
        page: 1,
        size: 5,
        total: 0
      },
      
      // 表单数据
      metricForm: {
        name: '',
        type: 'api',
        schedule: '',
        description: '',
        // API类型字段
        apiUrl: '',
        clientId: '',
        clientSecret: '',
        accessToken: '',
        targetDbHost: '',
        targetDbName: '',
        targetTableName: '',
        // 数据库类型字段
        sourceDbHost: '',
        sourceDbName: '',
        sourceTableName: '',
        fieldName: '',
        aggregateMethod: '',
        customSql: '',
        enableProfitAlert: false,
        profitThreshold: 10
      },
      
      // 表单验证规则
      metricRules: {
        name: [
          { required: true, message: '请输入指标名称', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择指标类型', trigger: 'change' }
        ],
        schedule: [
          { required: true, message: '请选择调度周期', trigger: 'change' }
        ],
        apiUrl: [
          { required: true, message: '请输入API地址', trigger: 'blur' }
      ]
      }
    }
  },
  
  computed: {
    filteredMetrics() {
      let filtered = this.metrics
      
      if (this.filterType) {
        filtered = filtered.filter(m => m.type === this.filterType)
      }
      
      if (this.filterStatus) {
        const enabled = this.filterStatus === 'enabled'
        filtered = filtered.filter(m => m.enabled === enabled)
      }
      
      if (this.searchKeyword) {
        filtered = filtered.filter(m => 
          m.name.toLowerCase().includes(this.searchKeyword.toLowerCase())
        )
      }
      
      return filtered
    },
    
    apiMetricsCount() {
      return this.metrics.filter(m => m.type === 'api').length
    },
    
    dbMetricsCount() {
      return this.metrics.filter(m => m.type === 'database').length
    },
    
    activeMetricsCount() {
      return this.metrics.filter(m => m.enabled).length
    },
    
    warningMetricsCount() {
      return this.metrics.filter(m => m.lastResult === 'warning' || m.lastResult === 'error').length
    }
  },
  
  mounted() {
    this.fetchMetrics()
  },
  
  methods: {
    async fetchMetrics() {
      this.loading = true
      try {
        const response = await axios.get('/api/metrics', {
          params: {
            page: this.pagination.page,
            size: this.pagination.size
          }
        })
        
        if (response.data.code === 1) {
          this.metrics = (response.data.data.records || []).map(metric => ({
            ...metric,
            scheduled: false, // 初始化调度状态
            scheduleLoading: false // 初始化调度loading状态
          }))
          this.pagination.total = response.data.data.total || 0
          
          // 异步获取每个指标的调度状态
          this.fetchScheduleStatuses()
        } else {
          this.metrics = []
          this.pagination.total = 0
        }
      } catch (error) {
        console.error('获取指标列表失败:', error)
        ElMessage.error('获取指标列表失败')
        this.metrics = []
        this.pagination.total = 0
      } finally {
        this.loading = false
      }
    },
    
    async fetchScheduleStatuses() {
      // 为每个指标异步获取调度状态
      const statusPromises = this.metrics.map(async (metric) => {
        try {
          const response = await axios.get(`/api/metrics/${metric.id}/schedule/status`)
          if (response.data.code === 1) {
            metric.scheduled = response.data.data.scheduled || false
            // 可以添加更多调度相关信息，如下次执行时间等
            metric.scheduleInfo = response.data.data
          }
        } catch (error) {
          console.warn(`获取指标${metric.id}调度状态失败:`, error)
          metric.scheduled = false
        }
      })
      
      // 等待所有状态查询完成
      await Promise.allSettled(statusPromises)
    },
    
    showAddMetricDialog() {
      this.isEditMode = false
      this.resetMetricForm()
      this.metricDialogVisible = true
    },
    
    editMetric(metric) {
      this.isEditMode = true
      this.metricForm = { ...metric }
      this.metricDialogVisible = true
      this.detailDialogVisible = false
    },
    
    async viewMetricDetail(metric) {
      this.selectedMetric = metric
      this.detailDialogVisible = true
      
      // 获取最新的调度状态
      try {
        const response = await axios.get(`/api/metrics/${metric.id}/schedule/status`)
        if (response.data.code === 1) {
          this.selectedMetric.scheduled = response.data.data.scheduled || false
          this.selectedMetric.scheduleInfo = response.data.data
        }
      } catch (error) {
        console.warn('获取调度状态失败:', error)
      }
      
      // 加载历史记录
      this.loadMetricHistory()
    },
    
    async saveMetric() {
      try {
        await this.$refs.metricFormRef.validate()
        
        this.saving = true
        
        const url = this.isEditMode ? `/api/metrics/${this.metricForm.id}` : '/api/metrics'
        const method = this.isEditMode ? 'put' : 'post'
        
        const response = await axios[method](url, this.metricForm)
        
        if (response.data.code === 1) {
          ElMessage.success(this.isEditMode ? '指标更新成功' : '指标创建成功')
          this.metricDialogVisible = false
          this.fetchMetrics()
        } else {
          ElMessage.error(response.data.message || '操作失败')
        }
      } catch (error) {
        console.error('保存指标失败:', error)
        ElMessage.error('保存指标失败')
      } finally {
        this.saving = false
      }
    },
    
    async toggleMetricStatus(metric) {
      metric.statusLoading = true
      try {
        const response = await axios.put(`/api/metrics/${metric.id}/status`, {
          enabled: metric.enabled
        })
        
        if (response.data.code === 1) {
          ElMessage.success(metric.enabled ? '指标已启用' : '指标已禁用')
        } else {
          metric.enabled = !metric.enabled // 回滚
          ElMessage.error(response.data.message || '状态更新失败')
        }
      } catch (error) {
        metric.enabled = !metric.enabled // 回滚
        console.error('更新指标状态失败:', error)
        ElMessage.error('更新指标状态失败')
      } finally {
        metric.statusLoading = false
      }
    },
    
    async runMetricNow(metric) {
      metric.runLoading = true
      try {
        const response = await axios.post(`/api/metrics/${metric.id}/run`)
        
        if (response.data.code === 1) {
          ElMessage.success('指标运行成功，调度任务已启动')
          
          // 更新调度状态
          metric.scheduled = response.data.data?.scheduleStarted || false
          
          // 如果当前正在查看这个指标的详情，更新执行结果数据
          if (this.selectedMetric && this.selectedMetric.id === metric.id) {
            this.selectedMetric.lastExecutionResult = response.data.data || {}
            this.selectedMetric.lastRun = new Date().toISOString()
            this.selectedMetric.lastResult = 'success'
            this.selectedMetric.scheduled = metric.scheduled
          }
          
          this.fetchMetrics() // 刷新列表获取最新状态
        } else {
          ElMessage.error(response.data.message || '指标运行失败')
        }
      } catch (error) {
        console.error('运行指标失败:', error)
        ElMessage.error('运行指标失败')
      } finally {
        metric.runLoading = false
      }
    },
    
    async stopSchedule(metric) {
      metric.scheduleLoading = true
      try {
        const response = await axios.post(`/api/metrics/${metric.id}/schedule/stop`)
        
        if (response.data.code === 1) {
          ElMessage.success('调度任务已停止')
          metric.scheduled = false
          
          // 如果当前正在查看这个指标的详情，更新调度状态
          if (this.selectedMetric && this.selectedMetric.id === metric.id) {
            this.selectedMetric.scheduled = false
          }
        } else {
          ElMessage.error(response.data.message || '停止调度失败')
        }
      } catch (error) {
        console.error('停止调度失败:', error)
        ElMessage.error('停止调度失败')
      } finally {
        metric.scheduleLoading = false
      }
    },
    
    async startScheduleFromDetail() {
      if (!this.selectedMetric) return
      
      this.selectedMetric.scheduleLoading = true
      try {
        const response = await axios.post(`/api/metrics/${this.selectedMetric.id}/schedule/start`)
        
        if (response.data.code === 1) {
          ElMessage.success('调度任务已启动')
          this.selectedMetric.scheduled = true
          this.selectedMetric.scheduleInfo = response.data.data
          
          // 更新列表中的对应项
          const listItem = this.metrics.find(m => m.id === this.selectedMetric.id)
          if (listItem) {
            listItem.scheduled = true
            listItem.scheduleInfo = response.data.data
          }
        } else {
          ElMessage.error(response.data.message || '启动调度失败')
        }
      } catch (error) {
        console.error('启动调度失败:', error)
        ElMessage.error('启动调度失败')
      } finally {
        this.selectedMetric.scheduleLoading = false
      }
    },
    
    async stopScheduleFromDetail() {
      if (!this.selectedMetric) return
      
      this.selectedMetric.scheduleLoading = true
      try {
        const response = await axios.post(`/api/metrics/${this.selectedMetric.id}/schedule/stop`)
        
        if (response.data.code === 1) {
          ElMessage.success('调度任务已停止')
          this.selectedMetric.scheduled = false
          this.selectedMetric.scheduleInfo = null
          
          // 更新列表中的对应项
          const listItem = this.metrics.find(m => m.id === this.selectedMetric.id)
          if (listItem) {
            listItem.scheduled = false
            listItem.scheduleInfo = null
          }
        } else {
          ElMessage.error(response.data.message || '停止调度失败')
        }
      } catch (error) {
        console.error('停止调度失败:', error)
        ElMessage.error('停止调度失败')
      } finally {
        this.selectedMetric.scheduleLoading = false
      }
    },
    
    async deleteMetric(metricId) {
      try {
        await ElMessageBox.confirm('确定要删除这个指标吗？删除后无法恢复。', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const response = await axios.delete(`/api/metrics/${metricId}`)
        
        if (response.data.code === 1) {
          ElMessage.success('指标删除成功')
          this.fetchMetrics()
        } else {
          ElMessage.error(response.data.message || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除指标失败:', error)
          ElMessage.error('删除指标失败')
        }
      }
    },
    
    resetMetricForm() {
      this.metricForm = {
        name: '',
        type: 'api',
        schedule: '',
        description: '',
        apiUrl: '',
        clientId: '',
        clientSecret: '',
        accessToken: '',
        targetDbHost: '',
        targetDbName: '',
        targetTableName: '',
        sourceDbHost: '',
        sourceDbName: '',
        sourceTableName: '',
        fieldName: '',
        aggregateMethod: '',
        customSql: '',
        enableProfitAlert: false,
        profitThreshold: 10
      }
    },
    
    handleTypeChange(type) {
      // 切换类型时清理对应的字段
      if (type === 'api') {
        // 清理数据库类型字段
        this.metricForm.sourceDbHost = ''
        this.metricForm.sourceDbName = ''
        this.metricForm.sourceTableName = ''
        this.metricForm.fieldName = ''
        this.metricForm.aggregateMethod = ''
        this.metricForm.customSql = ''
        this.metricForm.enableProfitAlert = false
      } else {
        // 清理API类型字段
        this.metricForm.apiUrl = ''
        this.metricForm.clientId = ''
        this.metricForm.clientSecret = ''
        this.metricForm.accessToken = ''
        this.metricForm.targetDbHost = ''
        this.metricForm.targetDbName = ''
        this.metricForm.targetTableName = ''
      }
    },
    
    handleCloseMetricDialog() {
      this.metricDialogVisible = false
      this.resetMetricForm()
    },
    
    handleCloseDetailDialog() {
      this.detailDialogVisible = false
      this.selectedMetric = null
      this.metricHistory = []
      this.historyPagination.page = 1
    },
    
    // 历史记录相关方法
    async loadMetricHistory() {
      if (!this.selectedMetric) return
      
      this.metricHistoryLoading = true
      try {
        const response = await axios.get(`/api/metrics/${this.selectedMetric.id}/history`, {
          params: {
            page: this.historyPagination.page,
            size: this.historyPagination.size
          }
        })
        
        if (response.data.code === 1) {
          this.metricHistory = (response.data.data.records || []).map(record => ({
            ...record,
            showDetail: false
          }))
          this.historyPagination.total = response.data.data.total || 0
        } else {
          this.metricHistory = []
          this.historyPagination.total = 0
        }
      } catch (error) {
        console.error('获取历史记录失败:', error)
        ElMessage.error('获取历史记录失败')
        this.metricHistory = []
        this.historyPagination.total = 0
      } finally {
        this.metricHistoryLoading = false
      }
    },
    
    toggleHistoryDetail(record) {
      record.showDetail = !record.showDetail
    },
    
    handleHistoryPageChange(page) {
      this.historyPagination.page = page
      this.loadMetricHistory()
    },
    
    formatDetailKey(key) {
      const keyMap = {
        'api_response_status': 'API响应状态',
        'api_url': 'API地址',
        'response_time': '响应时间',
        'records_fetched': '拉取记录数',
        'database_write': '数据库写入',
        'records_saved': '保存记录数',
        'target_table': '目标表',
        'sql_executed': '执行SQL',
        'query_result': '查询结果',
        'execution_time': '执行时间',
        'profit_alert': '利润率警告',
        'alert_message': '警告信息',
        'summary': '执行摘要'
      }
      return keyMap[key] || key
    },
    
    formatDetailValue(value) {
      if (typeof value === 'object') {
        return JSON.stringify(value, null, 2)
      }
      return String(value)
    },
    
    // 获取执行SQL预览
    getExecutedSql(metric) {
      if (!metric) return ''
      
      // 如果有自定义SQL，直接返回
      if (metric.customSql && metric.customSql.trim()) {
        return metric.customSql.trim()
      }
      
      // 否则根据配置构建SQL
      if (!metric.sourceTableName) {
        return '-- 请配置数据源表名'
      }
      
      let sql = 'SELECT '
      
      if (metric.aggregateMethod && metric.fieldName) {
        // 将中文统计方式转换为SQL函数名
        const sqlFunction = this.getStandardSqlFunction(metric.aggregateMethod)
        sql += `${sqlFunction}(${metric.fieldName}) as result`
      } else {
        sql += 'COUNT(*) as result'
      }
      
      sql += ` FROM ${metric.sourceTableName}`
      
      return sql
    },
    
    // 获取标准SQL函数名
    getStandardSqlFunction(method) {
      // 处理中文到英文的映射
      const methodMap = {
        '求和': 'SUM',
        '计数': 'COUNT', 
        '平均值': 'AVG',
        '最大值': 'MAX',
        '最小值': 'MIN',
        '求和(SUM)': 'SUM',
        '计数(COUNT)': 'COUNT',
        '平均值(AVG)': 'AVG', 
        '最大值(MAX)': 'MAX',
        '最小值(MIN)': 'MIN',
        // 标准SQL函数名（直接返回）
        'SUM': 'SUM',
        'COUNT': 'COUNT',
        'AVG': 'AVG',
        'MAX': 'MAX', 
        'MIN': 'MIN'
      }
      
      return methodMap[method] || method.toUpperCase()
    },
    
    // 复制到剪贴板
    async copyToClipboard(text) {
      try {
        await navigator.clipboard.writeText(text)
        ElMessage.success('SQL已复制到剪贴板')
      } catch (error) {
        // 如果现代API不可用，尝试传统方法
        try {
          const textarea = document.createElement('textarea')
          textarea.value = text
          document.body.appendChild(textarea)
          textarea.select()
          document.execCommand('copy')
          document.body.removeChild(textarea)
          ElMessage.success('SQL已复制到剪贴板')
        } catch (fallbackError) {
          ElMessage.error('复制失败，请手动复制')
          console.error('复制到剪贴板失败:', error, fallbackError)
        }
      }
    },
    
    // 格式化查询结果值
    formatQueryResult(value) {
      if (value === null || value === undefined) {
        return '--'
      }
      
      // 如果是数字，添加千分位分隔符
      if (typeof value === 'number') {
        return value.toLocaleString('zh-CN')
      }
      
      return String(value)
    },
    
    handleSizeChange(size) {
      this.pagination.size = size
      this.pagination.page = 1
      this.fetchMetrics()
    },
    
    handleCurrentChange(page) {
      this.pagination.page = page
      this.fetchMetrics()
    },
    
    getScheduleText(schedule) {
      const scheduleMap = {
        '5min': '每5分钟',
        '15min': '每15分钟',
        '30min': '每30分钟',
        '1hour': '每小时',
        '1day': '每天',
        '1week': '每周'
      }
      return scheduleMap[schedule] || schedule
    },
    
    formatTime(timestamp) {
      if (!timestamp) return '-'
      const date = new Date(timestamp)
      return date.toLocaleString('zh-CN')
    },
    
    getResultType(result) {
      const typeMap = {
        'success': 'success',
        'error': 'danger',
        'warning': 'warning'
      }
      return typeMap[result] || 'info'
    },
    
    getResultText(result) {
      const textMap = {
        'success': '运行成功',
        'error': '运行失败',
        'warning': '运行异常'
      }
      return textMap[result] || '未知状态'
    }
  }
}
</script>

<style scoped>
.metrics-center {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.header-content h1 {
  margin: 0;
  color: #303133;
}

.header-content p {
  margin: 8px 0 0 0;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.api-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.db-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.active-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.warning-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-top: 4px;
}

.metrics-list-card {
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 16px;
}

.header-filters {
  display: flex;
  gap: 12px;
}

.metric-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.type-icon {
  font-size: 16px;
}

.type-icon.api {
  color: #409eff;
}

.type-icon.db {
  color: #67c23a;
}

.schedule-text {
  color: #606266;
}

.last-run {
  font-size: 12px;
  color: #909399;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.metric-detail {
  max-height: 600px;
  overflow-y: auto;
}

/* 运行结果样式 */
.result-section {
  margin-top: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 4px solid #409eff;
}

.run-time {
  margin-top: 8px;
  font-size: 12px;
  color: #606266;
}

.error-message {
  margin-top: 8px;
  padding: 8px 12px;
  background: #fef0f0;
  border: 1px solid #fde2e2;
  border-radius: 4px;
  color: #f56c6c;
  font-size: 12px;
}

/* 历史记录样式 */
.history-section {
  margin-top: 24px;
  border-top: 1px solid #e4e7ed;
  padding-top: 20px;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.history-header h4 {
  margin: 0;
  color: #303133;
}

.history-loading {
  text-align: center;
  padding: 20px;
  color: #909399;
}

.no-history {
  text-align: center;
  padding: 20px;
  color: #c0c4cc;
  font-style: italic;
}

.history-list {
  max-height: 300px;
  overflow-y: auto;
}

.history-item {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  margin-bottom: 12px;
  overflow: hidden;
  transition: all 0.3s;
}

.history-item:hover {
  border-color: #c6e2ff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.history-item.error {
  border-left: 4px solid #f56c6c;
}

.history-item.warning {
  border-left: 4px solid #e6a23c;
}

.history-main {
  padding: 12px 16px;
}

.history-status {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.history-time {
  font-size: 12px;
  color: #909399;
}

.execution-time {
  font-size: 12px;
  color: #67c23a;
  background: #f0f9ff;
  padding: 2px 6px;
  border-radius: 3px;
}

.history-summary {
  font-size: 13px;
  color: #606266;
  line-height: 1.4;
}

.history-error {
  margin-top: 8px;
  font-size: 12px;
  color: #f56c6c;
  background: #fef0f0;
  padding: 6px 10px;
  border-radius: 4px;
  border: 1px solid #fde2e2;
}

.history-detail {
  padding: 0 16px 12px;
  text-align: right;
}

.detail-data {
  background: #fafbfc;
  border-top: 1px solid #e4e7ed;
  padding: 12px 16px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 8px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.detail-item label {
  font-size: 12px;
  color: #909399;
  font-weight: 500;
}

.detail-item span {
  font-size: 13px;
  color: #303133;
  word-break: break-all;
  max-height: 60px;
  overflow-y: auto;
  background: white;
  padding: 4px 8px;
  border-radius: 3px;
  border: 1px solid #e4e7ed;
}

.history-pagination {
  margin-top: 16px;
  text-align: center;
  padding: 12px 0;
  border-top: 1px solid #f0f2f5;
}

.config-section {
  margin-top: 24px;
}

.config-section h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 16px;
}

.result-section {
  margin-top: 24px;
}

.result-section h4 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 16px;
}

.result-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.error-message {
  background: #fef0f0;
  color: #f56c6c;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 14px;
  border-left: 3px solid #f56c6c;
}

.sql-code {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 4px;
  padding: 12px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  white-space: pre-wrap;
  word-break: break-all;
}

.dialog-footer {
  display: flex;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-section {
    flex-direction: column;
    gap: 16px;
  }
  
  .header-filters {
    flex-direction: column;
    width: 100%;
  }
  
  .stats-cards {
    grid-template-columns: 1fr;
  }
}

/* SQL预览样式 */
.sql-preview-container {
  position: relative;
}

.sql-preview {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  padding: 12px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
  line-height: 1.5;
  color: #2d3748;
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 200px;
  overflow-y: auto;
}

.copy-sql-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 4px 8px !important;
  font-size: 12px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #ddd;
  border-radius: 4px;
  transition: all 0.2s;
}

.copy-sql-btn:hover {
  background: rgba(255, 255, 255, 1);
  border-color: #409eff;
  color: #409eff;
}

.copy-sql-btn .el-icon {
  margin-right: 4px;
}

/* 改进现有的sql-code样式 */
.sql-code {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  padding: 12px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
  line-height: 1.5;
  color: #2d3748;
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
}

/* 最近运行结果显示样式 */
.result-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.result-status {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.sql-result-display, .api-result-display {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 12px;
  margin-top: 12px;
}

.result-value-card, .executed-sql-card, .execution-time-card {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 12px;
  transition: all 0.2s;
}

.result-value-card:hover, .executed-sql-card:hover, .execution-time-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.result-label {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
  margin-bottom: 6px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.result-value {
  font-size: 20px;
  font-weight: bold;
  color: #1e293b;
  line-height: 1.2;
}

.executed-sql-card {
  grid-column: 1 / -1; /* 占据整行 */
}

.executed-sql {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  padding: 8px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
  line-height: 1.4;
  color: #2d3748;
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 120px;
  overflow-y: auto;
}

.run-time {
  font-size: 12px;
  color: #64748b;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sql-result-display, .api-result-display {
    grid-template-columns: 1fr;
  }
  
  .result-value {
    font-size: 18px;
  }
}
</style>

<template>
  <div class="bg-gray-100 min-h-screen">
    <main class="container mx-auto px-4 py-8 space-y-6">
      <!-- 页面标题 -->
      <div class="bg-white rounded-lg shadow p-6">
        <div class="flex items-center justify-between">
          <h1 class="text-2xl font-bold text-gray-900">元数据管理</h1>
          <div class="flex items-center space-x-4">
            <button @click="testConnection" class="px-4 py-2 bg-yellow-500 text-white rounded-button hover:bg-yellow-600 flex items-center space-x-2">
              <i class="fas fa-plug"></i>
              <span>测试连接</span>
            </button>
            <button @click="refreshTables" class="px-4 py-2 bg-primary text-white rounded-button hover:bg-primary/90 flex items-center space-x-2">
              <i class="fas fa-sync-alt"></i>
              <span>刷新</span>
            </button>
            <button @click="exportMetadata" class="px-4 py-2 bg-secondary text-white rounded-button hover:bg-secondary/90 flex items-center space-x-2">
              <i class="fas fa-download"></i>
              <span>导出</span>
            </button>
          </div>
        </div>
        
        <!-- 连接状态提示 -->
        <div v-if="connectionStatus !== null" class="mt-4 p-4 rounded-lg" 
             :class="connectionStatus === 'success' ? 'bg-green-50 border border-green-200' : 'bg-red-50 border border-red-200'">
          <div class="flex items-center">
            <i :class="connectionStatus === 'success' ? 'fas fa-check-circle text-green-500' : 'fas fa-exclamation-circle text-red-500'" class="mr-2"></i>
            <span :class="connectionStatus === 'success' ? 'text-green-800' : 'text-red-800'" class="text-sm font-medium">
              {{ connectionMessage }}
            </span>
            <button @click="clearConnectionStatus" class="ml-auto text-gray-400 hover:text-gray-600">
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- 统计信息卡片 -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center">
            <div class="p-3 bg-blue-100 rounded-full">
              <i class="fas fa-database text-blue-600 text-xl"></i>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-500">数据库表总数</p>
              <p class="text-2xl font-bold text-gray-900">{{ totalTables }}</p>
            </div>
          </div>
        </div>
        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center">
            <div class="p-3 bg-green-100 rounded-full">
              <i class="fas fa-columns text-green-600 text-xl"></i>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-500">总字段数</p>
              <p class="text-2xl font-bold text-gray-900">{{ totalColumns }}</p>
            </div>
          </div>
        </div>
        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center">
            <div class="p-3 bg-yellow-100 rounded-full">
              <i class="fas fa-list text-yellow-600 text-xl"></i>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-500">总记录数</p>
              <p class="text-2xl font-bold text-gray-900">{{ totalRecords }}</p>
            </div>
          </div>
        </div>
        <div class="bg-white rounded-lg shadow p-6">
          <div class="flex items-center">
            <div class="p-3 bg-purple-100 rounded-full">
              <i class="fas fa-clock text-purple-600 text-xl"></i>
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-500">最后更新</p>
              <p class="text-lg font-bold text-gray-900">{{ lastUpdateTime }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="bg-white rounded-lg shadow p-6">
        <div class="flex items-center justify-center">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
          <span class="ml-3 text-gray-600">正在加载数据库表信息...</span>
        </div>
      </div>

      <!-- 错误信息 -->
      <div v-else-if="error" class="bg-white rounded-lg shadow p-6">
        <div class="flex items-center justify-center">
          <div class="text-center">
            <i class="fas fa-exclamation-triangle text-red-500 text-3xl mb-3"></i>
            <p class="text-red-600 mb-2">加载失败</p>
            <p class="text-gray-500 text-sm mb-4">{{ error }}</p>
            <button @click="loadTables" class="px-4 py-2 bg-primary text-white rounded-button hover:bg-primary/90">
              重试
            </button>
          </div>
        </div>
      </div>

      <!-- 数据库表列表 -->
      <div v-else class="bg-white rounded-lg shadow">
        <div class="px-6 py-4 border-b border-gray-200">
          <h2 class="text-lg font-medium text-gray-900">数据库表信息</h2>
        </div>
        <div v-if="tables.length === 0" class="p-6 text-center text-gray-500">
          <i class="fas fa-database text-4xl mb-3"></i>
          <p>暂无数据库表信息</p>
        </div>
        <div v-else class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">表名</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">字段数</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">记录数</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">状态</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
              <tr v-for="table in tables" :key="table.tableName" class="hover:bg-gray-50">
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <i class="fas fa-table text-gray-400 mr-3"></i>
                    <div>
                      <div class="text-sm font-medium text-gray-900">{{ table.tableName }}</div>
                      <div class="text-sm text-gray-500">{{ getTableDescription(table.tableName) }}</div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ table.columnCount || 0 }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                  {{ table.recordCount || 0 }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full" 
                        :class="table.error ? 'bg-red-100 text-red-800' : 'bg-green-100 text-green-800'">
                    {{ table.error ? '错误' : '正常' }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                  <button @click="viewTableDetails(table)" class="text-primary hover:text-primary/80 mr-3">
                    <i class="fas fa-eye"></i>
                    结构
                  </button>
                  <button @click="viewTableData(table)" class="text-blue-600 hover:text-blue-800 mr-3">
                    <i class="fas fa-table"></i>
                    数据
                  </button>
                  <button @click="exportTable(table)" class="text-secondary hover:text-secondary/80">
                    <i class="fas fa-download"></i>
                    导出
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 表详情模态框 -->
      <div v-if="showTableModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
        <div class="relative top-20 mx-auto p-5 border w-11/12 md:w-3/4 lg:w-1/2 shadow-lg rounded-md bg-white">
          <div class="mt-3">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-medium text-gray-900">表结构: {{ selectedTable?.tableName }}</h3>
              <button @click="closeTableModal" class="text-gray-400 hover:text-gray-600">
                <i class="fas fa-times text-xl"></i>
              </button>
            </div>
            
            <div v-if="selectedTable?.columns" class="overflow-x-auto">
              <table class="w-full">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">字段名</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">类型</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">是否为空</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">键</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">默认值</th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase">额外</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-200">
                  <tr v-for="column in selectedTable.columns" :key="column.Field">
                    <td class="px-4 py-2 text-sm text-gray-900">{{ column.Field }}</td>
                    <td class="px-4 py-2 text-sm text-gray-500">{{ column.Type }}</td>
                    <td class="px-4 py-2 text-sm text-gray-500">{{ column.Null }}</td>
                    <td class="px-4 py-2 text-sm text-gray-500">{{ column.Key }}</td>
                    <td class="px-4 py-2 text-sm text-gray-500">{{ column.Default || '-' }}</td>
                    <td class="px-4 py-2 text-sm text-gray-500">{{ column.Extra || '-' }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <div v-if="selectedTable?.error" class="mt-4 p-4 bg-red-50 border border-red-200 rounded-lg">
              <p class="text-red-800">{{ selectedTable.error }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 表数据模态框 -->
      <div v-if="showDataModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
        <div class="relative top-10 mx-auto p-5 border w-11/12 md:w-5/6 lg:w-4/5 shadow-lg rounded-md bg-white">
          <div class="mt-3">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-medium text-gray-900">表数据: {{ selectedTableData?.tableName }}</h3>
              <div class="flex items-center space-x-4">
                <span class="text-sm text-gray-500">共 {{ selectedTableData?.total || 0 }} 条记录</span>
                <button @click="closeDataModal" class="text-gray-400 hover:text-gray-600">
                  <i class="fas fa-times text-xl"></i>
                </button>
              </div>
            </div>
            
            <!-- 操作按钮 -->
            <div class="flex items-center justify-between mb-4">
              <div class="flex items-center space-x-2">
                <button @click="addNewRow" 
                        class="px-4 py-2 bg-green-500 text-white rounded-button hover:bg-green-600 flex items-center space-x-2">
                  <i class="fas fa-plus"></i>
                  <span>新增记录</span>
                </button>
                <button @click="refreshTableData" 
                        class="px-4 py-2 bg-blue-500 text-white rounded-button hover:bg-blue-600 flex items-center space-x-2">
                  <i class="fas fa-sync-alt"></i>
                  <span>刷新</span>
                </button>
              </div>
            </div>
            
            <!-- 分页控制 -->
            <div class="flex items-center justify-between mb-4">
              <div class="flex items-center space-x-2">
                <span class="text-sm text-gray-500">每页显示:</span>
                <select v-model="pageSize" @change="loadTableData" class="px-2 py-1 border border-gray-300 rounded text-sm">
                  <option value="10">10</option>
                  <option value="20">20</option>
                  <option value="50">50</option>
                  <option value="100">100</option>
                </select>
                <span class="text-sm text-gray-500">条</span>
              </div>
              <div class="flex items-center space-x-2">
                <button @click="prevPage" :disabled="currentPage <= 1" 
                        class="px-3 py-1 border border-gray-300 rounded text-sm disabled:opacity-50 disabled:cursor-not-allowed">
                  <i class="fas fa-chevron-left"></i>
                </button>
                <span class="text-sm text-gray-500">
                  第 {{ currentPage }} 页，共 {{ totalPages }} 页
                </span>
                <button @click="nextPage" :disabled="currentPage >= totalPages" 
                        class="px-3 py-1 border border-gray-300 rounded text-sm disabled:opacity-50 disabled:cursor-not-allowed">
                  <i class="fas fa-chevron-right"></i>
                </button>
              </div>
            </div>
            
            <!-- 数据表格 -->
            <div v-if="tableData.length > 0" class="overflow-x-auto">
              <table class="w-full border border-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th v-for="column in tableColumns" :key="column" 
                        class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase border-b border-gray-200">
                      {{ column }}
                    </th>
                    <th class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase border-b border-gray-200">
                      操作
                    </th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-gray-200">
                  <tr v-for="(row, index) in tableData" :key="index" class="hover:bg-gray-50">
                    <td v-for="column in tableColumns" :key="column" 
                        class="px-4 py-2 text-sm text-gray-900 border-b border-gray-100">
                      <div class="max-w-xs truncate" :title="row[column]">
                        {{ row[column] || '-' }}
                      </div>
                    </td>
                    <td class="px-4 py-2 text-sm border-b border-gray-100">
                      <div class="flex items-center space-x-2">
                        <button @click="editRow(row, index)" 
                                class="text-blue-600 hover:text-blue-800 p-1 rounded hover:bg-blue-50"
                                title="编辑">
                          <i class="fas fa-edit text-sm"></i>
                        </button>
                        <button @click="deleteRow(row, index)" 
                                class="text-red-600 hover:text-red-800 p-1 rounded hover:bg-red-50"
                                title="删除">
                          <i class="fas fa-trash text-sm"></i>
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- 空数据状态 -->
            <div v-else class="text-center py-8 text-gray-500">
              <i class="fas fa-database text-4xl mb-3"></i>
              <p>暂无数据</p>
            </div>
            
            <!-- 加载状态 -->
            <div v-if="dataLoading" class="text-center py-4">
              <div class="animate-spin rounded-full h-6 w-6 border-b-2 border-primary mx-auto"></div>
              <span class="ml-2 text-gray-600">加载中...</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 编辑记录模态框 -->
      <div v-if="showEditModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
        <div class="relative top-20 mx-auto p-5 border w-11/12 md:w-3/4 lg:w-1/2 shadow-lg rounded-md bg-white">
          <div class="mt-3">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-medium text-gray-900">编辑记录</h3>
              <button @click="closeEditModal" class="text-gray-400 hover:text-gray-600">
                <i class="fas fa-times text-xl"></i>
              </button>
            </div>
            
            <form @submit.prevent="saveEditRow" class="space-y-4">
              <div v-for="column in tableColumns" :key="column" class="grid grid-cols-1 gap-2">
                <label :for="column" class="text-sm font-medium text-gray-700">{{ column }}</label>
                <input 
                  :id="column"
                  v-model="editingRowData[column]"
                  type="text"
                  class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  :readonly="column === 'id' && isPrimaryKey(column)"
                />
              </div>
              
              <div class="flex items-center justify-end space-x-3 pt-4">
                <button type="button" @click="closeEditModal" 
                        class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50">
                  取消
                </button>
                <button type="submit" 
                        class="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600">
                  保存
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- 新增记录模态框 -->
      <div v-if="showAddModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
        <div class="relative top-20 mx-auto p-5 border w-11/12 md:w-3/4 lg:w-1/2 shadow-lg rounded-md bg-white">
          <div class="mt-3">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-lg font-medium text-gray-900">新增记录</h3>
              <button @click="closeAddModal" class="text-gray-400 hover:text-gray-600">
                <i class="fas fa-times text-xl"></i>
              </button>
            </div>
            
            <form @submit.prevent="saveNewRow" class="space-y-4">
              <div v-for="column in tableColumns" :key="column" class="grid grid-cols-1 gap-2">
                <label :for="'new-' + column" class="text-sm font-medium text-gray-700">{{ column }}</label>
                <input 
                  :id="'new-' + column"
                  v-model="newRowData[column]"
                  type="text"
                  class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  :required="isRequired(column)"
                  :placeholder="getColumnPlaceholder(column)"
                />
              </div>
              
              <div class="flex items-center justify-end space-x-3 pt-4">
                <button type="button" @click="closeAddModal" 
                        class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50">
                  取消
                </button>
                <button type="submit" 
                        class="px-4 py-2 bg-green-500 text-white rounded-md hover:bg-green-600">
                  新增
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
export default {
  name: 'MetadataManagement',
  data() {
    return {
      tables: [],
      showTableModal: false,
      selectedTable: null,
      loading: false,
      lastUpdateTime: '--',
      error: null,
      // 表数据相关
      showDataModal: false,
      selectedTableData: null,
      tableData: [],
      tableColumns: [],
      currentPage: 1,
      pageSize: 10,
      totalPages: 1,
      dataLoading: false,
      // 连接测试相关
      connectionStatus: null,
      connectionMessage: '',
      // 编辑相关
      showEditModal: false,
      editingRow: null,
      editingIndex: null,
      editingRowData: {},
      // 新增相关
      showAddModal: false,
      newRowData: {}
    }
  },
  computed: {
    totalTables() {
      return this.tables.length
    },
    totalColumns() {
      return this.tables.reduce((sum, table) => sum + (table.columnCount || 0), 0)
    },
    totalRecords() {
      return this.tables.reduce((sum, table) => sum + (table.recordCount || 0), 0)
    }
  },
  mounted() {
    this.loadTables()
  },
  methods: {
    async loadTables() {
      this.loading = true
      this.error = null
      try {
        console.log('开始请求数据库表信息...')
        const response = await fetch('/api/database/tables')
        console.log('响应状态:', response.status, response.statusText)
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status} - ${response.statusText}`)
        }
        
        const result = await response.json()
        console.log('API响应:', result)
        
        if (result.code === 1) {
          this.tables = result.data.tables || []
          this.lastUpdateTime = new Date().toLocaleString('zh-CN')
          console.log('成功加载表信息:', this.tables)
        } else {
          this.error = result.msg || '获取表信息失败'
          console.error('获取表信息失败:', result.msg)
        }
      } catch (error) {
        this.error = `请求失败: ${error.message}`
        console.error('请求失败:', error)
      } finally {
        this.loading = false
      }
    },
    
    async testConnection() {
      try {
        const response = await fetch('/api/database/test')
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        const result = await response.json()
        
        if (result.code === 1) {
          this.connectionStatus = 'success'
          this.connectionMessage = '数据库连接正常！'
          console.log('连接测试结果:', result)
        } else {
          this.connectionStatus = 'error'
          this.connectionMessage = `连接测试失败: ${result.msg}`
        }
      } catch (error) {
        this.connectionStatus = 'error'
        this.connectionMessage = `连接测试失败: ${error.message}`
        console.error('连接测试失败:', error)
      }
    },
    
    clearConnectionStatus() {
      this.connectionStatus = null
      this.connectionMessage = ''
    },
    
    refreshTables() {
      this.loadTables()
    },
    
    viewTableDetails(table) {
      this.selectedTable = table
      this.showTableModal = true
    },
    
    closeTableModal() {
      this.showTableModal = false
      this.selectedTable = null
    },
    
    viewTableData(table) {
      this.selectedTableData = table
      this.currentPage = 1
      this.pageSize = 10
      this.showDataModal = true
      this.loadTableData()
    },
    
    closeDataModal() {
      this.showDataModal = false
      this.selectedTableData = null
      this.tableData = []
      this.tableColumns = []
    },
    
    async loadTableData() {
      if (!this.selectedTableData) return
      
      this.dataLoading = true
      try {
        const response = await fetch(`/api/database/table/${this.selectedTableData.tableName}/data?page=${this.currentPage}&size=${this.pageSize}`)
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        
        const result = await response.json()
        if (result.code === 1) {
          this.tableData = result.data.data || []
          this.totalPages = result.data.totalPages || 1
          
          // 提取列名
          if (this.tableData.length > 0) {
            this.tableColumns = Object.keys(this.tableData[0])
          }
        } else {
          this.connectionStatus = 'error'
          this.connectionMessage = `加载表数据失败: ${result.msg}`
        }
      } catch (error) {
        this.connectionStatus = 'error'
        this.connectionMessage = `加载表数据失败: ${error.message}`
        console.error('加载表数据失败:', error)
      } finally {
        this.dataLoading = false
      }
    },
    
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--
        this.loadTableData()
      }
    },
    
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++
        this.loadTableData()
      }
    },
    
    getTableDescription(tableName) {
      const descriptions = {
        'transacation_record_test': '交易记录测试表',
        'user': '用户表',
        'order': '订单表',
        'product': '商品表'
      }
      return descriptions[tableName] || '数据库表'
    },
    
    async exportTable(table) {
      try {
        // 获取表数据
        const response = await fetch(`/api/database/table/${table.tableName}/data?page=1&size=1000`)
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        
        const result = await response.json()
        if (result.code === 1) {
          const exportData = {
            tableName: table.tableName,
            columns: table.columns,
            recordCount: table.recordCount,
            data: result.data.data || [],
            exportTime: new Date().toISOString()
          }
          
          const blob = new Blob([JSON.stringify(exportData, null, 2)], { type: 'application/json' })
          const url = URL.createObjectURL(blob)
          const a = document.createElement('a')
          a.href = url
          a.download = `${table.tableName}_data.json`
          document.body.appendChild(a)
          a.click()
          document.body.removeChild(a)
          URL.revokeObjectURL(url)
          
          this.connectionStatus = 'success'
          this.connectionMessage = `表数据导出成功: ${table.tableName}`
        } else {
          this.connectionStatus = 'error'
          this.connectionMessage = `导出失败: ${result.msg}`
        }
      } catch (error) {
        this.connectionStatus = 'error'
        this.connectionMessage = `导出失败: ${error.message}`
        console.error('导出失败:', error)
      }
    },
    
    exportMetadata() {
      const data = {
        tables: this.tables,
        totalTables: this.totalTables,
        totalColumns: this.totalColumns,
        totalRecords: this.totalRecords,
        exportTime: new Date().toISOString()
      }
      
      const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = 'database_metadata.json'
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      URL.revokeObjectURL(url)
      
      this.connectionStatus = 'success'
      this.connectionMessage = '元数据导出成功'
    },

    editRow(row, index) {
      this.editingRow = row;
      this.editingIndex = index;
      this.editingRowData = { ...row }; // 复制当前行数据到编辑数据
      this.showEditModal = true;
    },

    closeEditModal() {
      this.showEditModal = false;
      this.editingRow = null;
      this.editingIndex = null;
      this.editingRowData = {};
    },

    async saveEditRow() {
      if (!this.editingRow) return;

      this.dataLoading = true;
      try {
        const response = await fetch(`/api/database/table/${this.selectedTableData.tableName}/update`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            id: this.editingRow.id, // 假设id是主键
            ...this.editingRowData
          })
        });
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const result = await response.json();
        if (result.code === 1) {
          this.tableData[this.editingIndex] = { ...this.editingRow, ...this.editingRowData }; // 更新列表中的数据
          this.closeEditModal();
          this.connectionStatus = 'success';
          this.connectionMessage = `记录更新成功: ${this.selectedTableData.tableName}`;
        } else {
          this.connectionStatus = 'error';
          this.connectionMessage = `更新失败: ${result.msg}`;
        }
      } catch (error) {
        this.connectionStatus = 'error';
        this.connectionMessage = `更新失败: ${error.message}`;
        console.error('更新失败:', error);
      } finally {
        this.dataLoading = false;
      }
    },

    deleteRow(row, index) {
      // 模拟删除，实际需要发送删除请求
      console.log('删除记录:', row, '索引:', index);
      // 例如，可以发送一个删除请求到后端
      // try {
      //   const response = await fetch(`/api/database/table/${this.selectedTableData.tableName}/delete`, {
      //     method: 'POST',
      //     headers: {
      //       'Content-Type': 'application/json'
      //     },
      //     body: JSON.stringify({ id: row.id }) // 假设id是主键
      //   });
      //   if (!response.ok) {
      //     throw new Error(`HTTP error! status: ${response.status}`);
      //   }
      //   const result = await response.json();
      //   if (result.code === 1) {
      //     this.tableData.splice(index, 1);
      //     this.totalPages = Math.ceil(this.tableData.length / this.pageSize);
      //     this.currentPage = Math.min(this.currentPage, this.totalPages);
      //     this.loadTableData(); // 重新加载数据
      //     this.connectionStatus = 'success';
      //     this.connectionMessage = `记录删除成功: ${this.selectedTableData.tableName}`;
      //   } else {
      //     this.connectionStatus = 'error';
      //     this.connectionMessage = `删除失败: ${result.msg}`;
      //   }
      // } catch (error) {
      //   this.connectionStatus = 'error';
      //   this.connectionMessage = `删除失败: ${error.message}`;
      //   console.error('删除失败:', error);
      // } finally {
      //   this.dataLoading = false;
      // }
      
      // 确认删除
      if (confirm(`确定要删除这条记录吗？\n${JSON.stringify(row, null, 2)}`)) {
        this.performDelete(row, index);
      }
    },

    async performDelete(row, index) {
      this.dataLoading = true;
      try {
        const response = await fetch(`/api/database/table/${this.selectedTableData.tableName}/delete`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ id: row.id }) // 假设id是主键
        });
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const result = await response.json();
        if (result.code === 1) {
          this.tableData.splice(index, 1);
          this.totalPages = Math.ceil(this.tableData.length / this.pageSize);
          this.currentPage = Math.min(this.currentPage, this.totalPages);
          this.loadTableData(); // 重新加载数据
          this.connectionStatus = 'success';
          this.connectionMessage = `记录删除成功: ${this.selectedTableData.tableName}`;
        } else {
          this.connectionStatus = 'error';
          this.connectionMessage = `删除失败: ${result.msg}`;
        }
      } catch (error) {
        this.connectionStatus = 'error';
        this.connectionMessage = `删除失败: ${error.message}`;
        console.error('删除失败:', error);
      } finally {
        this.dataLoading = false;
      }
    },

    refreshTableData() {
      this.loadTableData();
    },

    addNewRow() {
      this.newRowData = {}; // 清空新增数据
      this.showAddModal = true;
    },

    closeAddModal() {
      this.showAddModal = false;
      this.newRowData = {};
    },

    async saveNewRow() {
      if (!this.selectedTableData) return;

      this.dataLoading = true;
      try {
        const response = await fetch(`/api/database/table/${this.selectedTableData.tableName}/add`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.newRowData)
        });
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const result = await response.json();
        if (result.code === 1) {
          this.tableData.push(result.data); // 添加到列表
          this.totalPages = Math.ceil((this.tableData.length + 1) / this.pageSize); // 更新总页数
          this.currentPage = Math.min(this.currentPage, this.totalPages); // 确保当前页不超过总页数
          this.loadTableData(); // 重新加载数据
          this.closeAddModal();
          this.connectionStatus = 'success';
          this.connectionMessage = `记录新增成功: ${this.selectedTableData.tableName}`;
        } else {
          this.connectionStatus = 'error';
          this.connectionMessage = `新增失败: ${result.msg}`;
        }
      } catch (error) {
        this.connectionStatus = 'error';
        this.connectionMessage = `新增失败: ${error.message}`;
        console.error('新增失败:', error);
      } finally {
        this.dataLoading = false;
      }
    },

    isPrimaryKey(columnName) {
      // 假设 'id' 是主键
      return columnName === 'id';
    },

    isRequired(columnName) {
      // 假设 'id' 是主键，且不允许为空
      return columnName !== 'id';
    },

    getColumnPlaceholder(columnName) {
      // 根据列名设置占位符
      if (columnName === 'username') return '请输入用户名';
      if (columnName === 'password') return '请输入密码';
      if (columnName === 'email') return '请输入邮箱';
      return '请输入值';
    }
  }
}
</script>

<style scoped>
.rounded-button {
  border-radius: 8px;
}
</style> 
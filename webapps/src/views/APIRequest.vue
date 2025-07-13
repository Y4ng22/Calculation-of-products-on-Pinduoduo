<template>
  <div class="api-request-page">
    <!-- 头部区域 -->
    <div class="header-section">
      <div class="header-content">
        <div class="logo-section">
          <div class="logo">
            <el-icon class="logo-icon"><Shop /></el-icon>
          </div>
          <div class="title">
            <h1>拼多多开发平台</h1>
            <p>API 接口测试与商品管理</p>
          </div>
        </div>
        <div class="header-actions">
          <el-button type="primary" :icon="Document" @click="exportData">
            导出数据
          </el-button>
          <el-button :icon="Setting" @click="showSettings">
            设置
          </el-button>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <el-container>
        <el-main>
          <!-- API请求表单 -->
          <ApiRequestForm 
            @submit="handleApiSubmit"
            @reset="handleReset"
          />

          <!-- Excel操作区域 -->
          <el-card class="excel-actions-card" shadow="never">
            <template #header>
              <div class="excel-actions-header">
                <el-icon><Document /></el-icon>
                <span>Excel数据导入</span>
              </div>
            </template>
            <div class="excel-actions-content">
              <div class="excel-actions-description">
                <p>支持从Excel文件中导入商品数据，请确保Excel文件包含正确的表头和数据格式。</p>
              </div>
              <div class="excel-actions-buttons">
                <el-upload
                  ref="excelUpload"
                  :show-file-list="false"
                  :before-upload="handleExcelUpload"
                  accept=".xlsx,.xls"
                  :auto-upload="false"
                  @change="handleFileChange"
                >
                  <el-button 
                    type="success" 
                    :icon="Upload"
                    :loading="excelUploading"
                    :disabled="excelUploading"
                    size="large"
                  >
                    {{ excelUploading ? '解析中...' : '上传Excel文件' }}
                  </el-button>
                </el-upload>
                <el-button 
                  type="info" 
                  :icon="Download"
                  @click="downloadExcelTemplate"
                  size="large"
                >
                  下载Excel模板
                </el-button>
                <el-button
                  type="primary"
                  :icon="Document"
                  @click="excelHistoryVisible = true"
                  size="large"
                >
                  查看历史记录
                </el-button>
              </div>
              <div v-if="excelFileName" class="excel-upload-info">
                <el-tag type="success" :icon="Upload">
                  正在处理: {{ excelFileName }}
                </el-tag>
              </div>
              
              <!-- 上传文件历史 -->
              <div v-if="uploadedFiles.length > 0" class="uploaded-files-section">
                <el-divider content-position="left">
                  <el-icon><Document /></el-icon>
                  <span>上传历史</span>
                </el-divider>
                
                <div class="uploaded-files-list">
                  <div 
                    v-for="(file, index) in uploadedFiles" 
                    :key="index"
                    class="uploaded-file-item"
                    :class="{ 'success': file.status === 'success', 'error': file.status === 'error' }"
                  >
                    <div class="file-info">
                      <div class="file-name">
                        <el-icon class="file-icon">
                          <Document v-if="file.status === 'success'" />
                          <Box v-else />
                        </el-icon>
                        <span class="name">{{ file.name }}</span>
                        <el-tag 
                          :type="file.status === 'success' ? 'success' : 'danger'" 
                          size="small"
                          style="margin-left: 8px"
                        >
                          {{ file.status === 'success' ? '成功' : '失败' }}
                        </el-tag>
                      </div>
                      <div class="file-details">
                        <span class="upload-time">
                          <el-icon><Clock /></el-icon>
                          {{ formatTime(file.uploadTime) }}
                        </span>
                        <span class="file-size">
                          <el-icon><Files /></el-icon>
                          {{ formatFileSize(file.size) }}
                        </span>
                        <span v-if="file.status === 'success'" class="product-count">
                          <el-icon><Shop /></el-icon>
                          {{ file.productCount }} 条商品
                        </span>
                        <span v-if="file.status === 'error'" class="error-message">
                          <el-icon><Warning /></el-icon>
                          {{ file.errorMessage }}
                        </span>
                      </div>
                    </div>
                    <div class="file-actions">
                      <el-button 
                        v-if="file.status === 'success'"
                        type="primary" 
                        size="small" 
                        @click="viewFileProducts(file)"
                      >
                        查看商品
                      </el-button>
                      <el-button 
                        type="danger" 
                        size="small" 
                        @click="removeFileHistory(index)"
                      >
                        删除记录
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 结果展示区域 -->
          <el-card class="result-card" shadow="never">
            <template #header>
              <div class="result-header">
                <el-icon><DataAnalysis /></el-icon>
                <span>查询结果</span>
                <div class="result-stats">
                  <el-tag type="info">总数: {{ totalCount }}</el-tag>
                  <el-tag type="success">成功: {{ successCount }}</el-tag>
                  <el-tag type="warning">处理中: {{ processingCount }}</el-tag>
                </div>
              </div>
            </template>

            <!-- 加载状态 -->
            <div v-if="loading" class="loading-container">
              <el-skeleton :rows="6" animated />
            </div>

            <!-- 错误状态 -->
            <div v-else-if="error" class="error-container">
              <el-result
                icon="error"
                :title="error.title"
                :sub-title="error.message"
              >
                <template #extra>
                  <el-button type="primary" @click="retryRequest">
                    重试
                  </el-button>
                </template>
              </el-result>
            </div>

            <!-- JSON结果展示 -->
            <div v-if="apiResponse !== null" class="json-result-section">
              <el-collapse v-model="jsonCollapseActive">
                <el-collapse-item title="API 返回结果 (JSON)" name="json">
                  <template #title>
                    <div class="json-header">
                      <el-icon><Document /></el-icon>
                      <span>API 返回结果 (JSON)</span>
                      <el-tag type="success" size="small" style="margin-left: 10px">
                        状态码: {{ apiResponse.status || 200 }}
                      </el-tag>
                      <el-tag type="info" size="small" style="margin-left: 5px">
                        数据量: {{ apiResponse.data?.length || 0 }} 条
                      </el-tag>
                      <el-tag 
                        v-if="apiResponse.databaseStatus" 
                        :type="apiResponse.databaseStatus === 'success' ? 'success' : 'danger'" 
                        size="small" 
                        style="margin-left: 5px"
                      >
                        {{ apiResponse.databaseStatus === 'success' ? '已写入数据库' : '数据库写入失败' }}
                      </el-tag>
                    </div>
                  </template>
                  
                  <div class="json-content">
                    <div class="json-toolbar">
                      <el-button-group>
                        <el-button 
                          size="small" 
                          @click="copyJson"
                          :icon="CopyDocument"
                        >
                          复制JSON
                        </el-button>
                        <el-button 
                          size="small" 
                          @click="downloadJson"
                          :icon="Download"
                        >
                          下载JSON
                        </el-button>
                        <el-button 
                          size="small" 
                          @click="formatJson"
                          :icon="Edit"
                        >
                          格式化
                        </el-button>
                      </el-button-group>
                      
                      <el-switch
                        v-model="jsonSyntaxHighlight"
                        active-text="语法高亮"
                        inactive-text="普通文本"
                        size="small"
                      />
                    </div>
                    
                    <div class="json-display">
                      <pre v-if="jsonSyntaxHighlight" class="json-highlight">{{ formattedJson }}</pre>
                      <el-input
                        v-else
                        v-model="formattedJson"
                        type="textarea"
                        :rows="12"
                        readonly
                        class="json-textarea"
                      />
                    </div>
                  </div>
                </el-collapse-item>
              </el-collapse>
            </div>

            <!-- 商品展示区域 -->
            <div v-if="apiResponse || products.length > 0" class="products-section">
              <el-card class="products-card" shadow="never">
                <template #header>
                  <div class="products-header">
                    <el-icon><Shop /></el-icon>
                    <span>商品信息展示</span>
                    <div class="products-stats">
                      <el-tag type="info">总数: {{ products.length }}</el-tag>
                      <el-tag v-if="dataSource !== '暂无数据'" type="success" style="margin-left: 5px">
                        {{ dataSource }}
                      </el-tag>
                    </div>
                  </div>
                </template>
                
                <!-- 商品列表 -->
                <div class="products-content">
                  <el-table 
                    :data="pagedProducts" 
                    style="width: 100%"
                    v-loading="productsLoading"
                    empty-text="暂无商品数据"
                  >
                    <el-table-column prop="id" label="商品ID" width="120" />
                    <el-table-column prop="name" label="商品名称" min-width="200" />
                    <el-table-column prop="image" label="商品图片" width="120">
                      <template #default="{ row }">
                        <el-image 
                          v-if="row.image"
                          :src="row.image" 
                          style="width: 80px; height: 80px"
                          fit="cover"
                          :preview-src-list="[row.image]"
                        />
                        <span v-else class="no-image">暂无图片</span>
                      </template>
                    </el-table-column>
                    <el-table-column prop="cost" label="进货价" width="100">
                      <template #default="{ row }">
                        <span v-if="row.cost" class="cost-price">¥{{ row.cost }}</span>
                        <span v-else>-</span>
                      </template>
                    </el-table-column>
                    <el-table-column prop="price" label="卖出价" width="100">
                      <template #default="{ row }">
                        <span v-if="row.price" class="sale-price">¥{{ row.price }}</span>
                        <span v-else>-</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="利润" width="100">
                      <template #default="{ row }">
                        <span v-if="row.cost && row.price" class="profit" :class="{ 'positive': (row.price - row.cost) > 0, 'negative': (row.price - row.cost) < 0 }">
                          ¥{{ (row.price - row.cost).toFixed(2) }}
                        </span>
                        <span v-else>-</span>
                      </template>
                    </el-table-column>
                    <el-table-column prop="category" label="分类" width="120" />
                    <el-table-column prop="status" label="状态" width="100">
                      <template #default="{ row }">
                        <el-tag 
                          v-if="row.status"
                          :type="getStatusType(row.status)"
                          size="small"
                        >
                          {{ row.status }}
                        </el-tag>
                        <span v-else>-</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="操作" width="120" fixed="right">
                      <template #default="{ row }">
                        <el-button size="small" @click="viewProductDetail(row)">
                          详情
                        </el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                  <div style="margin: 20px 0; text-align: right;" v-if="products.length > 0">
                    <el-pagination
                      background
                      layout="total, sizes, prev, pager, next, jumper"
                      :total="products.length"
                      :page-size="pageSize"
                      :current-page="currentPage"
                      @size-change="val => { pageSize = val; currentPage = 1; }"
                      @current-change="val => { currentPage = val; }"
                      :page-sizes="[5, 10, 20, 50]"
                    />
                  </div>
                </div>
                
                <!-- 计算滚动成本区域 -->
                <div class="profit-calculation-section">
                  <el-divider content-position="left">
                    <el-icon><DataAnalysis /></el-icon>
                    <span>利润计算</span>
                  </el-divider>
                  
                  <div class="calculation-content">
                    <div class="calculation-stats">
                      <el-row :gutter="20">
                        <el-col :span="6">
                          <div class="stat-card">
                            <div class="stat-title">商品总数</div>
                            <div class="stat-value">{{ products.length }}</div>
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="stat-card">
                            <div class="stat-title">总成本</div>
                            <div class="stat-value cost">¥{{ totalCost.toFixed(2) }}</div>
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="stat-card">
                            <div class="stat-title">总售价</div>
                            <div class="stat-value revenue">¥{{ totalRevenue.toFixed(2) }}</div>
                          </div>
                        </el-col>
                        <el-col :span="6">
                          <div class="stat-card">
                            <div class="stat-title">总利润</div>
                            <div class="stat-value profit" :class="{ 'positive': totalProfit > 0, 'negative': totalProfit < 0 }">
                              ¥{{ totalProfit.toFixed(2) }}
                            </div>
                          </div>
                        </el-col>
                      </el-row>
                    </div>
                    
                    <div class="calculation-actions">
                      <el-button 
                        type="primary" 
                        @click="calculateProfit"
                        :loading="calculating"
                        :icon="DataAnalysis"
                        size="large"
                      >
                        计算滚动成本
                      </el-button>
                      <el-button 
                        @click="exportProfitReport"
                        :icon="Download"
                        size="large"
                      >
                        导出利润报告
                      </el-button>
                      <el-button 
                        type="success"
                        @click="writeToDatabase"
                        :loading="writingToDatabase"
                        :disabled="!apiResponse"
                        :icon="Document"
                        size="large"
                      >
                        写入数据库
                      </el-button>
                    </div>
                    
                    <!-- 计算结果显示 -->
                    <div v-if="showCalculationResult" class="calculation-result">
                      <el-alert
                        :title="calculationResultTitle"
                        :type="calculationResultType"
                        :description="calculationResultDescription"
                        show-icon
                        :closable="false"
                      />
                    </div>
                  </div>
                </div>
              </el-card>
            </div>

            <!-- 空状态 -->
            <div v-if="!apiResponse && !loading && !error" class="empty-container">
              <el-empty 
                description="暂无数据，请先发送API请求"
                :image-size="200"
              >
                <template #image>
                  <el-icon class="empty-icon"><Box /></el-icon>
                </template>
              </el-empty>
            </div>
          </el-card>
        </el-main>
      </el-container>
    </div>

    <!-- 商品详情对话框 -->
    <el-dialog
      v-model="productDetailVisible"
      title="商品详情"
      width="600px"
      :before-close="handleCloseProductDetail"
    >
      <div v-if="selectedProduct" class="product-detail">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-image 
              v-if="selectedProduct.image"
              :src="selectedProduct.image" 
              style="width: 100%; height: 200px"
              fit="cover"
              :preview-src-list="[selectedProduct.image]"
            />
            <div v-else class="no-image-placeholder">
              <el-icon class="no-image-icon"><Picture /></el-icon>
              <span>暂无图片</span>
            </div>
          </el-col>
          <el-col :span="12">
            <h3>{{ selectedProduct.name || '商品名称' }}</h3>
            <div class="detail-info">
              <p><strong>商品ID:</strong> {{ selectedProduct.id || '-' }}</p>
              <p><strong>进货价:</strong> 
                <span v-if="selectedProduct.cost" class="cost-price">¥{{ selectedProduct.cost }}</span>
                <span v-else>-</span>
              </p>
              <p><strong>卖出价:</strong> 
                <span v-if="selectedProduct.price" class="sale-price">¥{{ selectedProduct.price }}</span>
                <span v-else>-</span>
              </p>
              <p v-if="selectedProduct.cost && selectedProduct.price"><strong>利润:</strong> 
                <span class="profit" :class="{ 'positive': (selectedProduct.price - selectedProduct.cost) > 0, 'negative': (selectedProduct.price - selectedProduct.cost) < 0 }">
                  ¥{{ (selectedProduct.price - selectedProduct.cost).toFixed(2) }}
                </span>
              </p>
              <p><strong>分类:</strong> {{ selectedProduct.category || '-' }}</p>
              <p><strong>状态:</strong> 
                <el-tag 
                  v-if="selectedProduct.status"
                  :type="getStatusType(selectedProduct.status)"
                  size="small"
                >
                  {{ selectedProduct.status }}
                </el-tag>
                <span v-else>-</span>
              </p>
              <p v-if="selectedProduct.description"><strong>描述:</strong> {{ selectedProduct.description }}</p>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-dialog>

    <!-- Excel上传历史记录弹窗 -->
    <el-dialog
      v-model="excelHistoryVisible"
      title="Excel上传历史记录"
      width="700px"
      :close-on-click-modal="false"
    >
      <div v-if="uploadedFiles.length === 0" style="text-align:center; color:#999; padding:40px 0;">
        暂无上传历史
      </div>
      <div v-else>
        <div class="uploaded-files-list">
          <div 
            v-for="(file, index) in uploadedFiles" 
            :key="index"
            class="uploaded-file-item"
            :class="{ 'success': file.status === 'success', 'error': file.status === 'error' }"
          >
            <div class="file-info">
              <div class="file-name">
                <el-icon class="file-icon">
                  <Document v-if="file.status === 'success'" />
                  <Box v-else />
                </el-icon>
                <span class="name">{{ file.name }}</span>
                <el-tag 
                  :type="file.status === 'success' ? 'success' : 'danger'" 
                  size="small"
                  style="margin-left: 8px"
                >
                  {{ file.status === 'success' ? '成功' : '失败' }}
                </el-tag>
              </div>
              <div class="file-details">
                <span class="upload-time">
                  <el-icon><Clock /></el-icon>
                  {{ formatTime(file.uploadTime) }}
                </span>
                <span class="file-size">
                  <el-icon><Files /></el-icon>
                  {{ formatFileSize(file.size) }}
                </span>
                <span v-if="file.status === 'success'" class="product-count">
                  <el-icon><Shop /></el-icon>
                  {{ file.productCount }} 条商品
                </span>
                <span v-if="file.status === 'error'" class="error-message">
                  <el-icon><Warning /></el-icon>
                  {{ file.errorMessage }}
                </span>
              </div>
            </div>
            <div class="file-actions">
              <el-button 
                v-if="file.status === 'success'"
                type="primary" 
                size="small" 
                @click="viewFileProducts(file)"
              >
                查看商品
              </el-button>
              <el-button 
                type="danger" 
                size="small" 
                @click="removeFileHistory(index)"
              >
                删除记录
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { 
  Shop, 
  Document, 
  Setting, 
  DataAnalysis, 
  Box,
  CopyDocument,
  Download,
  Edit,
  Picture,
  Upload,
  Clock,
  Files,
  Warning
} from '@element-plus/icons-vue'
import ApiRequestForm from '@/components/ApiRequestForm.vue'
import axios from 'axios'
import * as XLSX from 'xlsx'

export default {
  name: 'APIRequest',
  components: {
    ApiRequestForm,
    Shop,
    Document,
    Setting,
    DataAnalysis,
    Box,
    Picture,
    Upload,
    Clock,
    Files,
    Warning
  },
  data() {
    return {
      loading: false,
      error: null,
      apiResponse: null,
      jsonCollapseActive: ['json'],
      jsonSyntaxHighlight: true,
      // 商品展示相关
      products: [],
      productsLoading: false,
      productDetailVisible: false,
      selectedProduct: null,
      // 利润计算相关
      calculating: false,
      showCalculationResult: false,
      calculationResultTitle: '',
      calculationResultType: 'info',
      calculationResultDescription: '',
      // 数据库写入相关
      writingToDatabase: false,
      // Excel上传相关
      excelUploading: false,
      excelFileName: '',
      uploadedFiles: [], // 存储已上传的文件信息
      // 商品分页相关
      currentPage: 1,
      pageSize: 5
    }
  },
  computed: {
    formattedJson() {
      if (!this.apiResponse) return ''
      try {
        // 直接显示API响应，不做任何包装
        return JSON.stringify(this.apiResponse, null, 2)
      } catch (error) {
        return 'JSON格式化失败'
      }
    },
    
    // 利润计算相关计算属性
    totalCost() {
      return this.products.reduce((sum, product) => {
        const cost = parseFloat(product.cost || product.purchasePrice || 0)
        return sum + (isNaN(cost) ? 0 : cost)
      }, 0)
    },
    
    totalRevenue() {
      return this.products.reduce((sum, product) => {
        const revenue = parseFloat(product.price || product.salePrice || 0)
        return sum + (isNaN(revenue) ? 0 : revenue)
      }, 0)
    },
    
    totalProfit() {
      return this.totalRevenue - this.totalCost
    },
    
    // 数据来源信息
    dataSource() {
      if (this.excelFileName) {
        return `Excel文件: ${this.excelFileName}`
      }
      if (this.apiResponse && this.apiResponse.message === 'Excel数据导入成功') {
        return 'Excel数据导入'
      }
      if (this.apiResponse) {
        return 'API接口数据'
      }
      return '暂无数据'
    },
    
    totalCount() {
      return this.products.length
    },
    
    successCount() {
      return this.products.filter(p => p.status === 'active' || p.status === 'on_sale').length
    },
    
    processingCount() {
      return this.products.filter(p => p.status === 'pending' || p.status === 'processing').length
    },

    pagedProducts() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.products.slice(start, end)
    }
  },
  methods: {
    async handleApiSubmit(requestData) {
      if (!requestData.url) {
        this.$message.error('请输入接口路径')
        return
      }

      this.loading = true
      this.error = null
      this.apiResponse = null

      try {
        // 构建请求配置
        const config = {
          method: requestData.method,
          url: requestData.url,
          headers: {}
        }
        
        // 添加请求头
        requestData.headers.forEach(header => {
          if (header.key && header.value) {
            config.headers[header.key] = header.value
          }
        })
        
        // 处理请求参数
        if (requestData.method === 'GET') {
          const params = {}
          requestData.params.forEach(param => {
            if (param.key && param.value) {
              let value = param.value
              if (param.type === 'number') {
                value = parseFloat(value)
              } else if (param.type === 'boolean') {
                value = value === 'true'
              }
              params[param.key] = value
            }
          })
          config.params = params
        } else {
          const data = {}
          requestData.params.forEach(param => {
            if (param.key && param.value) {
              let value = param.value
              if (param.type === 'number') {
                value = parseFloat(value)
              } else if (param.type === 'boolean') {
                value = value === 'true'
              }
              data[param.key] = value
            }
          })
          config.data = data
        }

        const res = await axios(config)
        this.apiResponse = res.data
        this.error = null
        this.$message.success('请求成功')
        this.resetPagination()
        
        // 记录API调用（这里可以调用后端接口记录，但为了简化，暂时不记录外部API调用）
        
      } catch (err) {
        console.log('请求错误详情:', err)
        console.log('错误响应:', err.response)
        console.log('错误状态:', err.response?.status)
        console.log('错误数据:', err.response?.data)
        
        // 更详细的错误处理
        if (err.response) {
          // 服务器返回了响应，但状态码不在2xx范围内
          this.apiResponse = {
            status: err.response.status,
            statusText: err.response.statusText,
            data: err.response.data,
            headers: err.response.headers
          }
          this.error = {
            title: `请求失败 (${err.response.status})`,
            message: err.response.statusText || err.message
          }
        } else if (err.request) {
          // 请求已发出，但没有收到响应
          this.apiResponse = {
            error: 'Network Error',
            message: '没有收到服务器响应，可能是跨域问题或网络连接问题',
            details: err.message
          }
          this.error = {
            title: '网络错误',
            message: '没有收到服务器响应，请检查：1. 后端服务是否启动 2. 端口是否正确 3. 是否有跨域问题'
          }
        } else {
          // 其他错误
          this.apiResponse = {
            error: err.message,
            type: 'Request Error'
          }
          this.error = {
            title: '请求错误',
            message: err.message
          }
        }
        
        this.$message.error('请求失败')
      } finally {
        this.loading = false
      }
    },
    

    
    handleReset() {
      this.apiResponse = null
      this.error = null
      this.products = []
      this.excelFileName = ''
      this.uploadedFiles = []
      this.resetPagination()
    },
    
    retryRequest() {
      // 重新发送最后一次请求
      this.$message.info('重试功能待实现')
    },
    
    exportData() {
      // 导出数据功能
      this.$message.info('导出功能待实现')
    },
    
    showSettings() {
      // 显示设置
      this.$message.info('设置功能待实现')
    },
    

    
    copyJson() {
      try {
        navigator.clipboard.writeText(this.formattedJson).then(() => {
          this.$message.success('JSON已复制到剪贴板')
        }).catch(() => {
          this.$message.error('复制失败，请手动复制')
        })
      } catch (error) {
        this.$message.error('复制失败')
      }
    },
    
    downloadJson() {
      try {
        const blob = new Blob([this.formattedJson], { type: 'application/json' })
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `api-response-${new Date().toISOString().slice(0, 19).replace(/:/g, '-')}.json`
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
        URL.revokeObjectURL(url)
        this.$message.success('JSON文件已下载')
      } catch (error) {
        this.$message.error('下载失败')
      }
    },
    
    formatJson() {
      // 这个方法主要是为了重新格式化JSON，实际上computed属性已经处理了
      this.$message.success('JSON已格式化')
    },
    
    // 商品展示相关方法
    viewProductDetail(product) {
      this.selectedProduct = product
      this.productDetailVisible = true
    },
    
    handleCloseProductDetail() {
      this.productDetailVisible = false
      this.selectedProduct = null
    },
    
    getStatusType(status) {
      const statusMap = {
        'active': 'success',
        'inactive': 'info',
        'pending': 'warning',
        'deleted': 'danger',
        'on_sale': 'success',
        'off_sale': 'info',
        'sold_out': 'warning'
      }
      return statusMap[status] || 'info'
    },
    
    // 利润计算相关方法
    async calculateProfit() {
      if (this.products.length === 0) {
        this.$message.warning('暂无商品数据，无法计算利润')
        return
      }
      
      this.calculating = true
      this.showCalculationResult = false
      
      try {
        // 模拟计算过程
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        // 计算利润
        const profit = this.totalProfit
        const profitRate = this.totalCost > 0 ? (profit / this.totalCost) * 100 : 0
        
        // 设置结果显示
        this.calculationResultTitle = '利润计算完成'
        this.calculationResultType = profit >= 0 ? 'success' : 'warning'
        this.calculationResultDescription = `
          商品总数: ${this.products.length} 个
          总成本: ¥${this.totalCost.toFixed(2)}
          总售价: ¥${this.totalRevenue.toFixed(2)}
          总利润: ¥${profit.toFixed(2)}
          利润率: ${profitRate.toFixed(2)}%
        `
        
        this.showCalculationResult = true
        
        // 记录计算操作到后端
        try {
          await axios.post('http://localhost:8089/api/database/stats/calculation', {
            productCount: this.products.length,
            totalCost: this.totalCost,
            totalRevenue: this.totalRevenue,
            totalProfit: profit,
            profitRate: profitRate,
            calculationTime: new Date().toISOString()
          })
        } catch (error) {
          console.log('记录计算操作失败:', error)
          // 不影响主要功能，只记录日志
        }
        
        // 显示成功消息
        if (profit >= 0) {
          this.$message.success(`利润计算完成！总利润: ¥${profit.toFixed(2)}`)
        } else {
          this.$message.warning(`利润计算完成！总亏损: ¥${Math.abs(profit).toFixed(2)}`)
        }
        
      } catch (error) {
        this.$message.error('利润计算失败')
        console.error('利润计算错误:', error)
      } finally {
        this.calculating = false
      }
    },
    
    exportProfitReport() {
      if (this.products.length === 0) {
        this.$message.warning('暂无商品数据，无法导出报告')
        return
      }
      
      try {
        // 生成报告数据
        const reportData = {
          calculationTime: new Date().toISOString(),
          summary: {
            totalProducts: this.products.length,
            totalCost: this.totalCost,
            totalRevenue: this.totalRevenue,
            totalProfit: this.totalProfit,
            profitRate: this.totalCost > 0 ? (this.totalProfit / this.totalCost) * 100 : 0
          },
          products: this.products.map(product => ({
            id: product.id,
            name: product.name,
            cost: parseFloat(product.cost || product.purchasePrice || 0),
            price: parseFloat(product.price || product.salePrice || 0),
            profit: parseFloat(product.price || product.salePrice || 0) - parseFloat(product.cost || product.purchasePrice || 0)
          }))
        }
        
        // 创建并下载文件
        const blob = new Blob([JSON.stringify(reportData, null, 2)], { type: 'application/json' })
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `profit-report-${new Date().toISOString().slice(0, 19).replace(/:/g, '-')}.json`
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
        URL.revokeObjectURL(url)
        
        this.$message.success('利润报告已导出')
      } catch (error) {
        this.$message.error('导出失败')
        console.error('导出错误:', error)
      }
    },
    
    // 文件选择变化事件
    handleFileChange(file, fileList) {
      // 如果文件存在，手动调用Excel上传处理
      if (file && file.raw) {
        this.handleExcelUpload(file.raw)
      }
    },
    
    // Excel上传处理方法
    async handleExcelUpload(file) {
      
      // 先进行简单的文件验证
      const isExcel = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' || 
                     file.type === 'application/vnd.ms-excel' ||
                     file.name.endsWith('.xlsx') || 
                     file.name.endsWith('.xls')
      
      if (!isExcel) {
        // 即使不是Excel文件，也记录到历史中
        const fileInfo = {
          name: file.name,
          size: file.size,
          uploadTime: new Date(),
          status: 'error',
          errorMessage: '请上传Excel文件 (.xlsx 或 .xls)'
        }
        this.uploadedFiles.unshift(fileInfo)
        this.$message.error('请上传Excel文件 (.xlsx 或 .xls)')
        return false
      }
      
      // 验证文件大小 (限制为10MB)
      const isLt10M = file.size / 1024 / 1024 < 10
      
      if (!isLt10M) {
        // 记录文件过大的错误
        const fileInfo = {
          name: file.name,
          size: file.size,
          uploadTime: new Date(),
          status: 'error',
          errorMessage: '文件大小不能超过10MB'
        }
        this.uploadedFiles.unshift(fileInfo)
        this.$message.error('文件大小不能超过10MB')
        return false
      }
      
      this.excelUploading = true
      this.excelFileName = file.name
      
      try {
        // 检查XLSX是否可用
        if (!XLSX) {
          throw new Error('XLSX库未正确加载')
        }
        
        const data = await this.readExcelFile(file)
        this.processExcelData(data)
        
        // 记录上传成功的文件信息
        const fileInfo = {
          name: file.name,
          size: file.size,
          uploadTime: new Date(),
          status: 'success',
          productCount: this.products.length
        }
        this.uploadedFiles.unshift(fileInfo) // 添加到列表开头

        
        this.$message.success(`Excel文件 "${file.name}" 解析成功，共导入 ${this.products.length} 条商品数据`)
      } catch (error) {
        
        // 记录上传失败的文件信息
        const fileInfo = {
          name: file.name,
          size: file.size,
          uploadTime: new Date(),
          status: 'error',
          errorMessage: error.message
        }
        this.uploadedFiles.unshift(fileInfo) // 添加到列表开头

        
        this.$message.error('Excel文件解析失败: ' + error.message)
      } finally {
        this.excelUploading = false
        this.excelFileName = ''
      }
      
      return false // 阻止默认上传行为
    },
    
    // 读取Excel文件
    readExcelFile(file) {
      return new Promise((resolve, reject) => {
        const reader = new FileReader()
        
        reader.onload = (e) => {
          try {
            const data = new Uint8Array(e.target.result)
            const workbook = XLSX.read(data, { type: 'array' })
            
            // 获取第一个工作表
            const firstSheetName = workbook.SheetNames[0]
            const worksheet = workbook.Sheets[firstSheetName]
            
            // 将工作表转换为JSON
            const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 })
            
            resolve(jsonData)
          } catch (error) {
            reject(new Error('Excel文件格式错误: ' + error.message))
          }
        }
        
        reader.onerror = () => {
          reject(new Error('文件读取失败'))
        }
        
        reader.readAsArrayBuffer(file)
      })
    },
    
    // 处理Excel数据
    processExcelData(data) {
      if (!data || data.length < 2) {
        throw new Error('Excel文件数据不足，至少需要包含表头和一行数据')
      }
      
      const headers = data[0] // 第一行作为表头
      const rows = data.slice(1) // 其余行作为数据
      
      // 清空现有商品数据
      this.products = []
      
      // 处理每一行数据
      rows.forEach((row, index) => {
        if (row.length === 0 || row.every(cell => !cell)) {
          return // 跳过空行
        }
        
        const product = {}
        
        // 根据表头映射数据
        headers.forEach((header, colIndex) => {
          if (header && row[colIndex] !== undefined) {
            const value = row[colIndex]
            
            // 根据表头名称映射到商品属性
            switch (header.toString().toLowerCase().trim()) {
              case 'id':
              case '商品id':
              case '商品编号':
                product.id = value
                break
              case 'name':
              case '商品名称':
              case '名称':
              case '标题':
                product.name = value
                break
              case 'price':
              case '价格':
              case '售价':
              case '销售价格':
              case '卖出价':
                product.price = parseFloat(value) || 0
                break
              case 'cost':
              case '成本':
              case '进价':
              case '采购价格':
              case '进货价':
                product.cost = parseFloat(value) || 0
                break
              case 'category':
              case '分类':
              case '类别':
              case '商品分类':
                product.category = value
                break
              case 'status':
              case '状态':
              case '商品状态':
                product.status = value
                break
              case 'image':
              case '图片':
              case '商品图片':
              case '图片链接':
                product.image = value
                break
              case 'description':
              case '描述':
              case '商品描述':
              case '详情':
                product.description = value
                break
              default:
                // 对于未识别的列，直接添加到商品对象中
                product[header] = value
            }
          }
        })
        
        // 如果商品有基本信息，则添加到商品列表
        if (product.id || product.name) {
          this.products.push(product)
        }
      })
      
      // 如果没有解析到任何商品，抛出错误
      if (this.products.length === 0) {
        throw new Error('未找到有效的商品数据，请检查Excel文件格式')
      }
      
      // 模拟API响应格式，以便与现有逻辑兼容
      this.apiResponse = {
        status: 200,
        data: this.products,
        message: 'Excel数据导入成功',
        total: this.products.length
      }
      
      // 清除错误状态
      this.error = null
      this.resetPagination()
    },
    
    // 下载Excel模板
    downloadExcelTemplate() {
      try {
        // 创建示例数据
        const templateData = [
          ['商品ID', '商品名称', '进货价', '卖出价', '分类', '状态', '图片链接', '描述'],
          ['P001', '立白洗衣液去渍', 80, 98, '生活用品', 'active', 'https://tse4-mm.cn.bing.net/th/id/OIP-C.45TxzesOc67PKw-3ihJriAHaHa?w=208&h=208&c=7&r=0&o=7&dpr=1.5&pid=1.7&rm=3', '洗衣液'],
          ['P002', '立白亮白护色洗衣液', 50, 88, '生活用品', 'active', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.NQD0ClnokHOAXwd5PAkw6wHaHa?w=212&h=212&c=7&r=0&o=7&dpr=1.7&pid=1.7&rm=3', '洗衣液'],
          ['P003', '立白大师香氛洗衣液', 60, 188, '生活用品', 'on_sale', 'https://tse3-mm.cn.bing.net/th/id/OIP-C.QhePUmkasB5ggpZzzQYj7wHaNU?w=115&h=206&c=7&r=0&o=7&dpr=1.7&pid=1.7&rm=3', '洗衣液'],
          ['P004', '立白除菌除螨洗衣液*4', 188, 288, '生活用品', 'pending', 'https://ts1.tc.mm.bing.net/th/id/OIP-C.aKm5UPLgXri6LF7Yp205fwHaIs?rs=1&pid=ImgDetMain&o=7&rm=3', '洗衣液'],
          ['P005', '立白超洁薰衣草', 68, 79, '生活用品', 'active', 'https://img.alicdn.com/bao/uploaded/i1/2208302994653/O1CN018vxw5Z1kF7fLCweqW_!!0-item_pic.jpg', '洗衣液'],
          ['P006', '立白超洁薰衣草1', 68, 79, '生活用品', 'active', 'https://img.alicdn.com/bao/uploaded/i1/2208302994653/O1CN018vxw5Z1kF7fLCweqW_!!0-item_pic.jpg', '洗衣液'],
          ['P007', '立白超洁薰衣草2', 68, 79, '生活用品', 'active', 'https://img.alicdn.com/bao/uploaded/i1/2208302994653/O1CN018vxw5Z1kF7fLCweqW_!!0-item_pic.jpg', '洗衣液'],
          ['P008', '立白超洁薰衣草3', 68, 79, '生活用品', 'active', 'https://img.alicdn.com/bao/uploaded/i1/2208302994653/O1CN018vxw5Z1kF7fLCweqW_!!0-item_pic.jpg', '洗衣液'],
          ['P009', '立白超洁薰衣草4', 68, 79, '生活用品', 'active', 'https://img.alicdn.com/bao/uploaded/i1/2208302994653/O1CN018vxw5Z1kF7fLCweqW_!!0-item_pic.jpg', '洗衣液']
        ]
        
        // 创建工作簿
        const workbook = XLSX.utils.book_new()
        const worksheet = XLSX.utils.aoa_to_sheet(templateData)
        
        // 设置列宽
        const colWidths = [
          { wch: 10 }, // 商品ID
          { wch: 20 }, // 商品名称
          { wch: 10 }, // 进货价
          { wch: 10 }, // 卖出价
          { wch: 15 }, // 分类
          { wch: 10 }, // 状态
          { wch: 30 }, // 图片链接
          { wch: 25 }  // 描述
        ]
        worksheet['!cols'] = colWidths
        
        // 添加工作表到工作簿
        XLSX.utils.book_append_sheet(workbook, worksheet, '商品数据模板')
        
        // 生成文件并下载
        const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' })
        const blob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = '商品数据模板.xlsx'
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
        URL.revokeObjectURL(url)
        
        this.$message.success('Excel模板已下载')
      } catch (error) {
        this.$message.error('模板下载失败')
        console.error('模板下载错误:', error)
      }
    },
    
    // 格式化时间
    formatTime(date) {
      // 兼容 date 可能是字符串
      const d = typeof date === 'string' ? new Date(date) : date
      const pad = n => n < 10 ? '0' + n : n
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
    },
    
    // 格式化文件大小
    formatFileSize(bytes) {
      if (bytes === 0) return '0 B'
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
    },
    
    // 查看文件商品
    viewFileProducts(file) {
      // 这里可以添加查看特定文件导入的商品的功能
      this.$message.info(`查看文件 "${file.name}" 导入的商品`)
    },
    
    // 删除文件历史记录
    removeFileHistory(index) {
      this.uploadedFiles.splice(index, 1)
      this.$message.success('记录已删除')
    },

    // 在导入Excel、API请求等数据变动后，重置分页到第一页
    resetPagination() {
      this.currentPage = 1
    },

    // 写入数据库方法
    async writeToDatabase() {
      if (!this.apiResponse) {
        this.$message.warning('暂无数据，无法写入数据库')
        return
      }

      this.writingToDatabase = true
      try {
        // 构建发送到后端的数据
        const requestData = {
          data: this.apiResponse,
          products: this.products,
          totalCount: this.products.length,
          dataSource: this.dataSource,
          writeTime: new Date().toISOString()
        }

        // 发送到后端接口
        const response = await axios.post('http://localhost:8089/api/database/write', requestData, {
          headers: {
            'Content-Type': 'application/json'
          }
        })

        if (response.data.code === 1) {
          this.$message.success('数据已成功写入数据库')
          console.log('数据库写入响应:', response.data)
          
          // 更新API响应状态
          this.apiResponse = {
            ...this.apiResponse,
            databaseStatus: 'success',
            writeTime: new Date().toISOString()
          }
        } else {
          this.$message.error('写入失败: ' + response.data.msg)
          this.apiResponse = {
            ...this.apiResponse,
            databaseStatus: 'error',
            error: response.data.msg
          }
        }

      } catch (error) {
        console.error('写入数据库失败:', error)
        this.$message.error('写入数据库失败: ' + (error.response?.data?.msg || error.message))
        
        // 更新API响应状态
        this.apiResponse = {
          ...this.apiResponse,
          databaseStatus: 'error',
          error: error.response?.data?.msg || error.message
        }
      } finally {
        this.writingToDatabase = false
      }
    }
  }
}
</script>

<style scoped>
.api-request-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.header-section {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  padding: 20px 0;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.logo {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #ff6b6b, #ee5a24);
  border-radius: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
}

.logo-icon {
  font-size: 30px;
  color: white;
}

.title h1 {
  margin: 0;
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.title p {
  margin: 5px 0 0 0;
  color: #666;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.excel-actions-card {
  margin-top: 20px;
  margin-bottom: 20px;
}

.excel-actions-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
}

.excel-actions-content {
  padding: 10px 0;
}

.excel-actions-description {
  margin-bottom: 20px;
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}

.excel-actions-description p {
  margin: 0;
}

.excel-actions-buttons {
  display: flex;
  gap: 15px;
  align-items: center;
  margin-bottom: 15px;
}

.excel-actions-buttons .el-upload {
  display: inline-block;
}

.excel-upload-info {
  margin-top: 10px;
}

/* 上传文件历史样式 */
.uploaded-files-section {
  margin-top: 20px;
}

.uploaded-files-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.uploaded-file-item {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s ease;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.uploaded-file-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.uploaded-file-item.success {
  border-left: 4px solid #67c23a;
  background: linear-gradient(135deg, #f0f9ff 0%, #f8f9fa 100%);
}

.uploaded-file-item.error {
  border-left: 4px solid #f56c6c;
  background: linear-gradient(135deg, #fef0f0 0%, #f8f9fa 100%);
}

.file-info {
  flex: 1;
}

.file-name {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.file-icon {
  margin-right: 8px;
  font-size: 16px;
  color: #409eff;
}

.name {
  font-weight: 600;
  color: #303133;
  margin-right: 8px;
}

.file-details {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #606266;
  flex-wrap: wrap;
}

.file-details span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.upload-time {
  color: #909399;
}

.file-size {
  color: #909399;
}

.product-count {
  color: #67c23a;
  font-weight: 500;
}

.error-message {
  color: #f56c6c;
  font-weight: 500;
}

.file-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.result-card {
  margin-top: 20px;
}

.result-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
}

.result-stats {
  margin-left: auto;
  display: flex;
  gap: 10px;
}

.loading-container,
.error-container,
.empty-container {
  padding: 40px 0;
  text-align: center;
}

.empty-icon {
  font-size: 80px;
  color: #c0c4cc;
}

/* 商品展示区域样式 */
.products-section {
  margin-top: 20px;
}

.products-card {
  margin-bottom: 20px;
}

.products-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
}

.products-stats {
  margin-left: auto;
}

.products-content {
  padding: 10px 0;
}

.no-image {
  color: #c0c4cc;
  font-size: 12px;
}

.cost-price {
  font-weight: 600;
  color: #f56c6c;
}

.sale-price {
  font-weight: 600;
  color: #67c23a;
}

.profit {
  font-weight: 700;
}

.profit.positive {
  color: #67c23a;
}

.profit.negative {
  color: #f56c6c;
}

.price {
  font-weight: 600;
  color: #e6a23c;
}

/* 商品详情对话框样式 */
.product-detail h3 {
  margin: 0 0 15px 0;
  color: #303133;
}

.detail-info p {
  margin: 8px 0;
  color: #606266;
}

.detail-info strong {
  color: #303133;
}

.no-image-placeholder {
  width: 100%;
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  color: #c0c4cc;
}

.no-image-icon {
  font-size: 40px;
  margin-bottom: 10px;
}

/* 利润计算区域样式 */
.profit-calculation-section {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.calculation-content {
  padding: 20px 0;
}

.calculation-stats {
  margin-bottom: 30px;
}

.stat-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.stat-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
}

.stat-value.cost {
  color: #e6a23c;
}

.stat-value.revenue {
  color: #67c23a;
}

.stat-value.profit {
  font-weight: 800;
}

.stat-value.profit.positive {
  color: #67c23a;
}

.stat-value.profit.negative {
  color: #f56c6c;
}

.calculation-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-bottom: 20px;
}

.calculation-result {
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .calculation-stats .el-col {
    margin-bottom: 15px;
  }
  
  .calculation-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .calculation-actions .el-button {
    width: 100%;
    max-width: 300px;
  }
  
  .excel-actions-buttons {
    flex-direction: column;
    align-items: stretch;
  }
  
  .excel-actions-buttons .el-button {
    width: 100%;
  }
  
  .uploaded-file-item {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .file-actions {
    justify-content: flex-end;
  }
  
  .file-details {
    flex-direction: column;
    gap: 8px;
  }
}



/* JSON结果展示区域样式 */
.json-result-section {
  margin-bottom: 20px;
}

.json-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.json-content {
  padding: 20px 0;
}

.json-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
}

.json-display {
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  overflow: hidden;
}

.json-highlight {
  margin: 0;
  padding: 15px;
  background: #1e1e1e;
  color: #d4d4d4;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.5;
  max-height: 400px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.json-textarea {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.5;
}

.json-textarea :deep(.el-textarea__inner) {
  background: #f8f9fa;
  border: none;
  resize: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 20px;
  }
  
  .title h1 {
    font-size: 24px;
  }
  

  
  .json-toolbar {
    flex-direction: column;
    gap: 10px;
    align-items: stretch;
  }
}
</style> 
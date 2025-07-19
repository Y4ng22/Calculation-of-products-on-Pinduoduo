<template>
  <div class="product-display-page">
    <div class="header-section">
      <div class="header-content">
        <h1 class="text-2xl font-bold text-gray-900">商品展示</h1>
      </div>
      <div class="stats-row">
        <div class="stat-card">
          <div class="stat-title">商品总数</div>
          <div class="stat-value">{{ allProducts.length }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-title">选中的商品数</div>
          <div class="stat-value" style="color:#409eff;">{{ selectedProductIds.length }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-title">总成本</div>
          <div class="stat-value cost">¥{{ totalCost.toFixed(2) }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-title">总售价</div>
          <div class="stat-value revenue">¥{{ totalRevenue.toFixed(2) }}</div>
        </div>
        <div class="stat-card">
          <div class="stat-title">总利润</div>
          <div class="stat-value profit" :class="{ 'positive': totalProfit > 0, 'negative': totalProfit < 0 }">
            ¥{{ totalProfit.toFixed(2) }}
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-title">利率</div>
          <div class="stat-value rate">{{ profitRate }}%</div>
        </div>
      </div>
    </div>
    <div class="main-content">
      <el-card class="products-card" shadow="never">
        <template #header>
          <div class="products-header">
            <el-icon><Shop /></el-icon>
            <span>商品信息展示</span>
          </div>
        </template>
        <div class="products-content">
          <el-table
            ref="productTable"
            :data="products"
            style="width: 100%"
            v-loading="loading"
            empty-text="暂无商品数据"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="goodId" label="商品ID" width="120" />
            <el-table-column prop="name" label="商品名称" min-width="200" />
            <el-table-column prop="image" label="商品图片" width="120">
              <template #default="{ row }">
                <el-image v-if="row.image" :src="row.image" style="width: 80px; height: 80px" fit="cover" :preview-src-list="[row.image]" />
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
            <el-table-column label="利润" width="120">
              <template #default="{ row }">
                <div v-if="row.cost && row.price">
                  <div class="profit" :class="{ 'positive': (row.price - row.cost) > 0, 'negative': (row.price - row.cost) < 0 }">
                    ¥{{ (row.price - row.cost).toFixed(2) }}
                  </div>
                  <div v-if="row.sales" class="total-profit" :class="{ 'positive': ((row.price - row.cost) * row.sales) > 0, 'negative': ((row.price - row.cost) * row.sales) < 0 }">
                    总利润: ¥{{ ((row.price - row.cost) * row.sales).toFixed(2) }}
                  </div>
                </div>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="category" label="分类" width="120" />
            <el-table-column prop="sales" label="销量" width="100">
              <template #default="{ row }">
                <span v-if="row.sales !== undefined && row.sales !== null" class="sales-count">{{ row.sales }}</span>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.status" :type="getStatusType(row.status)" size="small">{{ row.status }}</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button size="small" @click="viewProductDetail(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 分页组件 -->
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
        </div>
      </el-card>
      <el-dialog v-model="productDetailVisible" title="商品详情" width="600px" :before-close="handleCloseProductDetail">
        <div v-if="selectedProduct" class="product-detail">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-image v-if="selectedProduct.image" :src="selectedProduct.image" style="width: 100%; height: 200px" fit="cover" :preview-src-list="[selectedProduct.image]" />
              <div v-else class="no-image-placeholder">
                <el-icon class="no-image-icon"><Picture /></el-icon>
                <span>暂无图片</span>
              </div>
            </el-col>
            <el-col :span="12">
              <h3>{{ selectedProduct.name || '商品名称' }}</h3>
              <div class="detail-info">
                <p><strong>ID:</strong> {{ selectedProduct.id || '-' }}</p>
                <p><strong>商品ID:</strong> {{ selectedProduct.goodId || '-' }}</p>
                <p><strong>进货价:</strong> <span v-if="selectedProduct.cost" class="cost-price">¥{{ selectedProduct.cost }}</span><span v-else>-</span></p>
                <p><strong>卖出价:</strong> <span v-if="selectedProduct.price" class="sale-price">¥{{ selectedProduct.price }}</span><span v-else>-</span></p>
                <p v-if="selectedProduct.cost && selectedProduct.price"><strong>单件利润:</strong> <span class="profit" :class="{ 'positive': (selectedProduct.price - selectedProduct.cost) > 0, 'negative': (selectedProduct.price - selectedProduct.cost) < 0 }">¥{{ (selectedProduct.price - selectedProduct.cost).toFixed(2) }}</span></p>
                <p v-if="selectedProduct.cost && selectedProduct.price && selectedProduct.sales"><strong>总利润:</strong> <span class="profit" :class="{ 'positive': ((selectedProduct.price - selectedProduct.cost) * selectedProduct.sales) > 0, 'negative': ((selectedProduct.price - selectedProduct.cost) * selectedProduct.sales) < 0 }">¥{{ ((selectedProduct.price - selectedProduct.cost) * selectedProduct.sales).toFixed(2) }}</span></p>
                <p><strong>销量:</strong> <span v-if="selectedProduct.sales !== undefined && selectedProduct.sales !== null" class="sales-count">{{ selectedProduct.sales }}</span><span v-else>-</span></p>
                <p><strong>分类:</strong> {{ selectedProduct.category || '-' }}</p>
                <p><strong>状态:</strong> <el-tag v-if="selectedProduct.status" :type="getStatusType(selectedProduct.status)" size="small">{{ selectedProduct.status }}</el-tag><span v-else>-</span></p>
                <p v-if="selectedProduct.description"><strong>描述:</strong> {{ selectedProduct.description }}</p>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-dialog>
    </div>
  </div>
  <!-- 商品销量预测模块 -->
  <div class="main-content">
    <el-card class="sales-forecast-card" shadow="never">
      <template #header>
        <div class="sales-forecast-header">
          <el-icon><Shop /></el-icon>
          <span>商品销量预测</span>
        </div>
      </template>
      <div class="sales-forecast-content">
        <div class="forecast-inner">
          <div class="forecast-title">AI 智能销量分析</div>
          <div class="forecast-subtitle">AI将提供详细的分析报告，包括趋势分析、影响因素、预测依据和优化建议。系统已预设专业分析模板，您可以在下方输入特殊要求进行补充。</div>
          <el-divider style="margin: 16px 0 24px 0;" />
          <transition name="fade">
            <div v-if="predictError" class="forecast-error">{{ predictError }}</div>
          </transition>
          <transition name="fade">
            <div v-if="displayedResult" class="forecast-result">
              <div class="result-header">
                <span class="word-count">字数：{{ displayedResult.length }}</span>
              </div>
              <div class="result-content">{{ displayedResult }}</div>
            </div>
          </transition>
          
          <!-- 折线图显示区域 -->
          <transition name="fade">
            <div v-if="showChart && chartData" class="chart-container">
              <div class="chart-title">销量预测趋势图</div>
              <div class="chart-wrapper">
                <v-chart 
                  :option="chartOption" 
                  :style="{ width: '100%', height: '400px' }"
                  autoresize
                />
              </div>
            </div>
          </transition>
          
          <div class="input-section">
            <div class="input-header">
              <el-input
                type="textarea"
                v-model="predictPrompt"
                :rows="4"
                class="forecast-textarea large-textarea"
                placeholder="请输入特殊要求（可选，系统将结合预设分析模板）"
              />
              <el-button 
                type="info" 
                size="small"
                @click="showHistory"
                class="history-btn"
              >
                历史记录
              </el-button>
            </div>
          </div>
          <div class="selected-tags-view" v-if="selectedProductIds.length > 0">
            <div class="selected-tags-list">
              <span v-for="item in computedProducts" :key="item.id" class="selected-tag">
                {{ item.id + ' ' + item.name + '  商品ID：' + item.goodId }}
              </span>
            </div>
          </div>
          <el-button type="primary" :loading="predicting" @click="predictSales" class="forecast-btn">
            {{ predicting ? 'AI正在生成详细分析...' : 'AI销量预测' }}
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
  
  <!-- 历史记录对话框 -->
  <el-dialog 
    v-model="historyVisible" 
    title="历史记录" 
    width="900px"
    :before-close="handleCloseHistory"
  >
    <div class="history-container">
      <div class="history-list">
        <div 
          v-for="(record, index) in historyRecords" 
          :key="index"
          class="history-item"
          :class="{ 'active': selectedHistory === record }"
        >
          <div class="history-content" @click="selectHistory(record)">
            <div class="history-header">
              <span class="history-time">{{ formatTime(record.timestamp) }}</span>
              <span class="history-products">商品数：{{ record.productsCount }}</span>
            </div>
            <div class="history-prompt">{{ record.prompt || '无特殊要求' }}</div>
            <div class="history-summary">{{ record.summary }}</div>
          </div>
          <div class="history-actions">
            <el-button 
              type="danger" 
              size="small" 
              circle
              @click.stop="deleteHistoryRecord(index)"
              class="delete-btn"
            >
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
      </div>
      
      <div v-if="selectedHistory" class="history-detail">
        <div class="detail-header">
          <h4>详细信息</h4>
          <el-button 
            type="primary" 
            size="small"
            @click="loadHistoryToForm"
          >
            加载到表单
          </el-button>
        </div>
        <div class="detail-content">
          <div class="detail-section">
            <h5>用户输入：</h5>
            <p>{{ selectedHistory.prompt || '无特殊要求' }}</p>
          </div>
          <div class="detail-section">
            <h5>分析结果：</h5>
            <div class="result-content">{{ selectedHistory.result }}</div>
          </div>
          <div v-if="selectedHistory.chartData" class="detail-section">
            <h5>图表数据：</h5>
            <div class="chart-wrapper">
              <v-chart 
                :option="getHistoryChartOption(selectedHistory.chartData)" 
                :style="{ width: '100%', height: '300px' }"
                autoresize
              />
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleCloseHistory">关闭</el-button>
        <el-button 
          type="danger" 
          @click="clearHistory"
          :disabled="historyRecords.length === 0"
        >
          清空历史
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script>
import { Shop, Picture, Delete } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'

use([
  CanvasRenderer,
  LineChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

export default {
  name: 'ProductDisplay',
  components: { Shop, Picture, Delete, VChart },
  data() {
    return {
      products: [],
      allProducts: [], // 存储所有商品数据用于统计
      loading: false,
      productDetailVisible: false,
      selectedProduct: null,
      selectedProducts: [], // 页面初始为空
      pagination: {
        page: 1,
        size: 10,
        total: 0
      },
      selectedProductIds: [], // 页面初始为空
      predictPrompt: '', // 用户输入的提示词
      defaultPrompt: '请根据以下商品的历史销量数据，进行深入的销量预测分析。要求：1. 详细分析每个商品的历史销量趋势和变化规律，说明变化幅度和速度；2. 深入识别影响销量的关键因素（季节性、价格策略、市场竞争、消费者偏好、营销活动等）；3. 基于数据趋势和影响因素，预测未来3个月的销量；4. 详细说明预测依据、分析方法和预测逻辑；5. 提供风险评估和不确定性分析；6. 给出具体的优化建议和业务指导。请确保分析全面、详细、有理有据，字数在500字左右。', // 预设的提示词
      predicting: false, // 新增，防止未定义警告
      predictError: '',  // 新增，防止未定义警告
      predictResult: '', // 新增，防止未定义警告
      displayedResult: '', // 新增，防止未定义警告
      chartData: null, // 新增：存储图表数据
      showChart: false, // 新增：控制图表显示
      // 历史记录相关
      historyRecords: [], // 存储历史记录
      historyVisible: false, // 控制历史记录对话框显示
      selectedHistory: null, // 当前选中的历史记录
    }
  },
  mounted() {
    this.fetchProducts()
    this.fetchAllProducts()
    this.loadHistoryRecords()
  },
  computed: {
    computedProducts() {
      // 只用 selectedProductIds 计算 selectedProducts，未选中时返回全部
      return this.selectedProductIds.length > 0 ? this.allProducts.filter(item => this.selectedProductIds.includes(item.id)) : this.allProducts
    },
    totalProfit() {
      return this.computedProducts.reduce((sum, p) => {
        const cost = parseFloat(p.cost || 0)
        const price = parseFloat(p.price || 0)
        const sales = parseInt(p.sales || 0)
        return sum + (price - cost) * sales
      }, 0)
    },
    totalCost() {
      return this.computedProducts.reduce((sum, p) => {
        const cost = parseFloat(p.cost || 0)
        const sales = parseInt(p.sales || 0)
        return sum + cost * sales
      }, 0)
    },
    totalRevenue() {
      return this.computedProducts.reduce((sum, p) => {
        const price = parseFloat(p.price || 0)
        const sales = parseInt(p.sales || 0)
        return sum + price * sales
      }, 0)
    },
    profitRate() {
      if (this.totalCost > 0) {
        return ((this.totalProfit / this.totalCost) * 100).toFixed(2)
      }
      return '0.00'
    },
    selectedProductsJson() {
      // 展示勾选商品的关键信息
      if (this.computedProducts.length === 0) return ''
      return JSON.stringify(this.computedProducts.map(p => ({
        name: p.name,
        cost: p.cost,
        price: p.price,
        sales: p.sales,
        category: p.category
      })), null, 2)
    },
    chartOption() {
      if (!this.chartData) return {}
      
      return {
        title: {
          text: '商品销量预测趋势',
          left: 'center',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        legend: {
          data: this.chartData.series.map(item => item.name),
          bottom: 10
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          top: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: this.chartData.xAxis,
          axisLabel: {
            rotate: 0
          }
        },
        yAxis: {
          type: 'value',
          name: '销量/利润（元）',
          axisLabel: {
            formatter: '{value}'
          }
        },
        series: this.chartData.series.map(item => ({
          name: item.name,
          type: 'line',
          data: item.data,
          smooth: true,
          lineStyle: {
            width: 3
          },
          itemStyle: {
            borderWidth: 2
          },
          markPoint: {
            data: [
              { type: 'max', name: '最大值' },
              { type: 'min', name: '最小值' }
            ]
          }
        }))
      }
    }
  },
  methods: {
    async fetchProducts() {
      this.loading = true
      try {
        const res = await axios.get('/api/database/records', {
          params: {
            page: this.pagination.page,
            size: this.pagination.size
          }
        })
        if (res.data.code === 1 && res.data.data) {
          this.products = res.data.data.records.map(item => ({
            ...item,
            status: item.status || item.state
          }))
          this.pagination.total = res.data.data.total
        } else {
          this.products = []
          this.pagination.total = 0
        }
      } catch (e) {
        this.products = []
        this.pagination.total = 0
      } finally {
        this.loading = false
        this.syncSelection()
      }
    },
    
    // 获取所有商品数据用于统计
    async fetchAllProducts() {
      try {
        const res = await axios.get('/api/database/records', {
          params: {
            page: 1,
            size: 999999 // 获取所有数据
          }
        })
        if (res.data.code === 1 && res.data.data) {
          this.allProducts = res.data.data.records.map(item => ({
            ...item,
            status: item.status || item.state
          }))
        } else {
          this.allProducts = []
        }
      } catch (e) {
        this.allProducts = []
      }
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
    viewProductDetail(product) {
      this.selectedProduct = product
      this.productDetailVisible = true
    },
    handleCloseProductDetail() {
      this.productDetailVisible = false
      this.selectedProduct = null
    },
    // 自动勾选当前页已选商品
    syncSelection() {
      this.$nextTick(() => {
        if (!this.$refs.productTable) return
        // 先清空所有 selection
        this.$refs.productTable.clearSelection()
        // 再勾选 selectedProductIds 里的
        this.products.forEach(row => {
          if (this.selectedProductIds.includes(row.id)) {
            this.$refs.productTable.toggleRowSelection(row, true)
          }
        })
      })
    },
    handleSelectionChange(selection) {
      // 先移除当前页所有商品的 id
      const currentPageIds = this.products.map(item => item.id)
      this.selectedProductIds = this.selectedProductIds.filter(id => !currentPageIds.includes(id))
      // 再把当前页选中的 id 加进去
      this.selectedProductIds.push(...selection.map(item => item.id))
      // 去重
      this.selectedProductIds = Array.from(new Set(this.selectedProductIds))
      // 选中的商品列表
      this.selectedProducts = this.allProducts.filter(item => this.selectedProductIds.includes(item.id))
    },
    
    // 处理每页显示数量变化
    handleSizeChange(size) {
      this.pagination.size = size
      this.pagination.page = 1 // 重置到第一页
      this.fetchProducts()
    },
    
    // 处理当前页变化
    handleCurrentChange(page) {
      this.pagination.page = page
      this.fetchProducts()
    },
    async predictSales() {
      this.predicting = true
      this.predictResult = ''
      this.displayedResult = ''
      this.predictError = ''
      this.chartData = null
      this.showChart = false
      try {
        // 只取勾选商品，若无勾选则传全部
        const products = this.computedProducts.length > 0 ? this.computedProducts : this.allProducts
        const simpleProducts = products.map(p => ({
          name: p.name,
          cost: p.cost,
          price: p.price,
          sales: p.sales,
          category: p.category
        }))
        // 构建完整的提示词内容，包含后端的完整提示词
        const fullContent = `你是一个专业的数据分析师，专门进行商品销量预测和趋势分析。你的分析必须详细、全面、有理有据。请按照以下要求进行分析：${this.defaultPrompt}

${this.predictPrompt ? '用户特殊要求：' + this.predictPrompt + '\n\n' : ''}商品数据：${JSON.stringify(simpleProducts, null, 2)}

分析要求（必须详细回答）：
1. 历史趋势分析：详细分析每个商品的历史销量变化趋势，识别增长、下降或稳定模式，说明变化幅度和速度
2. 影响因素分析：深入分析可能影响销量的因素，包括季节性变化、价格波动、市场竞争、消费者行为、营销活动、产品生命周期等
3. 预测方法说明：详细说明使用的预测方法（如趋势外推、回归分析、时间序列分析、机器学习模型等）及其适用性
4. 具体预测：给出未来的具体预测数值，详细解释每个月的预测理由和依据
5. 风险评估：分析预测的不确定性、潜在风险、外部因素影响等
6. 优化建议：基于分析结果提供具体的改进建议，包括定价策略、营销策略、库存管理等
7. 数据解读：对预测结果进行深入解读，说明对业务决策的指导意义

请按照以下JSON格式返回结果（根据具体情况做出改变）：
{
  "analysis": "请提供详细的分析结论，包含以下内容：1. 历史销量趋势分析或者利润分析（二选一，按照特殊需求）（详细说明每个商品的变化模式）；2. 影响销量的关键因素分析（季节性、价格变化、市场竞争、消费者行为等）；3. 预测依据和方法详细说明；4. 未来的具体预测数值及详细理由；5. 风险评估和不确定性分析；6. 具体的优化建议和业务指导。分析要具体、详细、有理有据。",
  "chartData": {
    "xAxis": ["1月", "2月", "3月", "4月", "5月", "6月", "7月"],
    "series": [
      {
        "name": "商品名称",
        "data": [历史销量1, 历史销量2, 历史销量3, 历史销量4, 预测销量5, 预测销量6, 预测销量7]
      }
    ]
  }
}`
        
        const res = await fetch('/api/database/ai/sales-forecast', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            content: fullContent,
            products: simpleProducts
          })
        })
        if (!res.ok) throw new Error('AI接口请求失败')
        const data = await res.json()
        console.log('AI接口返回：', data) // 调试
        if (data.code === 1 && data.data) {
          let aiData = data.data
          if (typeof aiData === 'string') {
            try { aiData = JSON.parse(aiData) } catch(e) {}
          }
          console.log('AI解析后数据：', aiData) // 调试
          // 健壮性处理
          let content = ''
          if (aiData && aiData.choices && aiData.choices[0] && aiData.choices[0].message && aiData.choices[0].message.content) {
            content = aiData.choices[0].message.content
          } else if (typeof aiData === 'string') {
            content = aiData
          } else if (aiData && aiData.content) {
            content = aiData.content
          }
          if (content && typeof content === 'string') {
            this.predictResult = content.trim()
            
            // 尝试解析JSON格式的图表数据
            try {
              // 查找JSON格式的数据
              const jsonMatch = content.match(/\{[\s\S]*\}/)
              if (jsonMatch) {
                const parsedData = JSON.parse(jsonMatch[0])
                if (parsedData.chartData && parsedData.analysis) {
                  this.chartData = parsedData.chartData
                  this.predictResult = parsedData.analysis
                  this.showChart = true
                }
              }
            } catch (jsonError) {
              console.log('JSON解析失败，显示文本结果：', jsonError)
            }
            
            // 打字机效果
            this.displayedResult = ''
            let i = 0
            const fullText = this.predictResult
            const typeWriter = () => {
              if (i <= fullText.length) {
                this.displayedResult = fullText.slice(0, i)
                i++
                setTimeout(typeWriter, 18)
              }
            }
            typeWriter()
            
            // 保存历史记录
            this.saveHistoryRecord(simpleProducts)
          } else {
            this.predictError = 'AI未返回有效结果'
          }
        } else {
          this.predictError = 'AI未返回有效结果'
        }
      } catch (e) {
        console.error(e) // 调试
        this.predictError = '预测失败: ' + (e.message || e)
      } finally {
        this.predicting = false
      }
    },
    
    // 历史记录相关方法
    loadHistoryRecords() {
      try {
        const history = localStorage.getItem('salesForecastHistory')
        this.historyRecords = history ? JSON.parse(history) : []
      } catch (e) {
        console.error('加载历史记录失败:', e)
        this.historyRecords = []
      }
    },
    
    saveHistoryRecord(products) {
      try {
        const record = {
          timestamp: new Date().toISOString(),
          prompt: this.predictPrompt,
          productsCount: products.length,
          result: this.predictResult,
          chartData: this.chartData,
          summary: this.predictResult.substring(0, 100) + '...'
        }
        
        this.historyRecords.unshift(record) // 添加到开头
        
        // 只保留最近50条记录
        if (this.historyRecords.length > 50) {
          this.historyRecords = this.historyRecords.slice(0, 50)
        }
        
        localStorage.setItem('salesForecastHistory', JSON.stringify(this.historyRecords))
      } catch (e) {
        console.error('保存历史记录失败:', e)
      }
    },
    
    showHistory() {
      this.historyVisible = true
      this.selectedHistory = null
    },
    
    handleCloseHistory() {
      this.historyVisible = false
      this.selectedHistory = null
    },
    
    selectHistory(record) {
      this.selectedHistory = record
    },
    
    loadHistoryToForm() {
      if (this.selectedHistory) {
        this.predictPrompt = this.selectedHistory.prompt || ''
        this.historyVisible = false
        ElMessage({
          message: '已加载历史记录到表单',
          type: 'success'
        })
      }
    },
    
    deleteHistoryRecord(index) {
      this.$confirm('确定要删除这条历史记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 如果删除的是当前选中的记录，清空选中状态
        if (this.selectedHistory === this.historyRecords[index]) {
          this.selectedHistory = null
        }
        
        this.historyRecords.splice(index, 1)
        localStorage.setItem('salesForecastHistory', JSON.stringify(this.historyRecords))
        
        ElMessage({
          message: '历史记录已删除',
          type: 'success'
        })
      }).catch(() => {})
    },
    
    clearHistory() {
      this.$confirm('确定要清空所有历史记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.historyRecords = []
        localStorage.removeItem('salesForecastHistory')
        this.selectedHistory = null
        ElMessage({
          message: '历史记录已清空',
          type: 'success'
        })
      }).catch(() => {})
    },
    
    formatTime(timestamp) {
      const date = new Date(timestamp)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    
    getHistoryChartOption(chartData) {
      return {
        title: {
          text: '历史预测趋势图',
          left: 'center',
          textStyle: {
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        legend: {
          data: chartData.series.map(item => item.name),
          bottom: 10
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          top: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: chartData.xAxis,
          axisLabel: {
            rotate: 0
          }
        },
        yAxis: {
          type: 'value',
          name: '销量',
          axisLabel: {
            formatter: '{value}'
          }
        },
        series: chartData.series.map(item => ({
          name: item.name,
          type: 'line',
          data: item.data,
          smooth: true,
          lineStyle: {
            width: 2
          },
          itemStyle: {
            borderWidth: 1
          }
        }))
      }
    }
  }
}
</script>

<style scoped>
.product-display-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 30px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.header-section {
  margin-bottom: 20px;
  width: 100%;
  max-width: 1600px;
  box-sizing: border-box;
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
.sales-count {
  font-weight: 600;
  color: #409eff;
}
.total-profit {
  font-size: 12px;
  margin-top: 2px;
}
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
.stats-row {
  display: flex;
  gap: 30px;
  margin: 20px 0 10px 0;
}
.stat-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px 30px;
  text-align: center;
  border: 1px solid #e9ecef;
  min-width: 180px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.03);
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
.stat-value.profit.positive {
  color: #67c23a;
}
.stat-value.profit.negative {
  color: #f56c6c;
}
.stat-value.cost {
  color: #e6a23c;
}
.stat-value.rate {
  color: #409eff;
}
.stat-value.revenue {
  color: #67c23a;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding: 20px 0;
}
.main-content {
  width: 100%;
  max-width: 1600px;
  box-sizing: border-box;
  margin: 0 auto;
}
.sales-forecast-card {
  max-width: 100%;
  margin: 0 auto 30px auto;
  box-sizing: border-box;
  /* 跟随主内容区宽度 */
  width: 100%;
  max-width: 1600px;
  margin-bottom: 30px;
}
.sales-forecast-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  font-size: 18px;
}
.sales-forecast-content {
  min-height: 80px;
  color: #c0c4cc;
  display: flex;
  align-items: center;
  justify-content: center;
}
.forecast-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px 24px 24px;
  background: #fafbfc;
  border-radius: 14px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  display: flex;
  flex-direction: column;
}
.forecast-title {
  font-size: 1.2rem;
  font-weight: 700;
  color: #222;
  letter-spacing: 1px;
  margin-bottom: 8px;
  width: 100%;
  text-align: left;
  align-self: flex-start;
}

.forecast-subtitle {
  font-size: 14px;
  color: #666;
  line-height: 1.4;
  margin-bottom: 0;
  width: 100%;
  text-align: left;
  align-self: flex-start;
}
.input-section {
  width: 100%;
  margin-bottom: 18px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.forecast-textarea {
  width: 1100px;
  margin-bottom: 8px;
  margin-left: auto;
  margin-right: auto;
  display: block;
}




.forecast-btn {
  margin-bottom: 18px;
  min-width: 140px;
}
.forecast-result {
  background: #f4f8fb;
  border-radius: 8px;
  padding: 0;
  color: #222;
  font-size: 15px;
  line-height: 1.6;
  margin-top: 8px;
  width: 100%;
  text-align: left;
  white-space: pre-line;
  box-shadow: 0 1px 4px rgba(64,158,255,0.06);
  word-break: break-word;
}

.result-header {
  background: #e8f4fd;
  padding: 8px 18px;
  border-bottom: 1px solid #d1e7dd;
  border-radius: 8px 8px 0 0;
}

.word-count {
  font-size: 12px;
  color: #409eff;
  font-weight: 500;
}

.result-content {
  padding: 20px 18px;
  line-height: 1.7;
}
.forecast-error {
  background: #fff0f0;
  border-radius: 8px;
  padding: 14px 12px;
  color: #d32f2f;
  font-size: 15px;
  margin-top: 8px;
  width: 100%;
  text-align: left;
  border: 1px solid #ffd6d6;
}
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
}
.large-textarea .el-textarea__inner {
  font-size: 18px;
  padding: 16px 14px;
  min-height: 180px;
}
.selected-tags-view {
  width: 100%;
  margin: 10px 0 10px 0;
  min-height: 32px;
}
.selected-tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: flex-start;
}
.selected-tag {
  display: inline-block;
  background: #e6f0fa;
  color: #409eff;
  border-radius: 16px;
  padding: 4px 14px;
  font-size: 15px;
  font-weight: 500;
  margin-bottom: 4px;
  box-shadow: 0 1px 2px rgba(64,158,255,0.06);
  border: 1px solid #b3d8fd;
  max-width: 320px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chart-container {
  margin: 20px 0;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  border: 1px solid #e8eaed;
}

.chart-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
  text-align: center;
}

.chart-wrapper {
  background: #fafbfc;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e8eaed;
}
@media (max-width: 1200px) {
  .forecast-inner {
    max-width: 98vw;
    padding: 18px 6px 16px 6px;
  }
  .forecast-title {
    font-size: 18px;
  }
  .forecast-result, .forecast-error {
    font-size: 14px;
    padding: 10px 6px;
  }
}

/* 历史记录相关样式 */
.input-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  width: 100%;
  max-width: 1100px;
}

.history-btn {
  margin-top: 8px;
  flex-shrink: 0;
}

.history-container {
  display: flex;
  gap: 20px;
  height: 500px;
}

.history-list {
  width: 300px;
  border-right: 1px solid #e4e7ed;
  overflow-y: auto;
  padding-right: 10px;
}

.history-item {
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  margin-bottom: 8px;
  transition: all 0.3s;
  background: #fff;
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.history-content {
  flex: 1;
  cursor: pointer;
}

.history-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64,158,255,0.1);
}

.history-item.active {
  border-color: #409eff;
  background: #f0f9ff;
  box-shadow: 0 2px 8px rgba(64,158,255,0.15);
}

.history-actions {
  flex-shrink: 0;
  opacity: 0;
  transition: opacity 0.3s;
}

.history-item:hover .history-actions {
  opacity: 1;
}

.delete-btn {
  padding: 4px;
  font-size: 12px;
}

.delete-btn:hover {
  background-color: #f56c6c;
  color: white;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.history-time {
  font-size: 12px;
  color: #909399;
}

.history-products {
  font-size: 12px;
  color: #409eff;
  background: #e6f0fa;
  padding: 2px 6px;
  border-radius: 4px;
}

.history-prompt {
  font-size: 13px;
  color: #606266;
  margin-bottom: 4px;
  line-height: 1.4;
  max-height: 40px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.history-summary {
  font-size: 12px;
  color: #909399;
  line-height: 1.3;
  max-height: 32px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.history-detail {
  flex: 1;
  overflow-y: auto;
  padding-left: 10px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e4e7ed;
}

.detail-header h4 {
  margin: 0;
  color: #303133;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h5 {
  margin: 0 0 8px 0;
  color: #606266;
  font-size: 14px;
  font-weight: 600;
}

.detail-section p {
  margin: 0;
  color: #303133;
  line-height: 1.5;
  background: #f8f9fa;
  padding: 8px 12px;
  border-radius: 4px;
  border-left: 3px solid #409eff;
}

.detail-section .result-content {
  background: #f4f8fb;
  border-radius: 6px;
  padding: 12px;
  border: 1px solid #e8f4fd;
  max-height: 200px;
  overflow-y: auto;
  font-size: 13px;
  line-height: 1.6;
}

.detail-section .chart-wrapper {
  background: #fafbfc;
  border-radius: 6px;
  padding: 12px;
  border: 1px solid #e8eaed;
}
</style> 
<template>
  <div class="product-display-page">
    <div class="header-section">
      <div class="header-content">
        <h1 class="text-2xl font-bold text-gray-900">商品展示</h1>
      </div>
      <div class="stats-row">
        <div class="stat-card">
          <div class="stat-title">商品总数</div>
          <div class="stat-value">{{ products.length }}</div>
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
          <el-table :data="products" style="width: 100%" v-loading="loading" empty-text="暂无商品数据"
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
</template>

<script>
import { Shop, Picture } from '@element-plus/icons-vue'
import axios from 'axios'
export default {
  name: 'ProductDisplay',
  components: { Shop, Picture },
  data() {
    return {
      products: [],
      loading: false,
      productDetailVisible: false,
      selectedProduct: null,
      selectedProducts: []
    }
  },
  mounted() {
    this.fetchProducts()
  },
  computed: {
    computedProducts() {
      return this.selectedProducts.length > 0 ? this.selectedProducts : this.products
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
    }
  },
  methods: {
    async fetchProducts() {
      this.loading = true
      try {
        const res = await axios.get('/api/database/records')
        if (res.data.code === 1 && res.data.data && res.data.data.records) {
          this.products = res.data.data.records.map(item => ({
            ...item,
            status: item.status || item.state
          }))
        } else {
          this.products = []
        }
      } catch (e) {
        this.products = []
      } finally {
        this.loading = false
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
    handleSelectionChange(selection) {
      this.selectedProducts = selection
    }
  }
}
</script>

<style scoped>
.product-display-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 30px 0;
}
.header-section {
  margin-bottom: 20px;
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
</style> 
<template>
  <el-card class="product-card" shadow="hover">
    <div class="product-image">
      <el-image 
        :src="product.image" 
        fit="cover"
        :preview-src-list="[product.image]"
        class="image"
      >
        <template #error>
          <div class="image-placeholder">
            <el-icon><Picture /></el-icon>
          </div>
        </template>
      </el-image>
    </div>
    
    <div class="product-info">
      <div class="product-header">
        <span class="product-id">ID: {{ product.id || '-' }}</span>
        <span v-if="product.goodId" class="good-id">商品ID: {{ product.goodId }}</span>
      </div>
      <h3 class="product-name">{{ product.name }}</h3>
      
      <div class="price-info">
        <div class="price-item">
          <span class="label">购入价:</span>
          <span class="price purchase-price">¥{{ product.purchasePrice }}</span>
        </div>
        <div class="price-item">
          <span class="label">卖出价:</span>
          <span class="price sale-price">¥{{ product.salePrice }}</span>
        </div>
        <div class="price-item">
          <span class="label">利润:</span>
          <span class="price profit" :class="{ 'positive': product.profit > 0, 'negative': product.profit < 0 }">
            ¥{{ product.profit }}
          </span>
        </div>
      </div>
      
      <div class="profit-rate">
        <span class="label">利润率:</span>
        <span class="rate" :class="{ 'positive': product.profitRate > 0, 'negative': product.profitRate < 0 }">
          {{ product.profitRate }}%
        </span>
      </div>
    </div>
  </el-card>
</template>

<script>
import { Picture } from '@element-plus/icons-vue'

export default {
  name: 'ProductCard',
  components: {
    Picture
  },
  props: {
    product: {
      type: Object,
      required: true,
      default: () => ({
        id: '',
        name: '',
        image: '',
        purchasePrice: 0,
        salePrice: 0,
        profit: 0,
        profitRate: 0
      })
    }
  }
}
</script>

<style scoped>
.product-card {
  width: 100%;
  height: 100%;
  transition: all 0.3s ease;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 12px;
}

.image {
  width: 100%;
  height: 100%;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;
  color: #909399;
  font-size: 24px;
}

.product-info {
  padding: 0 8px;
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 12px;
  color: #909399;
}

.product-id {
  font-weight: 500;
}

.good-id {
  font-weight: 500;
}

.product-name {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
  height: 44px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.price-info {
  margin-bottom: 8px;
}

.price-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.label {
  font-size: 14px;
  color: #606266;
}

.price {
  font-weight: 600;
  font-size: 14px;
}

.purchase-price {
  color: #e6a23c;
}

.sale-price {
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

.profit-rate {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 8px;
  border-top: 1px solid #ebeef5;
}

.rate {
  font-weight: 600;
  font-size: 14px;
}

.rate.positive {
  color: #67c23a;
}

.rate.negative {
  color: #f56c6c;
}
</style> 
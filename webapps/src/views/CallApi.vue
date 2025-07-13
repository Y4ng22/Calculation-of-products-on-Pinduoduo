<template>
  <div class="bg-gray-100 min-h-screen">
    <main class="container mx-auto px-4 py-8 space-y-6">
      <!-- 选择接口卡片 -->
      <div class="bg-white rounded-lg shadow p-6">
        <div class="space-y-4">
          <h2 class="text-lg font-medium">选择接口</h2>
          <div class="flex items-center space-x-4">
            <button class="px-4 py-2 bg-gray-100 hover:bg-gray-200 rounded-button flex items-center space-x-2">
              <span>选择 API 接口</span>
              <i class="fas fa-chevron-down text-gray-500"></i>
            </button>
          </div>
        </div>
      </div>
      <!-- 请求URL卡片 -->
      <div class="bg-white rounded-lg shadow p-6">
        <div class="space-y-4">
          <h2 class="text-lg font-medium">请求 URL</h2>
          <div class="flex items-center space-x-2">
            <div class="relative">
              <button class="px-4 py-2 bg-gray-100 hover:bg-gray-200 rounded-button flex items-center space-x-2 whitespace-nowrap">
                <span>POST</span>
                <i class="fas fa-chevron-down text-gray-500"></i>
              </button>
            </div>
            <input type="text" :value="requestUrl" class="flex-1 px-4 py-2 border border-gray-300 rounded-button focus:outline-none focus:ring-2 focus:ring-primary/20" readonly>
          </div>
        </div>
      </div>
      <!-- 请求参数卡片 -->
      <div class="bg-white rounded-lg shadow p-6">
        <div class="space-y-4">
          <h2 class="text-lg font-medium">请求参数</h2>
          <div class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="bg-gray-50">
                  <th class="px-4 py-3 text-left text-sm font-medium text-gray-500">参数名</th>
                  <th class="px-4 py-3 text-left text-sm font-medium text-gray-500">类型</th>
                  <th class="px-4 py-3 text-left text-sm font-medium text-gray-500">必填</th>
                  <th class="px-4 py-3 text-left text-sm font-medium text-gray-500">参数值</th>
                  <th class="px-4 py-3 text-left text-sm font-medium text-gray-500">说明</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-200">
                <tr v-for="param in requestParams" :key="param.name">
                  <td class="px-4 py-3 text-sm text-gray-900">{{ param.name }}</td>
                  <td class="px-4 py-3 text-sm text-gray-500">{{ param.type }}</td>
                  <td class="px-4 py-3 text-sm text-gray-500">{{ param.required ? '是' : '否' }}</td>
                  <td class="px-4 py-3">
                    <input type="number" :value="param.value" @input="updateParamValue(param.name, $event.target.value)" class="px-3 py-1.5 border border-gray-300 rounded-button text-sm w-full focus:outline-none focus:ring-2 focus:ring-primary/20">
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-500">{{ param.description }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <!-- 操作按钮卡片 -->
      <div class="bg-white rounded-lg shadow p-6">
        <div class="flex items-center space-x-4">
          <button @click="sendRequest" class="px-6 py-2 bg-primary text-white rounded-button hover:bg-primary/90 flex items-center space-x-2">
            <i class="fas fa-paper-plane"></i>
            <span>发送请求</span>
          </button>
          <button @click="resetForm" class="px-6 py-2 bg-gray-100 text-gray-600 rounded-button hover:bg-gray-200 flex items-center space-x-2">
            <i class="fas fa-redo"></i>
            <span>重置</span>
          </button>
        </div>
      </div>
      <!-- 返回结果卡片 -->
      <div class="bg-white rounded-lg shadow p-6">
        <div class="space-y-4">
          <div class="flex items-center justify-between">
            <h2 class="text-lg font-medium">返回结果</h2>
            <div class="flex items-center space-x-4">
              <span class="text-gray-500">共 {{ totalItems }} 条数据</span>
              <button @click="copyResults" class="text-gray-500 hover:text-primary">
                <i class="far fa-copy"></i>
                <span class="ml-1">复制</span>
              </button>
              <button @click="clearResults" class="text-gray-500 hover:text-primary">
                <i class="far fa-trash-alt"></i>
                <span class="ml-1">清空</span>
              </button>
            </div>
          </div>
          <div class="bg-gray-50 rounded-lg p-6 space-y-4">
            <div class="grid grid-cols-1 gap-4">
              <div v-for="product in products" :key="product.id" class="flex items-start space-x-4 bg-white p-4 rounded-lg">
                <img :src="product.image" class="w-32 h-32 object-cover rounded-lg" :alt="product.name">
                <div class="flex-1 space-y-2">
                  <div class="flex items-center space-x-4 mb-2">
                    <span class="text-sm text-gray-500">ID: {{ product.id }}</span>
                    <span v-if="product.goodId" class="text-sm text-gray-500">商品ID: {{ product.goodId }}</span>
                  </div>
                  <h3 class="text-lg font-medium">{{ product.name }}</h3>
                  <p class="text-gray-500">{{ product.description }}</p>
                  <div class="flex items-center space-x-4">
                    <div class="text-gray-500">
                      <span>进货价：</span>
                      <span class="text-gray-900">¥{{ product.costPrice }}</span>
                    </div>
                    <div class="text-gray-500">
                      <span>销售价：</span>
                      <span class="text-primary">¥{{ product.salePrice }}</span>
                    </div>
                    <div class="text-gray-500">
                      <span>利润：</span>
                      <span class="text-green-500">¥{{ product.profit }}</span>
                    </div>
                    <div class="text-gray-500">
                      <span>已售：</span>
                      <span>{{ product.soldCount }}</span>
                    </div>
                    <div class="text-gray-500">
                      <span>销量：</span>
                      <span>{{ product.sales }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="flex items-center justify-between mt-6">
              <div class="text-gray-500">
                第 1-{{ products.length }} 条，共 {{ totalItems }} 条
              </div>
              <div class="flex items-center space-x-2">
                <button class="px-3 py-1 border border-gray-300 rounded-button text-gray-500 hover:border-primary hover:text-primary disabled:opacity-50 disabled:hover:border-gray-300 disabled:hover:text-gray-500" disabled>
                  <i class="fas fa-chevron-left"></i>
                </button>
                <button class="px-3 py-1 border border-gray-300 rounded-button text-white bg-primary hover:bg-primary/90">1</button>
                <button class="px-3 py-1 border border-gray-300 rounded-button text-gray-500 hover:border-primary hover:text-primary">2</button>
                <button class="px-3 py-1 border border-gray-300 rounded-button text-gray-500 hover:border-primary hover:text-primary">3</button>
                <button class="px-3 py-1 border border-gray-300 rounded-button text-gray-500 hover:border-primary hover:text-primary">4</button>
                <button class="px-3 py-1 border border-gray-300 rounded-button text-gray-500 hover:border-primary hover:text-primary">
                  <i class="fas fa-chevron-right"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
    <div class="fixed bottom-4 right-4">
      <button class="p-3 bg-secondary text-white rounded-full shadow-lg hover:bg-secondary/90">
        <i class="fas fa-expand-arrows-alt"></i>
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CallApi',
  data() {
    return {
      requestUrl: 'https://api.pinduoduo.com/v1/goods/detail',
      requestParams: [
        {
          name: 'goods_id',
          type: 'Number',
          required: true,
          value: 123456789,
          description: '商品 ID'
        },
        {
          name: 'shop_id',
          type: 'Number',
          required: false,
          value: 987654321,
          description: '店铺 ID'
        }
      ],
      products: [
        {
          id: 1,
          name: '立白洗衣液 茶籽洁净',
          description: '深层去渍 持久清香',
          costPrice: '19.90',
          salePrice: '29.90',
          profit: '10.00',
          soldCount: 3421,
          sales: 150,
          image: 'https://mastergo.com/ai/api/search-image?query=A photo of green dish soap bottle with transparent liquid, simple white background, product photography, high resolution, commercial quality&width=200&height=200&orientation=squarish&flag=cdd01b24d30a7705c5ad63691edc658e'
        },
        {
          id: 2,
          name: '飘柔洗发水 滋润养护',
          description: '修护受损发质 滋润柔顺',
          costPrice: '25.90',
          salePrice: '39.90',
          profit: '14.00',
          soldCount: 2876,
          sales: 89,
          image: 'https://mastergo.com/ai/api/search-image?query=A photo of premium shampoo bottle with golden cap, simple white background, product photography, high resolution, commercial quality&width=200&height=200&orientation=squarish&flag=23311795e66b8bfa40876b45152c6e43'
        },
        {
          id: 3,
          name: '百雀羚洗面奶 温和净透',
          description: '深层清洁 补水保湿',
          costPrice: '32.90',
          salePrice: '49.90',
          profit: '17.00',
          soldCount: 1932,
          sales: 234,
          image: 'https://mastergo.com/ai/api/search-image?query=A photo of facial cleanser tube with pump dispenser, simple white background, product photography, high resolution, commercial quality&width=200&height=200&orientation=squarish&flag=8e9725f5fe2f23b39b2fa280f357f974'
        }
      ]
    }
  },
  computed: {
    totalItems() {
      return this.products.length
    }
  },
  methods: {
    updateParamValue(paramName, value) {
      const param = this.requestParams.find(p => p.name === paramName)
      if (param) {
        param.value = value
      }
    },
    sendRequest() {
      // 模拟发送请求
      console.log('发送请求:', {
        url: this.requestUrl,
        params: this.requestParams
      })
      // 这里可以添加实际的API调用逻辑
    },
    resetForm() {
      this.requestParams.forEach(param => {
        if (param.name === 'goods_id') {
          param.value = 123456789
        } else if (param.name === 'shop_id') {
          param.value = 987654321
        }
      })
    },
    copyResults() {
      const resultsText = JSON.stringify(this.products, null, 2)
      navigator.clipboard.writeText(resultsText).then(() => {
        alert('结果已复制到剪贴板')
      }).catch(() => {
        alert('复制失败')
      })
    },
    clearResults() {
      this.products = []
    }
  }
}
</script>

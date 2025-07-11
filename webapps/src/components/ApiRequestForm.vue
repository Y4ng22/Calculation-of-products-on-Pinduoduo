<template>
  <el-card class="api-form-card" shadow="never">
    <template #header>
      <div class="card-header">
        <el-icon><Connection /></el-icon>
        <span>API 请求配置</span>
      </div>
    </template>
    
    <el-form 
      ref="formRef" 
      :model="formData" 
      :rules="rules" 
      label-width="120px"
      class="api-form"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="API 类型" prop="apiType">
            <el-select 
              v-model="formData.apiType" 
              placeholder="请选择API类型"
              style="width: 100%"
              @change="handleApiTypeChange"
            >
              <el-option
                v-for="item in apiTypes"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        
        <el-col :span="12">
          <el-form-item label="请求方法" prop="method">
            <el-select 
              v-model="formData.method" 
              placeholder="请选择请求方法"
              style="width: 100%"
            >
              <el-option label="GET" value="GET" />
              <el-option label="POST" value="POST" />
              <el-option label="PUT" value="PUT" />
              <el-option label="DELETE" value="DELETE" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      
      <!-- API模板选择器 -->
      <el-form-item v-if="formData.apiType === 'custom_api'" label="API模板">
        <el-select 
          v-model="selectedTemplate" 
          placeholder="选择常用API模板"
          style="width: 100%"
          @change="applyTemplate"
          clearable
        >
          <el-option
            v-for="template in apiTemplates"
            :key="template.name"
            :label="template.label"
            :value="template.name"
          />
        </el-select>
      </el-form-item>
      
      <el-form-item label="API 地址" prop="url">
        <el-input 
          v-model="formData.url" 
          placeholder="请输入API地址"
          :prefix-icon="Link"
        />
      </el-form-item>
      
      <el-form-item label="请求参数">
        <div :class="['params-container', { 'has-data': formData.params.length > 0 }]">
          <div class="params-flex-row">
            <div class="params-list">
              <div 
                v-for="(param, index) in formData.params" 
                :key="index" 
                class="param-item"
              >
                <el-row :gutter="10">
                  <el-col :span="8">
                    <el-input 
                      v-model="param.key" 
                      placeholder="参数名"
                      size="small"
                    />
                  </el-col>
                  <el-col :span="8">
                    <el-input 
                      v-model="param.value" 
                      placeholder="参数值"
                      size="small"
                    />
                  </el-col>
                  <el-col :span="4">
                    <el-select 
                      v-model="param.type" 
                      placeholder="类型"
                      size="small"
                      style="width: 100%"
                    >
                      <el-option label="String" value="string" />
                      <el-option label="Number" value="number" />
                      <el-option label="Boolean" value="boolean" />
                    </el-select>
                  </el-col>
                  <el-col :span="4">
                    <el-button 
                      type="danger" 
                      size="small" 
                      @click="removeParam(index)"
                      :icon="Delete"
                    >
                      删除
                    </el-button>
                  </el-col>
                </el-row>
              </div>
            </div>
            <el-button 
              type="primary" 
              size="small" 
              @click="addParam"
              :icon="Plus"
              class="add-param-btn"
            >
              添加参数
            </el-button>
          </div>
        </div>
      </el-form-item>
      
      <el-form-item label="请求头">
        <div :class="['headers-container', { 'has-data': formData.headers.length > 0 }]">
          <div class="headers-flex-row">
            <div class="headers-list">
              <div 
                v-for="(header, index) in formData.headers" 
                :key="index" 
                class="header-item"
              >
                <el-row :gutter="10">
                  <el-col :span="10">
                    <el-input 
                      v-model="header.key" 
                      placeholder="Header名"
                      size="small"
                    />
                  </el-col>
                  <el-col :span="10">
                    <el-input 
                      v-model="header.value" 
                      placeholder="Header值"
                      size="small"
                    />
                  </el-col>
                  <el-col :span="4">
                    <el-button 
                      type="danger" 
                      size="small" 
                      @click="removeHeader(index)"
                      :icon="Delete"
                    >
                      删除
                    </el-button>
                  </el-col>
                </el-row>
              </div>
            </div>
            <el-button 
              type="primary" 
              size="small" 
              @click="addHeader"
              :icon="Plus"
              class="add-header-btn"
            >
              添加Header
            </el-button>
          </div>
        </div>
      </el-form-item>
      
      <el-form-item>
        <el-button 
          type="primary" 
          @click="handleSubmit"
          :loading="loading"
          :icon="Search"
        >
          发送请求
        </el-button>
        <el-button 
          @click="handleReset"
          :icon="Refresh"
        >
          重置
        </el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import { 
  Connection, 
  Link, 
  Delete, 
  Plus, 
  Search, 
  Refresh 
} from '@element-plus/icons-vue'

export default {
  name: 'ApiRequestForm',
  components: {
    Connection,
    Link,
    Delete,
    Plus,
    Search,
    Refresh
  },
  emits: ['submit', 'reset'],
  data() {
    return {
      loading: false,
      selectedTemplate: '',
      formData: {
        apiType: '',
        method: 'GET',
        url: '',
        params: [],
        headers: []
      },
      // API模板配置
      apiTemplates: [
        {
          name: 'jsonplaceholder_posts',
          label: 'JSONPlaceholder - 获取文章列表',
          config: {
            url: 'https://jsonplaceholder.typicode.com/posts',
            method: 'GET',
            params: []
          }
        },
        {
          name: 'jsonplaceholder_users',
          label: 'JSONPlaceholder - 获取用户列表',
          config: {
            url: 'https://jsonplaceholder.typicode.com/users',
            method: 'GET',
            params: []
          }
        },
        {
          name: 'httpbin_get',
          label: 'HTTPBin - GET请求测试',
          config: {
            url: 'https://httpbin.org/get',
            method: 'GET',
            params: [
              { key: 'param1', value: 'value1', type: 'string' },
              { key: 'param2', value: '123', type: 'number' }
            ]
          }
        },
        {
          name: 'httpbin_post',
          label: 'HTTPBin - POST请求测试',
          config: {
            url: 'https://httpbin.org/post',
            method: 'POST',
            params: [
              { key: 'name', value: 'test', type: 'string' },
              { key: 'age', value: '25', type: 'number' }
            ]
          }
        },
        {
          name: 'local_test',
          label: '本地测试接口',
          config: {
            url: 'http://localhost:8089/test/api/test',
            method: 'GET',
            params: []
          }
        },
        {
          name: 'local_goods',
          label: '本地商品接口',
          config: {
            url: 'http://localhost:8089/api/goods',
            method: 'GET',
            params: [
              { key: 'page', value: '1', type: 'number' },
              { key: 'size', value: '10', type: 'number' }
            ]
          }
        }
      ],
      apiTypes: [
        { label: '自定义接口', value: 'custom_api' },
        { label: '测试接口', value: 'test_api' },
        { label: '商品搜索', value: 'goods_search' },
        { label: '商品详情', value: 'goods_detail' },
        { label: '商品推荐', value: 'goods_recommend' },
        { label: '商品分类', value: 'goods_category' },
        { label: '订单查询', value: 'order_query' },
        { label: '用户信息', value: 'user_info' }
      ],
      rules: {
        apiType: [
          { required: true, message: '请选择API类型', trigger: 'change' }
        ],
        method: [
          { required: true, message: '请选择请求方法', trigger: 'change' }
        ],
        url: [
          { required: true, message: '请输入API地址', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    handleApiTypeChange(value) {
      // 根据API类型预设URL和参数
      const apiConfigs = {
        custom_api: {
          url: 'http://localhost:8089/',
          method: 'GET',
          params: []
        },
        test_api: {
          url: 'http://localhost:8089/test/api/test',
          method: 'GET',
          params: []
        },
        goods_search: {
          url: 'http://localhost:8089/api/goods/search',
          method: 'GET',
          params: [
            { key: 'keyword', value: '', type: 'string' },
            { key: 'page', value: '1', type: 'number' },
            { key: 'size', value: '20', type: 'number' }
          ]
        },
        goods_detail: {
          url: 'http://localhost:8089/api/goods/detail',
          method: 'GET',
          params: [
            { key: 'goods_id', value: '', type: 'string' }
          ]
        },
        goods_recommend: {
          url: 'http://localhost:8089/api/goods/recommend',
          method: 'GET',
          params: [
            { key: 'category_id', value: '', type: 'string' },
            { key: 'limit', value: '10', type: 'number' }
          ]
        },
        goods_category: {
          url: 'http://localhost:8089/api/goods/category',
          method: 'GET',
          params: []
        },
        order_query: {
          url: 'http://localhost:8089/api/order/query',
          method: 'GET',
          params: [
            { key: 'order_sn', value: '', type: 'string' },
            { key: 'start_time', value: '', type: 'string' },
            { key: 'end_time', value: '', type: 'string' }
          ]
        },
        user_info: {
          url: 'http://localhost:8089/api/user/info',
          method: 'GET',
          params: []
        }
      }
      
      if (apiConfigs[value]) {
        const config = apiConfigs[value]
        this.formData.url = config.url
        this.formData.method = config.method
        this.formData.params = [...config.params]
        
        // 如果是自定义接口，设置默认前缀
        if (value === 'custom_api') {
          this.formData.url = 'http://localhost:8089/'
          this.$message.info('已设置默认前缀，请继续输入API路径')
        }
      }
    },
    
    addParam() {
      this.formData.params.push({
        key: '',
        value: '',
        type: 'string'
      })
    },
    
    removeParam(index) {
      this.formData.params.splice(index, 1)
    },
    
    addHeader() {
      this.formData.headers.push({
        key: '',
        value: ''
      })
    },
    
    removeHeader(index) {
      this.formData.headers.splice(index, 1)
    },
    
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
        this.loading = true
        
        // 构建请求参数
        const requestData = {
          ...this.formData,
          params: this.formData.params.filter(p => p.key && p.value),
          headers: this.formData.headers.filter(h => h.key && h.value)
        }
        
        this.$emit('submit', requestData)
      } catch (error) {
        console.error('表单验证失败:', error)
      } finally {
        this.loading = false
      }
    },
    
    handleReset() {
      this.$refs.formRef.resetFields()
      this.formData.params = []
      this.formData.headers = []
      this.selectedTemplate = ''
      this.$emit('reset')
    },
    
    applyTemplate(templateName) {
      if (!templateName) return
      
      const template = this.apiTemplates.find(t => t.name === templateName)
      if (template) {
        this.formData.url = template.config.url
        this.formData.method = template.config.method
        this.formData.params = [...template.config.params]
        this.$message.success(`已应用模板: ${template.label}`)
      }
    }
  }
}
</script>

<style scoped>
.api-form-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.api-form {
  padding: 20px 0;
}

.params-container,
.headers-container {
  border: none;
  background: none;
  padding: 0;
}
.params-container.has-data,
.headers-container.has-data {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 15px;
  background-color: #fafafa;
}

.param-item,
.header-item {
  margin-bottom: 10px;
}

.param-item:last-child,
.header-item:last-child {
  margin-bottom: 0;
}

.params-flex-row,
.headers-flex-row {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
}
.params-list,
.headers-list {
  flex: 1;
}
.add-param-btn,
.add-header-btn {
  margin-left: 16px;
  margin-top: 0;
  align-self: flex-start;
}
</style> 
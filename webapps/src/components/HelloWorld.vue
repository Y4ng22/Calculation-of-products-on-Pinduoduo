<template>
  <div class="home-page">
    <!-- 头部横幅区域 -->
    <div class="hero-section">
      <div class="hero-content">
        <div class="hero-text">
          <h1 class="hero-title">
            <el-icon class="title-icon"><Shop /></el-icon>
            拼多多开发平台
          </h1>
          <p class="hero-subtitle">专业的API接口测试与商品管理系统</p>
          <p class="hero-description">
            提供完整的API测试工具、Excel数据导入、商品管理、利润计算等功能，
            助力商家高效管理商品数据和业务运营
          </p>
          <div class="hero-actions">
            <el-button 
              type="primary" 
              size="large" 
              @click="navigateToAPI"
              :icon="Document"
            >
              开始API测试
            </el-button>
            <el-button 
              size="large" 
              @click="navigateToExcel"
              :icon="Upload"
            >
              Excel导入
            </el-button>
          </div>
        </div>
        <div class="hero-image">
          <div class="platform-illustration">
            <el-icon class="illustration-icon"><DataAnalysis /></el-icon>
          </div>
        </div>
      </div>
    </div>

    <!-- 功能特性区域 -->
    <div class="features-section">
      <div class="container">
        <div class="section-header">
          <h2>平台功能特性</h2>
          <p>为您提供全方位的开发和管理工具</p>
        </div>
        
        <div class="features-grid">
          <div class="feature-card" @click="navigateToAPI">
            <div class="feature-icon">
              <el-icon><Document /></el-icon>
            </div>
            <h3>API接口测试</h3>
            <p>支持多种HTTP方法，实时测试API接口，查看响应结果和状态码</p>
            <div class="feature-tags">
              <el-tag size="small" type="primary">GET/POST</el-tag>
              <el-tag size="small" type="success">实时响应</el-tag>
              <el-tag size="small" type="warning">状态监控</el-tag>
            </div>
          </div>

          <div class="feature-card" @click="navigateToExcel">
            <div class="feature-icon">
              <el-icon><Upload /></el-icon>
            </div>
            <h3>Excel数据导入</h3>
            <p>支持Excel文件批量导入商品数据，自动解析和验证数据格式</p>
            <div class="feature-tags">
              <el-tag size="small" type="primary">批量导入</el-tag>
              <el-tag size="small" type="success">格式验证</el-tag>
              <el-tag size="small" type="warning">历史记录</el-tag>
            </div>
          </div>

          <div class="feature-card" @click="navigateToProducts">
            <div class="feature-icon">
              <el-icon><Shop /></el-icon>
            </div>
            <h3>商品管理</h3>
            <p>完整的商品信息展示，支持图片预览、状态管理和详细信息查看</p>
            <div class="feature-tags">
              <el-tag size="small" type="primary">商品展示</el-tag>
              <el-tag size="small" type="success">图片预览</el-tag>
              <el-tag size="small" type="warning">状态管理</el-tag>
            </div>
          </div>

          <div class="feature-card">
            <div class="feature-icon">
              <el-icon><DataAnalysis /></el-icon>
            </div>
            <h3>利润计算</h3>
            <p>自动计算商品利润，生成详细的利润报告和数据分析</p>
            <div class="feature-tags">
              <el-tag size="small" type="primary">自动计算</el-tag>
              <el-tag size="small" type="success">利润报告</el-tag>
              <el-tag size="small" type="warning">数据分析</el-tag>
            </div>
          </div>

          <div class="feature-card" @click="navigateToMetadata">
            <div class="feature-icon">
              <el-icon><Setting /></el-icon>
            </div>
            <h3>元数据管理</h3>
            <p>支持数据表结构、字段、记录等元数据的可视化管理和导出</p>
            <div class="feature-tags">
              <el-tag size="small" type="primary">表结构</el-tag>
              <el-tag size="small" type="success">字段管理</el-tag>
              <el-tag size="small" type="warning">元数据导出</el-tag>
            </div>
          </div>

          <div class="feature-card">
            <div class="feature-icon">
              <el-icon><Download /></el-icon>
            </div>
            <h3>数据导出</h3>
            <p>支持多种格式的数据导出，方便数据分析和报表生成</p>
            <div class="feature-tags">
              <el-tag size="small" type="primary">多格式导出</el-tag>
              <el-tag size="small" type="success">报表生成</el-tag>
              <el-tag size="small" type="warning">数据分析</el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 快速操作区域 -->
    <div class="quick-actions-section">
      <div class="container">
        <div class="section-header">
          <h2>快速操作</h2>
          <p>一键访问常用功能</p>
        </div>
        
        <div class="quick-actions-grid">
          <div class="action-card" @click="navigateToAPI">
            <div class="action-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="action-content">
              <h3>API测试</h3>
              <p>测试接口功能和响应</p>
            </div>
            <el-icon class="action-arrow"><ArrowRight /></el-icon>
          </div>

          <div class="action-card" @click="navigateToExcel">
            <div class="action-icon">
              <el-icon><Upload /></el-icon>
            </div>
            <div class="action-content">
              <h3>数据导入</h3>
              <p>批量导入商品数据</p>
            </div>
            <el-icon class="action-arrow"><ArrowRight /></el-icon>
          </div>

          <div class="action-card" @click="navigateToProducts">
            <div class="action-icon">
              <el-icon><Shop /></el-icon>
            </div>
            <div class="action-content">
              <h3>商品管理</h3>
              <p>查看和管理商品信息</p>
            </div>
            <el-icon class="action-arrow"><ArrowRight /></el-icon>
          </div>

          <div class="action-card" @click="downloadTemplate">
            <div class="action-icon">
              <el-icon><Download /></el-icon>
            </div>
            <div class="action-content">
              <h3>下载模板</h3>
              <p>获取Excel导入模板</p>
            </div>
            <el-icon class="action-arrow"><ArrowRight /></el-icon>
          </div>

          <div class="action-card" @click="viewDocumentation">
            <div class="action-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="action-content">
              <h3>使用文档</h3>
              <p>查看详细使用说明</p>
            </div>
            <el-icon class="action-arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据统计区域 -->
    <div class="stats-section">
      <div class="container">
        <div class="section-header">
          <h2>平台数据统计</h2>
          <p>实时监控平台使用情况</p>
          <el-button 
            type="primary" 
            size="small" 
            @click="fetchPlatformStats"
            :loading="statsLoading"
            :icon="DataAnalysis"
            style="margin-top: 10px"
          >
            刷新数据
          </el-button>
        </div>
        
        <div class="stats-grid">
          <div class="stat-card" v-loading="statsLoading" @click="navigateToProducts" style="cursor: pointer;">
            <div class="stat-icon">
              <el-icon><Shop /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.products }}</div>
              <div class="stat-label">商品总数</div>
            </div>
            <div class="stat-trend info">
              <el-icon><DataAnalysis /></el-icon>
              <span>实时数据</span>
            </div>
          </div>

          <div class="stat-card" v-loading="statsLoading">
            <div class="stat-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.apiCalls }}</div>
              <div class="stat-label">API调用次数</div>
            </div>
            <div class="stat-trend info">
              <el-icon><DataAnalysis /></el-icon>
              <span>实时数据</span>
            </div>
          </div>

          <div class="stat-card" v-loading="statsLoading">
            <div class="stat-icon">
              <el-icon><Upload /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.fileUploads }}</div>
              <div class="stat-label">数据导入</div>
            </div>
            <div class="stat-trend info">
              <el-icon><DataAnalysis /></el-icon>
              <span>实时数据</span>
            </div>
          </div>

          <div class="stat-card" v-loading="statsLoading">
            <div class="stat-icon">
              <el-icon><DataAnalysis /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stats.profitRate.toFixed(2) }}%</div>
              <div class="stat-label">商品利润率</div>
            </div>
            <div class="stat-trend" :class="stats.profitRate >= 0 ? 'positive' : 'negative'">
              <el-icon><TrendCharts /></el-icon>
              <span>{{ stats.profitRate >= 0 ? '盈利' : '亏损' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 技术栈展示区域 -->
    <div class="tech-stack-section">
      <div class="container">
        <div class="section-header">
          <h2>技术栈</h2>
          <p>基于现代化的技术架构</p>
        </div>
        
        <div class="tech-stack-grid">
          <div class="tech-item">
            <div class="tech-icon">Vue</div>
            <div class="tech-name">Vue.js 3</div>
            <div class="tech-desc">前端框架</div>
          </div>
          
          <div class="tech-item">
            <div class="tech-icon">Element</div>
            <div class="tech-name">Element Plus</div>
            <div class="tech-desc">UI组件库</div>
          </div>
          
          <div class="tech-item">
            <div class="tech-icon">Spring</div>
            <div class="tech-name">Spring Boot</div>
            <div class="tech-desc">后端框架</div>
          </div>
          
          <div class="tech-item">
            <div class="tech-icon">MySQL</div>
            <div class="tech-name">MySQL</div>
            <div class="tech-desc">数据库</div>
          </div>
          
          <div class="tech-item">
            <div class="tech-icon">Axios</div>
            <div class="tech-name">Axios</div>
            <div class="tech-desc">HTTP客户端</div>
          </div>
          
          <div class="tech-item">
            <div class="tech-icon">XLSX</div>
            <div class="tech-name">SheetJS</div>
            <div class="tech-desc">Excel处理</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 页脚区域 -->
    <div class="footer-section">
      <div class="container">
        <div class="footer-content">
          <div class="footer-info">
            <h3>拼多多开发平台</h3>
            <p>专业的API接口测试与商品管理系统</p>
            <div class="footer-links">
              <a href="#" @click.prevent="viewDocumentation">使用文档</a>
              <a href="#" @click.prevent="contactSupport">技术支持</a>
              <a href="#" @click.prevent="aboutPlatform">关于平台</a>
            </div>
          </div>
          <div class="footer-stats">
            <div class="footer-stat">
              <div class="stat-value">{{ stats.totalUsers }}</div>
              <div class="stat-label">活跃用户</div>
            </div>
            <div class="footer-stat">
              <div class="stat-value">{{ stats.uptime }}</div>
              <div class="stat-label">运行时间</div>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2025 surf yangxinzhuo.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { 
  Shop, 
  Document, 
  Upload, 
  DataAnalysis, 
  Setting, 
  Download,
  ArrowRight,
  TrendCharts
} from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'HelloWorld',
  components: {
    Shop,
    Document,
    Upload,
    DataAnalysis,
    Setting,
    Download,
    ArrowRight,
    TrendCharts
  },
  data() {
    return {
      stats: {
        apiCalls: 0,
        products: 0,
        fileUploads: 0,
        profitRate: 0,
        totalUsers: '统计中...',
        uptime: '99.9%'
      },
      statsLoading: false
    }
  },
  methods: {
    // 获取平台统计数据
    async fetchPlatformStats() {
      this.statsLoading = true
      try {
        const response = await axios.get('/api/database/stats')
        if (response.data.code === 1) {
          const data = response.data.data
          this.stats = {
            apiCalls: data.apiCalls || 0, // API调用次数
            products: data.totalProducts || 0, // 数据库中的商品记录数
            fileUploads: data.totalProducts || 0, // 数据导入次数（暂时用商品数代替）
            profitRate: data.profitRate || 0, // 商品利润率
            totalUsers: data.activeUsers || '统计中...',
            uptime: data.uptime || '99.9%'
          }
          
          // 显示成功消息
          this.$message.success('统计数据已更新')
        } else {
          console.error('获取统计数据失败:', response.data.msg)
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
        // 如果获取失败，显示默认值
        this.stats = {
          apiCalls: 0,
          products: 0,
          fileUploads: 0,
          profitRate: 0,
          totalUsers: '获取失败',
          uptime: '99.9%'
        }
      } finally {
        this.statsLoading = false
      }
    },

    navigateToAPI() {
      this.$router.push('/APIRequest')
    },
    
    navigateToExcel() {
      this.$router.push('/APIRequest')
      // 可以添加参数来直接跳转到Excel部分
    },
    
    navigateToMetadata() {
      this.$router.push('/metadata')
    },
    
    navigateToProducts() {
      this.$router.push('/products')
    },
    
    downloadTemplate() {
      this.$message.info('模板下载功能将在API页面中提供')
      this.$router.push('/APIRequest')
    },
    
    viewDocumentation() {
      this.$message.info('使用文档功能开发中...')
    },
    
    contactSupport() {
      this.$message.info('技术支持功能开发中...')
    },
    
    aboutPlatform() {
      this.$message.info('关于平台功能开发中...')
    }
  },

  // 组件挂载时获取统计数据
  mounted() {
    this.fetchPlatformStats()
  }
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 头部横幅区域 */
.hero-section {
  padding: 80px 0;
  color: white;
}

.hero-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 60px;
}

.hero-text {
  flex: 1;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  margin: 0 0 20px 0;
  display: flex;
  align-items: center;
  gap: 15px;
}

.title-icon {
  font-size: 48px;
  color: #ffd700;
}

.hero-subtitle {
  font-size: 24px;
  margin: 0 0 15px 0;
  opacity: 0.9;
}

.hero-description {
  font-size: 16px;
  line-height: 1.6;
  margin: 0 0 30px 0;
  opacity: 0.8;
}

.hero-actions {
  display: flex;
  gap: 15px;
}

.hero-image {
  flex: 1;
  display: flex;
  justify-content: center;
}

.platform-illustration {
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.illustration-icon {
  font-size: 120px;
  color: rgba(255, 255, 255, 0.8);
}

/* 功能特性区域 */
.features-section {
  padding: 80px 0;
  background: white;
}

.section-header {
  text-align: center;
  margin-bottom: 60px;
}

.section-header h2 {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 15px 0;
  color: #303133;
}

.section-header p {
  font-size: 18px;
  color: #606266;
  margin: 0;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 30px;
}

.feature-card {
  background: #f8f9fa;
  border-radius: 15px;
  padding: 30px;
  text-align: center;
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #e9ecef;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  background: white;
}

.feature-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.feature-icon .el-icon {
  font-size: 36px;
  color: white;
}

.feature-card h3 {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 15px 0;
  color: #303133;
}

.feature-card p {
  font-size: 14px;
  line-height: 1.6;
  color: #606266;
  margin: 0 0 20px 0;
}

.feature-tags {
  display: flex;
  gap: 8px;
  justify-content: center;
  flex-wrap: wrap;
}

/* 快速操作区域 */
.quick-actions-section {
  padding: 80px 0;
  background: #f8f9fa;
}

.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.action-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  display: flex;
  align-items: center;
  gap: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e9ecef;
}

.action-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.action-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.action-icon .el-icon {
  font-size: 24px;
  color: white;
}

.action-content {
  flex: 1;
}

.action-content h3 {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 5px 0;
  color: #303133;
}

.action-content p {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

.action-arrow {
  font-size: 20px;
  color: #c0c4cc;
  transition: all 0.3s ease;
}

.action-card:hover .action-arrow {
  color: #667eea;
  transform: translateX(5px);
}

/* 数据统计区域 */
.stats-section {
  padding: 80px 0;
  background: white;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.stat-card {
  background: #f8f9fa;
  border-radius: 15px;
  padding: 30px;
  display: flex;
  align-items: center;
  gap: 20px;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  background: white;
}

.stat-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon .el-icon {
  font-size: 24px;
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  font-weight: 500;
}

.stat-trend.positive {
  color: #67c23a;
}

.stat-trend.negative {
  color: #f56c6c;
}

.stat-trend.info {
  color: #409eff;
}

/* 利润率特殊样式 */
.stat-card .stat-number {
  font-weight: 800;
}

.stat-card:nth-child(4) .stat-number {
  color: #67c23a;
  font-size: 28px;
}

.stat-card:nth-child(4) .stat-label {
  color: #67c23a;
  font-weight: 600;
}

/* 技术栈展示区域 */
.tech-stack-section {
  padding: 80px 0;
  background: #f8f9fa;
}

.tech-stack-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 30px;
}

.tech-item {
  background: white;
  border-radius: 12px;
  padding: 25px;
  text-align: center;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
}

.tech-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.tech-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
  font-size: 20px;
  font-weight: 600;
  color: white;
}

.tech-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
}

.tech-desc {
  font-size: 12px;
  color: #606266;
}

/* 页脚区域 */
.footer-section {
  padding: 60px 0 20px;
  background: #2c3e50;
  color: white;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 40px;
}

.footer-info h3 {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 15px 0;
}

.footer-info p {
  font-size: 14px;
  color: #bdc3c7;
  margin: 0 0 20px 0;
}

.footer-links {
  display: flex;
  gap: 20px;
}

.footer-links a {
  color: #bdc3c7;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
}

.footer-links a:hover {
  color: white;
}

.footer-stats {
  display: flex;
  gap: 40px;
}

.footer-stat {
  text-align: center;
}

.footer-stat .stat-value {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 5px;
}

.footer-stat .stat-label {
  font-size: 12px;
  color: #bdc3c7;
}

.footer-bottom {
  border-top: 1px solid #34495e;
  padding-top: 20px;
  text-align: center;
}

.footer-bottom p {
  font-size: 12px;
  color: #bdc3c7;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-content {
    flex-direction: column;
    text-align: center;
    gap: 40px;
  }
  
  .hero-title {
    font-size: 36px;
  }
  
  .hero-subtitle {
    font-size: 20px;
  }
  
  .hero-actions {
    justify-content: center;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .quick-actions-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .tech-stack-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .footer-content {
    flex-direction: column;
    gap: 30px;
    text-align: center;
  }
  
  .footer-stats {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .tech-stack-grid {
    grid-template-columns: 1fr;
  }
  
  .hero-title {
    font-size: 28px;
  }
  
  .section-header h2 {
    font-size: 28px;
  }
}
</style>

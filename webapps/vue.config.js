const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,

  devServer: {
    proxy: {
      // 所有以 /test 开头的请求，转发到 Spring Boot 后端
      '/test': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    }
  }
})


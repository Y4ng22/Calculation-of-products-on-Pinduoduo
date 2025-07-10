const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,

  devServer: {
    port: 8085,
    proxy: {
      // 所有以 /test 开头的请求，转发到 Spring Boot 后端
      '/test': {
        target: 'http://localhost:8089',
        changeOrigin: true
      }
    }
  }
})


<template>
  <div id="app">
    <nav class="app-nav">
      <div class="nav-container">
        <router-link to="/" class="nav-link">首页</router-link>
        <router-link to="/call-api" class="nav-link">API调用</router-link>
        <router-link to="/APIRequest" class="nav-link">拼多多开发平台</router-link>
        <router-link to="/metadata" class="nav-link">元数据管理</router-link>
      </div>
    </nav>

    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f7fa;
}

:root {
  --primary: #409eff;
  --secondary: #67c23a;
}

#app {
  min-height: 100vh;
}

.app-nav {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 15px 0;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  gap: 30px;
}

.nav-link {
  text-decoration: none;
  color: #606266;
  font-weight: 500;
  padding: 8px 16px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.nav-link:hover {
  color: #409eff;
  background-color: #ecf5ff;
}

.nav-link.router-link-active {
  color: #409eff;
  background-color: #ecf5ff;
}
</style>

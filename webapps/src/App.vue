<template>
  <div id="app">
    <nav>
      <router-link to="/">首页</router-link>
      |
      <router-link to="/random">不存在的页面</router-link>
    </nav>

    <router-view /> <!-- 显示路由页面 -->
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style scoped>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  padding: 20px;
  text-align: center;
}

nav {
  margin-bottom: 20px;
}

a {
  text-decoration: none;
  color: #42b983;
  margin: 0 10px;
}
</style>

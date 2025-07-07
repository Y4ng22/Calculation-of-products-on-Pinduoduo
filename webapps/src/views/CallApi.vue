<template>
  <div class="call-api-page">
    <h2>接口调用测试</h2>

    <form @submit.prevent="callApi">
      <label for="endpoint">接口路径：</label>
      <input
          id="endpoint"
          v-model="endpoint"
          placeholder="/api/test"
          class="input"
      />
      <button type="submit" class="button">调用接口</button>
    </form>

    <div class="result-box">
      <h3>接口返回结果：</h3>
      <pre>{{ response }}</pre>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'CallApi',
  data() {
    return {
      endpoint: '',
      response: ''
    }
  },
  methods: {
    async callApi() {
      if (!this.endpoint) {
        this.response = '❌ 请输入接口路径'
        return
      }

      try {
        const res = await axios.get(this.endpoint)
        this.response = JSON.stringify(res.data, null, 2)
      } catch (err) {
        this.response = `❌ 请求失败：${err.message}`
      }
    }
  }
}
</script>

<style scoped>
.call-api-page {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.input {
  width: 300px;
  padding: 8px;
  margin-right: 10px;
}

.button {
  padding: 8px 16px;
}

.result-box {
  margin-top: 30px;
  background: #f9f9f9;
  padding: 15px;
  border: 1px solid #ccc;
  white-space: pre-wrap;
}
</style>

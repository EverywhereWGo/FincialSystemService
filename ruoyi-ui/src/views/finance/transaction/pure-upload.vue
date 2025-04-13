<template>
  <div class="app-container">
    <h2>基础文件上传测试</h2>
    
    <form ref="uploadForm" action="/dev-api/common/upload" method="post" enctype="multipart/form-data">
      <input type="file" name="file">
      <button type="button" @click="submitForm">上传</button>
    </form>
    
    <div style="margin-top: 20px">
      <h3>上传结果</h3>
      <pre>{{ uploadResult }}</pre>
    </div>
  </div>
</template>

<script>
import { getToken } from "@/utils/auth";

export default {
  name: "PureUpload",
  data() {
    return {
      uploadResult: null
    };
  },
  methods: {
    submitForm() {
      const formData = new FormData();
      const fileInput = this.$refs.uploadForm.querySelector('input[type="file"]');
      
      if (fileInput.files.length === 0) {
        this.$message.error('请选择文件');
        return;
      }
      
      const file = fileInput.files[0];
      formData.append('file', file);
      
      // 使用 XMLHttpRequest 上传
      const xhr = new XMLHttpRequest();
      xhr.open('POST', '/dev-api/common/upload', true);
      xhr.setRequestHeader('Authorization', 'Bearer ' + getToken());
      
      xhr.onload = () => {
        if (xhr.status === 200) {
          try {
            const response = JSON.parse(xhr.responseText);
            this.uploadResult = response;
            this.$message.success('上传成功');
          } catch (e) {
            this.uploadResult = { error: '解析响应失败', text: xhr.responseText };
            this.$message.error('上传失败: 解析响应失败');
          }
        } else {
          this.uploadResult = { error: xhr.statusText, status: xhr.status };
          this.$message.error('上传失败: ' + xhr.statusText);
        }
      };
      
      xhr.onerror = () => {
        this.uploadResult = { error: '网络错误' };
        this.$message.error('上传失败: 网络错误');
      };
      
      xhr.send(formData);
    }
  }
};
</script>
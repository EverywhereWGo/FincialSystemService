<template>
  <div class="app-container">
    <h2>文件上传测试</h2>
    
    <el-form label-width="120px">
      <el-form-item label="简单上传测试">
        <el-upload
          class="upload-demo"
          action="/dev-api/common/upload"
          :headers="headers"
          name="file"
          :on-preview="handlePreview"
          :on-remove="handleRemove"
          :on-success="handleSuccess"
          :on-error="handleError"
          :before-upload="beforeUpload"
          list-type="picture">
          <el-button size="small" type="primary">点击上传</el-button>
          <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
        </el-upload>
      </el-form-item>
    </el-form>

    <div style="margin-top: 20px">
      <h3>上传结果</h3>
      <pre>{{ uploadResult }}</pre>
    </div>
  </div>
</template>

<script>
import { getToken } from "@/utils/auth";

export default {
  name: "TestUpload",
  data() {
    return {
      headers: {
        Authorization: "Bearer " + getToken()
      },
      uploadResult: null
    };
  },
  methods: {
    handleRemove(file, fileList) {
      console.log(file, fileList);
    },
    handlePreview(file) {
      console.log(file);
    },
    handleSuccess(response, file, fileList) {
      console.log('上传成功', response);
      this.uploadResult = response;
      this.$message.success('上传成功');
    },
    handleError(err, file, fileList) {
      console.error('上传失败', err);
      this.uploadResult = err;
      this.$message.error('上传失败: ' + (err.message || '未知错误'));
    },
    beforeUpload(file) {
      const isJPG = file.type === 'image/jpeg';
      const isPNG = file.type === 'image/png';
      const isLt500K = file.size / 1024 < 500;

      if (!isJPG && !isPNG) {
        this.$message.error('上传图片只能是 JPG 或 PNG 格式!');
      }
      if (!isLt500K) {
        this.$message.error('上传图片大小不能超过 500KB!');
      }
      return (isJPG || isPNG) && isLt500K;
    }
  }
};
</script>
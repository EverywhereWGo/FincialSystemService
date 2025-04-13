<template>
  <div>
    <div class="user-avatar-container">
      <img
        v-if="user.avatar"
        :src="avatar"
        class="user-avatar"
        @click="handleClickAvatar"
      />
      <div v-else class="user-avatar-placeholder" @click="handleClickAvatar">
        <el-avatar :size="100" icon="el-icon-user-solid"></el-avatar>
      </div>
    </div>
    <div class="username">{{ user.nickName || user.userName }}</div>
    <el-upload
      ref="upload"
      :action="uploadImgUrl"
      :show-file-list="false"
      :on-success="handleAvatarSuccess"
      :before-upload="beforeUpload"
      :headers="headers"
      style="display: none"
    >
      <input ref="avatarInput" type="file" accept="image/*" @change="uploadImg" />
    </el-upload>
  </div>
</template>

<script>
import { getToken } from "@/utils/auth";

export default {
  props: {
    user: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      // 头像上传地址
      uploadImgUrl: process.env.VUE_APP_BASE_API + "/system/user/profile/avatar",
      headers: {
        Authorization: "Bearer " + getToken()
      }
    };
  },
  computed: {
    avatar() {
      return this.user.avatar ? process.env.VUE_APP_BASE_API + this.user.avatar : require("@/assets/images/profile.jpg");
    }
  },
  methods: {
    // 点击头像上传
    handleClickAvatar() {
      this.$refs.avatarInput.click();
    },
    // 上传前校验
    beforeUpload(file) {
      const isJPG = file.type === "image/jpeg" || file.type === "image/png";
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        this.$message.error("上传头像图片只能是JPG或PNG格式!");
        return false;
      }
      if (!isLt2M) {
        this.$message.error("上传头像图片大小不能超过2MB!");
        return false;
      }
      return true;
    },
    // 文件上传中处理
    uploadImg() {
      this.$refs.upload.submit();
    },
    // 上传成功处理
    handleAvatarSuccess(res) {
      if (res.code === 200) {
        this.$message.success("修改头像成功");
        this.user.avatar = res.imgUrl;
        this.$emit("update:avatar", res.imgUrl);
      } else {
        this.$message.error(res.msg);
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.user-avatar-container {
  margin-top: 20px;
  margin-bottom: 10px;
  text-align: center;
  
  .user-avatar {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    cursor: pointer;
  }
  
  .user-avatar-placeholder {
    cursor: pointer;
    display: inline-block;
  }
}

.username {
  font-size: 16px;
  font-weight: 500;
  margin: 10px 0;
  text-align: center;
  color: #333;
}
</style>
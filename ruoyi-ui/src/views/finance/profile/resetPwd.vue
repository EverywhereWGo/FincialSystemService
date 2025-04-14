<template>
  <el-form ref="form" :model="user" :rules="rules" label-width="100px">
    <el-form-item label="旧密码" prop="oldPassword">
      <el-input v-model="user.oldPassword" placeholder="请输入旧密码" type="password" show-password />
    </el-form-item>
    <el-form-item label="新密码" prop="newPassword">
      <el-input v-model="user.newPassword" placeholder="请输入新密码" type="password" show-password />
    </el-form-item>
    <el-form-item label="确认密码" prop="confirmPassword">
      <el-input v-model="user.confirmPassword" placeholder="请确认密码" type="password" show-password />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" size="mini" @click="submit">保存</el-button>
      <el-button type="danger" size="mini" @click="close">关闭</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { updateUserPassword } from "@/api/finance/auth";

export default {
  props: {
    user: {
      type: Object
    }
  },
  data() {
    const validatePass = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error("密码不能小于6个字符"));
      } else {
        callback();
      }
    };
    const validatePass2 = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("请再次输入密码"));
      } else if (value !== this.user.newPassword) {
        callback(new Error("两次输入密码不一致"));
      } else {
        callback();
      }
    };
    return {
      user: {
        oldPassword: "",
        newPassword: "",
        confirmPassword: ""
      },
      // 表单校验
      rules: {
        oldPassword: [
          { required: true, message: "旧密码不能为空", trigger: "blur" }
        ],
        newPassword: [
          { required: true, message: "新密码不能为空", trigger: "blur" },
          { validator: validatePass, trigger: "blur" }
        ],
        confirmPassword: [
          { required: true, message: "确认密码不能为空", trigger: "blur" },
          { validator: validatePass2, trigger: "blur" }
        ]
      }
    };
  },
  methods: {
    submit() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 调用API更新密码
          const data = {
            userId: this.user.id,
            oldPassword: this.user.oldPassword,
            newPassword: this.user.newPassword
          };
          
          updateUserPassword(data).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.close();
          }).catch(error => {
            console.error("密码修改失败", error);
          });
        }
      });
    },
    close() {
      this.user = {
        oldPassword: "",
        newPassword: "",
        confirmPassword: ""
      };
    }
  }
};
</script>
<template>
  <div class="register">
    <el-form ref="registerForm" :model="registerForm" :rules="registerRules" class="register-form">
      <h3 class="title">财务管理系统注册</h3>
      <el-form-item prop="username">
        <el-input
          v-model="registerForm.username"
          type="text"
          auto-complete="off"
          placeholder="账号"
        >
          <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="registerForm.password"
          type="password"
          auto-complete="off"
          placeholder="密码"
        >
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="confirmPassword">
        <el-input
          v-model="registerForm.confirmPassword"
          type="password"
          auto-complete="off"
          placeholder="确认密码"
        >
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="nickname">
        <el-input
          v-model="registerForm.nickname"
          type="text"
          auto-complete="off"
          placeholder="昵称"
        >
          <svg-icon slot="prefix" icon-class="peoples" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="phone">
        <el-input
          v-model="registerForm.phone"
          type="text"
          auto-complete="off"
          placeholder="手机号码"
        >
          <svg-icon slot="prefix" icon-class="phone" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="email">
        <el-input
          v-model="registerForm.email"
          type="text"
          auto-complete="off"
          placeholder="邮箱"
        >
          <svg-icon slot="prefix" icon-class="email" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="medium"
          type="primary"
          style="width:100%;"
          @click.native.prevent="handleRegister"
        >
          <span v-if="!loading">注 册</span>
          <span v-else>注 册 中...</span>
        </el-button>
      </el-form-item>
      <div class="login-link">
        <router-link class="link-type" :to="'/finance/login'">使用已有账户登录</router-link>
      </div>
    </el-form>
  </div>
</template>

<script>
import { register } from "@/api/finance/auth";

export default {
  name: "FinanceRegister",
  data() {
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error("密码长度不能小于6位"));
      } else {
        callback();
      }
    };
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        callback();
      }
    };
    const validatePhone = (rule, value, callback) => {
      if (value && !/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error("请输入正确的手机号码"));
      } else {
        callback();
      }
    };
    const validateEmail = (rule, value, callback) => {
      if (value && !/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(value)) {
        callback(new Error("请输入正确的邮箱地址"));
      } else {
        callback();
      }
    };
    return {
      registerForm: {
        username: "",
        password: "",
        confirmPassword: "",
        nickname: "",
        phone: "",
        email: ""
      },
      registerRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" },
          { min: 2, max: 20, message: "账号长度在2-20个字符之间", trigger: "blur" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" },
          { validator: validatePassword, trigger: "blur" }
        ],
        confirmPassword: [
          { required: true, trigger: "blur", message: "请再次输入您的密码" },
          { validator: validateConfirmPassword, trigger: "blur" }
        ],
        nickname: [
          { required: false, trigger: "blur", message: "请输入您的昵称" }
        ],
        phone: [
          { required: false, trigger: "blur", message: "请输入您的手机号码" },
          { validator: validatePhone, trigger: "blur" }
        ],
        email: [
          { required: false, trigger: "blur", message: "请输入您的邮箱" },
          { validator: validateEmail, trigger: "blur" }
        ]
      },
      loading: false
    };
  },
  methods: {
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true;
          const registerData = {
            username: this.registerForm.username,
            password: this.registerForm.password,
            nickname: this.registerForm.nickname,
            phone: this.registerForm.phone,
            email: this.registerForm.email
          };
          register(registerData)
            .then(res => {
              if (res.code === 200) {
                this.$message.success("注册成功，请登录");
                this.$router.push("/finance/login");
              } else {
                this.$message.error(res.msg);
              }
              this.loading = false;
            })
            .catch(() => {
              this.loading = false;
            });
        }
      });
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss">
.register {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-color: #f5f5f5;
  background-size: cover;
}
.title {
  margin: 0 auto 30px auto;
  text-align: center;
  color: #707070;
}

.register-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;
  box-shadow: 0 0 10px #ddd;
  .el-input {
    height: 38px;
    input {
      height: 38px;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}
.login-link {
  text-align: right;
  margin-top: 10px;
  
  .link-type {
    color: #409EFF;
  }
}
.register-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}
</style>
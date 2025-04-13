<template>
  <div class="login">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
      <h3 class="title">财务管理系统登录</h3>
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          type="text"
          auto-complete="off"
          placeholder="账号"
        >
          <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          auto-complete="off"
          placeholder="密码"
          @keyup.enter.native="handleLogin"
        >
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="deviceInfo" v-if="false">
        <el-input v-model="loginForm.deviceInfo" />
      </el-form-item>
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="medium"
          type="primary"
          style="width:100%;"
          @click.native.prevent="handleLogin"
        >
          <span v-if="!loading">登 录</span>
          <span v-else>登 录 中...</span>
        </el-button>
      </el-form-item>
      <div class="login-register-link">
        <router-link class="link-type" :to="'/finance/register'">立即注册</router-link>
      </div>
    </el-form>
  </div>
</template>

<script>
import { login } from "@/api/finance/auth";
import { getFinanceToken, setFinanceToken, removeFinanceToken } from "@/utils/financeAuth";
import Cookies from "js-cookie";
import { getDeviceInfo } from "@/utils/device";

export default {
  name: "FinanceLogin",
  data() {
    return {
      loginForm: {
        username: "",
        password: "",
        deviceInfo: getDeviceInfo()
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ]
      },
      loading: false,
      redirect: undefined
    };
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  created() {
    // 如果已登录则直接跳转到首页
    if (getFinanceToken()) {
      this.$router.push({ path: this.redirect || "/finance/index" });
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          login(this.loginForm.username, this.loginForm.password, this.loginForm.deviceInfo)
            .then(res => {
              if (res.code === 200) {
                // 存储Token到本地存储
                setFinanceToken(res.data.token);
                localStorage.setItem("financeTokenExpiry", res.data.expiryTime);
                localStorage.setItem("financeUser", JSON.stringify(res.data.user));
                
                this.$router.push({ path: this.redirect || "/finance/index" });
                this.loading = false;
              } else {
                this.loading = false;
                this.$message.error(res.msg);
              }
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
.login {
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

.login-form {
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
.login-register-link {
  text-align: right;
  margin-top: 10px;
  
  .link-type {
    color: #409EFF;
  }
}
.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}
.login-code {
  width: 33%;
  height: 38px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
  }
}
</style>
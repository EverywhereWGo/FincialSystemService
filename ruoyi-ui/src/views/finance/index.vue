<template>
  <div class="app-container">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>财务系统首页</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="handleLogout">退出登录</el-button>
          </div>
          <div class="user-info">
            <div class="user-avatar">
              <el-avatar :size="80" icon="el-icon-user-solid"></el-avatar>
            </div>
            <div class="user-details">
              <h2>欢迎您，{{ userInfo.nickname || userInfo.username }}</h2>
              <p>用户名：{{ userInfo.username }}</p>
              <p>角色：{{ userRole }}</p>
              <p>上次登录：{{ lastLoginTime }}</p>
              <p>登录IP：{{ lastLoginIp }}</p>
            </div>
          </div>
          <div class="system-menu">
            <h3>系统功能</h3>
            <el-row :gutter="20" class="menu-grid">
              <el-col :xs="12" :sm="8" :md="6" :lg="4" v-for="(menu, index) in menuList" :key="index">
                <el-card shadow="hover" class="menu-item" @click.native="goToModule(menu.path)">
                  <div><i :class="menu.icon"></i></div>
                  <div>{{ menu.name }}</div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getLatestLogin } from "@/api/finance/auth";
import { mapState } from "vuex";
import { getToken } from "@/utils/auth";

export default {
  name: "FinanceIndex",
  data() {
    return {
      lastLoginTime: "暂无记录",
      lastLoginIp: "暂无记录",
      menuList: [
        { name: "交易记录", path: "/finance/transaction", icon: "el-icon-money" },
        { name: "分类管理", path: "/finance/category", icon: "el-icon-collection" },
        { name: "预算管理", path: "/finance/budget", icon: "el-icon-wallet" },
        { name: "统计报表", path: "/finance/statistics", icon: "el-icon-data-line" },
        { name: "通知管理", path: "/finance/notification", icon: "el-icon-bell" },
        { name: "个人设置", path: "/finance/profile", icon: "el-icon-user" }
      ],
      isMainSystemUser: false
    };
  },
  computed: {
    ...mapState({
      financeUserInfo: state => state.financeUser.user,
      mainUserInfo: state => state.user.name,
      usingMainToken: state => state.financeUser.usingMainToken
    }),
    userInfo() {
      // 如果是原系统用户访问，使用原系统用户信息
      if (this.usingMainToken) {
        return {
          username: this.mainUserInfo,
          nickname: this.mainUserInfo,
          role: 'admin'  // 假设原系统用户都是管理员
        };
      }
      return this.financeUserInfo;
    },
    userRole() {
      if (this.userInfo.role === "admin") {
        return "管理员";
      } else {
        return "普通用户";
      }
    }
  },
  created() {
    // 检查是否是主系统用户访问财务系统
    this.isMainSystemUser = this.usingMainToken;
    
    // 如果是财务系统用户，才获取登录历史
    if (!this.isMainSystemUser) {
      this.getLoginHistory();
    }
  },
  methods: {
    getLoginHistory() {
      if (this.financeUserInfo && this.financeUserInfo.id) {
        getLatestLogin(this.financeUserInfo.id)
          .then(res => {
            if (res.code === 200 && res.data) {
              this.lastLoginTime = res.data.loginTime || "暂无记录";
              this.lastLoginIp = res.data.ipAddress || "暂无记录";
            }
          })
          .catch(() => {
            this.lastLoginTime = "获取失败";
            this.lastLoginIp = "获取失败";
          });
      }
    },
    goToModule(path) {
      this.$router.push(path);
    },
    handleLogout() {
      // 如果是主系统用户，不执行财务系统登出
      if (this.isMainSystemUser) {
        this.$router.push("/");
        return;
      }
      
      this.$confirm("确定要退出登录吗?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          this.$store.dispatch("FinanceLogOut").then(() => {
            this.$router.push("/finance/login");
          });
        })
        .catch(() => {});
    }
  }
};
</script>

<style scoped>
.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 4px;
}
.user-avatar {
  margin-right: 20px;
}
.user-details h2 {
  margin-top: 0;
  margin-bottom: 10px;
  color: #303133;
}
.user-details p {
  margin: 5px 0;
  color: #606266;
}
.system-menu {
  margin-top: 20px;
}
.system-menu h3 {
  margin-bottom: 20px;
  font-weight: 500;
  color: #303133;
}
.menu-grid {
  margin-bottom: 20px;
}
.menu-item {
  text-align: center;
  cursor: pointer;
  height: 100px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  transition: all 0.3s;
}
.menu-item:hover {
  background-color: #ecf5ff;
  color: #409EFF;
}
.menu-item i {
  font-size: 24px;
  margin-bottom: 10px;
}
</style>
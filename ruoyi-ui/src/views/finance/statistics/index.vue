<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>收支统计</span>
        <el-date-picker
          style="float: right; margin-left: 10px;"
          v-model="dateRange"
          type="daterange"
          align="right"
          unlink-panels
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :picker-options="pickerOptions"
          @change="handleDateChange"
        ></el-date-picker>
        <el-input
          v-model="userIdInput"
          style="float: right; width: 150px; margin-right: 10px;"
          placeholder="用户ID"
          size="small"
        >
          <el-button slot="append" icon="el-icon-search" @click="switchUser"></el-button>
        </el-input>
        <el-button 
          v-if="currentUserId !== originalUserId" 
          type="text" 
          style="float: right; margin-right: 10px;" 
          @click="resetToCurrentUser">
          返回个人数据
        </el-button>
        <span v-if="currentUserId !== originalUserId" style="float: right; margin-right: 10px; color: #E6A23C;">
          <i class="el-icon-warning"></i> 当前查看: 用户ID {{ currentUserId }}
        </span>
      </div>
      <div class="summary-cards">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-card shadow="hover" class="summary-card income">
              <div class="summary-title">收入</div>
              <div class="summary-amount">¥{{ formatAmount(amountStat.income || 0) }}</div>
              <div class="summary-desc">{{ dateRangeDesc }}总收入</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="summary-card expense">
              <div class="summary-title">支出</div>
              <div class="summary-amount">¥{{ formatAmount(amountStat.expense || 0) }}</div>
              <div class="summary-desc">{{ dateRangeDesc }}总支出</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="summary-card balance">
              <div class="summary-title">结余</div>
              <div class="summary-amount">¥{{ formatAmount(amountStat.balance || 0) }}</div>
              <div class="summary-desc">{{ dateRangeDesc }}净收入</div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header" class="clearfix">
            <span>收支趋势</span>
            <el-radio-group v-model="trendType" size="mini" style="float: right;">
              <el-radio-button label="week">周</el-radio-button>
              <el-radio-button label="month">月</el-radio-button>
              <el-radio-button label="year">年</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="trendChart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header" class="clearfix">
            <span>支出分布</span>
            <el-radio-group v-model="pieChartType" size="mini" style="float: right;">
              <el-radio-button label="1">支出</el-radio-button>
              <el-radio-button label="2">收入</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="pieChart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="box-card" style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>分类明细</span>
        <el-radio-group v-model="detailType" size="mini" style="float: right;">
          <el-radio-button label="1">支出</el-radio-button>
          <el-radio-button label="2">收入</el-radio-button>
        </el-radio-group>
      </div>
      <el-table :data="categoryDetails" style="width: 100%" v-loading="loading">
        <el-table-column prop="categoryName" label="分类名称"></el-table-column>
        <el-table-column prop="amount" label="金额">
          <template slot-scope="scope">
            ¥{{ formatAmount(scope.row.amount) }}
          </template>
        </el-table-column>
        <el-table-column prop="percentage" label="占比">
          <template slot-scope="scope">
            <el-progress 
              :percentage="scope.row.percentage" 
              :color="getRandomColor(scope.row.categoryId)"
              :stroke-width="15">
            </el-progress>
          </template>
        </el-table-column>
        <el-table-column prop="count" label="笔数"></el-table-column>
      </el-table>
    </el-card>

    <el-card class="box-card" style="margin-top: 20px;">
      <div slot="header" class="clearfix">
        <span>预算执行情况</span>
      </div>
      <div v-if="budgetData.length > 0">
        <div v-for="(item, index) in budgetData" :key="index" class="budget-item">
          <div class="budget-info">
            <div class="budget-category">{{ item.categoryName || '总预算' }}</div>
            <div class="budget-amount">
              <span>已用: ¥{{ formatAmount(item.spentAmount || 0) }}</span>
              <span> / </span>
              <span>预算: ¥{{ formatAmount(item.amount || 0) }}</span>
            </div>
          </div>
          <el-progress 
            :text-inside="true" 
            :stroke-width="20" 
            :percentage="item.percentage"
            :color="getBudgetColor(item.percentage)">
          </el-progress>
        </div>
      </div>
      <div v-else class="no-data">
        <i class="el-icon-warning"></i>
        <p>暂无预算数据</p>
      </div>
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import { 
  getCategoryStatistic, 
  getYearStatistic, 
  getTrendStatistic, 
  getBudgetStatistic,
  getTopTransactions,
  getAmountStatistic 
} from "@/api/finance/statistic";

export default {
  name: "FinanceStatistics",
  data() {
    return {
      loading: false,
      dateRange: [new Date(new Date().setDate(1)), new Date()], // 默认当月
      pickerOptions: {
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setMonth(start.getMonth() - 1);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setMonth(start.getMonth() - 3);
            picker.$emit('pick', [start, end]);
          }
        }]
      },
      trendType: 'month',
      pieChartType: '1',
      detailType: '1',
      amountStat: {
        income: 0,
        expense: 0,
        balance: 0
      },
      categoryDetails: [],
      yearData: [],
      trendData: [],
      budgetData: [],
      trendChart: null,
      pieChart: null,
      colors: ['#FF5722', '#4CAF50', '#2196F3', '#9C27B0', '#FFC107', '#3F51B5', '#E91E63', '#009688'],
      currentUserId: null, // 存储当前用户ID
      userIdInput: '', // 用户输入的ID
      originalUserId: null // 保存当前登录用户的ID
    };
  },
  computed: {
    dateRangeDesc() {
      if (!this.dateRange || this.dateRange.length !== 2) return '';
      const start = this.dateRange[0];
      const end = this.dateRange[1];
      if (start.getFullYear() === end.getFullYear() && 
          start.getMonth() === end.getMonth() && 
          start.getDate() === 1 && 
          end.getDate() === new Date(end.getFullYear(), end.getMonth() + 1, 0).getDate()) {
        return `${start.getFullYear()}年${start.getMonth() + 1}月`;
      }
      return `${start.toLocaleDateString()} - ${end.toLocaleDateString()}`;
    },
    startTime() {
      return this.dateRange && this.dateRange.length === 2 ? this.dateRange[0].getTime() : new Date().setDate(1);
    },
    endTime() {
      return this.dateRange && this.dateRange.length === 2 ? this.dateRange[1].getTime() : new Date().getTime();
    },
    userId() {
      // 始终返回currentUserId，避免可能的空值
      return this.currentUserId || 1; // 如果为空则返回默认值1
    }
  },
  mounted() {
    // 获取用户ID
    this.getUserId();
    
    // 初始化图表
    this.initCharts();
    
    // 如果已经获取到用户ID，则加载数据
    if (this.currentUserId) {
      this.getStatisticsData();
    }
  },
  methods: {
    // 切换用户ID
    switchUser() {
      if (!this.userIdInput || isNaN(this.userIdInput)) {
        this.$message.warning('请输入有效的用户ID');
        return;
      }
      
      const newUserId = parseInt(this.userIdInput);
      if (newUserId <= 0) {
        this.$message.warning('用户ID必须为正整数');
        return;
      }
      
      // 暂时移除权限检查，默认允许所有用户查看任意ID的统计数据
      /*
      // 检查权限：只有管理员或原始ID是切换ID才允许
      if (newUserId !== this.originalUserId) {
        // 检查当前用户是否为管理员
        const user = this.$store.getters.financeUser;
        // 打印用户信息以便调试
        console.log('当前用户信息:', user);
        
        // 检查角色，支持多种可能的管理员角色标识
        const isAdmin = user && (
          user.role === 'admin' || 
          user.role === 'ROLE_ADMIN' ||
          (user.roles && user.roles.some(r => r === 'admin' || r === 'ROLE_ADMIN')) ||
          user.permissions && user.permissions.some(p => p === 'system:user:list')
        );
        
        if (!isAdmin) {
          this.$message.error('权限不足，只有管理员可以查看其他用户的统计数据');
          this.userIdInput = this.originalUserId.toString();
          return;
        }
      }
      */
      
      // 切换用户ID
      this.currentUserId = newUserId;
      this.$message.success(`已切换到用户ID: ${newUserId}`);
      
      // 重新加载数据
      this.getStatisticsData();
    },
    
    // 重置为当前登录用户
    resetToCurrentUser() {
      if (this.originalUserId && this.currentUserId !== this.originalUserId) {
        this.currentUserId = this.originalUserId;
        this.userIdInput = this.originalUserId.toString();
        this.$message.success('已重置为当前登录用户');
        this.getStatisticsData();
      }
    },
    
    // 获取用户ID方法
    getUserId() {
      try {
        // 尝试从store获取
        const user = this.$store.getters.financeUser;
        if (user && user.id) {
          this.currentUserId = user.id;
          this.originalUserId = user.id;
          this.userIdInput = user.id.toString();
          return;
        }
        
        // 尝试从localStorage获取
        const userStr = localStorage.getItem('financeUser');
        if (userStr) {
          const userObj = JSON.parse(userStr);
          if (userObj && userObj.id) {
            this.currentUserId = userObj.id;
            this.originalUserId = userObj.id;
            this.userIdInput = userObj.id.toString();
            return;
          }
        }
        
        // 使用默认ID
        this.currentUserId = 1;
        this.originalUserId = 1;
        this.userIdInput = "1";
        console.warn('无法获取用户ID，使用默认ID: 1');
      } catch (e) {
        console.error('获取用户ID失败', e);
        this.currentUserId = 1;
        this.originalUserId = 1;
        this.userIdInput = "1";
      }
    },
    formatAmount(amount) {
      return Number(amount).toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
    },
    handleDateChange() {
      this.getStatisticsData();
    },
    getStatisticsData() {
      this.loading = true;
      
      // 检查userId是否存在
      if (!this.userId) {
        this.$message.error('用户ID未获取，无法加载数据');
        this.loading = false;
        return;
      }
      
      // 获取收支总额
      getAmountStatistic({
        userId: this.userId,
        startTime: this.startTime,
        endTime: this.endTime
      }).then(response => {
        if (response.code === 200) {
          this.amountStat = response.data || {
            income: 0,
            expense: 0,
            balance: 0
          };
        }
      });
      
      // 获取分类统计
      this.getCategoryData();
      
      // 根据趋势类型获取趋势数据
      this.getTrendData();
      
      // 获取预算数据
      const date = new Date(this.dateRange[0]);
      const yearMonth = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
      
      getBudgetStatistic({
        userId: this.userId,
        month: yearMonth
      }).then(response => {
        if (response.code === 200) {
          if (response.data && response.data.categories) {
            this.budgetData = response.data.categories;
          } else {
            this.budgetData = [];
          }
        }
        this.loading = false;
        this.updateCharts();
      }).catch(() => {
        this.loading = false;
      });
    },
    getCategoryData() {
      // 检查userId是否存在
      if (!this.userId) {
        console.error('用户ID未获取，无法获取分类数据');
        return;
      }
      
      getCategoryStatistic({
        userId: this.userId,
        startTime: this.startTime,
        endTime: this.endTime,
        type: this.detailType
      }).then(response => {
        if (response.code === 200) {
          this.categoryDetails = response.data || [];
        }
      });
    },
    getTrendData() {
      // 检查userId是否存在
      if (!this.userId) {
        console.error('用户ID未获取，无法获取趋势数据');
        return;
      }
      
      if (this.trendType === 'year') {
        const year = new Date(this.dateRange[0]).getFullYear();
        getYearStatistic({
          userId: this.userId,
          year: year
        }).then(response => {
          if (response.code === 200) {
            this.yearData = response.data || [];
            this.updateTrendChart();
          }
        });
      } else {
        const months = this.trendType === 'week' ? 1 : 12;
        getTrendStatistic({
          userId: this.userId,
          months: months
        }).then(response => {
          if (response.code === 200) {
            this.trendData = response.data || [];
            this.updateTrendChart();
          }
        });
      }
    },
    initCharts() {
      this.trendChart = echarts.init(this.$refs.trendChart);
      this.pieChart = echarts.init(this.$refs.pieChart);
      
      // 监听窗口大小变化，调整图表大小
      window.addEventListener('resize', () => {
        this.trendChart.resize();
        this.pieChart.resize();
      });
    },
    updateCharts() {
      // 更新饼图
      this.updatePieChart();
    },
    updateTrendChart() {
      let xAxisData = [];
      let incomeData = [];
      let expenseData = [];
      
      // 根据选择的时间范围类型生成数据
      if (this.trendType === 'week') {
        const weekDays = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
        xAxisData = weekDays;
        
        // 处理趋势数据
        if (this.trendData && this.trendData.length > 0) {
          // 将数据按天分组
          const weekData = {
            1: { income: 0, expense: 0 },
            2: { income: 0, expense: 0 },
            3: { income: 0, expense: 0 },
            4: { income: 0, expense: 0 },
            5: { income: 0, expense: 0 },
            6: { income: 0, expense: 0 },
            0: { income: 0, expense: 0 }  // 周日是0
          };
          
          this.trendData.forEach(item => {
            const date = new Date(item.month);
            const dayOfWeek = date.getDay();
            weekData[dayOfWeek].income += item.income || 0;
            weekData[dayOfWeek].expense += item.expense || 0;
          });
          
          // 将数据转换为数组
          [1, 2, 3, 4, 5, 6, 0].forEach(day => {
            incomeData.push(weekData[day].income);
            expenseData.push(weekData[day].expense);
          });
        } else {
          // 空数据
          incomeData = Array(7).fill(0);
          expenseData = Array(7).fill(0);
        }
      } else if (this.trendType === 'month') {
        // 获取当月天数
        const year = new Date(this.dateRange[0]).getFullYear();
        const month = new Date(this.dateRange[0]).getMonth();
        const daysInMonth = new Date(year, month + 1, 0).getDate();
        
        xAxisData = Array.from({length: daysInMonth}, (_, i) => i + 1);
        
        // 处理趋势数据
        if (this.trendData && this.trendData.length > 0) {
          // 初始化每一天的数据
          const dayData = {};
          for (let i = 1; i <= daysInMonth; i++) {
            dayData[i] = { income: 0, expense: 0 };
          }
          
          // 填充实际数据
          this.trendData.forEach(item => {
            const date = new Date(item.month);
            const day = date.getDate();
            if (dayData[day]) {
              dayData[day].income += item.income || 0;
              dayData[day].expense += item.expense || 0;
            }
          });
          
          // 将数据转换为数组
          for (let i = 1; i <= daysInMonth; i++) {
            incomeData.push(dayData[i].income);
            expenseData.push(dayData[i].expense);
          }
        } else {
          // 空数据
          incomeData = Array(daysInMonth).fill(0);
          expenseData = Array(daysInMonth).fill(0);
        }
      } else {
        xAxisData = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'];
        
        // 处理年度数据
        if (this.yearData && this.yearData.length > 0) {
          // 初始化每个月的数据
          const monthData = {};
          for (let i = 1; i <= 12; i++) {
            monthData[i] = { income: 0, expense: 0 };
          }
          
          // 填充实际数据
          this.yearData.forEach(item => {
            if (monthData[item.month]) {
              monthData[item.month].income = item.income || 0;
              monthData[item.month].expense = item.expense || 0;
            }
          });
          
          // 将数据转换为数组
          for (let i = 1; i <= 12; i++) {
            incomeData.push(monthData[i].income);
            expenseData.push(monthData[i].expense);
          }
        } else {
          // 空数据
          incomeData = Array(12).fill(0);
          expenseData = Array(12).fill(0);
        }
      }
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          },
          formatter: function(params) {
            let result = params[0].name + '<br/>';
            params.forEach(item => {
              result += item.marker + ' ' + item.seriesName + ': ¥' + (item.value < 0 ? -item.value : item.value).toFixed(2) + '<br/>';
            });
            return result;
          }
        },
        legend: {
          data: ['收入', '支出']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: xAxisData
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '¥{value}'
          }
        },
        series: [
          {
            name: '收入',
            type: 'bar',
            stack: 'Total',
            itemStyle: {
              color: '#67C23A'
            },
            emphasis: {
              focus: 'series'
            },
            data: incomeData
          },
          {
            name: '支出',
            type: 'bar',
            stack: 'Total',
            itemStyle: {
              color: '#F56C6C'
            },
            emphasis: {
              focus: 'series'
            },
            data: expenseData.map(v => -v)
          }
        ]
      };
      
      this.trendChart.setOption(option);
    },
    updatePieChart() {
      const pieData = this.categoryDetails.map(item => {
        return {
          value: item.amount,
          name: item.categoryName,
          itemStyle: {
            color: this.getRandomColor(item.categoryId)
          }
        };
      });
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{b}: ¥{c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'center',
          formatter: function(name) {
            const item = pieData.find(data => data.name === name);
            if (item) {
              return `${name}: ¥${item.value.toFixed(2)}`;
            }
            return name;
          }
        },
        series: [
          {
            name: this.pieChartType === '1' ? '支出分布' : '收入分布',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: pieData
          }
        ]
      };
      
      this.pieChart.setOption(option);
    },
    getBudgetColor(percentage) {
      if (percentage < 70) return '#67C23A';
      if (percentage < 90) return '#E6A23C';
      return '#F56C6C';
    },
    getRandomColor(id) {
      return this.colors[id % this.colors.length] || '#909399';
    }
  },
  watch: {
    trendType() {
      this.getTrendData();
    },
    pieChartType() {
      this.getCategoryData();
    },
    detailType() {
      this.getCategoryData();
    }
  }
};
</script>

<style scoped>
.summary-cards {
  margin-bottom: 20px;
}
.summary-card {
  text-align: center;
  height: 120px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.summary-card.income {
  background-color: #f0f9eb;
}
.summary-card.expense {
  background-color: #fef0f0;
}
.summary-card.balance {
  background-color: #f0f2f5;
}
.summary-title {
  font-size: 16px;
  color: #606266;
  margin-bottom: 10px;
}
.summary-amount {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}
.income .summary-amount {
  color: #67C23A;
}
.expense .summary-amount {
  color: #F56C6C;
}
.balance .summary-amount {
  color: #409EFF;
}
.summary-desc {
  font-size: 12px;
  color: #909399;
}
.chart-card {
  height: 350px;
}
.budget-item {
  margin-bottom: 15px;
}
.budget-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}
.budget-category {
  font-weight: bold;
}
.budget-amount {
  color: #606266;
  font-size: 14px;
}
.no-data {
  text-align: center;
  padding: 30px 0;
  color: #909399;
}
.no-data i {
  font-size: 48px;
  margin-bottom: 10px;
}
</style>
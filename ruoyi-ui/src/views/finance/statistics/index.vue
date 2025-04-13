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
      </div>
      <div class="summary-cards">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-card shadow="hover" class="summary-card income">
              <div class="summary-title">收入</div>
              <div class="summary-amount">¥{{ formatAmount(totalIncome) }}</div>
              <div class="summary-desc">{{ dateRangeDesc }}总收入</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="summary-card expense">
              <div class="summary-title">支出</div>
              <div class="summary-amount">¥{{ formatAmount(totalExpense) }}</div>
              <div class="summary-desc">{{ dateRangeDesc }}总支出</div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover" class="summary-card balance">
              <div class="summary-title">结余</div>
              <div class="summary-amount">¥{{ formatAmount(totalIncome - totalExpense) }}</div>
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
              <el-radio-button label="expense">支出</el-radio-button>
              <el-radio-button label="income">收入</el-radio-button>
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
        <el-table-column prop="name" label="分类名称"></el-table-column>
        <el-table-column prop="amount" label="金额">
          <template slot-scope="scope">
            ¥{{ formatAmount(scope.row.amount) }}
          </template>
        </el-table-column>
        <el-table-column prop="percentage" label="占比">
          <template slot-scope="scope">
            <el-progress 
              :percentage="scope.row.percentage" 
              :color="scope.row.color || '#909399'"
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
              <span>已用: ¥{{ formatAmount(item.used) }}</span>
              <span> / </span>
              <span>预算: ¥{{ formatAmount(item.amount) }}</span>
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
      pieChartType: 'expense',
      detailType: '1',
      totalIncome: 15000,
      totalExpense: 8500,
      categoryDetails: [
        { name: '餐饮', amount: 2500, percentage: 29.4, count: 25, color: '#FF5722' },
        { name: '购物', amount: 1800, percentage: 21.2, count: 15, color: '#4CAF50' },
        { name: '交通', amount: 1200, percentage: 14.1, count: 30, color: '#2196F3' },
        { name: '住房', amount: 2000, percentage: 23.5, count: 2, color: '#9C27B0' },
        { name: '娱乐', amount: 1000, percentage: 11.8, count: 10, color: '#FFC107' }
      ],
      budgetData: [
        { categoryName: '总预算', amount: 10000, used: 8500, percentage: 85 },
        { categoryName: '餐饮', amount: 3000, used: 2500, percentage: 83.3 },
        { categoryName: '购物', amount: 2000, used: 1800, percentage: 90 },
        { categoryName: '交通', amount: 1500, used: 1200, percentage: 80 },
        { categoryName: '住房', amount: 2500, used: 2000, percentage: 80 },
        { categoryName: '娱乐', amount: 1000, used: 1000, percentage: 100 }
      ],
      trendChart: null,
      pieChart: null
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
    }
  },
  mounted() {
    this.initCharts();
    this.getStatisticsData();
  },
  methods: {
    formatAmount(amount) {
      return amount.toFixed(2).replace(/\d(?=(\d{3})+\.)/g, '$&,');
    },
    handleDateChange() {
      this.getStatisticsData();
    },
    getStatisticsData() {
      this.loading = true;
      // 模拟请求数据
      setTimeout(() => {
        // 实际项目中应该调用API获取数据
        this.loading = false;
        this.updateCharts();
      }, 500);
    },
    initCharts() {
      this.trendChart = echarts.init(this.$refs.trendChart);
      this.pieChart = echarts.init(this.$refs.pieChart);
      this.updateCharts();
      
      // 监听窗口大小变化，调整图表大小
      window.addEventListener('resize', () => {
        this.trendChart.resize();
        this.pieChart.resize();
      });
    },
    updateCharts() {
      // 更新趋势图
      this.updateTrendChart();
      // 更新饼图
      this.updatePieChart();
    },
    updateTrendChart() {
      let xAxisData = [];
      let incomeData = [];
      let expenseData = [];
      
      // 根据选择的时间范围类型生成数据
      if (this.trendType === 'week') {
        xAxisData = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
        incomeData = [1200, 300, 450, 800, 900, 1000, 350];
        expenseData = [800, 550, 600, 450, 700, 950, 500];
      } else if (this.trendType === 'month') {
        xAxisData = Array.from({length: 30}, (_, i) => i + 1);
        incomeData = Array.from({length: 30}, () => Math.floor(Math.random() * 500 + 200));
        expenseData = Array.from({length: 30}, () => Math.floor(Math.random() * 300 + 100));
      } else {
        xAxisData = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'];
        incomeData = [4500, 5000, 4800, 5200, 5500, 6000, 5800, 6200, 6000, 6500, 7000, 7500];
        expenseData = [3500, 3800, 3600, 4000, 4200, 4500, 4300, 4800, 4600, 5000, 5200, 5500];
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
              result += item.marker + ' ' + item.seriesName + ': ¥' + item.value.toFixed(2) + '<br/>';
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
      const pieData = this.pieChartType === 'expense' ? 
        [
          { value: 2500, name: '餐饮', itemStyle: { color: '#FF5722' } },
          { value: 1800, name: '购物', itemStyle: { color: '#4CAF50' } },
          { value: 1200, name: '交通', itemStyle: { color: '#2196F3' } },
          { value: 2000, name: '住房', itemStyle: { color: '#9C27B0' } },
          { value: 1000, name: '娱乐', itemStyle: { color: '#FFC107' } }
        ] : 
        [
          { value: 12000, name: '工资', itemStyle: { color: '#3F51B5' } },
          { value: 2000, name: '奖金', itemStyle: { color: '#E91E63' } },
          { value: 1000, name: '理财', itemStyle: { color: '#009688' } }
        ];
      
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
            name: this.pieChartType === 'expense' ? '支出分布' : '收入分布',
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
    }
  },
  watch: {
    trendType() {
      this.updateTrendChart();
    },
    pieChartType() {
      this.updatePieChart();
    },
    detailType() {
      // 切换支出/收入类型时更新数据
      if (this.detailType === '2') {
        this.categoryDetails = [
          { name: '工资', amount: 12000, percentage: 80, count: 1, color: '#3F51B5' },
          { name: '奖金', amount: 2000, percentage: 13.3, count: 1, color: '#E91E63' },
          { name: '理财', amount: 1000, percentage: 6.7, count: 2, color: '#009688' }
        ];
      } else {
        this.categoryDetails = [
          { name: '餐饮', amount: 2500, percentage: 29.4, count: 25, color: '#FF5722' },
          { name: '购物', amount: 1800, percentage: 21.2, count: 15, color: '#4CAF50' },
          { name: '交通', amount: 1200, percentage: 14.1, count: 30, color: '#2196F3' },
          { name: '住房', amount: 2000, percentage: 23.5, count: 2, color: '#9C27B0' },
          { name: '娱乐', amount: 1000, percentage: 11.8, count: 10, color: '#FFC107' }
        ];
      }
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
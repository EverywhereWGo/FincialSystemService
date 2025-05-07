<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户ID" prop="userId" v-if="isAdmin">
        <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable style="width: 240px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
        <el-select v-model="queryParams.categoryId" placeholder="请选择分类" clearable style="width: 240px">
          <el-option v-for="item in categoryOptions" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="年份" prop="year">
        <el-date-picker
          v-model="queryParams.year"
          type="year"
          placeholder="选择年份"
          style="width: 240px"
          value-format="yyyy"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="月份" prop="month">
        <el-select v-model="queryParams.month" placeholder="请选择月份" clearable style="width: 240px">
          <el-option v-for="i in 12" :key="i" :label="i + '月'" :value="i" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['finance:budget:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['finance:budget:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['finance:budget:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['finance:budget:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="budgetList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="预算ID" align="center" prop="id" v-if="columns[0].visible" />
      <el-table-column label="用户ID" align="center" prop="userId" v-if="columns[1].visible && isAdmin" />
      <el-table-column label="分类" align="center" prop="categoryName" v-if="columns[2].visible" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span v-if="scope.row.categoryId">{{ scope.row.categoryName }}</span>
          <el-tag v-else>总预算</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="年月" align="center" v-if="columns[3].visible">
        <template slot-scope="scope">
          {{ scope.row.year }}年{{ scope.row.month }}月
        </template>
      </el-table-column>
      <el-table-column label="预算金额" align="center" prop="amount" v-if="columns[4].visible" />
      <el-table-column label="已使用" align="center" v-if="columns[5].visible">
        <template slot-scope="scope">
          <el-progress 
            :percentage="(scope.row.usedAmount / scope.row.amount * 100).toFixed(2)" 
            :color="getBudgetProgressColor(scope.row)"
            :format="percentageFormat"
          ></el-progress>
          {{ scope.row.usedAmount }} / {{ scope.row.amount }}
        </template>
      </el-table-column>
      <el-table-column label="预警阈值" align="center" prop="warningThreshold" v-if="columns[6].visible">
        <template slot-scope="scope">
          {{ scope.row.warningThreshold }}%
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" v-if="columns[7].visible">
        <template slot-scope="scope">
          <el-tag type="success" v-if="scope.row.usedAmount / scope.row.amount * 100 < scope.row.warningThreshold">正常</el-tag>
          <el-tag type="warning" v-else-if="scope.row.usedAmount / scope.row.amount * 100 < 100">临近预算</el-tag>
          <el-tag type="danger" v-else>已超预算</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['finance:budget:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['finance:budget:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改预算对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option label="总预算" :value="null" />
            <el-option
              v-for="item in categoryOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
              <span><i :class="'el-icon-' + item.icon" :style="{color: item.color}"></i> {{ item.name }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="年份" prop="year">
          <el-date-picker
            v-model="form.year"
            type="year"
            placeholder="选择年份"
            style="width: 100%"
            value-format="yyyy"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="月份" prop="month">
          <el-select v-model="form.month" placeholder="请选择月份" style="width: 100%">
            <el-option v-for="i in 12" :key="i" :label="i + '月'" :value="i" />
          </el-select>
        </el-form-item>
        <el-form-item label="预算金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0.01" :precision="2" :step="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预警阈值" prop="warningThreshold">
          <el-slider v-model="form.warningThreshold" :min="1" :max="100" :step="1" :format-tooltip="formatTooltip"></el-slider>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listBudget, getBudget, delBudget, addBudget, updateBudget, exportBudget } from "@/api/finance/budget";
import { listCategoryByType } from "@/api/finance/category";

export default {
  name: "Budget",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 预算表格数据
      budgetList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否为管理员
      isAdmin: false,
      // 分类选项
      categoryOptions: [],
      // 日期范围
      dateRange: [],
      // 表格展示的列
      columns: [
        { key: 0, label: "预算ID", visible: false },
        { key: 1, label: "用户ID", visible: true },
        { key: 2, label: "分类", visible: true },
        { key: 3, label: "年月", visible: true },
        { key: 4, label: "预算金额", visible: true },
        { key: 5, label: "使用情况", visible: true },
        { key: 6, label: "预警阈值", visible: true },
        { key: 7, label: "状态", visible: true }
      ],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: undefined,
        categoryId: undefined,
        year: undefined,
        month: undefined
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        year: [
          { required: true, message: "年份不能为空", trigger: "blur" }
        ],
        month: [
          { required: true, message: "月份不能为空", trigger: "change" }
        ],
        amount: [
          { required: true, message: "预算金额不能为空", trigger: "blur" }
        ],
        warningThreshold: [
          { required: true, message: "预警阈值不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getCategoryOptions();
    this.isAdmin = this.$store.getters.roles.includes("admin");
    
    // 不默认设置当前年月，保持默认不筛选
  },
  methods: {
    /** 查询预算列表 */
    getList() {
      this.loading = true;
      
      // 如果用户不是管理员，自动填充用户ID查询条件
      if (!this.isAdmin && !this.queryParams.userId) {
        this.queryParams.userId = this.$store.getters.userId;
      }
      
      listBudget(this.queryParams).then(response => {
        this.budgetList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 获取分类选项 */
    getCategoryOptions() {
      // 获取支出分类
      listCategoryByType(1).then(response => {
        this.categoryOptions = response.data || [];
      });
    },
    // 预警进度条颜色
    getBudgetProgressColor(row) {
      const percentage = row.usedAmount / row.amount * 100;
      if (percentage >= 100) {
        return '#F56C6C'; // 红色，超预算
      } else if (percentage >= row.warningThreshold) {
        return '#E6A23C'; // 黄色，接近预算
      } else {
        return '#67C23A'; // 绿色，正常
      }
    },
    // 进度条格式化
    percentageFormat(percentage) {
      return percentage + '%';
    },
    // 预警阈值滑块提示
    formatTooltip(val) {
      return val + '%';
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: undefined,
        userId: this.$store.getters.userId,
        categoryId: null,
        year: new Date().getFullYear().toString(),
        month: new Date().getMonth() + 1,
        amount: 0,
        warningThreshold: 80,
        warned: false,
        remark: undefined
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      // 不设置默认年月，保持不筛选
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加预算";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids[0];
      getBudget(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改预算";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != undefined) {
            updateBudget(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              // 清除查询条件
              this.queryParams.year = undefined;
              this.queryParams.month = undefined;
              this.getList();
            });
          } else {
            addBudget(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              // 清除查询条件
              this.queryParams.year = undefined;
              this.queryParams.month = undefined;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除预算编号为"' + ids + '"的数据项？').then(function() {
        return delBudget(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('finance/budget/export', {
        ...this.queryParams
      }, `budget_${new Date().getTime()}.xlsx`);
    }
  }
};
</script>

<style scoped>
.el-progress {
  margin-bottom: 5px;
}
</style>
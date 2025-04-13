<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择类型" clearable>
          <el-option
            v-for="dict in dict.type.notification_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="是否已读" prop="read">
        <el-select v-model="queryParams.read" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.sys_yes_no"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['finance:notification:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['finance:notification:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['finance:notification:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['finance:notification:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-check"
          size="mini"
          @click="handleMarkAllAsRead"
          v-hasPermi="['finance:notification:read']"
        >全部标为已读</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="notificationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="通知编号" align="center" prop="id" />
      <el-table-column label="标题" align="center" prop="title" :show-overflow-tooltip="true" />
      <el-table-column label="内容" align="center" prop="content" :show-overflow-tooltip="true" />
      <el-table-column label="类型" align="center" prop="type">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.notification_type" :value="scope.row.type"/>
        </template>
      </el-table-column>
      <el-table-column label="是否已读" align="center" prop="read">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_yes_no" :value="scope.row.read ? 'Y' : 'N'"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['finance:notification:query']"
          >查看</el-button>
          <el-button
            v-if="!scope.row.read"
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleMarkAsRead(scope.row)"
            v-hasPermi="['finance:notification:read']"
          >标为已读</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['finance:notification:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['finance:notification:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改通知对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option
              v-for="dict in dict.type.notification_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否已读" prop="read">
          <el-radio-group v-model="form.read">
            <el-radio
              v-for="dict in dict.type.sys_yes_no"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
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
    
    <!-- 查看通知对话框 -->
    <el-dialog title="通知详情" :visible.sync="viewOpen" width="500px" append-to-body>
      <div class="notification-view">
        <h2 class="notification-title">{{ viewForm.title }}</h2>
        <div class="notification-info">
          <span>类型：{{ getNotificationType(viewForm.type) }}</span>
          <span style="margin-left: 20px;">时间：{{ parseTime(viewForm.createTime) }}</span>
        </div>
        <div class="notification-content">
          {{ viewForm.content }}
        </div>
        <div class="notification-remark" v-if="viewForm.remark">
          <p>备注：{{ viewForm.remark }}</p>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listNotification, getNotification, addNotification, updateNotification, delNotification, markAsRead, markAllAsRead, exportNotification } from "@/api/finance/notification";

export default {
  name: "Notification",
  dicts: ['sys_yes_no', 'notification_type'],
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
      // 通知表格数据
      notificationList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示查看弹出层
      viewOpen: false,
      // 查看表单
      viewForm: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        type: null,
        read: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "用户ID不能为空", trigger: "blur" }
        ],
        title: [
          { required: true, message: "标题不能为空", trigger: "blur" }
        ],
        content: [
          { required: true, message: "内容不能为空", trigger: "blur" }
        ],
        type: [
          { required: true, message: "类型不能为空", trigger: "change" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询通知列表 */
    getList() {
      this.loading = true;
      listNotification(this.queryParams).then(response => {
        this.notificationList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        userId: null,
        title: null,
        content: null,
        type: null,
        read: 'N',
        remark: null
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
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加通知";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids[0]
      getNotification(id).then(response => {
        this.form = response.data;
        this.form.read = response.data.read ? 'Y' : 'N';
        this.open = true;
        this.title = "修改通知";
      });
    },
    /** 查看按钮操作 */
    handleView(row) {
      getNotification(row.id).then(response => {
        this.viewForm = response.data;
        this.viewOpen = true;
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 将read从字符串转换为布尔值
          const formData = { ...this.form };
          formData.read = formData.read === 'Y';
          
          if (this.form.id != null) {
            updateNotification(formData).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addNotification(formData).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除通知编号为"' + ids + '"的数据项？').then(function() {
        return delNotification(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 标记为已读 */
    handleMarkAsRead(row) {
      markAsRead(row.id).then(response => {
        this.$modal.msgSuccess("已标记为已读");
        this.getList();
      });
    },
    /** 标记所有为已读 */
    handleMarkAllAsRead() {
      this.$modal.confirm('是否确认将所有通知标记为已读？').then(() => {
        return markAllAsRead();
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("所有通知已标记为已读");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$modal.confirm('是否确认导出所有通知数据项？').then(() => {
        this.exportLoading = true;
        return exportNotification(this.queryParams);
      }).then(response => {
        this.download(response.msg);
        this.exportLoading = false;
      }).catch(() => {});
    },
    /** 获取通知类型名称 */
    getNotificationType(type) {
      const typeDict = this.dict.type.notification_type;
      if (typeDict) {
        const found = typeDict.find(item => item.value === type);
        return found ? found.label : type;
      }
      return type;
    }
  }
};
</script>

<style scoped>
.notification-view {
  padding: 0 20px;
}
.notification-title {
  text-align: center;
  margin-bottom: 20px;
}
.notification-info {
  color: #909399;
  font-size: 14px;
  margin-bottom: 15px;
}
.notification-content {
  padding: 15px;
  background-color: #f8f8f8;
  border-radius: 4px;
  margin-bottom: 15px;
  min-height: 100px;
  line-height: 1.6;
}
.notification-remark {
  font-style: italic;
  color: #606266;
}
</style>
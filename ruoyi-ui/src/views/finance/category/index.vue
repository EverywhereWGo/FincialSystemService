<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="分类名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入分类名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="请选择类型" clearable>
          <el-option label="支出" :value="1" />
          <el-option label="收入" :value="2" />
          <el-option label="收入(旧)" :value="0" />
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
          v-hasPermi="['finance:category:add']"
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
          v-hasPermi="['finance:category:edit']"
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
          v-hasPermi="['finance:category:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['finance:category:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="categoryList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="分类ID" align="center" prop="id" />
      <el-table-column label="分类名称" align="center" prop="name" />
      <el-table-column label="类型" align="center" prop="type">
        <template slot-scope="scope">
          <el-tag type="danger" v-if="scope.row.type === 1">支出</el-tag>
          <el-tag type="success" v-else-if="scope.row.type === 2">收入</el-tag>
          <span v-else>{{ scope.row.type }}</span>
        </template>
      </el-table-column>
      <el-table-column label="图标" align="center" prop="icon">
        <template slot-scope="scope">
          <img 
            v-if="isImageUrl(scope.row.icon)" 
            :src="getImageUrl(scope.row.icon)" 
            alt="图标" 
            style="width: 30px; height: 30px; object-fit: contain;"
          />
          <i v-else :class="'el-icon-' + scope.row.icon" v-if="scope.row.icon"></i>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="颜色" align="center" prop="color">
        <template slot-scope="scope">
          <div v-if="scope.row.color" class="color-block" :style="{backgroundColor: scope.row.color}"></div>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="用户ID" align="center" prop="userId">
        <template slot-scope="scope">
          <span v-if="scope.row.userId">{{ scope.row.userId }}</span>
          <el-tag type="success" v-else>系统预设</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="显示顺序" align="center" prop="displayOrder" />
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
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['finance:category:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['finance:category:remove']"
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

    <!-- 添加或修改分类对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio
              v-for="dict in dict.type.fin_category_type"
              :key="dict.value"
              :label="parseInt(dict.value)"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-upload 
            name="file"
            ref="upload"
            :limit="1"
            accept=".jpg, .png, .jpeg"
            :action="upload.url + (form.id ? '?categoryId=' + form.id : '')"
            :headers="upload.headers"
            :file-list="upload.fileList"
            :on-progress="handleFileUploadProgress"
            :on-success="handleFileSuccess"
            :on-error="handleUploadError"
            :on-exceed="handleExceed"
            :on-remove="handleRemove"
            :on-preview="handlePictureCardPreview"
            :before-upload="handleBeforeUpload"
            list-type="picture-card"
            :class="{hide: upload.fileList.length >= 1}">
            <i class="el-icon-plus"></i>
          </el-upload>
          <!-- 上传提示 -->
          <div class="el-upload__tip" slot="tip">只能上传jpg/png/jpeg文件，且不超过2MB</div>
        
          <el-dialog
            :visible.sync="dialogVisible"
            title="预览"
            width="800"
            append-to-body
          >
            <img
              :src="dialogImageUrl"
              style="display: block; max-width: 100%; margin: 0 auto"
            />
          </el-dialog>
        </el-form-item>
        <el-form-item label="颜色" prop="color">
          <el-color-picker v-model="form.color"></el-color-picker>
          <div class="el-form-item__tips">用于显示分类标识的颜色</div>
        </el-form-item>
        <el-form-item label="显示顺序" prop="displayOrder">
          <el-input-number v-model="form.displayOrder" :min="0" />
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
import { listCategory, getCategory, delCategory, addCategory, updateCategory, uploadCategoryIcon } from "@/api/finance/category";
import { getToken } from "@/utils/auth";

export default {
  name: "Category",
  dicts: ['fin_category_type'],
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
      // 分类表格数据
      categoryList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        type: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: "分类名称不能为空", trigger: "blur" }
        ],
        type: [
          { required: true, message: "类型不能为空", trigger: "change" }
        ],
        displayOrder: [
          { required: true, message: "显示顺序不能为空", trigger: "blur" }
        ]
      },
      upload: {
        // 是否禁用上传
        isUploading: false,
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/finance/category/icon",
        // 上传的文件列表
        fileList: []
      },
      dialogVisible: false,
      dialogImageUrl: ''
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询分类列表 */
    getList() {
      this.loading = true;
      listCategory(this.queryParams).then(response => {
        this.categoryList = response.rows;
        // 调试输出
        console.log("分类数据: ", this.categoryList);
        console.log("字典数据: ", this.dict.type.fin_category_type);
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
        name: null,
        type: 1,
        icon: null,
        color: null,
        displayOrder: 0,
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
      this.upload.fileList = [];
      this.open = true;
      this.title = "添加分类";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids[0]
      getCategory(id).then(response => {
        this.form = response.data;
        this.form.type = parseInt(this.form.type);
        // 设置上传文件列表
        if (this.form.icon && this.isImageUrl(this.form.icon)) {
          this.upload.fileList = [{ name: this.getFileName(this.form.icon), url: this.getImageUrl(this.form.icon) }];
        } else {
          this.upload.fileList = [];
        }
        this.open = true;
        this.title = "修改分类";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.upload.isUploading) {
            this.$modal.msgWarning("图标正在上传中，请等待上传完成后再提交");
            return;
          }
          
          if (this.form.id != null) {
            updateCategory(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCategory(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除分类编号为"' + ids + '"的数据项？').then(function() {
        return delCategory(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$modal.confirm('是否确认导出所有分类数据项？').then(() => {
        this.exportLoading = true;
        return exportCategory(this.queryParams);
      }).then(response => {
        this.download(response.msg);
        this.exportLoading = false;
      }).catch(() => {});
    },
    isImageUrl(icon) {
      if (!icon) return false;
      return icon.startsWith('/profile') || icon.startsWith('http');
    },
    getImageUrl(path) {
      if (!path) return '';
      if (path.startsWith('http')) return path;
      return process.env.VUE_APP_BASE_API + path;
    },
    getFileName(path) {
      if (!path) return '';
      return path.substring(path.lastIndexOf("/") + 1);
    },
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
    handleFileSuccess(response, file, fileList) {
      this.upload.isUploading = false;
      if (response.code === 200) {
        this.form.icon = response.iconUrl || response.data.iconUrl;
        this.$modal.msgSuccess(response.msg || "上传成功");
      } else {
        this.$modal.msgError(response.msg || "上传失败");
      }
    },
    handleUploadError(error, file) {
      this.upload.isUploading = false;
      this.$modal.msgError("上传失败，请检查网络连接或文件格式");
    },
    handleExceed(files, fileList) {
      this.$modal.msgWarning("只能上传一个文件，请先删除已上传的文件");
    },
    handleRemove(file, fileList) {
      this.upload.fileList = fileList;
      this.form.icon = '';
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    handleBeforeUpload(file) {
      const isJPG = file.type === 'image/jpeg';
      const isPNG = file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG && !isPNG) {
        this.$modal.msgWarning('上传图标只能是 JPG/PNG 格式!');
      }
      if (!isLt2M) {
        this.$modal.msgWarning('上传图标大小不能超过 2MB!');
      }
      return (isJPG || isPNG) && isLt2M;
    }
  }
};
</script>

<style scoped>
.color-block {
  width: 20px;
  height: 20px;
  border-radius: 4px;
  margin: 0 auto;
}

/* 上传组件样式 */
::v-deep .hide .el-upload--picture-card {
  display: none;
}
</style>
<template>
  <el-dialog :title="!dataForm.id ? 'Add' : 'Edit'" :close-on-click-modal="false" :visible.sync="visible"
    @closed="dialogClose">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" label-width="140px">
      <!--       @keyup.enter.native="dataFormSubmit()" -->
      <el-form-item label="Attribute Name" prop="attrName">
        <el-input v-model="dataForm.attrName" placeholder="Attribute Name"></el-input>
      </el-form-item>
      <el-form-item label="Attribute Type" prop="attrType">
        <el-select v-model="dataForm.attrType" placeholder="请选择">
          <el-option label="Specs" :value="1"></el-option>
          <el-option label="Sales attributes" :value="0"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="Value Type" prop="valueType">
        <el-switch v-model="dataForm.valueType" active-text="Multiple" inactive-text="Single" active-color="#13ce66"
          inactive-color="#ff4949" :inactive-value="0" :active-value="1"></el-switch>
      </el-form-item>
      <el-form-item label="Value" prop="valueSelect">
        <!-- <el-input v-model="dataForm.valueSelect"></el-input> -->
        <el-select v-model="dataForm.valueSelect" multiple filterable allow-create placeholder="Input value">
        </el-select>
      </el-form-item>
      <el-form-item label="Attribute Icon" prop="icon">
        <el-input v-model="dataForm.icon" placeholder="Attribute Icon"></el-input>
      </el-form-item>
      <el-form-item label="Category" prop="catelogId">
        <category-cascader :catelogPath.sync="catelogPath"></category-cascader>
      </el-form-item>
      <el-form-item label="Group" prop="attrGroupId" v-if="type == 1">
        <el-select ref="groupSelect" v-model="dataForm.attrGroupId" placeholder="Select">
          <el-option v-for="item in attrGroups" :key="item.attrGroupId" :label="item.attrGroupName"
            :value="item.attrGroupId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Retrievable" prop="searchType" v-if="type == 1">
        <el-switch v-model="dataForm.searchType" active-color="#13ce66" inactive-color="#ff4949" :active-value="1"
          :inactive-value="0"></el-switch>
      </el-form-item>
      <el-form-item label="Show in summary" prop="showDesc" v-if="type == 1">
        <el-switch v-model="dataForm.showDesc" active-color="#13ce66" inactive-color="#ff4949" :active-value="1"
          :inactive-value="0"></el-switch>
      </el-form-item>
      <el-form-item label="Enable" prop="enable">
        <el-switch v-model="dataForm.enable" active-color="#13ce66" inactive-color="#ff4949" :active-value="1"
          :inactive-value="0"></el-switch>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">Cancel</el-button>
      <el-button type="primary" @click="dataFormSubmit()">Confirm</el-button>
    </span>
  </el-dialog>
</template>

<script>
import CategoryCascader from "../common/category-cascader";
export default {
  data() {
    return {
      visible: false,
      dataForm: {
        attrId: 0,
        attrName: "",
        searchType: 0,
        valueType: 0,
        icon: "",
        valueSelect: "",
        attrType: 1,
        enable: 1,
        catelogId: "",
        attrGroupId: "",
        showDesc: 0
      },
      catelogPath: [],
      attrGroups: [],
      dataRule: {
        attrName: [
          { required: true, message: "Attribute Name不能为空", trigger: "blur" }
        ],
        searchType: [
          {
            required: true,
            message: "是否需要检索不能为空",
            trigger: "blur"
          }
        ],
        valueType: [
          {
            required: true,
            message: "值类型不能为空",
            trigger: "blur"
          }
        ],
        icon: [
          { required: true, message: "Attribute Icon不能为空", trigger: "blur" }
        ],
        attrType: [
          {
            required: true,
            message: "属性类型不能为空",
            trigger: "blur"
          }
        ],
        enable: [
          {
            required: true,
            message: "启用状态不能为空",
            trigger: "blur"
          }
        ],
        catelogId: [
          {
            required: true,
            message: "需要选择正确的三级Category数据",
            trigger: "blur"
          }
        ],
        showDesc: [
          {
            required: true,
            message: "快速展示不能为空",
            trigger: "blur"
          }
        ]
      }
    };
  },
  props: {
    type: {
      type: Number,
      default: 1
    }
  },
  watch: {
    catelogPath(path) {
      //监听到路径变化需要查出这个三级Category的分组信息
      console.log("路径变了", path);
      this.attrGroups = [];
      this.dataForm.attrGroupId = "";
      this.dataForm.catelogId = path[path.length - 1];
      if (path && path.length == 3) {
        this.$http({
          url: this.$http.adornUrl(
            `/product/attrgroup/list/${path[path.length - 1]}`
          ),
          method: "get",
          params: this.$http.adornParams({ page: 1, limit: 10000000 })
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.attrGroups = data.page.list;
          } else {
            this.$message.error(data.msg);
          }
        });
      } else if (path.length == 0) {
        this.dataForm.catelogId = "";
      } else {
        this.$message.error("请选择正确的Category");
        this.dataForm.catelogId = "";
      }
    }
  },
  components: { CategoryCascader },
  methods: {
    init(id) {
      this.dataForm.attrId = id || 0;
      this.dataForm.attrType = this.type;
      this.visible = true;
      this.$nextTick(() => {
        this.$refs["dataForm"].resetFields();
        if (this.dataForm.attrId) {
          this.$http({
            url: this.$http.adornUrl(
              `/product/attr/info/${this.dataForm.attrId}`
            ),
            method: "get",
            params: this.$http.adornParams()
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.dataForm.attrName = data.attr.attrName;
              this.dataForm.searchType = data.attr.searchType;
              this.dataForm.valueType = data.attr.valueType;
              this.dataForm.icon = data.attr.icon;
              this.dataForm.valueSelect = data.attr.valueSelect.split(";");
              this.dataForm.attrType = data.attr.attrType;
              this.dataForm.enable = data.attr.enable;
              this.dataForm.catelogId = data.attr.catelogId;
              this.dataForm.showDesc = data.attr.showDesc;
              //attrGroupId
              //catelogPath
              this.catelogPath = data.attr.catelogPath;
              this.$nextTick(() => {
                this.dataForm.attrGroupId = data.attr.attrGroupId;
              });
            }
          });
        }
      });
    },
    // 表单提交
    dataFormSubmit() {
      this.$refs["dataForm"].validate(valid => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(
              `/product/attr/${!this.dataForm.attrId ? "save" : "update"}`
            ),
            method: "post",
            data: this.$http.adornData({
              attrId: this.dataForm.attrId || undefined,
              attrName: this.dataForm.attrName,
              searchType: this.dataForm.searchType,
              valueType: this.dataForm.valueType,
              icon: this.dataForm.icon,
              valueSelect: this.dataForm.valueSelect.join(";"),
              attrType: this.dataForm.attrType,
              enable: this.dataForm.enable,
              catelogId: this.dataForm.catelogId,
              attrGroupId: this.dataForm.attrGroupId,
              showDesc: this.dataForm.showDesc
            })
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: "Operation Successful",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.visible = false;
                  this.$emit("refreshDataList");
                }
              });
            } else {
              this.$message.error(data.msg);
            }
          });
        }
      });
    },
    //dialogClose
    dialogClose() {
      this.catelogPath = [];
    }
  }
};
</script>

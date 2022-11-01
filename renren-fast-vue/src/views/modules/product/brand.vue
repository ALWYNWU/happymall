<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.key" placeholder="Parameter" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">Search</el-button>
        <el-button
          v-if="isAuth('product:brand:save')"
          type="primary"
          @click="addOrUpdateHandle()"
        >Add</el-button>
        <el-button
          v-if="isAuth('product:brand:delete')"
          type="danger"
          @click="deleteHandle()"
          :disabled="dataListSelections.length <= 0"
        >Delete</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="dataList"
      border
      v-loading="dataListLoading"
      @selection-change="selectionChangeHandle"
      style="width: 100%;"
    >
      <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
      <el-table-column prop="brandId" header-align="center" align="center" label="BrandId"></el-table-column>
      <el-table-column prop="name" header-align="center" align="center" label="Brand name"></el-table-column>
      <el-table-column prop="logo" header-align="center" align="center" label="Brand logo url">
        <template slot-scope="scope">
          <!-- <el-image
              style="width: 100px; height: 80px"
              :src="scope.row.logo"
          fit="fill"></el-image>-->
          <img :src="scope.row.logo" style="width: 70px; height: 70px" />
        </template>
      </el-table-column>
      <el-table-column prop="descript" header-align="center" align="center" label="Description"></el-table-column>
      <el-table-column prop="showStatus" header-align="center" align="center" label="Show status">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.showStatus"
            active-color="#13ce66"
            inactive-color="#ff4949"
            :active-value="1"
            :inactive-value="0"
            @change="updateBrandStatus(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column prop="firstLetter" header-align="center" align="center" label="First letter"></el-table-column>
      <el-table-column prop="sort" header-align="center" align="center" label="Sort"></el-table-column>
      <el-table-column fixed="right" header-align="center" align="center" width="250" label="Operation">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="updateCatelogHandle(scope.row.brandId)">Related to categories</el-button>
          <el-button type="text" size="small" @click="addOrUpdateHandle(scope.row.brandId)">Edit</el-button>
          <el-button type="text" size="small" @click="deleteHandle(scope.row.brandId)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      @size-change="sizeChangeHandle"
      @current-change="currentChangeHandle"
      :current-page="pageIndex"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pageSize"
      :total="totalPage"
      layout="total, sizes, prev, pager, next, jumper"
    ></el-pagination>
    <!-- 弹窗, Add / Edit -->
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>

    <el-dialog title="Related to categories" :visible.sync="cateRelationDialogVisible" width="30%">
      <el-popover placement="right-end" v-model="popCatelogSelectVisible">
        <category-cascader :catelogPath.sync="catelogPath"></category-cascader>
        <div style="text-align: right; margin: 0">
          <el-button size="mini" type="text" @click="popCatelogSelectVisible = false">Cancel</el-button>
          <el-button type="primary" size="mini" @click="addCatelogSelect">Confirm</el-button>
        </div>
        <el-button slot="reference">Add relation</el-button>
      </el-popover>
      <el-table :data="cateRelationTableData" style="width: 100%">
        <el-table-column prop="id" label="#"></el-table-column>
        <el-table-column prop="brandName" label="Brand name"></el-table-column>
        <el-table-column prop="catelogName" label="Category name"></el-table-column>
        <el-table-column fixed="right" header-align="center" align="center" label="Option">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="deleteCateRelationHandle(scope.row.id,scope.row.brandId)"
            >Remove</el-button>
          </template>
        </el-table-column>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cateRelationDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="cateRelationDialogVisible = false">Confirm</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import AddOrUpdate from "./brand-add-or-update";
import CategoryCascader from "../common/category-cascader";
export default {
  data() {
    return {
      dataForm: {
        key: ""
      },
      brandId: 0,
      catelogPath: [],
      dataList: [],
      cateRelationTableData: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      cateRelationDialogVisible: false,
      popCatelogSelectVisible: false
    };
  },
  components: {
    AddOrUpdate,
    CategoryCascader
  },
  activated() {
    this.getDataList();
  },
  methods: {
    addCatelogSelect() {
      //{"brandId":1,"catelogId":2}
      this.popCatelogSelectVisible =false;
      this.$http({
        url: this.$http.adornUrl("/product/categorybrandrelation/save"),
        method: "post",
        data: this.$http.adornData({brandId:this.brandId,catelogId:this.catelogPath[this.catelogPath.length-1]}, false)
      }).then(({ data }) => {
        this.getCateRelation();
      });
    },
    deleteCateRelationHandle(id, brandId) {
      this.$http({
        url: this.$http.adornUrl("/product/categorybrandrelation/delete"),
        method: "post",
        data: this.$http.adornData([id], false)
      }).then(({ data }) => {
        this.getCateRelation();
      });
    },
    updateCatelogHandle(brandId) {
      this.cateRelationDialogVisible = true;
      this.brandId = brandId;
      this.getCateRelation();
    },
    getCateRelation() {
      this.$http({
        url: this.$http.adornUrl("/product/categorybrandrelation/catelog/list"),
        method: "get",
        params: this.$http.adornParams({
          brandId: this.brandId
        })
      }).then(({ data }) => {
        this.cateRelationTableData = data.data;
      });
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/product/brand/list"),
        method: "get",
        params: this.$http.adornParams({
          page: this.pageIndex,
          limit: this.pageSize,
          key: this.dataForm.key
        })
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.dataList = data.page.list;
          this.totalPage = data.page.totalCount;
        } else {
          this.dataList = [];
          this.totalPage = 0;
        }
        this.dataListLoading = false;
      });
    },
    updateBrandStatus(data) {
      console.log("最新信息", data);
      let { brandId, showStatus } = data;
      //发送请求Edit状态
      this.$http({
        url: this.$http.adornUrl("/product/brand/update/status"),
        method: "post",
        data: this.$http.adornData({ brandId, showStatus }, false)
      }).then(({ data }) => {
        this.$message({
          type: "success",
          message: "状态更新successfully"
        });
      });
    },
    // 每页数
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.pageIndex = 1;
      this.getDataList();
    },
    // 当前页
    currentChangeHandle(val) {
      this.pageIndex = val;
      this.getDataList();
    },
    // 多选
    selectionChangeHandle(val) {
      this.dataListSelections = val;
    },
    // Add / Edit
    addOrUpdateHandle(id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id);
      });
    },
    // Delete
    deleteHandle(id) {
      var ids = id
        ? [id]
        : this.dataListSelections.map(item => {
            return item.brandId;
          });
      this.$confirm(
        `Confirm对[id=${ids.join(",")}]进行[${id ? "Delete" : "Delete"}]Operation?`,
        "",
        {
          confirmButtonText: "Confirm",
          cancelButtonText: "Cancel",
          type: "warning"
        }
      ).then(() => {
        this.$http({
          url: this.$http.adornUrl("/product/brand/delete"),
          method: "post",
          data: this.$http.adornData(ids, false)
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({
              message: "Operation Successful",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.getDataList();
              }
            });
          } else {
            this.$message.error(data.msg);
          }
        });
      });
    }
  }
};
</script>

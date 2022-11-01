<template>
  <div class="mod-config">
    <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()">
      <el-form-item>
        <el-input v-model="dataForm.key" placeholder="" clearable></el-input>
      </el-form-item>
      <el-form-item>
        <el-button @click="getDataList()">Search</el-button>
        <el-button v-if="isAuth('order:order:save')" type="primary" @click="addOrUpdateHandle()">Add</el-button>
        <el-button v-if="isAuth('order:order:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">Delete</el-button>
      </el-form-item>
    </el-form>
    <el-table
      :data="dataList"
      border
      v-loading="dataListLoading"
      @selection-change="selectionChangeHandle"
      style="width: 100%;">
      <el-table-column
        type="selection"
        header-align="center"
        align="center"
        width="50">
      </el-table-column>
      <el-table-column
        prop="id"
        header-align="center"
        align="center"
        label="id">
      </el-table-column>
      <el-table-column
        prop="memberId"
        header-align="center"
        align="center"
        label="User Id">
      </el-table-column>
      <el-table-column
        prop="orderSn"
        header-align="center"
        align="center"
        width="200px"
        label="Order Number">
      </el-table-column>

      <el-table-column
        prop="modifyTime"
        header-align="center"
        align="center"
        width="150px"
        label="Create Time">
      </el-table-column>



      <el-table-column
        prop="totalAmount"
        header-align="center"
        align="center"
        width="120px"
        label="Total Amount">
      </el-table-column>
      <el-table-column
        prop="payAmount"
        header-align="center"
        align="center"
        width="120px"
        label="Grand Amount">
      </el-table-column>
      <el-table-column
        prop="freightAmount"
        header-align="center"
        align="center"
        width="100px"
        label="Delievry fee">
      </el-table-column>




      <el-table-column
        prop="status"
        header-align="center"
        align="center"
        label="Status">
      </el-table-column>
      <el-table-column
        prop="deliveryCompany"
        header-align="center"
        align="center"
        width="100px"
        label="Logistics company">
      </el-table-column>
      <el-table-column
        prop="deliverySn"
        header-align="center"
        align="center"
        label="Tracking No">
      </el-table-column>






      <el-table-column
        prop="receiverName"
        header-align="center"
        align="center"
        label="Receiver">
      </el-table-column>
      <el-table-column
        prop="receiverPhone"
        header-align="center"
        align="center"
        label="Phone">
      </el-table-column>
      <el-table-column
        prop="receiverPostCode"
        header-align="center"
        align="center"
        width="100px"
        label="Post Code">
      </el-table-column>
      <el-table-column
        prop="receiverProvince"
        header-align="center"
        align="center"
        width="90px"
        label="Province">
      </el-table-column>
      <el-table-column
        prop="receiverCity"
        header-align="center"
        align="center"
        label="City">
      </el-table-column>

      <el-table-column
        prop="receiverDetailAddress"
        header-align="center"
        align="center"
        label="Address">
      </el-table-column>

      <el-table-column
        prop="confirmStatus"
        header-align="center"
        align="center"
        width="100px"
        label="Delivered">
      </el-table-column>
      <el-table-column
        prop="deleteStatus"
        header-align="center"
        align="center"
        width="150px"
        label="Delete Status">
      </el-table-column>

      <el-table-column
        prop="paymentTime"
        header-align="center"
        align="center"
        width="130px"
        label="Payment time">
      </el-table-column>
      <el-table-column
        prop="deliveryTime"
        header-align="center"
        align="center"
        width="130px"
        label="Shipping Time">
      </el-table-column>
      <el-table-column
        prop="receiveTime"
        header-align="center"
        align="center"
        width="130px"
        label="Delivered Time">
      </el-table-column>
      <el-table-column
        prop="commentTime"
        header-align="center"
        align="center"
        label="Review">
      </el-table-column>

      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="150"
        label="Operaion">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">Edit</el-button>
          <el-button type="text" size="small" @click="deleteHandle(scope.row.id)">Delete</el-button>
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
      layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>
    <!-- 弹窗, Add / Edit -->
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @refreshDataList="getDataList"></add-or-update>
  </div>
</template>

<script>
  import AddOrUpdate from './order-add-or-update'
  export default {
    data () {
      return {
        dataForm: {
          key: ''
        },
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false,
        dataListSelections: [],
        addOrUpdateVisible: false
      }
    },
    components: {
      AddOrUpdate
    },
    activated () {
      this.getDataList()
    },
    methods: {
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/order/order/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'key': this.dataForm.key
          })
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.dataList = data.page.list
            this.totalPage = data.page.totalCount
          } else {
            this.dataList = []
            this.totalPage = 0
          }
          this.dataListLoading = false
        })
      },
      // 每页数
      sizeChangeHandle (val) {
        this.pageSize = val
        this.pageIndex = 1
        this.getDataList()
      },
      // 当前页
      currentChangeHandle (val) {
        this.pageIndex = val
        this.getDataList()
      },
      // 多选
      selectionChangeHandle (val) {
        this.dataListSelections = val
      },
      // Add / Edit
      addOrUpdateHandle (id) {
        this.addOrUpdateVisible = true
        this.$nextTick(() => {
          this.$refs.addOrUpdate.init(id)
        })
      },
      // Delete
      deleteHandle (id) {
        var ids = id ? [id] : this.dataListSelections.map(item => {
          return item.id
        })
        this.$confirm(`确定对[id=${ids.join(',')}]进行[${id ? 'Delete' : 'Delete'}]Operation?`, '', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/order/order/delete'),
            method: 'post',
            data: this.$http.adornData(ids, false)
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.$message({
                message: 'Operationsuccessfully',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.getDataList()
                }
              })
            } else {
              this.$message.error(data.msg)
            }
          })
        })
      }
    }
  }
</script>

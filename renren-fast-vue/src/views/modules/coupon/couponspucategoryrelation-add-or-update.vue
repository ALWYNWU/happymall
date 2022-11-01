<template>
  <el-dialog
    :title="!dataForm.id ? 'Add' : 'Edit'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="120px">
    <el-form-item label="优惠券id" prop="couponId">
      <el-input v-model="dataForm.couponId" placeholder="优惠券id"></el-input>
    </el-form-item>
    <el-form-item label="产品Categoryid" prop="categoryId">
      <el-input v-model="dataForm.categoryId" placeholder="产品Categoryid"></el-input>
    </el-form-item>
    <el-form-item label="产品CategoryName" prop="categoryName">
      <el-input v-model="dataForm.categoryName" placeholder="产品CategoryName"></el-input>
    </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">Cancel</el-button>
      <el-button type="primary" @click="dataFormSubmit()">Confirm</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible: false,
        dataForm: {
          id: 0,
          couponId: '',
          categoryId: '',
          categoryName: ''
        },
        dataRule: {
          couponId: [
            { required: true, message: '优惠券id不能为空', trigger: 'blur' }
          ],
          categoryId: [
            { required: true, message: '产品Categoryid不能为空', trigger: 'blur' }
          ],
          categoryName: [
            { required: true, message: '产品CategoryName不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.dataForm.id = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            this.$http({
              url: this.$http.adornUrl(`/coupon/couponspucategoryrelation/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.couponId = data.couponSpuCategoryRelation.couponId
                this.dataForm.categoryId = data.couponSpuCategoryRelation.categoryId
                this.dataForm.categoryName = data.couponSpuCategoryRelation.categoryName
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/coupon/couponspucategoryrelation/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'couponId': this.dataForm.couponId,
                'categoryId': this.dataForm.categoryId,
                'categoryName': this.dataForm.categoryName
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: 'Operation Successful',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      }
    }
  }
</script>

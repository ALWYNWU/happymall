<template>
  <el-dialog
    :title="!dataForm.id ? 'Add' : 'Edit'"
    :close-on-click-modal="false"
    append-to-body
    :visible.sync="visible"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="120px"
    >
      <el-form-item label="活动场次id" prop="promotionSessionId">
        <el-input v-model="sessionId" placeholder="活动场次id" :disabled="true"></el-input>
      </el-form-item>
      <el-form-item label="productid" prop="skuId">
        <el-input v-model="dataForm.skuId" placeholder="productid"></el-input>
      </el-form-item>
      <el-form-item label="秒杀Price" prop="seckillPrice">
        <el-input-number v-model="dataForm.seckillPrice" :min="0" :precision="2" :step="0.1"></el-input-number>
      </el-form-item>
      <el-form-item label="秒杀总量" prop="seckillCount">
        <el-input-number v-model="dataForm.seckillCount" :min="1" label="秒杀总量"></el-input-number>
      </el-form-item>
      <el-form-item label="每人限购数量" prop="seckillLimit">
        <el-input-number v-model="dataForm.seckillLimit" :min="1" label="每人限购数量"></el-input-number>
      </el-form-item>
      <el-form-item label="Sort" prop="seckillSort">
        <el-input v-model="dataForm.seckillSort" placeholder="Sort"></el-input>
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
  data() {
    return {
      visible: false,
      dataForm: {
        id: 0,
        promotionId: "",
        promotionSessionId: "",
        skuId: "",
        seckillPrice: "",
        seckillCount: "",
        seckillLimit: 1,
        seckillSort: 0
      },
      dataRule: {
        sessionId: [
          { required: true, message: "活动场次id不能为空", trigger: "blur" }
        ],
        skuId: [{ required: true, message: "productid不能为空", trigger: "blur" }],
        seckillPrice: [
          { required: true, message: "秒杀Price不能为空", trigger: "blur" }
        ],
        seckillCount: [
          { required: true, message: "秒杀总量不能为空", trigger: "blur" }
        ],
        seckillLimit: [
          { required: true, message: "每人限购数量不能为空", trigger: "blur" }
        ],
        seckillSort: [
          { required: true, message: "Sort不能为空", trigger: "blur" }
        ]
      }
    };
  },
  props: {
    sessionId: {
      type: Number,
      default: 0
    }
  },
  methods: {
    init(id) {
      this.dataForm.id = id || 0;
      this.visible = true;
      this.$nextTick(() => {
        this.$refs["dataForm"].resetFields();
        if (this.dataForm.id) {
          this.$http({
            url: this.$http.adornUrl(
              `/coupon/seckillskurelation/info/${this.dataForm.id}`
            ),
            method: "get",
            params: this.$http.adornParams()
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.dataForm.promotionId = data.seckillSkuRelation.promotionId;
              this.dataForm.promotionSessionId =
                data.seckillSkuRelation.promotionSessionId;
              this.dataForm.skuId = data.seckillSkuRelation.skuId;
              this.dataForm.seckillPrice = data.seckillSkuRelation.seckillPrice;
              this.dataForm.seckillCount = data.seckillSkuRelation.seckillCount;
              this.dataForm.seckillLimit = data.seckillSkuRelation.seckillLimit;
              this.dataForm.seckillSort = data.seckillSkuRelation.seckillSort;
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
              `/coupon/seckillskurelation/${
                !this.dataForm.id ? "save" : "update"
              }`
            ),
            method: "post",
            data: this.$http.adornData({
              id: this.dataForm.id || undefined,
              promotionId: this.dataForm.promotionId,
              promotionSessionId: this.sessionId,
              skuId: this.dataForm.skuId,
              seckillPrice: this.dataForm.seckillPrice,
              seckillCount: this.dataForm.seckillCount,
              seckillLimit: this.dataForm.seckillLimit,
              seckillSort: this.dataForm.seckillSort
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
    }
  }
};
</script>

<template>
    <div>
      <el-input placeholder="Filter keyword" v-model="filterText"></el-input>
      <el-tree
        :data="menus"
        :props="defaultProps"
        node-key="catId"
        ref="menuTree"
        @node-click="nodeclick"
        :filter-node-method="filterNode"
        :highlight-current = "true"
      ></el-tree>
    </div>
  </template>
  
  <script>

  
  export default {

    components: {},
    props: {},
    data() {
      //这里存放数据
      return {
        filterText: "",
        menus: [],
        expandedKey: [],
        defaultProps: {
          children: "children",
          label: "name"
        }
      };
    },

    computed: {},

    watch: {
      filterText(val) {
        this.$refs.menuTree.filter(val);
      }
    },

    methods: {

      filterNode(value, data) {
        if (!value) return true;
        return data.name.indexOf(value) !== -1;
      },
      getMenus() {
        this.$http({
          url: this.$http.adornUrl("/product/category/list/tree"),
          method: "get"
        }).then(({ data }) => {
          this.menus = data.data;
        });
      },
      nodeclick(data, node, component) {
        // console.log("子组件category的节点被点击", data, node, component);

        this.$emit("tree-node-click", data, node, component);
      }
    },
    created() {
      this.getMenus();
    },
    mounted() {},
    beforeCreate() {}, //生命周期 - 创建之前
    beforeMount() {}, //生命周期 - 挂载之前
    beforeUpdate() {}, //生命周期 - 更新之前
    updated() {}, //生命周期 - 更新之后
    beforeDestroy() {}, //生命周期 - 销毁之前
    destroyed() {}, //生命周期 - 销毁完成
    activated() {} //如果页面有keep-alive缓存功能，这个函数会触发
  };
  </script>
  <style scoped>
  
  </style>
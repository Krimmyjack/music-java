
<template>
  <el-container> 
    <el-header>
      <yin-header></yin-header> 
    </el-header>
    <el-main> 
      <router-view />
      <yin-current-play></yin-current-play>
      <yin-play-bar></yin-play-bar>
      <yin-scroll-top></yin-scroll-top>
      <yin-audio></yin-audio>
    </el-main>
    <el-footer> 
      <yin-footer></yin-footer>
    </el-footer>
  </el-container>
</template>

<script lang="ts" setup>
import { getCurrentInstance } from "vue";
import YinHeader from "@/components/layouts/YinHeader.vue";
import YinCurrentPlay from "@/components/layouts/YinCurrentPlay.vue";
import YinPlayBar from "@/components/layouts/YinPlayBar.vue";
import YinScrollTop from "@/components/layouts/YinScrollTop.vue";
import YinFooter from "@/components/layouts/YinFooter.vue";
import YinAudio from "@/components/layouts/YinAudio.vue";

const { proxy } = getCurrentInstance(); //getCurrentInstance() 是 Vue 3 提供的一个组合 API 函数。它可以用来访问当前组件的实例，通常在 setup 函数中使用

if (sessionStorage.getItem("dataStore")) {
  proxy.$store.replaceState(Object.assign({}, proxy.$store.state, JSON.parse(sessionStorage.getItem("dataStore"))));
} //并在页面卸载时保存当前状态

window.addEventListener("beforeunload", () => {
  sessionStorage.setItem("dataStore", JSON.stringify(proxy.$store.state));  //前端存储：这段代码完全基于浏览器的 sessionStorage 功能，属于前端的处理。因此，它不需要任何后端支持
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

.el-container {
  min-height: calc(100% - 60px);
}
.el-header {
  padding: 0;
  background-color: $color-blue;
}
.el-main {
  padding-left: 0;
  padding-right: 0;
  background: linear-gradient(to bottom, #2796dd, #ffffff);

}
</style>

//主页面布局，包含头部、主体、底部三个部分，其中头部、底部使用组件实现，主体部分使用路由视图实现。
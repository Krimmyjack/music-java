<template>
  <!--轮播图-->
  <el-carousel v-if="swiperList.length" class="swiper-container" type="card" height="20vw" :interval="4000">
    <el-carousel-item v-for="(item, index) in swiperList" :key="index">
      <img :src="HttpManager.attachImageUrl(item.pic)" />
    </el-carousel-item>
  </el-carousel>
  <!--热门歌单-->
  <div class="section-container">
    <play-list 
      class="play-list-container" 
      title="歌单" 
      path="song-sheet-detail" 
      :playList="songList"
    >
      <template #actions>
        <router-link to="/song-sheet" class="more-link">更多</router-link>
      </template>
    </play-list>
  </div>

  <!--热门歌手-->
  <div class="section-container">
    <play-list 
      class="play-list-container" 
      title="歌手" 
      path="singer-detail" 
      :playList="singerList"
    >
      <template #actions>
        <router-link to="/singer" class="more-link">更多</router-link>
      </template>
    </play-list>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from "vue";

import PlayList from "@/components/PlayList.vue";
import {  NavName } from "@/enums";
import { HttpManager } from "@/api";
import mixin from "@/mixins/mixin";

const songList = ref([]); // 歌单列表
const singerList = ref([]); // 歌手列表
const swiperList = ref([]);// 轮播图 每次都在进行查询
const { changeIndex } = mixin();
try {

  HttpManager.getBannerList().then((res) => {
    swiperList.value = (res as ResponseBody).data.sort();
  });

  HttpManager.getSongList().then((res) => {
    songList.value = (res as ResponseBody).data.sort().slice(10, 20);
  });

  HttpManager.getAllSinger().then((res) => {
    singerList.value = (res as ResponseBody).data.sort().slice(1, 11);
  });

  onMounted(() => {
    changeIndex(NavName.Home);
  });
} catch (error) {
  console.error(error);
}
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

/*轮播图*/
.swiper-container {
  width: 90%;
  margin: auto;
  padding-top: 20px;
  img {
    width: 100%;
  }
}

.swiper-container:deep(.el-carousel__indicators.el-carousel__indicators--outside) {
  display: inline-block;
  transform: translateX(30vw);
}

.el-slider__runway {
  background-color: $color-blue;
}

.section-container {
  margin-bottom: 20px;

  .section-header {
    display: flex;
    justify-content: space-between; // 左右两端对齐
    align-items: center; // 垂直居中
    margin-bottom: 1rem;

    .section-title {
      font-size: 28px;
      font-weight: 500;
      color: $color-black;
    }

    .more-link {
      font-size: 16px;
      color: $color-black; // 设置更多链接的颜色为黑色
      text-decoration: none;
      font-weight: bold;
      transition: all 0.3s ease;
      padding-left: 20px; // 给更多链接一些左边距以确保它不会紧贴内容

      &:hover {
        color: darken($color-black, 10%); // 悬停时加深链接颜色
        text-decoration: underline;
      }
    }
  }

  .play-list-container {
    flex-grow: 1; // 让播放列表占据尽可能多的空间
    max-width: 100%; // 确保播放列表容器宽度适应内容
  }
}
</style>

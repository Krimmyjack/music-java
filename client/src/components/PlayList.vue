<template>
  <div class="play-list">
    <!-- 顶部标题部分，包含主标题和副标题 -->
    <div class="play-header" v-if="title || subtitle">
      <div class="header-content">
        <!-- 显示主标题 -->
        <div class="play-title" v-if="title">{{ title }}</div>
        <!-- 显示副标题 -->
        <div class="play-subtitle" v-if="subtitle">{{ subtitle }}</div>
      </div>
      <!-- 可选的操作按钮组 -->
      <div class="header-actions" v-if="$slots.actions">
        <slot name="actions"></slot>
      </div>
    </div>

    <!-- 播放列表项 -->
    <ul class="play-body">
      <li class="card-frame" v-for="(item, index) in playList" :key="index">
        <div class="card" @click="goAblum(item)">
          <!-- 专辑封面 -->
          <el-image class="card-img" fit="contain" :src="attachImageUrl(item.pic)" />
          <!-- 遮罩层，点击跳转 -->
          <div class="mask" @click="goAblum(item)">
            <yin-icon class="mask-icon" :icon="BOFANG"></yin-icon>
          </div>
        </div>
        <!-- 专辑/歌曲名 -->
        <p class="card-name">{{ item.name || item.title }}</p>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, toRefs, computed, ref } from "vue";
import { useStore } from "vuex";
import YinIcon from "@/components/layouts/YinIcon.vue";
import mixin from "@/mixins/mixin";
import { Icon } from "@/enums";
import { HttpManager } from "@/api";

export default defineComponent({
  components: {
    YinIcon,
  },
  props: {
    title: String,
    subtitle: String, // 新增副标题
    playList: Array,
    path: String,
  },
  setup(props) {
    const { proxy } = getCurrentInstance();
    const { routerManager, checkStatus } = mixin();
    const store = useStore();
    const { path } = toRefs(props);
    const isCollection = ref(false); // 是否收藏

    // 从 Vuex 获取用户ID和播放列表ID
    const userIdVO = computed(() => store.getters.userId);
    const playListIdVO = computed(() => store.getters.playListId);

    // 跳转到专辑/歌曲详情页
    function goAblum(item) {
      console.log("item clicked: ", JSON.stringify(item, null, 2));
      console.log("item path: "+`/${path.value}/${item.id}`);
      proxy.$store.commit("setSongDetails", item);
      routerManager(path.value, { path: `/${path.value}/${item.id}` });
    }


    return {
      BOFANG: Icon.BOFANG,
      goAblum,
      attachImageUrl: HttpManager.attachImageUrl,
      isCollection,
    };
  },

  data() {
    return {
      iconList: {
        dislike: Icon.Dislike,
        like: Icon.Like,
      },
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

// 颜色定义
$primary-text-color: $color-black;
$more-link-color: $primary-text-color;
$more-link-hover-color: darken($more-link-color, 10%);

.play-list {
  padding: 0 1rem;

  .play-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
    color: $primary-text-color;

    .header-content {
      flex-grow: 1;
      text-align: center;

      .play-title {
        height: 60px;
        line-height: 60px;
        font-size: 28px;
        font-weight: 500;
        box-sizing: border-box;
      }

      .play-subtitle {
        font-size: 18px;
        color: darken($primary-text-color, 10%);
      }
    }

    .header-actions {
      margin-left: 20px;

      .more-link {
        font-size: 16px;
        color: $more-link-color;
        text-decoration: none;
        font-weight: bold;
        transition: all 0.3s ease;

        &:hover {
          color: $more-link-hover-color;
          text-decoration: underline;
        }
      }
    }
  }

  .play-body {
    display: flex;
    flex-wrap: wrap;
    justify-content: flex-start;
    align-items: stretch;
  }

  .card-frame {
    width: 46%;
    margin: 0.5rem 2%;

    .card {
      position: relative;
      height: 0;
      padding-bottom: 100%;
      overflow: hidden;
      border-radius: 5px;

      .card-img {
        width: 100%;
        transition: all 0.4s ease;
      }
    }

    .card-name {
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
      margin: 0.5rem 0;
    }

    &:hover .card-img {
      transform: scale(1.2);
    }
  }

  .mask {
    position: absolute;
    top: 0;
    width: 100%;
    height: 100%;
    overflow: hidden;
    border-radius: 5px;
    background-color: rgba(52, 47, 41, 0.4);
    display: flex;
    justify-content: center;
    align-items: center;
    transition: all 0.3s ease-in-out;
    opacity: 0;

    .mask-icon {
      font-size: 2em;
      color: rgba(240, 240, 240, 1);
    }

    &:hover {
      opacity: 1;
      cursor: pointer;
    }
  }

  @media screen and (min-width: $sm) {
    .card-frame {
      width: 18%;
      margin: 0.5rem 1%;
    }
  }

  @media screen and (max-width: $sm) {
    .card-frame {
      width: 46%;
      margin: 0.5rem 2%;
    }
  }
}
</style>

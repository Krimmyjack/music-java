<template>
  <transition name="aside-fade">
    <div class="yin-current-play" v-if="showAside">
      <h2 class="title">当前播放</h2>
      <div class="control">共 {{ (currentPlayList && currentPlayList.length) || 0 }} 首</div>
      <ul class="menus">
        <li
          v-for="(item, index) in currentPlayList"
          :class="{ 'is-play': songId === item.id }"
          :key="index"
          @click="playMusic({
            id: item.id,
            url: item.url,
            pic: item.pic,
            index: index,
            name: item.name,
            lyric: item.lyric,
            currentSongList: currentPlayList,
          })">
          {{ getSongTitle(item.name) }}
          <button v-if="show" class="delete-button" @click.stop="deleteSong(item.id)">
            删除
          </button>
        </li>
      </ul>
    </div>
  </transition>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, toRefs, computed, reactive, ref, watch, onMounted } from "vue";
import { MoreFilled, Delete, Download } from "@element-plus/icons-vue";
import { useStore } from "vuex";
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api";
import { Icon } from "@/enums";
import YinIcon from "./layouts/YinIcon.vue";

export default defineComponent({
  props: {
    songList: Array,
    show: {
      default: true
    }
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();
    const {  getSongTitle, playMusic, checkStatus } = mixin();

    const songId = computed(() => store.getters.songId); // 音乐 ID
    // 深拷贝 currentPlayList，确保独立于原始歌单
    const currentPlayList = computed(() => {
      return JSON.parse(JSON.stringify(store.getters.currentPlayList));
    });
    //const currentPlayList = computed(() => store.getters.currentPlayList); // 当前播放
    //const playlistCopy = [...currentPlayList.value];
    // const currentPlayList = JSON.parse(JSON.stringify(currentPlayList1));
    const showAside = computed(() => store.getters.showAside); // 是否显示侧边栏

    onMounted(() => {
      document.addEventListener('click', () => {
        proxy.$store.commit('setShowAside', false)
      }, true)
    })

    const userId = computed(() => store.getters.userId);

    /*async function deleteCollection({ id }) {
      if (!checkStatus()) return;

      const result = (await HttpManager.deleteCollection(userId.value, id)) as ResponseBody;
      (proxy as any).$message({
        message: result.message,
        type: result.type,
      });
      if (result.data === false) proxy.$emit("changeData", result.data);
    } */

    const deleteSong = async (id: number) => {
      const index = currentPlayList.value.findIndex((item) => item.id === id);
      if (index !== -1) {
        // 从 currentPlayList 中移除对应的行
        currentPlayList.value.splice(index, 1);

        store.commit("player/SET_CURRENT_PLAY_LIST", currentPlayList.value);

        // 显示删除成功的提示
        (proxy as any).$message({
          message: "删除成功",
          type: "success",
        });
      } else {
        // 显示未找到歌曲的提示
        (proxy as any).$message({
          message: "未找到要删除的歌曲",
          type: "error",
        });
      }
    }; 

    return {
      songId,
      currentPlayList,
      showAside,
      getSongTitle,
      playMusic,
      deleteSong,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/yin-current-play.scss";

.delete-button {
  background-color: transparent; /* 透明背景 */
  color: #ff4d4f; /* 保留红色字体 */
  border: none;
  padding: 5px 10px;
  cursor: pointer;
  border-radius: 4px;
  position: absolute;
  right: 10px; /* 距离右边 10px */
  font-weight: bold; /* 加粗 */
}

</style>

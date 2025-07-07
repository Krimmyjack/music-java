<template>
  <div class="content">
    <el-table highlight-current-row :data="dataList" @row-click="handleClick">
      <el-table-column prop="songName" label="歌曲" />
      <el-table-column prop="singerName" label="歌手" />
      <el-table-column prop="introduction" label="专辑" />
      <el-table-column label="下载" width="80" align="center">
      <!-- 使用 template 插槽自定义列内容 -->
      <!-- <template #default="scope">
        <yin-icon
          class="yin-play-show"
          :class="{ active: scope.row.isCollection }"
          :icon="scope.row.isCollection ? iconList.like : iconList.dislike"
          @click="changeCollection({row:scope.row})"
        ></yin-icon>
      </template> -->

        <template #default="scope">
          <el-button 
            icon="Download" 
            @click="downloadMusic({
              songUrl: scope.row.url,
              songName: scope.row.name,
            })"
            class="custom-download-btn">
            下载  
          </el-button>
          <!-- <el-dropdown>
            <el-icon @click="handleEdit(scope.row)"><MoreFilled /></el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  :icon="Download"
                  @click="
                    downloadMusic({
                      songUrl: scope.row.url,
                      songName: scope.row.name,
                    })
                  ">下载</el-dropdown-item>

                <el-dropdown-item :icon="Delete" v-if="show" @click="deleteCollection({ id: scope.row.id })">删除</el-dropdown-item> 
              </el-dropdown-menu>
            </template>
          </el-dropdown> -->
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, toRefs, computed, reactive, ref, watch } from "vue";
import { useStore } from "vuex";
import { MoreFilled, Delete, Download } from "@element-plus/icons-vue";

import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api";
import { Icon } from "@/enums";
import YinIcon from "./layouts/YinIcon.vue";

export default defineComponent({
  components: {
  },
  props: {
    songList: Array,
    show: {
      default: false
    }
  },
  emits: ["changeData"],
  setup(props) {
    const { getSongTitle, getSingerName, playMusic, checkStatus, downloadMusic } = mixin();
    const { proxy } = getCurrentInstance();
    const store = useStore();
    const isCollection = ref(false); // 是否收藏
    const { songList } = toRefs(props);
    console.log("songList: ", JSON.stringify(songList, null, 2));
    
    const userIdVO = computed(() => store.getters.userId);
    const songIdVO = computed(() => store.getters.songId);
    const token = computed(() => store.getters.token);

    watch(songIdVO, () => {
      initCollection();
    });
    watch(token, (value) => {
      if (!value) isCollection.value = false;
    });
    
    async function initCollection() {
      if (!checkStatus(false)) return;
      const userId = userIdVO.value;
      const type = '0';
     
      for (const row of songList.value) {
        console.log("row: ", JSON.stringify(row, null, 2));
        // const songId = row.id;
        // row.isCollection = ((await HttpManager.isCollection({ userId, type, songId })) as ResponseBody).data;
      }
    }


    async function changeCollection({row}) {
      if (!checkStatus()) return;
      
      console.log("Changing collection for row:", row);
      const userId = userIdVO.value;
      const type = '0'; //这里要看看 不能直接写死
      const songId = songIdVO.value;

      const result = row.isCollection
          ? ((await HttpManager.deleteCollection(userIdVO.value, songIdVO.value)) as ResponseBody)
          : ((await HttpManager.setCollection({userId, type, songId})) as ResponseBody);
      (proxy as any).$message({
        message: result.message,
        type: result.type,
      });
      console.log("songId"+songId);
      console.log("row.isCollection"+row.isCollection);
      if (result.data == true || result.data == false) row.isCollection = result.data;
    }

    const songUrl = computed(() => store.getters.songUrl);
    const singerName = computed(() => store.getters.singerName);
    const songTitle = computed(() => store.getters.songTitle);
    const dataList = computed(() => {
      const list = [];
        songList.value.forEach((item: any, index) => {
        console.log("item: ", JSON.stringify(item, null, 2));
        item["songName"] = getSongTitle(item.name);
        item["singerName"] = getSingerName(item.name);
        item["index"] = index;
        list.push(item);
      });
      return list;
    });

    function handleClick(row) {
      playMusic({
        id: row.id,
        url: row.url,
        pic: row.pic,
        index: row.index,
        name: row.name,
        lyric: row.lyric,
        currentSongList: songList.value,
      });
    }

    function handleEdit(row) {
      console.log("row", row);
    }

    const userId = computed(() => store.getters.userId);

    async function deleteCollection({ id }) {
      if (!checkStatus()) return;

      const result = (await HttpManager.deleteCollection(userId.value, id)) as ResponseBody;
      (proxy as any).$message({
        message: result.message,
        type: result.type,
      });

      if (result.data === false) proxy.$emit("changeData", result.data);
    }

    return {
      dataList,
      Delete,
      Download,
      songUrl,
      singerName,
      songTitle,
      handleClick,
      handleEdit,
      downloadMusic,
      deleteCollection,
      changeCollection,
      isCollection,
      initCollection,
    };
  },
  data(vm) {
    return {
      iconList: {
        dislike: Icon.Dislike,
        like: Icon.Like,

      },
    }
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";

.content {
  background-color: $color-white;
  border-radius: $border-radius-songlist;
  padding: 10px;
}

.content:deep(.el-table__row.current-row) {
  color: $color-black;
  font-weight: bold;
}

.content:deep(.el-table__row) {
 cursor: pointer;
}

.custom-download-btn {
  width: 50px;
  padding: 6px 10px;
  font-size: 14px;  /* 可调整字体大小 */
}

.icon {
  @include icon(1.2em, $color-black);
}
</style>
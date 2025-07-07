<template>
    <div class="content">
      <el-table highlight-current-row :data="dataList" @row-click="handleClick">
        <el-table-column prop="title" label="歌单" />
        <!-- <el-table-column label="收藏" width="80" align="center">
          收藏
          <yin-icon
              class="yin-play-show"
              :class="{ active: isCollection }"
              :icon="isCollection ? iconList.like : iconList.dislike"
              @click="changeCollection"
          ></yin-icon>
        </el-table-column> -->
      </el-table>
    </div>
  </template>
  
  <script lang="ts">
  import { defineComponent, getCurrentInstance, toRefs, computed, reactive, ref, watch, onMounted } from "vue";
  import { useStore } from "vuex";
  import { MoreFilled, Delete, Download } from "@element-plus/icons-vue";
  
  import mixin from "@/mixins/mixin";
  import { HttpManager } from "@/api";
  import { Icon } from "@/enums";
  import YinIcon from "./layouts/YinIcon.vue";
  import { RouterName } from "@/enums";
  export default defineComponent({
    components: {
    
    },
    props: {
        path: String,
        SongListList: {
            type: Array,
            default: () => [],  // 设置默认值为空数组
        },
        show: {
            type: Boolean,
            default: false
        }
    },
    emits: ["changeData"],
    setup(props) {
      const { path } = toRefs(props);
      const { getSongTitle, getSingerName, playMusic, checkStatus, downloadMusic } = mixin();
      const { proxy } = getCurrentInstance();
      const store = useStore();
      const isCollection = ref(false); // 是否收藏
      const { SongListList } = toRefs(props);
      const { routerManager } = mixin();
      const songDetails = computed(() => store.getters.songDetails);
      const songlistIdVO = songDetails.value?.id; // 使用可选链操作符来避免空值错误
      console.log("歌单ID"+songlistIdVO);
      const userIdVO = computed(() => store.getters.userId);
      const token = computed(() => store.getters.token);
  
    watch(songlistIdVO, () => {
        initCollection();
    });
    watch(token, (value) => {
        if (!value) isCollection.value = false;
    });

    async function initCollection() {
      if (!checkStatus(false)) return;

      const userId = userIdVO.value;
      const type = '1';
      const songListId = songlistIdVO;
      isCollection.value = ((await HttpManager.issonglistollection({userId, type, songListId})) as ResponseBody).data;
    }

    async function changeCollection() {
        if (!checkStatus()) return;

        const userId = userIdVO.value;
        const type = '1';  //这里要看看 不能直接写死
        const songListId = songlistIdVO;
        
        const result = isCollection.value
            ? (await HttpManager.deletesonglistCollection(userIdVO.value, songlistIdVO)) as ResponseBody
            : (await HttpManager.setlistcollection({ userId, type, songListId })) as ResponseBody;

        (proxy as any).$message({
            message: result.message,
            type: result.type,
        });

        if (result.data === true || result.data === false) {
            isCollection.value = Boolean(result.data);  // 确保是布尔值
        }
    }


    onMounted(() => {
      if (songlistIdVO) initCollection();
    });
  
    const playListName = songDetails.value?.title;
    const dataList = computed(() => {
    const list = [];
        SongListList.value.forEach((item: any, index) => {
        item["index"] = index;
        list.push(item);
        });
  return list;
});

  
    function handleClick(row) {
      console.log("Row clicked: ", JSON.stringify(row, null, 2));
      console.log("Row path: ", `/${path.value}/${row.id}`);
      proxy.$store.commit("setSongDetails", row);
      routerManager(path.value, { path: `/${path.value}/${row.id}` });
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
        playListName,
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
  
  .icon {
    @include icon(1.2em, $color-black);
  }
  </style>
  
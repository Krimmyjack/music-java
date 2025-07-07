<template>
  <div class="personal">
    <div class="personal-info">
      <div class="personal-img" @click="dialogTableVisible = true">
        <el-image fit="contain" :src="attachImageUrl(userPic)"/>
      </div>
      <div class="personal-msg">
        <div class="username">{{ personalInfo.username }}</div>
        <div class="introduction">{{ personalInfo.introduction }}</div>
      </div>
      <el-button class="edit-info" round :icon="Edit" @click="goPage()">修改个人信息</el-button>
    </div>
    <div class="personal-body">
      <h3>收藏的歌曲</h3>
      <song-list :songList="collectSongList" :show="true" @changeData="changeData"></song-list>
      <h3>收藏的歌单</h3>
      <!--<div v-for="(playlist, index) in SongListList" :key="index" @click="goToPlaylist(playlist)">-->
        <SongListList :SongListList="collectPlaylist" path="song-sheet-detail"  :show="true" ></SongListList >
      <!-- </div> -->
    </div>
    <el-dialog v-model="dialogTableVisible" title="修改头像">
      <upload></upload>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, nextTick, ref, toRefs, computed, watch, reactive } from "vue";
import { useStore } from "vuex";
import { Edit } from "@element-plus/icons-vue";
import SongList from "@/components/SongList.vue";
import SongListList from "@/components/SongListList.vue"
import Upload from "../setting/Upload.vue";
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api";
import { RouterName } from "@/enums";

export default defineComponent({
  props: {
    SongListList: Array,
    path: String,
  },
  components: {
    SongList,
    SongListList,
    Upload,
  },
  setup(props) {
    const store = useStore();
    const { proxy } = getCurrentInstance();
    const { path } = toRefs(props);
    const { routerManager } = mixin();

    const dialogTableVisible = ref(false);
    const collectSongList = ref([]); // 收藏的歌曲
    const collectPlaylist = ref([]); // 收藏的歌单
    const personalInfo = reactive({
      username: "",
      userSex: "",
      birth: "",
      location: "",
      introduction: "",
    });
    const userId = computed(() => store.getters.userId);
    const userPic = computed(() => store.getters.userPic);
    watch(userPic, () => {
      dialogTableVisible.value = false;
    });

    function goPage() {
      routerManager(RouterName.Setting, { path: RouterName.Setting });
    }
    async function getUserInfo(id) {
      const result = (await HttpManager.getUserOfId(id)) as ResponseBody;
      personalInfo.username = result.data[0].username;
      personalInfo.userSex = result.data[0].sex;
      personalInfo.birth = result.data[0].birth;
      personalInfo.introduction = result.data[0].introduction;
      personalInfo.location = result.data[0].location;
    }

    // 获取收藏的歌曲
    async function getCollection(userId) {
      collectSongList.value = []
      const result = (await HttpManager.getCollectionOfUser(userId)) as ResponseBody;
      const collectIDList = result.data || []; // 存放收藏的歌曲ID
      // console.log("collectSongList1: ", JSON.stringify(collectSongList, null, 2));
      // 通过歌曲ID获取歌曲信息
      for (const item of collectIDList) {
        if (!item.songId) {
          console.error(`歌曲${item}异常`);
          continue;
        }
        const result = (await HttpManager.getSongOfId(item.songId)) as ResponseBody;
        // console.log("result.data:", JSON.stringify(result.data, null, 2));
        collectSongList.value.push(result.data[0]);
      }
      // console.log("collectSongList2: ", JSON.stringify(collectSongList, null, 2));
    }

    // 获取收藏的歌单
    async function getCollectionPlaylist(userId) {
      collectPlaylist.value = []
      const result = (await HttpManager.getCollectionPlaylistOfUser(userId)) as ResponseBody;
      const collectIDList = result.data || []; // 存放收藏的歌单ID
      // 通过歌单ID获取歌单信息
      for (const item of collectIDList) {
        if (!item.songListId) {
          console.error(`歌单${item}异常`);
          continue;
        }
        const result = (await HttpManager.getPlaylistOfId(item.songListId)) as ResponseBody;
        
        collectPlaylist.value.push(result.data[0]);
      }
      // console.log("collectPlaylist", JSON.stringify(collectPlaylist.value, null, 2));
      // console.log("collectPlaylist.title"+collectPlaylist.value[2]);
    }

    function changeData() {
      getCollection(userId.value);
    }

    // function goToPlaylist(item){
    //   console.log("goToPlaylist"+item);
    //   proxy.$store.commit("setSongDetails", item);
    //   routerManager(path.value, { path: `/${path.value}/${item.id}` });
    // }

    function changePlaylistData() {
      getCollectionPlaylist(userId.value);
    }

    nextTick(() => {
      getUserInfo(userId.value);
      getCollection(userId.value);
      getCollectionPlaylist(userId.value);
    });

    return {
      Edit,
      userPic,
      dialogTableVisible,
      collectSongList,
      collectPlaylist,
      personalInfo,
      // goToPlaylist,
      attachImageUrl: HttpManager.attachImageUrl,
      goPage,
      changeData,
      changePlaylistData,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

.personal {
  padding-top: $header-height + 150px;

  &::before {
    content: "";
    background-color: $color-blue-shallow;
    position: absolute;
    top: 0;
    width: 100%;
    height: $header-height + 150px;
  }
}

.personal-info {
  position: relative;
  margin-bottom: 60px;
  .personal-img {
    height: 200px;
    width: 200px;
    border-radius: 50%;
    border: 5px solid $color-white;
    position: absolute;
    top: -180px;
    left: 50px;
    cursor: pointer;
  }
  .personal-msg {
    margin-left: 300px;
    position: absolute;
    top: -120px;

    .username {
      font-size: 50px;
      font-weight: 600;
    }

    .introduction {
      font-size: 20px;
      font-weight: 500;
    }
  }
  .edit-info {
    position: absolute;
    right: 10vw;
    margin-top: -120px;
  }
}

@media screen and (min-width: $sm) {
  .personal-body {
    padding: 0px 100px;
  }
}

@media screen and (max-width: $sm) {
  .edit-info {
    display: none;
  }
}
</style>

<template>
  <el-container>
    <el-aside class="album-slide">
      <el-image class="album-img" fit="contain" :src="attachImageUrl(songDetails.pic)" />
      <h3 class="album-info">{{ songDetails.title }}</h3>
    </el-aside>
    <el-main class="album-main">
      <h1>简介</h1>
      <p>{{ songDetails.introduction }}</p>
      <!--评分-->
      <div class="album-score">
        <div>
          <h3>歌单评分</h3>
          <el-rate v-model="rank" allow-half disabled></el-rate>
        </div>
        <span>{{ rank * 2 }}</span>
        <div>
          <h3>{{ assistText }} {{ score * 2 }}</h3>
          <el-rate allow-half v-model="score" :disabled="disabledRank" @click="pushValue()"></el-rate>
        </div>
        <!-- 收藏按钮 -->
        <div class="song-edit song-ctr">
          <yin-icon
              class="yin-play-show-new"
              :class="{ active: isCollection }"
              :icon="isCollection ? iconList.like : iconList.dislike"
              @click="changeCollection"
          ></yin-icon>
        </div>
      </div>
      <!--歌曲-->
      <song-list class="album-body" :songList="currentSongList1"></song-list>
      <comment :playId="songListId" :type="1"></comment>
    </el-main>
  </el-container> 
</template>

<script lang="ts">
import {computed, defineComponent, getCurrentInstance, onMounted, ref, watch} from "vue";
import { useStore } from "vuex";
import mixin from "@/mixins/mixin";
import SongList from "@/components/SongList.vue";
import Comment from "@/components/Comment.vue";
import { HttpManager } from "@/api";
import {Icon, RouterName} from "@/enums";
import YinIcon from "@/components/layouts/YinIcon.vue";

export default defineComponent({
  components: {
    YinIcon,
    SongList,
    Comment,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();
    const { checkStatus } = mixin();
    const isCollection = ref(false); // 是否收藏
    const currentSongList = ref([]); // 存放的音乐
    const currentSongList1 = ref([]);
    const nowSongListId = ref(""); // 歌单 ID
    const nowScore = ref(0);
    const nowRank = ref(0);
    const disabledRank = ref(false);
    const assistText = ref("评价");
    // const evaluateList = ref(["很差", "较差", "还行", "推荐", "力推"]);
    const songDetails = computed(() => store.getters.songDetails); // 单个歌单信息
    const nowUserId = computed(() => store.getters.userId);
    const songlistIdVO = songDetails.value.id;
    const token = computed(() => store.getters.token);
    nowSongListId.value = songDetails.value.id; // 给歌单ID赋值
    watch(songlistIdVO, () => {
      initCollection();
    });
    watch(token, (value) => {
      if (!value) isCollection.value = false;
    });

    async function initCollection() {
      if (!checkStatus(false)) return;

      const userId = nowUserId.value;
      const type = '1';
      const songListId = songlistIdVO;
      isCollection.value = ((await HttpManager.issonglistollection({userId, type, songListId})) as ResponseBody).data;
    }

    async function changeCollection() {
      if (!checkStatus()) return;

      const userId = nowUserId.value;
      const type = '1'; //这里要看看 不能直接写死
      const songListId = songlistIdVO;
      console.log(userId, type, songListId);
      const result = isCollection.value
          ? ((await HttpManager.deletesonglistCollection(nowUserId.value, songlistIdVO)) as ResponseBody)
          : ((await HttpManager.setlistcollection({userId, type, songListId})) as ResponseBody);
      (proxy as any).$message({
        message: result.message,
        type: result.type,
      });

      if (result.data == true || result.data == false) isCollection.value = result.data;
    }

    onMounted(() => {
      if (songlistIdVO) initCollection();
    });
    // 收集歌单里面的歌曲
    async function getSongId(id) {
      const result = (await HttpManager.getListSongOfSongId(id)) as ResponseBody;
      // 获取歌单里的歌曲信息
      for (const item of result.data) {
        // 获取单里的歌曲
        const resultSong = (await HttpManager.getSongOfId(item.songId)) as ResponseBody;
        currentSongList.value.push(resultSong.data[0]);
        currentSongList1.value.push(resultSong.data[0]);
      }
    }
    // 获取评分
    async function getRank(id) {
      const result = (await HttpManager.getRankOfSongListId(id)) as ResponseBody;
      nowRank.value = result.data / 2;
    }
    async function getUserRank(userId, songListId) {
      const result = (await HttpManager.getUserRank(userId, songListId)) as ResponseBody;
      if(result.data.data==-1){
        disabledRank.value = false;  // 允许用户评分
        assistText.value = "请为该歌单评分"; // 提示用户评分
      }
      else{
        nowScore.value = result.data.data / 2;
        disabledRank.value = true;
        assistText.value = "已评价";
      }
    }
    // 提交评分
    async function pushValue() {
      if (disabledRank.value || !checkStatus()) return;

      const songListId = nowSongListId.value;
      var consumerId = nowUserId.value;
      const score = nowScore.value*2;
      try {
        const result = (await HttpManager.setRank({songListId,consumerId,score})) as ResponseBody;
        (proxy as any).$message({
          message: result.message,
          type: result.type,
        });

        if (result.success) {
          getRank(nowSongListId.value);
          disabledRank.value = true;
          assistText.value = "已评价";
        }
      } catch (error) {
        console.error(error);
      }
    }

    getUserRank(nowUserId.value, nowSongListId.value);
    getRank(nowSongListId.value); // 获取评分
    getSongId(nowSongListId.value); // 获取歌单里面的歌曲ID

    return {
      songDetails,
      rank: nowRank,
      score: nowScore,
      disabledRank,
      assistText,
      currentSongList,
      currentSongList1,
      songListId: nowSongListId,
      attachImageUrl: HttpManager.attachImageUrl,
      pushValue,
      changeCollection,
      isCollection,
    };
  },
  data()
  {
    return {
      iconList: {
        dislike: Icon.Dislike,
        like: Icon.Like,
      },
    }
  }
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

.album-slide {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 20px;

  .album-img {
    height: 250px;
    width: 250px;
    border-radius: 10%;
  }

  .album-info {
    width: 70%;
    padding-top: 2rem;
  }
}

.album-main {
  h1 {
    font-size: 22px;
  }

  p {
    color: rgba(0, 0, 0, 0.5);
    margin: 10px 0 20px 0px;
  }
  /*歌单打分*/
  .album-score {
    display: flex;
    align-items: center;
    margin: 1vw;
    h3 {
      margin: 10px 0;
    }
    span {
      font-size: 60px;
    }

    & > span {
      margin-right: 100px; /* 设置span与第二个div之间的间隔 */
    }

    & > div:last-child {
      margin-left: 600px;
    }
  }
  .song-ctr {
      position: relative;
      margin: auto;
      flex-wrap: nowrap;

      svg {
        width: 5rem;
        cursor: pointer;
      }
    }
    .song-edit {
      width: 30%;
      justify-content: flex-end;
    }    
    
  .album-body {
    margin: 20px 0 20px 0px;
  }
}

@media screen and (min-width: $sm) {
  .album-slide {
    position: fixed;
    width: 400px;
  }
  .album-main {
    min-width: 600px;
    padding-right: 10vw;
    margin-left: 400px;
  }
}

@media screen and (max-width: $sm) {
  .album-slide {
    display: none;
  }
}
</style>

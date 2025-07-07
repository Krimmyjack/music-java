<template>
  <div class="yin-header">
    <!--图标-->
    <div class="header-logo" @click="goPage()">
      <yin-icon :icon="iconList.ERJI"></yin-icon>
      <span>{{ musicName }}</span>
    </div>
    <yin-header-nav class="yin-header-nav" :styleList="headerNavList" :activeName="activeNavName" @click="goPage"></yin-header-nav>
    <!--搜索框-->
    <div class="header-search">
      <el-input
        placeholder="搜索"
        :prefix-icon="Search"
        v-model="keywords"
        @keyup.enter="goSearch"
        @focus="showDropdown = true"
        
      />
      <!-- 下拉框 -->
        <div v-if="showDropdown" class="search-dropdown" ref="dropdownRef">
            <template v-if="searchResults.length">
              <el-table highlight-current-row :data="dataList" @row-click="handleClick" style="width: 100%">
                <el-table-column prop="songName" label="歌曲名" />
                <el-table-column prop="singerName" label="歌手" />
              </el-table>
            </template>
            <p v-else class="no-results">未找到相关歌曲</p>
          </div>
    </div>

    <!--设置-->
    <yin-header-nav v-if="!token" :styleList="signList" :activeName="activeNavName" @click="goPage"></yin-header-nav>
    <el-dropdown class="user-wrap" v-if="token" trigger="click">
      <el-image class="user" fit="contain" :src="attachImageUrl(userPic)" />
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item v-for="(item, index) in menuList" :key="index" @click.stop="goMenuList(item.path)">{{ item.name }}</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, getCurrentInstance, computed, reactive, watch ,onMounted, onUnmounted} from "vue";
import { Search } from "@element-plus/icons-vue";
import { useStore } from "vuex";
import YinIcon from "./YinIcon.vue";
import YinHeaderNav from "./YinHeaderNav.vue";
import mixin from "@/mixins/mixin";
import { HEADERNAVLIST, SIGNLIST, MENULIST, Icon, MUSICNAME, RouterName, NavName } from "@/enums";
import { HttpManager } from "@/api";

export default defineComponent({
  components: {
    YinIcon,
    YinHeaderNav,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();
    const {getSongTitle, getSingerName, playMusic,  changeIndex, routerManager } = mixin();

    const musicName = ref(MUSICNAME);
    const headerNavList = ref(HEADERNAVLIST); // 左侧导航栏
    const signList = ref(SIGNLIST); // 右侧导航栏
    const menuList = ref(MENULIST); // 用户下拉菜单项
    const iconList = reactive({
      ERJI: Icon.ERJI,
    });
    const keywords = ref("");
    const searchResults = ref([]);
    const showDropdown = ref(false);
    const activeNavName = computed(() => store.getters.activeNavName);
    const userPic = computed(() => store.getters.userPic);
    const token = computed(() => store.getters.token);
    const songIdVO = computed(() => store.getters.songId);
    const songId = songIdVO.value;
    const songUrl = computed(() => store.getters.songUrl);
    const singerName = computed(() => store.getters.singerName);
    const songTitle = computed(() => store.getters.songTitle);
    const dataList = computed(() => {
      const list = [];
      searchResults.value.forEach((item: any, index) => {
        item["songName"] = getSongTitle(item.name);
        item["singerName"] = getSingerName(item.name);
        item["index"] = index;
        list.push(item);
      });
      return list;
    });

    const dropdownRef = ref(null);
    const handleClickOutside = (event) => {
      if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
        showDropdown.value = false; // 隐藏下拉框
      }
    };
     // 绑定全局点击事件
     onMounted(() => {
      document.addEventListener("click", handleClickOutside);
    });

    // 卸载全局点击事件
    onUnmounted(() => {
      document.removeEventListener("click", handleClickOutside);
    });


    watch(keywords, async (value) => {
      if (value.trim() === "") {
        searchResults.value = [];
        showDropdown.value = false;
        return;
      }
      await fetchSearchResults(value);
    });

    async function fetchSearchResults(query: string) {
      const result = (await HttpManager.getSongOfSingerName(query)) as ResponseBody; // 假设 HttpManager.searchSong 是搜索接口
      if (result.data) {
        searchResults.value = result.data
        showDropdown.value = true;
      } else {
        searchResults.value = [];
      }
    }

    function handleClick(row) {
      showDropdown.value = false;
      playMusic({
        id: row.id,
        url: row.url,
        pic: row.pic,
        index: row.index,
        name: row.name,
        lyric: row.lyric,
        currentSongList: searchResults.value,
      });
      routerManager(RouterName.Lyric, { path: `${RouterName.Lyric}/${row.id}` });
    }


    function goPage(path, name) {
      if (!path && !name) {
        changeIndex(NavName.Home);
        routerManager(RouterName.Home, { path: RouterName.Home });
      } else {
        changeIndex(name);
        routerManager(path, { path });
      }
    }

    function goMenuList(path) {
      if (path == RouterName.SignOut) {
        proxy.$store.commit("setToken", false);
        changeIndex(NavName.Home);
        routerManager(RouterName.Home, { path: RouterName.Home });
      } else {
        routerManager(path, { path });
      }
    }
    function goSearch() {
      showDropdown.value = false;
      if (keywords.value !== "") {
        proxy.$store.commit("setSearchWord", keywords.value);
        routerManager(RouterName.Search, { path: RouterName.Search, query: { keywords: keywords.value } });
      } else {
        (proxy as any).$message({
          message: "搜索内容不能为空",
          type: "error",
        });
      }
    }

    return {
      showDropdown,
      dropdownRef,
      dataList,
      searchResults,
      handleClick,
      musicName,
      headerNavList,
      signList,
      menuList,
      iconList,
      keywords,
      activeNavName,
      userPic,
      token,
      Search,
      goPage,
      goMenuList,
      goSearch,
      attachImageUrl: HttpManager.attachImageUrl,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";
@import "@/assets/css/global.scss";
@import "element-plus/dist/index.css";

@media screen and (min-width: $sm) {
  .header-logo {
    margin: 0 1rem;
  }
}

@media screen and (max-width: $sm) {
  .header-logo {
    margin: 0 1rem;
    span {
      display: none;
    }
  }
  .header-search {
    display: none;
  }
}

.yin-header {
  position: fixed;
  width: 100%;
  height: $header-height;
  line-height: $header-height;
  padding: $header-padding;
  margin: $header-margin;
  background-color: $theme-header-color;
  box-shadow: $box-shadow;
  box-sizing: border-box;
  z-index: 100;
  display: flex;
  white-space: nowrap;
  flex-wrap: nowrap;
}

/* LOGO */
.header-logo {
  font-size: $font-size-logo;
  font-weight: bold;
  cursor: pointer;
  .icon {
    @include icon(1.9rem, $color-black);
    vertical-align: middle;
  }
  span {
    margin-left: 1rem;
  }
}

.yin-header-nav {
  flex: 1;
}


/*用户*/
.user-wrap {
  position: relative;
  display: flex;
  align-items: center;

  .user {
    width: $header-user-width;
    height: $header-user-width;
    border-radius: $header-user-radius;
    margin-right: $header-user-margin;
    cursor: pointer;
  }
}


/* 搜索输入框 */
.header-search {
  position: relative;
  margin: 0 20px;
  width: 100%;
}

  /* 下拉框样式 */
  .search-dropdown {
  position: absolute;
  top: calc(100% + 4px);
  left: 0;
  width: 100%;
  max-height: 200px;
  overflow-y: auto;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  z-index: 200;
}
.el-table {
  font-size: 20px;
  line-height: 30px;
}
.no-results {
  padding: 25px;
  text-align: center;
  color: #999;
  height:80;
}

</style>

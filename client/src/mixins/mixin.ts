import { getCurrentInstance, computed } from "vue"; //获取当前实例和计算属性
import { useStore } from "vuex";
import { LocationQueryRaw } from "vue-router"; //路由查询参数
import { RouterName } from "@/enums"; //定义路由名称
import { HttpManager } from "@/api";
import axios from 'axios'
interface routerOptions { //定义了可选的路由路径和查询参数
  path?: string;
  query?: LocationQueryRaw;
}

export default function () {
  const { proxy } = getCurrentInstance(); //获取当前实例

  const store = useStore();
  const token = computed(() => store.getters.token); //计算属性获取当前用户的登陆状态

  function getUserSex(sex) { //获取用户性别
    if (sex === 0) {
      return "女";
    } else if (sex === 1) {
      return "男";
    }
  }

  // 获取歌曲名
  function getSongTitle(str) {
    return str.split("-")[1];
  }

  // 获取歌手名
  function getSingerName(str) {
    return str.split("-")[0];
  }

  // 判断登录状态
  function checkStatus(status?: boolean) {
    if (!token.value) {
      if (status !== false)
        (proxy as any).$message({
          message: "请先登录",
          type: "warning",
        });
      return false;
    }
    return true;
  }

  // 播放
  function playMusic({ id, url, pic, index, name, lyric, currentSongList }) {
    const songTitle = getSongTitle(name);
    const singerName = getSingerName(name);
    proxy.$store.commit("setIsPlay", false);
    proxy.$store.dispatch("playMusic", {
      id,
      url,
      pic,
      index,
      songTitle,
      singerName,
      lyric,
      currentSongList,
    });
  }

  function getFileName(path) {
    const parts = path.split('/');
    return parts[parts.length - 1];
  }

  // 下载使用 axios 发起下载请求
  async function downloadMusic({ songUrl, songName }) {
    if (!songUrl) {
      (proxy as any).$message({
        message: "下载链接为空！",
        type: "error",
      });
      console.error("下载链接为空！");
      return;
    }
    const fileName = getFileName(songUrl);
    const downUrl="/download/"+fileName
   // const result = (await HttpManager.downloadMusic(downUrl)) as ResponseBody;
   // console.log(result.data);

    // const eleLink = document.createElement("a");
    // eleLink.download = `${fileName}`;
    // eleLink.style.display = "none";
    // // 字符内容转变成 blob 地址
    // const blob = new Blob([result.data]);
    // console.log(blob)
    // eleLink.href = URL.createObjectURL(blob);
    // document.body.appendChild(eleLink); // 触发点击
    // eleLink.click();
    // document.body.removeChild(eleLink); // 移除

      const response = await axios.get(downUrl, {
        responseType: 'blob', // 指定响应类型为二进制数据
      });
      
      // 创建一个Blob URL来下载文件
      const blob = new Blob([response.data], { type: 'application/octet-stream' });
      const url = window.URL.createObjectURL(blob);

      // 创建一个隐藏的<a>标签来下载文件
      const link = document.createElement('a');
      link.href = url;
      link.download = fileName;
      document.body.appendChild(link);
      link.click();

      // 释放URL对象
      window.URL.revokeObjectURL(url);
      document.body.removeChild(link);

  }

  // 导航索引
  function changeIndex(value) {
    proxy.$store.commit("setActiveNavName", value);
  }
  // 路由管理
  function routerManager(routerName: string | number, options: routerOptions) {
    switch (routerName) {
      case RouterName.Search:
        proxy.$router.push({ path: options.path, query: options.query });
        break;
      case RouterName.Home:
      case RouterName.SongSheet:
      case RouterName.SongSheetDetail:
      case RouterName.Singer:
      case RouterName.SingerDetail:
      case RouterName.Personal:
      case RouterName.PersonalData:
      case RouterName.Setting:
      case RouterName.SignIn:
      case RouterName.SignUp:
      case RouterName.SignOut:
      case RouterName.Lyric:
      case RouterName.Error:
      default:
        proxy.$router.push({ path: options.path });
        break;
    }
  }

  function goBack(step = -1) {
    proxy.$router.go(step);
  }

  return {
    getUserSex,
    getSongTitle,
    getSingerName,
    changeIndex,
    checkStatus,
    playMusic,
    routerManager,
    goBack,
    downloadMusic,
  };
}

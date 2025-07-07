//这里导入了多个文件中的内容，包括区域信息、图标、音乐名称、导航名称、菜单等
//这种组织方式有助于保持代码的整洁和模块化，并便于在其他部分进行导入使用
//这样做的结果是其他模块可以直接通过导入 index 模块来获取所有这些常量和功能，而无需单独导入每个模块

import { AREA } from "./area";
import { Icon } from "./icon";
import { MUSICNAME } from "./music-name";
import { NavName, HEADERNAVLIST, SIGNLIST, MENULIST } from "./nav";
import { singerStyle } from "./singer";
import { SONGSTYLE } from "./songList";
import { RouterName } from "./router-name";
import { validatePassword, SignInRules, SignUpRules } from "./validate";

export {
  AREA,
  Icon,
  MUSICNAME,
  NavName,
  HEADERNAVLIST,
  RouterName,
  SIGNLIST,
  MENULIST,
  singerStyle,
  SONGSTYLE,
  validatePassword,
  SignInRules,
  SignUpRules,
};

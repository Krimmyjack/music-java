package com.example.yin.controller;

import com.example.yin.common.R;
import com.example.yin.model.request.CollectRequest;
import com.example.yin.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CollectController {

    @Autowired
    private CollectService collectService;

    // 添加收藏的歌曲
    //前台界面逻辑
    @PostMapping("/collection/add")
    public R addCollection(@RequestBody CollectRequest addCollectRequest) {
        return collectService.addCollection(addCollectRequest);
    }

    // 取消收藏的歌曲
    @DeleteMapping("/collection/delete")
    public R deleteCollection(@RequestParam Integer userId, @RequestParam Integer songId) {
        return collectService.deleteCollect(userId, songId);
    }

    // 是否收藏歌曲
    @PostMapping("/collection/status")
    public R isCollection(@RequestBody CollectRequest isCollectRequest) {
        return collectService.existSongId(isCollectRequest);
    }

    // 返回的指定用户 ID 收藏的列表
    @GetMapping("/collection/detail")
    public R collectionOfUser(@RequestParam Integer userId) {
        return collectService.collectionOfUser(userId);
    }

    // 删除收藏的歌单
    @DeleteMapping("/collection/deletelist")
    public R deleteCollectionList(@RequestParam Integer userId, @RequestParam Integer songListId) {
        return collectService.deleteCollectList(userId, songListId);
    }

    // 是否收藏歌单
    @PostMapping("/collection/statuslist")
    public R isCollectionList(@RequestBody CollectRequest isCollectRequest) {
        return collectService.existSongListId(isCollectRequest);
    }

    // 返回的指定用户 ID 收藏的歌单列表
    @GetMapping("/collection/detaillist")
    public R collectionOfUserList(@RequestParam Integer userId) {
        return collectService.collectionOfUserList(userId);
    }
}

Array.prototype.contains = function(item){
  return RegExp("\\b"+item+"\\b").test(this);
};

/**
 * 点赞或取消点赞
 * @param replyId 对应的回复 id
 * @param element 点击的元素,换背景 id
 */
function like(postUser, replyId){
    //是不是本人
    if(postUser == window.nutz.loginName()){
        window.nutz.toast("不能帮自己点赞");
    }else{
        window.nutz.like(replyId);
    }
}
//点击like的回调
function likeCallback(result, replyId){
    if(result){
        var checked = $("#chk_" + replyId);
        var img = $("#like_img_" + replyId);
        var like = $("#like_" + replyId);
        var likeNum = parseInt(like.html());

        //点击之前没有like
        if(checked.val() == 0){
            img.attr("src", "checkbox_good_check.png");
            like.html(likeNum + 1);
            checked.val(1);
        }else{
            img.attr("src", "checkbox_good_normal.png");
            like.html(likeNum - 1);
            checked.val(0);
        }
    }
}

/**
 * 回复某人的评论
 * @param authorName 评论作者用户名
 * @param replyId 对应的回复 id
 */
function reply(authorName, replyId){
    window.nutz.replyComment(authorName, replyId);
}

function topicType(isTop, origin){
    if(isTop){
        return "置顶";
    }else if(origin == "duanzi"){
        return "段子";
    }else if(origin == "news"){
        return "新鲜事";
    }else if(origin == "good"){
        return "精华";
    }else{
        return "其他"
    }
}
layui.use(['form','jquery','jquery_cookie','layer'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);


    form.on("submit(login)", function(data) {
// 获取表单元素的值 （用户名 + 密码）
        //layer.msg(JSON.stringify(data.field));
   /*     layer.msg(data.field.username)
        return false*/
        var fieldData=data.field;
        if(fieldData.username=='undefined' || fieldData.username==''){
            layer.msg("用户名不能为空");
            return;
        }
        if(fieldData.password=='undefined' || fieldData.password==''){
            layer.msg("密码不能为空");
            return;
        }
        $.ajax({
            type:"post",
            url:ctx+"/user/login",
            data:{
                "userName":fieldData.username,
                "userPwd":fieldData.password
            },
            dataType:"json",
            success:function (result){
                if(result.code==200){
                    //layer.msg("登录成功了",{icon:5});
                    layer.msg("登录成功了",function (){
                        $.cookie("userIdStr",result.result.userIdStr);
                        $.cookie("userName",result.result.userName);
                        $.cookie("trueName",result.result.trueName);
                        // 如果用户选择"记住我"，则设置cookie的有效期为7天
                        if($("input[type='checkbox']").is(":checked")) {
                            $.cookie("userIdStr", result.result.userIdStr, {expires: 7});
                            $.cookie("userName", result.result.userName, {expires: 7});
                            $.cookie("trueName", result.result.trueName, {expires: 7});
                        }
                        window.location.href=ctx+"/main";
                    });


                }else {
                    layer.msg(result.msg);
                }
            }
        });
        return false;  //取消默认行为
    });
    
});
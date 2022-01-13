layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;
    /**
     * 监听submit事件
     * 实现营销机会的添加与更新
     */
    form.on("submit(addOrUpdateSaleChance)", function (obj) {
//判断是添加还是修改，id==null，添加   id==null 修改
        var url = ctx + "/sale_chance/save";
        //判断当前页面的隐藏域有没有id，有id做修改，无id做添加
        if ($("input[name=id]").val()){
            url = ctx + "/sale_chance/update"
        }
        $.ajax({
            type: "post",
            url: url,
            data: obj.field,
            dataType: "json",
            success: function (obj) {
                if (obj.code == 200) {
                    layer.msg(obj.msg);
                    //刷新页面
                    window.parent.location.reload();
                } else {
                    layer.msg(obj.msg, {icon: 6})
                }
            }
        });
        return false; // 阻止表单提交
    });
    $("#closeBtn").click(function (){
        //获取当前弹出层的索引
        var idx =parent.layer.getFrameIndex(window.name);
        //根据索引关闭
        parent.layer.close(idx);
    });
    var assignMan=$("input[name='man']").val();
    $.ajax({
        type:"post",
        url:ctx+"/user/sales",
        dataType: "json",
        success:function (data){
            for(var x in data){
                if(data[x].id==assignMan){
                    $("#assignMan").append("<option selected value='"+data[x].id+"'>"+data[x].uname+"</option>");
                }else{
                    $("#assignMan").append("<option value='"+data[x].id+"'>"+data[x].uname+"</option>");
                }

            }
            layui.form.render("select"); //重新渲染
        }
    });
});


var zTreeObj;
$(function () {
    loadModuleInfo();
});
function loadModuleInfo() {
    $.ajax({
        type:"post",
        url:ctx+"/module/findModules",
        dataType:"json",
        success:function (data) {
// zTree 的参数配置，深入使用请参考 API 文档（setting 配置详解）
            var setting = {
                data: {
                    simpleData: {
                        enable: true
                    }
                },
                view:{
                    showLine: false
// showIcon: false
                },
                check: {
                    enable: true,
                   chkboxType: { "Y": "ps", "N": "ps" }
                }
            };
            var zNodes =data;
           /* $(document).ready(function (){
                $.fn.zTree.init($("#test1"), setting, zNodes);
            });*/
            zTreeObj=$.fn.zTree.init($("#test1"), setting, zNodes);
        }
    });
}
function zTreeOnCheck(event, treeId, treeNode) {
    var nodes= zTreeObj.getCheckedNodes(true);
    var roleId=$("#roleId").val();
    var mids="mids=";
    for(var i=0;i<nodes.length;i++){
        if(i<nodes.length-1){
            mids=mids+nodes[i].id+"&mids=";
        }else{
            mids=mids+nodes[i].id;
        }
    }
    $.ajax({
        type:"post",
        url:ctx+"/role/addGrant",
        data:mids+"&roleId="+roleId,
        dataType:"json",
        success:function (data) {
            console.log(data);
        }
    });
}
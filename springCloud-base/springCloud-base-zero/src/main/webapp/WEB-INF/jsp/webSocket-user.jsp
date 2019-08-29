<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>To ${toNickName}</title>
    <style type="text/css">
        .controller{
            width: 350px;
            margin: 50px auto 10px;
            padding: 5px 5px 8px;
            border: 1px solid;
        }
        #nickNameDisplay{
            font-size:14px;
        }
        .show{
            display:none;
        }
        #view{
            overflow: auto;
            width: 360px;
            height: 500px;
            margin: 0 auto;
            border: 1px solid;
        }
        .broad{
            position:relative;
            width: 360px;
            height: 500px;
            margin: 0 auto;
            border: 0;
        }
        #msg{
            width:120px;
        }
        .right{
            float:right;
        }
        #toScrollTop{
            position: absolute;
            display: none;
            width: 60px;
            right: 20px;
            bottom: 10px;
            font-size: 1px;
            color: blueviolet;
        }
        .showMsg{
            display: block;
            margin: 0 4px 8px;
            /* 自动换行 */
            word-wrap: break-word;
            overflow: hidden;
            font-size:14px;
        }
    </style>
</head>
<body>
    <div class="show">
        <div class="controller">
            <lable>solo 1.4</lable>&nbsp;&nbsp;
            <span id="nickNameDisplay">用户：${fromNickName}</span>
            <div class="right">
                <button id="disConnection">关闭</button>
            </div>
            <div class="show"><br/>
                <input id="msg" type="text" placeholder="请输入..." autofocus/>
                <button id="clear">清空</button>&nbsp;
                <div class="right">
                    <button id="clearScreen">清屏</button>&nbsp;
                    <button id="send">发送</button>
                </div>
            </div>
        </div>
        <div class="broad">
            <div id="view" class="show">
                <div id="toScrollTop">查看最新</div>
            </div>
        </div>
    </div>
    <div hidden>
        <span id="chatId" hidden>${chatId}</span>
        <span id="fromAccountId" hidden>${fromAccountId}</span>
        <span id="fromIp" hidden>${fromIp}</span>
        <span id="toAccountId" hidden>${toAccountId}</span>
        <span id="toIp" hidden>${toIp}</span>
    </div>
</body>
</html>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.4.3.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/sockjs.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/stomp.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/mine.js"></script>
<script type="text/javascript">
    var stompClient = null;
    var scrollFlag = true;
    $(function(){
        if(!window.WebSocket){
            alert("您的浏览器不支持websocket通讯!")
            return;
        }
        $(".controller").parent().removeClass("show");

        //初始化按钮
        transfer(true);
        //初始化 disconnection
        $("#disConnection").click(function(){
            disConnection();
        });
        //初始化 clear
        $("#clear").click(function(){
            clear();
        });
        //初始化 clear screen
        $("#clearScreen").click(function(){
            clearScreen();
        });
        //初始化 send
        $("#send").click(function(){
            sendMsg();
        });
        //Enter事件，Esc事件
        $(document).keyup(function(event){
            //Enter事件
            if(event.keyCode == 13){
                sendMsg();
            }
            //Esc事件
            if (event.keyCode == 27) {
                disConnection();
            }
        });
        //跳转到最新消息
        $("#toScrollTop").click(function(){
            $("#view").scrollTop($("#view")[0].scrollHeight);
        });
        //滚动条改变
        $("#view").scroll(function(){
            var scrollH = 1*$("#view")[0].scrollTop.toFixed(0) + 1*$("#view").height().toFixed(0);
            var scrollHeight = $("#view")[0].scrollHeight;
//            console.log("scrollH = " + scrollH);
//            console.log("scrollHeight = " + scrollHeight);
            if(scrollH >= scrollHeight) {
                scrollFlag = true;
                $("#toScrollTop").attr("style","display:none");
            }else{
                scrollFlag = false;
                $("#toScrollTop").attr("style","display:block");
            }
//            console.log("scrollFlag = " + scrollFlag);
        });

        connection();
    });

    //控制按钮是否可用
    function transfer(flag){
        $("#disConnection").attr("disabled",!flag);
        $("#send").attr("disabled",!flag);
        $("#msg").attr("disabled",!flag);
        $(".show").attr("style","display:"+(flag?"block":"none"));
    }

    //创建连接
    function connection(){
//        alert("connection");
        //建立连接对象(还未发起连接)
        var socket = new SockJS("/thread/endpointDemo");//服务器的全路径
        //获取STOMP子协议的客户端对象
        stompClient = Stomp.over(socket);
        stompClient.connect({},
            function connectCallback(frame){//连接成功，服务器返回connected帧的回调方法
                <%--$("#toScrollTop").before("<span style='text-align:center;display:block;margin:0 0 8px;color:blue;'>Chatting with ${toNickName}</span>");--%>
//                console.log("frame：" + frame);
                stompClient.subscribe("/user/" + $("#fromAccountId").text().trim() + "/queue/getMsg",
                    function(response){//url不需要写全路径
                        show(JSON.parse(response.body));
                        if(scrollFlag) //定位到滚动条最下
                            $("#view").scrollTop($("#view")[0].scrollHeight.toFixed(0));
                    },
                    function errCallback(err){
                        alert("连接失败：" + err);
                    });
        });

    }

    //关闭连接，关闭窗口
    function disConnection(){
        if(confirm("确定关闭聊天窗口吗?")){
            //断开连接
            if(stompClient != null) {
                stompClient.disconnect({},getHeaders());
            }
            transfer(false);
            clearScreen();
            $("#nickName").val("");
//        console.log("disconnected");

            //关闭窗口(关闭js打开的窗口)
            window.opener = null;
            window.open('','_self');
            window.close();
        }
    }

    //断开返回参数
    function getHeaders(){
        return {
            "sendType": "user",
            "chatId": $("#chatId").text().trim()
        }
    }

    //清空
    function clear(){
        $("#msg").val("");
    }

    //清屏
    function clearScreen(){
        scrollFlag = true;
        clear();
        $("#view").children().last().siblings().remove();
        $("#toScrollTop").attr("style","display:none");
    }

    //发送消息
    function sendMsg(){
        var msg = $("#msg").val();
        $("#msg")[0].focus();
        if(msg.trim() == "") return;
        var json = {"fromAccountId": $("#fromAccountId").text().trim(), "fromIp": $("#fromIp").text().trim(), "toAccountId": $("#toAccountId").text().trim(), "toIp": $("#toIp").text().trim(), "fromNickName": "${fromNickName}", "msg": msg};
//        alert(msg);
        stompClient.send("/webSocket/queue/sendMsg",{},JSON.stringify(json));//url不需要写全路径
        $("#msg").val("");
    }

    //显示消息
    function show(data){
        $("#toScrollTop").before("<span class='showMsg'><span style='color:blue;'>" +
                                data.nickName + " [" + data.ip + "] " + data.time
                                + "</span><br/>&nbsp;" + data.msg + "</span>");
    }
</script>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>WebSocket-topic</title>
    <style type="text/css">
        #nickName{
            width:90px;
        }
        #nickNameDisplay{
            font-size:14px;
        }
        .controller{
            width: 350px;
            margin: 50px auto 10px;
            padding: 5px 5px 8px;
            border: 1px solid;
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
<br>
    <div class="show">
        <div class="controller">
            <lable>wechat 1.4</lable>&nbsp;
            <span id="nickNameDisplay"></span>
            <input id="nickName" type="text" placeholder="请输入昵称" autofocus/>
            <div class="right">
                <button id="connection">连接</button>&nbsp;
                <button id="disConnection">断开</button>
            </div>
            <div class="show"><br/>
                <input id="msg" type="text" placeholder="请输入..."/>
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
            <span class="tips" style="float:right;font-size:10px;color:red;">PS：双击可以拉取私聊窗口!</span>
        </div>
    </div>
    <div hidden>
        <span id="accountId" hidden>${sessionScope.accountId}</span>
        <span id="ip" hidden>${sessionScope.ip}</span>
    </div>
</body>
</html>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.4.3.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/sockjs.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/stomp.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/mine.js"></script>
<script type="text/javascript">
    /*************** WebSocket-Topic START ***************/
    var stompClient = null;
    var scrollFlag = true;
    var flag = false;
    $(function(){
        if(!window.WebSocket){
            alert("您的浏览器不支持websocket通讯!")
            return;
        }
        $(".controller").parent().removeClass("show");

        //初始化按钮
        transfer();
        //初始化 connection
        $("#connection").click(function(){
            connection();
        });
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
                if(!flag)
                    connection();
                else
                    sendMsg();
            }
            //Esc事件
            if (event.keyCode == 27) {
                if (!flag) {
                    if (confirm("确定退出聊天室吗?")) {
                        //断开连接
                        disConnection();
                        //关闭窗口(chrome关闭非js打开的窗口)
                        window.location.href = "about:blank";
                        window.close();
                    }
                } else {
                    if (confirm("确定关闭连接吗?")) {
                        //断开连接
                        disConnection();
                    }
                }
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
        //tips渐隐
        setTimeout(function () {
            $(".tips").hide(3000);
        }, 3000);
    });

    //控制按钮是否可用
    function transfer(){
        $("#nickName").attr("style","display:"+(!flag?"inline-block":"none"));
        $("#connection").attr("disabled",flag);
        $("#disConnection").attr("disabled",!flag);
        $("#send").attr("disabled",!flag);
        $("#msg").attr("disabled",!flag);
        $(".show").attr("style","display:"+(flag?"block":"none"));
        if(flag)
            $("#msg")[0].focus();

    }

    //创建连接
    function connection(){
//        alert("connection");
        if($("#nickName").val().trim().length>6){
            alert("昵称太长啦...");
            return;
        }
        $("#nickName").attr("style","display:none");
        $("#nickNameDisplay").text("用户：" + ($("#nickName").val().trim()==""?"Anonymous":$("#nickName").val().trim()));

        flag = true;
        /*************** WebSocket-Topic connection START ***************/
        //建立连接对象(还未发起连接)
        var socket = new SockJS("/thread/endpointDemo");//服务器的全路径
        //获取STOMP子协议的客户端对象
        stompClient = Stomp.over(socket);
        stompClient.connect({},
            function connectCallback(frame) {//连接成功，服务器返回connected帧的回调方法
                $("#toScrollTop").before("<span style='text-align:center;display:block;margin:0 0 8px;color:blue;'>welcome to chat-room !</span>");
                transfer();
//                console.log("frame：" + frame);
                stompClient.subscribe("/topic/getMsg", function (response) {//url不需要写全路径
                    show(JSON.parse(response.body));
                    if (scrollFlag) //定位到滚动条最下
                        $("#view").scrollTop($("#view")[0].scrollHeight.toFixed(0));
                },
                function errCallback(err) {
                    alert("连接失败：" + err);
                    flag = false;
                });
        });
        /*************** WebSocket-Topic connection END ***************/

    }

    //关闭连接
    function disConnection(){
//        alert("disConnection");
        if(stompClient != null) {
            stompClient.disconnect({},getHeaders());
        }
        flag = false;
        transfer();
        clearScreen();
        $("#nickName").val("");
        $("#nickNameDisplay").text("");
        $("#nickName")[0].focus();
//        console.log("disconnected");
    }

    //断开返回参数
    function getHeaders(){
        return {
            "sendType": "topic"
        }
    }

    //清空
    function clear(){
        $("#msg").val("");
        $("#msg")[0].focus();
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
        var json = {"fromAccountId": "${sessionScope.accountId}", "fromIp": "${sessionScope.ip}", "fromNickName": $("#nickName").val(), "msg": msg};
//        alert(msg);
        stompClient.send("/webSocket/topic/sendMsg",{},JSON.stringify(json));//url不需要写全路径
        $("#msg").val("");
    }

    //显示消息
    function show(data){
        $("#toScrollTop").before("<span class='showMsg' ondblclick=\"solo('" + data.accountId + "','" + data.ip + "','" + data.nickName + "');\">" +
                                    "<span style='color:blue;'>" + data.nickName + " [ " + data.ip + " ] " + data.time + "</span><br/>&nbsp;" +
                                    data.msg +
                                "</span>");
    }
    /*************** WebSocket-Topic END ***************/


    /*************** WebSocket-User START ***************/
    function solo(accountId,ip,nickName){
        var json = {
            'fromAccountId': $("#accountId").text().trim(),
            'fromIp': $("#ip").text().trim(),
            'fromNickName': $("#nickName").val(),
            'toAccountId': accountId,
            'toIp': ip,
            'toNickName': nickName
        };
//        console.log("json=" + JSON.stringify(json));

        $.ajax({
            url: "/thread/webSocket/user/checkUser",
            type: "POST",
            data: Base64.encode(JSON.stringify(json)),
            contentType:"text/plain",
            dataType: "json",
            success: function (ret) {
//                console.log(ret);
                if ("success" == ret.code) {
                    json.chatId = ret.msg;
//                    console.log("json=" + JSON.stringify(json));
                    window.open("/thread/webSocket/user?t=" + Base64.encode(JSON.stringify(json)));
                } else {
                    alert(ret.msg);
                }
            }
        });
    }
    /*************** WebSocket-User END ***************/

</script>
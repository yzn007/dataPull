<% String path = request.getContextPath();
    String ppath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path;%>


<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Insert title here</title>

    <script src="../jquery-3.4.1/jquery-3.4.1.js"></script>
    <script language="JavaScript">
        debugger;
        var IM =(function(jQuery){
            var $ =jQuery;
            return {
                poll : function() {
                    $.ajax({
                        url:"/im/poll",
                        type: "POST",
                        success: function (data) {
                            console.log(JSON.stringify(data));
                            IM.poll();
                        },
                        error: function (err) {
                            console.log(JSON.stringify(err));
                            setTimeout(IM.poll,2000);
                        }
                    });
                },

                subscribe : function(channel) {
                    $.post('/im/subscribe',{
                        channel:channel
                    },function (e) {
                        console.log(e);
                    })
                },
                unsubscribe : function (channelName) {
                    var channel = channelName?{channel:channelName}:null;
                    $.post('/im/unsubscribe',channel,function (e) {
                        console.log(e);
                    })
                },
                send : function(channel,text) {
                    $.ajax({
                        url:"/im/emit",
                        type: "POST",
                        data: JSON.stringify({
                            channel:channel,
                            message:{
                                text:text
                            }
                        }),
                        success: function (e) {
                            console.log(e);
                        },
                        dataType: "json",
                        contentType: "application/json"
                    });
                }
            };
        })(jQuery);

    </script>
</head>
<body>

<div id="logs"></div>
<div id="state"></div>
</body>
</html>

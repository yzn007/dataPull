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
        <%--$(function () {--%>

            <%--window.setInterval(function () {--%>

                <%--$.get("<%=basePath%>/httpService/sendGetData?RayData=Rout",--%>
                    <%--{"timed": new Date().getTime()},--%>
                    <%--function (data) {--%>
                        <%--$("#logs").append("[data: " + data + " ]<br/>");--%>
                    <%--});--%>
            <%--}, 3000);--%>

        <%--});--%>
        <%-- 常连接 --%>
        $(function () {

            (function longPolling() {

                $.ajax({
                    url: "${pageContext.request.contextPath}/person/all",
                    data: {"timed": new Date().getTime()},
                    dataType: "text",
                    timeout: 5000,
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        $("#state").append("[state: " + textStatus + ", error: " + errorThrown + " ]<br/>");
                        if (textStatus == "timeout") { // 请求超时
                            longPolling(); // 递归调用

                            // 其他错误，如网络错误等
                        } else {
                            longPolling();
                        }
                    },
                    success: function (data, textStatus) {
                        $("#state").append("[state: " + textStatus + ", data: { " + data + "} ]<br/>");

                        if (textStatus == "success") { // 请求成功
                            longPolling();
                        }
                    }
                });
            })();

        });
    </script>
</head>
<body>

<div id="logs"></div>
<div id="state"></div>
</body>
</html>

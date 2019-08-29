<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>SpringBoot</title>
</head>
<body>
    <div>${requestScope.get("name")}</div>
    <div>This is a jsp.</div>
    <img src="${pageContext.request.contextPath}/pic/Z.jpg"/>
</body>
</html>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.4.3.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/mime.js"></script>
<script type="text/javascript">
    $(function(){

    });
</script>
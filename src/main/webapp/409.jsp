<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
    <title>Ошибка 409</title>
</head>
<body>
    Произошла ошибка: ${requestScope['javax.servlet.error.message']}
    <br><br>
    <a href="./">Вернуться</a>
</body>
</html>

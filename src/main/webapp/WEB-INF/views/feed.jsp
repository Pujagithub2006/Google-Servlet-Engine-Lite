<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="org.gse_lite.model.Post" %>
<%
List<Post> posts = (List<Post>) request.getAttribute("posts");
%>

<!DOCTYPE html>

<html>

<head>

<title>GSE Lite Feed</title>

<style>

body{

font-family:Arial;

background:#f5f5f5;

margin:0;

padding:40px;

}

.feed{

max-width:700px;

margin:auto;

}

.card{

background:white;

padding:20px;

margin-bottom:20px;

border-radius:8px;

box-shadow:0 2px 8px rgba(0,0,0,.08);

}

.author{

font-weight:bold;

}

.time{

color:gray;

font-size:12px;

}

.likes{

margin-top:10px;

color:#1976d2;

}

</style>

</head>

<body>

<div class="feed">

<%
if(posts == null || posts.isEmpty()) {
%>

<p>No posts available!</p>

<%
}

else {
    for(Post post:posts) {
%>

<div class="card">

<div class="author">

<%=post.getAuthor()%>

</div>

<p>

<%=post.getContent()%>

</p>

<div class="time">

<%=post.getTimestamp()%>

</div>

<div class="likes">

&#10084; <%=post.getLikesCount()%>

</div>

</div>

<%
    }
}

%>


</div>

</body>

</html>
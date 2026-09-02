<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="org.gse_lite.model.Post" %>

<%
List<Post> posts = (List<Post>) request.getAttribute("posts");
%>

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
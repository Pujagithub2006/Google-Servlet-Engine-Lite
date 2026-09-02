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
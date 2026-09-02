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

<div id="feed-container">
    <jsp:include page="partials/post_item.jsp"/>
</div>

<div id="scroll-trigger"></div>

<p id="end-message" style="display:none; text-align:center; color:gray; margin:20px;">
    No more posts to load.
</p>

</div>

<script>

const contextPath = "${pageContext.request.contextPath}";
let currentPage = 1;

const feedContainer = document.getElementById("feed-container");
const scrollTrigger = document.getElementById("scroll-trigger");

const observer = new IntersectionObserver(loadMore);
observer.observe(scrollTrigger);

function loadMore(entries) {
    if(!entries[0].isIntersecting) return;
    currentPage++;

    fetch(
        contextPath + "/feed?page=" + currentPage,
        {
            headers:{
                "X-Requested-With":"XMLHttpRequest"
            }
        }
    )

    .then(response=>response.text())
    .then(html=>{
        if(html.trim() === "") {
            document.getElementById("end-message").style.display = "block";

            observer.disconnect();
            return;
        }

        feedContainer.insertAdjacentHTML(
            "beforeend",
            html
        );

        bindEvents();

        history.pushState(
            {},
            "",
            contextPath + "/feed?page=" + currentPage
        );
    })
    .catch(error => {
        console.error("Failed to load more posts:", error);
    });
}

function bindEvents() {
    // Future implementation
}

</script>

</body>

</html>
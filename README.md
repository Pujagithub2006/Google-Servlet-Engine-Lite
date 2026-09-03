# GSE-Lite: High-Throughput Activity Feed Engine

> **Zero-Framework Server-Side Rendering (SSR) & Client Hydration Architecture**  
> *Inspired by Google's Servlet Engine (OpenGSE) and the Google+ Infrastructure Architecture.*

[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)](https://www.oracle.com/java/)
[![Architecture](https://img.shields.io/badge/Architecture-First--Principles-blue.svg)](#architecture--system-design)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A small activity-feed web app built directly on Java Servlets and JSP — no Spring, no template engine, no build magic. The point of this project is to understand what frameworks like Spring MVC are actually doing under the hood, by writing a stripped-down version of the same pieces: a front controller, annotation-based routing, reflection-based dispatch, and a couple of servlet filters, all wired by hand.

The visible feature is a paginated feed of posts that loads more content as you scroll, similar to a typical social feed, rendered server-side and hydrated with plain JavaScript.

## Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Tech stack](#tech-stack)
- [Project structure](#project-structure)
- [Getting started](#getting-started)
- [Building and running locally](#building-and-running-locally)
- [Screenshots](#screenshots)
- [Features](#features)
- [Future enhancements](#future-enhancements)
- [Contributing](#contributing)
- [License](#license)

## Overview

`gse-lite` is a Jakarta EE web application packaged as a WAR. Instead of pulling in a full MVC framework, it implements a minimal version of one:

- Controllers are plain classes annotated with a custom `@Controller`, and their methods are mapped to URLs with a custom `@GetMapping`.
- A single `DispatcherServlet` scans for these controllers on startup, builds a route table, and forwards every incoming request to the right handler through reflection.
- Below the web layer sits a small service/repository setup that stores posts in memory.

There's no database, no security layer, and no templating engine beyond raw JSP — this is intentionally close to the metal.

## Architecture

Every request goes through the same pipeline:

```
Client
  │
  ▼
RateLimiterFilter        (blocks a client past 5 requests / 10s)
  │
  ▼
PerformanceFilter        (times the request, adds X-Response-Time-Ns)
  │
  ▼
DispatcherServlet        (front controller — resolves URL to a handler)
  │
  ▼
HandlerMethod            (invokes the matching @GetMapping method via reflection)
  │
  ▼
Controller  →  Service  →  Repository   (business logic + in-memory data)
  │
  ▼
JSP view (forwarded from /WEB-INF/views/)
  │
  ▼
Response
```

A few details worth knowing if you're reading the code:

- **Routing is built once, at startup.** `ClassScanner` walks the `org.gse_lite.controller` package, finds classes annotated `@Controller`, and registers every `@GetMapping` method it finds into a `Map<String, HandlerMethod>` inside `DispatcherServlet`. There's no dynamic re-scanning — adding a controller means restarting the app.
- **Only GET is wired up.** `@GetMapping` and `doGet` are implemented; there's no `@PostMapping` yet, even though `PostService.createPost(...)` already exists and is ready to be called from one.
- **Views are resolved by convention.** A handler method returns a `String` (e.g. `"feed"` or `"partials/post_item"`), and the dispatcher forwards to `/WEB-INF/views/<name>.jsp`. Views live under `WEB-INF` so they can only be reached through the dispatcher, not requested directly.
- **The data layer is in-memory and thread-safe, not persistent.** `PostRepository` is a singleton backed by a `CopyOnWriteArrayList`, seeded with 30 sample posts at startup. Restarting the app resets the data — there's no database behind it.
- **Infinite scroll is a client/server pair.** `FeedController` checks for the `X-Requested-With: XMLHttpRequest` header. A normal request renders the full `feed.jsp` page; an AJAX request (fired by an `IntersectionObserver` in the page's own script) gets back just the `partials/post_item` fragment, which is appended to the DOM.

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java (pom targets release 25 — adjust if your JDK differs) |
| Web layer | Jakarta Servlet API 6.1 |
| Views | Jakarta Server Pages (JSP) 4.0 |
| Build | Maven (packaged as WAR) |
| Frontend | Vanilla JavaScript (`fetch`, `IntersectionObserver`) — no frontend framework, no bundler |
| Routing/DI | Hand-written, using Java reflection and custom annotations |

No Spring, no ORM, no external runtime dependencies beyond the two `provided`-scope Jakarta APIs in `pom.xml`.

## Project structure

```
Java_Servlets_JSP/
├── pom.xml
├── LICENSE
└── src/main/
    ├── java/org/gse_lite/
    │   ├── annotation/
    │   │   ├── Controller.java        # marks a class as a controller
    │   │   └── GetMapping.java        # maps a method to a URL path
    │   ├── controller/
    │   │   └── FeedController.java    # /feed — paginated + AJAX feed endpoint
    │   ├── filter/
    │   │   ├── RateLimiterFilter.java # per-IP request throttling
    │   │   └── PerformanceFilter.java # request timing header
    │   ├── model/
    │   │   └── Post.java              # immutable Post domain object
    │   ├── repository/
    │   │   ├── Repository.java        # generic CRUD-style contract
    │   │   └── PostRepository.java    # thread-safe in-memory store + seed data
    │   ├── scanner/
    │   │   └── ClassScanner.java      # finds @Controller classes on the classpath
    │   ├── service/
    │   │   └── PostService.java       # thin layer between controller and repository
    │   └── servlet/
    │       ├── DispatcherServlet.java # front controller
    │       └── HandlerMethod.java     # wraps a controller instance + Method for invocation
    └── webapp/WEB-INF/
        ├── web.xml                    # registers filters + dispatcher servlet
        └── views/
            ├── feed.jsp                # full feed page + infinite scroll script
            └── partials/
                └── post_item.jsp       # feed fragment, reused for both initial and AJAX loads
```

## Getting started

You'll need:

- **JDK** matching the version in `pom.xml` (currently set to release 25 — lower it in `pom.xml` if you're on an older JDK; the code itself doesn't use anything version-specific)
- **Maven** 3.9+
- **A Jakarta EE 11-compatible servlet container**, since the app depends on Servlet 6.1 / JSP 4.0 — Tomcat 11+ is the straightforward option

Clone the repository:

```bash
git clone https://github.com/Pujagithub2006/Java-Servlets-JSP.git
cd Java-Servlets-JSP
```

## Building and running locally

Build the WAR:

```bash
mvn clean package
```

This produces `target/gse-lite.war`.

**Deploy to Tomcat:**

Drop the WAR into Tomcat's `webapps/` directory:

```bash
cp target/gse-lite.war $CATALINA_HOME/webapps/
$CATALINA_HOME/bin/startup.sh
```

Tomcat will explode the WAR automatically. Once it's up, visit:

```
http://localhost:8080/gse-lite/feed
```

**Running from an IDE:**

If you're using IntelliJ or Eclipse, you can instead configure a local Tomcat/Jetty server run configuration pointing at this module as an exploded WAR artifact, which gives you redeploy-on-change without repackaging each time.

There's no separate database or environment setup needed — the app is self-contained, since `PostRepository` seeds its own sample data on startup.

## Screenshots

_Add screenshots or a short GIF of the feed page and the infinite-scroll behavior here once you have a build running locally — a shot of `/feed` on load, and one mid-scroll, cover it well._

## Features

What's actually implemented right now:

- Custom `@Controller` / `@GetMapping` annotations and a startup-time classpath scanner that discovers them
- A front-controller `DispatcherServlet` that routes requests to controller methods via reflection
- A paginated activity feed (`GET /feed`, `GET /feed?page=N`)
- Infinite scroll on the feed page, powered by `IntersectionObserver` and fetch-based fragment loading, with URL state kept in sync via `history.pushState`
- A generic `Repository<T, ID>` abstraction with an in-memory, thread-safe implementation (`CopyOnWriteArrayList` + `AtomicLong` for IDs)
- Seed data generation (30 sample posts across 10 authors) so the app has something to show without any setup
- A per-IP rate-limiting filter (5 requests per 10-second window, returns HTTP 429 past that)
- A performance-timing filter that stamps every response with an `X-Response-Time-Ns` header

## Future enhancements

Things that are natural next steps given what's already scaffolded but not yet wired up:

- **`@PostMapping` support** — `PostService.createPost(...)` already exists; there's just no endpoint or form calling it yet
- **A proper 404/error view** instead of the container's default error page on unmatched routes
- **Path variables and query parameter binding** (e.g. `@GetMapping("/posts/{id}")`) instead of manual `request.getParameter(...)` calls in each controller
- **Persistent storage** — swapping `PostRepository`'s in-memory list for a real database, so data survives a restart
- **Automated tests** — there's currently no test suite covering the dispatcher, filters, or repository
- **Centralized exception handling** in the dispatcher, rather than each `doGet` call propagating raw exceptions
- **Configurable rate limits** (currently hardcoded to 5 requests / 10 seconds in `RateLimiterFilter`)
- **A simple logging setup** in place of the `System.out.println` calls currently in `DispatcherServlet`

## Contributing

This project is developed feature-by-feature on short-lived branches, then merged into `main`. If you're contributing:

1. Branch off `main` with a name that describes the piece of work, e.g. `feature/post-mapping-support` or `fix/pagination-edge-case`.
2. Keep commits scoped and use a conventional prefix — `feat`, `fix`, `refactor`, `chore`, `build`, or `config` — with the affected area in parentheses where it helps, e.g. `feat(filter): add request logging filter`.
3. Keep the "no external MVC framework" constraint in mind — the point of this repo is to implement things by hand rather than reach for a library. If a change genuinely needs a dependency, explain why in the PR description.
4. Open a pull request against `main` with a short description of what changed and why. Reference an issue number if there is one.
5. Make sure `mvn clean package` succeeds before opening the PR.

## License

Licensed under the Apache License, Version 2.0 — see [LICENSE](LICENSE) for the full text.
# intranet-url-shortener

An URL Shortener but for Intranet purpose

:fr: [Version française](README_fr.md)

:uk: _English version under constuction_

## A- Project introduction

### A.1- The Aim

TODO

### A.2- What is a URL shortener?

TODO

### A.3- The issues related to URL shorteners

TODO

### A.4- Finding

TODO

## B- Project description

### B.1- The Architecture

TODO

### B.2- The Components

| Component | Purpose | Technologies |
|---|---|---|
| Frontend Web | TODO | Vue.js + Typescript within a NGINX http server |
| Backend REST | TOOD | Spring Boot + with embedded Tomcat |
| Database | Store the entries | H2,  MySQL, MariaDB, PostGreSQL, ... |


## Annexes

### Annex 1: RFC HTTP protocol

:bulb: [RFC HTTP Status - section-6.4.2](https://tools.ietf.org/html/rfc7231#section-6.4.2)

### Annex 2: Redirection HTTP status codes

| Code | Message | Meaning |
|---|---|---|
| `300` | **Multiple Choices** | Indicates multiple options for the resource from which the client may choose (via agent-driven content negotiation). |
| `301` | **Moved Permanently** | This and all future requests should be directed to the given URI. |
| `302` | **Found** | Tells the client to look at (browse to) another url. 302 has been superseded by 303 and 307. |
| `303` | **See Other** | The response to the request can be found under another URI using the GET method. |
| `304` | **Not Modified** | Indicates that the resource has not been modified since the version specified by the request headers If-Modified-Since or If-None-Match. In such case, there is no need to retransmit the resource since the client still has a previously-downloaded copy. |
| `305` | **Use Proxy** | (since HTTP/1.1) The requested resource is available only through a proxy, the address for which is provided in the response. Many HTTP clients (such as Mozilla and Internet Explorer) do not correctly handle responses with this status code, primarily for security reasons. |
| `306` | **Switch Proxy** | No longer used. Originally meant "Subsequent requests should use the specified proxy. |
| `307` | **Temporary Redirect** | In this case, the request should be repeated with another URI; however, future requests should still use the original URI. In contrast to how 302 was historically implemented, the request method is not allowed to be changed when reissuing the original request. For example, a POST request should be repeated using another POST request. |
| `308` | **Permanent Redirect** | The request and all future requests should be repeated using another URI. 307 and 308 parallel the behaviors of 302 and 301, but do not allow the HTTP method to change. So, for example, submitting a form to a permanently redirected resource may continue smoothly. |
| `310` | **Too many Redirects** | - |

Source : [Wikipedia](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes)

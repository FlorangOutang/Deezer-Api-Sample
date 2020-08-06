# Deezer-Api-Sample

## Purpose
Demonstrate that we can't make the google connection on Deezer works using the Deezer API.

## How to reproduce the issue
We have followed the instructions of the Deezer api here : https://developers.deezer.com/api/oauth

The flow is the following :
1) Create the Oauth WebView with your app credentials (app id, client secret...) and the the deezer oauth URI
2) Select "Connexion avec Google" in the WebView.
3) Enter your google credentials (email then password)
4) Pass the captcha test
5) At this point we have an infinite loading (instead of being redirect to the deezer page I guess).

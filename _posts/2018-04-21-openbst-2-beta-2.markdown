---
layout: post
title:  "OpenBST 2.0 Beta 2 is available!"
date:   2018-04-21
categories: main
author: utybo
highlight: true
ccolor: "#009abc"
---

A huge update to OpenBST has just been released. It includes refined visuals, HiDPI compatibility, a friendly editor, and much more!

Almost 6 months after the [beta 1]({{ site.baseurl }}{% post_url 2017-07-31-openbst-2-beta %}), OpenBST 2.0 Beta 2 provides tons of improvements over previous iterations of OpenBST. Check out the [CHANGELOG.md](https://github.com/utybo/BST/blob/v2.0-beta2/CHANGELOG.md) file for more information.

**Wanna talk about OpenBST? Need help?** Or maybe you just want to yell at me because I broke everything? Come join the Discord server! [Click here!](https://discord.gg/6SVDCMM)

#### Download links
Downloads can be found, as usual, on the [Downloads page of this website]({{ site.baseurl }}/#downloads).

#### What's new?
Here are some highlights :

##### Good news for everyone!
* **An editor was added!** While it is still in beta and needs a lot of testing, it is quite usable. I would still recommend using a regular text editor.
* **OpenBST is compatible with HiDPI screens** with 1x, 1x25, 1x5 and 2x scaling
* **Hadware acceleration for everything.** I just had to find a small option, and boom, everything is faster.
* **New icons!** A fresh new and more colorful design!
* **OpenBST is compatible with non-latin languages.** I un-broke things.
* The fonts inside the reader now look crisp and delicious!
* An update checker! You will get a notification when an update is available.
* A splash screen! Because it looked cool, and OpenBST is taking longer to load. Sorry about that.
* OpenBST will ask for permission when you ask to close it, just to be sure.
* You can also middle-click on a tab to close it.
* A debug info dialog was added, which will help everyone diagnose crashes and errors. Check it out now in the Advanced Menu!
* More backgrounds on the welcome screen, because they look cool
* Message dialogs now look way better. It may also have added a few bugs. Oops.
* Many new themes are available, although they have not been tested thoroughly.
* See those buttons you click on to decide where the story is going? Yeah, these ones. They were buggy as hell. And now they're changed, they're better, and they're not buggy. Good? Good.
* The about text has been changed a lil' bit. Go check it out if you're curious!
* OpenBST will always ask for permission before running JavaScript code from BST files.
* The See Background and Background Visible buttons didn't work properly. They work correctly now.

##### Good news for writers!
* **Syntax highlighting** is available for the Atom editor! More details in the Tutorial!
* A **Reference file** was added, which gives a handy cheatsheet for syntaxes, actions and checkers in OpenBST
* **New module : XSF** allows you to have full-fledged JavaScript files you can call from your BST file!
* **Experimental features** were added. These come and go as needed, and are not stable. You can still play around with them, and tell us what you think on the Discord server!
* All image formats should be supported properly. As long as they are supported by WebKit, they should work in OpenBST.
* *EXPERIMENTAL : custom CSS files*
* *EXPERIMENTAL : use welcome-screen backgrounds in your file. Check out the internal BST file in the advanced options to see how they look like*

##### Behind the scenes
* Large resources are now compressed using the XZ format
* Optimized changing fonts. The IMG module worked like this : store the raw (PNG, JPG...) image in its bytes, then, when needed, convert it to base64, and send it off to the panel that shows the node text. This ended up slowing down changing nodes as the conversion was done every single time a new node was shown. The conversion is now done right when files are loaded, which makes browsing nodes way faster.
* Unused Ubuntu font types were removed.
* During the development of the Beta 2, I had to go through some font nightmares. I was originally using WOFF fonts from FontSquirrel that, in a nutshell, didn't work well. I then switched to TTF, which was better in most cases, but slowed everything down. OpenBST now uses WOFF2 fonts from Google Fonts, and the nightmare has come to an end.


#### What's next?

So all of this is cool and all, but what's next? I'm not sure. I'll probably have to work on getting OpenBST to be more popular. I also wish to publish updates more regularly.

Some work also needs to be done on the translation system. If you wish to translate OpenBST to your language, hit me up on the discord server, and I'll try to help.

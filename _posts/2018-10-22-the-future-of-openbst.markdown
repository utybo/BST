---
layout: post
title:  "The future of OpenBST"
date:   2018-10-22
categories: main
author: utybo
highlight: true
ccolor: "#15c6b2"
---

Hello everyone. I just wanted to tell you what is planned for OpenBST. I guess making a lengthy post explaining stuff on Halloween is going to be an OpenBST Tradition :)

I'd first like to apologize for the lack of updates. My motivation for this project has been fluctuating quite a lot. I don't have much free time anymore due to undergrad school and all of that jazz, but I'll still try. So, what is on the menu for OpenBST 2.0 beta 3?

A lot of things, actually. First of all, thanks to a [great PR](https://github.com/utybo/BST/pull/15) from [@siordache](https://github.com/siordache), OpenBST can now be built to create platform-dependent packages with a bundled JRE. In a nutshell: executables! Packages! No need to download Java anymore! I needed that because the go-to website for making people download Java only distributes Java 8, whereas we have now reached Java 11. This also created more issues (the current version system doesn't work for some reason, and the embedded file mechanism introduced in the beta 2 will not work there, unfortunately). Starting with the beta3, there will be multipled packages distributed:

* OpenBST for Windows: a pre-built package for Windows, which includes a bundled JRE
* OpenBST for Linux: a pre-built package Linux, which includes the Oracle JRE (and JavaFX)
* OpenBST Universal: the good ol' JAR you're used to.

Now, let's talk about Nashorn. For those of you who aren't aware, OpenBST provides scripting functionalities in JavaScript thanks to Nashorn, a library included in Java, which is, simply put, *fantastic*. However, Oracle [plans to remove it in future Java releases](https://openjdk.java.net/jeps/335), basically making it unsafe to use. A major problem is that this would greatly restrict what story creators could do. If Oracle actually decides to remove Nashorn, I will have to use another library (or a community-made continuation of Nashorn if there will be one). Only time will tell; OpenBST risks being stuck on a specific Java version and never recovering.

Talking about packages, almost all of the libraries OpenBST depends on (except MigLayout) were updated to their latest versions. For some reason, the MigLayout library absolutely blows after the 4.2 version, so it will probably stay at this version for a while. I'd like to give a huge shoutout to [Kirill Grouchnikov](https://github.com/kirill-grouchnikov), who is the author of the amazing [Radiance libraries](https://github.com/kirill-grouchnikov/radiance). If OpenBST doesn't look like shit, it's thanks to him.

Distribution-wise, I originally intended to migrate OpenBST to GitLab after Microsoft bought GitHub, but to be fully honest, I got lazy and gave up on that.

Some new bugs need to be fixed: for some bizarre reason, fonts don't work anymore, the beta2 is unusable on crappy Linux setups (like the Linux machines at my school), and more things need to be tested. Do I have time or motivation for this? Hell no. Getting motivated to work on OpenBST is pretty complicated; the only reason for it being just pride and a sense of accomplishment in my life. I don't even use OpenBST myself -- this program is the definition of something made for others to use.

This was pretty short. I don't know how to make lengthy communication posts. I'm here to be honest and tell the facts, I guess that may be a quality nowadays. Enjoy your day!

utybo

PS: Let's be clear, beta 3 will not be as huge as beta 2 was. Most of the changes are internal. This beta update will mostly polish what was already there.

---
layout: post
title:  "Halloween news!"
date:   2017-10-30
categories: main
author: utybo
ccolor: "#a85803"
---

Hello everyone, and happy Halloween! Let's have a look on stuff that happened recently.

First of all, I'd like to apologize for the lack of updates, content and all the things that I promised but didn't happen. Mostly, OpenBST 2.0 wasn't released. Life has been pretty hard for me recently, and I wasn't able to do much. Still, OpenBST 2.0 Beta 2 should be coming soonish, as most of the features are done, mostly HiDPI icon support and new experimental features. While the changelog for the first beta was massive, this one is rather slim, and mostly takes care of polishing the details.

Also, Java 9 came out. I am not planning of switching to the module system any time soon, however, you will notice that HiDPI support on Java 9 is rather crappy. This is due to a bug in the theme I use called Substance. Basically, before Java 9, if you used a non-native theme with Swing (the toolkit commonly used for interfaces), it would not be scaled and would be ridiculously small on displays like 4k screens. However, Substance successfully uses its own method of applying HiDPI stuff to Swing, so interfaces looked correct anyway. However, because Java 9 introduces HiDPI support in Swing itself, this HiDPI mechanism included in Substance is no longer necessary and actually makes the interfaces look bad now. The only way to fix this is by using a special argument. **For now, just don't update to Java 9, as it is far from useful if you only use OpenBST or Minecraft** (which are, I assume, the most common use cases for people reading these lines).

Also, to be honest, I am not sure if I am going to continue updating OpenBST for a while. Why? Because no one cares! It's as simple as that. This software isn't popular and I don't even use it myself that much. So, I just made a nice thing no one really gives a damn about, which isn't the greatest feeling ever to be honest.

After this rather sad note, I'd like to introduce something new, something shiny : the BST reference! This is a new document that supplements the Tutorial and provides documentation on all the actions and checkers used in BST. As a reminder, BST is the name of the language you use when writing your stories, and OpenBST is the name of the software used to read them.

For the future, here is what's coming : I'd like to have the Beta 2 coming out in November and then a bug-fixing Beta 3. After that, either a Beta 4 if needed or directly release OpenBST 2.0 to the public eye. Feature-wise, everything is pretty much done.

So, have a happy Halloween and feel free to share the Beta 1, which is already pretty stable, and get hyped (a little bit) for OpenBST 2.0 Beta 2!

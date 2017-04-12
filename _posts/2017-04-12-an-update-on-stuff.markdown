---
title:  "An update on stuff coming soon in BST 2.0!"
date:   2017-04-12
---

Hey there, just a small blog post on what I've been doing. Before you ask, yes, the next BST version will be version 2.0, but don't get *too* excited yet :)

One of the features that cause this version number bump is the fact I modified the internals of BST quite a bit, which means I had to made small changes everywhere and broke compatibility with older versions. Since I am following the semantic versioning system, this means I *have* to make the next version 2.0!

Here are a few features that are done or are currently being worked on :

- **PACKAGING** : You'll now be able to distribute your stories with a single file that includes your file, the resources associated, and more!
- **MENU** : Yes, an actual menu, with buttons to click on, an about menu, and other stuff.
- **LOGGING UPDATE** : The logs are now saved in a file, so you'll have a record of what happens inside BST.
- **XBF** : XBF allows you to use multiple BST files in a very easy way. You'll be able to have multiple files instead of one massive BST file. All of them share the same variables, and can call each other.

By the way, XBF is the reason why I had to break everything. BST was internally made for only *one* file, but I had to adapt everything to the possibility that there would be multiple files. All of that is complicated, and long, but I'll try.

*I may also try to switch the sound system (again) to one that may be compatible with everything ;)*

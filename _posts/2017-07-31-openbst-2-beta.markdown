---
layout: post
title:  "OpenBST 2.0 Beta 1 is out!"
date:   2017-07-31 00:00:00
categories: main
author: utybo
highlight: true
ccolor: "#3aea00"
---

OpenBST 2.0 is here! Well, in beta at least. This post will cover how you can download it and the most notable changes. So come in, have some coffee and let's talk about what's up with this new release!

I am proud to announce the release of OpenBST 2.0 Beta 1, a first beta that can be considered "pretty stable". Here is a recap of all the changes that happened, but if you want a complete log of everything that changed, have a look at our brand new [CHANGELOG.md](https://github.com/utybo/BST/blob/v2.0-beta1/CHANGELOG.md) file!

By the way, the bst-java library also got improved and reaches version 2.0 beta 1 as well, while the tutorial also got updated and reaches version 8!

- New Welcome screen! No more trying to sell the program when trying to launch a file! (This accidentally makes OpenBST significantly larger, bumping up to 10 MB, which is still acceptable imo)
- Added an OpenBST Menu with more options! With it also comes a language completeness checker (that will be revised and improved before the final release), an about menu, and others!
- Added a way to package your files! Now you just have one .bsp file instead of dozens of .bst files!
- HTML5 support! Your files now look good! With it also comes an option to change the font of your file! (More advanced features like custom CSS is planned for addition, but indefinitely postponed as it is not high on my priority list right now) Also new is a HTB module that provides options for manipulating the HTML5 environment!
- Added an XBF module, which allows you to split your file in multiple .bst files, making it cleaner!
- Node aliasing and auto id : You can now give names to your nodes!
- Better overall looks! (We now have a dark mode!)
- Logs are now saved to files! On Mac and Linux, location is ~/.openbst/logs, for Windows, location is .openbst\logs inside your user folder.
- A small tool for translators : an i18n completeness checker, which tells you what strings are missing in your language
- BRM now loads your resources automatically : you no longer need to call brm_load!
- All nodes can now be tagged
- Jump To Node dialog got a new look!

A lot of other things changed internally. **Story writers, you may be interested in the complete changelog as it is well organized!**

To download this release, head to the [download section]({{ site.url }}/BST/#downloads)

**PLEASE REPORT ANY BUG YOU FIND [HERE](https://github.com/utybo/BST/issues)!**

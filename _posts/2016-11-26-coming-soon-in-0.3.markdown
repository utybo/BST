---
layout: post
title:  "Coming soon in OpenBST 0.3"
date:   2016-11-26
categories: main
author: utybo
image: img/v0.3.png
---
0.3 may be the last version before we reach the golden goal of 1.0. Why? Because it is packed with awesome feature that make it the best release of BST yet. Well, I say that every time I make a release anyway, so... whatever, let's just move on to the cool stuff!

![Promo]({{ site.baseurl }}/img/v0.3.png)

Version 0.3 introduces even more advanced features, this time aimed more at game developers. Yes, I aim to make BST usable for small text games development. The main thing that was missing from BST was the ability to create your own GUI, the ability to *show* stuff without needing to do some weird virtual node inclusion at the top of every other node.

For this reason (and because that feature is seriously neat), UIB comes in.

## UIB -- User Info Bar (or User Interface for BST)
Not really sure on the name (but i've never been professional anyway)

The User Info Bar is a module. Modules are a concept inside BST like what libraries are to developers : they add cool functionalities to your program. But enough words, I guess a picture would explain everything better.

![Screenshot]({{ site.baseurl }}/img/0.3/screenshot1.png)

The bars and text you see on the picture are not a template. The layout is fully customized, and it's very easy to do. This is not the simples example though; let's take a simpler layout :

![Screenshot]({{ site.baseurl }}/img/0.3/screenshot2.png)

UIB uses the tag `uib_layout` to determine the layout. Here, it is nothing more than :

    uib_layout=tb,vs,tb,ln,hs,ln,tb,ln,hs,ln,t

Where `tb` is a Text+Bar couple, `vs` is a vertical separator, `ln` is a new line, `hs` is a horizontal separator and `t` is just a text component. The first node of the BST file is just a logical node : 

<pre><code># We initiate UIB first. This translates our text layout into actual components, and
# needs to be done before setting anything
uib_init:

# Each component is identified by an id. The ID is automatically attributed
# depending on the position of the component in the uib_layout tag, starting by 0.
# Here, we take the first text label declared, so its ID is t0 
# We set its value to the logical node 5. It is automagically updated, so
# you don't need to do anything after this.
uib_set:t0,>5

# uib_setprop is a useful function that allows you to set some special property.
# Here, we set the maximum value of the bar 0 to 10. The minimum is 0 by default,
# so we just need to set a maximum
uib_setprop:b0,max,10

# And we set the bar's value to the variable a. Do note that this would not have worked
# with a text component (ie using t0 instead of b0), as the text "a" would have been shown. 
uib_set:b0,a

# We do the same for the next components...
uib_set:t1,>5
uib_setprop:b1,max,10
uib_set:b1,a

uib_set:t2,>5
uib_setprop:b2,max,10
uib_set:b2,a

# And finally set the text for the lonely text component. We can also put markdown
# or any supported markup language here!
uib_set:t3,This is a teaser for **BST**. Yay!

# By default, UIB is hidden. We have to make it visible somewhere.
# We can also hide it later on if we want.
uib_setvisible:true</code></pre>

As you can see, this was rather simple to do. Once UIB is set up, we don't have to do anything else : the values and the bars are automatically updated.

So yeah, that's pretty neat, but you can do much more than that! A grid mode (used in the first screenshot) and advanced layout options are available and, since UIB uses MIGLayout, you can use [all of the MIGLayout component constraints when using Advanced Mode and all of the column constraints in Grid mode!][migref] You can also use both Grid Mode and Advanced Mode at the same time!

Here is an example of using Grid Mode, which was used in the first screenshot :

<pre><code>uib_grid=[][grow][][][grow]
uib_layout=tb,gu,tb,nl,tb,gu,tb</code></pre>

Each `[]` is a column, and you can place constraints inside them. Here, we make the second and fifth column grow to a maximum width, and, since the bars go in these columns, they will receive a very nice, grid-like layout. `gu` means `gap unrelated` and is just a small gap. It can be used instead of a `vs` to provide a mild visual separation instead of a strong one.

## Supertools
You may have also noticed that, in the first screenshot, a number of tools were absent. **Don't worry, they're still here!** It's just that a new tag is now available in BST that allows you to hide categories of tools that may be used for cheating purposes! Also, they now have a name. They're called Supertools. Don't ask why and don't judge me, I just needed a cool name >_<

The tag `supertools` can have 5 different values, from most permissive to most restrictive :
* `all` : Show all the tools. This is the default value if this tag is not defined in the story, and is the default behavior that could be seen before this version.
* `hidecheat` : Hide node ID information, node jumping and the variable watcher. These are the most powerful tools and it is highly recommended that, outside of story creation, you use this value.
* `savestate` : Hide everything except save states tools. This will hide everything hidden by `hidecheat` and the Reset and Reload options.
* `savestatenoio` : Same as `savestate`, but also hide Import and Export options
* `none` : Hide all the tools and hide the "Hover for information" text. This is the default behavior if you the tag is present in your story but the option is mispelt, to be sure that the user does not have access to what he is not supposed to see. Do note that this will not hide the Close button.

## Other stuff
There were a lot of bug fixes, and OpenBST is almost complete on the stabilization tasks.

OpenBST 0.3 RC1 is expected to be released next week. I could release it tomorrow, but the Tutorial is not ready yet, as it does not include any information on UIB.

Thank you for reading!

[migref]: http://www.miglayout.com/cheatsheet.html

# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

Major changes and highlights are in **bold**. Other changes that impact users are in plain. Changes impacting only developers are in *italics*.

Story makers should read all changes in bold and plain. Some mechanisms may be torn apart between major (1.x ==> 2.0) or minor (x.0 ==> x.1) updates, though backward-compatibility is always attempted for BST files.

Unstable versions will appear inside OpenBST with a "u" at the end and a warning on the welcome screen.

Less technical explanations for each update available on the [OpenBST website](https://utybo.github.io/BST/)

## [2.0-beta2] (Unreleased) - 2.0 with HiDPI Glory, and an editor
### Added
- **An editor was added.** You can now create and edit BST files directly from inside OpenBST!
- **Experimental features were added.** They provide cool features, but can be very unstable and may disappear at any time. A warning is issued every time you call an experimental function.
- **Better icon support for HiDPI.** Icons were previously very small, but the new addition of scalable icons provides good looking icons on various display sizes.
- **New XSF Module** which adds full Javascript capacity to BST, with the ability to run .js files with all the great stuff from Java's Nashorn! There is also a Next Node Definer available as an experimental feature.
- **A fancy splash screen has appeared!** Because we're now loading a *lot* of things.
- An update checker has been added. Due to how OpenBST can be distributed and ran, it is only a checker, and will just warn you that a new update exists, leaving a link for you to click on to go and download it.
- A Debug Info dialog has been created, which will make troubleshooting easier for devs when users will create issues.
- Added better message dialogs. Just fancy stuff, but hey, at least errors are shown more consistently!
- Options now support variables
- 2 new, beautiful backgrounds have been added!
- Additional themes from Substance are now supported, although they are not considered to be official ones.
- Added new experimental features for HTB : custom CSS files.
- Added a new Experimental Warning icon
- Added a new experimental feature for IMG : using the internal backgrounds.
- *Note on experimental features : See the javadoc for the @Experimental annotation which explains the general contracts around experimental elements*
- *Added warnExperimental(line, from) inside BSTClient in bst-java*	 
- *Added a BezierEase class adapted from the Javascript library [bezier-easing](https://github.com/gre/bezier-easing)*
- *Next Node Definers now require line arguments to provide better information when an error occurs*

### Changed
- **All icons have been changed to Icons8's Color set.** This improves consistency.
- Large resources are now compressed (with XZ) to avoid massive file sizes.
- IMG now automatically caches images into the Base64 format, which makes selecting an option *much* faster now.
- All common image types should be supported now.
- The option buttons system has been reworked to be much more dynamic
- *Most NNDs and NodeOption now publicly expose their variables to allow for easier recreating of text*
- *Code related to the user interface and code related to the main routines of OpenBST have been separated from the OpenBST class to OpenBST and OpenBSTGUI*

### Deprecated

### Removed
- *Removed most of the leftovers from JSE's old manual update system. JSE does not need to be implemented by clients anymore.*
### Fixed
- **OpenBST is now compatible with non-latin languages.**
- Fonts no longer look like trash inside story panels. This was caused by a crappy, wonky and potentially license-breaking WOFF+WOFF2 conversion from TTF or OTF.
- Fixed options not working at all in some cases.
- Fixed the background visibility button not working
- Fixed the Show Background button only showing the first background it has ever showed.
- Fixed font size on the error screen
- Fixed a typo in French language file.
- *Fixed a (stupid) typo in a class name : Dictionnary -> Dictionary*

### i18n changes
- **NOTE :** for a more detailed list, use the Lang completeness checker tool inside OpenBST (OpenBST menu > Advanced tools)
- added story.experimental story.experimental.title copy saveas save play close menu.create menu.debug menu.themes.morelight menu.themes.moredark file.error story.experimental story.exeprimental.title story.unicodecompat story.unicodecompat.title welcome.java9warning welcome.java10warning welcome.snapshot welcome.openeditor up.* editor.* splash.* debug.*
- removed all the html tags that were used for notifications (the new message dialogs automatically adds them where needed)

## [2.0-beta1] - The 2.0 Beginning
### Added
- **New, simpler, cleaner, sleeker OpenBST Welcome screen!**
- **New OpenBST menu with animations and stuff!**
- **New BSP format that allows you to pack a whole story, including resources, in a single file. An assistant is available in the new menu.**
- **Now using HTML5 for viewing nodes! Which means that now your BST stories finally look good!**
- **A new module (HTB) is also available to configure the HTML5 environment**
- **New XBF module. You can now use multiple BST files in your resources and call their nodes.**
- **Automatic ID attribution is now available, simply use `*` as your node ID**
- **Node aliasing added, you can now give your nodes names and call them using their names!**
- You can choose between two fonts for your story now with the tag "font": either "libre_baskerville" (default) which is a serif, more classical font, or "ubuntu" which provides a more modern look.
- UI theme selector added
- File logging. Logs are now saved in ~/.openbst/logs
- We now have an About dialog! Gotta give credit :)
- Icons8 credits were moved to the About dialog
- Also introduce a small i18n completeness checker. It basically allows you to check if your current language is completely translated or not, and tells you what still needs to be translated.
- Added a warning for unknown colors
- New icons and a few images from Pixabay were added for the new Welcome screen
- Added a small mechanism for warning about unstable releases
- *Missing language strings now yield warnings in the logs*
- *BSTClient.getResourceHandler() method provides a unified way to get resource folder names*
- *TagHolder.getTagOrDefault() method provides a way to use getOrDefault() from underlying object*
- *OpenBST.addDarkModeCallback() allows you to have a callback when you need to adapt to dark mode activation*
- *New visual utility classes : JBackgroundPanel and JBannerPanel*
- *Added a warn() method in BSTClient in bst-java for more logging power*
- *New method for parsing stories that forces a variable registry*
- *Added a constructor for BranchingStories that takes a given variable registry as its own*


### Changed
- **Switched from System Look and Feel to a custom Look and Feel that provides a unified look from platform to platform (and eases testing)**
- **BRM Loading has been made automatic. You do not need to call brm_load anymore.**
- **All node types can now be tagged.** Previously, only text nodes could receive tags, and this was unnoticed due to the fact no tags were useful for other nodes.
- Error page is now fully HTML and also much more swag
- Brand new node jump dialog box that looks nice!
- Icon sizes are smaller for a few dialog boxes as they were way too big before
- *BRM Loading mechanism is much more flexible now (e.g InputStream loading)*
- *All modules were adapted to this new loading mechanism to add compatibility with BRM. This may break APIs.*
- *MAJOR API BREAK : Almost everything now has additional context requirements due to the addition of XBF, which now means you can have multiple BranchingStories for one file.*
- *MAJOR API BREAK : Changed package organisation because that's what we're doing now apparently*
- *All the icons are now final, and loaded differently.*
- *Registries are now more tightly linked to the story/stories they belong to. This was necessary for XBF to work properly*

### Deprecated
- *Everything internally related to the old brm_load method that is now unnecessary has been deprecated.*

### Removed
- Language strings and icons from the old welcome menu have been removed from OpenBST
- The entire JSE manual update mechanism has been deleted (in a backward compatible way)
- Tip on "hover for more info" has been removed as it took too much space

### Fixed
- The variable watcher was not translatable before. This is fixed.
- OpenBST will not crash anymore when loading a valid BST file with an unvalid extension.
- Sometimes, ambient sounds in SSB would not be recorded properly due to some race condition. This is now fixed.
- Fixed crash on stopping when there are no ambient sounds.
- Fixed some issues related to threading and concurrency (a few deadlocks and wrong thread errors here and there)
- OpenBST no longer crashes on unrespected language file model
- A lot of exceptions were not logged before. This is now fixed.
- Some exceptions were not notified to the user before, they now are.
- *We're attempting to avoid System.out.println at all cost to avoid polluting System.out, please use the logger if you need to log something!*
- *Using Findbugs, a LOT of work has been done to stabilize OpenBST and make it more reliable, along with a lot of fixes.*
- *Also, the new Look and Feel crashes when changing UI components outside of the EDT thread, which is great since it allows us to track down what is still bugged and outside of the EDT thread.*
- *VariableRegistry objects are now cloned properly*

### (Partial) i18n changes
- Added menu.themes.debug
- Changed story.error and story.error2
- Removed welcome.whatis welcome.about welcome.imagine welcome.write welcome.play welcome.enjoy welcome.icons
- Added welcome.changebackground welcome.pixabay welcome.credits welcome.ontheedge welcome.reportbugs
- Added a small clarifiation inside the language file on a langcheck string
- Removed story.tip as it was making the toolbar dangerously large
- Added a vwatch category for variable watcher strings



[2.0]: https://github.com/utybo/BST/compare/v1.1...dev
[2.0-beta1]: https://github.com/utybo/BST/compare/v1.1...v2.0-beta1
[2.0-beta2]: https://github.com/utybo/BST/compare/v.2.0-beta1...dev

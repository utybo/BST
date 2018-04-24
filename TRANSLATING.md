# How can I translate OpenBST?
## Adding/Editing
### If you are adding a new language
#### 1) Download the latest English translation
[Click here](https://raw.githubusercontent.com/utybo/BST/dev/openbst/src/main/resources/utybo/branchingstorytree/swing/lang/en.lang) to download it. (Use Ctrl+S if your browser doesn't automatically download it)

#### 2) Edit all the fields
The fields follow the format

    key=Value

Simply replace the Value by the translation you find to be the most appropriate. The value can also contain some HTML tags : please leave them there and/or adapt them to your translation.

#### 3) Test everything
Make sure the file you edited is named `en.lang`, if it isn't, please name it like that even if your language is not English. Open the latest version of OpenBST (the .jar file) using WinRar or any archive explorer, and browse to utybo/branchingstorytree/swing/lang and replace the en.lang file with your translation. Then, run OpenBST and make sure everything looks ok.

### If you are editing a language that already exists
Do note this won't always be necessary. Only do this if you noticed that some text is in English in OpenBST whereas the rest is in your language.

#### 1) Download the current translation for the language
Go to [this page](https://github.com/utybo/BST/tree/dev/openbst/src/main/resources/utybo/branchingstorytree/swing/lang) and find the language you wish to edit and download it. You should also download the reference English translation, available [here](https://raw.githubusercontent.com/utybo/BST/dev/openbst/src/main/resources/utybo/branchingstorytree/swing/lang/en.lang), for reference

#### 2) Edit all the fields
Find the problematic translations and edit them (more info in the other 2) above). Can't find the one you're looking for? If the element in OpenBST is in English instead of your language, you should look inside the en.lang file for the line you want to translate.

#### 3) Test everything
Open the OpenBST file (with a .jar at the end) using any archive manager (i.e WinRAR, 7zip...), browse to utybo/branchingstorytree/swing/lang and replace the language file with your own. Then, run OpenBST and make sure everything looks good.

## Submitting
Create an issue here : https://github.com/utybo/BST/issues

Make sure you attach your .lang file and tell us which language you have translated into. We will then take care of giving the .lang file the correct name and making additional changes if necessary.

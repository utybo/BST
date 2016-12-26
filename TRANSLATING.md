# How can I translate OpenBST?
## Creating/Editing
### If you are creating a new language
#### 1) Download the English translation from the "dev" branch
The translation should be in the folder openbst/src/main/resources/utybo/branchingstorytree/swing/lang

Here is a direct link : https://raw.githubusercontent.com/utybo/BST/dev/openbst/src/main/resources/utybo/branchingstorytree/swing/lang/en.lang
#### 2) Edit all the fields
The fields follow the format

    key=Value

Simply replace the Value by the translation you find to be the most appropriate. The value can also contain some HTML tags : please leave them untouched.

#### 3) Test everything
Make sure the file you edited is name `en.lang`, if it isn't, please name it like that even if your language is not english. Open the latest .jar file downloadable from the OpenBST website using WinRar or any archive explorer, and browse to utybo/branchingstorytree/swing/lang and replace the en.lang by yours. Then, run OpenBST and make sure everything looks ok.

### If you are editing a language that already exists
#### 1) Download the current translation for the language
Go to the following page : https://github.com/utybo/BST/tree/dev/openbst/src/main/resources/utybo/branchingstorytree/swing/lang and find the language you wish to edit.

Memorize the name, and download the file at https://raw.githubusercontent.com/utybo/BST/dev/openbst/src/main/resources/utybo/branchingstorytree/swing/lang/en.lang **replacing en.lang by the name you previously saw**.

#### 2) Edit all the fields
Follow the same instructions than the 2) above

#### 3) Test everything
The instructions are the same as the 3), **but you need to replace the file for YOUR language, not en.lang, and you must not rename your file to en.lang**

## Submitting
Create on issue here : https://github.com/utybo/BST/issues

Make sure you attach your .lang file and tell us which language you have translated into. We will then take care of giving the .lang file the correct name and making additional changes to another file that indexes all the languages.

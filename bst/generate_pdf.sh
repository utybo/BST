#!/bin/sh
echo "Launching headless LibreOffice and converting -- this may take a while..."
echo "Converting the tutorial"
libreoffice --headless --convert-to pdf TheBSTTutorial.odt
mv -f TheBSTTutorial.pdf "The BST Tutorial.pdf"
echo "Converting the reference"
libreoffice --headless --convert-to pdf BSTReference.ods
mv -f BSTReference.ods "BST Reference.pdf"
echo "Done!"

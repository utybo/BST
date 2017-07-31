#!/bin/sh
echo "Launching headless LibreOffice and converting -- this may take a while..."
libreoffice --headless --convert-to pdf TheBSTTutorial.odt
echo "Moving to final file name..."
mv -f TheBSTTutorial.pdf "The BST Tutorial.pdf"
echo "Done!"

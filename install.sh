#!/bin/bash
mkdir -p ./bin/classes;
cd src;
javac -verbose -d ../bin/classes ./pomotron/model/*.java ./pomotron/controller/*.java  ./pomotron/view/*.java && jar vcmf Manifest.txt ../bin/pomotron -C ../bin/classes .;
cd ..;
cp -f ./resources/icons/48x48/pomotron.png  ~/.local/share/icons/;
chmod +x ./resources/shortcuts/pomotron.desktop;
sudo desktop-file-install --rebuild-mime-info-cache  ./resources/shortcuts/pomotron.desktop
sudo cp -f ./bin/pomotron /usr/local/bin/;

#!/bin/bash

# Répertoire contenant les fichiers .ass
directory="src/test/deca/codegen/valid/created/"

# Vérifie si le répertoire existe
if [ -d "$directory" ]; then
    # Boucle sur tous les fichiers .ass dans le répertoire
    for file in "$directory"*.ass; do
        # Vérifie si des fichiers .ass existent
        if [ -e "$file" ]; then
            echo "Exécution de ima sur : $file"
            ima "$file"
        else
            echo "Aucun fichier .ass trouvé dans $directory"
            break
        fi
    done
else
    echo "Le répertoire $directory n'existe pas."
fi

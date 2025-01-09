#! /bin/sh

# Auteur : gl01
# Version initiale : 01/01/2025

# Base pour un script de test de la lexicographie.
# On teste un fichier valide et un fichier invalide.
# Il est conseillé de garder ce fichier tel quel, et de créer de
# nouveaux scripts (en s'inspirant si besoin de ceux fournis).

# Il faudrait améliorer ce script pour qu'il puisse lancer test_lex
# sur un grand nombre de fichiers à la suite.

# On se place dans le répertoire du projet (quel que soit le
# répertoire d'où est lancé le script) :
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

# /!\ test valide lexicalement, mais invalide pour l'étape A.
# test_lex peut au choix afficher les messages sur la sortie standard
# (1) ou sortie d'erreur (2). On redirige la sortie d'erreur sur la
# sortie standard pour accepter les deux (2>&1)

test_valid_files() {
    for file in src/test/deca/syntax/valid/provided/*.deca; do
        result=$(test_lex "$file" 2>&1)
        expected_file="${file%.deca}.expected"
        echo "$expected_file"
        a=$(sed -n '/\/\/ BEGIN LEXER \//,/\/\/ END LEXER \//{//!p}' "$expected_file")
        echo $(diff $a $result)
        if diff < $a $result > /dev/null
        then
            echo "\033[0;32mOK pour $file\033[0m"
        else
            echo "\033[0;31mEchec inattendu de test_lex pour $file\033[0m"
            echo "$result"
            exit 1
        fi
    done
}

test_invalid_files() {
    for file in src/test/deca/syntax/invalid/provided/*.deca; do
        if test_lex "$file" 2>&1 | grep -q -e "$file:[0-9]"; then
            echo "\033[0;32mEchec attendu pour test_lex pour $file\033[0m"
        else
            echo "\033[0;31mErreur non detectee par test_lex pour $file\033[0m"
            exit 1
        fi
    done
}

test_valid_files
test_invalid_files

echo "\033[1;34mTous les tests sont passés.\033[0m"
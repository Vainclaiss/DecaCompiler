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
. "$(dirname "$0")/utils.sh"

# /!\ test valide lexicalement, mais invalide pour l'étape A.
# test_lex peut au choix afficher les messages sur la sortie standard
# (1) ou sortie d'erreur (2). On redirige la sortie d'erreur sur la
# sortie standard pour accepter les deux (2>&1)

check_lex() {
    if test_lex "$1" 2>&1 | grep -q -e ".deca:[0-9]" -e ".decah:[0-9]"; then
        if [ "$2" = false ]; then
            failure "Lexer failed for $1, but it was expected to succeed."
            exit 1
        fi
    else
        if [ "$2" = true ]; then
            failure "Lexer succeeded for $1, but it was expected to fail."
            exit 1
        fi
    fi
}

# Valid tests
make_valid_tests(){
    find src/test/deca/syntax/lexer/valid -type f -name '*.deca' | while read -r file; do
        check_lex "$file" false
        success "[valid] Test passed for $file"
    done
}

# Invalid tests
make_invalid_tests() {
    find src/test/deca/syntax/lexer/invalid -type f -name '*.deca' | while read -r file; do
        check_lex "$file" true
        success "[invalid] Test passed for $file"
    done
}

main() {
    prompt_strong "Running basic-lex tests..."
    setup_path_and_cd
    make_valid_tests
    make_invalid_tests
}

main

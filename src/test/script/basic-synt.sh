#! /bin/sh

# Auteur : gl01
# Version initiale : 01/01/2025

# Test minimaliste de la syntaxe.
# On lance test_synt sur un fichier valide, et les tests invalides.

# dans le cas du fichier valide, on teste seulement qu'il n'y a pas eu
# d'erreur. Il faudrait tester que l'arbre donné est bien le bon. Par
# exemple, en stoquant la valeur attendue quelque part, et en
# utilisant la commande unix "diff".
#
# Il faudrait aussi lancer ces tests sur tous les fichiers deca
# automatiquement. Un exemple d'automatisation est donné avec une
# boucle for sur les tests invalides, il faut aller encore plus loin.

. "$(dirname "$0")/utils.sh"

check_synt() {
    if [ "$2" = true ]; then
        if ! test_synt "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"; then
            failure "Succes inattendu de test_synt sur $1."
            exit 1
        fi

    else
        if test_synt "$1" 2>&1 | grep -q -e ':[0-9][0-9]*:'; then
            failure "Echec inattendu pour $1"
            exit 1
        fi
    fi
}

make_valid_tests() {
    for file in src/test/deca/syntax/parser/valid/*/*.deca; do
        check_synt "$file" false
        success "[valid] Test passed for $file"
    done
}

make_invalid_tests() {
    for file in src/test/deca/syntax/parser/invalid/*/*.deca; do
        check_synt "$file" true
        success "[invalid] Test passed for $file"
    done
}

main() {
    prompt_strong "Running basic-synt tests..."
    setup_path_and_cd
    make_valid_tests
    make_invalid_tests
}

main

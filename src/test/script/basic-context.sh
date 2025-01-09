#! /bin/sh

# Auteur : gl01
# Version initiale : 01/01/2025

# Test minimaliste de la vérification contextuelle.
# Le principe et les limitations sont les mêmes que pour basic-synt.sh
. "$(dirname "$0")/utils.sh"

check_context() {
    if [ "$2" = true ]; then
        if ! test_context "$1" 2>&1 | grep -q -e "$1:[0-9][0-9]*:"; then
            failure "Succes inattendu de test_synt sur $1."
            exit 1
        fi

    else
        if test_context "$1" 2>&1 | grep -q -e ':[0-9][0-9]*:'; then
            failure "Echec inattendu pour $1"
            exit 1
        fi
    fi
}


make_valid_tests() {
    for file in src/test/deca/context/valid/*/*.deca; do
        check_context "$file" false
        success "[valid] Test passed for $file"
    done
}

make_invalid_tests() {
    # TODOOOOO: Verify thrown Error /!\
    for file in src/test/deca/context/invalid/*/*.deca; do
        check_context "$file" true
        success "[invalid] Test passed for $file"
    done
}

main() {
    prompt_strong "Running basic-context tests..."
    setup_path_and_cd
    make_valid_tests
    make_invalid_tests
}

main

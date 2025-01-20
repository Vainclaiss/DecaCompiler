#! /bin/sh

# Auteur : gl01
# Version initiale : 01/01/2025

# Test minimaliste de la vérification contextuelle.
# Le principe et les limitations sont les mêmes que pour basic-synt.sh
. "$(dirname "$0")/utils.sh"

check_context() {
    if [ "$2" = true ]; then
        context_res=$(test_context "$1" 2>&1)
        context_ret=$?
        if [ $context_ret = 0 ]; then
            failure "Succes inattendu de test_synt sur $1."
            exit 1
        else
            check_error_message "$1" "$context_res"
            rm -f "${1%.deca}.res" 2>/dev/null
            rm -f "${1%.deca}.expected" 2>/dev/null
        fi

    else
        if test_context "$1" 2>&1 | grep -q -e ':[0-9][0-9]*:'; then
            failure "Echec inattendu pour $1"
            exit 1
        fi
    fi
}

check_error_message() {
    echo "$2" 1>"${1%.deca}.res"
    awk '/\/\/ Resultats:/{flag=1; next} /^$/{flag=0} flag' "$1" | sed 's/^\s*//' | sed 's/\/\///' >"${1%.deca}.expected"
    error_message=$(grep '^src/test/deca/context/invalid/' "${1%.deca}.res")
    if [ -n "$error_message" ]; then
        echo "$error_message" | sed 's|.*/||' >"${1%.deca}.res"
        if ! diff -B -w -q "${1%.deca}.expected" "${1%.deca}.res" >/dev/null; then
            failure "Incorrect result for $1."
            diff "${1%.deca}.expected" "${1%.deca}.res"
            exit 1
        fi
    else
        failure "Erreur attendue non trouvée pour $1"
        exit 1
    fi
}

make_valid_tests() {
    find src/test/deca/context/valid -type f -name '*.deca' | while read -r file; do
        check_context "$file" false
        success "[valid] Test passed for $file"
    done
}

make_invalid_tests() {
    find src/test/deca/context/invalid -type f -name '*.deca' | while read -r file; do
        check_gencode_file_format "$file"
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

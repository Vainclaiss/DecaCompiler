#!/bin/sh

# Auteur : gl01
# Version initiale : 01/01/2025

. "$(dirname "$0")/utils.sh"

check_gencode_file_format() {
    if ! grep -q -e ".*\/\/ Description:.*" "$1"; then
        failure "Invalid file $1: no description line."
        exit 1
    fi
    if ! grep -q -e ".*\/\/ Resultats:.*" "$1"; then
        failure "Invalid file $1: no results line."
        exit 1
    fi
}

check_compilation() {
    ass_file="${1%.deca}.ass"
    if ./src/main/bin/decac "$1" -ne 0; then
        if [ "$2" = true ]; then
            failure "Compilation failed for $1, but it was expected to succeed."
            exit 1
        fi
    else
        if [ "$2" = false ]; then
            failure "Compilation succeeded for $1, but it was expected to fail."
            exit 1
        fi
    fi
    if [ "$2" = true ] && [ ! -f "$ass_file" ]; then
        failure "File $ass_file not generated for $1."
        exit 1
    fi
}

check_result() {
    ass_file="${1%.deca}.ass"
    awk '/\/\/ Resultats:/{flag=1; next} /^$/{flag=0} flag' "$1" | sed 's/^\s*//' | sed 's/\/\///' >"${1%.deca}.expected"
    ima "$ass_file" > "${1%.deca}.res"

    if ! diff -B -w -q "${1%.deca}.expected" "${1%.deca}.res" >/dev/null; then
        failure "Incorrect result for $1."
        diff "${1%.deca}.expected" "${1%.deca}.res"
        exit 1
    fi
}

# Valid tests
make_valid_tests() {
    for file in ./src/test/deca/codegen/valid/created/*.deca; do
        check_gencode_file_format "$file"
        check_compilation "$file" false
        check_result "$file" false
        rm -f "${file%.deca}.ass" 2>/dev/null
        rm -f "${file%.deca}.res" 2>/dev/null
        rm -f "${file%.deca}.expected" 2>/dev/null
        success "[valid] Test passed for $file"
    done
}

# Invalid tests
make_invalid_tests() {
    for file in ./src/test/deca/codegen/invalid/created/*.deca; do
        check_gencode_file_format "$file"
        check_compilation "$file" false
        check_result "$file" false
        rm -f "${file%.deca}.ass" 2>/dev/null
        rm -f "${file%.deca}.res" 2>/dev/null
        rm -f "${file%.deca}.expected" 2>/dev/null
        success "[invalid] Test passed for $file"
    done
}

main() {
    prompt_strong "Running basic-gencode tests..."
    setup_path_and_cd
    make_valid_tests
    make_invalid_tests
}

main
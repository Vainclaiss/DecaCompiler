#! /bin/sh

prompt_strong() {
    printf "\033[1;34m%s\033[0m\n" "$1"
}

prompt_check() {
    printf "\033[0;35m%s\033[0m\n" "$1"
}
prompt() {
    printf "\033[0;34m%s\033[0m\n" "$1"
}
success() {
    printf "\033[0;32m%s\033[0m\n" "$1"
}

failure() {
    printf "\033[0;31m%s\033[0m\n" "$1"
}

setup_path_and_cd() {
    cd "$(dirname "$0")"/../../.. || exit 1
    PATH=./src/main/bin:"$PATH"
    PATH=./src/test/script/launchers:"$PATH"
}

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

clean_temp_test_files() {
    find "$1" -type f \
        \( -name "*.ass" \
        -o -name "*_p2.deca" \
        -o -name "*_p3.deca" \
        -o -name "*.expected" \
        -o -name "*.res" \
        -o -name "*.class" \
        -o -name "*Bytecode.txt" \
        -o -name "*RawBytes.txt" \) \
        -delete
}

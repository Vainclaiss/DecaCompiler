#!/bin/sh

# Auteur : gl01
# Version initiale : 01/01/2025

. "$(dirname "$0")/utils.sh"

check_compilation() {
    ass_file="${1%.deca}.ass"
    if ! ./src/main/bin/decac "$1"; then
        if [ "$2" = true ]; then
            failure "Compilation failed for $1, but it was expected to succeed."
            exit 1
        fi
    # else
    #     if [ "$2" = false ]; then
    #         failure "Compilation succeeded for $1, but it was expected to fail."
    #         exit 1
    #     fi
    fi
    if [ "$2" = true ] && [ ! -f "$ass_file" ]; then
        failure "File $ass_file not generated for $1."
        exit 1
    fi
}

check_result() {
    ass_file="${1%.deca}.ass"
    awk '/\/\/ Resultats:/{flag=1; next} /^$/{flag=0} flag' "$1" | sed 's/^\s*//' | sed 's/\/\///' >"${1%.deca}.expected"
    ./global/bin/ima "$ass_file" >"${1%.deca}.res"

    if ! diff -B -w -q "${1%.deca}.expected" "${1%.deca}.res" >/dev/null; then
        failure "Incorrect result for $1."
        diff "${1%.deca}.expected" "${1%.deca}.res"
        exit 1
    fi
}

# Valid tests
make_valid_tests() {
    find ./src/test/deca/codegen/valid -type f -name '*.deca' | while read -r file; do
        check_gencode_file_format "$file"
        check_compilation "$file" true
        check_result "$file" true
        clean_temp_test_files ./src/test/deca/codegen/valid
        success "[valid] Test passed for $file"
    done

    # Make HeapOverflow
    check_gencode_file_format "src/test/deca/codegen/valid/created/LValueHell.deca"
    check_compilation "src/test/deca/codegen/valid/created/LValueHell.deca" true
    ass_file="src/test/deca/codegen/valid/created/LValueHell.ass"
    echo "Error: Heap Overflow" >"src/test/deca/codegen/valid/created/LValueHell.expected"
    ./global/bin/ima -t 10 "$ass_file" >"src/test/deca/codegen/valid/created/LValueHell.res"

    if ! diff -B -w -q "src/test/deca/codegen/valid/created/LValueHell.expected" "src/test/deca/codegen/valid/created/LValueHell.res" >/dev/null; then
        failure "Incorrect result for src/test/deca/codegen/valid/created/LValueHell.deca."
        diff "src/test/deca/codegen/valid/created/LValueHell.expected" "src/test/deca/codegen/valid/created/LValueHell.res"
        clean_temp_test_files ./src/test/deca/codegen/valid
        exit 1
    fi
    clean_temp_test_files ./src/test/deca/codegen/valid

    # Make StackOverflow
    check_gencode_file_format "src/test/deca/codegen/valid/created/minus.deca"
    check_compilation "src/test/deca/codegen/valid/created/minus.deca" true
    ass_file="src/test/deca/codegen/valid/created/minus.ass"
    echo "Error: Stack Overflow" >"src/test/deca/codegen/valid/created/minus.expected"
    ./global/bin/ima -p 1 "$ass_file" >"src/test/deca/codegen/valid/created/minus.res"

    if ! diff -B -w -q "src/test/deca/codegen/valid/created/minus.expected" "src/test/deca/codegen/valid/created/minus.res" >/dev/null; then
        failure "Incorrect result for src/test/deca/codegen/valid/created/minus.deca."
        diff "src/test/deca/codegen/valid/created/minus.expected" "src/test/deca/codegen/valid/created/minus.res"
        clean_temp_test_files ./src/test/deca/codegen/valid
        exit 1
    fi
    clean_temp_test_files ./src/test/deca/codegen/valid

}

# Invalid tests
make_invalid_tests() {
    find ./src/test/deca/codegen/invalid/created -type f -name '*.deca' | while read -r file; do
        check_gencode_file_format "$file"
        check_compilation "$file" false
        check_result "$file" false
        rm -f "${file%.deca}.ass" 2>/dev/null
        rm -f "${file%.deca}.res" 2>/dev/null
        rm -f "${file%.deca}.expected" 2>/dev/null
        success "[invalid] Test passed for $file"
    done
}

check_valid_interactive_result() {
    file=$1
    shift
    nb_args=$1
    shift
    input_args=""
    outputs=""
    count=1

    for arg in "$@"; do
        if [ $count -le "$nb_args" ]; then
            input_args="${input_args}${input_args:+\\n}$arg"
        else
            outputs="${outputs}${outputs:+\\n}$arg"
        fi
        count=$((count + 1))
    done

    input_args="${input_args}\\n"

    decac "$file"
    expected_output=$(echo "$outputs" | tr -d ' ')
    actual_output=$(echo "$input_args" | ima "${file%.deca}.ass" | tr -d ' ')

    if [ "$expected_output" != "$actual_output" ]; then
        failure "Incorrect interactive result for $file."
        echo "Expected: $expected_output"
        echo "Actual: $actual_output"
        rm -f "${file%.deca}.ass" 2>/dev/null
        exit 1
    fi
    rm -f "${file%.deca}.ass" 2>/dev/null
    success "[interactive valid] Tests passed for $file"
}

check_invalid_interactive_result() {
    file=$1
    shift
    nb_args=$1
    shift
    input_args=""
    outputs=""
    count=1

    for arg in "$@"; do
        if [ $count -le "$nb_args" ]; then
            input_args="${input_args}${input_args:+\\n}$arg"
        else
            outputs="$outputs $arg"
        fi
        count=$((count + 1))
    done

    input_args="${input_args}\\n"

    decac "$file"
    expected_output=$(echo "$outputs" | tr -d ' ')
    actual_output=$(echo "$input_args" | ima "${file%.deca}.ass" | tr -d ' ')

    if [ "$expected_output" = "$actual_output" ]; then
        failure "Incorrect interactive result for $file."
        echo "Expected: $expected_output"
        echo "Actual: $actual_output"
        rm -f "${file%.deca}.ass" 2>/dev/null
        exit 1
    fi
    rm -f "${file%.deca}.ass" 2>/dev/null

    success "[interactive invalid] Tests passed for $file"
}

make_invalid_interactive_tests() {
    i=1
    while [ $i -le 5 ]; do
        file="./src/test/deca/codegen/interactive/read${i}.deca"
        check_compilation "$file" false
        i=$((i + 1))
    done

    check_invalid_interactive_result ./src/test/deca/codegen/interactive/read1.deca 1 2 2
    check_invalid_interactive_result ./src/test/deca/codegen/interactive/read2.deca 2 2.0 3.0 5.00000e+00
    check_invalid_interactive_result ./src/test/deca/codegen/interactive/read3.deca 1 3.0 3.00000e+00
    check_invalid_interactive_result ./src/test/deca/codegen/interactive/read4.deca 2 2.0 3.0 5.00000e+00
    check_invalid_interactive_result ./src/test/deca/codegen/interactive/read5.deca 2 2.0 3 5.00000e+00
    check_invalid_interactive_result ./src/test/deca/codegen/interactive/read6.deca 2 2 3.00000e+00 2 3
}

make_valid_interactive_tests() {
    i=1
    while [ $i -le 6 ]; do
        file="./src/test/deca/codegen/interactive/read${i}.deca"
        check_compilation "$file" false
        i=$((i + 1))
    done

    check_valid_interactive_result ./src/test/deca/codegen/interactive/read1.deca 1 2 2.00000e+00
    check_valid_interactive_result ./src/test/deca/codegen/interactive/read2.deca 2 2 3.00000e+00 5.00000e+00
    check_valid_interactive_result ./src/test/deca/codegen/interactive/read3.deca 1 3 3.00000e+00
    check_valid_interactive_result ./src/test/deca/codegen/interactive/read4.deca 2 2 3 5.00000e+00
    check_valid_interactive_result ./src/test/deca/codegen/interactive/read5.deca 2 2 3.00000e+00 5.00000e+00
    check_valid_interactive_result ./src/test/deca/codegen/interactive/read6.deca 2 2 3.00000e+00 2 3.00000e+00
}

main() {
    prompt_strong "Running basic-gencode tests..."
    setup_path_and_cd
    clean_temp_test_files ./src/test/deca/codegen/
    make_valid_tests
    make_invalid_tests
    make_valid_interactive_tests
    make_invalid_interactive_tests
}

main

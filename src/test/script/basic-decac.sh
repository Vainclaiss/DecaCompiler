#! /bin/sh

# Test de l'interface en ligne de commande de decac.
. "$(dirname "$0")/utils.sh"

check_non_zero_status() {
    prompt_check "- non zero status"
    if [ "$1" = 0 ]; then
        failure "$2"
        exit "$1"
    fi
    return "$1"
}

check_zero_status() {
    prompt_check "- zero status"
    if [ "$1" != 0 ]; then
        failure "$2"
        exit "$1"
    fi
    return "$1"
}

check_no_output() {
    prompt_check "- no output"
    echo "$1"
    if [ "$1" != "" ]; then
        failure "$2"
        exit 1
    fi
}

check_output() {
    prompt_check "- output"
    echo "$1"
    if [ "$1" = "" ]; then
        failure "$2"
        exit 1
    fi
}

check_error() {
    prompt_check "- error"
    if ! (echo "$1" | grep -i -e "erreur" -e "error"); then
        failure "$2"
        exit 1
    fi
}

check_no_error() {
    prompt_check "- no error"
    if (echo "$1" | grep -i -e "erreur" -e "error"); then
        failure "$2"
        exit 1
    fi
}

check_log_level() {
    prompt_check "- log level"
    if ! (echo "$1" | grep -e "$2"); then
        failure "$3"
        exit 1
    fi
}

check_register_number() {
    prompt_check "- register number"
    for i in $(seq "$2" 16); do
        if (grep -e "R$i" "$1"); then
            failure "$3"
            exit 1
        fi
    done

}

test_uncompatible_options() {
    prompt_strong "[uncompatible options]"

    prompt "- decac -p -v"
    decac_p_v=$(decac -p -v ./src/test/deca/codegen/invalid/created/ArithmeticExp.deca)

    check_non_zero_status "$?" "ERREUR: decac -p -v a termine avec un status zero."
    check_output "$decac_p_v" "ERREUR: decac -p -v n'a produit aucune sortie."
    check_error "$decac_p_v" "ERREUR: decac -p -v n'a pas produit d'erreur."

    prompt "- decac -b [other options]"
    decac_b_and_other=$(decac -b -P ./src/test/deca/codegen/invalid/created/ArithmeticExp.deca)

    check_non_zero_status "$?" "ERREUR: decac -b [other options] a termine avec un status zero."
    check_output "$decac_b_and_other" "ERREUR: decac -b [other options] n'a produit aucune sortie."
    check_error "$decac_b_and_other" "ERREUR: decac -b [other options] n'a pas produit d'erreur."
    success "SUCCESS: test_uncompatible_options"
}

test_decac_b() {
    prompt_strong "[decac -b]"
    decac_moins_b=$(decac -b)

    check_zero_status "$?" "ERREUR: decac -b a termine avec un status different de zero."

    check_output "$decac_moins_b" "ERREUR: decac -b n'a produit aucune sortie."

    check_no_error "$decac_moins_b" "ERREUR: decac -b a produit une erreur."

    success "Pas de probleme detecte avec decac -b."
}

check_decompilation_idempotence() {
    prompt_check "- decac -p [idempotence]"
    decac -p "$1" >"${1%.deca}_p2.deca"
    decac -p "${1%.deca}_p2.deca" >"${1%.deca}_p3.deca"
    if ! diff "${1%.deca}_p2.deca" "${1%.deca}_p3.deca"; then
        failure "$2"
        exit 1
    fi
}

check_decompilation_idempotence_interactive() {
    prompt_check "- decac -p [idempotence interactive]"
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

    decac -p "$file" 1>"${file%.deca}_p2.deca"
    decac -p "${file%.deca}_p2.deca" 1>"${file%.deca}_p3.deca"
    if ! diff "${file%.deca}_p2.deca" "${file%.deca}_p3.deca"; then
        failure "$3"
        exit 1
    fi
    success "[interactive valid] Tests passed for $file"
}

test_decac_p() {
    prompt_strong "[decac -p]"
    run_decac_p_tests() {
        find "$1" -type f -name '*.deca' | while read -r file; do
            prompt "- decac -p $file"
            decac_moins_p=$(decac -p "$file")
            check_zero_status "$?" "ERREUR: decac -p a termine avec un status different de zero pour le fichier $file."
            if [ "$(basename "$file")" = "noMain.deca" ]; then
                check_no_output "$decac_moins_p" "ERREUR: decac -p a produit une sortie pour le fichier $file."
            else
                check_output "$decac_moins_p" "ERREUR: decac -p n'a produit aucune sortie pour le fichier $file."
            fi
            check_no_error "$decac_moins_p" "ERREUR: decac -p a produit une erreur pour le fichier $file."
            check_decompilation_idempotence "$file" "ERREUR: decac -p n'a pas produit de décompilation idempotente pour le fichier $file."
            clean_temp_test_files "src/test/deca/codegen/"
        done
    }

    # Valid tests
    run_decac_p_tests "src/test/deca/codegen/valid/created"

    # Interactive tests
    run_decac_p_tests "src/test/deca/codegen/interactive"

    success "SUCCESS: test_decac_p"
}

test_decac_v() {
    prompt_strong "[decac -v]"
    decac_moins_v=$(decac -v ./src/test/deca/codegen/valid/created/bool.deca)
    check_zero_status "$?" "ERREUR: decac -v a termine avec un status different de zero."
    check_no_output "$decac_moins_v" "ERREUR: decac -v a produit une sortie."
    check_no_error "$decac_moins_v" "ERREUR: decac -v a produit une erreur."
    # TODO : check also present of error message with an invalid file
    success "SUCCESS: test_decac_v"
}

test_decac_n() {
    prompt_strong "[decac -n]"
    decac_moins_n=$(decac -n ./src/test/deca/codegen/valid/created/bool.deca)
    check_zero_status "$?" "ERREUR: decac -n a termine avec un status different de zero."
    check_no_output "$decac_moins_n" "ERREUR: decac -n a produit une sortie."
    check_no_error "$decac_moins_n" "ERREUR: decac -n a produit une erreur."
    success "SUCCESS: test_decac_n"
}

test_decac_r() {
    # TODO : test on multiple files
    prompt_strong "[decac -r X] [Valid]"

    prompt "- deca -r [4 - 16]"
    for i in $(seq 4 16); do
        prompt "- deca -r $i"
        decac_moins_r=$(decac -r "$i" ./src/test/deca/codegen/valid/created/var1.deca)
        check_zero_status "$?" "ERREUR: decac -r $i a termine avec un status different de zero."
        check_no_output "$decac_moins_r" "ERREUR: decac -r $i a produit une sortie."
        check_no_error "$decac_moins_r" "ERREUR: decac -r $i a produit une erreur."
        check_register_number ./src/test/deca/codegen/valid/created/var1.ass "$i" "ERREUR: decac -r n'a pas produit le bon nombre de registres."
    done
    rm -f ./src/test/deca/codegen/valid/created/var1.ass

    prompt "- deca -r [Nothing]"
    decac_moins_r_error=$(decac -r ./src/test/deca/codegen/valid/created/var1.deca)
    check_non_zero_status "$?" "ERREUR: decac -r a terminé avec un status différent de zero."
    check_output "$decac_moins_r_error" "ERREUR: decac -r n'a produit aucune sortie."
    check_error "$decac_moins_r_error" "ERREUR: decac -r $i a produit une erreur."

    prompt_strong "[decac -r X] [Invalid]"

    for i in -1 0 3 17 18; do
        prompt "- decac -r $i"
        decac_moins_r_error=$(decac -r "$i" ./src/test/deca/codegen/valid/created/var1.deca)
        check_non_zero_status "$?" "ERREUR: decac -r $i a terminé avec un status différent de zero."
        check_output "$decac_moins_r_error" "ERREUR: decac -r $i n'a produit aucune sortie."
        check_error "$decac_moins_r_error" "ERREUR: decac -r $i a produit une erreur."
    done

    prompt "- decac -r a"
    decac_moins_r_error=$(decac -r a ./src/test/deca/codegen/valid/created/var1.deca)
    check_non_zero_status "$?" "ERREUR: decac -r a a terminé avec un status différent de zero."
    check_output "$decac_moins_r_error" "ERREUR: decac -r a n'a produit aucune sortie."
    check_error "$decac_moins_r_error" "ERREUR: decac -r a a produit une erreur."

    prompt "- decac -r ?"
    decac_moins_r_error=$(decac -r ? ./src/test/deca/codegen/valid/created/var1.deca)
    check_non_zero_status "$?" "ERREUR: decac -r ? a terminé avec un status différent de zero."
    check_output "$decac_moins_r_error" "ERREUR: decac -r ? n'a produit aucune sortie."
    check_error "$decac_moins_r_error" "ERREUR: decac -r ? a produit une erreur."

    success "SUCCESS: test_decac_r"
}

test_decac_d() {
    prompt_strong "[decac -d]"

    prompt "- decac -d"
    decac_moins_d=$(decac -d ./src/test/deca/codegen/valid/created/bool.deca)
    check_zero_status "$?" "ERREUR: decac -d a termine avec un status different de zero."
    check_output "$decac_moins_d" "ERREUR: decac -d n'a produit aucune sortie."
    # check_no_error "$decac_moins_d" "ERREUR: decac -d n'a pas produit d'erreur."
    echo "$decac_moins_d"
    check_log_level "$decac_moins_d" "INFO" "ERREUR: decac -d n'a pas produit de log de niveau INFO."

    prompt "- decac -d -d"
    decac_moins_d=$(decac -d -d ./src/test/deca/codegen/valid/created/bool.deca)
    check_zero_status "$?" "ERREUR: decac -d a termine avec un status different de zero."
    check_output "$decac_moins_d" "ERREUR: decac -d n'a produit aucune sortie."
    # check_no_error "$decac_moins_d" "ERREUR: decac -d n'a pas produit d'erreur."
    echo "$decac_moins_d"
    check_log_level "$decac_moins_d" "DEBUG" "ERREUR: decac -d n'a pas produit de log de niveau DEBUG."

    prompt "- decac -d -d -d"
    decac_moins_d=$(decac -d -d -d ./src/test/deca/codegen/valid/created/bool.deca)
    check_zero_status "$?" "ERREUR: decac -d a termine avec un status different de zero."
    check_output "$decac_moins_d" "ERREUR: decac -d n'a produit aucune sortie."
    # check_no_error "$decac_moins_d" "ERREUR: decac -d n'a pas produit d'erreur."
    echo "$decac_moins_d"
    check_log_level "$decac_moins_d" "TRACE" "ERREUR: decac -d n'a pas produit de log de niveau TRACE."

    prompt "- decac -d -d -d -d"
    decac_moins_d=$(decac -d -d -d -d ./src/test/deca/codegen/valid/created/bool.deca)
    check_zero_status "$?" "ERREUR: decac -d a termine avec un status different de zero."
    check_output "$decac_moins_d" "ERREUR: decac -d n'a produit aucune sortie."
    # check_no_error "$decac_moins_d" "ERREUR: decac -d n'a pas produit d'erreur."
    # TODO : find a way to check error without catching the overflow_error from ASM
    echo "$decac_moins_d"
    check_log_level "$decac_moins_d" "ALL" "ERREUR: decac -d n'a pas produit de log de niveau ALL."

    success "SUCCESS: test_decac_d"
}

test_decac_P() {
    # TODO: find a better way to test parrallelized compilation
    prompt_strong "[decac -P]"
    decac_moins_P=$(decac -P ./src/test/deca/codegen/valid/created/bool.deca ./src/test/deca/codegen/valid/created/bool2.deca)
    check_zero_status "$?" "ERREUR: decac -P a termine avec un status different de zero."
    check_no_output "$decac_moins_P" "ERREUR: decac -P a produit une sortie."
    check_no_error "$decac_moins_P" "ERREUR: decac -P a produit une erreur."
    success "SUCCESS: test_decac_d"
}


test_decac_a() {
    # TODO : test on multiple files
    prompt_strong "[decac -a X] [Valid]"

    prompt "- deca -a [Options]"
    for i in TONEAREST UPWARD DOWNWARD TOWARDZERO; do
        prompt "- deca -a $i"
        decac_moins_r=$(decac -a "$i" ./src/test/deca/codegen/valid/created/var1.deca)
        check_zero_status "$?" "ERREUR: decac -a $i a termine avec un status different de zero."
        check_no_output "$decac_moins_r" "ERREUR: decac -a $i a produit une sortie."
        check_no_error "$decac_moins_r" "ERREUR: decac -a $i a produit une erreur."
        # TODO: check arithmetical round
    done
    rm -f ./src/test/deca/codegen/valid/created/var1.ass

    prompt "- deca -a [Nothing]"
    decac_moins_r_error=$(decac -a ./src/test/deca/codegen/valid/created/var1.deca)
    check_non_zero_status "$?" "ERREUR: decac -a à terminé avec un status différent de zero."
    check_output "$decac_moins_r_error" "ERREUR: decac -a n'a produit aucune sortie."
    check_error "$decac_moins_r_error" "ERREUR: decac -a $i a produit une erreur."

    prompt_strong "[decac -a X] [Invalid]"

    #TODO: is there a maximum numbers after dot ?
    # for i in 17 18; do
    #     prompt "- decac -a $i"
    #     decac_moins_r_error=$(decac -a "$i" ./src/test/deca/codegen/valid/created/var1.deca)
    #     check_non_zero_status "$?" "ERREUR: decac -a $i a terminé avec un status différent de zero."
    #     check_output "$decac_moins_r_error" "ERREUR: decac -a $i n'a produit aucune sortie."
    #     check_error "$decac_moins_r_error" "ERREUR: decac -a $i a produit une erreur."
    # done

    for i in -1 a ?; do
        prompt "- decac -a $i"
        decac_moins_r_error=$(decac -a "$i" ./src/test/deca/codegen/valid/created/var1.deca)
        check_non_zero_status "$?" "ERREUR: decac -a $i a terminé avec un status différent de zero."
        check_output "$decac_moins_r_error" "ERREUR: decac -a $i n'a produit aucune sortie."
        check_error "$decac_moins_r_error" "ERREUR: decac -a $i a produit une erreur."
    done

    success "SUCCESS: test_decac_a"
}


check_java_execution(){ 
    class_file="${1%.deca}"
    awk '/\/\/ Resultats:/{flag=1; next} /^$/{flag=0} flag' "$1" | sed 's/^\s*//' | sed 's/\/\///' >"${1%.deca}.expected"
    java -cp ./"$(dirname "$1")" "$(basename "$class_file")" >"${1%.deca}.res"

    if ! diff -B -w -q "${1%.deca}.expected" "${1%.deca}.res" >/dev/null; then
        failure "Incorrect result for $1."
        diff "${1%.deca}.expected" "${1%.deca}.res"
        clean_temp_test_files "src/test/deca/codegen/"
        exit 1;
    fi
}


test_decac_e() {
    prompt_strong "[decac -e]"
    find "src/test/deca/codegen/valid/created" -type f -name '*.deca' | while read -r file; do
        if [ "$(basename "$file")" = "printCarriageReturn.deca" ]; then
            continue
        fi
        prompt "- decac -e $file"
        decac_moins_p=$(decac -e "$file")
        check_zero_status "$?" "ERREUR: decac -e a termine avec un status different de zero pour le fichier $file."
        check_no_output "$decac_moins_p" "ERREUR: decac -e a produit une sortie pour le fichier $file."
        check_no_error "$decac_moins_p" "ERREUR: decac -e a produit une erreur pour le fichier $file."
        check_java_execution "$file" "ERREUR: decac -e a produit une décompilation .class qui donne un résultat différent sur le fichier $file."
        clean_temp_test_files "src/test/deca/codegen/"
    done

    success "SUCCESS: test_decac_e"
}



main() {
    setup_path_and_cd
    clean_temp_test_files "src/test/deca/codegen"
    test_uncompatible_options
    test_decac_b
    test_decac_p
    test_decac_v
    test_decac_n
    test_decac_r
    test_decac_d
    test_decac_P
    test_decac_w
    test_decac_a
    test_decac_e
}

main

#!/bin/sh

scripts="
    ./basic-lex.sh
    ./basic-synt.sh
    ./basic-context.sh
    ./basic-gencode.sh
    ./basic-decac.sh
    ./common-tests.sh
"

for script in $scripts; do
    $script
done

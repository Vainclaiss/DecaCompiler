#! /bin/sh

# Auteur : gl01
# Version initiale : 01/01/2025

# Encore un test simpliste. On compile un fichier (cond0.deca), on
# lance ima dessus, et on compare le résultat avec la valeur attendue.

# Ce genre d'approche est bien sûr généralisable, en conservant le
# résultat attendu dans un fichier pour chaque fichier source.
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:./src/main/bin:"$PATH"

check_gencode_file_format() {
    if ! grep -q -e ".*\/\/ Description:.*" "$1"; then
        echo "Fichier $1 invalide: pas de ligne de description."
        exit 1
    fi
    if ! grep -q -e ".*\/\/ Resultats:.*" "$1"; then
        echo "Fichier $1 invalide: pas de ligne de résultats."
        exit 1
    fi
    if ! grep -q -e ".*\/\/ Historique:.*" "$1"; then
        echo "Fichier $1 invalide: pas de ligne d'historique."
        exit 1
    fi
}

# On teste tous les fichiers .deca dans le répertoire spécifié
for file in ./src/test/deca/codegen/valid/created/*.deca; 
do
    check_gencode_file_format "$file";
    echo "$file"
    awk '/\/\/ Resultats:/{flag=1; next} /^\/\/ Historique:/{flag=0} flag' "$file" | sed 's/^\/\/ *//' | tr -d '\n'
    # ass_file="${file%.deca}.ass"
    # rm -f "$ass_file" 2>/dev/null
    # decac "$file" || exit 1
    # if [ ! -f "$ass_file" ]; then
    #     echo "Fichier $ass_file non généré pour $file."
    #     exit 1
    # else
    #     echo "Oui"
    # fi
done

# resultat=$(ima ./src/test/deca/codegen/valid/provided/cond0.ass) || exit 1
# rm -f ./src/test/deca/codegen/valid/provided/cond0.ass

# # On code en dur la valeur attendue.
# attendu=ok

# if [ "$resultat" = "$attendu" ]; then
#     echo "Tout va bien"
# else
#     echo "Résultat inattendu de ima:"
#     echo "$resultat"
#     exit 1
# fi

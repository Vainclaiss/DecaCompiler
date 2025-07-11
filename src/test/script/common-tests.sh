#! /bin/sh

# Quelques tests que tous les compilateurs devraient passer.
# Le code est volontairement peu illisible : ce n'est pas un exemple à
# suivre pour écrire une belle base de tests.
#
die() {
    echo "\033[1;31mERREUR :\033[0m" "$@"
    exit 1
}
if [ "$GL_EVALUATION_ONGOING" = "" ]; then PATH=$(dirname "$0")/../../../src/main/bin:"$PATH"; fi
DECAC=$(cd "$(dirname "$(command -v decac)")" && pwd)/decac
[ -x "$DECAC" ] || die "commande decac non-trouvée"
echo "Test de l'executable:"
echo "$DECAC"
test_dir=$(dirname "$DECAC")/../../test/deca
cd "$test_dir" 2>/dev/null || die "repertoire $test_dir inaccessible"
(cd "$test_dir" && find . -name '*.deca' | grep -E -v '^[0-9ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_/.+-]*$' 2>/dev/null) && die "caracteres illicites dans les noms des tests ou des repertoires"
decac() { (
    ulimit -t 10 -f 100 2>/dev/null || true
    "$DECAC" "$@" 2>&1
); }
tmpdir=.minimal-test.$$
mkdir "$tmpdir" || die "Impossible de créer le répertoire de test"
(
    cd "$tmpdir" || die "Impossible de changer de répertoire"
    mkdir source || die "Impossible de créer un répertoire dans $PWD"
    echo '{println(}' >source/syntax-ko.deca
    echo '{println(x);}' >source/syntax-ok-verif-ko.deca
    echo '{println("Hello");}' >source/hello.deca


    decac -p source/hello.deca >hello-p.deca || die "Échec de la compilation d'un programme Deca simple avec -p"
    [ -s hello-p.deca ] || die "decac -p n'a pas généré de sortie sur un programme Deca simple"
    decac -p hello-p.deca >/dev/null || die "decac -p a produit une sortie qui n'est pas un programme Deca valide"
    ! decac -p source/syntax-ko.deca >sortie.txt 2>&1 || die "decac -p n'a pas terminé avec un status différent de 0 sur un programme incorrect"
    grep -qi "source/syntax-ko.deca:1:" sortie.txt || die "decac -p n'a pas affiche de message d'erreur correctement"
    decac -p source/syntax-ok-verif-ko.deca >sortie.txt 2>&1 || die "decac -p a échoue sur un programme invalide mais syntaxiquement correct"
    [ -s sortie.txt ] || die "decac -p n'a pas produit une sortie sur un programme invalide mais syntaxiquement correct"
    ! grep -qi "source/syntax-ok-verif-ko.deca:1:" sortie.txt || die "decac -p a affiché une erreur sur un programme syntaxiquement correct"
    decac -v source/hello.deca >sortie.txt 2>&1 || die "Échec de la compilation d'un programme Deca simple avec -v"
    [ ! -s sortie.txt ] || die "decac -v a produit une sortie sur un fichier correct"
    ! decac -v source/syntax-ok-verif-ko.deca >sortie.txt 2>&1 || die "decac -v n'a pas échoué sur une variable non-déclarée"
    [ -s sortie.txt ] || die "decac -v n'a pas produit de sortie sur une variable non-déclarée"
    grep -qi "source/syntax-ok-verif-ko.deca:1:" sortie.txt || die "decac -v n'a pas produit de message d'erreur sur une variable non-déclarée"
    decac source/hello.deca >sortie.txt 2>&1 || die "Échec de la compilation d'un programme Deca simple"
    [ ! -s sortie.txt ] || die "deca a produit une sortie sur un programme correct"
    [ ! -f hello.ass ] || die "decac a généré le fichier .ass dans le répertoire courant.;Il devrait être généré au même endroit que le fichier source."
    [ -f source/hello.ass ] || die "decac n'a pas généré le fichier .ass au bon endroit"
    ./../../../../global/bin/ima source/hello.ass >sortie || die "Erreur lors de l'exécution du code généré"
    echo "Hello" >attendu
    diff sortie attendu || die "L'exécution du code généré pour un hello-world est incorrecte"
    echo "Les fonctionnalités de base semblent opérationnelles."
)
status=$?
rm -fr "$tmpdir"
exit "$status"

# 🧾 Compilateur Deca – Projet de Génie Logiciel

Ce dépôt contient l’implémentation d’un compilateur pour le langage **Deca**, réalisé dans le cadre du module de Génie Logiciel.  
Il comprend à la fois le code source du compilateur et l’ensemble des documents produits tout au long du projet : conception, validation, extensions, manuel utilisateur, bilan d’équipe, etc.

## 📂 Structure du dépôt

- `src/` : code source du compilateur (analyse lexicale, syntaxique, sémantique, génération de code)
- `.docs/` : tous les livrables et documents du projet (PDF)
- `tests/` : fichiers de test pour valider le bon fonctionnement du compilateur
- `scripts/` : scripts d’installation, de compilation ou de test

## 📖 Documentation (dans `.docs/`)

- **[Manuel_Utilisateur.pdf](.docs/Manuel_Utilisateur.pdf)** : guide complet pour compiler et exécuter des programmes Deca avec ce compilateur.
- **[Document_de_conception.pdf](.docs/Document_de_conception.pdf)** : détails sur la conception technique, l'architecture logicielle, et les choix de développement.
- **[Document_de_validation.pdf](.docs/Document_de_validation.pdf)** : description des tests, outils de vérification, et stratégie de validation du compilateur.
- **[Document_Extension.pdf](.docs/Document_Extension.pdf)** : extensions ajoutées au langage ou au compilateur.
- **[Analyse_de_l_impact_energetique.pdf](.docs/Analyse_de_l_impact_energetique.pdf)** : étude sur l’impact énergétique du compilateur.
- **[Bilan_Equipe.pdf](.docs/Bilan_Equipe.pdf)** : répartition des tâches et retour d’expérience de l’équipe.
- **[Soutenance_GL.pdf](.docs/Soutenance_GL.pdf)** : diapositive de soutenance du projet.

## ▶️ Utilisation

```bash
# Compilation du compilateur
./compile.sh

# Compilation d’un programme Deca
java -jar decac.jar exemple.deca

# Exécution sur machine virtuelle
ima exemple.ass

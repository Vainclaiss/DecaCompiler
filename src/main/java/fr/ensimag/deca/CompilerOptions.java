package fr.ensimag.deca;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ensimag.ima.pseudocode.Register;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl01
 * @date 01/01/2025
 */
public class CompilerOptions {
    public static final int QUIET = 0;
    public static final int INFO = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;

    public int getDebugValue() {
        return debugValue;
    }

    public boolean getParallel() {
        return parallel;
    }

    public boolean getPrintBanner() {
        return printBanner;
    }

    public boolean getSkipExecErrors() {
        return skipExecErrors;
    }

    public int getArithmeticalPrecision() {
        return arithmeticalPrecision;
    }

    // public static int getNumRegisters() {
    // return numRegisters;
    // }

    public boolean getStopAfterParse() {
        return stopAfterParse;
    }

    public boolean getStopAfterVerification() {
        return stopAfterVerification;
    }

    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }

    private int debugValue = 0;
    private int arithmeticalPrecision = 2;
    // private static int numRegisters = 16; // TODO : chiant mais pratique le
    // static
    private boolean parallel = false;
    private boolean printBanner = false;
    private boolean skipExecErrors = false;
    private boolean stopAfterParse = false;
    private boolean stopAfterVerification = false;
    private List<File> sourceFiles = new ArrayList<>();

    public void parseArgs(String[] args) throws CLIException {
        Logger logger = Logger.getRootLogger();
        Set<String> options = new HashSet<>(Arrays.asList(args));
        debugValue = Collections.frequency(Arrays.asList(args), "-d");
        // map command-line debug option to log4j's level.
        switch (getDebugValue()) {
            case QUIET:
                break; // keep default
            case INFO:
                logger.setLevel(Level.INFO);
                break;
            case DEBUG:
                logger.setLevel(Level.DEBUG);
                break;
            case TRACE:
                logger.setLevel(Level.TRACE);
                break;
            default:
                logger.setLevel(Level.ALL);
                break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        // WARNING:
        // Pas capté pourquoi ils mettent assert assertsEnabled = true;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }

        // TODO : finir les arguments pour decac
        // if (options.contains("-w")) {
        // throw new NotImplementedException("Argument not yet implemented");
        // }

        if (options.contains("-r")) {
            parseRCommand(args);
        }
        if (options.contains("-a")) {
            parseACommand(args);
        }

        if (options.contains("-p") && options.contains("-v")) {
            throw new CLIException("Cannot use both -p and -v options");
        }
        if (options.contains("-b") && options.size() > 1) {
            throw new CLIException("Cannot use -b with other arguments");
        }

        parallel = options.contains("-P");
        printBanner = options.contains("-b");
        skipExecErrors = options.contains("-n");
        stopAfterParse = options.contains("-p");
        stopAfterVerification = options.contains("-v");

        for (String arg : options) {
            if (arg.endsWith(".deca")) {
                logger.debug("Added file with path :" + arg);
                sourceFiles.add(new File(arg));
            }
        }
        if (!options.contains("-b") && sourceFiles.isEmpty()) {
            throw new CLIException("No source file .deca specified");
        }

    }

    private static void parseRCommand(String[] args) throws CLIException {
        int rArgumentIndex = Arrays.asList(args).indexOf("-r");
        if (args.length - 2 < rArgumentIndex + 1) {
            throw new CLIException("Cannot use the -r option without specifying the number of registers");
        }

        try {
            Register.RMAX = Integer.parseInt(args[rArgumentIndex + 1]) - 1;
        } catch (NumberFormatException e) {
            throw new CLIException("The argument for -r must be an integer");
        }

        if (Register.RMAX < 3 || Register.RMAX > 15) { // RMAX = nbRegisters-1
            throw new CLIException("Invalid number of registers (must be between 4 and 16)");
        }
    }

    private void parseACommand(String[] args) throws CLIException {
        int aArgumentIndex = Arrays.asList(args).indexOf("-a");
        if (args.length - 2 < aArgumentIndex + 1) {
            throw new CLIException("Cannot use the -a option without specifying the precision");
        }
        try {
            arithmeticalPrecision = Integer.parseInt(args[aArgumentIndex + 1]);
        } catch (NumberFormatException e) {
            throw new CLIException("The argument for -a must be an integer");
        }

        if (arithmeticalPrecision < 0) {
            throw new CLIException("Invalid precision (must be positive)");
        }
    }

    protected void displayUsage() {
        System.out.println("Usage: decac [[-p | -v] [-n] [-r X] [-d]* [-P] [-w] [-a] <fichier .deca>...] | [-b]");
        System.out.println(
                "La commande decac, sans argument, affichera les options disponibles. On peut appeler la commande");
        System.out.println("decac avec un ou plusieurs fichiers sources Deca.");
        System.out.println("Options:");
        System.out.println("  -b (banner) : affiche une bannière indiquant le nom de l'équipe");
        System.out.println("  -p (parse) : arrête decac après l'étape de construction de");
        System.out.println("               l'arbre, et affiche la décompilation de ce dernier");
        System.out.println("               (i.e. s'il n'y a qu'un fichier source à");
        System.out.println("               compiler, la sortie doit être un programme");
        System.out.println("               deca syntaxiquement correct)");
        System.out.println("  -v (verification) : arrête decac après l'étape de vérifications");
        System.out.println("                      (ne produit aucune sortie en l'absence d'erreur)");
        System.out.println("  -n (no check) : supprime les tests à l'exécution spécifiés dans");
        System.out.println("                  les points 11.1 et 11.3 de la sémantique de Deca.");
        System.out.println("  -r X (registers) : limite les registres banalisés disponibles à");
        System.out.println("                     R0 ... R{X-1}, avec 4 <= X <= 16");
        System.out.println("  -d (debug) : active les traces de debug. Répéter");
        System.out.println("               l'option plusieurs fois pour avoir plus de");
        System.out.println("               traces.");
        System.out.println("  -P (parallel) : s'il y a plusieurs fichiers sources,");
        System.out.println("                  lance la compilation des fichiers en");
        System.out.println("                  parallèle (pour accélérer la compilation)");
        System.out.println("  -w (warning) : active l'affichage des messages d'avertissement");
        System.out.println("                 en cours de compilation.");
        System.out.println("  -a X (arithmetic rounding) : fixe le mode d'arrondi à X chiffres après");
        System.out.println("                la virgule pour les opérations arithmétiques. DÉFAUT À 2");
    }

    protected void displayBanner() {
        System.out.println("                                _                       ");
        System.out.println("                              .' `'.__                  ");
        System.out.println("                             /      \\ `'\"-,             Équipe 1");
        System.out.println("            .-''''--...__..-/ .     |      \\            ");
        System.out.println("          .'               ; :'     '.  a   |           ROBOAM          Guillaume");
        System.out.println("         /                 | :.       \\     =\\          TALBI           Mehdi");
        System.out.println("        ;                   \\':.      /  ,-.__;.-;`     CHARLES-MENNIER Matéo");
        System.out.println("       /|     .              '--._   /-.7`._..-;`       ALTIERI         Aubin");
        System.out.println("      ; |       '                |`-'      \\  =|        RABALLAND       Cyprien");
        System.out.println("      |/\\        .   -' /     /  ;         |  =/        ");
        System.out.println("      (( ;.       ,_  .:|     | /     /\\   | =|          _____ _         __ ");
        System.out.println("       ) / `\\     | `\"\"`;     / |    | /   / =/         / ____| |       /_ |");
        System.out.println("         | ::|    |      \\    \\ \\    \\ `--' =/         | |  __| |        | | ");
        System.out.println("        /  '/\\    /       )    |/     `-...-`          | | |_ | |        | |");
        System.out.println("       /    | |  `\\    /-'    /;                       | |__| | |_____   | |");
        System.out.println("       \\  ,,/ |    \\   D    .'  \\                       \\_____|______|   |_|");
        System.out.println("        `\"\"`   \\  nnh  D_.-'L__nnh                    ");
        System.out.println();
        System.out.println("Art by Joan G. Stark");
    }
}

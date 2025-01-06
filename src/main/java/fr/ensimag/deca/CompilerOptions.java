package fr.ensimag.deca;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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

    public boolean getSkipDecaCompilationErrors() {
        return skipDecaCompilationErrors;
    }

    public int getArithmeticalPrecision() {
        return arithmeticalPrecision;
    }

    public int getNumRegisters() {
        return numRegisters;
    }

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
    private int numRegisters = 16;
    private boolean parallel = false;
    private boolean printBanner = false;
    private boolean skipDecaCompilationErrors = false;
    private boolean stopAfterParse = false;
    private boolean stopAfterVerification = false;
    private List<File> sourceFiles = new ArrayList<>();

    public void parseArgs(String[] args) throws CLIException {
        Logger logger = Logger.getRootLogger();
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

        Set<String> options = new HashSet<>(Arrays.asList(args));
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
        skipDecaCompilationErrors = options.contains("-n");
        stopAfterParse = options.contains("-p");
        stopAfterVerification = options.contains("-v");

        for (String arg : args) {
            if (arg.endsWith(".deca")) {
                logger.debug("Added file with path :" + arg);
                sourceFiles.add(new File(arg));
            }
        }

    }

    private void parseRCommand(String[] args) throws CLIException {
        int rArgumentIndex = Arrays.asList(args).indexOf("-r");
        if (args.length - 1 < rArgumentIndex + 1) {
            throw new CLIException("Cannot use the -r option without specifying the number of registers");
        }

        try {
            numRegisters = Integer.parseInt(args[rArgumentIndex + 1]);
        } catch (NumberFormatException e) {
            throw new CLIException("The argument for -r must be an integer");
        }

        if (numRegisters < 4 || numRegisters > 16) {
            throw new CLIException("Invalid number of registers (must be between 4 and 16)");
        }
    }

    private void parseACommand(String[] args) throws CLIException {
        int aArgumentIndex = Arrays.asList(args).indexOf("-a");
        if (args.length - 1 < aArgumentIndex + 1) {
            throw new CLIException("Cannot use the -a option without specifying the precision");
        }
        try {
            arithmeticalPrecision = Integer.parseInt(args[aArgumentIndex + 1]);
        } catch (NumberFormatException e) {
            throw new CLIException("The argument for -r must be an integer");
        }

        if (arithmeticalPrecision < 0) {
            throw new CLIException("Invalid precision (must be positive)");
        }
    }

    protected void displayUsage() {
        System.err.println("Usage: decac [[-p | -v] [-n] [-r X] [-d]* [-P] [-w] [-a] <fichier .deca>...] | [-b]");
        System.err.println(
                "La commande decac, sans argument, affichera les options disponibles. On peut appeler la commande");
        System.err.println("decac avec un ou plusieurs fichiers sources Deca.");
        System.err.println("Options:");
        System.err.println("  -b (banner) : affiche une bannière indiquant le nom de l'équipe");
        System.err.println("  -p (parse) : arrête decac après l'étape de construction de");
        System.err.println("               l'arbre, et affiche la décompilation de ce dernier");
        System.err.println("               (i.e. s'il n'y a qu'un fichier source à");
        System.err.println("               compiler, la sortie doit être un programme");
        System.err.println("               deca syntaxiquement correct)");
        System.err.println("  -v (verification) : arrête decac après l'étape de vérifications");
        System.err.println("                      (ne produit aucune sortie en l'absence d'erreur)");
        System.err.println("  -n (no check) : supprime les tests à l'exécution spécifiés dans");
        System.err.println("                  les points 11.1 et 11.3 de la sémantique de Deca.");
        System.err.println("  -r X (registers) : limite les registres banalisés disponibles à");
        System.err.println("                     R0 ... R{X-1}, avec 4 <= X <= 16");
        System.err.println("  -d (debug) : active les traces de debug. Répéter");
        System.err.println("               l'option plusieurs fois pour avoir plus de");
        System.err.println("               traces.");
        System.err.println("  -P (parallel) : s'il y a plusieurs fichiers sources,");
        System.err.println("                  lance la compilation des fichiers en");
        System.err.println("                  parallèle (pour accélérer la compilation)");
        System.err.println("  -w (warning) : active l'affichage des messages d'avertissement.");
        System.err.println("  -a X (arithmetic rounding) : fixe le mode d'arrondi à X chiffres après");
        System.err.println("                la virgule pour les opérations arithmétiques. DÉFAUT À 2");
    }

    protected void displayBanner() {
        System.err.println("      /\\  ___  /\\             Équipe 1                    ");
        System.err.println("     // \\/   \\/ \\\\              GL01                      ");
        System.err.println("    ((    O O    ))                                         ");
        System.err.println("     \\\\ /     \\ //           RABALLAND       Cyprien     ");
        System.err.println("      \\/  | |  \\/            ROBOAM          Guillaume   ");
        System.err.println("       |  | |  |             TALBI EL        Mehdi       ");
        System.err.println("       |  | |  |             CHARLES-MENNIER Matéo       ");
        System.err.println("       |   o   |             ALTIERI         Aubin       ");
        System.err.println("       | |   | |                                            ");
        System.err.println("       |m|   |m|                                            ");
    }
}

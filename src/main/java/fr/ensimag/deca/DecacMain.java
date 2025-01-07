package fr.ensimag.deca;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import fr.ensimag.deca.syntax.DecaLexer;
import fr.ensimag.ima.pseudocode.Label;

/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl01
 * @date 01/01/2025
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);

    public static void main(String[] args) {
        LOG.info("Decac compiler started");
        boolean error = false;
        final CompilerOptions options = new CompilerOptions();
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            System.err.println("Error during option parsing:\n"
                    + e.getMessage());
            options.displayUsage();
            System.exit(1);
        }
        if (options.getPrintBanner()) {
            options.displayBanner();
        } else if (options.getSourceFiles().isEmpty()) {
            options.displayUsage();
        }
        if (options.getParallel()) {
            error = launchParallelCompilation(options);
        } else {
            error = launchNonParallelCompilation(options);

        }
        System.exit(error ? 1 : 0);
    }

    private static boolean launchNonParallelCompilation(CompilerOptions options) {
        boolean error = false;
        for (File source : options.getSourceFiles()) {
            DecacCompiler compiler = new DecacCompiler(options, source);
            Label.resetSuffixId();
            error = compiler.compile();
        }
        return error;
    }

    private static boolean launchParallelCompilation(CompilerOptions options) {
        ExecutorService threadPool = Executors
                .newFixedThreadPool(java.lang.Runtime.getRuntime().availableProcessors());
        List<Future<Boolean>> futureList = new ArrayList<>();
        boolean error = false;
        for (File source : options.getSourceFiles()) {
            DecacCompiler compiler = new DecacCompiler(options, source);
            futureList.add(threadPool.submit(compiler::compile));
        }
        for (Future<Boolean> future : futureList) {

            try {
                if (Boolean.TRUE.equals(future.get())) {
                    error = true;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return error;
    }
}

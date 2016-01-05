package com.timreset.arduino.parser;

import com.timreset.arduino.BaseArduino;
import org.eclipse.jdt.core.dom.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tim
 * @date 19.11.2015
 */
public class Parser {
    
    private final static String libraryPackage = "com.timreset.arduino.library";

    private Set<String> foundedUsedLibs = new HashSet<>();
    private final String sourceCode;
    private final String sourceFileName;
    private final String[] classpathEntries;

    public Parser(@Nonnull Path sourceFile, String... classpathEntries) throws IOException {
        sourceCode = Files.readAllLines(sourceFile).stream().collect(Collectors.joining("\n"));
        sourceFileName = sourceFile.getFileName().toString();
        this.classpathEntries = classpathEntries;
    }

    @Nonnull
    public String parseWithVisitor() {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource(sourceCode.toCharArray());
        //parser.setSource("abc".toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        parser.setEnvironment( // apply classpath
                classpathEntries, //
                null, null, true);
        parser.setUnitName(sourceFileName);
        StringBuilder generatedCode = new StringBuilder();
        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        TypeDeclaration arduinoType = getArduinoTypeOrThrow(cu);
        for (ASTNode o : (List<? extends ASTNode>) arduinoType.bodyDeclarations()) {
            VisitorForC visitorForC = new VisitorForC();
            o.accept(visitorForC);
            generatedCode.append(visitorForC.getResult());
            generatedCode.append("\n");
            foundedUsedLibs.addAll(visitorForC.getFoundedUsedLibs());
        }
        for (String foundedUsedLib : foundedUsedLibs) {
            generatedCode.insert(0, "#include <" + foundedUsedLib + ".h>\n");
        }
        return generatedCode.toString();
    }


    public static void main(String[] args) throws IOException {
        final String inputFileName = args[0];
        final String outputFileName = args[1];
        System.out.println("Input file name: " + inputFileName);
        System.out.println("Output file name: " + outputFileName);
        final String source = new Parser(Paths.get(inputFileName), Arrays.copyOfRange(args, 2, args.length)).parseWithVisitor();
        Path inoFile = Paths.get(outputFileName);
        Files.createDirectories(inoFile.getParent());
        Files.write(inoFile, Collections.singletonList(source), StandardOpenOption.CREATE);
    }

    /**
     * @throws IllegalStateException if it is not Arduino unit
     */
    @Nonnull
    private TypeDeclaration getArduinoTypeOrThrow(CompilationUnit cu) {
        List<TypeDeclaration> typeDeclarations = new ArrayList<>();
        cu.accept(new ASTVisitor() {
            @Override
            public boolean visit(TypeDeclaration node) {
                typeDeclarations.add(node);
                return true;
            }
        });
        if (typeDeclarations.size() != 1) {
            throw new IllegalStateException("Not one type!");
        }
        TypeDeclaration typeDeclaration = typeDeclarations.get(0);
        Type base = typeDeclaration.getSuperclassType();
        if (!BaseArduino.class.getName().equals(base.resolveBinding().getQualifiedName())) {
            throw new IllegalStateException("Should be " + BaseArduino.class.getName());
        }
        //		typeDeclaration.ge
        //		SuperTypeNamesCollector.TypeDeclarationVisitor v = new SuperTypeNamesCollector.TypeDeclarationVisitor(); 
        //		    cu.accept();
        return typeDeclaration;
    }

}

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

	@Nonnull
	public String parse() {
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
			final String addedString;
			if (o instanceof MethodDeclaration) {
				MethodDeclaration node = (MethodDeclaration) o;
				addedString = parseMethodDeclaration(node);
			} else if (o instanceof FieldDeclaration) {
				FieldDeclaration node = (FieldDeclaration) o;
				List<? extends IExtendedModifier> m = node.modifiers();
				if (hasClassInstanceCreation(node)) {
					Type type = node.getType();
					foundedUsedLibs.add(getLibraryPackage(type));
					addedString = getClassInstantiates(node.getType(), node.fragments());
				} else {
					clearPrivateAndFinal(m);
					addedString = o.toString();
				}
			} else {
				addedString = o.toString();
			}
			generatedCode.append(addedString);
			generatedCode.append("\n");
			//			System.out.println(o);
		}
		for (String foundedUsedLib : foundedUsedLibs) {
			generatedCode.insert(0, "#include <" + foundedUsedLib + ".h>\n");
		}
		return generatedCode.toString();
	}

	private final static String libraryPackage = "com.timreset.arduino.library";

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

	@Nonnull
	private String parseMethodDeclaration(@Nonnull MethodDeclaration methodDeclaration) {
		StringBuilder stringBuilder = new StringBuilder(" ");
		stringBuilder.append(methodDeclaration.getReturnType2()).append(" ").append(methodDeclaration.getName()).append(
						"(){\n");
		Block b = methodDeclaration.getBody();
		for (ASTNode o : (List<? extends ASTNode>) b.statements()) {
			final String addedString;
			if (o instanceof VariableDeclarationStatement) {
				VariableDeclarationStatement node = (VariableDeclarationStatement) o;
				if (hasClassInstanceCreation(node)) {
					Type type = node.getType();
					foundedUsedLibs.add(getLibraryPackage(type));
					addedString = getClassInstantiates(node.getType(), node.fragments());
				} else {
					clearPrivateAndFinal(node.modifiers());
					addedString = node.toString();
				}
			} else {
				addedString = o.toString();
			}
			stringBuilder.append(" ").append(addedString).append("\n");
		}
		stringBuilder.append(" }\n");
		return stringBuilder.toString();
	}

	private boolean hasClassInstanceCreation(FieldDeclaration o) {
		return hasClassInstanceCreation(o.fragments());
	}

	private boolean hasClassInstanceCreation(VariableDeclarationStatement o) {
		return hasClassInstanceCreation(o.fragments());
	}

	private boolean hasClassInstanceCreation(List<? extends VariableDeclaration> variableDeclarations) {
		if (!variableDeclarations.isEmpty() && hasClassInstanceCreation(variableDeclarations.get(0))) {
			return true;
		} else {
			return false;
		}
	}

	private boolean hasClassInstanceCreation(@Nonnull VariableDeclaration o) {
		if (o.getInitializer() instanceof ClassInstanceCreation) {
			return true;
		} else {
			return false;
		}
	}

	@Nonnull
	private String getClassInstantiates(@Nonnull Type type, List<? extends VariableDeclaration> variableDeclarations) {
		@Nonnull VariableDeclaration variableDeclaration = variableDeclarations.get(0);
		final SimpleName variableName = variableDeclaration.getName();
		List arguments = ((ClassInstanceCreation) variableDeclaration.getInitializer()).arguments();
		if (!arguments.isEmpty()) {
			return getSimpleName(type) + " " + variableName + "(" +
						 arguments.stream().map(Object::toString).collect(Collectors.joining(",")) + ");";
		} else {
			return getSimpleName(type) + " " + variableName + ";";
		}
	}

	@Nonnull
	private String getSimpleName(@Nonnull Type simpleType) {
		return simpleType.resolveBinding().getName();
	}

	private void clearPrivateAndFinal(@Nonnull List<? extends IExtendedModifier> m) {
		for (Iterator<? extends IExtendedModifier> iterator = m.iterator(); iterator.hasNext(); ) {
			IExtendedModifier iExtendedModifier = iterator.next();
			if (iExtendedModifier instanceof Modifier) {
				final Modifier.ModifierKeyword keyword = ((Modifier) iExtendedModifier).getKeyword();
				if (keyword == Modifier.ModifierKeyword.PRIVATE_KEYWORD || keyword == Modifier.ModifierKeyword.FINAL_KEYWORD) {
					iterator.remove();
				}
			}
		}
	}

	/**
	 * s	 * @throws IllegalStateException if it is not Arduino unit
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

	@Nonnull
	private String getLibraryPackage(@Nonnull Type simpleType) {
		String[] p = simpleType.resolveBinding().getPackage().getNameComponents();
		return p[p.length - 1];
	}

}

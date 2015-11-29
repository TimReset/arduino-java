package com.timreset.arduino.parser;

import com.timreset.arduino.BaseArduino;
import org.eclipse.jdt.core.dom.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tim
 * @date 19.11.2015
 */
public class Parser {

	public static void main(String[] args) throws IOException {
		final String inputFileName = args[0];
		final String outputFileName = args[1];
		System.out.println("Input file name: " + inputFileName);
		System.out.println("Output file name: " + outputFileName);
		final String source = transform(Paths.get(inputFileName), Arrays.copyOfRange(args, 2, args.length));
		Path inoFile = Paths.get(outputFileName);
		Files.createDirectories(inoFile.getParent());
		Files.write(inoFile, Collections.singletonList(source), StandardOpenOption.CREATE);
	}

	@Nonnull
	public static String transform(@Nonnull Path sourceFile, String... classpathEntries) throws IOException {
		final String sourceCode = Files.readAllLines(sourceFile).stream().collect(Collectors.joining("\n"));
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(sourceCode.toCharArray());
		//parser.setSource("abc".toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setEnvironment( // apply classpath
						classpathEntries, //
						null, null, true);
		parser.setUnitName(sourceFile.getFileName().toString());
		StringBuilder generatedCode = new StringBuilder();
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		TypeDeclaration arduinoType = getArduinoType(cu);
		for (ASTNode o : (List<? extends ASTNode>) arduinoType.bodyDeclarations()) {
			if (o instanceof MethodDeclaration) {
				MethodDeclaration node = (MethodDeclaration) o;
				String identifier = node.getName().getIdentifier();
				List<? extends IExtendedModifier> m = node.modifiers();
				if ("setup".equals(identifier) || "loop".equals(identifier)) {
					m.clear();
				} else {
					clearPrivateAndFinal(m);
				}

			} else if (o instanceof FieldDeclaration) {
				FieldDeclaration node = (FieldDeclaration) o;
				List<? extends IExtendedModifier> m = node.modifiers();
				clearPrivateAndFinal(m);
			}
			generatedCode.append(o.toString());
			generatedCode.append("\n");
			//			System.out.println(o);
		}
		return generatedCode.toString();
	}

	private static void clearPrivateAndFinal(@Nonnull List<? extends IExtendedModifier> m) {
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

	@Nonnull
	static TypeDeclaration getArduinoType(CompilationUnit cu) {
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

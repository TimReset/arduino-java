package com.timreset.arduino.parser;

import com.timreset.arduino.BaseArduino;
import org.eclipse.jdt.core.dom.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tim
 * @date 19.11.2015
 */
public class Parser {

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
				if ("setup".equals(identifier) || "loop".equals(identifier)) {
					List<? extends IExtendedModifier> m = node.modifiers();
					m.clear();
				}
			}
			generatedCode.append(o.toString());
			generatedCode.append("\n");
			//			System.out.println(o);
		}
		return generatedCode.toString();
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
		List<SimpleType> list = typeDeclaration.superInterfaceTypes();
		SimpleType base = list.get(0);
		if (!BaseArduino.class.getName().equals(base.resolveBinding().getQualifiedName())) {
			throw new IllegalStateException("Should be " + BaseArduino.class.getName());
		}
		//		typeDeclaration.ge
		//		SuperTypeNamesCollector.TypeDeclarationVisitor v = new SuperTypeNamesCollector.TypeDeclarationVisitor(); 
		//		    cu.accept();
		return typeDeclaration;
	}
}

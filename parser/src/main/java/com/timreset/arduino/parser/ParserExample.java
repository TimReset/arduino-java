package com.timreset.arduino.parser;

/**
 * @author Tim
 * @date 16.11.2015
 */

import com.timreset.arduino.BaseArduino;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.internal.core.search.matching.SuperTypeNamesCollector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ParserExample {
	public static void main(String args[]) throws IOException {
		List<String> lines = Files.readAllLines(
						Paths.get("D:\\arduino\\arduino-java\\src\\main\\java\\com\\timreset\\arduino\\SerialExample.java"));
		StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line);
			sb.append("\n");
		}
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		//		parser.setSource(
		//						"public class A { int i = 9;  \n int j; \n ArrayList<Integer> al = new ArrayList<Integer>();j=1000; }".toCharArray());
		parser.setSource(sb.toString().toCharArray());
		//parser.setSource("abc".toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setResolveBindings(true);
		parser.setEnvironment( // apply classpath
						new String[]{"D:\\arduino\\arduino-java\\build\\classes\\main"}, //
						null, null, true);
		parser.setUnitName("SerialExample");
		//ASTNode node = parser.createAST(null);

		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		TypeDeclaration t = getType(cu);
		
/*		cu.accept(new ASTVisitor() {

			@Override
			public boolean visit(MethodDeclaration node) {
				String identifier = node.getName().getIdentifier();
				if ("setup".equals(identifier) || "loop".equals(identifier)) {
					List<? extends IExtendedModifier> m = node.modifiers();
					m.clear();
				}
				//				m.add(node.getAST().newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
				//				System.out.println(node);
				return true;
			}

			@Override
			public boolean visit(TypeDeclaration node) {
				System.out.println(node);
				//				node.accept();
				return true;
				//				return super.visit(node);
			}
		})*/
		;
		System.out.println(cu.toString());
	}

	static TypeDeclaration getType(CompilationUnit cu) {
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
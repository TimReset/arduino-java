package com.timreset.arduino.parser;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.internal.core.dom.NaiveASTFlattener;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.timreset.arduino.parser.Util.*;

/**
 * @author Tim
 * @date 19.12.2015
 */
public class VisitorForC extends NaiveASTFlattener {

	private Set<String> foundedUsedLibs = new HashSet<>();

	@Override
	public boolean visit(FieldDeclaration node) {
		List<? extends IExtendedModifier> m = node.modifiers();
		final String addedString;
		if (hasClassInstanceCreation(node)) {
			Type type = node.getType();
			foundedUsedLibs.add(getLibraryPackage(type));
			addedString = getClassInstantiates(node.getType(), node.fragments());
		} else {
			clearPrivateAndFinal(m);
			addedString = node.toString();
		}
		buffer.append(addedString);
		return false;
	}

	@Override
	public boolean visit(MethodDeclaration node) {
		StringBuilder stringBuilder = new StringBuilder(" ");
		stringBuilder.append(node.getReturnType2()).append(" ").append(node.getName()).append("()");
		buffer.append(stringBuilder);

		if (node.getBody() == null) {
			this.buffer.append(";\n");//$NON-NLS-1$
		} else {
			node.getBody().accept(this);
		}
		return false;
	}

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		final String addedString;

		if (hasClassInstanceCreation(node)) {
			Type type = node.getType();
			foundedUsedLibs.add(getLibraryPackage(type));
			addedString = getClassInstantiates(node.getType(), node.fragments());
		} else {
			clearPrivateAndFinal(node.modifiers());
			addedString = node.toString();
		}
		buffer.append(addedString).append("\n");
		return false;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		if (node.getExpression() != null) {
			node.getExpression().accept(this);
			this.buffer.append(".");//$NON-NLS-1$
		}
		if (node.getAST().apiLevel() >= AST.JLS3) {
			if (!node.typeArguments().isEmpty()) {
				this.buffer.append("<");//$NON-NLS-1$
				for (Iterator it = node.typeArguments().iterator(); it.hasNext(); ) {
					Type t = (Type) it.next();
					t.accept(this);
					if (it.hasNext()) {
						this.buffer.append(",");//$NON-NLS-1$
					}
				}
				this.buffer.append(">");//$NON-NLS-1$
			}
		}
		node.getName().accept(this);
		this.buffer.append("(");//$NON-NLS-1$
		for (Iterator it = node.arguments().iterator(); it.hasNext(); ) {
			Expression e = (Expression) it.next();
			if ((e instanceof SimpleName) && (!e.resolveTypeBinding().isPrimitive())) {
				buffer.append("&");
			}
			e.accept(this);
			if (it.hasNext()) {
				this.buffer.append(",");//$NON-NLS-1$
			}
		}
		this.buffer.append(")");//$NON-NLS-1$
		return false;
	}

	public Set<String> getFoundedUsedLibs() {
		return foundedUsedLibs;
	}
}

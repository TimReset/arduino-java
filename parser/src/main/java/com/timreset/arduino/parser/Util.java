package com.timreset.arduino.parser;

import org.eclipse.jdt.core.dom.*;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tim
 * @date 19.12.2015
 */
public final class Util {
	private Util() {
	}

	@Nonnull
	public static String getClassInstantiates(@Nonnull Type type,
					List<? extends VariableDeclaration> variableDeclarations) {
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

	public static void clearPrivateAndFinal(@Nonnull List<? extends IExtendedModifier> m) {
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
	public static String getLibraryPackage(@Nonnull Type simpleType) {
		String[] p = simpleType.resolveBinding().getPackage().getNameComponents();
		return p[p.length - 1];
	}

	public static boolean hasClassInstanceCreation(FieldDeclaration o) {
		return hasClassInstanceCreation(o.fragments());
	}

	public static boolean hasClassInstanceCreation(VariableDeclarationStatement o) {
		return hasClassInstanceCreation(o.fragments());
	}

	public static boolean hasClassInstanceCreation(List<? extends VariableDeclaration> variableDeclarations) {
		if (!variableDeclarations.isEmpty() && hasClassInstanceCreation(variableDeclarations.get(0))) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean hasClassInstanceCreation(@Nonnull VariableDeclaration o) {
		if (o.getInitializer() instanceof ClassInstanceCreation) {
			return true;
		} else {
			return false;
		}
	}

	@Nonnull
	public static String getSimpleName(@Nonnull Type simpleType) {
		return simpleType.resolveBinding().getName();
	}

}

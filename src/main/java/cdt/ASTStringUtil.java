package cdt;

import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDeclaration;

public class ASTStringUtil {
	public static String getName(ICPPASTUsingDeclaration declaration) {
		return declaration.getName().toString().replace("::", ".");
	}
}

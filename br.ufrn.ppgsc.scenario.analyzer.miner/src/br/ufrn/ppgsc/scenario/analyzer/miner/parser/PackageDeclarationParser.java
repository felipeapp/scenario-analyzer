package br.ufrn.ppgsc.scenario.analyzer.miner.parser;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;

public class PackageDeclarationParser {

	private String package_name;

	public PackageDeclarationParser(String source_code) {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(source_code.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {
			public boolean visit(PackageDeclaration node) {
				if (package_name != null)
					throw new RuntimeException(node.getName() + "\n\tDouble package declaration");

				package_name = node.getName().getFullyQualifiedName();

				return true;
			}
		});
	}

	public String getPackageName() {
		if (package_name != null && !package_name.equals("$"))
			return package_name;
		
		return null;
	}
	
	public String getPackageNameDeclaredText() {
		return package_name;
	}

}

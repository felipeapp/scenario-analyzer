package br.ufrn.ppgsc.scenario.analyzer.miner.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import br.ufrn.ppgsc.scenario.analyzer.miner.model.MethodLimit;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;

public class MethodLimitBuilder {
	
	private List<MethodLimit> methods;
	
	public MethodLimitBuilder(String source) {
		methods = new ArrayList<MethodLimit>();
		
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(source.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		 
		cu.accept(new ASTVisitor() {
 			public boolean visit(MethodDeclaration node) {
 				int startLine = cu.getLineNumber(node.getStartPosition());
 				int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
 				String signature = node.getName().getFullyQualifiedName();
 				
 				methods.add(new MethodLimit(signature, startLine, endLine));
				
				return true;
			}
		});
	}
	
	public Collection<UpdatedMethod> filterChangedMethods(List<UpdatedLine> lines) {
		Map<String, UpdatedMethod> result = new HashMap<String, UpdatedMethod>();
		
		for (UpdatedLine l : lines) {
			for (MethodLimit m : methods) {
				if (l.getLineNumber() >= m.getStartLine() && l.getLineNumber() <= m.getEndLine()) {
					UpdatedMethod mu = result.get(m.getSignature());
					
					if (mu == null) {
						mu = new UpdatedMethod(m);
						result.put(m.getSignature(), mu);
					}
					
					mu.addUpdatedLine(l);
				}
			}
		}
		
		return Collections.unmodifiableCollection(result.values());
	}
	
}

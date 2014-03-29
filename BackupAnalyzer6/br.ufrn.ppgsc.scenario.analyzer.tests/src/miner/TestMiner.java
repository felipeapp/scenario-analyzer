package miner;

import java.util.Collection;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IContentIssue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;
import br.ufrn.ppgsc.scenario.analyzer.miner.svn.RepositoryManager;

public class TestMiner {
	
	public static void main(String[] args) throws Exception {
		
		RepositoryManager repository = new RepositoryManager("http://scenario-analyzer.googlecode.com/svn", "", "");
//		RepositoryManager repository = new RepositoryManager("http://desenvolvimento.info.ufrn.br/projetos", "felipe_app", "s870312n");
//		RepositoryManager repository = new RepositoryManager("http://desenvolvimento.info.ufrn.br/projetos", "user", "password");
		
		Collection<UpdatedMethod> result = repository.getUpdatedMethodsFromRepository(
				"/trunk/br.ufrn.ppgsc.scenario.analyzer.tests/src/tests/Main.java",
				"/home/felipe/Doutorado/workspaces/workspace-analyzer/br.ufrn.ppgsc.scenario.analyzer.tests_oldrevision/src/tests/Main.java",
				"/home/felipe/Doutorado/workspaces/workspace-analyzer/br.ufrn.ppgsc.scenario.analyzer.tests/src/tests/Main.java");
		
//		Collection<UpdatedMethod> result = repository.getUpdatedMethodsFromRepository(
//				"/trunk/Arquitetura/src/br/ufrn/arq/email/Mail.java",
//				"C:/Eclipse/EclipseDoutorado/workspace/Arquitetura_2.5.22/src/br/ufrn/arq/email/Mail.java",
//				"C:/Eclipse/EclipseDoutorado/workspace/Arquitetura_2.6.26/src/br/ufrn/arq/email/Mail.java");
//		
//		Collection<UpdatedMethod> result = repository.getUpdatedMethodsFromRepository(
//				"/trunk/Arquitetura/src/br/ufrn/arq/email/Mail.java", 123141, 136067);
		
		for (UpdatedMethod m : result) {
			System.out.println("******************************************");
			System.out.println("Method: " + m.getMethodLimit().getSignature());

			for (UpdatedLine l : m.getUpdatedLines()) {
				System.out.println("\tAuthor: " + l.getAuthor());
				System.out.println("\tLineNumber: " + l.getLineNumber());
				System.out.println("\tLine: " + l.getLine());
				System.out.println("\tRevision: " + l.getRevision());
				System.out.println("\tDate: " + l.getDate());
				System.out.print("\tTasks: ");
				
				for (IContentIssue issue : l.getIssues())
					System.out.print(issue.getNumber() + " ");
				
				System.out.println("\n\t-------------------------------------");
			}
		}

	}
	
}

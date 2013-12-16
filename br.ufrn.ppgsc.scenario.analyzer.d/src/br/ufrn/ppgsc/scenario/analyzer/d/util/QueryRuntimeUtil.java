package br.ufrn.ppgsc.scenario.analyzer.d.util;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.ppgsc.scenario.analyzer.d.data.RuntimeNode;
import br.ufrn.ppgsc.scenario.analyzer.d.data.RuntimeScenario;

public abstract class QueryRuntimeUtil {

	/*
	 * O método iniciou o lançamento de uma exceção quando ele não terminou de
	 * executar, mas todos os seus filhos executaram com sucesso.
	 */
	public static boolean hasThrownException(RuntimeNode parent) {
		if (parent.getExecutionTime() != -1)
			return false;

		for (RuntimeNode child : parent.getChildren())
			if (child.getExecutionTime() == -1)
				return false;

		return true;
	}

	public static List<RuntimeNode> getFailedNodes(RuntimeScenario scenario) {
		return getFailedNodes(scenario.getRoot());
	}

	public static List<RuntimeNode> getFailedNodes(RuntimeNode root) {
		List<RuntimeNode> result = new ArrayList<RuntimeNode>();

		for (RuntimeNode node : root.getChildren())
			result.addAll(getFailedNodes(node));
		
		if (hasThrownException(root))
			result.add(root);

		return result;
	}

}

package br.ufrn.ppgsc.scenario.analyzer.runtime.util;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.ppgsc.scenario.analyzer.runtime.model.RuntimeNode;
import br.ufrn.ppgsc.scenario.analyzer.runtime.model.RuntimeScenario;

//TODO: Remover, pois atualmente ninguém está usando esta classe
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

	/*
	 * Consulta recursivamente no grafo o nó que levou o cenário a falhar
	 * Existe uma implementação mais rápida em
	 * br.ufrn.ppgsc.scenario.analyzer.miner.db.GenericDBHibernateImpl.getFailedNodes(RuntimeScenario)
	 * que resolve tudo usando apenas SQL
	 */
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

package br.ufrn.ppgsc.scenario.analyzer.miner;

/*
 * Converte as assinaturas do método para caminhos de classe
 * java para que as mesmas possam ser minerados.
 * Deve ser passado um objeto por quem está usando a ferramenta,
 * pois isso é dependente de cada aplicação.
 */
public interface IPathTransformer {

	String[] convert(String method_signature, String repository_prefix, String workcopy_prefix_v1, String workcopy_prefix_v2);

}

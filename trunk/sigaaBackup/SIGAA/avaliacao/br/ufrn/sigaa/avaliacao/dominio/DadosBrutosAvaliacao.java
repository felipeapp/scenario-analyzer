/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/03/2010
 * 
 */
package br.ufrn.sigaa.avaliacao.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe utilizada para armazenar dados brutos de uma Avaliação Institucional.
 * Estes dados brutos são utilizados para geração de relatórios estatísticos
 * pela Comissão de Avaliação Institucional.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class DadosBrutosAvaliacao {
	/** Lista de campos dos dados exportados. */
	private List<String> campos;
	/** Mapa composto de AvaliacaoDocenteTurma / Campo / Valor de dados exportados. */
	private Map<AvaliacaoDocenteTurmaInvalida, Map<String, Object>> dados;
	
	/** Construtor Padrão. */
	public DadosBrutosAvaliacao() {
		campos = new ArrayList<String>();
		dados = new HashMap<AvaliacaoDocenteTurmaInvalida, Map<String,Object>>();
	}
	
	/** Quantidade de linhas de dados exportados. */
	public long size() {
		return dados.size();
	}
	
	/** Retorna o valor de um determinado dado de uma Avaliação Institucional e de um DocenteTurma
	 * @param idAvaliacao
	 * @param idDocenteTurma
	 * @param coluna
	 * @return
	 */
	public Object getDado(AvaliacaoDocenteTurmaInvalida chave, String coluna) {
		if (dados == null)
			return null;
		return dados.get(chave).get(coluna);
	}
	
	/** Seta o valor de um determinado dado de uma Avaliação Institucional e de um DocenteTurma
	 * @param idAvaliacao
	 * @param idDocenteTurma
	 * @param coluna
	 * @param dado
	 */
	public void putDado(AvaliacaoDocenteTurmaInvalida chave, String coluna, Object dado) {
		if (dados == null)
			dados = new HashMap<AvaliacaoDocenteTurmaInvalida, Map<String, Object>>();
		Map<String, Object> dadosAvaliacao = this.dados.get(chave);
		if (dadosAvaliacao == null)
			dadosAvaliacao = new HashMap<String, Object>();
		dadosAvaliacao.put(coluna, dado);
		this.dados.put(chave, dadosAvaliacao);
		if (!this.campos.contains(coluna))
			campos.add(coluna);
	}
	
	/** Retorna uma lista de array de chaves utilizados na tabela de dados exportado. 
	 * @return a posição 0 (zero) do array armazena o idAvaliação; a posição 1 do array armazena o idDocenteTurma
	 */
	public Collection<AvaliacaoDocenteTurmaInvalida> getListaAvaliacaoDocenteTurma() {
		if (dados == null)
			return null;
		return dados.keySet();
	}
	
	/** Adiciona um campo à lista de campos da tabela.
	 * @param campo
	 */
	public void addCampo(String campo) {
		if (campos == null) campos = new ArrayList<String>();
		campos.add(campo);
	}

	/** Retorna a lista de campos da tabela.
	 * @return
	 */
	public List<String> getCampos() {
		return campos;
	}

	/** Retorna uma representação textual dos dados exportando, informando os campos e a quantidade de linhas.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (campos == null) return "";
		StringBuilder str = new StringBuilder();
		for (String campo : campos)
			str.append(campo).append("; ");
		str.append(" registros: ").append(size());
		return str.toString();
	}
}

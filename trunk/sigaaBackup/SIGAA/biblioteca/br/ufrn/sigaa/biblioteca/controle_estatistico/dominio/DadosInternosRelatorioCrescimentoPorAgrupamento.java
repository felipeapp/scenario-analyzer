/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 17/06/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.biblioteca.util.ClassificacoesBibliograficasUtil;

/**
 * <p>Agrupa os dados do relat�rio de crescimento dependendo o agrupamento utilizado(Normalmente Tipo de Material ou Cole��o.) </p>
 * 
 * @author jadson
 *
 */
public class DadosInternosRelatorioCrescimentoPorAgrupamento {
	
	/**
	 * A descria��o do agrupamento utilizando, por exemplo a descria��o da Cole��o
	 */
	private String descricaoAgrupamento;
	
	/**
	 * Cont�m a quantidade de materiais adicionados por classifica�a� dentro deste agrupamento.
	 */
	private List<DadosInternos> dadosInternos = new ArrayList<DadosInternos>();
	
	/**
	 * <p>Organizar melhor os dados do relat�rio, j� que o relat�rio � uma matriz</p>
	 * 
	 * <p>Cont�m a quantidade de materiais por classifica��o.</p>
	 * 
	 * @author jadson
	 *
	 */
	public class DadosInternos implements Comparable<DadosInternos>{
		
		/**
		 * A classifica��o busca (CDU ou Black) por onde � feito o agrupamento
		 */
		private String classificacao;
		
		/**
		 * A quantidade de materais da classifica��o indicada pela vari�val acima. 
		 */
		private long quantidade;
		
		/**
		 * Mostra a quantidade que existia antes do crescimento.
		 */
		private long quantidadeAnterior = 0l;

		
		public DadosInternos(String classificacao) {
			this.classificacao = classificacao;
		}
		
		public DadosInternos(String classificacao, long quantidade) {
			this.classificacao = classificacao;
			this.quantidade = quantidade;
		}


		public String getDescricaoAgrupamento() {
			return descricaoAgrupamento;
		}

		public String getClassificacao() {
			return classificacao;
		}

		public long getQuantidade() {
			return quantidade;
		}
		
		public long getQuantidadeAnterior() {
			return quantidadeAnterior;
		}
		
		/**
		 * M�todo set
		 *
		 * @param quantidade
		 */
		public void setQuantidade(Long quantidade) {
			if(quantidade == null)
				this.quantidade = 0;
			else
			 this.quantidade = quantidade;
		}
		
		/**
		 * M�todo set
		 *
		 * @param quantidade
		 */
		public void setQuantidadeAnterior(Long quantidadeAnterior) {
			if(quantidadeAnterior == null)
				this.quantidadeAnterior = 0;
			else
			 this.quantidadeAnterior = quantidadeAnterior;
		}
		
		/**
		 * Um dado do relat�rio � igual a outro se possuir os mesmos dados pelo qual ele � agrupado da consulta
		 *
		 * @return
		 */
		@Override
		public int hashCode() {
			return HashCodeUtil.hashAll(classificacao );
		}

		/**
		 * Um dado do relat�rio � igual a outro se possuir os mesmos dados pelo qual ele � agrupado da consulta
		 *
		 * @return
		 */
		@Override
		public boolean equals(Object obj) {
			return EqualsUtil.testEquals(this, obj, new String[]{"classificacao"} );
		}
		
		@Override
		public String toString() {
			return "classificacao = "+classificacao + "quantidade = "+quantidade;
		}


		/**
		 * Ver coment�rios da classe pai.<br/>
		 *
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(DadosInternos d) {
			return this.classificacao.compareTo(d.getClassificacao());
		}
		
	}
	
	/**
	 * <p>M�todo que preenche as classe n�o retornas pela consulta com o valor zero</p>
	 * <p>Essse m�todo deve ser chamado sempre ap�s realizar a consulta e antes de mostrar os dados para o usu�rio.
	 * Garante que o relat�rio vai possuir sempre a mesma quantidade colunas, sen�o fica muito dif�cil opera-lo e visualiza-lo.</p>
	 *
	 * @param isBlack
	 * @throws DAOException 
	 */
	public void adicionaClassificacoesSemCrescimento(FiltroClassificacoesRelatoriosBiblioteca classificacao) throws DAOException{
		
		List<String> classesPrincipais = new ArrayList<String>();
		
		if(classificacao == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO1){
			classesPrincipais.addAll(ClassificacoesBibliograficasUtil.getClassesPrincipaisClassificacao1());
			classesPrincipais.add("Sem Classe");
		}
		
		if(classificacao == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO2){
			classesPrincipais.addAll(ClassificacoesBibliograficasUtil.getClassesPrincipaisClassificacao2());
			classesPrincipais.add("Sem Classe");
		}
		
		if(classificacao == FiltroClassificacoesRelatoriosBiblioteca.CLASSIFICACAO3){
			classesPrincipais.addAll(ClassificacoesBibliograficasUtil.getClassesPrincipaisClassificacao3());
			classesPrincipais.add("Sem Classe");
		}
		
		if(classesPrincipais != null) {
			for (String classePrincipal : classesPrincipais) {
				DadosInternos d = new DadosInternos(classePrincipal, 0);
				if(! dadosInternos.contains(d)){
					dadosInternos.add(d);
				}
			}
		}
		
		Collections.sort(dadosInternos);
	}
	
	
	public DadosInternosRelatorioCrescimentoPorAgrupamento(String descricaoAgrupamento) {
		this.descricaoAgrupamento = descricaoAgrupamento;
	}


	/**
	 * Adiciona a quantidade de materiais da classifica��o
	 *
	 * @param classificacao
	 * @param quantidade
	 */
	public void addDadosInternos(String classificacao, long quantidade){
		if(dadosInternos == null){
			dadosInternos = new ArrayList<DadosInternos>();
		}
		
		DadosInternos temp = new DadosInternos(classificacao);
		
		if(dadosInternos.contains(temp)){
			temp = dadosInternos.get(dadosInternos.indexOf(temp));
			temp.setQuantidade(quantidade);
		}else{
			dadosInternos.add( new DadosInternos(classificacao, quantidade));
		}
		
		
	}
	
	
	/**
	 * Retorna  o somat�rio do crescimento por agrupamento.
	 *
	 * @return
	 */
	public long getTotalPorAgrupamento(){
		
		long somatorio = 0l;
		if(dadosInternos != null)
		for (DadosInternos dado : dadosInternos) {
			somatorio += dado.getQuantidade();
			somatorio += dado.getQuantidadeAnterior(); // Caso o usu�rio queira saber a quantidade antes do crescimento
		}
		
		return somatorio;
	}
	
	
	public String getDescricaoAgrupamento() {
		return descricaoAgrupamento;
	}

	public List<DadosInternos> getDadosInternos() {
		return dadosInternos;
	}

	/**
	 * Um dado do relat�rio � igual a outro se possuir os mesmos dados pelo qual ele � agrupado da consulta
	 *
	 * @return
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(descricaoAgrupamento );
	}

	/**
	 * Um dado do relat�rio � igual a outro se possuir os mesmos dados pelo qual ele � agrupado da consulta
	 *
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, new String[]{"descricaoAgrupamento"} );
	}
	
	/**
	 * Imprime a descri��o do agrupamento.<br/>
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buider = new StringBuilder();
		
		buider.append( descricaoAgrupamento + " => " );
		
		for (DadosInternos dados : dadosInternos) {
			buider.append( dados.toString());
		}
		
		return buider.toString();
	}
	
	
	
}

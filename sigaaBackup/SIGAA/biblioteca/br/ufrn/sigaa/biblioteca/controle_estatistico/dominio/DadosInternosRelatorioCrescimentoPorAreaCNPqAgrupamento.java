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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 * <p>Agrupa os dados do relat�rio de crescimento por �rea de CNPq dependendo o agrupamento utilizado
 * (Normalmente Cole��o, Tipo de Material, Situa��o de Material ou Biblioteca.) </p>
 * 
 * @author jadson
 *
 */
public class DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento {

	/**
	 * Todoas as poss�veis classes principais CDU mostradas no relat�rio.  Como esse dado dificilmente deve mudar e � v�lido para os poss�veis contextos do sistema, pode 
	 * ficar em forma de constante.
	 * 
	 * <p>Esse dado � utilizado para deixar sempre o relat�rio com o mesmo n�mero de colunas. Sen�o fica muito confuso de entender </p>
	 */
	//final static String[] AREAS = new String[]{"CA", "CB", "CET", "CH", "CS", "CSA", "CT", "LLA", "MULTI", "Sem �rea"};
	
	/**
	 * A descria��o do agrupamento utilizando, por exemplo a descria��o da Cole��o
	 */
	private String descricaoAgrupamento;
	
	/**
	 * Cont�m a quantidade de materiais adicionados por �rea dentro deste agrupamento.
	 */
	private List<DadosInternos> dadosInternos = new ArrayList<DadosInternos>();
	
	/**
	 * <p>Organizar melhor os dados do relat�rio, j� que o relat�rio � uma matriz</p>
	 * 
	 * <p>Cont�m a quantidade de materiais por �rea.</p>
	 * 
	 * @author jadson
	 *
	 */
	public class DadosInternos implements Comparable<DadosInternos>{
		
		/**
		 * A �rea de busca por onde � feito o agrupamento
		 */
		//private String area;
		
		/**
		 * A descri��o da �rea de busca por onde � feito o agrupamento
		 */
		private String descricaoArea;
		
		/**
		 * A quantidade de materais da �rea indicada pela vari�val acima. 
		 */
		private long quantidade;
		
		/**
		 * Mostra a quantidade que existia antes do crescimento.
		 */
		private long quantidadeAnterior = 0l;

		
		/*public DadosInternos(String area) {
			this.area = area;
		}*/
		
		public DadosInternos(String descricaoArea) {
			//this.area = area;
			this.descricaoArea = descricaoArea;
		}
		
		/*public DadosInternos(String area, long quantidade) {
			this.area = area;
			this.quantidade = quantidade;
		}*/
		
		public DadosInternos(String descricaoArea, long quantidade) {
			//this.area = area;
			this.descricaoArea = descricaoArea;
			this.quantidade = quantidade;
		}


		public String getDescricaoAgrupamento() {
			return descricaoAgrupamento;
		}

//		public String getArea() {
//			return area;
//		}

		public String getDescricaoArea() {
			return descricaoArea;
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
			return HashCodeUtil.hashAll(descricaoArea );
		}

		/**
		 * Um dado do relat�rio � igual a outro se possuir os mesmos dados pelo qual ele � agrupado da consulta
		 *
		 * @return
		 */
		@Override
		public boolean equals(Object obj) {
			return EqualsUtil.testEquals(this, obj, new String[]{"descricaoArea"} );
		}
		
		@Override
		public String toString() {
			return "area = "+descricaoArea + " quantidade = "+quantidade;
		}


		/**
		 * Ver coment�rios da classe pai.<br/>
		 *
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(DadosInternos d) {
			return this.descricaoArea.compareTo(d.getDescricaoArea());
		}
		
	}
	
	/**
	 * <p>M�todo que preenche as classe n�o retornas pela consulta com o valor zero</p>
	 * <p>Essse m�todo deve ser chamado sempre ap�s realizar a consulta e antes de mostrar os dados para o usu�rio.
	 * Garante que o relat�rio vai possuir sempre a mesma quantidade colunas, sen�o fica muito dif�cil opera-lo e visualiza-lo.</p>
	 */
	public void adicionaAreasSemCrescimento(Collection<AreaConhecimentoCnpq> areas){
		
		for (AreaConhecimentoCnpq area : areas) {
			DadosInternos d = new DadosInternos(area.getSigla(), 0);
			if(! dadosInternos.contains(d)){
				dadosInternos.add(d);
			}
		}
		
		Collections.sort(dadosInternos, new Comparator<DadosInternos>() {
			@Override
			public int compare(DadosInternos o1, DadosInternos o2) {
				if (o1.descricaoArea == null) {
					if (o2.descricaoArea == null) {
						return 0;
					} else {
						return -1;
					}
				} else {
					if (o2.descricaoArea == null) {
						return 1;
					} else {
						//int comparacaoDescricao = o1.descricaoArea.compareTo(o2.descricaoArea);
						
						//if (comparacaoDescricao == 0) {
							return o1.descricaoArea.compareTo(o2.descricaoArea);
						//} else {
						//	return comparacaoDescricao;
						//}
					}
				}
			}		
		});
	}
	
	
	public DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento(String descricaoAgrupamento) {
		this.descricaoAgrupamento = descricaoAgrupamento;
	}


	/**
	 * Adiciona a quantidade de materiais da �rea
	 *
	 * @param area
	 * @param quantidade
	 */
	public void addDadosInternos(String descricaoArea, long quantidade){
		if(dadosInternos == null){
			dadosInternos = new ArrayList<DadosInternos>();
		}
		
		DadosInternos temp = new DadosInternos(descricaoArea);
		
		if(dadosInternos.contains(temp)){
			temp = dadosInternos.get(dadosInternos.indexOf(temp));
			temp.setQuantidade(quantidade);
		}else{
			dadosInternos.add( new DadosInternos(descricaoArea, quantidade));
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

/*
 * ObjetoPlanilhaCamposControle.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.StringUtils;

/**
 *
 * 		Objeto que guarda as informação dos campos de controle que já vêm preenchidos 
 * no cadastro de títulos usando uma planilha. Cada objeto desse vai gerar um campo de controle
 * na hora da catalogação.
 *
 *
 *
 * @author jadson
 * @since 11/09/2008
 * @version 1.0 criacao da classe
 *
 */
@Entity
@Table(name = "objeto_planilha_catalogacao_controle", schema = "biblioteca")
public class ObjetoPlanilhaCatalogacaoControle implements Validatable, Comparable<ObjetoPlanilhaCatalogacaoControle>{
	

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.hibernate_sequence") })
	@Column(name = "id_objeto_planilha_catalogacao_controle", nullable = false)
	private int id;


	/** Guarda apenas a tag da etiqueta que o campos de controle possuirá */
	@Column(name = "tag_etiqueta", nullable = false)
	private String tagEtiqueta;


	/** O dado que o campo de controle vai possuir */
	@Column(name = "dado", nullable = false)
	private String dado = "";

	/** Referencia a planilha que ele pertence para torna o relacionamento bidirecional */
	@ManyToOne
	@JoinColumn(name = "id_planilha_catalogacao", referencedColumnName = "id_planilha_catalogacao", nullable=false)
	private PlanilhaCatalogacao planilha;

	/**
	 * Construtor default
	 */
	public ObjetoPlanilhaCatalogacaoControle(){

	}


	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		if(StringUtils.isEmpty(dado)){
			lista.addErro("É preciso informar o conteúdo do campo de controle");
		}

		if(StringUtils.isEmpty(tagEtiqueta)){
			lista.addErro("É preciso informar a etiqueta do campo de controle");
		}

		return lista;
	}

	
	/**
	 * Compara dois objetos de controle da planilha pelo código de barras.
	 *  
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(ObjetoPlanilhaCatalogacaoControle o) {
		if(o == null)
			return -1; // o atual aparece primeiro
		else{
			
			if("LDR".equals(this.tagEtiqueta)) // se for lider vem primeiro
				return -1;
			else
				if("LDR".equals(o.getTagEtiqueta())) // se o outro por lider vem primeiro
					return +1;
			
			return this.tagEtiqueta.compareTo(o.getTagEtiqueta());
		}
	}
	
	
	
	// sets e gets indesejados

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}



	public String getTagEtiqueta() {
		return tagEtiqueta;
	}


	public void setTagEtiqueta(String tagEtiqueta) {
		this.tagEtiqueta = tagEtiqueta;
	}


	public String getDado() {
		return dado;
	}

	/**
	 * 
	 * Formata os dados para mostrar "^"  no lugar dos espaços em branco
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/PlanilhaCatalogacao/form.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String getDadoParaExibicao() {
		String dadosTemp = new String(dado);

		while(dadosTemp.contains(" ")){
			dadosTemp = dadosTemp.substring(0, dadosTemp.indexOf(" "))
			+ "^" 
			+dadosTemp.substring(dadosTemp.indexOf(" ")+1, dadosTemp.length());	
		}
		return dadosTemp;
	}

	
	public void setDado(String dado) {
		this.dado = dado;
	}


	public PlanilhaCatalogacao getPlanilha() {
		return planilha;
	}


	public void setPlanilha(PlanilhaCatalogacao planilha) {
		this.planilha = planilha;
	}


}

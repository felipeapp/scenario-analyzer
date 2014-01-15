/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 08/12/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.vestibular.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Classe de domínio responsável pelo modelo do resultado dos argumentos do candidato no processo seletivo.
 * @author Rafael Gomes
 *
 */
@Entity
@Table(name = "resultado_argumento", schema = "vestibular")
public class ResultadoArgumento implements PersistDB, Validatable{

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_resultado_argumento", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	@Column(name = "descricao_prova")
	private String descricaoProva;
	
	@Column(name = "argumento")
	private double argumento;
	
	@Column(name = "fase")
	private int fase;
	
	@ManyToOne
	@JoinColumn(name = "id_resultado_classificacao_candidato")
	private ResultadoClassificacaoCandidato resultadoClassificacaoCandidato;

	/** Constructor **/
	public ResultadoArgumento() {
		super();
	}

	/** Constructor
	 * @param id
	 * @param descricaoProva
	 * @param argumento
	 * @param fase
	 */
	public ResultadoArgumento(int id, String descricaoProva, double argumento,
			int fase) {
		super();
		this.id = id;
		this.descricaoProva = descricaoProva;
		this.argumento = argumento;
		this.fase = fase;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricaoProva() {
		return descricaoProva;
	}

	public void setDescricaoProva(String descricaoProva) {
		this.descricaoProva = descricaoProva;
	}

	public double getArgumento() {
		return argumento;
	}

	public void setArgumento(double argumento) {
		this.argumento = argumento;
	}

	public int getFase() {
		return fase;
	}

	public void setFase(int fase) {
		this.fase = fase;
	}

	public ResultadoClassificacaoCandidato getResultadoClassificacaoCandidato() {
		return resultadoClassificacaoCandidato;
	}

	public void setResultadoClassificacaoCandidato(
			ResultadoClassificacaoCandidato resultadoClassificacaoCandidato) {
		this.resultadoClassificacaoCandidato = resultadoClassificacaoCandidato;
	}

	@Override
	public ListaMensagens validate() {
		return null;
	}
	
}

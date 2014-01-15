/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 11/05/2011
 *
 */
package br.ufrn.comum.gru.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateMaxLength;
import static br.ufrn.arq.util.ValidatorUtil.validateMinLength;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Os c�digos de recolhimento indicam, dentre outros, par�metros para
 * classifica��o e/ou destina��o dos recursos arrecadados. Assim, quando da
 * cria��o de um c�digo de receita a STN/COFIN poder� indicar, por exemplo, se
 * aquele c�digo se destina a ingressos de receita, estorno de despesa,
 * dep�sitos de diversas origens, indicando ainda a fonte de origem do
 * or�amento, a natureza da receita, etc.
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
@Entity
@Table(name = "codigo_recolhimento", schema="gru")
public class CodigoRecolhimentoGRU implements Validatable {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="gru.gru_sequence") })
	@Column(name = "id_codigo_recolhimento")
	private int id;

	/** C�digo de recolhimento. */
	private String codigo;

	/** Descri��o abreviada do c�digo de recolhimento */
	private String titulo;

	/** Descri��o detalhada do c�digo de recolhimento */
	private String descricao;

	public CodigoRecolhimentoGRU() {
	}
	
	public CodigoRecolhimentoGRU(int id) {
		this();
		setId(id);
	}
	
	public CodigoRecolhimentoGRU(String codigo) {
		this();
		setCodigo(codigo);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public String toString() {
		return codigo;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateMaxLength(codigo, 7, "C�digo", lista);
		validateMinLength(codigo, 7, "C�digo", lista);
		return lista;
	}
	
	/**
	 *Retorna o c�digo do recolhimento junto com a descri��o 
	 * @return
	 */
	public String getDetalhamento(){
		String detalhamento = "";
		if (codigo != null && descricao != null)
			detalhamento = codigo + descricao;
		return detalhamento;
	}
	
	/**
	 * Retorna o c�digo de recolhimento sem o d�gito verificador
	 * @return
	 */
	public int getCodigoSemDigitoVerificador(){
		int codigoSemDigitoVerificador = 0;
		String[] codigoCompleto = codigo.split("-");
		if (codigoCompleto != null && codigoCompleto.length > 0)
			codigoSemDigitoVerificador = Integer.parseInt(codigoCompleto[0]);
		return codigoSemDigitoVerificador;
	}
}

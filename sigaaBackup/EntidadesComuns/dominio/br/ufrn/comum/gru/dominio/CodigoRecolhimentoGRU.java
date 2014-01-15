/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Os códigos de recolhimento indicam, dentre outros, parâmetros para
 * classificação e/ou destinação dos recursos arrecadados. Assim, quando da
 * criação de um código de receita a STN/COFIN poderá indicar, por exemplo, se
 * aquele código se destina a ingressos de receita, estorno de despesa,
 * depósitos de diversas origens, indicando ainda a fonte de origem do
 * orçamento, a natureza da receita, etc.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
@Entity
@Table(name = "codigo_recolhimento", schema="gru")
public class CodigoRecolhimentoGRU implements Validatable {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="gru.gru_sequence") })
	@Column(name = "id_codigo_recolhimento")
	private int id;

	/** Código de recolhimento. */
	private String codigo;

	/** Descrição abreviada do código de recolhimento */
	private String titulo;

	/** Descrição detalhada do código de recolhimento */
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
		validateMaxLength(codigo, 7, "Código", lista);
		validateMinLength(codigo, 7, "Código", lista);
		return lista;
	}
	
	/**
	 *Retorna o código do recolhimento junto com a descrição 
	 * @return
	 */
	public String getDetalhamento(){
		String detalhamento = "";
		if (codigo != null && descricao != null)
			detalhamento = codigo + descricao;
		return detalhamento;
	}
	
	/**
	 * Retorna o código de recolhimento sem o dígito verificador
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

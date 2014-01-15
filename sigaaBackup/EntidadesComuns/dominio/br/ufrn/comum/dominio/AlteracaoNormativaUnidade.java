/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 03/08/2012
 * Autor:     Gleydson Lima 
 */
package br.ufrn.comum.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;

/**
 * Classe que representa uma alteração no nome de uma unidade determinada através de uma instrução normativa.
 * @author itamirfilho
 *
 */
@Entity
@Table(name = "alteracao_normativa_unidade", schema="comum")
public class AlteracaoNormativaUnidade implements PersistDB{

	/**
	 * Identificador.
	 */
	@Id
	@Column(name="id")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="comum.alteracao_normativa_unidade_seq") })
	private int id;
	
	/**
	 * Nova sigla da unidade.
	 */
	@Column(name="nova_sigla")
	private String novaSigla;
	
	/**
	 * Novo nome da unidade.
	 */
	@Column(name="novo_nome")
	private String novoNome;
	
	/**
	 * Novo código da unidade.
	 */
	@Column(name="novo_codigo")
	private Long novoCodigo;

	/**
	 * Nome anterior da unidade.
	 */
	@Column(name="nome_anterior")
	private String nomeAnterior;
	
	/**
	 * Sigla anterior da unidade.
	 */
	@Column(name="sigla_anterior")
	private String siglaAnterior;
	
	/**
	 * Código anterior da unidade.
	 */
	@Column(name="codigo_anterior")
	private Long codigoAnterior;
	
	/**
	 * A unidade que teve seu nome alterado.
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_unidade")
	private Unidade unidade;
	
	/**
	 * Instrução normativa que alterou o nome da unidade.
	 */
	private String documento;
	
	/**
	 * Observações referentes ao processo de alteração da unidade.
	 */
	private String observacoes;

	/**
	 * Data de alteração no nome da unidade.
	 */
	@Column(name="data_alteracao")
	private Date dataAlteracao;
	
	/**
	 * Data do documento. Data início da vigência do novo nome da unidade.
	 */
	@Column(name="data_documento")
	private Date dataDocumento;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_registro_entrada")
	private RegistroEntrada registroEntrada;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNovaSigla() {
		return novaSigla;
	}

	public void setNovaSigla(String novaSigla) {
		this.novaSigla = novaSigla;
	}

	public String getNovoNome() {
		return novoNome;
	}

	public void setNovoNome(String novoNome) {
		this.novoNome = novoNome;
	}

	public String getNomeAnterior() {
		return nomeAnterior;
	}

	public void setNomeAnterior(String nomeAnterior) {
		this.nomeAnterior = nomeAnterior;
	}

	public String getSiglaAnterior() {
		return siglaAnterior;
	}

	public void setSiglaAnterior(String siglaAnterior) {
		this.siglaAnterior = siglaAnterior;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Date getDataDocumento() {
		return dataDocumento;
	}

	public void setDataDocumento(Date dataDocumento) {
		this.dataDocumento = dataDocumento;
	}

	public Long getNovoCodigo() {
		return novoCodigo;
	}

	public void setNovoCodigo(Long novoCodigo) {
		this.novoCodigo = novoCodigo;
	}

	public Long getCodigoAnterior() {
		return codigoAnterior;
	}

	public void setCodigoAnterior(Long codigoAnterior) {
		this.codigoAnterior = codigoAnterior;
	}
	
}

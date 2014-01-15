package br.ufrn.sigaa.projetos.dominio;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.sigaa.ensino.graduacao.dominio.DadosAluno;
import br.ufrn.sigaa.pessoa.dominio.Discente;


/**
 * <p>
 * Representa uma inscrição de um discente para a seleção de uma ação associada
 *  
 * 
 * Em outro momento, os discentes inscritos na seleção serão cadastrados em
 * DiscenteProjeto onde terão um plano de trabalho, dados bancários, etc.
 * </p>
 * 
 * @author geyson
 * 
 **/
@Entity
@Table(name = "inscricao_selecao_projeto", schema = "projetos", uniqueConstraints = {})
public class InscricaoSelecaoProjeto implements PersistDB, Comparable<InscricaoSelecaoProjeto> {
	
	/** Identificador */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_inscricao_selecao_projeto", unique = true, nullable = false)
	private int id;

	/** Discente inscrito */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente", nullable = false)
	private Discente discente;

	/** Projeto na qual ocorreu a inscrição */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_projeto", nullable = false)
	private Projeto projeto;

	/** Registro de entrada do responsável pela inscrição */
	@Column(name = "id_registro_entrada")
	private Integer registroEntrada;

	/** Situação do discente perante a seleção */ 
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao_discente_projeto")
	private TipoSituacaoDiscenteProjeto situacaoDiscenteProjeto;

	/** Discente projeto */
	@ManyToOne
	@JoinColumn(name = "id_discente_projeto")
	private DiscenteProjeto discenteProjeto;
	
	/** Vínculo do discente */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_vinculo_discente")
	private TipoVinculoDiscente tipoVinculo;

	/** Data de cadastro da inscrição */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	@Column(name = "ativo")
	private boolean ativo;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_dados_aluno")
	private DadosAluno dados;

	/**
	 * Justificativa do coordenador da ação de extensão para seleção do discente
	 */
	@Transient
	private String justificativa;
	
	/**
	 * Informa se discente foi selecionado para seleção de uma ação de extensão
	 */
	@Transient
	private boolean selecionado;
	
	/**
	 * Informa se o discente possui algum tipo de prioridade na seleção.
	 */
	@Transient
	private Boolean prioritario;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public Integer getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(Integer registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public TipoSituacaoDiscenteProjeto getSituacaoDiscenteProjeto() {
		return situacaoDiscenteProjeto;
	}

	public void setSituacaoDiscenteProjeto(
			TipoSituacaoDiscenteProjeto situacaoDiscenteProjeto) {
		this.situacaoDiscenteProjeto = situacaoDiscenteProjeto;
	}

	public DiscenteProjeto getDiscenteProjeto() {
		return discenteProjeto;
	}

	public void setDiscenteProjeto(DiscenteProjeto discenteProjeto) {
		this.discenteProjeto = discenteProjeto;
	}

	public TipoVinculoDiscente getTipoVinculo() {
		return tipoVinculo;
	}

	public void setTipoVinculo(TipoVinculoDiscente tipoVinculo) {
		this.tipoVinculo = tipoVinculo;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public DadosAluno getDados() {
		return dados;
	}

	public void setDados(DadosAluno dados) {
		this.dados = dados;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public Boolean getPrioritario() {
		return prioritario;
	}

	public void setPrioritario(Boolean prioritario) {
		this.prioritario = prioritario;
	}
	

	/**
	 * 
	 * @param other
	 * @return
	 */
	public int compareTo(InscricaoSelecaoProjeto other) {
	    	int  result = 0;
		
	    	// Primeiro critério: se é prioritário.
	    	if (other.getPrioritario() != null) {
	    	    result = other.getPrioritario().compareTo(this.getPrioritario());
	    	}
	    	
	    	if (result == 0) {
		    // Segundo critério: Ordem alfabética
		    result = this.getDiscente().getPessoa().compareTo(other.getDiscente().getPessoa());
		}
		return result;
	}

}

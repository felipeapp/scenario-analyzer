/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/02/2008
 *
 */
package br.ufrn.sigaa.extensao.dominio;

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
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.comum.dominio.notificacoes.Notificacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DadosAluno;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;

/*******************************************************************************
 * <p>
 * Representa uma inscri��o de um discente para a sele��o de uma a��o de
 * extens�o. <br/>
 * 
 * Assim, se um discente ativo demonstrar interesse em participar da equipe
 * organizadora da a��o de extens�o ele realiza sua inscri��o nas a��es que tem
 * vagas dispon�veis, atrav�s do portal do discente. <br/>
 * 
 * Em outro momento, os discentes inscritos na sele��o ser�o cadastrados em
 * DiscenteExtens�o onde ter�o um plano de trabalho, dados banc�rios, etc.
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "inscricao_selecao_extensao", schema = "extensao", uniqueConstraints = {})
public class InscricaoSelecaoExtensao implements PersistDB, Comparable<InscricaoSelecaoExtensao> {

	/** Chave prim�ria da inscri��o Sele��o discente */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_inscricao_selecao_extensao", unique = true, nullable = false)
	private int id;

	/** Discente Inscrito */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente", nullable = false)
	private Discente discente;

	/** Atividade na qual ocorreu a inscri��o */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_atividade", nullable = false)
	private AtividadeExtensao atividade;

	/** Registro de entrada do respons�vel pela inscri��o */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** Situa��o do discente perante a sele��o */ 
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao_discente_extensao")
	private TipoSituacaoDiscenteExtensao situacaoDiscenteExtensao;

	/** Discente de extens�o */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente_extensao")
	private DiscenteExtensao discenteExtensao;
	
	/** V�nculo do discente */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_vinculo_discente")
	private TipoVinculoDiscente tipoVinculo;

	/** Data de cadastro da inscri��o */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	/** Serve para indicar se a inscri��o est� ativa */
	@Column(name = "ativo")
	private boolean ativo;
	
	/** Armazena os dados do discente inscrito */
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_dados_aluno")
	private DadosAluno dados;

	/**
	 * Justificativa do coordenador da a��o de extens�o para sele��o do discente
	 */
	@Transient
	private String justificativa;
	
	/**
	 * Informa se discente foi selecionado para sele��o de uma a��o de extens�o
	 */
	@Transient
	private boolean selecionado;
	
	/**
	 * Informa se o discente possui algum tipo de prioridade na sele��o.
	 */
	@Transient
	private Boolean prioritario;
	
	/** Serve para informar se o discente vai ser notificado */
	@Transient
	private Notificacao notificacao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "discente.id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, discente.getId());
	}

	public AtividadeExtensao getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeExtensao atividade) {
		this.atividade = atividade;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * Justificativa do coordenador da a��o de extens�o para sele��o do discente
	 * este campo foi substitu�do por {@link DiscenteExtensao#setJustificativa()}
	 * 
	 * @return
	 */
	public String getJustificativa() {
		return justificativa;
	}
	
	/**
	 * Seta a justificativa para a inscri��o sele��o 
	 * 
	 * @param justificativa
	 */
	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	/**
	 * Representa a situa��o atual do discente no processo seletivo pode ser 1 =
	 * inscrito no processo seletivo ou 2 = n�o selecionado
	 * 
	 * @see constantes em {@link TipoSituacaoDiscenteExtensao}
	 * @return
	 */
	public TipoSituacaoDiscenteExtensao getSituacaoDiscenteExtensao() {
		return situacaoDiscenteExtensao;
	}

	public void setSituacaoDiscenteExtensao(
			TipoSituacaoDiscenteExtensao situacaoDiscenteExtensao) {
		this.situacaoDiscenteExtensao = situacaoDiscenteExtensao;
	}

	/**
	 * verifica se discente foi selecionado para sele��o de uma a��o de extens�o.
	 * @return
	 */
	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public TipoVinculoDiscente getTipoVinculo() {
		return tipoVinculo;
	}

	public void setTipoVinculo(TipoVinculoDiscente tipoVinculo) {
		this.tipoVinculo = tipoVinculo;
	}

	public DiscenteExtensao getDiscenteExtensao() {
		return discenteExtensao;
	}

	public void setDiscenteExtensao(DiscenteExtensao discenteExtensao) {
		this.discenteExtensao = discenteExtensao;
	}

	/**
	 * Informa se o discente possui algum tipo de prioridade na sele��o.
	 * Esta informa��o � gerada a partir da ades�o do discente ao cadastro �nico.
	 * 
	 * @return <code>true</code> se o discente for priorit�rio.
	 */
	public Boolean getPrioritario() {
	    return prioritario;
	}

	public void setPrioritario(Boolean prioritario) {
	    this.prioritario = prioritario;
	}

	public DadosAluno getDados() {
		return dados;
	}

	public void setDados(DadosAluno dados) {
		this.dados = dados;
	}
	
	public Notificacao getNotificacao() {
		return notificacao;
	}

	public void setNotificacao(Notificacao notificacao) {
		this.notificacao = notificacao;
	}

	/**
	 * Serve para verificar se o discente � priorit�rio e ordenar pela ordem alfab�tica.
	 * 
	 * @param other
	 * @return
	 */
	public int compareTo(InscricaoSelecaoExtensao other) {
	    	int  result = 0;
		
	    	// Primeiro crit�rio: se � priorit�rio.
	    	if (other.getPrioritario() != null && this.getPrioritario() != null) {
	    	    result = other.getPrioritario().compareTo(this.getPrioritario());
	    	}
	    	
	    	if (result == 0) {
		    // Segundo crit�rio: Ordem alfab�tica
		    result = this.getDiscente().getPessoa().compareTo(other.getDiscente().getPessoa());
		}
		return result;
	}

}
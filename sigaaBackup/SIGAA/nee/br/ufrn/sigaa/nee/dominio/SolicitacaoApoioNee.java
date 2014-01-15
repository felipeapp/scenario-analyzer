/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 05/10/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.nee.dominio;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

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

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Entidade referente aos Discentes com Necessidades Educacionais Especiais.
 * @author Rafael Gomes
 *
 */

@Entity
@Table(schema = "nee", name = "solicitacao_apoio_nee")
public class SolicitacaoApoioNee implements Validatable{

	
	
	
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="sequence_name", value="nee.nee_seq") })
	@Column(name = "id_solicitacao_apoio_nee", unique = true, nullable = false)
	private int id;

	/** Discente */
    @ManyToOne(fetch = FetchType.EAGER, targetEntity=Discente.class)
    @JoinColumn(name = "id_discente")
    private DiscenteAdapter discente = new Discente();
	
    /** Código automático e seqüencial para cada aluno cadastrado, para controle interno do setor responsável. */ 
    @Column(name = "codigo_cadastro")
    private int codigoCadastro;
    
    /** Justificativa para solicitação de apoio do setor responsável por aluno com Necessidades Educacionais Especiais.*/
    @Column(name = "justificativa_solicitacao")
    private String justificativaSolicitacao;
    
    /** Parecer da comissão de apoio ao estudante com necessidades educacionais especiais, 
     * na qual este parecer será visualizado pelo corpo docente do aluno.*/
    @Column(name = "parecer_comissao")
    private String parecerComissao;
    
    /** Atributo responsável por informar se a solicitação de apoio está ativa ou inativa. */
    @Column(name = "ativo")
    private Boolean ativo;
    
    /** Atributo responsável por informar o status da situação da solicitação de apoio. */
   	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_status_atendimento", unique = false, nullable = false, insertable = true, updatable = true)
    private StatusAtendimento statusAtendimento;
    
    /** Atributo responsável por informar se a solicitação já foi lida pela comissão pertinente. */
    @Column(name = "lida")
    private Boolean lida;
    
    /** Atributo responsável por informar se a solicitação possui parecer favorável ou não 
     * pela comissão de Apoio ao aluno com NEE. */
    @Column(name = "parecer_ativo")
    private Boolean parecerAtivo;
    
	/** Data onde o discente foi cadastrado. */
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	private Date dataCadastro;
	
	/** Grava os dados da inserção do discente no NEE. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada = new RegistroEntrada();
	
	@Transient
	private Collection<TipoNecessidadeSolicitacaoNee> tiposNecessidadeSolicitacaoNee = new HashSet<TipoNecessidadeSolicitacaoNee>(
			0);
	
	/** Constructors **/

	public SolicitacaoApoioNee() {
		super();
	}
	
	/**
	 * @param id
	 */
	public SolicitacaoApoioNee(int id) {
		super();
		this.id = id;
	}

	/**
	 * @param id
	 * @param discente
	 * @param codigoCadastro
	 * @param justificativa
	 * @param dataCadastro
	 * @param registroEntrada
	 */
	public SolicitacaoApoioNee(int id, Discente discente, int codigoCadastro,
			String justificativaSolicitacao, Date dataCadastro,
			RegistroEntrada registroEntrada) {
		super();
		this.id = id;
		this.discente = discente;
		this.codigoCadastro = codigoCadastro;
		this.justificativaSolicitacao = justificativaSolicitacao;
		this.dataCadastro = dataCadastro;
		this.registroEntrada = registroEntrada;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	public int getCodigoCadastro() {
		return codigoCadastro;
	}

	public void setCodigoCadastro(int codigoCadastro) {
		this.codigoCadastro = codigoCadastro;
	}

	public String getJustificativaSolicitacao() {
		return justificativaSolicitacao;
	}

	public void setJustificativaSolicitacao(String justificativaSolicitacao) {
		this.justificativaSolicitacao = justificativaSolicitacao;
	}

	public String getParecerComissao() {
		return parecerComissao;
	}

	public void setParecerComissao(String parecerComissao) {
		this.parecerComissao = parecerComissao;
	}

	public Boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public StatusAtendimento getStatusAtendimento() {
		return statusAtendimento;
	}

	public void setStatusAtendimento(StatusAtendimento statusAtendimento) {
		this.statusAtendimento = statusAtendimento;
	}

	public boolean isLida() {
		return lida;
	}

	public void setLida(boolean lida) {
		this.lida = lida;
	}

	public Boolean getParecerAtivo() {
		return parecerAtivo;
	}

	public void setParecerAtivo(Boolean parecerAtivo) {
		this.parecerAtivo = parecerAtivo;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Collection<TipoNecessidadeSolicitacaoNee> getTiposNecessidadeSolicitacaoNee() {
		return tiposNecessidadeSolicitacaoNee;
	}

	public void setTiposNecessidadeSolicitacaoNee(
			Collection<TipoNecessidadeSolicitacaoNee> tiposNecessidadeSolicitacaoNee) {
		this.tiposNecessidadeSolicitacaoNee = tiposNecessidadeSolicitacaoNee;
	}

//	public String getStatusString() {
//		switch (statusAtendimento.getId()) {
//		case SUBMETIDO:
//			return "SUBMETIDO A CAENE";
//		case EM_ATENDIMENTO:
//			return "EM ATENDIMENTO";
//		case CANCELADO:
//			return "CANCELADO";
//		case CONCLUIDO:
//			return "CONCLUÍDO";
//		default:
//			return "INDEFINIDO";
//		}
//	}
	
//	/** Situações pendentes de parecer sobre necessidades educacionais. */
//	public static Collection<SolicitacaoApoioNee> getSituacoesPendentesParecer() {
//		ArrayList<SolicitacaoApoioNee> situacoes = new ArrayList<SolicitacaoApoioNee>(0);
//		situacoes.add(new SolicitacaoApoioNee(SUBMETIDO));
//		return situacoes;
//	}
//	
//	/** Retornar os status ativos, que estão aptos a receberem parecer técnico. */
//	public static Collection<Integer> getAtivos() {
//		Collection<Integer> status = new ArrayList<Integer>(3);
//		status.add(EM_ATENDIMENTO);
//		status.add(SUBMETIDO);
//		return status;
//	}
	
	@Override
	public ListaMensagens validate() {
		
		return null;
	}
	
	
}

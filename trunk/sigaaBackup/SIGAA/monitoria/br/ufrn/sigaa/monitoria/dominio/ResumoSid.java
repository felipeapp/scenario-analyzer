package br.ufrn.sigaa.monitoria.dominio;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosMonitoria;

/*******************************************************************************
 * <p>
 * Representa um resumo das atividades do projeto para o semin�rio de
 * inicia��o � doc�ncia. Anualmente um resumo das atividades do projeto �
 * submetido a avalia��o da PROGRAD para apresenta��o na semana de ci�ncia e
 * tecnologia atrav�s do semin�rio de inicia��o � doc�ncia (SID).
 * <br/>
 * 
 * Os coordenadores do projeto cadastram os resumos e indicam quais os discentes
 * que participaram de sua elabora��o.
 * <br/>
 * 
 * Este resumo � distribu�do e avaliado por membros da comiss�o cient�fica de monitoria.
 * </p>
 * 
 * 
 * ResumoSid generated by hbm2java
 ******************************************************************************/
@Entity
@Table(name = "resumo_sid", schema = "monitoria")
public class ResumoSid implements Validatable {

	/**Identificador do resumo  das atividades do projeto para o semin�rio de inicia��o � doc�ncia*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_resumo_sid")
	private int id;
	/**Texto resumido contendo todas as atividade desenvolvidas no projeto.*/
	@Column(name = "resumo")
	private String resumo;
	/**Ano em que o resumo foi publicado*/
	@Column(name = "ano_sid")
	private Integer anoSid;
	/**Palavras chaves do resumo*/
	@Column(name = "palavras_chave")
	private String palavrasChave;
	/**data de envio do resumo*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_envio")
	private Date dataEnvio;
	/**projeto de monitotoria*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_projeto_monitoria")
	private ProjetoEnsino projetoEnsino = new ProjetoEnsino();
	/**	status do resumo. valores possiveis: 1-aguardando distribuicao, 2-aguardando avaliacao,
	 *  3-avaliado sem ressalvas, 4-avaliado com ressalvas, 5-cadastro em andamento*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_status")
	private StatusRelatorio status = new StatusRelatorio();
	/**Representa a participa��o de um discente de monitoria em um resumo do SID*/
	@OneToMany(mappedBy = "resumoSid")
	@OrderBy(value = "discenteMonitoria")
	private Set<ParticipacaoSid> participacoesSid = new HashSet<ParticipacaoSid>();
	/** Atributo utilizado para representar as avalia��es do projeto */
	@OneToMany(mappedBy = "resumoSid")
	@OrderBy(value = "avaliador")
	private Set<AvaliacaoMonitoria> avaliacoes = new HashSet<AvaliacaoMonitoria>();
	/** Registro entrada do usu�rio que cadastrou. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")	
	private RegistroEntrada registroEntrada;
	/** Registro entrada do usu�rio que removeu o resumo. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada_exclusao")
	private RegistroEntrada registroEntradaExclusao;
	/**ativo = false quando o resumo for excluido*/
	private boolean ativo;

	// Constructors

	/** default constructor */
	public ResumoSid() {
	}

	/** minimal constructor */
	public ResumoSid(int id) {
		this.id = id;
	}

	// Property accessors
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public String getResumo() {
		return this.resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
	}


	public Integer getAnoSid() {
		return anoSid;
	}

	public void setAnoSid(Integer anoSid) {
		this.anoSid = anoSid;
	}

	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public String getPalavrasChave() {
		return this.palavrasChave;
	}

	public void setPalavrasChave(String palavrasChave) {
		this.palavrasChave = palavrasChave;
	}

	public ProjetoEnsino getProjetoEnsino() {
		return projetoEnsino;
	}

	public void setProjetoEnsino(ProjetoEnsino projetoEnsino) {
		this.projetoEnsino = projetoEnsino;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(resumo, "Resumo", lista);
		ValidatorUtil.validateRequired(palavrasChave, "Palavras-Chave", lista);
		ValidatorUtil.validaInt(anoSid, "Ano do Semin�rio", lista);
		ValidatorUtil.validateRequired(projetoEnsino, "Projeto de Monitoria", lista);
		int totalParticipacoes = 0;
		if ((participacoesSid != null) && (!participacoesSid.isEmpty())) {
			for (ParticipacaoSid ps : participacoesSid) {
				if ((!ps.isParticipou()) && (ps.getJustificativa().trim().equals(""))) {
					lista.addErro("Em caso de n�o participa��o do discente no resumo, deve ser informada uma justificativa.");
					break;
				}
				if ( ps.isParticipou() ) 
					totalParticipacoes++;
			}
		}
		
		int maximoBolsistasSid = ParametroHelper.getInstance().getParametroInt(ParametrosMonitoria.NUMERO_MAXIMO_BOLSISTAS_MONITORIA);
		if ( totalParticipacoes > maximoBolsistasSid ) {
			lista.addErro("S� � permitido cadastrar no m�ximo " + maximoBolsistasSid + " bolsistas por Resumo SID.");
		}
		
		if ((resumo != null) && (!resumo.trim().equals(""))) {
			ValidatorUtil.validateMaxLength(resumo, 1500, "Resumo", lista);
		}
		return lista;
	}

	public StatusRelatorio getStatus() {
		return status;
	}

	public void setStatus(StatusRelatorio status) {
		this.status = status;
	}

	public Set<ParticipacaoSid> getParticipacoesSid() {
		return participacoesSid;
	}

	public void setParticipacoesSid(Set<ParticipacaoSid> participacoesSid) {
		this.participacoesSid = participacoesSid;
	}

	// usu�rio que cadastrou o resumo
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Set<AvaliacaoMonitoria> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(Set<AvaliacaoMonitoria> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	// usu�rio que cadastrou o resumo
	public RegistroEntrada getRegistroEntradaExclusao() {
		return registroEntradaExclusao;
	}

	public void setRegistroEntradaExclusao(
			RegistroEntrada registroEntradaExclusao) {
		this.registroEntradaExclusao = registroEntradaExclusao;
	}

	/**
	 * Monta string como o nome de todos os participantes do resumo SID
	 * 
	 * @return
	 */
	@Transient
	public String getParticipantesCertificado() {

		StringBuffer participantesNomes = new StringBuffer();
		participantesNomes.append("");

		for (ParticipacaoSid p : getParticipacoesSid()) {
			if (p.isApresentou() && p.isParticipou())
				participantesNomes.append(", "
						+ p.getDiscenteMonitoria().getDiscente().getNome());
		}

		if (participantesNomes.toString().trim().equals("")) {
			return null;
		}
		
		String result = participantesNomes.toString().replaceFirst("[, ]", "");
		int pos = result.lastIndexOf(',');

		if (pos > 0) {
			return result.substring(0, pos) + " e" + result.substring(pos + 1);
		} else {
			return result;
		}

	}

	/**
	 * total de participantes do resumo sid. usado no certificado do sid
	 * 
	 * @return total de participantes do resumo sid
	 */
	@Transient
	public int getNumParticipantesCertificado() {
		int qtd = 0;
		for (ParticipacaoSid p : getParticipacoesSid()) {
			if (p.isApresentou() && p.isParticipou()) {
				qtd++;
			}
		}
		return qtd;
	}

	/**
	 * verifica se algu�m do projeto participou e apresentou o resumo
	 * 
	 * @return
	 */
	@Transient
	public boolean isAutorizarEmissaoCertificado() {
		return (getParticipantesCertificado() != null);
	}

	/**
	 * Sequ�ncia utilizada para montagem do nome do 3� Semin�rio de Inicia��o �
	 * Doc�ncia Utilizado no certificado do sid
	 * 
	 * @return
	 */
	@Transient
	public String getOrdinal() {
		return String.valueOf(anoSid - 2003);
	}

	/**
	 * String com todos os docentes ativos do projeto utilizado no certificado
	 * do sid
	 * 
	 * @return String com todos os docentes ativos do projeto
	 */
	@Transient
	public String getEquipeDocentes() {

		StringBuffer equipeDocentes = new StringBuffer();
		equipeDocentes.append("");
		for (EquipeDocente edocente : getProjetoEnsino().getEquipeDocentes()) {
			if ((edocente.getDataSaidaProjeto() == null) && (!edocente.isExcluido()) && (edocente.isAtivo())) {
				// && (!edocente.isCoordenador())) // n�o repete o coordenado na lista de orientadores...
				equipeDocentes.append(", " + edocente.getServidor().getNome());
			}
		}

		if (equipeDocentes.toString().trim().equals("")) {
			return "";
		}

		String result = equipeDocentes.toString().replaceFirst("[, ]", "");
		int pos = result.lastIndexOf(',');

		if (pos > 0) {
			return result.substring(0, pos) + " e" + result.substring(pos + 1);
		} else {
			return result == null ? "" : result;
		}

	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/**
	 * retorna total de membros da comiss�o avaliando a resumos sid (com
	 * avalia��es ativas)
	 * 
	 * @return
	 */
	@Transient
	public int getTotalAvaliadoresResumoSidAtivos() {
		int result = 0;
		for (AvaliacaoMonitoria a : avaliacoes) {
			if ((a.getTipoAvaliacao().getId() == TipoAvaliacaoMonitoria.AVALIACAO_RESUMO_SID)
					&& (a.getStatusAvaliacao().getId() != StatusAvaliacao.AVALIACAO_CANCELADA)) {
				result++;
			}
		}
		return result;
	}
	
	/**
	 * verifica se o resumo ou relatorio possui algum status valido para a exibi��o.
	 *@return  
	 */
	public boolean isPermitidoVisualizarResumoSid() {		
		if(status!= null && status.getId() != StatusRelatorio.REMOVIDO)
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
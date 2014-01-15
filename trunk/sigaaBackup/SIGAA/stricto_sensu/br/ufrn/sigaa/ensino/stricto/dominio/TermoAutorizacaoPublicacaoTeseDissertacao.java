/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 24/05/2010
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.prodocente.atividades.dominio.InstituicaoFomento;

/**
 * Entidade que Representa a Solicitação e Publicação de Tese e Dissertações OnLine no Sistema e 
 * na BDTD (Biblioteca Digital de Teses e Dissertações da UFRN).
 * 
 * @author Arlindo Rodrigues
 *
 */
@Entity
@Table(name="termo_autorizacao_publicacao_tese_dissertacao", schema="stricto_sensu")
public class TermoAutorizacaoPublicacaoTeseDissertacao implements Validatable {
	
	/* Indica que a solicitação está aguardando aprovação */
	public static final int EM_ANALISE = 1;
	/* Indica que a solicitação foi aprovada */
	public static final int APROVADO = 2;
	/* Indica que a solicitação foi negada */
	public static final int REPROVADO = 3;	
	
	/* Descrição dos Status  */
	private static final Map<Integer, String> descricoesStatus;
	static {
		descricoesStatus = new HashMap<Integer, String>();
		descricoesStatus.put(EM_ANALISE, "EM ANÁLISE");
		descricoesStatus.put(APROVADO, "APROVADO");
		descricoesStatus.put(REPROVADO, "REPROVADO");
	}
	
	/**
	 * Chave primária da indicação.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	       	@Column(name = "id_termo_autorizacao_publicacao_tese_dissertacao")			
	private int id;
	
	/** Discente que solicitou a publicação */
	@ManyToOne
	@JoinColumn(name = "id_discente")
	private DiscenteStricto discente;	
	
	/** Banca de defesa associada ao termo. */
	@ManyToOne 
	@JoinColumn(name="id_banca")
	private BancaPos banca;	
	
	/** Status da Solicitação */
	private int status;	
	
	/** Indicação se a publicação é Parcial ou não */
	@Column(name = "parcial")
	private boolean parcial;
	
	/** Indica se o termo está ativo ou não */
	private boolean ativo;
	
	/** Data da Publicação */
	@Column(name = "data_publicacao")
	private Date dataPublicacao;	
	
	/** Instituição de Vínculo Empregatício do Autor da Tese */
	private String afiliacao;
	
	/** CNPJ da Instituição Afiliada */
	@Column(name = "cnpj_afiliacao")
	private Long CNPJAfiliacao;
	
	/** Data da Publicação na BDTD */
	@Column(name = "data_publicacao_bdtd")
	private Date dataPublicacaoBDTD;
	
	/** Caso for do tipo Parcial, deve informar as restrições. */
	@Column(name = "restricoes")
	private String restricoes;
	
	/**Instituição de Fomento */
	@ManyToOne
	@JoinColumn(name = "id_instituicao_fomento")
	private InstituicaoFomento instituicaoFomento;
		
	/** usuário que cadastrou */
	@ManyToOne 
	@JoinColumn(name="id_registro_cadastro")
	@CriadoPor
	private Usuario criadoPor;
	
	/** data de cadastro */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date criadoEm;
	
	/** Registro de Entrada do usuário que modificou */
	@ManyToOne
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Data de alteração. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_alteracao")
	@AtualizadoEm
	private Date dataAlteracao;
	
	/** URL que foi publicada na BDTD */
	@Transient
	private String urlBDTD;	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DiscenteStricto getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteStricto discente) {
		this.discente = discente;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isParcial() {
		return parcial;
	}

	public void setParcial(boolean parcial) {
		this.parcial = parcial;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getDataPublicacao() {
		return dataPublicacao;
	}

	public void setDataPublicacao(Date dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}

	public String getAfiliacao() {
		return afiliacao;
	}

	public void setAfiliacao(String afiliacao) {
		this.afiliacao = afiliacao;
	}

	public Long getCNPJAfiliacao() {
		return CNPJAfiliacao;
	}

	public void setCNPJAfiliacao(Long cNPJAfiliacao) {
		CNPJAfiliacao = cNPJAfiliacao;
	}

	public Date getDataPublicacaoBDTD() {
		return dataPublicacaoBDTD;
	}

	public void setDataPublicacaoBDTD(Date dataPublicacaoBDTD) {
		this.dataPublicacaoBDTD = dataPublicacaoBDTD;
	}

	public String getUrlBDTD() {
		return urlBDTD;
	}

	public void setUrlBDTD(String urlBDTD) {
		this.urlBDTD = urlBDTD;
	}

	public String getRestricoes() {
		return restricoes;
	}

	public void setRestricoes(String restricoes) {
		this.restricoes = restricoes;
	}

	public InstituicaoFomento getInstituicaoFomento() {
		return instituicaoFomento;
	}

	public void setInstituicaoFomento(InstituicaoFomento instituicaoFomento) {
		this.instituicaoFomento = instituicaoFomento;
	}

	public Usuario getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(Usuario criadoPor) {
		this.criadoPor = criadoPor;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}	

	public BancaPos getBanca() {
		return banca;
	}

	public void setBanca(BancaPos banca) {
		this.banca = banca;
	}

	@Transient
	public String getDescricaoStatus() {
		return descricoesStatus.get(status);
	}		
	
	@Transient
	public boolean isAprovado(){
		return status == APROVADO;
	}
	
	@Transient
	public boolean isReprovado(){
		return status == REPROVADO;
	}	

	@Override
	public ListaMensagens validate() {
		return null;
	}
	
	

}

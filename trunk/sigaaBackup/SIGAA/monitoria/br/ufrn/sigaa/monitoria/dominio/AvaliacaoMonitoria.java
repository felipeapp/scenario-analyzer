package br.ufrn.sigaa.monitoria.dominio;

// Generated 09/10/2006 10:44:38 by Hibernate Tools 3.1.0.beta5

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;

/*******************************************************************************
 * <p>
 * Representa uma avaliação feita pelos membro da comissão de monitoria,
 * comissão científica ou prograd.
 * </p>
 * <p>
 * Esta classe é utilizada para avaliar um projeto ou um resumo SID
 * </p>
 * 
 * <p>
 * As Avaliações são geradas quando o projeto é distribuído para um determinado
 * avaliador(membro da comissão)
 * </p>
 * 
 * <p>
 * A prograd pode, eventualmente, avaliar projetos que tiveram avaliações
 * discrepantes entre os membros da comissão. A avaliação da PROGRAD tem um peso
 * maior que a avaliação dos membros da comissão de monitoria.
 * </p>
 * 
 ******************************************************************************/
@Entity
@Table(name = "avaliacao", schema = "monitoria")
public class AvaliacaoMonitoria implements Validatable {

	// Fields
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_avaliacao", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_avaliacao", unique = false, nullable = true, insertable = true, updatable = true)
	private Date dataAvaliacao;

	@Column(name = "parecer", unique = false, nullable = true, insertable = true, updatable = true)
	private String parecer;

	@Column(name = "nota_avaliacao", unique = false, nullable = false, insertable = true, updatable = true, precision = 4)
	private double notaAvaliacao;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_membro_comissao", unique = false, nullable = false, insertable = true, updatable = true)
	private MembroComissao avaliador;

	/** usado na avaliação de projetos */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_projeto_monitoria", unique = false, nullable = false, insertable = true, updatable = true)
	private ProjetoEnsino projetoEnsino = new ProjetoEnsino();

	/** usado na avaliação de projetos */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "avaliacao")
	private List<NotaItemMonitoria> notasItem = new ArrayList<NotaItemMonitoria>();

	/** usado na avaliação de resumos sid */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_resumo_sid")
	private ResumoSid resumoSid = new ResumoSid();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_avaliacao", unique = false, nullable = false, insertable = true, updatable = true)
	private TipoAvaliacaoMonitoria tipoAvaliacao = new TipoAvaliacaoMonitoria();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_status_avaliacao", unique = false, nullable = false, insertable = true, updatable = true)
	private StatusAvaliacao statusAvaliacao = new StatusAvaliacao();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_distribuicao", unique = false, nullable = true, insertable = true, updatable = true)
	private Date dataDistribuicao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_retirada_distribuicao", unique = false, nullable = true, insertable = true, updatable = true)
	private Date dataRetiradaDistribuicao;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada_retirada_distribuicao", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroEntradaRetiradaDistribuicao;

	@Column(name = "avaliacao_prograd")
	private boolean avaliacaoPrograd;
	
	@CampoAtivo
	private boolean ativo;

	// Constructors
	/** default constructor */
	public AvaliacaoMonitoria() {
	}

	/** full constructor */
	public AvaliacaoMonitoria(int idAvaliacao, Date dataAvaliacao,
			String parecer, float notaAvaliacao, MembroComissao avaliador,
			ProjetoEnsino projetoEnsino, List<NotaItemMonitoria> notasItem,
			StatusAvaliacao statusAvaliacao) {
		this.id = idAvaliacao;
		this.dataAvaliacao = dataAvaliacao;
		this.parecer = parecer;
		this.notaAvaliacao = notaAvaliacao;
		this.avaliador = avaliador;
		this.projetoEnsino = projetoEnsino;
		this.notasItem = notasItem;
		this.statusAvaliacao = statusAvaliacao;
	}

	// Property accessors
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDataAvaliacao() {
		return this.dataAvaliacao;
	}

	public void setDataAvaliacao(Date dataAvaliacao) {
		this.dataAvaliacao = dataAvaliacao;
	}

	public String getParecer() {
		return this.parecer;
	}

	public void setParecer(String parecer) {
		this.parecer = parecer;
	}

	public double getNotaAvaliacao() {
		return this.notaAvaliacao;
	}

	public void setNotaAvaliacao(double notaAvaliacao) {
		this.notaAvaliacao = notaAvaliacao;
	}

	public MembroComissao getAvaliador() {
		return this.avaliador;
	}

	public void setAvaliador(MembroComissao avaliador) {
		this.avaliador = avaliador;
	}

	public ProjetoEnsino getProjetoEnsino() {
		return this.projetoEnsino;
	}

	public void setProjetoEnsino(ProjetoEnsino projetoEnsino) {
		this.projetoEnsino = projetoEnsino;
	}

	/**
	 * Utilizado na avaliação da proposta do projeto de ensino
	 */
	public List<NotaItemMonitoria> getNotasItem() {
		return this.notasItem;
	}

	public void setNotasItem(List<NotaItemMonitoria> notasItem) {
		this.notasItem = notasItem;
	}

	public StatusAvaliacao getStatusAvaliacao() {
		return statusAvaliacao;
	}

	public void setStatusAvaliacao(StatusAvaliacao statusAvaliacao) {
		this.statusAvaliacao = statusAvaliacao;
	}

	public TipoAvaliacaoMonitoria getTipoAvaliacao() {
		return tipoAvaliacao;
	}

	public void setTipoAvaliacao(TipoAvaliacaoMonitoria tipoAvaliacao) {
		this.tipoAvaliacao = tipoAvaliacao;
	}

	public boolean addNotaItemMonitoria(NotaItemMonitoria obj) {
		obj.setAvaliacao(this);
		return notasItem.add(obj);
	}

	public boolean removeNotaItemMonitoria(NotaItemMonitoria obj) {
		obj.setAvaliacao(null);
		return notasItem.remove(obj);
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		for (NotaItemMonitoria nota : notasItem) {
			if (nota.getNota() < 0
					|| nota.getNota() > nota.getItemAvaliacao().getNotaMaxima())
				lista
						.addErro("A nota dada ao item "
								+ nota.getItemAvaliacao().getDescricao()
								+ " não pode ser "
								+ "menor que zero nem maior que a nota máxima para esse item.");
		}

		return lista;
	}

	public void calcularMedia() {
		Double mediaFinal = 0.0;
		for (NotaItemMonitoria nota : notasItem) {
			nota.getItemAvaliacao().getGrupo().addNota(nota.getNota());
			mediaFinal += nota.getNota();
		}

		setNotaAvaliacao(mediaFinal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "avaliador",	"projetoEnsino");
	}

	public Date getDataDistribuicao() {
		return dataDistribuicao;
	}

	public void setDataDistribuicao(Date dataDistribuicao) {
		this.dataDistribuicao = dataDistribuicao;
	}

	public Date getDataRetiradaDistribuicao() {
		return dataRetiradaDistribuicao;
	}

	public void setDataRetiradaDistribuicao(Date dataRetiradaDistribuicao) {
		this.dataRetiradaDistribuicao = dataRetiradaDistribuicao;
	}

	public RegistroEntrada getRegistroEntradaRetiradaDistribuicao() {
		return registroEntradaRetiradaDistribuicao;
	}

	public void setRegistroEntradaRetiradaDistribuicao(
			RegistroEntrada registroEntradaRetiradaDistribuicao) {
		this.registroEntradaRetiradaDistribuicao = registroEntradaRetiradaDistribuicao;
	}

	public ResumoSid getResumoSid() {
		return resumoSid;
	}

	public void setResumoSid(ResumoSid resumoSid) {
		this.resumoSid = resumoSid;
	}

	public boolean isAvaliacaoPrograd() {
		return avaliacaoPrograd;
	}

	public void setAvaliacaoPrograd(boolean avaliacaoPrograd) {
		this.avaliacaoPrograd = avaliacaoPrograd;
	}

	/**
	 * Retorna true se a avaliação ainda não foi realizada
	 * 
	 * dataAvaliacao != null && statusAvaliacao != Cancelada
	 * 
	 * @return
	 */
	public boolean isAvaliacaoEmAberto() {
		if (this.statusAvaliacao != null) {
			return ((this.statusAvaliacao.getId() != StatusAvaliacao.AVALIACAO_CANCELADA) && (this.dataAvaliacao == null));
		} else {
			return false;
		}
	}

	public void addNota(double nota) {
		this.notaAvaliacao += nota;
	}
	
	
	public boolean isPermitidoVisualizarAvaliacao() {
		if(dataAvaliacao != null)
			return true;
		return false;
	}

	public boolean isAtivo() {
	    return ativo;
	}

	public void setAtivo(boolean ativo) {
	    this.ativo = ativo;
	}

}

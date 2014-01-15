/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;


/**
 * Armazena as avaliações de projetos de pesquisa
 * 
 * @author Ricardo Wendell
 */
@Entity
@Table(name = "avaliacao_projeto", schema = "pesquisa", uniqueConstraints = {})
public class AvaliacaoProjeto extends AbstractMovimento implements PersistDB{

	/** Constantes */
	/** tipo de distribuição */
	public static final int  TIPO_DISTRIBUICAO_AUTOMATICA 		= 1;
	public static final int  TIPO_DISTRIBUICAO_MANUAL		 	= 2;
	public static final int  CONSULTORIA_ESPECIAL			 	= 3;

	/** situação da avaliação */
	public static final int AGUARDANDO_AVALIACAO				= 1;
	public static final int REALIZADA							= 2;
	public static final int DESISTENTE							= 3; //consultor informou que não pôde realizar avaliação
	public static final int NAO_REALIZADA						= 4; //a avaliação não foi realizada no período indicado e foi finalizada, não podendo portanto ser realizada.

	/** Fields */
	private int id;

	private ProjetoPesquisa projetoPesquisa;

	private Consultor consultor;

	private String observacoes;

	private Date dataDistribuicao;

	private Date dataAvaliacao;

	private String justificativa;

	private int tipoDistribuicao;

	private int situacao;

	private Collection<NotaItem> notasItens = new TreeSet<NotaItem>();

	private double media;

	// Constructors

	/** default constructor */
	public AvaliacaoProjeto() {
	}

	/** minimal constructor */
	public AvaliacaoProjeto(int idAvaliacaoProjeto) {
		this.id = idAvaliacaoProjeto;
	}

	/** full constructor */
	public AvaliacaoProjeto(int idAvaliacaoProjeto,
			ProjetoPesquisa projetoPesquisa, Consultor consultor,
			String observacoes, Date dataSubmissao, Date dataAvaliacao,
			String justificativa, Collection<NotaItem> notaItems) {
		this.id = idAvaliacaoProjeto;
		this.projetoPesquisa = projetoPesquisa;
		this.consultor = consultor;
		this.observacoes = observacoes;
		this.dataDistribuicao = dataSubmissao;
		this.dataAvaliacao = dataAvaliacao;
		this.justificativa = justificativa;
		this.notasItens = notaItems;
	}


	public AvaliacaoProjeto(int idAvaliacaoProjeto,
			int idProjeto, CodigoProjetoPesquisa codigo, String titulo,
			int idArea, String nomeArea, Consultor consultor) {

		this.id = idAvaliacaoProjeto;
		this.projetoPesquisa.setId( idProjeto );
		this.projetoPesquisa.setCodigo(codigo);
		this.projetoPesquisa.setTitulo(titulo);
		this.projetoPesquisa.getAreaConhecimentoCnpq().getGrandeArea().setId(idArea);
		this.projetoPesquisa.getAreaConhecimentoCnpq().getGrandeArea().setNome(nomeArea);
		this.consultor = consultor;
	}


	public AvaliacaoProjeto(int idAvaliacaoProjeto,
			int idProjeto, CodigoProjetoPesquisa codigo, String titulo,
			int idArea, String nomeArea, int idConsultor,
			String nomeConsultor, String emailConsultor) {

		this.projetoPesquisa = new ProjetoPesquisa();
		this.projetoPesquisa.setAreaConhecimentoCnpq( new AreaConhecimentoCnpq() );
		this.projetoPesquisa.getAreaConhecimentoCnpq().setGrandeArea( new AreaConhecimentoCnpq() );
		this.consultor = new Consultor();

		this.id = idAvaliacaoProjeto;
		this.projetoPesquisa.setId( idProjeto );
		this.projetoPesquisa.setCodigo(codigo);
		this.projetoPesquisa.setTitulo(titulo);
		this.projetoPesquisa.getAreaConhecimentoCnpq().getGrandeArea().setId(idArea);
		this.projetoPesquisa.getAreaConhecimentoCnpq().getGrandeArea().setNome(nomeArea);
		this.consultor.setId(idConsultor);
		this.consultor.setNome(nomeConsultor);
		this.consultor.setEmail(emailConsultor);
	}

	@Transient
	public final static Map<Integer, String> getTiposDistribuicao() {
		Map<Integer, String> tipos = new TreeMap<Integer, String>();
		tipos.put(TIPO_DISTRIBUICAO_AUTOMATICA, "DISTRIBUIÇÃO MANUAL");
		tipos.put(TIPO_DISTRIBUICAO_MANUAL, "DISTRIBUIÇÃO AUTOMÁTICA");
		tipos.put(CONSULTORIA_ESPECIAL, "CONSULTORIA ESPECIAL");
		return tipos;
	}

	@Transient
	public final static Map<Integer, String> getTiposSituacao() {
		Map<Integer, String> tipos = new TreeMap<Integer, String>();
		tipos.put(AGUARDANDO_AVALIACAO, "AGUARDANDO AVALIAÇÃO");
		tipos.put(REALIZADA, "AVALIAÇÃO REALIZADA");
		tipos.put(DESISTENTE, "DESISTENTE");
		tipos.put(NAO_REALIZADA, "NÃO REALIZADA");
		return tipos;
	}

	@Transient
	public String getTipoDistribuicaoString() {
		return getTiposDistribuicao().get(this.tipoDistribuicao);
	}

	@Transient
	public String getStatusAvaliacao() {
/*		String status = "INDEFINIDO";
		if (dataDistribuicao != null) {
			status = "PENDENTE";
			if (dataAvaliacao != null) {
				status = "AVALIADO";
				if (justificativa != null) {
					status = "NÃO AVALIADO (JUSTIFICADO)";
				}
			}
		}
		return status;*/
		return getTiposSituacao().get(this.situacao);
	}

	/**
	 * Calcular a media das notas da avaliação
	 *
	 * @return
	 */
	public void calcularMedia() {
		double media = 0;
		if ( notasItens != null && !notasItens.isEmpty() ) {
			int somaPesos = 0;
			for (NotaItem nota : notasItens){
				media += nota.getNota() * nota.getItemAvaliacao().getPeso();
				somaPesos += nota.getItemAvaliacao().getPeso();
			}
			if (somaPesos != 0) {
				media /= somaPesos;
			}
		}
		setMedia(media);
	}

	@Transient
	public boolean isRealizada() {
		return (situacao == REALIZADA);
	}

	@Transient
	public boolean isJustificada() {
		return (situacao == DESISTENTE);
	}

	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_avaliacao_projeto", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idAvaliacaoProjeto) {
		this.id = idAvaliacaoProjeto;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_projeto_pesquisa", unique = false, nullable = true, insertable = true, updatable = true)
	public ProjetoPesquisa getProjetoPesquisa() {
		return this.projetoPesquisa;
	}

	public void setProjetoPesquisa(ProjetoPesquisa projetoPesquisa) {
		this.projetoPesquisa = projetoPesquisa;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_consultor", unique = false, nullable = true, insertable = true, updatable = true)
	public Consultor getConsultor() {
		return this.consultor;
	}

	public void setConsultor(Consultor consultor) {
		this.consultor = consultor;
	}

	@Column(name = "observacoes", unique = false, nullable = true, insertable = true, updatable = true, length = 250)
	public String getObservacoes() {
		return this.observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_distribuicao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataDistribuicao() {
		return this.dataDistribuicao;
	}

	public void setDataDistribuicao(Date dataSubmissao) {
		this.dataDistribuicao = dataSubmissao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_avaliacao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataAvaliacao() {
		return this.dataAvaliacao;
	}

	public void setDataAvaliacao(Date dataAvaliacao) {
		this.dataAvaliacao = dataAvaliacao;
	}

	@Column(name = "justificativa", unique = false, nullable = true, insertable = true, updatable = true, length = 1000)
	public String getJustificativa() {
		return this.justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	@Column(name = "tipo_distribuicao", unique = false, nullable = false, insertable = true, updatable = true)
	public int getTipoDistribuicao() {
		return tipoDistribuicao;
	}

	public void setTipoDistribuicao(int tipoDistribuicao) {
		this.tipoDistribuicao = tipoDistribuicao;
	}

	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "avaliacaoProjeto" )
	public Collection<NotaItem> getNotasItens() {
		return this.notasItens;
	}

	/**
	 * @return the media
	 */
	@Column(name = "media", unique = false, nullable = true, insertable = true, updatable = true)
	public double getMedia() {
		return media;
	}

	/**
	 * @param media the media to set
	 */
	public void setMedia(double media) {
		this.media = media;
	}

	public void setNotasItens(Collection<NotaItem> notaItems) {
		this.notasItens = notaItems;
	}

	public boolean addNotaItem(NotaItem obj) {
		obj.setAvaliacaoProjeto(this);
		return notasItens.add(obj);
	}

	public boolean removeNotaItem(NotaItem obj) {
		obj.setAvaliacaoProjeto(null);
		return notasItens.remove(obj);
	}

	@Column(name = "situacao", nullable = false)
	public int getSituacao() {
		return situacao;
	}

	public void setSituacao(int situacao) {
		this.situacao = situacao;
	}

	@Override
	public boolean equals(Object obj) {
		if (getId() == 0) {
			return false;
		} else {
			return EqualsUtil.testEquals(this, obj, "id");
		}
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/** 
	 * Verifica se há algum plano de trabalho vinculado ao projeto 
	 * que ainda precisa ser avaliado. 
	 */
	@Transient
	public boolean hasPlanoPendenteAvaliacao(){
		for ( PlanoTrabalho plano : projetoPesquisa.getPlanosTrabalho() ) {
			if ( plano.getStatus() == TipoStatusPlanoTrabalho.CADASTRADO  || plano.getStatus() == TipoStatusPlanoTrabalho.CORRIGIDO )
				return true;
		}
		return false;
	}

}

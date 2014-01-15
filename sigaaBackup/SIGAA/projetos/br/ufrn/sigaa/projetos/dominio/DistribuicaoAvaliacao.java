package br.ufrn.sigaa.projetos.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Representa uma distribui��o de projetos para avalia��o com base em um modelo de avalia��o.
 * 
 * @author ilueny santos.
 *
 */
@Entity
@Table(name = "distribuicao_avaliacao", schema = "projetos")
public class DistribuicaoAvaliacao implements PersistDB {

    /** Indica que a distribui��o de projetos para os avaliadores foi realizada manualmente, ou seja, por um gestor. */
    public static final Character MANUAL = 'M';
    /** Indica que a distribui��o foi realizada automaticamente pelo sistema, se a interfer�ncia do gestor. */
    public static final Character AUTOMATICA = 'A';
    
    /** Identificador �nico do objeto.*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_distribuicao_avaliacao")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Informa se a distribui��o foi feita manualmente (M) ou automaticamente (A). */
	@Column(name = "metodo_distribuicao")
	private Character metodo = 'M';

	/** Tipo de avaliador para o qual o projeto pode ser distribu�do. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_avaliador")
	private TipoAvaliador tipoAvaliador = new TipoAvaliador();

	/** Mensagem utilizada para notifica��o dos avaliadores que recebem projetos para avaliar. */
	@Column(name = "msg_notificacao_avaliadores")
	private String msgNotificacaoAvaliadores;
	
	/** Registro de entrada do dia em que a distribui��o foi criada. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** Indica o modelo de avalia��o utilizado nesta distribui��o. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_modelo_avaliacao")
	private ModeloAvaliacao modeloAvaliacao = new ModeloAvaliacao();
	
	/** Informa se todas as avalia��es desta distribui��o foram consolidadas. */
	@Column(name = "avaliacao_consolidada")
	private boolean avaliacaoConsolidada;
	
	/** Indica se o objeto em quest�o est� ativo no sistema. */
	@CampoAtivo
	private boolean ativo;
	
	/** Atributo transiente que armazena o total de avalia��es distribu�das para essa distribui��o. */
	@Transient
	private int totalAvaliacoesDistribuidas;
	
	/** Atributo transiente que armazena o total de avalia��es realizadas para essa distribui��o. */
	@Transient
	private int totalAvaliacoesRealizadas;
	
	public DistribuicaoAvaliacao() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isAtivo() {
	    return ativo;
	}

	public void setAtivo(boolean ativo) {
	    this.ativo = ativo;
	}

	public RegistroEntrada getRegistroEntrada() {
	    return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
	    this.registroEntrada = registroEntrada;
	}

	public ModeloAvaliacao getModeloAvaliacao() {
	    return modeloAvaliacao;
	}

	public void setModeloAvaliacao(ModeloAvaliacao modeloAvaliacao) {
	    this.modeloAvaliacao = modeloAvaliacao;
	}

	/**
	 * M�todo de distribui��o da avalia��o: 
	 * M = Manual, A = Autom�tica 
	 * @return
	 */
	public Character getMetodo() {
	    return metodo;
	}

	public void setMetodo(Character metodo) {
	    this.metodo = metodo;
	}
	
	public String getMsgNotificacaoAvaliadores() {
	    return msgNotificacaoAvaliadores;
	}

	public void setMsgNotificacaoAvaliadores(String msgNotificacaoAvaliadores) {
	    this.msgNotificacaoAvaliadores = msgNotificacaoAvaliadores;
	}

	public TipoAvaliador getTipoAvaliador() {
		return tipoAvaliador;
	}

	public void setTipoAvaliador(TipoAvaliador tipoAvaliador) {
		this.tipoAvaliador = tipoAvaliador;
	}

	@Override
	public boolean equals(Object obj) {
	    return EqualsUtil.testEquals(this, obj, "id", "modeloAvaliacao.id");
	}

	@Override
	public int hashCode() {
	    return HashCodeUtil.hashAll(id, modeloAvaliacao.getId());
	}
	
	/**
	 * Informa se a distribui��o foi realizada para membros do comit� interno.
	 * 
	 * @return
	 */
	public boolean isDistribuicaoComiteInterno() {
		return this.tipoAvaliador.getId() == TipoAvaliador.COMITE_INTEGRADO_ENSINO_PESQUISA_EXTENSAO; 
	}
	
	/**
	 * Informa se � uma distribui��o de projeto.
	 * 
	 * @return
	 */
	public boolean isDistribuicaoProjeto() {
		return this.modeloAvaliacao.isModeloAvaliacaoProjeto(); 
	}

	/**
	 * Informa se � uma distribui��o de relat�rio.
	 * 
	 * @return
	 */
	public boolean isDistribuicaoRelatorio() {
		return this.modeloAvaliacao.isModeloAvaliacaoRelatorio(); 
	}

	public boolean isAvaliacaoConsolidada() {
		return avaliacaoConsolidada;
	}

	public void setAvaliacaoConsolidada(boolean avaliacaoConsolidada) {
		this.avaliacaoConsolidada = avaliacaoConsolidada;
	}

	/**
	 * Informa se � uma distribui��o autom�tica
	 * @return
	 */
	public boolean isAutomatica(){
		return metodo.equals(AUTOMATICA);
	}
	
	/**
	 * Informa se � uma distribui��o manual
	 * @return
	 */
	public boolean isManual(){
		return metodo.equals(MANUAL);
	}

	public int getTotalAvaliacoesDistribuidas() {
		return totalAvaliacoesDistribuidas;
	}

	public void setTotalAvaliacoesDistribuidas(int totalAvaliacoesDistribuidas) {
		this.totalAvaliacoesDistribuidas = totalAvaliacoesDistribuidas;
	}

	public int getTotalAvaliacoesRealizadas() {
		return totalAvaliacoesRealizadas;
	}

	public void setTotalAvaliacoesRealizadas(int totalAvaliacoesRealizadas) {
		this.totalAvaliacoesRealizadas = totalAvaliacoesRealizadas;
	}
}

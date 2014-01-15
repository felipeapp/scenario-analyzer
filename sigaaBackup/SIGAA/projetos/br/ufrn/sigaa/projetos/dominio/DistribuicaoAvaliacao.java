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
 * Representa uma distribuição de projetos para avaliação com base em um modelo de avaliação.
 * 
 * @author ilueny santos.
 *
 */
@Entity
@Table(name = "distribuicao_avaliacao", schema = "projetos")
public class DistribuicaoAvaliacao implements PersistDB {

    /** Indica que a distribuição de projetos para os avaliadores foi realizada manualmente, ou seja, por um gestor. */
    public static final Character MANUAL = 'M';
    /** Indica que a distribuição foi realizada automaticamente pelo sistema, se a interferência do gestor. */
    public static final Character AUTOMATICA = 'A';
    
    /** Identificador único do objeto.*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_distribuicao_avaliacao")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Informa se a distribuição foi feita manualmente (M) ou automaticamente (A). */
	@Column(name = "metodo_distribuicao")
	private Character metodo = 'M';

	/** Tipo de avaliador para o qual o projeto pode ser distribuído. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_avaliador")
	private TipoAvaliador tipoAvaliador = new TipoAvaliador();

	/** Mensagem utilizada para notificação dos avaliadores que recebem projetos para avaliar. */
	@Column(name = "msg_notificacao_avaliadores")
	private String msgNotificacaoAvaliadores;
	
	/** Registro de entrada do dia em que a distribuição foi criada. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** Indica o modelo de avaliação utilizado nesta distribuição. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_modelo_avaliacao")
	private ModeloAvaliacao modeloAvaliacao = new ModeloAvaliacao();
	
	/** Informa se todas as avaliações desta distribuição foram consolidadas. */
	@Column(name = "avaliacao_consolidada")
	private boolean avaliacaoConsolidada;
	
	/** Indica se o objeto em questão está ativo no sistema. */
	@CampoAtivo
	private boolean ativo;
	
	/** Atributo transiente que armazena o total de avaliações distribuídas para essa distribuição. */
	@Transient
	private int totalAvaliacoesDistribuidas;
	
	/** Atributo transiente que armazena o total de avaliações realizadas para essa distribuição. */
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
	 * Método de distribuição da avaliação: 
	 * M = Manual, A = Automática 
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
	 * Informa se a distribuição foi realizada para membros do comitê interno.
	 * 
	 * @return
	 */
	public boolean isDistribuicaoComiteInterno() {
		return this.tipoAvaliador.getId() == TipoAvaliador.COMITE_INTEGRADO_ENSINO_PESQUISA_EXTENSAO; 
	}
	
	/**
	 * Informa se é uma distribuição de projeto.
	 * 
	 * @return
	 */
	public boolean isDistribuicaoProjeto() {
		return this.modeloAvaliacao.isModeloAvaliacaoProjeto(); 
	}

	/**
	 * Informa se é uma distribuição de relatório.
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
	 * Informa se é uma distribuição automática
	 * @return
	 */
	public boolean isAutomatica(){
		return metodo.equals(AUTOMATICA);
	}
	
	/**
	 * Informa se é uma distribuição manual
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

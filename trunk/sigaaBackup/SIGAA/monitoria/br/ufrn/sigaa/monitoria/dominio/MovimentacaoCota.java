/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/06/2007
 *
 */
package br.ufrn.sigaa.monitoria.dominio;

import java.util.Date;

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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;

/*******************************************************************************
 * <p>
 * Entidade que representa a movimentação de cotas entre projetos de monitoria.
 * Só quem pode realizar movimentação de cota é a PROGRAD. O total de bolsas de
 * todos os projetos de um mesmo edital não pode ultrapassar a quantidade
 * definida no edital
 * </p>
 * 
 * @author Victor Hugo
 * @author Ilueny Santos
 * 
 * 
 ******************************************************************************/

@Entity
@Table(schema = "monitoria", name = "movimentacao_cota")
public class MovimentacaoCota implements Validatable {

   	/** Informa o tipo de operação da movimentação realizada */
   	public static final char TIPO_MOVIMENTACAO_INCLUIR = 'I';
   	/** Informa o tipo de operação da movimentação realizada */
   	public static final char TIPO_MOVIMENTACAO_REMOVER = 'R';

   	/** Identificador único para objetos desta classe. */
   	@Id
   	@GeneratedValue(generator="seqGenerator")
   	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
   		parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
   		@Column(name = "id_movimentacao_cota")
	private int id;

	/**
	 * projeto da cota movimentada
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_projeto_monitoria")
	private ProjetoEnsino projetoEnsino;

	/**
	 * Ação de extensão da cota movimentada.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_atividade")
	private AtividadeExtensao acaoExtensao;

	
	/**
	 * quantidade de cotas de bolsistas movimentadas
	 */
	@Column(name = "qtd_cotas")
	private Integer qtdCotas = 0;

	/**
	 * data da movimentação
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data")
	private Date data;

	/**
	 * motivo da movimentação da bolsa
	 */
	@Column(name = "observacao", length=255)	
	private String observacao;

	/**
	 * usuário que fez a movimentação das cotas
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	private RegistroEntrada registroEntrada;
	
	/** determina o tipo de movimentação realizada pelo usuário. R = Remoção, I = Inclusão.*/
	@Column(name = "tipo_movimentacao")
	private char tipoMovimentacao = 'I';
	
	/**
	 * Tipo de cota movimentada (Tipo de vínculo).
	 * @see Tipo
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_cota")
	private TipoVinculoDiscente tipoCota = new TipoVinculoDiscente();

	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public ProjetoEnsino getProjetoEnsino() {
	    return projetoEnsino;
	}

	public void setProjetoEnsino(ProjetoEnsino projetoEnsino) {
	    this.projetoEnsino = projetoEnsino;
	}

	public AtividadeExtensao getAcaoExtensao() {
	    return acaoExtensao;
	}

	public void setAcaoExtensao(AtividadeExtensao acaoExtensao) {
	    this.acaoExtensao = acaoExtensao;
	}

	public char getTipoMovimentacao() {
	    return tipoMovimentacao;
	}

	public void setTipoMovimentacao(char tipoMovimentacao) {
	    this.tipoMovimentacao = tipoMovimentacao;
	}
	
	/** Informa se é uma movimentação de inclusão de bolsas. */
	public boolean isIncluirBolsa() {
	    return this.tipoMovimentacao == TIPO_MOVIMENTACAO_INCLUIR && isCotaRemunerada();
	    		
	}
	
	/** Informa se é uma movimentação de remoção de bolsas. */
	public boolean isRemoverBolsa() {
	    return this.tipoMovimentacao == TIPO_MOVIMENTACAO_REMOVER && isCotaRemunerada();
	}

	/** Informa se é uma movimentação de inclusão de cota não remunerada. */
	public boolean isIncluirCotaNaoRemunerada() {
	    return this.tipoMovimentacao == TIPO_MOVIMENTACAO_INCLUIR && isCotaNaoRemunerada(); 
	}
	
	/** Informa se é uma movimentação de remoção de cota não remunerada. */
	public boolean isRemoverCotaNaoRemunerada() {
	    return this.tipoMovimentacao == TIPO_MOVIMENTACAO_REMOVER && isCotaNaoRemunerada();
	}
	
	/** Informa se a cota que está sendo movimentada é uma cota remunerada (bolsa)*/
	public boolean isCotaRemunerada() {
		return (this.tipoCota.getId() == TipoVinculoDiscente.EXTENSAO_BOLSISTA_INTERNO
    			|| this.tipoCota.getId() == TipoVinculoDiscente.MONITORIA_BOLSISTA);
	}

	/** Informa se a cota que está sendo movimentada é uma cota NÃO remunerada (voluntário)*/
	public boolean isCotaNaoRemunerada() {
		return (this.tipoCota.getId() == TipoVinculoDiscente.EXTENSAO_VOLUNTARIO
				|| this.tipoCota.getId() == TipoVinculoDiscente.MONITORIA_VOLUNTARIO);
	}
	
	public ListaMensagens validate() {
	    ListaMensagens lista = new ListaMensagens();
	    	ValidatorUtil.validateRequired(observacao, "Observações", lista);
	    	ValidatorUtil.validateMaxLength(observacao, 255, "Observações", lista);
	    	ValidatorUtil.validateRequired(qtdCotas, "Nº de Cotas", lista);
	    	ValidatorUtil.validateMinValue(qtdCotas, 1, "Nº de Cotas", lista);
	    	ValidatorUtil.validateRequired(tipoMovimentacao, "Tipo de movimentação", lista);
	    	ValidatorUtil.validateRequired(tipoCota, "Tipo de Cota", lista);
	    	if(getProjetoEnsino() != null){
		    	if (!getProjetoEnsino().isMonitoriaEmExecucao()) {
		    		lista.addErro("Somente Projetos em execução podem ter cotas movimentadas.");
		    	}
	    	}
	    return lista;
	}

	public Integer getQtdCotas() {
		return qtdCotas;
	}

	public void setQtdCotas(Integer qtdCotas) {
		this.qtdCotas = qtdCotas;
	}

	public TipoVinculoDiscente getTipoCota() {
		return tipoCota;
	}

	public void setTipoCota(TipoVinculoDiscente tipoCota) {
		this.tipoCota = tipoCota;
	}
	
	
}

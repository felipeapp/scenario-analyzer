package br.ufrn.sigaa.extensao.dominio;

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

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Classe responsável por definir regras específicas para cada tipo de 
 * Ação de extensão no mesmo edital.
 * 
 * 
 * @author ilueny santos.
 *
 */
@Entity
@Table(schema = "extensao", name = "edital_extensao_regra")
public class EditalExtensaoRegra implements Validatable {


    /** Atributo utilizado para representar o ID do Edital de Extensão Regra. */
    @Id
    @GeneratedValue(generator="seqGenerator")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
    		parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
    @Column(name = "id_edital_extensao_regra", nullable = false)
    private int id;

    /** Atributo utilizado para representar o Edital de Extensão. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_edital_extensao")
    private EditalExtensao editalExtensao = new EditalExtensao();

    /** Atributo utilizado para representar o tipo de atividade a qual a regra se aplica. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_atividade_extensao")
    private TipoAtividadeExtensao tipoAtividadeExtensao = new TipoAtividadeExtensao();

    /** Atributo utilizado para representar se o Edital Extensão Regra está ou não ativo. */
    @CampoAtivo
    private boolean ativo = true;

    /** Atributo utilizado para representar a data de cadastro do Edital Extensão Regra. */
    @CriadoEm
    @Column(name="data_cadastro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;

    /** Valor limite do financiamento solicitado para o tipo de ação de extensao desta regra. */ 
    @Column(name = "orcamento_maximo")
    private double orcamentoMaximo = 0.0;

    /** Define um período mínimo (em meses) de realização de um determinado tipo de ação. Ex.: todos os projetos devem ser realizados, no mínimo, em 3 meses. */
    @Column(name = "periodo_minimo_execucao")
    private Integer periodoMinimoExecucao = 0;
    
    /** Usuário que cadastrou o projeto */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_entrada")
    @CriadoPor
    private RegistroEntrada registroEntrada;

    /** Informa o total de coordenações ativas que o docente pode ter para este tipo de ação de extensão no mesmo edital. Ex.: Um docente pode coordenar ao mesmo tempo até 2 projetos de extensão.*/
    @Column(name="max_coordenacoes_ativas", nullable = false)
    private Integer maxCoordenacoesAtivas = 0;

    /** 
     * Informa a quantidade mínima de ações vinculadas para que uma ação deste tipo seja realizada. 
     * Utilizado nos casos de regras definidas para Programas de Extensão, onde é necessário determinar uma quantidade mínima de ações que o tornem viável.
     * Ex: Os Programas deste edital devem ter no mínimo 5 projetos vinculados a ele.
     */
    @Column(name="min_acoes_vinculadas", nullable = false)
    private Integer minAcoesVinculadas = 0;

    
    
    @Override
    public int getId() {
	return id;
    }

    /**
     * Método utilizado para setar o ID do Edital de Extensão Regra
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Não é chamdo por JSP(s)</li>
     * </ul>
     */
    @Override
    public void setId(int id) {
    }

    /**
     * Método utilizado para realizar validações no Edital de Extensão Regra
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Não é chamdo por JSP(s)</li>
     * </ul>
     */
    @Override
    public ListaMensagens validate() {
    	ListaMensagens lista = new ListaMensagens();
    	ValidatorUtil.validaDouble(orcamentoMaximo, "Orçamento Máximo", lista);
    	ValidatorUtil.validateRequired(periodoMinimoExecucao, "Período Mínimo de Realização da Ação", lista);
    	ValidatorUtil.validateRequired(maxCoordenacoesAtivas, "Máximo de Coordenações Ativas por Docente neste Tipo de Ação", lista);
    	ValidatorUtil.validateRequired(tipoAtividadeExtensao, "Tipo de Ação", lista);
    	
    	if (ValidatorUtil.isNotEmpty(tipoAtividadeExtensao) && tipoAtividadeExtensao.isPrograma()) {
    		ValidatorUtil.validateRequired(minAcoesVinculadas, "Quantidade mínima de ações vinculadas ao Programa", lista);
    	}
    	return lista;
    }

    public EditalExtensao getEditalExtensao() {
	return editalExtensao;
    }

    public void setEditalExtensao(EditalExtensao editalExtensao) {
	this.editalExtensao = editalExtensao;
    }

    public TipoAtividadeExtensao getTipoAtividadeExtensao() {
	return tipoAtividadeExtensao;
    }

    public void setTipoAtividadeExtensao(TipoAtividadeExtensao tipoAtividadeExtensao) {
	this.tipoAtividadeExtensao = tipoAtividadeExtensao;
    }

    public boolean isAtivo() {
	return ativo;
    }

    public void setAtivo(boolean ativo) {
	this.ativo = ativo;
    }

    public Date getDataCadastro() {
	return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
	this.dataCadastro = dataCadastro;
    }

    /**
     * Método utilizado para retornar o hashCode
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Não é chamdo por JSP(s)</li>
     * </ul>
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (ativo ? 1231 : 1237);
	result = prime * result
	+ ((editalExtensao == null) ? 0 : editalExtensao.hashCode());
	result = prime * result + id;
	result = prime
	* result
	+ ((tipoAtividadeExtensao == null) ? 0 : tipoAtividadeExtensao
		.hashCode());
	return result;
    }

    /**
     * Método utilizado para verificar se o objeto passado por parâmetro é igual ao Edital Extensão Regra atual.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Não é chamdo por JSP(s)</li>
     * </ul>
     * 
     * @param obj
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	EditalExtensaoRegra other = (EditalExtensaoRegra) obj;
	if (id != 0 && other.id != 0 && id != other.id)
	    return false;

	if (ativo != other.ativo)
	    return false;
	if (editalExtensao == null) {
	    if (other.editalExtensao != null)
		return false;
	} else if (!editalExtensao.equals(other.editalExtensao))
	    return false;
	if (tipoAtividadeExtensao == null) {
	    if (other.tipoAtividadeExtensao != null)
		return false;
	} else if (!tipoAtividadeExtensao.equals(other.tipoAtividadeExtensao))
	    return false;
	
	return true;
    }

    public double getOrcamentoMaximo() {
        return orcamentoMaximo;
    }

    public void setOrcamentoMaximo(double orcamentoMaximo) {
        this.orcamentoMaximo = orcamentoMaximo;
    }

    public Integer getPeriodoMinimoExecucao() {
        return periodoMinimoExecucao;
    }

    public void setPeriodoMinimoExecucao(Integer periodoMinimoExecucao) {
        this.periodoMinimoExecucao = periodoMinimoExecucao;
    }

    public RegistroEntrada getRegistroEntrada() {
        return registroEntrada;
    }

    public void setRegistroEntrada(RegistroEntrada registroEntrada) {
        this.registroEntrada = registroEntrada;
    }

    /**
     * Informa o máximo de coordenações ativas permitidas por docente.
     * 
     * @return
     */
    public Integer getMaxCoordenacoesAtivas() {
        return maxCoordenacoesAtivas;
    }

    public void setMaxCoordenacoesAtivas(Integer maxCoordenacoesAtivas) {
        this.maxCoordenacoesAtivas = maxCoordenacoesAtivas;
    }

	public Integer getMinAcoesVinculadas() {
		return minAcoesVinculadas;
	}

	public void setMinAcoesVinculadas(Integer minAcoesVinculadas) {
		this.minAcoesVinculadas = minAcoesVinculadas;
	}

}

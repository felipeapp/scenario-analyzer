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
 * Classe respons�vel por definir regras espec�ficas para cada tipo de 
 * A��o de extens�o no mesmo edital.
 * 
 * 
 * @author ilueny santos.
 *
 */
@Entity
@Table(schema = "extensao", name = "edital_extensao_regra")
public class EditalExtensaoRegra implements Validatable {


    /** Atributo utilizado para representar o ID do Edital de Extens�o Regra. */
    @Id
    @GeneratedValue(generator="seqGenerator")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
    		parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
    @Column(name = "id_edital_extensao_regra", nullable = false)
    private int id;

    /** Atributo utilizado para representar o Edital de Extens�o. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_edital_extensao")
    private EditalExtensao editalExtensao = new EditalExtensao();

    /** Atributo utilizado para representar o tipo de atividade a qual a regra se aplica. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_atividade_extensao")
    private TipoAtividadeExtensao tipoAtividadeExtensao = new TipoAtividadeExtensao();

    /** Atributo utilizado para representar se o Edital Extens�o Regra est� ou n�o ativo. */
    @CampoAtivo
    private boolean ativo = true;

    /** Atributo utilizado para representar a data de cadastro do Edital Extens�o Regra. */
    @CriadoEm
    @Column(name="data_cadastro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCadastro;

    /** Valor limite do financiamento solicitado para o tipo de a��o de extensao desta regra. */ 
    @Column(name = "orcamento_maximo")
    private double orcamentoMaximo = 0.0;

    /** Define um per�odo m�nimo (em meses) de realiza��o de um determinado tipo de a��o. Ex.: todos os projetos devem ser realizados, no m�nimo, em 3 meses. */
    @Column(name = "periodo_minimo_execucao")
    private Integer periodoMinimoExecucao = 0;
    
    /** Usu�rio que cadastrou o projeto */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_entrada")
    @CriadoPor
    private RegistroEntrada registroEntrada;

    /** Informa o total de coordena��es ativas que o docente pode ter para este tipo de a��o de extens�o no mesmo edital. Ex.: Um docente pode coordenar ao mesmo tempo at� 2 projetos de extens�o.*/
    @Column(name="max_coordenacoes_ativas", nullable = false)
    private Integer maxCoordenacoesAtivas = 0;

    /** 
     * Informa a quantidade m�nima de a��es vinculadas para que uma a��o deste tipo seja realizada. 
     * Utilizado nos casos de regras definidas para Programas de Extens�o, onde � necess�rio determinar uma quantidade m�nima de a��es que o tornem vi�vel.
     * Ex: Os Programas deste edital devem ter no m�nimo 5 projetos vinculados a ele.
     */
    @Column(name="min_acoes_vinculadas", nullable = false)
    private Integer minAcoesVinculadas = 0;

    
    
    @Override
    public int getId() {
	return id;
    }

    /**
     * M�todo utilizado para setar o ID do Edital de Extens�o Regra
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>N�o � chamdo por JSP(s)</li>
     * </ul>
     */
    @Override
    public void setId(int id) {
    }

    /**
     * M�todo utilizado para realizar valida��es no Edital de Extens�o Regra
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>N�o � chamdo por JSP(s)</li>
     * </ul>
     */
    @Override
    public ListaMensagens validate() {
    	ListaMensagens lista = new ListaMensagens();
    	ValidatorUtil.validaDouble(orcamentoMaximo, "Or�amento M�ximo", lista);
    	ValidatorUtil.validateRequired(periodoMinimoExecucao, "Per�odo M�nimo de Realiza��o da A��o", lista);
    	ValidatorUtil.validateRequired(maxCoordenacoesAtivas, "M�ximo de Coordena��es Ativas por Docente neste Tipo de A��o", lista);
    	ValidatorUtil.validateRequired(tipoAtividadeExtensao, "Tipo de A��o", lista);
    	
    	if (ValidatorUtil.isNotEmpty(tipoAtividadeExtensao) && tipoAtividadeExtensao.isPrograma()) {
    		ValidatorUtil.validateRequired(minAcoesVinculadas, "Quantidade m�nima de a��es vinculadas ao Programa", lista);
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
     * M�todo utilizado para retornar o hashCode
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>N�o � chamdo por JSP(s)</li>
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
     * M�todo utilizado para verificar se o objeto passado por par�metro � igual ao Edital Extens�o Regra atual.
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>N�o � chamdo por JSP(s)</li>
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
     * Informa o m�ximo de coordena��es ativas permitidas por docente.
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

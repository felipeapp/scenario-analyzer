/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/11/2007
 *
 */
package br.ufrn.sigaa.projetos.dominio;

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
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;

/*******************************************************************************
 * Classe que representa a autorização dada pelo chefe de departamento que
 * participa da ação através de seus docentes/técnicos administrativos.
 * <br>
 * Exemplo: Quando um docente faz parte da equipe de uma ação, o chefe do
 * departamento deste docente terá que autorizar sua participação na ação.
 * Somente após essa autorização é que a proposta será encaminhada para análise
 * pelos membros do comitê responsável por aprovar ou reprovar o projeto.
 * <br>
 * As autorizações também são obrigatórias nos casos dos relatórios gerados
 * pelos coordenadores da ação periodicamente. Somente após a aprovação do
 * relatório pelo chefe do departamento é que o relatório e encaminhado para
 * análise da pró-reitoria que deverá concluir a execução da ação.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "autorizacao_departamento", schema = "projetos")
public class AutorizacaoDepartamento implements Validatable, Cloneable {

    // Fields
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
    @Column(name = "id_autorizacao_departamento")
    private int id;

    /** Representa o tipo de autorização dada aos projetos de monitoria pelos chefes de departamentos envolvidos no projeto. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_autorizacao")
    private TipoAutorizacaoDepartamento tipoAutorizacao = new TipoAutorizacaoDepartamento();

    /** Atividade associada a esta autorização */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_atividade")
    private AtividadeExtensao atividade;

    /** Projeto associado a esta autorização */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_projeto", nullable = false)
    private Projeto projeto;
    
    /** Unidade/Departamento que deverá analisar a solicitação. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_unidade", nullable = false)
    private Unidade unidade;

    /** Representa a autorização ou a não autorização do departamento para o projeto. */
    @Column(name = "autorizado")
    private Boolean autorizado;

    /** Data da autorização do departamento envolvido no projeto. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_autorizacao")
    private Date dataAutorizacao;

    /** Data da reunião dos chefes de unidade associados aos servidores da ufrn ligados ao projeto. */  
    @Temporal(TemporalType.DATE)
    @Column(name = "data_reuniao")
    private Date dataReuniao;

    /** Registro de entrada do responsável pela autorização. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_entrada")
    private RegistroEntrada registroEntrada;

    /** Registro de entrada do responsável pela devolução. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_entrada_devolucao")
    private RegistroEntrada registroEntradaDevolucao;

    /** Registro de entrada do responsável pelo cadastro da autorização. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_entrada_cadastro")
    @CriadoPor
    private RegistroEntrada registroEntradaCadastro;

    /** Data do cadastro da autorização. */ 
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_cadastro")
    @CriadoEm
    private Date dataCadastro;

    /** Indica se a autorização está ativa. Utilizado para exclusão lógica.*/
    @Column(name="ativo")
    @CampoAtivo
    private boolean ativo;

    // Constructors
    /** 
     * default constructor. 
     */
    public AutorizacaoDepartamento() {
    }

    // Property accessors

    /**
     * id da autorização.
     */
    public int getId() {
	return this.id;
    }

    public void setId(int id) {
	this.id = id;
    }

    /**
     * Retorna a unidade responsável pela autorização.
     * 
     * @return Unidade
     */
    public Unidade getUnidade() {
	return this.unidade;
    }

    public void setUnidade(Unidade unidade) {
	this.unidade = unidade;
    }

    /**
     * Retorna a ação de extensão que deve ser autorizada.
     * 
     * @return Ação de extensão.
     */
    public AtividadeExtensao getAtividade() {
	return this.atividade;
    }

    public void setAtividade(AtividadeExtensao atividade) {
	this.atividade = atividade;
    }

    /**
     * Retorna o tipo de autorização.
     * 
     * @return Retorna o {@link TipoAutorizacaoDepartamento} tipo de autorização realizado.
     */
    public TipoAutorizacaoDepartamento getTipoAutorizacao() {
	return this.tipoAutorizacao;
    }

    public void setTipoAutorizacao(TipoAutorizacaoDepartamento tipoAutorizacao) {
	this.tipoAutorizacao = tipoAutorizacao;
    }

    /**
     * Registro de entrada do usuário que autorizou o projeto.
     * 
     * @return Registro de entrada
     */
    public RegistroEntrada getRegistroEntrada() {
	return this.registroEntrada;
    }

    public void setRegistroEntrada(RegistroEntrada registroEntrada) {
	this.registroEntrada = registroEntrada;
    }

    /**
     * Informa a data da autorização/desautorização dada pelo chefe do
     * departamento.
     * 
     * @return Data da autorização
     */
    public Date getDataAutorizacao() {
	return dataAutorizacao;
    }

    public void setDataAutorizacao(Date dataAutorizacao) {
	this.dataAutorizacao = dataAutorizacao;
    }

    /**
     * Data da reunião realizada onde o projeto foi aprovado.
     * 
     * @return Data da reunião.
     */
    public Date getDataReuniao() {
	return dataReuniao;
    }

    public void setDataReuniao(Date dataReuniao) {
	this.dataReuniao = dataReuniao;
    }

    /**
     * Informa se o projeto foi autorizado.
     *  
     * @return <code>true</code> se o projeto foi autorizado. 
     */
    public Boolean getAutorizado() {
	return autorizado;
    }

    public void setAutorizado(Boolean autorizado) {
	this.autorizado = autorizado;
    }

    /**
     * Realiza validações iniciais na autorização.
     * Validações diferentes de ad referendum exigem que seja informada a data da reunião.
     * 
     * @return {@link ListaMensagens} com as mensagens de validação  
     */
    public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();
	
		if (getAutorizado()) {
		    ValidatorUtil.validateRequired(tipoAutorizacao,"Tipo de Autorização", lista);
		    if ((tipoAutorizacao != null) && (tipoAutorizacao.getId() != TipoAutorizacaoDepartamento.AD_REFERENDUM)) {
		    	if(ValidatorUtil.isEmpty(dataReuniao)){
		    		ValidatorUtil.validateRequired(dataReuniao, "Data Reunião", lista);
		    	}else{
		    		ValidatorUtil.validaDataAnteriorIgual(dataReuniao, new Date(), "Data Reunião", lista);
		    		if(dataReuniao.getTime() < CalendarUtils.descartarHoras(projeto.getDataCadastro()).getTime()){
		    			lista.addErro("Data Reunião: esta data deve ser maior ou igual a "+ Formatador.getInstance().formatarData(projeto.getDataCadastro()));
		    		}
		    	}
		    	
		    }else{
		    	dataReuniao = null;
		    }
		} else {
		    if (tipoAutorizacao != null && tipoAutorizacao.getId() != 0) {
			lista.addErro("Atividade Não será autorizada! Tipo de Autorizacao Não deve ser informado");
		    }
		    if (dataReuniao != null) {
			lista.addErro("Atividade Não será autorizada! Data da Reunião Não deve ser informada");
		    }
		}
	
		return lista;
    }

    /**
     * Informa se a autorização está ativa.
     * 
     * @return <code>true</code> se ativa.
     */
    public boolean isAtivo() {
	return ativo;
    }

    public void setAtivo(boolean ativo) {
	this.ativo = ativo;
    }

    /**
     * Informa o identificador do registro de entrada do usuário da PROEX que
     * inativou a autorização devolvendo da proposta para o coordenador pra
     * reedição.
     * 
     * @return Registro de entrada
     */
    public RegistroEntrada getRegistroEntradaDevolucao() {
	return registroEntradaDevolucao;
    }

    public void setRegistroEntradaDevolucao(RegistroEntrada registroEntradaDevolucao) {
	this.registroEntradaDevolucao = registroEntradaDevolucao;
    }

    /**
     * Data do cadastro da autorização.
     * 
     * @return Data do cadastro.
     */
    public Date getDataCadastro() {
	return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
	this.dataCadastro = dataCadastro;
    }

    /**
     * Registro de entrada do cadastro da autorização. O cadastro de autorizações pode ser realizado 
     * automaticamente na submissão da proposta ou pela pró-reitoria de extensão.
     * 
     * @return Registro de entrada do cadastro da autorização.
     */
    public RegistroEntrada getRegistroEntradaCadastro() {
	return registroEntradaCadastro;
    }

    public void setRegistroEntradaCadastro(RegistroEntrada registroEntradaCadastro) {
	this.registroEntradaCadastro = registroEntradaCadastro;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

    /** Informa se o departamento autorizou a proposta. */
    public boolean isDepartamentoAutorizou() {
    	return dataAutorizacao != null && autorizado;
    }

}

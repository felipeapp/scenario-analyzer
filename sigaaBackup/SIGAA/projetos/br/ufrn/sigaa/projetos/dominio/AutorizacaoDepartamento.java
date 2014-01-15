/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Classe que representa a autoriza��o dada pelo chefe de departamento que
 * participa da a��o atrav�s de seus docentes/t�cnicos administrativos.
 * <br>
 * Exemplo: Quando um docente faz parte da equipe de uma a��o, o chefe do
 * departamento deste docente ter� que autorizar sua participa��o na a��o.
 * Somente ap�s essa autoriza��o � que a proposta ser� encaminhada para an�lise
 * pelos membros do comit� respons�vel por aprovar ou reprovar o projeto.
 * <br>
 * As autoriza��es tamb�m s�o obrigat�rias nos casos dos relat�rios gerados
 * pelos coordenadores da a��o periodicamente. Somente ap�s a aprova��o do
 * relat�rio pelo chefe do departamento � que o relat�rio e encaminhado para
 * an�lise da pr�-reitoria que dever� concluir a execu��o da a��o.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "autorizacao_departamento", schema = "projetos")
public class AutorizacaoDepartamento implements Validatable, Cloneable {

    // Fields
	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
    @Column(name = "id_autorizacao_departamento")
    private int id;

    /** Representa o tipo de autoriza��o dada aos projetos de monitoria pelos chefes de departamentos envolvidos no projeto. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_autorizacao")
    private TipoAutorizacaoDepartamento tipoAutorizacao = new TipoAutorizacaoDepartamento();

    /** Atividade associada a esta autoriza��o */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_atividade")
    private AtividadeExtensao atividade;

    /** Projeto associado a esta autoriza��o */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_projeto", nullable = false)
    private Projeto projeto;
    
    /** Unidade/Departamento que dever� analisar a solicita��o. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_unidade", nullable = false)
    private Unidade unidade;

    /** Representa a autoriza��o ou a n�o autoriza��o do departamento para o projeto. */
    @Column(name = "autorizado")
    private Boolean autorizado;

    /** Data da autoriza��o do departamento envolvido no projeto. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_autorizacao")
    private Date dataAutorizacao;

    /** Data da reuni�o dos chefes de unidade associados aos servidores da ufrn ligados ao projeto. */  
    @Temporal(TemporalType.DATE)
    @Column(name = "data_reuniao")
    private Date dataReuniao;

    /** Registro de entrada do respons�vel pela autoriza��o. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_entrada")
    private RegistroEntrada registroEntrada;

    /** Registro de entrada do respons�vel pela devolu��o. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_entrada_devolucao")
    private RegistroEntrada registroEntradaDevolucao;

    /** Registro de entrada do respons�vel pelo cadastro da autoriza��o. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_entrada_cadastro")
    @CriadoPor
    private RegistroEntrada registroEntradaCadastro;

    /** Data do cadastro da autoriza��o. */ 
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_cadastro")
    @CriadoEm
    private Date dataCadastro;

    /** Indica se a autoriza��o est� ativa. Utilizado para exclus�o l�gica.*/
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
     * id da autoriza��o.
     */
    public int getId() {
	return this.id;
    }

    public void setId(int id) {
	this.id = id;
    }

    /**
     * Retorna a unidade respons�vel pela autoriza��o.
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
     * Retorna a a��o de extens�o que deve ser autorizada.
     * 
     * @return A��o de extens�o.
     */
    public AtividadeExtensao getAtividade() {
	return this.atividade;
    }

    public void setAtividade(AtividadeExtensao atividade) {
	this.atividade = atividade;
    }

    /**
     * Retorna o tipo de autoriza��o.
     * 
     * @return Retorna o {@link TipoAutorizacaoDepartamento} tipo de autoriza��o realizado.
     */
    public TipoAutorizacaoDepartamento getTipoAutorizacao() {
	return this.tipoAutorizacao;
    }

    public void setTipoAutorizacao(TipoAutorizacaoDepartamento tipoAutorizacao) {
	this.tipoAutorizacao = tipoAutorizacao;
    }

    /**
     * Registro de entrada do usu�rio que autorizou o projeto.
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
     * Informa a data da autoriza��o/desautoriza��o dada pelo chefe do
     * departamento.
     * 
     * @return Data da autoriza��o
     */
    public Date getDataAutorizacao() {
	return dataAutorizacao;
    }

    public void setDataAutorizacao(Date dataAutorizacao) {
	this.dataAutorizacao = dataAutorizacao;
    }

    /**
     * Data da reuni�o realizada onde o projeto foi aprovado.
     * 
     * @return Data da reuni�o.
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
     * Realiza valida��es iniciais na autoriza��o.
     * Valida��es diferentes de ad referendum exigem que seja informada a data da reuni�o.
     * 
     * @return {@link ListaMensagens} com as mensagens de valida��o  
     */
    public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();
	
		if (getAutorizado()) {
		    ValidatorUtil.validateRequired(tipoAutorizacao,"Tipo de Autoriza��o", lista);
		    if ((tipoAutorizacao != null) && (tipoAutorizacao.getId() != TipoAutorizacaoDepartamento.AD_REFERENDUM)) {
		    	if(ValidatorUtil.isEmpty(dataReuniao)){
		    		ValidatorUtil.validateRequired(dataReuniao, "Data Reuni�o", lista);
		    	}else{
		    		ValidatorUtil.validaDataAnteriorIgual(dataReuniao, new Date(), "Data Reuni�o", lista);
		    		if(dataReuniao.getTime() < CalendarUtils.descartarHoras(projeto.getDataCadastro()).getTime()){
		    			lista.addErro("Data Reuni�o: esta data deve ser maior ou igual a "+ Formatador.getInstance().formatarData(projeto.getDataCadastro()));
		    		}
		    	}
		    	
		    }else{
		    	dataReuniao = null;
		    }
		} else {
		    if (tipoAutorizacao != null && tipoAutorizacao.getId() != 0) {
			lista.addErro("Atividade N�o ser� autorizada! Tipo de Autorizacao N�o deve ser informado");
		    }
		    if (dataReuniao != null) {
			lista.addErro("Atividade N�o ser� autorizada! Data da Reuni�o N�o deve ser informada");
		    }
		}
	
		return lista;
    }

    /**
     * Informa se a autoriza��o est� ativa.
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
     * Informa o identificador do registro de entrada do usu�rio da PROEX que
     * inativou a autoriza��o devolvendo da proposta para o coordenador pra
     * reedi��o.
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
     * Data do cadastro da autoriza��o.
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
     * Registro de entrada do cadastro da autoriza��o. O cadastro de autoriza��es pode ser realizado 
     * automaticamente na submiss�o da proposta ou pela pr�-reitoria de extens�o.
     * 
     * @return Registro de entrada do cadastro da autoriza��o.
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

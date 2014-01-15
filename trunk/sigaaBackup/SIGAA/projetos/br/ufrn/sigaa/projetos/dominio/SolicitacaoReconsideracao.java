/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/04/2008
 *
 */
package br.ufrn.sigaa.projetos.dominio;

// Generated 24/04/2008 10:44:38

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
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;

/*******************************************************************************
 * <p>
 * Classe de dom�nio atrav�s da qual o coordenador da a��o de extens�o pode
 * pedir uma segunda chance na avalia��o da a��o que foi, inicialmente,
 * reprovada pelo comite de extens�o.
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(name = "solicitacao_reconsideracao", schema = "projetos")
public class SolicitacaoReconsideracao implements Validatable {

 
    // Fields
    @Id
    @GeneratedValue(generator="seqGenerator")
    @Column(name="id_solicitacao_reconsideracao")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	    parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
    private int id;

    /** A��o referente a solicita��o */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_atividade")
    private AtividadeExtensao atividade = new AtividadeExtensao();

    /** id do projeto que solicitou a reconsidera��o. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_projeto")
    private Projeto projeto = new Projeto();

    /** Projeto de monitoria referente a solicita��o */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_projeto_monitoria")
    private ProjetoEnsino projetoMonitoria = new ProjetoEnsino();
    
    /** Informa o tipo de solicita��o de reconsidera��o. Ex. 1 - Solicita��o de a��es de extens�o, 2 - Solicita��o de a��es acad�micas. */
    @ManyToOne
    @JoinColumn(name="id_tipo_solicitacao_reconsideracao")
    private TipoSolicitacaoReconsideracao tipo;
    
    /** Registro de entrada do solicitante da reconsidera��o, geralmente o coordenador do projeto */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_entrada_solicitacao")
    private RegistroEntrada registroEntradaSolicitacao;
    
    /** Data da solicita��o */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_solicitacao")
    private Date dataSolicitacao;

    /** Justificativa dada pelo coordenador para reconsidera��o */
    @Column(name = "justificativa")
    private String justificativa;

    /** Registro de entrada do parecer da PROEx */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_entrada_parecer")
    private RegistroEntrada registroEntradaParecer;

    /** Data do parecer */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_parecer")
    private Date dataParecer;

    /** Texto com o parecer do analisador da solicita��o membro da proex */
    @Column(name = "parecer")
    private String parecer;

    /**  Solicita��o aprovada pela PROEx */
    @Column(name = "aprovado")
    private boolean aprovado;

    /** Informa se a solicita��o est� ativa. */
    @Column(name = "ativo")
    private boolean ativo = true;

    
    /** default constructor */
    public SolicitacaoReconsideracao() {
    }

    public int getId() {
	return this.id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public AtividadeExtensao getAtividade() {
	return atividade;
    }

    public void setAtividade(AtividadeExtensao atividade) {
	this.atividade = atividade;
    }

    public String getJustificativa() {
	return justificativa;
    }

    public void setJustificativa(String justificativa) {
	this.justificativa = justificativa;
    }

    public String getParecer() {
	return parecer;
    }

    public void setParecer(String parecer) {
	this.parecer = parecer;
    }

    public boolean isAprovado() {
	return aprovado;
    }

    public void setAprovado(boolean aprovado) {
	this.aprovado = aprovado;
    }

    public ListaMensagens validate() {
	ListaMensagens lista = new ListaMensagens();
	ValidatorUtil.validateRequired(justificativa, "Justificatva", lista);
	return lista;
    }

    public RegistroEntrada getRegistroEntradaSolicitacao() {
	return registroEntradaSolicitacao;
    }

    public void setRegistroEntradaSolicitacao(
	    RegistroEntrada registroEntradaSolicitacao) {
	this.registroEntradaSolicitacao = registroEntradaSolicitacao;
    }

    public Date getDataSolicitacao() {
	return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
	this.dataSolicitacao = dataSolicitacao;
    }

    public RegistroEntrada getRegistroEntradaParecer() {
	return registroEntradaParecer;
    }

    public void setRegistroEntradaParecer(RegistroEntrada registroEntradaParecer) {
	this.registroEntradaParecer = registroEntradaParecer;
    }

    public Date getDataParecer() {
	return dataParecer;
    }

    public void setDataParecer(Date dataParecer) {
	this.dataParecer = dataParecer;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

    public TipoSolicitacaoReconsideracao getTipo() {
        return tipo;
    }

    public void setTipo(TipoSolicitacaoReconsideracao tipo) {
        this.tipo = tipo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

	public ProjetoEnsino getProjetoMonitoria() {
		return projetoMonitoria;
	}

	public void setProjetoMonitoria(ProjetoEnsino projetoMonitoria) {
		this.projetoMonitoria = projetoMonitoria;
	}

}

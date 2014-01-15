/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Classe de domínio através da qual o coordenador da ação de extensão pode
 * pedir uma segunda chance na avaliação da ação que foi, inicialmente,
 * reprovada pelo comite de extensão.
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

    /** Ação referente a solicitação */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_atividade")
    private AtividadeExtensao atividade = new AtividadeExtensao();

    /** id do projeto que solicitou a reconsideração. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_projeto")
    private Projeto projeto = new Projeto();

    /** Projeto de monitoria referente a solicitação */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_projeto_monitoria")
    private ProjetoEnsino projetoMonitoria = new ProjetoEnsino();
    
    /** Informa o tipo de solicitação de reconsideração. Ex. 1 - Solicitação de ações de extensão, 2 - Solicitação de ações acadêmicas. */
    @ManyToOne
    @JoinColumn(name="id_tipo_solicitacao_reconsideracao")
    private TipoSolicitacaoReconsideracao tipo;
    
    /** Registro de entrada do solicitante da reconsideração, geralmente o coordenador do projeto */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registro_entrada_solicitacao")
    private RegistroEntrada registroEntradaSolicitacao;
    
    /** Data da solicitação */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_solicitacao")
    private Date dataSolicitacao;

    /** Justificativa dada pelo coordenador para reconsideração */
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

    /** Texto com o parecer do analisador da solicitação membro da proex */
    @Column(name = "parecer")
    private String parecer;

    /**  Solicitação aprovada pela PROEx */
    @Column(name = "aprovado")
    private boolean aprovado;

    /** Informa se a solicitação está ativa. */
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

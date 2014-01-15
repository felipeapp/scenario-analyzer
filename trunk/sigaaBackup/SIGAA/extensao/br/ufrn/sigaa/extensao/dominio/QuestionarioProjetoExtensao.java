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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.questionario.dominio.Questionario;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;

@Entity
@Table(schema = "extensao", name = "questionario_projeto_extensao")
public class QuestionarioProjetoExtensao implements Validatable {
	
	/** Constante que define o tipo coordenador. */
	public static final int COOORDENADOR = 1;
	
    /** identificador da ação */
    @Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="sequence_name", value="extensao.questionario_projeto_sequence") })
	@Column(name = "id_questionario_projeto", unique = true, nullable = false)
    private int id;

    /** Projeto associado à atividade. Contém os dados básicos da Ação de Extensão. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_projeto")
    private Projeto projeto;

    /** Projeto associado à atividade. Contém os dados básicos da Ação de Extensão. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_questionario")
    private Questionario questionario;

    /** Pessoa responsável por responder o questionário. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pessoa")
    private Pessoa pessoa;
    
	/** Data de cadastro do componente. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	private Date dataCadastro;
    
	/** Registro entrada do usuário que cadastrou. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	private RegistroEntrada registroCadastro;

	/** Data da última atualização do componente. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_resposta")
	private Date dataResposta;

	/** Registro entrada do usuário que realizou a última atualização. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_resposta")
	private RegistroEntrada registroResposta;

	/** Indica se o componente é ativo ou não */
	@CampoAtivo(true)
	private boolean ativo = true;

	/** Indica o tipo de grupos que o questionário vai ser aplicado */
	@Column(name = "tipo_grupo")
	private int tipoGrupo;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_questionario_resposta")
    private QuestionarioRespostas questionarioResposta;

    /** Tipo de atividade de extensão que o questionário será aplicado. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_atividade_extensao")
    private TipoAtividadeExtensao tipoAtividade;
    
	/** Indica se o questionário é obrigatório ou não */
	@CampoAtivo(false)
	private boolean obrigatoriedade;
    
	/** Edital que se deseja que os coordenadores e/ou boslistas respondam o questionário */
	@Transient
	private int edital;

	/** Filtro pelo ano do edital */
	@Transient
	private Integer ano;

	@Transient
	private String nomeGrupo;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public Questionario getQuestionario() {
		return questionario;
	}

	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataResposta() {
		return dataResposta;
	}

	public void setDataResposta(Date dataResposta) {
		this.dataResposta = dataResposta;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public RegistroEntrada getRegistroResposta() {
		return registroResposta;
	}

	public void setRegistroResposta(RegistroEntrada registroResposta) {
		this.registroResposta = registroResposta;
	}

	public int getTipoGrupo() {
		return tipoGrupo;
	}

	public void setTipoGrupo(int tipoGrupo) {
		this.tipoGrupo = tipoGrupo;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public int getEdital() {
		return edital;
	}

	public void setEdital(int edital) {
		this.edital = edital;
	}

	public boolean isCoordenador(){
		return tipoGrupo == COOORDENADOR;
	}
	
	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public String getNomeGrupo() {
		return nomeGrupo;
	}

	public void setNomeGrupo(String nomeGrupo) {
		this.nomeGrupo = nomeGrupo;
	}

	public QuestionarioRespostas getQuestionarioResposta() {
		return questionarioResposta;
	}

	public void setQuestionarioResposta(QuestionarioRespostas questionarioResposta) {
		this.questionarioResposta = questionarioResposta;
	}

	public TipoAtividadeExtensao getTipoAtividade() {
		return tipoAtividade;
	}

	public void setTipoAtividade(TipoAtividadeExtensao tipoAtividade) {
		this.tipoAtividade = tipoAtividade;
	}

	public boolean isObrigatoriedade() {
		return obrigatoriedade;
	}

	public void setObrigatoriedade(boolean obrigatoriedade) {
		this.obrigatoriedade = obrigatoriedade;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequiredId(questionario.getId(), "Questionário", lista);
		ValidatorUtil.validateRequiredId(tipoGrupo, "Grupo de Usuário", lista);
		if ( ano == null && edital == 0 ) {
			lista.addErro("É necessário informar o ano ou o edital.");
		} 
		return lista;
	}

}
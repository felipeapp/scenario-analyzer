/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criada em: 08/12/2008
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import java.util.Date;
import java.util.HashMap;

import javax.persistence.CascadeType;
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
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.ParticipanteExterno;
import br.ufrn.sigaa.parametros.dominio.ParametrosExtensao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;
/*******************************************************************************
 * <p>
 * Representa os Membros do Projeto. 
 * O membro do projeto pode ser do tipo Docente, Discente, Servidor ou Externo.
 * Pode ser remunerado ou não, informar sua função(ex.: Coordenador, Vice-Coordenador, Colaborador, etc)
 * </p>
 * 
 * @author Ricardo Wendell
 * 
 ******************************************************************************/
@Entity
@Table(name = "membro_projeto", schema = "projetos", uniqueConstraints = {})
public class MembroProjeto implements PersistDB, ViewAtividadeBuilder {
	
	/** Atributo utilizado como constante para a carga horária semanal máxima permitida */
	public static final int CARGA_HORARIA_SEMANAL_MAXIMA_PERMITIDA = 32;

	/** Chave principal */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_membro_projeto")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="projetos.membro_projetos_sequence") })
	private int id;
	
	/** Deve ser definido para todos */ 
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pessoa", nullable = false)
	private Pessoa pessoa = new Pessoa();
	
	/** Utilizado para todos os servidores, incluindo os docentes */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_servidor")
	private Servidor servidor = new Servidor();

	/** Discentes participantes do projeto como colaboradores */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente")
	private Discente discente = new Discente();
	
	/** Docentes externos */
	@ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_docente_externo")
	private DocenteExterno docenteExterno = new DocenteExterno();
	
	/** Categoria do membro: Docente, discente, servidor, externo */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_categoria_membro")
	private CategoriaMembro categoriaMembro;

	/** Substituído por projetos.FuncaoMembro */
	@Deprecated
	@Column(name = "tipo_participacao")
	private Integer tipoParticipacao;

	/** Data de início do Membro no projeto */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio")
	private Date dataInicio;

	/** Data de início do Membro no projeto */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim")
	private Date dataFim;

	/** Carga Horário dedicada ao Projeto */
	@Column(name = "ch_dedicada")
	private Integer chDedicada;

	/** Serve para identificar se o Membro Projeto está em uso ou não */
	@Column(name = "ativo")
	@CampoAtivo
	private boolean ativo = true;

	/** Serve para informar a carga horária necessária para a execução do Projeto pelo membro */
	@Column(name = "ch_execucao")
	private Integer chExecucao;
    
    /** Serve para informar a carga horária necessária para a preparação do Projeto pelo membro */
    @Column(name = "ch_preparacao")
    private Integer chPreparacao;

    /** Serve para identificar de qual projeto o membro em questão faz parte. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_projeto", nullable = false)
    private Projeto projeto;

	/** Serve para identificar a função que o membro desempenha no projeto */
    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_funcao_membro")
    private FuncaoMembro funcaoMembro;

    /** Serve para informar se a função que o membro desempenha e remunerado ou não. */
    @Column(name = "remunerado")
    private boolean remunerado;
    
    /** utilizado para permitir a que tal membro gerencie participantes de ação de extensão */
    @Column(name = "gerencia_participantes")
    private boolean gerenciaParticipantes = false;
    
    /** Atributo utilizado para informar se o Membro está ou não selecionado */
    @Transient
    private boolean selecionado = false;
    
    /** Serve para idenficar os participantes externos, que fazem parte do Projeto. */
	@ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_participante_externo")
    private ParticipanteExterno participanteExterno;
    
	/** Registra o registro de entrada do usuário no caso de uso.*/
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")    	
	private RegistroEntrada registroEntrada;
    
	/** Guarda temporariamente a atividade do projeto desse membro. */
	@Transient
	private AtividadeExtensao atividade;
	
	/** Construtor da classe */
	public MembroProjeto() {
	}

	/** Construtor Mínimo */
	public MembroProjeto(int id) {
		this.id = id;
	}

	public MembroProjeto(int id, int idPessoa) {
		this.id = id;
		this.pessoa.setId( idPessoa );
	}

	/**
	 * Usado para otimizar os projetos a serem carregados para o PID 
	 * 
	 * @param idMembroProjeto
	 * @param chDedicadaMembroProjeto
	 * @param idProjeto
	 * @param anoProjeto
	 * @param idSituacaoProjeto
	 * @param idServidor
	 * @param ativo
	 */
	public MembroProjeto(int idMembroProjeto, Integer chDedicadaMembroProjeto, int idProjeto, Integer anoProjeto, String tituloProjeto, int idSituacaoProjeto, 
			int idServidor, boolean ativo, Date dataInicio, Date dataFim, String descricaoSituacaoProjeto) {
		this.id = idMembroProjeto;
		
		if (chDedicadaMembroProjeto == null)
			this.chDedicada = 0;
		else
			this.chDedicada = chDedicadaMembroProjeto;
		
		this.projeto = new Projeto(idProjeto);
		this.projeto.setAno(anoProjeto);
		
		TipoSituacaoProjeto situacaoProjeto = new TipoSituacaoProjeto(idSituacaoProjeto, descricaoSituacaoProjeto);
		
		this.projeto.setSituacaoProjeto(situacaoProjeto);
		this.projeto.setTitulo(tituloProjeto);
		
		this.servidor.setId(idServidor);
		
		this.ativo = ativo;
		
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
	}
	
	/** Teste para verificar se os objetos passados são os mesmos ou não. */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "pessoa.id");
	}

	/**
	 * DOCENTE = 1; DISCENTE = 2; SERVIDOR = 3; EXTERNO = 4;
	 * @return
	 */
	public CategoriaMembro getCategoriaMembro() {
		return categoriaMembro;
	}

	/** Retorna a Carga Horária Dedicada */
	public Integer getChDedicada() {
		return chDedicada;
	}

	/** Retorna o tipo de Participação */
	public Integer getTipoParticipacao() {
		return tipoParticipacao;
	}

	/** Retorna a data Final */
	public Date getDataFim() {
		return dataFim;
	}

	/** Retorna a Data Inicial */
	public Date getDataInicio() {
		return dataInicio;
	}

	/** Retorna o Discente */
	public Discente getDiscente() {
		return discente;
	}

	/** Retorna o Docente Externo */
	public DocenteExterno getDocenteExterno() {
		return docenteExterno;
	}

	/** Retorna a chave primária */
	public int getId() {
		return id;
	}

	/** Retorna a pessoa */
	public Pessoa getPessoa() {
		return pessoa;
	}

	/** Retorna o Projeto */
	public Projeto getProjeto() {
		return projeto;
	}

	/** Retorna o Servidor */
	public Servidor getServidor() {
		return servidor;
	}

	/** Retorna o Participante Externo */
	public ParticipanteExterno getParticipanteExterno() {
		return participanteExterno;
	}

	/** Seta o Participante Externo */
	public void setParticipanteExterno(ParticipanteExterno participanteExterno) {
		this.participanteExterno = participanteExterno;
	}

	/** Retorna o estado do objeto, se está em uso ou não */
	public boolean isAtivo() {
		return ativo;
	}

	/** Cria um hash apartir dos argumentos passando no parâmetro */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, pessoa.getId());
	}

	/** Seta a Categoria do Membro */
	public void setCategoriaMembro(CategoriaMembro categoriaMembro) {
		this.categoriaMembro = categoriaMembro;
	}

	/** Seta a Carga horária dedicada para o projeto */
	public void setChDedicada(Integer chDedicada) {
		this.chDedicada = chDedicada;
	}
	
	/** Seta o tipo de Participação */
	@Deprecated
	public void setTipoParticipacao(Integer tipoParticipacao) {
		this.tipoParticipacao = tipoParticipacao;
	}

	/** Seta a Data final */
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	/** Seta a data Inicial */
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	/** Seta o discente */
	public void setDiscente(Discente discente) {
		this.discente = discente;
	}
	
	/** Seta o Docente Externo */
	public void setDocenteExterno(DocenteExterno docenteExterno) {
		this.docenteExterno = docenteExterno;
	}

	/** Seta a chave primária */
	public void setId(int id) {
		this.id = id;
	}
	
	/** Seta uma pessoa */
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	/** Seta o projeto */
	public void setProjeto(Projeto projeto) {
	    this.projeto = projeto;
	}

	/** Seta um servidor para o projeto */
	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	/** Seta se o projeto está ou não em uso. */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Retorna a Carga horária da execução */
    public Integer getChExecucao() {
        return chExecucao;
    }
    
    /** Seta a Carga horária para a execução */
    public void setChExecucao(Integer chExecucao) {
        this.chExecucao = chExecucao;
    }
    
    /** Retorna a Carga Horária de preparação */    
    public Integer getChPreparacao() {
        return chPreparacao;
    }

    /** Seta a carga horária usada na preparação */
    public void setChPreparacao(Integer chPreparacao) {
        this.chPreparacao = chPreparacao;
    }
    
    /**
     *  Função do membro no projeto
     *  COORDENADOR;
	 *  VICE_COORDENADOR;
	 *  COLABORADOR;
	 *  MINISTRANTE;
	 *  INSTRUTOR / SUPERVISOR;
	 *  CONSULTOR / TUTOR;
	 *  ORIENTADOR;
	 *  ASSESSOR;
	 *  PALESTRANTE;
	 *  AUXILIAR TÉCNICO;
	 *  PRESTADOR DE SERVIÇOS;
	 *  MONITOR;
	 *  ALUNO BOLSISTA;
	 *  ALUNO VOLUNTARIO;
	 *  OUTROS;
	 *
     * @return
     */
    public FuncaoMembro getFuncaoMembro() {
        return funcaoMembro;
    }

    /** Seta a função do Membro no projeto */
    public void setFuncaoMembro(FuncaoMembro funcaoMembro) {
        this.funcaoMembro = funcaoMembro;
    }
    
    /** Retorna no registro de Entrada */
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}
    
	/** Seta o registro de entrada */
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
    
    /**
     * Informa se o membro da equipe é remunerado pela função Exercida no projeto. 
     * @return
     */    	
	public boolean isRemunerado() {
		return remunerado;
	}

	/** Seta é o projeto e remunerado ou não. */
	public void setRemunerado(boolean remunerado) {
		this.remunerado = remunerado;
	}
    
	/** Retorna se o mesmo e coordenador ou não */
	public boolean isCoordenador() {
	    return (getFuncaoMembro() != null) && (getFuncaoMembro().getId() == FuncaoMembro.COORDENADOR);
	}

	/** Retorna se o mesmo e o coordenador atual do projeto ou não com base nas datas de início e fim */
	public boolean isCoordenadorAtivo() {
	    return isCoordenador() && this.isAtivo() && this.isValido() 
    		&& this.getProjeto().isValido();
	}
	
	/**
	 * Método utilizado para função específica de Alterar Coordenador do Projeto.
	 * @return true caso o membro atual seja o coordenador atual do projeto
	 */
	public boolean isCoordenadorProjeto(){
		return isCoordenadorAtivo() && ValidatorUtil.isNotEmpty(getProjeto().getCoordenador()) && getProjeto().getCoordenador().getId() == this.getId();
	}

	/** Não está sendo realizada nenhuma validação */
	public ListaMensagens validate() {
		return null;
	}
	
	/** Verifica se foi selecionado ou não. */
	public boolean isSelecionado() {
		return selecionado;
	}
    
	/** Seta os Selecionado */
	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}
    
	/** Verifica se e colaborador ou não  */
	public boolean isColaborador() {
		return (getFuncaoMembro() != null) && (getFuncaoMembro().getId() == FuncaoMembro.COLABORADOR);
	}
    
	/** Retorna os Itens na view */
	public String getItemView() {
		return "  <td>" + getTitulo() + "</td>" +
		   "  <td>"+Formatador.getInstance().formatarData(dataInicio) + " - " +Formatador.getInstance().formatarData(dataFim)+"</td>";

	}
    
	/** Retorna o título */
	public String getTitulo() {
		return getProjeto().getTitulo();
	}
	
	/**
	 * Método utilizado para setar o títilo do projeto relacionado ao membro.
	 * @param titulo
	 */
	public void setTitulo(String titulo) {
		if(projeto == null) 
			projeto = new Projeto();
		projeto.setTitulo(titulo);
	}

	/** Retorna o título na view */
	public String getTituloView() {
		return "    <td>Nome da Atividade</td>" +
			   "    <td>Período</td>";
	}

	/** Retorna todos os itens do projeto tais como projeto, dataInicio, dataFim */
	public HashMap<String, String> getItens() {
		HashMap<String, String> itens = new HashMap<String, String>();
		itens.put("projeto", null);
		itens.put("dataInicio", null);
		itens.put("dataFim", null);
		itens.put("funcaoMembro", null);
		return itens;
	}
    
	/** Retorna a quantidade de Base */
	public float getQtdBase() {
		return 1;
	}
    
	/**
	 * Informa se membro do projeto pode receber certificado. 
	 * @return
	 */
	public boolean isPassivelEmissaoCertificado() {
		return (isFinalizado() && projeto.isConcluido()) || projeto.isAutorizarCertificadoGestor();
	}	
    
	/**
	 * Informa se membro do projeto pode receber declaração. 
	 * @return
	 */
	public boolean isPassivelEmissaoDeclaracao() {
		return ((getDataInicio() != null) 
		&& ((new Date()).after(CalendarUtils.descartarHoras(getDataInicio())) || CalendarUtils.descartarHoras(new Date()).equals(CalendarUtils.descartarHoras(getDataInicio())) ) 
		&& !isFinalizado()) || projeto.getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO;
	}

	/** Verifica e o mesmo e da categoria Discente */
	public boolean isCategoriaDocente(){
		return getCategoriaMembro().isDocente();
	}

	/** Verifica se o mesmo e da categoria servidor */
	public boolean isCategoriaServidor(){
		return getCategoriaMembro().isApenasServidor();
	}

	/** Verifica se o mesmo e da categoria servidor */
	public boolean isCategoriaDiscente(){
		return getCategoriaMembro().isDiscente();
	}

	/**
	 * Informa se o membro da equipe do projeto está finalizado.
	 * 
	 * @return
	 */
	public boolean isFinalizado() {
	    return isAtivo() && isValido() && getDataFim().before(CalendarUtils.descartarHoras(new Date()));
	}
	
	/**
	 * Indica, com base na data início e data fim, se o membro do projeto já iniciou sua participação.
	 * @return
	 */
	public boolean isValido() {
	    return (getDataInicio() != null) && (getDataFim() != null);
	}

	/**
	 * Verifica, com base na data início e data fim, se a o membro está atuante no projeto.
	 * @return
	 */
	public boolean isVigente() {
	    return projeto.isVigente() && isAtivo() && isValido() && CalendarUtils.isDentroPeriodo(getDataInicio(), getDataFim());
	}
	
	/**
	 * Retorna o status do membro do projeto com base nas datas de início e fim.
	 * 
	 * @return
	 */
	public String getStatus() {
	    return isFinalizado() ? "Finalizado" : isVigente() ? "Ativo" : "Inativo" ;
	}

	
	/**
	 * Retorna a carga horária que deverá ser informada no certificado/declaração do membro da equipe. 
	 * 
	 * @return
	 */
	public Integer getChCertificadoDeclaracao() {
		int ch = 0;
		Date dataLimite = ParametroHelper.getInstance()
				.getParametroDate(ParametrosExtensao.DATA_LIMITE_ALTERACAO_CH_EQUIPE_EXECUTORA);
		if ( getProjeto() != null && getProjeto().getTipoProjeto() != null && getProjeto().getTipoProjeto().isExtensao() 
					&& getProjeto().getDataCadastro().after(dataLimite) ) {
			return chDedicada;
		} else {
		    if (chDedicada != null && isValido() && isAtivo()) {	    	
			    int semanas = 0;
			    
			    if (isFinalizado()) {
			    	semanas = (CalendarUtils.calculaQuantidadeSemanasEntreDatasIntervaloFechado(getDataInicio(), getDataFim()) == 0) ? 1 
			    			: CalendarUtils.calculaQuantidadeSemanasEntreDatasIntervaloFechado(getDataInicio(), getDataFim());  
			    }else {
			    	semanas = CalendarUtils.calculaQuantidadeSemanasEntreDatasIntervaloFechado(this.getDataInicio(), new Date());
			    }
		    	
			    ch = semanas * chDedicada;
		    }    
		}
	    return ch;
	}
	
	/**
	 * Retorna a unidade do Membro do Projeto
	 * @return
	 */
	public Unidade getUnidade(){
		if(servidor != null){
			return servidor.getUnidade();
		}else if(docenteExterno != null){
			return docenteExterno.getUnidade();
		}else if(discente != null){
			return discente.getUnidade();
		}
		return null;
	}
	
	/**
	 * Retorna o nome da pessoa do Membro Projeto
	 * @return
	 */
	public String getNomeMembroProjeto(){
		return pessoa.getNome();
	}

	public boolean isGerenciaParticipantes() {
		return gerenciaParticipantes;
	}

	public void setGerenciaParticipantes(boolean gerenciaParticipantes) {
		this.gerenciaParticipantes = gerenciaParticipantes;
	}

	public AtividadeExtensao getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeExtensao atividade) {
		this.atividade = atividade;
	}
   	
}

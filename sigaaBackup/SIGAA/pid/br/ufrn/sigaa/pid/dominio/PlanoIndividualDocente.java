/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criada em: 29/10/2009
 *
 */

package br.ufrn.sigaa.pid.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.ead.dominio.CargaHorariaEad;
import br.ufrn.sigaa.parametros.dominio.ParametrosPortalDocente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.CHDedicadaResidenciaMedica;

/**
 * Entidade que representa o  Plano Individual do Docente - PID.
 * O PID � composto por v�rias tipos de cargas hor�rias, 
 * como CH de Ensino, CH de Orienta��o, etc.
 *  
 * @author agostinho campos
 *
 */
@Entity
@Table(name = "plano_individual_docente", schema = "pid")
public class PlanoIndividualDocente implements PersistDB, Validatable {

	/** Status do PID de cadastrado, � inicializado com esse status. */
	public static final int CADASTRADO = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.CADASTRADO );
	/** Status do PID quando homologado. */
	public static final int HOMOLOGADO = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.HOMOLOGADO );
	/** Status do PID aguardando homologa��o */
	public static final int ENVIADO_HOMOLOGACAO = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.ENVIADO_HOMOLOGACAO );
	/** Constante que define o ano inicial do cadastramento do Plano Individual do Docente - PID */
	public static final int ANO_INICIO = ParametroHelper.getInstance().getParametroInt(ParametrosPortalDocente.ANO_INICIAL_CADASTRO_PID );
	/** Constante que define o per�odo inicial do cadastramento do Plano Individual do Docente - PID */
	public static final int PERIODO_INICIO = ParametroHelper.getInstance().getParametroInt(ParametrosPortalDocente.PERIODO_INICIAL_CADASTRO_PID );
	
	/**
	 * Define a unicidade na base de dados.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_plano_individual_docente")
	private int id;

	/**
	 * Define o servidor associado ao PID
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_servidor")
	private Servidor servidor;

	/**
	 * Define quem e quando foi cadastrado.
	 */
	@CriadoPor
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada")
    private RegistroEntrada registroEntrada;
	
	/**
	 * Data de quando a homologa��o � realizada pelo chefe do departamento 
	 */
	@Column(name="data_homologacao")
	private Date dataHomologacao;
	
	/**
	 * Tempo dedicado a orienta��o de alunos de gradua��o ou p�s-gradua��o, bem como 
	 * atendimento aos alunos
	 */
	@ManyToOne(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_carga_horaria_ensino")
	private CargaHorariaEnsino chEnsino = new CargaHorariaEnsino();
	
	/**
	 * Tempo dedicado a CH de projeto de pesquisa ou extens�o. 
	 */
	@ManyToOne(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_carga_horaria_projeto")
	private CargaHorariaProjeto chProjeto = new CargaHorariaProjeto();
	
	/**
	 * 	O docente pode dedicar o tempo dele para "Atividades de Ensino" ou "Outras Atividades".
	 * 
	 *  O campo "Outras Atividades" se refere a atividades desenvolvidas em Cursos de Gradua��o e 
	 *  p�s-gradua��o e/ou outros projetos institucionais com remunera��o espec�fica, mediante 
	 *  autoriza��o do CONSEPE.
	 *	
	 */
	@ManyToOne(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_carga_horaria_outras_atividades")
	private CargaHorariaOutrasAtividades chOutrasAtividades = new CargaHorariaOutrasAtividades();
	
	/**
	 * O docente pode selecionar um conjunto de v�rias atividades na qual ele trabalha/atua.
	 * 	
	 */
	@OneToMany(mappedBy="planoIndividualDocente", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name = "id_plano_individual_docente")
	private List<CargaHorariaAtividadesComplementares> chAtividadesComplementares = new ArrayList<CargaHorariaAtividadesComplementares>();
	
	/**
	 * Representa o tempo que ele dedicada para as designa��es que possa vir a possuir. 
	 */
	@OneToMany(mappedBy="planoIndividualDocente", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name = "id_plano_individual_docente")
	private List<CargaHorariaAdministracao> designacoesDocente = new ArrayList<CargaHorariaAdministracao>();
	
	/**
	 * Carga hor�ria dedicada ao ensino de turmas � dist�ncia
	 */
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="plano_individual_docente_ch_ead", schema="pid",
			joinColumns=@JoinColumn(name="id_plano_individual_docente"),  
			inverseJoinColumns=@JoinColumn(name="id_carga_horaria_ead"))
	private List<CargaHorariaEad> chEnsinoEad = new ArrayList<CargaHorariaEad>();
	
	/**
	 * Orienta��es de Gradua��o e P�s-Gradua��o do docente
	 */
	@OneToMany(mappedBy="planoIndividualDocente", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name = "id_plano_individual_docente")
	private List<CargaHorariaOrientacao> chOrientacao = new ArrayList<CargaHorariaOrientacao>();
	
	/**
	 * Define a data que foi cadastrado o PID.
	 */
	@CriadoEm
	@Column(name="data_cadastro")
	private Date dataCadastro;
	
	/**
	 * Total da CH que � definida para o grupo que envolve as orienta��es 
	 * de gradua��o cadastrada, atendimento aos alunos e orienta��o de p�s-gradua��o.
	 */
	@Column(name="ch_total_grupo_ensino")
	private double totalGrupoEnsino;
	
	/**
	 * Total da CH que � definida para o grupo que envolve as orienta��es 
	 * de gradua��o cadastrada, atendimento aos alunos e orienta��o de p�s-gradua��o.
	 */
	@Column(name="ch_total_grupo_outras_ativ")
	private double totalGrupoOutrasAtividades;
	
	/**
	 * Carga Hor�ria Administra��o
	 */
	@Column(name="ch_total_administracao")
	private double chTotalAdministracao;
	
	/**
	 * Valor em percentual do tempo dedicado pelo docente
	 */
	@Column(name="percentual_ch_dedicada_admin")
	private Integer percentualAdministracao = 0;
	
	/**
	 * Define a carga hor�ria da resid�ncia m�dica
	 */
	@OneToMany(mappedBy="planoIndividualDocente",fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="id_plano_individual_docente")
	private List<ChResidenciaMedicaPID> chResidenciaMedicaPID = new ArrayList<ChResidenciaMedicaPID>();
	
	/**
	 * Status do PID. Ao cadastrar um novo PID o default � CADASTRADO 
	 */
	private int status;
	
	/**
	 * Define oa no de refer�ncia do PID.
	 */
	private int ano;
	
	/**
	 * Observa��o registrada pelo docente ao enviar o PID
	 */
	private String observacao;
	
	/**
	 * Observa��o feita pelo chefe do departamento ao homologar/recusar o PID
	 */
	@Column(name="obs_chefe_departamento")
	private String observacaoChefeDepartamento;
	
	/**
	 * Define o per�odo de refer�ncia do PID.
	 */
	private int periodo;

	/**
	 * Docente precisa informar que as informa��es do PID est�o corretas 
	 */
	@Transient
	private boolean concordanciaTermoEnvioPID;
	
	/**
	 * Descri��o da atividade que � adicionada manualmente pelo docente
	 */
	@Transient
	private String outrasAtividades;
	
	/**
	 * Quando o o docente adiciona um atividade manualmente ele precisa informar que a mesma � autorizada pela CONSEPE
	 */
	@Transient
	private Boolean atividadeAutorizadaCONSEPE;
	
	/**
	 * O docente tamb�m pode adicionar atividades espec�ficas de acordo com a necessidade.
	 */
	@OneToMany(mappedBy="planoIndividualDocente", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JoinColumn(name = "id_atividades_especificas_docente")
	private List<AtividadesEspecificasDocente> atividadesEspecificasDocente;
	
	/**
	 * Representa CH ultrapassada quando o docente tem mais turmas do que a CH do seu regime
	 * de trabalho.
	 * 
	 */
	@Transient
	private boolean cargaHorariaDocenteExcedida;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public CargaHorariaEnsino getChEnsino() {
		return chEnsino;
	}

	public void setChEnsino(CargaHorariaEnsino chEnsino) {
		this.chEnsino = chEnsino;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public void setChAtividadesComplementares(List<CargaHorariaAtividadesComplementares> chAtividadesComplementares) {
		this.chAtividadesComplementares = chAtividadesComplementares;
	}

	public CargaHorariaProjeto getChProjeto() {
		return chProjeto;
	}

	public void setChProjeto(CargaHorariaProjeto chProjeto) {
		this.chProjeto = chProjeto;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	public double getTotalGrupoEnsino() {
		return totalGrupoEnsino;
	}
	
	public void setTotalGrupoEnsino(double totalGrupoEnsino) {
		this.totalGrupoEnsino = totalGrupoEnsino;
	}

	public double getTotalGrupoOutrasAtividades() {
		return totalGrupoOutrasAtividades;
	}

	public void setTotalGrupoOutrasAtividades(double totalGrupoOutrasAtividades) {
		this.totalGrupoOutrasAtividades = totalGrupoOutrasAtividades;
	}

	public CargaHorariaOutrasAtividades getChOutrasAtividades() {
		return chOutrasAtividades;
	}
	
	public void setChOutrasAtividades(
			CargaHorariaOutrasAtividades chOutrasAtividades) {
		this.chOutrasAtividades = chOutrasAtividades;
	}
	
	public List<CargaHorariaOrientacao> getChOrientacao() {
		return chOrientacao;
	}

	public void setChOrientacao(List<CargaHorariaOrientacao> chOrientacao) {
		this.chOrientacao = chOrientacao;
	}

	public double getChTotalAdministracao() {
		return chTotalAdministracao;
	}

	public void setChTotalAdministracao(double chTotalAdministracao) {
		this.chTotalAdministracao = chTotalAdministracao;
	}

	public Integer getPercentualAdministracao() {
		return percentualAdministracao;
	}

	public void setPercentualAdministracao(Integer percentualAdministracao) {
		this.percentualAdministracao = percentualAdministracao;
	}
	
	public List<CargaHorariaAtividadesComplementares> getChAtividadesComplementares() {
		return chAtividadesComplementares;
	}
	
	public List<CargaHorariaAdministracao> getDesignacoesDocente() {
		return designacoesDocente;
	}
	public void setDesignacoesDocente(
			List<CargaHorariaAdministracao> designacoesDocente) {
		this.designacoesDocente = designacoesDocente;
	}

	public String getOutrasAtividades() {
		return outrasAtividades;
	}

	public void setOutrasAtividades(String outrasAtividades) {
		this.outrasAtividades = outrasAtividades;
	}
	
	public Boolean getAtividadeAutorizadaCONSEPE() {
		return atividadeAutorizadaCONSEPE;
	}

	public void setAtividadeAutorizadaCONSEPE(Boolean atividadeAutorizadaCONSEPE) {
		this.atividadeAutorizadaCONSEPE = atividadeAutorizadaCONSEPE;
	}
	
	public List<AtividadesEspecificasDocente> getAtividadesEspecificasDocente() {
		return atividadesEspecificasDocente;
	}

	public void setAtividadesEspecificasDocente(
			List<AtividadesEspecificasDocente> atividadesEspecificasDocente) {
		this.atividadesEspecificasDocente = atividadesEspecificasDocente;
	}
	
	public List<ChResidenciaMedicaPID> getChResidenciaMedicaPID() {
		return chResidenciaMedicaPID;
	}

	public void setChResidenciaMedicaPID(
			List<ChResidenciaMedicaPID> chResidenciaMedicaPID) {
		this.chResidenciaMedicaPID = chResidenciaMedicaPID;
	}

	/**
	 * M�todo para auxiliar a associa��o entre a CHDedicadaResidenciaMedica e o PID 
	 * 
	 * @param chEnsinoDocente
	 * @param docenteTurma
	 */
	public void addCHResidenciaMedicaPID(CHDedicadaResidenciaMedica chDedicadaResidenciaMedica, ChResidenciaMedicaPID residenciaMedicaPID) {
		residenciaMedicaPID.setPlanoIndividualDocente(this);
		residenciaMedicaPID.setChDedicadaResidenciaMedica(chDedicadaResidenciaMedica);
		chResidenciaMedicaPID.add(residenciaMedicaPID);
	}
	
	/**
	 * Valida se o usu�rio tentou adicionar atividades espec�ficas ao PID
	 * @return
	 */
	public ListaMensagens validateOutrasAtividades() {
		ListaMensagens lista = new ListaMensagens();
		if (ValidatorUtil.isEmpty(outrasAtividades))
			lista.addErro("Voc� n�o pode adicionar uma atividade vazia.");
		
		return lista;
	}
	
	/**
	 * Valida entrada do usu�rio
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRange(chProjeto.getPercentualPesquisa(), 0, 100, "Percentual Pesquisa", lista);
		ValidatorUtil.validateRange(chProjeto.getPercentualExtensao(), 0, 100, "Percentual Extens�o", lista);
		ValidatorUtil.validateRange(getPercentualAdministracao(), 0, 100, "Percentual Administra��o", lista);
		ValidatorUtil.validateRange(chOutrasAtividades.getPercentualOutrasAtividades(), 0, 100, "Percentual Outras Atividades", lista);
		ValidatorUtil.validateRange(chOutrasAtividades.getPercentualOutrasAtividadesEnsino(), 0, 100, "Percentual Outras Atividades Ensino", lista);
		ValidatorUtil.validateMaxValue(totalGrupoEnsino, 40d, "TOTAL DE CARGA HOR�RIA DE ENSINO", lista);
		
		if (chEnsino.getChAtendimentoAluno()==null){
			lista.addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Carga Hor�ria Atendimento Aluno");
			chEnsino.setChAtendimentoAluno(0.0);
		}	
		if (chEnsino.getChOrientacoesAlunosGraduacao()==null){
			lista.addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Carga Hor�ria Orienta��o Alunos de Gradua��o");
			chEnsino.setChOrientacoesAlunosGraduacao(0.0);
		}	
		if (chEnsino.getChOrientacoesAlunosPosGraduacao()==null){
			lista.addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Carga Hor�ria Orienta��o Alunos de P�s-Gradua��o");
			chEnsino.setChOrientacoesAlunosPosGraduacao(0.0);
		}	
		if (chOutrasAtividades.getPercentualOutrasAtividadesEnsino()==null){
			lista.addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Outras Atividades de Ensino");
			chOutrasAtividades.setPercentualOutrasAtividadesEnsino(0);
		}	
		if (chOutrasAtividades.getPercentualOutrasAtividades()==null){
			lista.addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Outras Atividades");
			chOutrasAtividades.setPercentualOutrasAtividades(0);
		}	
		if (chProjeto.getPercentualPesquisa()==null){
			lista.addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Pesquisa Produ��o Acad�mica");
			chProjeto.setPercentualPesquisa(0);
		}	
		if (chProjeto.getPercentualExtensao()==null){
			lista.addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Extens�o e Outras Atividades");
			chProjeto.setPercentualExtensao(0);
		}	
		if (percentualAdministracao==null){
			lista.addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Fun��es Administrativas");
			percentualAdministracao = 0;
		}
		
		if (!getChEnsino().getChEnsinoDocenteTurma().isEmpty())
			ValidatorUtil.validateRange(chEnsino.getChAtendimentoAluno(), 2.0, 4.0, "Carga Hor�ria Atendimento Aluno", lista);
		
		double total = UFRNUtils.truncateDouble(totalGrupoEnsino + totalGrupoOutrasAtividades, 1);
		if ( !isCargaHorariaDocenteExcedida() ) {
			if (total > servidor.getRegimeTrabalho() ) 
				lista.addErro("A soma da Carga Hor�ria de Ensino e de Outras Atividades n�o pode ultrapassar a CH do seu regime de trabalho.");
		}
		
		return lista;
	}
	
	
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Date getDataHomologacao() {
		return dataHomologacao;
	}

	public void setDataHomologacao(Date dataHomologacao) {
		this.dataHomologacao = dataHomologacao;
	}
	
	public String getObservacaoChefeDepartamento() {
		return observacaoChefeDepartamento;
	}

	public void setObservacaoChefeDepartamento(String observacaoChefeDepartamento) {
		this.observacaoChefeDepartamento = observacaoChefeDepartamento;
	}

	@Transient
	public boolean isEnviadoHomologacao() {
		return status == ENVIADO_HOMOLOGACAO ? true : false;  
	}
	
	@Transient
	public boolean isCadastrado() {
		return status == CADASTRADO ? true : false;
	}
	
	@Transient
	public boolean isHomologado() {
		return status == HOMOLOGADO ? true : false;
	}
	
	public boolean isConcordanciaTermoEnvioPID() {
		return concordanciaTermoEnvioPID;
	}

	public void setConcordanciaTermoEnvioPID(boolean concordanciaTermoEnvioPID) {
		this.concordanciaTermoEnvioPID = concordanciaTermoEnvioPID;
	}

	/**
	 * Descri��o apresentada na tela de listagem dos PID enviados
	 * para a chefia do departamento.
	 * @return
	 */
	@Transient
	public String getDescricaoStatusChefiaDepartamento() {
		if (status == HOMOLOGADO)
			return "HOMOLOGADO";
		if (status == ENVIADO_HOMOLOGACAO)
			return "PENDENTE HOMOLOGA��O";
		if (status == CADASTRADO)
			return "CADASTRANDO";
		
		return "N�O CADASTRADO";
	}
	
	/**
	 * Descri��o exibida na listagem dos PIDs cadastrados pelos docentes.
	 * @return
	 */
	@Transient
	public String getDescricaoStatus() {
		if (getId() == 0)
			return "N�O CADASTRADO";
		if (status == CADASTRADO)
			return "CADASTRADO";
		if (status == HOMOLOGADO)
			return "HOMOLOGADO";
		if (status == ENVIADO_HOMOLOGACAO)
			return "SUBMETIDO PARA HOMOLOGA��O DA CHEFIA";
		return "";
	}
	
	/**
	 * Retorna o total de horas dedicadas a orienta��es e acompanhamento de discentes
	 * 
	 * @return
	 */
	public double getTotalChOrientacoes() {
		return getChEnsino().calcularCHEnsino();
	}
	
	/**
	 * Retorna o total semanal de horas dedicadas a ensino em turmas 
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public double getTotalChTurmas() throws DAOException {
		double chTotalDisciplinas = 0.0;
		
		for (ChEnsinoPIDocenteTurma chEnsino : getChEnsino().getChEnsinoDocenteTurma()) {
			if (chEnsino.getDocenteTurma() != null) {
				if (!chEnsino.getDocenteTurma().isChResidenciaSemTurma() && chEnsino.getDocenteTurma().getChDedicadaSemana() != null)
					chTotalDisciplinas += chEnsino.getDocenteTurma().getChDedicadaSemana();
			}
			if (chEnsino.getDocenteTurma() != null) {
				if (chEnsino.getDocenteTurma().isChResidenciaSemTurma() && chEnsino.getDocenteTurma().getChDedicadaPeriodo() != null)
					chTotalDisciplinas += chEnsino.getDocenteTurma().getChDedicadaPeriodo();
			}
		}
		
		return chTotalDisciplinas;
	}
	
	/**
	 * Retorna o total semanal de horas dedicadas a orienta��o de atividades 
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public double getTotalChOrientacaoAtividades() throws DAOException {
		double chTotal = 0.0;
		for (CargaHorariaOrientacao chOrientacao : this.chOrientacao) {
			chTotal += chOrientacao.getChDedicadaSemanal();
		}
		return chTotal;
	}

	public void setCHDocenteExcedida(boolean cargaHorariaDocenteExcedida) {
		this.cargaHorariaDocenteExcedida = cargaHorariaDocenteExcedida; 
	}

	public boolean isCargaHorariaDocenteExcedida() {
		return cargaHorariaDocenteExcedida;
	}

	public void setCargaHorariaDocenteExcedida(boolean cargaHorariaDocenteExcedida) {
		this.cargaHorariaDocenteExcedida = cargaHorariaDocenteExcedida;
	}

	/** Retorna uma representa��o textual deste PID, no formato: ano.periodo - nomeServidor: totalGrupoEnsino/totalGrupoOutrasAtividades
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ano+"."+periodo + " - " + servidor.getNome() + ": " + totalGrupoEnsino + "/" + totalGrupoOutrasAtividades;
	}

	public List<CargaHorariaEad> getChEnsinoEad() {
		return chEnsinoEad;
	}

	public void setChEnsinoEad(List<CargaHorariaEad> chEnsinoEad) {
		this.chEnsinoEad = chEnsinoEad;
	}
		
}


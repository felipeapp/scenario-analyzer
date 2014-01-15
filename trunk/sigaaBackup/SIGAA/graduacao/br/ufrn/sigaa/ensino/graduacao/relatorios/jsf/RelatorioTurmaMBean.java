/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/08/2009
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.graduacao.AbstractRelatorioSqlDao;
import br.ufrn.sigaa.arq.dao.graduacao.RelatorioTurmaSqlDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.jsf.OperacoesCoordenadorGeralEadMBean;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.relatorios.dao.RelatorioTurmaDao;

/**
 * MBean respons�vel pela gera��o de relat�rios de turma.
 * Relat�rios que este MBean � respons�vel:
 * 	Turmas abertas sem solicita��o.
 * 	Turmas por quantidade de docentes.
 *  Rela��o por departamento das disciplinas de est�gio com a carga hor�ria e o n�mero de professores envolvidos.
 *  Ocupa��o de vagas por turma.
 *  Turmas n�o consolidadas.
 *  
 *  
 * @author Eric Moura
 *
 */
@Component("relatorioTurma") @Scope("request")
public class RelatorioTurmaMBean extends AbstractRelatorioGraduacaoMBean {

	/** Lista contendo alguns dos dados das turmas encontradas. */
	private List<Map<String,Object>> listaTurma = new ArrayList<Map<String,Object>>();
	/** Lista turmas encontradas. */
	private Collection<Turma> turmas = new ArrayList<Turma>();
	/** Campos que poder�o ser alterados pelo usu�rio. */
	private Campos campos = new Campos();
	
	/** Campo de filtro de pesquisa por Matriculados. */
	private boolean filtroMatriculados;
	
	/** Campo de filtro de pesquisa por Ano no qual a Turma foi lecionada. */
	private boolean filtroAnoTurma;
	
	/** Campo de filtro de pesquisa por Per�odo no qual a Turma foi lecionada. */
	private boolean filtroPeriodoTurma;
	
	/** Campo de filtro de pesquisa por Situa��o da Turma. */
	private boolean filtroSituacaoTurma;
	
	/** Campo de filtro de pesquisa para contabilizar tamb�m turmas de EAD. */
	private boolean filtroContabilizarEnsinoDistancia;

	

	//Constantes de navega��o - p�ginas dos relat�rios
	//Listas
	/** Link do Relat�rio de Turmas. */
	private static final String JSP_RELATORIO_TURMA= "lista_turma.jsp";
	/** Link do Relat�rio Turmas por Quantidade de Docentes. */
	private static final String JSP_RELATORIO_DOCENTE= "lista_docente.jsp";
	/** Link do Relat�rio de Disciplinas de Est�gio. */
	private static final String JSP_RELATORIO_CH_ESTAGIO= "lista_ch_estagio.jsp";
	/** Link do Relat�rio de Turmas Ofertadas ao Curso. */
	private static final String JSP_RELATORIO_TURMAS_OFERTADAS= "lista_turmas_ofertadas.jsp";
	/** Link do formul�rio do Relat�rio de Turmas Ofertadas ao Curso. */
	private static final String JSP_SELECIONA_TURMAS_OFERTADAS= "seleciona_turmas_ofertadas.jsp";

	/** Link do formul�rio do Relat�rio de Ocupa��o de Vagas de Turmas. */
	private static final String JSP_SELECIONA_OCUPACAO_VAGAS_TURMAS= "seleciona_ocupacao_vagas.jsp";
	/** Link do Relat�rio de Ocupa��o de Vagas de Turmas. */
	private static final String JSP_REL_OCUPACAO_VAGAS_TURMAS = "rel_ocupacao_vagas.jsp";

	/** Link do Relat�rio Sint�tico De Turmas por Situa��o. */
	private static final String JSP_SITUACAO_TURMA = "situacao_turma.jsp";
	/** Link do formul�rio do Relat�rio Sint�tico De Turmas por Situa��o. */
	private static final String JSP_SELECIONA_SITUACAO_TURMA = "rel_situacao_turma.jsp";

	/** Link do formul�rio do Relat�rio dos Alunos que Solicitaram Disciplinas de F�rias. */
	private static final String JSP_SELECIONA_SOLIC_TURMA_FERIAS = "seleciona_solicitacao_turma_ferias.jsp";
	/** Link do Relat�rio dos Alunos que Solicitaram Disciplinas de F�rias. */
	private static final String JSP_ALUNOS_SOLIC_TURMA_FERIAS = "rel_solicitacao_turma_ferias.jsp";
	
	/** Link do formul�rio do Relat�rio de Turmas n�o Consolidadas. */
	private static final String JSP_SELECIONA_TURMA_NAO_CONSOLIDADA = "seleciona_turma_nao_consolidada.jsp";
	/** Link do Relat�rio de Turmas n�o Consolidadas. */
	private static final String JSP_REL_TURMA_NAO_CONSOLIDADA = "rel_turma_nao_consolidada.jsp";
	
	/** Link do formul�rio do Relat�rio de Turmas Abertas sem Solicita��o. */
	private static final String JSP_SELECIONA_TURMA_ABERTA_SEM_SOLICITACAO = "seleciona_turmas_abertas_sem_solicitacao";
	/** Link do Relat�rio de Turmas Abertas sem Solicita��o. */
	private static final String JSP_REL_TURMA_ABERTA_SEM_SOLICITACAO = "rel_turmas_abertas_sem_solicitacao";

	/** Link do formul�rio do Relat�rio dos Cursos que n�o Solicitaram Turmas. */
	private static final String JSP_SELECIONA_TURMA_CURSO = "seleciona_curso_turma.jsp";
	/** Link do Relat�rio dos Cursos que n�o Solicitaram Turmas. */
	private static final String JSP_REL_TURMA_CURSO = "rel_curso_turma.jsp";
	
	/** Link do formul�rio do Relat�rio quantitativo de turma e disciplina por departamento. */
	private static final String JSP_SELECIONA_QUANT_TURMA_DISC_DEPARTAMENTO = "seleciona_quant_turma_disciplina_departamento.jsp";
	/** Link do Relat�rio quantitativo de turma e disciplina por departamento. */
	private static final String JSP_REL_QUANT_TURMA_DISC_DEPARTAMENTO = "rel_quant_turma_disciplina_departamento.jsp";
	
	
	/** Contexto das p�ginas utilizadas. */
	private static final String CONTEXTO ="/graduacao/relatorios/turma/";
	/** Indica se a turma � de EAD. */
	private boolean ead;

	/**
	 * Construtor Padr�o
	 * Inicializa os campos do formul�rio
	 * @throws DAOException 
	 */
	public RelatorioTurmaMBean() throws DAOException {
		initObj();
		
		if (isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE, SigaaPapeis.CDP)) {
			campos.habilitarPROGRAD();
		} else if (isUserInRole(SigaaPapeis.PPG)) {
			campos.habilitarPPG();
		} else if (isUserInRole(SigaaPapeis.SECRETARIA_DEPARTAMENTO)) {
			campos.habilitarDepartamento();
			if( !isEmpty( getCursoAtualCoordenacao() ) )
				curso = getCursoAtualCoordenacao();
			if (getUsuarioLogado().getVinculoAtivo().getDiscente() != null){
				ServidorDao dao = getDAO(ServidorDao.class);
				long cpfPessoa = getUsuarioLogado().getVinculoAtivo().getDiscente().getPessoa().getCpf_cnpj();
				departamento = (dao.findByCpf(cpfPessoa).getUnidade());
			}
			else {
				departamento = getUsuarioLogado().getVinculoAtivo().getUnidade();								
			}

			setFiltroDepartamento(true); //tem que setar como true, para o DAO pesquisar pelo departamento
			setFiltroReservaCurso(true);
			setFiltroAnoPeriodo(true);
		} else if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO)) {
			campos.habilitarCoordenacao();
			curso = getCursoAtualCoordenacao();			
			if (getUsuarioLogado().getVinculoAtivo().getDiscente() != null){
				ServidorDao dao = getDAO(ServidorDao.class);
				long cpfPessoa = getUsuarioLogado().getVinculoAtivo().getDiscente().getPessoa().getCpf_cnpj();
				departamento = (dao.findByCpf(cpfPessoa).getUnidade());
			}
			if(getUsuarioLogado().getVinculoAtivo().getServidor() != null){
				departamento = getServidorUsuario().getUnidade();
			}
			if( (getUsuarioLogado().getVinculoAtivo().getServidor() == null) && (getUsuarioLogado().getVinculoAtivo().getDiscente() == null)){
				departamento = getUsuarioLogado().getVinculoAtivo().getUnidade();
			}
				
			setFiltroDepartamento(true); //tem que setar como true, para o DAO pesquisar pelo departamento
			setFiltroReservaCurso(true);
			setFiltroAnoPeriodo(true);
		} else {
			if (getAcessoMenu().isChefeDepartamento()) {
				campos.habilitarChefeDepartamento();
				departamento = getServidorUsuario().getUnidade();
				setFiltroDepartamento(true); //tem que setar como true, para o DAO pesquisar pelo departamento
			} else if (getAcessoMenu().isDiretorCentro()) {
				campos.habilitarChefeDepartamento();
				centro = getServidorUsuario().getUnidade().getUnidadeResponsavel();
				setFiltroCentro(true); //tem que setar como true, para o DAO pesquisar pelo centro
			}
			
		}
	}

	/**
	 * M�todo que verifica se o papel do Usu�rio � COORDENADOR_CURSO 
	 * ou SECRETARIA_COORDENACAO e o subsistema PORTAL_COORDENADOR
	 * 
	 */
	public boolean getCoordenadorCursoGradOuSecretarioGraduacao() throws DAOException {
		if ( (isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO))
				 && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR) )
			return true;
		else return false;  
	}
	
	/**
	 * M�todo que verifica se papel do Usu�rio � 
	 * DAE ou SECRETARIA_CENTRO e o subsistema GRADUA��O
	 */
	public boolean getDaeOuSecretarioCentroGraduacao() throws DAOException {
		if ( (isUserInRole(SigaaPapeis.DAE, SigaaPapeis.SECRETARIA_CENTRO))
				&& (getSubSistema().equals(SigaaSubsistemas.GRADUACAO)  ))
			return true;
		else return false;  
	}
	
	/**
	 * M�todo que verifica se o papel do Usu�rio � SEDIS e se o subsistema � EAD.
	 */
	public boolean getSedisEad() throws DAOException {
		if ((isUserInRole(SigaaPapeis.SEDIS)) && (getSubSistema().equals(SigaaSubsistemas.SEDIS) )) return true;
		else return false;
	}
	
	
	
	
	/**
	 * M�todo que repassa pra view o resultado do Relat�rio de Turmas
	 * JSP:<br>
	 * /sigaa.war/graduacao/relatorios/turma/seleciona_ch_estagio.jsp
	 * /sigaa.war/graduacao/relatorios/turma/seleciona_turma.jsp
	 * /sigaa.war/graduacao/relatorios/turma/seleciona_turmas_ofertadas.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaTurma() throws DAOException{

		if (isFiltroCentro() && isFiltroDepartamento()) {
			addMensagemErro("Selecione Departamento ou Centro");
			return null;
		}
		
		if (isFiltroReservaCurso() && curso.getId() == 0) {
			addMensagemErro("Selecione um curso v�lido.");
			return null;
		}
		
		carregarTituloRelatorio();
		Unidade unidade;
		RelatorioTurmaSqlDao dao = getDAO(RelatorioTurmaSqlDao.class);
		UnidadeDao uDao = null;
		GenericDAO situacaoTurmaDao = null;

		try {
			if(filtros.get(AbstractRelatorioSqlDao.DEPARTAMENTO)) {
				if (departamento.getId() != 0) {
					unidade = departamento;
				} else {
					addMensagemErro("Selecione um Departamento V�lido");
					return null;
				}
			} else if(filtros.get(AbstractRelatorioSqlDao.CENTRO)) {
				if (centro.getId() != 0){
					uDao = getDAO(UnidadeDao.class);
					centro = uDao.findByPrimaryKey(centro.getId(), Unidade.class);			
					unidade = centro;
				} else {
					addMensagemErro("Selecione um Centro V�lido");
					return null;
				}
			} else {
				unidade = new Unidade();
			}
	
			if(!filtros.get(AbstractRelatorioSqlDao.ANO_PERIODO)){
				ano=0;
				periodo=0;
			} 
			
			if (isFiltroSituacaoTurma()){
				situacaoTurmaDao = getDAO(GenericDAOImpl.class);
				if(situacaoTurma.getId()!=0 && situacaoTurma !=null)
					situacaoTurma = situacaoTurmaDao.findByPrimaryKey(situacaoTurma.getId(), SituacaoTurma.class);
			}
			
			listaTurma = dao.findListaTurma(ano, periodo,unidade,curso,situacaoTurma,filtros);
		} finally {
			dao.close();
			if(situacaoTurmaDao != null)
				situacaoTurmaDao.close();
			if(uDao != null)
				uDao.close();
		}
		
		return forward(CONTEXTO+JSP_RELATORIO_TURMA);

	}

	/**
	 * M�todo que repassa pra view o resultado do Relat�rio Turmas por quantidade de docentes <br/>
	 * JSP: N�o invocado por JSP
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaTurmasDocentes() throws DAOException{

		carregarTituloRelatorio();
		
		if (ano <= 0 || periodo <= 0) {
			addMensagemErro("Informe Ano e Per�odo v�lidos.");
			return null;
		}
		
		if (isEmpty(ano) || isEmpty(periodo)) {
			addMensagemErro("Ano e per�odo s�o campos obrigat�rios.");
			return null;
		}
		
		RelatorioTurmaSqlDao dao = getDAO(RelatorioTurmaSqlDao.class);
		
		try {
			listaTurma = dao.findListaTurmasDocentes(centro, ano, periodo);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+JSP_RELATORIO_DOCENTE);
	}


	/**
	 * M�todo que repassa pra view o resultado da Rela��o por departamento das disciplinas de est�gio com a
	 * carga hor�ria e o n�mero de professores envolvidos.<br/>
	 * JSP:
	 * /sigaa.war/graduacao/relatorios/turma/seleciona_ch_estagio.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaTurmasChEstagio() throws DAOException{
		carregarTituloRelatorio();
		
		RelatorioTurmaSqlDao dao = getDAO(RelatorioTurmaSqlDao.class);
		
		try {
			listaTurma = dao.findListaTurmasChEstagio(departamento, ano, periodo);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+JSP_RELATORIO_CH_ESTAGIO);

	}
	
	/**
	 * Inicia o relat�rio de oferta de curso.<br/>
	 * JSP:
	 * /sigaa.war/ead/menu.jsp
	 * /sigaa.war/graduacao/menu_coordenador.jsp
	 * /sigaa.war/graduacao/menus/coordenacao.jsp
	 * /sigaa.war/graduacao/menus/relatorios_dae.jsp
	 * @return
	 * @throws Exception
	 */
	public String iniciarRelatorioListaTurmasOfertadasCurso() throws Exception{
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		return forward(CONTEXTO+JSP_SELECIONA_TURMAS_OFERTADAS);
	}

	/**
	 * M�todo que repassa pra view o resultado do Relat�rio de Oferta de Curso.<br/>
	 * JSP:
	 * /sigaa.war/graduacao/relatorios/turma/seleciona_turmas_ofertadas.jsp
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioListaTurmasOfertadasCurso() throws DAOException{
		if(getAcessoMenu().isCoordenadorCursoGrad() || getAcessoMenu().isSecretarioGraduacao())
			curso = getCursoAtualCoordenacao();

		if(curso == null || curso.getId() == 0){
			addMensagemErro("Selecione um curso.");
			return null;
		}
		if(ano == null || periodo == null){
			addMensagemErro("Informe o ano-per�odo.");
			return null;
		}

		RelatorioTurmaSqlDao dao = getDAO(RelatorioTurmaSqlDao.class);
		
		try {
			curso = dao.findByPrimaryKey(curso.getId(), Curso.class);
			turmas = dao.findListaTurmasOfertadasCurso(curso, ano, periodo);
		} finally {
			dao.close();
		}
		
		return forward(CONTEXTO+JSP_RELATORIO_TURMAS_OFERTADAS);
	}

	/**
	 * Inicia o relat�rio de ocupa��o de vagas por turma.
	 * Chamado pelas JSPs:
	 * /sigaa.war/graduacao/menus/relatorios_dae.jsp
	 * /sigaa.war/graduacao/departamento.jsp
	 * /sigaa.war/graduacao/menu_coordenador.jsp
	 * /sigaa.war/portais/docente/menu_docente.jsp
	 * /sigaa.war/stricto/menus/relatorios.jsp
	 * /sigaa.war/stricto/menu_coordenador.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciarRelatorioOcupacaoVagas() throws SegurancaException, DAOException{
		checkRole( SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG,
				SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO,
				SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS );

		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		nivel = NivelEnsino.GRADUACAO;


		if( isUserInRole( SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE ) ){
			departamento = new Unidade();
		}else if( isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO) ){
			departamento = getUsuarioLogado().getVinculoAtivo().getUnidade();
//			return gerarRelatorioOcupacaoVagas();
		}else if ( isUserInRole( SigaaPapeis.PPG) ){
			nivel = NivelEnsino.STRICTO;
		}else if ( isUserInRole( SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS ) ){
			nivel = NivelEnsino.STRICTO;
			departamento = getProgramaStricto();
//			return gerarRelatorioOcupacaoVagas();
		}else if ( isUserInRole( SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO ) ){
			curso = getCursoAtualCoordenacao();
//			return gerarRelatorioOcupacaoVagas();
		}

		return forward( CONTEXTO + JSP_SELECIONA_OCUPACAO_VAGAS_TURMAS );
	}

	/**
	 * Exibe  o relat�rio de ocupa��o de vagas por turma.<br/>
	 * JSP: /sigaa.war/graduacao/relatorios/turma/seleciona_ocupacao_vagas.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioOcupacaoVagas() throws DAOException{
		
		if (periodo == null || periodo <= 0)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Per�odo");		
		
		if (ano == null || ano <= 0)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		
		if (hasErrors())
			return null;

		RelatorioTurmaDao dao = getDAO(RelatorioTurmaDao.class);
		departamento = dao.refresh(departamento);
		try {
			
			Collection<Curso> cursos = null;
			if (getAcessoMenu().isCoordenacaoProbasica() ||  isUserInRole(SigaaPapeis.COORDENADOR_CURSO) && getCursoAtualCoordenacao() != null && getCursoAtualCoordenacao().isProbasica()) {
				CursoDao cdao = getDAO(CursoDao.class);
				cursos = cdao.findByConvenioAcademico(ConvenioAcademico.PROBASICA, NivelEnsino.GRADUACAO);
			}

			Curso curso = null;
			if (  isPortalCoordenadorGraduacao() ) {
				curso = getCursoAtualCoordenacao();
				departamento = null;
			}
			
			if ( isPortalCoordenadorStricto()) {
				departamento = getProgramaStricto();
				curso = null;
			}
			
			turmas = dao.findOcupacaoTurmas(nivel, departamento,(situacaoTurma.getId() > 0 ? new Integer[]{ situacaoTurma.getId() } : null),
					ano, periodo, cursos, new ModalidadeEducacao(ModalidadeEducacao.PRESENCIAL), curso);
			
			situacaoTurma = dao.refresh(situacaoTurma);			
			
			if (isEmpty(turmas)){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}				
		} catch (Exception e) {
			turmas = new ArrayList<Turma>();
			return tratamentoErroPadrao(e);
		}

		return forward( CONTEXTO + JSP_REL_OCUPACAO_VAGAS_TURMAS );
	}

	/**
	 * JSP: N�o invocado por JSP
	 * 
	 * @return the listaDiscente
	 */
	public int getNumeroRegistosEncontrados() {
		if(listaTurma!=null)
			return listaTurma.size();
		else
			return 0;
	}

	/** Retorna a lista contendo alguns dos dados das turmas encontradas.
	 * @return Lista contendo alguns dos dados das turmas encontradas.
	 */
	public List<Map<String, Object>> getListaTurma() {
		return listaTurma;
	}

	/** Seta a lista contendo alguns dos dados das turmas encontradas.
	 * @param listaTurma Lista contendo alguns dos dados das turmas encontradas.
	 */
	public void setListaTurma(List<Map<String, Object>> listaTurma) {
		this.listaTurma = listaTurma;
	}

	/** 
	 * Retorna a lista de turmas encontradas.
	 * @return Lista turmas encontradas.
	 */
	public Collection<Turma> getTurmas() {
		return turmas;
	}

	/** Seta a lista de turmas encontradas.
	 * @param turmas  Lista turmas encontradas.
	 */
	public void setTurmas(Collection<Turma> turmas) {
		this.turmas = turmas;
	}

	/** Indica se a turma � de EAD.
	 * @param ead
	 */
	public void setEad(boolean ead) {
		this.ead = ead;
	}

	/** Seta se a turma � de EAD.
	 * @return
	 */
	public boolean isEad() {
		return ead;
	}

	/**
	 * Retorna a coordena��o do curso.<br/>
	 * 
	 * JSP: N�o invocado por nenhum jsp.
	 */
	@Override
	public Curso getCursoAtualCoordenacao() {
		if (ead) {
			OperacoesCoordenadorGeralEadMBean bean = (OperacoesCoordenadorGeralEadMBean) getMBean("opCoordenadorGeralEad");
			return bean.getCurso();
		} else {
			return super.getCursoAtualCoordenacao();
		}
	}
	
	/**
	 * Serve pra direcionar o usu�rio para a tela situa��o turma. 
	 * 
	 * JSP: sigaa.war/graduacao/relatorios/turma/relatorio_dae.jsp
	 * @return 
	 */
	public String iniciarSituacaoTurma(){
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		return forward(CONTEXTO + JSP_SELECIONA_SITUACAO_TURMA);
	}

	/**
	 * Relat�rio de situa��o da turma em fun��o de um ano e um per�odo. 
	 *   
	 * JSP: sigaa.war/graduacao/relatorios/turma/rel_situacao_turma.jsp
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String situacaoTurma() throws DAOException, ArqException {
		RelatorioTurmaSqlDao dao = getDAO(RelatorioTurmaSqlDao.class);
		listaTurma = dao.relatorioSituacaoTurma(ano, periodo);
		if (listaTurma.size() > 0) {
			return forward(JSP_SITUACAO_TURMA);	
		}
			addMensagemInformation("N�o foi encontrado nenhum registro com o par�metro informado.");	
		return null;
	}

	/**
	 * Direciona o usu�rio para a tela situa��o turma.
	 *  
	 * JSP: sigaa.war/graduacao/relatorios/turma/relatorios_dae.jsp
	 * @return 
	 */
	public String iniciarVisualizacaoSolicitacaoDisciplinaFerias(){
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodoFeriasVigente();
		return forward(CONTEXTO + JSP_SELECIONA_SOLIC_TURMA_FERIAS);
	}

	/**
	 * Lista todos os Discentes que solicitaram disciplinas de f�rias, bem como o status das mesmas. 
	 * 
	 * JSP: sigaa.war/graduacao/relatorios/turma/seleciona_solicitacao_turma_ferias.jsp
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String solicitacaoDisciplinaFerias() throws DAOException, ArqException {
		if (ano == null || periodo == null) {
			addMensagemErro("Ano-Per�odo: Campo Obrigat�rio n�o informado.");
			return iniciarVisualizacaoSolicitacaoDisciplinaFerias();
		}
		if ((periodo != 3) && (periodo != 4)) {
			addMensagemErro("Informe um Per�odo de F�rias V�lido.");
			return iniciarVisualizacaoSolicitacaoDisciplinaFerias();
		}
		RelatorioTurmaSqlDao dao = getDAO(RelatorioTurmaSqlDao.class);
		listaTurma = dao.relatorioSolicitacaoDisciplinaFerias(ano, periodo);
		if (listaTurma.size() > 0) {
			return forward(JSP_ALUNOS_SOLIC_TURMA_FERIAS);	
		}
		  addMensagemInformation("N�o foi encontrado nenhum registro com o par�metro informado.");	
		return null;
	}

	/**
	 * Serve pra direcionar o usu�rio para a tela situa��o turma.<br/>
	 *  
	 * JSP: sigaa.war/graduacao/relatorios/turma/relatorios_dae.jsp
	 * @return 
	 */
	public String iniciarRelatorioTurmaNaoConsolidada(){
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		return forward(CONTEXTO + JSP_SELECIONA_TURMA_NAO_CONSOLIDADA);
	}
	
	/**
	 * Redireciona o usu�rio para a tela de Turmas Abertas sem Solicita��o.
	 * JSP: sigaa.war/graduacao/relatorios/menu.jsp
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioTurmaAbertaSemSolicitacao() throws SegurancaException{
		checkListRole();
		return forward(CONTEXTO + JSP_SELECIONA_TURMA_ABERTA_SEM_SOLICITACAO);
	}

	/**
	 * Lista todos os Discentes que solicitaram disciplinas de f�rias, bem como o status das mesmas.<br/>
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/relatorios/turma/seleciona_solicitacao_turma_ferias.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String relatorioTurmaNaoConsolidada() throws DAOException, ArqException {
		if (ano == null || periodo == null) {
			addMensagemErro("Ano-Per�odo: Campo Obrigat�rio n�o informado.");
			return iniciarRelatorioTurmaNaoConsolidada();
		}
		
		RelatorioTurmaSqlDao dao = getDAO(RelatorioTurmaSqlDao.class);
		listaTurma = dao.relatorioTurmaNaoConsolidada(ano, periodo, ead);
		
		if (listaTurma.size() > 0) {
			return forward(JSP_REL_TURMA_NAO_CONSOLIDADA);	
		}
		  addMensagemInformation("N�o foi encontrado nenhum registro com o par�metro informado.");	
		return null;
	}
	
	/**
	 * Relat�rio de Turmas abertas sem solicita��o.<br/>
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/relatorios/seleciona_turmas_abertas_sem_solicitacao</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String relatorioTurmaAbertaSemSolicitacao() throws DAOException{
		RelatorioTurmaSqlDao dao = getDAO(RelatorioTurmaSqlDao.class);
		
		if (ano == null || periodo == null){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano-Per�odo");
		}
		if(!isFiltroSituacaoTurma() && situacaoTurma.getId() != 0){		
			addMensagemErro("Situa��o da Turma: preencher filtro selecionado.");
		}
			
					
		if(!hasErrors()){
			listaTurma = dao.relatorioTurmaAbertaSemSolicitacao(ano, periodo, situacaoTurma.getId(), isFiltroContabilizarEnsinoDistancia());
		if (listaTurma.size() > 0) {
			return forward(JSP_REL_TURMA_ABERTA_SEM_SOLICITACAO);
		}
		else
			addMensagemErro("N�o foi encontrado nenhum registro com os par�metros informados.");
			return null;
		}
		
		return null;
			
	}

	
	/**
	 * Checa se o usu�rio tem permiss�o para realizar a opera��o e em seguida o redireciona para a tela para o preenchimento 
	 * do ano, do per�odo e de qual forma o usu�rio gostaria que o relat�rio fosse exibido: se com os coordenadores que 
	 * solicitaram ou n�o turma.<br/>
	 * 
	 * JSP: sigaa.war/graduacao/relatorios/menu.jsp
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioSolicitacaoTurmaCurso() throws SegurancaException{
		checkListRole();
		return forward(CONTEXTO + JSP_SELECIONA_TURMA_CURSO);
	}
	
	/**
	 * Verifica se o ano e per�odo foram realmente preenchidos e realiza a busca, caso o resultado n�o retorne nenhum
	 * registro � exibido ao usu�rio e n�o h� a gera��o do relat�rio.<br/>
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/relatorios/turma/seleciona_solicitacao_turma_ferias.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String relatorioCursoTurma() throws DAOException, ArqException {
		if (ano == null || periodo == null) {
			addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Ano-Per�odo");
			return iniciarRelatorioSolicitacaoTurmaCurso();
		}
		RelatorioTurmaSqlDao dao = getDAO(RelatorioTurmaSqlDao.class);
		listaTurma = dao.relatorioCursoTurma(ano, periodo, filtroMatriculados);
		
		if (listaTurma.size() > 0) {
			return forward(CONTEXTO + JSP_REL_TURMA_CURSO);	
		}
		  addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);	
		return null;
	}	
	
	/**
	 * Checa se o usu�rio tem permiss�o para realizar a opera��o e em seguida o redireciona 
	 * para a tela para o preenchimento do ano, do per�odo e de qual forma o usu�rio gostaria que 
	 * o relat�rio quantitativo de turmas e disciplinas por departamento.<br/>
	 * 
	 * JSP: sigaa.war/graduacao/relatorios/menu.jsp
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioQuantTurmaDisciplinaPorDepto() throws SegurancaException{
		checkListRole();
		return forward(CONTEXTO + JSP_SELECIONA_QUANT_TURMA_DISC_DEPARTAMENTO);
	}
	
	/**
	 * Verifica se o ano e per�odo foram realmente preenchidos e realiza a busca, caso o resultado n�o retorne nenhum
	 * registro � exibido ao usu�rio e n�o h� a gera��o do relat�rio.<br/>
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/relatorios/turma/seleciona_quant_turma_disciplina_departamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String relatorioQuantTurmaDisciplinaPorDepto() throws DAOException, ArqException {
		if (ano == null || periodo == null) {
			addMensagem(MensagensArquitetura.CONTEUDO_INVALIDO, "Ano-Per�odo");
			return iniciarRelatorioQuantTurmaDisciplinaPorDepto();
		}
		
		RelatorioTurmaSqlDao dao = getDAO(RelatorioTurmaSqlDao.class);
		listaTurma = dao.relatorioQuantTurmaDisciplinaPorDepto(ano, periodo, filtroMatriculados);
		
		if (listaTurma.size() > 0) {
			return forward(CONTEXTO + JSP_REL_QUANT_TURMA_DISC_DEPARTAMENTO);	
		}
		addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);	
		return null;
	}	
		
	/**
	 * Classe para controlar os campos que poder�o ser alterados pelo usu�rio
	 * 
	 * @author Henrique Andr�
	 *
	 */
	public class Campos implements Serializable {
		
		/** Indica se o usu�rio pode alterar o ano/per�odo. */
		private boolean anoPeriodo;
		/** Indica se o usu�rio pode alterar o centro.*/
		private boolean centro;
		/** Indica se o usu�rio pode alterar o departamento. */
		private boolean departamento;
		/** Indica se o usu�rio pode alterar a situa��o da turma. */
		private boolean situacaoTurma;
		/** Indica se o usu�rio pode alterar o curso. */
		private boolean curso;
		/** Indica se o usu�rio pode alterar se � curso EAD.*/
		private boolean ead;
		
		/** Construtor padr�o. */
		public Campos() {
			this.anoPeriodo = true;
			this.centro = true;
			this.departamento = true;
			this.situacaoTurma = true;
			this.curso = true;
			this.ead = true;
		}
		
		/** Indica se o usu�rio pode alterar o ano/per�odo.
		 * @return
		 */
		public boolean isAnoPeriodo() {
			return anoPeriodo;
		}
		/** Seta se o usu�rio pode alterar o ano/per�odo.
		 * @param anoPeriodo
		 */
		public void setAnoPeriodo(boolean anoPeriodo) {
			this.anoPeriodo = anoPeriodo;
		}
		/** Indica se o usu�rio pode alterar o centro.
		 * @return
		 */
		public boolean isCentro() {
			return centro;
		}
		/** Seta se o usu�rio pode alterar o centro.
		 * @param centro
		 */
		public void setCentro(boolean centro) {
			this.centro = centro;
		}
		/** Indica se o usu�rio pode alterar o departamento.
		 * @return
		 */
		public boolean isDepartamento() {
			return departamento;
		}
		/** Seta se o usu�rio pode alterar o departamento.
		 * @param departamento
		 */
		public void setDepartamento(boolean departamento) {
			this.departamento = departamento;
		}
		/** Indica se o usu�rio pode alterar a situa��o da turma.
		 * @return
		 */
		public boolean isSituacaoTurma() {
			return situacaoTurma;
		}
		/** Seta se o usu�rio pode alterar a situa��o da turma.
		 * @param situacaoTurma
		 */
		public void setSituacaoTurma(boolean situacaoTurma) {
			this.situacaoTurma = situacaoTurma;
		}
		/** Indica se o usu�rio pode alterar o curso.
		 * @return
		 */
		public boolean isCurso() {
			return curso;
		}
		/** Seta se o usu�rio pode alterar o curso.
		 * @param curso
		 */
		public void setCurso(boolean curso) {
			this.curso = curso;
		}
		
		/**
		 * JSP: N�o invocado por JSP
		 */
		public void habilitarCoordenacao() {
			this.anoPeriodo = false;
			this.situacaoTurma = false;
		}
		
		/** Indica se o usu�rio pode alterar se � curso EAD.
		 * @return
		 */
		public boolean isEad() {
			return ead;
		}

		/** Seta se o usu�rio pode alterar se � curso EAD.
		 * @param ead
		 */
		public void setEad(boolean ead) {
			this.ead = ead;
		}

		/** Configura quais campos podem ser alterados pelo coordenador de departamento. 
		 * JSP: N�o invocado por JSP
		 * */
		public void habilitarDepartamento() {
			this.anoPeriodo = false;
			this.situacaoTurma = false;
		}
		
		/** Configura quais campos podem ser alterados pela PROGRAD. 
		 * JSP: N�o invocado por JSP
		 * */
		public void habilitarPROGRAD() {
			this.anoPeriodo = false;
			this.curso = false;
			this.departamento = false;
			this.situacaoTurma = false;
			this.centro = false;
		}

		/** Configura quais campos podem ser alterados pela PPG. 
		 * JSP: N�o invocado por JSP
		 * */
		public void habilitarPPG() {
			this.anoPeriodo = false;
			this.curso = false;
			this.departamento = false;
			this.situacaoTurma = false;
			this.centro = false;
		}		
		
		/** Configura quais campos podem ser alterados pelo chefe de departamento. 
		 * JSP: N�o invocado por JSP
		 * */
		public void habilitarChefeDepartamento() {
			this.anoPeriodo = false;
			this.situacaoTurma = false;
		}		
		
		/** Configura quais campos podem ser alterados pelo chefe de centro. 
		 * JSP: N�o invocado por JSP
		 * */
		public void habilitarChefeCentro() {
			this.anoPeriodo = false;
			this.situacaoTurma = false;
			this.departamento = false;
		}
		
		
	}
	
	/** Retorna os campos que poder�o ser alterados pelo usu�rio.
	 * @return Campos que poder�o ser alterados pelo usu�rio.
	 */
	public Campos getCampos() {
		return campos;
	}

	/** Seta os campos que poder�o ser alterados pelo usu�rio.
	 * @param campos Campos que poder�o ser alterados pelo usu�rio.
	 */
	public void setCampos(Campos campos) {
		this.campos = campos;
	}	
	
	public boolean isFiltroAnoTurma() {
		return filtroAnoTurma;
	}

	public void setFiltroAnoTurma(boolean filtroAnoTurma) {
		this.filtroAnoTurma = filtroAnoTurma;
	}

	public boolean isFiltroPeriodoTurma() {
		return filtroPeriodoTurma;
	}

	public void setFiltroPeriodoTurma(boolean filtroPeriodoTurma) {
		this.filtroPeriodoTurma = filtroPeriodoTurma;
	}
	


	public boolean isFiltroMatriculados() {
		return filtroMatriculados;
	}

	public void setFiltroMatriculados(boolean filtroMatriculados) {
		this.filtroMatriculados = filtroMatriculados;
	}

	public SituacaoTurma getSituacaoTurma() {
		return situacaoTurma;
	}

	public void setSituacaoTurma(SituacaoTurma situacaoTurma) {
		this.situacaoTurma = situacaoTurma;
	}

	public boolean isFiltroSituacaoTurma() {
		return filtroSituacaoTurma;
	}

	public void setFiltroSituacaoTurma(boolean filtroSituacaoTurma) {
		this.filtroSituacaoTurma = filtroSituacaoTurma;
	}

	public boolean isFiltroContabilizarEnsinoDistancia() {
		return filtroContabilizarEnsinoDistancia;
	}

	public void setFiltroContabilizarEnsinoDistancia(
			boolean filtroContabilizarEnsinoDistancia) {
		this.filtroContabilizarEnsinoDistancia = filtroContabilizarEnsinoDistancia;
	}
	
}
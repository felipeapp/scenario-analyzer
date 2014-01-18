/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 29/01/2008
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularProgramaDao;
import br.ufrn.sigaa.arq.dao.ensino.DisciplinaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricularPrograma;

/**
 * MBean responsável por associar um programa a um componente curricular.
 * @author Andre M Dantas
 */
@Component("programaComponente") @Scope("session")
public class ProgramaComponenteCurricularMBean extends SigaaAbstractController<ComponenteCurricularPrograma> {
	
	/** Parametro utilizado para buscar por programa. */
	private String paramBusca = null;
	
	/** Auxiliar para passar a identificação do componente para o mbean sem ser por request. */
	int idComponente;

	/** Caminho para o relatório com as informações do programa do componente cuja modalidade de ensino é presencial */
	public static final String JSP_PROGRAMA_RELATORIO_PRESENCIAL = "/graduacao/componente_programa/programa_relatorio.jsp";
	
	/** Caminho para o relatório com as informações do programa do componente cuja modalidade de ensino é a distância */
	public static final String JSP_PROGRAMA_RELATORIO_EAD = "/graduacao/componente_programa/programa_relatorio_ead.jsp";

	/** Caminho para o relatório com as informações do programa do componente cuja modalidade de ensino é a distância */
	public static final String JSP_PROGRAMA_HISTORICO = "/graduacao/componente_programa/programa_historico.jsp";
	
	/** Lista de componentes encontrados na busca. */
	private List<ComponenteCurricular> componentesEncontrados;
	
	/** Lista de componentes encontrados na busca. */
	private List<ComponenteCurricularPrograma> programasEncontrados;

	/** Indica se o usuário pode buscar por unidade. */
	public boolean isPermiteBuscarUnidade() {
		return isUserInRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.GESTOR_LATO);
	}
	
	/** Construtor padrão. */
	public ProgramaComponenteCurricularMBean() {
		initObj();
	}

	/** Inicializa os atributos do controller. */
	private void initObj() {
		obj = new ComponenteCurricularPrograma();
		obj.setComponenteCurricular(new ComponenteCurricular());
		
	}

	/** Retorna uma coleção de semestres.
	 * <br />
	 * Chamado por: 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/componente_programa/programa.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getSemestres() {
		Collection<SelectItem> combo = new ArrayList<SelectItem>(0);

		CalendarioAcademico vig = getCalendarioVigente();
		for (int i = 4 ; i >= 1; i--) {
			CalendarioAcademico cal = vig.incrCalendario(i * -1);
			combo.add(new SelectItem(cal.getAno() + "" + cal.getPeriodo(), cal.getAnoPeriodo()));
			if (cal.getAno() == vig.getAno()) {
				cal.setPeriodo(cal.getPeriodo() + 2);
				combo.add(new SelectItem(cal.getAno() + "" + cal.getPeriodo(), cal.getAnoPeriodo()));
			}
		}
		CalendarioAcademico cal = getCalendarioVigente().incrCalendario(0);
		combo.add(new SelectItem(cal.getAno() + "" + cal.getPeriodo(), cal.getAnoPeriodo()));
		cal.setPeriodo(cal.getPeriodo() + 2);
		combo.add(new SelectItem(cal.getAno() + "" + cal.getPeriodo(), cal.getAnoPeriodo()));
		cal = getCalendarioVigente().incrCalendario(1);
		combo.add(new SelectItem(cal.getAno() + "" + cal.getPeriodo(), cal.getAnoPeriodo()));
		return combo;
	}
	
	/**
	 * Chama o gerarGelatorioPrograma sem passar a id do componente por request.<br/><br/>
	 * 
	 * Método não invocado por JSPs. É chamado pela classe MenuTurmaMBean.
	 * 
	 * @param idPrograma
	 * @return
	 * @throws DAOException 
	 */
	public String gerarRelatorioPrograma (int idComponente) throws DAOException {
		this.idComponente = idComponente;
		return gerarRelatorioPrograma ();
	}

	/**
	 * Realizar a busca dos dados do programa do componente curricular, 
	 * tratando os casos de EAD (Ensino a distância) ou ensino presencial.
	 * <br />
     * Chamado por:
     * <ul>
     * <li>/SIGAA/app/sigaa.ear/sigaa.war/ava/menu.jsp
     * <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/componente_curricular/busca_geral.jsp</li>
     * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/componente/lista.jsp</li>
     * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/componente/view_painel.jsp</li>
     * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/componentes/busca_componentes.jsp</li>
     * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/curso/relatorio_curriculo.jsp</li>
     * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/curso/resumo_curriculo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioPrograma() throws DAOException {
		obj = new ComponenteCurricularPrograma();
		ComponenteCurricularProgramaDao dao = getDAO(ComponenteCurricularProgramaDao.class);

		// Se não for enviado por request, utiliza o identificador enviado por parâmetro.
		Integer auxIdComponente = getParameterInt("idComponente");
		if (auxIdComponente == null)
			auxIdComponente = idComponente;
		
		ComponenteCurricular cc = dao.findAtualByComponenteItemPrograma(auxIdComponente); 
		obj.setComponenteCurricular(cc);
		
		if (cc == null) {
			addMensagemErro("Ocorreu um problema ao buscar as informações deste componente. Por favor, tente novamente buscá-lo para efetuar a consulta de seu programa");
			return null;
		}

		// No caso de ensino a distância o componente tem os dados do programa cadastrados diretamente nele
		if ( isEmpty(cc.getItemPrograma()) ) {
			obj = dao.findAtualByComponente(cc.getId());
			
			if (obj == null) {
				addMensagemErro("O componente curricular " + cc.getCodigoNome() + " não possui um programa cadastrado.");
				return null;
			}
		
			if (verificarPreechimento())
				return null;	
			
			return forward(JSP_PROGRAMA_RELATORIO_PRESENCIAL);
		} else {
			return forward(JSP_PROGRAMA_RELATORIO_EAD);
		} 
	}
	
	/**
	 * Realizar a busca dos dados do programa do componente curricular, 
	 * tratando os casos de EAD (Ensino a distância) ou ensino presencial.
	 * <br />
	 * Chamado por:
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/ava/menu.jsp
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/componente_curricular/busca_geral.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/componente/lista.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/componente/view_painel.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/componentes/busca_componentes.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/curso/relatorio_curriculo.jsp</li>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/curso/resumo_curriculo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioProgramaHistorico() throws DAOException {
		obj = new ComponenteCurricularPrograma();
		ComponenteCurricularProgramaDao dao = getDAO(ComponenteCurricularProgramaDao.class);
		
		// Se não for enviado por request, utiliza o identificador enviado por parâmetro.
		Integer auxIdComponente = getParameterInt("idComponente");
		Integer auxIdPrograma = getParameterInt("idPrograma");
		if (auxIdComponente == null)
			auxIdComponente = idComponente;
		
		ComponenteCurricular cc = dao.findAtualByComponenteItemPrograma(auxIdComponente); 
		ComponenteCurricularPrograma ccp = dao.findByPrimaryKey(auxIdPrograma, ComponenteCurricularPrograma.class);
		cc.setPrograma(ccp);
		obj.setComponenteCurricular(cc);
		
		// No caso de ensino a distância o componente tem os dados do programa cadastrados diretamente nele
		if ( isEmpty(cc.getItemPrograma()) ) {
			obj = ccp;
			
			if (obj == null) {
				addMensagemErro("O componente curricular " + cc.getCodigoNome() + " não possui um programa cadastrado.");
				return null;
			}
			
			if (verificarPreechimento())
				return null;	
			
			return forward(JSP_PROGRAMA_RELATORIO_PRESENCIAL);
		} else {
			return forward(JSP_PROGRAMA_RELATORIO_EAD);
		} 
	}

	/** Verifica o preenchimento dos dados.
	 * @return
	 */
	private boolean verificarPreechimento(){
		if ( isEmpty( obj.getAno() ) && isEmpty( obj.getPeriodo() ) 
				&& ( isEmpty( obj.getReferencias() ) || obj.getReferencias().length() == 0 )  
				&& ( isEmpty( obj.getConteudo()) || obj.getConteudo().length() == 0 )
				&& ( isEmpty( obj.getCompetenciasHabilidades() ) || obj.getCompetenciasHabilidades().length() == 0 ) ){
			addMensagemWarning("Não possível visualizar o relatório, pois os dados do programa não foram preenchidos.");
			return true;
		}else
			return false;
	}
	
	/**
	 * Inicia o caso de uso de cadastro de programa de componente curricular.
	 * <br />
     * Chamado por
     * <ul>
     * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/departamento.jsp</li>
     * <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/menu_coordenador.jsp</li>
     * <li>/SIGAA/app/sigaa.ear/sigaa.war/portais/docente/menu_docente.jsp</li>
     * </ul>
     * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException{
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO);
		initObj();
		componentesEncontrados = new ArrayList<ComponenteCurricular>();
		programasEncontrados = new ArrayList<ComponenteCurricularPrograma>();

		return telaBuscaComponentes();
	}

	/** Busca por componentes de acordo com o nível do usuário.
	 * <br />
	 * Chamado por:
	 * <ul> 
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/componente_programa/busca_componente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String buscarComponente() throws DAOException {
		componentesEncontrados = new ArrayList<ComponenteCurricular>();
		DisciplinaDao dao = getDAO(DisciplinaDao.class);
		Curso curso = new Curso();
		String tipoBusca = paramBusca = getParameter("paramBusca");
		ComponenteCurricular cc = obj.getComponenteCurricular();
		char nivelEnsino = NivelEnsino.GRADUACAO;
		int unidade = 0;
		
		if (isPortalCoordenadorGraduacao())
			unidade = getCursoAtualCoordenacao().getUnidadeCoordenacao().getId();
		else if (isUserInRole(SigaaPapeis.SECRETARIA_DEPARTAMENTO, SigaaPapeis.CHEFE_DEPARTAMENTO)) 
			unidade = getUsuarioLogado().getVinculoAtivo().getUnidade().getId();
		else if (isUserInRole(SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO) && getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO)) {
			nivelEnsino = NivelEnsino.LATO;
			curso = getCursoAtualCoordenacao();
			unidade = 0;
		}
		
		try {
			if ("Nome".equalsIgnoreCase(tipoBusca)) {
				if ("".equals(cc.getNome())) {
					addMensagemErro("Informe o o parâmetro de busca");
					return null;
				}
				tipoBusca = "";
				componentesEncontrados = (List<ComponenteCurricular>) dao.findByNomeCursoLato(cc.getNome(), unidade, curso.getId(), nivelEnsino, null, true, null);
			} else if ("Unidade".equalsIgnoreCase(tipoBusca)) {
				if (cc.getUnidade().getId() == 0) {
					addMensagemErro("Informe o parâmetro de busca");
					return null;
				}
				tipoBusca = "";
				componentesEncontrados = (List<ComponenteCurricular>) dao.findByNomeCursoLato("", cc.getUnidade().getId(), curso.getId(),
						nivelEnsino, null, true, null);
			} else if ("Codigo".equalsIgnoreCase(tipoBusca)) {
				if ("".equals(cc.getCodigo())) {
					addMensagemErro("Informe o parâmetro de busca");
					return null;
				}
				tipoBusca = "";
				ComponenteCurricular comp = dao.findByCodigo(cc.getCodigo(), unidade, curso.getId(), nivelEnsino, true);
				if (comp != null) {
					componentesEncontrados = new ArrayList<ComponenteCurricular>();
					componentesEncontrados.add(comp);
				}

			} else if ("Tipo".equalsIgnoreCase(tipoBusca)) {
				if (cc.getTipoComponente().getId() == 0) {
					addMensagemErro("Informe o parâmetro de busca");
					return null;
				}
				tipoBusca = "";
				componentesEncontrados = (List<ComponenteCurricular>) dao.findByTipo(cc.getTipoComponente()
						.getId(), unidade, curso.getId(), nivelEnsino, null, null, 0);
			} else {
				addMensagemErro("Informe o parâmetro de busca");
			}
		} catch (LimiteResultadosException e) {
			addMensagemErro(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	/** Gera histórico dos programas encontrados.
	 * <br />
     * Chamado por:
     * <ul>
     * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/componente_programa/busca_componente.jsp</li>
     * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String historicoPrograma() throws ArqException {
		
		initObj();
		ComponenteCurricularProgramaDao dao = getDAO(ComponenteCurricularProgramaDao.class);

		obj.setComponenteCurricular(getGenericDAO().findByPrimaryKey(getParameterInt("id"),	ComponenteCurricular.class));
		
		if( obj.getComponenteCurricular() != null && obj.getComponenteCurricular().getId() > 0 ){
			programasEncontrados = (List<ComponenteCurricularPrograma>) dao.findByComponente(obj.getComponenteCurricular());
		}
		
		return forward(JSP_PROGRAMA_HISTORICO);
	}

	/** Seleciona um componente para tratar.
	 * <br />
     * Chamado por:
     * <ul>
     * <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/componente_programa/busca_componente.jsp</li>
     * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String selecionaComponente() throws ArqException {
		
		initObj();
		Comando comando = SigaaListaComando.CADASTRAR_PROGRAMA_COMPONENTE;
		setConfirmButton("Cadastrar");
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_PROGRAMA_COMPONENTE.getId());
		
		if ("alteracao".equalsIgnoreCase(getParameter("operacao"))) {
			comando = SigaaListaComando.ALTERAR_PROGRAMA_COMPONENTE;
			setConfirmButton("Alterar");
			setOperacaoAtiva(SigaaListaComando.ALTERAR_PROGRAMA_COMPONENTE.getId());
		}

		obj.setComponenteCurricular(getGenericDAO().findByPrimaryKey(getParameterInt("id"),	ComponenteCurricular.class));
		
		if( obj.getComponenteCurricular().getPrograma() != null && obj.getComponenteCurricular().getPrograma().getId() > 0 ){
			obj = getGenericDAO().findByPrimaryKey(obj.getComponenteCurricular().getPrograma().getId(), ComponenteCurricularPrograma.class);
		}
		
		obj.setAno(getCalendarioVigente().getAno());
		obj.setPeriodo(getCalendarioVigente().getPeriodo());
		
		if (getConfirmButton().equalsIgnoreCase("alterar") && (obj == null || obj.getId() == 0)) {
			addMensagemErro("Esse componente ainda não possui um programa cadastrado.");
			return null;
		}
				
		prepareMovimento(comando);
		return forward(getFormPage());
	}

	/** Retorna o link para o formulário de busca de componentes.
	 * <br />
	 * Chamado por:
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/componente_programa/programa.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaBuscaComponentes() {
		return forward(getListPage());
	}

	/** Retorna o link para a página do programa.
	 * <br/>Método não invocado por JSP´s
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/graduacao/componente_programa/programa.jsp";
	}

	/** Retorna o link para a página de listagem.
	 * <br/>Método não invocado por JSP´s
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/graduacao/componente_programa/busca_componente.jsp";
	}

	/** Cadastra o programa do componente.
	 * Chamado por
	 * <ul> 
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/componente_programa/programa.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/portais/turma/programa.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws NegocioException, ArqException {
		
		erros = new ListaMensagens();
		erros.addAll(obj.validate().getMensagens());
		
		ComponenteCurricularProgramaDao dao = getDAO(ComponenteCurricularProgramaDao.class);
		
		// Buscar se existe programa cadastrado no ano-período informado
		ComponenteCurricularPrograma programaAnoPeriodo = dao.findAtualByComponente(obj.getComponenteCurricular().getId(), obj.getAno(), obj.getPeriodo());
		dao.detach(programaAnoPeriodo);
		
		if(programaAnoPeriodo != null && getConfirmButton().equalsIgnoreCase("Cadastrar") ){
			erros.addErro("Não é possível cadastrar o programa pois já existe um programa cadastrado para o componente curricular no ano-período informado.");
		} else if(programaAnoPeriodo == null && getConfirmButton().equalsIgnoreCase("Alterar") ){
			erros.addErro("Não é possível atualizar o programa pois não existe um programa cadastrado para o componente curricular no ano-período informado.");
		}
		
		if (hasErrors())
			return null;

		Comando comando = null;
		if( programaAnoPeriodo == null && getConfirmButton().equalsIgnoreCase("Cadastrar") ){
			
			if( !checkOperacaoAtiva(SigaaListaComando.CADASTRAR_PROGRAMA_COMPONENTE.getId()) )
				return null;
			comando = SigaaListaComando.CADASTRAR_PROGRAMA_COMPONENTE;
			obj.setId(0);
		}else if( programaAnoPeriodo != null && getConfirmButton().equalsIgnoreCase("Alterar") ){
			
			if( !checkOperacaoAtiva(SigaaListaComando.ALTERAR_PROGRAMA_COMPONENTE.getId()) )
				return null;			
			comando = SigaaListaComando.ALTERAR_PROGRAMA_COMPONENTE;
			obj.setId(programaAnoPeriodo.getId());
		} else {
			addMensagemErroPadrao();
			return null;
		}
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(comando);
		mov.setObjMovimentado(obj);

		try {
			executeWithoutClosingSession(mov, getCurrentRequest());

			if( getConfirmButton().equalsIgnoreCase("Cadastrar") )
				addMensagemInformation("Programa cadastrado com sucesso!");
			else if( getConfirmButton().equalsIgnoreCase("Alterar") )
				addMensagemInformation("Programa atualizado com sucesso!");
			
			removeOperacaoAtiva();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} catch (ArqException e) {
			throw e;
		} 
		resetBean();
		initObj();		
		return telaBuscaComponentes();
	}

	/** Retorna uma coleção de SelectItem de inteiros (quantidade de avaliações).
	 * <br />
	 * Chamado por:
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/componente_programa/programa.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/portais/turma/programa.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public ArrayList<SelectItem> getQtdAvaliacoes() throws DAOException {
		ArrayList<SelectItem> itens = new ArrayList<SelectItem>();
		if (obj.getComponenteCurricular().getUnidade().getId() == 0
				|| obj.getComponenteCurricular().getUnidade().getGestoraAcademica() == null)
			return itens;
		ParametrosGestoraAcademica params = getParametrosAcademicos();
		int max = 1;
		if (params != null)
			max = params.getQtdAvaliacoes();
		for (int i = max; i > 0; i--) {
			itens.add(new SelectItem(i, i + ""));
		}
		return itens;
	}

	/** Retorna a lista de componentes encontrados na busca. 
	 * @return
	 */
	public List<ComponenteCurricular> getComponentesEncontrados() {
		return componentesEncontrados;
	}

	public List<ComponenteCurricularPrograma> getProgramasEncontrados() {
		return programasEncontrados;
	}

	public void setProgramasEncontrados(
			List<ComponenteCurricularPrograma> programasEncontrados) {
		this.programasEncontrados = programasEncontrados;
	}

	/** Seta a lista de componentes encontrados na busca.
	 * @param componentesEncontrados
	 */
	public void setComponentesEncontrados(List<ComponenteCurricular> componentesEncontrados) {
		this.componentesEncontrados = componentesEncontrados;
	}

	public String getParamBusca() {
		return paramBusca;
	}

	public void setParamBusca(String paramBusca) {
		this.paramBusca = paramBusca;
	}

}

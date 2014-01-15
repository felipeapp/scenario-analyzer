package br.ufrn.sigaa.ensino.tecnico.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.dominio.UnidadeTempo;
import br.ufrn.sigaa.ensino.tecnico.dao.EstruturaCurricularTecDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.DisciplinaComplementar;
import br.ufrn.sigaa.ensino.tecnico.dominio.EstruturaCurricularTecnica;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;
import br.ufrn.sigaa.ensino.tecnico.dominio.ModuloCurricular;
import br.ufrn.sigaa.ensino.tecnico.dominio.ModuloDisciplina;
import br.ufrn.sigaa.ensino.tecnico.negocio.EstruturaCurricularTecnicaMov;

/**
 * Managed Bean respons�vel pelas opera��es b�sicas 
 * da Estrutura Curricular T�cnica.
 *  
 * @author guerethes
 */
@Component @Scope("request")
public class EstruturaCurricularTecnicoMBean extends SigaaAbstractController<EstruturaCurricularTecnica> {

	/** Constante utilizada na view que auxilia na consulta*/ 
	private static boolean filtroCurso; 
	
	/** Constante de visualiza��o de adi��o dos m�dulos  */
	private static final String JSP_MODULO = "/modulo.jsp";
	/** Constante de visualiza��o de adi��o das disciplinas   */
	private static final String JSP_DISCIPLINAS = "/disciplinas.jsp";
	/** Constante de visualiza��o do resumo  */
	private static final String JSP_RESUMO = "/resumo.jsp";
	
	/** Armazena o Modulo Curricular da Estrutura */
	private ModuloCurricular moduloCurricular = new ModuloCurricular();
	/** Armazena a disciplina Complementar da Estrutura Curricular T�cnico */
	private DisciplinaComplementar disciplinaComplementar = new DisciplinaComplementar();
	/** Armazena uma cole��o das Estruturas Curriculares Tecnica */
	private Collection<EstruturaCurricularTecnica> estruturas = new ArrayList<EstruturaCurricularTecnica>();
	
	public EstruturaCurricularTecnicoMBean() {
		init();
	}
	
	/**
	 * Inicializa os atributos b�sicos a serem utilizados
	 */
	private void init(){
		obj = new EstruturaCurricularTecnica();
		moduloCurricular.setModulo(new Modulo());
		disciplinaComplementar = new DisciplinaComplementar();
		disciplinaComplementar.setDisciplina(new ComponenteCurricular());
		disciplinaComplementar.setEstruturaCurricularTecnica(new EstruturaCurricularTecnica());
	}
	
	@Override
	public String getDirBase() {
		return "/ensino/tecnico/estrutura";
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
	}
	
	/**
	 * Carrega as unidades de tempo cadastradas.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/estrutura/form.jsp</li>
	 * </ul>
     *
     * estruturaCurricularTecnicoMBean.allUnidadeTempo
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllUnidadeTempo() throws DAOException{
		return toSelectItems(getDAO(CursoDao.class).findAll(UnidadeTempo.class), "id", "descricao");
	}

	/**
	 * Carrega todos as Estruturar Curriculares cadastradas 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/turmaEntrada/form.jsp</li>
	 * </ul>
     *
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllCursoEstruturaCurricular() throws DAOException{
		return toSelectItems(getDAO(EstruturaCurricularTecDao.class).findAll(getUsuarioLogado().getVinculoAtivo().getUnidade().getId(),
				getNivelEnsino(), null, null), "cursoTecnico.id", "descricaoEstruturaCursoTecnico");
	}
	
	
	
	/**
	 * Retorna todos os cursos de T�CNICO em formato para combo
	 * <br/><br/>
	 * M�todo Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino/tecnico/estrutura/lista.jsp</li>
	 * </ul>
	 * @return 
	 */
	public Collection<SelectItem> getAllCursoTecnicoCombo() throws DAOException{
		CursoDao dao = getDAO(CursoDao.class);
		return toSelectItems(dao.findByNivel(NivelEnsino.TECNICO, true, null, null), "id", "descricao");
	}
	
	/**
	 * Carrega o Curriculo a partir de um curso T�cnico.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>M�todo n�o Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	public void changeCurriculoCurso(ValueChangeEvent e) throws DAOException {
		alterarCurriculoCurso((Integer) e.getNewValue());
	}

	/**
	 * Altera o Estrutura Curricular dependendo do curso T�cnico informado.
	 * 
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>M�todo n�o Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> alterarCurriculoCurso(int id) throws DAOException {
		return  toSelectItems(getDAO(EstruturaCurricularTecDao.class).findByCursoTecnico(id), "id", "descricaoResumida");
	}
	
	/**
	 * Carrega a Unidade de Tempo selecionada.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/estrutura/form.jsp</li>
	 * </ul>
     *
	 * @param e
	 * @throws DAOException
	 */
	public void changeUnidadeTempo(ValueChangeEvent e) throws DAOException {
		obj.setUnidadeTempo(getGenericDAO().findByPrimaryKey((Integer) e.getNewValue(), UnidadeTempo.class));
	}
	
	/**
	 * Submete os dados gerais e direciona para a tela de adi��o de m�dulo
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/estrutura/form.jsp</li>
	 * </ul>
     *
	 * @return
	 * @throws DAOException
	 */
	public String submeterDadosGerais() throws DAOException{
		erros.addAll(obj.validate());
		if (hasErrors()) 
			return null;
		obj.setUnidadeTempo(getGenericDAO().refresh(obj.getUnidadeTempo()));
		obj.setCursoTecnico(getGenericDAO().findByPrimaryKey(obj.getCursoTecnico().getId(), CursoTecnico.class));
		obj.setTurno(getGenericDAO().refresh(obj.getTurno()));
		
		return forward(getDirBase() + JSP_MODULO);
	}
	
	/**
	 * Realizar a adi��o de um novo m�dulo para uma Estrutura Curricular.
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/estrutura/modulo.jsp</li>
	 * </ul>
     *
	 * @throws DAOException
	 */
	public void adicionarModulo() throws DAOException{
		
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		
		ValidatorUtil.validateRequiredId(moduloCurricular.getModulo().getId(), "M�dulo", erros);
		ValidatorUtil.validaInt(moduloCurricular.getPeriodoOferta(), "Per�odo Oferta", erros);
		
		if (hasErrors())
			return ;
		
		if (moduloCurricular.getModulo().getId() != 0) {
			moduloCurricular.setModulo(dao.findByPrimaryKey(moduloCurricular.getModulo().getId(), Modulo.class));
			moduloCurricular.getModulo().getModuloDisciplinas().iterator();
			
			for (ModuloCurricular modCurr : obj.getModulosCurriculares()) {
				if (modCurr.getModulo().getId() == moduloCurricular.getModulo().getId())
					addMensagemErro("M�dulo j� est� presente na Estrutura Curricular.");
				
				for (ModuloDisciplina modDisc : modCurr.getModulo().getModuloDisciplinas()) {
					if (moduloCurricular.getModulo().getDisciplinas().contains(modDisc.getDisciplina())) 
						addMensagemErro("O Componente Curricular: " + modDisc.getDisciplina().getDescricao() + " j� est� presente na estrutura Curricular.");
					
				}
			}
			if (!hasOnlyErrors()) {
				erros.addAll(obj.validaMensagens(obj.getModulosCurriculares(),moduloCurricular));
				if (!hasOnlyErrors()) {
					moduloCurricular.setEstruturaCurricularTecnica(obj);
					for (ModuloCurricular mc : moduloCurricular.getModulo().getModuloCurriculares())
						mc.getModulo().getDisciplinas().iterator();
					obj.addModuloCurricular(moduloCurricular);
					moduloCurricular = new ModuloCurricular();
					moduloCurricular.setModulo(new Modulo());
				}
			}
	    } 
	}
	
	/**
	 * Remove M�dulo.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/estrutura/modulo.jsp
	 */
	public String removerModulo(){
		int id = getParameterInt("id", 0);
		if (id != 0) {
			for (Iterator<ModuloCurricular> iterator = obj.getModulosCurriculares().iterator(); iterator.hasNext();) {
				ModuloCurricular it = iterator.next();
				if (it.getModulo().getId() == id) {
					obj.setChTotalModulos(obj.getChTotalModulos() - it.getModulo().getCargaHoraria());
					iterator.remove();
				}
			}
			if (obj.getModulosCurriculares().size() == 0) obj.setChTotalModulos(0);
		}
		return viewModulo();
	}
	
	/**
	 * Confere se a carga hor�ria total dos m�dulos � igual a carga hor�ria da estrutura.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/estrutura/modulo.jsp
	 * 
	 * @return
	 */
	public String submeterModulos(){
		return forward(getDirBase() + JSP_DISCIPLINAS);
	}

	/**
	 * Adicionar disciplinas complementares a estrutura Curricular T�cnico.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/estrutura/disciplinas.jsp
	 * @throws DAOException 
	 */
	public void adicionarDisciplinaComplementar() throws DAOException{
		
		ValidatorUtil.validateRequiredId(disciplinaComplementar.getDisciplina().getId(), "Disciplina", erros);
		ValidatorUtil.validaInt(disciplinaComplementar.getPeriodoOferta(), "Per�odo de Oferta", erros);
		
		if (hasErrors())
			return ;
		
		disciplinaComplementar.setDisciplina(getGenericDAO().refresh(disciplinaComplementar.getDisciplina()));
		
		if (obj.getDisciplinasComplementares().contains(disciplinaComplementar)) 
			addMensagemErro("Essa disciplina j� est� presente na Estrutura Curricular.");
		else{
			if (!hasOnlyErrors()) {
				disciplinaComplementar.setEstruturaCurricularTecnica(obj);
				obj.addDisciplinaComplementar(disciplinaComplementar);
				disciplinaComplementar = new DisciplinaComplementar();
				disciplinaComplementar.setDisciplina(new ComponenteCurricular());
				disciplinaComplementar.setEstruturaCurricularTecnica(new EstruturaCurricularTecnica());
			}
		}
	}
	
	/**
	 * Carrega a Estrutura Curricular Tecnica.
	 * 
	 * @return
	 * @throws DAOException
	 */
	private EstruturaCurricularTecnica carregarEstruturaCurricular() throws DAOException{
		EstruturaCurricularTecnica estrutura = new EstruturaCurricularTecnica();
		GenericDAO dao = getGenericDAO();
		try {
			int id = getParameterInt("id", 0);
			if (id != 0) 
				estrutura = dao.findByPrimaryKey(id, EstruturaCurricularTecnica.class);
			else
				estrutura = dao.refresh(obj);

			for (ModuloCurricular mc : estrutura.getModulosCurriculares()){ 
				mc.getModulo().getDisciplinas().iterator();
			}

			if (isNotEmpty(estrutura.getDisciplinasComplementares()))
				estrutura.getDisciplinasComplementares().iterator();
			
			if (estrutura.getUnidadeTempo() == null)
				estrutura.setUnidadeTempo(new UnidadeTempo());
			
			if (ValidatorUtil.isEmpty(estrutura.getTurno()))
				estrutura.setTurno(new Turno());
			
			setObj(estrutura);
			return obj;

		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return estrutura;
	}

	/**
	 * M�todo respons�vel pela realiza��o da busca de uma estrutura Curricular T�cnico.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/estrutura/lista.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/estrutura/view.jsp</li>
	 *      <li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/curso.jsp</li>
	 * </ul>
     *
	 */
	@Override
	public String buscar() throws Exception {
		EstruturaCurricularTecDao ecDao = getDAO(EstruturaCurricularTecDao.class);
		estruturas.clear();
		try {
			if (filtroCurso && obj.getCursoTecnico().getId() != 0) {
				obj.setCursoTecnico(ecDao.findByPrimaryKey(obj.getCursoTecnico().getId(), CursoTecnico.class));
				estruturas.addAll(ecDao.findByCursoTecnico(obj.getCursoTecnico().getId()));
			}else if (filtroCurso && obj.getCursoTecnico().getId() == 0) 
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
			else {
				int idUnidade = (getAcessoMenu().isPedagogico()?0:getUsuarioLogado().getVinculoAtivo().getUnidade().getId() );
				estruturas = ecDao.findAll(idUnidade,getNivelEnsino(), null, getPaginacao());
				return forward(getDirBase() + "/lista.jsp");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			ecDao.close();
		}
		return forward(getListPage());
	}

	/**
	 * Realizar o cadastro da Estrutura Curricular de T�cnico
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/estrutura/view.jsp
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {

		Set<DisciplinaComplementar> discComplemRemovidas = new HashSet<DisciplinaComplementar>(0);
		Set<ModuloCurricular> modCurrRemovidos = new HashSet<ModuloCurricular>(0);
		
		EstruturaCurricularTecnica estruturaOld = getGenericDAO().findByPrimaryKey(obj.getId(), EstruturaCurricularTecnica.class);
		
		Set<Integer> modulosObj = new HashSet<Integer>(0);
		for (ModuloCurricular mc : obj.getModulosCurriculares()) {
			modulosObj.add(mc.getId());
		}
		
		if (obj.getId() != 0) {
			for (ModuloCurricular modCurr : estruturaOld.getModulosCurriculares()) {
				if (!modulosObj.contains(modCurr.getId())) {
					modCurrRemovidos.add( modCurr );
				}
			}

			if (!obj.getDisciplinasComplementares().isEmpty()) {
				for (DisciplinaComplementar discCompl : estruturaOld.getDisciplinasComplementares()) {
					if ( DisciplinaComplementar.compareTo(discCompl.getId(), obj.getDisciplinasComplementares()) ){ 
						discComplemRemovidas.add( discCompl );
					}
				}
			}
		}
		if (obj.getUnidadeTempo() == null || obj.getUnidadeTempo().getId() == 0)
			obj.setUnidadeTempo(null);
		if (obj.getTurno() == null || obj.getTurno().getId() == 0) 
			 obj.setTurno(null);
		
		EstruturaCurricularTecnicaMov mov;
		getGenericDAO().detach(estruturaOld);
		if (obj.getId() != 0) {
			setOperacaoAtiva(SigaaListaComando.ALTERAR_ESTRUTURA_CURR_TECNICO.getId());
			prepareMovimento(SigaaListaComando.ALTERAR_ESTRUTURA_CURR_TECNICO);
			mov = new EstruturaCurricularTecnicaMov(obj, SigaaListaComando.ALTERAR_ESTRUTURA_CURR_TECNICO);
			mov.setDiscComplemRemovidas(discComplemRemovidas);
			mov.setModCurrRemovidos(modCurrRemovidos);
			execute(mov);
			addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Estrutura Curricular");
		}else {
			setOperacaoAtiva(SigaaListaComando.CADASTRAR_ESTRUTURA_CURR_TECNICO.getId());
			prepareMovimento(SigaaListaComando.CADASTRAR_ESTRUTURA_CURR_TECNICO);
			mov = new EstruturaCurricularTecnicaMov(obj, SigaaListaComando.CADASTRAR_ESTRUTURA_CURR_TECNICO);
			execute(mov);
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Estrutura Curricular");
		}
			

		if (mov.getCodMovimento().getId() == ArqListaComando.ALTERAR.getId()) 
			return listar();
		
		return cancelar();
	}

	/**
	 * Retorna todas as Estruturas Curriculares T�cnica da Unidade do usu�rio
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/estrutura/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String listar() throws ArqException {
		EstruturaCurricularTecDao ecDao = getDAO(EstruturaCurricularTecDao.class);
		try {
			estruturas = new ArrayList<EstruturaCurricularTecnica>();
			
			int idUnidade = (getAcessoMenu().isPedagogico()? 0 : getUsuarioLogado().getVinculoAtivo().getUnidade().getId());
			
			estruturas.addAll(ecDao.findAll(idUnidade,getNivelEnsino(), null, getPaginacao()));
				
		} finally {
			ecDao.close();
		}
		return forward(getListPage());
	}
	
	/**
	 * Para ao se cadastrar ou atualizar o usu�rio ser direcionado para a tela listagem.
	 * 
	 * JSP: N�o invocado por jsp.
	 */
	@Override
	public String forwardCadastrar() {
		return getDirBase() + "/lista.jsp";
	}
	
	/**
	 * Direcionar para a tela de listagem.
	 */
	@Override
	public String getListPage() {
		return getDirBase() + "/lista.jsf";
	}
	
	/**
	 * Antes de atualizar carregar a Collection para realizar a atualiza��o.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/estrutura/lista.jsp
	 */
	@Override
	public String atualizar() throws ArqException {
		checkChangeRole();
		carregarEstruturaCurricular();
		setConfirmButton("Alterar");
		return forward(getFormPage());
	}
	
	/**
	 * Realiza��o da remo��o de uma estrutura Curricular T�cnica
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/estrutura/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String remover() throws ArqException {
		checkChangeRole();
		setId();
		if (obj.getId() != 0) 
			obj = getGenericDAO().findByPrimaryKey(obj.getId(), EstruturaCurricularTecnica.class);
		
		if (obj == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return forward(getListPage());
		}
		prepareMovimento(ArqListaComando.REMOVER);
		MovimentoCadastro mov = new MovimentoCadastro(obj, ArqListaComando.REMOVER);

		try {
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);
			obj = new EstruturaCurricularTecnica();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return redirect(getListPage());
		} 
		return listar();
	}
	
	@Override
	public void afterRemover() {
	}
	
	/**
	 * Redireciona para a tela de listagem depois da remo��o.
	 */
	@Override
	protected String forwardRemover() {
		return forwardCadastrar();
	}
	
	/**
	 * Direciona o usu�rio para a tela de visualiza��o.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/estrutura/disciplinas.jsp
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String viewResumo() throws DAOException {
		return forward(getDirBase() + JSP_RESUMO);
	}
	
	/**
	 * Direciona o usu�rio para a tela de visualiza��o.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/estrutura/disciplinas.jsp
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String view() throws DAOException{
		carregarEstruturaCurricular();
		return forward(getViewPage());
	}
	
	/**
	 * Direciona para a tela de cadastro de m�dulos
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/estrutura/disciplinas.jsp
	 * @return
	 */
	public String viewModulo(){
		return forward(getDirBase() + JSP_MODULO);
	}
	
	/**
	 * Direciona para a tela de cadastro de m�dulos
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/estrutura/disciplinas.jsp
	 * @return
	 */
	public String viewDisciplina(){
		return forward(getDirBase() + JSP_DISCIPLINAS);
	}
	
	/**
	 * Remove Disciplina Complementar.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/estrutura/disciplinas.jsp
	 * @throws DAOException 
	 */
	public String removerDisciplinaComplementar() throws DAOException{
		int id = getParameterInt("id", 0);
		if (id != 0) {
			for (Iterator<DisciplinaComplementar> iterator = obj.getDisciplinasComplementares().iterator(); iterator.hasNext();) {
				DisciplinaComplementar it = iterator.next();
				if (it.getDisciplina().getId() == id) {
					obj.setChTotalDisciplinasComplementares(obj.getChTotalDisciplinasComplementares() - it.getDisciplina().getChTotal());
					iterator.remove();
				}
			}
		}
		return viewDisciplina();
	}
	
	/**
	 * Inicializa��o da Disciplina Complementar a ser usada para adi��o da estrutura Curricular T�cnica. 
	 */
	private void inicializarDisciplinaComplementar() {
		disciplinaComplementar = new DisciplinaComplementar();
		disciplinaComplementar.setDisciplina(new ComponenteCurricular());
		disciplinaComplementar.setEstruturaCurricularTecnica(new EstruturaCurricularTecnica());
	}
	
	/**
	 * Retorna para a tela inicial, a tela dos dados gerais.
	 *
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/estrutura/modulo.jsp</li>
	 * </ul>
     *
	 * @return
	 */
	public String dadosGerais() {
		return forward(getFormPage());
	}

	public ModuloCurricular getModuloCurricular() {
		return moduloCurricular;
	}

	public void setModuloCurricular(ModuloCurricular moduloCurricular) {
		this.moduloCurricular = moduloCurricular;
	}

	public DisciplinaComplementar getDisciplinaComplementar() {
		return disciplinaComplementar;
	}

	public void setDisciplinaComplementar(
			DisciplinaComplementar disciplinaComplementar) {
		this.disciplinaComplementar = disciplinaComplementar;
	}

	public boolean isFiltroCurso() {
		return filtroCurso;
	}

	public void setFiltroCurso(boolean filtroCurso) {
		this.filtroCurso = filtroCurso;
	}

	public Collection<EstruturaCurricularTecnica> getEstruturas() {
		return estruturas;
	}

	public void setEstruturas(Collection<EstruturaCurricularTecnica> estruturas) {
		this.estruturas = estruturas;
	}

}
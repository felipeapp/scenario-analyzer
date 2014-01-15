/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/12/2006
 *
 */
package br.ufrn.sigaa.ensino.tecnico.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.DiscenteLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.TurmaEntradaLatoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.jsf.AtestadoMatriculaMBean;
import br.ufrn.sigaa.ensino.negocio.dominio.MatriculaMov;
import br.ufrn.sigaa.ensino.tecnico.dao.CursoTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dao.DiscenteTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dao.ModuloDao;
import br.ufrn.sigaa.ensino.tecnico.dao.TurmaEntradaTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ModuloCurricular;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Managed bean para cadastro de matrículas. Utilizado nas matrículas do ensino técnico e do lato sensu.
 *
 * @author David Ricardo
 *
 */
@Component("matriculaTecnico") @Scope("session")
public class CadastroMatriculaMBean extends SigaaAbstractController<Object> {

	/** Referência ao curso técnico. */
	private CursoTecnico curso;

	/** Referência a entrada da turma. */
	private TurmaEntradaTecnico turmaEntrada;

	/** Tipo de curso. */
	private String tipo;

	/** Tipo de aluno. */
	private DiscenteTecnico discente;

	/** Referência a turma. */
	private Turma turma;
	
	/** Lista de turmas do semestre. */
	private List<Turma> turmasSemestre;

	/** Collection de turmas. */
	private Collection<Turma> turmas;
	
	/** Collection de turmas de um módulo. */
	private Collection<Turma> turmasModulo;

	/** Collection de discentes. */
	private Collection<DiscenteAdapter> discentes;

	/** Referência ao módulo curricular. */
	private ModuloCurricular modulo;

	public CadastroMatriculaMBean() {
		inicializar();
	}

	public DiscenteTecnico getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteTecnico discente) {
		this.discente = discente;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public Collection<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(Collection<Turma> turmas) {
		this.turmas = turmas;
	}

	public ModuloCurricular getModulo() {
		return modulo;
	}

	public void setModulo(ModuloCurricular modulo) {
		this.modulo = modulo;
	}

	public String getTipo() {
		return tipo;
	}

	public CursoTecnico getCurso() {
		return curso;
	}

	public void setCurso(CursoTecnico curso) {
		this.curso = curso;
	}

	public TurmaEntradaTecnico getTurmaEntrada() {
		return turmaEntrada;
	}

	public void setTurmaEntrada(TurmaEntradaTecnico turmaEntrada) {
		this.turmaEntrada = turmaEntrada;
	}

	public Collection<DiscenteAdapter> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<DiscenteAdapter> discentes) {
		this.discentes = discentes;
	}

	public List<Turma> getTurmasSemestre() {
		return turmasSemestre;
	}

	public void setTurmasSemestre(List<Turma> turmasSemestre) {
		this.turmasSemestre = turmasSemestre;
	}
	
	@Override
	public String buscar() throws Exception {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequiredId(curso.getId(), "Curso", lista);
		if ("turmaTurma".equals(tipo))
			ValidatorUtil.validateRequiredId(turma.getId(), "Turma", lista);
		ValidatorUtil.validateRequiredId(turmaEntrada.getId(), "Turma de Entrada", lista);
		
		if ( !isEmpty(lista) ) {
			addMensagens(lista);
			return null;
		}

		getDiscentesMatricula();
		return forward("/ensino/tecnico/matricula/matriculaTurma.jsp");
	}
	
	/**
	 * Listagem das entradas das matrículas dos discentes.
	 * 
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>Método não invocado por JSP.</li>
	 * </ul> 
	 * @throws ArqException 
	 * @throws DAOException 
	 */
	public Collection<DiscenteAdapter> getDiscentesMatricula() throws DAOException, ArqException {
		DiscenteTecnicoDao dao = getDAO(DiscenteTecnicoDao.class);
		DiscenteLatoDao daoLato = getDAO(DiscenteLatoDao.class);
		try {
			if (turmaEntrada.getId() == 0) {
				return Collections.emptyList();
			} else {
				if( getSubSistema().equals(SigaaSubsistemas.TECNICO) )
					discentes = dao.findByTurmaEntrada(turmaEntrada.getId(), getUnidadeGestora(), NivelEnsino.TECNICO, true);
				else if ( getSubSistema().equals(SigaaSubsistemas.FORMACAO_COMPLEMENTAR) ) {
					discentes = dao.findByTurmaEntrada(turmaEntrada.getId(), getUnidadeGestora(), NivelEnsino.FORMACAO_COMPLEMENTAR, true);
					if ("turmaTurma".equals(tipo) ) {
						turma = dao.findByPrimaryKey(turma.getId(), Turma.class);
						Collection<DiscenteAdapter> discenteMatriculados = 
								dao.findByDiscentesMatriculados(turmaEntrada.getId(), SituacaoMatricula.MATRICULADO.getId(), 
										getUnidadeGestora(), NivelEnsino.FORMACAO_COMPLEMENTAR, turma.getDisciplina().getId());
						for (DiscenteAdapter discente : discentes)
							discente.setMatricular( discenteMatriculados.contains(discente) );
						
					}
				} else if( getSubSistema().equals(SigaaSubsistemas.LATO_SENSU) || getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO) )
					discentes = daoLato.findByTurmaEntrada(turmaEntrada.getId(), true);
				
				if ( !hasOnlyErrors() && discentes.isEmpty() )
					addMensagemErro("Não foram encontrados discentes para a turma de entrada selecionada.");
				
				return discentes;
			}
		} finally {
			dao.close();
			daoLato.close();
		}
	}
	
	/**
	 * Listagem dos cursos.
	 * 
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>Método não invocado por JSP.</li>
	 * </ul> 
	 */
	public Collection<SelectItem> getCursos() throws DAOException, ArqException {
		CursoTecnicoDao dao = getDAO(CursoTecnicoDao.class);
		Collection<Curso> cursos = new HashSet<Curso>();
		
		if( getSubSistema().equals(SigaaSubsistemas.TECNICO))
			cursos = dao.findAll(getUnidadeGestora(), NivelEnsino.TECNICO, CursoTecnico.class, null);
		else if( getSubSistema().equals(SigaaSubsistemas.FORMACAO_COMPLEMENTAR))
			cursos = dao.findAll(getUnidadeGestora(), NivelEnsino.FORMACAO_COMPLEMENTAR, CursoTecnico.class, null);
		else if( getSubSistema().equals(SigaaSubsistemas.LATO_SENSU))
			cursos = dao.findAllOtimizado(NivelEnsino.LATO, null);
		else if( getSubSistema().equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO))
			cursos = getAllCursosCoordenacaoNivel();
		
		return toSelectItems(cursos, "id", "nome");
	}

	/**
	 * Listagem das entradas de turmas.
	 * 
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>Método não invocado por JSP.</li>
	 * </ul> 
	 */
	public Collection<SelectItem> getTurmasEntrada() throws DAOException {
		Collection<SelectItem> itens = new ArrayList<SelectItem>();
		TurmaEntradaTecnicoDao dao = getDAO(TurmaEntradaTecnicoDao.class);
		TurmaEntradaLatoDao daoLato = getDAO(TurmaEntradaLatoDao.class);
		
		if (curso.getId() > 0) {
			if( !getSubSistema().equals( SigaaSubsistemas.LATO_SENSU ) && !getSubSistema().equals( SigaaSubsistemas.PORTAL_COORDENADOR_LATO ) )
				itens.addAll(toSelectItems(dao.findByCursoTecnico(curso.getId(),0,0,0), "id", "descricao"));
			else
				itens.addAll(toSelectItems(daoLato.findByCursoLato(curso.getId(), true), "id", "descricao"));
		}

		return itens;
	}
	
	/**
	 * Cancela a matrícula.
	 * 
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>Método não invocado por JSP.</li>
	 * </ul> 
	 */
	@Override
	public String cancelar() {
		inicializar();
		return super.cancelar();
	}

	/**
	 * Inicializa o processo de matrícula.
	 * 
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>Método não invocado por JSP.</li>
	 * </ul> 
	 */
	private void inicializar() {
		curso = new CursoTecnico();
		turmaEntrada = new TurmaEntradaTecnico();

		discente = new DiscenteTecnico();
		discente.setPessoa(new Pessoa());
		turma = new Turma();
		turma.setDisciplina(new ComponenteCurricular());
		modulo = new ModuloCurricular();

		turmas = new ArrayList<Turma>();
		turmasModulo = new ArrayList<Turma>();
		discentes = new ArrayList<DiscenteAdapter>();
	}


	/**
	 * Escolhe o tipo de matrícula.
	 * 
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>Método não invocado por JSP.</li>
	 * </ul> 
	 * @throws Exception 
	 */
	public String escolheTipo() throws Exception {
		turmas.clear();
		if (getParameter("tipo") != null)
			tipo = getParameter("tipo");
		if (tipo != null && !tipo.equals(getParameter("tipo")) && getParameter("tipo") != null)
			tipo = getParameter("tipo");
		if ("turmasSemestre".equals(tipo))
			return matriculaTurmasSemestre();
		if ("turmaTurma".equals(tipo) || "turmaModulo".equals(tipo))
			return matriculaTurma();
		if ("alunoTurma".equals(tipo)) {
			discente = new DiscenteTecnico();
			turma = new Turma();
			turma.setDisciplina(new ComponenteCurricular());
		}
		if("alunoModulo".equals(tipo) && !turmas.isEmpty()) {
			return forward("/ensino/tecnico/matricula/disciplinasModulo.jsp");
		}
		return tipo;
	}

	/**
	 * Seleciona o tipo de escolha de matricula para o discente
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>Método não invocado por JSP.</li>
	 * </ul> 
	 * 
	 * @param event
	 * @throws ArqException
	 */
	public void escolhaDoTipo(ActionEvent event) throws ArqException {  
	    UIParameter parameter = (UIParameter) event.getComponent().findComponent("tipo");  
	    if (parameter.getValue().toString().equals("alunoTurma")) 
	    	forward("/ensino/tecnico/matricula/matriculaAluno.jsp");
	    if ("turmasSemestre".equals(tipo))
	    	forward("/ensino/tecnico/matricula/turmasSemestre.jsp");
	 }  
	
	/**
	 * Redireciona para a matrícula do aluno.
	 * 
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>Método não invocado por JSP.</li>
	 * </ul> 
	 */
	public String matriculaAluno() {
		turmasModulo.clear();
		return forward("/ensino/tecnico/matricula/matriculaAluno.jsp");
	}

	/**
	 * Inicializa e redireciona para a matrícula nas turmas do semestre.
	 * 
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>Método não invocado por JSP.</li>
	 * </ul> 
	 */
	public String matriculaTurmasSemestre() throws ArqException {
		turmas = new ArrayList<Turma>(0);
		discente = new DiscenteTecnico();
		if (turmasSemestre == null || turmasSemestre.isEmpty()) {
			CalendarioAcademico cal = getCalendarioVigente();
			TurmaDao dao = getDAO(TurmaDao.class);
			turmasSemestre = (List<Turma>) dao.findGeral(new Character(getNivelEnsino()), new Unidade(getUnidadeGestora()), null, null, null, null, new Integer[]{SituacaoTurma.ABERTA, SituacaoTurma.A_DEFINIR_DOCENTE}, cal.getAno(), cal.getPeriodo(), null, null, null, null, null, false, null, null, false, null, null, null,null);
		}
		return forward("/ensino/tecnico/matricula/turmasSemestre.jsp");
	}

	/**
	 * Redireciona para a matrícula em uma turma.
	 * Método não invocado por  JSPs
	 * @throws Exception 
	 */
	public String matriculaTurma() throws Exception {
		return forward("/ensino/tecnico/matricula/matriculaTurma.jsp");
	}
	
	/**
	 * Validação da matricula em Turma
	 * 
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>Método não invocado por JSP.</li>
	 * </ul> 
	 */
	public void validacaoTurma(){
		boolean haDiscenteSelecionado = false; 
		ValidatorUtil.validateRequiredId(curso.getId(), "Curso", erros);
		ValidatorUtil.validateRequiredId(turmaEntrada.getId(), "Turma de Entrada", erros);
		if (!"turmaModulo".equals(tipo))
			ValidatorUtil.validateRequiredId(turma.getId(), "Turma", erros);

		for (DiscenteAdapter d : discentes) {
			if (d.getDiscente().getDiscente().isSelecionado()){
				haDiscenteSelecionado = true;
				break;
			}
		}
		
		if (!haDiscenteSelecionado)
			erros.addErro("É obrigatório informar um discente.");
	}
	
	/**
	 * Realiza a matrícula e redireciona para confirmação.
	 * 
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>Método não invocado por JSP.</li>
	 * </ul> 
	 * @throws ArqException 
	 * 
	 * matriculaTecnico.realizarMatricula
	 */
	public String realizarMatricula() throws ArqException {
		DiscenteDao dao = getDAO(DiscenteDao.class);
		ModuloDao mDao = getDAO(ModuloDao.class);

		if ("turmaTurma".equals(tipo) || "turmaModulo".equals(tipo))
			validacaoTurma();
		
		if (hasOnlyErrors())
			return null;
		
		prepareMovimento(SigaaListaComando.MATRICULAR_TECNICO);

		if (!validar())
			return null;

		if ("alunoTurma".equals(tipo)) { // Matricular um aluno em uma turma
			turmas.clear();
			discentes.clear();

			//turmas.addAll(dao.findTurmasSemestre(discente.getId()));
			Turma t = dao.findByPrimaryKey(turma.getId(), Turma.class);
			t.setMatricular(true);
			if (!turmas.contains(t))
				turmas.add(t);

			DiscenteAdapter discenteBD = dao.findByPK(discente.getId());
			if(discenteBD.isTecnico() && ((DiscenteTecnico) discenteBD).getTurmaEntradaTecnico() != null 
					&& ((DiscenteTecnico) discenteBD).getTurmaEntradaTecnico().getEspecializacao() != null){
				((DiscenteTecnico) discenteBD).getTurmaEntradaTecnico().getEspecializacao().getId();
				((DiscenteTecnico) discenteBD).getTurmaEntradaTecnico().getEspecializacao().getDescricao();
			}
			discentes.add(discenteBD);
		} else if ("alunoModulo".equals(tipo)) { // matricular um aluno em um módulo

			if (turmasModulo.isEmpty()) {
				turmasModulo.addAll(mDao.findTurmasAtivasByModulo(modulo.getId()));
				if(!turmas.isEmpty()){
					for(Turma t1: turmas)
						for(Turma t2: turmasModulo){
							if(t1.getId() == t2.getId())
								t2.setMatricular(true);
						}
				}
				return forward("/ensino/tecnico/matricula/disciplinasModulo.jsp");
			} else {
				for (Iterator<Turma> it = turmasModulo.iterator(); it.hasNext(); ) {
					Turma t = it.next();
					if (!t.isMatricular()){
						turmas.remove(t);
					} else {
						if(!turmas.contains(t)){
							t.getDescricaoHorario();
							turmas.add((Turma) UFRNUtils.deepCopy(t));
						}
					}
				}
				
				if(turmas.isEmpty()) {
					addMensagemErro("Selecione pelo menos uma turma para efetuar a matrícula.");
					return forward("/ensino/tecnico/matricula/disciplinasModulo.jsp");
				}

				discentes.clear();
				discentes.add(discente);
			}

		} else if ("turmaTurma".equals(tipo)) { // matricular uma turma de entrada em uma turma
			for (Iterator<DiscenteAdapter> it = discentes.iterator(); it.hasNext(); ) {
				DiscenteAdapter d = it.next();
				if ( !d.isSelecionado() )
					it.remove();
			}
			Turma t = dao.findByPrimaryKey(turma.getId(), Turma.class);
			t.setMatricular(true);
			t.getDescricaoHorario();
			if (!turmas.contains(t))
				turmas.add(t);

		} else if ("turmaModulo".equals(tipo)) {// Matricular uma turma de entrada em um módulo.
			for (Iterator<DiscenteAdapter> it = discentes.iterator(); it.hasNext(); ) {
				DiscenteAdapter d = it.next();
				if (!d.isMatricular())
					it.remove();
			}
			
			if (getCurrentURL().contains("matriculaTurma.jsf"))
				turmas.clear();
			else{
				validarTurma();
				if (hasErrors())
					return null;
			}
			if (turmas.isEmpty()) {
				Collection<Turma> turmasAtivas = mDao.findTurmasAtivasByModulo(modulo.getId()); 
				for (Turma linhaTurma : turmasAtivas) {
					if (!turmas.contains(linhaTurma)) {
						linhaTurma.getDescricaoHorario();
						linhaTurma.getHorarios();
						turmas.add(linhaTurma);
					}
				}
				return forward("/ensino/tecnico/matricula/disciplinasModulo.jsp");
			} else {
				for (Iterator<Turma> it = turmas.iterator(); it.hasNext(); ) {
					Turma t = it.next();
					if (!t.isMatricular())
						it.remove();
				}
			}
		} else if ("turmasSemestre".equals(tipo)) {// Matricular um aluno em algumas turmas do semestre
			turmas.clear();
			discentes.clear();
			// pegando posições selecionadas
			for (String linha : getParameterValues("selecionadas")) {
				Turma t = turmasSemestre.get(new Integer(linha));
				turmas.add(dao.findByPrimaryKey(t.getId(), Turma.class));
			}
			DiscenteAdapter discenteBD = dao.findByPK(discente.getId());
			if(discenteBD.isTecnico() && ((DiscenteTecnico) discenteBD).getTurmaEntradaTecnico() != null 
					&& ((DiscenteTecnico) discenteBD).getTurmaEntradaTecnico().getEspecializacao() != null){
				((DiscenteTecnico) discenteBD).getTurmaEntradaTecnico().getEspecializacao().getId();
				((DiscenteTecnico) discenteBD).getTurmaEntradaTecnico().getEspecializacao().getDescricao();
			}
			discentes.add(discenteBD);
			for(Turma t: turmas)
				t.setMatricular(true);
		}

		return forward("/ensino/tecnico/matricula/confirmar.jsp");
	}

	/**
	 * É necessário selecionar uma turma.
	 */
	private void validarTurma(){
		boolean turmaSelecionada = false;
		for (Turma linha : turmas) {
			if (linha.isMatricular()){
				turmaSelecionada = true;
				break;
			}
		}
		if (!turmaSelecionada)
			addMensagemErro("É necessário selecionar uma Turma");
	}
	
	/**
	 * Realiza a confirmação da matrícula.
	 * 
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>Método não invocado por JSP.</li>
	 * </ul> 
	 */
	public String confirmar() throws ArqException {

		try {

			MatriculaMov mov = new MatriculaMov();
			mov.setCodMovimento(SigaaListaComando.MATRICULAR_TECNICO);
			mov.setTurmas(turmas);
			mov.setDiscentes(discentes);

			execute(mov, getCurrentRequest());
			addMessage("Matrícula executada com sucesso", TipoMensagemUFRN.INFORMATION);
		} catch(NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		if ("turmasSemestre".equals(tipo) || "alunoTurma".equals(tipo) || "alunoModulo".equals(tipo)) {
			Discente d = getGenericDAO().findByPrimaryKey(discente.getId(), Discente.class);
			if(d != null)
				discente.setDiscente(d);
			return forward("/ensino/tecnico/matricula/resumo.jsp");
		} else {
			inicializar();
			resetBean();
			return forward("/ensino/tecnico/matricula/tipoMatricula.jsp");
		}
	}

	/**
	 * Realiza a emissão de atestado de matrícula.
	 * 
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>Método não invocado por JSP.</li>
	 * </ul> 
	 */
	public String verAtestado() throws IOException, SegurancaException, DAOException{
		if (discente == null) {
			addMensagemErro("Ainda não é possível visualizar o atestado de matrícula para o seu nível de ensino.");
			return null;
		}
		if ( discente.isGraduacao() || discente.isStricto() || discente.isLato() || discente.isTecnico() || discente.isFormacaoComplementar())  {
			AtestadoMatriculaMBean atestado = (AtestadoMatriculaMBean) getMBean("atestadoMatricula");
			getCurrentSession().setAttribute("atestadoLiberado", discente.getId());
			atestado.setDiscente(discente);
			return atestado.selecionaDiscente();
		}
		addMensagemErro("Ainda não é possível visualizar o atestado de matrícula para o seu nível de ensino.");
		return null;		
	}

	/**
	 * Inicializa a realização de uma nova matrícula.
	 * 
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>Método não invocado por JSP.</li>
	 * </ul> 
	 */
	public String novaMatricula() throws ArqException {
		if ("turmasSemestre".equals(tipo)) {
			return matriculaTurmasSemestre();
		} else {
			inicializar();
			resetBean();
			return forward("/ensino/tecnico/matricula/tipoMatricula.jsp");
		}
	}
	
	/**
	 * Realiza a emissão de atestado de matrícula.
	 * 
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   	<li>Método não invocado por JSP.</li>
	 * </ul> 
	 */
	public Collection<SelectItem> getModulos() {
		ModuloDao dao = getDAO(ModuloDao.class);
		Collection<SelectItem> itens = new ArrayList<SelectItem>();

		try {

			if (curso.getId() == 0) {
				SelectItem item = new SelectItem("0", "Selecione um curso.");
				itens.add(item);
			} else {
				itens = toSelectItems(dao.findByCursoTecnico(curso.getId()), "id", "descricao");
			}
		} catch(Exception e) {
			notifyError(e);
			e.printStackTrace();
		}

		return itens;
	}

	/** Valida os dados da matrícula antes de cadastrar, 
	 * @return
	 */
	private boolean validar() {
		boolean valid = true;

		if ("alunoTurma".equals(tipo)) {
			if (discente.getId() == 0) {
				valid = false;
				ValidatorUtil.validateRequired(null, "Aluno", erros);
			}

			if (turma.getId() == 0) {
				valid = false;
				ValidatorUtil.validateRequired(null, "Turma", erros);
			}

		}else if ("alunoModulo".equals(tipo)) {
			if (discente.getId() == 0) {
				valid = false;
				ValidatorUtil.validateRequired(null, "Aluno", erros);
			}
			
			if (curso.getId() == 0) {
				valid = false;
				ValidatorUtil.validateRequired(null, "Curso", erros);
			}
			
			if (modulo.getId() == 0) {
				valid = false;
				ValidatorUtil.validateRequired(null, "Módulos", erros);
			}
		}else if ("turmasSemestre".equals(tipo)) {
			if (discente.getId() == 0) {
				valid = false;
				addMensagemErro("É necessário escolher um discente para efetuar a matrícula.");
			}

			if (getParameterValues("selecionadas") == null) {
				valid = false;
				addMensagemErro("É necessário escolher uma turma para efetuar a matrícula.");
			}
		}
		addMensagens(erros);
		return valid;
	}

	public Collection<Turma> getTurmasModulo() {
		return turmasModulo;
	}

	public void setTurmasModulo(Collection<Turma> turmasModulo) {
		this.turmasModulo = turmasModulo;
	}
}
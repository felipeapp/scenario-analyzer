/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 15/06/2011
 * Autor: Rafael Gomes
 */


package br.ufrn.sigaa.ensino.medio.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.CalendarioAcademicoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.medio.dao.CurriculoMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.SerieDao;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.CurriculoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.CurriculoMedioComponente;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerieAno;
import br.ufrn.sigaa.ensino.medio.negocio.MovimentoCadastrarTurmaDependencia;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;

/**
 *  MBean responsável por operações com a Turma anual de dependencia do Ensino Médio, que o aluno será vinculado .
 * 
 * @author Joab Galdino
 *
 */
@Component("turmaSerieDependencia") @Scope("request")
public class TurmaSerieDependenciaMBean extends TurmaSerieMBean{

	
	/** Lista dos Currículos por série de ensino médio. */
	private List<SelectItem> curriculosBySerieDependencia = new ArrayList<SelectItem>(0);
	
	/** Lista dos ids das disciplinas da turma de dependencia*/
	private List<Integer> idsDisciplinasTurma = new ArrayList<Integer>();
	
	/** guarda o valor do curriculo selecionado. */
	private int curriculoSelecionado = 0 ;
	
	/** Guarda a turma */
	private TurmaSerie turmaSerie;
	
	/** indica que a turma de dependencia que o usuario tentou cadastrar ja existe */
	private boolean cadastroTurmaExistente = false;
	
	/** Define o link para o resumo dos dados da turma de dependencia. */
	public static final String JSP_FORM_TURMA_DEPENDENCIA = "/formTurmaDependencia.jsp";

	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkChangeRole();
		setConfirmButton("Cadastrar");
		setLabelCombo("Cadastrar");
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_TURMA_SERIE_DEPENDENCIA.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_TURMA_SERIE_DEPENDENCIA);		
		return forward(getFormTurmaDependenciaPage());
		
	}
	/** Inicializando das variáveis utilizadas */
	private void initObj(){
		obj = new TurmaSerie();
		obj.setSerie(new Serie());
		obj.getSerie().setCursoMedio(new CursoMedio());
		obj.setTurno(new Turno());
		setFiltroInativos(false);
		setPossuiAlunosMatriculados(false);
	}
	/** 
	 * Carrega as séries pertencentes ao curso de ensino médio selecionado na jsp..
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/medio/turmaSerie/form.jsp</li>
	 * </ul>
 	 * @param e
	 * @throws DAOException
	 */
	@Override
	public void carregarSeriesByCurso(ValueChangeEvent e) throws DAOException {
		SerieDao dao = getDAO( SerieDao.class );
		CalendarioAcademicoDao calDao = getDAO(CalendarioAcademicoDao.class);
		CursoMedio cursoMedio = null;
		
		if( e != null && (Integer)e.getNewValue() > 0 ){
			cursoMedio = dao.findByPrimaryKey((Integer)e.getNewValue(), CursoMedio.class);
			curriculosBySerieDependencia = new ArrayList<SelectItem>(0);
			setCurriculoMedio(new CurriculoMedio());
		}else {
			setSeriesByCurso(new ArrayList<SelectItem>(0));
			curriculosBySerieDependencia = new ArrayList<SelectItem>(0);
			setCurriculoMedio(new CurriculoMedio());
			return;
		}	
		
		cursoMedio.setNivel(getNivelEnsino());
		if (cursoMedio != null){
			setSeriesByCurso( toSelectItems(dao.findByCurso(cursoMedio), "id", "descricaoCompleta"));
			obj.getSerie().setId(0);
			setCalendarioTurmaSerie(calDao.findByParametros(null, null, cursoMedio.getUnidade(), NivelEnsino.MEDIO,
					null, null, cursoMedio, null));
			if (getCalendarioTurmaSerie() == null)
				setCalendarioTurmaSerie(CalendarioAcademicoHelper.getCalendarioUnidadeGlobalMedio());	
			obj.setDataInicio(getCalendarioTurmaSerie().getInicioPeriodoLetivo());
			obj.setDataFim(getCalendarioTurmaSerie().getFimPeriodoLetivo());
		}	
	}
	
	/** 
	 * Carrega os currículos ativos pertencentes a série de ensino médio selecionado na jsp.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/medio/turmaSerie/formTurmaDependencia.jsp</li>
	 * </ul>
 	 * @param e
	 * @throws DAOException
	 */
	public void carregarCurriculosBySerieDependencia(ValueChangeEvent e) throws DAOException {		
        CurriculoMedioDao dao = getDAO( CurriculoMedioDao.class );
		
		Serie serie = null;
		
		if( e != null && e.getNewValue() != null && (Integer)e.getNewValue() > 0 )
			serie = dao.findByPrimaryKey((Integer)e.getNewValue(), Serie.class);
		else {
			setCurriculosBySerie(new ArrayList<CurriculoMedio>());
			curriculosBySerieDependencia = new ArrayList<SelectItem>(0);
			setCurriculoMedio(new CurriculoMedio());
			return;
		}	
		
		boolean ativo = true;
		
		if (serie != null){
			setCurriculosBySerie(dao.findByCursoOrSerie(null, serie, null, ativo));
			curriculosBySerieDependencia = toSelectItems(getCurriculosBySerie(),"id","codigo");
			new ArrayList<SelectItem>(0);
			setExibeCurriculos(true);
		}		
		
	}
	/** 
	 * Carrega os currículos ativos pertencentes a série de ensino médio selecionado na jsp.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> NENHUMA JSP</li>
	 * </ul>
 	 * @param e
	 * @throws DAOException
	 */
	@Override
	public void carregarCurriculosBySerie() throws DAOException {
		CurriculoMedioDao dao = getDAO( CurriculoMedioDao.class );
		
		boolean curriculosAtivos = true;
		
		if (obj.getSerie() != null){
			setCurriculosBySerie(dao.findByCursoOrSerie(null, obj.getSerie(), null, curriculosAtivos));
			curriculosBySerieDependencia = toSelectItems(getCurriculosBySerie(),"id","codigo");
			setExibeCurriculos(true);
		} else {
			setCurriculosBySerie(new ArrayList<CurriculoMedio>());
			curriculosBySerieDependencia = new ArrayList<SelectItem>(0);
			setExibeCurriculos(false);
		}
	}

	/**
	 * Método responsável por redirecionar o usuário para o formulário de atualização da turma de dependencia.
	 * @return
	 */
	public String getFormTurmaDependenciaPage() {
		return getDirBase() + JSP_FORM_TURMA_DEPENDENCIA;
	}
	
	/**
	 * Método responsável por exibir o detalhe da estrutura curricular selecionada na view.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/medio/turmaSerieDependencia/form.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws DAOException
	 */	
	public void visualizarCurriculo(ValueChangeEvent evt) throws DAOException{
		int id = getParameterInt(((Integer)curriculoSelecionado).toString(), 0);
		if( evt != null && evt.getNewValue() != null && (Integer)evt.getNewValue() > 0 )			
			id = (Integer)evt.getNewValue();
		else {
			id = 0;
			
		}	
		
		setCurriculoMedio(getGenericDAO().findByPrimaryKey(id, CurriculoMedio.class));
		
	}
	/**
	 * Método responsável por exibir o detalhe da estrutura curricular selecionada na view.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> Nenhuma JSP</li>
	 * </ul>
	 * @throws DAOException
	 */	
	public void visualizarCurriculo() throws DAOException{
		TurmaSerieDao dao  = getDAO(TurmaSerieDao.class);
		
		Map<Integer, Long> mapMatriculadosDisciplina = dao.findQtdeAlunosMatriculadosByDisciplina(obj);
		
		for (TurmaSerieAno tsa : obj.getDisciplinas()) {
			if ( mapMatriculadosDisciplina.get(tsa.getTurma().getId()) != null )
				tsa.getTurma().setQtdMatriculados(mapMatriculadosDisciplina.get(tsa.getTurma().getId()));
		}
		
		for(TurmaSerieAno tsa: obj.getDisciplinas())
			tsa.getTurma().getQtdMatriculados();
		List<Integer> idComponenteComMatricula = new ArrayList<Integer>();
		
		for(TurmaSerieAno tsa : obj.getDisciplinas()){
			idsDisciplinasTurma.add(tsa.getTurma().getDisciplina().getId());
			if(tsa.getTurma().getQtdMatriculados() > 0)
				idComponenteComMatricula.add(tsa.getTurma().getDisciplina().getId());
		}
		
		setCurriculoMedio(dao.findByPrimaryKey(obj.getCurriculo().getId(), CurriculoMedio.class));
				
		for(CurriculoMedioComponente cc : getCurriculoMedio().getCurriculoComponentes()){
			if(idsDisciplinasTurma.contains(cc.getComponente().getId()))
				cc.setSelecionado(true);
			if(idComponenteComMatricula.contains(cc.getComponente().getId()))
				cc.setPossuiAlunoMatriculado(true);
	
		}
		
		
	}
	
	@Override
	public String atualizar() throws ArqException {
		checkChangeRole();
		TurmaSerieDao tsDao = getDAO(TurmaSerieDao.class);
		if(obj.getId() <= 0) 
			setId();
		setObj(tsDao.findByPrimaryKey(obj.getId(), TurmaSerie.class));
		if( isEmpty(obj.getTurno()) ) obj.setTurno(new Turno());
		setPossuiAlunosMatriculados(tsDao.findQtdeAlunosByTurma(obj) > 0);
		curriculoSelecionado = obj.getCurriculo().getId();
		carregarSeriesByCurso();
		carregarCurriculosBySerie();
		visualizarCurriculo();
		setConfirmButton("Alterar");
		setOperacaoAtiva(SigaaListaComando.ALTERAR_TURMA_SERIE_DEPENDENCIA.getId());
		prepareMovimento(SigaaListaComando.ALTERAR_TURMA_SERIE_DEPENDENCIA);
		return forward(getFormTurmaDependenciaPage());
	}
	
	
	/**
	 * Método responsável pelo cadastro de uma Série de Ensino Médio.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/turmaSerie/form.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * 
	 */
	@Override
	public String cadastrar() throws ArqException {
		TurmaSerieDao tsDao = getDAO(TurmaSerieDao.class);
		
		String[] linhas = getCurrentRequest().getParameterValues("disciplinas");
		obj.setNome("dep");
		obj.setDependencia(true);
		checkChangeRole();
		
		if ("remover".equalsIgnoreCase(getConfirmButton()))
			return remover();
		Comando comando;
		if (obj.getId() > 0)
			comando = SigaaListaComando.ALTERAR_TURMA_SERIE_DEPENDENCIA;
		else
			comando = SigaaListaComando.CADASTRAR_TURMA_SERIE_DEPENDENCIA;
		
		setOperacaoAtiva(comando.getId());
		int idCurriculoSelecionado = curriculoSelecionado;
		
		if ( !isPossuiAlunosMatriculados() ) {
			ValidatorUtil.validateRequiredId(idCurriculoSelecionado, "Estrutura Curricular", erros);
		}	
		List<Integer> idDisciplinasSelecionadas = new ArrayList<Integer>();
		List<Integer> idDisciplinasAdicionar = new ArrayList<Integer>();
		
		boolean turmaPossuiComponentes = false;
		//verificando se a turma esta vazia
		if ( linhas == null || linhas.length == 0 ) {
			for(CurriculoMedioComponente cmc :getCurriculoMedio().getCurriculoComponentes()){
				if(cmc.isSelecionado()){
					turmaPossuiComponentes = true;
					break;
				}
			}
			if(!turmaPossuiComponentes){
				addMensagemErro("Ao menos uma disciplina deve ser escolhida");
				return null;
			}
		}else{
			for (String linha : linhas) {
				idDisciplinasSelecionadas.add(new Integer(linha));
			}
			
		}
		
		for(Integer idDC : idDisciplinasSelecionadas){
			if(!idsDisciplinasTurma.contains(idDC))
				idDisciplinasAdicionar.add(idDC);
		}
					
		for(CurriculoMedioComponente cc : getCurriculoMedio().getCurriculoComponentes()){
			if(cc.isSelecionado())//para o caso dos componentes com aluno matriculado
				idDisciplinasSelecionadas.add(cc.getComponente().getId());
			if(idDisciplinasSelecionadas.contains(cc.getComponente().getId())){
				cc.setSelecionado(true);
			}else{
				cc.setSelecionado(false);
			}
		}	
		if ( tsDao.existeTurmaDependenciaMesmoCurriculoByAno(obj) )
			erros.addErro("Já existe uma turma de dependência cadastrada para esta série e ano.");
		erros.addAll(obj.validate());
		if (hasErrors()) return null;
		
		Boolean alterarCurriculo = false;
		if ( comando.equals(SigaaListaComando.ALTERAR_TURMA_SERIE_DEPENDENCIA) && !isPossuiAlunosMatriculados() 
			&& idCurriculoSelecionado > 0 && obj.getCurriculo() != null
			&& obj.getCurriculo().getId() != idCurriculoSelecionado ){
				alterarCurriculo = true;
				obj.setCurriculo(tsDao.findByPrimaryKey(idCurriculoSelecionado, CurriculoMedio.class));
		}
		if(comando.equals(SigaaListaComando.ALTERAR_TURMA_SERIE_DEPENDENCIA) && !idDisciplinasSelecionadas.equals(idsDisciplinasTurma))
			alterarCurriculo = true;			
		if ( comando.equals(SigaaListaComando.CADASTRAR_TURMA_SERIE_DEPENDENCIA) )
			obj.setCurriculo(tsDao.findByPrimaryKey(idCurriculoSelecionado, CurriculoMedio.class));
		MovimentoCadastrarTurmaDependencia mov = new MovimentoCadastrarTurmaDependencia();
		mov.setCodMovimento(comando);
		mov.setObjMovimentado(obj);
		mov.setObjAuxiliar(alterarCurriculo);
		mov.setdisciplinasSelecionadas(idDisciplinasSelecionadas);
		mov.setdisciplinasAdicionadas(idDisciplinasAdicionar);
		try {
			execute(mov);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} 
		
		TurmaSerieMBean tsMben = getMBean("turmaSerie");
		tsMben.setObj(obj);
		tsMben.setSeriesByCurso(getSeriesByCurso());
		tsMben.listarTurmasAnual();
		if (comando.equals(SigaaListaComando.CADASTRAR_TURMA_SERIE_DEPENDENCIA)) {
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Turma da Série");
		} else {
			addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Turma da Série");
		} 
		removeOperacaoAtiva();
		return null;
		
	}
	
	/** Método responsável por criar as turmas de disciplinas pertencentes a uma turma anual de ensino médio,
	 * com base as disciplinas do currículo da série selecionada.
	 * 
	 * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/turmaSerie/form.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * */
	public String carregarTurmaAnualExistente(ValueChangeEvent evt) throws ArqException{
		
		if (isEmpty(obj.getAno())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		if (hasErrors())
			return null;
		if( evt != null && evt.getNewValue() != null && (Integer)evt.getNewValue() > 0 )			
			curriculoSelecionado = (Integer)evt.getNewValue();
		cadastroTurmaExistente = false;
		// Buscar as turma disponibilizadas para a série e ano informados
		TurmaSerieDao dao = getDAO(TurmaSerieDao.class);
		CurriculoMedio curriculo = new CurriculoMedio(curriculoSelecionado);
		TurmaSerie turmaDep = null ;
		turmaDep = dao.findDependenciaByCursoSerieAnoCurriculo(obj.getSerie().getCursoMedio(), obj.getSerie(), obj.getAno(),false,curriculo);
		
		if (turmaDep == null) {
			initObj();			
			if(isOperacaoAtiva(SigaaListaComando.ALTERAR_TURMA_SERIE_DEPENDENCIA.getId())){
				setConfirmButton("Cadastrar");
				setLabelCombo("Cadastrar");
				setOperacaoAtiva(SigaaListaComando.CADASTRAR_TURMA_SERIE_DEPENDENCIA.getId());
				prepareMovimento(SigaaListaComando.CADASTRAR_TURMA_SERIE_DEPENDENCIA);	
			}
			visualizarCurriculo(evt);
			return null;
		}
		cadastroTurmaExistente = true;		
		obj = turmaDep;
		return atualizar();
	}
	
		
	public List<SelectItem> getCurriculosBySerieDependencia() {
		return curriculosBySerieDependencia;
		
	}

	public void setCurriculosBySerieDependencia(
			List<SelectItem> curriculosBySerieDependencia) {
		this.curriculosBySerieDependencia = curriculosBySerieDependencia;
	}
	public int getCurriculoSelecionado() {
		return curriculoSelecionado;
	}
	public void setCurriculoSelecionado(int curriculoSelecionado) {
		this.curriculoSelecionado = curriculoSelecionado;
	}
	public List<Integer> getIdsDisciplinasTurma() {
		return idsDisciplinasTurma;
	}
	public void setIdsDisciplinasTurma(List<Integer> idsDisciplinasTurma) {
		this.idsDisciplinasTurma = idsDisciplinasTurma;
	}
	public TurmaSerie getTurmaSerie() {
		return turmaSerie;
	}
	public void setTurmaSerie(TurmaSerie turmaSerie) {
		this.turmaSerie = turmaSerie;
	}
	public boolean isCadastroTurmaExistente() {
		return cadastroTurmaExistente;
	}
	public void setCadastroTurmaExistente(boolean cadastroTurmaExistente) {
		this.cadastroTurmaExistente = cadastroTurmaExistente;
	}

	
}

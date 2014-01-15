/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on 13/07/2009
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ead.EADDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.OrientacaoAtividadeDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.dao.prodocente.EstagioDao;
import br.ufrn.sigaa.arq.dao.prodocente.TrabalhoFimCursoDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.OrientacaoAtividade;
import br.ufrn.sigaa.ensino.dominio.RegistroAtividade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.Estagio;
import br.ufrn.sigaa.prodocente.atividades.dominio.TeseOrientada;
import br.ufrn.sigaa.prodocente.atividades.dominio.TrabalhoFimCurso;

/** Controller respons�vel por opera��es de busca em orienta��o de atividades.
 * @author �dipo Elder F. Melo
 *
 */
@SuppressWarnings("serial")
@Component("orientacaoAtividade")
@Scope("request")
public class OrientacaoAtividadeMBean extends SigaaAbstractController<OrientacaoAtividade> {

	/** Indica se � poss�vel informa o orientador no formul�rio. */
	private boolean escolheOrientador;
	
	/** Indica se filtra a busca pelo nome do discente. */
	private boolean filtroDiscente;
	/** Indica se filtra a busca pelo componente curricular. */
	private boolean filtroComponente;
	/** Indica se filtra a busca pelo ano/per�odo. */
	private boolean filtroAnoPeriodo;
	/** Indica se filtra a busca pelo orientador. */
	private boolean filtroOrientador;
	/** Indica se filtra a busca pela situa��o da matr�cula no componente (aprovado/reprovado). */
	private boolean filtroResultado;
	
	/** Discente ao qual a busca se restringe. */
	private Discente discente;
	/** Componente Curricular ao qual a busca se restringe. */
	private ComponenteCurricular componenteCurricular;
	/** Ano ao qual a busca se restringe. */
	private int ano;
	/** Per�odo ao qual a busca se restringe. */
	private int periodo;
	/** Orientador ao qual a busca se restringe. */
	private Servidor orientador;
	/** Lista de situa��es de matr�cula no componente (aprovado/reprova) ao qual a busca se restringe. */
	private Collection<Integer> resultados;
	
	/** Lista de atividades que o usu�rio pode utilizar para restringir a busca. */
	private Collection<ComponenteCurricular> listaAtividades;
	/** Cole��o de orienta��es encontradas.*/
	private Collection<OrientacaoAtividade> listaOrientacoesEncontradas;

	/** Trabalho de fim de curso do discente. */
	private TrabalhoFimCurso trabalhoFimCurso;
	
	/** Lista de docentes do departamento. */
	private Collection<Servidor> docentes;
	
	/** Cole��o de est�gios do discente. */
	private Collection<Estagio> estagios;

	/** Tese orientada pelo discente. */
	private TeseOrientada teseOrientada;
	
	/** Construtor padr�o. */
	public OrientacaoAtividadeMBean() {
		initObj();
	}
	
	/** Inicia a busca por orienta��o de atividades.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/consultas.jsp</li>
	 * <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciarBusca() throws SegurancaException, DAOException {
		initObj();
		
		if (isUserInRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.COORDENADOR_GERAL_EAD)) {
			// caso seja usu�rio DAE, escolhe o orientador no formul�rio de busca
			iniciaDAE();			
		} else if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.CHEFE_DEPARTAMENTO)) {
			// caso seja coordenador ou secret�rio de coordena��o
			iniciarCoordenacao();
		} else if (isUserInRole(SigaaPapeis.DOCENTE)) {
			// caso seja docente, o orientador � o pr�prio
			iniciaDocente();
		} else {
			// caso nenhuma op��o acima, erro de seguran�a.
			throw new SegurancaException();
		}
		return forward(getListPage());
	}

	/** Inicia os atributos referentes � busca realizada pelo docente. 
	 * @throws DAOException */
	private void iniciaDocente() throws DAOException {
		this.orientador = getUsuarioLogado().getServidorAtivo();
		this.escolheOrientador = false;
		this.filtroOrientador = true;
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		this.listaAtividades = dao.findAtividades('G', null, null, null, null);
		
	}

	/** Inicia os atributos referentes � busca realizada pelo DAE. 
	 * @throws DAOException */
	private void iniciaDAE() throws DAOException {
		this.orientador = new Servidor();
		this.escolheOrientador = true;
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		this.listaAtividades = dao.findAtividades('G', null, null, null, null);
	}

	/** Inicia os atributos referentes � busca realizada pela coordena��o do curso. 
	 * @throws DAOException */
	private void iniciarCoordenacao() throws DAOException {
		this.orientador = new Servidor();
		this.escolheOrientador = true;
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		this.listaAtividades = dao.findAtividades('G', null, null, null, null);
	}	
	
	/** 
	 * Inicializa os atributos deste controller.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>N�o � chamado por JSP</li>
	 * </ul>
	 */
	public void initObj() {
		this.filtroDiscente = false;
		this.filtroComponente = false;
		this.filtroAnoPeriodo = false;
		this.filtroOrientador = false;
		this.filtroResultado = false;
		this.escolheOrientador = false;
		
		this.discente = new Discente();
		this.componenteCurricular = new ComponenteCurricular();
		this.ano =(short) CalendarUtils.getAnoAtual();
		this.periodo = (byte) getPeriodoAtual();
		this.orientador = new Servidor();
		this.resultados = new ArrayList<Integer>(); 
		
		this.listaAtividades = new ArrayList<ComponenteCurricular>();
		this.listaOrientacoesEncontradas = new ArrayList<OrientacaoAtividade>();
	}
	
	/** Realiza a busca por orienta��o de atividades.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/orientacao_atividade/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws Exception {
		Integer idOrientador = null;
		String nomeDiscente = null;
		Integer ano = null, periodo = null;
		Integer idComponente = null;
		Collection<Integer> situacoesMatricula = null;
		
		int paramCount = 0;
		
		// par�metros da busca
		if (escolheOrientador) {
			if (filtroOrientador && this.orientador.getId() != 0) {
				idOrientador = this.orientador.getId();
				paramCount++;
			}
		} else {
			idOrientador = this.orientador.getId();
			paramCount++;
		}
		if (filtroDiscente && !this.discente.getNome().equals("")) {
			nomeDiscente = this.discente.getNome();
			paramCount++;
		}
		if (filtroComponente && this.componenteCurricular.getId() != 0) {
			idComponente = this.componenteCurricular.getId();
			paramCount++;
		}
		if (filtroAnoPeriodo && (this.ano != 0 && this.periodo != 0)) {
			ano = this.ano;
			periodo = this.periodo;
			paramCount++;
		}
		if (filtroResultado && !ValidatorUtil.isEmpty(resultados)) {
			paramCount++;
			// cast para integer
			situacoesMatricula = new ArrayList<Integer>();
			for (Object i : this.resultados)
				situacoesMatricula.add(Integer.parseInt(i.toString()));
		}
		
		if (paramCount < 1) {
			addMensagemErro("Utilize pelo menos um crit�rio para realizar a busca");
			return null;
		}
		
		// realiza a busca
		int idUnidade = 0;
		if (!isUserInRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.COORDENADOR_GERAL_EAD)) {
			if (getServidorUsuario() != null) {
				idUnidade = getServidorUsuario().getUnidade().getId();
			}
		}
		
		OrientacaoAtividadeDao dao = getDAO(OrientacaoAtividadeDao.class);
		if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO) && isPortalCoordenadorGraduacao()) {
			listaOrientacoesEncontradas = dao.findByOrientadorDiscenteComponente(idOrientador, nomeDiscente, idComponente, 
					ano, periodo, situacoesMatricula, null, isBuscaRegistroAtivEspecificas(),
					NivelEnsino.GRADUACAO,getCursoAtualCoordenacao().getId());
		}
		else {
			listaOrientacoesEncontradas = dao.findByOrientadorDiscenteComponente(idOrientador, nomeDiscente, idComponente, 
					ano, periodo, situacoesMatricula, idUnidade, isBuscaRegistroAtivEspecificas(), NivelEnsino.GRADUACAO,null);
		}
		
		if (listaOrientacoesEncontradas.isEmpty()) {
			addMensagemErro("N�o foram encontradas orienta��es de atividades com os crit�rios informados.");
		}
		
		// seta o nome do orientador para reexibi��o
		orientador = dao.refresh(orientador);
		return null;
	}
	
	/** Exibe um resumo dos dados da orienta��o da atividade.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/orientacao_atividade/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String visualizar() throws DAOException {
		this.estagios = null;
		this.trabalhoFimCurso = null;
		this.teseOrientada = null;
		this.obj = new OrientacaoAtividade();
		populateObj(true);
		if( obj != null && obj.getId() > 0){
			ComponenteCurricular componente = this.obj.getRegistroAtividade().getMatricula().getComponente();
			DiscenteAdapter discente = this.obj.getRegistroAtividade().getMatricula().getDiscente();
			Servidor servidor = this.obj.getOrientador() != null ? this.obj.getOrientador() : this.obj.getOrientadorExterno().getServidor();
			if (componente.isEstagio()) {
				Integer idServidor = ValidatorUtil.isNotEmpty(servidor) ? servidor.getId() : null;
				estagios = getDAO(EstagioDao.class).findByDiscenteServidor(discente.getId(), idServidor);
			} else if (componente.isTrabalhoConclusaoCurso()) {
				trabalhoFimCurso = getDAO(TrabalhoFimCursoDao.class).findByOrientando(discente);
			} else if (componente.isTese() || componente.isQualificacao()) {
				teseOrientada = getDAO(OrientacaoAcademicaDao.class).findTeseOrientadaByDiscenteOrientador(discente, servidor);
			}
		}else {
			int idRegistroAtividade = getParameterInt("idRegistroAtividade", 0);
			obj.setRegistroAtividade( getGenericDAO().findByPrimaryKey(idRegistroAtividade, RegistroAtividade.class) );
		}
		return forward(getViewPage());
	}
	
	/** Retorna uma cole��o de SelectItem de docentes da unidade.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getDocentes() throws DAOException {
		if (docentes == null) {
			docentes = new ArrayList<Servidor>();
			ServidorDao dao = getDAO(ServidorDao.class);
			try {
					if (getServidorUsuario() != null) {
						docentes.addAll(dao.findByDocente("", getServidorUsuario().getUnidade().getId()));
					}
					else {
						Servidor s = dao.findByPrimaryKey(getUsuarioLogado().getIdServidor(), Servidor.class);
						docentes.addAll(dao.findByDocente("", s.getUnidade().getId()));
					}
						
            		} finally {
            		    dao.close();
            		}
		}
		return toSelectItems(docentes, "id", "nome");
	}
	
	/** Retorna uma cole��o de SelectItem de docentes ead.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getDocentesEad() throws DAOException {
		if (docentes == null) {
			docentes = new ArrayList<Servidor>();
			EADDao dao = getDAO(EADDao.class);
			try {
			    docentes.addAll(dao.findAllDocentesEad());
			} finally {
			    dao.close();
			}
		}
		return toSelectItems(docentes, "id", "nome");
	}
	
	@Override
	public String getFormPage() {
		return "/ensino/orientacao_atividade/form.jsp";
	}
	
	@Override
	public String getViewPage() {
		return "/ensino/orientacao_atividade/view.jsp";
	}
	
	@Override
	public String getListPage() {
		return "/ensino/orientacao_atividade/lista.jsp";
	}
	
	/** Retorna uma cole��o de SelectItem de atividades.
	 * @return
	 */
	public Collection<SelectItem> getAtividadesCombo() {
		return toSelectItems(listaAtividades, "id", "descricao");
	}
	
	/** Retorna uma cole��o de SelectItem de Situa��es de matr�cula em componentes curriculares (aprovado/reprovado).
	 * @return
	 */
	public Collection<SelectItem> getResultadosCombo(){
		Collection<SelectItem> resultados = new ArrayList<SelectItem>();
		resultados.add(new SelectItem(SituacaoMatricula.APROVADO.getId(), SituacaoMatricula.APROVADO.getDescricao()));
		resultados.add(new SelectItem(SituacaoMatricula.REPROVADO.getId(), SituacaoMatricula.REPROVADO.getDescricao()));
		return resultados; 
	}

	/** Indica se � poss�vel informa o orientador no formul�rio. 
	 * @return Caso true, permite informar o orientador no formul�rio. 
	 */
	public boolean isEscolheOrientador() {
		return escolheOrientador;
	}

	/** Seta se � poss�vel informa o orientador no formul�rio. 
	 * @param escolheOrientador True, caso seja poss�vel informa o orientador no formul�rio. 
	 */
	public void setEscolheOrientador(boolean escolheOrientador) {
		this.escolheOrientador = escolheOrientador;
	}

	/** Indica se filtra a busca pelo nome do discente. 
	 * @return True, caso filtra a busca pelo nome do discente. 
	 */
	public boolean isFiltroDiscente() {
		return filtroDiscente;
	}

	/** Seta se filtra a busca pelo nome do discente. 
	 * @param filtroDiscente Caso true, filtra a busca pelo nome do discente. 
	 */
	public void setFiltroDiscente(boolean filtroDiscente) {
		this.filtroDiscente = filtroDiscente;
	}

	/** Indica se filtra a busca pelo componente curricular. 
	 * @return True, caso filtra a busca pelo componente curricular. 
	 */
	public boolean isFiltroComponente() {
		return filtroComponente;
	}

	/** Seta se filtra a busca pelo componente curricular. 
	 * @param filtroComponente Caso true, filtra a busca pelo componente curricular. 
	 */
	public void setFiltroComponente(boolean filtroComponente) {
		this.filtroComponente = filtroComponente;
	}

	/** Indica se filtra a busca pelo ano/per�odo. 
	 * @return True, caso filtra a busca pelo ano/per�odo. 
	 */
	public boolean isFiltroAnoPeriodo() {
		return filtroAnoPeriodo;
	}

	/** Seta se filtra a busca pelo ano/per�odo. 
	 * @param filtroAnoPeriodo Caso true, filtra a busca pelo ano/per�odo. 
	 */
	public void setFiltroAnoPeriodo(boolean filtroAnoPeriodo) {
		this.filtroAnoPeriodo = filtroAnoPeriodo;
	}

	/** Indica se filtra a busca pelo orientador. 
	 * @return True, caso filtra a busca pelo orientador. 
	 */
	public boolean isFiltroOrientador() {
		return filtroOrientador;
	}

	/** Seta se filtra a busca pelo orientador. 
	 * @param filtroOrientador Caso true, filtra a busca pelo orientador. 
	 */
	public void setFiltroOrientador(boolean filtroOrientador) {
		this.filtroOrientador = filtroOrientador;
	}

	/** Indica se filtra a busca pela situa��o da matr�cula no componente (aprovado/reprovado). 
	 * @return True, caso filtra a busca pela situa��o da matr�cula no componente (aprovado/reprovado). 
	 */
	public boolean isFiltroResultado() {
		return filtroResultado;
	}

	/** Indica se filtra a busca pela situa��o da matr�cula no componente (aprovado/reprovado). 
	 * @param filtroResultado Caso true, filtra a busca pela situa��o da matr�cula no componente (aprovado/reprovado). 
	 */
	public void setFiltroResultado(boolean filtroResultado) {
		this.filtroResultado = filtroResultado;
	}

	/** 
	 * Indica se a busca retornar� todas as atividades especificas, independente de haver orienta��o para a mesma.
	 * @return
	 */
	public boolean isBuscaRegistroAtivEspecificas() {
		return getParameterBoolean("regAtividadesEspecificas");
	}

	/** Retorna o Discente ao qual a busca se restringe. 
	 * @return Discente ao qual a busca se restringe. 
	 */
	public Discente getDiscente() {
		return discente;
	}

	/** Seta o Discente ao qual a busca se restringe. 
	 * @param discente Discente ao qual a busca se restringe. 
	 */
	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	/** Retorna o Componente Curricular ao qual a busca se restringe. 
	 * @return Componente Curricular ao qual a busca se restringe. 
	 */
	public ComponenteCurricular getComponenteCurricular() {
		return componenteCurricular;
	}

	/** Seta o Componente Curricular ao qual a busca se restringe. 
	 * @param componenteCurricular Componente Curricular ao qual a busca se restringe. 
	 */
	public void setComponenteCurricular(ComponenteCurricular componenteCurricular) {
		this.componenteCurricular = componenteCurricular;
	}

	/** Retorna o ano ao qual a busca se restringe. 
	 * @return Ano ao qual a busca se restringe. 
	 */
	public int getAno() {
		return ano;
	}

	/** Seta o ano ao qual a busca se restringe. 
	 * @param ano Ano ao qual a busca se restringe. 
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna o per�odo ao qual a busca se restringe. 
	 * @return Per�odo ao qual a busca se restringe. 
	 */
	public int getPeriodo() {
		return periodo;
	}

	/** Seta o per�odo ao qual a busca se restringe. 
	 * @param periodo Per�odo ao qual a busca se restringe. 
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	/** Retorna o orientador ao qual a busca se restringe. 
	 * @return Orientador ao qual a busca se restringe. 
	 */
	public Servidor getOrientador() {
		return orientador;
	}

	/** Seta o orientador ao qual a busca se restringe. 
	 * @param orientador Orientador ao qual a busca se restringe. 
	 */
	public void setOrientador(Servidor orientador) {
		this.orientador = orientador;
	}

	/** Retorna a lista de atividades que o usu�rio pode utilizar para restringir a busca. 
	 * @return Lista de atividades que o usu�rio pode utilizar para restringir a busca. 
	 */
	public Collection<ComponenteCurricular> getListaAtividades() {
		return listaAtividades;
	}

	/** Seta a lista de atividades que o usu�rio pode utilizar para restringir a busca. 
	 * @param listaAtividades Lista de atividades que o usu�rio pode utilizar para restringir a busca. 
	 */
	public void setListaAtividades(Collection<ComponenteCurricular> listaAtividades) {
		this.listaAtividades = listaAtividades;
	}

	/** Retorna a cole��o de orienta��es encontradas.
	 * @return Cole��o de orienta��es encontradas.
	 */
	public Collection<OrientacaoAtividade> getListaOrientacoesEncontradas() {
		return listaOrientacoesEncontradas;
	}

	/** Seta a cole��o de orienta��es encontradas.
	 * @param listaOrientacoesEncontradas Cole��o de orienta��es encontradas.
	 */
	public void setListaOrientacoesEncontradas(
			Collection<OrientacaoAtividade> listaOrientacoesEncontradas) {
		this.listaOrientacoesEncontradas = listaOrientacoesEncontradas;
	}

	/** Retorna a lista de situa��es de matr�cula no componente (aprovado/reprova) ao qual a busca se restringe. 
	 * @return Lista de situa��es de matr�cula no componente (aprovado/reprova) ao qual a busca se restringe. 
	 */
	public Collection<Integer> getResultados() {
		return resultados;
	}

	/** Seta a lista de situa��es de matr�cula no componente (aprovado/reprova) ao qual a busca se restringe. 
	 * @param resultados Lista de situa��es de matr�cula no componente (aprovado/reprova) ao qual a busca se restringe. 
	 */
	public void setResultados(Collection<Integer> resultados) {
		this.resultados = resultados;
	}

	/** Retorna o trabalho de fim de curso do discente. 
	 * @return Trabalho de fim de curso do discente. 
	 */
	public TrabalhoFimCurso getTrabalhoFimCurso() {
		return trabalhoFimCurso;
	}

	/** Seta o trabalho de fim de curso do discente. 
	 * @param trabalhoFimCurso Trabalho de fim de curso do discente. 
	 */
	public void setTrabalhoFimCurso(TrabalhoFimCurso trabalhoFimCurso) {
		this.trabalhoFimCurso = trabalhoFimCurso;
	}

	/** Retorna a cole��o de est�gios do discente.
	 * @return Cole��o de est�gios do discente. 
	 */
	public Collection<Estagio> getEstagios() {
		return estagios;
	}

	/** Seta a cole��o de est�gios do discente. 
	 * @param estagios Cole��o de est�gios do discente. 
	 */
	public void setEstagios(Collection<Estagio> estagios) {
		this.estagios = estagios;
	}

	/** Retorna a tese orientada pelo discente. 
	 * @return Tese orientada pelo discente. 
	 */
	public TeseOrientada getTeseOrientada() {
		return teseOrientada;
	}

	/** Seta a tese orientada pelo discente. 
	 * @param teseOrientada Tese orientada pelo discente. 
	 */
	public void setTeseOrientada(TeseOrientada teseOrientada) {
		this.teseOrientada = teseOrientada;
	}
}

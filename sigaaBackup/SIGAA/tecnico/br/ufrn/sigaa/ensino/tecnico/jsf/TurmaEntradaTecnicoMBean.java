package br.ufrn.sigaa.ensino.tecnico.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.tecnico.dao.CursoTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dao.TurmaEntradaTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.EspecializacaoTurmaEntrada;
import br.ufrn.sigaa.ensino.tecnico.dominio.EstruturaCurricularTecnica;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;

/**
 * Managed Bean respons�vel pelas opera��es b�sicas 
 * da Turma de Entrada T�cnica.
 * 
 * @author guerethes
 *
 */
@Component @Scope("request")
public class TurmaEntradaTecnicoMBean extends SigaaAbstractController<TurmaEntradaTecnico> {

	/** Constante utilizada na view que auxilia na consulta na busca por ano */ 
	private boolean filtroAno;
	/** Constante utilizada na view que auxilia na consulta na busca por curso */
	private boolean filtroCurso;
	/** Constante utilizada na view que auxilia na consulta na busca por especializa��o */
	private boolean filtroEspecializacao;
	
	/** Cole��o respons�vel pelo armazenamento da turma de entrada T�cnico */
	private Collection<TurmaEntradaTecnico> turmaEntradaTecnico = new ArrayList<TurmaEntradaTecnico>();
	/** Select Item respons�vel por carregar as Estruturas Curriculares T�cnico */
	private Collection<SelectItem> listaEstCurricularTecnico = new ArrayList<SelectItem>();
	
	public TurmaEntradaTecnicoMBean() {
		init();
	}

	/**
	 * Respons�vel por inicializar todos os atributos a serem utilizados na Turma de Entrada T�cnica
	 */
	private void init(){
		obj = new TurmaEntradaTecnico();
		obj.setEspecializacao(new EspecializacaoTurmaEntrada());
		listaEstCurricularTecnico = new ArrayList<SelectItem>();
	}

	/**
	 * Realizado cadastro de uma nova turma de Entrada.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/turmaEntrada/form.jsp
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		if (obj.getId() != 0) {
			setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
			prepareMovimento(ArqListaComando.ALTERAR);
		}
		if (obj.getEspecializacao() != null && obj.getEspecializacao().getId() == 0) 
			obj.setEspecializacao(null);
		obj.setUnidade(getUsuarioLogado().getVinculoAtivo().getUnidade());
		obj.setEstruturaCurricularTecnica(getGenericDAO().refresh(obj.getEstruturaCurricularTecnica()));
		return super.cadastrar();
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
	public void changeCurriculoCurso(ValueChangeEvent e) throws DAOException  {
		Integer idCurso = (Integer) e.getNewValue();
		obj.getCursoTecnico().setId(idCurso);
		getEstrCurriculoCurso(idCurso);
	}
	
	public String getEstrCurriculoCurso(int cursoTecnico) throws DAOException{
//		listaEstCurricularTecnico.clear();
		EstruturaCurricularTecnicoMBean mBean = new EstruturaCurricularTecnicoMBean();
//		Collection<SelectItem> lista = mBean.alterarCurriculoCurso(cursoTecnico); 
//		if (lista.size() > 0) {
//			listaEstCurricularTecnico = lista;
//		}
		listaEstCurricularTecnico = mBean.alterarCurriculoCurso(cursoTecnico);
		return forward(getFormPage());
	}
	
	/**
	 * M�todo respons�vel pela atualiza��o de uma turma de entrada T�cnico.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/turmaEntrada/lista.jsp</li>
	 * </ul>
     *
	 */
	@Override
	public String atualizar() throws ArqException {
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		try {
			int id = getParameterInt("id", 0);
			setObj(dao.findByPrimaryKey(id, TurmaEntradaTecnico.class));
			obj.setEstruturaCurricularTecnica(dao.findByPrimaryKey(obj.getEstruturaCurricularTecnica().getId(), EstruturaCurricularTecnica.class));
			if (obj != null) {
				EstruturaCurricularTecnicoMBean mBean = new EstruturaCurricularTecnicoMBean();
				listaEstCurricularTecnico = mBean.alterarCurriculoCurso(obj.getEstruturaCurricularTecnica().getCursoTecnico().getId());
			}
			if (obj.getEspecializacao() == null) 
				obj.setEspecializacao(new EspecializacaoTurmaEntrada());
			setConfirmButton("Alterar");
		} finally {
			dao.close();
		}
		return forward(getFormPage());
	}
	
	/**
	 * M�todo respons�vel pela realiza��o das buscas nas turma de entrada T�cnico.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/turmaEntrada/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String buscar() throws Exception {

		TurmaEntradaTecnicoDao dao = getDAO(TurmaEntradaTecnicoDao.class);
		try {
			turmaEntradaTecnico = new ArrayList<TurmaEntradaTecnico>();
			Integer ano = null;
			Integer curso = null;
			Integer especializacao = null;
			
			if (filtroAno && obj.getAnoReferencia() == null)
				erros.addErro("Ano: campo n�o informado");
			if (filtroCurso && obj.getEstruturaCurricularTecnica().getCursoTecnico().getId() == 0)
				erros.addErro("Curso: campo n�o informado");
			if (filtroEspecializacao && obj.getEspecializacao().getId() == 0)
				erros.addErro("Especializa��o: campo n�o informado");
			
			if (hasOnlyErrors())
				return null;
			
			if (filtroAno) 
				ano = obj.getAnoReferencia();
			if (filtroCurso) 
				curso = obj.getEstruturaCurricularTecnica().getCursoTecnico().getId();
			if (filtroEspecializacao) 
				especializacao = obj.getEspecializacao().getId();
			
			if (filtroAno || filtroCurso || filtroEspecializacao) 
				turmaEntradaTecnico = dao.findByAnoCursoEspecializacao(curso, especializacao, ano);
			else 
				turmaEntradaTecnico = dao.findAll(getUnidadeGestora(), getNivelEnsino(), null);
			
			if (turmaEntradaTecnico.size() == 0){ 
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		} finally {
			dao.close();
		}
		return forward(getListPage());
	}

	/**
	 * Verifica se h� realmente alguma Turma de Entrada, caso n�o haja ser� exibido para o usu�rio
	 * uma mensagem informando que a turma j� foi removida anteriormente.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/turmaEntrada/lista.jsp
	 */
	@Override
	public String inativar() throws ArqException, NegocioException {
		obj = getGenericDAO().findByPrimaryKey(getParameterInt("id", 0), TurmaEntradaTecnico.class);
		if (obj == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return forward(getListPage());
		}
		prepareMovimento(ArqListaComando.DESATIVAR);
		super.inativar();
		return listar();
	}
	
	/**
	 * Lista todas as turma de entrada do n�vel e da unidade gestora do usu�rio.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/curso.jsp</li>
	 * </ul>
	 */
	@Override
	public String listar() throws ArqException {
		TurmaEntradaTecnicoDao dao = getDAO(TurmaEntradaTecnicoDao.class);
		try {
			turmaEntradaTecnico = dao.findAll(getUnidadeGestora(), getNivelEnsino(), getPaginacao());
		} finally {
			dao.close();
		}
		return forward(getListPage());
	}

	/**
	 * Informa��o referente ao diret�rio que se encontra as jsp's.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>M�todo n�o Invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public String getDirBase() {
		return "/ensino/tecnico/turmaEntrada";
	}
	
	/**
	 * Retorna todas as Especializa��es cadastradas para a unidade gestora.
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/turmaEntrada/form.jsp</li>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/turmaEntrada/lista.jsp</li>
	 * </ul>
     *
     * turmaEntradaTecnicoMBean.allEspecializacao
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getAllEspecializacao() throws ArqException{
		return toSelectItems(getDAO(CursoTecnicoDao.class).
				findEspecializacoesParaTurmaEntrada(getUnidadeGestora()),"id", "descricao");
	}

	public boolean isFiltroAno() {
		return filtroAno;
	}

	public void setFiltroAno(boolean filtroAno) {
		this.filtroAno = filtroAno;
	}

	public boolean isFiltroCurso() {
		return filtroCurso;
	}

	public void setFiltroCurso(boolean filtroCurso) {
		this.filtroCurso = filtroCurso;
	}

	public boolean isFiltroEspecializacao() {
		return filtroEspecializacao;
	}

	public void setFiltroEspecializacao(boolean filtroEspecializacao) {
		this.filtroEspecializacao = filtroEspecializacao;
	}

	/**
	 * Retorna todas as Turmas de entrada T�cnico
	 * 
	 * <br>
	 * M�todo utilizado para informar os Editais de a��es associadas abertos
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/turmaEntrada/lista.jsp</li>
	 * </ul>
     *
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public Collection<TurmaEntradaTecnico> getTurmaEntradaTecnico() throws DAOException, ArqException {
		return turmaEntradaTecnico;
	}

	public void setTurmaEntradaTecnico(
			Collection<TurmaEntradaTecnico> turmaEntradaTecnico) {
		this.turmaEntradaTecnico = turmaEntradaTecnico;
	}

	public Collection<SelectItem> getListaEstCurricularTecnico() {
		return listaEstCurricularTecnico;
	}

	public void setListaEstCurricularTecnico(
			Collection<SelectItem> listaEstCurricularTecnico) {
		this.listaEstCurricularTecnico = listaEstCurricularTecnico;
	}

}
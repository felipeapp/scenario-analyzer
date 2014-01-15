package br.ufrn.sigaa.ensino.tecnico.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.dao.ensino.QualificacaoTecnicoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.tecnico.dao.ModuloDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;
import br.ufrn.sigaa.ensino.tecnico.dominio.ModuloQualificacao;
import br.ufrn.sigaa.ensino.tecnico.dominio.QualificacaoTecnico;

/**
 * Cont�m opera��es realizadas sobre as qualifica��es t�cnicos.
 *  
 * @author guerethes
 */
@Component @Scope("request")
public class QualificacaoTecnicoMBean extends SigaaAbstractController<QualificacaoTecnico> {

	/** Constante de visualiza��o */
	private static final String MODULO = "/modulo.jsp";
	
	/** Representa um m�dulo que far� parte de uma qualifica��o tecnica. */
	private Modulo modulo = new Modulo();
	/** Cole��o que armazena os m�dulos adicionados */
	private Collection<Modulo> modulosAdicionados = new ArrayList<Modulo>();
	/** Filtro de Curso utilizado na busca */
	private boolean filtroCurso;
	/** Filtro de Descri��o utilizado na busca */
	private boolean filtroDescricao;
	/** Campo que indica se e apenas visualiza��o */
	private boolean visualizar = true;
	
	/** Construtor padr�o */
	public QualificacaoTecnicoMBean() {
		init();
	}

	/** Realiza a inicializa��o dos valores utilizados */
	private void init() {
		obj = new QualificacaoTecnico();
	}

	/** Armazena o diret�rio que as view's est�o localizadas */
	@Override
	public String getDirBase() {
		return "/ensino/tecnico/qualificacao";
	}
	
	/**
	 * Submete as informa��es b�sicas da qualifica��o.
	 * 
     * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/qualificacao/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String submeterDadosBasicos() throws DAOException{
		erros.addAll(obj.validate());
		if (hasErrors()) 
			return null;
		obj.setCursoTecnico(getGenericDAO().refresh(obj.getCursoTecnico()));
		return forward(getDirBase() + MODULO);
	}

	/**
	 * Lista todos os m�dulos cadastrados para um determinado curso.
	 *  
     * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/qualificacao/modulo.jsp</li>
	 * </ul>
	 */
	public Collection<SelectItem> getAllModulosCursoTecnico() throws DAOException{
		return toSelectItems(getDAO(ModuloDao.class).findByCursoTecnico(obj.getCursoTecnico().getId()), "id", "descricao");
	}

	/**
	 * Adi��o de m�dulos na qualifica��o.
	 * 
     * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/qualificacao/modulo.jsp</li>
	 * </ul>
	 * @throws DAOException
	 */
	public void cadastrarModulo() throws DAOException {
		if (modulo.getId() != 0) {
			setModulo(getGenericDAO().refresh(modulo));
			if (modulosAdicionados.contains(modulo)) 
				addMensagemErro("M�dulo j� presente na qualifica��o.");
			else{
				modulosAdicionados.add(modulo);
				modulo = new Modulo();
			}
		}
	}

	/**
	 * Realiza uma busca pelos par�metros informados.
	 * 
     * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/qualificacao/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String buscar() throws Exception {
		Integer idCurso = null;
		String descricao = null;
		
		if (filtroCurso && obj.getCursoTecnico().getId() == 0) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
		
		if (filtroDescricao && obj.getDescricao().equals("")) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Descri��o");
		
		if (filtroCurso) 
			idCurso = obj.getCursoTecnico().getId();
		if (filtroDescricao) 
			descricao = obj.getDescricao();
		
		QualificacaoTecnicoDao qualifDao = getDAO(QualificacaoTecnicoDao.class);
		all = qualifDao.findByDescricaoCurso(descricao, idCurso, getUnidadeGestora(), getNivelEnsino());

		return forward(getListPage());
	}

	/**
	 * Remo��o de m�dulo cadastrado.
	 * 
     * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/qualificacao/modulo.jsp</li>
	 * </ul>
	 */
	public void removerModulo(){
		int id = getParameterInt("id", 0);
		if (id != 0) {
			for (Modulo mod : modulosAdicionados) {
				if (mod.getId() == id){ 
					modulosAdicionados.remove(mod);
					break;
				}
			}
		}
	}
	
	/**
	 * Realizar o cadastro de uma qualifica��o T�cnica.
	 *
     * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/qualificacao/modulo.jsp</li>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/qualificacao/view.jsp</li>
	 * </ul>
	 */
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		if (obj.getModulos().size() != 0) {
			for (Modulo mod : obj.getModulos()) {
				if (!modulosAdicionados.contains(mod)) 
					getGenericDAO().remove(mod);
			}
		}
		
		for (Modulo modulo : modulosAdicionados) {
				ModuloQualificacao mq = new ModuloQualificacao();
				mq.setModulo(modulo);
				mq.setQualificacao(obj);
				obj.getModuloQualificacoes().add(mq);
		}
		super.cadastrar();
		removeOperacaoAtiva();
		return redirectJSF(getSubSistema().getLink()); 
	}

	/**
	 * Carregar as informa��es da qualifica��o e direcionar o usu�rio para a tela de 
	 * altera��o.
	 * 
     * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/qualificacao/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public String atualizar() throws ArqException {
		setConfirmButton("Atualizar");
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		prepareMovimento(ArqListaComando.ALTERAR);
		carregar();
		return forward(getFormPage());
	}
	
	/**
	 * Realizar a remo��o de uma qualifica��o T�cnica.
	 * 
	 * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/qualificacao/modulo.jsp</li>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/qualificacao/view.jsp</li>
	 * </ul>
	 */
	@Override
	public String remover() throws ArqException {
		setOperacaoAtiva(ArqListaComando.REMOVER.getId());
		prepareMovimento(ArqListaComando.REMOVER);
		carregar();
		if (obj == null) 
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
		else
			super.remover();
		return forward(getListPage());
	}

	/**
	 * Carrega as informa��es da qualifica��o cujo id foi passado.
	 * 
	 * @throws DAOException
	 */
	private void carregar() throws DAOException {
		int id = getParameterInt("id", 0);
		if (id != 0) 
			setObj(getGenericDAO().findByPrimaryKey(id, QualificacaoTecnico.class));

		for (ModuloQualificacao mq : obj.getModuloQualificacoes()) 
			if (!modulosAdicionados.contains(mq.getModulo())) 
				modulosAdicionados.add(mq.getModulo());
	}
	
	/**
	 * Carrega para a tela de visualiza��o
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/qualificacao/lista.jsp
	 * 		/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/qualificacao/modulo.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String view() throws DAOException {
		carregar();
		if (modulosAdicionados.size() == 0) {
			addMensagemErro("� necess�rio informar pelo menos um m�dulo");
			return null;
		}
		if (getCurrentURL().contains("modulo")) 
			setVisualizar(true);
		else
			setVisualizar(false);
		return forward(getViewPage());
	}
	
	/**
	 * Direcionar o usuario para a tela do formul�rio.
	 * 
	 * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/qualificacao/modulo.jsp</li>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/qualificacao/view.jsp</li>
	 * </ul>
	 * @return
	 */
	public String dadosBasicos(){
		return forward(getFormPage());
	}
	
	public Modulo getModulo() {
		return modulo;
	}

	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}

	public Collection<Modulo> getModulosAdicionados() {
		return modulosAdicionados;
	}

	public void setModulosAdicionados(Collection<Modulo> modulosAdicionados) {
		this.modulosAdicionados = modulosAdicionados;
	}

	public boolean isFiltroCurso() {
		return filtroCurso;
	}

	public void setFiltroCurso(boolean filtroCurso) {
		this.filtroCurso = filtroCurso;
	}

	public boolean isFiltroDescricao() {
		return filtroDescricao;
	}

	public void setFiltroDescricao(boolean filtroDescricao) {
		this.filtroDescricao = filtroDescricao;
	}

	public boolean isVisualizar() {
		return visualizar;
	}

	public void setVisualizar(boolean visualizar) {
		this.visualizar = visualizar;
	}

}
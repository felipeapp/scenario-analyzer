/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 31/05/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.medio.dao.SerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;

/**
 * Classe de controle para as opera��es referentes ao objeto {@link Serie}. 
 * 
 * @author Rafael Gomes
 *
 */
@Component("serie") @Scope("request")
public class SerieMBean extends SigaaAbstractController<Serie>{
	
	/** Filtro da busca pelo c�digo */
	private boolean filtroNumero;
	/** Filtro da busca pelo Nome */
	private boolean filtroDescricao;
	/** Filtro da busca pelo Curso */
	private boolean filtroCurso;
	/** Collection que ir� armazenar a listagem das Series. */
	private Collection<Serie> listaSeries= new ArrayList<Serie>(); 
	/** Lista das s�ries por curso de ensino m�dio. */
	private List<SelectItem> seriesByCurso = new ArrayList<SelectItem>(0);
	
	/**
	 * Construtor padr�o
	 */
	public SerieMBean() {
		init();
	}
	
	/** Inicializando das vari�veis utilizadas */
	private void init(){
		obj = new Serie();
		obj.setCursoMedio(new CursoMedio());
	}
	
	/**
	 * Diret�rio que se encontra as view's
	 * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>M�todo n�o invocado por JSP</li>
	 * </ul>
	 */
	@Override
	public String getDirBase() {
		return "/medio/serie";
	}
	
	@Override
	public String atualizar() throws ArqException {
		checkChangeRole();
		setId();
		setObj(getGenericDAO().findByPrimaryKey(obj.getId(), Serie.class));

		setConfirmButton("Alterar");
		prepareMovimento(ArqListaComando.ALTERAR);
		
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		return forward(getFormPage());
	}
	
	/**
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/medio/serie/form.jsp</li>
	 * </ul>
	 * 
	 * M�todo respons�vel por Setar a opera��o ativa.
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkChangeRole();
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		return super.preCadastrar();
	}
	
	/**
	 * M�todo respons�vel pelo cadastro de uma S�rie de Ensino M�dio.
	 * 
     * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/serie/form.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		checkChangeRole();
		if (!checkOperacaoAtiva(ArqListaComando.CADASTRAR.getId(), ArqListaComando.ALTERAR.getId()))
			return cancelar();
		
		obj.setGestoraAcademica(new Unidade(getUnidadeGestora()));
		erros.addAll(obj.validate());
		
		if (getDAO(SerieDao.class).existeSerieByMesmoNumeroCurso(obj))
			addMensagemErro("N�mero: J� utilizado por outra S�rie do Curso.");
		
		if (hasErrors()) return null;
		
		super.cadastrar();
		removeOperacaoAtiva();
		return cancelar();  
	}

	@Override
	public void beforeRemover() throws DAOException {
		try {
			prepareMovimento(ArqListaComando.REMOVER);
			setOperacaoAtiva(ArqListaComando.REMOVER.getId());
			checkChangeRole();
			setId();
			obj = getGenericDAO().findByPrimaryKey(obj.getId(), Serie.class);
		} catch (ArqException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * M�todo respons�vel pela busca de s�ries de ensino M�dio.
	 * 
     * <br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/serie/lista.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String buscar() throws ArqException {
		
		SerieDao dao = getDAO(SerieDao.class);
		
		if (filtroNumero && isEmpty(obj.getNumero())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "N�mero de S�rie");
		
		if (filtroDescricao && isEmpty(obj.getDescricao())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Descri��o");
		
		if (filtroCurso && isEmpty(obj.getCursoMedio().getId())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
		
		if (hasOnlyErrors()) {
			listaSeries.clear();
			return null;
		}
	
		listaSeries = dao.findByDescricaoNumeroCurso(filtroDescricao ? obj.getDescricao() : null, 
				filtroNumero ? obj.getNumero() : null, filtroCurso ? obj.getCursoMedio() : null, getNivelEnsino());
		if (listaSeries.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		return forward(getListPage());
	}

	/** 
	 * Carrega as s�ries pertencentes ao curso de ensino m�dio selecionado na jsp..
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/medio/curriculo/form.jsp</li>
	 * </ul>
 	 * @param e
	 * @throws DAOException
	 */
	public void carregarSeriesByCurso(ValueChangeEvent e) throws DAOException {
		SerieDao dao = getDAO( SerieDao.class );
		
		CursoMedio cursoMedio = null;
		
		if( e != null )
			cursoMedio = dao.findByPrimaryKey((Integer)e.getNewValue(), CursoMedio.class);
		else if( !isEmpty( obj.getCursoMedio() ) )
			cursoMedio = dao.refresh(obj.getCursoMedio());
		else
			return;
		
		cursoMedio.setNivel(getNivelEnsino());
		if (cursoMedio != null){
			seriesByCurso = toSelectItems(dao.findByCurso(cursoMedio), "id", "descricaoCompleta");
		}
	}
	
	// Getters and Setters
	
	public boolean isFiltroNumero() {
		return filtroNumero;
	}
	public void setFiltroNumero(boolean filtroNumero) {
		this.filtroNumero = filtroNumero;
	}
	public boolean isFiltroDescricao() {
		return filtroDescricao;
	}
	public void setFiltroDescricao(boolean filtroDescricao) {
		this.filtroDescricao = filtroDescricao;
	}
	public boolean isFiltroCurso() {
		return filtroCurso;
	}
	public void setFiltroCurso(boolean filtroCurso) {
		this.filtroCurso = filtroCurso;
	}
	public Collection<Serie> getListaSeries() {
		return listaSeries;
	}
	public void setListaSeries(Collection<Serie> listaSeries) {
		this.listaSeries = listaSeries;
	}
	public List<SelectItem> getSeriesByCurso() {
		return seriesByCurso;
	}
	public void setSeriesByCurso(List<SelectItem> seriesByCurso) {
		this.seriesByCurso = seriesByCurso;
	}

	/** Verifica os pap�is: GESTOR_MEDIO, COORDENADOR_MEDIO.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> NENHUMA</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO);
	}
	
}
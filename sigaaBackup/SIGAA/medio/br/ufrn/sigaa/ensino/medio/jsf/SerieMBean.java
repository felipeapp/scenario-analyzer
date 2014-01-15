/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Classe de controle para as operações referentes ao objeto {@link Serie}. 
 * 
 * @author Rafael Gomes
 *
 */
@Component("serie") @Scope("request")
public class SerieMBean extends SigaaAbstractController<Serie>{
	
	/** Filtro da busca pelo código */
	private boolean filtroNumero;
	/** Filtro da busca pelo Nome */
	private boolean filtroDescricao;
	/** Filtro da busca pelo Curso */
	private boolean filtroCurso;
	/** Collection que irá armazenar a listagem das Series. */
	private Collection<Serie> listaSeries= new ArrayList<Serie>(); 
	/** Lista das séries por curso de ensino médio. */
	private List<SelectItem> seriesByCurso = new ArrayList<SelectItem>(0);
	
	/**
	 * Construtor padrão
	 */
	public SerieMBean() {
		init();
	}
	
	/** Inicializando das variáveis utilizadas */
	private void init(){
		obj = new Serie();
		obj.setCursoMedio(new CursoMedio());
	}
	
	/**
	 * Diretório que se encontra as view's
	 * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Método não invocado por JSP</li>
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
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> /sigaa.war/medio/serie/form.jsp</li>
	 * </ul>
	 * 
	 * Método responsável por Setar a operação ativa.
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkChangeRole();
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		return super.preCadastrar();
	}
	
	/**
	 * Método responsável pelo cadastro de uma Série de Ensino Médio.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemErro("Número: Já utilizado por outra Série do Curso.");
		
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
	 * Método responsável pela busca de séries de ensino Médio.
	 * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/serie/lista.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String buscar() throws ArqException {
		
		SerieDao dao = getDAO(SerieDao.class);
		
		if (filtroNumero && isEmpty(obj.getNumero())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Número de Série");
		
		if (filtroDescricao && isEmpty(obj.getDescricao())) 
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Descrição");
		
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
	 * Carrega as séries pertencentes ao curso de ensino médio selecionado na jsp..
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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

	/** Verifica os papéis: GESTOR_MEDIO, COORDENADOR_MEDIO.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
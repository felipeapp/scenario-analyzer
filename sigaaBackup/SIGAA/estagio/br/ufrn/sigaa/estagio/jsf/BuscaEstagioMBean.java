/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 20/10/2010
 */
package br.ufrn.sigaa.estagio.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.estagio.EstagiarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.estagio.dominio.Estagiario;
import br.ufrn.sigaa.estagio.dominio.StatusEstagio;
import br.ufrn.sigaa.estagio.dominio.TipoEstagio;

/**
 * Este MBean tem como finalidade de auxiliar nas operações relacionadas busca e Gerência de Estágio
 * 
 * @author Arlindo Rodrigues
 *
 */
@Component("buscaEstagioMBean") @Scope("request")
public class BuscaEstagioMBean extends SigaaAbstractController<Estagiario> {
	
	/** Lista de Estágios encontrados */
	private Collection<Estagiario> listaEstagios = new ArrayList<Estagiario>();
	
	/** Lista de Estágios encontrados */
	private Collection<Estagiario> listaEstagiosPendentes = new ArrayList<Estagiario>();	
	
	/** Atributos que auxiliam na busca, 
	 * identificando se será filtrado pelo discente informado */
	private boolean filtroDiscente;
	/** Atributos que auxiliam na busca, 
	 * identificando se será filtrado pelo concedente informado */
	private boolean filtroConcedente;
	/** Atributos que auxiliam na busca, 
	 * identificando se será filtrado pelo orientador informado */
	private boolean filtroOrientador;
	/** Atributos que auxiliam na busca, 
	 * identificando se será filtrado pelo curso informado */
	private boolean filtroCurso;
	/** Atributos que auxiliam na busca, 
	 * identificando se será filtrado pelo tipo de estágio informado */
	private boolean filtroTipoEstagio;
	/** Atributos que auxiliam na busca, 
	 * identificando se será filtrado pelo período informado */
	private boolean filtroPeriodo;
	
	/** Discente informado na busca */
	private String discente;
	/** Concedente Informado na busca */
	private String concedente;
	/** Orientador do discente no estágio informado na busca */
	private String orientador;
	/** Curso informado na busca */
	private Curso curso;

	/** Lista de estágios solicitado cancelamento encontrados */
	private Collection<Estagiario> listaEstagiosCancelamento;
	
	/** Construtor padrão */
	public BuscaEstagioMBean() {
		initObj();
	}
	
	/**
	 * Inicializa os Objetos
	 */
	private void initObj(){
		obj = new Estagiario();
		obj.setTipoEstagio(new TipoEstagio());
		curso = new Curso();
	}
	
	/**
	 * Iniciar a Busca de Estágios
	 * Método Chamado pela seguinte JSP
	 * <br/><br/>
	 * <ul>
	 *   <li>/sigaa.war/graduacao/menu_coordenador.jsp </li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String iniciar() throws HibernateException, DAOException{
		
		if (isPortalDiscente()){
			discente = getDiscenteUsuario().getMatricula().toString();
		} else if (isPortalDocente()){
			orientador = getServidorUsuario().getPessoa().getCpf_cnpjString();
		} 			
		
		if (isPortalDiscente() || isPortalDocente())
			return filtrar();
		else if (isPortalCoordenadorGraduacao()) {
			buscar();
			carregaEstagiosPendentes();
		}
		
		return forward(getListPage());
	}
	
	/** 
	 * Carrega os estágios pendentes de aprovação
	 * <br/><br/>
	 * Método não chamado por JSP. 
	 **/
	public void carregaEstagiosPendentes() throws HibernateException, DAOException{
		listaEstagiosPendentes = getEstagios(StatusEstagio.EM_ANALISE);
		listaEstagiosCancelamento = getEstagios(StatusEstagio.SOLICITADO_CANCELAMENTO);
	}
	
	/**
	 * Busca os Estágios conforme os filtros informados
	 * Método Chamado pela seguinte JSP
	 * <br/><br/>
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/lista.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String buscar() throws HibernateException, DAOException{
		if (!isSelecionaCurso()) {
			filtroCurso = true;
			curso = getCursoAtualCoordenacao();
		}
		if (!filtroCurso && !filtroDiscente && !filtroOrientador && 
				!filtroPeriodo && !filtroConcedente && !filtroTipoEstagio){
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			return null;
		}
		
		if (filtroDiscente && ValidatorUtil.isEmpty(discente)){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Discente");
			return null;
		}
		
		if (filtroConcedente && ValidatorUtil.isEmpty(concedente)){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Concedente");
			return null;
		}		
		
		if (filtroOrientador && ValidatorUtil.isEmpty(orientador)){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Orientador");
			return null;
		}				
				
		if (filtroPeriodo && (ValidatorUtil.isEmpty(obj.getDataInicio()) || ValidatorUtil.isEmpty(obj.getDataFim()))){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Período de Publicação");
			return null;
		}
		
		if (filtroTipoEstagio && (obj.getTipoEstagio() == null || obj.getTipoEstagio().getId() < 0)){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tipo do Estágio");
			return null;
		}						
		
		if (filtroCurso && !isPortalCoordenadorGraduacao() && (curso == null || curso.getId() < 0)){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
			return null;
		}				
		
		return filtrar();
	}
	
	/**
	 * Filtra a consulta conforme os parâmetros informados
	 * <br/><br/>
	 * Método não chamado por JSPs.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String filtrar() throws HibernateException, DAOException{
		EstagiarioDao dao = getDAO(EstagiarioDao.class);
		try {			
			listaEstagios = getEstagios(null);
			 
			carregaEstagiosPendentes();
			
			if (ValidatorUtil.isEmpty(listaEstagios) && ValidatorUtil.isEmpty(listaEstagiosPendentes)){
				if (isPortalDocente()){
					addMensagemErro("Você não possui nenhuma Orientação de Estágio.");
					return null;					
				} else if (isPortalDiscente()){
					addMensagemErro("Você não possui Estágios Cadastrados.");
					return null;					
				}				
			}
			
			// exclui duplicidade
			if (!isEmpty(listaEstagios)) {
				Iterator<Estagiario> iterator = listaEstagios.iterator();
				while (iterator.hasNext()) {
					Estagiario next = iterator.next();
					if (!isEmpty(listaEstagiosCancelamento))
						for (Estagiario estagio : listaEstagiosCancelamento)
							if (next.getId() == estagio.getId()) {
								iterator.remove(); continue;
							}
					if (!isEmpty(listaEstagiosPendentes))
						for (Estagiario estagio : listaEstagiosPendentes)
							if (next.getId() == estagio.getId()) {
								iterator.remove(); continue;
							}
				}
			}
			
			return telaBusca();				
		} finally {
			if (dao != null)
				dao.close();
		}			
	}
	
	/**
	 * Retorna os Estágios conforme os parâmetros informados
	 * @param status
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private Collection<Estagiario> getEstagios(Integer status) throws HibernateException, DAOException{
		
		if (isPortalCoordenadorGraduacao() && !isSelecionaCurso()) 
			curso = getCursoAtualCoordenacao();			
		
		EstagiarioDao dao = getDAO(EstagiarioDao.class);
		try {			
			return dao.findGeral(
					(filtroDiscente || isPortalDiscente() ? discente : null),
					(filtroConcedente || isPortalConcedenteEstagio() ? concedente : null),
					(isPortalConcedenteEstagio() ? getUsuarioLogado().getPessoa() : null),
					(filtroOrientador || isPortalDocente() ?  orientador : null),
					(filtroCurso || !isSelecionaCurso() ? curso.getId() : null),
					(filtroTipoEstagio ? obj.getTipoEstagio().getId() : null),
					(filtroPeriodo && obj.getDataInicio() != null ? obj.getDataInicio() : null),
					(filtroPeriodo && obj.getDataFim() != null ? obj.getDataFim() : null),
					status);
		} finally {
			if (dao != null)
				dao.close();
		}			
	}
	
	/**
	 * Redireciona para a tela de busca.
	 * <br/><br/>
	 * Método não chamado por JSPs.
	 * @return
	 */
	public String telaBusca(){
		return forward(getListPage());
	}
	
	/** Indica se o usuário poderá selecionar um ou mais cursos para a oferta de estágios
	 * @return
	 */
	public boolean isSelecionaCurso() {
		if (isPortalCoordenadorGraduacao()) {
			if (isUserInRole(SigaaPapeis.COORDENADOR_ESTAGIOS))
				return true;
			else 
				return false;
		} else {
			return true;
		}
	}
	
	@Override
	public String getListPage() {
		return "/estagio/estagio/lista.jsp";
	}

	public Collection<Estagiario> getListaEstagios() {
		return listaEstagios;
	}

	public void setListaEstagios(Collection<Estagiario> listaEstagios) {
		this.listaEstagios = listaEstagios;
	}

	public boolean isFiltroDiscente() {
		return filtroDiscente;
	}

	public void setFiltroDiscente(boolean filtroDiscente) {
		this.filtroDiscente = filtroDiscente;
	}

	public boolean isFiltroConcedente() {
		return filtroConcedente;
	}

	public void setFiltroConcedente(boolean filtroConcedente) {
		this.filtroConcedente = filtroConcedente;
	}

	public boolean isFiltroOrientador() {
		return filtroOrientador;
	}

	public void setFiltroOrientador(boolean filtroOrientador) {
		this.filtroOrientador = filtroOrientador;
	}

	public boolean isFiltroCurso() {
		return filtroCurso;
	}

	public void setFiltroCurso(boolean filtroCurso) {
		this.filtroCurso = filtroCurso;
	}

	public boolean isFiltroTipoEstagio() {
		return filtroTipoEstagio;
	}

	public void setFiltroTipoEstagio(boolean filtroTipoEstagio) {
		this.filtroTipoEstagio = filtroTipoEstagio;
	}

	public boolean isFiltroPeriodo() {
		return filtroPeriodo;
	}

	public void setFiltroPeriodo(boolean filtroPeriodo) {
		this.filtroPeriodo = filtroPeriodo;
	}

	public String getDiscente() {
		return discente;
	}

	public void setDiscente(String discente) {
		this.discente = discente;
	}

	public String getConcedente() {
		return concedente;
	}

	public void setConcedente(String concedente) {
		this.concedente = concedente;
	}

	public String getOrientador() {
		return orientador;
	}

	public void setOrientador(String orientador) {
		this.orientador = orientador;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public Collection<Estagiario> getListaEstagiosPendentes() {
		return listaEstagiosPendentes;
	}

	public void setListaEstagiosPendentes(
			Collection<Estagiario> listaEstagiosPendentes) {
		this.listaEstagiosPendentes = listaEstagiosPendentes;
	}

	public Collection<Estagiario> getListaEstagiosCancelamento() {
		return listaEstagiosCancelamento;
	}

	public void setListaEstagiosCancelamento(
			Collection<Estagiario> listaEstagiosCancelamento) {
		this.listaEstagiosCancelamento = listaEstagiosCancelamento;
	}
}

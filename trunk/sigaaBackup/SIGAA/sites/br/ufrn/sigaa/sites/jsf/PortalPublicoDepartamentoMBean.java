/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/11/2008
 *
 */
package br.ufrn.sigaa.sites.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.sites.dao.PortalPublicoDao;
import br.ufrn.sigaa.sites.dominio.TipoPortalPublico;

/**
 * MBean que controla o acesso aos dados do portal público dos departamentos da
 * UFRN
 * 
 * @author Mário Rizzi
 */
@Component("portalPublicoDepartamento") @Scope("request")
public class PortalPublicoDepartamentoMBean extends AbstractControllerPortalPublico {


	private Collection<Unidade> departamentos;
	
	private Collection<ComponenteCurricular> componentes;
	
	private Collection<ProjetoPesquisa>  pesquisas;
	
	private Collection<ProjetoEnsino>  monitorias;
	
	private Collection<AtividadeExtensao>  extensoes;
	
	private Collection<Servidor> docentes;
	
	/**
	 * Construtor padrão.
	 *  
	 * @throws ArqException
	 */
	public PortalPublicoDepartamentoMBean() throws ArqException {
		
		if(getUnidade() == null)
			setUnidade(new Unidade());
		
		if(getParameterInt("centro",0)>0){
			
			getUnidade().setId(getParameterInt("centro",0));
		}
	}
	
	/**
	 * Método responsável em reiniciar todos objetos envolvidos no portal.
	 * <br/>
	 * Chamado por
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/public/departamento/include/cabecalho.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String getIniciar() throws ArqException{
		
		// Seta o id passado pela URL ou no objeto isEmpty(getParameterInt("id", 0))?getId():
		int id = getParameterInt("id", 0);
		
		if(!isEmpty(id)) {
			// Popula objeto de acordo com o tipo do portal
			setUnidade(getGenericDAO().findByPrimaryKey(id,Unidade.class));
			setTipoPortalPublico(TipoPortalPublico.UNIDADE);
			getDetalhesSite();
			getRedirecionarSiteExterno();
		}
		
		if (!isEmpty(getUnidade())) {
			if (validaPortal(getUnidade())){
				componentes = null;
				pesquisas = null;
				extensoes = null;
				monitorias = null;
			}
		}
		
		return "";
	
	}
	
	/**
	 * Verifica se o portal pertence ao tipo acadêmico correto
	 * @param unidade
	 * @return
	 */
	private boolean validaPortal(Unidade unidade) {
		//Verifica se o portal que esta acessando é referente ao tipoUnidadeAcademico
		if(!isEmpty(unidade.getTipoAcademica()) && (unidade.getTipoAcademica().equals(TipoUnidadeAcademica.DEPARTAMENTO) 
				|| unidade.getTipoAcademica().equals(TipoUnidadeAcademica.CENTRO))) 
			return true;
		return false;
	}
	
	/**
	 * Retorna uma coleção de componentes curriculares de um departamento.
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/departamento/componentes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> getComponentesCurriculares()
			throws DAOException {
		if (isEmpty(componentes) && !isEmpty(getUnidade()))
			componentes = getDAO(ComponenteCurricularDao.class).findResumoComponentes(getUnidade());
		return componentes;
	}

	/**
	 * Buscar todos os docentes da unidade
	 * 
	 * <br/>
	 * Chamado por
	 * <ul>
	 * <li>sigaa.war/public/departamento/professores.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> getDocentes() throws DAOException {
		if(!isEmpty(getUnidade()) && docentes == null){
			ServidorDao docDao = getDAO(ServidorDao.class);
			docentes = docDao.findDocenteByUnidadeNome(getUnidade().getId(), null);
			populaPerfis();
		}
		return docentes;
	}
	
	/**
	 * Buscar todos os servidores que não são docentes associados ao departamento.
	 * 
	 * <br/>
	 * Chamado por
	 * <ul>
	 * <li>sigaa.war/public/departamento/administrativo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> getCorpoAdministrativo() throws DAOException {
		if(!isEmpty(getUnidade()) && docentes == null){
			ServidorDao docDao =  getDAO(ServidorDao.class);
			docentes = docDao.findGeral(getUnidade().getId(), null, Ativo.SERVIDOR_ATIVO, Categoria.TECNICO_ADMINISTRATIVO);
			populaPerfis();
		}
		return docentes;
	}
		
	
	/**
	 * Popula o perfil de cada servidor na coleção.
	 * @return
	 */
	private Collection<Servidor> populaPerfis(){
			
		Collection<Integer> idsPerfis = new ArrayList<Integer>();
		for (Servidor s : docentes) 
			if (s.getIdPerfil() != null)
				idsPerfis.add(s.getIdPerfil());

		Collection<PerfilPessoa> perfis = PerfilPessoaDAO.getDao().getByIds(idsPerfis);
		forServidor: for (Servidor s : docentes) {
			for (PerfilPessoa p : perfis) 
				if (s.getIdPerfil() != null
						&& s.getIdPerfil().equals(p.getId())) {
						s.getPessoa().setPerfil(p);
					continue forServidor;
				}
		}
		
		return docentes;
			
	}


	/**
	 * Projetos de pesquisa que a unidade participou
	 * 
	 * <br/>
	 * Chamado por
	 * <ul>
	 * <li>sigaa.war/public/departamento/pesquisa.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<ProjetoPesquisa> getProjetosPesquisa()
			throws DAOException {
		if(!isEmpty(getUnidade()) && isEmpty(pesquisas))
			pesquisas = getDAO(ProjetoPesquisaDao.class).findByUnidadeProjecEquipe(getUnidade(),
					TipoUnidadeAcademica.DEPARTAMENTO);
		return pesquisas;
	}

	/**
	 * Coleção de projetos de monitoria que a unidade participou.
	 * 
	 * <br/>
	 * Chamado por
	 * <ul>
	 * <li>sigaa.war/public/departamento/monitoria.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<ProjetoEnsino> getProjetosMonitoria() throws DAOException {
		if(!isEmpty(getUnidade()) && isEmpty(monitorias))
			 monitorias = getDAO(PortalPublicoDao.class).findByUnidadeProjecEquipe( getUnidade() );
		return monitorias;
	}

	/**
	 * Projetos de extensão que a unidade participou
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/departamento/extensao.jsp</li>
	 * </ul>
	 *  
	 * @return
	 * @throws DAOException
	 */
	public Collection<AtividadeExtensao> getProjetosExtensao()
			throws DAOException {
		
		// Listar somente as situações que estão valendo
		Integer[]  idSituacaoAtividade = new Integer[3];
		idSituacaoAtividade[0] = TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO;
		idSituacaoAtividade[1] = TipoSituacaoProjeto.EXTENSAO_CONCLUIDO;
		idSituacaoAtividade[2] = TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO;
		
		if(!isEmpty(getUnidade()) && isEmpty(extensoes))
			extensoes = getDAO(AtividadeExtensaoDao.class).filter(null, null, null, null, null, null, null, null,
					null, idSituacaoAtividade, getUnidade().getId(), null, null, null, null, null, null, null, null, null, null, null, null,
					false, null, null, null,null, null, false);
		
		return extensoes;
	}

	/**
	 * Realiza a consulta de todos os departamentos a partir da unidade
	 * tipo centro setada.
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/departamento/busca_departamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String buscarDepartamentosDetalhes() throws DAOException {

		Integer idUnidade = null;
		if( !isEmpty( getUnidade() ) )	
			idUnidade = getUnidade().getId();
			
			departamentos = getDAO(PortalPublicoDao.class).findUnidadeByNomeCentro(null, idUnidade, TipoUnidadeAcademica.DEPARTAMENTO);
	
			if ( isEmpty(departamentos) ) 
				addMensagemErro("Nenhum departamento foi encontrado de acordo com os critérios de busca informados");

			getCurrentRequest().setAttribute("departamentos", departamentos);
				
		return null;

	}
	
	/**
	 * Retorna o chefe do departamento
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/departamento/portal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Servidor> getChefesDepto() throws DAOException {
		if(!isEmpty(getUnidade())){
			ServidorDao servidorDao = getDAO(ServidorDao.class);
			return servidorDao.findChefesByDepartamento(getUnidade().getId());
		}else
			return null;
	}

	/**
	 * Retorna todos os departamentos conforme o centro passado. <br /><br />
	 * 
	 * Não é chamado por nenhuma jsp.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Unidade> getAllDepartamentos() throws DAOException {
		if (isEmpty(departamentos))
			departamentos = getGenericDAO().findByExactField(Unidade.class,
					"tipoAcademica", TipoUnidadeAcademica.DEPARTAMENTO, "asc",
					"gestora");
		return departamentos;
	}

	public Collection<ProjetoPesquisa> getPesquisas() {
		return pesquisas;
	}

	public void setPesquisas(Collection<ProjetoPesquisa> pesquisas) {
		this.pesquisas = pesquisas;
	}

	public Collection<ProjetoEnsino> getMonitorias() {
		return monitorias;
	}

	public void setMonitorias(Collection<ProjetoEnsino> monitorias) {
		this.monitorias = monitorias;
	}

	public Collection<AtividadeExtensao> getExtensoes() {
		return extensoes;
	}

	public void setExtensoes(Collection<AtividadeExtensao> extensoes) {
		this.extensoes = extensoes;
	}


}

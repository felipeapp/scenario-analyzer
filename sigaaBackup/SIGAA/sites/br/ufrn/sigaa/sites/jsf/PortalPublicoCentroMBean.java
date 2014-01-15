/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/04/2009
 *
 */
package br.ufrn.sigaa.sites.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.sigaa.arq.dao.pesquisa.GrupoPesquisaDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.dao.site.DetalhesSiteDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.portal.jsf.ConsultaPublicaCursosMBean;
import br.ufrn.sigaa.sites.dominio.TipoPortalPublico;

/**
 * Classe MBean que controla o acesso aos dados dos centros que serão exibidos na
 * página pública do SIGAA
 * 
 * @author Mário Rizzi
 */
@Component("portalPublicoCentro") @Scope("request")
public class PortalPublicoCentroMBean extends AbstractControllerPortalPublico{

	
	private List<Map<String,Object>> centros;
	
	private List<Map<String,Object>> unidadesEspecializadas;
	
	/** Método construtor */
	public PortalPublicoCentroMBean() throws ArqException{

		
	}
	
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
		
		if (!isEmpty(getUnidade()))
			if (!validaPortal(getUnidade()))
				cancelar();
		
		
		return "";
	
	}
	
	/**
	 * Verifica se o portal pertence ao tipo acadêmico correto 
	 * @param unidade
	 * @return
	 */
	private boolean validaPortal(Unidade unidade) {
		//Verifica se o portal que esta acessando é referente ao tipoUnidadeAcademico 
		if(unidade != null && unidade.getTipoAcademica() != null &&
				unidade.getTipoAcademica().equals(TipoUnidadeAcademica.CENTRO)) 
			return true;

		return false;
	}
	
	public String iniciarPortalPublico() throws ArqException{
		
		// Seta o id passado pela URL ou no objeto
		int id = isEmpty(getParameterInt("id", 0))?getId():getParameterInt("id", 0);
		
		if(!isEmpty(id)) {
			// Popula objeto de acordo com o tipo do portal
			setUnidade(getGenericDAO().findByPrimaryKey(id,Unidade.class));
			getDetalhesSite();
			getRedirecionarSiteExterno();
		}

		return getUrlOficial();
		
	}
	
	/**
	 * Retorna para página de busca dos centros
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/centro/include/menu.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String cancelar() {		
		return redirect("/sigaa/public/home.jsf");
	}
	
	/**
	 * Retorna todos centros apartir dos   
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/centro/busca_centro.jsp</li>
	 * </ul>
	 * 
	 */
	public List<Map<String, Object>> getCentros() throws ArqException {
		
		if(centros == null)
			centros = getDAO(DetalhesSiteDao.class).findAllCentro();
		
		return centros;
		
	}
	
	/**
	 * Retorna todos sites das unidades acadêmicas especializadas.
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/centro/busca_centro.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public List<Map<String, Object>> getUnidadesEspecializadas() throws DAOException {

		if(unidadesEspecializadas == null)
			unidadesEspecializadas = getDAO(DetalhesSiteDao.class).findAllUnidadesEspecializada();

		return unidadesEspecializadas;
		
	}

	/**
	 * Retorna para um variável programas todos os programas do centro
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/centro/lista_programas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String getProgramas() throws ArqException{
		
		PortalPublicoProgramaMBean ppgmBean  = getMBean("portalPublicoPrograma");
		
		if( !isEmpty( getUnidade() ) ){	
			ppgmBean.getUnidade().setNome("");
			ppgmBean.getUnidade().setGestora(getUnidade());
		}
		
		return ppgmBean.buscarProgramas();
				
	}
	
	/**
	 * Retorna o chefe do centro
	 *
	 * @return
	 * @throws ArqException 
	 */
	public Collection<Servidor> getDiretoresCentro() throws ArqException {
		if(!isEmpty(getUnidade())){
			ServidorDao servidorDao = getDAO(ServidorDao.class);
			Set<Servidor> servidores = CollectionUtils.toHashSet(servidorDao.findDiretoresByCentro(getUnidade().getId()));
			return servidores;
		}else
			return null;
	}
	
	/**
	 * Retorna para uma variável departamentos todos os departamentos do centro
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/centro/lista_departamentos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String getDepartamentos() throws ArqException{
		
		PortalPublicoDepartamentoMBean pdmBean  = getMBean("portalPublicoDepartamento");
		if( !isEmpty( getUnidade() ) ){	
			pdmBean.setUnidade( new Unidade() );
			pdmBean.setUnidade( getUnidade() );
		}
		return pdmBean.buscarDepartamentosDetalhes();
				
	}
	
	/**
	 * Retorna para uma variável cursos todos os cursos de graduação do centro
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/centro/lista_cursos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String getCursos() throws ArqException{
		
		if( !isEmpty( getUnidade() ) ){		
			
			ConsultaPublicaCursosMBean cpcBean  = getMBean("consultaPublicaCursos");
			cpcBean.setPrograma(getUnidade());
			cpcBean.buscarCursos();
			getCurrentRequest().setAttribute("cursos", cpcBean.getCursos());
			
		}
		
		return "";
				
	}
	
	/**
	 * Retorna para um variável com todos as bases de pesquisa do centro
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * 	<li>sigaa.war/public/centro/bases_pesquisa.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String getBasesPesquisa() throws ArqException{
		GrupoPesquisaDao dao = getDAO(GrupoPesquisaDao.class);

		if( !isEmpty( getUnidade() ) ){		
			
			Collection<GrupoPesquisa> lista = dao.findOtimizado(getUnidade().getId(), null,
				0, null, null, true, null);
			
			if (!lista.isEmpty())
				getCurrentRequest().setAttribute("bases",lista);
			else
				addMessage("Nenhuma base de pesquisa foi encontrada para o centro.", TipoMensagemUFRN.WARNING);
			
		}
		
		return "";	
	}

	public void setCentros(List<Map<String, Object>> centros) {
		this.centros = centros;
	}
	
		
}

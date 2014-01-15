/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 28/10/2009
 */
package br.ufrn.comum.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.faces.model.SelectItem;
import org.springframework.stereotype.Component;
import br.ufrn.arq.dao.UnidadeDAOImpl;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ArrayUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.comum.dominio.UnidadeGeral;

/**
 * Managed Bean para possibilitar a criação de autocompletes
 * de unidades.
 * 
 * @author David Pereira
 *
 */
@Component
public class UnidadeAutoCompleteMBean extends AbstractController {

	/** Atributos que auxiliam consultas no autocomplete */
	private Boolean organizacional; //Para trazer apenas unidades organizacionais
	private Boolean orcamentaria; //Para trazer apenas unidades orçamentária
	private Boolean gestoras; //Para trazer apenas unidades gestoras
	private Boolean patrimonial; //Para trazer apenas unidades patrimoniais
	private Integer idResponsavelOrcamentaria; //Para trazer filhas orçamentárias da unidade com este id, considerando hierarquia
	private Integer idResponsavelOrganizacional; //Para trazer filhas organizacionais da unidade com este id, considerando hierarquia
	
	
	public UnidadeAutoCompleteMBean(){
		organizacional = false;
		orcamentaria = false;
		gestoras = false;
		patrimonial = false;
		
		idResponsavelOrcamentaria = null;
		idResponsavelOrganizacional = null;
	}
	
	/**
	 * Autocomplete utilizado pelo suggestion box do richfaces.
	 * A pessoa pode digitar o código da unidade ou o nome.
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public List<UnidadeGeral> autocompleteNomeUnidade(Object event) throws DAOException {
		
		String nome = event.toString(); //Nome da unidade digitada no autocomplete
		Long codigo = StringUtils.getNumerosIniciais(nome);
				
		if (codigo != null){
			nome = null; //consulta pelo código apenas
		}
		
      Integer idResponsavelOrcamentaria = getParameterInt("idResponsavelOrcamentaria");
      Boolean gestoras = getParameterBoolean("gestoras");
      Boolean orcamentaria = getParameterBoolean("orcamentaria");
      String parametroResponsavelOrganizacional = getParameter("idResponsavelOrganizacional");
      Boolean organizacional = null;
      String organizacionalStr = getParameter("organizacional");
      if (organizacionalStr != null)
         organizacional = Boolean.valueOf(organizacionalStr);
      
      Boolean patrimonial = null;
      String patrimonialStr = getParameter("patrimonial");
      if (patrimonialStr != null)
         patrimonial = Boolean.valueOf(patrimonialStr);

      Integer[] idsResponsavelOrganizacional = null;
      if (!isEmpty(parametroResponsavelOrganizacional) && !"null".equalsIgnoreCase(parametroResponsavelOrganizacional)) {
         idsResponsavelOrganizacional = ArrayUtils.toIntArray(parametroResponsavelOrganizacional.split(";"));
      }

      if (!gestoras)
         gestoras = null;
		
		if(!orcamentaria)
			orcamentaria = null;
		
		List<UnidadeGeral> unidades = getUnidades(null,codigo, nome, idResponsavelOrcamentaria, idsResponsavelOrganizacional, orcamentaria, organizacional, gestoras, patrimonial, null, null);
		
		return unidades;
	}
	
	/**
	 * Autocomplete utilizado pelo suggestion box do richfaces.
	 * A pessoa pode digitar o código da unidade ou o nome.
	 * Busca apenas unidades que possuem código de SIAPECAD.
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public List<UnidadeGeral> autocompleteNomeUnidadeComSiapecad(Object event) throws DAOException {
		
		String nome = event.toString(); //Nome da unidade digitada no autocomplete
		Long codigo = StringUtils.getNumerosIniciais(nome);
				
		if (codigo != null){
			nome = null; //consulta pelo código apenas
		}
		
		return getUnidades(null, codigo, nome, null, null, null, null, null, null, null, true);
	}
	
	/**
	 * Consulta unidades na base de dados conforme um conjunto de parâmetros.
	 * @param codigo
	 * @param nomeLike
	 * @param idResponsavelOrcamentaria
	 * @param idResponsavelOrganizacional
	 * @param orcamentarias
	 * @param organizacionais
	 * @param gestoras
	 * @return
	 * @throws DAOException
	 */
	private List<UnidadeGeral> getUnidades(Long codigoSiapecad, Long codigo, String nomeLike, Integer idResponsavelOrcamentaria, Integer[] idResponsavelOrganizacional, 
			Boolean orcamentarias, Boolean organizacionais, Boolean gestoras, Boolean patrimonial, Boolean protocolizadora, Boolean siapecad) throws DAOException {
		
		UnidadeDAOImpl unidDAO = getDAO(UnidadeDAOImpl.class);
		List<UnidadeGeral> result = null;
		
		try {
			result = (List<UnidadeGeral>)
				unidDAO.findByInformacoes(codigoSiapecad,codigo, nomeLike, idResponsavelOrcamentaria, idResponsavelOrganizacional, orcamentarias, organizacionais, gestoras, patrimonial, protocolizadora, siapecad);
		} finally {
			unidDAO.close();
		}
		
		return result;
	}

	public Boolean getOrganizacional() {
		return organizacional;
	}

	public void setOrganizacional(Boolean organizacional) {
		this.organizacional = organizacional;
	}

	public Boolean getOrcamentaria() {
		return orcamentaria;
	}

	public void setOrcamentaria(Boolean orcamentaria) {
		this.orcamentaria = orcamentaria;
	}

	public Boolean getGestoras() {
		return gestoras;
	}

	public void setGestoras(Boolean gestoras) {
		this.gestoras = gestoras;
	}

	public Boolean getPatrimonial() {
		return patrimonial;
	}

	public void setPatrimonial(Boolean patrimonial) {
		this.patrimonial = patrimonial;
	}

	public Integer getIdResponsavelOrcamentaria() {
		return idResponsavelOrcamentaria;
	}

	public void setIdResponsavelOrcamentaria(Integer idResponsavelOrcamentaria) {
		this.idResponsavelOrcamentaria = idResponsavelOrcamentaria;
	}

	public Integer getIdResponsavelOrganizacional() {
		return idResponsavelOrganizacional;
	}

	public void setIdResponsavelOrganizacional(Integer idResponsavelOrganizacional) {
		this.idResponsavelOrganizacional = idResponsavelOrganizacional;
	}
	
	/**
	 *  Método utilizado para popular o combo com siglas das unidades gestoras
	 * */
	public Collection<SelectItem> getAllSiglasGestoras() throws DAOException{
		UnidadeDAOImpl uniDao = getDAO(UnidadeDAOImpl.class);
		ArrayList<UnidadeGeral> unidades = (ArrayList<UnidadeGeral>) uniDao.findByInformacoes(null, null, null, null, null, true, null, true, null, null, null);
		return toSelectItems(unidades, "id", "sigla");
	}
	
}

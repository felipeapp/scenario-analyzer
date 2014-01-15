package br.ufrn.arq.web.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.component.html.HtmlOutputText;

import br.ufrn.arq.dao.UnidadeDAOImpl;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.Unidade;
import br.ufrn.comum.dominio.UnidadeGeral;

/**
 * Classe com métodos para busca de unidades e criação
 * da hierarquia de unidades no componente da árvore de unidades.
 * 
 * @author Itamir Filho
 *
 */
public class ArvoreUnidade extends AbstractController {

	private HtmlOutputText output;

	private Unidade unidade;

	// Como este MBean está no escopo da aplicação, é necessário que a arvore
	// esteja em um atributo, pois assim se evita de estar construindo a árvore
	// sempre que for preciso, ela só é construida uma única vez.
	private Collection<Unidade> unidades;


	private Collection<Unidade> unidadesLotacao;

	// ------------------------------------//

	// O tipo de Unidade: UnidadeOrganizacional, UnidadeAcademica,
	// UnidadeOrcamentaria
	public static int UNIDADE_ORGANIZACIONAL = 1;

	public static int UNIDADE_ACADEMICA = 2;

	public static int UNIDADE_ORCAMENTARIA = 3;

	public boolean APENAS_UNIDADES_DE_LOTACAO = true;

	private int tipoUnidade;

	private String camposJSP;

//	private static ArrayList<Integer> unidadeMapaFerias;
//	private static ArrayList<String> nomeUnidadeMapaFerias;

	public void setOutput(HtmlOutputText output) {
		this.output = output;
	}

	public HtmlOutputText getOutput() {
		return output;
	}

	public String getArvoreUnidadesDaUnidadeRaiz() throws DAOException {
		Map<String, Object> attributes = output.getAttributes();

		Integer idUnidadeRaiz = null;

		if (!isEmpty(attributes) && attributes.containsKey("idUnidadeRaiz")) {
			idUnidadeRaiz = Integer.parseInt(attributes.get("idUnidadeRaiz").toString());
		}else{
			idUnidadeRaiz = (Integer) FacesHelper.getValueExpression(output.getValueExpression("idUnidadeRaiz"));
		}

		if (!isEmpty(idUnidadeRaiz)) {
			return getArvoreUnidadeDaUnidadeRaiz(idUnidadeRaiz, UNIDADE_ORGANIZACIONAL);
		} else {
			return getArvoreUnidadeOrganizacional();
		}
	}

	public String getArvoreUnidadeDaUnidadeRaiz(int idUnidadeRaiz, int tipoUnidade) throws DAOException {
		carregaUnidades();

		StringBuffer arvore = new StringBuffer("");
		UnidadeDAOImpl uniDao = new UnidadeDAOImpl(Sistema.COMUM);

		Unidade unidadeRaiz = uniDao.findByPrimaryKey(idUnidadeRaiz, Unidade.class);

		arvore.append("<ul id=\"arvoreUnidade\">");
		arvore.append(getUnidadesFilhas(unidadeRaiz, tipoUnidade));
		arvore.append("</ul>");

		return arvore.toString();
	}

	public String getArvoreUnidadesDisponiveis() throws DAOException {
		Map<String, Object> attributes = output.getAttributes();

		boolean lotacao = false;

		if (attributes != null && attributes.size() > 0) {
			if (attributes.containsKey("lotacao")) {
				lotacao = Boolean.parseBoolean(attributes.get("lotacao").toString());
			}
		}

		if (lotacao) {
			return getArvoreUnidadeOrganizacionalLotacao();
		} else {
			return getArvoreUnidadeOrganizacional();
		}
	}

	public int getTipoUnidade() {
		return this.tipoUnidade;
	}

	public void setTipoUnidade(int tipoUnidade) {
		this.tipoUnidade = tipoUnidade;
	}

	public void carregaUnidades() throws DAOException {
		UnidadeDAOImpl uniDao = new UnidadeDAOImpl(Sistema.COMUM);
		unidades = uniDao.findAllUnidadesOrganizacionais();
		unidadesLotacao = uniDao.findAllUnidadesOrganizacionaisLotacao(true, false);
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	// -----------------------------//

	// -------------------------//
	public ArvoreUnidade() {

	}

	public ArvoreUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public String getArvoreUnidadeAcademica() throws DAOException {
		carregaUnidades();
		return getArvoreUnidade(UNIDADE_ACADEMICA);
	}

	public String getArvoreUnidadeOrcamentaria() throws DAOException {
		carregaUnidades();
		return getArvoreUnidade(UNIDADE_ORGANIZACIONAL);
	}

	public String getArvoreUnidadeOrganizacional() throws DAOException {
		carregaUnidades();
		
		StringBuffer arvore = new StringBuffer("");
		UnidadeDAOImpl uniDao = new UnidadeDAOImpl(Sistema.COMUM);
		Unidade raiz = uniDao.findByPrimaryKey(UnidadeGeral.UNIDADE_DIREITO_GLOBAL, Unidade.class);

		arvore.append("<ul id=\"arvoreUnidade\" class=\"expanded\">");
		arvore.append(getUnidadesFilhas(raiz, UNIDADE_ORGANIZACIONAL));
		arvore.append("</ul>");

		return arvore.toString();
	}

	public String getArvoreUnidadeOrganizacionalInclusaoMeuSetor() throws DAOException {
		carregaUnidades();
		return getArvoreUnidadeMeuSetor(UNIDADE_ORGANIZACIONAL);
	}

	public String getArvoreUnidadeOrganizacionalLotacao() throws DAOException {
		carregaUnidades();
		return getArvoreUnidadeLotacao(UNIDADE_ORGANIZACIONAL);
	}

	public String getUnidadesFilhas(Unidade unidade, int tipoUnidade, Collection<Unidade> unidadesBusca)
			throws DAOException {

		Collection<Unidade> unidadesFilhas = new ArrayList<Unidade>();
		//if (!unidadesFilhas.contains(unidade))
		//	unidadesFilhas.add(unidade);
		
		unidade.setUnidadeSipac(true);

		// Buscando as unidades Organizacionais
		if (tipoUnidade == UNIDADE_ORGANIZACIONAL) {
			for (Unidade u : unidadesBusca) {
				if (u.getResponsavelOrganizacional().getId() == unidade.getId() && u.getId() != unidade.getId()) {
					unidadesFilhas.add(u);
				}
			} 
		} else if (tipoUnidade == UNIDADE_ORCAMENTARIA) {
			for (Unidade u : unidadesBusca) {
				if (u.getUnidadeResponsavel() != null && u.getUnidadeResponsavel().getId() == unidade.getId()) {
					unidadesFilhas.add(u);
				} 
			}
		} else if (tipoUnidade == UNIDADE_ACADEMICA) {
			for (Unidade u : unidadesBusca) {
				if (u.getUnidadeResponsavel().getId() == unidade.getId()) {
					unidadesFilhas.add(u);
				} 
			}
		}

		// base da recursão
		if (unidadesFilhas.size() == 0) {
			return "<li id=\"" + unidade.getId() + "\"><label>"
					+ unidade.getCodigoNome() + "</label><span>"
					+ unidade.getCodigoFormatado() + "</span></li>";
		} else {
			StringBuffer noStr = new StringBuffer(2000);
			noStr.append(" <li id=\"" + unidade.getId() + "\"><label>"
					+ unidade.getCodigoNome() + "</label><span>"
					+ unidade.getCodigoFormatado() + "</span><ul>");
			for (Unidade u : unidadesFilhas) {
				if (!u.isUnidadeSipac()) {
					noStr.append(getUnidadesFilhas(u, tipoUnidade));
				}
			}
			noStr.append("</ul> </li>");

			return noStr.toString();
		}

	}

	/**
	 * Procura as Unidades Filhas e sai Montando a arvore
	 */
	public String getUnidadesFilhas(Unidade unidade, int tipoUnidade) throws DAOException {
		return getUnidadesFilhas(unidade, tipoUnidade, unidades);
	}


	public String getUnidadesFilhasLotacao(Unidade unidade, int tipoUnidade) throws DAOException {
		return getUnidadesFilhas(unidade, tipoUnidade, unidadesLotacao);
	}

//	public void getUnidadesFilhasMapaFerias(Unidade unidade, int tipoUnidade)
//	throws DAOException {
//
//		Collection<Unidade> unidadesFilhas = new ArrayList<Unidade>();
//		unidade.setUnidadeSipac(true);
//
//		// Buscando as unidades Organizacionais
//		if (tipoUnidade == UNIDADE_ORGANIZACIONAL) {
//			for (Unidade u : unidades) {
//				// if (u.getUnidadeResponsavel().getId() == unidade.getId()) {
//				if (u.getResponsavelOrganizacional().getId() == unidade.getId()) {
//					unidadesFilhas.add(u);
//				}
//
//			}
//		}
//
//		// base da recursão
//		if (unidadesFilhas.size() == 0) {
//			unidadeMapaFerias.add(unidade.getId());
//			nomeUnidadeMapaFerias.add(unidade.getNome());
//
//		} else {
//
//			unidadeMapaFerias.add(unidade.getId());
//			nomeUnidadeMapaFerias.add(unidade.getNome());
//			for (Unidade u : unidadesFilhas) {
//				if (!u.isUnidadeSipac()) {
//					getUnidadesFilhasMapaFerias(u, tipoUnidade);
//				}
//			}
//		}
//
//	}

	/**
	 * Procura as Unidades Filhas e sai Montando a arvore
	 *
	 * public TreeNode getUnidadesFilhasFaces(Unidade unidade, int tipoUnidade)
	 * throws DAOException {
	 *
	 * Collection<Unidade> unidadesFilhas = new ArrayList<Unidade>();
	 * unidade.setUnidadeSipac(true);
	 *
	 * // Buscando as unidades Organizacionais if (tipoUnidade ==
	 * UNIDADE_ORGANIZACIONAL) { for (Unidade u : unidades) { //if
	 * (u.getUnidadeResponsavel().getId() == unidade.getId()) { if
	 * (u.getResponsavelOrganizacional().getId() == unidade.getId()) {
	 * unidadesFilhas.add(u); }
	 *
	 * } }
	 *
	 * // base da recursão if (unidadesFilhas.size() == 0) { TreeNode node = new
	 * TreeNodeImpl(); node.setData(unidade); return node; //return "<li id=\"" + unidade.getId() + "\">
	 * <label>" + unidade.getCodigoNome() + "</label><codigo>
	 * " + unidade.getCodigoFormatado() + "</codigo></li>"; } else {
	 * StringBuffer noStr = new StringBuffer(2000); TreeNode node = new
	 * TreeNodeImpl(); node.setData(unidade); noStr.append(" <li id=\"" + unidade.getId() + "\">
	 * <label>" + unidade.getCodigoNome() + "</label><codigo>
	 * " + unidade.getCodigoFormatado() + "</codigo>
	 * <ul>" ); for (Unidade u : unidadesFilhas) { if (!u.isUnidadeSipac()) {
	 * node.addChild(u, getUnidadesFilhas(u, tipoUnidade));
	 * ..noStr.append(getUnidadesFilhas(u, tipoUnidade)); } } noStr.append("
	 * </ul>
	 * </li>");
	 *
	 * return noStr.toString(); }
	 *
	 * }
	 */

	// -------//

	// --------------------------------------------------------------------------------------------------//
	// --------------------------------------------------------------------------------------------------//

	public String getArvoreUnidade(int tipoUnidade) throws DAOException {

		StringBuffer arvore = new StringBuffer("");
		UnidadeDAOImpl uniDao = new UnidadeDAOImpl(Sistema.COMUM);
		Collection<Unidade> CentrosEHospitais = uniDao.findGestoras();

		arvore.append("<ul id=\"arvoreUnidade\">");

		for (Unidade unidadeRaiz : CentrosEHospitais) {
			arvore.append(getUnidadesFilhas(unidadeRaiz, tipoUnidade));
		}
		arvore.append("</ul>");

		return arvore.toString();
	}

	public String getArvoreUnidadeLotacao(int tipoUnidade) throws DAOException {

		StringBuffer arvore = new StringBuffer("");
		UnidadeDAOImpl uniDao = new UnidadeDAOImpl(Sistema.COMUM);
		Collection<Unidade> CentrosEHospitais = uniDao.findGestoras();

		arvore.append("<ul id=\"arvoreUnidade\">");
		for (Unidade unidadeRaiz : CentrosEHospitais) {
			arvore.append(getUnidadesFilhasLotacao(unidadeRaiz, tipoUnidade));
		}
		arvore.append("</ul>");

		return arvore.toString();
	}

	public String getArvoreUnidadeMeuSetor(int tipoUnidade) throws DAOException {

		StringBuffer arvore = new StringBuffer("");
		UnidadeDAOImpl uniDao = new UnidadeDAOImpl(Sistema.COMUM);

		Unidade unidadeRaiz = uniDao.findByPrimaryKey(getUsuarioLogado()
				.getUnidade().getId(), Unidade.class);

		arvore.append("<ul id=\"arvoreUnidade\">");

		arvore.append(getUnidadesFilhas(unidadeRaiz, tipoUnidade));

		arvore.append("</ul>");

		return arvore.toString();
	}

//	public void getArvoreUnidadeMeuMapaFerias(int codigoUnidade,
//			int tipoUnidade, boolean incluirSubUnidades) throws DAOException {
//
//		carregaUnidades();
//
//		UnidadeDAOImpl uniDao = new UnidadeDAOImpl(Sistema.COMUM);
//		unidadeMapaFerias = new ArrayList<Integer>();
//		nomeUnidadeMapaFerias = new ArrayList<String>();
//
//		Unidade unidadeRaiz = uniDao.findByPrimaryKey(codigoUnidade,
//				Unidade.class);
//
//		if (incluirSubUnidades) {
//			getUnidadesFilhasMapaFerias(unidadeRaiz, tipoUnidade);
//		} else {
//			unidadeMapaFerias.add(unidadeRaiz.getId());
//			nomeUnidadeMapaFerias.add(unidadeRaiz.getNome());
//		}
//
//	}

	public String getCamposJSP() {
		return camposJSP;
	}

	public void setCamposJSP(String camposJSP) {
		this.camposJSP = camposJSP;
	}

	/**
	 * Autocomplete utilizado pelo suggestion box do richfaces.
	 *
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public List<Unidade> autocomplete(Object event) throws DAOException {
		
		Map<String, Object> attributes = null;
		
		if(output != null)
			attributes = output.getAttributes();

		Integer idUnidadeRaiz = null;

		if (attributes != null && attributes.size() > 0) {

			if (attributes.containsKey("idUnidadeRaiz")) {
				idUnidadeRaiz = Integer.parseInt(attributes.get("idUnidadeRaiz").toString());

			}else{
				idUnidadeRaiz = (Integer) FacesHelper.getValueExpression(output.getValueExpression("idUnidadeRaiz"));
			}
		}
		
		if (isEmpty(idUnidadeRaiz)) idUnidadeRaiz = 0;
		
		String nome = event.toString();
		UnidadeDAOImpl dao = new UnidadeDAOImpl(Sistema.COMUM);
		List<Unidade> result = null;
		try {
			result = dao.findByNome(idUnidadeRaiz, nome);
		} finally {
			dao.close();
		}
		return result;
	}
	
	public UnidadeGeral buscaUnidade(Integer idUnidade) {
		for (UnidadeGeral unidade : unidades) {
			if (unidade.getId() == idUnidade)
				return unidade;
		}
		return null;
	}

	public List<UnidadeGeral> getUnidades(String nome) {
		List<UnidadeGeral> result = new LinkedList<UnidadeGeral>();
		for (UnidadeGeral unidade : unidades) {
			if (StringUtils.toAsciiAndUpperCase(unidade.getNome()).contains(nome)) {
				result.add(unidade);
			}
		}
		return result;
	}

//	public ArrayList<Integer> getUnidadeMapaFerias() {
//		return unidadeMapaFerias;
//	}
//
//	public void setUnidadeMapaFerias(ArrayList<Integer> unidadeMapaFerias) {
//		this.unidadeMapaFerias = unidadeMapaFerias;
//	}
//
//	public ArrayList<String> getNomeUnidadeMapaFerias() {
//		return nomeUnidadeMapaFerias;
//	}
//
//	public void setNomeUnidadeMapaFerias(ArrayList<String> nomeUnidadeMapaFerias) {
//		this.nomeUnidadeMapaFerias = nomeUnidadeMapaFerias;
//	}
}

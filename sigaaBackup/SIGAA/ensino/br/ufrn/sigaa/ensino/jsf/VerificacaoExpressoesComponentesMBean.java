package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.expressao.ArvoreExpressao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/**
 * MBean respons�vel por verificar os componentes curriculares que possuem 
 * express�es inv�lidas
 * 
 * @author wendell
 *
 */
@Component("verificacaoExpressoesComponentesBean") @Scope("request")
//Suppress necess�rio por que nesse caso n�o h� forma de parametrizar a superclasse
@SuppressWarnings("unchecked")
public class VerificacaoExpressoesComponentesMBean extends SigaaAbstractController {

	/** Cole��o respons�vel por armazenar o mapa e componentes curriculares inv�lidos nos m�todos desta classe. */
	private	Collection<Map<String, Object>> componentesInvalidos = new ArrayList<Map<String, Object>>();
	
	/**
	 * Listar todos os componentes que possuem express�es inv�lidas
	 * 
	 * Chamado por:
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/cdp.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String listarInvalidos() throws ArqException {
		checkRole(SigaaPapeis.CDP, SigaaPapeis.PPG, SigaaPapeis.GESTOR_TECNICO);
		
		// Buscar componentes que possuem express�es
		ComponenteCurricularDao componenteDao = getDAO(ComponenteCurricularDao.class);
		Collection<ComponenteCurricular> componentes = componenteDao.findAllExpressoes(getNivelEnsino());
		
		if ( ValidatorUtil.isEmpty(componentes) ) {
			addMensagemWarning("Nenhum componente curricular foi encontrado");
		}
		
		// cria uma lista dos IDs dos componentes curriculares que ser�o consultados
		// durante a valida��o das express�es
		Set<Integer> listaIdComponente = new TreeSet<Integer>();
		for (ComponenteCurricular componente : componentes) {
			List<String> expressoes = new ArrayList<String>();
			if (!isEmpty(componente.getEquivalencia())) {
				expressoes.add(componente.getEquivalencia());
			}
			if (!isEmpty(componente.getPreRequisito())) {
				expressoes.add(componente.getPreRequisito());
			}
			if (!isEmpty(componente.getCoRequisito())) {
				expressoes.add(componente.getCoRequisito());
			}
			for (String expressao : expressoes) {
				StringBuffer integer = new StringBuffer();
				for (int i = 0; i < expressao.length(); i++) {
					if (org.apache.commons.lang.StringUtils.isNumeric(expressao.charAt(i)+"")) {
						integer.append(expressao.charAt(i));
					} else if (integer.length() > 0) {
						listaIdComponente.add(Integer.parseInt(integer.toString()));
						integer = new StringBuffer();
					}
				}
			}
		}
		// cria um mapa com os pares <idComponenteCurricular,
		// ComponenteCurricular> para ser utilizado como cache na valida��o
		Map<Integer, String> cacheComponente = componenteDao.criaCacheComponente(CollectionUtils.toList(listaIdComponente), true);
		
		// Verificar, para cada componente, se suas express�es s�o v�lidas
		componentesInvalidos = new ArrayList<Map<String, Object>>();
		for (ComponenteCurricular componente : componentes) {
			Map<String, Object> componenteInvalido = new HashMap<String, Object>();
			boolean invalido = false;

			// Analisar equival�ncias
			String equivalencia = componente.getEquivalencia();
			if (!isEmpty(equivalencia)) {
				if (!verificaExpressao(componenteInvalido, equivalencia, componenteDao, "equivalencia", cacheComponente))
					invalido = true;
			}
			
			// Analisar pr�-requisitos
			String preRequisito = componente.getPreRequisito();
			if (!isEmpty(preRequisito)) {
				if (!verificaExpressao(componenteInvalido, preRequisito, componenteDao, "preRequisito", cacheComponente))
					invalido = true;
			}
			
			String coRequisito = componente.getCoRequisito();
			// Analisar co-requisitos
			if (!isEmpty(coRequisito)) {
				if (!verificaExpressao(componenteInvalido, coRequisito, componenteDao, "coRequisito", cacheComponente))
					invalido = true;
			}
			
			// Se o componente possuir alguma express�o inv�lida adicionar � lista
			if (invalido) {
				componenteInvalido.put("componente", componente);
				componentesInvalidos.add(componenteInvalido);
			}
		}		
		
		if (isEmpty(componentesInvalidos)) {
			addMensagemWarning("N�o foram encontrados componentes curriculares com express�es inv�lidas");
			return null;
		}
		
		return forward("/geral/componente_curricular/expressoes_invalidas.jsp");
	}

	/** Verifica se a express�o � v�lida.
	 * @param componenteInvalido
	 * @param preRequisito
	 * @param componenteDao
	 * @param tipoExpressao
	 * @param cacheComponente 
	 * @return
	 */
	private boolean verificaExpressao(Map<String, Object> componenteInvalido, String preRequisito, ComponenteCurricularDao componenteDao, String tipoExpressao, Map<Integer, String> cacheComponente) {
		String expressao = null;
		boolean valido = true;
		try {
			expressao = ExpressaoUtil.buildExpressaoFromDB(preRequisito, componenteDao, true, cacheComponente);
			componenteInvalido.put(tipoExpressao, expressao);
			ArvoreExpressao.fromExpressao(preRequisito);
		} catch (Exception e) {
			valido = false;
			if (expressao == null)
				componenteInvalido.put(tipoExpressao, "Express�o com componente curricular inativo/inv�lido.");
			componenteInvalido.put(tipoExpressao + "Invalido", true);
		}		
		return valido;
	}

	public Collection<Map<String, Object>> getComponentesInvalidos() {
		return componentesInvalidos;
	}

	public void setComponentesInvalidos(
			Collection<Map<String, Object>> componentesInvalidos) {
		this.componentesInvalidos = componentesInvalidos;
	}
	
}

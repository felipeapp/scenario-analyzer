/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Criado em: 2007/10/23
 */
package br.ufrn.comum.dominio;

import java.util.HashMap;
import java.util.Map;


/**
 * Portais onde estão localizadas as notícias.
 *
 * @author David Pereira
 *
 */
public class LocalizacaoNoticiaPortal {

	public static final String PORTAL_DOCENTE = "portal_docente";

	public static final String PORTAL_DISCENTE = "portal_discente";

	public static final String PORTAL_CONSULTOR = "portal_consultor";

	public static final String PORTAL_COORDENADOR_GRADUACAO = "portal_coord_graduacao";

	public static final String PORTAL_COORDENADOR_STRICTO = "portal_coord_stricto";

	public static final String PORTAL_COORDENADOR_LATO = "portal_coord_lato";

	public static final String PORTAL_PUBLICO_SIGAA = "portal_publico_sigaa";
	
	public static final String PORTAL_PUBLICO_SIPAC = "portal_publico_sipac";

	public static final String PORTAL_TUTOR = "portal_tutor";

	public static final String PORTAL_SERVIDOR = "portal_servidor";

	public static final String PORTAL_PUBLICO_SIGRH = "portal_publico_sigrh";
	
	public static final String PORTAL_PLANO_SAUDE = "portal_plano_saude";

	public static final String PORTAL_SIPAC = "portal_administrativo";

	public static final String PORTAL_AVALIACAO_INSTITUCIONAL = "portal_avaliacao_institucional";
	
	public static final String PORTAL_CHEFIA_UNIDADE = "portal_chefia_unidade";
	
	public static final String PORTAL_SIGPP = "portal_sigpp";
	
	public static final String PORTAL_PROGRAMA_REDE = "portal_programa_rede";

	private static Map<String, Portal> portais; 
	
	/**
	 * Cria um hashmap de portais 
	 */
	static {
		portais = new HashMap<String, Portal>();
		portais.put(PORTAL_DOCENTE, new Portal(PORTAL_DOCENTE, "Portal Docente"));
		portais.put(PORTAL_DISCENTE, new Portal(PORTAL_DISCENTE, "Portal Discente"));
		portais.put(PORTAL_CONSULTOR, new Portal(PORTAL_CONSULTOR, "Portal Consultor"));
		portais.put(PORTAL_COORDENADOR_GRADUACAO, new Portal(PORTAL_COORDENADOR_GRADUACAO, "Portal Coordenador Graduação"));
		portais.put(PORTAL_COORDENADOR_STRICTO, new Portal(PORTAL_COORDENADOR_STRICTO, "Portal Coordenador Stricto"));
		portais.put(PORTAL_COORDENADOR_LATO, new Portal(PORTAL_COORDENADOR_LATO, "Portal Coordenador Lato"));
		portais.put(PORTAL_PUBLICO_SIGAA, new Portal(PORTAL_PUBLICO_SIGAA, "Portal Público SIGAA"));
		portais.put(PORTAL_PUBLICO_SIPAC, new Portal(PORTAL_PUBLICO_SIPAC, "Portal Público SIPAC"));
		portais.put(PORTAL_TUTOR, new Portal(PORTAL_TUTOR, "Portal Tutor"));
		portais.put(PORTAL_SERVIDOR, new Portal(PORTAL_SERVIDOR, "Portal Servidor"));
		portais.put(PORTAL_PUBLICO_SIGRH, new Portal(PORTAL_PUBLICO_SIGRH, "Portal Público SIGRH"));
		portais.put(PORTAL_PLANO_SAUDE, new Portal(PORTAL_PLANO_SAUDE, "Portal Plano de Saúde"));
		portais.put(PORTAL_SIPAC, new Portal(PORTAL_SIPAC, "Portal Administrativo"));
		portais.put(PORTAL_AVALIACAO_INSTITUCIONAL, new Portal(PORTAL_AVALIACAO_INSTITUCIONAL, "Portal Avaliação Institucional"));
		portais.put(PORTAL_CHEFIA_UNIDADE, new Portal(PORTAL_CHEFIA_UNIDADE, "Portal da Chefia da Unidade"));
		portais.put(PORTAL_SIGPP, new Portal(PORTAL_SIGPP, "Portal SIGPP"));
		portais.put(PORTAL_PROGRAMA_REDE, new Portal(PORTAL_PROGRAMA_REDE, "Portal Programa em Rede"));
	}
	
	/**
	 * Retorna a descricao do portal
	 * 
	 * @param portal
	 * @return
	 */
	public static Portal getPortal(String portal) {
		return portais.get(portal);
	}	
	
	public static Map<String, Portal> getPortais(){
		return portais;
	}
	
	/**
	 * Retorna o portal que possui o md5 passado
	 * 
	 * @param md5
	 * @return
	 */
	public static Portal getPortalByMD5(String md5) {
		
		for (Portal np : portais.values()) {
			if (np.getMd5().equals(md5))
				return np;
		}
		
		return null;
	}	
	

}
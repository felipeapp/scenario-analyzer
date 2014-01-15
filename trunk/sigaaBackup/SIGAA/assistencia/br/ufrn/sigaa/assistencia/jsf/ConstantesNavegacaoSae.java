package br.ufrn.sigaa.assistencia.jsf;


/**
 * Classe utilizada para as constantes de navega��o do sistema de assist�ncia ao estudante.
 * 
 * @author geyson
 **/
public class ConstantesNavegacaoSae {
	
	/** tela de busca de discentes contemplados com bolsa alimenta��o */
	public static final String DISCENTE_CARTAO_BUSCA						= "/sae/CartaoBeneficioDiscente/buscar.jsp";
	
	/** formul�rio para associar bolsista a cart�o de cart�es de acesso ao ru */
	public static final String ASSOCIA_DISCENTE_CARTAO						= "/sae/CartaoBeneficioDiscente/form_associar.jsp";
	
	/** tela de busca de cart�es beneficio ativos no sistema */
	public static final String BLOQUEIO_CARTAO_BUSCA						= "/sae/CartaoBeneficioDiscente/buscar_cartoes.jsp";
	
	/** formul�rio para associar bolsista a cart�o de cart�es de acesso ao ru */
	public static final String BLOQUEIO_CARTAO_FORM						= "/sae/CartaoBeneficioDiscente/form_bloquear.jsp";
	
	/** formul�rio para selecionar bolsista para gerar relat�rio */
	public static final String SELECIONA_DISCENTE_CARTAO						= "/sae/RelatorioCartaoBeneficio/seleciona.jsp";

	/** relat�rio de discentes com cartao beneficio */
	public static final String RELATORIO_DISCENTE_CARTAO						= "/sae/RelatorioCartaoBeneficio/relatorio.jsp";
	
	/** formul�rio para selecionar bolsista para gerar relat�rio */
	public static final String SELECIONA_DISCENTE_ASSINATURA						= "/sae/RelatorioCartaoBeneficio/seleciona_assinaturas.jsp";

	/** relat�rio de discentes com cartao beneficio */
	public static final String RELATORIO_DISCENTE_ASSINATURA						= "/sae/RelatorioCartaoBeneficio/relatorio_assinaturas.jsp";

	/** formul�rio para selecionar per�do de busca para gerar relat�rio de acesso ao RU */
	public static final String SELECIONA_ACESSO_RU						= "/sae/RelatorioAcessoRU/seleciona.jsp";
	
	/** relat�rio de acesso ao RU por per�odo */
	public static final String RELATORIO_ACESSO_RU						= "/sae/RelatorioAcessoRU/relatorio.jsp";
	
	/** relat�rio detalhado de acesso ao RU por per�odo */
	public static final String RELATORIO_DETALHES_ACESSO_RU						= "/sae/RelatorioAcessoRU/relatorio_detalhes.jsp";
}

package br.ufrn.sigaa.assistencia.jsf;


/**
 * Classe utilizada para as constantes de navegação do sistema de assistência ao estudante.
 * 
 * @author geyson
 **/
public class ConstantesNavegacaoSae {
	
	/** tela de busca de discentes contemplados com bolsa alimentação */
	public static final String DISCENTE_CARTAO_BUSCA						= "/sae/CartaoBeneficioDiscente/buscar.jsp";
	
	/** formulário para associar bolsista a cartão de cartões de acesso ao ru */
	public static final String ASSOCIA_DISCENTE_CARTAO						= "/sae/CartaoBeneficioDiscente/form_associar.jsp";
	
	/** tela de busca de cartões beneficio ativos no sistema */
	public static final String BLOQUEIO_CARTAO_BUSCA						= "/sae/CartaoBeneficioDiscente/buscar_cartoes.jsp";
	
	/** formulário para associar bolsista a cartão de cartões de acesso ao ru */
	public static final String BLOQUEIO_CARTAO_FORM						= "/sae/CartaoBeneficioDiscente/form_bloquear.jsp";
	
	/** formulário para selecionar bolsista para gerar relatório */
	public static final String SELECIONA_DISCENTE_CARTAO						= "/sae/RelatorioCartaoBeneficio/seleciona.jsp";

	/** relatório de discentes com cartao beneficio */
	public static final String RELATORIO_DISCENTE_CARTAO						= "/sae/RelatorioCartaoBeneficio/relatorio.jsp";
	
	/** formulário para selecionar bolsista para gerar relatório */
	public static final String SELECIONA_DISCENTE_ASSINATURA						= "/sae/RelatorioCartaoBeneficio/seleciona_assinaturas.jsp";

	/** relatório de discentes com cartao beneficio */
	public static final String RELATORIO_DISCENTE_ASSINATURA						= "/sae/RelatorioCartaoBeneficio/relatorio_assinaturas.jsp";

	/** formulário para selecionar perído de busca para gerar relatório de acesso ao RU */
	public static final String SELECIONA_ACESSO_RU						= "/sae/RelatorioAcessoRU/seleciona.jsp";
	
	/** relatório de acesso ao RU por período */
	public static final String RELATORIO_ACESSO_RU						= "/sae/RelatorioAcessoRU/relatorio.jsp";
	
	/** relatório detalhado de acesso ao RU por período */
	public static final String RELATORIO_DETALHES_ACESSO_RU						= "/sae/RelatorioAcessoRU/relatorio_detalhes.jsp";
}

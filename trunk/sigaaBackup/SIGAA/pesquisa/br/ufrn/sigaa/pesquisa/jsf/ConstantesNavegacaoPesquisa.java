/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 19/10/2011
 *
 */
package br.ufrn.sigaa.pesquisa.jsf;

/**
 * Classe utilizada para armazenar as constantes de navegação do módulo de
 * pesquisa.
 * 
 * @author Leonardo Campos
 * 
 */
public class ConstantesNavegacaoPesquisa {

	// COTA DE BOLSAS
	/** Tela com formulário de cadastro de cotas de bolsas. */
	public static final String COTABOLSAS_FORM 					= "/pesquisa/CotaBolsas/form.jsp";
	/** Tela com listagem de cotas de bolsas. */
	public static final String COTABOLSAS_LISTA 				= "/pesquisa/CotaBolsas/lista.jsp";
	
	// PROPOSTA DE CRIAÇÃO DE GRUPO DE PESQUISA
	/** Tela para informação dos dados gerais da proposta de criação de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_DADOS_GERAIS 		 = "/pesquisa/GrupoPesquisa/proposta/dados_gerais.jsp";
	/** Tela para informação dos membros da proposta de criação de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_MEMBROS	 		 = "/pesquisa/GrupoPesquisa/proposta/membros.jsp";
	/** Tela para informação do termo de concordância da proposta de criação de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_TERMO_CONCORDANCIA	 = "/pesquisa/GrupoPesquisa/proposta/termo.jsp";
	/** Tela para informação da descrição detalhada da proposta de criação de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_DESCRICAO	 		 = "/pesquisa/GrupoPesquisa/proposta/descricao.jsp";
	/** Tela para informação dos projetos vinculados da proposta de criação de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_PROJETOS_VINCULADOS = "/pesquisa/GrupoPesquisa/proposta/projetos_vinculados.jsp";
	/** Tela com lista das propostas de criação de grupo de pesquisa cadastradas pelo usuário. */
	public static final String PROPOSTAGRUPO_MINHAS_PROPOSTAS	 = "/pesquisa/GrupoPesquisa/proposta/minhas_propostas.jsp";
	/** Tela com formulário para emitir parecer sobre proposta de criação de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_EMITIR_PARECER	     = "/pesquisa/GrupoPesquisa/proposta/parecer.jsp";
	/** Tela de comprovante de submissão da proposta de criação de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_COMPROVANTE	     = "/pesquisa/GrupoPesquisa/proposta/comprovante.jsp";
	/** Tela de emissão da declaração da proposta de criação de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_EMITIR_CERTIFICADO  = "/pesquisa/GrupoPesquisa/emissao_declaracao.jsp";
	/** Tela de emissão do certificado da proposta de criação de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_CERTIFICADO         = "/pesquisa/GrupoPesquisa/declaracao.jsp";
	/** Tela com resumo da proposta de criação de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_VISUALIZAR	         = "/pesquisa/GrupoPesquisa/view.jsp";
	/** Tela para assinatura do termo de concordância de participação na proposta de criação de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_ASSINATURA_DIGITAL	 = "/public/pesquisa/assinatura_digital_grupo_pesquisa.jsf";
	
	
	//AUTORIZAÇÃO DE GRUPO DE PESQUISA
	/** Tela de listagem de propostas de criação de grupo de pesquisa pendentes de parecer. */
	public static final String AUTORIZACAO_GRUPO_PESQUISA_LISTA		 = "/pesquisa/GrupoPesquisa/autorizacao/lista.jsp";
	/** Tela para emissão do parecer sobre uma proposta de criação de grupo de pesquisa. */
	public static final String AUTORIZACAO_GRUPO_PESQUISA_PARECER	 = "/pesquisa/GrupoPesquisa/autorizacao/parecer.jsp";

	// RELATÓRIO DE PROJETO DE PESQUISA
	/** Tela com o formulário de envio do relatório de projeto de pesquisa. */
	public static final String RELATORIO_PROJETO_FORM	 = "/pesquisa/RelatorioProjeto/form.jsp";
	
	// FINALIZAÇÃO DE PROJETO DE PESQUISA
	/** Tela com o formulário de envio do relatório de projeto de pesquisa. */
	public static final String FINALIZAR_PROJETO_FORM	 		= "/pesquisa/FinalizarProjetoPesquisa/form.jsp";
	/** Tela com o comprovante de envio do relatório de projeto de pesquisa. */
	public static final String FINALIZAR_PROJETO_COMPROVANTE	= "/pesquisa/FinalizarProjetoPesquisa/comprovante.jsp";

}

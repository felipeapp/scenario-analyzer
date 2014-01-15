/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 19/10/2011
 *
 */
package br.ufrn.sigaa.pesquisa.jsf;

/**
 * Classe utilizada para armazenar as constantes de navega��o do m�dulo de
 * pesquisa.
 * 
 * @author Leonardo Campos
 * 
 */
public class ConstantesNavegacaoPesquisa {

	// COTA DE BOLSAS
	/** Tela com formul�rio de cadastro de cotas de bolsas. */
	public static final String COTABOLSAS_FORM 					= "/pesquisa/CotaBolsas/form.jsp";
	/** Tela com listagem de cotas de bolsas. */
	public static final String COTABOLSAS_LISTA 				= "/pesquisa/CotaBolsas/lista.jsp";
	
	// PROPOSTA DE CRIA��O DE GRUPO DE PESQUISA
	/** Tela para informa��o dos dados gerais da proposta de cria��o de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_DADOS_GERAIS 		 = "/pesquisa/GrupoPesquisa/proposta/dados_gerais.jsp";
	/** Tela para informa��o dos membros da proposta de cria��o de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_MEMBROS	 		 = "/pesquisa/GrupoPesquisa/proposta/membros.jsp";
	/** Tela para informa��o do termo de concord�ncia da proposta de cria��o de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_TERMO_CONCORDANCIA	 = "/pesquisa/GrupoPesquisa/proposta/termo.jsp";
	/** Tela para informa��o da descri��o detalhada da proposta de cria��o de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_DESCRICAO	 		 = "/pesquisa/GrupoPesquisa/proposta/descricao.jsp";
	/** Tela para informa��o dos projetos vinculados da proposta de cria��o de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_PROJETOS_VINCULADOS = "/pesquisa/GrupoPesquisa/proposta/projetos_vinculados.jsp";
	/** Tela com lista das propostas de cria��o de grupo de pesquisa cadastradas pelo usu�rio. */
	public static final String PROPOSTAGRUPO_MINHAS_PROPOSTAS	 = "/pesquisa/GrupoPesquisa/proposta/minhas_propostas.jsp";
	/** Tela com formul�rio para emitir parecer sobre proposta de cria��o de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_EMITIR_PARECER	     = "/pesquisa/GrupoPesquisa/proposta/parecer.jsp";
	/** Tela de comprovante de submiss�o da proposta de cria��o de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_COMPROVANTE	     = "/pesquisa/GrupoPesquisa/proposta/comprovante.jsp";
	/** Tela de emiss�o da declara��o da proposta de cria��o de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_EMITIR_CERTIFICADO  = "/pesquisa/GrupoPesquisa/emissao_declaracao.jsp";
	/** Tela de emiss�o do certificado da proposta de cria��o de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_CERTIFICADO         = "/pesquisa/GrupoPesquisa/declaracao.jsp";
	/** Tela com resumo da proposta de cria��o de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_VISUALIZAR	         = "/pesquisa/GrupoPesquisa/view.jsp";
	/** Tela para assinatura do termo de concord�ncia de participa��o na proposta de cria��o de grupo de pesquisa. */
	public static final String PROPOSTAGRUPO_ASSINATURA_DIGITAL	 = "/public/pesquisa/assinatura_digital_grupo_pesquisa.jsf";
	
	
	//AUTORIZA��O DE GRUPO DE PESQUISA
	/** Tela de listagem de propostas de cria��o de grupo de pesquisa pendentes de parecer. */
	public static final String AUTORIZACAO_GRUPO_PESQUISA_LISTA		 = "/pesquisa/GrupoPesquisa/autorizacao/lista.jsp";
	/** Tela para emiss�o do parecer sobre uma proposta de cria��o de grupo de pesquisa. */
	public static final String AUTORIZACAO_GRUPO_PESQUISA_PARECER	 = "/pesquisa/GrupoPesquisa/autorizacao/parecer.jsp";

	// RELAT�RIO DE PROJETO DE PESQUISA
	/** Tela com o formul�rio de envio do relat�rio de projeto de pesquisa. */
	public static final String RELATORIO_PROJETO_FORM	 = "/pesquisa/RelatorioProjeto/form.jsp";
	
	// FINALIZA��O DE PROJETO DE PESQUISA
	/** Tela com o formul�rio de envio do relat�rio de projeto de pesquisa. */
	public static final String FINALIZAR_PROJETO_FORM	 		= "/pesquisa/FinalizarProjetoPesquisa/form.jsp";
	/** Tela com o comprovante de envio do relat�rio de projeto de pesquisa. */
	public static final String FINALIZAR_PROJETO_COMPROVANTE	= "/pesquisa/FinalizarProjetoPesquisa/comprovante.jsp";

}

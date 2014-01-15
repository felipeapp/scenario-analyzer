/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/08/2009
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface para as constantes de mensagens do M�dulo de Pesquisa que ser�o exibidas ao usu�rio.
 * 
 * @author Leonardo Campos
 *
 */
public interface MensagensPesquisa {

	/**
	 * Prefixo para estas mensagens.
	 */
	static final String prefix = Sistema.SIGAA + "_" + SigaaSubsistemas.PESQUISA.getId() + "_";
	
	/**
	 * Mensagem exibida quando tenta-se atribuir um limite de cota excepcional de valor igual ou inferior ao limite padr�o. 
	 *  
	 * Conte�do: O limite de cota excepcional deve ser maior que o limite padr�o de %d cotas.
	 */
	public static final String LIMITE_EXCEPCIONAL_DEVE_SER_MAIOR_QUE_LIMITE_PADRAO = prefix + "30";
	
	/**
	 * Mensagem exibida quando tenta-se atribuir um limite de cota excepcional para um docente que j� possui um limite ativo cadastrado. 
	 *  
	 * Conte�do: J� foi definido um limite de cota excepcional para o docente informado.
	 */
	public static final String LIMITE_JA_ATRIBUIDO_DOCENTE = prefix + "31";
	
	// Mensagens da integra��o de bolsas de pesquisa com o SIPAC
	
	/**
	 * Mensagem exibida quando o gestor de pesquisa confirma a opera��o de ignorar uma indica��o/finaliza��o de bolsista incorreta. 
	 *  
	 * Conte�do: %s ignorada com sucesso.
	 */
	public static final String IGNORADO_SUCESSO = prefix + "65";
	
	/**
	 * Mensagem exibida quando se busca discentes para homologar suas bolsas mas o sistema n�o encontra discente algum. 
	 *  
	 * Conte�do: N�o h� discentes pendentes de homologa��o na modalidade selecionada.
	 */
	public static final String NAO_HA_DISCENTES_PENDENTES_HOMOLOGACAO = prefix + "66";
	
	/**
	 * Mensagem exibida quando se busca discentes para finalizar suas bolsas mas o sistema n�o encontra discente algum. 
	 *  
	 * Conte�do: N�o h� discentes pendentes de finaliza��o na modalidade selecionada.
	 */
	public static final String NAO_HA_DISCENTES_PENDENTES_FINALIZACAO = prefix + "67";
	
	/**
	 * Mensagem exibida quando o usu�rio tenta confirmar a homologa��o de bolsistas sem ter selecionado nenhum aluno
	 * no formul�rio. 
	 *  
	 * Conte�do: Selecione pelo menos um discente para realizar a homologa��o.
	 */
	public static final String SELECIONE_DISCENTE_HOMOLOGACAO = prefix + "68";
	
	/**
	 * Mensagem exibida quando o usu�rio tenta confirmar a finaliza��o de bolsistas sem ter selecionado nenhum aluno
	 * no formul�rio. 
	 *  
	 * Conte�do: Selecione pelo menos um discente para realizar a finaliza��o.
	 */
	public static final String SELECIONE_DISCENTE_FINALIZACAO = prefix + "69";
	
	
	// Mensagens de resumos para congresso de inicia��o cient�fica
	
	/**
	 * Mensagem exibida quando usu�rio tenta acessar a opera��o para submeter um resumo pro CIC fora do per�odo
	 * definido no calend�rio de pesquisa
	 *  
	 * Conte�do: O per�odo de submiss�o de resumos n�o est� vigente
	 */
	public static final String PERIODO_SUBMISSAO_NAO_VIGENTE = prefix + "70";
	
	/**
	 * Mensagem exibida quando outro usu�rio que n�o o autor do resumo tenta acessar a opera��o para editar o resumo. 
	 *  
	 * Conte�do: Somente o autor do resumo pode edit�-lo
	 */
	public static final String SOMENTE_AUTOR_PODE_EDITAR = prefix + "71";
	
	/**
	 * Mensagem exibida quando o resumo pretendido n�o � localizado na base de dados 
	 *  
	 * Conte�do: Resumo n�o encontrado
	 */
	public static final String RESUMO_NAO_ENCONTRADO = prefix + "72";
	
	/**
	 * Mensagem exibida quando tenta-se submeter um resumo a partir de um relat�rio final
	 * sem selecionar o relat�rio 
	 *  
	 * Conte�do: � necess�rio selecionar um relat�rio final submetido para cadastrar o resumo de um aluno
	 */
	public static final String NECESSARIO_SELECIONAR_RELATORIO_FINAL = prefix + "73";
	
	/**
	 * Mensagem exibida quando o relat�rio selecionado n�o � localizado na base de dados 
	 *  
	 * Conte�do: Relat�rio n�o encontrado
	 */
	public static final String RELATORIO_NAO_ENCONTRADO = prefix + "74";
	
	/**
	 * Mensagem exibida quando o autor informado j� � autor de outro resumo para o mesmo congresso. 
	 *  
	 * Conte�do: O discente %s j� est� cadastrado como o autor de um resumo para o %s Congresso de Inicia��o Cient�fica. 
	 * 			Caso deseje alter�-lo, realize a consulta dos resumos enviados.
	 */
	public static final String AUTOR_DUPLICADO = prefix + "75";
	
	// Mensagens de notifica��es de inven��o
	
	/**
	 * Mensagem exibida quando usu�rio tenta avan�ar no cadastro de inven��o sem informar inventores
	 *  
	 * Conte�do: Informe pelo menos um autor para a inven��o
	 */
	public static final String INFORME_PELO_MENOS_UM_INVENTOR = prefix + "80";
	
	/**
	 * Mensagem exibida quando usu�rio tenta gerar uma capa de processo para uma notifica��o de inven��o sem que haja um processo
	 * criado para a mesma.
	 *  
	 * Conte�do: N�o foi localizado o processo no sistema de protocolos correspondente a esta notifica��o de inven��o
	 */
	public static final String NAO_EXISTE_PROCESSO_INVENCAO = prefix + "81";
	
	
	/**
	 * Essa mensagem ser� exibida quando um avaliador tentar gerar o certificado � o mesmo estiver com o campo presen�a
	 * setado como false, o que impedir� que o mesmo gere o certificado.
	 *  
	 *  Conte�do: N�o consta a presen�a do docente %s no Congresso de Inicia��o Cient�fica.
	 */
	public static final String USUARIO_COM_FALTA_COMO_AVALIADOR = prefix + "90"; 
	
	
	/**
	 * Mensagem exibida quando o usu�rio tenta gerar um relat�rio como as cotas de bolsa n�o seguidas.
	 * 
	 * Conte�do: N�o houve renova��o entre as cotas selecionadas.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String SEM_COTAS_RELATORIO_BOLSA_PIBIC = prefix + "100";

	/**
	 * Mensagem exibida quando o usu�rio tenta indicar um bolsista com uma conta que n�o seja do banco do Brasil..
	 * 
	 * Conte�do: Aten��o: O CNPq n�o aceita contas banc�rias que n�o sejam do BANCO DO BRASIL.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String BOLSA_CNPQ_BB = prefix + "110";
	
	
	
	/**
	 * Mensagem exibida para informar que n�o � poss�vel indicar um bolsista com dados desatualizados.  
	 * 
	 * Conte�do: N�o � poss�vel indicar este discente pois os seguintes dados est�o desatualizados: %s. O discente deve entrar em contato com o DAE para atualiz�-los.
	 * Tipo: ERROR
	 * 
	 */
	public static final String INDICAR_BOLSISTA_DADOS_DESATUALIZADOS = prefix + "111";

	
	/**
	 * Mensagem exibida quando se tenta cadastrar um bolsista com o tipo de bolsa diferente de Conta Corrente.
	 * 
	 * Conte�do: Aten��o: O CNPq n�o aceita contas banc�rias que n�o sejam Conta Corrente.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String BOLSA_CNPQ_CONTA_CORRENTE = prefix + "112";
	
}

/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/08/2009
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface para as constantes de mensagens do Módulo de Pesquisa que serão exibidas ao usuário.
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
	 * Mensagem exibida quando tenta-se atribuir um limite de cota excepcional de valor igual ou inferior ao limite padrão. 
	 *  
	 * Conteúdo: O limite de cota excepcional deve ser maior que o limite padrão de %d cotas.
	 */
	public static final String LIMITE_EXCEPCIONAL_DEVE_SER_MAIOR_QUE_LIMITE_PADRAO = prefix + "30";
	
	/**
	 * Mensagem exibida quando tenta-se atribuir um limite de cota excepcional para um docente que já possui um limite ativo cadastrado. 
	 *  
	 * Conteúdo: Já foi definido um limite de cota excepcional para o docente informado.
	 */
	public static final String LIMITE_JA_ATRIBUIDO_DOCENTE = prefix + "31";
	
	// Mensagens da integração de bolsas de pesquisa com o SIPAC
	
	/**
	 * Mensagem exibida quando o gestor de pesquisa confirma a operação de ignorar uma indicação/finalização de bolsista incorreta. 
	 *  
	 * Conteúdo: %s ignorada com sucesso.
	 */
	public static final String IGNORADO_SUCESSO = prefix + "65";
	
	/**
	 * Mensagem exibida quando se busca discentes para homologar suas bolsas mas o sistema não encontra discente algum. 
	 *  
	 * Conteúdo: Não há discentes pendentes de homologação na modalidade selecionada.
	 */
	public static final String NAO_HA_DISCENTES_PENDENTES_HOMOLOGACAO = prefix + "66";
	
	/**
	 * Mensagem exibida quando se busca discentes para finalizar suas bolsas mas o sistema não encontra discente algum. 
	 *  
	 * Conteúdo: Não há discentes pendentes de finalização na modalidade selecionada.
	 */
	public static final String NAO_HA_DISCENTES_PENDENTES_FINALIZACAO = prefix + "67";
	
	/**
	 * Mensagem exibida quando o usuário tenta confirmar a homologação de bolsistas sem ter selecionado nenhum aluno
	 * no formulário. 
	 *  
	 * Conteúdo: Selecione pelo menos um discente para realizar a homologação.
	 */
	public static final String SELECIONE_DISCENTE_HOMOLOGACAO = prefix + "68";
	
	/**
	 * Mensagem exibida quando o usuário tenta confirmar a finalização de bolsistas sem ter selecionado nenhum aluno
	 * no formulário. 
	 *  
	 * Conteúdo: Selecione pelo menos um discente para realizar a finalização.
	 */
	public static final String SELECIONE_DISCENTE_FINALIZACAO = prefix + "69";
	
	
	// Mensagens de resumos para congresso de iniciação científica
	
	/**
	 * Mensagem exibida quando usuário tenta acessar a operação para submeter um resumo pro CIC fora do período
	 * definido no calendário de pesquisa
	 *  
	 * Conteúdo: O período de submissão de resumos não está vigente
	 */
	public static final String PERIODO_SUBMISSAO_NAO_VIGENTE = prefix + "70";
	
	/**
	 * Mensagem exibida quando outro usuário que não o autor do resumo tenta acessar a operação para editar o resumo. 
	 *  
	 * Conteúdo: Somente o autor do resumo pode editá-lo
	 */
	public static final String SOMENTE_AUTOR_PODE_EDITAR = prefix + "71";
	
	/**
	 * Mensagem exibida quando o resumo pretendido não é localizado na base de dados 
	 *  
	 * Conteúdo: Resumo não encontrado
	 */
	public static final String RESUMO_NAO_ENCONTRADO = prefix + "72";
	
	/**
	 * Mensagem exibida quando tenta-se submeter um resumo a partir de um relatório final
	 * sem selecionar o relatório 
	 *  
	 * Conteúdo: É necessário selecionar um relatório final submetido para cadastrar o resumo de um aluno
	 */
	public static final String NECESSARIO_SELECIONAR_RELATORIO_FINAL = prefix + "73";
	
	/**
	 * Mensagem exibida quando o relatório selecionado não é localizado na base de dados 
	 *  
	 * Conteúdo: Relatório não encontrado
	 */
	public static final String RELATORIO_NAO_ENCONTRADO = prefix + "74";
	
	/**
	 * Mensagem exibida quando o autor informado já é autor de outro resumo para o mesmo congresso. 
	 *  
	 * Conteúdo: O discente %s já está cadastrado como o autor de um resumo para o %s Congresso de Iniciação Científica. 
	 * 			Caso deseje alterá-lo, realize a consulta dos resumos enviados.
	 */
	public static final String AUTOR_DUPLICADO = prefix + "75";
	
	// Mensagens de notificações de invenção
	
	/**
	 * Mensagem exibida quando usuário tenta avançar no cadastro de invenção sem informar inventores
	 *  
	 * Conteúdo: Informe pelo menos um autor para a invenção
	 */
	public static final String INFORME_PELO_MENOS_UM_INVENTOR = prefix + "80";
	
	/**
	 * Mensagem exibida quando usuário tenta gerar uma capa de processo para uma notificação de invenção sem que haja um processo
	 * criado para a mesma.
	 *  
	 * Conteúdo: Não foi localizado o processo no sistema de protocolos correspondente a esta notificação de invenção
	 */
	public static final String NAO_EXISTE_PROCESSO_INVENCAO = prefix + "81";
	
	
	/**
	 * Essa mensagem será exibida quando um avaliador tentar gerar o certificado é o mesmo estiver com o campo presença
	 * setado como false, o que impedirá que o mesmo gere o certificado.
	 *  
	 *  Conteúdo: Não consta a presença do docente %s no Congresso de Iniciação Científica.
	 */
	public static final String USUARIO_COM_FALTA_COMO_AVALIADOR = prefix + "90"; 
	
	
	/**
	 * Mensagem exibida quando o usuário tenta gerar um relatório como as cotas de bolsa não seguidas.
	 * 
	 * Conteúdo: Não houve renovação entre as cotas selecionadas.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String SEM_COTAS_RELATORIO_BOLSA_PIBIC = prefix + "100";

	/**
	 * Mensagem exibida quando o usuário tenta indicar um bolsista com uma conta que não seja do banco do Brasil..
	 * 
	 * Conteúdo: Atenção: O CNPq não aceita contas bancárias que não sejam do BANCO DO BRASIL.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String BOLSA_CNPQ_BB = prefix + "110";
	
	
	
	/**
	 * Mensagem exibida para informar que não é possível indicar um bolsista com dados desatualizados.  
	 * 
	 * Conteúdo: Não é possível indicar este discente pois os seguintes dados estão desatualizados: %s. O discente deve entrar em contato com o DAE para atualizá-los.
	 * Tipo: ERROR
	 * 
	 */
	public static final String INDICAR_BOLSISTA_DADOS_DESATUALIZADOS = prefix + "111";

	
	/**
	 * Mensagem exibida quando se tenta cadastrar um bolsista com o tipo de bolsa diferente de Conta Corrente.
	 * 
	 * Conteúdo: Atenção: O CNPq não aceita contas bancárias que não sejam Conta Corrente.<br>
	 * Tipo: ERROR.<br>
	 */
	public static final String BOLSA_CNPQ_CONTA_CORRENTE = prefix + "112";
	
}

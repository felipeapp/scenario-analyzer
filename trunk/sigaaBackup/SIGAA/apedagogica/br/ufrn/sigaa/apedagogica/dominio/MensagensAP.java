package br.ufrn.sigaa.apedagogica.dominio;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface para as constantes de mensagens do M�dulo do Programa de Atualiza��o Pedag�gica.
 * @author M�rio Rizzi
 *
 */
public interface MensagensAP {

	 /**
	  ** Prefixo para estas mensagens.
	 */
	static final String PREFIX = Sistema.SIGAA + "_" + SigaaSubsistemas.PROGRAMA_ATUALIZACAO_PEDAGOGICA.getId() + "_";
	
	/**
	 * Mensagem exibida quando usu�rio tenta selecionar um atividade para alterar
	 * a situa��o dos participantes ({@link br.ufrn.sigaa.apedagogica.jsf.AlteracaoSituacaoInscricaoAtividadeAPMBean#selecionaAtividade()}
	 *  
	 * Conte�do: N�o foram encontrados participantes para a atividade selecionada.
	 * Tipo: ERRO
	 */
	public static final String BUSCA_PARTICIPANTES_SEM_RESULTADO = PREFIX + "01";
	
	/**
	 * Mensagem exibida quando usu�rio realizar uma consulta de participantes.
	 * {@link br.ufrn.sigaa.apedagogica.jsf.CertificadoParticipacaoAtividadeAPMBean#consultar()}
	 *  
	 * Conte�do: Pelo menos um filtro deve ser selecionado para consulta
	 * Tipo: ERRO
	 */
	public static final String PREENCHA_PELO_MENOS_UM_CAMPO = PREFIX + "02";
	
	/**
	 * Mensagem exibida quando usu�rio realiza a inscri��o em uma atividade
	 * {@link br.ufrn.sigaa.apedagogica.jsf.InscricaoAtividadeAPMBean#selecionarGrupo()}
	 *  
	 * Conte�do: O grupo de atividade selecionado n�o est� mais aberto.
	 * Tipo: ERRO
	 */
	public static final String GRUPO_ATIVIDADE_FECHADO = PREFIX + "03";
	
	/**
	 * Mensagem exibida quando usu�rio tenta remover um grupo que possui participantes cadastrados.
	 * {@link br.ufrn.sigaa.apedagogica.jsf.GrupoAtividadesAPMBean#remover()}
	 *  
	 * Conte�do: O grupo n�o pode ser removido pois existem participantes inscritos em suas atividades.
	 * Tipo: ERRO
	 */
	public static final String GRUPO_POSSUI_PARTICIPANTE_ATIVIDADES = PREFIX + "04";
	
	/**
	 * Mensagem exibida quando usu�rio tenta alterar situa��o sem ter selecionado a op��o. 
	 * {@link br.ufrn.sigaa.apedagogica.jsf.AlteracaoSituacaoInscricaoAtividadeAPMBean#cadastrar()}
	 *  
	 * Conte�do: N�o houve altera��o na situa��o dos participantes.
	 * Tipo: WARNING
	 */
	public static final String SITUACAO_SEM_ALTERACAO = PREFIX + "05";
	
	/**
	 * Mensagem exibida quando usu�rio tenta remover um grupo que possui participantes cadastrados.
	 * {@link br.ufrn.sigaa.apedagogica.jsf.GrupoAtividadesAPMBean#remover()}
	 *  
	 * Conte�do: A atividade n�o pode ser removida pois existem participantes inscritos.
	 * Tipo: ERRO
	 */
	public static final String POSSUI_PARTICIPANTE_ATIVIDADES = PREFIX + "06";
	
}

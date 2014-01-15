package br.ufrn.sigaa.apedagogica.dominio;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface para as constantes de mensagens do Módulo do Programa de Atualização Pedagógica.
 * @author Mário Rizzi
 *
 */
public interface MensagensAP {

	 /**
	  ** Prefixo para estas mensagens.
	 */
	static final String PREFIX = Sistema.SIGAA + "_" + SigaaSubsistemas.PROGRAMA_ATUALIZACAO_PEDAGOGICA.getId() + "_";
	
	/**
	 * Mensagem exibida quando usuário tenta selecionar um atividade para alterar
	 * a situação dos participantes ({@link br.ufrn.sigaa.apedagogica.jsf.AlteracaoSituacaoInscricaoAtividadeAPMBean#selecionaAtividade()}
	 *  
	 * Conteúdo: Não foram encontrados participantes para a atividade selecionada.
	 * Tipo: ERRO
	 */
	public static final String BUSCA_PARTICIPANTES_SEM_RESULTADO = PREFIX + "01";
	
	/**
	 * Mensagem exibida quando usuário realizar uma consulta de participantes.
	 * {@link br.ufrn.sigaa.apedagogica.jsf.CertificadoParticipacaoAtividadeAPMBean#consultar()}
	 *  
	 * Conteúdo: Pelo menos um filtro deve ser selecionado para consulta
	 * Tipo: ERRO
	 */
	public static final String PREENCHA_PELO_MENOS_UM_CAMPO = PREFIX + "02";
	
	/**
	 * Mensagem exibida quando usuário realiza a inscrição em uma atividade
	 * {@link br.ufrn.sigaa.apedagogica.jsf.InscricaoAtividadeAPMBean#selecionarGrupo()}
	 *  
	 * Conteúdo: O grupo de atividade selecionado não está mais aberto.
	 * Tipo: ERRO
	 */
	public static final String GRUPO_ATIVIDADE_FECHADO = PREFIX + "03";
	
	/**
	 * Mensagem exibida quando usuário tenta remover um grupo que possui participantes cadastrados.
	 * {@link br.ufrn.sigaa.apedagogica.jsf.GrupoAtividadesAPMBean#remover()}
	 *  
	 * Conteúdo: O grupo não pode ser removido pois existem participantes inscritos em suas atividades.
	 * Tipo: ERRO
	 */
	public static final String GRUPO_POSSUI_PARTICIPANTE_ATIVIDADES = PREFIX + "04";
	
	/**
	 * Mensagem exibida quando usuário tenta alterar situação sem ter selecionado a opção. 
	 * {@link br.ufrn.sigaa.apedagogica.jsf.AlteracaoSituacaoInscricaoAtividadeAPMBean#cadastrar()}
	 *  
	 * Conteúdo: Não houve alteração na situação dos participantes.
	 * Tipo: WARNING
	 */
	public static final String SITUACAO_SEM_ALTERACAO = PREFIX + "05";
	
	/**
	 * Mensagem exibida quando usuário tenta remover um grupo que possui participantes cadastrados.
	 * {@link br.ufrn.sigaa.apedagogica.jsf.GrupoAtividadesAPMBean#remover()}
	 *  
	 * Conteúdo: A atividade não pode ser removida pois existem participantes inscritos.
	 * Tipo: ERRO
	 */
	public static final String POSSUI_PARTICIPANTE_ATIVIDADES = PREFIX + "06";
	
}

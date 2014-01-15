/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/11/2009
 *
 */

package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.parametros.dominio.ParametrosProdocente;

/**
 * Interface para as constantes de mensagens da Produção Intelectual que serão exibidas para o usuário.
 * 
 * @author Daniel Augusto
 *
 */
public interface MensagensProdocente {

	/**
	 * Prefixo para estas mensagens.
	 */
	static final String PREFIX = Sistema.SIGAA + "_" + SigaaSubsistemas.PROD_INTELECTUAL.getId() + "_";
	
	/**
	 * Mensagem exibida para o docente ao tentar cadastrar um trabalho de conclusão com a mesma natureza do exame.
	 *  
	 * Conteúdo: Já existe um trabalho de conclusão com a natureza do exame %s cadastrada com o mesmo título, 
	 * mesmo ano de referência e pertencente ao mesmo servidor.
	 * Tipo: ERROR
	 */
	public static final String BANCA_JA_CADASTRADA_COM_MESMOS_DADOS = PREFIX + "01";
	
	/**
	 * Mensagem exibida para o docente ao tentar importar produções do Lattes através de um arquivo xml, e este por sua vez,
	 *  não contenha nenhuma produção válida.
	 *  
	 * Conteúdo: O arquivo enviado não possui produções do Lattes passíveis de importação pelo sistema.
	 * Tipo: ERROR
	 */
	public static final String ARQUIVO_NAO_POSSUI_PRODUCOES = PREFIX + "02";
	
	/**
	 * Mensagem exibida para o docente ao tentar enviar arquivo com o tamanho superior ao definido na
	 * constante {@link ParametrosProdocente#TAMANHO_MAXIMO_ENVIO_ARQUIVO_PRODUCAO}
	 *  
	 * Conteúdo: O arquivo a ser inserido deve ter no máximo um tamanho de {@link ParametrosProdocente#TAMANHO_MAXIMO_ENVIO_ARQUIVO_PRODUCAO} MB.
	 * Tipo: ERROR
	 */
	public static final String TAMANHO_ARQUIVO_PRODUCAO_EXCEDE = PREFIX + "03";
	
}

/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Interface para as constantes de mensagens da Produ��o Intelectual que ser�o exibidas para o usu�rio.
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
	 * Mensagem exibida para o docente ao tentar cadastrar um trabalho de conclus�o com a mesma natureza do exame.
	 *  
	 * Conte�do: J� existe um trabalho de conclus�o com a natureza do exame %s cadastrada com o mesmo t�tulo, 
	 * mesmo ano de refer�ncia e pertencente ao mesmo servidor.
	 * Tipo: ERROR
	 */
	public static final String BANCA_JA_CADASTRADA_COM_MESMOS_DADOS = PREFIX + "01";
	
	/**
	 * Mensagem exibida para o docente ao tentar importar produ��es do Lattes atrav�s de um arquivo xml, e este por sua vez,
	 *  n�o contenha nenhuma produ��o v�lida.
	 *  
	 * Conte�do: O arquivo enviado n�o possui produ��es do Lattes pass�veis de importa��o pelo sistema.
	 * Tipo: ERROR
	 */
	public static final String ARQUIVO_NAO_POSSUI_PRODUCOES = PREFIX + "02";
	
	/**
	 * Mensagem exibida para o docente ao tentar enviar arquivo com o tamanho superior ao definido na
	 * constante {@link ParametrosProdocente#TAMANHO_MAXIMO_ENVIO_ARQUIVO_PRODUCAO}
	 *  
	 * Conte�do: O arquivo a ser inserido deve ter no m�ximo um tamanho de {@link ParametrosProdocente#TAMANHO_MAXIMO_ENVIO_ARQUIVO_PRODUCAO} MB.
	 * Tipo: ERROR
	 */
	public static final String TAMANHO_ARQUIVO_PRODUCAO_EXCEDE = PREFIX + "03";
	
}

/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 12/03/2010
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface para as constantes de mensagens do M�dulo Projetos que ser�o exibidas para o usu�rio.
 * 
 * @author Tiago Monteiro
 *
 */
public interface MensagensProjetos {
	
	/**
	 * Prefixo para estas mensagens.
	 */
	static final String prefix = Sistema.SIGAA + "_" + SigaaSubsistemas.PROJETOS.getId() + "_";
	
	/**
	 * Mensagem exibida quando n�o foi selecionado a categoria do servidor inserido 
	 * como membro de uma equipe organizadora de projetos.
	 *  
	 * Conte�do: Selecione uma categoria de membro da equipe: Docente, Servidor ou Externo.
	 * Tipo: ERROR
	 */
	public static final String SELECIONE_CATEGORIA_MEMBRO_EQUIPE = prefix + "01";
	
	/**
	 * Mensagem exibida quando o coordenador de um projeto de pesquisa 
	 * ou de monitoria n�o � um docente.
	 *  
	 * Conte�do: A coordena��o deste projeto deve ser ocupada por um Docente.
	 * Tipo: ERROR
	 */
	public static final String REGRA_COORDENACAO = prefix + "02";
	
	
	
	
	
}
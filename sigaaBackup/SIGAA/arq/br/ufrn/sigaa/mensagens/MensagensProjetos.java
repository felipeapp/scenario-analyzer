/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 12/03/2010
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface para as constantes de mensagens do Módulo Projetos que serão exibidas para o usuário.
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
	 * Mensagem exibida quando não foi selecionado a categoria do servidor inserido 
	 * como membro de uma equipe organizadora de projetos.
	 *  
	 * Conteúdo: Selecione uma categoria de membro da equipe: Docente, Servidor ou Externo.
	 * Tipo: ERROR
	 */
	public static final String SELECIONE_CATEGORIA_MEMBRO_EQUIPE = prefix + "01";
	
	/**
	 * Mensagem exibida quando o coordenador de um projeto de pesquisa 
	 * ou de monitoria não é um docente.
	 *  
	 * Conteúdo: A coordenação deste projeto deve ser ocupada por um Docente.
	 * Tipo: ERROR
	 */
	public static final String REGRA_COORDENACAO = prefix + "02";
	
	
	
	
	
}
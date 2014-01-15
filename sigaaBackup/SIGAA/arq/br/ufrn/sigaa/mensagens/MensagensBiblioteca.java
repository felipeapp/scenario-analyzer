/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 23/11/2009
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface para as constantes de mensagens do subsistema da biblioteca.
 * 
 * @author Fred de Castro
 */
public interface MensagensBiblioteca {
	
	/**
	 * Prefixo para estas mensagens.
	 */
	static final String prefixo = Sistema.SIGAA + "_" + SigaaSubsistemas.BIBLIOTECA.getId() + "_";
	
	
	/**
	 * Conteúdo: "O usuário não possui suspensões ativas."<br/>
	 * Tipo: Error
	 */
	public static final String USUARIO_SEM_SUSPENSOES_ATIVAS = prefixo + "1";
	
	/**
	 * Conteúdo: "Pelo menos uma suspensão deve ser selecionada."<br/>
	 * Tipo: Error
	 */
	public static final String SELECIONE_UMA_SUSPENSAO = prefixo + "2";
	
	/**
	 * Conteúdo: "Você não está cadastrado para utilizar o sistema das bibliotecas. Realize o cadastro."<br/>
	 * Tipo: Error
	 */
	public static final String USUARIO_NAO_CADASTRADO = prefixo + "3";
	
	/**
	 * Conteúdo: "Usuário não possui empréstimos ativos."<br/>
	 * Tipo: Warning
	 */
	public static final String USUARIO_SEM_EMPRESTIMOS_ATIVOS = prefixo + "4";
	
	/**
	 * Conteúdo: "Nenhum empréstimo foi selecionado para renovação."<br/>
	 * Tipo: Error
	 */
	public static final String NENHUM_EMPRESTIMO_SELECIONADO = prefixo + "5";
	
	/**
	 * Conteúdo: "Empréstimos renovados com sucesso!"<br/>
	 * Tipo: Information
	 */
	public static final String EMPRESTIMOS_RENOVADOS = prefixo + "6";
	
	
//	/**
//	 * Conteúdo: "Forneça o nome do arquivo de exportação para a FGV."<br/>
//	 * Tipo: Erro
//	 */
//	public static final String NOME_ARQUIVO_EXPORTACAO_FGV_NAO_INFORMADO = prefixo+ "7";
	
	/**
	 * Conteúdo: "A classe Black deve iniciar com a letra 'D' e ser seguida por um número. Ex.: D1, D2..., D1/9"<br/>
	 * Tipo: Erro
	 */
	public static final String CLASSE_BLACK_ERRADA = prefixo + "8";
	
	/**
	 * Conteúdo: "A faixa de classes deve iniciar e finalizar na mesma classe."<br/>
	 * Tipo: Erro
	 */
	public static final String CLASSE_INICIAL_E_FINAL_DEVEM_SER_IGUAIS = prefixo + "9";


	/**
	 * Conteúdo: "Não existem usuários com empréstimos em atraso no momento."<br/>
	 * Tipo: Information
	 */
	static final String NENHUM_EMPRESTIMO_ATRASADO = prefixo + "10";
	
}

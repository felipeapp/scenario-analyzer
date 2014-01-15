/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
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
	 * Conte�do: "O usu�rio n�o possui suspens�es ativas."<br/>
	 * Tipo: Error
	 */
	public static final String USUARIO_SEM_SUSPENSOES_ATIVAS = prefixo + "1";
	
	/**
	 * Conte�do: "Pelo menos uma suspens�o deve ser selecionada."<br/>
	 * Tipo: Error
	 */
	public static final String SELECIONE_UMA_SUSPENSAO = prefixo + "2";
	
	/**
	 * Conte�do: "Voc� n�o est� cadastrado para utilizar o sistema das bibliotecas. Realize o cadastro."<br/>
	 * Tipo: Error
	 */
	public static final String USUARIO_NAO_CADASTRADO = prefixo + "3";
	
	/**
	 * Conte�do: "Usu�rio n�o possui empr�stimos ativos."<br/>
	 * Tipo: Warning
	 */
	public static final String USUARIO_SEM_EMPRESTIMOS_ATIVOS = prefixo + "4";
	
	/**
	 * Conte�do: "Nenhum empr�stimo foi selecionado para renova��o."<br/>
	 * Tipo: Error
	 */
	public static final String NENHUM_EMPRESTIMO_SELECIONADO = prefixo + "5";
	
	/**
	 * Conte�do: "Empr�stimos renovados com sucesso!"<br/>
	 * Tipo: Information
	 */
	public static final String EMPRESTIMOS_RENOVADOS = prefixo + "6";
	
	
//	/**
//	 * Conte�do: "Forne�a o nome do arquivo de exporta��o para a FGV."<br/>
//	 * Tipo: Erro
//	 */
//	public static final String NOME_ARQUIVO_EXPORTACAO_FGV_NAO_INFORMADO = prefixo+ "7";
	
	/**
	 * Conte�do: "A classe Black deve iniciar com a letra 'D' e ser seguida por um n�mero. Ex.: D1, D2..., D1/9"<br/>
	 * Tipo: Erro
	 */
	public static final String CLASSE_BLACK_ERRADA = prefixo + "8";
	
	/**
	 * Conte�do: "A faixa de classes deve iniciar e finalizar na mesma classe."<br/>
	 * Tipo: Erro
	 */
	public static final String CLASSE_INICIAL_E_FINAL_DEVEM_SER_IGUAIS = prefixo + "9";


	/**
	 * Conte�do: "N�o existem usu�rios com empr�stimos em atraso no momento."<br/>
	 * Tipo: Information
	 */
	static final String NENHUM_EMPRESTIMO_ATRASADO = prefixo + "10";
	
}

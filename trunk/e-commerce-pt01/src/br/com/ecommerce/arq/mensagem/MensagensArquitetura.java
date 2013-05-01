package br.com.ecommerce.arq.mensagem;

import br.com.ecommerce.arq.dominio.TipoMensagem;

/**
 * Apresenta mensagens que s�o constatemente reusadas pelo sistema.
 * 
 * @author Rodrigo Dutra de Oliveira
 *
 */
public final class MensagensArquitetura {

	/**
	 * Mensagem informando que um determinado campo obrigat�rio n�o foi informado.
	 */
	public static final MensagemArq CAMPO_OBRIGATORIO_NAO_INFORMADO = new MensagemArq("%s: Campo obrigat�rio n�o informado.", 
			TipoMensagem.ERRO);
	
	/**
	 * Mensagem informando que um determinado solicita��o j� havia sido processada.
	 */
	public static final MensagemArq SOLICITACAO_JA_PROCESSADA = new MensagemArq("%s: Opera��o j� havia sido processada.", 
			TipoMensagem.ERRO);
	
	/**
	 * Mensagem informando que um determinado solicita��o j� havia sido processada.
	 */
	public static final MensagemArq OPERACAO_REALIZADA_COM_SUCESSO = new MensagemArq("%s: Opera��o realizada com sucesso.", 
			TipoMensagem.INFORMATION);
	
	/**
	 * Mensagem informando que o elemento n�o se encontra mais no banco de dados.
	 */
	public static final MensagemArq ELEMENTO_NAO_DISPONIVEL_NO_BANCO = new MensagemArq("%s: N�o se encontra mais no banco.", 
			TipoMensagem.INFORMATION);
	
	public static final MensagemArq BUSCA_SEM_RESULTADOS = new MensagemArq("%s: N�o foram encontrados resultados com estes par�metros.", 
			TipoMensagem.ERRO);
	
}

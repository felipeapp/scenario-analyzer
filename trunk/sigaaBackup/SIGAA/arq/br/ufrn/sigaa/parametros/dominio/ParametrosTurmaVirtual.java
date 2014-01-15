package br.ufrn.sigaa.parametros.dominio;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface contendo todos os par�metros da Turma Virtual.
 * @author Fred de Castro
 *
 */
public interface ParametrosTurmaVirtual {
	
	/** Prefixo para os c�digos dos par�metros. */
	public static final String PREFIX = Sistema.SIGAA + "_" + SigaaSubsistemas.PORTAL_TURMA.getId() + "_";
	
	/** Define o tamanho m�ximo para upload de arquivos pelo docente no Porta-Arquivos. */
	public static final String TAMANHO_MAXIMO_ARQUIVO = PREFIX + "1";
	
	/** Tamanho m�ximo padr�o para arquivos enviados pelos discentes. */
	public static final String TAMANHO_MAXIMO_PADRAO_ARQUIVO_ALUNO = PREFIX + "2";
	
	/** Par�metro que indica se � obrigat�rio o docente cadastrar o plano de curso. */
	public static final String PLANO_CURSO_OBRIGATORIO = PREFIX + "3";
	
	/** Par�metro que indica a sigla da Educa��o � Dist�ncia */
	public static final String EAD = PREFIX + "4";
	
	/** Tamanho m�ximo para o t�tulo das mensagens enviadas ao f�rum. */
	public static final String TAMANHO_MAXIMO_TITULO_MENSAGEM_FORUM = PREFIX + "5";
	
	/** Par�metro que indica o diret�rio ra�z onde os arquivos contendo as mensagens do chat ser�o armazenados. */
	public static final String DIRETORIO_RAIZ_CHAT = PREFIX + "6";
	
	/** Par�metro que indica o endere�o do manual da turma virtual para o docente. */
	public static final String LINK_MANUAL_TURMA_VIRTUAL_DOCENTE = PREFIX + "7";
	
	/** Par�metro que indica o endere�o do manual da turma virtual para o discente. */
	public static final String LINK_MANUAL_TURMA_VIRTUAL_DISCENTE = PREFIX + "8";
	
	/** Par�metro que indica o endere�o do conversor de v�deos da turma virtual. */
	public static final String ENDERECO_VIDEO_CONVERTER = PREFIX + "9";
	
	/** Par�metro que indica o consumer key para o aplicativo do twitter da turma virtual. */
	public static final String TWITTER_CONSUMER_KEY = PREFIX + "10";
	
	/** Par�metro que indica o consumer secret para o aplicativo do twitter da turma virtual. */
	public static final String TWITTER_CONSUMER_SECRET = PREFIX + "11";
	
	/**  
	 * <p>TEMPO em milisegundos que o sitema tenta atualizar automaticamente a planilha de frequencia </p>
	 * <p>Caso queira desativar essa funcionalidade coloque o valor  <strong> 0 </strong> ou  <strong>negativo </strong> para esse par�metro.</p>
	 * <p>Exemplo de desativa��o da funcionalidade: <br/><br/>
	 * 		<code>UPDATE comum.parametro set valor= '-1' where codigo = '2_12300_12' </code></p>
	 * */
	public static final String TEMPO_SALVAMENTO_AUTOMATICO_PLANILHA_FREQUENCIA = PREFIX + "12";  //  ex.: 600.000  (10 minutos)
	
	/** Par�metro que indica o endere�o do servidor de v�deo para os v�deo-chats da turma virtual. */
	public static final String ENDERECO_SERVIDOR_VIDEO = PREFIX + 13;
}
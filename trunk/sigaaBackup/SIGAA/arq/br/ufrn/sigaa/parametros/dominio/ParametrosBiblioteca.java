/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 02/06/2009
 *
 */
package br.ufrn.sigaa.parametros.dominio;

import br.ufrn.sigaa.biblioteca.jsf.AbstractConfiguraParametrosBiblioteca;
import br.ufrn.sigaa.biblioteca.jsf.ConfiguraParametrosCirculacaoMBean;
import br.ufrn.sigaa.biblioteca.jsf.ConfiguraParametrosGeraisBibliotecaMBean;
import br.ufrn.sigaa.biblioteca.jsf.ConfiguraParametrosInformacaoReferenciaMBean;
import br.ufrn.sigaa.biblioteca.jsf.ConfiguraParametrosProcessosTecnicosMBean;

/**
 * 
 * <p>Interface contendo todos os par�metros do m�dulo biblioteca.</p>
 * 
 * <p> 
 * 		<strong> 
 * 		*** 
 * 		Se adicionar um novo par�metro e quiser que o usu�rio possa alter�-lo, � preciso 
 * 		adicion�-lo aos casos de uso que realizam essa altera��o (veja as classes relacionada no "See Also").
 * 		*** 
 * 		</strong> 
 * </p>
 * 
 * 
 * <p>SQL para cria��o de um novo par�metro:<br/>
 * 	<pre>
 * 	INSERT INTO comum.parametro (nome, descricao, valor, id_subsistema, id_sistema, codigo)
 * 	VALUES (
 * 	'NOME_VARIAVEL', 
 * 	'descri��o do que a vari�vel cont�m',
 * 	'valor alvo da parametriza��o', 14000, 2, '2_14000_XX');
 * </pre>
 * </p>
 * 
 * 
 * <p>SQL para atualiza��o de um par�metro no banco:<br/>
 * 
 *  <pre>UPDATE comum.parametro set valor= '?' where codigo = '2_14000_XX'</pre>
 * 
 * </p>
 *
 *
 *
 * @author Jean Guerethes
 * @author Jadson
 * @since 31/07/2009
 * @version 1.0 cria��o da classe
 * @see ConfiguraParametrosGeraisBibliotecaMBean
 * @see ConfiguraParametrosInformacaoReferenciaMBean
 * @see ConfiguraParametrosProcessosTecnicosMBean
 * @see ConfiguraParametrosCirculacaoMBean
 * @see AbstractConfiguraParametrosBiblioteca 
 */
public interface ParametrosBiblioteca {

	/** Id da biblioteca Central. */
	public static final String BIBLIOTECA_CENTRAL = "2_14000_1";
	
	/** Prazo EM DIAS utilizado pelo sistema para enviar um email de notifica��es aos usu�rio que est�o com empr�stimos perto de vencer */
	public static final String PRAZO_ENVIO_EMAIL_EMPRESTIMO_VENCENDO = "2_14000_2";
	
	/** Indica se o sistema vai trabalhar com reservas, caso afirmativo vai habilitar os links para os usu�rios solicitarem as reservas
	 * e vai habilitar as verifica��o das regras das reservas nos processadores de empr�stimo e devolu��o.
	 */
	public static final String SISTEMA_TRABALHA_COM_RESERVAS = "2_14000_3"; // Ex.: true
	
	/**
	 * Identifica o formato do material que � de um livro.
	 */
	public static final String FORMATO_MATERIAL_LIVRO = "2_14000_4";
	
	/**
	 * Identifica o formato do material que � de um peri�dico.
	 */
	public static final String FORMATO_MATERIAL_PERIODICO = "2_14000_5"; 

	/**
	 * Indica a quantidade m�xima de reservas online que um usu�rio pode fazer ao mesmo tempo,
	 * para evitar que ele reserve todos os livros do acervo.
	 */
	public static final String QUANTIDADE_MAXIMA_RESERVAS_ONLINE = "2_14000_6"; // Ex.: 5 
	
	
	/**
	 * Indica o prazo EM DIAS que os usu�rio v�o ter para ir na biblioteca e realizar o empr�stimos 
	 * do material que ele tinha reservado e ficou dispon�vel para ele.
	 */
	public static final String PRAZO_EM_DIAS_USUARIO_TEM_PARA_EFETIVAR_RESERVA = "2_14000_7"; // Ex.: 1 dia 
	
	
	/**
	 * Indica que o sistema vai utilizar suspens�es como forma de "regular" os atrasos dos empr�stimos
	 */
	public static final String SISTEMA_TRABALHA_COM_SUSPENSAO = "2_14000_8";
	
	/**
	 * Indica que o sistema vai utilizar multas como forma de "regular" os atrasos dos empr�stimos
	 */
	public static final String SISTEMA_TRABALHA_COM_MULTA = "2_14000_9";
	
	/**
	 * <p>Quantidade m�nima de materiais que um t�tulo deve possuir para o sistema permitir a realiza��o
	 *  da reserva. </p>
	 */
	public static final String QUANTIDADE_MINIMA_MATERIAIS_SOLICITAR_RESERVA = "2_14000_10"; // Ex.: 1 
	
	
	/** Constantes para a coopera��o com a FGV.*/
	public static final String CODIGO_DA_BIBLIOTECA_CATALOGO_COLETIVO = "2_14000_11";     // Ex.: RN
	/** Constantes para a coopera��o com a FGV.	 */
	public static final String VERSAO_DO_PROGRAMA_REGISTRO_ALTERADO = "2_14000_12";       // Ex.: CatBib2.0q
	
	/** A unidade de recebimento da GRU. */
	//C�DICO LIVRE: 2_14000_13: public static final String UNIDADE_RECEBIMENTO_GRU = "2_14000_13";  //ex.: 21 - BIBLIOTECA CENTRAL ZILA MAMEDE / BCZM
	
	/** Constantes para a coopera��o com a FGV. Dados padr�o do campo 040 que precisam ser gerados quando o registro n�o tem. */
	public static final String CODIGO_INSTITUICAO_CATALOGACAO = "2_14000_14";  //ex.: BCZM
	
	/** Constantes para a coopera��o com a FGV. Dados padr�o do campo 040 que precisam ser gerados quando o registro n�o tem. */
	public static final String IDIOMA_CATALOGACAO = "2_14000_15";  //ex.: por
	
	
	/** Constante que define o id do tipo de documento "Outros" */
	public static final String SOLICITACAO_NORMALIZACAO_CATALOGACAO_FONTE_TIPO_DOCUMENTO_OUTROS = "2_14000_16";  //ex.: 99
	
	/** Constante que define a mensagem de autoria que � mostra acima da ficha catalogr�fica gerada pelo sistema */
	public static final String TEXTO_TITULO_FICHA_CATALOGRAFICA = "2_14000_17";  //Apoio ao Usu�rio

	/**
	 * Par�metro que informa a valor da multa que o usu�rio deve pagar por dia que ele atrasar o empr�stimo
	 */
	public static final String VALOR_MULTA_USUARIO_ATRASO_POR_DIA = "2_14000_18"; // ex.: 0,50
	
	/**
	 * <p>Parametro que informa a valor da multa que o usu�rio deve pagar se atrasar o empr�stimo 
	 * num peri�do inferior a 24h.</p>
	 * 
	 * <p>S� utilizado para os empr�timos que possuem o prazo contado em horas, de acordo com a sua pol�tica.</p> 
	 */
	public static final String VALOR_MULTA_USUARIO_ATRASO_POR_HORA = "2_14000_19"; // ex.: 0,10
	
	
	/**  Mensagem remotas enviadas para o opera��o do m�dulo de circula��o */
	public static final String MENSAGEM_REMOTA_ERRO_PADRAO = "2_14000_20";                            // ex.: "Desculpe-nos um erro inesperado ocorreu durante a execu��o desta opera��o. Se o problema persistir, contate o suporte."
	/**  Mensagem remotas enviadas para o opera��o do m�dulo de circula��o */
	public static final String MENSAGEM_REMOTA_ERRO_SEM_AUTORIZACAO_DESFAZER_OPERACAO = "2_14000_21"; // ex.: "O usu�rio usado para autorizar opera��o n�o possui o perfil de chefe da se��o de circula��o."
	/**  Mensagem remotas enviadas para o opera��o do m�dulo de circula��o */
	public static final String MENSAGEM_REMOTA_ERRO_LOGON_DESKTOP = "2_14000_22";                     // ex.: "Ocorreu um problema ao tentar se identificar. Se o problema persistir, contate o suporte."
	
	
	/** Descri��o do sistema que ser� impresso nas etiquetas de lombada e c�digo de barras */
	public static final String DESCRICAO_SUB_SISTEMA_BIBLIOTECA = "2_14000_23";    // ex.: SISTEMA DE BIBLIOTECAS INTEGRADAS
	
	
	/** C�digos que s�o usados para gerar o n�mero do controle na exporta��o para a FGV */
	public static final String CODIGO_NUMERO_CONTROLE_BIBLIOGRAFICO = "2_14000_24";  // ex.: RN
	/** C�digos que s�o usados para gerar o n�mero do controle na exporta��o para a FGV */
	public static final String CODIGO_NUMERO_CONTROLE_AUTORIDADES = "2_14000_25";  //   ex.: YY
	
	
	/**  
	 * <p>TEMPO em milisegundos que o sitema tenta atualizar automaticamente a cataloga��o que o bibliotec�rio est� trabalhando no momento </p>
	 * <p>Caso queira desativar essa funcionalidade coloque o valor  <strong> 0 </strong> ou  <strong>negativo </strong> para esse par�metro.</p>
	 * <p>Exemplo de desativa��o da funcionalidade: <br/><br/>
	 * 		<code>UPDATE comum.parametro set valor= '-1' where codigo = '2_14000_26' </code></p>
	 * */
	public static final String TEMPO_SALVAMENTO_AUTOMATICO_CATALOGACAO = "2_14000_26";  //  ex.: 600.000  (10 minutos) 
	
	/**
	 * Parametriza a mensagem a ser exibida na janela sobre do desktop.
	 */
	public static final String MENSAGEM_JANELA_SOBRE_DESKTOP = "2_14000_27";
	
	/**
	 * Endere�o para o manual para realizar as opera��es do m�dulo desktop 
	 */
	public static final String LINK_MANUAL_EMPRESTIMOS_DESKTOP = "2_14000_28"; // ex.: "http://www.info.ufrn.br/wikisistemas/doku.php?id=suporte:manuais:sigaa:biblioteca:desktop:circulacao:emprestimo_renovacao_devolucao"
	
	/**
	 * Endere�o para o manual para realizar a opera��o de check no m�dulo desktop 
	 */
	public static final String LINK_MANUAL_CHECKOUT_DESKTOP = "2_14000_29";  // ex.: 	"http://www.info.ufrn.br/wikisistemas/doku.php?id=suporte:manuais:sigaa:biblioteca:desktop:circulacao:checkout"
	
	/**
	 * <p>Usu�rio cujo empr�stimo atrase por um per�odo maior de dias que o contido nesse par�metro incorre em irregularidade administrativa que implica, 
	 * enquanto n�o sanada a pend�ncia, em san��es administrativas. </p>
	 * 
	 * <p> Utilize o valor menor ou 0(zero) para desativar essa funcionalidade. </p>
	 * 
	 * <p> RESOLU��O No 028/2010-CONSAD, de 16 de setembro de 2010 </p>
	 * 
	 */
	public static final String PRAZO_USUARIO_INCORRE_IRREGULARIDADE_ADMINISTRATIVA = "2_14000_30";    // ex.:  30 dias.

	/**
	 * <p>Mensagem de alerta que � mostrada no email enviado aos usu�rios que possuem empr�stimos em atraso.</p> 
	 */
	public static final String MENSAGEM_ALERTA_EMAIL_EMPRESTIMOS_EM_ATRASO = "2_14000_31"; 
	// ex.: O usu�rio do Sistema de Bibliotecas da UFRN com atraso superior a 30(trinta) dias incorre em irregularidade administrativa que implica, enquanto n�o sanada a pend�ncia, nas seguintes san��es de acordo com a RESOLU��O No 028/2010-CONSAD, de 16 de setembro de 2010. Leia a resolu��o na �ntegra em: http://www.sigrh.ufrn.br/sigrh/public/colegiados/filtro_busca.jsf 
	
	
	/**
	 * Parametro que informa a quantidade de dias que o usu�rio fica suspenso por dia que ele atrasar o material
	 */
	public static final String QUANTIDADE_DIAS_SUSPENSAO_USUARIO_ATRASO_POR_DIA = "2_14000_32";    // ex.: 3
	
	
	/**
	 * <p>Parametro que informa a quantidade de dias que o usu�rio fica suspenso se atrasar o material 
	 * num peri�do inferior a 24h.</p>
	 * 
	 * <p>S� utilizado para os empr�timos que possuem o prazo contado em horas, de acordo com a sua pol�tica.</p> 
	 */
	public static final String QUANTIDADE_DIAS_SUSPENSAO_USUARIO_ATRASO_POR_HORA = "2_14000_33";    // ex.: 1
	
	/**
	 * Caso algu�m queira mudar os calculos das supens�es no sistema, mude essa classe
	 */
	public static final String NOME_CLASSE_IMPLEMENTA_ESTRATEGIA_SUSPENSAO = "2_14000_34";    // ex.: br.ufrn.sigaa.biblioteca.circulacao.negocio.SuspensaoStrategyDefault
	
	/**
	 * Caso algu�m queira mudar os calculos das multas no sistema, mude essa classe
	 */
	public static final String NOME_CLASSE_IMPLEMENTA_ESTRATEGIA_MULTA = "2_14000_35";    // ex.: br.ufrn.sigaa.biblioteca.circulacao.negocio.MultaStrategyDefault
	
	/**
	 * Caso se utilize um banco diferente do postgres, ser� preciso mudar essa classe
	 */
	public static final String NOME_CLASSE_IMPLEMENTA_GERADOR_PESQUISA_TEXTUAL = "2_14000_36";  // ex.: br.ufrn.sigaa.arq.dao.biblioteca.GeraPesquisaTextualPosgreSQL
	
	/**
	 * Indica qual classe implementa as regras de obten��o do v�nculo que o usu�rio vai utilizar para realizar os empr�stimos
	 */
	public static final String NOME_CLASSE_IMPLEMENTA_ESTRATEGIA_OBTENCAO_VINCULO = "2_14000_37"; // ex.: br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaDefaultStrategy

	/**
	 * Armazena a sigla da unidade federativa da institui��o na forma como ela deve ser impressa na ficha catalogr�fica.
	 */
	public static final String UNIDADE_FEDERAL_IMPRESSO_FICHA_CATALOGRAFICA = "2_14000_38"; // ex.: RN/UF/
	
	/**
	 * <p>C�digo usado para gerar o nome do arquivo de exporta��o para a FGV.</p>
	 * <p>� o mesmo texto utilizando para gerar o c�digo de exporta��o da base biblioteg�rica, mas como eu n�o sei se ser� sempre igual, estou criando 
	 * outro par�metro.
	 * </p> 
	 */
	public static final String CODIGO_GERACAO_NOME_ARQUIVO_FGV = "2_14000_39"; // ex.: RN
	
	/**
	 * <p>O prazo m�nimo EM HORAS que um usu�rio do sistema pode voltar a emprestar o mesmo material. Atribuindo-se um valor menor ou igual a zero 
	 * essa regra vai ser desativada, podendo o usu�rio realizar empr�stimos sucessivos de um mesmo material sem precisar esperar um tempo m�nimo entre eles.</p>
	 */
	public static final String PRAZO_MINIMO_ENTRE_EMPRESTIMOS = "2_14000_40"; // ex.: 24
	
	/**
	 * <p> Cont�m as siglas que estando no campo 090$d, identifi��o uma cataloga��o como sendo uma cataloga��o de uma tese, disserta��o, monografico ou relat�rico acad�mico.</p>
	 * <p> <strong>Observa��o:</strong> As siglas devem est� separadas por v�rgula.</p>
	 * <p> Utilizado pricipalmente para gera��o do formato de refer�ncias dos trabalhos acad�micos do sistema.</p>
	 * <p> Infelizmente n�o existe outra maneira de se obter essa informa��o, a n�o ser a trav�s de texto digitado pelo usu�rio, por isso esses dados foram parametrizados. </p>
	 */
	public static final String SIGLAS_IDENTIFICAO_TRABALHOS_ACADEMICOS = "2_14000_41"; // ex.:  TESE, DISSERT, MONOG, RELAT
	
	/**
	 * <p>C�digo que identifica as bibliotecas do sistema internacionalmente.</p>
	 */
	public static final String CODIGO_IDENTIFICACAO_LIBRARY_OF_CONGRESS = "2_14000_42";
	
	
	/**
	 * <p>Configura se o sistema permite configurar pol�ticas diferentes para cada biblioteca do sistema, em caso afirmativo 
	 * o administrator vai poder cadastrar uma pol�tica para cada biblioteca. Se esse par�metro n�o 
	 * estiver ativo, o sistema vai utilizar apenas as pol�ticas da biblioteca central.</p>
	 */
	public static final String SISTEMA_PERMITE_CONFIGURAR_POLITICAS_DIFERENTES_POR_BIBLIOTECA = "2_14000_43";
	
	
	/**
	 * <p>Informa a quantidade de dias de retardo que o sistema deve considerar para os materiais estarem dispon�veis no acervo a partir 
	 *    do momento em que eles s�o cadastrados no sistema.</p>
	 *    
	 * <p>Utilizado por exemplo na parte da dissemina��o seletiva da informa��o. Para n�o divulgar para os usu�rios materiais que acabaram 
	 * de ser cadastrados no sistema.</p>   
	 */
	public static final String DIAS_RETARDO_MATERIAL_DISPONIVEL_ACERVO = "2_14000_44"; // ex.: 8

	/**
	 * <p>Quantidade m�xima de n�meros de patrim�nio que o usu�rio vai pode selcionar por vez, para incluir os exemplares.</p>
	 * 
	 * <p>J� ocorreu a situa��o de num mesmo termo de tombamento virem 1.800 n�mero de patrim�nio, o bibliotec�rio selecionou 
	 * todos e mandou incluir, neste caso n�o tem condi��es do banco suportar, por isso foi limitado.</p>
	 * 
	 */
	public static final String QUANTIDADE_MAXIMA_EXEMPLARES_INCLUIDA_POR_VEZ = "2_14000_47";
	/**
	 * <p>Informa o texto a ser inserido, no in�cio do "Cadastrar para Utilizar os Servi�os da Biblioteca" como um termo de responsabilidade com
	 *    autentica��o eletr�nica e com possibilidade de impress�o pelas Bibliotecas, onde os usu�rios se comprometem na devolu��o do material
	 *    informacional retirado por  empr�stimo e pelo ressarcimento de eventual preju�zo causado ao patrim�nio da Institui��o.</p>
	 *    
	 * <p>Se esse texto for informado pelo usu�rio, no momento de se cadastrar no sistema vai exibir uma p�gina com essa mensagem e o check box
	 * 	  "concordo com os termos acima"</p>   
	 */
	public static final String TEXTO_DE_ADESAO_AO_SISTEMA_DE_BIBLIOTECAS = "2_14000_45";	
}
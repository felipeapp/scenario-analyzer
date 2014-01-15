/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>Interface contendo todos os parâmetros do módulo biblioteca.</p>
 * 
 * <p> 
 * 		<strong> 
 * 		*** 
 * 		Se adicionar um novo parâmetro e quiser que o usuário possa alterá-lo, é preciso 
 * 		adicioná-lo aos casos de uso que realizam essa alteração (veja as classes relacionada no "See Also").
 * 		*** 
 * 		</strong> 
 * </p>
 * 
 * 
 * <p>SQL para criação de um novo parâmetro:<br/>
 * 	<pre>
 * 	INSERT INTO comum.parametro (nome, descricao, valor, id_subsistema, id_sistema, codigo)
 * 	VALUES (
 * 	'NOME_VARIAVEL', 
 * 	'descrição do que a variável contém',
 * 	'valor alvo da parametrização', 14000, 2, '2_14000_XX');
 * </pre>
 * </p>
 * 
 * 
 * <p>SQL para atualização de um parâmetro no banco:<br/>
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
 * @version 1.0 criação da classe
 * @see ConfiguraParametrosGeraisBibliotecaMBean
 * @see ConfiguraParametrosInformacaoReferenciaMBean
 * @see ConfiguraParametrosProcessosTecnicosMBean
 * @see ConfiguraParametrosCirculacaoMBean
 * @see AbstractConfiguraParametrosBiblioteca 
 */
public interface ParametrosBiblioteca {

	/** Id da biblioteca Central. */
	public static final String BIBLIOTECA_CENTRAL = "2_14000_1";
	
	/** Prazo EM DIAS utilizado pelo sistema para enviar um email de notificações aos usuário que estão com empréstimos perto de vencer */
	public static final String PRAZO_ENVIO_EMAIL_EMPRESTIMO_VENCENDO = "2_14000_2";
	
	/** Indica se o sistema vai trabalhar com reservas, caso afirmativo vai habilitar os links para os usuários solicitarem as reservas
	 * e vai habilitar as verificação das regras das reservas nos processadores de empréstimo e devolução.
	 */
	public static final String SISTEMA_TRABALHA_COM_RESERVAS = "2_14000_3"; // Ex.: true
	
	/**
	 * Identifica o formato do material que é de um livro.
	 */
	public static final String FORMATO_MATERIAL_LIVRO = "2_14000_4";
	
	/**
	 * Identifica o formato do material que é de um periódico.
	 */
	public static final String FORMATO_MATERIAL_PERIODICO = "2_14000_5"; 

	/**
	 * Indica a quantidade máxima de reservas online que um usuário pode fazer ao mesmo tempo,
	 * para evitar que ele reserve todos os livros do acervo.
	 */
	public static final String QUANTIDADE_MAXIMA_RESERVAS_ONLINE = "2_14000_6"; // Ex.: 5 
	
	
	/**
	 * Indica o prazo EM DIAS que os usuário vão ter para ir na biblioteca e realizar o empréstimos 
	 * do material que ele tinha reservado e ficou disponível para ele.
	 */
	public static final String PRAZO_EM_DIAS_USUARIO_TEM_PARA_EFETIVAR_RESERVA = "2_14000_7"; // Ex.: 1 dia 
	
	
	/**
	 * Indica que o sistema vai utilizar suspensões como forma de "regular" os atrasos dos empréstimos
	 */
	public static final String SISTEMA_TRABALHA_COM_SUSPENSAO = "2_14000_8";
	
	/**
	 * Indica que o sistema vai utilizar multas como forma de "regular" os atrasos dos empréstimos
	 */
	public static final String SISTEMA_TRABALHA_COM_MULTA = "2_14000_9";
	
	/**
	 * <p>Quantidade mínima de materiais que um título deve possuir para o sistema permitir a realização
	 *  da reserva. </p>
	 */
	public static final String QUANTIDADE_MINIMA_MATERIAIS_SOLICITAR_RESERVA = "2_14000_10"; // Ex.: 1 
	
	
	/** Constantes para a cooperação com a FGV.*/
	public static final String CODIGO_DA_BIBLIOTECA_CATALOGO_COLETIVO = "2_14000_11";     // Ex.: RN
	/** Constantes para a cooperação com a FGV.	 */
	public static final String VERSAO_DO_PROGRAMA_REGISTRO_ALTERADO = "2_14000_12";       // Ex.: CatBib2.0q
	
	/** A unidade de recebimento da GRU. */
	//CÓDICO LIVRE: 2_14000_13: public static final String UNIDADE_RECEBIMENTO_GRU = "2_14000_13";  //ex.: 21 - BIBLIOTECA CENTRAL ZILA MAMEDE / BCZM
	
	/** Constantes para a cooperação com a FGV. Dados padrão do campo 040 que precisam ser gerados quando o registro não tem. */
	public static final String CODIGO_INSTITUICAO_CATALOGACAO = "2_14000_14";  //ex.: BCZM
	
	/** Constantes para a cooperação com a FGV. Dados padrão do campo 040 que precisam ser gerados quando o registro não tem. */
	public static final String IDIOMA_CATALOGACAO = "2_14000_15";  //ex.: por
	
	
	/** Constante que define o id do tipo de documento "Outros" */
	public static final String SOLICITACAO_NORMALIZACAO_CATALOGACAO_FONTE_TIPO_DOCUMENTO_OUTROS = "2_14000_16";  //ex.: 99
	
	/** Constante que define a mensagem de autoria que é mostra acima da ficha catalográfica gerada pelo sistema */
	public static final String TEXTO_TITULO_FICHA_CATALOGRAFICA = "2_14000_17";  //Apoio ao Usuário

	/**
	 * Parâmetro que informa a valor da multa que o usuário deve pagar por dia que ele atrasar o empréstimo
	 */
	public static final String VALOR_MULTA_USUARIO_ATRASO_POR_DIA = "2_14000_18"; // ex.: 0,50
	
	/**
	 * <p>Parametro que informa a valor da multa que o usuário deve pagar se atrasar o empréstimo 
	 * num periódo inferior a 24h.</p>
	 * 
	 * <p>Só utilizado para os emprétimos que possuem o prazo contado em horas, de acordo com a sua política.</p> 
	 */
	public static final String VALOR_MULTA_USUARIO_ATRASO_POR_HORA = "2_14000_19"; // ex.: 0,10
	
	
	/**  Mensagem remotas enviadas para o operação do módulo de circulação */
	public static final String MENSAGEM_REMOTA_ERRO_PADRAO = "2_14000_20";                            // ex.: "Desculpe-nos um erro inesperado ocorreu durante a execução desta operação. Se o problema persistir, contate o suporte."
	/**  Mensagem remotas enviadas para o operação do módulo de circulação */
	public static final String MENSAGEM_REMOTA_ERRO_SEM_AUTORIZACAO_DESFAZER_OPERACAO = "2_14000_21"; // ex.: "O usuário usado para autorizar operação não possui o perfil de chefe da seção de circulação."
	/**  Mensagem remotas enviadas para o operação do módulo de circulação */
	public static final String MENSAGEM_REMOTA_ERRO_LOGON_DESKTOP = "2_14000_22";                     // ex.: "Ocorreu um problema ao tentar se identificar. Se o problema persistir, contate o suporte."
	
	
	/** Descrição do sistema que será impresso nas etiquetas de lombada e código de barras */
	public static final String DESCRICAO_SUB_SISTEMA_BIBLIOTECA = "2_14000_23";    // ex.: SISTEMA DE BIBLIOTECAS INTEGRADAS
	
	
	/** Códigos que são usados para gerar o número do controle na exportação para a FGV */
	public static final String CODIGO_NUMERO_CONTROLE_BIBLIOGRAFICO = "2_14000_24";  // ex.: RN
	/** Códigos que são usados para gerar o número do controle na exportação para a FGV */
	public static final String CODIGO_NUMERO_CONTROLE_AUTORIDADES = "2_14000_25";  //   ex.: YY
	
	
	/**  
	 * <p>TEMPO em milisegundos que o sitema tenta atualizar automaticamente a catalogação que o bibliotecário está trabalhando no momento </p>
	 * <p>Caso queira desativar essa funcionalidade coloque o valor  <strong> 0 </strong> ou  <strong>negativo </strong> para esse parâmetro.</p>
	 * <p>Exemplo de desativação da funcionalidade: <br/><br/>
	 * 		<code>UPDATE comum.parametro set valor= '-1' where codigo = '2_14000_26' </code></p>
	 * */
	public static final String TEMPO_SALVAMENTO_AUTOMATICO_CATALOGACAO = "2_14000_26";  //  ex.: 600.000  (10 minutos) 
	
	/**
	 * Parametriza a mensagem a ser exibida na janela sobre do desktop.
	 */
	public static final String MENSAGEM_JANELA_SOBRE_DESKTOP = "2_14000_27";
	
	/**
	 * Endereço para o manual para realizar as operações do módulo desktop 
	 */
	public static final String LINK_MANUAL_EMPRESTIMOS_DESKTOP = "2_14000_28"; // ex.: "http://www.info.ufrn.br/wikisistemas/doku.php?id=suporte:manuais:sigaa:biblioteca:desktop:circulacao:emprestimo_renovacao_devolucao"
	
	/**
	 * Endereço para o manual para realizar a operação de check no módulo desktop 
	 */
	public static final String LINK_MANUAL_CHECKOUT_DESKTOP = "2_14000_29";  // ex.: 	"http://www.info.ufrn.br/wikisistemas/doku.php?id=suporte:manuais:sigaa:biblioteca:desktop:circulacao:checkout"
	
	/**
	 * <p>Usuário cujo empréstimo atrase por um período maior de dias que o contido nesse parâmetro incorre em irregularidade administrativa que implica, 
	 * enquanto não sanada a pendência, em sanções administrativas. </p>
	 * 
	 * <p> Utilize o valor menor ou 0(zero) para desativar essa funcionalidade. </p>
	 * 
	 * <p> RESOLUÇÃO No 028/2010-CONSAD, de 16 de setembro de 2010 </p>
	 * 
	 */
	public static final String PRAZO_USUARIO_INCORRE_IRREGULARIDADE_ADMINISTRATIVA = "2_14000_30";    // ex.:  30 dias.

	/**
	 * <p>Mensagem de alerta que é mostrada no email enviado aos usuários que possuem empréstimos em atraso.</p> 
	 */
	public static final String MENSAGEM_ALERTA_EMAIL_EMPRESTIMOS_EM_ATRASO = "2_14000_31"; 
	// ex.: O usuário do Sistema de Bibliotecas da UFRN com atraso superior a 30(trinta) dias incorre em irregularidade administrativa que implica, enquanto não sanada a pendência, nas seguintes sanções de acordo com a RESOLUÇÃO No 028/2010-CONSAD, de 16 de setembro de 2010. Leia a resolução na íntegra em: http://www.sigrh.ufrn.br/sigrh/public/colegiados/filtro_busca.jsf 
	
	
	/**
	 * Parametro que informa a quantidade de dias que o usuário fica suspenso por dia que ele atrasar o material
	 */
	public static final String QUANTIDADE_DIAS_SUSPENSAO_USUARIO_ATRASO_POR_DIA = "2_14000_32";    // ex.: 3
	
	
	/**
	 * <p>Parametro que informa a quantidade de dias que o usuário fica suspenso se atrasar o material 
	 * num periódo inferior a 24h.</p>
	 * 
	 * <p>Só utilizado para os emprétimos que possuem o prazo contado em horas, de acordo com a sua política.</p> 
	 */
	public static final String QUANTIDADE_DIAS_SUSPENSAO_USUARIO_ATRASO_POR_HORA = "2_14000_33";    // ex.: 1
	
	/**
	 * Caso alguém queira mudar os calculos das supensões no sistema, mude essa classe
	 */
	public static final String NOME_CLASSE_IMPLEMENTA_ESTRATEGIA_SUSPENSAO = "2_14000_34";    // ex.: br.ufrn.sigaa.biblioteca.circulacao.negocio.SuspensaoStrategyDefault
	
	/**
	 * Caso alguém queira mudar os calculos das multas no sistema, mude essa classe
	 */
	public static final String NOME_CLASSE_IMPLEMENTA_ESTRATEGIA_MULTA = "2_14000_35";    // ex.: br.ufrn.sigaa.biblioteca.circulacao.negocio.MultaStrategyDefault
	
	/**
	 * Caso se utilize um banco diferente do postgres, será preciso mudar essa classe
	 */
	public static final String NOME_CLASSE_IMPLEMENTA_GERADOR_PESQUISA_TEXTUAL = "2_14000_36";  // ex.: br.ufrn.sigaa.arq.dao.biblioteca.GeraPesquisaTextualPosgreSQL
	
	/**
	 * Indica qual classe implementa as regras de obtenção do vínculo que o usuário vai utilizar para realizar os empréstimos
	 */
	public static final String NOME_CLASSE_IMPLEMENTA_ESTRATEGIA_OBTENCAO_VINCULO = "2_14000_37"; // ex.: br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaDefaultStrategy

	/**
	 * Armazena a sigla da unidade federativa da instituição na forma como ela deve ser impressa na ficha catalográfica.
	 */
	public static final String UNIDADE_FEDERAL_IMPRESSO_FICHA_CATALOGRAFICA = "2_14000_38"; // ex.: RN/UF/
	
	/**
	 * <p>Código usado para gerar o nome do arquivo de exportação para a FGV.</p>
	 * <p>É o mesmo texto utilizando para gerar o código de exportação da base bibliotegárica, mas como eu não sei se será sempre igual, estou criando 
	 * outro parâmetro.
	 * </p> 
	 */
	public static final String CODIGO_GERACAO_NOME_ARQUIVO_FGV = "2_14000_39"; // ex.: RN
	
	/**
	 * <p>O prazo mínimo EM HORAS que um usuário do sistema pode voltar a emprestar o mesmo material. Atribuindo-se um valor menor ou igual a zero 
	 * essa regra vai ser desativada, podendo o usuário realizar empréstimos sucessivos de um mesmo material sem precisar esperar um tempo mínimo entre eles.</p>
	 */
	public static final String PRAZO_MINIMO_ENTRE_EMPRESTIMOS = "2_14000_40"; // ex.: 24
	
	/**
	 * <p> Contém as siglas que estando no campo 090$d, identifição uma catalogação como sendo uma catalogação de uma tese, dissertação, monografico ou relatórico acadêmico.</p>
	 * <p> <strong>Observação:</strong> As siglas devem está separadas por vírgula.</p>
	 * <p> Utilizado pricipalmente para geração do formato de referências dos trabalhos acadêmicos do sistema.</p>
	 * <p> Infelizmente não existe outra maneira de se obter essa informação, a não ser a través de texto digitado pelo usuário, por isso esses dados foram parametrizados. </p>
	 */
	public static final String SIGLAS_IDENTIFICAO_TRABALHOS_ACADEMICOS = "2_14000_41"; // ex.:  TESE, DISSERT, MONOG, RELAT
	
	/**
	 * <p>Código que identifica as bibliotecas do sistema internacionalmente.</p>
	 */
	public static final String CODIGO_IDENTIFICACAO_LIBRARY_OF_CONGRESS = "2_14000_42";
	
	
	/**
	 * <p>Configura se o sistema permite configurar políticas diferentes para cada biblioteca do sistema, em caso afirmativo 
	 * o administrator vai poder cadastrar uma política para cada biblioteca. Se esse parâmetro não 
	 * estiver ativo, o sistema vai utilizar apenas as políticas da biblioteca central.</p>
	 */
	public static final String SISTEMA_PERMITE_CONFIGURAR_POLITICAS_DIFERENTES_POR_BIBLIOTECA = "2_14000_43";
	
	
	/**
	 * <p>Informa a quantidade de dias de retardo que o sistema deve considerar para os materiais estarem disponíveis no acervo a partir 
	 *    do momento em que eles são cadastrados no sistema.</p>
	 *    
	 * <p>Utilizado por exemplo na parte da disseminação seletiva da informação. Para não divulgar para os usuários materiais que acabaram 
	 * de ser cadastrados no sistema.</p>   
	 */
	public static final String DIAS_RETARDO_MATERIAL_DISPONIVEL_ACERVO = "2_14000_44"; // ex.: 8

	/**
	 * <p>Quantidade máxima de números de patrimônio que o usuário vai pode selcionar por vez, para incluir os exemplares.</p>
	 * 
	 * <p>Já ocorreu a situação de num mesmo termo de tombamento virem 1.800 número de patrimônio, o bibliotecário selecionou 
	 * todos e mandou incluir, neste caso não tem condições do banco suportar, por isso foi limitado.</p>
	 * 
	 */
	public static final String QUANTIDADE_MAXIMA_EXEMPLARES_INCLUIDA_POR_VEZ = "2_14000_47";
	/**
	 * <p>Informa o texto a ser inserido, no início do "Cadastrar para Utilizar os Serviços da Biblioteca" como um termo de responsabilidade com
	 *    autenticação eletrônica e com possibilidade de impressão pelas Bibliotecas, onde os usuários se comprometem na devolução do material
	 *    informacional retirado por  empréstimo e pelo ressarcimento de eventual prejuízo causado ao patrimônio da Instituição.</p>
	 *    
	 * <p>Se esse texto for informado pelo usuário, no momento de se cadastrar no sistema vai exibir uma página com essa mensagem e o check box
	 * 	  "concordo com os termos acima"</p>   
	 */
	public static final String TEXTO_DE_ADESAO_AO_SISTEMA_DE_BIBLIOTECAS = "2_14000_45";	
}
/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 18/06/2009
 */
package br.ufrn.arq.mensagens;

/**
 * Classe com constantes que fazem referência a mensagens 
 * da arquitetura que serão exibidas em todos os sistemas.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public interface MensagensArquitetura {

	/** Operação realizada com sucesso */
	public static final String OPERACAO_SUCESSO = "3_0_1";
	
	/** Não há objeto informado para remoção */
	public static final String NAO_HA_OBJETO_REMOCAO = "3_0_2";

	/** O objeto selecionado foi removido previamente. */
	public static final String OBJETO_SELECIONADO_FOI_REMOVIDO = "3_0_3";
	
	/** Objeto cadastrado com sucesso. Mensagem aceita um parâmetro, que é colocado no lugar de "Objeto". */
	public static final String CADASTRADO_COM_SUCESSO = "3_0_4";
	
	/** Objeto alterado com sucesso. Mensagem aceita um parâmetro, que é colocado no lugar de "Objeto". */
	public static final String ALTERADO_COM_SUCESSO = "3_0_5";
	
	/** Mensagem exibida quando se tenta fazer uma busca e não se obtem resultados. */
	public static final String BUSCA_SEM_RESULTADOS = "3_0_6";
	
	/** 
	 * Mensagem exibida quando se tenta fazer uma operação porém o usuário não informou um campo obrigatório. Aceita um parâmetro
	 * que corresponde ao nome do campo 
	 */	
	public static final String CAMPO_OBRIGATORIO_NAO_INFORMADO = "3_0_7";
	
	/**
	 * Mensagem que informa ao usuário que a remoção de algum PersistDB foi efetuada com sucesso.
	 * Aceita um parâmetro que corresponde ao nome do objeto removido.
	 */
	public static final String REMOCAO_EFETUADA_COM_SUCESSO = "3_0_8";

	/**
	 * Informa que um determinado campo de um formulário está em um formato inválido. Recebe
	 * como parâmetro o nome do campo.
	 */
	public static final String FORMATO_INVALIDO = "3_0_9";

	/**
	 * Utilizado em campos numéricos. Mensagem informa que um campo deve ser maior que zero. Recebe
	 * como parâmetro o nome do campo.
	 */
	public static final String VALOR_MAIOR_ZERO = "3_0_10";
	
	/**
	 * Utilizado em campos numéricos. Mensagem informa que um campo deve ser maior ou igual a zero. Recebe
	 * como parâmetro o nome do campo.
	 */
	public static final String VALOR_MAIOR_IGUAL_ZERO = "3_0_11";
	
	/**
	 * Utilizado em campos numéricos. Mensagem informa que um campo deve ser maior ou igual a um valor
	 * passado como parâmetro. Recebe como parâmetro o nome do campo e o valor.
	 */
	public static final String VALOR_MAIOR_IGUAL_A = "3_0_12";
	
	/**
	 * Utilizado em campos numéricos. Mensagem informa que um campo deve ser menor ou igual a um valor
	 * passado como parâmetro. Recebe como parâmetro o nome do campo e o valor.
	 */
	public static final String VALOR_MENOR_IGUAL_A = "3_0_13";
	
	/**
	 * Utilizado em campos numéricos. Mensagem informa que um campo deve ser maior ou igual a um valor
	 * passado como parâmetro e menor ou igual a outro parâmetro. Recebe como parâmetro o nome do campo 
	 * e os valores do intervalo.
	 */
	public static final String VALOR_MAIOR_QUE_MENOR_QUE = "3_0_14";
	
	/**
	 * Utilizado em campos do tipo data. Informa que a data deve ser posterior a um valor
	 * passado como parâmetro.
	 */
	public static final String DATA_POSTERIOR_A = "3_0_15";
	
	/**
	 * Utilizado em campos do tipo data. Informa que a data deve ser anterior a um valor
	 * passado como parâmetro.
	 */
	public static final String DATA_ANTERIOR_A = "3_0_16";

	/**
	 * Utilizado em campos do tipo data. Informa que a data deve ser anterior a um valor
	 * passado como parâmetro e posterior a outro parâmetro.
	 */
	public static final String DATA_POSTERIOR_ANTERIOR_A = "3_0_17";
	
	/**
	 * Utilizado em campos do tipo data. Informa que a data deve ser menor que a data de fim
	 * existente no formulário.
	 */
	public static final String DATA_INICIO_MENOR_FIM = "3_0_18";
	
	/** Informa que o conteúdo de um campo é inválido. Recebe como parâmetro o nome do campo. */
	public static final String CONTEUDO_INVALIDO = "3_0_19";
	
	/**
	 * Em um campo do tipo String, informa quando o número de caracteres supera o máximo. Recebe
	 * como parâmetros o nome do campo e o seu tamanho máximo.
	 */
	public static final String MAXIMO_CARACTERES = "3_0_20";

	/** Informa que o campo não pode conter abreviações. Recebe como parâmetro o nome do campo. */
	public static final String CAMPO_SEM_ABREVIACOES = "3_0_21";

	/** Informa que um campo do tipo autocomplete é obrigatório. Recebe como parâmetro o nome do campo. */
	public static final String AUTOCOMPLETE_OBRIGATORIO = "3_0_22";
	
	/** A data de nascimento não pode ser do ano passado como parâmetro. */
	public static final String DATA_NASCIMENTO_DIFERENTE_ANO = "3_0_23";
	
	/** A página inicial deve ser maior que a final. */
	public static final String PAGINA_INICIAL_MAIOR_FINAL = "3_0_24";

	/**
	 * Em um campo do tipo String, informa quando o número de caracteres é inferior ao mínimo. Recebe
	 * como parâmetros o nome do campo e o seu tamanho mínimo.
	 */
	public static final String MINIMO_CARACTERES = "3_0_25";
	
	/**
	 * Utilizado quando ocorre a tentativa de remoção de um objeto que está associado a outros registro na base de dados.
	 */
	public static final String REMOCAO_OBJETO_ASSOCIADO = "3_0_26";
	
	/**
	 * Informa que objeto que está sendo cadastrado já foi cadastrado anteriormente. Recebe como parâmetro o nome da classe em questão. 
	 */
	public static final String OBJETO_JA_CADASTRADO = "3_0_27";
	
	/**
	 * Informa quando o número de dígitos é inferior ao mínimo. Recebe
	 * como parâmetros o nome do campo e o seu tamanho mínimo.
	 */
	public static final String MINIMO_DIGITOS = "3_0_28";
	
	/**
	 * Informa quando o número de dígitos supera o máximo. Recebe
	 * como parâmetros o nome do campo e o seu tamanho máximo.
	 */
	public static final String MAXIMO_DIGITOS = "3_0_29";

	/**
	 * Informa quando o procedimento já foi processado anteriormente.
	 * 
	 * Conteúdo: O procedimento que você tentou realizar já foi processado anteriormente. Para realizá-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.
	 */
	public static final String PROCEDIMENTO_PROCESSADO_ANTERIORMENTE = "3_0_30";
	
	/**
	 * Informa quando o usuário deve selecionar pelo menos uma das opções em uma busca.
	 * 
	 * Conteúdo: Selecione uma das opções para a busca.
	 */
	public static final String SELECIONE_OPCAO_BUSCA = "3_0_31";
	
	/**
	 * Informa quando dois campos devem ter o mesmo conteúdo. Recebe como parâmetro os dois nomes dos campos correpondentes.
	 * 
	 * Conteúdo: Os campos %s e %s devem ser iguais.
	 */
	public static final String CAMPOS_IGUAIS = "3_0_32";
	
	/**
	 * Informa quando já foi executada uma ação em determinado item. Recebe como parâmetro o item em questão e o verbo na 
	 * conjugação Mais-que-perfeito. Ex: Esse(a) convênio já foi cadastrado anteriormente.
	 * 
	 * Conteúdo: Esse(a) %s já foi %s anteriormente.
	 */
	public static final String ACAO_JA_EXECUTADA = "3_0_33";
	
	/**
	 * Informa que o uma busca excedeu o número máximo de resultados e deve ser refinada. Recebe como parâmetro o número máximo de resultado.
	 * Conteúdo: A busca excedeu o número máximo de %s resultados. Favor refine a sua busca.
	 */
	public static final String BUSCA_MAXIMO_RESULTADOS = "3_0_34";

	/**
	 * Informa que o uma URL informada não possui um formato válido. Recebe
	 * como parâmetro o nome do campo.
	 */
	public static final String URL_INVALIDA = "3_0_35";

	/**
	 * Informa que a data passada como parâmetro deve ser anterior ou
	 * igual a outra data passada como parâmetro.
	 */
	public static final String DATA_ANTERIOR_IGUAL = "3_0_36";
	
	/**
	 * Mensagem utilizada para informar ao usuário que o arquivo enviado é inválido.
	 * 
	 * Conteúdo: O arquivo enviado é inválido!
	 */
	public static final String ARQUIVO_UPLOAD_INVALIDO = "3_0_37";
	
	/**
	 * Mensagem utilizada para informar ao usuário que um sitema(SIGAA,SIPAC ou outro) não
	 * está ativo. Logo, tal operação não pode ser realizada.
	 * 
	 * Conteúdo: Esta operação não poderá ser realizada, pois o %s não está ativo.
	 */
	public static final String SISTEMA_DESATIVADO = "3_0_40";
	
	/**
	* Mensagem exibida ao iniciar uma operação que espera um parametro ou objeto já populado.. 
	* Conteúdo: %s não foi selecionado(a). 
	* Tipo: ERROR. <br>	
	*/
	public static final String OBJETO_NAO_FOI_SELECIONADO = "3_0_41";
	
}

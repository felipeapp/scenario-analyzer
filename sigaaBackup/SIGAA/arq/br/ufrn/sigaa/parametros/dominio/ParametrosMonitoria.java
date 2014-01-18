/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/05/2009
 *
 */
package br.ufrn.sigaa.parametros.dominio;

/**
 * Interface contendo todos os par�metros do m�dulo
 * de monitoria.
 * @author David Pereira
 * @author wendell
 */
public interface ParametrosMonitoria {

	/**
	 * Lista (com elementos separados por v�rgula) dos IDs de tipos de bolsas de monitoria 
	 */
	public static final String LISTA_BOLSAS_MONITORIA = "2_11000_1";
	
	/**
	 * Nome do Coordenador did�tico pedag�gico. Utilizado nos certificados.
	 */
	public static final String NOME_COORDENADOR_DIDATICO_PEDAGOGICO = "2_11000_2";

	/**
	 * Nome do Pr�-Reitor de gradua��o. Utilizado nos certificados.
	 */
	public static final String NOME_PRO_REITOR_GRADUACAO = "2_11000_3";
	
	/**
	 * Lista (com elementos separados por v�rgula) dos IDs dos tipos de situa��es de turmas dos componentes curriculares que devem estar presentes no projeto de monitoria.
	 */
	public static final String LISTA_SITUACOES_TURMAS = "2_11000_4";
	
	/**
	 * Identificador da bolsa de monitoria no SIPAC. 
	 */
	public static final String BOLSA_MONITORIA = "2_11000_5";


	/**
	 * Informa o n�mero m�ximo de orienta��es ativas para cada docente do projeto. 
	 */
	public static final String MAXIMO_ORIENTACOES_ATIVAS = "2_11000_6";

	
	/**
	 * Informa o n�mero m�ximo de projetos ativos para cada docente. 
	 */
	public static final String MAXIMO_PROJETOS_ATIVOS = "2_11000_7";

	/**
	 * Informa qual �ndice ser� usado no processo seletivo de monitores. 
	 */
	public static final String INDICE_ACADEMICO_SELECAO_MONITORIA = "2_11000_9";

	/** Nome da unidade did�tico pedag�gico. Utilizado nos certificados. */
	public static final String UNIDADE_DIDATICO_PEDAGOGICO = "2_11000_10"; // ex.:"DIRETORIA DE DESENVOLVIMENTO PEDAG�GICO" 

	/** Nome do cargo ocupado pelo chefe da  "UNIDADE_DIDATICO_PEDAGOGICO". Utilizado nos certificados. */
	public static final String CARGO_DIDATICO_PEDAGOGICO = "2_11000_11"; //   ex.:"DIRETOR(A) DE DESENVOLVIMENTO PEDAG�GICO"
	
	/** Se existe obriga��o no lan�amento da frequencia pelos dicentes de monitoria. */
	public static final String FREQUENCIA_MONITORIA = "2_11000_12"; //   ex.:"DIRETOR(A) DE DESENVOLVIMENTO PEDAG�GICO"
	
	/**
	 * Email para envio de comunicados de desligamento de monitor bolsista da monitoria.
	 */
	public static final String EMAIL_DESLIGAMENTO_MONITORIA = "2_11000_13";
	
	/**
	 * N�mero m�ximo de bolsistas por resumo SID.
	 */
	public static final String NUMERO_MAXIMO_BOLSISTAS_MONITORIA = "2_11000_14";

	/**
	 * Informa o n�mero m�ximo de coordena��oes ativas para cada docente. 
	 */
	public static final String MAXIMO_COORDENACOES_ATIVAS = "2_11000_15";

	/**
	 * Indica se o discente vai ficar impossibilitado de submeter projeto. 
	 */
	public static final String PUNICAO_RELATORIO_FINAL_REPROVADO = "2_11000_16";

}
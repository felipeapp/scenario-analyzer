/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/05/2009
 *
 */
package br.ufrn.sigaa.parametros.dominio;

/**
 * Interface contendo todos os parâmetros do módulo
 * de monitoria.
 * @author David Pereira
 * @author wendell
 */
public interface ParametrosMonitoria {

	/**
	 * Lista (com elementos separados por vírgula) dos IDs de tipos de bolsas de monitoria 
	 */
	public static final String LISTA_BOLSAS_MONITORIA = "2_11000_1";
	
	/**
	 * Nome do Coordenador didático pedagógico. Utilizado nos certificados.
	 */
	public static final String NOME_COORDENADOR_DIDATICO_PEDAGOGICO = "2_11000_2";

	/**
	 * Nome do Pró-Reitor de graduação. Utilizado nos certificados.
	 */
	public static final String NOME_PRO_REITOR_GRADUACAO = "2_11000_3";
	
	/**
	 * Lista (com elementos separados por vírgula) dos IDs dos tipos de situações de turmas dos componentes curriculares que devem estar presentes no projeto de monitoria.
	 */
	public static final String LISTA_SITUACOES_TURMAS = "2_11000_4";
	
	/**
	 * Identificador da bolsa de monitoria no SIPAC. 
	 */
	public static final String BOLSA_MONITORIA = "2_11000_5";


	/**
	 * Informa o número máximo de orientações ativas para cada docente do projeto. 
	 */
	public static final String MAXIMO_ORIENTACOES_ATIVAS = "2_11000_6";

	
	/**
	 * Informa o número máximo de projetos ativos para cada docente. 
	 */
	public static final String MAXIMO_PROJETOS_ATIVOS = "2_11000_7";

	/**
	 * Informa qual índice será usado no processo seletivo de monitores. 
	 */
	public static final String INDICE_ACADEMICO_SELECAO_MONITORIA = "2_11000_9";

	/** Nome da unidade didático pedagógico. Utilizado nos certificados. */
	public static final String UNIDADE_DIDATICO_PEDAGOGICO = "2_11000_10"; // ex.:"DIRETORIA DE DESENVOLVIMENTO PEDAGÓGICO" 

	/** Nome do cargo ocupado pelo chefe da  "UNIDADE_DIDATICO_PEDAGOGICO". Utilizado nos certificados. */
	public static final String CARGO_DIDATICO_PEDAGOGICO = "2_11000_11"; //   ex.:"DIRETOR(A) DE DESENVOLVIMENTO PEDAGÓGICO"
	
	/** Se existe obrigação no lançamento da frequencia pelos dicentes de monitoria. */
	public static final String FREQUENCIA_MONITORIA = "2_11000_12"; //   ex.:"DIRETOR(A) DE DESENVOLVIMENTO PEDAGÓGICO"
	
	/**
	 * Email para envio de comunicados de desligamento de monitor bolsista da monitoria.
	 */
	public static final String EMAIL_DESLIGAMENTO_MONITORIA = "2_11000_13";
	
	/**
	 * Número máximo de bolsistas por resumo SID.
	 */
	public static final String NUMERO_MAXIMO_BOLSISTAS_MONITORIA = "2_11000_14";

	/**
	 * Informa o número máximo de coordenaçãoes ativas para cada docente. 
	 */
	public static final String MAXIMO_COORDENACOES_ATIVAS = "2_11000_15";

	/**
	 * Indica se o discente vai ficar impossibilitado de submeter projeto. 
	 */
	public static final String PUNICAO_RELATORIO_FINAL_REPROVADO = "2_11000_16";

}
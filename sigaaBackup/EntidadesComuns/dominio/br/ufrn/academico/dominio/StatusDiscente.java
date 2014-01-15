/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 06/11/2007
 *
 */
package br.ufrn.academico.dominio;

import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Classe que define os possíveis status de um discente.
 * @author Eric Moura
 */
public class StatusDiscente {

	//Constantes
	/**
	 * STATUS COMUNS A VÁRIOS NÍVEIS DE ENSINO
	 */
	/** Constante que define o discente que está cursando regularmente o curso. */
	public static final int ATIVO = 1;
	/** Constante que define o discente cadastrado no SIGAA, mas ainda não é discente ativo, ou seja, inexiste matrícula em disciplina, concluída ou não, para este discente. */
	public static final int CADASTRADO = 2;
	/** Constante que define o discente concluiu o curso e não está mais ativo. */
	public static final int CONCLUIDO = 3;
	/** Constante que define o discente que está desligado da Instituição por um certo período, retornando ao status ATIVO ao final deste. O tempo que o discente passa sob trancamento não é contabilizado no prazo máximo de conclusão do curso. */
	public static final int TRANCADO = 5;
	/** Constante que define o discente desligado da Instituição, deixando de estar ativo. */
	public static final int CANCELADO = 6;
	/** Constante que define o discente que deixou de ser cadastrado na instituição. Neste caso, é mantido o registro dos dados do discente para fins de histórico/auditoria. */  
	public static final int EXCLUIDO = 10;
	/** Constante que define o discente cadastrado na importação dos candidatos com situação de aprovado, que encontram-se em processo de convocação.*/
	public static final int PENDENTE_CADASTRO = 13;
	/** Constante que define o discente pré-cadastrado no SIGAA, aquele que compareceu à convocação e apresentou os documentos. Após análise da IMD, este discente passará a ser cadastrado. */
	public static final int PRE_CADASTRADO = 15;
	
	
	
	/**
	 * STATUS ESPECÍFICO PARA O NÍVEL GRADUAÇÃO
	 */
	/** Constante que define o discente de graduação matriculado nos componentes curriculares pendentes para integralização do currículo. Em outras palavras, o discente estaria "pagando" as últimas disciplinas do curso. */
	public static final int FORMANDO = 8;
	/** Constante que define o discente de graduação que integralizou todo currículo do curso e aguarda a Colação de Grau. */
	public static final int GRADUANDO = 9;
	
	
	
	/**
	 * STATUS ESPECÍFICO PARA O NÍVEL STRICTO SENSU
	 */
	/** Constante que define o discente de pós-graduação que defendeu a tese/dissertação, sendo aprovado, 
	 * já foi feito a solicitação de homologação do diploma via sistema e está e aguarda a homologação do grau. */
	public static final int EM_HOMOLOGACAO = 11;
	/** Constante que define o discente de pós-graduação que foi aprovado em uma atividade do tipo tese/dissertação porém ainda não teve entrada no processo de homologação. 
	 * Neste status o sistema já permite ao aluno obter outra matrícula no sistema*/
	public static final int DEFENDIDO = 12;
	
	
	
	/**
	 * STATUS ESPECÍFICO PARA O NÍVEL MÉDIO
	 */
	/** Constante que define o discente que está cursando regularmente o curso em Dependência. */
	public static final int ATIVO_DEPENDENCIA = 14;
	
	
	
	/**
	 * STATUS QUE NÃO SÃO MAIS UTILIZADOS PELO SISTEMA.
	 */
	/** Constante que define o discente que extrapolou o tempo  prazo máximo de conclusão do curso e foi desligado da Instituição. */
	@Deprecated
	public static final int JUBILADO = 7;
	/** Constante que define o discente não ativo, mas que possui um vínculo com a Instituição, podendo retornar a qualquer momento. O tempo afastamento é contabilizado no prazo máximo de conclusão do curso. */
	@Deprecated
	public static final int AFASTADO = 4;
	
	
	/** Chave primária. */
	private int id;
	
	/** Descrição do status. */
	private String descricao;

	/** Indica se i status informado é um tipo de afastamento.
	 * @param s
	 * @return
	 */
	public static boolean isAfastamento(int s) {
		return getAfastamentos().contains(s);
	}
	
	/** Indica se i status informado é um tipo de afastamento permanente.
	 * @param s
	 * @return
	 */
	public static boolean isAfastamentoPermanente(int s) {
		return getAfastamentosPermanentes().contains(s);
	}

	/** Retorna uma coleção de status que indicam afastamento do discente. 
	 * @return
	 */
	public static Collection<Integer> getAfastamentos() {
		Collection<Integer> status = new ArrayList<Integer>(0);
		status.add(CONCLUIDO);
		//status.add(AFASTADO);
		status.add(TRANCADO);
		status.add(CANCELADO);
		//status.add(JUBILADO);
		return status;
	}

	/** Retorna uma coleção de status que indicam afastamento permanente do discente.
	 * @return
	 */
	public static Collection<Integer> getAfastamentosPermanentes() {
		Collection<Integer> status = new ArrayList<Integer>(0);
		status.add(CONCLUIDO);
		status.add(CANCELADO);
		//status.add(JUBILADO);
		status.add(EXCLUIDO);
		return status;
	}

	/** Retorna uma coleção de status que indicam afastamento temporário do discente.
	 * @return
	 */
	public static Collection<Integer> getAfastamentosNaoPermanentes() {
		Collection<Integer> status = new ArrayList<Integer>(0);
		status.add(AFASTADO);
		status.add(TRANCADO);
		return status;
	}

	/** Retorna uma descrição textual do Status informado.
	 * @param status
	 * @return
	 */
	public static String getDescricao(int status){
		switch (status) {
		case ATIVO:          return "ATIVO";
		case CADASTRADO:     return "CADASTRADO";
		case PRE_CADASTRADO: return "PR…-CADASTRADO";
		case CONCLUIDO:      return "CONCLUÕDO";
		case AFASTADO:       return "AFASTADO";
		case TRANCADO:       return "TRANCADO";
		case CANCELADO:      return "CANCELADO";
		case JUBILADO:       return "JUBILADO";
		case FORMANDO:       return "FORMANDO";
		case GRADUANDO:      return "GRADUANDO";
		case EXCLUIDO:       return "EXCLUÕDO";
		case EM_HOMOLOGACAO: return "EM HOMOLOGA«√O";
		case DEFENDIDO:      return "DEFENDIDO";
		case PENDENTE_CADASTRO:      return "PENDENTE CADASTRO";
		case ATIVO_DEPENDENCIA: return "ATIVO EM DEPEND NCIA";
		default:             return "DESCONHECIDO";
		}
	}

	/** Construtor parametrizado.
	 * @param id
	 * @param descricao
	 */
	public StatusDiscente(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	/** Construtor padrão. */
	public StatusDiscente() {

	}

	/** Construtor parametrizado.
	 * @param id
	 */
	public StatusDiscente(int id) {
		this.id = id;
	}

	/** Retorna todos IDs de status na forma de um array. 
	 * @return
	 */
	public static int[] getTodos(){
		int[] arrStatus = new int[10];
		arrStatus[0] = StatusDiscente.ATIVO;
		arrStatus[1] = StatusDiscente.CADASTRADO;
		arrStatus[2] = StatusDiscente.CONCLUIDO;
		arrStatus[3] = StatusDiscente.TRANCADO;
		arrStatus[4] = StatusDiscente.CANCELADO;
		arrStatus[5] = StatusDiscente.FORMANDO;
		arrStatus[6] = StatusDiscente.GRADUANDO;
		arrStatus[7] = StatusDiscente.EXCLUIDO;
		arrStatus[8] = StatusDiscente.EM_HOMOLOGACAO;
		arrStatus[9] = StatusDiscente.DEFENDIDO;
		return arrStatus;
	}
	
	/** Retorna os IDs de status,na forma de um array, que um discente de graduação pode ter. 
	 * @return
	 */
	public static int[] getTodosGraduacao(){
		int[] arrStatus = new int[8];
		arrStatus[0] = StatusDiscente.ATIVO;
		arrStatus[1] = StatusDiscente.CADASTRADO;
		arrStatus[2] = StatusDiscente.CONCLUIDO;
		arrStatus[3] = StatusDiscente.TRANCADO;
		arrStatus[4] = StatusDiscente.CANCELADO;
		arrStatus[5] = StatusDiscente.FORMANDO;
		arrStatus[6] = StatusDiscente.GRADUANDO;
		arrStatus[7] = StatusDiscente.EXCLUIDO;
		return arrStatus;
	}
	
	/** Retorna os IDs de status,na forma de um array, que um discente de pós graduação pode ter. 
	 * @return
	 */
	public static int[] getTodosStricto(){
		int[] arrStatus = new int[7];
		arrStatus[0] = StatusDiscente.ATIVO;
		arrStatus[1] = StatusDiscente.CONCLUIDO;
		arrStatus[2] = StatusDiscente.TRANCADO;
		arrStatus[3] = StatusDiscente.CANCELADO;
		arrStatus[4] = StatusDiscente.EXCLUIDO;
		arrStatus[5] = StatusDiscente.EM_HOMOLOGACAO;
		arrStatus[6] = StatusDiscente.DEFENDIDO;
		return arrStatus;
	}

	/** Retorna todos IDs de status válidos na forma de um array.
	 * @return
	 */
	public static int[] getValidos(){
		int[] arrStatus = new int[10];
		arrStatus[0] = StatusDiscente.ATIVO;
		arrStatus[1] = StatusDiscente.CADASTRADO;
		arrStatus[2] = StatusDiscente.CONCLUIDO;
		arrStatus[3] = StatusDiscente.TRANCADO;
		arrStatus[4] = StatusDiscente.CANCELADO;
		arrStatus[5] = StatusDiscente.FORMANDO;
		arrStatus[6] = StatusDiscente.GRADUANDO;
		arrStatus[7] = StatusDiscente.EM_HOMOLOGACAO;
		arrStatus[8] = StatusDiscente.DEFENDIDO;
		arrStatus[9] = StatusDiscente.ATIVO_DEPENDENCIA; 
		return arrStatus;
	}

	/** Retorna uma coleção com todos status de discentes.
	 * @return
	 */
	public static Collection<StatusDiscente> getStatusTodos(){

		ArrayList<StatusDiscente> listaStatus = new ArrayList<StatusDiscente>();

		listaStatus.add(new StatusDiscente(ATIVO,          getDescricao(ATIVO         )));
		listaStatus.add(new StatusDiscente(CADASTRADO,     getDescricao(CADASTRADO    )));
		listaStatus.add(new StatusDiscente(CONCLUIDO,      getDescricao(CONCLUIDO     )));
		//listaStatus.add(new StatusDiscente(AFASTADO,       getDescricao(AFASTADO      )));
		listaStatus.add(new StatusDiscente(TRANCADO,       getDescricao(TRANCADO      )));
		listaStatus.add(new StatusDiscente(CANCELADO,      getDescricao(CANCELADO     )));
		//listaStatus.add(new StatusDiscente(JUBILADO,       getDescricao(JUBILADO      )));
		listaStatus.add(new StatusDiscente(FORMANDO,       getDescricao(FORMANDO      )));
		listaStatus.add(new StatusDiscente(GRADUANDO,      getDescricao(GRADUANDO     )));
		listaStatus.add(new StatusDiscente(EXCLUIDO,       getDescricao(EXCLUIDO      )));
		listaStatus.add(new StatusDiscente(EM_HOMOLOGACAO, getDescricao(EM_HOMOLOGACAO)));
		listaStatus.add(new StatusDiscente(DEFENDIDO,      getDescricao(DEFENDIDO     )));
		listaStatus.add(new StatusDiscente(PENDENTE_CADASTRO, getDescricao(PENDENTE_CADASTRO)));
		listaStatus.add(new StatusDiscente(ATIVO_DEPENDENCIA, getDescricao(ATIVO_DEPENDENCIA)));

		return listaStatus;

	}
	
	/** Retorna uma coleção com todos status de discentes de graduação.
	 * @return
	 */
	public static Collection<StatusDiscente> getStatusTecnico(){
		
		ArrayList<StatusDiscente> listaStatus = new ArrayList<StatusDiscente>();
		
		listaStatus.add(new StatusDiscente(ATIVO,          getDescricao(ATIVO         )));
		listaStatus.add(new StatusDiscente(CADASTRADO,     getDescricao(CADASTRADO    )));
		listaStatus.add(new StatusDiscente(CONCLUIDO,      getDescricao(CONCLUIDO     )));
		listaStatus.add(new StatusDiscente(TRANCADO,       getDescricao(TRANCADO      )));
		listaStatus.add(new StatusDiscente(CANCELADO,      getDescricao(CANCELADO     )));
		listaStatus.add(new StatusDiscente(EXCLUIDO,       getDescricao(EXCLUIDO      )));
		
		return listaStatus;
		
	}
	
	/** Retorna uma coleção com todos status de discentes de graduação.
	 * @return
	 */
	public static Collection<StatusDiscente> getStatusGraduacao(){

		ArrayList<StatusDiscente> listaStatus = new ArrayList<StatusDiscente>();

		listaStatus.add(new StatusDiscente(ATIVO,          getDescricao(ATIVO         )));
		listaStatus.add(new StatusDiscente(CADASTRADO,     getDescricao(CADASTRADO    )));
		listaStatus.add(new StatusDiscente(CONCLUIDO,      getDescricao(CONCLUIDO     )));
		//listaStatus.add(new StatusDiscente(AFASTADO,       getDescricao(AFASTADO      )));
		listaStatus.add(new StatusDiscente(TRANCADO,       getDescricao(TRANCADO      )));
		listaStatus.add(new StatusDiscente(CANCELADO,      getDescricao(CANCELADO     )));
		//listaStatus.add(new StatusDiscente(JUBILADO,       getDescricao(JUBILADO      )));
		listaStatus.add(new StatusDiscente(FORMANDO,       getDescricao(FORMANDO      )));
		listaStatus.add(new StatusDiscente(GRADUANDO,      getDescricao(GRADUANDO     )));
		listaStatus.add(new StatusDiscente(EXCLUIDO,       getDescricao(EXCLUIDO      )));
		listaStatus.add(new StatusDiscente(PENDENTE_CADASTRO, getDescricao(PENDENTE_CADASTRO      )));
		return listaStatus;

	}
	
	/** Retorna uma coleção com todos status de discentes de pós graduação STRICTO SENSU.
	 * @return
	 */
	public static Collection<StatusDiscente> getStatusStricto(){

		ArrayList<StatusDiscente> listaStatus = new ArrayList<StatusDiscente>();

		listaStatus.add(new StatusDiscente(ATIVO,          getDescricao(ATIVO         )));
		//listaStatus.add(new StatusDiscente(CADASTRADO,     getDescricao(CADASTRADO    )));
		listaStatus.add(new StatusDiscente(CONCLUIDO,      getDescricao(CONCLUIDO     )));
		//listaStatus.add(new StatusDiscente(AFASTADO,       getDescricao(AFASTADO      )));
		listaStatus.add(new StatusDiscente(TRANCADO,       getDescricao(TRANCADO      )));
		listaStatus.add(new StatusDiscente(CANCELADO,      getDescricao(CANCELADO     )));
		//listaStatus.add(new StatusDiscente(JUBILADO,       getDescricao(JUBILADO      )));
		listaStatus.add(new StatusDiscente(EXCLUIDO,       getDescricao(EXCLUIDO      )));
		listaStatus.add(new StatusDiscente(EM_HOMOLOGACAO, getDescricao(EM_HOMOLOGACAO)));
		listaStatus.add(new StatusDiscente(DEFENDIDO,      getDescricao(DEFENDIDO     )));

		return listaStatus;

	}
	
	
	/** Retorna uma coleção com todos status de discentes de pós graduação LATO SENSU.
	 * @return
	 */
	public static Collection<StatusDiscente> getStatusLatoSensu(){

		ArrayList<StatusDiscente> listaStatus = new ArrayList<StatusDiscente>();

		listaStatus.add(new StatusDiscente(ATIVO,          getDescricao(ATIVO         )));
		listaStatus.add(new StatusDiscente(CADASTRADO,     getDescricao(CADASTRADO    )));
		listaStatus.add(new StatusDiscente(CONCLUIDO,      getDescricao(CONCLUIDO     )));
		//listaStatus.add(new StatusDiscente(AFASTADO,       getDescricao(AFASTADO      )));
		//listaStatus.add(new StatusDiscente(TRANCADO,       getDescricao(TRANCADO      )));
		listaStatus.add(new StatusDiscente(CANCELADO,      getDescricao(CANCELADO     )));
		//listaStatus.add(new StatusDiscente(JUBILADO,       getDescricao(JUBILADO      )));
		//listaStatus.add(new StatusDiscente(EXCLUIDO,       getDescricao(EXCLUIDO      )));
		//listaStatus.add(new StatusDiscente(EM_HOMOLOGACAO, getDescricao(EM_HOMOLOGACAO)));
		//listaStatus.add(new StatusDiscente(DEFENDIDO,      getDescricao(DEFENDIDO     )));

		return listaStatus;

	}
	
	/** 
	 * Retorna uma coleção com todos status de discentes de Médio.
	 * @return
	 */
	public static Collection<StatusDiscente> getStatusMedio(){

		ArrayList<StatusDiscente> listaStatus = new ArrayList<StatusDiscente>();

		listaStatus.add(new StatusDiscente(ATIVO,             getDescricao(ATIVO)));
		listaStatus.add(new StatusDiscente(ATIVO_DEPENDENCIA, getDescricao(ATIVO_DEPENDENCIA)));
		listaStatus.add(new StatusDiscente(CANCELADO,         getDescricao(CANCELADO)));
		listaStatus.add(new StatusDiscente(CONCLUIDO,         getDescricao(CONCLUIDO)));
		listaStatus.add(new StatusDiscente(EXCLUIDO,          getDescricao(EXCLUIDO)));
		
		return listaStatus;

	}	

	/** Retorna a descrição do status. 
	 * @return descrição do status.
	 */
	public String getDescricao() {
		return descricao;
	}

	/** Seta a descrição do status.
	 * @param descricao descrição do status.
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/** Retorna a chave primária. 
	 * @return Chave primária. 
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave primária. 
	 * @param Chave primária. 
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna os status de um discente ativo. INFLUENCIA NO CÁLCULO DE INTEGRALIZAÇÃO DO DISCENTE.
	 * @return
	 */
	public static Collection<Integer> getAtivos() {
		Collection<Integer> status = new ArrayList<Integer>(3);
		status.add(ATIVO);
		status.add(FORMANDO);
		status.add(GRADUANDO);
		status.add(ATIVO_DEPENDENCIA);
		return status;
	}

	/** Indica se o status informado é de um discente ativo.
	 * @param status
	 * @return
	 */
	public static boolean isAtivo(int status) {
		return getAtivos().contains(status);
	}

	/** Retorna os status de um discente com vínculo à Instituição.
	 * @return
	 */
	public static Collection<Integer> getStatusComVinculo() {
		Collection<Integer> status = new ArrayList<Integer>(0);
		status.add(ATIVO);
		status.add(FORMANDO);
		status.add(GRADUANDO);
		status.add(TRANCADO);
		status.add(CADASTRADO);
		status.add(ATIVO_DEPENDENCIA);
		return status;
	}

	/** Retorna os possíveis status de um discente concluinte
	 * @return
	 */
	public static Collection<Integer> getStatusConcluinte() {
		Collection<Integer> status = new ArrayList<Integer>(0);
		status.add(CONCLUIDO);
		status.add(GRADUANDO);
		return status;
	}
	
	/** Retorna os possíveis status de um discente concluinte para os discentes de stricto Sensu
	 * @return
	 */
	public static Collection<Integer> getStatusConcluinteStricto() {
		Collection<Integer> status = new ArrayList<Integer>(0);
		status.add(CONCLUIDO);
		status.add(DEFENDIDO);
		status.add(EM_HOMOLOGACAO);
		return status;
	}
	
	/** Retorna os status habilitados para realizar matrÌcula online de GraduaÁ„o.
	 * @return
	 */
	public static Collection<Integer> getStatusMatriculavelGraduacao() {
		Collection<Integer> status = new ArrayList<Integer>(0);
		status.add(ATIVO);
		status.add(CADASTRADO);
		status.add(FORMANDO);
		return status;
	}
	 
	/** Retorna uma descrição textual, separado por vírgula, da coleção de status especificada.
	 * @param col
	 * @return
	 */
	public static String getDescricaoStatus( Collection<Integer> col ) {
		StringBuilder status = new StringBuilder("(");
		Integer[] arrStatus = col.toArray( new Integer[0] );

		for (int i = 0; i < arrStatus.length; i++) {
			if (i + 1 == arrStatus.length)
				status.append(StatusDiscente.getDescricao(arrStatus[i]) + ")");
			else
				status.append(StatusDiscente.getDescricao(arrStatus[i]) + ", ");
		}
		return status.toString();
	}


	/** Indica se este status é igual ao status passado como parâmetro.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/** Retorna o código hash deste status.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
	
	/** Retorna uma descrição textual deste status.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id + " - " + descricao;
	}

}

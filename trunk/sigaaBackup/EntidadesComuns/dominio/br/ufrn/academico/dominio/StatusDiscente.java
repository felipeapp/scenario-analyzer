/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Classe que define os poss�veis status de um discente.
 * @author Eric Moura
 */
public class StatusDiscente {

	//Constantes
	/**
	 * STATUS COMUNS A V�RIOS N�VEIS DE ENSINO
	 */
	/** Constante que define o discente que est� cursando regularmente o curso. */
	public static final int ATIVO = 1;
	/** Constante que define o discente cadastrado no SIGAA, mas ainda n�o � discente ativo, ou seja, inexiste matr�cula em disciplina, conclu�da ou n�o, para este discente. */
	public static final int CADASTRADO = 2;
	/** Constante que define o discente concluiu o curso e n�o est� mais ativo. */
	public static final int CONCLUIDO = 3;
	/** Constante que define o discente que est� desligado da Institui��o por um certo per�odo, retornando ao status ATIVO ao final deste. O tempo que o discente passa sob trancamento n�o � contabilizado no prazo m�ximo de conclus�o do curso. */
	public static final int TRANCADO = 5;
	/** Constante que define o discente desligado da Institui��o, deixando de estar ativo. */
	public static final int CANCELADO = 6;
	/** Constante que define o discente que deixou de ser cadastrado na institui��o. Neste caso, � mantido o registro dos dados do discente para fins de hist�rico/auditoria. */  
	public static final int EXCLUIDO = 10;
	/** Constante que define o discente cadastrado na importa��o dos candidatos com situa��o de aprovado, que encontram-se em processo de convoca��o.*/
	public static final int PENDENTE_CADASTRO = 13;
	/** Constante que define o discente pr�-cadastrado no SIGAA, aquele que compareceu � convoca��o e apresentou os documentos. Ap�s an�lise da IMD, este discente passar� a ser cadastrado. */
	public static final int PRE_CADASTRADO = 15;
	
	
	
	/**
	 * STATUS ESPEC�FICO PARA O N�VEL GRADUA��O
	 */
	/** Constante que define o discente de gradua��o matriculado nos componentes curriculares pendentes para integraliza��o do curr�culo. Em outras palavras, o discente estaria "pagando" as �ltimas disciplinas do curso. */
	public static final int FORMANDO = 8;
	/** Constante que define o discente de gradua��o que integralizou todo curr�culo do curso e aguarda a Cola��o de Grau. */
	public static final int GRADUANDO = 9;
	
	
	
	/**
	 * STATUS ESPEC�FICO PARA O N�VEL STRICTO SENSU
	 */
	/** Constante que define o discente de p�s-gradua��o que defendeu a tese/disserta��o, sendo aprovado, 
	 * j� foi feito a solicita��o de homologa��o do diploma via sistema e est� e aguarda a homologa��o do grau. */
	public static final int EM_HOMOLOGACAO = 11;
	/** Constante que define o discente de p�s-gradua��o que foi aprovado em uma atividade do tipo tese/disserta��o por�m ainda n�o teve entrada no processo de homologa��o. 
	 * Neste status o sistema j� permite ao aluno obter outra matr�cula no sistema*/
	public static final int DEFENDIDO = 12;
	
	
	
	/**
	 * STATUS ESPEC�FICO PARA O N�VEL M�DIO
	 */
	/** Constante que define o discente que est� cursando regularmente o curso em Depend�ncia. */
	public static final int ATIVO_DEPENDENCIA = 14;
	
	
	
	/**
	 * STATUS QUE N�O S�O MAIS UTILIZADOS PELO SISTEMA.
	 */
	/** Constante que define o discente que extrapolou o tempo  prazo m�ximo de conclus�o do curso e foi desligado da Institui��o. */
	@Deprecated
	public static final int JUBILADO = 7;
	/** Constante que define o discente n�o ativo, mas que possui um v�nculo com a Institui��o, podendo retornar a qualquer momento. O tempo afastamento � contabilizado no prazo m�ximo de conclus�o do curso. */
	@Deprecated
	public static final int AFASTADO = 4;
	
	
	/** Chave prim�ria. */
	private int id;
	
	/** Descri��o do status. */
	private String descricao;

	/** Indica se i status informado � um tipo de afastamento.
	 * @param s
	 * @return
	 */
	public static boolean isAfastamento(int s) {
		return getAfastamentos().contains(s);
	}
	
	/** Indica se i status informado � um tipo de afastamento permanente.
	 * @param s
	 * @return
	 */
	public static boolean isAfastamentoPermanente(int s) {
		return getAfastamentosPermanentes().contains(s);
	}

	/** Retorna uma cole��o de status que indicam afastamento do discente. 
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

	/** Retorna uma cole��o de status que indicam afastamento permanente do discente.
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

	/** Retorna uma cole��o de status que indicam afastamento tempor�rio do discente.
	 * @return
	 */
	public static Collection<Integer> getAfastamentosNaoPermanentes() {
		Collection<Integer> status = new ArrayList<Integer>(0);
		status.add(AFASTADO);
		status.add(TRANCADO);
		return status;
	}

	/** Retorna uma descri��o textual do Status informado.
	 * @param status
	 * @return
	 */
	public static String getDescricao(int status){
		switch (status) {
		case ATIVO:          return "ATIVO";
		case CADASTRADO:     return "CADASTRADO";
		case PRE_CADASTRADO: return "PR�-CADASTRADO";
		case CONCLUIDO:      return "CONCLU�DO";
		case AFASTADO:       return "AFASTADO";
		case TRANCADO:       return "TRANCADO";
		case CANCELADO:      return "CANCELADO";
		case JUBILADO:       return "JUBILADO";
		case FORMANDO:       return "FORMANDO";
		case GRADUANDO:      return "GRADUANDO";
		case EXCLUIDO:       return "EXCLU�DO";
		case EM_HOMOLOGACAO: return "EM HOMOLOGA��O";
		case DEFENDIDO:      return "DEFENDIDO";
		case PENDENTE_CADASTRO:      return "PENDENTE CADASTRO";
		case ATIVO_DEPENDENCIA: return "ATIVO EM DEPEND�NCIA";
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

	/** Construtor padr�o. */
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
	
	/** Retorna os IDs de status,na forma de um array, que um discente de gradua��o pode ter. 
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
	
	/** Retorna os IDs de status,na forma de um array, que um discente de p�s gradua��o pode ter. 
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

	/** Retorna todos IDs de status v�lidos na forma de um array.
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

	/** Retorna uma cole��o com todos status de discentes.
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
	
	/** Retorna uma cole��o com todos status de discentes de gradua��o.
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
	
	/** Retorna uma cole��o com todos status de discentes de gradua��o.
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
	
	/** Retorna uma cole��o com todos status de discentes de p�s gradua��o STRICTO SENSU.
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
	
	
	/** Retorna uma cole��o com todos status de discentes de p�s gradua��o LATO SENSU.
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
	 * Retorna uma cole��o com todos status de discentes de M�dio.
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

	/** Retorna a descri��o do status. 
	 * @return descri��o do status.
	 */
	public String getDescricao() {
		return descricao;
	}

	/** Seta a descri��o do status.
	 * @param descricao descri��o do status.
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/** Retorna a chave prim�ria. 
	 * @return Chave prim�ria. 
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria. 
	 * @param Chave prim�ria. 
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna os status de um discente ativo. INFLUENCIA NO C�LCULO DE INTEGRALIZA��O DO DISCENTE.
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

	/** Indica se o status informado � de um discente ativo.
	 * @param status
	 * @return
	 */
	public static boolean isAtivo(int status) {
		return getAtivos().contains(status);
	}

	/** Retorna os status de um discente com v�nculo � Institui��o.
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

	/** Retorna os poss�veis status de um discente concluinte
	 * @return
	 */
	public static Collection<Integer> getStatusConcluinte() {
		Collection<Integer> status = new ArrayList<Integer>(0);
		status.add(CONCLUIDO);
		status.add(GRADUANDO);
		return status;
	}
	
	/** Retorna os poss�veis status de um discente concluinte para os discentes de stricto Sensu
	 * @return
	 */
	public static Collection<Integer> getStatusConcluinteStricto() {
		Collection<Integer> status = new ArrayList<Integer>(0);
		status.add(CONCLUIDO);
		status.add(DEFENDIDO);
		status.add(EM_HOMOLOGACAO);
		return status;
	}
	
	/** Retorna os status habilitados para realizar matr�cula online de Gradua��o.
	 * @return
	 */
	public static Collection<Integer> getStatusMatriculavelGraduacao() {
		Collection<Integer> status = new ArrayList<Integer>(0);
		status.add(ATIVO);
		status.add(CADASTRADO);
		status.add(FORMANDO);
		return status;
	}
	 
	/** Retorna uma descri��o textual, separado por v�rgula, da cole��o de status especificada.
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


	/** Indica se este status � igual ao status passado como par�metro.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/** Retorna o c�digo hash deste status.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
	
	/** Retorna uma descri��o textual deste status.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id + " - " + descricao;
	}

}

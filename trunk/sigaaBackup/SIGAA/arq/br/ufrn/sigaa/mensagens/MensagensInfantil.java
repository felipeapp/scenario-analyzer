/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on 20/11/2009
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface para as constantes de mensagens do M�dulo do Ensino Infantil que ser�o exibidas ao usu�rio.
 * 
 * @author Leonardo Campos
 *
 */
public interface MensagensInfantil {

	/**
	 * Prefixo para estas mensagens.
	 */
	static final String prefix = Sistema.SIGAA + "_" + SigaaSubsistemas.INFANTIL.getId() + "_";
	
	/**
	 * Mensagem exibida quando se tenta acessar a opera��o de registro da evolu��o da crian�a sem que o aluno
	 * esteja matriculado em uma turma do ensino infantil
	 * 
	 * Conte�do: O aluno selecionado n�o est� matriculado.
	 */
	public static final String DISCENTE_NAO_MATRICULADO = prefix + "01";
	
	/**
	 * Mensagem exibida quando o professor termina de preencher o formul�rio de evolu��o da crian�a.
	 * 
	 * Conte�do: Registro de evolu��o da crian�a gravado com sucesso!
	 */
	public static final String REGISTRO_EVOLUCAO_CRIANCA_SUCESSO = prefix + "02";
	
	/**
	 * Mensagem exibida quando se tenta confirmar a matr�cula sem selecionar nenhum aluno.
	 * 
	 * Conte�do: Selecione pelo menos um aluno para confirmar a matr�cula na turma.
	 */
	public static final String SELECIONE_ALUNO_MATRICULA = prefix + "03";
	
	/**
	 * Mensagem exibida quando se seleciona um bloco/�rea que n�o possui conte�dos para avaliar durante o registro
	 * de evolu��o da crian�a.
	 * 
	 * Conte�do: O(A) bloco/�rea selecionado(a) n�o possui conte�dos associados.
	 */
	public static final String AREA_NAO_POSSUI_CONTEUDO = prefix + "04";
	
	/**
	 * Mensagem exibida quando um professor � adicionado a uma turma do ensino infantil.
	 * 
	 * Conte�do: Professor adicionado com sucesso.
	 */
	public static final String PROFESSOR_ADICIONADO_COM_SUCESSO = prefix + "10";
	
	/**
	 * Mensagem exibida quando se tenta cadastrar uma turma do ensino infantil sem informar pelo menos
	 * um professor servidor da IES 
	 * 
	 * Conte�do: Entre os professores da turma obrigatoriamente deve haver pelo menos um docente.
	 */
	public static final String PELO_MENOS_UM_DOCENTE_SERVIDOR = prefix + "11";
	
	/**
	 * Mensagem exibida quando se tenta cadastrar uma turma do ensino infantil com um professor que j�
	 * se encontra em outra turma de ensino infantil no mesmo ano
	 * 
	 * Conte�do: Professor(a) %s n�o pode ser cadastrado para essa turma pois j� possui outra no ano de %d. 
	 */
	public static final String PROFESSOR_JA_POSSUI_TURMA = prefix + "12";

	/**
	 * Mensagem exibida quando se tenta cadastrar uma turma do ensino infantil com um professor que j�
	 * se encontra cadaastrado na Turma.
	 * 
	 * Conte�do: Professor(a) %s j� est� presente no corpo docente da Turma. 
	 */
	public static final String PROFESSOR_JA_CASTRADO_NA_TURMA = prefix + "13";
	
	/**
	 * Mensagem exibida quando se tenta matricula um discente em uma turma do ensino infantil j� a capacidade � excedida.
	 * 
	 * Conte�do: Foi poss�vel matricular os Discentes, pois exece a capacidade da turma. 
	 */
	public static final String CAPACIDADE_TURMA_EXCEDIDA = prefix + "14";

}
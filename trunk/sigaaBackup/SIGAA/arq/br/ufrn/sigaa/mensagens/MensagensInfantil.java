/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 20/11/2009
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/**
 * Interface para as constantes de mensagens do Módulo do Ensino Infantil que serão exibidas ao usuário.
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
	 * Mensagem exibida quando se tenta acessar a operação de registro da evolução da criança sem que o aluno
	 * esteja matriculado em uma turma do ensino infantil
	 * 
	 * Conteúdo: O aluno selecionado não está matriculado.
	 */
	public static final String DISCENTE_NAO_MATRICULADO = prefix + "01";
	
	/**
	 * Mensagem exibida quando o professor termina de preencher o formulário de evolução da criança.
	 * 
	 * Conteúdo: Registro de evolução da criança gravado com sucesso!
	 */
	public static final String REGISTRO_EVOLUCAO_CRIANCA_SUCESSO = prefix + "02";
	
	/**
	 * Mensagem exibida quando se tenta confirmar a matrícula sem selecionar nenhum aluno.
	 * 
	 * Conteúdo: Selecione pelo menos um aluno para confirmar a matrícula na turma.
	 */
	public static final String SELECIONE_ALUNO_MATRICULA = prefix + "03";
	
	/**
	 * Mensagem exibida quando se seleciona um bloco/área que não possui conteúdos para avaliar durante o registro
	 * de evolução da criança.
	 * 
	 * Conteúdo: O(A) bloco/área selecionado(a) não possui conteúdos associados.
	 */
	public static final String AREA_NAO_POSSUI_CONTEUDO = prefix + "04";
	
	/**
	 * Mensagem exibida quando um professor é adicionado a uma turma do ensino infantil.
	 * 
	 * Conteúdo: Professor adicionado com sucesso.
	 */
	public static final String PROFESSOR_ADICIONADO_COM_SUCESSO = prefix + "10";
	
	/**
	 * Mensagem exibida quando se tenta cadastrar uma turma do ensino infantil sem informar pelo menos
	 * um professor servidor da IES 
	 * 
	 * Conteúdo: Entre os professores da turma obrigatoriamente deve haver pelo menos um docente.
	 */
	public static final String PELO_MENOS_UM_DOCENTE_SERVIDOR = prefix + "11";
	
	/**
	 * Mensagem exibida quando se tenta cadastrar uma turma do ensino infantil com um professor que já
	 * se encontra em outra turma de ensino infantil no mesmo ano
	 * 
	 * Conteúdo: Professor(a) %s não pode ser cadastrado para essa turma pois já possui outra no ano de %d. 
	 */
	public static final String PROFESSOR_JA_POSSUI_TURMA = prefix + "12";

	/**
	 * Mensagem exibida quando se tenta cadastrar uma turma do ensino infantil com um professor que já
	 * se encontra cadaastrado na Turma.
	 * 
	 * Conteúdo: Professor(a) %s já está presente no corpo docente da Turma. 
	 */
	public static final String PROFESSOR_JA_CASTRADO_NA_TURMA = prefix + "13";
	
	/**
	 * Mensagem exibida quando se tenta matricula um discente em uma turma do ensino infantil já a capacidade é excedida.
	 * 
	 * Conteúdo: Foi possível matricular os Discentes, pois exece a capacidade da turma. 
	 */
	public static final String CAPACIDADE_TURMA_EXCEDIDA = prefix + "14";

}
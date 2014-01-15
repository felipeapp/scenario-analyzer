/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 01/02/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.HashMap;

import org.apache.commons.lang.ArrayUtils;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe auxiliar para controle da utilização do Managed Bean da busca de discentes
 * de graduação.
 *
 * @author Ricardo Wendell
 *
 */
public class OperacaoDiscente {

	/** Nome do Managed Bean responsável pela operação */
	private String mBean;

	/** Nome da operação (Para utilização no título da página de busca do discente) */
	private String nome;

	/** Status dos discentes válidos para a busca de discentes */
	private int[] statusValidos;

	/** Tipos dos discentes válidos para a busca de discentes */
	private int[] tiposValidos;

	// Códigos das Operações
	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.medio.jsf.MatriculaMedioMBean</li>
	 * <li>br.ufrn.sigaa.ensino.jsf.MatriculaHorarioMBean</li>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.ExtrapolarCreditoMBean</li>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.MatriculaGraduacaoMBean</li>
	 * </ul>
	*/
	public static final int MATRICULA = 1;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * </ul>
	*/
	public static final int ATESTADO_MATRICULA = 2;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.AlteracaoDadosDiscenteMBean</li>
	 * </ul>
	*/
	public static final int ALTERACAO_DADOS_DISCENTE = 3;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.AlteracaoStatusMatriculaMBean</li>
	 * </ul>
	*/
	public static final int TRANCAMENTO_MATRICULA = 4;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.RegistroAtividadeMBean</li>
	 * </ul>
	*/
	public static final int REGISTRO_ATIVIDADE = 5;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.jsf.TrancamentoProgramaMBean</li>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.MovimentacaoAlunoMBean</li>
	 * </ul>
	*/
	public static final int AFASTAMENTO_ALUNO = 6;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.MovimentacaoAlunoMBean</li>
	 * </ul>
	*/
	public static final int RETORNO_AFASTAMENTO_ALUNO = 7;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.MovimentacaoAlunoMBean</li>
	 * </ul>
	*/
	public static final int ESTORNO_AFASTAMENTO_ALUNO = 8;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.stricto.jsf.MudancaCurriculoMBean</li>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.MudancaCurricularMBean</li>
	 * </ul>
	*/
	public static final int MUDANCA_CURRICULAR = 9;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.medio.jsf.ObservacaoDiscenteSerieMBean</li>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.ObservacaoDiscenteMBean</li>
	 * </ul>
	*/
	public static final int OBSERVACAO_DISCENTE = 10;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.ParticipacaoDiscenteEnadeMBean</li>
	 * </ul>
	*/
	public static final int PARTICIPACAO_ENADE = 11;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.ProrrogacaoPrazoMBean</li>
	 * </ul>
	*/
	public static final int PRORROGACAO_PRAZO = 12;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.MatriculaGraduacaoMBean</li>
	 * </ul>
	*/
	public static final int MATRICULA_COMPULSORIA = 13;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.MatriculaGraduacaoMBean</li>
	 * </ul>
	*/
	public static final int MATRICULA_FORA_PRAZO = 14;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ead.jsf.TutoriaAlunoMBean</li>
	 * </ul>
	*/
	public static final int TUTORIA_ALUNO_EAD = 15;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.medio.jsf.AtestadoMatriculaMedioMBean</li>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.AtestadoMatriculaMBean</li>
	 * </ul>
	*/
	public static final int EMISSAO_ATESTADO_MATRICULA = 16;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.medio.jsf.HistoricoMedioMBean</li>
	 * <li>br.ufrn.sigaa.ensino.jsf.HistoricoMBean</li>
	 * </ul>
	*/
	public static final int EMISSAO_HISTORICO = 17;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.jsf.RetificacaoMatriculaMBean</li>
	 * </ul>
	*/
	public static final int RETIFICACAO_MATRICULA = 18;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.AlteracaoStatusMatriculaMBean</li>
	 * </ul>
	*/
	public static final int ALTERACAO_STATUS_MATRICULA = 19;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * </ul>
	*/
	public static final int EXCLUSAO_MATRICULA = 20;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * </ul>
	*/
	public static final int FICHA_AVALIACAO_EAD = 21;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.jsf.AproveitamentoComponenteMBean</li>
	 * </ul>
	*/
	public static final int APROVEITAMENTO_COMPONENTE = 22;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.ConsultaSolicitacoesMatricula</li>
	 * <li>br.ufrn.sigaa.jsf.ConsultaDiscenteMBean</li>
	 * </ul>
	*/
	public static final int CONSULTA = 23;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.MatriculaGraduacaoMBean</li>
	 * </ul>
	*/
	public static final int MATRICULA_ALUNO_ESPECIAL = 24;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.HistoricoDiscenteMBean</li>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.AlteracaoDadosDiscenteMBean</li>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.DiscenteGraduacaoMBean</li>
	 * </ul>
	*/
	public static final int HISTORICO_COMPLETO_DISCENTE = 25;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * </ul>
	*/
	public static final int AFASTAMENTO_ALUNO_PASSADO = 26;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * </ul>
	*/
	public static final int REGISTRO_MOBILIDADE_INTERNA = 27;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.medio.jsf.ImplantarHistoricoMedioMBean</li>
	 * <li>br.ufrn.sigaa.ensino.jsf.ImplantarHistoricoMBean</li>
	 * </ul>
	*/
	public static final int IMPLANTAR_HISTORICO = 28;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.medio.jsf.ConsolidacaoIndividualMedioMBean</li>
	 * <li>br.ufrn.sigaa.ensino.jsf.ConsolidacaoIndividualMBean</li>
	 * </ul>
	*/
	public static final int CONSOLIDACAO_INDIVIDUAL = 29;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.MatriculaGraduacaoMBean</li>
	 * </ul>
	*/
	public static final int MATRICULA_ALUNO_FERIAS = 30;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.MovimentacaoAlunoMBean</li>
	 * </ul>
	*/
	public static final int CONCLUIR_ALUNO = 31;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.MovimentacaoAlunoMBean</li>
	 * </ul>
	*/
	public static final int TRANCAR_PROGRAMA = 32;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.MovimentacaoAlunoMBean</li>
	 * </ul>
	*/
	public static final int CANCELAR_PROGRAMA = 33;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.MovimentacaoAlunoMBean</li>
	 * </ul>
	*/
	public static final int CANCELAR_TRANCAMENTO = 34;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.AproveitamentoAutomaticoMBean</li>
	 * </ul>
	*/
	public static final int APROVEITAMENTO_AUTOMATICO = 35;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.jsf.AlterarFormaIngressoMBean</li>
	 * </ul>
	*/
	public static final int ALTERAR_FORMA_INGRESSO = 36;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.RegistroAtividadeMBean</li>
	 * </ul>
	*/
	public static final int EXCLUIR_REGISTRO_ATIVIDADE = 37;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.ProrrogacaoPrazoMBean</li>
	 * </ul>
	*/
	public static final int CANCELAR_PRORROGACAO_PRAZO = 38;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ead.jsf.AlterarPoloDiscenteMBean</li>
	 * </ul>
	*/
	public static final int ALTERAR_POLO_DISCENTE = 39;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.jsf.ExcluirDiscenteMBean</li>
	 * </ul>
	*/
	public static final int EXCLUIR_DISCENTE_GRADUACAO = 40;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.AlteracaoStatusMatriculaMBean</li>
	 * </ul>
	*/
	public static final int CANCELAMENTO_MATRICULA = 41;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * </ul>
	*/
	public static final int TRABALHO_FINAL = 42;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.stricto.jsf.BancaPosMBean</li>
	 * </ul>
	*/
	public static final int BANCA_POS = 43;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.AlteracaoDataColacaoMBean</li>
	 * </ul>
	*/
	public static final int ALTERAR_DATA_COLACAO = 44;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.IntegralizacaoAlunoMigradoMBean</li>
	 * </ul>
	*/
	public static final int INTEGRALIZAR_ALUNO_MIGRADO = 45;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.ConsultaSolicitacoesMatricula</li>
	 * </ul>
	*/
	public static final int CONSULTAR_SOLICITACOES_MATRICULAS = 46;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.AnaliseSolicitacaoMatriculaMBean</li>
	 * </ul>
	*/
	public static final int ANALISE_SOLICITACAO_MATRICULA = 47;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.jsf.AlteracaoStatusDiscenteMBean</li>
	 * </ul>
	*/
	public static final int ALTERACAO_STATUS_DISCENTE = 48;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.MatriculaGraduacaoMBean</li>
	 * </ul>
	*/
	public static final int MATRICULA_ALUNO_CADASTRADO = 49;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.MatriculaGraduacaoMBean</li>
	 * </ul>
	*/
	public static final int SOLICITACAO_MATRICULA = 50;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.MatriculaGraduacaoMBean</li>
	 * </ul>
	*/
	public static final int SOLICITACAO_MATRICULA_DAE = 51;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.MatriculaGraduacaoMBean</li>
	 * </ul>
	*/
	public static final int MATRICULA_TURMAS_NAO_ONLINE = 52;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.stricto.jsf.AproveitamentoCreditoMBean</li>
	 * </ul>
	*/
	public static final int APROVEITAMENTO_CREDITO = 53;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.DeclaracaoVinculoMBean</li>
	 * </ul>
	*/
	public static final int DECLARACAO_VINCULO = 54;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.stricto.jsf.HomologacaoTrabalhoFinalMBean</li>
	 * </ul>
	*/
	public static final int HOMOLOGACAO_TRABALHO_FINAL_STRICTO = 55;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.MovimentacaoAlunoMBean</li>
	 * </ul>
	*/
	public static final int CONCLUIR_ALUNO_STRICTO = 56;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.prodocente.atividades.jsf.TrabalhoFimCursoMBean</li>
	 * </ul>
	*/
	public static final int ALTERAR_TRABALHO_FIM_CURSO = 57;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.stricto.jsf.CertificadoParticipacaoBancaPosMBean</li>
	 * </ul>
	*/
	public static final int CERTIFICADO_PARTICIPACAO_BANCA_POS = 58;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.stricto.jsf.AtaBancaMBean</li>
	 * </ul>
	*/
	public static final int ENVIAR_ATA_BANCA_POS = 59;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.stricto.jsf.DiscenteStrictoMBean</li>
	 * </ul>
	*/
	public static final int ALTERAR_DISCENTE_POS_COORDENACAO = 60;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.OrientacaoAcademicaMBean</li>
	 * </ul>
	*/
	public static final int CADASTRAR_ORIENTADOR_POS = 61;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.stricto.jsf.BancaPosMBean</li>
	 * </ul>
	*/
	public static final int LISTAR_BANCA_POS = 62;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.stricto.jsf.RelatorioHomologacaoTrabalhoFinalMBean</li>
	 * </ul>
	*/
	public static final int RELATORIO_HOMOLOGACAO_STRICTO = 63;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.RecuperarSenhaDiscenteMBean</li>
	 * </ul>
	*/
	public static final int RECUPERACAO_SENHA_DISCENTE = 64;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.assistencia.jsf.BolsaAuxilioMBean</li>
	 * </ul>
	*/
	public static final int SOLICITAR_BOLSA_AUXILIO = 65;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.assistencia.jsf.BolsaAuxilioMBean</li>
	 * </ul>
	*/
	public static final int LISTA_BOLSA_AUXILIO = 66;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.assistencia.jsf.DiasAlimentacaoMBean</li>
	 * </ul>
	*/
	public static final int CADASTRAR_DIAS_ALIMENTACAO = 67;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * </ul>
	*/
	public static final int SOLICITAR_BOLSA_AUXILIO_SIMPLES = 68;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * </ul>
	*/
	public static final int LISTA_DIAS_ALIMENTACAO = 69;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.PlanoMatriculaMBean</li>
	 * </ul>
	*/
	public static final int GERAR_PLANO_MATRICULA = 70;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.ExtrapolarCreditoMBean</li>
	 * </ul>
	*/
	public static final int MATRICULA_EXTRAPOLAR_CREDITO = 71;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ead.jsf.AlterarDadosUsuarioAlunoMBean</li>
	 * </ul>
	*/
	public static final int ALTERAR_DADOS_USUARIO_ALUNO = 72;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.RelatorioIntegralizacaoCurriculoMBean</li>
	 * </ul>
	*/
	public static final int RELATORIO_INTEGRALIZACAO_CURRICULO = 73;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.stricto.jsf.DeclaracaoDefesaMBean</li>
	 * </ul>
	*/
	public static final int BUSCAR_ALUNOS_STRICTO = 74;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * </ul>
	*/
	public static final int SOLICITACAO_DIPLOMA = 75;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.stricto.jsf.BolsaCnpqStrictoMBean</li>
	 * </ul>
	*/
	public static final int CADASTRO_BOLSA_STRICTO = 76;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.stricto.jsf.BancaPosMBean</li>
	 * </ul>
	*/
	public static final int CADASTRO_DEFESA_ALUNO_CONCLUIDO = 77;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.stricto.jsf.TermoAutorizacaoPublicacaoTesesDissertacoesMBean</li>
	 * </ul>
	*/
	public static final int EMISSAO_TEDE = 78;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.assistencia.jsf.AdesaoCadastroUnicoBolsaMBean</li>
	 * </ul>
	*/
	public static final int ALTERAR_DADOS_BOLSISTA_CADASTRO_UNICO = 79;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.jsf.ExcluirDiscenteMBean</li>
	 * </ul>
	*/
	public static final int EXCLUIR_DISCENTE_STRICTO = 80;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.stricto.jsf.DiscenteStrictoMBean</li>
	 * </ul>
	*/
	public static final int ALTERAR_DISCENTE_POS_PPG = 81;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.diploma.jsf.ImpressaoDiplomaMBean</li>
	 * </ul>
	*/
	public static final int IMPRIMIR_DIPLOMA_GRADUACAO = 82;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.diploma.jsf.RegistroDiplomaIndividualMBean</li>
	 * </ul>
	*/
	public static final int REGISTRO_DIPLOMA_INDIVIDUAL = 83;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.infantil.jsf.DiscenteInfantilMBean</li>
	 * </ul>
	*/
	public static final int ALTERAR_DISCENTE_INFANTIL = 84;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * </ul>
	*/
	public static final int EMISSAO_DIPLOMA = 85;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * </ul>
	*/
	public static final int EMISSAO_FORMULARIO_CADASTRO_DISSETACAO = 86;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * </ul>
	*/
	public static final int EMISSAO_DECLARACAO_QUITACAO_BIBLIOTECA = 87;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.infantil.jsf.RegistroEvolucaoCriancaMBean</li>
	 * </ul>
	*/
	public static final int REGISTRO_EVOLUCAO_CRIANCA = 88;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.complexohospitalar.jsf.DiscentesResidenciaMedicaMBean</li>
	 * </ul>
	*/
	public static final int ALTERAR_DISCENTE_RESIDENCIA_MEDICA = 89;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.stricto.jsf.HomologacaoTrabalhoFinalMBean</li>
	 * </ul>
	*/
	public static final int DOCUMENTOS_HOMOLOGACAO_DIPLOMA = 90;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.stricto.reuni.jsf.IndicacaoBolsistaReuniMBean</li>
	 * </ul>
	*/
	public static final int INDICACAO_BOLSISTA_REUNI = 91;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.ProrrogacaoPrazoMBean</li>
	 * </ul>
	*/
	public static final int ANTECIPACAO_PRAZO = 92;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.jsf.IndiceAcademicoMBean</li>
	 * </ul>
	*/
	public static final int RELATORIO_INDICES_DISCENTE = 93;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.jsf.TrancamentoProgramaMBean</li>
	 * </ul>
	*/
	public static final int SOLICITACAO_TRANCAMENTO_PROGRAMA = 94;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.TransferenciaTurmaMBean</li>
	 * </ul>
	*/
	public static final int TRANSFERENCIA_TURMAS_ALUNO = 95;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.struts.DiscenteAction</li>
	 * <li>br.ufrn.sigaa.ensino.tecnico.jsf.DiscenteTecnicoMBean</li>
	 * </ul>
	*/
	public static final int ALTERAR_DISCENTE_TECNICO = 96;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.struts.DiscenteAction</li>
	 * <li>br.ufrn.sigaa.ensino.latosensu.jsf.DiscenteLatoMBean</li>
	 * </ul>
	*/
	public static final int ALTERAR_DISCENTE_LATO = 97;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.jsf.MobilidadeEstudantilMBean</li>
	 * </ul>
	*/
	public static final int MOBILIDADE_ESTUDANTIL = 98;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.estagio.jsf.InteresseOfertaMBean</li>
	 * </ul>
	*/
	public static final int INTERESSE_OFERTA_ESTAGIO = 99;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.nee.jsf.SolicitacaoApoioNeeMBean</li>
	 * </ul>
	*/
	public static final int DISCENTE_NEE = 100;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.nee.jsf.SolicitacaoApoioNeeMBean</li>
	 * </ul>
	*/
	public static final int LISTAR_SOLICITACOES_DISCENTE_NEE = 101;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.jsf.MatriculaHorarioMBean</li>
	 * </ul>
	*/
	public static final int MATRICULA_HORARIO = 102;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.stricto.jsf.MudancaCurriculoMBean</li>
	 * </ul>
	*/
	public static final int MUDANCA_CURRICULAR_STRICTO = 103;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.jsf.BancaDefesaMBean</li>
	 * </ul>
	*/
	public static final int BANCA_DEFESA = 104;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.estagio.jsf.EstagioMBean</li>
	 * </ul>
	*/
	public static final int CADASTRO_ESTAGIO_AVULSO = 105;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * </ul>
	*/
	public static final int CERTIFICADO_PARTICIPACAO_BANCA_DEFESA = 106;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.medio.jsf.DiscenteMedioMBean</li>
	 * </ul>
	*/
	public static final int ALTERAR_DISCENTE_MEDIO = 107;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.medio.jsf.AfastamentoDiscenteMedioMBean</li>
	 * </ul>
	*/
	public static final int AFASTAR_DISCENTE_MEDIO = 108;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.medio.jsf.AfastamentoDiscenteMedioMBean</li>
	 * </ul>
	*/
	public static final int ESTORNO_AFASTAMENTO_DISCENTE_MEDIO = 109;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.medio.jsf.MatriculaMedioMBean</li>
	 * </ul>
	*/
	public static final int MATRICULA_DISCENTE_MEDIO = 110;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * </ul>
	*/
	public static final int CADASTRO_NECESSIDADE_ESPECIAL = 111;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.prodocente.atividades.jsf.EstagioMBean</li>
	 * </ul>
	*/
	public static final int ALTERAR_ESTAGIO = 112;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.medio.jsf.ImplantarHistoricoMedioMBean</li>
	 * </ul>
	*/
	public static final int IMPLANTAR_HISTORICO_DISCENTE_MEDIO = 113;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.medio.jsf.MatriculaMedioMBean</li>
	 * </ul>
	*/
	public static final int MATRICULA_DEPENDENCIA_MEDIO = 114;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.medio.jsf.BoletimMedioMBean</li>
	 * </ul>
	*/
	public static final int EMISSAO_BOLETIM_MEDIO = 115;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.medio.jsf.ObservacaoDiscenteSerieMBean</li>
	 * </ul>
	*/
	public static final int OBSERVACAO_DISCENTE_SERIE = 116;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.medio.jsf.HistoricoMedioMBean</li>
	 * </ul>
	*/
	public static final int EMISSAO_HISTORICO_MEDIO = 117;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.medio.jsf.AtestadoMatriculaMedioMBean</li>
	 * </ul>
	*/
	public static final int EMISSAO_ATESTADO_MATRICULA_MEDIO = 118;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.medio.jsf.ConsolidacaoIndividualMedioMBean</li>
	 * </ul>
	*/
	public static final int CONSOLIDACAO_INDIVIDUAL_MEDIO = 119;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.jsf.DeclaracaoParticipacaoBancaMBean</li>
	 * </ul>
	*/
	public static final int DECLARACAO_PARTICIPACAO_BANCA = 120;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.jsf.ExcluirDiscenteMBean</li>
	 * </ul>
	*/
	public static final int EXCLUIR_DISCENTE_RESIDENCIA = 121;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.MergeDadosDiscenteMBean</li>
	 * </ul>
	*/
	public static final int MERGE_DADOS_DISCENTE = 122;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.nee.jsf.SolicitacaoApoioNeeMBean</li>
	 * </ul>
	*/
	public static final int DISCENTE_INFANTIL_NEE = 123;
	
	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.DiscenteGraduacaoMBean</li>
	 * </ul>
	*/
	public static final int ACRESCIMO_PERFIL_INICIAL = 124;

	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.medio.jsf.AlteracaoStatusMatriculaMedioMBean</li>
	 * </ul>
	*/
	public static final int ALTERACAO_STATUS_DISCIPLINA = 125;
	
	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.medio.jsf.AfastamentoDiscenteMedioMBean</li>
	 * </ul>
	*/
	public static final int CONCLUIR_ALUNO_MEDIO = 126;
	
	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.internacionalizacao.jsf.HistoricoTraducaoMBean</li>
	 * </ul>
	 */
	public static final int INTERNACIONALIZAR_HISTORICO = 127;
	
	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.internacionalizacao.jsf.ElementosDiscenteTraducaoMBean</li>
	 * </ul>
	 */
	public static final int INTERNACIONALIZAR_ELEMENTOS_HISTORICO_DISCENTE = 128;

	
	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.jsf.AcompanhamentoAcademicoDiscenteMBean</li>
	 * </ul>
	 */
	public static final int ACOMPANHAMENTO_ACADEMICO_DISCENTE = 129;
	
	/** Constante utilizada para selecionar discente nos seguinte(s) controller(s):<br/> 
	 * <ul>
	 * <li>br.ufrn.sigaa.ensino.graduacao.jsf.MatricularDiscentePlanoMatriculaMBean</li>
	 * </ul>
	 */
	public static final int MATRICULAR_DISCENTE_PLANO_MATRICULA = 131;
	
	/** HashMap com as Operações Disponíveis */
	private static HashMap<Integer, OperacaoDiscente> mapa;
	static {
		mapa = new HashMap<Integer, OperacaoDiscente>();
		mapa.put(MATRICULA, new OperacaoDiscente("matriculaGraduacao", "Matrícula", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CADASTRADO}));
		mapa.put(ATESTADO_MATRICULA, new OperacaoDiscente("atestadoMatricula", "Emissão de Atestado de Matrícula", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO}));
		mapa.put(ALTERACAO_DADOS_DISCENTE, new OperacaoDiscente("alteracaoDadosDiscente", "Atualizar Dados Pessoais de Discente", null, new int[] {Discente.REGULAR, Discente.ESPECIAL }));

		mapa.put(SOLICITAR_BOLSA_AUXILIO, new OperacaoDiscente("buscarBolsaAuxilioMBean", "Solicitar Bolsa Auxílio", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CADASTRADO} ));
		mapa.put(LISTA_BOLSA_AUXILIO, new OperacaoDiscente("buscarBolsaAuxilioMBean", "Listar Bolsa Auxílio",
				null, new int[] {StatusDiscente.ATIVO, StatusDiscente.AFASTADO, StatusDiscente.CADASTRADO, StatusDiscente.CANCELADO, StatusDiscente.CONCLUIDO,
								StatusDiscente.EXCLUIDO, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO, StatusDiscente.JUBILADO, StatusDiscente.TRANCADO }));

		mapa.put(SOLICITAR_BOLSA_AUXILIO_SIMPLES, new OperacaoDiscente("bolsaAuxilioMBean", "Cadastro Simplificado Bolsa Auxílio",
				null, new int[] {StatusDiscente.ATIVO, StatusDiscente.AFASTADO, StatusDiscente.CADASTRADO, StatusDiscente.CANCELADO, StatusDiscente.CONCLUIDO,
								StatusDiscente.EXCLUIDO, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO, StatusDiscente.JUBILADO, StatusDiscente.TRANCADO }));

		mapa.put(CADASTRAR_DIAS_ALIMENTACAO, new OperacaoDiscente("diasAlimentacaoMBean", "Cadastrar Dias de Alimentação", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO}));
		mapa.put(LISTA_DIAS_ALIMENTACAO, new OperacaoDiscente("diasAlimentacaoMBean", "Listar Dias de Alimentação", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO}));

		mapa.put(REGISTRO_ATIVIDADE, new OperacaoDiscente("registroAtividade", "Registro de Atividade Acadêmica Específica", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CADASTRADO, StatusDiscente.GRADUANDO, StatusDiscente.DEFENDIDO}, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(EXCLUIR_REGISTRO_ATIVIDADE, new OperacaoDiscente("registroAtividade", "Exclusão de Registro de Atividade Acadêmica Específica", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO, StatusDiscente.CADASTRADO}, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(MUDANCA_CURRICULAR, new OperacaoDiscente("mudancaCurricular", "Mudança Curricular", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CADASTRADO, StatusDiscente.GRADUANDO }));
		mapa.put(MUDANCA_CURRICULAR_STRICTO, new OperacaoDiscente("mudancaCurriculo", "Mudança Curricular", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CADASTRADO, StatusDiscente.GRADUANDO }));
		mapa.put(OBSERVACAO_DISCENTE, new OperacaoDiscente("observacaoDiscente", "Cadastro de Observação de Aluno", null, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(PARTICIPACAO_ENADE, new OperacaoDiscente("participacaoDiscenteEnade", "Cadastro de Participação no ENADE", null));
		mapa.put(PRORROGACAO_PRAZO, new OperacaoDiscente("prorrogacao", "Prorrogação de Prazo de Conclusão", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO}));
		mapa.put(MATRICULA_COMPULSORIA, new OperacaoDiscente("matriculaGraduacao", "Matrícula Compulsória", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CADASTRADO, StatusDiscente.GRADUANDO}, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(MATRICULA_FORA_PRAZO, new OperacaoDiscente("matriculaGraduacao", "Matrícula Fora do Prazo", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CADASTRADO}, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(TUTORIA_ALUNO_EAD, new OperacaoDiscente("tutoriaAluno", "Tutoria de Aluno", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CADASTRADO}));
		mapa.put(EMISSAO_ATESTADO_MATRICULA, new OperacaoDiscente("atestadoMatricula", "Emissão de Atestado de Matrícula", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CADASTRADO}, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(EMISSAO_HISTORICO, new OperacaoDiscente("historico", "Emissão de Histórico do Discente", null, new int[] {Discente.ESPECIAL, Discente.REGULAR,Discente.EM_ASSOCIACAO }));
		mapa.put(RETIFICACAO_MATRICULA, new OperacaoDiscente("retificacaoMatricula", "Retificação de Aproveitamento e Consolidação de Turmas", new int[] {StatusDiscente.ATIVO, StatusDiscente.ATIVO_DEPENDENCIA, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO, StatusDiscente.TRANCADO, StatusDiscente.CADASTRADO, StatusDiscente.CONCLUIDO, StatusDiscente.DEFENDIDO}, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(ALTERACAO_STATUS_MATRICULA, new OperacaoDiscente("alteracaoStatusMatricula", "Alteração de Status de Matrícula", null, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(TRANCAMENTO_MATRICULA, new OperacaoDiscente("alteracaoStatusMatricula", "Trancamento de Matrícula", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO}, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(CANCELAR_TRANCAMENTO, new OperacaoDiscente("movimentacaoAluno", "Cancelar Trancamentos", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.TRANCADO}));
		mapa.put(EXCLUSAO_MATRICULA, new OperacaoDiscente("exclusaoMatricula", "Exclusão de Matrícula", null, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(FICHA_AVALIACAO_EAD, new OperacaoDiscente("fichaAvaliacaoEad", "Avaliação Semanal", new int[] { StatusDiscente.ATIVO, StatusDiscente.FORMANDO }));
		mapa.put(APROVEITAMENTO_COMPONENTE, new OperacaoDiscente("aproveitamento", "Aproveitamento de Estudos", new int[] { StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CADASTRADO, StatusDiscente.TRANCADO, StatusDiscente.AFASTADO, StatusDiscente.GRADUANDO, StatusDiscente.CONCLUIDO}, new int[] {Discente.REGULAR }));
		mapa.put(CONSULTA, new OperacaoDiscente("consultaDiscente", "Consulta", new int[] { StatusDiscente.ATIVO, StatusDiscente.FORMANDO }, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(MATRICULA_ALUNO_ESPECIAL, new OperacaoDiscente("matriculaGraduacao", "Matrícula de Aluno Especial", new int[] { StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CADASTRADO }, new int[] {Discente.ESPECIAL }));
		mapa.put(HISTORICO_COMPLETO_DISCENTE, new OperacaoDiscente("historicoDiscente", "Histórico Completo do Discente",  ArrayUtils.addAll(new int [] { StatusDiscente.EXCLUIDO }, StatusDiscente.getValidos()) , new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(REGISTRO_MOBILIDADE_INTERNA, new OperacaoDiscente("registroMobilidadeInterna", "Registro de Mobilidade Interna", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO}));
		mapa.put(IMPLANTAR_HISTORICO, new OperacaoDiscente("implantarHistorico", "Implantar Histórico de Discente", new int[] {StatusDiscente.ATIVO, StatusDiscente.CONCLUIDO, StatusDiscente.CANCELADO}, new int[] {Discente.REGULAR, Discente.ESPECIAL}));
		mapa.put(CONSOLIDACAO_INDIVIDUAL, new OperacaoDiscente("consolidacaoIndividual", "Consolidação de Turma de Discente", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO, StatusDiscente.DEFENDIDO}, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(MATRICULA_ALUNO_FERIAS, new OperacaoDiscente("matriculaGraduacao", "Matrícula Em Turma de Férias", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO}));
		mapa.put(CONCLUIR_ALUNO, new OperacaoDiscente("movimentacaoAluno", "Concluir Aluno", new int[] {StatusDiscente.GRADUANDO}));
		mapa.put(AFASTAMENTO_ALUNO, new OperacaoDiscente("movimentacaoAluno", "Movimentação de Alunos", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CADASTRADO}, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(AFASTAMENTO_ALUNO_PASSADO, new OperacaoDiscente("movimentacaoAluno", "Movimentação de Alunos", new int[] {StatusDiscente.ATIVO, StatusDiscente.AFASTADO, StatusDiscente.FORMANDO, StatusDiscente.TRANCADO, StatusDiscente.CANCELADO, StatusDiscente.JUBILADO}));
		mapa.put(RETORNO_AFASTAMENTO_ALUNO, new OperacaoDiscente("movimentacaoAluno", "Retorno de Afastamentos de Alunos", new int[] {StatusDiscente.AFASTADO, StatusDiscente.TRANCADO, StatusDiscente.CANCELADO, StatusDiscente.JUBILADO}, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(ESTORNO_AFASTAMENTO_ALUNO, new OperacaoDiscente("movimentacaoAluno", "Estorno de Movimentação de Alunos", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CADASTRADO, StatusDiscente.AFASTADO, StatusDiscente.TRANCADO, StatusDiscente.CANCELADO, StatusDiscente.JUBILADO, StatusDiscente.CONCLUIDO, StatusDiscente.DEFENDIDO}, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(TRANCAR_PROGRAMA, new OperacaoDiscente("movimentacaoAluno", "Trancamento de Programa", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.AFASTADO, StatusDiscente.TRANCADO}));
		mapa.put(CANCELAR_PROGRAMA, new OperacaoDiscente("movimentacaoAluno", "Cancelamento de Programa", new int[] {StatusDiscente.ATIVO, StatusDiscente.CADASTRADO, StatusDiscente.AFASTADO, StatusDiscente.FORMANDO, StatusDiscente.TRANCADO}, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(APROVEITAMENTO_AUTOMATICO, new OperacaoDiscente("aproveitamentoAutomatico", "Aproveitamento Automático", new int[] {StatusDiscente.ATIVO, StatusDiscente.CADASTRADO, StatusDiscente.TRANCADO}));
		mapa.put(ALTERAR_FORMA_INGRESSO, new OperacaoDiscente("alterarFormaIngresso", "Alterar Dados de Ingresso", new int[] {StatusDiscente.ATIVO, StatusDiscente.CADASTRADO, StatusDiscente.FORMANDO, StatusDiscente.AFASTADO, StatusDiscente.GRADUANDO, StatusDiscente.TRANCADO}, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(CANCELAR_PRORROGACAO_PRAZO, new OperacaoDiscente("prorrogacao", "Cancelar Prorrogação de Prazo de Conclusão", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO}));
		mapa.put(ALTERAR_POLO_DISCENTE, new OperacaoDiscente("alterarPoloDiscente", "Alterar Pólo de Discente", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO}));
		mapa.put(EXCLUIR_DISCENTE_GRADUACAO, new OperacaoDiscente("excluirDiscente", "Excluir Discente", new int[] {StatusDiscente.CADASTRADO}, new int[] {Discente.REGULAR, Discente.ESPECIAL}));
		mapa.put(EXCLUIR_DISCENTE_STRICTO, new OperacaoDiscente("excluirDiscente", "Excluir Discente", new int[] {StatusDiscente.ATIVO}, new int[] {Discente.REGULAR, Discente.ESPECIAL}));
		mapa.put(CANCELAMENTO_MATRICULA, new OperacaoDiscente("alteracaoStatusMatricula", "Cancelar Matrícula", null, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(TRABALHO_FINAL, new OperacaoDiscente("trabalhoFinalStricto", "Trabalho Final", new int[] {StatusDiscente.ATIVO, StatusDiscente.CADASTRADO}));
		mapa.put(BANCA_POS, new OperacaoDiscente("bancaPos", "Banca de Pós", new int[] {StatusDiscente.ATIVO, StatusDiscente.CADASTRADO, StatusDiscente.EM_HOMOLOGACAO, StatusDiscente.DEFENDIDO}));
		mapa.put(ALTERAR_DATA_COLACAO, new OperacaoDiscente("alteracaoDataColacao", "Alterar Dados de Saída do Aluno", new int[] {StatusDiscente.CONCLUIDO}));
		mapa.put(INTEGRALIZAR_ALUNO_MIGRADO, new OperacaoDiscente("integralizacaoAlunoMigrado", "Integralização de Créditos de Aluno Migrado", new int[] {StatusDiscente.CONCLUIDO}));
		mapa.put(CONSULTAR_SOLICITACOES_MATRICULAS, new OperacaoDiscente("consultaSolicitacoes", "Consultar Solicitações de Matrículas", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CADASTRADO}));
		mapa.put(ANALISE_SOLICITACAO_MATRICULA, new OperacaoDiscente("analiseSolicitacaoMatricula", "Análise de Solicitações de Matrículas", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO}));
		mapa.put(ALTERACAO_STATUS_DISCENTE, new OperacaoDiscente("alteracaoStatusDiscente", "Alterar Status de Discente", new int[] {StatusDiscente.ATIVO, StatusDiscente.CADASTRADO, StatusDiscente.CONCLUIDO, StatusDiscente.TRANCADO, StatusDiscente.CANCELADO, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO, StatusDiscente.EM_HOMOLOGACAO, StatusDiscente.DEFENDIDO, StatusDiscente.ATIVO_DEPENDENCIA,StatusDiscente.PRE_CADASTRADO, StatusDiscente.EXCLUIDO}, null));
		mapa.put(MATRICULA_ALUNO_CADASTRADO, new OperacaoDiscente("matriculaGraduacao", "Matrícula de Aluno Ingressante", new int[] {StatusDiscente.CADASTRADO, StatusDiscente.ATIVO}));
		mapa.put(SOLICITACAO_MATRICULA, new OperacaoDiscente("matriculaGraduacao", "Matrícula OnLine", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO}));
		mapa.put(SOLICITACAO_MATRICULA_DAE, new OperacaoDiscente("matriculaGraduacao", "Matrícula OnLine", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CADASTRADO}));
		mapa.put(MATRICULA_TURMAS_NAO_ONLINE, new OperacaoDiscente("matriculaGraduacao", "Matrícula de Turmas Não OnLine", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO,StatusDiscente.CADASTRADO}));
		mapa.put(APROVEITAMENTO_CREDITO, new OperacaoDiscente("aproveitamentoCredito", "Aproveitamento de Crédito", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO,StatusDiscente.GRADUANDO, StatusDiscente.CADASTRADO}));
		mapa.put(DECLARACAO_VINCULO, new OperacaoDiscente("declaracaoVinculo", "Emissão de Declaração de Vínculo", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO,StatusDiscente.GRADUANDO, StatusDiscente.TRANCADO, StatusDiscente.CADASTRADO}));
		mapa.put(HOMOLOGACAO_TRABALHO_FINAL_STRICTO, new OperacaoDiscente("homologacaoTrabalhoFinal", "Homologar Trabalho Final", new int[] {StatusDiscente.ATIVO, StatusDiscente.DEFENDIDO}));
		mapa.put(CONCLUIR_ALUNO_STRICTO, new OperacaoDiscente("movimentacaoAluno", "Concluir Aluno", new int[] { StatusDiscente.EM_HOMOLOGACAO }));
		mapa.put(ALTERAR_TRABALHO_FIM_CURSO, new OperacaoDiscente("trabalhoFimCurso", "Alterar Trabalho de Fim de Curso", StatusDiscente.getValidos()));
		mapa.put(CERTIFICADO_PARTICIPACAO_BANCA_POS, new OperacaoDiscente("certificadoBancaPos", "Emitir Certificado de Participação em Banca", new int[] { StatusDiscente.ATIVO, StatusDiscente.EM_HOMOLOGACAO, StatusDiscente.CONCLUIDO, StatusDiscente.DEFENDIDO }));
		mapa.put(ENVIAR_ATA_BANCA_POS, new OperacaoDiscente("ataBancaMBean", "Enviar Ata", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.DEFENDIDO}));
		mapa.put(ALTERAR_DISCENTE_POS_COORDENACAO, new OperacaoDiscente("discenteStricto", "Alterar Discente", new int[] {StatusDiscente.ATIVO, StatusDiscente.DEFENDIDO, StatusDiscente.EM_HOMOLOGACAO}, new int[] {Discente.REGULAR, Discente.ESPECIAL, Discente.EM_ASSOCIACAO }));
		mapa.put(ALTERAR_DISCENTE_POS_PPG, new OperacaoDiscente("discenteStricto", "Alterar Discente", null, null));
		mapa.put(CADASTRAR_ORIENTADOR_POS, new OperacaoDiscente("orientacaoAcademica", "Cadastrar Orientador", new int[] {StatusDiscente.ATIVO, StatusDiscente.DEFENDIDO, StatusDiscente.CONCLUIDO}, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(LISTAR_BANCA_POS, new OperacaoDiscente("bancaPos", "Banca de Pós", new int[] {StatusDiscente.ATIVO, StatusDiscente.CADASTRADO, StatusDiscente.CONCLUIDO, StatusDiscente.DEFENDIDO, StatusDiscente.EM_HOMOLOGACAO}));
		mapa.put(RELATORIO_HOMOLOGACAO_STRICTO, new OperacaoDiscente("relatorioHomologacaoStricto", "Comprovante de Solicitação de Homologação", new int[] {StatusDiscente.ATIVO, StatusDiscente.EM_HOMOLOGACAO, StatusDiscente.CONCLUIDO}));
		mapa.put(RECUPERACAO_SENHA_DISCENTE, new OperacaoDiscente("recuperarSenhaDiscenteMBean", "Recuperação de senha de discente", null));
		mapa.put(GERAR_PLANO_MATRICULA, new OperacaoDiscente("planoMatriculaBean", "Plano de Matrícula", StatusDiscente.getTodosGraduacao()));
		mapa.put(MATRICULA_EXTRAPOLAR_CREDITO, new OperacaoDiscente("extrapolarCredito", "Extrapolar Créditos", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO,StatusDiscente.GRADUANDO, StatusDiscente.CADASTRADO} ));
		mapa.put(ALTERAR_DADOS_USUARIO_ALUNO, new OperacaoDiscente("alterarDadosUsuarioAluno", "Alterar Dados do Usuário do Aluno", null));
		mapa.put(RELATORIO_INTEGRALIZACAO_CURRICULO, new OperacaoDiscente("relatorioIntegralizacaoCurriculoMBean", "Relatório de Integralização de Currículo", null));
		mapa.put(BUSCAR_ALUNOS_STRICTO, new OperacaoDiscente("declaracaoDefesaMBean", "Buscar Alunos Stricto", new int[] { StatusDiscente.ATIVO, StatusDiscente.EM_HOMOLOGACAO, StatusDiscente.CONCLUIDO, StatusDiscente.DEFENDIDO }));
		mapa.put(SOLICITACAO_DIPLOMA, new OperacaoDiscente("solicitacaoDiplomaBean", "Solicitar Emissão de Diploma", new int[] {StatusDiscente.GRADUANDO, StatusDiscente.CONCLUIDO}));
		mapa.put(CADASTRO_BOLSA_STRICTO, new OperacaoDiscente("bolsasCnpqStrictoMBean", "Cadastro de Bolsas", new int[] {StatusDiscente.ATIVO, StatusDiscente.EM_HOMOLOGACAO}));
		mapa.put(CADASTRO_DEFESA_ALUNO_CONCLUIDO, new OperacaoDiscente("bancaPos", "Cadastro de Defesa Antiga", new int[] {StatusDiscente.CONCLUIDO}));
		mapa.put(EMISSAO_TEDE, new OperacaoDiscente("termoPublicacaoTD", "Termo de Autorização para Publicação de Teses e Dissertações", new int[] {StatusDiscente.EM_HOMOLOGACAO, StatusDiscente.DEFENDIDO, StatusDiscente.CONCLUIDO}));
		mapa.put(ALTERAR_DADOS_BOLSISTA_CADASTRO_UNICO, new OperacaoDiscente("adesaoCadastroUnico", "Localizar alunos", null));
		mapa.put(IMPRIMIR_DIPLOMA_GRADUACAO, new OperacaoDiscente("impressaoDiploma", "Imprimir Diploma", new int[] {StatusDiscente.CONCLUIDO, StatusDiscente.GRADUANDO}));
		mapa.put(REGISTRO_DIPLOMA_INDIVIDUAL, new OperacaoDiscente("registroDiplomaIndividual", "Registrar Diploma", new int[] {StatusDiscente.CONCLUIDO}));
		mapa.put(ALTERAR_DISCENTE_INFANTIL, new OperacaoDiscente("discenteInfantilMBean", "Atualizar Dados do Discente", new int[] {StatusDiscente.CADASTRADO, StatusDiscente.ATIVO, StatusDiscente.CONCLUIDO}));
		mapa.put(EMISSAO_DIPLOMA, new OperacaoDiscente("requisicaoDiploma", "Emissão de Requisição de Diploma", new int[] {StatusDiscente.EM_HOMOLOGACAO, StatusDiscente.DEFENDIDO }));
		mapa.put(EMISSAO_FORMULARIO_CADASTRO_DISSETACAO, new OperacaoDiscente("relatorioCadastroDissertacao", "Emissão de Formulário de Cadastro de Dissertação/Tese", new int[] {StatusDiscente.EM_HOMOLOGACAO, StatusDiscente.DEFENDIDO }));
		mapa.put(EMISSAO_DECLARACAO_QUITACAO_BIBLIOTECA, new OperacaoDiscente("verificaSituacaoUsuario", "Verificar Situação Usuário / Emitir Declaração de Quitação", StatusDiscente.getValidos()));
		mapa.put(REGISTRO_EVOLUCAO_CRIANCA, new OperacaoDiscente("registroEvolucaoCriancaMBean", "Registro de Evolução da Criança", new int[] {StatusDiscente.ATIVO}));
		mapa.put(ALTERAR_DISCENTE_RESIDENCIA_MEDICA, new OperacaoDiscente("discenteResidenciaMedica", "Alterar Discente", null, null));		
		mapa.put(DOCUMENTOS_HOMOLOGACAO_DIPLOMA, new OperacaoDiscente("homologacaoTrabalhoFinal", "Documentos da Solicitação de Homologação", new int[] {StatusDiscente.EM_HOMOLOGACAO}));		
		mapa.put(INDICACAO_BOLSISTA_REUNI, new OperacaoDiscente("indicacaoBolsistaReuni", "Indicação de Bolsista REUNI", new int[] {StatusDiscente.ATIVO}));
		mapa.put(ANTECIPACAO_PRAZO, new OperacaoDiscente("prorrogacao", "Antecipação de Prazo de Conclusão", new int[] {StatusDiscente.ATIVO}));
		mapa.put(RELATORIO_INDICES_DISCENTE, new OperacaoDiscente("indiceAcademicoMBean", "Relatório de Índices de Aluno", null, null));
		mapa.put(SOLICITACAO_TRANCAMENTO_PROGRAMA, new OperacaoDiscente("trancamentoPrograma", "Trancamento de Programa", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CADASTRADO,StatusDiscente.AFASTADO, StatusDiscente.TRANCADO}));
		mapa.put(TRANSFERENCIA_TURMAS_ALUNO, new OperacaoDiscente("transferenciaTurma", "Transferência de Turmas de Aluno", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CADASTRADO}));
		
		mapa.put(ALTERAR_DISCENTE_TECNICO, new OperacaoDiscente("discenteTecnico", "Dados do Discente Técnico", null, new int[] {}));
		mapa.put(ALTERAR_DISCENTE_LATO, new OperacaoDiscente("discenteLato", "Atualizar Dados do Discente Lato-Sensu", null, new int[] {}));
		mapa.put(MOBILIDADE_ESTUDANTIL, new OperacaoDiscente("mobilidadeEstudantil", "Cadastro de Mobilidade Estudantil", new int[] {StatusDiscente.ATIVO}));
		mapa.put(INTERESSE_OFERTA_ESTAGIO, new OperacaoDiscente("interesseOfertaMBean", "Interesse em Oferta de Estágio", new int[] {StatusDiscente.ATIVO}));
		mapa.put(DISCENTE_NEE, new OperacaoDiscente("solicitacaoApoioNee", "Cadastro de Discentes com Necessidades Educacionais Especiais", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO}));
		mapa.put(LISTAR_SOLICITACOES_DISCENTE_NEE, new OperacaoDiscente("solicitacaoApoioNee", "Lista das solicitações de Apoio a NEE de Discentes", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO}));
		mapa.put(MATRICULA_HORARIO, new OperacaoDiscente("matriculaHorarioMBean", "Matrícula por Horário", new int[] {StatusDiscente.ATIVO}));
		mapa.put(BANCA_DEFESA, new OperacaoDiscente("bancaDefesaMBean", "Banca de Defesa", new int[] {StatusDiscente.ATIVO, StatusDiscente.GRADUANDO, StatusDiscente.FORMANDO, StatusDiscente.CONCLUIDO}));
		mapa.put(CADASTRO_ESTAGIO_AVULSO, new OperacaoDiscente("estagioMBean", "Cadastro de Estágio", new int[] {StatusDiscente.ATIVO}));
		mapa.put(CERTIFICADO_PARTICIPACAO_BANCA_DEFESA, new OperacaoDiscente("certificadoBancaDefesa", "Emitir Certificado de Participação em Banca", new int[] { StatusDiscente.ATIVO, StatusDiscente.GRADUANDO, StatusDiscente.FORMANDO, StatusDiscente.CONCLUIDO }));
		mapa.put(ALTERAR_DISCENTE_MEDIO, new OperacaoDiscente("discenteMedio", "Dados do Discente Médio", null, null));
		mapa.put(AFASTAR_DISCENTE_MEDIO, new OperacaoDiscente("afastamentoDiscenteMedioMBean", "Afastamento de Discente", new int[] {StatusDiscente.ATIVO, StatusDiscente.ATIVO_DEPENDENCIA, StatusDiscente.CANCELADO, StatusDiscente.TRANCADO, StatusDiscente.EXCLUIDO, StatusDiscente.CONCLUIDO}, null));
		mapa.put(ESTORNO_AFASTAMENTO_DISCENTE_MEDIO, new OperacaoDiscente("afastamentoDiscenteMedioMBean", "Estorno de Afastamento de Discente", new int[] {StatusDiscente.ATIVO, StatusDiscente.ATIVO_DEPENDENCIA, StatusDiscente.CANCELADO, StatusDiscente.TRANCADO, StatusDiscente.EXCLUIDO, StatusDiscente.CONCLUIDO}, null));
		mapa.put(MATRICULA_DISCENTE_MEDIO, new OperacaoDiscente("matriculaMedio", "Matrícula de Discente do Nível Médio", new int[] {StatusDiscente.ATIVO, StatusDiscente.ATIVO_DEPENDENCIA}, new int[] {Discente.REGULAR}));
		mapa.put(CADASTRO_NECESSIDADE_ESPECIAL, new OperacaoDiscente("pessoaNecessidadeEspecial", "Cadastro de Necessidades Especiais", null, null));
		mapa.put(ALTERAR_ESTAGIO, new OperacaoDiscente("estagio", "Atualizar Dados do Estágio", null, null));
		mapa.put(IMPLANTAR_HISTORICO_DISCENTE_MEDIO, new OperacaoDiscente("implantarHistoricoMedioMBean", "Implantar Histórico de Discente", new int[] {StatusDiscente.ATIVO, StatusDiscente.ATIVO_DEPENDENCIA, StatusDiscente.CONCLUIDO, StatusDiscente.CANCELADO}));	
		mapa.put(MATRICULA_DEPENDENCIA_MEDIO, new OperacaoDiscente("matriculaMedio", "Matrícula de Discente em Dependência do Nível Médio", new int[] {StatusDiscente.ATIVO, StatusDiscente.ATIVO_DEPENDENCIA}, null));
		mapa.put(EMISSAO_BOLETIM_MEDIO, new OperacaoDiscente("boletimMedioMBean", "Emissão de Boletim", new int[] {StatusDiscente.ATIVO, StatusDiscente.ATIVO_DEPENDENCIA, StatusDiscente.CONCLUIDO, StatusDiscente.CANCELADO, StatusDiscente.TRANCADO}));
		mapa.put(OBSERVACAO_DISCENTE_SERIE, new OperacaoDiscente("observacaoDiscenteSerieMBean", "Editar Observações em Série do Discente", null, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(EMISSAO_HISTORICO_MEDIO, new OperacaoDiscente("historicoMedio", "Emissão de Histórico do Discente", null, new int[] {Discente.ESPECIAL, Discente.REGULAR,Discente.EM_ASSOCIACAO }));
		mapa.put(EMISSAO_ATESTADO_MATRICULA_MEDIO, new OperacaoDiscente("atestadoMatriculaMedio", "Emissão de Atestado de Matrícula", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CADASTRADO, StatusDiscente.ATIVO_DEPENDENCIA}, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(CONSOLIDACAO_INDIVIDUAL_MEDIO, new OperacaoDiscente("consolidacaoIndividualMedio", "Consolidação de Disciplina de Discente", new int[] {StatusDiscente.ATIVO, StatusDiscente.ATIVO_DEPENDENCIA}, new int[] {Discente.REGULAR}));
		mapa.put(DECLARACAO_PARTICIPACAO_BANCA, new OperacaoDiscente("declaracaoParticipacaoBanca", "Emitir Declaração de Participação em Banca", new int[] { StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CONCLUIDO, StatusDiscente.GRADUANDO }));
		mapa.put(EXCLUIR_DISCENTE_RESIDENCIA, new OperacaoDiscente("excluirDiscente", "Excluir Discente", new int[] {StatusDiscente.ATIVO, StatusDiscente.CADASTRADO}, new int[] {Discente.REGULAR}));
		mapa.put(MERGE_DADOS_DISCENTE, new OperacaoDiscente("mergeDadosDiscenteMBean", "Unificar Dados do Discente", StatusDiscente.getTodos(), new int[] {Discente.REGULAR}));
		mapa.put(DISCENTE_INFANTIL_NEE, new OperacaoDiscente("solicitacaoApoioNee", "Cadastro de Discentes com Necessidades Educacionais Especiais", new int[] {StatusDiscente.ATIVO, StatusDiscente.CADASTRADO}));
		mapa.put(ACRESCIMO_PERFIL_INICIAL, new OperacaoDiscente("discenteGraduacao", "Acréscimo de Perfil Inicial", new int[] {StatusDiscente.ATIVO}));
		mapa.put(ALTERACAO_STATUS_DISCIPLINA, new OperacaoDiscente("alteracaoStatusMatriculaMedioMBean", "Alteração de Status de Disciplina", null, new int[] {Discente.REGULAR, Discente.ESPECIAL }));
		mapa.put(CONCLUIR_ALUNO_MEDIO, new OperacaoDiscente("afastamentoDiscenteMedioMBean", "Conclusão de Aluno do Médio", new int[] {StatusDiscente.ATIVO,StatusDiscente.ATIVO_DEPENDENCIA}));		
		mapa.put(INTERNACIONALIZAR_HISTORICO, new OperacaoDiscente("historicoTraducaoMBean", "Internacionalizar Histórico", null, null));		
		mapa.put(INTERNACIONALIZAR_ELEMENTOS_HISTORICO_DISCENTE, new OperacaoDiscente("elementosDiscenteTraducaoMBean", "Internacionalizar Elementos do Discente", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CONCLUIDO, StatusDiscente.GRADUANDO }, null));
		mapa.put(ACOMPANHAMENTO_ACADEMICO_DISCENTE, new OperacaoDiscente("acompanhamentoAcademicoDiscenteMBean", "Acompanhamento Acadêmico Discente", new int[] {StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CADASTRADO,StatusDiscente.CONCLUIDO } ));
		mapa.put(MATRICULAR_DISCENTE_PLANO_MATRICULA, new OperacaoDiscente("matricularDiscentePlanoMatriculaMBean", "Matricular Discente Usando Plano de Matrículas", new int[] {StatusDiscente.CADASTRADO}));
	}

	public OperacaoDiscente() {
	}

	public OperacaoDiscente(String mBean, String nome, int[] statusValidos) {
		this.mBean = mBean;
		this.nome = nome;
		this.statusValidos = statusValidos;
		this.tiposValidos = new int[]{ Discente.REGULAR };
	}

	public OperacaoDiscente(String mBean, String nome, int[] statusValidos, int[] tiposValidos) {
		this.mBean = mBean;
		this.nome = nome;
		this.statusValidos = statusValidos;
		this.tiposValidos = tiposValidos;
	}

	/**
	 * Retorna a operação que está em uso.
	 * @param codigoOperacao
	 * @return
	 */
	public static OperacaoDiscente getOperacao(int codigoOperacao) {
		return mapa.get(codigoOperacao);
	}

	public String getMBean() {
		return mBean;
	}

	public int[] getStatusValidos() {
		return statusValidos;
	}

	public String getNome() {
		return nome;
	}

	public void setMBean(String bean) {
		mBean = bean;
	}

	public void setStatusValidos(int[] statusValidos) {
		this.statusValidos = statusValidos;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int[] getTiposValidos() {
		return tiposValidos;
	}

	public void setTiposValidos(int[] tiposValidos) {
		this.tiposValidos = tiposValidos;
	}

	/**
	 * Retorna a descrição dos status válidos para serem utilizados na 
	 * mensagem ao usuário nos casos em que não encontra nenhum discente
	 * @return
	 */
	public String getDescricaoStatusValidos() {
		StringBuilder status = new StringBuilder("(");
		int[] arrStatus = statusValidos;
		if (arrStatus == null)
			arrStatus = StatusDiscente.getValidos();

		for (int i = 0; i < arrStatus.length; i++) {
			if (i + 1 == arrStatus.length)
				status.append(StatusDiscente.getDescricao(arrStatus[i]) + ")");
			else
				status.append(StatusDiscente.getDescricao(arrStatus[i]) + ", ");
		}
		return status.toString();
	}
}

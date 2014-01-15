/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '29/01/2008'
 *
 */

package br.ufrn.sigaa.arq.negocio;

import java.lang.reflect.Field;

import br.ufrn.arq.dominio.Comando;

/**
 * Classe que enumera os comandos utilizados pelo SIGAA.
 * 
 */
public class SigaaListaComando {

	public static final String prefix = "br.ufrn.sigaa.";

	/** Movimento de Liberação de um comando */
	public static final Comando PREPARE_MOVIMENTO = new Comando(50, null);

	
	public static final Comando MIGRAR_CAMPOS_MARC = new Comando(7777, prefix + "migracao.ProcessadorMigracaoMarc");
	public static final Comando MIGRAR_AUTORIDADES = new Comando(7778, prefix + "migracao.biblioteca.ProcessadorMigracaoAutoridades");
	public static final Comando MIGRAR_AUTORIDADES_HISTORICO = new Comando(77780, prefix + "migracao.biblioteca.ProcessadorMigracaoAutoridades");
	
	public static final Comando LOGON = new Comando(51, prefix + "arq.negocio.LogonBean");
	public static final int LOGON_COD = 51;

	public static final Comando LOGOFF = new Comando(52, prefix + "arq.negocio.LogonBean");
	public static final int LOGOFF_COD = 52;


	public static final int LOGAR_COMO_COD = 53;
	public static final Comando LOGAR_COMO = new Comando(53, prefix + "arq.negocio.LogonBean");

	public static final int LOGAR_COMO_TUTOR_COD = 54;
	public static final Comando LOGAR_COMO_TUTOR = new Comando(54, prefix + "arq.negocio.LogonBean");

	public static final int LOGAR_COMO_COORD_POLO_COD = 55;
	public static final Comando LOGAR_COMO_COORD_POLO = new Comando(55, prefix + "arq.negocio.LogonBean");

	public static final int LOGAR_COMO_ADMIN_COD = 56;
	public static final Comando LOGAR_COMO_ADMIN = new Comando(530, prefix + "arq.negocio.LogonBean");
	
	public static final Comando CADASTRAR_USUARIO = new Comando(58, prefix + "arq.negocio.ProcessadorCadastroUsuarios");
	
	public static final Comando ATRIBUIR_PAPEL = new Comando(60, prefix + "negocio.ProcessadorAtribuirPapel");
	public static final Comando REMOVER_PAPEL = new Comando(61, prefix + "negocio.ProcessadorAtribuirPapel");

	public static final Comando AUTO_CADASTRO_DISCENTE = new Comando(62, prefix + "negocio.ProcessadorAutoCadastroDiscente");
	public static final Comando CONFIRMAR_CADASTRO = new Comando(63, prefix + "negocio.ProcessadorConfirmarCadastro");
	public static final Comando NEGAR_CADASTRO = new Comando(64, prefix + "negocio.ProcessadorConfirmarCadastro");

	public static final Comando ALTERAR_DADOS_USUARIO = new Comando(65, prefix + "negocio.ProcessadorAlterarDadosUsuario");

	public static final Comando ALTERAR_PARAMETROS = new Comando(66, prefix + "parametros.negocio.ProcessadorAlterarParametros");
	
	public static final Comando AUTO_CADASTRO_FAMILIAR = new Comando(67, prefix + "negocio.ProcessadorAutoCadastroFamiliar");
	
	public static final Comando CADASTRAR_PESSOA = new Comando(1005, prefix + "negocio.ProcessadorPessoa");

	public static final Comando ALTERAR_PESSOA = new Comando(1006, prefix + "negocio.ProcessadorPessoa");

	public static final Comando REMOVER_PESSOA = new Comando(1007, prefix + "negocio.ProcessadorPessoa");

	public static final Comando ALTERAR_CALENDARIO_ACADEMICO = new Comando(1008, prefix + "negocio.ProcessadorCalendarioAcademico");

	public static final Comando ELEICAO_CADASTRAR_CANDIDATO = new Comando(1009, prefix + "eleicao.negocio.ProcessadorCandidato");

	public static final Comando ELEICAO_VOTAR = new Comando(1010, prefix + "eleicao.negocio.ProcessadorVoto");

	public static final Comando CADASTRAR_CALENDARIO_ACADEMICO = new Comando(1011, prefix + "negocio.ProcessadorCalendarioAcademico");
	
	public static final Comando CORRIGIR_HORARIO = new Comando(1012, prefix + "negocio.ProcessadorCorrigirHorarios");

	public static final Comando CADASTRAR_REGIAO_CAMPUS = new Comando(1013, prefix + "negocio.ProcessadorRegiaoMatricula");
	public static final Comando REMOVER_REGIAO_CAMPUS = new Comando(1014, prefix + "negocio.ProcessadorRegiaoMatricula");

	/** GRU - Guia de Recolhimento da União */
	public static final Comando CADASTRAR_CONFIGURACAO_GRU = new Comando(1015, prefix + "gru.negocio.ProcessadorConfiguracaoGRU");
	public static final Comando CADASTRAR_GRUPO_EMISSAO_GRU = new Comando(1016, prefix + "gru.negocio.ProcessadorGrupoEmissaoGRU");
	
	public static final Comando LANCAR_FREQUENCIA = new Comando(3041, prefix + "ensino.negocio.ProcessadorFrequencia");
	public static final Comando REMOVER_FREQUENCIA = new Comando(3042, prefix + "ensino.negocio.ProcessadorFrequencia");
	public static final Comando LANCAR_FREQUENCIA_PLANILHA = new Comando(3043, prefix + "ensino.negocio.ProcessadorFrequenciaPlanilha");
	
	//COMANDOS DO LATO SENSU
	
	public static final Comando NOVA_PROPOSTA_EXISTENTE = new Comando(3028, prefix + "ensino.latosensu.negocio.ProcessadorCursoLato");
	
	public static final Comando PERSISTIR_CURSO_LATO = new Comando(3029, prefix + "ensino.latosensu.negocio.ProcessadorCursoLato");

	public static final Comando ALTERAR_PROPOSTA_CURSO_LATO = new Comando(3030, prefix + "ensino.latosensu.negocio.ProcessadorCursoLato");

	public static final Comando ALTERAR_CURSO_LATO = new Comando(3031, prefix + "ensino.latosensu.negocio.ProcessadorCursoLato");

	public static final Comando REMOVER_CURSO_LATO = new Comando(3032, prefix + "ensino.latosensu.negocio.ProcessadorCursoLato");

	public static final Comando CADASTRAR_PARCERIA_LATO = new Comando(3033, prefix + "ensino.latosensu.negocio.ProcessadorParceriaLato");

	public static final Comando ALTERAR_PARCERIA_LATO = new Comando(3034, prefix + "ensino.latosensu.negocio.ProcessadorParceriaLato");

	public static final Comando REMOVER_PARCERIA_LATO = new Comando(3035, prefix + "ensino.latosensu.negocio.ProcessadorParceriaLato");

	public static final Comando SUBMETER_TRABALHO_FINAL_LATO = new Comando(3036, prefix + "ensino.latosensu.negocio.ProcessadorTrabalhoFinalLato");
	public static final Comando REMOVER_TRABALHO_FINAL_LATO = new Comando(3039, prefix + "ensino.latosensu.negocio.ProcessadorTrabalhoFinalLato");

	public static final Comando GRAVAR_HISTORICO_SITUACAO = new Comando(3038, prefix + "ensino.latosensu.negocio.ProcessadorHistoricoSituacao");

	public static final Comando PRORROGAR_PRAZO_CURSO_LATO = new Comando(3040, prefix + "ensino.latosensu.negocio.ProcessadorProrrogacaoPrazoCursoLato");
	public static final Comando SOLICITAR_PRORROGACAO_PRAZO_CURSO_LATO = new Comando(30430, prefix + "ensino.latosensu.negocio.ProcessadorProrrogacaoPrazoCursoLato");
	
	public static final Comando CADASTRAR_TURMA_ENTRADA_LATO = new Comando(3044, prefix + "ensino.latosensu.negocio.ProcessadorTurmaEntrada");
	
	public static final Comando CONCLUIR_CURSO_LATO = new Comando(3045, prefix + "ensino.latosensu.negocio.ProcessadorConclusaoCursoLato");
	
	public static final Comando GERAR_GUIA_RECOLHIMENTO_UNIAO = new Comando(3046, prefix + "gru.negocio.ProcessadorGuiaRecolhimentoUniao");
	public static final Comando CRIAR_MENSALIDADES_CURSO_LATO = new Comando(10208, prefix + "ensino.latosensu.negocio.ProcessadorCriarMensalidadeCursoLato");
	public static final Comando QUITAR_MENSALIDADES_CURSO_LATO = new Comando(10211, prefix + "ensino.latosensu.negocio.ProcessadorCriarMensalidadeCursoLato");

	/** COMANDOS DO MÓDULO DE ENSINO INFANTIL */
	public static final Comando ALTERAR_DISCENTE_INFANTIL = new Comando(10201, prefix + "ensino.negocio.ProcessadorDiscente");
	public static final Comando CADASTRAR_TURMA_INFANTIL = new Comando(10202, prefix + "ensino.infantil.negocio.ProcessadorTurmaInfantil");
	public static final Comando REMOVER_TURMA_INFANTIL = new Comando(10203, prefix + "ensino.infantil.negocio.ProcessadorTurmaInfantil");
	public static final Comando REGISTRAR_EVOLUCAO_CRIANCA = new Comando(10204, prefix + "ensino.infantil.negocio.ProcessadorRegistroEvolucaoCrianca");
	public static final Comando CADASTRAR_FORMULARIO_EVOLUCAO = new Comando(10205, prefix + "ensino.infantil.negocio.ProcessadorCadastroFormularioEvolucao");
	public static final Comando ALTERAR_FORMULARIO_EVOLUCAO = new Comando(10209, prefix + "ensino.infantil.negocio.ProcessadorCadastroFormularioEvolucao");
	public static final Comando DUPLICAR_FORMULARIO_EVOLUCAO = new Comando(10210, prefix + "ensino.infantil.negocio.ProcessadorCadastroFormularioEvolucao");
	public static final Comando CADASTRAR_BLOCO_AREA = new Comando(10206, prefix + "ensino.infantil.negocio.ProcessadorCadastroFormularioEvolucao");
	public static final Comando REMOVER_BLOCO_AREA = new Comando(10207, prefix + "ensino.infantil.negocio.ProcessadorCadastroFormularioEvolucao");
	
	//----------------------------/

	public static final Comando CRIAR_PROJETO_PESQUISA = null;

	public static final Comando	VALIDAR_CONSOLIDACAO	= null;

	public static final Comando	CONSOLIDAR_MATRICULA	= null;

	public static final Comando	VALIDAR_CERTIFICADO	= null;

	public static final Comando	VALIDAR_MATRICULA	= null;

	public static final Comando PROCESSAR_MATRICULA_GRADUACAO = new Comando(9032, prefix + "processamento.negocio.ProcessadorProcessamentoMatriculaGraduacao");
	public static final Comando PROCESSAR_MATRICULA_MUSICA = new Comando(90320, prefix + "processamento.negocio.ProcessadorProcessamentoMatriculaMusica");
	public static final Comando PROCESSAR_MATRICULA_GRADUACAO_FERIAS = new Comando(90321, prefix + "processamento.negocio.ProcessadorProcessamentoMatriculaGraduacaoFerias");

	public static final Comando CADASTRAR_TURMA = new Comando(9033, prefix + "ensino.negocio.ProcessadorTurma");

	public static final Comando ALTERAR_TURMA = new Comando(9034, prefix + "ensino.negocio.ProcessadorTurma");

	public static final Comando CADASTRAR_DISCENTE = new Comando(90340, prefix + "ensino.negocio.ProcessadorDiscente");
	
	public static final Comando ALTERAR_DISCENTE = new Comando(9035, prefix + "ensino.negocio.ProcessadorDiscente");

	public static final Comando REMOVER_DISCENTE = new Comando(9036, prefix + "ensino.negocio.ProcessadorDiscente");

	public static final Comando CADASTRAR_AVALIACAO = new Comando(9037, prefix + "ensino.negocio.ProcessadorCadastroAvaliacao");

	public static final Comando REMOVER_AVALIACAO = new Comando(9038, prefix + "ensino.negocio.ProcessadorCadastroAvaliacao");

	public static final Comando SALVAR_CONSOLIDACAO_TURMA = new Comando(9039, prefix + "ensino.negocio.ProcessadorConsolidacaoTurma");

	public static final Comando CONSOLIDAR_TURMA = new Comando(9040, prefix + "ensino.negocio.ProcessadorConsolidacaoTurma");

	public static final Comando CADASTRAR_MODULO_TECNICO = new Comando(9041, prefix + "ensino.tecnico.negocio.ProcessadorModuloTecnico");

	public static final Comando MATRICULAR_TECNICO = new Comando(9045, prefix + "ensino.negocio.ProcessadorMatricula");

	public static final Comando ALTERAR_MODULO_TECNICO = new Comando(9046, prefix + "ensino.tecnico.negocio.ProcessadorModuloTecnico");

	public static final Comando REMOVER_MODULO_TECNICO = new Comando(9047, prefix + "ensino.tecnico.negocio.ProcessadorModuloTecnico");

	public static final Comando CADASTRAR_HORARIO = new Comando(9048, prefix + "ensino.negocio.ProcessadorHorario");

	public static final Comando ALTERAR_HORARIO = new Comando(9049, prefix + "ensino.negocio.ProcessadorHorario");

	public static final Comando CADASTRAR_ESTRUTURA_CURR_TECNICO = new Comando(9050, prefix + "ensino.tecnico.negocio.ProcessadorEstruturaCurricularTecnico");

	public static final Comando ALTERAR_ESTRUTURA_CURR_TECNICO = new Comando(9051, prefix + "ensino.tecnico.negocio.ProcessadorEstruturaCurricularTecnico");

	public static final Comando REMOVER_TURMA = new Comando(9052, prefix + "ensino.negocio.ProcessadorTurma");

	public static final Comando ALTERAR_QUALIFICACAO = new Comando(9053, prefix + "ensino.tecnico.negocio.ProcessadorQualificacao");

	public static final Comando CADASTRAR_CURSO = new Comando(9054, prefix + "ensino.negocio.ProcessadorCurso");

	public static final Comando CADASTRAR_AFASTAMENTO_ALUNO = new Comando(9056, prefix + "ensino.negocio.ProcessadorMovimentacaoAluno");

	public static final Comando ALTERAR_AFASTAMENTO_ALUNO = new Comando(9057, prefix + "ensino.negocio.ProcessadorMovimentacaoAluno");

	public static final Comando DESATIVAR_AFASTAMENTO_ALUNO = new Comando(9058, prefix + "ensino.negocio.ProcessadorMovimentacaoAluno");

	public static final Comando CADASTRAR_RETORNO_AFASTAMENTO_ALUNO = new Comando(9059, prefix + "ensino.negocio.ProcessadorMovimentacaoAluno");

	public static final Comando ALTERAR_COMPONENTE_CURRICULAR = new Comando(9060, prefix + "ensino.negocio.ProcessadorComponenteCurricular");

	public static final Comando CANCELAR_MAT_DISCIPLINA = new Comando(9061, prefix + "ensino.negocio.ProcessadorAlteracaoMatricula");

	public static final Comando TRANCAR_MAT_DISCIPLINA = new Comando(9062, prefix + "ensino.negocio.ProcessadorAlteracaoMatricula");

	public static final Comando CADASTRAR_TURMA_ENTRADA_TEC = new Comando(9063, prefix + "ensino.tecnico.negocio.ProcessadorTurmaEntrada");

	public static final Comando ALTERAR_TURMA_ENTRADA_TEC = new Comando(9064, prefix + "ensino.tecnico.negocio.ProcessadorTurmaEntrada");

	public static final Comando CADASTRAR_COMPONENTE_CURRICULAR = new Comando(9066, prefix + "ensino.negocio.ProcessadorComponenteCurricular");

	public static final Comando REMOVER_COMPONENTE_CURRICULAR = new Comando(9067, prefix + "ensino.negocio.ProcessadorComponenteCurricular");

	public static final Comando CADASTRAR_NOTAS_UNIDADES = new Comando(9068, prefix + "ensino.negocio.ProcessadorCadastroNotasUnidades");

	public static final Comando RETIFICAR_MATRICULA = new Comando(9069, prefix + "ensino.negocio.ProcessadorRetificacaoMatricula");

	public static final Comando IDENTIFICAR_SECRETARIO = new Comando(9070, prefix + "ensino.negocio.ProcessadorSecretariaUnidade");

	public static final Comando SUBSTITUIR_SECRETARIO = new Comando(9071, prefix + "ensino.negocio.ProcessadorSecretariaUnidade");

	public static final Comando IDENTIFICAR_COORDENADOR = new Comando(9072, prefix + "ensino.negocio.ProcessadorCoordenacaoCurso");

	public static final Comando SUBSTITUIR_COORDENADOR = new Comando(9073, prefix + "ensino.negocio.ProcessadorCoordenacaoCurso");

	public static final Comando NEGAR_CADASTRO_COMPONENTE = new Comando(9074, prefix + "ensino.negocio.ProcessadorComponenteCurricular");

	public static final Comando AUTORIZAR_CADASTRO_COMPONENTE = new Comando(9075, prefix + "ensino.negocio.ProcessadorComponenteCurricular");

	public static final Comando DESATIVAR_COMPONENTE_CURRICULAR = new Comando(9076, prefix + "ensino.negocio.ProcessadorComponenteCurricular");
	
	public static final Comando ATIVAR_DESATIVAR_COMPONENTE_DETALHE = new Comando(9077, prefix + "ensino.negocio.ProcessadorComponenteCurricular");
	
	public static final Comando APROVEITAR_COMPONENTE = new Comando(90770, prefix + "ensino.negocio.ProcessadorAproveitamento");

	public static final Comando MATRICULAR_ATIVIDADE = new Comando(9078, prefix + "ensino.negocio.ProcessadorRegistroAtividade");
	public static final Comando CONSOLIDAR_ATIVIDADE = new Comando(9079, prefix + "ensino.negocio.ProcessadorRegistroAtividade");
	public static final Comando VALIDAR_ATIVIDADE = new Comando(9080, prefix + "ensino.negocio.ProcessadorRegistroAtividade");

	public static final Comando RETORNAR_ALUNO_AFASTADO = new Comando(9081, prefix + "ensino.negocio.ProcessadorMovimentacaoAluno");
	public static final Comando ESTORNAR_AFASTAMENTO_ALUNO = new Comando(9082, prefix + "ensino.negocio.ProcessadorMovimentacaoAluno");
	public static final Comando AFASTAR_ALUNO = new Comando(9083, prefix + "ensino.negocio.ProcessadorMovimentacaoAluno");

	public static final Comando CADASTRAR_QUESTIONARIO = new Comando(9084, prefix + "questionario.negocio.ProcessadorQuestionario");
	public static final Comando ALTERAR_QUESTIONARIO = new Comando(9085, prefix + "questionario.negocio.ProcessadorQuestionario");
	public static final Comando REMOVER_QUESTIONARIO = new Comando(9086, prefix + "questionario.negocio.ProcessadorQuestionario");

	public static final Comando RESPONDER_QUESTIONARIO = new Comando(9087, prefix + "questionario.negocio.ProcessadorQuestionario");
	
	public static final Comando CADASTRAR_BANCA_DEFESA = new Comando(9088, prefix + "ensino.negocio.ProcessadorBancaDefesa");
	public static final Comando ALTERAR_BANCA_DEFESA = new Comando(9089, prefix + "ensino.negocio.ProcessadorBancaDefesa");
	public static final Comando REMOVER_BANCA_DEFESA = new Comando(9090, prefix + "ensino.negocio.ProcessadorBancaDefesa");


	public static final Comando GERAR_MOVIMENTACAO_INTEGRALIZACAO = new Comando(9091, prefix + "ensino.negocio.ProcessadorMovimentacaoAluno");
	public static final Comando INATIVAR_MOVIMENTACAO_INTEGRALIZACAO = new Comando(9092, prefix + "ensino.negocio.ProcessadorMovimentacaoAluno");
	
	public static final Comando MATRICULAR_ALUNO_TURMA_ENSINO_INDIVIDUAL = new Comando(9093, prefix + "ensino.negocio.ProcessadorTurmaEnsinoIndividual");
	
	
	//Comandos relacionados com trancamento
	public static final Comando SOLICITAR_TRANCAMENTO_MATRICULA = new Comando(90840, prefix + "ensino.negocio.ProcessadorTrancamentoMatricula");
	public static final Comando ANALISAR_SOLICITACAO_TRANCAMENTO = new Comando(90850, prefix + "ensino.negocio.ProcessadorTrancamentoMatricula");

	public static final Comando CADASTRAR_DOCENTE_EXTERNO = new Comando(90860, prefix + "ensino.negocio.ProcessadorDocenteExterno");

	public static final Comando APROVEITAMENTO_AUTOMATICO = new Comando(90870, prefix + "ensino.graduacao.negocio.ProcessadorAproveitamentoAutomatico");
	public static final Comando AFASTAR_ALUNO_NO_PASSADO = new Comando(90880, prefix + "ensino.negocio.ProcessadorMovimentacaoAluno");

	public static final Comando CANCELAR_TRANCAMENTO_PROGRAMA = new Comando(90890, prefix + "ensino.negocio.ProcessadorMovimentacaoAluno");

	public static final Comando IMPLANTAR_HISTORICO = new Comando(90900, prefix + "ensino.negocio.ProcessadorImplantarHistorico");

	public static final Comando CANCELAR_SOLICITACAO_TRANCAMENTO = new Comando(9091, prefix + "ensino.negocio.ProcessadorTrancamentoMatricula");
	public static final Comando CONSOLIDAR_SOLICITACOES_TRANCAMENTO = new Comando(9092, prefix + "ensino.negocio.ProcessadorTrancamentoMatricula");
	public static final Comando CONSOLIDAR_INDIVIDUAL = new Comando(9093, prefix + "ensino.negocio.ProcessadorConsolidacaoIndividual");

	public static final Comando ALTERAR_FORMA_INGRESSO_DISCENTE = new Comando(9094, prefix + "ensino.negocio.ProcessadorDiscente");
	public static final Comando EXCLUIR_MATRICULA_ATIVIDADE = new Comando(9095, prefix + "ensino.negocio.ProcessadorRegistroAtividade");

	public static final Comando CADASTRAR_PROGRAMA_COMPONENTE = new Comando(9096, prefix + "ensino.negocio.ProcessadorProgramaComponente");
	public static final Comando ALTERAR_PROGRAMA_COMPONENTE = new Comando(90960, prefix + "ensino.negocio.ProcessadorProgramaComponente");

	public static final Comando CANCELAR_SECRETARIO = new Comando(9097, prefix + "ensino.negocio.ProcessadorSecretariaUnidade");
	public static final Comando EXCLUIR_DISCENTE = new Comando(9098, prefix + "ensino.negocio.ProcessadorExcluirDiscente");

	public static final Comando CADASTRAR_ALTERAR_PROCESSO_SELETIVO = new Comando(9099, prefix + "ensino.negocio.ProcessadorProcessoSeletivo");
	public static final Comando REMOVER_PROCESSO_SELETIVO = new Comando(9100, prefix + "ensino.negocio.ProcessadorProcessoSeletivo");
	
	public static final Comando INSCREVER_PROCESSO_SELETIVO = new Comando(9101, prefix + "ensino.negocio.ProcessadorInscricaoSelecao");

	public static final Comando REABRIR_TURMA = new Comando(9102, prefix + "ensino.negocio.ProcessadorReabrirTurma");

	public static final Comando ALTERACAO_STATUS_DISCENTE = new Comando(9103, prefix + "ensino.negocio.ProcessadorAlteracaoStatusDiscente");

	public static final Comando PRE_PROCESSAR_MATRICULA = new Comando(9104, prefix + "processamento.negocio.ProcessadorPreProcessamentoMatricula");
	public static final Comando CALCULAR_POSSIVEIS_FORMANDOS = new Comando(9105, prefix + "processamento.negocio.ProcessadorPreProcessamentoMatricula");


	public static final Comando SALVAR_AVALIACAO_INSTITUCIONAL_DISCENTE = new Comando(9106, prefix + "avaliacao.negocio.ProcessadorAvaliacaoInstitucional");
	public static final Comando QUESTIONARIO_SATISFACAO = new Comando(9107, prefix + "avaliacao.negocio.ProcessadorQuestionarioSatisfacao");

	public static final Comando FECHAR_TURMA = new Comando(9108, prefix + "ensino.negocio.ProcessadorFecharTurma");
	public static final Comando FINALIZAR_AVALIACAO_INSTITUCIONAL_DISCENTE = new Comando(9109, prefix + "avaliacao.negocio.ProcessadorAvaliacaoInstitucional");

	public static final Comando POS_PROCESSAR_MATRICULA = new Comando(9110, prefix + "processamento.negocio.ProcessadorPosProcessamentoMatricula");

	public static final Comando ALTERAR_CONTATOS_COORDENADOR = new Comando(9111, prefix + "ensino.negocio.ProcessadorCoordenacaoCurso");

	public static final Comando RENOVAR_MATRICULA_ATIVIDADE = new Comando(9112, prefix + "ensino.negocio.ProcessadorRegistroAtividade");
	public static final Comando REMOVER_DOCENTE_EXTERNO = new Comando(9113, prefix + "ensino.negocio.ProcessadorDocenteExterno");
	
	public static final Comando SOLICITAR_EMISSAO_DIPLOMA = new Comando(9114, prefix + "ensino.negocio.ProcessadorSolicitacaoDiploma" );
	public static final Comando EMITIR_DIPLOMA = new Comando(9115, prefix + "ensino.negocio.ProcessadorEmissaoDiploma" );
	
	public static final Comando CADASTRAR_PARECER_PLANO_AULA = new Comando(9116, prefix + "ensino.negocio.ProcessadorParecerPlanoReposicaoAula");
	public static final Comando NOTIFICAR_PROFESSOR_PARECER_PLANO_AULA = new Comando(9117, prefix + "ensino.negocio.ProcessadorParecerPlanoReposicaoAula");
	
	public static final Comando AVISAR_FALTA_DOCENTE = new Comando(9118, prefix + "ensino.negocio.ProcessadorAvisoFaltaDocente");
	
	public static final Comando ENVIO_AUTOMATICO_RELATORIO_FALTA_DOCENTE_SEMANAL = new Comando(9119, prefix + "ensino.negocio.ProcessadorRelatorioAutomaticoFaltaDocente");
	public static final Comando ENVIO_AUTOMATICO_RELATORIO_FALTA_DOCENTE_MENSAL = new Comando(9120, prefix + "ensino.negocio.ProcessadorRelatorioAutomaticoFaltaDocente");
	public static final Comando CADASTRAR_FORMA_INGRESSO = new Comando(9121, prefix + "negocio.ProcessadorFormaIngresso");
	
	public static final Comando ALTERAR_COORDENADOR = new Comando(9122, prefix + "ensino.negocio.ProcessadorCoordenacaoCurso");
	
	public static final Comando ESTORNAR_CONCLUSAO_ALUNO = new Comando(9123, prefix + "ensino.negocio.ProcessadorMovimentacaoAluno");
	public static final Comando ALTERAR_DOCENTE_EXTERNO = new Comando(9124, prefix + "ensino.negocio.ProcessadorDocenteExterno");
	
	public static final Comando ALTERAR_PERIODO_ATIVIDADE = new Comando(9125, prefix + "ensino.negocio.ProcessadorRegistroAtividade");
	
	public static final Comando TRANSFERENCIA_TURMAS_ALUNO = new Comando(9126, prefix + "ensino.graduacao.negocio.ProcessadorTransferenciaTurmas");
	
	public static final Comando ALTERAR_DISCENTE_TECNICO = new Comando(9127, prefix + "ensino.negocio.ProcessadorDiscente");
	
	public static final Comando ALTERAR_DISCENTE_LATO = new Comando(9128, prefix + "ensino.negocio.ProcessadorDiscente");
	
	public static final Comando CADASTRAR_PLANO_RESPOSICAO_AULA = new Comando(9129, prefix + "ensino.negocio.ProcessadorPlanoReposicaoAula");
	
	public static final Comando CADASTRAR_MOBILIDADE_ESTUDANTIL = new Comando(9130, prefix + "ensino.negocio.ProcessadorMobilidadeEstudantil");
	
	public static final Comando CANCELAR_MOBILIDADE_ESTUDANTIL = new Comando(9131, prefix + "ensino.negocio.ProcessadorMobilidadeEstudantil");
	
	public static final Comando CADASTRAR_ALTERAR_EDITAL_PROCESSO_SELETIVO = new Comando(9132, prefix + "ensino.negocio.ProcessadorEditalProcessoSeletivo");
	
	public static final Comando ALTERAR_MOBILIDADE_ESTUDANTIL = new Comando(9133, prefix + "ensino.negocio.ProcessadorMobilidadeEstudantil");
	public static final Comando VALIDAR_INSCRICAO_SELECAO_LOTE = new Comando(9134, prefix + "ensino.negocio.ProcessadorValidaInscricaoSelecao");
	
	public static final Comando ATUALIZAR_DADOS_CAPES_COORDENADOR = new Comando(9135, prefix + "ensino.negocio.ProcessadorCoordenacaoCurso");
	
	public static final Comando PROCESSAR_MATRICULA_HORARIO = new Comando(9136, prefix + "ensino.negocio.ProcessadorProcessamentoMatriculaHorario");
	public static final Comando PERSISTIR_MATRICULA_HORARIO = new Comando(9137, prefix + "ensino.negocio.ProcessadorProcessamentoMatriculaHorario");
	
	public static final Comando CADASTRAR_SELECAO_SEGUNDO_CICLO = new Comando(9138, prefix + "ensino.negocio.ProcessadorSelecaoSegundoCiclo");
	
	public static final Comando NOTIFICAR_INSCRITOS = new Comando(9139, prefix + "ensino.negocio.ProcessadorNotificarInscritos");
	
	public static final Comando ALTERAR_DISCENTE_MEDIO = new Comando(9140, prefix + "ensino.negocio.ProcessadorDiscente");
	
	public static final Comando ALTERAR_ATIVIDADE_GRADUACAO = new Comando(9141, prefix + "ensino.negocio.ProcessadorRegistroAtividade");
	
	public static final Comando AJUSTAR_TURMA = new Comando(9142, prefix + "ensino.negocio.ProcessadorAjustarTurma");
	
	public static final Comando CALCULAR_INTEGRALIZACOES_DISCENTE_TECNICO = new Comando(9143, prefix + "ensino.stricto.negocio.ProcessadorCalculosDiscenteTecnico");
	
	public static final Comando ENVIAR_NOTIFICACAO_ACADEMICA = new Comando(9144, prefix + "ensino.negocio.ProcessadorNotificacaoAcademica");
	public static final Comando CADASTRAR_NOTIFICACAO_ACADEMICA = new Comando(9145, prefix + "ensino.negocio.ProcessadorNotificacaoAcademica");

	public static final Comando ALTERAR_INSCRICAO_PROCESSO_SELETIVO = new Comando(9146, prefix + "ensino.negocio.ProcessadorInscricaoSelecao");

	public static final Comando ANALISAR_MATRICULAS_APROVEITAMENTO_AUTOMATICO = new Comando(9147, prefix + "ensino.graduacao.negocio.ProcessadorAproveitamentoAutomatico");
	
	public static final Comando PROCESSAR_FALTAS = new Comando(9148, prefix + "processamento.negocio.ProcessadorProcessamentoFaltas");
	public static final Comando INATIVAR_COMPONENTES_CURRICULARES_DEPARTAMENTO = new Comando(9076, prefix + "ensino.negocio.ProcessadorInativarComponentesDepartamento");
	
	// COMANDOS DO SUBSISTEMA DE FORMACAO COMPLEMENTAR
	public static final Comando MATRICULAR_FORMACAO_COMPLEMENTAR = new Comando(10451, prefix + "ensino.tecnico.negocio.ProcessadorMatriculaFormacaoComplementar");
	
	// COMANDOS DO SUBSISTEMA DE TÉCNICO
	public static final Comando EFETUAR_TRANSFERENCIA_ENTRE_TURMAS = new Comando(10401, prefix + "ensino.tecnico.negocio.ProcessadorTransferenciaTurmaEntradaTecnico");
	public static final Comando IMPORTAR_APROVADOS_TECNICO = new Comando(10402, prefix + "ensino.tecnico.negocio.ProcessadorImportaAprovadosTecnico");
	public static final Comando DEFINICAO_LEIAUTE_IMPORTACAO_TECNICO = new Comando(10403, prefix + "ensino.tecnico.negocio.ProcessadorImportaAprovadosTecnico");
	public static final Comando CADASTRAR_PROCESSO_SELETIVO_TECNICO = new Comando(10404, prefix + "ensino.tecnico.negocio.ProcessadorProcessoSeletivoTecnico");
	public static final Comando CONVOCAR_CANDIDATOS_PST_PARA_VAGAS_REMANESCENTES = new Comando(10405, prefix + "ensino.tecnico.negocio.ProcessadorConvocacaoProcessoSeletivoTecnicoVR");
	public static final Comando CONVOCACAO_PROCESSO_SELETIVO_DISCENTE_TECNICO = new Comando(10406, prefix + "ensino.tecnico.negocio.ProcessadorConvocacaoProcessoSeletivoTecnico");
	public static final Comando CONFIRMAR_PRE_CADASTRAMENTO_TECNICO = new Comando(10407, prefix + "ensino.tecnico.negocio.ProcessadorPreCadastramentoDiscenteTecnico");
	public static final Comando CONFIRMAR_CADASTRAMENTO_TECNICO = new Comando(10408, prefix + "ensino.tecnico.negocio.ProcessadorCadastramentoDiscenteTecnico");
	public static final Comando ALTERAR_DADOS_PESSOAIS_DISCENTE_TECNICO = new Comando(10409, prefix + "ensino.tecnico.negocio.ProcessadorAlteracaoDadosPessoaisDiscenteTecnico");
	public static final Comando CONFIRMAR_ENVIO_EMAIL_TECNICO = new Comando(10410, prefix + "ensino.tecnico.negocio.ProcessadorCadastramentoDiscenteTecnico");
	public static final Comando SELECIONAR_TURMA_ENTADA_IMD = new Comando(10410, prefix + "ensino.tecnico.negocio.ProcessadorSelecionarTurmaEntradaTecnico");
	
	// Comandos do PRODOCENTE
	public static final Comando VALIDAR_PRODUCAO = new Comando(11001, prefix + "prodocente.negocio.ProcessadorValidacao");
	public static final Comando ALTERAR_PONTUACAO_RELATORIO = new Comando(11002, prefix + "prodocente.negocio.ProcessadorPontuacaoRelatorio");
	public static final Comando CADASTRAR_RELATORIO_PRODUTIVIDADE = new Comando(11003, prefix + "prodocente.negocio.ProcessadorRelatorioProdutividade");
	public static final Comando ALTERAR_RELATORIO_PRODUTIVIDADE = new Comando(11004, prefix + "prodocente.negocio.ProcessadorRelatorioProdutividade");
	public static final Comando REMOVER_RELATORIO_PRODUTIVIDADE = new Comando(11005, prefix + "prodocente.negocio.ProcessadorRelatorioProdutividade");
	public static final Comando IMPORTAR_CURRICULO_LATTES = new Comando(11006, prefix + "prodocente.negocio.ProcessadorImportacaoLattes");
	public static final Comando CALCULAR_IPI_DOCENTE = new Comando(11007, prefix + "prodocente.negocio.ProcessadorCalculoIpi");
	public static final Comando AUTO_VALIDAR_PRODUCAO = new Comando(11008, prefix + "prodocente.negocio.ProcessadorAutoValidacaoProducao");
	public static final Comando CONSOLIDAR_VALIDACAO_PRODUCAO = new Comando(11009, prefix + "prodocente.negocio.ProcessadorConsolidacaoProducao");
	public static final Comando CALCULAR_FPPI_CLASSIFICACAO = new Comando(11010, prefix + "prodocente.negocio.ProcessadorCalculoFppi");
	public static final Comando ALTERAR_IPI = new Comando(11011, prefix + "prodocente.negocio.ProcessadorAlteraIpi");
	public static final Comando CADASTRAR_PET = new Comando(11012, prefix + "prodocente.atividades.negocio.ProcessadorPet");
	public static final Comando CADASTRAR_SOLICITACAO_PROFESSOR_SUBSTITUTO = new Comando(11013, prefix + "rh.negocio.ProcessadorSolicitacaoProfessorSubstituto");
	public static final Comando PARECER_SOLICITACAO_PROFESSOR_SUBSTITUTO = new Comando(11014, prefix + "rh.negocio.ProcessadorSolicitacaoProfessorSubstituto");
	public static final Comando IMPORTAR_CURRICULOS_LATTES_EM_LOTE = new Comando(11015, prefix + "prodocente.negocio.ProcessadorImportacaoCurriculosLattes");
	
	// COMANDOS DO SUBSISTEMA DE GRADUACAO
	public static final Comando MATRICULAR_GRADUACAO = new Comando(10502, prefix + "ensino.graduacao.negocio.ProcessadorMatriculaGraduacao");
	public static final Comando SOLICITAR_ABERTURA_TURMA = new Comando(10503, prefix + "ensino.graduacao.negocio.ProcessadorSolicitacaoTurma");
	public static final Comando TRANCAR_MATRICULA_GRADUACAO = new Comando(10504, prefix + "ensino.graduacao.negocio.ProcessadorTrancamentoMatricula");
	public static final Comando ATUALIZAR_SOLICITACOES_TURMA = new Comando(10505, prefix + "ensino.graduacao.negocio.ProcessadorSolicitacaoTurma");
	public static final Comando ALTERAR_DADOS_PESSOAIS_DISCENTE = new Comando(10506, prefix + "ensino.negocio.ProcessadorDiscente");
	public static final Comando CADASTRAR_CURSO_GRADUACAO = new Comando(10507, prefix + "ensino.graduacao.negocio.ProcessadorCurso");
	public static final Comando ALTERAR_CURSO_GRADUACAO = new Comando(10508, prefix + "ensino.graduacao.negocio.ProcessadorCurso");
	public static final Comando CADASTRAR_MATRIZ_CURRICULAR = new Comando(10509, prefix + "ensino.graduacao.negocio.ProcessadorMatrizCurricular");
	public static final Comando ALTERAR_MATRIZ_CURRICULAR = new Comando(10510, prefix + "ensino.graduacao.negocio.ProcessadorMatrizCurricular");
	public static final Comando CADASTRAR_CURRICULO = new Comando(10511, prefix + "ensino.graduacao.negocio.ProcessadorEstruturaCurricular");
	public static final Comando ALTERAR_CURRICULO = new Comando(10512, prefix + "ensino.graduacao.negocio.ProcessadorEstruturaCurricular");
	public static final Comando MUDANCA_CURRICULAR = new Comando(10513, prefix + "ensino.graduacao.negocio.ProcessadorMudancaCurricular");
	public static final Comando PRORROGACAO_PRAZO = new Comando(10514, prefix + "ensino.negocio.ProcessadorMovimentacaoAluno");
	public static final Comando TRANSFERENCIA_AUTOMATICA = new Comando(10518, prefix + "ensino.graduacao.negocio.ProcessadorTransferenciaTurmas");
	public static final Comando TRANSFERENCIA_MANUAL = new Comando(10519, prefix + "ensino.graduacao.negocio.ProcessadorTransferenciaTurmas");
	public static final Comando CADASTRAR_OBSERVACAO_DISCENTE = new Comando(10520, prefix + "ensino.graduacao.negocio.ProcessadorObservacaoDiscente");
	public static final Comando CADASTRAR_PARTICIPACAO_ENADE = new Comando(10521, prefix + "ensino.graduacao.negocio.ProcessadorParticipacaoEnad");
	public static final Comando MATRICULA_COMPULSORIA = new Comando(10522, prefix + "ensino.graduacao.negocio.ProcessadorMatriculaGraduacao");
	public static final Comando MATRICULA_FORA_PRAZO = new Comando(10523, prefix + "ensino.graduacao.negocio.ProcessadorMatriculaGraduacao");
	public static final Comando ALTERAR_STATUS_MATRICULA = new Comando(10524, prefix + "ensino.graduacao.negocio.ProcessadorAlteracaoStatusMatricula");
	public static final Comando EXCLUSAO_MATRICULA = new Comando(10525, prefix + "ensino.graduacao.negocio.ProcessadorExclusaoMatricula");
	public static final Comando SOLICITACAO_MATRICULA = new Comando(10526, prefix + "ensino.graduacao.negocio.ProcessadorSolicitacaoMatricula");
	public static final Comando ANALISAR_SOLICITACAO_MATRICULA = new Comando(10527, prefix + "ensino.graduacao.negocio.ProcessadorSolicitacaoMatricula");
	public static final Comando SOLICITAR_ALTERACAO_HORARIO = new Comando(10528, prefix + "ensino.graduacao.negocio.ProcessadorSolicitacaoTurma");
	public static final Comando SOLICITAR_ENSINO_INDIVIDUAL = new Comando(10529, prefix + "ensino.graduacao.negocio.ProcessadorSolicitacaoEnsinoIndividual");
	public static final Comando REGISTRAR_MOBILIDADE_INTERNA = new Comando(10530, prefix + "ensino.graduacao.negocio.ProcessadorRegistroMobilidadeInterna");
	public static final Comando CADASTRAR_ORIENTACAO_ACADEMICA = new Comando(10531, prefix + "ensino.graduacao.negocio.ProcessadorOrientacaoAcademica");
	public static final Comando DESATIVAR_ORIENTACAO_ACADEMICA = new Comando(10532, prefix + "ensino.graduacao.negocio.ProcessadorOrientacaoAcademica");
	public static final Comando REMOVER_SOLICITACAO_TURMA = new Comando(10533, prefix + "ensino.graduacao.negocio.ProcessadorSolicitacaoTurma");
	public static final Comando CALCULAR_INTEGRALIZACOES_DISCENTE = new Comando(10534, prefix + "ensino.graduacao.negocio.ProcessadorCalculosDiscente");
	@Deprecated
	public static final Comando CALCULAR_TIPOS_STATUS = new Comando(10535, prefix + "ensino.graduacao.negocio.ProcessadorCalculosDiscente");
	@Deprecated
	public static final Comando CALCULAR_CONSOLIDACAO = new Comando(10536, prefix + "ensino.graduacao.negocio.ProcessadorCalculosDiscente");
	public static final Comando REMOVER_OBSERVACAO_DISCENTE = new Comando(10537, prefix + "ensino.graduacao.negocio.ProcessadorObservacaoDiscente");
	public static final Comando COLACAO_GRAU_COLETIVA = new Comando(10538, prefix + "ensino.graduacao.negocio.ProcessadorColacaoGrauColetiva");
	public static final Comando CANCELAR_PRORROGACAO_PRAZO = new Comando(10539, prefix + "ensino.negocio.ProcessadorMovimentacaoAluno");
	public static final Comando CANCELAR_SOLICITACAO_ENSINO_INDIVIDUAL = new Comando(10540, prefix + "ensino.graduacao.negocio.ProcessadorSolicitacaoEnsinoIndividual");
	public static final Comando MATRICULA_TURMA_FERIAS_ENSINO_INDIVIDUAL = new Comando(10541, prefix + "ensino.graduacao.negocio.ProcessadorMatriculaGraduacao");
	public static final Comando ALTERAR_DATA_COLACAO = new Comando(10543, prefix + "ensino.graduacao.negocio.ProcessadorAlterarDataColacao");
	public static final Comando INTEGRALIZAR_ALUNO_MIGRADO = new Comando(10544, prefix + "ensino.graduacao.negocio.ProcessadorIntegralizarAlunoMigrado");
	public static final Comando MUDANCA_CURRICULAR_COLETIVA = new Comando(10545, prefix + "ensino.graduacao.negocio.ProcessadorMudancaCurricularColetiva");
	public static final Comando ANULAR_SOLICITACAO_MATRICULA = new Comando(10546, prefix + "ensino.graduacao.negocio.ProcessadorSolicitacaoMatricula");
	public static final Comando NEGAR_SOLICITACAO_TURMA = new Comando(10547, prefix + "ensino.graduacao.negocio.ProcessadorSolicitacaoTurma");
	public static final Comando GRAVAR_REQUERIMENTO = new Comando(10548, prefix + "ensino.graduacao.negocio.ProcessadorRequerimento");
	public static final Comando ENVIAR_REQUERIMENTO = new Comando(10549, prefix + "ensino.graduacao.negocio.ProcessadorRequerimento");
	public static final Comando RECUPERAR_SENHA_DISCENTE = new Comando(10550, prefix + "ensino.graduacao.negocio.ProcessadorRecuperacaoSenhaDiscente");
	public static final Comando FINALIZAR_ORIENTACOES_DISCENTE = new Comando(10552, prefix + "ensino.graduacao.negocio.ProcessadorOrientacaoAcademica");
	public static final Comando CANCELAR_ORIENTACOES_DISCENTE = new Comando(10553, prefix + "ensino.graduacao.negocio.ProcessadorOrientacaoAcademica");
	public static final Comando MATRICULA_INSERIR_EXTRAPOLAR_CREDITO = new Comando(10554, prefix + "ensino.graduacao.negocio.ProcessadorExtrapolarCredito");
	public static final Comando MATRICULA_CACELAR_EXTRAPOLAR_CREDITO = new Comando(10555, prefix + "ensino.graduacao.negocio.ProcessadorExtrapolarCredito");
	public static final Comando CADASTRAR_OFERTA_VAGAS_CURSO = new Comando(10556, prefix + "ensino.graduacao.negocio.ProcessadorCadastroOfertaVagasCurso");
	public static final Comando ADICIONAR_NOTICIA_PORTAL_DISCENTE = new Comando(10558, prefix + "ensino.graduacao.negocio.ProcessadorNoticiaPortalDiscente");
	public static final Comando ALTERAR_NOTICIA_PORTAL_DISCENTE = new Comando(10559, prefix + "ensino.graduacao.negocio.ProcessadorNoticiaPortalDiscente");
	public static final Comando REMOVER_NOTICIA_PORTAL_DISCENTE = new Comando(10560, prefix + "ensino.graduacao.negocio.ProcessadorNoticiaPortalDiscente");
	public static final Comando DESPUBLICAR_NOTICIA_PORTAL_DISCENTE = new Comando(10561, prefix + "ensino.graduacao.negocio.ProcessadorNoticiaPortalDiscente");
	public static final Comando PUBLICAR_NOTICIA_PORTAL_DISCENTE = new Comando(10562, prefix + "ensino.graduacao.negocio.ProcessadorNoticiaPortalDiscente");
	public static final Comando ENVIAR_MENSAGEM_COORDENAOR = new Comando(10563, prefix + "ensino.graduacao.negocio.ProcessadorAgregadorBolsas");
	public static final Comando RECALCULAR_ESTRUTURA_CURRICULAR = new Comando(10564, prefix + "ensino.graduacao.negocio.ProcessadorRecalculoEstruturasCurriculares");
	public static final Comando CONFIRMACA_MATRICULA_FERIAS = new Comando(10565, prefix + "ensino.graduacao.negocio.ProcessadorConfirmacaoMatriculaFerias");
	public static final Comando RETORNAR_SOLICITACAO_ENSINO_INDIVIDUAL = new Comando(10566, prefix + "ensino.graduacao.negocio.ProcessadorSolicitacaoEnsinoIndividual");
	public static final Comando CADASTRAR_INTERESSE_BOLSA = new Comando(10567, prefix + "ensino.graduacao.negocio.ProcessadorInteressadoBolsa");
	public static final Comando GERAR_RELATORIO_ACOMPANHAMENTO_BOLSAS = new Comando(10570, prefix + "ensino.graduacao.negocio.ProcessadorRelatorioAcomponhamentoBolsa");
	public static final Comando CALCULAR_HISTORICO_DISCENTE = new Comando(10572, prefix + "ensino.negocio.ProcessadorCalculaHistorico");
	public static final Comando CADASTRAR_ENFASE = new Comando(10573, prefix + "ensino.graduacao.negocio.ProcessadorCadastroEnfase");
	public static final Comando CONCORDAR_REGULAMENTO = new Comando(10574, prefix + "ensino.graduacao.negocio.ProcessadorSolicitacaoMatricula");
	public static final Comando CADASTRAR_INTERESSE_BOLSA_EXTENSAO = new Comando(10575, prefix + "ensino.graduacao.negocio.ProcessadorInteressadoBolsa");
	public static final Comando CADASTRAR_INTERESSE_BOLSA_MONITORIA = new Comando(10576, prefix + "ensino.graduacao.negocio.ProcessadorInteressadoBolsa");
	public static final Comando ANTECIPACAO_PRAZO = new Comando(10577, prefix + "ensino.negocio.ProcessadorMovimentacaoAluno");
	public static final Comando RELATORIO_INDICES_DISCENTE = new Comando(10578, prefix + "ensino.graduacao.negocio.ProcessadorCalculosDiscente");
	public static final Comando CADASTRAR_REPOSICAO_PROVA = new Comando(10579, prefix + "ensino.graduacao.negocio.ProcessadorReposicaoAvaliacao");
	public static final Comando CANCELAR_REPOSICAO_PROVA = new Comando(10580, prefix + "ensino.graduacao.negocio.ProcessadorReposicaoAvaliacao");
	public static final Comando PARECER_REPOSICAO_PROVA = new Comando(10581, prefix + "ensino.graduacao.negocio.ProcessadorReposicaoAvaliacao");
	public static final Comando HOMOLOGAR_REPOSICAO_PROVA = new Comando(10582, prefix + "ensino.graduacao.negocio.ProcessadorReposicaoAvaliacao");
	public static final Comando NOTIFICAR_SOLICITACAO_TURMA = new Comando(10583, prefix + "ensino.graduacao.negocio.ProcessadorNotificarSolicitacaoTurma");
	public static final Comando RECADASTRAR_INTERESSE_BOLSA_EXTENSAO = new Comando(10584, prefix + "ensino.graduacao.negocio.ProcessadorInteressadoBolsa");
	public static final Comando ALTERAR_STATUS_GERAL_TURMA = new Comando(10585, prefix + "ensino.graduacao.negocio.ProcessadorAlteracaoStatusMatricula");
	public static final Comando ANULAR_SOLICITACAO_MATRICULA_AUTOMATICO = new Comando(10587, prefix + "ensino.graduacao.negocio.ProcessadorSolicitacaoMatricula");
	public static final Comando CONFIRMAR_CADASTRAMENTO = new Comando(10588, prefix + "ensino.graduacao.negocio.ProcessadorCadastramentoDiscente");
	public static final Comando CONVOCAR_CANDIDATOS_VESTIBULAR_PARA_VAGAS_REMANESCENTES = new Comando(10589, prefix + "ensino.graduacao.negocio.ProcessadorConvocacaoVestibular");
	public static final Comando CADASTRAR_INTERESSE_BOLSA_ACAO_ASSOCIADA = new Comando(10590, prefix + "ensino.graduacao.negocio.ProcessadorInteressadoBolsa");
	public static final Comando RECADASTRAR_INTERESSE_BOLSA_ASSOCIADA = new Comando(10591, prefix + "ensino.graduacao.negocio.ProcessadorInteressadoBolsa");
	public static final Comando CADASTRAR_MENSAGEM_ORIENTACAO = new Comando(10592, prefix + "ensino.graduacao.negocio.ProcessadorMensagemOrientacao");
	public static final Comando CADASTRAR_MENSAGEM_TODOS_ORIENTANDOS = new Comando(10593, prefix + "ensino.graduacao.negocio.ProcessadorMensagemOrientacao");
	public static final Comando MATRICULAR_EXTRAORDINARIA = new Comando(10594, prefix + "ensino.graduacao.negocio.ProcessadorMatriculaExtraordinaria");
	public static final Comando CADASTRAR_PARTICIPACAO_ENADE_LOTE = new Comando(10595, prefix + "ensino.graduacao.negocio.ProcessadorParticipacaoEnad");
	public static final Comando CADASTRAR_ATIVIDADES_ACADEMICAS_COMPLEMENTARES = new Comando(10596, prefix + "ensino.graduacao.negocio.registro_atividades_complementares.ProcessadorCadastrarAtividadesAcademicasComplementares");
	public static final Comando REGISTRAR_ATIVIDADES_ACADEMICAS_COMPLEMENTARES = new Comando(10597, prefix + "ensino.graduacao.negocio.registro_atividades_complementares.ProcessadorRegistrarAtividadesAcademicasComplementares");
	public static final Comando ENVIA_HOMOLOGACAO_ATIVIDADES_COMPLEMENTARES = new Comando(10598, prefix + "ensino.graduacao.negocio.registro_atividades_complementares.ProcessadorEnviaAtividadesComplementaresParaHomologacao");
	public static final Comando HOMOLOGAR_ATIVIDADE_ACADEMICA_COMPLEMENTAR = new Comando(10599, prefix + "ensino.graduacao.negocio.registro_atividades_complementares.ProcessadorHomologaAtividadeComplementar");
	public static final Comando JUBILAR_DISCENTE = new Comando(10600, prefix + "ensino.negocio.ProcessadorJubilamento");
	public static final Comando CANCELAR_MATRICULA_NAO_CONCLUIDA = new Comando(10601, prefix + "ensino.graduacao.negocio.ProcessadorCancelarMatriculaNaoConcluida");
	public static final Comando SUGERIR_SOLICITACAO_TURMA = new Comando(10602, prefix + "ensino.graduacao.negocio.ProcessadorSolicitacaoTurma");
	public static final Comando ESTORNAR_CONVOCACAO_VESTIBULAR = new Comando(10603, prefix + "ensino.graduacao.negocio.ProcessadorEstornarConvocacaoVestibular");
	public static final Comando MUDANCA_COLETIVA_MATRIZ_CURRICULAR = new Comando(10604, prefix + "ensino.graduacao.negocio.ProcessadorMudancaColetivaMatrizCurricular");
	public static final Comando IMPORTAR_APROVADOS_OUTROS_VESTIBULARES = new Comando(10605, prefix + "ensino.graduacao.negocio.ProcessadorImportaAprovadosOutrosVestibulares");
	public static final Comando ALTERAR_DATA_COLACAO_COLETIVA = new Comando(10606, prefix + "ensino.negocio.ProcessadorMovimentacaoAluno");
	public static final Comando CONVERTER_TURMA_REGULAR_ENSINO_INDIVIDUAL = new Comando(10607, prefix + "ensino.negocio.ProcessadorTurma");
	public static final Comando MERGE_DADOS_DISCENTE = new Comando(10608, prefix + "ensino.negocio.ProcessadorMergeDiscente");
	public static final Comando ACRESCIMO_PERFIL_INICIAL = new Comando(10609, prefix + "ensino.graduacao.negocio.ProcessadorPerfilInicial");
	public static final Comando DEFINICAO_LEIAUTE_IMPORTACAO_OUTROS_VESTIBULARES = new Comando(10610, prefix + "ensino.graduacao.negocio.ProcessadorImportaAprovadosOutrosVestibulares");
	public static final Comando CANCELAR_SOLICITACAO_ENSINO_INDIVIDUAL_DISCENTE = new Comando(10611, prefix + "ensino.graduacao.negocio.ProcessadorSolicitacaoEnsinoIndividual");	
	public static final Comando CALCULAR_INTEGRALIZACOES_DISCENTE_ANTIGO_REGULAMENTO = new Comando(10612, prefix + "ensino.graduacao.negocio.ProcessadorCalculosDiscente");
	public static final Comando EFETIVAR_CADASTRAMENTO_VESTIBULAR = new Comando(10613, prefix + "vestibular.negocio.ProcessadorEncerrarCadastramentoVestibular");
	public static final Comando ENCERRAR_CADASTRAMENTO_VESTIBULAR = new Comando(10614, prefix + "vestibular.negocio.ProcessadorEncerrarCadastramentoVestibular");

	
	
	// COMANDOS DE STRICTO
	public static final Comando CADASTRAR_TRABALHO_FINAL_STRICTO = new Comando(110001, prefix + "ensino.stricto.negocio.ProcessadorTrabalhoFinalStricto");
	public static final Comando CADASTRAR_BANCA_POS = new Comando(110002, prefix + "ensino.stricto.negocio.ProcessadorBancaPos");
	public static final Comando CADASTRAR_EQUIPE_PROGRAMA = new Comando(110003, prefix + "ensino.stricto.negocio.ProcessadorEquipePrograma");
	public static final Comando ALTERAR_EQUIPE_PROGRAMA = new Comando(110004, prefix + "ensino.stricto.negocio.ProcessadorEquipePrograma");
	public static final Comando CADASTRAR_LIMITE_BOLSISTAS_EQUIPE = new Comando(110005, prefix + "ensino.stricto.negocio.ProcessadorEquipePrograma");
	public static final Comando HOMOLOGAR_TRABALHO_FINAL_STRICTO = new Comando(110006, prefix + "ensino.stricto.negocio.ProcessadorHomologacaoTrabalhoFinal");
	public static final Comando CALCULAR_INTEGRALIZACOES_DISCENTE_STRICTO = new Comando(110007, prefix + "ensino.stricto.negocio.ProcessadorCalculosDiscenteStricto");
	public static final Comando CADASTRAR_ATA_BANCA_POS = new Comando(110008, prefix + "ensino.stricto.negocio.ProcessadorBancaPos");
	public static final Comando ALTERAR_DISCENTE_STRICTO = new Comando(110009, prefix + "ensino.negocio.ProcessadorDiscente");
	public static final Comando CANCELAR_ORIENTACAO_ACADEMICA = new Comando(110010, prefix + "ensino.graduacao.negocio.ProcessadorOrientacaoAcademica");
	public static final Comando ALTERAR_ORIENTACAO_ACADEMICA = new Comando(110011, prefix + "ensino.graduacao.negocio.ProcessadorOrientacaoAcademica");
	public static final Comando ALTERAR_BANCA_POS = new Comando(110012, prefix + "ensino.stricto.negocio.ProcessadorBancaPos");
	public static final Comando REMOVER_BANCA_POS = new Comando(110013, prefix + "ensino.stricto.negocio.ProcessadorBancaPos");
	public static final Comando SOLICITAR_MATRICULA_STRICTO = new Comando(110014, prefix + "ensino.stricto.negocio.ProcessadorMatriculaStricto");
	public static final Comando CANCELAR_SOLICITAO_MATRICULA_STRICTO = new Comando(110015, prefix + "ensino.stricto.negocio.ProcessadorMatriculaStricto");
	public static final Comando CADASTRAR_DOCUMENTO_SITE = new Comando(110016, prefix + "ensino.stricto.negocio.ProcessadorDocumentoSite");
	public static final Comando REMOVER_DOCUMENTO_SITE = new Comando(110017, prefix + "ensino.stricto.negocio.ProcessadorDocumentoSite");
	public static final Comando REMOVER_EQUIPE_PROGRAMA = new Comando(110018, prefix + "ensino.stricto.negocio.ProcessadorEquipePrograma");
	public static final Comando VINCULAR_PROGRAMAS_PROJETOS = new Comando(110019, prefix + "ensino.stricto.negocio.ProcessadorProgramaProjeto");
	public static final Comando CADASTRAR_BOLSA_CNPQ_STRICTO = new Comando(110020, prefix + "ensino.stricto.negocio.ProcessadorBolsaCnpqStricto");
	public static final Comando FINALIZAR_BOLSA_CNPQ_STRICTO = new Comando(110021, prefix + "ensino.stricto.negocio.ProcessadorBolsaCnpqStricto");
	public static final Comando CADASTRAR_DEFESA_ALUNO_CONCLUIDO = new Comando(110022, prefix + "ensino.stricto.negocio.ProcessadorBancaPos");
	public static final Comando CADASTRAR_EDITAL_BOLSAS_REUNI = new Comando(110023, prefix + "ensino.stricto.reuni.negocio.ProcessadorEditalBolsasReuni");
	public static final Comando REMOVER_EDITAL_BOLSAS_REUNI = new Comando(110024, prefix + "ensino.stricto.reuni.negocio.ProcessadorEditalBolsasReuni");
	public static final Comando SALVAR_SOLICITACAO_BOLSAS_REUNI = new Comando(110025, prefix + "ensino.stricto.reuni.negocio.ProcessadorSolicitacaoBolsasReuni");
	public static final Comando SUBMETER_SOLICITACAO_BOLSAS_REUNI = new Comando(110026, prefix + "ensino.stricto.reuni.negocio.ProcessadorSolicitacaoBolsasReuni");
	public static final Comando INATIVAR_ATIVAR_CURRICULO = new Comando(110027, prefix + "ensino.graduacao.negocio.ProcessadorEstruturaCurricular");
	public static final Comando GERAR_DOCUMENTO_HOMOLOGACAO_TRABALHO_FINAL = new Comando(110028, prefix + "ensino.stricto.negocio.ProcessadorHomologacaoTrabalhoFinal");
	public static final Comando ALTERAR_STATUS_SOLICITACAO_BOLSAS_REUNI = new Comando(110029, prefix + "ensino.stricto.reuni.negocio.ProcessadorSolicitacaoBolsasReuni");
	public static final Comando CADASTRAR_INDICACAO_BOLSAS_REUNI = new Comando(110030, prefix + "ensino.stricto.docenciaassistida.negocio.ProcessadorPlanoDocenciaAssistida");
	public static final Comando CADASTRAR_PLANO_DOCENCIA_ASSISTIDA = new Comando(110031, prefix + "ensino.stricto.docenciaassistida.negocio.ProcessadorPlanoDocenciaAssistida");
	public static final Comando ALTERAR_PLANO_DOCENCIA_ASSISTIDA = new Comando(110032, prefix + "ensino.stricto.docenciaassistida.negocio.ProcessadorPlanoDocenciaAssistida");
	public static final Comando ANALISAR_PLANO_DOCENCIA_ASSISTIDA = new Comando(110035, prefix + "ensino.stricto.docenciaassistida.negocio.ProcessadorPlanoDocenciaAssistida");
	public static final Comando CALCULAR_PRAZO_CONCLUSAO_DISCENTE_STRICTO = new Comando(110036, prefix + "ensino.stricto.negocio.ProcessadorCalculosDiscenteStricto");
	public static final Comando ALTERAR_ALUNO_INDICACAO_BOLSAS_REUNI = new Comando(110037, prefix + "ensino.stricto.docenciaassistida.negocio.ProcessadorPlanoDocenciaAssistida");
	public static final Comando CADASTRAR_SOLICITACAO_PUBLICACAO_TESE_DISSERTACAO = new Comando(110038, prefix + "ensino.stricto.negocio.ProcessadorPublicacaoTeseDissertacao");
	public static final Comando HOMOLOGAR_SOLICITACAO_PUBLICACAO_TESE_DISSERTACAO = new Comando(110039, prefix + "ensino.stricto.negocio.ProcessadorPublicacaoTeseDissertacao");
	public static final Comando PUBLICAR_TESE_DISSERTACAO = new Comando(110040, prefix + "ensino.stricto.negocio.ProcessadorPublicacaoTeseDissertacao");
	public static final Comando CANCELAR_BANCA_POS = new Comando(110041, prefix + "ensino.stricto.negocio.ProcessadorBancaPos");
	public static final Comando MUDANCA_CURRICULO = new Comando(105130, prefix + "ensino.stricto.negocio.ProcessadorMudancaCurriculo");
	public static final Comando CADASTRAR_BANCA_POS_ORIENTADOR = new Comando(110042, prefix + "ensino.stricto.negocio.ProcessadorBancaPos");
	public static final Comando APROVAR_BANCA_POS = new Comando(110043, prefix + "ensino.stricto.negocio.ProcessadorBancaPos");
	public static final Comando ALTERAR_SOLICITACAO_PUBLICACAO_TESE_DISSERTACAO = new Comando(110044, prefix + "ensino.stricto.negocio.ProcessadorPublicacaoTeseDissertacao");
	public static final Comando CALCULAR_PRAZO_CONCLUSAO_DISCENTE_STRICTO_EM_LOTE = new Comando(110045, prefix + "ensino.stricto.negocio.ProcessadorCalculosDiscenteStricto");

	
	
	//COMANDOS DO SUBSISTEMA DE PESQUISA
	//OBS. códigos da classe 8000
	public static final Comando ENVIAR_PROJETO_PESQUISA = new Comando(8001, prefix + "pesquisa.negocio.ProcessadorProjetoPesquisa");
	public static final Comando REMOVER_PROJETO_PESQUISA = new Comando(8002, prefix + "pesquisa.negocio.ProcessadorProjetoPesquisa");
	public static final Comando ALTERAR_PROJETO_PESQUISA = new Comando(8003, prefix + "pesquisa.negocio.ProcessadorProjetoPesquisa");
	public static final Comando RENOVAR_PROJETO_PESQUISA = new Comando(8004, prefix + "pesquisa.negocio.ProcessadorProjetoPesquisa");
	public static final Comando GRAVAR_PROJETO_PESQUISA = new Comando(8005, prefix + "pesquisa.negocio.ProcessadorProjetoPesquisa");
	public static final Comando ALTERAR_SITUACAO_PROJETO_PESQUISA = new Comando(8006, prefix + "pesquisa.negocio.ProcessadorAlteracaoSituacaoProjetoPesquisa");
	public static final Comando FINALIZAR_PROJETO_PESQUISA = new Comando(8007, prefix + "pesquisa.negocio.ProcessadorAlteracaoSituacaoProjetoPesquisa");

	public static final Comando CADASTRAR_PLANO_TRABALHO = new Comando(8010, prefix + "pesquisa.negocio.ProcessadorPlanoTrabalho");
	public static final Comando ALTERAR_PLANO_TRABALHO = new Comando(8011, prefix + "pesquisa.negocio.ProcessadorPlanoTrabalho");
	public static final Comando ENVIAR_RELATORIO_PARCIAL_BOLSA_PESQUISA = new Comando(8012, prefix + "pesquisa.negocio.ProcessadorRelatorioBolsaParcial");
	public static final Comando PARECER_RELATORIO_PARCIAL_BOLSA_PESQUISA = new Comando(8013, prefix + "pesquisa.negocio.ProcessadorRelatorioBolsaParcial");
	public static final Comando REMOVER_PARECER_RELATORIO_PARCIAL_BOLSA_PESQUISA = new Comando(8014, prefix + "pesquisa.negocio.ProcessadorRelatorioBolsaParcial");
	public static final Comando GRAVAR_PLANO_TRABALHO = new Comando(8015, prefix + "pesquisa.negocio.ProcessadorPlanoTrabalho");

	public static final Comando ENVIAR_RELATORIO_FINAL_PROJETO = new Comando(8021, prefix + "pesquisa.negocio.ProcessadorRelatorioProjeto");
	public static final Comando ENVIAR_RELATORIO_FINAL_BOLSA_PESQUISA = new Comando(8022, prefix + "pesquisa.negocio.ProcessadorRelatorioBolsaFinal");
	public static final Comando PARECER_RELATORIO_FINAL_BOLSA_PESQUISA = new Comando(8023, prefix + "pesquisa.negocio.ProcessadorRelatorioBolsaFinal");
	public static final Comando REMOVER_PARECER_RELATORIO_FINAL_BOLSA_PESQUISA = new Comando(8024, prefix + "pesquisa.negocio.ProcessadorRelatorioBolsaFinal");

	public static final Comando ENVIAR_RESUMO_CONGRESSO_IC = new Comando(8025, prefix + "pesquisa.negocio.ProcessadorResumoCongresso");
	public static final Comando AUTORIZAR_RESUMO_CONGRESSO_IC = new Comando(8026, prefix + "pesquisa.negocio.ProcessadorResumoCongresso");
	public static final Comando RECUSAR_RESUMO_CONGRESSO_IC = new Comando(8027, prefix + "pesquisa.negocio.ProcessadorResumoCongresso");
	public static final Comando RECUSADO_NECESSITA_CORRECOES = new Comando(8028, prefix + "pesquisa.negocio.ProcessadorResumoCongresso");
	public static final Comando ENVIAR_RELATORIO_E_FINALIZAR_PROJETO_PESQUISA = new Comando(8029, prefix + "pesquisa.negocio.ProcessadorRelatorioProjeto");
	
	public static final Comando DISTRIBUIR_PROJETO_PESQUISA = new Comando(8030, prefix + "pesquisa.negocio.ProcessadorDistribuirProjetoPesquisa");
	public static final Comando DISTRIBUIR_PLANOS_TRABALHO_PESQUISA = new Comando(8031, prefix + "pesquisa.negocio.ProcessadorDistribuicaoPlanosTrabalho");

	public static final Comando AVALIAR_PROJETO_PESQUISA = new Comando(8035, prefix + "pesquisa.negocio.ProcessadorAvaliacaoProjetoPesquisa");
	public static final Comando AVALIAR_RELATORIO_PROJETO_PESQUISA = new Comando(8036, prefix + "pesquisa.negocio.ProcessadorAvaliacaoRelatorioProjeto");
	public static final Comando AVALIAR_PLANO_TRABALHO_PESQUISA = new Comando(8037, prefix + "pesquisa.negocio.ProcessadorAvaliacaoPlanoTrabalho");

	public static final Comando DISTRIBUIR_COTAS_PESQUISA = new Comando(8038, prefix + "pesquisa.negocio.ProcessadorDistribuicaoCotasDocente");
	public static final Comando AJUSTAR_COTAS_PESQUISA = new Comando(8039, prefix + "pesquisa.negocio.ProcessadorDistribuicaoCotasDocente");

	public static final Comando PUBLICAR_EDITAL_PESQUISA = new Comando(8040, prefix + "pesquisa.negocio.ProcessadorEditalPesquisa");
	public static final Comando CADASTRAR_PERIODO_COTAS = new Comando(8041, prefix + "pesquisa.negocio.ProcessadorCotaBolsas");
	public static final Comando REMOVER_EDITAL_PESQUISA = new Comando(8042, prefix + "pesquisa.negocio.ProcessadorEditalPesquisa");
	public static final Comando ADICIONAR_COTA_DOCENTE = new Comando(8043, prefix + "pesquisa.negocio.ProcessadorDistribuicaoCotasDocente");
	public static final Comando ENVIAR_ARQUIVO_EDITAL_PESQUISA = new Comando(8044, prefix + "pesquisa.negocio.ProcessadorEditalPesquisa");
	public static final Comando REMOVER_RELATORIO_FINAL_BOLSA_PESQUISA = new Comando(8045, prefix + "pesquisa.negocio.ProcessadorRelatorioBolsaFinal");

	public static final Comando ANALISAR_AVALIACOES = new Comando(8050, prefix + "pesquisa.negocio.ProcessadorAnalisarAvaliacoes");
	public static final Comando ANALISAR_PROJETO_EXTERNO = new Comando(8051, prefix + "pesquisa.negocio.ProcessadorAnalisarProjetoExterno");
	public static final Comando ENCERRAR_AVALIACOES_PENDENTES = new Comando(8052, prefix + "pesquisa.negocio.ProcessadorEncerrarAvaliacoesPendentes");

	public static final Comando INATIVAR_BOLSISTA = new Comando(8059, prefix + "pesquisa.negocio.ProcessadorIndicarBolsista");
	public static final Comando IGNORAR_BOLSISTA = new Comando(8060, prefix + "pesquisa.negocio.ProcessadorIndicarBolsista");
	public static final Comando INDICAR_BOLSISTA = new Comando(8061, prefix + "pesquisa.negocio.ProcessadorIndicarBolsista");
	public static final Comando REMOVER_BOLSISTA = new Comando(8062, prefix + "pesquisa.negocio.ProcessadorIndicarBolsista");
	public static final Comando CADASTRAR_VOLUNTARIO_PESQUISA = new Comando(8064, prefix + "pesquisa.negocio.ProcessadorCadastroVoluntario");
	public static final Comando ALTERAR_DADOS_BOLSISTA = new Comando(8065, prefix + "pesquisa.negocio.ProcessadorAlterarMembroProjetoDiscente");

	public static final Comando REMOVER_PLANO_TRABALHO = new Comando(8066, prefix + "pesquisa.negocio.ProcessadorPlanoTrabalho");
	public static final Comando DESISTIR_AVALIACAO_PROJETO_PESQUISA = new Comando(8067, prefix + "pesquisa.negocio.ProcessadorAvaliacaoProjetoPesquisa");
	public static final Comando NOTIFICAR_CONSULTORES = new Comando(8068, prefix + "pesquisa.negocio.ProcessadorNotificacaoConsultores");
	public static final Comando CADASTRAR_CONSULTOR = new Comando(8069, prefix + "pesquisa.negocio.ProcessadorConsultor");
	public static final Comando NOTIFICAR_CONSULTORES_ESPECIAIS = new Comando(8070, prefix + "pesquisa.negocio.ProcessadorNotificacaoConsultores");
	public static final Comando FINALIZAR_PLANOS_TRABALHO_COM_COTA = new Comando(8071, prefix + "pesquisa.negocio.ProcessadorFinalizacaoPlanosTrabalho");
	public static final Comando FINALIZAR_PLANOS_TRABALHO_SEM_COTA = new Comando(80711, prefix + "pesquisa.negocio.ProcessadorFinalizacaoPlanosTrabalho");
	public static final Comando CADASTRAR_AVALIADOR_CIC = new Comando(8072, prefix + "pesquisa.negocio.ProcessadorAvaliadorCIC");
	public static final Comando ALTERAR_CALENDARIO_PESQUISA = new Comando(8073, prefix + "pesquisa.negocio.ProcessadorCalendarioPesquisa");
	public static final Comando CADASTRAR_COLABORADOR_VOLUNTARIO = new Comando(8074, prefix + "pesquisa.negocio.ProcessadorColaboradorVoluntario");

	public static final Comando GRAVAR_RELATORIO_PARCIAL_BOLSA_PESQUISA = new Comando(8075, prefix + "pesquisa.negocio.ProcessadorRelatorioBolsaParcial");
	public static final Comando GRAVAR_RELATORIO_FINAL_BOLSA_PESQUISA = new Comando(8076, prefix + "pesquisa.negocio.ProcessadorRelatorioBolsaFinal");

	public static final Comando DISTRIBUIR_AVALIACAO_RESUMO_CIC = new Comando(8077, prefix + "pesquisa.negocio.ProcessadorAvaliacaoResumo");
	public static final Comando AVALIAR_RESUMO_CIC = new Comando(8078, prefix + "pesquisa.negocio.ProcessadorAvaliacaoResumo");
	
	public static final Comando EMITIR_PARECER_INVENCAO = new Comando(8079, prefix + "pesquisa.negocio.ProcessadorParecerInvencao");
	
	public static final Comando DISTRIBUIR_AVALIACAO_APRESENTACAO_RESUMO_CIC = new Comando(8080, prefix + "pesquisa.negocio.ProcessadorAvaliacaoApresentacaoResumo");
	public static final Comando AJUSTAR_DISTRIBUICAO_AVALIACAO_APRESENTACAO_RESUMO_CIC = new Comando(8081, prefix + "pesquisa.negocio.ProcessadorAvaliacaoApresentacaoResumo");
	public static final Comando AVALIAR_APRESENTACAO_RESUMO_CIC = new Comando(8082, prefix + "pesquisa.negocio.ProcessadorAvaliacaoApresentacaoResumo");
	public static final Comando GERAR_NUMERACAO_PAINEIS_RESUMO_CIC = new Comando(8083, prefix + "pesquisa.negocio.ProcessadorAvaliacaoApresentacaoResumo");
	public static final Comando APROVAR_PLANO_TRABALHO_CORRIGIDO = new Comando(8084, prefix + "pesquisa.negocio.ProcessadorAprovarPlanoDeTrabalho");
	public static final Comando GRAVAR_INVENCAO = new Comando(80840, prefix + "pesquisa.negocio.ProcessadorNotificacaoInvencao");
	public static final Comando NOTIFICAR_INVENCAO = new Comando(8085, prefix + "pesquisa.negocio.ProcessadorNotificacaoInvencao");
	public static final Comando REMOVER_INVENCAO = new Comando(8086, prefix + "pesquisa.negocio.ProcessadorNotificacaoInvencao");
	public static final Comando CADASTRAR_PROJETO_INFRA_ESTRUTURA = new Comando(8087, prefix + "pesquisa.negocio.ProcessadorProjetoInfraEstrutura");
	public static final Comando ALTERAR_INSTITUTO_CIENCIA_TECNOLOGIA = new Comando(8090, prefix + "pesquisa.negocio.ProcessadorInstitutoCienciaTecnologia");
	public static final Comando CADASTRAR_INSTITUTO_CIENCIA_TECNOLOGIA = new Comando(8091, prefix + "pesquisa.negocio.ProcessadorInstitutoCienciaTecnologia");
	public static final Comando ALTERAR_STATUS_RESUMOS_CIC = new Comando(8092, prefix + "pesquisa.negocio.ProcessadorAlterarStatusResumosCIC");
	public static final Comando CADASTRAR_GRUPO_PESQUISA = new Comando(8093, prefix + "pesquisa.negocio.ProcessadorGrupoPesquisa");
	public static final Comando CADASTRAR_PROJETO_APOIO_GRUPO_PESQUISA = new Comando(8094, prefix + "pesquisa.negocio.ProcessadorProjetoApoioGrupoPesquisa");
	public static final Comando REMOVER_PROJETO_APOIO_GRUPO_PESQUISA = new Comando(8095, prefix + "pesquisa.negocio.ProcessadorProjetoApoioGrupoPesquisa");
	public static final Comando ALTERAR_PROJETO_APOIO_GRUPO_PESQUISA = new Comando(8096, prefix + "pesquisa.negocio.ProcessadorProjetoApoioGrupoPesquisa");
	public static final Comando CADASTRAR_PROJETO_APOIO_NOVOS_PESQUISADORES = new Comando(8097, prefix + "pesquisa.negocio.ProcessadorProjetoApoioNovosPesquisadores");
	public static final Comando REMOVER_PROJETO_APOIO_NOVOS_PESQUISADORES = new Comando(8098, prefix + "pesquisa.negocio.ProcessadorProjetoApoioNovosPesquisadores");
	public static final Comando ENVIAR_PROJETO_APOIO_NOVOS_PESQUISADORES = new Comando(8099, prefix + "pesquisa.negocio.ProcessadorProjetoApoioNovosPesquisadores");
	public static final Comando ENVIAR_PROPOSTA_GRUPO_PESQUISA = new Comando(8100, prefix + "pesquisa.negocio.ProcessadorPropostaGrupoPesquisa");
	public static final Comando GRAVAR_PROPOSTA_GRUPO_PESQUISA = new Comando(8101, prefix + "pesquisa.negocio.ProcessadorPropostaGrupoPesquisa");
	public static final Comando ENVIAR_EMAIL_MEMBROS_PROPOSTA_GRUPO_PESQUISA = new Comando(8102, prefix + "pesquisa.negocio.ProcessadorPropostaGrupoPesquisa");
	public static final Comando ASSINAR_PROPOSTA_GRUPO_PESQUISA = new Comando(8103, prefix + "pesquisa.negocio.ProcessadorPropostaGrupoPesquisa");
	
	/** Subsistema de monitoria */
	public static final Comando CALCULAR_BOLSAS_PROJETO = new Comando(10001, prefix + "monitoria.negocio.ProcessadorCalculoBolsas");
	public static final Comando AVALIAR_RESUMO_SID = new Comando(10002, prefix + "monitoria.negocio.ProcessadorAvaliacaoResumo");
	public static final Comando SOLICITAR_RENOVACAO_PROJETO = new Comando(10004, prefix + "monitoria.negocio.ProcessadorSolicitarRenovacao");
	public static final Comando CADASTRAR_PROJETO_MONITORIA = new Comando(10006, prefix + "monitoria.negocio.ProcessadorCadastroProjeto");
	public static final Comando DISTRIBUIR_MONITORIA = new Comando(10007, prefix + "monitoria.negocio.ProcessadorDistribuirMonitoria");
	public static final Comando AVALIAR_PROJETO_MONITORIA = new Comando(10008, prefix + "monitoria.negocio.ProcessadorAvaliacaoProjetoMonitoria");
	public static final Comando CADASTRAR_RESUMO_SID = new Comando(10010, prefix + "monitoria.negocio.ProcessadorCadastroResumoSid");
	public static final Comando AUTORIZAR_PROJETO_MONITORIA = new Comando(10011, prefix + "monitoria.negocio.ProcessadorAutorizacaoProjetoMonitoria");
	public static final Comando AUTORIZAR_RECONSIDERACAO = new Comando(10012, prefix + "monitoria.negocio.ProcessadorAutorizacaoReconsideracao");
	public static final Comando REMOVER_RESUMO_SID = new Comando(10013, prefix + "monitoria.negocio.ProcessadorCadastroResumoSid");

	public static final Comando ALTERAR_ORIENTACOES = new Comando(100130, prefix + "monitoria.negocio.ProcessadorAlterarOrientacao");
	public static final Comando CADASTRAR_RESULTADO_SELECAO_MONITORIA = new Comando(10014, prefix + "monitoria.negocio.ProcessadorDiscenteMonitoria");
	public static final Comando FINALIZAR_DISCENTEMONITORIA = new Comando(10015, prefix + "monitoria.negocio.ProcessadorDiscenteMonitoria");
	public static final Comando ASSOCIAR_MONITOR_DOCENTE = new Comando(10016, prefix + "monitoria.negocio.ProcessadorAssociarMonitorDocente");
	public static final Comando VALIDA_CADASTRO_SELECAO = new Comando(10017, prefix + "monitoria.negocio.ProcessadorSelecaoMonitoria");
	public static final Comando EXCLUIR_DISCENTEMONITORIA = new Comando(10018, prefix + "monitoria.negocio.ProcessadorDiscenteMonitoria");
	public static final Comando ALTERAR_VINCULO_DISCENTEMONITORIA = new Comando(10019, prefix + "monitoria.negocio.ProcessadorDiscenteMonitoria");
	public static final Comando NOTIFICAR_INSCRITO_SELECAO = new Comando(10052, prefix + "monitoria.negocio.ProcessadorDiscenteMonitoria");

	public static final Comando CADASTRAR_EQUIPEDOCENTE = new Comando(10020, prefix + "monitoria.negocio.ProcessadorEquipeDocente");
	public static final Comando ALTERAR_EQUIPEDOCENTE = new Comando(10021, prefix + "monitoria.negocio.ProcessadorEquipeDocente");
	public static final Comando EXCLUIR_EQUIPEDOCENTE = new Comando(10022, prefix + "monitoria.negocio.ProcessadorEquipeDocente");
	public static final Comando FINALIZAR_EQUIPEDOCENTE = new Comando(10023, prefix + "monitoria.negocio.ProcessadorEquipeDocente");

	public static final Comando MOVIMENTAR_COTAS_MONITORIA = new Comando(10024, prefix + "monitoria.negocio.ProcessadorMovimentarCotas");

	public static final Comando CADASTRAR_ATIVIDADE_MONITOR = new Comando(10025, prefix + "monitoria.negocio.ProcessadorAtividadeMonitor");
	public static final Comando ORIENTADOR_VALIDAR_ATIVIDADE_MONITOR = new Comando(10026, prefix + "monitoria.negocio.ProcessadorAtividadeMonitor");
	public static final Comando PROGRAD_VALIDAR_ATIVIDADE_MONITOR = new Comando(10027, prefix + "monitoria.negocio.ProcessadorAtividadeMonitor");
	

	public static final Comando CADASTRAR_ENVIO_FREQUENCIA = new Comando(10028, prefix + "monitoria.negocio.ProcessadorEnvioFrequencia");
	public static final Comando AVALIAR_RELATORIO_PROJETO_MONITORIA = new Comando(10029, prefix + "monitoria.negocio.ProcessadorAvaliacaoRelatorioProjeto");
	public static final Comando CADASTRAR_CALENDARIO_MONITORIA = new Comando(10030, prefix + "monitoria.negocio.ProcessadorCalendarioMonitoria");
	public static final Comando CANCELAR_BOLSAS_PROJETO_MONITORIA = new Comando(10031, prefix + "monitoria.negocio.ProcessadorCancelamentoBolsasMonitoria");
	public static final Comando REGISTRAR_FREQUENCIA_RESUMO_SID = new Comando(10032, prefix + "monitoria.negocio.ProcessadorCadastroResumoSid");
	public static final Comando REATIVAR_MONITORIA = new Comando(10033, prefix + "monitoria.negocio.ProcessadorDiscenteMonitoria");

	public static final Comando DESVALIDA_SELECAO_MONITORIA = new Comando(10034, prefix + "monitoria.negocio.ProcessadorSelecaoMonitoria");
	public static final Comando ALTERAR_NOTA_SELECAO_MONITORIA = new Comando(10035, prefix + "monitoria.negocio.ProcessadorDiscenteMonitoria");
	public static final Comando CADASTRAR_PROVA_SELECAO_MONITORIA = new Comando(10036, prefix + "monitoria.negocio.ProcessadorSelecaoMonitoria");

	public static final Comando CADASTRAR_RELATORIO_PROJETO_MONITORIA = new Comando(10037, prefix + "monitoria.negocio.ProcessadorCadastroRelatorioProjetoMonitoria");
	public static final Comando ENVIAR_RELATORIO_PROJETO_MONITORIA = new Comando(10038, prefix + "monitoria.negocio.ProcessadorCadastroRelatorioProjetoMonitoria");
	public static final Comando DEVOLVER_RELATORIO_COORDENADOR = new Comando(10039, prefix + "monitoria.negocio.ProcessadorCadastroRelatorioProjetoMonitoria");

	public static final Comando CADASTRAR_RELATORIO_MONITOR = new Comando(10040, prefix + "monitoria.negocio.ProcessadorCadastroRelatorioMonitor");
	public static final Comando ENVIAR_RELATORIO_MONITOR = new Comando(10041, prefix + "monitoria.negocio.ProcessadorCadastroRelatorioMonitor");
	public static final Comando DEVOLVER_RELATORIO_MONITOR = new Comando(10042, prefix + "monitoria.negocio.ProcessadorCadastroRelatorioMonitor");
	public static final Comando COORDENACAO_VALIDAR_RELATORIO_MONITOR = new Comando(10043, prefix + "monitoria.negocio.ProcessadorCadastroRelatorioMonitor");
	public static final Comando PROGRAD_VALIDAR_RELATORIO_MONITOR = new Comando(10044, prefix + "monitoria.negocio.ProcessadorCadastroRelatorioMonitor");
	public static final Comando REEDITAR_PROJETO_MONITORIA = new Comando(10045, prefix + "extensao.negocio.ProcessadorAutorizacaoDepartamento");
	public static final Comando ACEITAR_OU_RECUSAR_MONITORIA = new Comando(10046, prefix + "monitoria.negocio.ProcessadorDiscenteMonitoria");
	
	public static final Comando PUBLICAR_EDITAL_MONITORIA = new Comando(10047, prefix + "monitoria.negocio.ProcessadorEditalMonitoria");
	public static final Comando REMOVER_EDITAL_MONITORIA = new Comando(10048, prefix + "monitoria.negocio.ProcessadorEditalMonitoria");
	public static final Comando DESVALIDAR_ATIVIDADE_MONITOR = new Comando(10049, prefix + "monitoria.negocio.ProcessadorAtividadeMonitor");
	
	public static final Comando CADASTRAR_RESULTADO_PROVA_SELECAO = new Comando(10050, prefix + "monitoria.negocio.ProcessadorProvaSelecao");
	public static final Comando CADASTRAR_ORIENTACOES_CONVOCAR = new Comando(10051, prefix + "monitoria.negocio.ProcessadorProvaSelecao");
	
	public static final Comando CADASTRAR_RECONSIDERACAO_MONITORIA = new Comando(20052, prefix + "monitoria.negocio.ProcessadorReconsideracaoMonitoria");
	public static final Comando ANALISAR_RECONSIDERACAO_MONITORIA = new Comando(20053, prefix + "monitoria.negocio.ProcessadorReconsideracaoMonitoria");

	/**
	 * Portal do Docente
	 */
	public static final Comando ATUALIZAR_PERFIL_DOCENTE = new Comando(12001, prefix + "portal.negocio.ProcessadorPerfilDocente");
	public static final Comando ATUALIZAR_PERFIL_DISCENTE = new Comando(12002, prefix + "portal.negocio.ProcessadorPerfilDiscente");
	public static final Comando SALVAR_IMAGEM_PERFIL = new Comando(12003, prefix + "negocio.ProcessadorSalvarImagemPerfil");

	/**
	 * Portal Público do SIGAA
	 */
	public static final Comando ATUALIZAR_DETALHES_SITE = new Comando(120030, prefix + "negocio.ProcessadorDetalhesSite");
	public static final Comando REMOVER_ARQUIVO_DETALHES_SITE = new Comando(12004, prefix + "negocio.ProcessadorDetalhesSite");
	public static final Comando ATUALIZAR_NOTICIA_SITE = new Comando(12005, prefix + "negocio.ProcessadorNoticiaSite");
	public static final Comando ATUALIZAR_SECAO_EXTRA_SITE = new Comando(12009, prefix + "negocio.ProcessadorSecaoExtraSite");
	public static final Comando REMOVER_SECAO_EXTRA_SITE = new Comando(12010, prefix + "negocio.ProcessadorSecaoExtraSite");
	public static final Comando REMOVER_ARQUIVO_NOTICIA_SITE = new Comando(12006, prefix + "negocio.ProcessadorNoticiaSite");
	public static final Comando CADASTRA_REVALIDACAO_DIPLOMA = new Comando(12007, prefix + "negocio.ProcessadorSolicitacaoRevalidacaoDiploma");
	public static final Comando REAGENDAMENTO_REVALIDACAO_DIPLOMA = new Comando(12008, prefix + "negocio.ProcessadorSolicitacaoRevalidacaoDiploma");
	public static final Comando CADASTRA_AGENDA_REVALIDACAO_DIPLOMA = new Comando(120090, prefix + "negocio.ProcessadorAgendaRevalidacaoDiploma");
		
	
	/**
	 *  SubSistema de Extensão
	 */
	public static final Comando SUBMETER_ATIVIDADE_EXTENSAO                 = new Comando(20001, prefix + "extensao.negocio.ProcessadorAtividadeExtensao");
	public static final Comando ALTERAR_ATIVIDADE_EXTENSAO                  = new Comando(20002, prefix + "extensao.negocio.ProcessadorAtividadeExtensao");
	public static final Comando REMOVER_ATIVIDADE_EXTENSAO                  = new Comando(20003, prefix + "extensao.negocio.ProcessadorAtividadeExtensao");
	public static final Comando AUTORIZAR_ATIVIDADE_EXTENSAO                = new Comando(20004, prefix + "extensao.negocio.ProcessadorAutorizacaoDepartamento");
	public static final Comando REEDITAR_ATIVIDADE_EXTENSAO                 = new Comando(20005, prefix + "extensao.negocio.ProcessadorAutorizacaoDepartamento");
	public static final Comando ALTERAR_SITUACAO_ATIVIDADE_EXTENSAO         = new Comando(20006, prefix + "extensao.negocio.ProcessadorAtividadeExtensao");
	public static final Comando CONCLUIR_ATIVIDADE_EXTENSAO                 = new Comando(20007, prefix + "extensao.negocio.ProcessadorAtividadeExtensao");
	public static final Comando ALTERAR_BOLSAS_ATIVIDADE_EXTENSAO           = new Comando(20008, prefix + "extensao.negocio.ProcessadorAtividadeExtensao");
	public static final Comando ENVIAR_EMAIL_COORDENADOR_ACAO_SEM_RELATORIO = new Comando(20009, prefix + "extensao.negocio.ProcessadorAtividadeExtensao");
	public static final Comando GRAVAR_RASCUNHO_ATIVIDADE_EXTENSAO          = new Comando(20010, prefix + "extensao.negocio.ProcessadorAtividadeExtensao");
	public static final Comando SALVAR_ATIVIDADE_EXTENSAO                   = new Comando(20011, prefix + "extensao.negocio.ProcessadorAtividadeExtensao");
	public static final Comando SUBMETER_ATIVIDADE_ASSOCIADA_EXTENSAO       = new Comando(20012, prefix + "extensao.negocio.ProcessadorAtividadeExtensao");
	public static final Comando REATIVAR_ATIVIDADE_EXTENSAO                 = new Comando(20013, prefix + "extensao.negocio.ProcessadorAtividadeExtensao");
	public static final Comando EXECUTAR_ATIVIDADE_EXTENSAO                 = new Comando(20014, prefix + "extensao.negocio.ProcessadorAtividadeExtensao");
	public static final Comando NAO_EXECUTAR_ATIVIDADE_EXTENSAO             = new Comando(20015, prefix + "extensao.negocio.ProcessadorAtividadeExtensao");
	public static final Comando CADASTRAR_PARTICIPANTES_EXTENSAO            = new Comando(20016, prefix + "extensao.negocio.ProcessadorParticipantesExtensao");
	public static final Comando CADASTRAR_INSCRICAO_ACAO_EXTENSAO           = new Comando(20017, prefix + "extensao.negocio.ProcessadorInscricaoAtividade");
	
	public static final Comando CADASTRO_PARTICIPANTE_EXTENSAO_PELOS_GESTORES 		  	  = new Comando(20034, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorCadastroParticipanteExtensaoByGestores");
	public static final Comando CADASTRO_PARTICIPANTE_EXTENSAO 						      = new Comando(20018, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorCadastroParticipanteExtensao");
	public static final Comando CONFIRMA_CADASTRO_PARTICIPANTE_EXTENSAO 			      = new Comando(20019, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorConfirmaCadastroParticipanteExtensao");
	public static final Comando GERAR_NOVA_SENHA_CADASTRO_EXTENSAO 					      = new Comando(20020, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorGerarNovaSenhaCadastroParticipanteExtensao");
	public static final Comando CONFIRMA_ALTERACAO_SENHA_CADASTRO_EXTENSAO 			      = new Comando(20021, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorConfirmaAlteracaoSenhaCadastroParticipanteExtensao");
	public static final Comando CADASTRA_ALTERA_PERIODO_INSCRICAO_ATIVIDADE			      = new Comando(20022, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorCadastrarAlteraPeriodoInscricaoAtividade");
	public static final Comando SUSPENDER_PERIODO_INSCRICAO_ATIVIDADE			      	  = new Comando(20023, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorSuspendePeriodoInscricaoAtividade");
	public static final Comando SUSPENDER_PERIODO_INSCRICAO_SUB_ATIVIDADE			      = new Comando(20024, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorSuspendePeriodoInscricaoSubAtividade");
	public static final Comando CADASTRA_ALTERA_PERIODO_INSCRICAO_SUB_ATIVIDADE		      = new Comando(20025, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorCadastrarAlteraPeriodoInscricaoSubAtividade");
	public static final Comando INSCREVE_PARTICIPANTE_ATIVIDADE_EXTENSAO			      = new Comando(20026, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorInscreveParticipanteAtividadeExtensao");
	public static final Comando INSCREVE_PARTICIPANTE_MINI_ATIVIDADE_EXTENSAO		      = new Comando(20027, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorInscreveParticipanteMiniAtividadeExtensao");
	public static final Comando CANCELAR_INSCRICAO_PARTICIPANTE        			          = new Comando(20028, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorCancelaInscricaoParticipante");
	public static final Comando APROVAR_PARTICIPANTES_INSCRITOS_ATIVIDADE                 = new Comando(20029, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorAprovarParticipantesInscritosAtividade");
	public static final Comando RECUSAR_PARTICIPANTES_INSCRITOS_ATIVIDADE                 = new Comando(20030, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorRecusarParticipantesInscritosAtividade");
	public static final Comando APROVAR_PARTICIPANTES_INSCRITOS_SUB_ATIVIDADE             = new Comando(20031, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorAprovarParticipantesInscritosSubAtividade");
	public static final Comando RECUSAR_PARTICIPANTES_INSCRITOS_SUB_ATIVIDADE             = new Comando(20032, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorRecusarParticipantesInscritosSubAtividade");
	public static final Comando ATUALIZAR_INFORMACOES_PARTICIPANTES_EMITIR_CERTIFICADOS   = new Comando(20033, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorAtualizarInformacoesParticipantesEmtirCertificados");
	public static final Comando CADASTRO_PARTICIPANTE_EXTENSAO_PELO_COORDENADOR 		  = new Comando(20034, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorCadastroParticipanteExtensaoCoordenador");
	public static final Comando INCLUIR_PARTICIPANTE_ATIVIDADE_PELO_COORDENADOR 		  = new Comando(20035, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorIncluiParticipanteAtividadeExtensaoCoordenador");
	public static final Comando INCLUIR_PARTICIPANTE_MINI_ATIVIDADE_PELO_COORDENADOR 	  = new Comando(20036, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorIncluiParticipanteMiniAtividadeExtensaoCoordenador");
	public static final Comando REMOVE_PARTICIPANTE_ATIVIDADE_PELO_COORDENADOR 		 	  = new Comando(20037, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorRemoveParticipanteAtividadeExtensaoCoordenador");
	public static final Comando REMOVE_PARTICIPANTE_MINI_ATIVIDADE_PELO_COORDENADOR 	  = new Comando(20038, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorRemoveParticipanteMiniAtividadeExtensaoCoordenador");
	public static final Comando ALTERA_PARTICIPANTE_EXTENSAO_PELO_COORDENADOR 		  	  = new Comando(20039, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorAlteraParticipanteAtividadeExtensaoCoordenador");
	public static final Comando ESTORNA_PAGAMENTO_GRU_CURSOS_EVENTOS_EXTENSAO 	  		  = new Comando(20040, prefix + "extensao.negocio.inscricoes_atividades.ProcessadorEstornaPagamentoGRUCursosEventosExtensao");
	
	
	//public static final Comando APROVAR_INSCRICAO_PARTICIPANTE_EXTENSAO = new Comando(20100, prefix + "extensao.negocio.ProcessadorInscricaoParticipante");
	//public static final Comando RECUSAR_INSCRICAO_PARTICIPANTE_EXTENSAO = new Comando(20101, prefix + "extensao.negocio.ProcessadorInscricaoParticipante");
	//public static final Comando CADASTRAR_INSCRICAO_PARTICIPANTE        = new Comando(20102, prefix + "extensao.negocio.ProcessadorInscricaoParticipante");
	//public static final Comando CONFIRMAR_INSCRICAO_PARTICIPANTE        = new Comando(20103, prefix + "extensao.negocio.ProcessadorInscricaoParticipante");
	//public static final Comando CANCELAR_INSCRICAO_PARTICIPANTE         = new Comando(20104, prefix + "extensao.negocio.ProcessadorInscricaoParticipante");
	//public static final Comando ALTERAR_INSCRICAO_PARTICIPANTE          = new Comando(20105, prefix + "extensao.negocio.ProcessadorInscricaoParticipante");
	
	//public static final Comando CADASTRAR_INSCRICAO_SUB_ATIVIDADE              = new Comando(20200, prefix + "extensao.negocio.ProcessadorInscricaoAtividade");
	//public static final Comando CADASTRAR_INSCRICAO_PARTICIPANTE_SUB_ATIVIDADE = new Comando(20201, prefix + "extensao.negocio.ProcessadorInscricaoParticipante");
	//public static final Comando SUSPENDER_INSCRICAO_ACAO_EXTENSAO              = new Comando(20202, prefix + "extensao.negocio.ProcessadorInscricaoAtividade");
	//public static final Comando EXCLUIR_INSCRICAO_ACAO_EXTENSAO                = new Comando(20203, prefix + "extensao.negocio.ProcessadorInscricaoAtividade");
	//public static final Comando ALTERAR_PARTICIPACOES_EXTENSAO                 = new Comando(20204, prefix + "extensao.negocio.ProcessadorParticipantesExtensao");
	
	public static final Comando AVALIAR_ATIVIDADE_EXTENSAO           = new Comando(20300, prefix + "extensao.negocio.ProcessadorAvaliarAtividade");
	public static final Comando DISTRIBUIR_ATIVIDADE_EXTENSAO_MANUAL = new Comando(20301, prefix + "extensao.negocio.ProcessadorDistribuirExtensao");
	public static final Comando DISTRIBUIR_ATIVIDADE_EXTENSAO_AUTO   = new Comando(20302, prefix + "extensao.negocio.ProcessadorDistribuirExtensao");
	public static final Comando REENVIAR_SENHA_PARTICIPANTE_EXTENSAO = new Comando(20303, prefix + "extensao.negocio.ProcessadorInscricaoParticipante");
	public static final Comando CLASSIFICAR_ACOES_EXTENSAO           = new Comando(20304, prefix + "extensao.negocio.ProcessadorClassificarAcoesExtensao");

	public static final Comando RENOVAR_ATIVIDADE_EXTENSAO = new Comando(20400, prefix + "extensao.negocio.ProcessadorAtividadeExtensao");
	public static final Comando RENOVAR_SUB_ATIVIDADE_EXTENSAO_EM_EXECUCAO = new Comando(20401, prefix + "extensao.negocio.ProcessadorRemoverSubAtividadeEmExecucao");
	
	public static final Comando MOVIMENTAR_COTAS_EXTENSAO           = new Comando(20500, prefix + "extensao.negocio.ProcessadorMovimentarCotas");
	public static final Comando ENVIAR_PLANO_TRABALHO_EXTENSAO      = new Comando(20501, prefix + "extensao.negocio.ProcessadorPlanoTrabalhoExtensao");
	public static final Comando REMOVER_PLANO_TRABALHO_EXTENSAO     = new Comando(20502, prefix + "extensao.negocio.ProcessadorPlanoTrabalhoExtensao");
	public static final Comando SALVAR_PLANO_TRABALHO_EXTENSAO      = new Comando(20503, prefix + "extensao.negocio.ProcessadorPlanoTrabalhoExtensao");
	public static final Comando NOTIFICACAO_DISCENTE_MENSAGEM_EMAIL = new Comando(20504, prefix + "extensao.negocio.ProcessadorPlanoTrabalhoExtensao");

	public static final Comando CADASTRAR_SELECAO_EXTENSAO          = new Comando(20600, prefix + "extensao.negocio.ProcessadorDiscenteExtensao");
	public static final Comando FINALIZAR_DISCENTE_EXTENSAO         = new Comando(20601, prefix + "extensao.negocio.ProcessadorDiscenteExtensao");
	public static final Comando INDICAR_DISCENTE_EXTENSAO           = new Comando(20602, prefix + "extensao.negocio.ProcessadorDiscenteExtensao");
	public static final Comando VALIDAR_RELATORIO_DISCENTE_EXTENSAO = new Comando(20603, prefix + "extensao.negocio.ProcessadorDiscenteExtensao");
	public static final Comando CADASTRAR_RECONSIDERACAO_EXTENSAO   = new Comando(20605, prefix + "extensao.negocio.ProcessadorReconsideracaoExtensao");
	public static final Comando ANALISAR_RECONSIDERACAO_EXTENSAO    = new Comando(20606, prefix + "extensao.negocio.ProcessadorReconsideracaoExtensao");
	public static final Comando INATIVAR_AUTORIZACOES_DEPARTAMENTOS = new Comando(20607, prefix + "extensao.negocio.ProcessadorAutorizacaoDepartamento");

	public static final Comando ENVIAR_RELATORIO_PROJETO_EXTENSAO            = new Comando(20700, prefix + "extensao.negocio.ProcessadorRelatorioExtensao");
	public static final Comando ENVIAR_RELATORIO_CURSO_EVENTO_EXTENSAO       = new Comando(20701, prefix + "extensao.negocio.ProcessadorRelatorioExtensao");
	public static final Comando ENVIAR_RELATORIO_PRODUTO_EXTENSAO            = new Comando(20702, prefix + "extensao.negocio.ProcessadorRelatorioExtensao");
	public static final Comando ENVIAR_RELATORIO_PROGRAMA_EXTENSAO           = new Comando(20703, prefix + "extensao.negocio.ProcessadorRelatorioExtensao");
	public static final Comando SALVAR_RELATORIO_ACAO_EXTENSAO               = new Comando(20704, prefix + "extensao.negocio.ProcessadorRelatorioExtensao");
	public static final Comando REMOVER_RELATORIO_ACAO_EXTENSAO              = new Comando(20705, prefix + "extensao.negocio.ProcessadorRelatorioExtensao");
	public static final Comando DEVOLVER_RELATORIO_COORDENADOR_EXTENSAO      = new Comando(20706, prefix + "extensao.negocio.ProcessadorRelatorioExtensao");
	public static final Comando VALIDAR_RELATORIO_ACAO_EXTENSAO_PROEX        = new Comando(20707, prefix + "extensao.negocio.ProcessadorRelatorioExtensao");
	public static final Comando VALIDAR_RELATORIO_ACAO_EXTENSAO_DEPARTAMENTO = new Comando(20708, prefix + "extensao.negocio.ProcessadorRelatorioExtensao");
	
	public static final Comando PUBLICAR_EDITAL_EXTENSAO                              = new Comando(20800, prefix + "extensao.negocio.ProcessadorEditalExtensao");
	public static final Comando REMOVER_EDITAL_EXTENSAO                               = new Comando(20801, prefix + "extensao.negocio.ProcessadorEditalExtensao");
	public static final Comando ENCERRRAR_PROJETOS_COM_TEMPO_CADASTRO_EXPIRADO        = new Comando(20803, prefix + "extensao.negocio.ProcessadorEncerrarProjetosCadastroExpirado");
	public static final Comando REATIVAR_PROJETO_COM_TEMPO_CADASTRO_EXPIRADO          = new Comando(20804, prefix + "extensao.negocio.ProcessadorEncerrarProjetosCadastroExpirado");
	public static final Comando RECUPERAR_ACAO_EXTENSAO_REMOVIDA                      = new Comando(20805, prefix + "extensao.negocio.ProcessadorRecuperarAcao");
	public static final Comando ENCERRAR_ACOES_COM_DATA_FIM_MAIOR_QUE_PRAZO           = new Comando(20806, prefix + "extensao.negocio.ProcessadorEncerrarAcoesEmExecucao");
	public static final Comando ALTERAR_VAGAS_CURSO_EVENTO_EXTENSAO                   = new Comando(20807, prefix + "extensao.negocio.ProcessadorInscricaoAtividade");
	public static final Comando CRIA_GRU_CURSOS_EVENTOS_EXTENSAO                      = new Comando(20808, prefix + "extensao.negocio.ProcessadorCriaGRUCursosEventosExtensao");
	public static final Comando CONFIRMA_PAGAMENTO_MANUAL_GRU_CURSOS_EVENTOS_EXTENSAO = new Comando(20809, prefix + "extensao.negocio.ProcessadorConfirmaPagamentoGRUAtividadesExtensaoManualmente");
	public static final Comando ALTERAR_VAGAS_MINI_ATIVIDADE                          = new Comando(20810, prefix + "extensao.negocio.ProcessadorInscricaoAtividade");
	public static final Comando SUBMETER_OBJETIVO                 					  = new Comando(20811, prefix + "extensao.negocio.ProcessadorObjetivo");
	public static final Comando REMOVER_OBJETIVO                 					  = new Comando(20812, prefix + "extensao.negocio.ProcessadorObjetivo");
	public static final Comando ALTERAR_CH_OBJETIVO                					  = new Comando(20813, prefix + "extensao.negocio.ProcessadorObjetivo");
		
	public static final Comando ASSOCIAR_QUESTIONARIO_PROJETO      					  = new Comando(20814, prefix + "extensao.negocio.ProcessadorQuestionarioProjeto");
	public static final Comando RESPONDER_QUESTIONARIO_PROJETO     					  = new Comando(20815, prefix + "extensao.negocio.ProcessadorQuestionarioProjeto");
	public static final Comando REMOVER_ASSOCIACAO_QUESTIONARIO_PROJETO      		  = new Comando(20816, prefix + "extensao.negocio.ProcessadorQuestionarioProjeto");
	
	/** Projeto integrado de ação acadêmica (Projeto Base)*/
	public static final Comando GRAVAR_RASCUNHO_PROJETO_BASE = new Comando(20100, prefix + "projetos.negocio.ProcessadorProjetoBase");
	public static final Comando SALVAR_PROJETO_BASE = new Comando(20101, prefix + "projetos.negocio.ProcessadorProjetoBase");
	public static final Comando ENVIAR_PROJETO_BASE = new Comando(20102, prefix + "projetos.negocio.ProcessadorProjetoBase");
	public static final Comando CONCLUIR_PROJETO_BASE = new Comando(20103, prefix + "projetos.negocio.ProcessadorProjetoBase");
	public static final Comando REMOVER_PROJETO_BASE = new Comando(20104, prefix + "projetos.negocio.ProcessadorProjetoBase");
	public static final Comando ALTERAR_SITUACAO_PROJETO_BASE = new Comando(20105, prefix + "projetos.negocio.ProcessadorProjetoBase");
	public static final Comando ALTERAR_CADASTRO_PROJETO_BASE = new Comando(20106, prefix + "projetos.negocio.ProcessadorProjetoBase");
	public static final Comando SOLICITAR_RECONSIDERACAO_PROJETO_BASE = new Comando(20107, prefix + "projetos.negocio.ProcessadorReconsiderarAvaliacao");
	public static final Comando ANALISAR_RECONSIDERACAO_PROJETO_BASE = new Comando(20108, prefix + "projetos.negocio.ProcessadorReconsiderarAvaliacao");
	public static final Comando EXECUTAR_PROJETO_BASE = new Comando(20109, prefix + "projetos.negocio.ProcessadorProjetoBase");
	public static final Comando NAO_EXECUTAR_PROJETO_BASE = new Comando(20110, prefix + "projetos.negocio.ProcessadorProjetoBase");
	public static final Comando ALTERAR_ORCAMENTO_PROJETO_BASE = new Comando(20111, prefix + "projetos.negocio.ProcessadorProjetoBase");
	public static final Comando VINCULAR_PROJETO_UNIDADE_ORCAMENTARIA = new Comando(20112, prefix + "projetos.negocio.ProcessadorVincularUnidadeOrcamentaria");
	public static final Comando CADASTRAR_PLANO_TRABALHO_PROJETO = new Comando(20113, prefix + "projetos.negocio.ProcessadorPlanoTrabalhoProjeto");
	public static final Comando REMOVER_PLANO_TRABALHO_PROJETO = new Comando(20114, prefix + "projetos.negocio.ProcessadorPlanoTrabalhoProjeto");
	public static final Comando FINALIZAR_DISCENTE_PROJETO_PLANO = new Comando(20115, prefix + "projetos.negocio.ProcessadorDiscenteProjeto");
	public static final Comando INDICAR_DISCENTE_PROJETO_PLANO = new Comando(20116, prefix + "projetos.negocio.ProcessadorDiscenteProjeto");
	public static final Comando ENVIAR_RELATORIO_ACAO_ASSOCIADA = new Comando(20117, prefix + "projetos.negocio.ProcessadorRelatorioAcaoAssociada");
	public static final Comando SALVAR_RELATORIO_ACAO_ASSOCIADA = new Comando(20118, prefix + "projetos.negocio.ProcessadorRelatorioAcaoAssociada");
	public static final Comando REMOVER_RELATORIO_ACAO_ASSOCIADA = new Comando(20119, prefix + "projetos.negocio.ProcessadorRelatorioAcaoAssociada");
	public static final Comando VALIDAR_RELATORIO_ACAO_ASSOCIADA_DPTO = new Comando(20120, prefix + "projetos.negocio.ProcessadorRelatorioAcaoAssociada");
	public static final Comando VALIDAR_RELATORIO_ACAO_ASSOCIADA_COMITE = new Comando(20121, prefix + "projetos.negocio.ProcessadorRelatorioAcaoAssociada"); 
	public static final Comando DEVOLVER_RELATORIO_ACAO_ASSOCIADA_COORD = new Comando(20122, prefix + "projetos.negocio.ProcessadorRelatorioAcaoAssociada");
	public static final Comando RECUPERAR_ACAO_ASSOCIADA_REMOVIDA = new Comando(20123, prefix + "projetos.negocio.ProcessadorRecuperarAcaoAssociada");
	public static final Comando FINALIZAR_DISCENTE_PROJETO_PARA_AFASTADO = new Comando(20124, prefix + "projetos.negocio.ProcessadorDiscenteProjeto");
	
	
	public static final Comando SALVAR_MEMBRO_PROJETO_BASE = new Comando(20130, prefix + "projetos.negocio.ProcessadorMembroProjeto");
	public static final Comando REMOVER_MEMBRO_PROJETO_BASE = new Comando(20131, prefix + "projetos.negocio.ProcessadorMembroProjeto");
	public static final Comando DISTRIBUIR_PROJETO_MANUAL = new Comando(20132, prefix + "projetos.negocio.ProcessadorAvaliacaoProjeto");
	public static final Comando DISTRIBUIR_PROJETO_AUTOMATICO = new Comando(20133, prefix + "projetos.negocio.ProcessadorAvaliacaoProjeto");
	public static final Comando AVALIAR_PROJETO = new Comando(20134, prefix + "projetos.negocio.ProcessadorAvaliacaoProjeto");
	public static final Comando GRAVAR_QUESTIONARIO_ITENS = new Comando(20135, prefix + "projetos.negocio.ProcessadorQuestionarioAvaliacao");
	public static final Comando REMOVER_QUESTIONARIO_ITENS = new Comando(20136, prefix + "projetos.negocio.ProcessadorQuestionarioAvaliacao");
	public static final Comando REMOVER_AVALIACAO_PROJETO = new Comando(20137, prefix + "projetos.negocio.ProcessadorAvaliacaoProjeto");
	public static final Comando CONCEDER_RECURSOS_PROJETO = new Comando(20138, prefix + "projetos.negocio.ProcessadorProjetoBase");
	public static final Comando CONSOLIDAR_AVALIACOES_PROJETO = new Comando(20139, prefix + "projetos.negocio.ProcessadorAvaliacaoProjeto");
	public static final Comando CLASSIFICAR_PROJETOS = new Comando(20140, prefix + "projetos.negocio.ProcessadorAvaliacaoProjeto");
	public static final Comando ALTERAR_COORDENADOR_PROJETO_BASE = new Comando(20141, prefix + "projetos.negocio.ProcessadorMembroProjeto");
	
	/** Ensino */
	public static final Comando DADOS_TURMAS_CT = new Comando(25001, prefix + "ensino.graduacao.negocio.ProcessadorDadosTurmasCT");
	public static final Comando CADASTRAR_PLANO_MATRICULA_INGRESSANTE = new Comando(25002, prefix + "ensino.negocio.ProcessadorPlanoMatriculaIngressante");
	public static final Comando REMOVER_PLANO_MATRICULA_INGRESSANTE = new Comando(25003, prefix + "ensino.negocio.ProcessadorPlanoMatriculaIngressante");
	public static final Comando MATRICULAR_DISCENTE_PLANO_MATRICULA = new Comando(25004, prefix + "ensino.negocio.ProcessadorMatriculaDiscentePlanoMatricula");
	
	/** Portal Turma Virtual */
	public static final Comando GRAVAR_AGENDA_AULAS = new Comando(30001, prefix + "ava.negocio.ProcessadorConteudoAula");
	public static final Comando CADASTRAR_ARQUIVO = new Comando(30002, prefix + "ava.negocio.ProcessadorCadastroArquivo");
	public static final Comando CADASTRAR_AVA = new Comando(30003, prefix + "ava.negocio.ProcessadorCadastroAva");
	public static final Comando REMOVER_AVA = new Comando(30004, prefix + "ava.negocio.ProcessadorCadastroAva");
	public static final Comando AVALIAR_RESPOSTAS_TAREFA = new Comando(30005, prefix + "ava.negocio.ProcessadorAvaliacaoTarefa");
	public static final Comando RENOMEAR_ARQUIVO = new Comando(30006, prefix + "ava.negocio.ProcessadorPortaArquivos");
	public static final Comando REMOVER_ARQUIVO = new Comando(30007, prefix + "ava.negocio.ProcessadorPortaArquivos");
	public static final Comando CADASTRAR_PASTA = new Comando(30008, prefix + "ava.negocio.ProcessadorPortaArquivos");
	public static final Comando REMOVER_PASTA = new Comando(30009, prefix + "ava.negocio.ProcessadorPortaArquivos");
	public static final Comando VOTAR_ENQUETE = new Comando(30010, prefix + "ava.negocio.ProcessadorVotacaoEnquete");
	public static final Comando CADASTRAR_PASTA_TURMA = new Comando(30011, prefix + "ava.negocio.ProcessadorCadastroPastaTurma");
	public static final Comando REMOVER_ENQUETE = new Comando(30012, prefix + "ava.negocio.ProcessadorRemoverEnquete");
	public static final Comando MOVER_ARQUIVO = new Comando(30013, prefix + "ava.negocio.ProcessadorPortaArquivos");
	public static final Comando MOVER_PASTA = new Comando(30014, prefix + "ava.negocio.ProcessadorPortaArquivos");
	public static final Comando ATUALIZAR_AVA = new Comando(30015, prefix + "ava.negocio.ProcessadorCadastroAva");
	public static final Comando IMPORTAR_DADOS_TURMAS_ANTERIORES = new Comando(30016, prefix + "ava.negocio.ProcessadorImportacaoDadosTurmas");
	public static final Comando CADASTRAR_TAREFA = new Comando(30017, prefix + "ava.negocio.ProcessadorTarefaTurma");
	public static final Comando CADASTRAR_TOPICO_FORUM = new Comando(30018, prefix + "ava.negocio.ProcessadorMensagemForum");
	public static final Comando ATUALIZAR_ARQUIVO = new Comando(30019, prefix + "ava.negocio.ProcessadorCadastroArquivo");
	public static final Comando GERENCIAR_GRUPOS_DISCENTES = new Comando(30020, prefix + "ava.negocio.ProcessadorGruposDiscentes");
	public static final Comando CORRIGIR_TAREFA = new Comando(30021, prefix + "ava.negocio.ProcessadorCorrigirTarefa");
	public static final Comando REMOVER_TAREFA = new Comando(30022, prefix + "ava.negocio.ProcessadorTarefaTurma");
	public static final Comando ALTERAR_TAREFA = new Comando(30023, prefix + "ava.negocio.ProcessadorTarefaTurma");
	public static final Comando REMOVER_RESPOSTA = new Comando(30024, prefix + "ava.negocio.ProcessadorRemoverResposta");
	public static final Comando INATIVAR_AVA = new Comando(30025, prefix + "ava.negocio.ProcessadorCadastroAva"); 
	public static final Comando CADASTRAR_ARQUIVOS = new Comando(30026, prefix + "ava.negocio.ProcessadorCadastroArquivo");
	
	public static final Comando CADASTRAR_CV = new Comando(30027, prefix + "cv.negocio.ProcessadorCadastroCv");
	public static final Comando REMOVER_CV = new Comando(30028, prefix + "cv.negocio.ProcessadorCadastroCv");
	public static final Comando ATUALIZAR_CV = new Comando(30029, prefix + "cv.negocio.ProcessadorCadastroCv");
	public static final Comando CADASTRAR_COMUNIDADE_VIRTUAL = new Comando(30030, prefix + "cv.negocio.ProcessadorCadastroComunidadeVirtual");
	public static final Comando CADASTRAR_TOPICO_FORUM_COMUNIDADE = new Comando(30031, prefix + "cv.negocio.ProcessadorMensagemForumComunidade");
	public static final Comando REMOVER_ENQUETE_COMUNIDADE = new Comando(30032, prefix + "cv.negocio.ProcessadorRemoverEnqueteComunidade");
	public static final Comando REMOVER_TOPICO_FORUM_COMUNIDADE = new Comando(30033, prefix + "cv.negocio.ProcessadorMensagemForumComunidade");
	public static final Comando MARCAR_TAREFA_LIDA = new Comando(30034, prefix + "ava.negocio.ProcessadorTarefaTurma");
	public static final Comando CADASTRAR_ARQUIVO_CV = new Comando(30035, prefix + "cv.negocio.ProcessadorCadastroArquivoCV");
	public static final Comando VINCULAR_GRUPO_COMUNIDADE = new Comando(30036, prefix + "cv.negocio.ProcessadorCadastroComunidadeVirtual");
	public static final Comando CADASTRAR_MEMBRO_CONVITE = new Comando(30037, prefix + "cv.negocio.ProcessadorCadastroComunidadeVirtual");
	public static final Comando DESATIVAR_CONVITE_ACEITO_CV = new Comando(30038, prefix + "cv.negocio.ProcessadorCadastroComunidadeVirtual");
	public static final Comando CADASTRAR_SENHA_FREQUENCIA_ELETRONICA = new Comando(30039, prefix + "ava.negocio.ProcessadorFrequenciaEletronica");
	public static final Comando GERENCIAR_TOPICOS_AULA_EM_LOTE = new Comando(30040, prefix + "ava.negocio.ProcessadorGerenciarTopicosAulaLote");
	public static final Comando IMPORTAR_NOTAS_PLANILHA = new Comando (30041, prefix + "ava.negocio.ProcessadorImportarNotasPlanilha");
	
	public static final Comando SALVAR_PERGUNTA_QUESTIONARIO_TURMA = new Comando (30042, prefix + "ava.questionarios.negocio.ProcessadorSalvarPerguntaQuestionarioTurma");
	public static final Comando SALVAR_ORDEM_DAS_QUESTOES = new Comando (30043, prefix + "ava.questionarios.negocio.ProcessadorSalvarOrdemDasQuestoes");
	public static final Comando ADICIONAR_PERGUNTAS_DO_BANCO = new Comando (30044, prefix + "ava.questionarios.negocio.ProcessadorAdicionarPerguntasDoBanco");
	public static final Comando SALVAR_QUESTIONARIO_TURMA = new Comando (30045, prefix + "ava.questionarios.negocio.ProcessadorSalvarQuestionarioTurma");
	public static final Comando CORRIGIR_QUESTIONARIO_TURMA = new Comando (30046, prefix + "ava.questionarios.negocio.ProcessadorCorrigirQuestionarioTurma");
	public static final Comando PUBLICAR_NOTAS_QUESTIONARIO_TURMA = new Comando (30047, prefix + "ava.questionarios.negocio.ProcessadorPublicarNotasQuestionarioTurma");
	public static final Comando ALTERAR_RESPOSTAS_QUESTIONARIO_TURMA = new Comando (30048, prefix + "ava.questionarios.negocio.ProcessadorAlterarRespostasQuestionarioTurma");
	public static final Comando REMOVER_RESPOSTAS_QUESTIONARIO_TURMA = new Comando (30049, prefix + "ava.questionarios.negocio.ProcessadorAlterarRespostasQuestionarioTurma");
	public static final Comando REMOVER_QUESTIONARIO_TURMA = new Comando(30050, prefix + "ava.questionarios.negocio.ProcessadorSalvarQuestionarioTurma");
	public static final Comando REMOVER_CONJUNTO_RESPOSTAS_QUESTIONARIO_TURMA = new Comando (30051, prefix + "ava.questionarios.negocio.ProcessadorConjuntoRespostasQuestionarioTurma");
	public static final Comando REMOVER_PERGUNTA_QUESTIONARIO_TURMA = new Comando (30052, prefix + "ava.questionarios.negocio.ProcessadorRemoverPerguntaQuestionarioTurma");

	
	public static final Comando CADASTRAR_VIDEO_TURMA = new Comando (30053, prefix + "ava.negocio.ProcessadorCadastroVideoTurma");
	public static final Comando INATIVAR_VIDEO_TURMA = new Comando (30054, prefix + "ava.negocio.ProcessadorCadastroVideoTurma");
	
	
	public static final Comando CADASTRAR_FORUM_GERAL = new Comando(30055, prefix + "ava.forum.negocio.ProcessadorForumGeral");
	public static final Comando ALTERAR_FORUM_GERAL = new Comando(30056, prefix + "ava.forum.negocio.ProcessadorForumGeral");
	public static final Comando REMOVER_FORUM_GERAL = new Comando(30057, prefix + "ava.forum.negocio.ProcessadorForumGeral");
	
	public static final Comando CADASTRAR_FORUM_GERAL_MENSAGEM = new Comando(30058, prefix + "ava.forum.negocio.ProcessadorForumGeralMensagem");
	public static final Comando ALTERAR_FORUM_GERAL_MENSAGEM = new Comando(30059, prefix + "ava.forum.negocio.ProcessadorForumGeralMensagem");
	public static final Comando REMOVER_FORUM_GERAL_MENSAGEM = new Comando(30060, prefix + "ava.forum.negocio.ProcessadorForumGeralMensagem");
	public static final Comando INTERROMPER_FORUM_GERAL_MENSAGEM = new Comando(30061, prefix + "ava.forum.negocio.ProcessadorForumGeralMensagem");
	
	public static final Comando CADASTRAR_FORUM_GERAL_PARTICIPANTE = new Comando(30062, prefix + "ava.forum.negocio.ProcessadorForumGeralParticipante");
	public static final Comando REMOVER_FORUM_GERAL_PARTICIPANTE = new Comando(30063, prefix + "ava.forum.negocio.ProcessadorForumGeralParticipante");

	public static final Comando CADASTRAR_FORUM_TURMA = new Comando(30064, prefix + "ava.forum.negocio.ProcessadorForumTurma");
	public static final Comando ALTERAR_FORUM_TURMA = new Comando(30065, prefix + "ava.forum.negocio.ProcessadorForumTurma");
	public static final Comando REMOVER_FORUM_TURMA = new Comando(30066, prefix + "ava.forum.negocio.ProcessadorForumTurma");

	public static final Comando ORDENAR_MATERIAL_TURMA_CIMA = new Comando(30071, prefix + "ava.negocio.ProcessadorOrdenarMaterialTurma");
	public static final Comando ORDENAR_MATERIAL_TURMA_BAIXO = new Comando(30072, prefix + "ava.negocio.ProcessadorOrdenarMaterialTurma");
	public static final Comando ORDENAR_MATERIAL_TURMA_ESQUERDA = new Comando(30073, prefix + "ava.negocio.ProcessadorOrdenarMaterialTurma");
	public static final Comando ORDENAR_MATERIAL_TURMA_DIREITA = new Comando(30074, prefix + "ava.negocio.ProcessadorOrdenarMaterialTurma");
	public static final Comando ORDENAR_MATERIAL_TURMA_TROCAR_POSICAO = new Comando(30075, prefix + "ava.negocio.ProcessadorOrdenarMaterialTurma");
	
	public static final Comando REMOVER_MENSAGEM_TOPICO_FORUM = new Comando(30076, prefix + "ava.negocio.ProcessadorMensagemForum");
	
	public static final Comando SALVAR_CONFIGURACOES_CV = new Comando(30077, prefix + "cv.negocio.ProcessadorSalvarConfiguracoesComunidade");
	public static final Comando MOVER_TOPICOS_VC = new Comando(30078, prefix + "cv.negocio.ProcessadorMoverTopicos");
	public static final Comando ESCONDE_TOPICOS_CV = new Comando(30079, prefix + "cv.negocio.ProcessadorTopicoComunidade");
	public static final Comando EXIBIR_TOPICOS_CV = new Comando(30080, prefix + "cv.negocio.ProcessadorTopicoComunidade");
	
	public static final Comando REMOVER_DATA_AVALIACAO = new Comando(30081, prefix + "ava.negocio.ProcessadorDataAvaliacao");
	public static final Comando CADASTRAR_REGISTRO_ACAO_AVA = new Comando(30082, prefix + "ava.negocio.ProcessadorCadastroRegistroAcaoAva");

	public static final Comando DENUNCIAR_MENSAGEM_TOPICO_FORUM = new Comando(30083, prefix + "ava.negocio.ProcessadorMensagemForum");
	
	/** Portal da Turma */

	/** EAD */
	public static final Comando AVALIAR_DISCENTE_EAD = new Comando(40010, prefix + "ead.negocio.ProcessadorAvaliacaoSemanalEad");
	public static final Comando CADASTRO_COORDENADOR_TUTORIA = new Comando(40011, prefix + "ead.negocio.ProcessadorCoordenacaoTutoria");
	public static final Comando CADASTRAR_TUTORIA_ALUNO = new Comando(40012, prefix + "ead.negocio.ProcessadorTutoriaAluno");
	public static final Comando REMOVER_TUTORIA_ALUNO = new Comando(40013, prefix + "");
	public static final Comando CADASTRAR_POLO = new Comando(40014, prefix + "ead.negocio.ProcessadorCadastroPolo");
	public static final Comando REMOVER_POLO = new Comando(40015, prefix + "ead.negocio.ProcessadorCadastroPolo");
	public static final Comando CADASTRAR_USUARIO_TUTOR = new Comando(40016, prefix + "ead.negocio.ProcessadorCadastroUsuarioTutor");
	public static final Comando CADASTRAR_HORARIO_TUTOR = new Comando(40017, prefix + "ead.negocio.ProcessadorCadastroHorarioTutor");
	public static final Comando ALTERAR_POLO_DISCENTE = new Comando(40018, prefix + "ead.negocio.ProcessadorAlterarPolo");
	public static final Comando HABILITAR_AVALIACAO_SEMANA = new Comando(40019, prefix + "ead.negocio.ProcessadorHabilitacaoAvaliacao");
	public static final Comando DESABILITAR_AVALIACAO_SEMANA = new Comando(40020, prefix + "ead.negocio.ProcessadorHabilitacaoAvaliacao");
	public static final Comando CADASTRAR_USUARIO_COORD_POLO = new Comando(40021, prefix + "ead.negocio.ProcessadorCadastroUsuarioCoordPolo");
	public static final Comando CADASTRAR_ITEM_PROGRAMA = new Comando(40022, prefix + "ead.negocio.ProcessadorCadastroItemPrograma");
	public static final Comando MATRICULAR_EM_LOTE = new Comando(40023, prefix + "ead.negocio.ProcessadorLoteMatricula");
	public static final Comando REATIVAR_USUARIO_TUTOR = new Comando(40024, prefix + "ead.negocio.ProcessadorCadastroUsuarioTutor");
	public static final Comando INATIVAR_METODOLOGIA_AVALIACAO = new Comando(40025, prefix + "ead.negocio.ProcessadorMetodologiaAvaliacao");
	public static final Comando ALUNO_EAD_DEFININDO_HORARIO_TUTORIA = new Comando(40027, prefix + "ensino.graduacao.negocio.ProcessadorSolicitacaoMatricula");
	public static final Comando ATUALIZAR_CARGA_HORARIA_EAD = new Comando(40028, prefix + "ensino.graduacao.negocio.ProcessadorCargaHorariaEad");
	public static final Comando ALTERAR_DADOS_USUARIO_ALUNO = new Comando(40029, prefix + "ead.negocio.ProcessadorDadosUsuarioAluno"); 
	public static final Comando CADASTRAR_METODOLOGIA_AVALIACAO = new Comando(40030, prefix + "ead.negocio.ProcessadorMetodologiaAvaliacao"); 
	public static final Comando DADOS_EAD = new Comando(40031, prefix + "ead.negocio.ProcessadorDadosEad");
	public static final Comando CADASTRAR_TUTOR_DISTANCIA = new Comando(40032, prefix + "ead.negocio.ProcessadorTutorOrientador");

	/** EAD */

	
	/** SAE **/
	public static final Comando CADASTRAR_DIAS_ALIMENTACAO = new Comando(60001, prefix + "assistencia.negocio.ProcessadorCadastroDiasAlimentacao");
	public static final Comando CADASTRAR_BOLSA_AUXILIO = new Comando(60002, prefix + "assistencia.negocio.ProcessadorBolsaAuxilio");
	public static final Comando ALTERAR_BOLSA_AUXILIO = new Comando(60003, prefix + "assistencia.negocio.ProcessadorBolsaAuxilio");
	public static final Comando ALTERAR_SITUACAO_BOLSA = new Comando(60004, prefix + "assistencia.negocio.ProcessadorBolsaAuxilio");
	public static final Comando CADASTRAR_PERIODO_RESULTADOS = new Comando(60005, prefix + "assistencia.negocio.ProcessadorCadastroPeriodoResultado");
	public static final Comando REMOVER_PERIODO_RESULTADOS = new Comando(60006, prefix + "assistencia.negocio.ProcessadorCadastroPeriodoResultado");
	public static final Comando CADASTRAR_PARAMETROS_CADASTRO_UNICO = new Comando(60007, prefix + "assistencia.negocio.ProcessadorCadastroUnico");
	public static final Comando ALTERAR_PARAMETROS_CADASTRO_UNICO = new Comando(60008, prefix + "assistencia.negocio.ProcessadorCadastroUnico");
	public static final Comando ADESAO_CADASTRO_UNICO = new Comando(60009, prefix + "assistencia.negocio.ProcessadorAdesaoCadastroUnico");
	public static final Comando ALTERAR_ADESAO_CADASTRO_UNICO = new Comando(60010, prefix + "assistencia.negocio.ProcessadorAdesaoCadastroUnico");
	public static final Comando CADASTRAR_ANO_PERIODO_REFERENCIA = new Comando(600011, prefix + "assistencia.negocio.ProcessadorCadastroPeriodoResultado");
	public static final Comando ATUALIZAR_DIAS_ALIMENTACAO = new Comando(600012, prefix + "assistencia.negocio.ProcessadorCadastroDiasAlimentacao");
	public static final Comando ASSOCIAR_PESSOA_CARTAO_RU = new Comando(600013, prefix + "assistencia.negocio.ProcessadorCartaoBeneficioDiscente");
	public static final Comando BLOQUEAR_CARTAO_BENEFICIO = new Comando(600014, prefix + "assistencia.negocio.ProcessadorCartaoBeneficioDiscente");
	public static final Comando CADASTRAR_INDICES_ACADEMICOS = new Comando(600015, prefix + "assistencia.negocio.ProcessadorDadosIndiceAcademico");
	public static final Comando REMOVER_INDICES_ACADEMICOS = new Comando(600016, prefix + "assistencia.negocio.ProcessadorDadosIndiceAcademico");
	public static final Comando FINALIZAR_BOLSISTAS = new Comando(600017, prefix + "assistencia.negocio.ProcessadorFinalizacaoBolsista");
	public static final Comando CADASTRAR_CALENDARIO_SAE = new Comando(600018, prefix + "assistencia.negocio.ProcessadorCalendarioSAE");
	public static final Comando REMOVER_CALENDARIO_SAE = new Comando(600019, prefix + "assistencia.negocio.ProcessadorCalendarioSAE");
	public static final Comando CADASTRAR_RESTRICAO_SAE = new Comando(600020, prefix + "assistencia.negocio.ProcessadorRestricaoSAE");
	public static final Comando REMOVER_RESTRICAO_SAE = new Comando(600021, prefix + "assistencia.negocio.ProcessadorRestricaoSAE");
	public static final Comando CADASTRAR_BOLSISTA_SIPAC = new Comando(600022, prefix + "assistencia.negocio.ProcessadorHomologarBolsista");
	public static final Comando REMOVER_BOLSISTA_SIPAC = new Comando(600023, prefix + "assistencia.negocio.ProcessadorHomologarBolsista");

	/** SAE **/

	public static final Comando CADASTRAR_SENHA_MOBILE = new Comando(7000, prefix + "negocio.ProcessadorSenhaMobile");
	public static final Comando ALTERAR_SENHA_MOBILE = new Comando(7001, prefix + "negocio.ProcessadorSenhaMobile");

	/** Vestibular */
	public static final Comando CADASTRAR_INSCRICAO_FISCAL = new Comando(50001, prefix + "vestibular.negocio.ProcessadorInscricaoFiscal");
	public static final Comando PROCESSAR_SELECAO_FISCAL = new Comando(50002, prefix + "vestibular.negocio.ProcessadorSelecaoFiscal");
	public static final Comando PROCESSAR_PRESENCA_REUNIAO_FISCAL = new Comando(50003, prefix + "vestibular.negocio.ProcessadorAssiduidadeFiscal");
	public static final Comando PROCESSAR_FREQUENCIA_APLICACAO_FISCAL = new Comando(50004, prefix + "vestibular.negocio.ProcessadorAssiduidadeFiscal");
	public static final Comando TRANSFERENCIA_FISCAL_LOCAL_APLICACAO = new Comando(50005, prefix + "vestibular.negocio.ProcessadorTransferenciaFiscal");
	public static final Comando CADASTRAR_INSCRICAO_VESTIBULAR = new Comando(50006, prefix + "vestibular.negocio.ProcessadorInscricaoVestibular");
	public static final Comando ASSOCIAR_FISCAL_LOCAL_APLICACAO = new Comando(50007, prefix + "vestibular.negocio.ProcessadorAlocacaoFiscalLocalProva");
	public static final Comando SELECAO_MANUAL_FISCAL = new Comando(50008, prefix + "vestibular.negocio.ProcessadorSelecaoManualFiscal");
	public static final Comando CONVOCAR_FISCAIS_RESERVAS = new Comando(50009, prefix + "vestibular.negocio.ProcessadorAssiduidadeFiscal");
	public static final Comando CADASTRAR_PROCESSO_SELETIVO_VESTIBULAR = new Comando(50010, prefix + "vestibular.negocio.ProcessadorProcessoSeletivoVestibular");
	public static final Comando CADASTRAR_AVISO_PROCESSO_SELETIVO_VESTIBULAR = new Comando(50011, prefix + "vestibular.negocio.ProcessadorAvisoProcessoSeletivoVestibular");
	public static final Comando CADASTRAR_JUSTIFICATIVA_AUSENCIA_FISCAL = new Comando(50012, prefix + "vestibular.negocio.ProcessadorJustificativaAusencia");
	public static final Comando CADASTRAR_ISENTO_VESTIBULAR = new Comando(50013, prefix + "vestibular.negocio.ProcessadorCadastroIsento");
	public static final Comando CADASTRAR_PESSOA_VESTIBULAR = new Comando(50014, prefix + "vestibular.negocio.ProcessadorPessoaVestibular");
	public static final Comando RECUPERAR_SENHA_VESTIBULAR = new Comando(50015, prefix + "vestibular.negocio.ProcessadorPessoaVestibular");
	public static final Comando VALIDAR_INSCRICAO_VESTIBULAR_LOTE = new Comando(50016, prefix + "vestibular.negocio.ProcessadorValidarInscricaoVestibular");
	public static final Comando ATUALIZAR_STATUS_FOTO = new Comando(50017, prefix + "vestibular.negocio.ProcessadorValidacaoFoto");
	public static final Comando ATUALIZAR_EMAIL_CANDIDATO = new Comando(50018, prefix + "vestibular.negocio.ProcessadorPessoaVestibular");
	public static final Comando IMPORTAR_LOCAIS_PROVA_CANDIDATO = new Comando(50019, prefix + "vestibular.negocio.ProcessadorLocalProvaCandidato");
	public static final Comando PROCESSAR_IMPORTACAO_DADOS_PROCESSO_SELETIVO = new Comando(50020, prefix + "vestibular.negocio.ProcessadorImportacaoDadosProcessoSeletivo");
	public static final Comando CONVOCACAO_PROCESSO_SELETIVO_DISCENTE = new Comando(50021, prefix + "vestibular.negocio.ProcessadorConvocacaoProcessoSeletivo");
	public static final Comando MARCAR_GRUS_QUITADAS = new Comando(50022, prefix + "vestibular.negocio.ProcessadorMarcarGRUsQuitadas");
	public static final Comando CADASTRAR_DISCENTE_CONVOCADO = new Comando(50023, prefix + "vestibular.negocio.ProcessadorCadastramentoDiscenteConvocado");
	
	/** SUB SISTEMA DA BIBLIOTECA */
	
	public static final Comando CATALOGA_TITULO = new Comando(600010, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorCatalogaTitulo");
	public static final Comando ATUALIZA_TITULO = new Comando(600020, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorAtualizaTitulo");
	public static final Comando CADASTRA_EXEMPLAR = new Comando(600030, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorCadastraExemplar");
	public static final Comando ATUALIZA_EXEMPLAR = new Comando(600050, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorAtualizaExemplar");
	public static final Comando INCLUI_FASCICULO_ACERVO = new Comando(600040, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorIncluiFasciculoNoAcervo");
	public static final Comando ATUALIZA_FASCICULO = new Comando(600060, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorAtualizaFasciculo");
	public static final Comando CADASTRA_ANEXO_EXEMPLAR = new Comando(600070, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorCadastraAnexoExemplar");
	public static final Comando CATALOGA_AUTORIDADE = new Comando(600080, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorCatalogaAutoridade");
	public static final Comando ATUALIZA_AUTORIDADE = new Comando(600090, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorAtualizaAutoridade");
	public static final Comando CATALOGA_ARTIGO_DE_PERIODICO = new Comando(600100, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorCatalogaAtualizaArtigoPeriodico");
	public static final Comando ACOES_AUXILIARES_EXPORTA_TITULO_AUTORIDADE = new Comando(60011, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorAcoesAuxiliaresExportaTituloAutoridade");
	public static final Comando SALVA_ENTIDADES_IMPORTADAS = new Comando(60012, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorSalvaEntidadesImportadas");
	public static final Comando DAR_BAIXA_EXEMPLAR = new Comando(60013, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorDarBaixaExemplar");
	public static final Comando DAR_BAIXA_FASCICULO = new Comando(60014, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorDarBaixaFasciculo");
	public static final Comando SUBSTITUI_FASCICULO = new Comando(60015, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorSubstituirFasciculo");
	public static final Comando ATRIBUI_FASCICULO_AO_ARTIGO = new Comando(60016, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorAtribuiFasciculoAoArtigo");
	public static final Comando TRANSFERE_FASCICULOS_ENTRE_BIBLIOTECAS = new Comando(60017, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorTransfereFasciculosEntreBibliotecas");
	public static final Comando REMOVE_ENTIDADES_DO_ACERVO = new Comando(60018, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorRemoveEntidadesDoAcervo");
	public static final Comando TRANSFERE_EXEMPLARES_ENTRE_TITULOS = new Comando(60019, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorTransfereExemplaresEntreTitulos");
	public static final Comando REMOVE_MATERIAIS_MARCADOS_GERACAO_ETIQUETA = new Comando(60020, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorRemoveMateriaisMarcadosGeracaoEtiqueta");
	public static final Comando TRANSFERE_EXEMPLARES_ENTRE_BIBLIOTECAS = new Comando(60021, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorTransfereExemplaresEntreBibliotecas");
	public static final Comando CONFIRMA_TRANSFERENCIA_FASCICULOS_ENTRE_BIBLIOTECAS = new Comando(60022, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorConfirmaTransferenciaFasciculosEntreBiblioteca");
	public static final Comando ASSOCIA_ASSINATURA_TRANSFERENCIA_FASCIULOS = new Comando(60023, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorAssociaAssinaturaTransferenciaFasciculos");
	public static final Comando REMOVER_MATERIAIS_ACERVO = new Comando(60024, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorRemoverMateriaisAcervo");
	public static final Comando REMOVER_STATUS_MATERIAL_TIPO_MATERIAL_E_TIPO_EMPRESTIMO = new Comando(60025, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorRemoveStatusTipoMaterialETipoEmprestimo");
	public static final Comando SUBSTITUI_EXEMPLAR = new Comando(60026, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorSubstituirExemplar");
	public static final Comando DESFAZER_BAIXA_EXEMPLAR = new Comando(60027, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorDesfazerBaixaExemplar");
	public static final Comando DESFAZER_BAIXA_FASCICULO = new Comando(60028, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorDesfazerBaixaFasciculo");
	public static final Comando ALTERA_DADOS_VARIOS_MATERIAIS = new Comando(60029, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorAlteraVariosMateriais");
	public static final Comando ALTERAR_MOTIVO_BAIXA_VARIOS_MATERIAIS = new Comando(60030, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorAlterarMotivoBaixaVariosMateriais");
	public static final Comando TRANSFERIR_MATERIAIS_ENTRE_SETORES = new Comando(60031, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorTransferirMateriaisEntreSetores");
	public static final Comando ATUALIZA_RELACIONAMENTO_CLASSIFICACAO_BIBLIOTECA = new Comando(60032, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorAtualizaRelacionamentoClassificacaoBiblioteca");
	public static final Comando CADASTRA_INFORMACOES_AREAS_CNPQ_BIBLIOTECA = new Comando(60033, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorCadastraInformacoesAreasCNPQBiblioteca");
	public static final Comando CADASTRA_CLASSIFICACAO_BIBLIOGRAFICA = new Comando(60034, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorCadastraClassificacaoBibliografica");
	public static final Comando CADASTRA_INVENTARIO_ACERVO_BIBLIOTECA = new Comando(60035, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorCadastraInventarioAcervoBiblioteca");
	public static final Comando FECHA_INVENTARIO_ACERVO_BIBLIOTECA = new Comando(60036, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorFechaInventarioAcervoBiblioteca");
	public static final Comando ABRE_INVENTARIO_ACERVO_BIBLIOTECA = new Comando(60037, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorAbreInventarioAcervoBiblioteca");
	public static final Comando ALTERAR_STATUS_MATERIAL = new Comando(60038, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorAlterarStatusMaterial");
	public static final Comando REGISTRAR_MATERIAIS_INVENTARIO = new Comando(60039, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorRegistraMateriaisInventario");
	public static final Comando REMOVE_REGISTRO_MATERIAIS_INVENTARIO = new Comando(60040, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorRegistraMateriaisInventario");
	
	public static final Comando REGISTRA_CHEGADA_FASCICULO = new Comando(60101, prefix + "biblioteca.aquisicao.negocio.ProcessadorRegistraChegadaFasciculo");
	public static final Comando CADASTRA_ALTERA_ASSINATURAS_DE_PERIODICO = new Comando(60102, prefix + "biblioteca.aquisicao.negocio.ProcessadorCadastraAlteraAssinaturaDePeriodico");
	public static final Comando REGISTRA_CHEGADA_SUPLEMENTO = new Comando(60103, prefix + "biblioteca.aquisicao.negocio.ProcessadorRegistraChegadaSuplemento");
	public static final Comando RENOVA_ASSINATURA_PERIODICO = new Comando(60104, prefix + "biblioteca.aquisicao.negocio.ProcessadorRenovaAssinaturaPeriodico");
	public static final Comando REMOVE_ASSINATURAS_DE_PERIODICO = new Comando(60105, prefix + "biblioteca.aquisicao.negocio.ProcessadorRemoveAssinaturaPeriodico");
	public static final Comando ALTERA_FASCICULOS_REGISTRADOS = new Comando(60106, prefix + "biblioteca.aquisicao.negocio.ProcessadorAlteraFasciculoRegistrado");
	public static final Comando ALTERA_ASSOCIACAO_ASSINATURA_TITULO = new Comando(60107, prefix + "biblioteca.aquisicao.negocio.ProcessadorAlteraAssociacaoAssinaturaTitulo");
	public static final Comando REMOVER_FREQUENCIA_PERIODICO = new Comando(60108, prefix + "biblioteca.aquisicao.negocio.ProcessadorRemoveFrequenciaPeriodico");
	public static final Comando PROCESSADOR_REORGANIZA_CODIGO_BARRAS_FASCICULOS = new Comando(60109, prefix + "biblioteca.aquisicao.negocio.ProcessadorReorganizaCodigoBarrasFasciculos");
	
	
	public static final Comando REALIZA_EMPRESTIMO = new Comando(60201, prefix + "biblioteca.circulacao.negocio.ProcessadorRealizaEmprestimo");
	public static final Comando RENOVA_EMPRESTIMO = new Comando(60202, prefix + "biblioteca.circulacao.negocio.ProcessadorRenovaEmprestimo");
	public static final Comando DEVOLVE_EMPRESTIMO = new Comando(60203, prefix + "biblioteca.circulacao.negocio.ProcessadorDevolveEmprestimo");
	public static final Comando ESTORNA_EMPRESTIMO = new Comando(60204, prefix + "biblioteca.circulacao.negocio.ProcessadorEstornaEmprestimo");
	public static final Comando PRORROGA_PRAZOS_EMPRESTIMOS = new Comando(60205, prefix + "biblioteca.circulacao.negocio.ProcessadorProrrogaPrazoEmprestimo");
	public static final Comando DESFAZ_OPERACAO = new Comando(60206, prefix + "biblioteca.circulacao.negocio.ProcessadorDesfazOperacao");
	public static final Comando DEVOLVE_EMPRESTIMO_MATERIAL_PERDIDO = new Comando(60207, prefix + "biblioteca.circulacao.negocio.ProcessadorDevolveEmprestimoMaterialPerdido");
	public static final Comando CADASTRAR_SENHA_BIBLIOTECA = new Comando(60208, prefix + "biblioteca.circulacao.negocio.ProcessadorSenhaUsuarioBiblioteca");
	public static final Comando ALTERAR_SENHA_BIBLIOTECA = new Comando(60209, prefix + "biblioteca.circulacao.negocio.ProcessadorSenhaUsuarioBiblioteca");
	public static final Comando CADASTRAR_USUARIO_EXTERNO_BIBLIOTECA = new Comando(60211, prefix + "biblioteca.circulacao.negocio.ProcessadorUsuarioExternoBiblioteca");
	public static final Comando CADASTRA_ALTERA_POLITICAS_EMPRESTIMOS = new Comando(60212, prefix + "biblioteca.circulacao.negocio.ProcessadorCadastraAlteraPoliticaEmprestimo");
	public static final Comando ESTORNAR_SUSPENSOES_BIBLIOTECA = new Comando(60213, prefix + "biblioteca.circulacao.negocio.ProcessadorEstornarSuspensoes");
	public static final Comando CADASTRA_INTERRUPCAO_BIBLIOTECA = new Comando(60214, prefix + "biblioteca.circulacao.negocio.ProcessadorCadastraInterrupcaoBiblioteca");
	public static final Comando COMUNICAR_MATERIAL_PERDIDO = new Comando(60215, prefix + "biblioteca.circulacao.negocio.ProcessadorComunicarMaterialPerdido");
	public static final Comando ALTERAR_SUSPENSAO_USUARIO_BIBLIOTECA = new Comando(60216, prefix + "biblioteca.circulacao.negocio.ProcessadorAlterarSuspensaoUsuarioBiblioteca");
	public static final Comando REMOVE_INTERRUPCAO_BIBLIOTECA = new Comando(60217, prefix + "biblioteca.circulacao.negocio.ProcessadorRemoveInterrupcaoBiblioteca");
	public static final Comando CADASTRA_ATUALIZA_BIBLIOTECA_EXTERNA = new Comando(60218, prefix + "biblioteca.circulacao.negocio.ProcessadorCadastraAtualizaBibliotecaExterna");
	public static final Comando INCLUIR_NOTA_MATERIAL_INFORMACIONAL = new Comando(60219, prefix + "biblioteca.circulacao.negocio.ProcessadorModificarNotaCirculacaoMaterialInformacional");
	public static final Comando SOLICITA_RESERVA_MATERIAL_BIBLIOTECA = new Comando(60220, prefix + "biblioteca.circulacao.negocio.ProcessadorSolicitaReservaMaterialBiblioteca");
	public static final Comando CANCELA_RESERVA_MATERIAL_BIBLIOTECA = new Comando(60221, prefix + "biblioteca.circulacao.negocio.ProcessadorCancelaReservaMaterialBiblioteca");
	public static final Comando QUITA_MULTA_USUARIO_BIBLIOTECA = new Comando(60222, prefix + "biblioteca.circulacao.negocio.ProcessadorQuitaMultaUsuarioBiblioteca");
	public static final Comando ESTORNA_MULTA_USUARIO_BIBLIOTECA = new Comando(60223, prefix + "biblioteca.circulacao.negocio.ProcessadorEstornarMultaUsuarioBiblioteca");
	public static final Comando CRIA_GRU_MULTA_BIBLIOTECA = new Comando(60224, prefix + "biblioteca.circulacao.negocio.ProcessadorCriaGRUMultaBiblioteca");
	public static final Comando EMITE_QUITACAO_BIBLIOTECA = new Comando(60225, prefix + "biblioteca.circulacao.negocio.ProcessadorEmiteQuitacaoBiblioteca");
	public static final Comando DESFAZ_QUITACAO_BIBLIOTECA = new Comando(60226, prefix + "biblioteca.circulacao.negocio.ProcessadorDesfazQuitacaoUsuarioBiblioteca");
	public static final Comando REMOVER_NOTA_MATERIAL_INFORMACIONAL = new Comando(60227, prefix + "biblioteca.circulacao.negocio.ProcessadorModificarNotaCirculacaoMaterialInformacional");
	public static final Comando EDITAR_NOTA_MATERIAL_INFORMACIONAL = new Comando(60228, prefix + "biblioteca.circulacao.negocio.ProcessadorModificarNotaCirculacaoMaterialInformacional");
	
	
	public static final Comando CADASTRAR_SOLICITACAO_NORMALIZACAO = new Comando(60301, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorSolicitacaoNormalizacao");
	public static final Comando ATENDER_SOLICITACAO_NORMALIZACAO = new Comando(60302, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorSolicitacaoNormalizacao");
	public static final Comando CANCELAR_SOLICITACAO_NORMALIZACAO = new Comando(60303, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorSolicitacaoNormalizacao");
	public static final Comando CADASTRAR_MOVIMENTO_DIARIO_USUARIOS = new Comando(60304, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorCadastroMovimentoDiarioUsuarios");
	public static final Comando REGISTRAR_CONSULTAS_DIARIA_MATERIAIS = new Comando(60305, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorRegistraConsultasDiariasMateriais");
	public static final Comando REGISTRAR_CONSULTAS_DIARIA_MATERIAIS_LEITOR = new Comando(60306, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorRegistraConsultasDiariasMateriaisLeitor");
	public static final Comando ALTERAR_SOLICITACAO_NORMALIZACAO = new Comando(60307, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorSolicitacaoNormalizacao");
	public static final Comando CADASTRAR_SOLICITACAO_LEVANTAMENTO_INFRA = new Comando(60308, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorLevantamentoInfra");
	public static final Comando SALVAR_SOLICITACAO_LEVANTAMENTO_INFRA = new Comando(60309, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorLevantamentoInfra");
	public static final Comando ATENDER_SOLICITACAO_LEVANTAMENTO_INFRA = new Comando(60310, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorLevantamentoInfra");
	public static final Comando CANCELAR_SOLICITACAO_LEVANTAMENTO_INFRA = new Comando(60311, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorLevantamentoInfra");
	public static final Comando CADASTRAR_SOLICITACAO_CATALOGACAO = new Comando(60312, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorSolicitacaoCatalogacao");
	public static final Comando ATENDER_SOLICITACAO_CATALOGACAO = new Comando(60313, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorSolicitacaoCatalogacao");
	public static final Comando CANCELAR_SOLICITACAO_CATALOGACAO = new Comando(60314, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorSolicitacaoCatalogacao");
	public static final Comando ALTERAR_SOLICITACAO_CATALOGACAO = new Comando(60315, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorSolicitacaoCatalogacao");
	public static final Comando SALVAR_SOLICITACAO_CATALOGACAO = new Comando(60316, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorSolicitacaoCatalogacao");
	public static final Comando CADASTRAR_SOLICITACAO_ORIENTACAO = new Comando(60317, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorSolicitacaoOrientacao");
	public static final Comando ATENDER_SOLICITACAO_ORIENTACAO = new Comando(60318, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorSolicitacaoOrientacao");
	public static final Comando CONFIRMAR_SOLICITACAO_ORIENTACAO = new Comando(60319, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorSolicitacaoOrientacao");
	public static final Comando NAO_CONFIRMAR_HORARIO_AGENDADO_ORIENTACAO = new Comando(60320, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorSolicitacaoOrientacao");
	public static final Comando ALTERAR_SOLICITACAO_ORIENTACAO = new Comando(60321, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorSolicitacaoOrientacao");
	public static final Comando TRANSFERIR_SOLICITACAO_ORIENTACAO = new Comando(60322, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorSolicitacaoOrientacao");
	public static final Comando CANCELAR_ATENDIMENTO_SOLICITACAO_ORIENTACAO = new Comando(60323, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorSolicitacaoOrientacao");
	public static final Comando REENVIAR_SOLICITACAO_CATALOGACAO = new Comando(60324, prefix + "biblioteca.informacao_referencia.negocio.ProcessadorSolicitacaoCatalogacao");
	
	
	public static final Comando CADATRA_ATUALIZA_ETIQUETAS_LOCAIS = new Comando(60401, prefix + "biblioteca.negocio.ProcessadorCadastraAtualizaEtiquetasLocais");
	public static final Comando ATUALIZA_RELACAO_CLASSIFICACAO_AREAS_CNPq = new Comando(60402, prefix + "biblioteca.processos_tecnicos.negocio.ProcessadorAtualizaRelacaoClassificacaoAreasCNPq");
	public static final Comando ALTERAR_BIBLIOTECA_INTERNA = new Comando(60403, prefix + "biblioteca.negocio.ProcessadorAtualizaBibliotecaInterna");
	public static final Comando REMOVER_COLECAO = new Comando(60404, prefix + "biblioteca.negocio.ProcessadorRemoveColecao");
	public static final Comando REMOVER_FORMA_DOCUMENTO = new Comando(60406, prefix + "biblioteca.negocio.ProcessadorRemoveFormaDocumento");
	public static final Comando REMOVER_SITUACAO = new Comando(60407, prefix + "biblioteca.negocio.ProcessadorRemoveSituacao");
	public static final Comando CADASTRA_ATUALIZA_PERFIL_INTERESSE_USUARIO_BIBLIOTECA = new Comando(60408, prefix + "biblioteca.negocio.ProcessadorCadastraAtualizaPerfilInteresseUsuarioBiblioteca");
	
	
	
	/** SUB SISTEMA DA INFRA-ESTRUTURA FÍSICA*/

	public static final Comando ADICIONAR_ESPACO_FISICO = new Comando(70001, prefix + "infra_estrutura_fisica.negocio.ProcessadorEspacoFisico");
	public static final Comando ALTERAR_ESPACO_FISICO = new Comando(70002, prefix + "infra_estrutura_fisica.negocio.ProcessadorEspacoFisico");
	public static final Comando REMOVER_ESPACO_FISICO = new Comando(70003, prefix + "infra_estrutura_fisica.negocio.ProcessadorEspacoFisico");
	
	public static final Comando SOLICITAR_RESERVA_ESPACO_FISICO = new Comando(70004, prefix + "infra_estrutura_fisica.negocio.ProcessadorSolicitaReservaEspacoFisico");
	public static final Comando AUTORIZAR_CANCELAR_RESERVA_RECURSO = new Comando(70005, prefix + "infraestrutura.negocio.ProcessadorAutorizaCancelaReservaRecurso");

	public static final Comando CANCELAR_MINHAS_RESERVA_PENDENTES_ESPACO_FISICO = new Comando(70006, prefix + "infra_estrutura_fisica.negocio.ProcessadorCancelaMinhasReservasPendentesEspacoFisico");
	public static final Comando CANCELAR_RESERVAS_AUTORIZADAS_ESPACO_FISICO = new Comando(70007, prefix + "infra_estrutura_fisica.negocio.ProcessadorCancelaReservasAutorizadasEspacoFisico");
	
	
	
	
	/**
     * Plano Individual do Docente - PID
     */
	public static final Comando CADASTRAR_PID = new Comando(80001, prefix + "pid.negocio.ProcessadorCargaHorariaPID");
	public static final Comando ALTERAR_PID = new Comando(80002, prefix + "pid.negocio.ProcessadorCargaHorariaPID");
	public static final Comando REMOVER_ATIV_ESPECIFICAS_DOCENTE = new Comando(80003, prefix + "pid.negocio.ProcessadorCargaHorariaPID");
	public static final Comando REMOVER_TURMAS_DOCENTE_PID = new Comando(80004, prefix + "pid.negocio.ProcessadorCargaHorariaPID");
	public static final Comando REMOVER_DESIGNACOES_DOCENTE_PID = new Comando(80005, prefix + "pid.negocio.ProcessadorCargaHorariaPID");
	public static final Comando REMOVER_ORIENTACOES_DOCENTE_PID = new Comando(80006, prefix + "pid.negocio.ProcessadorCargaHorariaPID");
	public static final Comando REMOVER_TURMAS_RESIDENCIA_MEDICA = new Comando(80007, prefix + "pid.negocio.ProcessadorCargaHorariaPID");
	public static final Comando REMOVER_PROJETOS_PID = new Comando(80008, prefix + "pid.negocio.ProcessadorCargaHorariaPID");
	
	/**
     * Necessidades Educacionais Especiais - NEE
     */
	public static final Comando CADASTRAR_SOLICITACAO_NEE = new Comando(80300, prefix + "nee.negocio.ProcessadorSolicitacaoApoioNee");
	public static final Comando ALTERAR_SOLICITACAO_NEE = new Comando(80301, prefix + "nee.negocio.ProcessadorSolicitacaoApoioNee");
	public static final Comando CADASTRAR_PARECER_SOLICITACAO_NEE = new Comando(80302, prefix + "nee.negocio.ProcessadorSolicitacaoApoioNee");
	public static final Comando ALTERAR_PARECER_SOLICITACAO_NEE = new Comando(80303, prefix + "nee.negocio.ProcessadorSolicitacaoApoioNee");
	public static final Comando CADASTRAR_NEE = new Comando(80304, prefix + "negocio.ProcessadorPessoaNecessidadeEspecial");
	public static final Comando CADASTRAR_SOLICITACAO_PORTAL_DISCENTE_NEE = new Comando(80305, prefix + "nee.negocio.ProcessadorSolicitacaoApoioNee");
	public static final Comando ANALISAR_SOLICITACAO_NEE_POR_DISCENTE = new Comando(80306, prefix + "nee.negocio.ProcessadorSolicitacaoApoioNee");
	
	
	/**
	 * COMANDOS DA RESIDÊNCIA MÉDICA
	 */
	public static final Comando CADASTRAR_DISCENTE_RESIDENTE = new Comando(80500, prefix + "ensino.negocio.ProcessadorDiscente");
	public static final Comando ALTERAR_DISCENTE_RESIDENCIA = new Comando(80501, prefix + "ensino.negocio.ProcessadorDiscente");
	public static final Comando CADASTRAR_RESIDENCIA_MEDICA = new Comando(80502, prefix + "prodocente.negocio.ProcessadorProgramaResidenciaMedica");
	public static final Comando ALTERAR_RESIDENCIA_MEDICA = new Comando(80503, prefix + "prodocente.negocio.ProcessadorProgramaResidenciaMedica");
	public static final Comando REMOVER_RESIDENCIA_MEDICA = new Comando(80504, prefix + "prodocente.negocio.ProcessadorProgramaResidenciaMedica");
	public static final Comando MATRICULAR_ALUNOS_RESIDENCIA_EM_LOTE = new Comando(80505, prefix + "complexohospitalar.negocio.ProcessadorMatriculaResidenciaMedica");
	
	/**
     * COMANDOS DE GESTÃO DE ESTÁGIOS
     */
	public static final Comando CADASTRAR_CONVENIO_ESTAGIO = new Comando(90001, prefix + "estagio.negocio.ProcessadorConvenioEstagio");
	public static final Comando ANALISAR_CONVENIO_ESTAGIO = new Comando(90002, prefix + "estagio.negocio.ProcessadorConvenioEstagio");
	public static final Comando CADASTRAR_ESTAGIO_AVULSO = new Comando(90003, prefix + "estagio.negocio.ProcessadorEstagio");
	public static final Comando ALTERAR_ESTAGIO = new Comando(90004, prefix + "estagio.negocio.ProcessadorEstagio");
	public static final Comando ANALISAR_ESTAGIO = new Comando(90005, prefix + "estagio.negocio.ProcessadorEstagio");
	public static final Comando CADASTRAR_INTERESSE_OFERTA_ESTAGIO = new Comando(90006, prefix + "estagio.negocio.ProcessadorInteresseOfertaEstagio");
	public static final Comando CANCELAR_INTERESSE_OFERTA_ESTAGIO = new Comando(90007, prefix + "estagio.negocio.ProcessadorInteresseOfertaEstagio");
	public static final Comando SELECIONAR_INTERESSADO = new Comando(90008, prefix + "estagio.negocio.ProcessadorInteresseOfertaEstagio");
	public static final Comando CADASTRAR_RELATORIO_ESTAGIO = new Comando(90010, prefix + "estagio.negocio.ProcessadorRelatorioEstagio");
	public static final Comando RENOVAR_ESTAGIO = new Comando(90011, prefix + "estagio.negocio.ProcessadorRenovacaoEstagio");
	public static final Comando APROVAR_RELATORIO_ESTAGIO = new Comando(90012, prefix + "estagio.negocio.ProcessadorRelatorioEstagio");
	public static final Comando SOLICITAR_CANCELAMENTO_ESTAGIO = new Comando(90013, prefix + "estagio.negocio.ProcessadorEstagio");
	public static final Comando CANCELAR_ESTAGIO = new Comando(90014, prefix + "estagio.negocio.ProcessadorEstagio");
	public static final Comando ATUALIZAR_CONVENIO_ESTAGIO = new Comando(90015, prefix + "estagio.negocio.ProcessadorConvenioEstagio");
	
	
	/** PORTAL DA AVALIAÇÃO INSTITUCIONAL */
	public static final Comando CALCULAR_RESULTADO_AVALIACAO_INSTITUCIONAL = new Comando(11501, prefix + "avaliacao.negocio.ProcessadorResultadoAvaliacaoInstitucional");
	public static final Comando REMOVER_RESULTADO_AVALIACAO_INSTITUCIONAL = new Comando(11502, prefix + "avaliacao.negocio.ProcessadorResultadoAvaliacaoInstitucional");
	public static final Comando DETERMINA_AVALIACOES_INVALIDAS = new Comando(11503, prefix + "avaliacao.negocio.ProcessadorResultadoAvaliacaoInstitucional");
	public static final Comando GRAVAR_OBSERVACOES_DOCENTE_TURMA_MODERADA = new Comando(11504, prefix + "avaliacao.negocio.ProcessadorModeracaoComentarios");
	public static final Comando FINALIZAR_OBSERVACOES_DOCENTE_TURMA_MODERADA = new Comando(11505, prefix + "avaliacao.negocio.ProcessadorModeracaoComentarios");
	public static final Comando GRAVAR_OBSERVACOES_TRANCAMENTO_MODERADA = new Comando(11506, prefix + "avaliacao.negocio.ProcessadorModeracaoComentarios");
	public static final Comando FINALIZAR_OBSERVACOES_TRANCAMENTO_MODERADA = new Comando(11507, prefix + "avaliacao.negocio.ProcessadorModeracaoComentarios");
	public static final Comando LIBERAR_CONSULTA_AO_RESULTADO_DA_AVALIACAO = new Comando(11508, prefix + "avaliacao.negocio.ProcessadorModeracaoComentarios");
	public static final Comando ATUALIZAR_ESTATISTICAS_AVALIACAO_INSTITUCIONAL = new Comando(11509, prefix + "avaliacao.negocio.ProcessadorEstatisticaAvaliacaoInstitucional");
	
	/** PROGRAMA DE ATUALIZAÇÃO PEDAGÓGICA */
	public static final Comando CADASTRAR_GRUPO_ATIVIDADE_ATUALIZACAO_PEDAGOGICA = new Comando(11601, prefix + "apedagogica.negocio.ProcessadorGrupoAtividadeAP");
	public static final Comando REMOVER_GRUPO_ATIVIDADE_ATUALIZACAO_PEDAGOGICA = new Comando(11602, prefix + "apedagogica.negocio.ProcessadorGrupoAtividadeAP");
	public static final Comando INSCREVER_ATIVIDADE_ATUALIZACAO_PEDAGOGICA = new Comando(11603, prefix + "apedagogica.negocio.ProcessadorInscricaoAtividadeAP");
	public static final Comando ALTERAR_SITUACAO_PARTICIPANTE_ATUALIZACAO_PEDAGOGICA = new Comando(11604, prefix + "apedagogica.negocio.ProcessadorInscricaoAtividadeAP");
	public static final Comando NOTIFICAR_PARTICIPANTES = new Comando(11605, prefix + "apedagogica.negocio.ProcessadorNotificarParticipanteAP");
	public static final Comando REMOVER_ATIVIDADE_ATUALIZACAO_PEDAGOGICA = new Comando(11606, prefix + "apedagogica.negocio.ProcessadorInscricaoAtividadeAP");

	/**
	 * COMANDOS DO MÓDULO DE OUVIDORIA
	 */
	public static final Comando CADASTRAR_MANIFESTACAO_DISCENTE = new Comando(11701, prefix + "ouvidoria.negocio.ProcessadorManifestacaoDiscente");
	public static final Comando CADASTRAR_MANIFESTACAO_DOCENTE = new Comando(11702, prefix + "ouvidoria.negocio.ProcessadorManifestacaoDocente");
	public static final Comando CADASTRAR_MANIFESTACAO_SERVIDOR = new Comando(11703, prefix + "ouvidoria.negocio.ProcessadorManifestacaoServidor");
	public static final Comando CADASTRAR_MANIFESTACAO_TECNICO_ADMINISTRATIVO = new Comando(11704, prefix + "ouvidoria.negocio.ProcessadorManifestacaoTecnicoAdministrativo");
	public static final Comando CADASTRAR_MANIFESTACAO_COMUNIDADE_EXTERNA = new Comando(11705, prefix + "ouvidoria.negocio.ProcessadorManifestacaoComunidadeExterna");
	
	public static final Comando EDITAR_MANIFESTACAO = new Comando(11706, prefix + "ouvidoria.negocio.ProcessadorManifestacaoOuvidoria");
	public static final Comando ALTERAR_PRAZO_RESPOSTA_MANIFESTACAO = new Comando(11707, prefix + "ouvidoria.negocio.ProcessadorHistoricoManifestacaoOuvidoria");
	
	public static final Comando ENVIAR_RESPOSTA_USUARIO = new Comando(11708, prefix + "ouvidoria.negocio.ProcessadorHistoricoManifestacaoOuvidoria");
	public static final Comando ENCAMINHAR_MANIFESTACAO_UNIDADE = new Comando(11709, prefix + "ouvidoria.negocio.ProcessadorHistoricoManifestacaoOuvidoria");
	public static final Comando ENVIAR_RESPOSTA_OUVIDORIA = new Comando(11710, prefix + "ouvidoria.negocio.ProcessadorHistoricoManifestacaoResponsavel");
	public static final Comando ENVIAR_RESPOSTA_UNIDADE = new Comando(11711, prefix + "ouvidoria.negocio.ProcessadorHistoricoManifestacaoResponsavel");
	public static final Comando DESIGNAR_PESSOA_MANIFESTACAO = new Comando(11712, prefix + "ouvidoria.negocio.ProcessadorDelegacaoUsuarioResposta");
	public static final Comando DESIGNAR_OUTRA_PESSOA_MANIFESTACAO = new Comando(11713, prefix + "ouvidoria.negocio.ProcessadorDelegacaoUsuarioResposta");
	public static final Comando ENVIAR_COPIA_MANIFESTACAO = new Comando(11714, prefix + "ouvidoria.negocio.ProcessadorCopiaManifestacao");
	public static final Comando FINALIZAR_MANIFESTACAO = new Comando(11715, prefix + "ouvidoria.negocio.ProcessadorHistoricoManifestacaoOuvidoria");
	public static final Comando SOLICITAR_ESCLARECIMENTO = new Comando(11716, prefix + "ouvidoria.negocio.ProcessadorSolicitacaoEsclarecimentoOuvidoria");
	public static final Comando RESPONDER_ESCLARECIMENTO = new Comando(11717, prefix + "ouvidoria.negocio.ProcessadorSolicitacaoEsclarecimentoOuvidoria");
	public static final Comando CONFIRMAR_CODIGO_ACESSO_OUVIDORIA = new Comando(11718, prefix + "ouvidoria.negocio.ProcessadorSolicitacaoEsclarecimentoOuvidoria");

	
	/** COMANDOS DO MÓDULO DE ENSINO MÉDIO */
	public static final Comando CADASTRAR_TURMA_SERIE = new Comando(10301, prefix + "ensino.medio.negocio.ProcessadorTurmaSerie");
	public static final Comando ALTERAR_TURMA_SERIE = new Comando(10302, prefix + "ensino.medio.negocio.ProcessadorTurmaSerie");
	public static final Comando CADASTRAR_TURMA_SERIE_DEPENDENCIA = new Comando(10329, prefix + "ensino.medio.negocio.ProcessadorTurmaDependencia");
	public static final Comando ALTERAR_TURMA_SERIE_DEPENDENCIA = new Comando(10330, prefix + "ensino.medio.negocio.ProcessadorTurmaDependencia");
	public static final Comando CADASTAR_DISCIPLINA_MEDIO = new Comando(10303, prefix + "ensino.medio.negocio.ProcessadorDisciplinaMedio");
	public static final Comando ALTERAR_DISCIPLINA_MEDIO = new Comando(10304, prefix + "ensino.medio.negocio.ProcessadorDisciplinaMedio");
	public static final Comando REMOVER_DISCIPLINA_MEDIO = new Comando(10305, prefix + "ensino.medio.negocio.ProcessadorDisciplinaMedio");
	public static final Comando AFASTAR_DISCENTE_MEDIO = new Comando(10306, prefix + "ensino.medio.negocio.ProcessadorAfastamentoDiscenteMedio");
	public static final Comando ESTORNAR_AFASTAMENTO_DISCENTE_MEDIO = new Comando(10307, prefix + "ensino.medio.negocio.ProcessadorAfastamentoDiscenteMedio");
	public static final Comando MATRICULAR_DISCENTE_MEDIO = new Comando(103060, prefix + "ensino.medio.negocio.ProcessadorMatriculaMedio");
	public static final Comando REMATRICULAR_DISCENTE_MEDIO = new Comando(103070, prefix + "ensino.medio.negocio.ProcessadorMatriculaMedio");
	public static final Comando IMPLANTAR_HISTORICO_MEDIO = new Comando(10308, prefix + "ensino.medio.negocio.ProcessadorImplantarHistoricoMedio");
	public static final Comando ALTERAR_STATUS_MATRICULA_MEDIO = new Comando(10309, prefix + "ensino.medio.negocio.ProcessadorAlteracaoStatusMatriculaMedio");
	public static final Comando MATRICULAR_DEPENDENCIA_MEDIO = new Comando(10310, prefix + "ensino.medio.negocio.ProcessadorMatriculaMedio");
	public static final Comando TRANSFERENCIA_AUTOMATICA_MEDIO = new Comando(10311, prefix + "ensino.medio.negocio.ProcessadorTransferenciaTurmasMedio");
	public static final Comando TRANSFERENCIA_MANUAL_MEDIO = new Comando(10312, prefix + "ensino.medio.negocio.ProcessadorTransferenciaTurmasMedio");
	public static final Comando CADASTRAR_OBSERVACAO_DISCENTE_SERIE = new Comando(10313, prefix + "ensino.medio.negocio.ProcessadorObservacaoDiscenteSerie");
	public static final Comando REMOVER_OBSERVACAO_DISCENTE_SERIE = new Comando(10314, prefix + "ensino.medio.negocio.ProcessadorObservacaoDiscenteSerie");
	public static final Comando CONSOLIDAR_DISCIPLINA_MEDIO = new Comando(10315, prefix + "ensino.medio.negocio.ProcessadorConsolidacaoDisciplina");
	public static final Comando INATIVAR_ATIVAR_CURRICULO_MEDIO = new Comando(10316, prefix + "ensino.medio.negocio.ProcessadorCurriculoMedio");
	public static final Comando CADASTRAR_CURRICULO_MEDIO = new Comando(10317, prefix + "ensino.medio.negocio.ProcessadorCurriculoMedio");
	public static final Comando ALTERAR_CURRICULO_MEDIO = new Comando(10318, prefix + "ensino.medio.negocio.ProcessadorCurriculoMedio");
	public static final Comando REMOVER_CURRICULO_MEDIO = new Comando(10319, prefix + "ensino.medio.negocio.ProcessadorCurriculoMedio");
	public static final Comando SALVAR_CONSOLIDACAO_DISCIPLINA_MEDIO = new Comando(10320, prefix + "ensino.medio.negocio.ProcessadorConsolidacaoDisciplina");
	public static final Comando REMOVER_TURMA_SERIE = new Comando(10321, prefix + "ensino.medio.negocio.ProcessadorTurmaSerie");
	public static final Comando CADASTRAR_CALENDARIO_MEDIO = new Comando(10322, prefix + "ensino.medio.negocio.ProcessadorCalendarioMedio");
	public static final Comando ALTERAR_CALENDARIO_MEDIO = new Comando(10323, prefix + "ensino.medio.negocio.ProcessadorCalendarioMedio");
	public static final Comando CADASTRAR_TOPICO_FORUM_MEDIO = new Comando(10324, prefix + "ensino.medio.negocio.ProcessadorMensagemForumMedio");
	public static final Comando CONSOLIDAR_INDIVIDUAL_MEDIO = new Comando(10325, prefix + "ensino.medio.negocio.ProcessadorConsolidacaoIndividualMedio");
	public static final Comando ALTERACAO_STATUS_DISCIPLINA = new Comando(10326, prefix + "ensino.medio.negocio.ProcessadorAlteracaoStatusMatriculaMedio");
	public static final Comando CONSOLIDAR_MATRICULA_DISCENTE_SERIE = new Comando(10327, prefix + "ensino.medio.negocio.ProcessadorConsolidacaoDiscenteSerie");
	public static final Comando ALTERAR_STATUS_SERIE_DEPENDENCIA = new Comando(10328, prefix + "ensino.medio.negocio.ProcessadorAlteracaoStatusMatriculaMedio");
	
	
	/** comandos relacionados ao registro de diplomas */
	public static final Comando REGISTRO_DIPLOMA_COLETIVO = new Comando(22001, prefix + "diploma.negocio.ProcessadorRegistroDiplomaColetivo");
	public static final Comando REGISTRO_DIPLOMA_INDIVIDUAL = new Comando(22002, prefix + "diploma.negocio.ProcessadorRegistroDiplomaIndividual");
	public static final Comando REQUISITAR_NUMERO_REGISTRO_DIPLOMA = new Comando(22003, prefix + "diploma.negocio.ProcessadorRequisicaoNumeroRegistro");
	public static final Comando RESPONSAVEIS_ASSINATURA_DIPLOMAS = new Comando(22004, prefix + "diploma.negocio.ProcessadorAssinaturaDiploma");
	public static final Comando ALTERAR_REGISTRO_DIPLOMA = new Comando(22005, prefix + "diploma.negocio.ProcessadorRegistroDiplomaIndividual");
	
	/** COMANDOS DO PORTAL DO FAMILIAR */
	public static final Comando CADASTRAR_NOVO_VINCULO = new Comando(28001, prefix + "portal.negocio.ProcessadorVinculoFamiliar");

	/** COMANDOS PARA MÓDULO DE TRADUÇÃO DE DOCUMENTOS */
	public static final Comando TRADUZIR_COMPONENTE_CURRICULAR = new Comando(29001, prefix + "ensino.internacionalizacao.negocio.ProcessadorTraducaoElemento");
	public static final Comando TRADUZIR_ELEMENTO = new Comando(29002, prefix + "ensino.internacionalizacao.negocio.ProcessadorTraducaoElemento");
	public static final Comando CADASTRAR_CONSTANTE_TRADUCAO = new Comando(29003, prefix + "ensino.internacionalizacao.negocio.ProcessadorConstanteTraducao");
	public static final Comando ATUALIZAR_CONSTANTE_TRADUCAO = new Comando(29004, prefix + "ensino.internacionalizacao.negocio.ProcessadorConstanteTraducao");
	
	/** COMANDOS DO MÓDULO DE ENSINO EM REDE */
	public static final Comando DADOS_ENSINO_REDE = new Comando(21001, prefix + "ensino_rede.negocio.ProcessadorDadosEnsinoRede");
	public static final Comando ALTERAR_SITUACAO_MATRICULAS_REDE = new Comando(21002, prefix + "ensino_rede.negocio.ProcessadorAlterarSituacaoMatriculaRede");
	public static final Comando CADASTRO_DOCENTE_REDE = new Comando(21003, prefix + "ensino_rede.academico.negocio.ProcessadorDocenteRede");

	
	/** Retorna um comando dado o código. */
	public static Comando getComando(int codigo) {
		Field[] campos = SigaaListaComando.class.getDeclaredFields();
		for (Field f : campos) {
			Object comandoARetornar;
			try {
				comandoARetornar = f.get(null);
			} catch (Exception e) {
				return null;
			}
			if ( comandoARetornar instanceof Comando ) {
				if ( ((Comando) comandoARetornar).getId() == codigo ) {
					return (Comando) comandoARetornar;
				}
			}
		}
		return null;
	}
}

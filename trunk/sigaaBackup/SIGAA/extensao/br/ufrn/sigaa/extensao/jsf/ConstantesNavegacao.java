/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/12/2009
 *
 */
package br.ufrn.sigaa.extensao.jsf; 

import java.util.HashMap;
import java.util.Map;

/*******************************************************************************
 * Classe utilizada para as constantes de navegação do sistema de extensão
 * 
 * @author Gleydson
 * @author Victor Hugo
 * 
 ******************************************************************************/
public class ConstantesNavegacao {

	/** ATIVIDADE DE EXTENSÃO */
	/** Visualiza dados da ação de extensão */
	public static final String ATIVIDADE_VIEW						= "/extensao/Atividade/view.jsp";
	/** Visualiza dados da ação de extensão para impressão */
	public static final String ATIVIDADE_PRINT						= "/extensao/Atividade/print.jsp";
	/** Visualiza dados da ação de extensão na área publica do sistama. */
	public static final String ATIVIDADE_VIEW_PUBLIC				= "/public/extensao/view.jsp";
	/** Visualiza dados da sub-atividade na área publica do sistema. */
	public static final String SUB_ATIVIDADE_VIEW_PUBLIC				= "/public/extensao/view_sub_atividade.jsp";
	/** Visualiza dados do orçamento da ação de extensão */
	public static final String ATIVIDADE_ORCAMENTO_APROVADO_VIEW	= "/extensao/Atividade/orcamento_aprovado.jsp";

	/** Página que permite ao docente selecionar quais ações estão vinculadas a ação que está sendo cadastrada. */
	public static final String SELECIONA_ATIVIDADE					= "/extensao/Atividade/seleciona_atividade.jsp";
	/** Página que permite ao docente informar os dados gerais da ação que está sendo cadastrada. */
	public static final String DADOS_GERAIS							= "/extensao/Atividade/dados_gerais.jsp";
	/** Página que permite ao docente informar os membros da equipe da ação que está sendo cadastrada. */
	public static final String MEMBROS_EQUIPE						= "/extensao/Atividade/membros_equipe.jsp";
	/** Página que permite ao docente informar o orçamento que será utilizado na ação que está sendo cadastrada. */
	public static final String ORCAMENTO_DETALHADO					= "/extensao/Atividade/orcamento.jsp";
	/** Página que permite ao docente informar a forma como o orçamento será/foi captado para ação que está sendo cadastrada. */	
	public static final String ORCAMENTO_CONSOLIDADO				= "/extensao/Atividade/orcamento_consolidado.jsp";
	/** Página que permite ao docente anexar arquivos a ação que está sendo cadastrada. */	
	public static final String ANEXAR_ARQUIVOS 						= "/extensao/Atividade/anexar_arquivos.jsp";
	/** Página que permite ao docente anexar fotos a ação que está sendo cadastrada. */
	public static final String ANEXAR_FOTOS 						= "/extensao/Atividade/anexar_fotos.jsp";
	/** Página que exibe um resumo da ação que está sendo cadastrada. */
	public static final String RESUMO								= "/extensao/Atividade/resumo.jsp";
	/** Página que permite ao remover uma ação de extensão. */
	public static final String REMOVER								= "/extensao/Atividade/remover.jsp";
	/** Página que permite ao docente reativar a ação que foi inativada por cadastro expirado. */
	public static final String REATIVAR								= "/extensao/AlterarAtividade/form_reativar_acao.jsp";
	/** Página que permite ao docente confirmar a execução da ação que foi aprovada. */
	public static final String CONFIRMAR_EXECUCAO					= "/extensao/Atividade/confirmar_execucao.jsp";
	/** Página que permite ao docente seguir com o cadastro da ação junto a proplan para registro de convênio. */
	public static final String CONTINUAR_PROPLAM					= "/projetos/ProjetoBase/continuar_proplan.jsp";
	
	/** Página que permite ao docente informar dados específicos da ação projeto que está sendo cadastrada. */
	public static final String PROJETO						= "/extensao/Atividade/projeto.jsp";
	/** Página que permite ao docente informar os objetivos a ação que está sendo cadastrada. */
	public static final String OBJETIVOS_ESPERADOS 			= "/extensao/Atividade/objetivos_esperados.jsp";
	/** Página que permite ao docente informar os objetivos a ação que está sendo cadastrada. */
	public static final String OBJETIVOS_ESPERADOS_EXTENSAO	= "/extensao/Atividade/objetivos_esperados_extensao.jsp";
	/** Página que permite ao docente o cadastro do objetivo e atividades do projeto */
	public static final String CADASTRO_OBJETIVO            = "/extensao/Atividade/include/cadastrar_objetivo.jsp";
	/** Página que permite ao docente informar dados específicos da ação programa que está sendo cadastrada. */
	public static final String PROGRAMA						= "/extensao/Atividade/programa.jsp";
	/** Página que permite ao docente informar quais as atividades realizadas na ação que está sendo cadastrada. */
	public static final String ATIVIDADES 					= "/extensao/Atividade/atividades.jsp";
	/** Página que permite ao docente informar dados específicos da ação curso ou evento que está sendo cadastrada. */
	public static final String CURSO_EVENTO					= "/extensao/Atividade/curso_evento.jsp";
	/** Página que permite ao docente informar dados específicos da ação curso ou evento que está sendo cadastrada. */
	public static final String SUB_ATIVIDADES					= "/extensao/Atividade/sub_atividades.jsp";
	/** Página que permite ao docente informar dados específicos da ação produto que está sendo cadastrada. */
	public static final String PRODUTO						= "/extensao/Atividade/produto.jsp";
	/** Página que permite ao docente visualizar dados do participante da ação. */
	public static final String VIEW_PARTICIPANTES						= "/extensao/Atividade/view_participantes.jsp";

	/** Sequência de passos necessários para o cadastro de uma ação de extensão. Utilizado na execução do wizard do cadastro. */
	public static final Map<String, String> PASSOS_ATIVIDADES_EXTENSAO = new HashMap<String, String>() ;
	static {
		PASSOS_ATIVIDADES_EXTENSAO.put(SELECIONA_ATIVIDADE, "Seleção do tipo da ação de extensão");
		PASSOS_ATIVIDADES_EXTENSAO.put(DADOS_GERAIS, "Dados gerais da ação");
		PASSOS_ATIVIDADES_EXTENSAO.put(MEMBROS_EQUIPE, "Membros da equipe da ação");
		PASSOS_ATIVIDADES_EXTENSAO.put(ORCAMENTO_DETALHADO, "Orçamento detalhado");
		PASSOS_ATIVIDADES_EXTENSAO.put(ORCAMENTO_CONSOLIDADO, "Orçamento consolidado");
		PASSOS_ATIVIDADES_EXTENSAO.put(ANEXAR_ARQUIVOS, "Anexar arquivos");
		PASSOS_ATIVIDADES_EXTENSAO.put(ANEXAR_FOTOS, "Anexar fotos");
		PASSOS_ATIVIDADES_EXTENSAO.put(RESUMO, "Resumo da ação");
		
		PASSOS_ATIVIDADES_EXTENSAO.put(PROJETO, "Dados do projeto");
		PASSOS_ATIVIDADES_EXTENSAO.put(OBJETIVOS_ESPERADOS, "Objetivos e resultados esperados");
		PASSOS_ATIVIDADES_EXTENSAO.put(OBJETIVOS_ESPERADOS_EXTENSAO, "Equipe Executora");

		PASSOS_ATIVIDADES_EXTENSAO.put(PROGRAMA, "Dados do programa");
		PASSOS_ATIVIDADES_EXTENSAO.put(ATIVIDADES, "Atividades vinculadas");

		PASSOS_ATIVIDADES_EXTENSAO.put(CURSO_EVENTO, "Dados do curso/evento");
		PASSOS_ATIVIDADES_EXTENSAO.put(SUB_ATIVIDADES, "Mini Atividades");

		PASSOS_ATIVIDADES_EXTENSAO.put(PRODUTO, "Dados do produto");
	}

	/** LISTAGEM DE ATIVIDADES DE EXTENSÃO */
	/** Lista de ações de extensão sem planos de trabalho. */
	public static final String LISTA_ATIVIDADES_SEM_PLANO					= "/extensao/Atividade/lista_atividades_sem_plano.jsp";
	/** Lista de ações de extensão. */
	public static final String LISTA 										= "/extensao/Atividade/lista.jsp";
	/** Lista de ações de extensão do usuário logado. */
	public static final String LISTA_MINHAS_ATIVIDADES						= "/extensao/Atividade/lista_minhas_atividades.jsp";
	/** Lista de ações de extensão do usuário logado pendentes de envio. */
	public static final String LISTA_ATIVIDADES_PENDENTES					= "/extensao/Atividade/lista_atividades_pendentes.jsp";
	/** Lista de ações de extensão. */
	public static final String LISTA_ATIVIDADES_PERIODO_CONCLUSAO			= "/extensao/Atividade/lista_atividades_periodo_conclusao.jsp";

	/** ALTERAR ATIVIDADE PROEX */
	/** Lista de ações de extensão que podem ser alteradas. */
	public static final String ALTERAR_ATIVIDADE_LISTA						= "/extensao/AlterarAtividade/lista.jsp";
	/** Fomulário que permite alteração da situação da ação de extensão. */
	public static final String ALTERAR_SITUACAO_ATIVIDADE_FORM				= "/extensao/AlterarAtividade/form_situacao_atividade.jsp";
	/** Formulário que permite a alteração da quantidade de bolsas da ação de extensão. */
	public static final String ALTERAR_BOLSAS_ATIVIDADE_FORM				= "/extensao/AlterarAtividade/form_bolsas.jsp";

	/** RENOVAÇÃO DE ATIVIDADE DE EXTENSÃO */
	/** Formulário para renovação da ação de extensão. */
	public static final String RENOVACAO_FORM	= "/extensao/Atividade/renovar.jsp";

	/** PRESTACAO DE SERVIÇO */
	/** Fluxo de cadastro de propostas de prestação de serviços - contratante. */	
	public static final String CONTRATANTE 										= "/extensao/PrestacaoServico/contratante.jsp";
	/** Fluxo de cadastro de propostas de prestação de serviços - equipe */
	public static final String EQUIPE 											= "/extensao/PrestacaoServico/equipe.jsp";
	/** Fluxo de cadastro de propostas de prestação de serviços - serviços */
	public static final String SERVICO 											= "/extensao/PrestacaoServico/servico.jsp";
	/** Fluxo de cadastro de propostas de prestação de serviços - descrição */
	public static final String DESCRICAO			 							= "/extensao/PrestacaoServico/descricao.jsp";
	/** Fluxo de cadastro de propostas de prestação de serviços - resumo */
	public static final String RESUMO_PS		 								= "/extensao/PrestacaoServico/resumo.jsp";

	
	/** RELATORIO BOLSISTA EXTENSÃO */
	/** Cadastro de relatório de bolsistas - formulário de cadastros. */
	public static final String RELATORIOBOLSISTAEXTENSAO_FORM					= "/extensao/RelatorioBolsistaExtensao/form.jsp";
	/** Cadastro de relatório de bolsistas - lista de relatórios. */
	public static final String RELATORIOBOLSISTAEXTENSAO_LISTA	 				= "/extensao/RelatorioBolsistaExtensao/lista.jsp";
	/** Cadastro de relatório de bolsistas - formulário de validação. */
	public static final String RELATORIOBOLSISTAEXTENSAO_AVALIAR_FORM			= "/extensao/RelatorioBolsistaExtensao/avaliar_relatorio_form.jsp";
	/** Cadastro de relatório de bolsistas - lista de relatórios para validação. */
	public static final String RELATORIOBOLSISTAEXTENSAO_AVALIAR_LISTA			= "/extensao/RelatorioBolsistaExtensao/avaliar_relatorio_lista.jsp";
	/** Cadastro de relatório de bolsistas - visualização do relatório. */
	public static final String RELATORIOBOLSISTAEXTENSAO_VIEW	 				= "/extensao/RelatorioBolsistaExtensao/view.jsp";	

	
	/** RELATORIO DE ACAO DE EXTENSÃO */
	/** Cadastro de relatório de ação de extensão - lista de relatórios */
	public static final String RELATORIOACAOEXTENSAO_LISTA						= "/extensao/RelatorioAcaoExtensao/lista.jsp";
	/** Cadastro de relatório de ação de extensão - lista de ações de extensão pendentes de envio de relatórios. */
	public static final String NOTIFICACAO_AUSENCIA_RELATORIO					= "/extensao/RelatorioAcaoExtensao/notificacao_ausencia_rel.jsp";
	
	/** RELATORIO PROJETOS EXTENSÃO */
	/** Cadastro de relatório de ação de extensão - relatório de projetos */
	public static final String RELATORIOPROJETO_FORM 							= "/extensao/RelatorioProjeto/form.jsp";
	/** Cadastro de relatório de ação de extensão - visualização do relatório */
	public static final String RELATORIOPROJETO_VIEW 							= "/extensao/RelatorioProjeto/view.jsp";
	/** Cadastro de relatório de ação de extensão - exclusão do relatório */
	public static final String RELATORIOPROJETO_DELETE							= "/extensao/RelatorioProjeto/delete.jsp";

	/** Novo Cadastro de relatório de ação de extensão - relatório de projetos */
	public static final String RELATORIO_EXTENSAO_FORM 							= "/extensao/RelatorioProjeto/form_relatorio.jsp";

	/** RELATORIO CURSOS EVENTOS */
	/** Cadastro de relatório de ação de extensão - relatório de cursos e eventos */
	public static final String RELATORIOCURSOSEVENTOS_FORM 						= "/extensao/RelatorioCursoEvento/form.jsp";
	/** Cadastro de relatório de ação de extensão - visualização do relatório de cursos e eventos */
	public static final String RELATORIOCURSOSEVENTOS_VIEW 						= "/extensao/RelatorioCursoEvento/view.jsp";
	/** Cadastro de relatório de ação de extensão - exclusão do relatório */
	public static final String RELATORIOCURSOSEVENTOS_DELETE				= "/extensao/RelatorioCursoEvento/delete.jsp";

	/** RELATORIO PROGRAMAS */
	/** Cadastro de relatório de ação de extensão - relatório de programa */
	public static final String RELATORIO_PROGRAMA_FORM 						= "/extensao/RelatorioPrograma/form.jsp";
	/** Cadastro de relatório de ação de extensão - visualização de relatório de programa */
	public static final String RELATORIO_PROGRAMA_VIEW 						= "/extensao/RelatorioPrograma/view.jsp";
	/** Cadastro de relatório de ação de extensão - exclusão do relatório */
	public static final String RELATORIO_PROGRAMA_DELETE				= "/extensao/RelatorioPrograma/delete.jsp";
	/** RELATORIO PRODUTO */
	/** Cadastro de relatório de ação de extensão - relatório de produtos */
	public static final String RELATORIO_PRODUTO_FORM 						= "/extensao/RelatorioProduto/form.jsp";
	/** Cadastro de relatório de ação de extensão - visualização de relatório de produtos */
	public static final String RELATORIO_PRODUTO_VIEW 						= "/extensao/RelatorioProduto/view.jsp";
	/** Cadastro de relatório de ação de extensão - exclusão do relatório */
	public static final String RELATORIO_PRODUTO_DELETE 						= "/extensao/RelatorioProduto/delete.jsp";
	

	
	
	/** RELATORIOS PARA IMPRESSÃO */
	/** Relatórios de ações de extensão - por edital */
	public static final String RELATORIO_ACOES_POR_EDITAL_FORM					= "/extensao/Relatorios/relatorio_por_edital_form.jsp";
	/** Relatórios de ações de extensão - busca de dados bancários dos discentes */
	public static final String RELATORIO_DADOS_BANCARIOS_DISCENTES_FORM			= "/extensao/Relatorios/dados_bancarios_discentes_form.jsp";
	/** Relatórios de ações de extensão - visualização dos dados bancários dos discentes */
	public static final String RELATORIO_DADOS_BANCARIOS_DISCENTES_REL			= "/extensao/Relatorios/dados_bancarios_discentes_rel.jsp";
	/** Relatórios de ações de extensão - por publico estimado - busca */
	public static final String RELATORIO_PUBLICO_ESTIMADO_FORM					= "/extensao/Relatorios/publico_estimado_form.jsp";
	/** Relatórios de ações de extensão - membros da equipe por modalidade */
	public static final String RELATORIO_EQUIPE_POR_MODALIDADE					= "/extensao/Relatorios/relatorio_equipe_por_modalidade.jsp";
	/** Relatórios de ações de extensão - discentes por modalidade */
	public static final String RELATORIO_DISCENTE_POR_MODALIDADE				= "/extensao/Relatorios/relatorio_discente_por_modalidade.jsp";
	/** Relatórios de ações de extensão - por tipos de financiamento */
	public static final String RELATORIO_FINANCIAMENTO_INTERNO_EXTERNO			= "/extensao/Relatorios/relatorio_financiamento_interno_externo.jsp";
	/** Relatórios de ações de extensão - por local de realização */
	public static final String RELATORIO_ACOES_LOCAL_REALIZACAO					= "/extensao/Relatorios/relatorio_acoes_local_realizacao.jsp";
	/** Relatórios de ações de extensão - com nomes das ações de extensão */
	public static final String RELATORIO_NOMINAL_ACOES_LOCAL_REALIZACAO			= "/extensao/Relatorios/busca_relatorio_nominal_acoes_localidade.jsp";
	/** Relatórios de ações de extensão - por público estimado */
	public static final String RELATORIO_PUBLICO_ESTIMADO_REL					= "/extensao/Relatorios/publico_estimado_rel.jsp";
	/** Relatórios de ações de extensão - por participantes - busca */
	public static final String RELATORIO_TOTAL_PARTICIPANTES_FORM				= "/extensao/Relatorios/relatorio_participantes_form.jsp";
	/** Relatórios de ações de extensão - por participantes - visualização */	
	public static final String RELATORIO_TOTAL_PARTICIPANTES_REL				= "/extensao/Relatorios/relatorio_participantes_rel.jsp";
	/** Relatórios de ações de extensão - descentralização de recursos - busca */
	public static final String RELATORIO_DESCENTRALIZACAO_RECURSOS_FAEX_FORM	= "/extensao/Relatorios/descentralizacao_recursos_faex_form.jsp";
	/** Relatórios de ações de extensão - descentralização de recursos - visualização */
	public static final String RELATORIO_DESCENTRALIZACAO_RECURSOS_FAEX_REL		= "/extensao/Relatorios/descentralizacao_recursos_faex_rel.jsp";
	/** Relatórios de ações de extensão - por localidade - busca */
	public static final String RELATORIO_ACOES_POR_LOCALIDADE					= "/extensao/Relatorios/relatorio_acoes_por_localidade.jsp";
	/** Relatórios de ações de extensão - por localidade - visualização */
	public static final String RELATORIO_NOMINAL_ACOES_POR_LOCALIDADE				= "/extensao/Relatorios/relatorio_nominal_acoes_por_localidade.jsp";
	/** Relatórios de ações de extensão - exibe o total de participantes - busca */
	public static final String RELATORIO_TOTAL_PARTICIPANTES_ACAO_EXTENSAO_FORM		= "/extensao/Relatorios/relatorio_total_participantes_acao_extensao_form.jsp";
	/** Relatórios de ações de extensão - exibe o total de participantes - visialização */
	public static final String RELATORIO_TOTAL_PARTICIPANTES_ACAO_EXTENSAO_REL		= "/extensao/Relatorios/relatorio_total_participantes_acao_extensao_rel.jsp";
	/** Relatórios de ações de extensão - por nível de escolaridade - busca */
	public static final String RELATORIO_DOCENTES_POR_NIVEL					= "/extensao/Relatorios/relatorio_docentes_por_nivel.jsp";
	/** Relatórios de ações de extensão - por nível de escolaridade - visualização */
	public static final String BUSCA_RELATORIO_DOCENTES_POR_NIVEL			= "/extensao/Relatorios/busca_relatorio_docentes_por_nivel.jsp";
	

	
	/** AUTORIZAÇÃO DOS DEPARTAMENTOS */
	/** Validação de ações de extensão pelo chefe do departamento. */
	public static final String AUTORIZACAOATIVIDADE_FORM 						= "/extensao/AutorizacaoDepartamento/form.jsp";
	/** Lista de ações para validação de ações de extensão pelo chefe do departamento. */
	public static final String AUTORIZACAOATIVIDADE_LISTA						= "/extensao/AutorizacaoDepartamento/lista.jsf";
	/** Busca de autorizações de ações de extensão pelo chefe do departamento. */
	public static final String AUTORIZACAOATIVIDADE_BUSCA						= "/extensao/AutorizacaoDepartamento/form_busca_autorizar_acoes.jsf";
	/** Busca de autorizações de ações de extensão pelo chefe do departamento realizada pelo gestor de extensão. */
	public static final String AUTORIZACAOATIVIDADE_MONITORIA_BUSCA				= "/monitoria/AutorizacaoDepartamento/form_busca_autorizacoes.jsf";
	/** Recibo da  autorizações de ações de extensão pelo chefe do departamento. */
	public static final String AUTORIZACAOATIVIDADE_RECIBO						= "/extensao/AutorizacaoDepartamento/recibo_autorizacao.jsp";

	/** Busca de autorizações de relatório de ações de extensão pelo chefe do departamento. */
	public static final String AUTORIZACAORELATORIO_BUSCA						= "/extensao/AutorizacaoDepartamento/form_busca_autorizar_relatorios.jsf";
	/** Formulário de autorizações de relatório de ações de extensão pelo chefe do departamento. */
	public static final String AUTORIZACAORELATORIO_FORM_PROJETO 				= "/extensao/AutorizacaoDepartamento/form_relatorio_projeto.jsp";
	/** Busca de autorizações de relatório de ações de extensão pelo chefe do departamento. */
	public static final String AUTORIZACAORELATORIO_FORM_CURSO_EVENTO			= "/extensao/AutorizacaoDepartamento/form_relatorio_curso_evento.jsp";
	/** Lista de autorizações de relatório de ações de extensão pelo chefe do departamento. */
	public static final String AUTORIZACAORELATORIO_LISTA						= "/extensao/AutorizacaoDepartamento/lista_relatorio.jsp";
	/** Formulário de autorizações de relatório de ações de extensão pelo chefe do departamento para produto. */
	public static final String AUTORIZACAO_RELATORIO_FORM_PRODUTO				= "/extensao/AutorizacaoDepartamento/form_relatorio_produto.jsp";
	/** Formulário de autorizações de relatório de ações de extensão pelo chefe do departamento para programa. */
	public static final String AUTORIZACAO_RELATORIO_FORM_PROGRAMA				= "/extensao/AutorizacaoDepartamento/form_relatorio_programa.jsp";
	
	/** VALIDAÇÃO DOS RELATÓRIOS DE EXTENSÃO PELA PROEX */
	public static final String VALIDACAORELATORIO_PROJETO_PROEX_FORM 			= "/extensao/ValidacaoRelatorioProex/form_projeto.jsp";
	public static final String VALIDACAORELATORIO_CURSO_EVENTO_PROEX_FORM 		= "/extensao/ValidacaoRelatorioProex/form_curso_evento.jsp";
	public static final String VALIDACAORELATORIO_PROEX_LISTA					= "/extensao/ValidacaoRelatorioProex/lista.jsp";
	public static final String VALIDACAO_RELATORIO_PRODUTO_PROEX_FORM 			= "/extensao/ValidacaoRelatorioProex/form_produto.jsp";	
	public static final String VALIDACAO_RELATORIO_PROGRAMA_PROEX_FORM 			= "/extensao/ValidacaoRelatorioProex/form_programa.jsp";


	
	/** CADASTRO DOS PARTICIPANTES DE AÇÕES DE EXTENSÃO */ 
	public static final String PARTICIPANTESEXTENSAO_FORM 						= "/extensao/ParticipanteAcaoExtensao/form.jsp";
	public static final String PARTICIPANTESEXTENSAO_LISTA						= "/extensao/ParticipanteAcaoExtensao/lista.jsp";
	public static final String PARTICIPANTES_SUB_EXTENSAO_LISTA					= "/extensao/ParticipanteAcaoExtensao/lista_sub.jsp";
	public static final String PARTICIPANTESEXTENSAO_LISTA_PRINT				= "/extensao/ParticipanteAcaoExtensao/lista_participantes_extensao_print.jsp";
	public static final String PARTICIPANTES_EXTENSAO_LISTA_PRESENCA_PRINT		= "/extensao/ParticipanteAcaoExtensao/lista_presenca_print.jsp";
	public static final String PARTICIPANTESEXTENSAO_VIEW						= "/extensao/ParticipanteAcaoExtensao/view.jsp";
	public static final String PARTICIPANTESEXTENSAO_ACOES_LISTA				= "/extensao/ParticipanteAcaoExtensao/acoes_lista.jsp";
	public static final String PARTICIPANTESEXTENSAO_NOTIFICA					= "/extensao/ParticipanteAcaoExtensao/notificacao_participantes.jsp";
	public static final String PARTICIPANTEEXTENSAO_CONTATO_PRINT				= "/extensao/ParticipanteAcaoExtensao/lista_contatos_print.jsp";
	
	
	/** PLANO DE TRABALHO DO BOLSISTA */
	public static final String PLANOTRABALHOBOLSISTA_FORM 					= "/extensao/PlanoTrabalho/form.jsp";
	public static final String PLANOTRABALHOBOLSISTA_LISTA					= "/extensao/PlanoTrabalho/lista.jsp";
	public static final String PLANOTRABALHOBOLSISTA_ATIVIDADE_LISTA			= "/extensao/PlanoTrabalho/atividades_lista.jsp";	
	public static final String PLANOTRABALHOBOLSISTA_CRONOGRAMA				= "/extensao/PlanoTrabalho/cronograma.jsp";
	public static final String PLANOTRABALHOBOLSISTA_RESUMO					= "/extensao/PlanoTrabalho/resumo.jsp";	
	public static final String PLANOTRABALHOBOLSISTA_PLANOS_COORDENADOR			= "/extensao/PlanoTrabalho/planos_coordenador.jsp";
	public static final String PLANOTRABALHOBOLSISTA_VIEW					= "/extensao/PlanoTrabalho/view.jsp";
	public static final String PLANOTRABALHOBOLSISTA_ALTERAR_PLANO_LISTA			= "/extensao/PlanoTrabalho/alterar_plano_lista.jsp";	
	public static final String PLANOTRABALHOBOLSISTA_FINALIZAR_DISCENTE			= "/extensao/PlanoTrabalho/finalizar_discente.jsp";
	public static final String PLANOTRABALHOBOLSISTA_INDICAR_DISCENTE			= "/extensao/PlanoTrabalho/indicar_discente.jsp";
	public static final String PLANOTRABALHOBOLSISTA_DISCENTE				= "/extensao/PlanoTrabalho/planos_discente.jsp";
	public static final String PLANOTRABALHOBOLSISTA_CONTATO				= "/extensao/PlanoTrabalho/form_contato.jsp";
	
	
	public static final String AVALIACAO_PROPOSTA_FORM 					= "/extensao/AvaliacaoAtividade/form.jsp";
	public static final String AVALIACAO_PROPOSTA_LISTA					= "/extensao/AvaliacaoAtividade/lista.jsp";	
	public static final String AVALIACAO_PROPOSTA_VIEW 					= "/extensao/AvaliacaoAtividade/view.jsp";	
	public static final String AVALIACAO_PROPOSTA_LISTA_PARECER 				= "/extensao/AvaliacaoAtividade/lista_parecerista.jsp";	
	public static final String AVALIACAO_PROPOSTA_FORM_PARECER				= "/extensao/AvaliacaoAtividade/form_parecerista.jsp";
	public static final String AVALIACAO_PROPOSTA_CONFIRMAR_AVALIACAO	   		= "/extensao/AvaliacaoAtividade/confirmar_avaliacao.jsp";
	public static final String AVALIACAO_PROPOSTA_FORM_PRESIDENTE				= "/extensao/AvaliacaoAtividade/form_presidente.jsp";
	public static final String AVALIACAO_PROPOSTA_LISTA_PRESIDENTE				= "/extensao/AvaliacaoAtividade/lista_presidente.jsp";	
	public static final String AVALIACAO_CONSULTAR_AVALIADORES				= "/extensao/AvaliacaoAtividade/consultar_avaliadores.jsp";
	public static final String AVALIACAO_CONSULTAR_AVALIACOES				= "/extensao/AvaliacaoAtividade/consultar_avaliacoes.jsp";
	public static final String AVALIACAO_LISTA_COORDENADOR					= "/extensao/AvaliacaoAtividade/lista_coordenador.jsp";
	public static final String AVALIACAO_LISTA_AD_HOC					= "/extensao/AvaliacaoAtividade/lista_avaliacoes_ad_hoc.jsp";
	public static final String INICIAR_HOMOLOGACAO_BOLSAS					= "/extensao/DiscenteExtensao/homologacao_bolsa_discente.jsp";
	
	public static final String EXPORTAR_LISTA_FAEX_AVALIACAO_FINAL				= "/arquivoFaex";
	
	
	public static final String CLASSIFICAR_ACOES_CLASSIFICAR				= "/extensao/ClassificarAcoes/classificar.jsp";
	public static final String CLASSIFICAR_ACOES_LISTA					= "/extensao/ClassificarAcoes/lista.jsp";
	public static final String CLASSIFICAR_ACOES_VIEW					= "/extensao/ClassificarAcoes/view.jsp";

	/** DISCENTE EXTENSÃO */
	public static final String DISCENTEEXTENSAO_FORM 							= "/extensao/DiscenteExtensao/form.jsp";
	public static final String DISCENTEEXTENSAO_LISTA 							= "/extensao/DiscenteExtensao/lista.jsp";
	public static final String DISCENTEEXTENSAO_INSCRICAO_DISCENTE 				= "/extensao/DiscenteExtensao/inscricao_discente.jsp";
	public static final String DISCENTEEXTENSAO_VIEW			 				= "/extensao/DiscenteExtensao/view.jsp";
	
	/** VER DETALHES DA SELEÇÃO */
	public static final String DETALHESSELECAOEXTENSAO_LISTA_PROJETOS 							= "/extensao/DetalhesSelecaoExtensao/lista_projetos.jsp";
	public static final String DETALHESSELECAOEXTENSAO_VISUALIZAR_CANDIDATOS 					= "/extensao/DetalhesSelecaoExtensao/visualizar_candidatos.jsp";	

	/** VISUALIZAR RESULTADO SELEÇÃO */
	public static final String DISCENTEEXTENSAO_VISUALIZAR_RESULTADO_SELECAO 					= "/extensao/DiscenteExtensao/visualizar_resultado.jsp";
	public static final String DISCENTEEXTENSAO_VISUALIZAR_RESULTADO_SELECAO_LISTA_PROJETOS 	= "/extensao/DiscenteExtensao/atividades_visualizar_resultado.jsp";
	
	/** DISTRIBUIR PARECERISTA ATIVIDADE EXTENSÃO */
	public static final String DISTRIBUICAOPARECERISTA_FORM 									= "/extensao/DistribuicaoPareceristaExtensao/form.jsp";
	public static final String DISTRIBUICAOPARECERISTA_LISTA 									= "/extensao/DistribuicaoPareceristaExtensao/lista.jsp";
	public static final String DISTRIBUICAOPARECERISTA_AUTO_FORM								= "/extensao/DistribuicaoPareceristaExtensao/form_auto.jsp";
	public static final String DISTRIBUICAOPARECERISTA_AUTO_LISTA								= "/extensao/DistribuicaoPareceristaExtensao/lista_auto.jsp";
	public static final String DISTRIBUICAOPARECERISTA_LISTA_ATIVIDADES_VINCULADAS				= "/extensao/DistribuicaoPareceristaExtensao/lista_atividades_vinculadas.jsp";	

	/** DISTRIBUIR COMITE ATIVIDADE EXTENSÃO */
	public static final String DISTRIBUICAOCOMITE_FORM 											= "/extensao/DistribuicaoComiteExtensao/form.jsp";
	public static final String DISTRIBUICAOCOMITE_LISTA 										= "/extensao/DistribuicaoComiteExtensao/lista.jsp";
	public static final String DISTRIBUICAOCOMITE_LISTA_ATIVIDADES_VINCULADAS					= "/extensao/DistribuicaoComiteExtensao/lista_atividades_vinculadas.jsp";

	/** SOLICITAR RECONSIDERACAO DE AVALIAÇÃO */
	public static final String SOLICITACAORECONSIDERACAO_FORM 								= "/extensao/SolicitacaoReconsideracao/form.jsp";
	public static final String SOLICITACAORECONSIDERACAO_LISTA 								= "/extensao/SolicitacaoReconsideracao/lista.jsp";
	public static final String SOLICITACAORECONSIDERACAO_MINHAS_SOLICITACOES						= "/extensao/SolicitacaoReconsideracao/minhas_solicitacoes.jsp";
	public static final String SOLICITACAORECONSIDERACAO_VIEW 								= "/extensao/SolicitacaoReconsideracao/view.jsp";
	
	public static final String ANALISARSOLICITACAORECONSIDERACAO_LISTA 							= "/extensao/AnalisarSolicitacaoReconsideracao/lista.jsp";
	public static final String ANALISARSOLICITACAORECONSIDERACAO_FORM 							= "/extensao/AnalisarSolicitacaoReconsideracao/form.jsp";
	

	public static final String FOTOS_FORM 														= "/extensao/Fotos/form.jsp";
	
	
	public static final String CONTATO_COORDENADOR_FORM											= "/extensao/Atividade/form_contato.jsp";
	
	/** CADASTRO GRUPO DE ITENS DE AVALIACAO */
	public static final String CADASTRARGRUPOITEMAVALIACAO_LISTA		                        = "/extensao/GrupoItemAvaliacao/lista.jsp";
	public static final String CADASTRARGRUPOITEMAVALIACAO_FORM		                        = "/extensao/GrupoItemAvaliacao/form.jsp";
	
	/** CADASTRAR ITEM AVALIAÇAO */
	public static final String CADASTRARITEMAVALIACAO_FORM				                = "/extensao/ItemAvaliacaoExtensao/form.jsp";
	public static final String CADASTRARITEMAVALIACAO_LISTA						= "/extensao/ItemAvaliacaoExtensao/lista.jsp";
	
	public static final String FINALIZACAO_BOLSISTA							= "/extensao/DiscenteExtensao/finalizacao_bolsa_discente.jsp";
	
	/** MOVIMENTAÇÃO DE COTAS DE BOLSA */
	public static final String MOVIMENTAR_COTA_FORM							= "/extensao/MovimentacaoCota/form.jsp";
	public static final String MOVIMENTAR_COTA_LISTA						= "/extensao/MovimentacaoCota/lista.jsp";
	
	/** CONSTANTES RELACIONADAS A VÍNCULO DE UMA AÇÃO A UNIDADE ORÇAMENTÁRIA */
	public static final String VINCULAR_UNIDADE_ORCAMENTARIA_FORM	= "/extensao/VincularUnidadeOrcamentaria/form.jsp";
	public static final String VINCULAR_UNIDADE_ORCAMENTARIA_LISTA	= "/extensao/VincularUnidadeOrcamentaria/lista.jsp";
	
	/** CONSTANTES RELACIONADAS AO PROCEDIMENTO DE ALTERAÇÃO DE VAGAS DO CURSO PELO GESTOR DE EXTENSÃO*/
	public static final String ALTERAR_VAGAS_CURSO_EVENTO_FORM	= "/extensao/AlterarVagas/form.jsp";
	public static final String ALTERAR_VAGAS_CURSO_EVENTO_LISTA	= "/extensao/AlterarVagas/lista.jsp";

}

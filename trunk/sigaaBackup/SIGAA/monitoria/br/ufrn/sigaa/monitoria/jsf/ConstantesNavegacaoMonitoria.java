package br.ufrn.sigaa.monitoria.jsf;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe utilizada para as constantes de navegação do sistema de monitoria
 *
 * @author Victor Hugo
 * @author ilueny santos
 *
 */
public class ConstantesNavegacaoMonitoria {


	/** CADASTRO DO PROJETO DE MONITORIA */
	public static final String CADASTROPROJETO_MEUS_PROJETOS 		 = "/monitoria/ProjetoMonitoria/meus_projetos.jsp";
	public static final String CADASTROPROJETO_MEUS_PROJETOS_PAMQEG	 = "/monitoria/ProjetoMonitoria/meus_projetos_pamqeg.jsp";
	public static final String CADASTROPROJETO_DIMENSAO_PROJETO	 	 = "/monitoria/ProjetoMonitoria/dimensao_projeto.jsp";
	public static final String CADASTROPROJETO_FORM	 				 = "/monitoria/ProjetoMonitoria/form.jsp";
	public static final String CADASTROPROJETO_FORM_EXTERNO			 = "/monitoria/ProjetoMonitoria/form_externo.jsp";
	public static final String CADASTROPROJETO_COMPONENTE_CURRICULAR = "/monitoria/ProjetoMonitoria/componente_curricular.jsp";
	public static final String CADASTROPROJETO_SELECAO_DOCENTE 		 = "/monitoria/ProjetoMonitoria/selecao_docente.jsp";
	public static final String CADASTROPROJETO_SELECAO_COORDENADOR	 = "/monitoria/ProjetoMonitoria/selecao_coordenador.jsp";
	public static final String CADASTROPROJETO_ORCAMENTO			 = "/monitoria/ProjetoMonitoria/orcamento.jsp";
	public static final String CADASTROPROJETO_ANEXAR_ARQUIVOS		 = "/monitoria/ProjetoMonitoria/anexar_arquivos.jsp";
	public static final String CADASTROPROJETO_ANEXAR_FOTOS			 = "/monitoria/ProjetoMonitoria/anexar_fotos.jsp";
	public static final String CADASTROPROJETO_RESUMO 				 = "/monitoria/ProjetoMonitoria/resumo.jsp";
	public static final String CADASTROPROJETO_VISUALIZAR			 = "/monitoria/ProjetoMonitoria/view.jsp";
	public static final String CADASTROPROJETO_AVISO_ENVIO			 = "/monitoria/ProjetoMonitoria/aviso_envio.jsp";
	/** ALTERAÇÃO DO COORDENADOR DO PROJETO PELA PROGRAD **/
	public static final String ALTERAR_COORDENADOR_PROJETO			 = "/monitoria/SelecaoCoordenador/selecao_coordenador.jsp";

	public static Map<String, String> PASSOS_CADASTRO_PROJETO_MONITORIA = new HashMap<String, String>() ;
	static {
		PASSOS_CADASTRO_PROJETO_MONITORIA.put(CADASTROPROJETO_DIMENSAO_PROJETO, "Dimensão do Projeto");
		PASSOS_CADASTRO_PROJETO_MONITORIA.put(CADASTROPROJETO_FORM, "Dados Gerais do Projeto");
		PASSOS_CADASTRO_PROJETO_MONITORIA.put(CADASTROPROJETO_COMPONENTE_CURRICULAR, "Componentes Curriculares");
		PASSOS_CADASTRO_PROJETO_MONITORIA.put(CADASTROPROJETO_SELECAO_DOCENTE, "Selecionar Docentes");
		PASSOS_CADASTRO_PROJETO_MONITORIA.put(CADASTROPROJETO_SELECAO_COORDENADOR, "Selecionar Coordenador");
		PASSOS_CADASTRO_PROJETO_MONITORIA.put(CADASTROPROJETO_ANEXAR_ARQUIVOS, "Anexar Arquivos");
		PASSOS_CADASTRO_PROJETO_MONITORIA.put(CADASTROPROJETO_RESUMO, "Resumo do Projeto");
	}
    
	public static Map<String, String> PASSOS_CADASTRO_PROJETO_MONITORIA_EXTERNO = new HashMap<String, String>() ;
	static {
		PASSOS_CADASTRO_PROJETO_MONITORIA_EXTERNO.put(CADASTROPROJETO_DIMENSAO_PROJETO, "Dimensão do Projeto");
		PASSOS_CADASTRO_PROJETO_MONITORIA_EXTERNO.put(CADASTROPROJETO_FORM_EXTERNO, "Dados Gerais do Projeto");
		PASSOS_CADASTRO_PROJETO_MONITORIA_EXTERNO.put(CADASTROPROJETO_COMPONENTE_CURRICULAR, "Componentes Curriculares");
		PASSOS_CADASTRO_PROJETO_MONITORIA_EXTERNO.put(CADASTROPROJETO_SELECAO_DOCENTE, "Selecionar Docentes");
		PASSOS_CADASTRO_PROJETO_MONITORIA_EXTERNO.put(CADASTROPROJETO_SELECAO_COORDENADOR, "Selecionar Coordenador");
		PASSOS_CADASTRO_PROJETO_MONITORIA_EXTERNO.put(CADASTROPROJETO_ANEXAR_ARQUIVOS, "Anexar Arquivos");
		PASSOS_CADASTRO_PROJETO_MONITORIA_EXTERNO.put(CADASTROPROJETO_RESUMO, "Resumo do Projeto");
	}

	public static Map<String, String> PASSOS_CADASTRO_COMPLETO_PAMQEG = new HashMap<String, String>() ;
	static {
		PASSOS_CADASTRO_COMPLETO_PAMQEG.put(CADASTROPROJETO_DIMENSAO_PROJETO, "Dimensão do Projeto");
		PASSOS_CADASTRO_COMPLETO_PAMQEG.put(CADASTROPROJETO_FORM, "Dados Gerais do Projeto");
		PASSOS_CADASTRO_COMPLETO_PAMQEG.put(CADASTROPROJETO_COMPONENTE_CURRICULAR, "Componentes Curriculares");
		PASSOS_CADASTRO_COMPLETO_PAMQEG.put(CADASTROPROJETO_SELECAO_DOCENTE, "Selecionar Docentes");
		PASSOS_CADASTRO_COMPLETO_PAMQEG.put(CADASTROPROJETO_SELECAO_COORDENADOR, "Selecionar Coordenador");
		PASSOS_CADASTRO_COMPLETO_PAMQEG.put(CADASTROPROJETO_ORCAMENTO, "Orçamento");
		PASSOS_CADASTRO_COMPLETO_PAMQEG.put(CADASTROPROJETO_ANEXAR_ARQUIVOS, "Anexar Arquivos");
		PASSOS_CADASTRO_COMPLETO_PAMQEG.put(CADASTROPROJETO_RESUMO, "Resumo do Projeto");
	}
	
	public static Map<String, String> PASSOS_CADASTRO_COMPLETO_PAMQEG_EXTERNO = new HashMap<String, String>() ;
	static {
		PASSOS_CADASTRO_COMPLETO_PAMQEG_EXTERNO.put(CADASTROPROJETO_DIMENSAO_PROJETO, "Dimensão do Projeto");
		PASSOS_CADASTRO_COMPLETO_PAMQEG_EXTERNO.put(CADASTROPROJETO_FORM_EXTERNO, "Dados Gerais do Projeto");
		PASSOS_CADASTRO_COMPLETO_PAMQEG_EXTERNO.put(CADASTROPROJETO_COMPONENTE_CURRICULAR, "Componentes Curriculares");
		PASSOS_CADASTRO_COMPLETO_PAMQEG_EXTERNO.put(CADASTROPROJETO_SELECAO_DOCENTE, "Selecionar Docentes");
		PASSOS_CADASTRO_COMPLETO_PAMQEG_EXTERNO.put(CADASTROPROJETO_SELECAO_COORDENADOR, "Selecionar Coordenador");
		PASSOS_CADASTRO_COMPLETO_PAMQEG_EXTERNO.put(CADASTROPROJETO_ORCAMENTO, "Orçamento");
		PASSOS_CADASTRO_COMPLETO_PAMQEG_EXTERNO.put(CADASTROPROJETO_ANEXAR_ARQUIVOS, "Anexar Arquivos");
		PASSOS_CADASTRO_COMPLETO_PAMQEG_EXTERNO.put(CADASTROPROJETO_RESUMO, "Resumo do Projeto");
	}

	/** RELATORIO FINAL PROJETO */
	public static final String RELATORIOPROJETO_FORM					= "/monitoria/RelatorioProjetoMonitoria/form.jsp";
	public static final String RELATORIOPROJETO_LISTA					= "/monitoria/RelatorioProjetoMonitoria/lista.jsp";
	public static final String AVALIACOES_RELATORIOS					= "/monitoria/RelatorioProjetoMonitoria/avaliacoes_de_um_relatorio.jsp";
	

	/** RELATORIO FINAL MONITOR */
	public static final String RELATORIOMONITOR_FORM 					= "/monitoria/RelatorioMonitor/form.jsp";
	public static final String RELATORIOMONITOR_LISTA 					= "/monitoria/RelatorioMonitor/lista.jsp";
	public static final String RELATORIOMONITOR_BUSCA 					= "/monitoria/RelatorioMonitor/busca.jsp";
	public static final String RELATORIOMONITOR_LISTA_PROGRAD	 	= "/monitoria/RelatorioMonitor/lista_prograd.jsp";	
	public static final String RELATORIOMONITOR_VALIDACAO_PROGRAD		= "/monitoria/RelatorioMonitor/validacao_prograd.jsp";
	public static final String RELATORIOMONITOR_LISTA_COORDENACAO 		= "/monitoria/RelatorioMonitor/lista_coordenacao.jsp";
	public static final String RELATORIOMONITOR_VALIDACAO_COORDENACAO	= "/monitoria/RelatorioMonitor/validacao_coordenacao.jsp";

	/** AVALIACAO RELATORIO FINAL PROJETO */
	public static final String AVALIACAORELATORIOPROJETO_FORM 		= "/monitoria/AvaliacaoRelatorioProjeto/form.jsp";
	public static final String AVALIACAORELATORIOPROJETO_LISTA 		= "/monitoria/AvaliacaoRelatorioProjeto/lista.jsp";
	public static final String AUTORIZACAOATIVIDADE_MONITORIA_BUSCA		= "/monitoria/AutorizacaoDepartamento/form_busca_autorizacoes.jsf";
	
	
	/** VISUALIZAR AVALIACOES */
	public static final String VISUALIZARAVALIACOES_LISTA				= "/monitoria/VisualizarAvaliacoes/lista.jsp";

	/** DISTRIBUIR PROJETO DE ENSINO */
	public static final String DISTRIBUICAOPROJETO_FORM 				= "/monitoria/DistribuicaoProjeto/form.jsp";
	public static final String DISTRIBUICAOPROJETO_LISTA 				= "/monitoria/DistribuicaoProjeto/lista.jsp";
	
	/** DISTRIBUIR RELATORIO DE PROJETO DE ENSINO */
	public static final String DISTRIBUICAORELATORIOPROJETO_FORM 		= "/monitoria/DistribuicaoRelatorioProjeto/form.jsp";
	public static final String DISTRIBUICAORELATORIOPROJETO_LISTA		= "/monitoria/DistribuicaoRelatorioProjeto/lista.jsp";
	
	/** DISTRIBUIR RESUMO SID */
	public static final String DISTRIBUICAORESUMO_FORM 					= "/monitoria/DistribuicaoResumoSid/form.jsp";
	public static final String DISTRIBUICAORESUMO_LISTA 				= "/monitoria/DistribuicaoResumoSid/lista.jsp";
	
	/** CADASTRAR RESUMO SID */
	public static final String CADASTRARRESUMO_FORM 					= "/monitoria/ResumoSid/form.jsp";
	public static final String CADASTRARRESUMO_LISTA 					= "/monitoria/ResumoSid/lista.jsp";
	public static final String CADASTRARRESUMO_FORM_AVALIAR 			= "/monitoria/ResumoSid/form_avaliar.jsp";
	public static final String CADASTRARRESUMO_LISTA_AVALIAR 			= "/monitoria/ResumoSid/lista_avaliar.jsp";
	public static final String CADASTRARRESUMO_RESUMOS 					= "/monitoria/ResumoSid/resumos.jsp";
	public static final String CADASTRARRESUMO_VIEW	 					= "/monitoria/ResumoSid/view.jsp";
	public static final String REGISTRO_FREQUENCIA_DISCENTE_RESUMO_SID	= "/monitoria/ResumoSid/registro_frequencia_discente_sid.jsp";
	public static final String BUSCAR_RESUMO_SID			 			= "/monitoria/ResumoSid/busca.jsp";	
	public static final String RESUMO_SID_LISTA_PARTICIPACOES 			= "/monitoria/ResumoSid/lista_participacoes.jsp";	
		
	/** SELECAO MONITOR */
	public static final String DISCENTEMONITORIA_FORM 					= "/monitoria/DiscenteMonitoria/form.jsp";
	public static final String DISCENTEMONITORIA_LISTA 					= "/monitoria/DiscenteMonitoria/lista.jsp";
	public static final String DISCENTEMONITORIA_ORIENTADORES 			= "/monitoria/DiscenteMonitoria/orientadores.jsp";
	public static final String DISCENTEMONITORIA_LISTAPROJETOS 			= "/monitoria/DiscenteMonitoria/lista_projetos.jsp";
	public static final String DISCENTEMONITORIA_INSCRICAO_DISCENTE 	= "/monitoria/DiscenteMonitoria/inscricao_discente.jsp";
	public static final String DISCENTEMONITORIA_HOMOLOGACAO_BOLSA 	    = "/monitoria/DiscenteMonitoria/homologacao_bolsa_discente.jsp";
	
	public static final String CADASTROPROVASELECAO_PROJETOS 			= "/monitoria/CadastrarProvaSelecao/projetos.jsp";
	public static final String CADASTROPROVASELECAO_VIEW	 			= "/monitoria/CadastrarProvaSelecao/view.jsp";

	/** VER DETALHES DA SELECAO */
	public static final String DETALHESSELECAOMONITORIA_LISTA_PROJETOS 			= "/monitoria/DetalhesSelecaoMonitoria/lista_projetos.jsp";
	public static final String DETALHESSELECAOMONITORIA_VISUALIZAR_CANDIDATOS 	= "/monitoria/DetalhesSelecaoMonitoria/visualizar_candidatos.jsp";	
	public static final String DETALHESSELECAOMONITORIA_NOTIFICAR_CANDIDATO 	= "/monitoria/DetalhesSelecaoMonitoria/listar_candidatos_envio_email.jsp";
	public static final String DETALHESSELECAOMONITORIA_FORMULARIO_CANDIDATO 	= "/monitoria/DetalhesSelecaoMonitoria/envio_email_candidato.jsp";
	public static final String DETALHESSELECAOMONITORIA_FORMULARIO_GRUPO_CANDIDATO 	= "/monitoria/DetalhesSelecaoMonitoria/envio_email_grupo_candidato.jsp";
	
	/** VISUALIZAR RESULTADO SELECAO */
	public static final String DISCENTEMONITORIA_VISUALIZAR_RESULTADO_SELECAO 					= "/monitoria/DiscenteMonitoria/visualizar_resultado.jsp";
	public static final String DISCENTEMONITORIA_VISUALIZAR_RESULTADO_SELECAO_LISTA_PROJETOS 	= "/monitoria/DiscenteMonitoria/projetos_visualizar_resultado.jsp";

	/** AVALIACAO DO PROJETO */
	public static final String AVALIACAOPROJETO_FORM 					= "/monitoria/AvaliacaoMonitoria/form.jsp";
	public static final String AVALIACAOPROJETO_PROJETOS				= "/monitoria/AvaliacaoMonitoria/projetos.jsp";	
	public static final String AVALIACAOPROJETO_VIEW 					= "/monitoria/AvaliacaoMonitoria/view.jsp";	
	public static final String AVALIACAOPROJETO_CONFIRMAR_AVALIACAO		= "/monitoria/AvaliacaoMonitoria/confirmar_avaliacao.jsp";
	public static final String AVALIACAOPROJETO_DISCREPANCIA_FORM 		= "/monitoria/AvaliacaoMonitoria/discrepancia_form.jsp";
	public static final String AVALIACAOPROJETO_DISCREPANCIA_LISTA 		= "/monitoria/AvaliacaoMonitoria/discrepancia_lista.jsp";	

	/** AUTORIZACAO DO PROJETO */
	public static final String AUTORIZACAOPROJETO_FORM 					= "/monitoria/AutorizacaoProjetoMonitoria/form.jsp";
	public static final String AUTORIZACAOPROJETO_LISTA					= "/monitoria/AutorizacaoProjetoMonitoria/lista.jsp";
	public static final String AUTORIZACAOPROJETO_RECIBO				= "/monitoria/AutorizacaoProjetoMonitoria/recibo_autorizacao.jsp";

	/** AUTORIZACAO DA RECONSIDERACAO */
	public static final String AUTORIZACAORECONSIDERACAO_FORM 			= "/monitoria/AutorizacaoReconsideracao/form.jsp";
	public static final String AUTORIZACAORECONSIDERACAO_LISTA			= "/monitoria/AutorizacaoReconsideracao/lista.jsp";

	/** SOLICITACAO RECONSIDERACAO CADASTRO */
	public static final String SOLICITACAORECONSIDERACAOCADASTRO_FORM 	= "/monitoria/SolicitacaoReconsideracaoCadastro/form.jsp";
	public static final String SOLICITACAORECONSIDERACAOCADASTRO_LISTA	= "/monitoria/SolicitacaoReconsideracaoCadastro/lista.jsp";

	/** SOLICITACAO RECONSIDERACAO DE AVALIACAO */
	public static final String SOLICITACAO_RECONSIDERACAO_FORM 			= "/monitoria/SolicitacaoReconsideracao/form.jsp";
	public static final String SOLICITACAO_RECONSIDERACAO_LISTA			= "/monitoria/SolicitacaoReconsideracao/lista.jsp";
	public static final String SOLICITACAO_RECONSIDERACAO_MINHAS		= "/monitoria/SolicitacaoReconsideracao/minhas_solicitacoes.jsp";
	public static final String SOLICITACAO_RECONSIDERACAO_VIEW			= "/monitoria/SolicitacaoReconsideracao/view.jsp";

	public static final String ANALISAR_SOLICITACAO_RECONSIDERACAO_LISTA= "/monitoria/AnalisarSolicitacaoReconsideracao/lista.jsp";
	public static final String ANALISAR_SOLICITACAO_RECONSIDERACAO_FORM = "/monitoria/AnalisarSolicitacaoReconsideracao/form.jsp";

	
	/** CONSULTAR MONITORES */
	public static final String CONSULTARMONITOR_CERTIFICADO 			= "/monitoria/ConsultarMonitor/certificado.jsp";
	public static final String CONSULTARMONITOR_DECLARACAO	 			= "/monitoria/ConsultarMonitor/declaracao.jsp";
	public static final String CONSULTARMONITOR_DETALHES_ORIENTACAO		= "/monitoria/ConsultarMonitor/detalhes_orientacao.jsp";
	public static final String CONSULTARMONITOR_LISTA					= "/monitoria/ConsultarMonitor/lista.jsp";
	public static final String CONSULTARMONITOR_LOCALIZAR_ORIENTACOES	= "/monitoria/ConsultarMonitor/orientacoes_localizadas.jsp";	
	public static final String CONSULTARMONITOR_VIEW					= "/monitoria/ConsultarMonitor/view.jsp";

	/** ATIVIDADE MONITOR */
	public static final String ATIVIDADEMONITOR_FORM 				= "/monitoria/AtividadeMonitor/form.jsp";
	public static final String ATIVIDADEMONITOR_LISTA				= "/monitoria/AtividadeMonitor/lista.jsp";
	public static final String ATIVIDADEMONITOR_LISTAAVALIACAO			= "/monitoria/AtividadeMonitor/lista_avaliar_atividade.jsp";
	public static final String ATIVIDADEMONITOR_FORMAVALIACAO			= "/monitoria/AtividadeMonitor/form_avaliar_atividade.jsp";
	public static final String ATIVIDADEMONITOR_BUSCAR_MONITOR			= "/monitoria/AtividadeMonitor/buscar_monitor.jsp";
	
	public static final String ATIVIDADEMONITOR_VIEW					= "/monitoria/ValidacaoProgradAtividadeMonitor/view.jsp";
	public static final String ATIVIDADEMONITOR_LISTA_VALIDACAO			= "/monitoria/ValidacaoProgradAtividadeMonitor/lista.jsp";
	public static final String ATIVIDADEMONITOR_FORM_VALIDACAO			= "/monitoria/ValidacaoProgradAtividadeMonitor/form.jsp";
	
	/** RELATORIOS PROGRAD */
	public static final String RELATORIO_QUANTITATIVOMONITORES_FORM		= "/monitoria/Relatorios/quantitativo_monitores_form.jsp";
	public static final String RELATORIO_QUANTITATIVOMONITORES_REL		= "/monitoria/Relatorios/quantitativo_monitores_rel.jsp";
	public static final String RELATORIO_PROJETOSMONITORES_FORM			= "/monitoria/Relatorios/projetos_monitores_form.jsp";
	public static final String RELATORIO_PROJETOSMONITORES_REL			= "/monitoria/Relatorios/projetos_monitores_rel.jsp";
	public static final String RELATORIO_MONITORESMES_FORM				= "/monitoria/Relatorios/monitores_mes_form.jsp";
	public static final String RELATORIO_MONITORESMES_REL				= "/monitoria/Relatorios/monitores_mes_rel.jsp";

	/** CLASSIFICACAO DE PROJETOS */
	public static final String CLASSIFICACAODEPROJETOS_CLASSIFICACAO	= "/monitoria/ClassificarProjetos/classificacao.jsp";
	
	/** DISTRIBUICAO DE BOLSAS */
	public static final String DISTRIBUICAOBOLSAS_CLASSIFICACAO			= "/monitoria/CalcularBolsas/classificacao.jsp";
	
	/** CADASTRAR DOCENTES NO PROJETO, A QUALQUER TEMPO */
	public static final String CADASTRARDOCENTE_FORM					= "/monitoria/CadastrarEquipeDocente/form.jsp";
	public static final String CADASTRARDOCENTE_LISTA					= "/monitoria/CadastrarEquipeDocente/lista.jsp";	
	
	/** AVISOS DE PROJETOS */
	public static final String CADASTRARAVISOPROJETO_FORM				= "/monitoria/CadastrarAvisoProjeto/form.jsp";
	public static final String CADASTRARAVISOPROJETO_LISTA				= "/monitoria/CadastrarAvisoProjeto/lista.jsp";	
	public static final String CADASTRARAVISOPROJETO_LISTA_PROJETOS		= "/monitoria/CadastrarAvisoProjeto/lista_projetos.jsp";
	public static final String MOSTRAR_AVISOPROJETO						= "/monitoria/CadastrarAvisoProjeto/mostrar.jsp";	

	/** CADATRAR ORIENTACOES DO PROJETO */
	public static final String CADASTRARORIENTACOES_ESCOLHE_DISCENTE	= "/monitoria/CadastrarOrientacao/escolher_discente.jsp";
	public static final String CADASTRARORIENTACOES_ESCOLHE_ORIENTADOR	= "/monitoria/CadastrarOrientacao/escolher_orientador.jsp";
	public static final String CADASTRARORIENTACOES_LISTA_PROJETO		= "/monitoria/CadastrarOrientacao/lista.jsp";
	
	/** VALIDACAO DO CADASTRO DO RESULTADO DE SELECAO */
	public static final String VALIDASELECAO_FORM						= "/monitoria/ValidaSelecaoMonitor/form.jsp";
	public static final String VALIDASELECAO_LISTA						= "/monitoria/ValidaSelecaoMonitor/lista.jsp";
	public static final String VALIDASELECAO_PROVAS						= "/monitoria/ValidaSelecaoMonitor/provas.jsp";
	public static final String VALIDASELECAO_ALTERA						= "/monitoria/ValidaSelecaoMonitor/altera_resultado.jsp";

	/** CADASTRAR DADOS BANCARIOS */
	public static final String DISCENTEMONITORIA_CADASTRAR_DADOS_BANCARIOS	= "/monitoria/DiscenteMonitoria/cadastro_dados_bancarios.jsp";
	
	/** DISCENTE - MEUS PROJETOS */
	public static final String DISCENTEMONITORIA_MEUS_PROJETOS			= "/monitoria/DiscenteMonitoria/meus_projetos.jsp";	
	public static final String DISCENTEMONITORIA_ACEITAR_RECUSAR_MONITORIA		= "/monitoria/DiscenteMonitoria/aceitar_recusar_monitoria.jsp";

	/** CADASTRO */
	public static final String CADASTRARMONITOR_LISTA_PROJETOS			= "/monitoria/CadastrarMonitor/lista.jsp";	
	public static final String CADASTRARMONITOR_FORM				= "/monitoria/CadastrarMonitor/form.jsp";
	
	/** ALTERAR DISCENTE MONITORIA */
	public static final String ALTERARDISCENTEMONITORIA_FORM			= "/monitoria/AlterarDiscenteMonitoria/form.jsp";
	public static final String ALTERARDISCENTEMONITORIA_REATIVAR_FORM	= "/monitoria/AlterarDiscenteMonitoria/reativar_form.jsp";
	public static final String ALTERARDISCENTEMONITORIA_LISTA			= "/monitoria/AlterarDiscenteMonitoria/lista.jsp";
	public static final String ALTERARDISCENTEMONITORIA_ALTERAR_VINCULO	= "/monitoria/AlterarDiscenteMonitoria/alterar_vinculo_monitor.jsp";
	public static final String ALTERARDISCENTEMONITORIA_NOTA_FORM		= "/monitoria/AlterarDiscenteMonitoria/alterar_nota_monitor.jsp";

	/** ALTERAR COMPONENTES CURRICULARES OBRIGATORIOS */
	public static final String ALTERARCOMPONENTESOBRIGATORIOS_FORM		= "/monitoria/AlterarComponentesObrigatorios/form.jsp";
	public static final String ALTERARCOMPONENTESOBRIGATORIOS_LISTA		= "/monitoria/AlterarComponentesObrigatorios/lista.jsp";

	/** ALTERAR SITUAÇÃO DO PROJETO */
	public static final String ALTERARSITUACAOPROJETO_FORM				= "/monitoria/AlterarSituacaoProjeto/form.jsp";
	public static final String ALTERARSITUACAOPROJETO_LISTA				= "/monitoria/AlterarSituacaoProjeto/lista.jsp";

	/** RELATORIOS DE PROJETOS */
	public static final String RELATORIO_QUADRO_GERAL_PROJETOSA_FORM						= "/monitoria/Relatorios/quadro_geral_projetos_form.jsp";	
	public static final String RELATORIO_QUADRO_GERAL_PROJETOSA_REL							= "/monitoria/Relatorios/quadro_geral_projetos_rel.jsp";
	public static final String RELATORIO_PROJETOS_ATIVOS_COM_MONITORES_ATIVOS_INATIVOS		= "/monitoria/Relatorios/projetos_monitores_ativos_inativos_rel.jsp";
	
	/** RELATORIO DE DADOS BANCÁRIOS */
	public static final String RELATORIO_DADOS_BANCARIOS_MONITORES_FORM	= "/monitoria/Relatorios/dados_bancarios_monitores_form.jsp";	
	public static final String RELATORIO_DADOS_BANCARIOS_MONITORES_REL	= "/monitoria/Relatorios/dados_bancarios_monitores_rel.jsp";
	
	/** ALTERAR DOCENTE DO PROJETO */
	public static final String ALTERAREQUIPEDOCENTE_FORM				= "/monitoria/AlterarEquipeDocente/form.jsp";
	public static final String ALTERAREQUIPEDOCENTE_LISTA				= "/monitoria/AlterarEquipeDocente/lista.jsp";	
	
	/** CONSULTAR DOCENTE DO PROJETO */
	public static final String CONSULTAREQUIPEDOCENTE_VIEW				= "/monitoria/ConsultarEquipeDocente/view.jsp";
	
	/** CADASTRAR ITEM AVALIAÇAO */
	public static final String CADASTRARITEMAVALIACAO_FORM				= "/monitoria/ItemAvaliacaoMonitoria/form.jsp";
	public static final String CADASTRARITEMAVALIACAO_LISTA				= "/monitoria/ItemAvaliacaoMonitoria/lista.jsp";	
	
	/** CADASTRAR GRUPO */
	public static final String CADASTRARGRUPOITEMAVALIACAO_FORM			= "/monitoria/GrupoItemAvaliacao/form.jsp";
	public static final String CADASTRARGRUPOITEMAVALIACAO_LISTA		= "/monitoria/GrupoItemAvaliacao/lista.jsp";
	
	/** DOCUMENTOS AUTENTICADOS */
	public static final String DOCUMENTOS_AUTENTICADOS_LISTA_DISCENTES 	= "/monitoria/DocumentosAutenticados/lista_discentes.jsp";
	public static final String DOCUMENTOS_AUTENTICADOS_LISTA_DOCENTES  	= "/monitoria/DocumentosAutenticados/lista_docentes.jsp";

	/** DOCUMENTOS AUTENTICADOS */
	public static final String MOVIMENTAR_COTA_LISTA		 	= "/monitoria/MovimentacaoCota/lista.jsp";
	public static final String MOVIMENTAR_COTA_FORM  			= "/monitoria/MovimentacaoCota/form.jsp";

	/** PUBLICANDO RESULTADOS DAS AVALIAÇÕES DE MONITORIA */
	public static final String PUBLICAR_RESULTADO_LISTA  			= "/monitoria/PublicarResultado/lista.jsp";
	public static final String PUBLICAR_RESULTADO_SELECIONAR_EDITAL		= "/monitoria/PublicarResultado/busca_edital.jsp";
	
}
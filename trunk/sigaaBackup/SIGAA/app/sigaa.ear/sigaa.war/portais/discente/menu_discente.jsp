<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>

<%-- MENU DE OPÇÕES PARA O DISCENTE --%>

<div id="menu-dropdown">
<div class="wrapper">

<h:form id="form_menu_discente">

<input type="hidden" name="id" value="${ usuario.discenteAtivo.id }"/>
<input type="hidden" name="jscook_action"/>

<t:jscookMenu layout="hbr" theme="ThemeOffice" styleLocation="/css/jscookmenu">

	<t:navigationMenuItem itemLabel="Ensino" id="ensino" icon="/img/icones/ensino_menu.gif">
		
		<t:navigationMenuItem id="avaliacaoInstitucional" itemLabel="Avaliação Institucional" rendered="#{ acesso.usuario.discenteAtivo.nivelStr == 'G' && acesso.usuario.discenteAtivo.regular }">
			<t:navigationMenuItem id="listaAvaliacoesInstitucionais" itemLabel="Preencher a Avaliação Institucional" action="#{ calendarioAvaliacaoInstitucionalBean.listar }" itemDisabled="false" rendered="#{  acesso.usuario.discenteAtivo.nivelStr == 'G' }"/>
			<t:navigationMenuItem id="reverAvaliacaoAnterior" itemLabel="Rever a Avaliação Anterior" action="#{ avaliacaoInstitucionalAnterior.listaAnterior }" itemDisabled="false" rendered="#{ acesso.usuario.discenteAtivo.nivelStr == 'G' && empty acesso.usuario.discenteAtivo.polo }" split="true" />
			<t:navigationMenuItem id="resultadoAvaliacao" itemLabel="Consultar o Resultado da Avaliação" action="#{ relatorioAvaliacaoMBean.iniciarConsultaPublica }" itemDisabled="false" rendered="#{ acesso.usuario.discenteAtivo.nivelStr == 'G' && empty acesso.usuario.discenteAtivo.polo }"/>
			<t:navigationMenuItem id="observacaoDocente" itemLabel="Observações dos Docentes Sobre Minhas Turmas" action="#{ relatorioAvaliacaoMBean.iniciarObservacoesTurmasDiscente }" itemDisabled="false" rendered="#{  acesso.usuario.discenteAtivo.nivelStr == 'G' && empty acesso.usuario.discenteAtivo.polo  }"/>
		</t:navigationMenuItem>

		<t:navigationMenuItem id="minhasNotas" itemLabel="Minhas Notas" action="#{ relatorioNotasAluno.gerarRelatorio }" itemDisabled="false" icon="/img/celular_icone.gif"/>
		<t:navigationMenuItem id="atestadoMatricula" itemLabel="Atestado de Matrícula" action="#{ portalDiscente.atestadoMatricula }" itemDisabled="false" icon="/img/celular_icone.gif"/>
		<t:navigationMenuItem id="consultarHistorico" itemLabel="Consultar Histórico" action="#{ portalDiscente.historico }" itemDisabled="false"/>
		<t:navigationMenuItem id="consultarIndicesAcademicos" itemLabel="Consultar Índices Acadêmicos" action="#{ indiceAcademicoMBean.selecionaDiscente }" rendered="#{portalDiscente.passivelEmissaoRelatorioIndices and !portalDiscente.modoReduzido}"/>
		<t:navigationMenuItem id="consultarIndicesAcademicosInativo" itemLabel="Consultar Índices Acadêmicos" action="#{ indiceAcademicoMBean.relatorioInativo }" rendered="#{portalDiscente.passivelEmissaoRelatorioIndices and portalDiscente.modoReduzido}"/>
		<t:navigationMenuItem id="declaracaoVinculo" itemLabel="Declaração de Vínculo" action="#{ declaracaoVinculo.emitirDeclaracao}" itemDisabled="false" rendered="#{ !usuario.discenteAtivo.residencia && !usuario.discenteAtivo.formacaoComplementar}"/>
		
		<t:navigationMenuItem itemLabel="Termo de Autorização para Publicação de Teses e Dissertações - TEDE" action="#{ termoPublicacaoTD.iniciarDiscente }" icon="/img/graduacao/coordenador/documento.png"  rendered="#{usuario.discenteAtivo.stricto}" />
		
		<t:navigationMenuItem itemLabel="Destrancar Curso" action="#{ destrancarPrograma.iniciar}"  rendered="false" />		
		
		<c:if test="${ usuario.discenteAtivo.discenteEad }">
			<t:navigationMenuItem id="verFichaAvaliacao" itemLabel="Ver Fichas de Avaliação" action="#{ fichaAvaliacaoEad.verFichaDiscente }" itemDisabled="false"/>
			<t:navigationMenuItem id="definirHorarioTutoriaPresencial" itemLabel="Definir horário de tutoria presencial" action="#{ matriculaGraduacao.selecionarTutoriaAluno }" itemDisabled="false"/>
		</c:if>
		
		<%-- 
		<c:if test="${!usuario.discenteAtivo.discenteEad && !usuario.discenteAtivo.residencia && !usuario.discenteAtivo.formacaoComplementar}">
		--%>
	 	<t:navigationMenuItem id="matriculaOnlineFormacaoComplementar" itemLabel="Matrícula On-Line" split="true" rendered="#{usuario.discenteAtivo.formacaoComplementar}">
	 		<t:navigationMenuItem id="realizarMatriculaFormacaoComplementar" itemLabel="Realizar Matrícula" action="#{matriculaFormacaoComplementarMBean.iniciar}" rendered="#{usuario.discenteAtivo.formacaoComplementar}" />
	 	</t:navigationMenuItem>
	 	<t:navigationMenuItem id="matriculaOnlineMetropoleDigital" itemLabel="Matrícula On-Line" split="true" rendered="#{usuario.discenteAtivo.metropoleDigital}">
	 		<t:navigationMenuItem id="realizarmatriculaMetropoleDigital" itemLabel="Realizar Matrícula no Módulo Básico" action="#{ matriculaGraduacao.telaInstrucoes}" rendered="#{usuario.discenteAtivo.metropoleDigital}" />
	 		<t:navigationMenuItem id="realizarmatriculaModuloAvancadoMetropoleDigital" itemLabel="Realizar Matrícula no Módulo Avançado" action="#{ matriculaModuloAvancadoMBean.iniciarDiscente}" rendered="#{usuario.discenteAtivo.metropoleDigital}" />
	 		<t:navigationMenuItem id="comprovanteMatriculaMetropoleDigital" itemLabel="Ver Comprovante de Matrícula" action="#{ matriculaGraduacao.verComprovanteSolicitacoes}" split="true"/>
	 	</t:navigationMenuItem>
		<c:if test="${!usuario.discenteAtivo.lato && !usuario.discenteAtivo.residencia && !usuario.discenteAtivo.formacaoComplementar && !usuario.discenteAtivo.discente.cancelado && !usuario.discenteAtivo.metropoleDigital}">
		 	<t:navigationMenuItem id="matriculaOnline" itemLabel="Matrícula On-Line" split="true">
		 		<t:navigationMenuItem id="realizarmatricula" itemLabel="Realizar Matrícula" action="#{ matriculaGraduacao.telaInstrucoes}" rendered="#{!usuario.discenteAtivo.stricto}" />
		 		<t:navigationMenuItem id="realizarmatriculaStricto" itemLabel="Realizar Matrícula" action="#{ matriculaStrictoBean.iniciar}" rendered="#{usuario.discenteAtivo.stricto}" />
		 		<t:navigationMenuItem id="realizarMatriculaFerias" itemLabel="Realizar Matrícula em Turma de Férias" action="#{ confirmacaoMatriculaFeriasBean.iniciar}" rendered="#{!usuario.discenteAtivo.stricto}" />		 			 	
		 		<t:navigationMenuItem id="realizarMatriculaExtraordinaria" itemLabel="Realizar Matrícula Extraordinária" action="#{ matriculaExtraordinaria.iniciar}" rendered="#{!usuario.discenteAtivo.stricto && !usuario.discenteAtivo.discenteEad}" />
		 		<t:navigationMenuItem id="realizarMatriculaFeriasExtraordinaria" itemLabel="Realizar Matrícula Extraordinária em Turma de Férias" action="#{ matriculaExtraordinaria.iniciarFerias}" rendered="#{!usuario.discenteAtivo.stricto && !usuario.discenteAtivo.discenteEad}"/>
		 		<t:navigationMenuItem id="comprovanteMatricula" itemLabel="Ver Comprovante de Matrícula" action="#{ matriculaGraduacao.verComprovanteSolicitacoes}" split="true"/>
				<t:navigationMenuItem id="comprovanteMatriculaTurmaFerias" itemLabel="Ver Comprovante de Matrícula para Turmas de Férias" action="#{ portalDiscente.atestadoMatriculaTurmaFerias}" rendered="#{!usuario.discenteAtivo.stricto && !usuario.discenteAtivo.discenteEad}" />		 		
				<t:navigationMenuItem id="orientacoesMatricula" itemLabel="Ver Orientações de Matrícula" action="#{ matriculaGraduacao.acompanharSolicitacoes}"  />
		 		<t:navigationMenuItem id="resultadoProcessamento" itemLabel="Ver Resultado do Processamento" action="#{ matriculaGraduacao.verComprovanteSolicitacoes}"  />
		 		<t:navigationMenuItem id="planoMatriculas" itemLabel="Meu Plano de Matrículas" action="#{ planoMatriculaBean.gerar }" split="true" rendered="#{usuario.discenteAtivo.graduacao}" />
		 	</t:navigationMenuItem >
			<c:if test="${usuario.discenteAtivo.graduacao and !usuario.discenteAtivo.discenteEad}">
				<t:navigationMenuItem  itemLabel="Solicitações de Ensino Individual" id="solicitacoesEnsinoIndividual">
					<t:navigationMenuItem id="solicitarEnsinoIndividual" itemLabel="Solicitar Ensino Individual" action="#{ solicitacaoEnsinoIndividual.iniciarEnsinoIndividualizado }" itemDisabled="false" />
					<t:navigationMenuItem id="solicitacoesEnviadas" itemLabel="Visualizar Solicitações Enviadas" action="#{ solicitacaoEnsinoIndividual.listarEnsinoIndividual }" itemDisabled="false"/>
					<t:navigationMenuItem id="comprovanteSolicitacoes" itemLabel="Emitir Comprovante de Solicitações" action="#{ solicitacaoEnsinoIndividual.emitirComprovanteEnsinoIndividual }" itemDisabled="false"/>
				</t:navigationMenuItem>
				<t:navigationMenuItem itemLabel="Solicitações de Turma de Férias" id="solicitacoesTurmasFerias">
					<t:navigationMenuItem id="solicitarTurmaFerias" itemLabel="Solicitar Turma de Férias" action="#{ solicitacaoEnsinoIndividual.iniciarFerias }" itemDisabled="false" />
					<t:navigationMenuItem id="solicitacoesFeriasEnviadas" itemLabel="Visualizar Solicitações Enviadas" action="#{ solicitacaoEnsinoIndividual.listarFerias}" itemDisabled="false"/>
					<t:navigationMenuItem id="comprovanteSolicitacoesFerias" itemLabel="Emitir Comprovante de Solicitações" action="#{ solicitacaoEnsinoIndividual.emitirComprovanteFerias }" itemDisabled="false"/>
				</t:navigationMenuItem>
			</c:if>
			<c:if test="${trancamentoMatricula.permiteTrancarComponente}">
				<t:navigationMenuItem itemLabel="Trancamento de Componente Curricular" id="trancamentoMatricula">
					<t:navigationMenuItem id="trancarComponente" itemLabel="Trancar" action="#{ trancamentoMatricula.popularSolicitacao }" itemDisabled="false"/>
					<t:navigationMenuItem id="andamentoTrancamento" itemLabel="Exibir Andamento do Trancamento" action="#{ trancamentoMatricula.iniciarMeusTrancamentos }" itemDisabled="false"/>
				</t:navigationMenuItem>
			</c:if>
			<c:if test="${(usuario.discenteAtivo.graduacao or usuario.discenteAtivo.stricto)}">	
				<t:navigationMenuItem itemLabel="Trancamento de Programa" id="trancamentoPrograma" split="true">
					<t:navigationMenuItem id="solicitarTrancamento" itemLabel="Iniciar Solicitação de Trancamento Regular" action="#{ trancamentoPrograma.iniciarTrancamentoRegular }"/>
					<t:navigationMenuItem id="solicitarTrancamentoPosteriori" itemLabel="Solicitar Trancamento a Posteriori" action="#{ trancamentoPrograma.iniciarTrancamentoPosteriori }"/>
					<t:navigationMenuItem id="exibirSolicitacoesTrancamento" itemLabel="Exibir Solicitações de Trancamento" action="#{ trancamentoPrograma.exibirMinhasSolicitacoes }"/>
				</t:navigationMenuItem>
			</c:if>
		</c:if>
		
		<c:if test="${usuario.discenteAtivo.graduacao and !usuario.discenteAtivo.discenteEad}">
			<t:navigationMenuItem itemLabel="Reposição de Avaliação" id="reposicaoProva" split="true">
				<t:navigationMenuItem id="solicitacoesReposicaoProva" itemLabel="Solicitar Reposição de Avaliação" action="#{ solicitacaoReposicaoProva.iniciar }"/>
				<t:navigationMenuItem id="exibirSolicitacoes" itemLabel="Exibir Solicitações" action="#{ solicitacaoReposicaoProva.listarSolicitacoes }"/>
			</t:navigationMenuItem>				
		</c:if>
		
		<t:navigationMenuItem id="consultarCurso" itemLabel="Consultar Curso" split="true" action="#{ curso.popularBuscaGeral }" itemDisabled="false"/>
		<t:navigationMenuItem id="consultarComponenteCurricular" itemLabel="Consultar Componente Curricular" action="#{ componenteCurricular.popularBuscaDiscente }"  itemDisabled="false"/>
		<c:if test="${usuario.discenteAtivo.graduacao}">
			<t:navigationMenuItem id="consultarEstruturaCurricular" itemLabel="Consultar Estrutura Curricular"  action="#{ curriculo.popularBuscaGeral }" itemDisabled="false"/>
		</c:if>
		<t:navigationMenuItem id="consultarTurma" itemLabel="Consultar Turma" action="#{ buscaTurmaBean.popularBuscaGeral }" itemDisabled="false"/>
		<t:navigationMenuItem id="consultarTurmasSolicitadas" itemLabel="Consultar Turmas Solicitadas"  action="#{ solicitacaoTurma.iniciarListaSolicitacoesCursoDiscente }" rendered="#{usuario.discenteAtivo.graduacao}"/>		
		<c:if test="${usuario.discenteAtivo.tecnico}">
			<t:navigationMenuItem id="consultarEstruturaCurricularTecnico" itemLabel="Consultar Estrutura Curricular"  actionListener="#{ menuDiscente.consultaEstruturaTec }" itemDisabled="false"/>
		</c:if>
		<t:navigationMenuItem id="unidadesAcademicas" itemLabel="Unidades Acadêmicas" action="#{ unidade.popularBuscaGeral }" itemDisabled="false"/>
		<t:navigationMenuItem id="consultarCalendarioAcademico" itemLabel="Consultar Calendário Acadêmico" action="#{calendario.iniciarBusca}" itemDisabled="false" split="true"/>
		<c:if test="${usuario.discenteAtivo.lato}">
			<t:navigationMenuItem id="consultarGRUMensalidade" itemLabel="Consultar Mensalidades" action="#{mensalidadeCursoLatoMBean.listarDiscente}" itemDisabled="false" />
		</c:if>		
	</t:navigationMenuItem>

	<%-- PESQUISA --%>
	<t:navigationMenuItem itemLabel="Pesquisa" id="pesquisa" icon="/img/icones/pesquisa_menu.gif">
		<t:navigationMenuItem itemLabel="Consultar Projetos" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/projetoPesquisa/buscarProjetos.do?dispatch=consulta&popular=true"/>
		<c:if test="${usuario.discenteAtivo.graduacao or usuario.discenteAtivo.tecnico}">
			<t:navigationMenuItem itemLabel="Plano de Trabalho" split="true">
				<t:navigationMenuItem itemLabel="Meus Planos de Trabalho" actionListener="#{menuDiscente.redirecionar}" itemValue="/sigaa/pesquisa/planoTrabalho/wizard.do?dispatch=listarPorDiscente"/>
			</t:navigationMenuItem>
			<t:navigationMenuItem id="report_iniciacaoCientifica" itemLabel="Relatórios de Iniciação Científica">
				<t:navigationMenuItem id="relatoriosParciais" itemLabel="Relatórios Parciais">
					<t:navigationMenuItem id="report_enviarRelatorioBolsaParcial" itemLabel="Enviar" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/relatorioBolsaParcial.do?dispatch=listarPlanos"/>
					<t:navigationMenuItem id="report_consultarRelatorioBolsaParcial" itemLabel="Consultar" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/relatorioBolsaParcial.do?dispatch=listarRelatorios"/>
				</t:navigationMenuItem>
				<t:navigationMenuItem id="relatoriosFinais" itemLabel="Relatórios Finais">
					<t:navigationMenuItem id="report_enviarRelatorioBolsaFinal" itemLabel="Enviar" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/relatorioBolsaFinal.do?dispatch=listarPlanos"/>
					<t:navigationMenuItem id="report_consultarRelatorioBolsaFinal" itemLabel="Consultar" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/relatorioBolsaFinal.do?dispatch=listarRelatorios"/>
				</t:navigationMenuItem>
			</t:navigationMenuItem>
			<t:navigationMenuItem id="certificadosDeclaracoes" itemLabel="Certificados e Declarações">
				<t:navigationMenuItem id="declaracoesBolsista" itemLabel="Declaração de Bolsista em Plano de Trabalho" action="#{declaracoesPesquisa.listarDeclararoesPlanoTrabalhoBolsista}" />				
			</t:navigationMenuItem>
		</c:if>
		<t:navigationMenuItem itemLabel="Congresso de Iniciação Científica">
			<t:navigationMenuItem id="submeterResumoCongresso" itemLabel="Submeter resumo" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/resumoCongresso.do?dispatch=popularInicioEnvio" rendered="#{(usuario.discenteAtivo.graduacao || usuario.discenteAtivo.tecnico)}"/>
			<t:navigationMenuItem itemLabel="Meus resumos" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/resumoCongresso.do?dispatch=listarResumosAutor"/>
		</t:navigationMenuItem>
	</t:navigationMenuItem>

	<%-- EXTENSÃO --%>
	<t:navigationMenuItem itemLabel="Extensão" id="extensao" icon="/img/icones/extensao_menu.gif"
		rendered="#{(usuario.discenteAtivo.graduacao || usuario.discenteAtivo.tecnico || usuario.discenteAtivo.stricto)}">
		
			<t:navigationMenuItem id="consultarAcoesExtensao" itemLabel="Consultar Ações" action="#{atividadeExtensao.preLocalizar}" itemDisabled="false" />
			<t:navigationMenuItem id="meusPlanosTrabalhoExtensao" itemLabel="Meus Planos de Trabalho" action="#{ planoTrabalhoExtensao.carregarPlanosTrabalhoDiscenteLogado }" split="true"/>
			<t:navigationMenuItem id="acoesMembroEquipeExtensao" itemLabel="Minhas Ações como Membro da Equipe" action="#{ atividadeExtensao.carregarAcoesDiscenteLogado }"/>			
			<t:navigationMenuItem id="relatoriosExtensao" itemLabel="Meus Relatórios" action="#{ relatorioBolsistaExtensao.iniciarCadastroRelatorio }"/>
			<t:navigationMenuItem id="certificadosDeclaracoesExtensao" itemLabel="Certificados e Declarações" action="#{documentosAutenticadosExtensao.participacoesDiscenteUsuarioLogado}"/>
			<t:navigationMenuItem id="inscreverAcaoExtensao" itemLabel="Inscrição On-line em Ações de Extensão" actionListener="#{menuDiscente.redirecionar}" itemValue="/sigaa/public/extensao/paginaListaPeriodosInscricoesAtividadesPublico.jsf?aba=p-extensao" itemDisabled="false"/>
			<t:navigationMenuItem id="visualizarResultadosExtensao" itemLabel="Visualizar Resultados das inscrições" action="#{selecaoDiscenteExtensao.iniciarVisualizarResultados}" itemDisabled="false"/>

	</t:navigationMenuItem>
	
	<%-- MONITORIA --%>
	<t:navigationMenuItem itemLabel="Monitoria" id="monitoria" icon="/img/icones/monitoria_menu.gif"
		rendered="#{(usuario.discenteAtivo.graduacao)}">
	
		<t:navigationMenuItem id="consultarProjetosMonitoria" itemLabel="Consultar Projetos" action="#{coordMonitoria.situacaoProjeto}" />
		<t:navigationMenuItem id="projetosMonitoria" itemLabel="Meus Projetos de Monitoria" actionListener="#{menuDiscente.redirecionar}" itemValue="/monitoria/DiscenteMonitoria/meus_projetos.jsf" itemDisabled="false" split="true"/>
		<t:navigationMenuItem id="relatoriosMonitoria" itemLabel="Meus Relatórios" action="#{ relatorioMonitor.listar }"/>
		<t:navigationMenuItem id="certificadosMonitoria" itemLabel="Meus Certificados">
			<t:navigationMenuItem id="certificadosProjetosMonitoria" itemLabel="Certificados de Projetos" action="#{documentosAutenticadosMonitoria.participacoesDiscenteUsuarioLogado}"/>
			<t:navigationMenuItem id="certificadosSID_Monitoria" itemLabel="Certificados do SID" action="#{resumoSid.listarParticipacoesDiscente}" itemDisabled="false" />
		</t:navigationMenuItem>			
		<t:navigationMenuItem id="atividadesMesFrequenciaMonitoria" itemLabel="Atividades do Mês / Freqüência" rendered="#{monitoria.frequenciaMonitoria}">
			<t:navigationMenuItem id="cadastrarAtividadesMonitoria" itemLabel="Cadastrar" actionListener="#{menuDiscente.redirecionar}" itemValue="/monitoria/DiscenteMonitoria/meus_projetos.jsf" itemDisabled="false"/>
			<t:navigationMenuItem id="consultarAtividadesMonitoria" itemLabel="Consultar" action="#{atividadeMonitor.listarAtividades}" itemDisabled="false" split="true"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem id="inscreverSelecaoMonitoria" itemLabel="Inscrever-se em Seleção de Monitoria" action="#{agregadorBolsas.iniciarBuscar}" itemDisabled="false"/>
		<t:navigationMenuItem id="resultadoSelecaoMonitoria" itemLabel="Visualizar Resultado da Seleção" action="#{discenteMonitoria.popularVisualizarResultados}" itemDisabled="false"/>

	</t:navigationMenuItem>
	
	
    <%-- AÇÃO ACADÊMICA ASSOCIADA --%>
    <t:navigationMenuItem itemLabel="Ações Associadas" id="acoesAssociadas" icon="/img/projetos/associado_menu.png"
      		rendered="#{(usuario.discenteAtivo.graduacao || usuario.discenteAtivo.tecnico || usuario.discenteAtivo.stricto || usuario.discenteAtivo.lato)}">

			<t:navigationMenuItem id="consultarAcoesAssociadas" itemLabel="Consultar Ações Associadas" action="#{ buscaAcaoAssociada.iniciar }" itemDisabled="false" />
			<t:navigationMenuItem id="meusPlanosTrabalhoAssociadas" itemLabel="Meus Planos de Trabalho" action="#{ planoTrabalhoProjeto.planosDiscente }" split="true"/>
			<t:navigationMenuItem id="certificadoDeclaracao" itemLabel="Certificados e Declaracões" action="#{ declaracaoMembroProjIntegradoMBean.participacoesDiscenteUsuarioLogado }" split="true"/>

    </t:navigationMenuItem> 	
	

	<%-- ACESSO AOS SERVIÇOS DA BIBLIOTECA PARA O DISCENTE --%>

	<%-- o aluno estando ativo é para aparecer essa opção no menu --%>
	<t:navigationMenuItem id="biblioteca" itemLabel="Biblioteca" icon="/img/icones/biblioteca_menu.gif">
		
		<t:navigationMenuItem id="bib_cadServicosBiblioteca" itemLabel="Cadastrar para Utilizar os Serviços da Biblioteca" action="#{cadastroUsuarioBibliotecaMBean.iniciarAutoCadastro}" icon="/img/biblioteca/novo_usuario.gif" itemDisabled="false" />
		
		<t:navigationMenuItem id="bib_PesqMaterialAcervo" action="#{ pesquisaInternaBibliotecaMBean.iniciarBusca }" itemLabel="Pesquisar Material no Acervo" split="true"/>
		<t:navigationMenuItem id="bib_PesqArtigoAcervo" action="#{ pesquisaInternaArtigosBibliotecaMBean.iniciarBuscaArtigo }" itemLabel="Pesquisar Artigo no Acervo" />
			
		<t:navigationMenuItem id="bib_emprestimos" itemLabel="Empréstimos" split="true">
			<t:navigationMenuItem id="bib_renovarEmprestimos" itemLabel="Renovar Meus Empréstimos" action="#{meusEmprestimosBibliotecaMBean.iniciarVisualizarEmprestimosRenovaveis}" itemDisabled="false" />
			<t:navigationMenuItem id="bib_historicoEmprestimos" itemLabel="Meu Histórico de Empréstimos" action="#{emiteHistoricoEmprestimosMBean.iniciaUsuarioLogado}" itemDisabled="false" />
			<t:navigationMenuItem id="bib_Emitir_GRU" itemLabel="Imprimir GRU para pagamentos de multas" action="#{emitirGRUPagamentoMultasBibliotecaMBean.listarMinhasMultasAtivas}" rendered="#{emitirGRUPagamentoMultasBibliotecaMBean.sistemaTrabalhaComMultas}" itemDisabled="false" />
		</t:navigationMenuItem>
		
		<t:navigationMenuItem id="bib_dessiminacao_informacao" itemLabel="Disseminação Seletiva da Informação" split="true">
			<t:navigationMenuItem id="bib_cadastrar_interesse" itemLabel="Cadastrar Interesse" action="#{configuraPerfilInteresseUsuarioBibliotecaMBean.iniciar}" itemDisabled="false" />
		</t:navigationMenuItem>
		
		
		<t:navigationMenuItem id="bib_verificarSituacao" itemLabel="Verificar minha Situação / Emitir Documento de Quitação" action="#{verificaSituacaoUsuarioBibliotecaMBean.verificaSituacaoUsuarioAtualmenteLogado}" itemDisabled="false" />
		
		<t:navigationMenuItem id="bib_informacoes_usuario" itemLabel="Informações ao Usuário">
			<t:navigationMenuItem id="bib_visualizarVinculos" itemLabel="Visualizar meus Vínculos no Sistema" action="#{verificaVinculosUsuarioBibliotecaMBean.iniciarVerificacaoMeusVinculos}" itemDisabled="false" />
			<t:navigationMenuItem id="bib_Visualizar_Politicas" itemLabel="Visualizar as Políticas de Empréstimo" action="#{visualizarPoliticasDeEmprestimoMBean.iniciarVisualizacao}" itemDisabled="false" />
		</t:navigationMenuItem>
		
		<t:navigationMenuItem id="bib_revervas" itemLabel="Reservas de Materiais" split="true" rendered="#{solicitarReservaMaterialBibliotecaMBean.sistemaTrabalhaComReservas}"> 
			<t:navigationMenuItem id="bib_revervas_visualizar" itemLabel="Visualizar Minhas Reservas" action="#{visualizarReservasMaterialBibliotecaMBean.iniciaVisualizacaoMinhasReservas}" itemDisabled="false"  />
			<t:navigationMenuItem id="bib_revervas_solicitar" itemLabel="Solicitar Nova Reserva" action="#{solicitarReservaMaterialBibliotecaMBean.iniciarReservaPeloUsuario}" itemDisabled="false"  />
		</t:navigationMenuItem>
		
		<%-- Ainda não está em produção --%>
		<t:navigationMenuItem id="bib_solicitacoes" itemLabel="Serviços ao Usuário">
			<t:navigationMenuItem id="bib_SolOrientacao" itemLabel="Agendamento de Orientação" action="#{solicitacaoOrientacaoMBean.verMinhasSolicitacoes}" rendered="#{solicitacaoOrientacaoMBean.quantidadeBibliotecasRealizandoOrientacaoNormalizacao > 0}" />
	        <t:navigationMenuItem id="bib_SolNormalizacao" itemLabel="Normalização" action="#{ solicitacaoServicoDocumentoMBean.verMinhasSolicitacoes }" rendered="#{solicitacaoServicoDocumentoMBean.quantidadeBibliotecasRealizandoNormalizacao > 0}" />
	        <t:navigationMenuItem id="bib_SolCatalocacao" itemLabel="Catalogação na Fonte" action="#{ solicitacaoServicoDocumentoMBean.verMinhasSolicitacoes }" rendered="#{solicitacaoServicoDocumentoMBean.quantidadeBibliotecasRealizandoCatalogacaoNaFonte > 0}" />	    </t:navigationMenuItem>
         
        
        <t:navigationMenuItem id="bib_compras" itemLabel="Compras de Livro">
        	<t:navigationMenuItem id="bib_SolCompraLivro" actionListener="#{ menuDiscente.redirecionar }" itemLabel="Solicitar Compra de Livros" itemValue="/entrarSistema.do?sistema=sipac&url=listaReqBiblioteca.do?acao=474" rendered="#{usuario.discenteAtivo.graduacao or usuario.discenteAtivo.stricto}"/>
			<t:navigationMenuItem id="bib_AcomCompraLivro" actionListener="#{ menuDiscente.redirecionar }" itemLabel="Acompanhar Solicitações de Compra de Livros" itemValue="/entrarSistema.do?sistema=sipac&url=manipulaReqBiblioteca.do?acao=480" rendered="#{usuario.discenteAtivo.graduacao or usuario.discenteAtivo.stricto}"  />					
		</t:navigationMenuItem>
		
	</t:navigationMenuItem>	



	
	<%-- ACESSO AOS SERVIÇOS DE BOLSAS PARA O DISCENTE --%>
	<%-- Se o aluno for de GRADUAÇÃO, STRICTO, LATO OU TÉCNICO, estando o mesmo ATIVO e REGULAR é para aparecer essa opção no menu --%>
	
	<t:navigationMenuItem id="bolsas" itemLabel="Bolsas" icon="/img/bolsas.png" 
		rendered="#{(usuario.discenteAtivo.graduacao || usuario.discenteAtivo.tecnico || usuario.discenteAtivo.stricto 
			|| usuario.discenteAtivo.lato ) && usuario.discente.regular }">
	
		<t:navigationMenuItem id="bolsa_cadastroUnico" itemLabel="Aderir ao Cadastro Único" action="#{ adesaoCadastroUnico.apresentacaoCadastroUnico }"	/>	
			
		<t:navigationMenuItem id="bolsa_oportunidades" itemLabel="Oportunidades de Bolsa" action="#{ agregadorBolsas.iniciarBuscar }" split="true" />
			
		<t:navigationMenuItem id="bolsa_registrosInteresse" itemLabel="Acompanhar Meus Registros de Interesse" action="#{interessadoBolsa.acompanharInscricoes}" />
		
		<t:navigationMenuItem id="bolsa_minhasBolsasInstituicao" itemLabel="Minhas Bolsas na Instituição" action="#{ relatorioBolsasDiscenteBean.listarBolsasInstituicao }" />

		<t:navigationMenuItem id="solicitacao" itemLabel="Solicitação de Bolsas">
			<t:navigationMenuItem id="bolsa_auxilioAlimentacao" itemLabel="Solicitação de Bolsa Auxílio" action="#{ bolsaAuxilioMBean.iniciarSolicitacaoBolsaAuxilio }" />
			<t:navigationMenuItem id="bolsa_acompanharAuxilios" itemLabel="Acompanhar Solicitação de Bolsa Auxílio" action="#{ bolsaAuxilioMBean.acompanharSituacaoBolsaAuxilio }" />
		</t:navigationMenuItem>	
			
			<t:navigationMenuItem id="bolsa_planoDocenciaAssistida" itemLabel="Plano de Docência Assistida" action="#{ planoDocenciaAssistidaMBean.iniciar }" split="true"
				rendered="#{ usuario.discenteAtivo.stricto}"  />
				
	</t:navigationMenuItem>	

	<%-- ESTÁGIOS --%>
	<t:navigationMenuItem id="estagio" itemLabel="Estágio" icon="/img/estagio/estagio_menu.png" rendered="#{usuario.discenteAtivo.graduacao}">
		<t:navigationMenuItem id="muralVagasEstagio" itemLabel="Mural de Vagas" action="#{ ofertaEstagioMBean.listarOfertasDisponiveis }"/>
		<t:navigationMenuItem id="gerenciarEstagio" itemLabel="Gerenciar Estágios" action="#{buscaEstagioMBean.iniciar}" split="true"/>
	</t:navigationMenuItem>			
	
	<%-- AMBIENTES VIRTUAIS --%>
	<t:navigationMenuItem id="ambientesVirtuais" itemLabel="Ambientes Virtuais" icon="/img/icones/amb_virt.png">
		<t:navigationMenuItem itemLabel="Comunidades Virtuais" id="comunidadeVirtual">
			<t:navigationMenuItem id="comunidadeVirtual_BuscarComunidade" actionListener="#{ buscarComunidadeVirtualMBean.criar }" itemLabel="Buscar Comunidades Virtuais"/>
			<t:navigationMenuItem id="comunidadeVirtual_MinhasComunidades" action="#{ buscarComunidadeVirtualMBean.exibirTodasComunidadesDocente }" itemLabel="Minhas Comunidades"/>		
		</t:navigationMenuItem>
	</t:navigationMenuItem>	
	
	<t:navigationMenuItem id="menuOutros" itemLabel="Outros" icon="/img/menu/outros.png">
	
		<c:if test="${usuario.discenteAtivo.graduacao}">
			<t:navigationMenuItem id="avisarFaltaDocente" itemLabel="Avisar Ausência do Professor" action="#{ avisoFalta.iniciar }" />
		</c:if>

		<t:navigationMenuItem id="coordenacaoCurso" itemLabel="Coordenação de Curso" >
			<c:if test="${usuario.discenteAtivo.graduacao or usuario.discenteAtivo.stricto}">
				<t:navigationMenuItem id="atendimentoAluno" itemLabel="Atendimento ao Aluno" action="#{ atendimentoAluno.novaPergunta }"/>
			</c:if>
			<t:navigationMenuItem id="forumCursos" itemLabel="Fórum de Cursos" action="#{ forum.listarForunsCurso }" itemDisabled="false"/>
			<c:if test="${usuario.discente.curso != null }">
				<t:navigationMenuItem id="paginaCurso" value="#{ menuDiscente.paginaCurso }" itemDisabled="false"/>
			</c:if>
		</t:navigationMenuItem>
		
		<t:navigationMenuItem id="ouvidoria" itemLabel="Ouvidoria">
			<t:navigationMenuItem id="entrarEmContato" itemLabel="Entrar em Contato" action="#{ manifestacaoDiscente.preCadastrar }" itemDisabled="false"/>
			<t:navigationMenuItem id="acompanharManifestacoes" itemLabel="Acompanhar Manifestações" action="#{ analiseManifestacaoDiscente.listarManifestacoes }" itemDisabled="false"/>
		</t:navigationMenuItem>
		
		<%--
		<c:if test="${usuario.discenteAtivo.graduacao}">
			<t:navigationMenuItem id="requerimentoPadrao" itemLabel="Requerimento Padrão" >
				<t:navigationMenuItem id="criarRequerimentoPadrao" itemLabel="Criar Requerimento Padrão" action="#{ requerimento.requerimentoPadrao }" itemDisabled="false"/>
				<t:navigationMenuItem id="exibirMeusRequerimentos" itemLabel="Exibir Meus Requerimentos" action="#{ requerimento.exibirRequerimento }" itemDisabled="false"/>
			</t:navigationMenuItem>		
		</c:if>
 		--%>
 		
		<t:navigationMenuItem id="producoesIntelectuais" itemLabel="Produções Intelectuais">
			<t:navigationMenuItem id="acervoDocentes" itemLabel="Acervo dos Docentes" action="#{acervoProducao.verAcervoDigital}" />
			<t:navigationMenuItem id="defesasPosGraduacao" itemLabel="Consultar Defesas de Pós-graduação" action="#{consultarDefesaMBean.iniciar}" />
		</t:navigationMenuItem>

		<t:navigationMenuItem itemLabel="Fiscal do Vestibular" id="fiscal" icon="/img/icones/producao_menu.gif" rendered="#{usuario.discenteAtivo.graduacao || usuario.discenteAtivo.stricto}">
			<t:navigationMenuItem id="fiscal_inscricao" itemLabel="Inscrição" action="#{inscricaoSelecaoFiscalVestibular.iniciarInscricaoFiscal}"/>
			<t:navigationMenuItem id="fiscal_resultadoSelecao" itemLabel="Resultado da Seleção" action="#{inscricaoSelecaoFiscalVestibular.exibeResultadoDoProcessamento}"/>
			<t:navigationMenuItem id="fiscal_locaisProva" itemLabel="Lista de Locais de Aplicação de Prova" action="#{relatoriosVestibular.iniciarListaLocaisProva}"/>
			<t:navigationMenuItem id="fiscal_comprovanteInscricao" itemLabel="Comprovante de Inscrição" action="#{inscricaoSelecaoFiscalVestibular.exibeComprovanteInscricao}"/>
			<t:navigationMenuItem id="fiscal_justificativaAusencia" itemLabel="Justificativa de Ausência" action="#{justificativaAusencia.preCadastrar}"/>
		</t:navigationMenuItem>
		
		<t:navigationMenuItem itemLabel="Auxílio Financeiro"  actionListener="#{ menuDiscente.redirecionar }" itemValue="/entrarSistema.do?sistema=sipac&url=portal_aluno/index.jsf?voltar=/sigaa/verPortalDiscente" />
		
		<%-- 
		<t:navigationMenuItem id="senhaAcessoCelular" itemLabel="Criar senha de acesso por Celular" action="#{ senhaMobileMBean.iniciar }" icon="/img/celular_icone.gif" split="true"/>
		--%>
		<c:if test="${usuario.discenteAtivo.graduacao or usuario.discenteAtivo.stricto}">
			<t:navigationMenuItem id="consultarProcessos" itemLabel="Consultar Processos do Aluno" action="#{ consultaProcesso.iniciar }" />
		</c:if>
			
		<t:navigationMenuItem
				id="cartaoRestaurante" itemLabel="Saldo do Cartão do Restaurante" icon="/img/icones/restaurante_small.gif"
				actionListener="#{menuDiscente.redirecionar}"
				itemValue="/entrarSistema.do?sistema=sipac&url=restaurante/vendas/saldo_cartao.jsf?voltar=/sigaa/verPortalDiscente" />
		
	</t:navigationMenuItem>

</t:jscookMenu>

</h:form>

</div>
</div>
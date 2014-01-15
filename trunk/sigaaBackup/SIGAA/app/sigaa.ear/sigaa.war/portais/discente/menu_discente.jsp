<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>

<%-- MENU DE OP��ES PARA O DISCENTE --%>

<div id="menu-dropdown">
<div class="wrapper">

<h:form id="form_menu_discente">

<input type="hidden" name="id" value="${ usuario.discenteAtivo.id }"/>
<input type="hidden" name="jscook_action"/>

<t:jscookMenu layout="hbr" theme="ThemeOffice" styleLocation="/css/jscookmenu">

	<t:navigationMenuItem itemLabel="Ensino" id="ensino" icon="/img/icones/ensino_menu.gif">
		
		<t:navigationMenuItem id="avaliacaoInstitucional" itemLabel="Avalia��o Institucional" rendered="#{ acesso.usuario.discenteAtivo.nivelStr == 'G' && acesso.usuario.discenteAtivo.regular }">
			<t:navigationMenuItem id="listaAvaliacoesInstitucionais" itemLabel="Preencher a Avalia��o Institucional" action="#{ calendarioAvaliacaoInstitucionalBean.listar }" itemDisabled="false" rendered="#{  acesso.usuario.discenteAtivo.nivelStr == 'G' }"/>
			<t:navigationMenuItem id="reverAvaliacaoAnterior" itemLabel="Rever a Avalia��o Anterior" action="#{ avaliacaoInstitucionalAnterior.listaAnterior }" itemDisabled="false" rendered="#{ acesso.usuario.discenteAtivo.nivelStr == 'G' && empty acesso.usuario.discenteAtivo.polo }" split="true" />
			<t:navigationMenuItem id="resultadoAvaliacao" itemLabel="Consultar o Resultado da Avalia��o" action="#{ relatorioAvaliacaoMBean.iniciarConsultaPublica }" itemDisabled="false" rendered="#{ acesso.usuario.discenteAtivo.nivelStr == 'G' && empty acesso.usuario.discenteAtivo.polo }"/>
			<t:navigationMenuItem id="observacaoDocente" itemLabel="Observa��es dos Docentes Sobre Minhas Turmas" action="#{ relatorioAvaliacaoMBean.iniciarObservacoesTurmasDiscente }" itemDisabled="false" rendered="#{  acesso.usuario.discenteAtivo.nivelStr == 'G' && empty acesso.usuario.discenteAtivo.polo  }"/>
		</t:navigationMenuItem>

		<t:navigationMenuItem id="minhasNotas" itemLabel="Minhas Notas" action="#{ relatorioNotasAluno.gerarRelatorio }" itemDisabled="false" icon="/img/celular_icone.gif"/>
		<t:navigationMenuItem id="atestadoMatricula" itemLabel="Atestado de Matr�cula" action="#{ portalDiscente.atestadoMatricula }" itemDisabled="false" icon="/img/celular_icone.gif"/>
		<t:navigationMenuItem id="consultarHistorico" itemLabel="Consultar Hist�rico" action="#{ portalDiscente.historico }" itemDisabled="false"/>
		<t:navigationMenuItem id="consultarIndicesAcademicos" itemLabel="Consultar �ndices Acad�micos" action="#{ indiceAcademicoMBean.selecionaDiscente }" rendered="#{portalDiscente.passivelEmissaoRelatorioIndices and !portalDiscente.modoReduzido}"/>
		<t:navigationMenuItem id="consultarIndicesAcademicosInativo" itemLabel="Consultar �ndices Acad�micos" action="#{ indiceAcademicoMBean.relatorioInativo }" rendered="#{portalDiscente.passivelEmissaoRelatorioIndices and portalDiscente.modoReduzido}"/>
		<t:navigationMenuItem id="declaracaoVinculo" itemLabel="Declara��o de V�nculo" action="#{ declaracaoVinculo.emitirDeclaracao}" itemDisabled="false" rendered="#{ !usuario.discenteAtivo.residencia && !usuario.discenteAtivo.formacaoComplementar}"/>
		
		<t:navigationMenuItem itemLabel="Termo de Autoriza��o para Publica��o de Teses e Disserta��es - TEDE" action="#{ termoPublicacaoTD.iniciarDiscente }" icon="/img/graduacao/coordenador/documento.png"  rendered="#{usuario.discenteAtivo.stricto}" />
		
		<t:navigationMenuItem itemLabel="Destrancar Curso" action="#{ destrancarPrograma.iniciar}"  rendered="false" />		
		
		<c:if test="${ usuario.discenteAtivo.discenteEad }">
			<t:navigationMenuItem id="verFichaAvaliacao" itemLabel="Ver Fichas de Avalia��o" action="#{ fichaAvaliacaoEad.verFichaDiscente }" itemDisabled="false"/>
			<t:navigationMenuItem id="definirHorarioTutoriaPresencial" itemLabel="Definir hor�rio de tutoria presencial" action="#{ matriculaGraduacao.selecionarTutoriaAluno }" itemDisabled="false"/>
		</c:if>
		
		<%-- 
		<c:if test="${!usuario.discenteAtivo.discenteEad && !usuario.discenteAtivo.residencia && !usuario.discenteAtivo.formacaoComplementar}">
		--%>
	 	<t:navigationMenuItem id="matriculaOnlineFormacaoComplementar" itemLabel="Matr�cula On-Line" split="true" rendered="#{usuario.discenteAtivo.formacaoComplementar}">
	 		<t:navigationMenuItem id="realizarMatriculaFormacaoComplementar" itemLabel="Realizar Matr�cula" action="#{matriculaFormacaoComplementarMBean.iniciar}" rendered="#{usuario.discenteAtivo.formacaoComplementar}" />
	 	</t:navigationMenuItem>
	 	<t:navigationMenuItem id="matriculaOnlineMetropoleDigital" itemLabel="Matr�cula On-Line" split="true" rendered="#{usuario.discenteAtivo.metropoleDigital}">
	 		<t:navigationMenuItem id="realizarmatriculaMetropoleDigital" itemLabel="Realizar Matr�cula no M�dulo B�sico" action="#{ matriculaGraduacao.telaInstrucoes}" rendered="#{usuario.discenteAtivo.metropoleDigital}" />
	 		<t:navigationMenuItem id="realizarmatriculaModuloAvancadoMetropoleDigital" itemLabel="Realizar Matr�cula no M�dulo Avan�ado" action="#{ matriculaModuloAvancadoMBean.iniciarDiscente}" rendered="#{usuario.discenteAtivo.metropoleDigital}" />
	 		<t:navigationMenuItem id="comprovanteMatriculaMetropoleDigital" itemLabel="Ver Comprovante de Matr�cula" action="#{ matriculaGraduacao.verComprovanteSolicitacoes}" split="true"/>
	 	</t:navigationMenuItem>
		<c:if test="${!usuario.discenteAtivo.lato && !usuario.discenteAtivo.residencia && !usuario.discenteAtivo.formacaoComplementar && !usuario.discenteAtivo.discente.cancelado && !usuario.discenteAtivo.metropoleDigital}">
		 	<t:navigationMenuItem id="matriculaOnline" itemLabel="Matr�cula On-Line" split="true">
		 		<t:navigationMenuItem id="realizarmatricula" itemLabel="Realizar Matr�cula" action="#{ matriculaGraduacao.telaInstrucoes}" rendered="#{!usuario.discenteAtivo.stricto}" />
		 		<t:navigationMenuItem id="realizarmatriculaStricto" itemLabel="Realizar Matr�cula" action="#{ matriculaStrictoBean.iniciar}" rendered="#{usuario.discenteAtivo.stricto}" />
		 		<t:navigationMenuItem id="realizarMatriculaFerias" itemLabel="Realizar Matr�cula em Turma de F�rias" action="#{ confirmacaoMatriculaFeriasBean.iniciar}" rendered="#{!usuario.discenteAtivo.stricto}" />		 			 	
		 		<t:navigationMenuItem id="realizarMatriculaExtraordinaria" itemLabel="Realizar Matr�cula Extraordin�ria" action="#{ matriculaExtraordinaria.iniciar}" rendered="#{!usuario.discenteAtivo.stricto && !usuario.discenteAtivo.discenteEad}" />
		 		<t:navigationMenuItem id="realizarMatriculaFeriasExtraordinaria" itemLabel="Realizar Matr�cula Extraordin�ria em Turma de F�rias" action="#{ matriculaExtraordinaria.iniciarFerias}" rendered="#{!usuario.discenteAtivo.stricto && !usuario.discenteAtivo.discenteEad}"/>
		 		<t:navigationMenuItem id="comprovanteMatricula" itemLabel="Ver Comprovante de Matr�cula" action="#{ matriculaGraduacao.verComprovanteSolicitacoes}" split="true"/>
				<t:navigationMenuItem id="comprovanteMatriculaTurmaFerias" itemLabel="Ver Comprovante de Matr�cula para Turmas de F�rias" action="#{ portalDiscente.atestadoMatriculaTurmaFerias}" rendered="#{!usuario.discenteAtivo.stricto && !usuario.discenteAtivo.discenteEad}" />		 		
				<t:navigationMenuItem id="orientacoesMatricula" itemLabel="Ver Orienta��es de Matr�cula" action="#{ matriculaGraduacao.acompanharSolicitacoes}"  />
		 		<t:navigationMenuItem id="resultadoProcessamento" itemLabel="Ver Resultado do Processamento" action="#{ matriculaGraduacao.verComprovanteSolicitacoes}"  />
		 		<t:navigationMenuItem id="planoMatriculas" itemLabel="Meu Plano de Matr�culas" action="#{ planoMatriculaBean.gerar }" split="true" rendered="#{usuario.discenteAtivo.graduacao}" />
		 	</t:navigationMenuItem >
			<c:if test="${usuario.discenteAtivo.graduacao and !usuario.discenteAtivo.discenteEad}">
				<t:navigationMenuItem  itemLabel="Solicita��es de Ensino Individual" id="solicitacoesEnsinoIndividual">
					<t:navigationMenuItem id="solicitarEnsinoIndividual" itemLabel="Solicitar Ensino Individual" action="#{ solicitacaoEnsinoIndividual.iniciarEnsinoIndividualizado }" itemDisabled="false" />
					<t:navigationMenuItem id="solicitacoesEnviadas" itemLabel="Visualizar Solicita��es Enviadas" action="#{ solicitacaoEnsinoIndividual.listarEnsinoIndividual }" itemDisabled="false"/>
					<t:navigationMenuItem id="comprovanteSolicitacoes" itemLabel="Emitir Comprovante de Solicita��es" action="#{ solicitacaoEnsinoIndividual.emitirComprovanteEnsinoIndividual }" itemDisabled="false"/>
				</t:navigationMenuItem>
				<t:navigationMenuItem itemLabel="Solicita��es de Turma de F�rias" id="solicitacoesTurmasFerias">
					<t:navigationMenuItem id="solicitarTurmaFerias" itemLabel="Solicitar Turma de F�rias" action="#{ solicitacaoEnsinoIndividual.iniciarFerias }" itemDisabled="false" />
					<t:navigationMenuItem id="solicitacoesFeriasEnviadas" itemLabel="Visualizar Solicita��es Enviadas" action="#{ solicitacaoEnsinoIndividual.listarFerias}" itemDisabled="false"/>
					<t:navigationMenuItem id="comprovanteSolicitacoesFerias" itemLabel="Emitir Comprovante de Solicita��es" action="#{ solicitacaoEnsinoIndividual.emitirComprovanteFerias }" itemDisabled="false"/>
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
					<t:navigationMenuItem id="solicitarTrancamento" itemLabel="Iniciar Solicita��o de Trancamento Regular" action="#{ trancamentoPrograma.iniciarTrancamentoRegular }"/>
					<t:navigationMenuItem id="solicitarTrancamentoPosteriori" itemLabel="Solicitar Trancamento a Posteriori" action="#{ trancamentoPrograma.iniciarTrancamentoPosteriori }"/>
					<t:navigationMenuItem id="exibirSolicitacoesTrancamento" itemLabel="Exibir Solicita��es de Trancamento" action="#{ trancamentoPrograma.exibirMinhasSolicitacoes }"/>
				</t:navigationMenuItem>
			</c:if>
		</c:if>
		
		<c:if test="${usuario.discenteAtivo.graduacao and !usuario.discenteAtivo.discenteEad}">
			<t:navigationMenuItem itemLabel="Reposi��o de Avalia��o" id="reposicaoProva" split="true">
				<t:navigationMenuItem id="solicitacoesReposicaoProva" itemLabel="Solicitar Reposi��o de Avalia��o" action="#{ solicitacaoReposicaoProva.iniciar }"/>
				<t:navigationMenuItem id="exibirSolicitacoes" itemLabel="Exibir Solicita��es" action="#{ solicitacaoReposicaoProva.listarSolicitacoes }"/>
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
		<t:navigationMenuItem id="unidadesAcademicas" itemLabel="Unidades Acad�micas" action="#{ unidade.popularBuscaGeral }" itemDisabled="false"/>
		<t:navigationMenuItem id="consultarCalendarioAcademico" itemLabel="Consultar Calend�rio Acad�mico" action="#{calendario.iniciarBusca}" itemDisabled="false" split="true"/>
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
			<t:navigationMenuItem id="report_iniciacaoCientifica" itemLabel="Relat�rios de Inicia��o Cient�fica">
				<t:navigationMenuItem id="relatoriosParciais" itemLabel="Relat�rios Parciais">
					<t:navigationMenuItem id="report_enviarRelatorioBolsaParcial" itemLabel="Enviar" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/relatorioBolsaParcial.do?dispatch=listarPlanos"/>
					<t:navigationMenuItem id="report_consultarRelatorioBolsaParcial" itemLabel="Consultar" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/relatorioBolsaParcial.do?dispatch=listarRelatorios"/>
				</t:navigationMenuItem>
				<t:navigationMenuItem id="relatoriosFinais" itemLabel="Relat�rios Finais">
					<t:navigationMenuItem id="report_enviarRelatorioBolsaFinal" itemLabel="Enviar" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/relatorioBolsaFinal.do?dispatch=listarPlanos"/>
					<t:navigationMenuItem id="report_consultarRelatorioBolsaFinal" itemLabel="Consultar" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/relatorioBolsaFinal.do?dispatch=listarRelatorios"/>
				</t:navigationMenuItem>
			</t:navigationMenuItem>
			<t:navigationMenuItem id="certificadosDeclaracoes" itemLabel="Certificados e Declara��es">
				<t:navigationMenuItem id="declaracoesBolsista" itemLabel="Declara��o de Bolsista em Plano de Trabalho" action="#{declaracoesPesquisa.listarDeclararoesPlanoTrabalhoBolsista}" />				
			</t:navigationMenuItem>
		</c:if>
		<t:navigationMenuItem itemLabel="Congresso de Inicia��o Cient�fica">
			<t:navigationMenuItem id="submeterResumoCongresso" itemLabel="Submeter resumo" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/resumoCongresso.do?dispatch=popularInicioEnvio" rendered="#{(usuario.discenteAtivo.graduacao || usuario.discenteAtivo.tecnico)}"/>
			<t:navigationMenuItem itemLabel="Meus resumos" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/resumoCongresso.do?dispatch=listarResumosAutor"/>
		</t:navigationMenuItem>
	</t:navigationMenuItem>

	<%-- EXTENS�O --%>
	<t:navigationMenuItem itemLabel="Extens�o" id="extensao" icon="/img/icones/extensao_menu.gif"
		rendered="#{(usuario.discenteAtivo.graduacao || usuario.discenteAtivo.tecnico || usuario.discenteAtivo.stricto)}">
		
			<t:navigationMenuItem id="consultarAcoesExtensao" itemLabel="Consultar A��es" action="#{atividadeExtensao.preLocalizar}" itemDisabled="false" />
			<t:navigationMenuItem id="meusPlanosTrabalhoExtensao" itemLabel="Meus Planos de Trabalho" action="#{ planoTrabalhoExtensao.carregarPlanosTrabalhoDiscenteLogado }" split="true"/>
			<t:navigationMenuItem id="acoesMembroEquipeExtensao" itemLabel="Minhas A��es como Membro da Equipe" action="#{ atividadeExtensao.carregarAcoesDiscenteLogado }"/>			
			<t:navigationMenuItem id="relatoriosExtensao" itemLabel="Meus Relat�rios" action="#{ relatorioBolsistaExtensao.iniciarCadastroRelatorio }"/>
			<t:navigationMenuItem id="certificadosDeclaracoesExtensao" itemLabel="Certificados e Declara��es" action="#{documentosAutenticadosExtensao.participacoesDiscenteUsuarioLogado}"/>
			<t:navigationMenuItem id="inscreverAcaoExtensao" itemLabel="Inscri��o On-line em A��es de Extens�o" actionListener="#{menuDiscente.redirecionar}" itemValue="/sigaa/public/extensao/paginaListaPeriodosInscricoesAtividadesPublico.jsf?aba=p-extensao" itemDisabled="false"/>
			<t:navigationMenuItem id="visualizarResultadosExtensao" itemLabel="Visualizar Resultados das inscri��es" action="#{selecaoDiscenteExtensao.iniciarVisualizarResultados}" itemDisabled="false"/>

	</t:navigationMenuItem>
	
	<%-- MONITORIA --%>
	<t:navigationMenuItem itemLabel="Monitoria" id="monitoria" icon="/img/icones/monitoria_menu.gif"
		rendered="#{(usuario.discenteAtivo.graduacao)}">
	
		<t:navigationMenuItem id="consultarProjetosMonitoria" itemLabel="Consultar Projetos" action="#{coordMonitoria.situacaoProjeto}" />
		<t:navigationMenuItem id="projetosMonitoria" itemLabel="Meus Projetos de Monitoria" actionListener="#{menuDiscente.redirecionar}" itemValue="/monitoria/DiscenteMonitoria/meus_projetos.jsf" itemDisabled="false" split="true"/>
		<t:navigationMenuItem id="relatoriosMonitoria" itemLabel="Meus Relat�rios" action="#{ relatorioMonitor.listar }"/>
		<t:navigationMenuItem id="certificadosMonitoria" itemLabel="Meus Certificados">
			<t:navigationMenuItem id="certificadosProjetosMonitoria" itemLabel="Certificados de Projetos" action="#{documentosAutenticadosMonitoria.participacoesDiscenteUsuarioLogado}"/>
			<t:navigationMenuItem id="certificadosSID_Monitoria" itemLabel="Certificados do SID" action="#{resumoSid.listarParticipacoesDiscente}" itemDisabled="false" />
		</t:navigationMenuItem>			
		<t:navigationMenuItem id="atividadesMesFrequenciaMonitoria" itemLabel="Atividades do M�s / Freq��ncia" rendered="#{monitoria.frequenciaMonitoria}">
			<t:navigationMenuItem id="cadastrarAtividadesMonitoria" itemLabel="Cadastrar" actionListener="#{menuDiscente.redirecionar}" itemValue="/monitoria/DiscenteMonitoria/meus_projetos.jsf" itemDisabled="false"/>
			<t:navigationMenuItem id="consultarAtividadesMonitoria" itemLabel="Consultar" action="#{atividadeMonitor.listarAtividades}" itemDisabled="false" split="true"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem id="inscreverSelecaoMonitoria" itemLabel="Inscrever-se em Sele��o de Monitoria" action="#{agregadorBolsas.iniciarBuscar}" itemDisabled="false"/>
		<t:navigationMenuItem id="resultadoSelecaoMonitoria" itemLabel="Visualizar Resultado da Sele��o" action="#{discenteMonitoria.popularVisualizarResultados}" itemDisabled="false"/>

	</t:navigationMenuItem>
	
	
    <%-- A��O ACAD�MICA ASSOCIADA --%>
    <t:navigationMenuItem itemLabel="A��es Associadas" id="acoesAssociadas" icon="/img/projetos/associado_menu.png"
      		rendered="#{(usuario.discenteAtivo.graduacao || usuario.discenteAtivo.tecnico || usuario.discenteAtivo.stricto || usuario.discenteAtivo.lato)}">

			<t:navigationMenuItem id="consultarAcoesAssociadas" itemLabel="Consultar A��es Associadas" action="#{ buscaAcaoAssociada.iniciar }" itemDisabled="false" />
			<t:navigationMenuItem id="meusPlanosTrabalhoAssociadas" itemLabel="Meus Planos de Trabalho" action="#{ planoTrabalhoProjeto.planosDiscente }" split="true"/>
			<t:navigationMenuItem id="certificadoDeclaracao" itemLabel="Certificados e Declarac�es" action="#{ declaracaoMembroProjIntegradoMBean.participacoesDiscenteUsuarioLogado }" split="true"/>

    </t:navigationMenuItem> 	
	

	<%-- ACESSO AOS SERVI�OS DA BIBLIOTECA PARA O DISCENTE --%>

	<%-- o aluno estando ativo � para aparecer essa op��o no menu --%>
	<t:navigationMenuItem id="biblioteca" itemLabel="Biblioteca" icon="/img/icones/biblioteca_menu.gif">
		
		<t:navigationMenuItem id="bib_cadServicosBiblioteca" itemLabel="Cadastrar para Utilizar os Servi�os da Biblioteca" action="#{cadastroUsuarioBibliotecaMBean.iniciarAutoCadastro}" icon="/img/biblioteca/novo_usuario.gif" itemDisabled="false" />
		
		<t:navigationMenuItem id="bib_PesqMaterialAcervo" action="#{ pesquisaInternaBibliotecaMBean.iniciarBusca }" itemLabel="Pesquisar Material no Acervo" split="true"/>
		<t:navigationMenuItem id="bib_PesqArtigoAcervo" action="#{ pesquisaInternaArtigosBibliotecaMBean.iniciarBuscaArtigo }" itemLabel="Pesquisar Artigo no Acervo" />
			
		<t:navigationMenuItem id="bib_emprestimos" itemLabel="Empr�stimos" split="true">
			<t:navigationMenuItem id="bib_renovarEmprestimos" itemLabel="Renovar Meus Empr�stimos" action="#{meusEmprestimosBibliotecaMBean.iniciarVisualizarEmprestimosRenovaveis}" itemDisabled="false" />
			<t:navigationMenuItem id="bib_historicoEmprestimos" itemLabel="Meu Hist�rico de Empr�stimos" action="#{emiteHistoricoEmprestimosMBean.iniciaUsuarioLogado}" itemDisabled="false" />
			<t:navigationMenuItem id="bib_Emitir_GRU" itemLabel="Imprimir GRU para pagamentos de multas" action="#{emitirGRUPagamentoMultasBibliotecaMBean.listarMinhasMultasAtivas}" rendered="#{emitirGRUPagamentoMultasBibliotecaMBean.sistemaTrabalhaComMultas}" itemDisabled="false" />
		</t:navigationMenuItem>
		
		<t:navigationMenuItem id="bib_dessiminacao_informacao" itemLabel="Dissemina��o Seletiva da Informa��o" split="true">
			<t:navigationMenuItem id="bib_cadastrar_interesse" itemLabel="Cadastrar Interesse" action="#{configuraPerfilInteresseUsuarioBibliotecaMBean.iniciar}" itemDisabled="false" />
		</t:navigationMenuItem>
		
		
		<t:navigationMenuItem id="bib_verificarSituacao" itemLabel="Verificar minha Situa��o / Emitir Documento de Quita��o" action="#{verificaSituacaoUsuarioBibliotecaMBean.verificaSituacaoUsuarioAtualmenteLogado}" itemDisabled="false" />
		
		<t:navigationMenuItem id="bib_informacoes_usuario" itemLabel="Informa��es ao Usu�rio">
			<t:navigationMenuItem id="bib_visualizarVinculos" itemLabel="Visualizar meus V�nculos no Sistema" action="#{verificaVinculosUsuarioBibliotecaMBean.iniciarVerificacaoMeusVinculos}" itemDisabled="false" />
			<t:navigationMenuItem id="bib_Visualizar_Politicas" itemLabel="Visualizar as Pol�ticas de Empr�stimo" action="#{visualizarPoliticasDeEmprestimoMBean.iniciarVisualizacao}" itemDisabled="false" />
		</t:navigationMenuItem>
		
		<t:navigationMenuItem id="bib_revervas" itemLabel="Reservas de Materiais" split="true" rendered="#{solicitarReservaMaterialBibliotecaMBean.sistemaTrabalhaComReservas}"> 
			<t:navigationMenuItem id="bib_revervas_visualizar" itemLabel="Visualizar Minhas Reservas" action="#{visualizarReservasMaterialBibliotecaMBean.iniciaVisualizacaoMinhasReservas}" itemDisabled="false"  />
			<t:navigationMenuItem id="bib_revervas_solicitar" itemLabel="Solicitar Nova Reserva" action="#{solicitarReservaMaterialBibliotecaMBean.iniciarReservaPeloUsuario}" itemDisabled="false"  />
		</t:navigationMenuItem>
		
		<%-- Ainda n�o est� em produ��o --%>
		<t:navigationMenuItem id="bib_solicitacoes" itemLabel="Servi�os ao Usu�rio">
			<t:navigationMenuItem id="bib_SolOrientacao" itemLabel="Agendamento de Orienta��o" action="#{solicitacaoOrientacaoMBean.verMinhasSolicitacoes}" rendered="#{solicitacaoOrientacaoMBean.quantidadeBibliotecasRealizandoOrientacaoNormalizacao > 0}" />
	        <t:navigationMenuItem id="bib_SolNormalizacao" itemLabel="Normaliza��o" action="#{ solicitacaoServicoDocumentoMBean.verMinhasSolicitacoes }" rendered="#{solicitacaoServicoDocumentoMBean.quantidadeBibliotecasRealizandoNormalizacao > 0}" />
	        <t:navigationMenuItem id="bib_SolCatalocacao" itemLabel="Cataloga��o na Fonte" action="#{ solicitacaoServicoDocumentoMBean.verMinhasSolicitacoes }" rendered="#{solicitacaoServicoDocumentoMBean.quantidadeBibliotecasRealizandoCatalogacaoNaFonte > 0}" />	    </t:navigationMenuItem>
         
        
        <t:navigationMenuItem id="bib_compras" itemLabel="Compras de Livro">
        	<t:navigationMenuItem id="bib_SolCompraLivro" actionListener="#{ menuDiscente.redirecionar }" itemLabel="Solicitar Compra de Livros" itemValue="/entrarSistema.do?sistema=sipac&url=listaReqBiblioteca.do?acao=474" rendered="#{usuario.discenteAtivo.graduacao or usuario.discenteAtivo.stricto}"/>
			<t:navigationMenuItem id="bib_AcomCompraLivro" actionListener="#{ menuDiscente.redirecionar }" itemLabel="Acompanhar Solicita��es de Compra de Livros" itemValue="/entrarSistema.do?sistema=sipac&url=manipulaReqBiblioteca.do?acao=480" rendered="#{usuario.discenteAtivo.graduacao or usuario.discenteAtivo.stricto}"  />					
		</t:navigationMenuItem>
		
	</t:navigationMenuItem>	



	
	<%-- ACESSO AOS SERVI�OS DE BOLSAS PARA O DISCENTE --%>
	<%-- Se o aluno for de GRADUA��O, STRICTO, LATO OU T�CNICO, estando o mesmo ATIVO e REGULAR � para aparecer essa op��o no menu --%>
	
	<t:navigationMenuItem id="bolsas" itemLabel="Bolsas" icon="/img/bolsas.png" 
		rendered="#{(usuario.discenteAtivo.graduacao || usuario.discenteAtivo.tecnico || usuario.discenteAtivo.stricto 
			|| usuario.discenteAtivo.lato ) && usuario.discente.regular }">
	
		<t:navigationMenuItem id="bolsa_cadastroUnico" itemLabel="Aderir ao Cadastro �nico" action="#{ adesaoCadastroUnico.apresentacaoCadastroUnico }"	/>	
			
		<t:navigationMenuItem id="bolsa_oportunidades" itemLabel="Oportunidades de Bolsa" action="#{ agregadorBolsas.iniciarBuscar }" split="true" />
			
		<t:navigationMenuItem id="bolsa_registrosInteresse" itemLabel="Acompanhar Meus Registros de Interesse" action="#{interessadoBolsa.acompanharInscricoes}" />
		
		<t:navigationMenuItem id="bolsa_minhasBolsasInstituicao" itemLabel="Minhas Bolsas na Institui��o" action="#{ relatorioBolsasDiscenteBean.listarBolsasInstituicao }" />

		<t:navigationMenuItem id="solicitacao" itemLabel="Solicita��o de Bolsas">
			<t:navigationMenuItem id="bolsa_auxilioAlimentacao" itemLabel="Solicita��o de Bolsa Aux�lio" action="#{ bolsaAuxilioMBean.iniciarSolicitacaoBolsaAuxilio }" />
			<t:navigationMenuItem id="bolsa_acompanharAuxilios" itemLabel="Acompanhar Solicita��o de Bolsa Aux�lio" action="#{ bolsaAuxilioMBean.acompanharSituacaoBolsaAuxilio }" />
		</t:navigationMenuItem>	
			
			<t:navigationMenuItem id="bolsa_planoDocenciaAssistida" itemLabel="Plano de Doc�ncia Assistida" action="#{ planoDocenciaAssistidaMBean.iniciar }" split="true"
				rendered="#{ usuario.discenteAtivo.stricto}"  />
				
	</t:navigationMenuItem>	

	<%-- EST�GIOS --%>
	<t:navigationMenuItem id="estagio" itemLabel="Est�gio" icon="/img/estagio/estagio_menu.png" rendered="#{usuario.discenteAtivo.graduacao}">
		<t:navigationMenuItem id="muralVagasEstagio" itemLabel="Mural de Vagas" action="#{ ofertaEstagioMBean.listarOfertasDisponiveis }"/>
		<t:navigationMenuItem id="gerenciarEstagio" itemLabel="Gerenciar Est�gios" action="#{buscaEstagioMBean.iniciar}" split="true"/>
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
			<t:navigationMenuItem id="avisarFaltaDocente" itemLabel="Avisar Aus�ncia do Professor" action="#{ avisoFalta.iniciar }" />
		</c:if>

		<t:navigationMenuItem id="coordenacaoCurso" itemLabel="Coordena��o de Curso" >
			<c:if test="${usuario.discenteAtivo.graduacao or usuario.discenteAtivo.stricto}">
				<t:navigationMenuItem id="atendimentoAluno" itemLabel="Atendimento ao Aluno" action="#{ atendimentoAluno.novaPergunta }"/>
			</c:if>
			<t:navigationMenuItem id="forumCursos" itemLabel="F�rum de Cursos" action="#{ forum.listarForunsCurso }" itemDisabled="false"/>
			<c:if test="${usuario.discente.curso != null }">
				<t:navigationMenuItem id="paginaCurso" value="#{ menuDiscente.paginaCurso }" itemDisabled="false"/>
			</c:if>
		</t:navigationMenuItem>
		
		<t:navigationMenuItem id="ouvidoria" itemLabel="Ouvidoria">
			<t:navigationMenuItem id="entrarEmContato" itemLabel="Entrar em Contato" action="#{ manifestacaoDiscente.preCadastrar }" itemDisabled="false"/>
			<t:navigationMenuItem id="acompanharManifestacoes" itemLabel="Acompanhar Manifesta��es" action="#{ analiseManifestacaoDiscente.listarManifestacoes }" itemDisabled="false"/>
		</t:navigationMenuItem>
		
		<%--
		<c:if test="${usuario.discenteAtivo.graduacao}">
			<t:navigationMenuItem id="requerimentoPadrao" itemLabel="Requerimento Padr�o" >
				<t:navigationMenuItem id="criarRequerimentoPadrao" itemLabel="Criar Requerimento Padr�o" action="#{ requerimento.requerimentoPadrao }" itemDisabled="false"/>
				<t:navigationMenuItem id="exibirMeusRequerimentos" itemLabel="Exibir Meus Requerimentos" action="#{ requerimento.exibirRequerimento }" itemDisabled="false"/>
			</t:navigationMenuItem>		
		</c:if>
 		--%>
 		
		<t:navigationMenuItem id="producoesIntelectuais" itemLabel="Produ��es Intelectuais">
			<t:navigationMenuItem id="acervoDocentes" itemLabel="Acervo dos Docentes" action="#{acervoProducao.verAcervoDigital}" />
			<t:navigationMenuItem id="defesasPosGraduacao" itemLabel="Consultar Defesas de P�s-gradua��o" action="#{consultarDefesaMBean.iniciar}" />
		</t:navigationMenuItem>

		<t:navigationMenuItem itemLabel="Fiscal do Vestibular" id="fiscal" icon="/img/icones/producao_menu.gif" rendered="#{usuario.discenteAtivo.graduacao || usuario.discenteAtivo.stricto}">
			<t:navigationMenuItem id="fiscal_inscricao" itemLabel="Inscri��o" action="#{inscricaoSelecaoFiscalVestibular.iniciarInscricaoFiscal}"/>
			<t:navigationMenuItem id="fiscal_resultadoSelecao" itemLabel="Resultado da Sele��o" action="#{inscricaoSelecaoFiscalVestibular.exibeResultadoDoProcessamento}"/>
			<t:navigationMenuItem id="fiscal_locaisProva" itemLabel="Lista de Locais de Aplica��o de Prova" action="#{relatoriosVestibular.iniciarListaLocaisProva}"/>
			<t:navigationMenuItem id="fiscal_comprovanteInscricao" itemLabel="Comprovante de Inscri��o" action="#{inscricaoSelecaoFiscalVestibular.exibeComprovanteInscricao}"/>
			<t:navigationMenuItem id="fiscal_justificativaAusencia" itemLabel="Justificativa de Aus�ncia" action="#{justificativaAusencia.preCadastrar}"/>
		</t:navigationMenuItem>
		
		<t:navigationMenuItem itemLabel="Aux�lio Financeiro"  actionListener="#{ menuDiscente.redirecionar }" itemValue="/entrarSistema.do?sistema=sipac&url=portal_aluno/index.jsf?voltar=/sigaa/verPortalDiscente" />
		
		<%-- 
		<t:navigationMenuItem id="senhaAcessoCelular" itemLabel="Criar senha de acesso por Celular" action="#{ senhaMobileMBean.iniciar }" icon="/img/celular_icone.gif" split="true"/>
		--%>
		<c:if test="${usuario.discenteAtivo.graduacao or usuario.discenteAtivo.stricto}">
			<t:navigationMenuItem id="consultarProcessos" itemLabel="Consultar Processos do Aluno" action="#{ consultaProcesso.iniciar }" />
		</c:if>
			
		<t:navigationMenuItem
				id="cartaoRestaurante" itemLabel="Saldo do Cart�o do Restaurante" icon="/img/icones/restaurante_small.gif"
				actionListener="#{menuDiscente.redirecionar}"
				itemValue="/entrarSistema.do?sistema=sipac&url=restaurante/vendas/saldo_cartao.jsf?voltar=/sigaa/verPortalDiscente" />
		
	</t:navigationMenuItem>

</t:jscookMenu>

</h:form>

</div>
</div>
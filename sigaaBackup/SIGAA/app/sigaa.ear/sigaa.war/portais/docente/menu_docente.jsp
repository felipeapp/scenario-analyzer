
<%-- MENU DE OP��ES PARA O DOCENTE --%>
<%@ taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ufrn" uri="/tags/ufrn" %>
<ufrn:subSistema teste="portalDocente">
<div id="menu-dropdown">
<div class="wrapper">
<c:if test="${acesso.docente}">
<h:form>
	<input type="hidden" name="jscook_action" id="jscook_action" />
<t:jscookMenu layout="hbr" theme="ThemeOffice" styleLocation="/css/jscookmenu">

	<%-- ENSINO --%>
	<t:navigationMenuItem itemLabel="Ensino" id="ensino" icon="/img/icones/ensino_menu.gif">

		<t:navigationMenuItem id="ensino_OrientacaoAcademica" itemLabel="Orienta��o Acad�mica - Gradua��o" itemDisabled="#{acesso.nivelDocente == 'M'}">
			<t:navigationMenuItem id="ensOrienAcad_Orientandos" itemLabel="Meus Orientandos" action="#{orientacaoAcademica.listar}" rendered="#{acesso.orientadorAcademico}" icon="/img/report.png"/>
			<t:navigationMenuItem id="ensOrienAcad_MsgOrientandos" itemLabel="Enviar Mensagem aos Orientandos" action="#{notificarOrientandos.iniciar}" rendered="#{acesso.orientadorAcademico}" icon="/img/email_go.png"/>
			<t:navigationMenuItem id="ensOrienAcad_ListarMensagens" itemLabel="Listar Mensagens Enviadas" action="#{mensagemOrientacao.listarMensagens}" rendered="#{acesso.orientadorAcademico}" icon="/img/notificar.png"/>
			<t:navigationMenuItem id="ensOrienAcad_Analise" itemLabel="Analisar Solicita��es de Matr�cula" action="#{ analiseSolicitacaoMatricula.iniciarOrientadorAcademico}" icon="/img/graduacao/coordenador/analisar_solicitacoes.png" rendered="#{acesso.orientadorAcademico}" split="true"/>
			<t:navigationMenuItem id="ensOrienAcad_Indeferidos" itemLabel="Listar Indeferimentos" action="#{ listaIndeferimentos.iniciarOrientadorAcademico }" icon="/img/cross.png" rendered="#{acesso.orientadorAcademico}"/>
			<t:navigationMenuItem id="ensOrienAcad_ConsolidarTCCEstagio" itemLabel="Consolidar TCC e Est�gio" action="#{registroAtividade.telaListaAtividadesOrientandos}" icon="/img/graduacao/coordenador/validar.png" itemDisabled="false"/>
			<t:navigationMenuItem id="ensOrienAcad_OrienTracamento" itemLabel="Orientar Trancamentos" action="#{ atenderTrancamentoMatricula.iniciarAtendimentoSolicitacaoGraduacao}" icon="/img/graduacao/coordenador/confirmar_trancamento.png" rendered="#{acesso.orientadorAcademico}"/>
		</t:navigationMenuItem>
		
		<t:navigationMenuItem id="ensino_OrientacaoPosGraduacao" itemLabel="Orienta��es P�s-Gradua��o" itemDisabled="#{not acesso.orientadorStricto && not acesso.coOrientadorStricto}">
			<t:navigationMenuItem id="ensOrienPosGrad_Orientandos" itemLabel="Meus Orientandos" action="#{orientacaoAcademica.listarStricto}" icon="/img/report.png" itemDisabled="#{not acesso.orientadorStricto && not acesso.coOrientadorStricto}"/>
						<t:navigationMenuItem id="ensOrienPosGrad_Analise" itemLabel="Analisar Solicita��es de Matr�cula" action="#{ analiseSolicitacaoMatricula.mostrarCursosOrientandos}" icon="/img/graduacao/coordenador/analisar_solicitacoes.png" split="true" itemDisabled="#{not acesso.orientadorStricto}"  />
			<t:navigationMenuItem id="ensOrienPosGrad_OrienTracamento" itemLabel="Confirmar Trancamentos" action="#{ atenderTrancamentoMatricula.iniciarAtendimentoSolicitacaoStricto}" icon="/img/graduacao/coordenador/confirmar_trancamento.png" itemDisabled="#{not acesso.orientadorStricto }"/>
			
			<t:navigationMenuItem itemLabel="Bancas" split="true" itemDisabled="#{not acesso.orientadorStricto}">
				<t:navigationMenuItem itemLabel="Solicitar Cadastro" action="#{ orientacaoAcademica.listarStricto }"/>
				<t:navigationMenuItem itemLabel="Acompanhar Solicita��o" action="#{ bancaPos.listarBancasPendentesAprovacao }"/>
			</t:navigationMenuItem>				
						
			<%-- Comentado Aguardando libera��o da tarefa: 43902
			<t:navigationMenuItem itemLabel="Termo de Autoriza��o para Publica��o de Teses e Disserta��es - TEDE" split="true" icon="/img/graduacao/coordenador/documento.png" itemDisabled="#{not acesso.orientadorStricto}">
				<t:navigationMenuItem itemLabel="Exibir Solicita��es Pendentes de Aprova��o" action="#{ termoPublicacaoTD.exibirPendentes }"/>
				<t:navigationMenuItem itemLabel="Buscar/Listar" action="#{ termoPublicacaoTD.buscarDiscente }"/>
			</t:navigationMenuItem>
			--%>						
		</t:navigationMenuItem>

		
		<%-- EST�GIOS --%>
		<t:navigationMenuItem id="menuEstagios" itemLabel="Est�gios" icon="/img/estagio/estagio_menu.png">
			<t:navigationMenuItem id="muralVagasEstagio" itemLabel="Consultar Ofertas de Est�gio" action="#{ ofertaEstagioMBean.iniciarConsulta }"/>		
			<t:navigationMenuItem id="gerenciarEstagios" itemLabel="Gerenciar Est�gios" action="#{buscaEstagioMBean.iniciar}" split="true"/>		
		</t:navigationMenuItem>			

		<t:navigationMenuItem id="ensino_apedagogica" itemLabel="PAP" split="true" rendered="#{acesso.docenteUFRN}">
			<t:navigationMenuItem id="ensinscreverPAP" itemLabel="Increver-se em Atividade" action="#{inscricaoAtividadeAP.preCadastrar}"  />
			<t:navigationMenuItem id="consultaSituacaoInscricaoPAP" itemLabel="Consultar Situa��o da Inscri��o" action="#{inscricaoAtividadeAP.listar}"  />
			<t:navigationMenuItem id="ensCadPAP" split="true" itemLabel="Cadastrar Participa��o em Atividades" action="#{registroParticipacaoAP.preCadastrar}"  />
			<t:navigationMenuItem id="ensPartPAP" itemLabel="Listar Participa��o em Atividades"  action="#{registroParticipacaoAP.listar}"  />
		</t:navigationMenuItem>

<%--
		<t:navigationMenuItem id="ensino_ReposicaoAula" itemLabel="Reposi��o de Aula" split="true">
			<t:navigationMenuItem id="ensRepAula_PlanoAula" itemLabel="Listar Planos de Aulas" action="#{planoReposicaoAula.listarTodosPlanos}" />
		</t:navigationMenuItem>
		 --%>

		<t:navigationMenuItem id="ensino_ReposicaoProva" itemLabel="Reposi��o de Avalia��o">
			<t:navigationMenuItem id="ensRepProva_Solicitacoes" itemLabel="Analisar Solicita��es" action="#{solicitacaoReposicaoProva.analisarSolicitacao}" />
			<t:navigationMenuItem id="ensRepProvaHomologar" itemLabel="Apreciar Solicita��es" rendered="#{acesso.chefeDepartamento}" action="#{solicitacaoReposicaoProva.iniciarHomologacao}" />
		</t:navigationMenuItem>

		<t:navigationMenuItem id="ensino_Turmas" itemLabel="Turmas" split="true">
			<t:navigationMenuItem id="ensTur_CadastrarNotas" itemLabel="Cadastrar Notas" action="consolidaTurma"  itemDisabled="false"/>
			<c:if test="${ !consolidarTurma.fromMenuGestor }">
				<t:navigationMenuItem id="ensTur_PlanoCurso" itemLabel="Gerenciar Plano de Curso" action="planoCurso"  itemDisabled="false"/>
			</c:if>
 			<t:navigationMenuItem id="ensTur_GradeHorario" itemLabel="Grade de Hor�rios" action="#{gradeDocente.gerarGrade}"  itemDisabled="false"/>
			<t:navigationMenuItem id="ensTur_Consultar" itemLabel="Consultar Turmas" actionListener="#{menuDocente.consultaTurma}"  itemDisabled="false"/>			
		</t:navigationMenuItem>

		<t:navigationMenuItem id="ensProp_ProjetoEnsino" itemLabel="Projetos">
	            <t:navigationMenuItem id="projMon_Projetos" itemLabel="Projeto de Monitoria / Apoio da Qualidade do Ensino">
	                <t:navigationMenuItem id="projMonProj_MeusProjetos" itemLabel="Listar Meus Projetos" actionListener="#{menuDocente.redirecionar}" itemValue="/monitoria/ProjetoMonitoria/meus_projetos.jsf" itemDisabled="false" />
	                <t:navigationMenuItem id="projMonProj_SubmeterProj_Interno" itemLabel="Submeter Projeto Interno" action="#{projetoMonitoria.iniciarProjetoMonitoriaInterno}"/>
	                <t:navigationMenuItem id="projMonProj_SubmeterProj_Externo" itemLabel="Submeter Projeto Externo" action="#{projetoMonitoria.iniciarProjetoMonitoriaExterno}" />
	                <t:navigationMenuItem id="projMonProj_Consultar" itemLabel="Consultar Projetos Submetidos" action="#{coordMonitoria.situacaoProjeto}"/>
	                <t:navigationMenuItem id="projMonProj_Certificados_declaracoes" itemLabel="Declara��es" action="#{documentosAutenticadosMonitoria.participacoesServidorUsuarioLogado}"/>
	                
		            <t:navigationMenuItem id="projMon_Monitores" itemLabel="Monitores">
		                <t:navigationMenuItem id="projMonMonitores_Avaliar" itemLabel="Avaliar Relat�rio de Atividades Mensal" action="#{atividadeMonitor.iniciarAvaliacaoAtividadeOrientador}" rendered="#{monitoria.frequenciaMonitoria}"/>
		                <t:navigationMenuItem id="projMonMonitores_Consultar" itemLabel="Consultar Monitores" action="#{coordMonitoria.consultaMonitores}"/>
		            </t:navigationMenuItem>
		    
		            <t:navigationMenuItem id="projMon_CoordenacaoProj" itemLabel="Coordena��o de Projeto" rendered="#{acesso.coordenadorMonitoria}">
		                <t:navigationMenuItem id="projMonCoordProj_Gerenciar" itemLabel="Gerenciar Monitores do Projeto" action="#{consultarMonitor.coordenadorAlterarMonitor}"/>
		                <t:navigationMenuItem id="projMonCoordProj_Validar" itemLabel="Validar Relat�rios de Desligamento" action="#{relatorioMonitor.listarRelatoriosDesligamentoUsuarioAtualCoordena}" />
		                <t:navigationMenuItem id="projMonCoordProj_Processo" itemLabel="Processo Seletivo" action="#{provaSelecao.iniciarProcessoSeletivo}" />
		                <t:navigationMenuItem id="projMonCoordProj_Visualizar" itemLabel="Visualizar Avalia��es de Projetos" actionListener="#{menuDocente.redirecionar}" itemValue="/monitoria/VisualizarAvaliacoes/lista_projetos.jsf" itemDisabled="false"/>
		                <t:navigationMenuItem id="projMonCoordProj_SolReconsAva" itemLabel="Solicitar Reconsidera��o da Avalia��o" action="#{solicitacaoReconsideracao.iniciarSolicitacaoProjetosMonitoria}" split="true" />
		                <t:navigationMenuItem id="projMonCoordProj_SolReanalise" itemLabel="Solicitar Rean�lise dos Req. Formais" action="#{autorizacaoReconsideracao.listarMeusProjetosReconsideraveis}" />
		                <t:navigationMenuItem id="projMonCoordProj_RelParFinal" itemLabel="Relat�rios Parciais e Finais do Projeto" action="#{relatorioProjetoMonitoria.listarRelatorios}" split="true"/>
		                <t:navigationMenuItem id="projMonCoordProj_ResumoSID" itemLabel="Resumo SID do Projeto" action="#{coordMonitoria.resumoSID}"/>
		                <t:navigationMenuItem id="projMonCoordProj_EnviarAviso" itemLabel="Enviar Avisos" actionListener="#{menuDocente.redirecionar}" itemValue="/monitoria/CadastrarAvisoProjeto/lista_projetos.jsf" itemDisabled="false" split="true"/>          
		            </t:navigationMenuItem>
		    
		            <t:navigationMenuItem id="projMon_Comissao" itemLabel="Comiss�o Monitoria" rendered="#{acesso.comissaoMonitoria || acesso.comissaoCientificaMonitoria}">
		                <t:navigationMenuItem id="projMonComis_AvaProj" itemLabel="Avaliar Projetos" action="#{comissaoMonitoria.avaliaProjeto}" rendered="#{acesso.comissaoMonitoria}"/>
		                <t:navigationMenuItem id="projMonComis_AvaRelProj" itemLabel="Avaliar Relat�rios de Projetos" action="#{avalRelatorioProjetoMonitoria.listar}" rendered="#{acesso.comissaoMonitoria}"/>
		                <t:navigationMenuItem id="projMonComis_ConsultResSID" itemLabel="Consultar Resumos do SID" action="#{comissaoMonitoria.buscarResumoSID}"/>
		                <t:navigationMenuItem id="projMonComis_AvaResSID" itemLabel="Avaliar Resumos do SID" action="#{comissaoMonitoria.avaliaResumoSID}"/>
		            </t:navigationMenuItem> 
	            </t:navigationMenuItem>
	            
	       <%-- <t:navigationMenuItem id="projMon_Apoio" itemLabel="Projeto de Apoio � Melhoria da Qualidade do Ensino"> --%>
	       <%--     <t:navigationMenuItem id="projMonApoio_MeusProjetos" itemLabel="Listar Meus Projetos" actionListener="#{menuDocente.redirecionar}" itemValue="/monitoria/ProjetoMonitoria/meus_projetos_pamqeg.jsf" itemDisabled="false" />  --%>
	       <%--     <t:navigationMenuItem id="projMonApoio_SubmeterProj" itemLabel="Submeter Projeto" action="#{projetoMonitoria.novoProjetoPAMQEG}" split="true"/> --%>
	       <%--     <t:navigationMenuItem id="projMonApoio_Consultar" itemLabel="Consultar Projetos Submetidos" action="#{coordMonitoria.situacaoProjeto}"/> --%>
	       <%-- </t:navigationMenuItem> --%>


	            <t:navigationMenuItem id="ensProp_CursoLatoSensu" itemLabel="Proposta de Curso Lato Sensu">
<%-- 	                <t:navigationMenuItem id="ensPropLatoSen_SubmeterProp" itemLabel="Submeter Nova Proposta (Struts)" actionListener="#{menuDocente.redirecionar}" itemValue="/ensino/latosensu/criarCurso.do?dispatch=popular"  itemDisabled="false"/> --%>
<%-- 	                <t:navigationMenuItem id="ensPropLatoSen_MinhasProp" itemLabel="Minhas Propostas (Struts)" actionListener="#{menuDocente.redirecionar}" itemValue="/ensino/latosensu/criarCurso.do?dispatch=minhasPropostas"  itemDisabled="false"/> --%>
	                <t:navigationMenuItem id="ensPropLatoSen_SubmeterProp2" itemLabel="Submeter Nova Proposta" action="#{cursoLatoMBean.preCadastrar}"  itemDisabled="false"/>
	                <t:navigationMenuItem id="ensPropLatoSen_MinhasProp2" itemLabel="Minhas Propostas" action="#{cursoLatoMBean.listar}"  itemDisabled="false"/>
	            </t:navigationMenuItem>
		</t:navigationMenuItem>

		<t:navigationMenuItem id="ensino_AvaliacaoInstitucional" itemLabel="Avalia��o Institucional">
			<t:navigationMenuItem id="ensAvaInst_Preencher" itemLabel="Preencher a Avalia��o Institucional" action="#{ calendarioAvaliacaoInstitucionalBean.listar }" itemDisabled="false"/>
			<t:navigationMenuItem itemLabel="Avalia��o Institucional da Doc�ncia Assistida" action="#{ calendarioAvaliacaoInstitucionalBean.listar}" id="calendarioAvaliacaoInstitucionalBean_listar"/>
			<t:navigationMenuItem id="ensAvaInst_ConsultarResult" itemLabel="Consultar Resultados da Avalia��o" action="#{ portalResultadoAvaliacao.inicializa }" split="true" itemDisabled="false"/>
			<t:navigationMenuItem id="ensAvaInst_SintDocDepart_chefia" itemLabel="Consulta P�blica dos Docentes por Departamento" action="#{relatorioAvaliacaoMBean.iniciarConsultaPublica}"/>
			<%-- <t:navigationMenuItem itemLabel="Avalia��o Institucional Anterior" action="#{ avaliacaoInstitucional.reverAnteriorDocente }" itemDisabled="false"/> --%>
			<t:navigationMenuItem id="ensino_AvaliacaoInstitucional_chefia" itemLabel="Chefia" rendered="#{acesso.chefeDepartamento || acesso.diretorCentro}">
	 			<t:navigationMenuItem id="ensino_chefRel_Analitico_chefia" itemLabel="Resultado Anal�tico do Docente por Turma" action="#{relatorioAvaliacaoMBean.iniciarResultadoAnalitico}" rendered="#{acesso.chefeDepartamento || acesso.diretorCentro}" />
				<t:navigationMenuItem id="ensino_chefRel_SintDocCentro" itemLabel="Resultado Sint�tico do seu Centro" action="#{relatorioAvaliacaoMBean.iniciarMediaNotas}" rendered="#{acesso.diretorCentro}"/>
 				<t:navigationMenuItem id="ensino_chefRel_SintDocDepart" itemLabel="Resultado Sint�tico do do seu Departamento" action="#{relatorioAvaliacaoMBean.iniciarMediaNotas}" rendered="#{acesso.chefeDepartamento}"/>
	 			
			</t:navigationMenuItem>
		</t:navigationMenuItem>
		
		<t:navigationMenuItem id="ensino_PlanoIndividual" itemLabel="Plano Individual do Docente (PID)" split="true" rendered="#{cargaHorariaPIDMBean.verificarExibicaoLinkPID}" >
			<t:navigationMenuItem id="ensinoPid_Cadastrar" action="#{cargaHorariaPIDMBean.iniciar}" itemLabel="Meus PIDs"/>
			<t:navigationMenuItem id="ensinoPid_Consultar" action="#{consultaPidMBean.iniciar}" itemLabel="Consultar Outros PIDs"/>
			<t:navigationMenuItem id="ensinoPid_Relatorio" action="#{relatorioPID.gerarRelatorioSintetico}" itemLabel="Relat�rio Sint�tico por Departamento"/>
		</t:navigationMenuItem>
		
		<t:navigationMenuItem id="ensino_Consultas" itemLabel="Consultas" split="true">
			<t:navigationMenuItem id="ensCons_OrientacaoAtividades" itemLabel="Orienta��es de Atividades" action="#{orientacaoAtividade.iniciarBusca}" icon="/img/report.png"/>
			<t:navigationMenuItem id="ensCons_Turmas" itemLabel="Turmas" actionListener="#{menuDocente.consultaTurma}"  itemDisabled="false"/>
			<t:navigationMenuItem id="ensCons_Cursos" itemLabel="Cursos" action="#{ curso.popularBuscaGeral }"  itemDisabled="false"/>
			<c:if test="${acesso.nivelDocente == 'T'}">
				<t:navigationMenuItem id="ensConsTec_Disciplinas" itemLabel="Disciplinas" actionListener="#{menuDocente.consultaDisciplinaTec}"  itemDisabled="false" />
				
				<t:navigationMenuItem id="ensCons_Estrutura" itemLabel="Estruturas Curriculares">
					<t:navigationMenuItem id="ensConsTec_Estrutura" itemLabel="Estrutura Curricular T�cnica" action="#{ estruturaCurricularTecnicoMBean.listar }"  itemDisabled="false"/>
				 	<t:navigationMenuItem id="ensConsGrad_Estrutura" itemLabel="Estrutura Curricular de Gradua��o" action="#{ curriculo.popularBuscaGeral }"  itemDisabled="false"/>
				</t:navigationMenuItem>
			</c:if>
			
			<c:if test="${acesso.nivelDocente == 'G'}">
				<t:navigationMenuItem id="ensConsGrad_Disciplinas" itemLabel="Componentes Curriculares" action="#{ componenteCurricular.popularBuscaGeral }" itemDisabled="false"/>
				
				<t:navigationMenuItem id="ensCons_Estrutura" itemLabel="Estruturas Curriculares">
					<t:navigationMenuItem id="ensConsTec_Estrutura" itemLabel="Estrutura Curricular T�cnica" action="#{ estruturaCurricularTecnicoMBean.listar }"  itemDisabled="false"/>
				 	<t:navigationMenuItem id="ensConsGrad_Estrutura" itemLabel="Estrutura Curricular de Gradua��o" action="#{ curriculo.popularBuscaGeral }"  itemDisabled="false"/>
				</t:navigationMenuItem>
			</c:if>
			
			<t:navigationMenuItem id="ensCons_Unidades" action="#{unidade.popularBuscaGeral}" itemLabel="Unidades Acad�micas" split="true"/>
			<t:navigationMenuItem id="ensCons_Defesas" action="#{consultarDefesaMBean.iniciar}" itemLabel="Defesas de P�s-Gradua��o" split="true"/>
			<t:navigationMenuItem id="ensCons_Calendario" action="#{calendario.iniciarBusca}" itemLabel="Calend�rio Acad�mico" split="true"/>
		</t:navigationMenuItem>
		
		<%-- 
		<t:navigationMenuItem id="ensino_Docentes" itemLabel="Docentes do Centro" rendered="#{acesso.diretorCentro}" split="true">
			<t:navigationMenuItem id="ensDoc_ListarFalta" itemLabel="Listar Avisos de Falta" action="#{faltaDocente.preBusca}" rendered="#{acesso.diretorCentro}" />
		</t:navigationMenuItem>
		--%>
		<t:navigationMenuItem id="ensino_SoliCompLivro" actionListener="#{ menuDocente.redirecionar }" itemLabel="Solicitar Compra de Livros para a Biblioteca" itemValue="/entrarSistema.do?sistema=sipac&url=listaReqBiblioteca.do?acao=474"/>
		
		<t:navigationMenuItem itemLabel="F�rum de Cursos" action="#{listagemCursosForumDocenteMBean.findAnoPeriodoCursosAnteriores}" split="true"/>
		<t:navigationMenuItem id="declaracaoDisciplinas" itemLabel="Declara��o de Disciplinas Ministradas" split="true" action="#{declaracaoDisciplinasMinistradas.emitir}"/>
		<t:navigationMenuItem id="planosDocenciaAssistida" itemLabel="Planos de Doc�ncia Assistida " split="true" action="#{parecerDocenciaAssistida.iniciar}"/>		
	</t:navigationMenuItem>


	<%-- CHEFIA --%>
	<t:navigationMenuItem itemLabel="Chefia" id="chefia"  icon="/img/icones/coordenacao_menu.gif" rendered="#{acesso.chefeUnidade}">

			<t:navigationMenuItem id="chefia_CompCurriculares" itemLabel="Componentes Curriculares" rendered="#{acesso.chefeDepartamento}">
				<t:navigationMenuItem id="chefCompCur_CadastrarComp" itemLabel="Solicitar Cadastro de Componentes" action="#{componenteCurricular.preCadastrar}" icon="/img/graduacao/coordenador/solicitar.png"/>
				<t:navigationMenuItem id="chefCompCur_ListarSoliCad" itemLabel="Listar Solicita��es de Cadastro Enviadas" action="#{autorizacaoComponente.telaComponentes}" rendered="#{acesso.chefeDepartamento}"  itemDisabled="false"/>
				<t:navigationMenuItem id="chefCompCur_CadastrarProg" itemLabel="Cadastrar Programa de Componente" action="#{programaComponente.iniciar}" rendered="#{acesso.chefeDepartamento}"  itemDisabled="false"/>
				<t:navigationMenuItem id="chefCompCur_ConsultarComp" itemLabel="Consultar Componentes com Programas Cadastrados" action="#{relatorioPorDepartamento.iniciarRelatorioComponentesComPrograma}" rendered="#{acesso.chefeDepartamento}"  itemDisabled="false"/>
				<t:navigationMenuItem id="chefCompCur_ConsultarIncompl" itemLabel="Consultar Componentes com Programas Incompletos" action="#{relatorioPorDepartamento.iniciarRelatorioComponentesComProblemasCadastroEmPrograma}" rendered="#{acesso.chefeDepartamento}"  itemDisabled="false"/>
			</t:navigationMenuItem>
			<t:navigationMenuItem id="chefia_Turmas" itemLabel="Turmas" rendered="#{acesso.chefeDepartamento}">
				<t:navigationMenuItem id="chefTur_AltRemTurma" itemLabel="Alterar/Remover Turma" rendered="#{acesso.chefeDepartamento}" action="#{buscaTurmaBean.popularBuscaGeral}" itemDisabled="false"/>
	 			<t:navigationMenuItem id="chefTur_CriarTurma" itemLabel="Criar Turma Sem Solicita��o" rendered="#{acesso.chefeDepartamento}" action="#{turmaGraduacaoBean.iniciarTurmaSemSolicitacao}" itemDisabled="false"/>
				<t:navigationMenuItem id="chefTur_Transferir" itemLabel="Transferir Alunos entre turmas" action="#{transferenciaTurma.iniciarAutomatica}" rendered="#{acesso.chefeDepartamento}"  itemDisabled="false"/>
	 			<t:navigationMenuItem id="chefTur_Gerenciar" itemLabel="Gerenciar Solicita��es de Turmas">
	                <t:navigationMenuItem id="sugerir_Turma" itemLabel="Sugerir Turma para Curso" action="#{ sugestaoSolicitacaoTurma.iniciarSugestaoTurma }" icon="/img/graduacao/coordenador/turma_sol.png" rendered="#{sugestaoSolicitacaoTurma.chefeDepartamento}"/>	 			
		 			<t:navigationMenuItem id="chefTurGer_Regular" itemLabel="Turmas Regulares" rendered="#{acesso.chefeDepartamento}" action="#{analiseSolicitacaoTurma.gerenciarSolicitacoesRegulares}" itemDisabled="false"/>
		 			<t:navigationMenuItem id="chefTurGer_Ferias" itemLabel="Turmas de F�rias" rendered="#{acesso.chefeDepartamento}" action="#{analiseSolicitacaoTurma.gerenciarSolicitacoesFerias}" itemDisabled="false"/>
		 			<t:navigationMenuItem id="chefTurGer_EnsIndividual" itemLabel="Turmas de Ensino Individual" rendered="#{acesso.chefeDepartamento}" action="#{analiseSolicitacaoTurma.gerenciarSolicitacoesEnsinoIndividual}" itemDisabled="false"/>
		 			<t:navigationMenuItem id="chefTurGer_Todas" itemLabel="Ver Todas" rendered="#{acesso.chefeDepartamento}" action="#{analiseSolicitacaoTurma.gerenciarSolicitacoesTodas}" itemDisabled="false"/>
	                <t:navigationMenuItem id="chefTurGer_Indicador" itemLabel="Indicadores de Solicita��o de Ensino Individual" rendered="#{acesso.chefeDepartamento}" action="#{indicadorSolicitacaoEnsinoIndividualMBean.iniciar}" itemDisabled="false"/>
	 			</t:navigationMenuItem>
	 		<t:navigationMenuItem id="ensTur_Consultar1" itemLabel="Consultar Turmas" actionListener="#{menuDocente.consultaTurma}"  itemDisabled="false"/>
            <t:navigationMenuItem id="ensTur_AdicionarReserva" itemLabel="Adicionar Reservas de Vagas" actionListener="#{menuDocente.consultaTurma}"  itemDisabled="false"/>
            <t:navigationMenuItem id="ensTur_AjustarTurma" itemLabel="Ajustar Turma" actionListener="#{menuDocente.consultaTurma}"  itemDisabled="false"/>
			</t:navigationMenuItem>
			<t:navigationMenuItem id="chefia_Relatorios" itemLabel="Relat�rios" rendered="#{acesso.chefeDepartamento || acesso.diretorCentro}">
				<t:navigationMenuItem id="chefRel_Geral" itemLabel="Relat�rio Geral de Turmas" rendered="#{acesso.chefeDepartamento || acesso.diretorCentro}" actionListener="#{menu.redirecionar}" itemValue="/graduacao/relatorios/turma/seleciona_turma.jsf?aba=relatorios" />			
				<t:navigationMenuItem id="chefRel_TurSituacao" itemLabel="Relat�rio de Turmas por Situa��o" rendered="#{acesso.chefeDepartamento}" actionListener="#{menuDocente.redirecionar}" itemValue="/graduacao/turma/lista.jsf"  itemDisabled="false"/>
		 		<t:navigationMenuItem id="chefRel_TurOferecida" itemLabel="Relat�rio de Turmas Oferecidas" rendered="#{acesso.chefeDepartamento}" actionListener="#{menuDocente.redirecionar}" itemValue="/graduacao/relatorios/turmas_oferecidas.jsf"  itemDisabled="false"/>
	 			<t:navigationMenuItem id="chefRel_TurConsolida" itemLabel="Relat�rio de Turmas Consolidadas" rendered="#{acesso.chefeDepartamento}" action="#{relatoriosCoordenador.relatorioTurmasConsolidadasDepartamento}" itemDisabled="false"/>
	 			<t:navigationMenuItem id="chefRel_OcupVagas" itemLabel="Relat�rio de Ocupa��o de Vagas de Turmas" rendered="#{acesso.chefeDepartamento}" action="#{relatorioTurma.iniciarRelatorioOcupacaoVagas}"/>
	 			<t:navigationMenuItem id="chefRel_TurDocente" itemLabel="Relat�rio de Turmas por Docente" rendered="#{acesso.chefeDepartamento}" action="#{relatoriosDepartamentoCpdi.iniciarTurmasDepartamentoDocente}"/>
                <t:navigationMenuItem id="chefRel_Tur_Dis_Matriculado" itemLabel=" Relat�rio de Turmas com Discentes Matriculados" rendered="#{acesso.chefeDepartamento}" action="#{relatorioTurmaDiscentes.inicio}"/>	 			
	 			<t:navigationMenuItem id="chefRel_AluIngressante" itemLabel="Lista Alunos Ingressantes" rendered="#{acesso.chefeDepartamento}" actionListener="#{menuDocente.redirecionar}" itemValue="/graduacao/relatorios/discente/seleciona_ingressantes.jsf" split="true"/>
	            <t:navigationMenuItem id="chefRel_AlunosPendentesComponenteCurricular" itemLabel="Alunos Pendentes de Componente Curricular" action="#{ relatorioAlunosPendentesDeComponente.iniciar }" />
				<t:navigationMenuItem id="chefRel_AlunosAptosACursarDeterminadoComponenteCurricular" itemLabel="Alunos Aptos a Cursar Determinado Componente Curricular" action="#{ relatorioAlunosPendentesDeComponente.iniciarAptos }" />
				<t:navigationMenuItem id="chefRel_Docente" itemLabel="Relat�rio de Docentes" rendered="#{acesso.chefeDepartamento}" action="#{relatorioDocente.gerarRelatorioLista}" itemDisabled="false" split="true"/>
	           	<t:navigationMenuItem id="chefRel_DocentesMinistrantesComponente" itemLabel="Relat�rio de Docentes Ministrantes de um Componente" rendered="#{acesso.chefeDepartamento}" action="#{relatorioMinistrantes.iniciar}" itemDisabled="false" />				
	 			<t:navigationMenuItem id="chefRel_DiscRepro" itemLabel="Relat�rio de Disciplinas com Reprova��es" rendered="#{acesso.chefeDepartamento}" action="#{ relatoriosCoordenador.relatorioReprovacoesDisciplinasDepartamento }" itemDisabled="false"/>
	 			<t:navigationMenuItem id="chefRel_Academico" itemLabel="Relat�rio Gerencial Acad�mico" rendered="#{acesso.chefeDepartamento || acesso.diretorCentro}" action="#{relatoriosDepartamentoCpdi.iniciarRelatorioGerencialAcademico}" split="true"/>
	 		</t:navigationMenuItem>
	 		
	 			
			<t:navigationMenuItem id="chefia_Discentes" itemLabel="Discentes" rendered="#{acesso.chefeDepartamento}">
				<t:navigationMenuItem id="chefDis_AnaSolMatAluEspecial" itemLabel="Analisar Solicita��es de Matr�cula de Aluno Especial" action="#{analiseSolicitacaoMatricula.iniciarAlunoEspecial}" rendered="#{acesso.chefeDepartamento}"  itemDisabled="false"/>
            	<t:navigationMenuItem id="chefDis_ConsulHistorico" itemLabel="Consultar Hist�rico" action="#{historico.buscarDiscente}"  itemDisabled="false"/>
				<t:navigationMenuItem id="chefDis_MatAluEspecial" itemLabel="Matricular Aluno Especial" action="#{matriculaGraduacao.iniciarEspecial}" rendered="#{acesso.chefeDepartamento}"  itemDisabled="false"/>
				<t:navigationMenuItem id="chefDis_TransferirAluno" itemLabel="Transferir Alunos entre turmas" action="#{transferenciaTurma.iniciarAutomatica}" rendered="#{acesso.chefeDepartamento}"  itemDisabled="false"/>
			</t:navigationMenuItem>

			<t:navigationMenuItem id="chefia_Docentes" itemLabel="Docentes" rendered="#{acesso.chefeDepartamento}">
				<t:navigationMenuItem id="chefDoc_CadastrarDocenteExterno" itemLabel="Cadastrar Docente Externo" action="#{docenteExterno.popular}" rendered="#{acesso.chefeDepartamento}"  itemDisabled="false"/>
       	 		<t:navigationMenuItem id="chefDoc_CadastrarUsuarioDocenteExterno"  itemLabel="Cadastrar Usu�rio Para Docente Externo" action="#{docenteExterno.listar}" rendered="#{acesso.chefeDepartamento}"  itemDisabled="false"/>	        	
	        	<t:navigationMenuItem id="chefDoc_ListarAvisoFalta" itemLabel="Listar/Homologar Aviso de Falta" action="#{avisoFalta.iniciarBusca}" rendered="#{acesso.chefeDepartamento}"  itemDisabled="false"/>
	            <%--
    	        <t:navigationMenuItem id="chefDoc_PlanoAula" itemLabel="Gerenciar Avisos de Falta Homologados" action="#{planoReposicaoAula.listarPlanoAulaPendentesAprovacao}" rendered="#{acesso.chefeDepartamento}"  itemDisabled="false" />
		 		--%>
			</t:navigationMenuItem>
			<t:navigationMenuItem id="pid" itemLabel="Plano Individual do Docente" rendered="#{acesso.chefeDepartamento}">
				<t:navigationMenuItem id="listar_pids" itemLabel="Listar/Homologar Plano Individual do Docente" action="#{cargaHorariaPIDMBean.gerarListagemPIDChefeDepartamento}" rendered="#{acesso.chefeDepartamento}"  itemDisabled="false"/>
			</t:navigationMenuItem>			

        <t:navigationMenuItem id="chefia_Autorizacao" itemLabel="Autoriza��es" rendered="#{acesso.chefeUnidade || acesso.diretorCentro}">
            <t:navigationMenuItem id="chefAuto_ProjMonitoria" itemLabel="Autorizar Projetos de Monitoria" action="#{coordMonitoria.autorizacaoChefe}"/>
            <t:navigationMenuItem id="chefAuto_AcaoExtensao" itemLabel="Autorizar A��es de Extens�o" action="#{autorizacaoDepartamento.autorizacaoChefe}"/>
            <t:navigationMenuItem id="chefAuto_GrupoPesq" itemLabel="Autorizar Grupo de Pesquisa" action="#{autorizacaoGrupoPesquisaMBean.listarGruposPesquisaPendentes}"/> 
            <t:navigationMenuItem id="chefAuto_ValidarRel" itemLabel="Validar Relat�rios de A��es de Extens�o" action="#{autorizacaoDepartamento.autorizacaoRelatorioChefe}"/> 
        </t:navigationMenuItem> 
					
        <t:navigationMenuItem id="chefia_PaginaWeb" itemLabel="P�gina WEB" rendered="#{(detalhesSite.acessoCentroUnidEspecializada || acesso.chefeDepartamento)}">
            <t:navigationMenuItem id="chefPag_Centro" itemLabel="Centro" rendered="#{detalhesSite.acessoCentroUnidEspecializada}">
                <t:navigationMenuItem id="chefPagCent_Apres" itemLabel="Apresenta��o" action="#{detalhesSite.iniciarDetalhesCentro}"  />
                <t:navigationMenuItem id="chefPagCent_DocArq" itemLabel="Documentos/Arquivos">
					<t:navigationMenuItem id="chefPagCentDocArq_Cadastrar" itemLabel="Cadastrar" action="#{documentoSite.preCadastrarCentro}"/>
					<t:navigationMenuItem id="chefPagCentDocArq_AltRem" itemLabel="Alterar / Remover"  action="#{documentoSite.listarCentro}"/>
				</t:navigationMenuItem>
				<t:navigationMenuItem id="chefPagCent_Noticias" itemLabel="Not�cias" >
						<t:navigationMenuItem id="chefPagCentNot_Cadastrar" itemLabel="Cadastrar" action="#{noticiaSite.preCadastrarCentro}" />
						<t:navigationMenuItem id="chefPagCentNot_AltRem" itemLabel="Alterar / Remover"  action="#{noticiaSite.listarCentro}"  />
				</t:navigationMenuItem>
				<t:navigationMenuItem id="chefPagCent_Extras" itemLabel="Se��es Extras" >
					<t:navigationMenuItem id="chefPagCentExt_Cadastrar" itemLabel="Cadastrar" action="#{secaoExtraSite.preCadastrarCentro}" />
					<t:navigationMenuItem id="chefPagCentExt_AltRem" itemLabel="Alterar / Remover"  action="#{secaoExtraSite.listarCentro}"  />
				</t:navigationMenuItem>
			</t:navigationMenuItem>
		
			<t:navigationMenuItem id="chefPag_Departamento" itemLabel="Departamento" rendered="#{acesso.chefeDepartamento}">
				<t:navigationMenuItem id="chefPagDepart_Apres" itemLabel="Apresenta��o"  action="#{detalhesSite.iniciarDetalhesDepartamento}"/>
				<t:navigationMenuItem id="chefPagDepart_DocArq" itemLabel="Documentos/Arquivos" rendered="#{acesso.chefeDepartamento}">
					<t:navigationMenuItem id="chefPagDepartDocArq_Cadastrar" itemLabel="Cadastrar" action="#{documentoSite.preCadastrarDepartamento}" />
					<t:navigationMenuItem id="chefPagDepartDocArq_AltRem" itemLabel="Alterar / Remover"  action="#{documentoSite.listarDepartamento}"/>
				</t:navigationMenuItem>
				<t:navigationMenuItem id="chefPagDepart_Noticias" itemLabel="Not�cias" >
					<t:navigationMenuItem id="chefPagDepartNot_Cadastrar" itemLabel="Cadastrar" action="#{noticiaSite.preCadastrarDepartamento}" />
					<t:navigationMenuItem id="chefPagDepartNot_AltRem" itemLabel="Alterar / Remover"  action="#{noticiaSite.listarDepartamento}" />
				</t:navigationMenuItem>
				<t:navigationMenuItem id="chefPagDepart_Outras" itemLabel="Outras Op��es">
					<t:navigationMenuItem id="chefPagDepartOut_Cadastrar" itemLabel="Cadastrar" action="#{secaoExtraSite.preCadastrarDepartamento}" />
					<t:navigationMenuItem id="chefPagDepartOut_AltRem" itemLabel="Alterar / Remover"  action="#{secaoExtraSite.listarDepartamento}" />
				</t:navigationMenuItem>
			</t:navigationMenuItem>
			
		</t:navigationMenuItem>
		
		<t:navigationMenuItem id="chefia_AvaliacaoInstitucional" itemLabel="Avalia��o Institucional" rendered="#{acesso.chefeDepartamento || acesso.diretorCentro}">
			<t:navigationMenuItem id="chefRel_SintDocCentro" itemLabel="Resultado Sint�tico dos Docentes por Centro" action="#{relatorioAvaliacaoMBean.iniciarMediaNotas}" rendered="#{acesso.diretorCentro}"/>
 			<t:navigationMenuItem id="chefRel_SintDocDepart" itemLabel="Resultado Sint�tico dos Docentes por Departamento" action="#{relatorioAvaliacaoMBean.iniciarMediaNotas}" rendered="#{acesso.chefeDepartamento}"/>
 			<t:navigationMenuItem id="chefRel_Analitico" itemLabel="Resultado Anal�tico do Docente por Turma" action="#{relatorioAvaliacaoMBean.iniciarResultadoAnalitico}" rendered="#{acesso.chefeDepartamento || acesso.diretorCentro}" />
		</t:navigationMenuItem>
	
	</t:navigationMenuItem>

 <%-- PESQUISA --%>
	   <t:navigationMenuItem itemLabel="Pesquisa" id="projeto_pesquisa" icon="/img/icones/pesquisa_menu.gif">
			<t:navigationMenuItem id="projPes_Grupos" itemLabel="Grupo de Pesquisa/Projetos de Apoio">
 					<t:navigationMenuItem id="projPesGru_SubmeterProp" itemLabel="Proposta de Cria��o de Grupo de Pesquisa">
 						<t:navigationMenuItem id="cad_projPesGru_SubmeterProp" itemLabel="Cadastrar" action="#{ propostaGrupoPesquisaMBean.iniciar }" /> 
 						<t:navigationMenuItem id="list_projPesGru_SubmeterProp" itemLabel="Consultar" action="#{ propostaGrupoPesquisaMBean.listar }" /> 
 					</t:navigationMenuItem> 
					<t:navigationMenuItem id="proj_apoio_grupo_pesq" itemLabel="Apoio a Grupo de Pesquisa">
						<t:navigationMenuItem id="cad_proj_apoio_grupo_pesq" itemLabel="Cadastrar" action="#{ projetoApoioGrupoPesquisaMBean.preCadastrar }" />
						<t:navigationMenuItem id="list_proj_apoio_grupo_pesq" itemLabel="Consultar" action="#{ projetoApoioGrupoPesquisaMBean.listar }" />
					</t:navigationMenuItem>
					<t:navigationMenuItem id="proj_apoio_novos_pesq" itemLabel="Apoio a Novos Pesquisadores">
						<t:navigationMenuItem id="cad_proj_apoio_novos_pesq" itemLabel="Cadastrar" action="#{ projetoApoioNovosPesquisadoresMBean.preCadastrar }" />
						<t:navigationMenuItem id="list_proj_apoio_novos_pesq" itemLabel="Consultar" action="#{ projetoApoioNovosPesquisadoresMBean.listar }" />
					</t:navigationMenuItem>
			</t:navigationMenuItem>	   
			<t:navigationMenuItem id="projPes_Projetos" itemLabel="Projetos de Pesquisa">
				<t:navigationMenuItem id="projPesPro_MeusProjetos" itemLabel="Listar Meus Projetos" itemValue="/pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=listByMembro" actionListener="#{menuDocente.redirecionar}"/>
				<t:navigationMenuItem id="projPesPro_GerenciaEquipe" itemLabel="Gerenciar Equipes Organizadoras" action="#{membroProjeto.gerenciarMembrosProjetoPesquisa}" rendered="#{acesso.coordPesquisa}"/>
				<t:navigationMenuItem id="projPesPro_SubmeterProp" itemLabel="Submeter Proposta de Projeto Interno"  split="true" actionListener="#{menuDocente.redirecionar}" itemValue="/pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=popular&interno=true" itemDisabled="false"/>
				<t:navigationMenuItem id="projPesPro_CadastrarProj" itemLabel="Cadastrar Projeto Externo" actionListener="#{menuDocente.redirecionar}" itemValue="/pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=popular" itemDisabled="false"/>
				<t:navigationMenuItem id="projPesPro_SolRenovacao" itemLabel="Solicitar Renova��o" itemValue="/pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=listaRenovacao" actionListener="#{menuDocente.redirecionar}" itemDisabled="false"/>
			</t:navigationMenuItem>
	
			<t:navigationMenuItem id="projPes_Plano" itemLabel="Planos de Trabalho">
				<t:navigationMenuItem id="projPesPlano_MeusProjetos" itemLabel="Listar Meus Planos de Trabalho" itemValue="/pesquisa/planoTrabalho/wizard.do?dispatch=listarPorOrientador" actionListener="#{menuDocente.redirecionar}" />
				<t:navigationMenuItem id="projPesPlano_ResultDistCotas" itemLabel="Resultado da Distribui��o de Cotas" itemValue="/pesquisa/indicarBolsista.do?dispatch=verResultadoCota" actionListener="#{menuDocente.redirecionar}" rendered="#{acesso.docenteUFRN}"/>
				<t:navigationMenuItem id="projPesPlano_SolCotasBolsa" itemLabel="Solicitar Cota de Bolsa"  split="true" itemValue="/pesquisa/planoTrabalho/wizard.do?dispatch=iniciarSolicitacaoCota&solicitacaoCota=true&cadastroVoluntario=false" actionListener="#{menuDocente.redirecionar}" itemDisabled="false" />
	 			<t:navigationMenuItem id="projPesPlano_CadPlanoVolun" itemLabel="Cadastrar Plano de Volunt�rio" itemValue="/pesquisa/planoTrabalho/wizard.do?dispatch=iniciarPlanoVoluntario&solicitacaoCota=true&cadastroVoluntario=true" actionListener="#{menuDocente.redirecionar}" itemDisabled="false"/>
	 			<t:navigationMenuItem id="projPesPlano_CadPlanoTrab" itemLabel="Cadastrar Plano de Trabalho sem Cota" itemValue="/pesquisa/planoTrabalho/wizard.do?dispatch=iniciarPlanoSemCota&solicitacaoCota=false&cadastroVoluntario=false" actionListener="#{menuDocente.redirecionar}" itemDisabled="false" rendered="#{acesso.docenteUFRN}"/>
				<t:navigationMenuItem id="projPesPlano_IndSubBolsista" itemLabel="Indicar/Substituir Bolsista" itemValue="/pesquisa/indicarBolsista.do?dispatch=popular" actionListener="#{menuDocente.redirecionar}" itemDisabled="false" />
			</t:navigationMenuItem>
	
			<t:navigationMenuItem id="projPes_Iniciacao" itemLabel="Relat�rios de Inicia��o Cient�fica">
				<t:navigationMenuItem id="projPesIni_RelParciais" itemLabel="Relat�rios Parciais">
					<t:navigationMenuItem id="projPesIniRelPar_ConsulEmitir" itemLabel="Consultar/Emitir Parecer" actionListener="#{menuDocente.redirecionar}" itemValue="/pesquisa/relatorioBolsaParcial.do?dispatch=listarOrientandos"/>
				</t:navigationMenuItem>
				<t:navigationMenuItem id="projPesIni_RelFinais" itemLabel="Relat�rios Finais">
					<t:navigationMenuItem id="projPesIniRelFin_ConsulEmitir" itemLabel="Consultar/Emitir Parecer" actionListener="#{menuDocente.redirecionar}" itemValue="/pesquisa/relatorioBolsaFinal.do?dispatch=listarOrientandos"/>
				</t:navigationMenuItem>
			</t:navigationMenuItem>
			
			<t:navigationMenuItem id="projPes_Anuais" itemLabel="Relat�rios Anuais de Projeto">
				<t:navigationMenuItem id="projPesAnuais_Submeter" itemLabel="Submeter" itemValue="/pesquisa/relatorioProjeto.do?dispatch=listarRelatorios" actionListener="#{menuDocente.redirecionar}"/>
				<t:navigationMenuItem id="projPesAnuais_Consultar" itemLabel="Consultar" itemValue="/pesquisa/relatorioProjeto.do?dispatch=listarMembroProjeto" actionListener="#{menuDocente.redirecionar}"/>
			</t:navigationMenuItem>
			
			<t:navigationMenuItem id="projPes_Congresso" itemLabel="Congresso de Inicia��o Cient�fica" >
				<t:navigationMenuItem id="projPesCongr_MeusResumos" itemLabel="Meus Resumos" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/resumoCongresso.do?dispatch=listarResumosAutor"/>
				<t:navigationMenuItem id="projPesCongr_CertAvaliador" itemLabel="Certificado de Avaliador" action="#{avaliadorCIC.listarCertificados}" />
				<t:navigationMenuItem id="projPesCongr_AvaliarRes" itemLabel="Avaliar Resumos" action="#{avaliacaoResumoBean.listarResumos}" split="true" />
				<t:navigationMenuItem id="projPesCongr_AvaliarApres" itemLabel="Avaliar Apresenta��es de Trabalhos" action="#{avaliacaoApresentacaoResumoBean.listarResumosAvaliador}" />
				<t:navigationMenuItem id="projPesCongr_AutorizarRes" itemLabel="Autorizar Resumos" action="#{autorizacaoResumo.listarResumos}" />
				<t:navigationMenuItem id="projPesCongr_Maninfestar" itemLabel="Manifestar Interesse em Avaliar Trabalhos" action="#{ interesseAvaliarBean.iniciar }"  rendered="#{ interesseAvaliarBean.permissaoAcesso }"/>
				<t:navigationMenuItem id="justificarAusencia" itemLabel="Justificar Aus�ncia" action="#{justificativaCIC.preCadastrar}" rendered="#{justificativaCIC.permissaoAcesso}"/>				
			</t:navigationMenuItem>
			
			<t:navigationMenuItem id="certificadosDeclaracoes" itemLabel="Certificados e Declara��es">
<%-- 				<t:navigationMenuItem id="declaracaoOrientacoes" itemLabel="Declara��o de Orienta��es" action="#{declaracoesPesquisa.listarDeclararoesOrientacoes}" /> --%>
				<t:navigationMenuItem id="declaracaoProjetos" itemLabel="Declara��o de Coordena��o de projetos" action="#{declaracoesPesquisa.listarDeclaracoesCoordenadorProjetos}" />
			</t:navigationMenuItem>
			
			<t:navigationMenuItem id="projPes_AreaConhecimento" itemLabel="Consultar �reas de Conhecimento" actionListener="#{menuDocente.redirecionar}" itemValue="/pesquisa/cadastroAreaConhecimento.do?dispatch=list&page=0"/>
			<t:navigationMenuItem id="projPes_PortalConsultor" itemLabel="Acessar Portal do Consultor" actionListener="#{menuDocente.redirecionar}" itemValue="/verPortalConsultor.do"/>
			<t:navigationMenuItem id="projPes_Comissao" itemLabel="Avalia��o de Projetos" rendered="#{acesso.comissaoPesquisa}">
				<t:navigationMenuItem id="projPesqComissao_Avaliar" itemLabel="Listar Minhas Avalia��es" action="#{avaliacaoProjetoBean.listarAvaliacoes}" itemDisabled="false"/>
				<t:navigationMenuItem id="projPesqComissao_AvaliarProjApoioGP" itemLabel="Avaliar Projetos de Apoio a Grupos de Pesquisa" action="#{avaliacaoProjetoApoioGrupoPesquisaMBean.listarProjetos}" itemDisabled="false"/>
				<t:navigationMenuItem id="projPesqComissao_AvaliarProjApoioNP" itemLabel="Avaliar Projetos de Apoio a Novos Pesquisadores" action="#{avaliacaoProjetoApoioNovosPesquisadoresMBean.listarProjetos}" itemDisabled="false"/>
			</t:navigationMenuItem>
			<t:navigationMenuItem id="projPes_Intervencao" itemLabel="Notificar Inven��o" action="#{invencao.iniciar}" split="true"/>
		</t:navigationMenuItem>
		
       <%-- EXTENS�O --%>
	   <t:navigationMenuItem itemLabel="Extens�o" id="projeto_extensao" icon="/img/icones/extensao_menu.gif">
	
			<t:navigationMenuItem id="projExt_Acoes" itemLabel="A��es de Extens�o">
				
				<t:navigationMenuItem id="projExtAcoes_Submissoes" itemLabel="Submiss�es de Propostas">
					<t:navigationMenuItem id="projExtAcoes_Submeter" itemLabel="Submeter Proposta" action="#{atividadeExtensao.listarCadastrosEmAndamento}" itemDisabled="false" />
					<t:navigationMenuItem id="projExtAcoes_Solicitar" itemLabel="Solicitar Reconsidera��o de Avalia��o" action="#{solicitacaoReconsideracao.iniciarSolicitacaoExtensao}" itemDisabled="false" />
					<t:navigationMenuItem id="projExtAcoes_Consultar" itemLabel="Consultar A��es Submetidas" action="#{atividadeExtensao.preLocalizar}" itemDisabled="false" />
				</t:navigationMenuItem>
				
				<t:navigationMenuItem id="projExtAcoes_Inscricoes" itemLabel="Inscri��es">
					<t:navigationMenuItem id="projExtAcoesInsc_AbrirAlt" itemLabel="Gerenciar Inscri��es" action="#{gerenciarInscricoesCursosEventosExtensaoMBean.listarCursosEventosParaGerenciarInscricao}" />
					<t:navigationMenuItem itemLabel="Question�rios para Inscri��es" action="#{questionarioBean.gerenciarInscricaoAtividade}" id="QuestionarioInscricaoAtividade"/>
				</t:navigationMenuItem>
				
				<t:navigationMenuItem id="projExtAcoes_Gerenciar" itemLabel="Gerenciar A��es">
					<t:navigationMenuItem id="projExtAcoes_MinhasAcoes" itemLabel="Listar Minhas A��es" action="#{atividadeExtensao.listarMinhasAtividades}" itemDisabled="false"/>
					<t:navigationMenuItem id="projExtAcoes_GerEquipes" itemLabel="Gerenciar Equipes Organizadoras" action="#{membroProjeto.gerenciarMembrosProjeto}" />			
					<t:navigationMenuItem id="projExtAcoes_GerParticipantes" itemLabel="Gerenciar Participantes" action="#{listaAtividadesParticipantesExtensaoMBean.listarAtividadesComParticipantesCoordenador}" />
					<t:navigationMenuItem id="projExtAcoes_GerAcoesPendentes" itemLabel="A��es com Tempo de Cadastro Expirado" action="#{expirarTempoCadastro.iniciaBuscaAcoesEncerradas}" />	
				</t:navigationMenuItem>
				
				
			</t:navigationMenuItem> 
	
			<t:navigationMenuItem id="projExt_Plano" itemLabel="Planos de Trabalho">
				<t:navigationMenuItem id="projExtPlano_MeusPlanos" itemLabel="Listar Meus Planos de Trabalho" action="#{planoTrabalhoExtensao.listarPlanosCoordenador}" />			
				<t:navigationMenuItem id="projExtPlano_CadPlanoBolsista" itemLabel="Cadastrar Plano de Trabalho de Bolsista" action="#{planoTrabalhoExtensao.iniciarCadastroPlanoBolsista}" split="true" />
				<t:navigationMenuItem id="projExtPlano_CadPlanoVoluntario" itemLabel="Cadastrar Plano de Trabalho de Volunt�rio" action="#{planoTrabalhoExtensao.iniciarCadastroPlanoVoluntario}" />
				<t:navigationMenuItem id="projExtPlano_IndSubDiscente" itemLabel="Indicar/Substituir Discente" action="#{planoTrabalhoExtensao.iniciarAlterarPlano}" />
			</t:navigationMenuItem>
	
			<t:navigationMenuItem id="projExt_Relatorio" itemLabel="Relat�rios">
				<t:navigationMenuItem id="projExtRel_Bolsista" itemLabel="Relat�rios de Discentes de Extens�o" action="#{relatorioBolsistaExtensao.listarRelatoriosDiscentes}" itemDisabled="false"/>
				<t:navigationMenuItem id="projExtRel_Acoes" itemLabel="Relat�rios de A��es de Extens�o" action="#{relatorioAcaoExtensao.iniciarCadastroRelatorio}" itemDisabled="false"/>
			</t:navigationMenuItem>
	
			<t:navigationMenuItem id="projExt_Comite" itemLabel="Comit� de Extens�o" rendered="#{acesso.comissaoExtensao}">
				<t:navigationMenuItem id="projExtComite_Avaliar" itemLabel="Avaliar Propostas de A��es" itemValue="/extensao/AvaliacaoAtividade/lista.jsf" actionListener="#{menuDocente.redirecionar}"/>
				<t:navigationMenuItem id="projExtComite_AvaFinal" itemLabel="Avalia��o Final de Propostas (Presidente do comit�)" action="#{filtroAtividades.irTelaAvaliarPresidenteComite}" rendered="#{acesso.presidenteComiteExtensao}"/>			
				<t:navigationMenuItem id="projExtComite_Verificar" itemLabel="Verificar Relat�rios de A��es" itemValue="/extensao/RelatorioAcaoExtensao/busca.jsf" actionListener="#{menuDocente.redirecionar}"  split="true"/>				
			</t:navigationMenuItem>
			
			<t:navigationMenuItem id="projExt_Comissao" itemLabel="Comiss�o de Avaliadores Ad Hoc" rendered="#{acesso.pareceristaExtensao}">
				<t:navigationMenuItem id="projExtComissao_Avaliar" itemLabel="Avaliar Propostas" itemValue="/extensao/AvaliacaoAtividade/lista_parecerista.jsf" actionListener="#{menuDocente.redirecionar}"/>
			</t:navigationMenuItem>
			
			
			<t:navigationMenuItem id="projExt_CertDecl" itemLabel="Certificados e Declara��es">
				<t:navigationMenuItem id="projExtCertDecl_Participante" itemLabel="Como Participante ou Membro da Equipe" action="#{documentosAutenticadosExtensao.participacoesServidorUsuarioLogado}"/>
				<t:navigationMenuItem id="projExtCertDecl_Avaliador" itemLabel="Meus Certificados como Avaliador" action="#{certificadoAvaliadorExtensao.irTelaCertificadoAvaliadorAdHoc}"/>
			</t:navigationMenuItem>
			
			<t:navigationMenuItem id="projExt_Editais" itemLabel="Editais de Extens�o" itemValue="/extensao/EditalExtensao/lista.jsf" actionListener="#{menuDocente.redirecionar}"/>
	   </t:navigationMenuItem>		
	

       <%-- PROJETOS --%>
       <%-- A��O ACAD�MICA INTEGRADA --%>
       <t:navigationMenuItem itemLabel="A��es Integradas" id="acoesAssociadas" icon="/img/projetos/associado_menu.png">
            <t:navigationMenuItem id="projAssoc_MeusProjetos" itemLabel="Listar Meus Projetos" action="#{projetoBase.listarMeusProjetos}" itemDisabled="false" />
            <t:navigationMenuItem id="projAssoc_SubmeterNovaProp" itemLabel="Submeter Nova Proposta" action="#{projetoBase.listarCadastrosEmAndamento}" itemDisabled="false"/>
            <t:navigationMenuItem id="projAssoc_Consultar" itemLabel="Consultar Projetos Submetidos" itemValue="/projetos/ProjetoBase/lista.jsf" actionListener="#{menuDocente.redirecionar}"/>         
            <t:navigationMenuItem id="projAssoc_SolReconsAva" itemLabel="Solicitar Reconsidera��o de Avalia��o" action="#{solicitacaoReconsideracao.iniciarSolicitacaoProjetos}" itemDisabled="false" />            
             <t:navigationMenuItem id="projAssoc_PlanosTrabalho" itemLabel="Planos de Trabalho" split="true">
                 <t:navigationMenuItem id="projAssocPlanos_CadastrarBolsista" itemLabel="Cadastrar Plano de Trabalho de Bolsista" action="#{planoTrabalhoProjeto.iniciarCadastroPlano}" itemDisabled="false" />
                 <t:navigationMenuItem id="projAssocPlanos_CadastrarVoluntario" itemLabel="Cadastrar Plano de Trabalho de Volunt�rio" action="#{planoTrabalhoProjeto.iniciarCadastroPlanoVoluntario}" itemDisabled="false" />
                 <t:navigationMenuItem id="projAssocPlanos_IndSubDiscente" itemLabel="Indicar/Substituir Discente" action="#{planoTrabalhoProjeto.iniciarIndicarDiscentePlanoLista}" />
 				 <t:navigationMenuItem id="projAssocPlanos_MeusPlanos" itemLabel="Listar Meus Planos de Trabalho" action="#{planoTrabalhoProjeto.planosCoordenador}" />            
             </t:navigationMenuItem>            
             <t:navigationMenuItem id="projAssocRelatorios" itemLabel="Relat�rios" >
				<t:navigationMenuItem id="projAssocRelatoriosLista" itemLabel="Relat�rios de A��es Integradas" action="#{relatorioAcaoAssociada.iniciarCadastroRelatorio}" itemDisabled="false"/>
			</t:navigationMenuItem>
             <t:navigationMenuItem id="projAssoc_Comissao" itemLabel="Avalia��o de Projetos" rendered="#{acesso.avaliadorAcoesAssociadas || acesso.comissaoIntegrada}">
				<t:navigationMenuItem id="projAssocComissao_Avaliar" itemLabel="Listar Minhas Avalia��es" action="#{avaliacaoProjetoBean.listarAvaliacoes}" itemDisabled="false"/>
			 </t:navigationMenuItem>
              <t:navigationMenuItem id="certicado_declaracaoAssoc" itemLabel="Certificados e Declara��o" rendered="#{acesso.avaliadorAcoesAssociadas || acesso.comissaoIntegrada}">
				<t:navigationMenuItem id="certificadoAvaliador" itemLabel="Meus Certificados como Avaliador" action="#{declaracaoAvaliadorMBean.listarAvaliacoes}" itemDisabled="false"/>
			 </t:navigationMenuItem>
			 <t:navigationMenuItem id="gerParticipantes" itemLabel="Gerenciar Membros do Projeto" action="#{membroProjeto.gerenciarMembrosProjetoAssociados}" itemDisabled="false" />
			 <t:navigationMenuItem id="avaliarProposta" itemLabel="Avaliar Proposta" itemValue="/extensao/AvaliacaoAtividade/lista_avaliacoes.jsf" actionListener="#{menuDocente.redirecionar}"/>			 
       </t:navigationMenuItem>           
        
      <%-- REQUISI��ES/PROJETOS (SIPAC) --%>
      <t:navigationMenuItem itemLabel="Conv�nios" icon="/img/requisicoes.png">
            <t:navigationMenuItem itemLabel="Pr�-projeto">
                    <t:navigationMenuItem itemLabel="Encaminhar Projeto de um Pr�-Projeto" action="#{acessoRequisicoesProjetos.iniciarEncaminharProjeto}" />
                    <t:navigationMenuItem itemLabel="Listar Pr�-Projetos" action="#{acessoRequisicoesProjetos.iniciarEncaminharProjeto}" />
                    <t:navigationMenuItem itemLabel="Submeter Pr�-Projeto" action="#{acessoRequisicoesProjetos.iniciarSubmeterPreProjetos}" />
            </t:navigationMenuItem>         
            <t:navigationMenuItem itemLabel="Projeto/Plano de Trabalho (PROPLAN)">
                <t:navigationMenuItem itemLabel="Submeter Proposta" action="#{acessoRequisicoesProjetos.iniciarSubmeterPropostas}" />
                <t:navigationMenuItem itemLabel="Alterar Proposta"  action="#{acessoRequisicoesProjetos.iniciarAlterarPropostas}"/>
                <t:navigationMenuItem itemLabel="Acompanhar Tramita��o On-line" action="#{acessoRequisicoesProjetos.iniciarAcompanharTramitacaoOnLine}"/>
                <t:navigationMenuItem itemLabel="Membros da Equipe de Trabalho"  action="#{acessoRequisicoesProjetos.iniciarMembrosEquipeTrabalho}" split="true"/>
            </t:navigationMenuItem>
            <t:navigationMenuItem itemLabel="Planilha Or�ament�ria (FUNPEC)">
                <t:navigationMenuItem itemLabel="Cadastrar Plano de Aplica��o Detalhado" action="#{acessoRequisicoesProjetos.iniciarCadastroPlanoAplicacao}" />
                <t:navigationMenuItem itemLabel="Alterar Plano de Aplica��o Detalhado" action="#{acessoRequisicoesProjetos.iniciarCadastroPlanoAplicacao}" />
            </t:navigationMenuItem>
            <t:navigationMenuItem itemLabel="Aditivos" split="true">
                <t:navigationMenuItem itemLabel="Solicitar Aditivo de Conv�nio/Contrato"  action="#{acessoRequisicoesProjetos.iniciarSolicitarAditivo}"/>
                <t:navigationMenuItem itemLabel="Listar Solicita��es"  action="#{acessoRequisicoesProjetos.iniciarListarSolicitacoesAditivo}"/>
            </t:navigationMenuItem>
            <t:navigationMenuItem itemLabel="Autoriza��es/Parecer">
                <t:navigationMenuItem itemLabel="Autorizar Tramita��o" action="#{acessoRequisicoesProjetos.iniciarAutorizarTramitacao}"/>
                <t:navigationMenuItem itemLabel="Emitir" action="#{acessoRequisicoesProjetos.iniciarEmitirParecer}"/>
            </t:navigationMenuItem>
    </t:navigationMenuItem>

    <%-- ACESSO AOS SERVI�OS DA BIBLIOTECA PARA O DOCENTE --%>

    <t:navigationMenuItem id="biblioteca" itemLabel="Biblioteca" icon="/img/icones/biblioteca_menu.gif">

		<t:navigationMenuItem id="bib_Cadastrar" itemLabel="Cadastrar para Utilizar os Servi�os da Biblioteca" action="#{cadastroUsuarioBibliotecaMBean.iniciarAutoCadastro}" icon="/img/biblioteca/novo_usuario.gif" itemDisabled="false" />

        <t:navigationMenuItem id="bib_PesqMaterialAcervo" action="#{ pesquisaInternaBibliotecaMBean.iniciarBusca }" itemLabel="Pesquisar Material no Acervo" split="true"/>
        <t:navigationMenuItem id="bib_PesqArtigoAcervo" action="#{ pesquisaInternaArtigosBibliotecaMBean.iniciarBuscaArtigo }" itemLabel="Pesquisar Artigo no Acervo" />
        
        <t:navigationMenuItem id="bib_Emprestimo" itemLabel="Empr�stimos" split="true">
                <t:navigationMenuItem id="bibEmpr_Renovar" itemLabel="Renovar Meus Empr�stimos" action="#{meusEmprestimosBibliotecaMBean.iniciarVisualizarEmprestimosRenovaveis}" itemDisabled="false" />
                <t:navigationMenuItem id="bibEmpr_Historico" itemLabel="Meu Hist�rico de Empr�stimos" action="#{emiteHistoricoEmprestimosMBean.iniciaUsuarioLogado}" itemDisabled="false" />
                <t:navigationMenuItem id="bibEmpr_Emitir_GRU" itemLabel="Imprimir GRU para pagamentos de multas" action="#{emitirGRUPagamentoMultasBibliotecaMBean.listarMinhasMultasAtivas}" rendered="#{emitirGRUPagamentoMultasBibliotecaMBean.sistemaTrabalhaComMultas}" itemDisabled="false" />
        </t:navigationMenuItem>
        
         <t:navigationMenuItem id="bib_dessiminacao_informacao" itemLabel="Dissemina��o Seletiva da Informa��o" split="true">
			<t:navigationMenuItem id="bib_cadastrar_interesse" itemLabel="Cadastrar Interesse" action="#{configuraPerfilInteresseUsuarioBibliotecaMBean.iniciar}" itemDisabled="false" />
		</t:navigationMenuItem>
        
        <t:navigationMenuItem id="bib_VerificarSituacao" itemLabel="Verificar minha Situa��o / Emitir Documento de Quita��o  " action="#{verificaSituacaoUsuarioBibliotecaMBean.verificaSituacaoUsuarioAtualmenteLogado}" itemDisabled="false" />

		<t:navigationMenuItem id="bib_informacoes_usuario" itemLabel="Informa��es ao Usu�rio">
			<t:navigationMenuItem id="bib_visualizarVinculos" itemLabel="Visualizar meus V�nculos no Sistema " action="#{verificaVinculosUsuarioBibliotecaMBean.iniciarVerificacaoMeusVinculos}" itemDisabled="false" />
			<t:navigationMenuItem id="bib_Visualizar_Politicas" itemLabel="Visualizar as Pol�ticas de Empr�stimo" action="#{visualizarPoliticasDeEmprestimoMBean.iniciarVisualizacao}" itemDisabled="false" />
		</t:navigationMenuItem>

		<t:navigationMenuItem id="bib_revervas" itemLabel="Reservas de Materiais" split="true" rendered="#{solicitarReservaMaterialBibliotecaMBean.sistemaTrabalhaComReservas}"> 
			<t:navigationMenuItem id="bib_revervas_visualizar" itemLabel="Visualizar Minhas Reservas" action="#{visualizarReservasMaterialBibliotecaMBean.iniciaVisualizacaoMinhasReservas}" itemDisabled="false"  />
			<t:navigationMenuItem id="bib_revervas_solicitar" itemLabel="Solicitar Nova Reserva " action="#{solicitarReservaMaterialBibliotecaMBean.iniciarReservaPeloUsuario}" itemDisabled="false"  />
		</t:navigationMenuItem>

		<%-- Ainda n�o est� em produ��o --%>
		<t:navigationMenuItem id="bib_solicitacoes" itemLabel="Servi�os ao Usu�rio">
			<t:navigationMenuItem id="bib_SolOrientacao" itemLabel="Agendamento de Orienta��o" action="#{solicitacaoOrientacaoMBean.verMinhasSolicitacoes}" rendered="#{solicitacaoOrientacaoMBean.quantidadeBibliotecasRealizandoOrientacaoNormalizacao > 0}" />
	        <t:navigationMenuItem id="bib_SolNormalizacao" itemLabel="Normaliza��o" action="#{ solicitacaoServicoDocumentoMBean.verMinhasSolicitacoes }" rendered="#{solicitacaoServicoDocumentoMBean.quantidadeBibliotecasRealizandoNormalizacao > 0}" />
	        <t:navigationMenuItem id="bib_SolCatalocacao" itemLabel="Cataloga��o na Fonte" action="#{ solicitacaoServicoDocumentoMBean.verMinhasSolicitacoes }" rendered="#{solicitacaoServicoDocumentoMBean.quantidadeBibliotecasRealizandoCatalogacaoNaFonte > 0}" />
        </t:navigationMenuItem>
         
        
        <%-- Casos de uso suspensos
        <t:navigationMenuItem id="bib_SolLevantamentoBibInfraEstru" itemLabel="Solicitar Levantamento Bibliogr�fico e Infra-estrutura" action="#{levantamentoBibliograficoInfraMBean.listarSolicitacoesLevantIndividualPorUsuario}" />

        
        <ufrn:checkRole papeis="${ levantamentoInfraMBean.papeisUsuario }">
        	<t:navigationMenuItem itemLabel="Levantamento de Infra-Estrutura" action="#{ levantamentoInfraMBean.listarParaUsuario }" id="levantamentoInfra" />
        </ufrn:checkRole>
        --%>
         
        <t:navigationMenuItem id="bib_compras" itemLabel="Compras de Livro">
        	<t:navigationMenuItem id="bib_Sol_Compra_Livro" actionListener="#{ menuDocente.redirecionar }" itemLabel="Solicitar Compra de Livros" itemValue="/entrarSistema.do?sistema=sipac&url=listaReqBiblioteca.do?acao=474"/>
        	<t:navigationMenuItem id="bib_Acom_Compra_Livro" actionListener="#{ menuDocente.redirecionar }" itemLabel="Acompanhar Solicita��es de Compra de Livros" itemValue="/entrarSistema.do?sistema=sipac&url=manipulaReqBiblioteca.do?acao=480" />
        	<t:navigationMenuItem id="bib_Relatorio_Compras" actionListener="#{ menuDocente.redirecionar }" itemLabel="Relat�rio de Novas Compras" itemValue="/public/biblioteca/buscaPublicaAquisicoes.jsf?aba=p-biblioteca" />
        	<t:navigationMenuItem id="bib_Relatorio_Aqui" actionListener="#{ menuDocente.redirecionar }" itemLabel="Relat�rio de Novas Aquisi��es" itemValue="/public/biblioteca/buscaPublicaNovasAquisicoes.jsf?aba=p-biblioteca" />
        </t:navigationMenuItem>
        
       
        
    </t:navigationMenuItem> 
    


	<%-- PRODU��O INTELECTUAL --%>
	<t:navigationMenuItem id="producao_intelectual" itemLabel="Produ��o Intelectual" icon="/img/icones/producao_menu.gif" rendered="#{acesso.docenteUFRN}">
		<t:navigationMenuItem id="prodInt_MinhasProd" itemLabel="Minhas Produ��es">
			<t:navigationMenuItem id="prodIntMinhasProd_Cadastrar" itemLabel="Cadastrar novas" itemValue="/prodocente/nova_producao.jsf" actionListener="#{menuDocente.redirecionar}" />
			<t:navigationMenuItem id="prodIntMinhasProd_ListarCad" itemLabel="Listar cadastradas" itemValue="/prodocente/listar_producao.jsf" actionListener="#{menuDocente.redirecionar}" />
			<t:navigationMenuItem id="prodIntMinhasProd_Validar" itemLabel="Validar minhas produ��es" action="#{autoValidacaoProducaoBean.listarPendentes}" />
			<t:navigationMenuItem id="prodIntMinhasProd_Importar" itemLabel="Importar produ��es do Lattes" itemValue="/prodocente/producao/ImportLattes/form.jsf" actionListener="#{menuDocente.redirecionar}" split="true"/>
			<t:navigationMenuItem id="prodIntMinhasProd_Autorizar_Importacao" itemLabel="Autorizar Importa��o do Curr�culo Lattes" action="#{pessoaLattesMBean.iniciar}" />
		</t:navigationMenuItem>

		<t:navigationMenuItem id="prodInt_FormAcademica" itemLabel="Forma��o Acad�mica" action="#{formacaoAcademica.listar}" />

		<t:navigationMenuItem id="prodInt_OutrasAtividades" itemLabel="Outras Atividades">
			<t:navigationMenuItem id="prodIntOutAtiv_Minicurso" itemLabel="Mini-Curso" action="#{miniCurso.listar}"/>
			<t:navigationMenuItem id="prodIntOutAtiv_Orientacoes" itemLabel="Orienta��es">
				<t:navigationMenuItem id="prodIntOutAtivOrien_Estagio" itemLabel="Est�gios" action="#{estagio.listar }"/>
				<t:navigationMenuItem id="prodIntOutAtivOrien_TrabFimCurso" itemLabel="Trabalho Final de Curso" action="#{trabalhoFimCurso.listar }"/>
				<t:navigationMenuItem id="prodIntOutAtivOrien_PosGrad" itemLabel="Orienta��es P�s-Gradua��o" action="#{teseOrientada.listarStricto}"/>
				<t:navigationMenuItem id="prodIntOutAtivOrien_Bolsista" itemLabel="Bolsista IC Externo" action="#{orientacoesICExternoBean.direcionar}"/>
			</t:navigationMenuItem>
		</t:navigationMenuItem>

		<t:navigationMenuItem id="prodInt_OperacoesChefe" itemLabel="Opera��es do Chefe" rendered="#{acesso.chefeDepartamento}">
			<t:navigationMenuItem id="prodInt_DocenteQualif" itemLabel="Docente em Qualifica��o">
				<t:navigationMenuItem id="prodInt_DocenteQualif_cad" itemLabel="Cadastrar" action="#{qualificacaoDocente.preCadastrar}"/>
				<t:navigationMenuItem id="prodInt_DocenteQualif_list" itemLabel="Listar/Alterar" action="#{qualificacaoDocente.listar}"/>
			</t:navigationMenuItem>
			<t:navigationMenuItem id="prodIntOpChefe_Chefias" itemLabel="Chefias" action="#{chefia.listar}"/>
			<%--<t:navigationMenuItem itemLabel="Consolidar Valida��es" action="#{consolidacaoProducaoBean.listarPendentes}"/>--%>
		</t:navigationMenuItem>

		<t:navigationMenuItem id="prodInt_RelatorioDocente" itemLabel="Relat�rios do Docente" split="true">
			<t:navigationMenuItem id="prodIntRelDoc_TodasAtiv" itemLabel="Relat�rio de Todas as Atividades" action="#{todaProducao.exibirOpcoes}"/>
			<t:navigationMenuItem id="prodIntRelDoc_ProdAcademica" itemLabel="Quantitativos de Produ��o Acad�mica" action="#{prodQuantitativo.verFormulario}"/>
			<t:navigationMenuItem id="prodIntRelDoc_AvaConcessaoCota" itemLabel="Relat�rio de Avalia��o para Concess�o de Cotas" action="#{producao.verRelatorioCotas}"/>
			<t:navigationMenuItem id="prodIntRelDoc_ProdDocente" itemLabel="Relat�rio de Produtividade Docente (Antigo GED)" action="#{producao.verRelatorioProgressao}"/>
			<t:navigationMenuItem id="prodIntRelDoc_Individual" itemLabel="Relat�rio Individual do Docente (RID)" action="#{relatorioRID.popular}"/>
		</t:navigationMenuItem>

		<t:navigationMenuItem id="prodInt_RelatorioDepartamento" itemLabel="Relat�rios do Departamento">
			<t:navigationMenuItem id="prodIntRelDep_SituacAtual" itemLabel="Situa��o Docente Atual" action="#{relatoriosDepartamentoCpdi.iniciarSituacaoDocente}"/>
			<t:navigationMenuItem id="prodIntRelDep_IndEnsino" itemLabel="Indicadores de Ensino" action="#{relatoriosDepartamentoCpdi.iniciarIndicadoresEnsino}"/>
			<t:navigationMenuItem id="prodIntRelDep_DistTurma" itemLabel="Distribui��o de Turmas" action="#{relatoriosDepartamentoCpdi.iniciarDistribuicaoTurmas}"/>
			<t:navigationMenuItem id="prodIntRelDep_IndPesquisa" itemLabel="Indicadores de Pesquisa" action="#{relatoriosDepartamentoCpdi.iniciarIndicadoresPesquisa}"/>
			<t:navigationMenuItem id="prodIntRelDep_IndExtensao" itemLabel="Indicadores de Extens�o" action="#{relatoriosDepartamentoCpdi.iniciarIndicadoresExtensao}"/>
			<t:navigationMenuItem id="prodIntRelDep_TurmaporDepart" itemLabel="Relat�rio de Turmas por Departamento" action="#{relatoriosDepartamentoCpdi.iniciarTurmasDepartamentoDocente}"/>
		</t:navigationMenuItem>

		<t:navigationMenuItem id="prodInt_DiretorCentro" itemLabel="Opera��es do Diretor de Centro" rendered="#{acesso.diretorCentro}">
			<t:navigationMenuItem id="prodIntDiretCen_Consolidar" itemLabel="Consolidar Valida��es dos Chefes" action="#{consolidacaoProducaoBean.listarPendentes}"/>			
		</t:navigationMenuItem>

		<t:navigationMenuItem id="prodInt_AcervoDigital" itemLabel="Acervo Digital" action="#{acervoProducao.verAcervoDigital}" split="true"/>
	</t:navigationMenuItem>


	<%-- ACESSO A OUTROS PORTAIS --%>
	<%-- Foi solicitado para remover na tarefa: 23269 
	<t:navigationMenuItem itemLabel="Portais" icon="/img/icones/administracao_menu.gif">
		<t:navigationMenuItem itemLabel="Portal Docente" actionListener="#{menuDocente.redirecionar}" itemValue="/portais/docente/docente.jsf"/>
		<t:navigationMenuItem itemLabel="Coordenador Gradua��o"  itemValue="/verMenuGraduacao.do?destino=coordenacao" actionListener="#{menuDocente.redirecionar}" rendered="#{acesso.coordenadorCursoGrad}" split="true"/>
		<t:navigationMenuItem itemLabel="Coordenador Lato Sensu" itemValue="/verMenuLato.do" actionListener="#{menuDocente.redirecionar}" rendered="#{acesso.coordenadorCursoLato}" />
		<t:navigationMenuItem itemLabel="Coordenador Stricto Sensu" itemValue="/verMenuStricto.do?portal=programa" actionListener="#{menuDocente.redirecionar}" rendered="#{acesso.coordenadorCursoStricto}" />
	</t:navigationMenuItem>
	--%>
	
	<%-- AMBIENTES VIRTUAIS --%>
	<t:navigationMenuItem itemLabel="Ambientes Virtuais" icon="/img/icones/amb_virt.png">
		<t:navigationMenuItem itemLabel="Turmas Virtuais" id="turmaVirtual">
			<t:navigationMenuItem id="turmaVirtual_Abertas" action="#{ turmaVirtual.iniciarTurmasAbertas }" itemLabel="Turmas Virtuais Abertas"/>
			<t:navigationMenuItem id="turmaVirtual_Listar" action="#{ turmaVirtual.listarTurmasVirtuais }" itemLabel="Listar Turmas Virtuais"/>
			<t:navigationMenuItem id="turmaVirtual_PublicarRemover" action="#{ turmaVirtual.iniciarPublicacaoTurma }" itemLabel="Publicar/Remover Turma Virtual para comunidade externa"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Comunidades Virtuais" id="comunidadeVirtual">
			<t:navigationMenuItem id="comunidadeVirtual_OQueEIsso" actionListener="#{comunidadeVirtualMBean.oQueEIsso}" itemLabel="O que � isso?"/>
			<t:navigationMenuItem id="comunidadeVirtual_BuscarComunidade" actionListener="#{ buscarComunidadeVirtualMBean.criar }" itemLabel="Buscar Comunidades Virtuais"/>
			<t:navigationMenuItem id="comunidadeVirtual_CriarComunidade" actionListener="#{ comunidadeVirtualMBean.criar }" itemLabel="Criar Comunidade Virtual"/>
			<t:navigationMenuItem id="comunidadeVirtual_MinhasComunidades" action="#{ buscarComunidadeVirtualMBean.exibirTodasComunidadesDocente }" itemLabel="Minhas Comunidades"/>		
		</t:navigationMenuItem>
	</t:navigationMenuItem>	
		
	<%-- OUTROS --%>
	<t:navigationMenuItem itemLabel="Outros" icon="/img/menu/outros.png">
		<t:navigationMenuItem id="outros_PortalArquivos" action="#{ arquivoUsuario.portaArquivos }" itemLabel="Porta Arquivos"/>
		<t:navigationMenuItem id="ouvidoria" itemLabel="Ouvidoria" rendered="#{acesso.docenteUFRN }">
			<t:navigationMenuItem id="comunidadeUniversitaria" itemLabel="Comunidade Universit�ria">
				<t:navigationMenuItem id="entrarEmContato" itemLabel="Entrar em Contato" action="#{ manifestacaoDocente.preCadastrar }" itemDisabled="false"/>
				<t:navigationMenuItem id="acompanharManifestacoes" itemLabel="Acompanhar Minhas Manifesta��es" action="#{ analiseManifestacaoDocente.listarManifestacoes }" itemDisabled="false"/>
			</t:navigationMenuItem>
			<t:navigationMenuItem id="responsavelUnidade" itemLabel="Respons�vel por Unidade" rendered="#{acesso.chefeUnidade }">
				<t:navigationMenuItem id="analisarPendentesUnidade" itemLabel="Analisar/Designar Manifesta��es Pendentes" action="#{ analiseManifestacaoResponsavel.listarPendentes }" itemDisabled="false"/>
				<t:navigationMenuItem id="acompanharManifestacoesUnidade" itemLabel="Acompanhar Manifesta��o da Unidade" action="#{ analiseManifestacaoResponsavel.listarManifestacoes }" itemDisabled="false"/>
			</t:navigationMenuItem>
			<t:navigationMenuItem id="designadoResposta" itemLabel="Designado para Resposta">
				<t:navigationMenuItem id="analisarPendentesDesignado" itemLabel="Analisar Manifesta��es Pendentes" action="#{ analiseManifestacaoDesignado.listarPendentes }" itemDisabled="false"/>
				<t:navigationMenuItem id="acompanharManifestacoesDesignadas" itemLabel="Acompanhar Manifesta��o Designadas" action="#{ analiseManifestacaoDesignado.listarManifestacoes }" itemDisabled="false"/>
			</t:navigationMenuItem>
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Fiscal do Vestibular" id="fiscal" icon="/img/icones/producao_menu.gif">
			<t:navigationMenuItem id="fiscal_Inscricao" itemLabel="Inscri��o" action="#{inscricaoSelecaoFiscalVestibular.iniciarInscricaoFiscal}"/>
			<t:navigationMenuItem id="fiscal_ResultSelecao" itemLabel="Resultado da Sele��o" action="#{inscricaoSelecaoFiscalVestibular.exibeResultadoDoProcessamento}"/>
			<t:navigationMenuItem id="fiscal_LocaisAplicacaoProva" itemLabel="Lista de Locais de Aplica��o de Prova" action="#{relatoriosVestibular.iniciarListaLocaisProva}"/>
			<t:navigationMenuItem id="fiscal_Comprovante" itemLabel="Comprovante de Inscri��o" action="#{inscricaoSelecaoFiscalVestibular.exibeComprovanteInscricao}"/>
			<t:navigationMenuItem id="fiscal_JustificativaAusencia" itemLabel="Justificativa de Aus�ncia" action="#{justificativaAusencia.preCadastrar}"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem id="outros_BuscaAlunoCadUnico" itemLabel="Busca de Aluno no Cadastro �nico" action="#{buscaDiscenteCadastroUnico.iniciarBusca}" />
		<t:navigationMenuItem
				id="cartaoRestaurante" itemLabel="Saldo do Cart�o do Restaurante" icon="/img/icones/restaurante_small.gif"
				actionListener="#{menuDocente.redirecionar}"
				itemValue="/entrarSistema.do?sistema=sipac&url=restaurante/vendas/saldo_cartao.jsf?voltar=/sigaa/verPortalDocente" />
		
	</t:navigationMenuItem>
	
</t:jscookMenu>
</h:form>
</c:if>
</div>
</div>
</ufrn:subSistema>
<%-- MENU DE OPÇÕES PARA O DOCENTE --%>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@taglib uri="http://java.sun.com/jsf/core"  prefix="f" %>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<div id="menu-dropdown">
<div class="wrapper">
<h:form>
<input type="hidden" name="jscook_action"/>

<t:jscookMenu layout="hbr" theme="ThemeOffice" styleLocation="/css/jscookmenu">
	
	<t:navigationMenuItem itemLabel="Ensino" id="ensino" icon="/img/icones/ensino_menu.gif">
		<t:navigationMenuItem id="avaliacaoInstitucional" itemLabel="Avaliação Institucional" rendered="#{usuario.tutor.presencial}">
			<t:navigationMenuItem id="preencherAvaliacaoInstitucional" itemLabel="Preencher a Avaliação Institucional" action="#{ calendarioAvaliacaoInstitucionalBean.listar }" />
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Consultar Cursos" actionListener="#{menuDocente.redirecionar}" itemValue="/geral/curso/busca_geral.jsf"  itemDisabled="false"/>
		<t:navigationMenuItem itemLabel="Consultar Disciplinas" actionListener="#{menuDocente.redirecionar}"  itemValue="/geral/componente_curricular/busca_geral.jsf" itemDisabled="false"/>
		<t:navigationMenuItem itemLabel="Consultar Turma" action="#{ buscaTurmaBean.popularBuscaGeral }" itemDisabled="false" > </t:navigationMenuItem>
		
		<t:navigationMenuItem itemLabel="Definir Horário" action="#{tutorOrientador.definirHorario}"  itemDisabled="false"/>
	</t:navigationMenuItem>

	<t:navigationMenuItem itemLabel="Alunos" icon="/img/group.png" rendered="#{usuario.tutor.presencial && usuario.tutor.acessoCompleto }">
		<t:navigationMenuItem itemLabel="Avaliar Aluno" action="#{ fichaAvaliacaoEad.iniciar }"  itemDisabled="false"/>
		
		<t:navigationMenuItem itemLabel="Matricular Alunos em Turmas" action="#{matriculaGraduacao.iniciarSolicitacaoMatricula}" itemDisabled="false" rendered="#{!portalTutor.alunoEadFazMatriculaOnline}"/>
		<t:navigationMenuItem itemLabel="Analisar Solicitações de Matrícula" action="#{analiseSolicitacaoMatricula.iniciar}" itemDisabled="false" rendered="#{portalTutor.alunoEadFazMatriculaOnline }"/>

		<t:navigationMenuItem itemLabel="Trancar Matrícula de Aluno" action="#{trancamentoMatriculaTutor.buscarDiscente}"  itemDisabled="false"/>
		<t:navigationMenuItem itemLabel="Ver Alunos Orientados" action="#{portalTutor.verAlunos }"  itemDisabled="false"/>
		<t:navigationMenuItem itemLabel="Visualizar avaliações do Aluno" action="#{fichaAvaliacaoEad.visualizarAvaliacoes }" itemDisabled="false" />
	</t:navigationMenuItem>
</t:jscookMenu>
</h:form>

</div>
</div>
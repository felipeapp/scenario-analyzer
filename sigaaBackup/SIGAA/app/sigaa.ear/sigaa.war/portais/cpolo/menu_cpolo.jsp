<%-- MENU DE OPÇÕES PARA O DOCENTE --%>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@taglib uri="http://java.sun.com/jsf/core"  prefix="f" %>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<div id="menu-dropdown">
<div class="wrapper">
<h:form id="menu">
<input type="hidden" name="jscook_action"/>

<t:jscookMenu layout="hbr" theme="ThemeOffice" styleLocation="/css/jscookmenu">
	
	<t:navigationMenuItem itemLabel="Ensino" id="ensino" icon="/img/icones/ensino_menu.gif">
		
		<t:navigationMenuItem itemLabel="Consultar Cursos" actionListener="#{menuDocente.redirecionar}" itemValue="/geral/curso/busca_geral.jsf"  itemDisabled="false"/>
		<t:navigationMenuItem itemLabel="Consultar Disciplinas" action="#{componenteCurricular.popularBuscaGeral}"  itemDisabled="false"/>
		<t:navigationMenuItem itemLabel="Consultar Turma" action="#{ buscaTurmaBean.popularBuscaGeral }" itemDisabled="false" > </t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Editar dados do Pólo" action="#{poloBean.atualizarPolo}" itemDisabled="false" > </t:navigationMenuItem>
		
	</t:navigationMenuItem>

	<t:navigationMenuItem itemLabel="Tutores" icon="/img/group.png">
		
		<t:navigationMenuItem itemLabel="Listar tutores" action="#{ portalCoordPolo.listagemTurores }" itemDisabled="false"/>
		
		<t:navigationMenuItem itemLabel="Definir Horário de Atendimento" action="#{portalCoordPolo.definirHorarioTutorPeloCoordenadorPolo}"  itemDisabled="false"> </t:navigationMenuItem>
		
	</t:navigationMenuItem>
	
	<t:navigationMenuItem itemLabel="Relatórios" icon="/img/group.png">
		
		<t:navigationMenuItem itemLabel="Alunos por Pólo/Curso" action="#{ portalCoordPolo.relatorioAlunosPoloForm }" itemDisabled="false"/>
		<t:navigationMenuItem itemLabel="Horários de Tutores e Discentes" action="#{portalCoordPolo.relatorioHorariosTutoresDiscentes}"  itemDisabled="false"/>
		
	</t:navigationMenuItem>
	
</t:jscookMenu>
</h:form>

</div>
</div>
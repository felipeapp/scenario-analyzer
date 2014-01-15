<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:view>
<%@include file="/portais/turma/cabecalho.jsp"%>

<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
	tinyMCE.init({
    	mode : "textareas",
        theme : "advanced",
        width : "100%",
        height : "250",
        language : "pt_br",
        plugins : "fullscreen, zoom, searchreplace, separator,  cut, copy, paste, forecolor, backcolor, charmap, visualaid",
        theme_advanced_buttons3_add : "fullscreen, zoom, ltr,rtl, search,replace,separator,  cut, copy, paste, forecolor, backcolor, charmap, visualaid",
        fullscreen_settings : {
        	theme_advanced_path_location : "top"
        },
        extended_valid_elements : "hr[class|width|size|noshade]"
	});
</script>
<style type="text/css">
fieldset { padding: 10px; }
.mceEditorContainer { margin: 0 auto; }
span.ementa { padding: 5px; }
span.ementa h5 { text-align: center; padding: 10px; font-size: 1em; }
</style>
<h3>Plano de Ensino</h3>

<h:outputText value="#{ portalTurma.create }"/>
<h:form>

<h:messages showDetail="true"/>

<fieldset>
<legend>Dados do Componente Curricular</legend>

<table cellpadding="3">
	<tr>
		<td><strong>Código:</strong> </td>
		<td>${ portalTurma.obj.turma.disciplina.codigo }</td>
	</tr>
	<tr>
		<td><strong>Disciplina:</strong> </td>
		<td>${ portalTurma.obj.turma.disciplina.detalhes.nome }</td>
	</tr>
	<tr>
		<td><strong>Turma:</strong> </td>
		<td>${ portalTurma.obj.turma.codigo }</td>
	</tr>
	<tr>
		<td><strong>Período:</strong> </td>
		<td>${ portalTurma.obj.turma.anoPeriodo }</td>
	</tr>
	<tr>
		<td><strong>Horário:</strong> </td>
		<td>${ portalTurma.obj.turma.descricaoHorario }</td>
	</tr>
	<tr>
		<td><strong>Professor(es):</strong> </td>
		<td>
			<c:forEach var="docente" items="${ portalTurma.obj.turma.docentes }">
				${ docente.pessoa.nome }<br/>
			</c:forEach>
		</td>
	</tr>
	<tr>
		<td><strong>Carga Horária Total:</strong> </td>
		<td>${ portalTurma.obj.turma.disciplina.detalhes.chTotal } horas</td>
	</tr>
	<tr>
		<td><strong>Carga Horária de Aulas:</strong> </td>
		<td>${ portalTurma.obj.turma.disciplina.detalhes.chAula } horas</td>
	</tr>
	<tr>
		<td><strong>Carga Horária de Laboratório:</strong> </td>
		<td>${ portalTurma.obj.turma.disciplina.detalhes.chLaboratorio } horas</td>
	</tr>
	<tr>
		<td><strong>Carga Horária de Estágio:</strong> </td>
		<td>${ portalTurma.obj.turma.disciplina.detalhes.chEstagio } horas</td>
	</tr>
</table>
</fieldset>

<fieldset>
<legend>Objetivos</legend>
<t:inputTextarea value="#{ portalTurma.obj.objetivos }"/>
</fieldset>

<fieldset>
<legend>Ementa</legend>
<span class="ementa">
	<c:if test="${ not empty portalTurma.obj.turma.disciplina.detalhes.ementa }">
	${ portalTurma.obj.turma.disciplina.detalhes.ementa }
	</c:if>
	<c:if test="${ empty portalTurma.obj.turma.disciplina.detalhes.ementa }">
	<h5>Não há ementa cadastrada para este componente curricular</h5>
	</c:if>
</span>
</fieldset>

<fieldset>
<legend>Metodologia</legend>
<t:inputTextarea value="#{ portalTurma.obj.metodologia }"/>
</fieldset>

<fieldset>
<legend>Bibliografia</legend>
<t:inputTextarea value="#{ portalTurma.obj.bibliografia }"/>
</fieldset>

<p align="center">
	<br/>
	<input type="hidden" name="idTurma" value="${ portalTurma.obj.turma.id  }"/>
	<h:inputHidden value="#{ portalTurma.obj.id }"/>
	<h:commandButton value="Salvar Dados" action="#{portalTurma.cadastrar}" />
	<h:commandButton value="Cancelar" action="#{portalTurma.cancelar}" />
</p>

</h:form>
	
</f:view>
<%@include file="/portais/turma/rodape.jsp"%>

<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<f:view>
<%@include file="/portais/turma/cabecalho.jsp"%>
<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>

<script type="text/javascript">
tinyMCE.init({ mode : "textareas", theme : "advanced", width : "95%", height : "200", language : "pt_br",         plugins : "table, preview, iespell, print, fullscreen, advhr, directionality, searchreplace, insertdatetime, paste", theme_advanced_buttons3_add : "preview, emotions, iespell, print, fullscreen, zoom, advhr, ltr,rtl, search,replace,insertdate,inserttime, anchor, newdocument, separator, cut, copy, paste, forecolor, backcolor, charmap, visualaid", plugin_preview_width : "500", plugin_preview_height : "600", fullscreen_settings : { theme_advanced_path_location : "top" }, extended_valid_elements : "hr[class|width|size|noshade]", plugin_insertdate_dateFormat : "%Y-%m-%d", plugin_insertdate_timeFormat : "%H:%M:%S" });
</script>

<h3>Tópicos de Aula</h3>

<h:form>
	<h:messages showDetail="true" />
	<h:outputText value="#{ util.create }"/>
	<h:inputHidden value="#{ topicoAula.obj.id }"/>
	
	<fieldset>
	<legend>Novo Tópico</legend>
	<ul>
		<li>
			<label class="required" for="inicio">Data Inicial <span class="required">&nbsp;</span></label>
			<h:selectOneMenu value="#{topicoAula.obj.data}" id="inicio">
				<f:convertDateTime pattern="dd/MM/yyyy" />
				<f:selectItems value="#{topicoAula.aulasCombo}" />
			</h:selectOneMenu>
		</li>
		<li>
			<label class="required" for="inicio">Data Final <span class="required">&nbsp;</span></label>
			<h:selectOneMenu value="#{topicoAula.obj.fim}" id="fim">
				<f:convertDateTime pattern="dd/MM/yyyy" />
				<f:selectItems value="#{topicoAula.aulasCombo}" />
			</h:selectOneMenu>
		</li>
		<li>
			<label for="pai">Tópico Pai </label>
			<h:selectOneMenu value="#{topicoAula.obj.topicoPai.id}">
				<f:selectItem itemValue="0" itemLabel="-- NENHUM --" />
				<f:selectItems value="#{topicoAula.allComboNivel}" />
			</h:selectOneMenu>
		</li>
		<li>
			<label for="descricao" class="required">Descrição <span class="required">&nbsp;</span></label>
			<h:inputText value="#{topicoAula.obj.descricao}" style="width: 95%" id="descricao" />
		</li>
		<li>
			<label for="conteudo" class="required">Conteúdo <span class="required">&nbsp;</span></label>
			<t:inputTextarea value="#{ topicoAula.obj.conteudo }" />
		</li>
		
		<li class="botoes">
			<h:commandButton action="#{topicoAula.cadastrar}" value="#{ topicoAula.confirmButton }" /> 
			<h:commandButton action="#{topicoAula.cancelar}" value="Cancelar" immediate="true"/>
		</li>
	</ul>
	</fieldset>
</h:form>

<br/>

<c:set var="topicos" value="${ topicoAula.allTurma }"/>
<c:if test="${ not empty topicos }">
<h:form>
<table width="99%">
<caption style="text-align: center; font-weight: bold; margin: 10px auto;">Lista de Tópicos Cadastrados</caption>
<thead>
<tr><th>Descrição</th><th>Tópico Pai</th><th>Início</th><th>Fim</th><th></th><th></th></tr>
</thead>
<c:forEach var="topico" items="${ topicos }">
<jsp:setProperty name="util" property="id" value="${ topico.id }"/>
<tr>
	<td>${ topico.descricao }</td><td>${ empty topico.topicoPai.descricao ? '--' : topico.topicoPai.descricao }</td>
	<td><fmt:formatDate value="${ topico.data }" pattern="dd/MM/yyyy"/></td>
	<td><fmt:formatDate value="${ topico.fim }" pattern="dd/MM/yyyy"/></td>
	<td width="20">
		<h:commandLink action="#{topicoAula.atualizar}">
			<h:graphicImage value="/img/alterar.gif"/>
			<f:param name="id" value="#{ util.id }"/>
		</h:commandLink>
	</td>
	<td width="25">
		<h:commandLink action="#{topicoAula.remover}"  onclick="return(confirm('Deseja realmente excluir este registro?'))">
			<h:graphicImage value="/img/delete.gif"/>
			<f:param name="id" value="#{ util.id }"/>
		</h:commandLink>
	</td>
</tr>
</c:forEach>
</table>
</h:form>
</c:if>

</f:view>

<%@include file="/portais/turma/rodape.jsp"%>

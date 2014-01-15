<%@include file="/ava/cabecalho.jsp"%>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form>


<fieldset>
<legend>Lista de Presença &gt; Selecionar Sub-Turma</legend>
<br />

<div class="infoAltRem">
<img src="${ctx}/img/seta.gif"/> : Selecionar Sub-turma
</div>

<table class="listing">
<thead>
<tr><th>Sub-Turma</th><th></th></tr>
</thead>
<tbody>
<c:forEach var="st" items="#{ turmaVirtual.subTurmasDocente }" varStatus="loop">
<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }"><td class="first">${ st.descricaoSemDocente }</td>
<td class="icon">
<h:commandLink action="#{turmaVirtual.escolherSubTurmaListaPresenca}">
	<h:graphicImage value="/img/seta.gif"/>
	<f:param name="id" value="#{st.id}"/>
</h:commandLink>
</td></tr>
</c:forEach>
</tbody>
</table>
</fieldset>
</h:form>

</f:view>
<%@include file="/ava/rodape.jsp"%>

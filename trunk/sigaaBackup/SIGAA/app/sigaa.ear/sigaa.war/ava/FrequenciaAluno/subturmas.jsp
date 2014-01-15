<%@include file="/ava/cabecalho.jsp"%>

<f:view>
<a4j:keepAlive beanName="frequenciaAluno" />
<%@include file="/ava/menu.jsp" %>
<h:form>


<fieldset>
<c:if test="${!frequenciaAluno.lancarEmPlanilha}">
<legend>Lançar Frequência &gt; Selecionar Sub-Turma</legend>
</c:if>
<c:if test="${frequenciaAluno.lancarEmPlanilha}">
<legend>Lançar Freq. em Planilha &gt; Selecionar Sub-Turma</legend>
</c:if>
<br />

<div class="infoAltRem">
<img src="${ctx}/img/seta.gif"/>: Selecionar Sub-turma
</div>

<table class="listing">
<thead>
<tr><th>Sub-Turma</th><th></th></tr>
</thead>
<tbody>
<c:forEach var="st" items="#{ turmaVirtual.subTurmasDocente }" varStatus="loop">
<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }"><td class="first">${ st.descricaoSemDocente }</td>
<td class="icon">
<h:commandLink action="#{frequenciaAluno.escolherSubTurma}" target="#{frequenciaAluno.lancarEmPlanilha ? '_blank' : '' }">
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

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h:form>
<h2><ufrn:subSistema/> &gt; Lançar Notas &gt; Selecionar Subturma</h2>

<div class="infoAltRem">
<img src="${ctx}/img/seta.gif"/> : Selecionar Subturma
</div>

<table class="listagem">
<caption>Subturmas de ${ turmaVirtual.turma.descricaoSemDocente }</caption>
<c:forEach var="st" items="#{ turmaVirtual.subTurmasDocente }" varStatus="loop">
<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }"><td>${ st.descricaoSemDocente }</td>
<td>
<h:commandLink action="#{consolidarTurma.escolherSubTurma}">
	<h:graphicImage value="/img/seta.gif" title="Selecionar Subturma"/>
	<f:param name="id" value="#{st.id}"/>
</h:commandLink>
</td></tr>
</c:forEach>
</table>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
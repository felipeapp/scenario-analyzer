<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<f:view>
<f:subview id="menu">
	<%@include file="/portais/docente/menu_docente.jsp" %>
</f:subview>
<h2 class="tituloPagina"> <ufrn:subSistema /> > Gerenciar Plano de Curso</h2>

<div class="descricaoOperacao">

Selecione uma turma aberta abaixo para gerenciar o plano de curso.

</div>


<h:form id="escolher">

<h:messages showDetail="true"/>
<input type="hidden" name="gestor" value="${ param['gestor'] }"/>

<ufrn:checkNotRole papeis="<%= new int[]{SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_LATO} %>">
<div class="infoAltRem" style="width: 100%">
	<h:graphicImage value="/img/seta.gif"style="overflow: visible;" />: Gerenciar Plano de Curso
</div>
</ufrn:checkNotRole>

<table class="formulario" width="100%">
<caption>Selecione uma Turma</caption>
<tbody>

<c:if test="${ !consolidarTurma.fromMenuGestor }">
	<c:forEach var="t" items="#{ consolidarTurma.turmasProfessorCombo }" varStatus="loop">
	<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
		<td>${ t.label }</td>
		<td style="width:20px;"> 
			<h:commandLink action="#{planoCurso.gerenciarPlanoCurso}" title="Gerenciar Plano de Curso">
				<h:graphicImage value="/img/seta.gif" />
				<f:param name="idTurma" value="#{ t.value }" />
			</h:commandLink>
		</td>
	</tr>
	</c:forEach>
</c:if>

</tbody>

</table>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

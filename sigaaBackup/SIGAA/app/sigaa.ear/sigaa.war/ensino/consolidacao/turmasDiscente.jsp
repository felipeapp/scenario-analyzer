<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
<%@include file="/stricto/menu_coordenador.jsp" %>

<h2 class="tituloPagina"><ufrn:subSistema /> > Consolida&ccedil;&atilde;o de Turmas</h2>

<input type="hidden" name="gestor" value="${ param['gestor'] }"/>
<h:messages showDetail="true"/>
<h:outputText value="#{ consolidacaoIndividual.create }"/>

<div class="descricaoOperacao">

Selecione uma das turmas que o aluno encontra-se matriculado.

</div>

<div class="infoAltRem" style="width: 100%">
	<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar Turma
</div>
<table class="formulario" width="80%">
<caption>Turmas do Discente</caption>

<thead>
<tr><th>Turma</th><th></th></tr>
</thead>
<tbody>
<c:forEach items="${ consolidacaoIndividual.matriculasAbertas }" var="mc" varStatus="loop">
<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
	<td>${ mc.turma.nome }</td>
	<td>
	<h:form>
		<input type="hidden" name="id" value="${mc.id }">
		<h:commandLink action="#{consolidacaoIndividual.escolherTurma}" title="Selecionar Turma">
			<h:graphicImage url="/img/seta.gif" />
		</h:commandLink>
	</h:form>
	</td>
</tr>
</c:forEach>
</tbody>

<tfoot>
<tr>
	<td colspan="2">
	<h:form id="botoes">
	<h:commandButton action="#{ consolidacaoIndividual.cancelar }" value="Cancelar" immediate="true" onclick="#{confirm}"/>
	</h:form>
	</td>
</tr>
</tfoot>

</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<%@include file="/ava/cabecalho.jsp" %>

<f:view>
<%@include file="/ava/menu.jsp" %>
<h:form>

<fieldset>
	<legend>Cronograma de Aulas</legend>
</fieldset>

	<table cellpadding="0" cellspacing="0" style="margin: 20px auto; border: 1px solid black;">
		<tr>
			<td align="center" style="font-weight:bold;padding: 2px;border-right: 1px solid silver;"><p align="left">Aula</p></td>
			<c:forEach items="${ topicoAula.mesesAulas }" var="data">
				<td align="center" style="font-weight:bold;padding: 2px;border-top: 1px solid black;" colspan="${ sf:qtdDias(topicoAula.datasAulas, data) }">${ sf:descMes(data) }</td>
			</c:forEach>
		</tr>
		<c:forEach items="#{ topicoAula.aulasGantt }" var="t" varStatus="loop">
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<td style="padding: 2px;border-right: 1px solid silver;border-top: 1px solid black;">${ t.descricao }</td>
				<c:forEach items="${ topicoAula.datasAulas }" var="data">
					<td title="<fmt:formatDate value="${ data }" pattern="dd/MM"/>" style="background-color: ${ sf:interseccao(data, t.data, t.fim) ? 'blue' : 'white' }; width: 13px; border: none; border-top: 1px solid black;"></td>
				</c:forEach>
			</tr>
		</c:forEach>
		<tr>
			<td colspan="${ 1 + fn:length(topicoAula.datasAulas) }"></td>
		</tr>
	</table>
	<div class="botoes">
		<div class="other-actions">
			<h:commandButton action="#{ topicoAula.listar }" value="<< Voltar"/> 
			<h:commandButton action="#{ topicoAula.cancelar }" onclick="#{confirm}"  value="Cancelar"/> 
		</div>
	</div>
</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>
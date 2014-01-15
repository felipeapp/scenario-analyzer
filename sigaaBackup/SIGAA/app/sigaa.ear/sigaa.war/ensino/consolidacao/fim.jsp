<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2>Consolidação de Turma</h2>

<h:form id="imprimir">
<input type="hidden" name="idTurma" value="${ param['idTurma'] }"/>
<p align="center" style="margin: 70px 0;">
<span class="title">
<c:if test="${ param['parcial'] == 'true' }">
Consolidação Parcial da Turma realizada com Sucesso!
</c:if>
<c:if test="${ param['parcial'] != 'true' }">
Turma Consolidada com Sucesso!
</c:if>
</span>
</p>

<table align="center" width="50%">
<tr>
	<td width="33%" align="center" valign="top"><h:commandButton action="#{ relatorioConsolidacao.imprimirComprovante }" image="/img/consolidacao/printer.png" alt="Imprimir" title="Imprimir"/></td>
	<td width="33%" align="center" valign="top"><h:commandButton action="#{ consolidarTurma.cancelar }" image="/img/consolidacao/nav_right_green.png" alt="Continuar" title="Continuar" immediate="true"/></td>
	<td width="33%" align="center" valign="top"><a href="${ctx}/ensino/consolidacao/selecionaTurma.jsf?gestor=${ param['gestor'] }"><img src="${ ctx }/img/consolidacao/nav_right_green.png"/></a></td>
</tr>
<tr>
	<td width="33%" align="center" valign="top"><h:commandLink action="#{ relatorioConsolidacao.imprimirComprovante }" value="Imprimir Comprovante"/></td>
	<td width="33%" align="center" valign="top"><h:commandLink action="#{ consolidarTurma.cancelar }" value="Continuar"/></td>
	<td width="33%" align="center" valign="top"><a href="${ctx}/ensino/consolidacao/selecionaTurma.jsf?gestor=${ param['gestor'] }">Consolidar Outra Turma</a></td>
</tr>
</table>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

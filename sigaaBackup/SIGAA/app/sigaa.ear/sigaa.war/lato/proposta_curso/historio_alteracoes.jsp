<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2><ufrn:subSistema /> &gt; Histórico de Situações </h2>

<table class="formulario" width="80%">
<caption>Proposta do Curso: ${cursoLatoMBean.obj.nome}</caption>
<tbody>
<c:if test="${ empty cursoLatoMBean.obj.historicoSituacoes }">
	<tr>
		<td>
		<div style="font-style: italic; text-align:center">
			Nenhum registro a ser exibido
		</div>
		</td>
	</tr>
</c:if>
<c:forEach items="#{cursoLatoMBean.obj.historicoSituacoes}" var="historico">
	<tr>
	<td>
		<table class="subformulario" width="100%">
		<caption>
		<fmt:formatDate value="${historico.dataCadastro}" pattern="dd/MM/yyyy"/> - ${historico.situacao.descricao}
		</caption>
		<tr>
			<td>Despacho:</td>
		</tr>
		<tr>
			<td style="text-align: justify;">${historico.observacoes}</td>
		</tr>
		</table>
	</td>
	</tr>
</c:forEach>
</tbody>
<tfoot>
	 <tr>
		<td colspan="2">
			<input type="button" value="<< Voltar" onclick="javascript:history.go(-1)" />
		</td>
	 </tr>
</tfoot>
</table>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
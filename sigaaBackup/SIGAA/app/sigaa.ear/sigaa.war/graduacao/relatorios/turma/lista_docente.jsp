<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Relatório Turmas por Quantidade de docentes</b></caption>
			<tr>
				<th>Unidade:</th>
				<td><b><h:outputText value="#{relatorioTurma.centro.nome }" /></b></td>
			</tr>
			<tr>
				<th>Ano-Período:</th>
				<td><b><h:outputText value="#{relatorioTurma.ano }" />.<h:outputText value="#{relatorioTurma.periodo }" /></b></td>
			</tr>			
	</table>
	<hr>
	<table>
	<c:set var="deptoAtual" value="" />
	<c:forEach items="${relatorioTurma.listaTurma}" var="linha">
		<c:if test="${deptoAtual != linha.depto}">
			<tr>
				<td colspan="4">
					<br/>
					<b>${linha.depto}</b>
					<hr />
				</td>
			</tr>
			<tr>
				<td width="10%" nowrap="nowrap"><b>Código</b></td>
				<td nowrap="nowrap"><b>Disciplina</b></td>
				<td width="10%" nowrap="nowrap" align="center"><b>CH Total</b></td>
				<td width="5%" nowrap="nowrap" align="center"><b>Qtd. de Docentes</b></td>
			</tr>			
			<c:set var="deptoAtual" value="${linha.depto}" />
		</c:if>
		<tr>
			<td>
				${linha.codigo}
			</td>
			<td>
				${linha.disciplina}
			</td>			
			<td align="center">
				${linha.ch_total}
			</td>
			<td align="center">
				${linha.qtd_docentes}
			</td>
		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

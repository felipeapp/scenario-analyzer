<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="portalFamiliar" />
	<h:form>
		
		<h2><ufrn:subSistema /> &gt; Observações </h2>
		
		<table class="listagem" style="width: 80%;">
			<thead>
				<tr>
					<th style="text-align:center; width: 10%;">Data</th>
					<th style="text-align:left;">Observação</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="item" items="#{ portalFamiliar.observacoes }" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td style="text-align:center;">
							<fmt:formatDate value="${ item.data }" pattern="dd/MM/yyyy"/>
						</td>
						<td style="text-align:justify;">
							${ item.observacao }
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		
	</h:form>
	
	<center>
		<a href="javascript:history.back();"> Voltar </a>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
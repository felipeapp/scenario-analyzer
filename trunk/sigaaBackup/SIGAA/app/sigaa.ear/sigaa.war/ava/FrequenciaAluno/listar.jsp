<%@include file="/ava/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="frequenciaAluno" />
	<%@include file="/ava/menu.jsp"%>
	<h:form>

		<fieldset><legend>TOTAL DE FALTAS POR UNIDADE</legend> <h:messages />

			<table class="listing" style="width:600px;">
				<thead>
					<tr>
						<th><p align="left">Aluno</p></th>
						<th style="text-align:right;width:50px;">Unid. 1</th>
						<th style="text-align:right;width:50px;">Unid. 2</th>
						<th style="text-align:right;width:50px;">Unid. 3</th>
					</tr>
				</thead>

				<tbody>
					<c:forEach items="${frequenciaAluno.mapa}" var="item" varStatus="loop">
						
						<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">
							
							<td>${item.key}</td>
							<c:set var="auxC" value="1"/>
							<c:forEach items="${item.value}" var="itemValue">
								<td style="text-align:right;">${itemValue}</td>
								<c:set var="auxC" value="${auxC + 1}"/>
							</c:forEach>
							
							<%-- Completa com células em branco --%>
							<c:forEach begin="${auxC}" end="3" step="1">
								<td>&nbsp;</td>
							</c:forEach>
							
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</fieldset>

</h:form>

</f:view>
<%@include file="/ava/rodape.jsp"%>

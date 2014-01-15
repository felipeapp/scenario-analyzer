<!-- visualização do cronograma -->
		<tr> <td colspan="2" style="margin:0; padding: 0;">
			<div style="overflow-x: auto; width: 984px" >
			<table id="cronograma" class="subFormulario" width="100%">
			<caption> Cronograma de Atividades </caption>
			<thead>
				<tr>
					<th width="30%" rowspan="2" style="text-align: center"> Atividade </th>
					<c:forEach items="${projetoPesquisaForm.telaCronograma.mesesAno}" var="ano">
					<th colspan="${fn:length(ano.value)}" align="center" class="inicioAno fimAno" style="text-align: center;">
						${ano.key}
					</th>
					</c:forEach>
				</tr>
				<tr>
					<c:forEach items="${projetoPesquisaForm.telaCronograma.mesesAno}" var="ano">
						<c:forEach items="${ano.value}" var="mes" varStatus="status">
						<c:set var="classeCabecalho" value=""/>
						<c:if test="${status.first}"> <c:set var="classeCabecalho" value="inicioAno"/> </c:if>
						<c:if test="${status.last}"> <c:set var="classeCabecalho" value="fimAno"/> </c:if>
						<th class="${classeCabecalho}" style="text-align: center"> ${mes}	</th>
						</c:forEach>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				<c:set var="numeroAtividades" value="${fn:length(projetoPesquisaForm.telaCronograma.cronogramas)}" />
				<c:set var="valoresCheckboxes" value=",${fn:join(projetoPesquisaForm.telaCronograma.calendario, ',')}" />
				<c:forEach begin="1" end="${numeroAtividades}" varStatus="statusAtividades">
				<tr>
					<th> ${projetoPesquisaForm.telaCronograma.atividade[statusAtividades.index-1]} </th>
					<c:forEach items="${projetoPesquisaForm.telaCronograma.mesesAno}" var="ano" varStatus="statusCheckboxes">
						<c:forEach items="${ano.value}" var="mes">
							<c:set var="valorCheckbox" value=",${statusAtividades.index-1}_${mes}_${ano.key}" />
							<c:set var="classeCelula" value=""/>
							<c:if test="${fn:contains(valoresCheckboxes, valorCheckbox)}">
								<c:set var="classeCelula" value="selecionado"/>
							</c:if>
							<td align="center" class="${classeCelula}" >
								&nbsp;
							</td>
						</c:forEach>
					</c:forEach>
				</tr>
				</c:forEach>
			</tbody>
			</table>
			</div>
			</td>
		</tr>
		<!-- FIM da visualização do cronograma -->
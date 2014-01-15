		<tr> <td colspan="2" style="margin:0; padding: 0;">
			<div style="overflow-x: auto; width: 898px;" >
			<table id="cronograma" class="subFormulario" width="100%;">
			<caption> Cronograma de Atividades <c:if test="${ min > 0 }">- (Cont.)</c:if></caption>
			<thead>
				<tr>
					<th width="30%" rowspan="2" style="text-align: left;"> Atividade </th>
					<c:forEach items="${projetoPesquisaForm.telaCronograma.mesesAno}" var="ano" varStatus="status">
					<c:if test="${ status.index >= min && status.index <= max}">
					<th colspan="${fn:length(ano.value)}" align="center" class="inicioAno fimAno" style="text-align: center;">
						${ano.key}
					</th>
					</c:if>
					</c:forEach>
				</tr>
				<tr>
					<c:forEach items="${projetoPesquisaForm.telaCronograma.mesesAno}" var="ano" varStatus="status">
						<c:if test="${ status.index >= min && status.index <= max}">
							<c:forEach items="${ano.value}" var="mes">
								<c:set var="classeCabecalho" value=""/>
								<c:if test="${status.first}"> <c:set var="classeCabecalho" value="inicioAno"/> </c:if>
								<c:if test="${status.last}"> <c:set var="classeCabecalho" value="fimAno"/> </c:if>
								<th class="${classeCabecalho}" style="text-align: center"> ${mes}</th>
							</c:forEach>
						</c:if>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				<c:set var="numeroAtividades" value="${fn:length(projetoPesquisaForm.telaCronograma.cronogramas)}" />
				<c:set var="valoresCheckboxes" value=",${fn:join(projetoPesquisaForm.telaCronograma.calendario, ',')}" />
				<c:forEach begin="1" end="${numeroAtividades}" varStatus="statusAtividades">
				<tr>
					<th> ${projetoPesquisaForm.telaCronograma.atividade[statusAtividades.index-1]} </th>
					<c:forEach items="${projetoPesquisaForm.telaCronograma.mesesAno}" var="ano" varStatus="status">
						<c:forEach items="${ano.value}" var="mes">
							<c:if test="${ status.index >= min && status.index <= max}">
							<c:set var="valorCheckbox" value=",${statusAtividades.index-1}_${mes}_${ano.key}" />
							<c:set var="classeCelula" value=""/>
							<c:if test="${fn:contains(valoresCheckboxes, valorCheckbox)}">
								<c:set var="classeCelula" value="selecionado"/>
							</c:if>
							<td align="center" class="${classeCelula}" >
								&nbsp;
							</td>
							</c:if>
						</c:forEach>
					</c:forEach>
				</tr>
				</c:forEach>
			</tbody>
			</table>
			</div>
			</td>
		</tr>
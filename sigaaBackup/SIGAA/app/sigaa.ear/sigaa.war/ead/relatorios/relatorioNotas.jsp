<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<h2>Relatório de Alunos por Turma</h2>

	 <table id="parametrosRelatorio" style="margin-bottom:10px;">
		<tr><td width="300px"><b>Curso:</b> ${relatorioAlunosEad.nomeCurso}</td></tr>
		<tr><td><b>Ano:</b> ${relatorioAlunosEad.ano}</td></tr>
		<tr><td><b>Semestre:</b> ${relatorioAlunosEad.semestre}</td></tr>
		<tr><td><b>Total de Turmas Abertas:</b> ${relatorioAlunosEad.totalTurmasAbertas}</td></tr>
		<tr><td><b>Total de Turmas À Definir Docentes:</b> ${relatorioAlunosEad.totalTurmasADefinirDocente}</td></tr>
		<c:if test="${!relatorioAlunosEad.turmaAberta}">
			<tr><td><b>Total de Turmas Consolidadas:</b> ${relatorioAlunosEad.totalTurmasConsolidadas}</td></tr>
			<tr><td><b>Total de Turmas Excluídas:</b> ${relatorioAlunosEad.totalTurmasExcluidas}</td></tr>
		</c:if>	
		<tr><td><b>Total de Alunos Matriculados:</b> ${relatorioAlunosEad.totalAlunosMatriculados}</td></tr>
		<tr><td><b>Total de Alunos Aprovados:</b> ${relatorioAlunosEad.totalAlunosAprovados}</td></tr>
		<tr><td><b>Total de Alunos Reprovados:</b> ${relatorioAlunosEad.totalAlunosReprovados}</td></tr>
		<tr><td><b>Total de Alunos Trancados:</b> ${relatorioAlunosEad.totalAlunosTrancados}</td></tr>
	</table>
		
	<c:if test="${not empty relatorioAlunosEad.linhas}">
		
		<c:set var="disciplina" value=""/>
		<c:set var="subTotalMatr" value="0"/>
		<c:set var="subTotalApr" value="0"/>
		<c:set var="subTotalRep" value="0"/>
		<c:set var="subTotalTr" value="0"/>
		<c:forEach items="#{relatorioAlunosEad.linhas}" var="l" varStatus="i">
			
			<c:if test="${l[1] != disciplina}">
				<c:if test="${!i.first}">
					</tbody>
					<tfoot>
					<tr class="linhaAcimaRelatorio">
						<th colspan="4" style="text-align:right;" class="rotulo">Subtotal:</th>
						<td style="text-align:right;">${subTotalMatr}</td>
						<td style="text-align:right;">${subTotalApr}</td>
						<td style="text-align:right;">${subTotalRep}</td>
						<td style="text-align:right;">${subTotalTr}</td>
					</tr>
					</tfoot>
					<c:set var="subTotalMatr" value="0"/>
					<c:set var="subTotalApr" value="0"/>
					<c:set var="subTotalRep" value="0"/>
					<c:set var="subTotalTr" value="0"/>
				</table>
				<br/>
				</c:if>
				
				<h3 class="tituloSubTabelaRelatorio">${l[1]}</h3>
				<br/>
				<table class="tabelaRelatorio" style="width:100%;">
					<thead>
						<tr>
							<th style="text-align:center;">Código</th>
							<th style="text-align:center;">Turma</th>
							<th style="text-align:left;">Status</th>
							<th style="text-align:left;">Pólo</th>
							<th style="text-align:right;">Matr.</th>
							<th style="text-align:right;">Apr.</th>
							<th style="text-align:right;">Rep.</th>
							<th style="text-align:right;">Tr.</th>
						</tr>
					</thead>
					<tbody>
			</c:if>
			
			<tr class="linhaPontilhadaAbaixo">
				<td style="text-align:center;">${l[0]}</td>
				<td style="text-align:center;">${l[2]}</td>
				<td style="text-align:left;">${l[4]}</td>
				<td style="text-align:left;">${l[5]}</td>
				<td style="text-align:right;">${l[6]}</td>
				<td style="text-align:right;">${l[7]}</td>
				<td style="text-align:right;">${l[8]}</td>
				<td style="text-align:right;">${l[9]}</td>
			</tr>
			
			<c:set var="disciplina" value="${l[1]}"/>
			<c:set var="subTotalMatr" value="${subTotalMatr+l[6]}"/>
			<c:set var="subTotalApr" value="${subTotalApr+l[7]}"/>
			<c:set var="subTotalRep" value="${subTotalRep+l[8]}"/>
			<c:set var="subTotalTr" value="${subTotalTr+l[9]}"/>
			
			<c:if test="${i.last}">
					</tbody>
					<tfoot>
					<tr class="linhaAcimaRelatorio">
						<th colspan="4" style="text-align:right;" class="rotulo">Subtotal:</th>
						<td style="text-align:right;">${subTotalMatr}</td>
						<td style="text-align:right;">${subTotalApr}</td>
						<td style="text-align:right;">${subTotalRep}</td>
						<td style="text-align:right;">${subTotalTr}</td>
					</tr>
					</tfoot>
				</table>
				<br/>
			</c:if>
			
		</c:forEach>
	</c:if>
		
	<c:if test="${empty relatorioAlunosEad.linhas}">
		<br/>
		<center>Não há turmas para os parâmetros selecionados.</center>
	</c:if>			

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

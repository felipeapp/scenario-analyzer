<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	.relatorio td {
		border: 1px solid silver;
	}
	
	.titulo {
		background-color: #DCDCDC;
		font-weight:bold;
	}
	
	.espaco {
		padding: 15px;
		height: 15px;
	}
</style>

<h2 align="center">Número de alunos por Ano/Polo/Curso</h2>

<c:set var="_ano" />
<c:set var="_polo" />
<c:set var="_curso"/>

<c:set var="totalAno"  value="0"/>
<c:set var="totalPolo" value="0"/>
<c:set var="totalCurso" value="0"/>
<c:set var="totalGeral" value="0"/>

<div class="parametrosRelatorio" style="border-bottom: 2px solid black;padding-bottom:10px;">
	<table>
		<tr>
			<td><b>Polo: </b>  </td>
			<td><c:if test="${ not empty relatorioAlunosPolo.polo }">${ relatorioAlunosPolo.polo.descricao }</c:if>
			<c:if test="${ empty relatorioAlunosPolo.polo || relatorioAlunosPolo.polo.id == 0 }">Todos</c:if>
			</td>
		</tr>
		<tr>
			<td><b>Curso: </b> </td>
			<td><c:if test="${ not empty relatorioAlunosPolo.curso }">${ relatorioAlunosPolo.curso.nome }</c:if>
			<c:if test="${ empty relatorioAlunosPolo.curso || relatorioAlunosPolo.curso.id == 0 }">Todos</c:if>
			</td>
		</tr>
		<tr>
			<td><b>Ano de Ingresso: </b>  </td>
			<td>${(relatorioAlunosPolo.ano > 0 ? relatorioAlunosPolo.ano : 'Todos') }</td>
		</tr>
		<c:if test="${ relatorioAlunosPolo.periodo > 0 }">
		<tr>
			<td><b>Período de Ingresso: </b>${ relatorioAlunosPolo.periodo }</td>
		</tr>
		</c:if>
		<tr>
			<td><b>Apenas Matrículados: </b></td>
			<td><c:if test="${ relatorioAlunosPolo.matriculados }">Sim</c:if>
			<c:if test="${ !relatorioAlunosPolo.matriculados }">Não</c:if>
			</td>
		</tr>
	</table>
</div>

<br />

<table class="relatorio" align="center">
	<c:forEach var="d" items="#{relatorioAlunosPolo.discentes}" varStatus="status">
		
		<c:if test="${status.first}">
			<c:set var="_curso" value="${d.curso.descricao}"/>
		</c:if>
		
		<c:set var="anoAtual"   value="${d.discente.anoEntrada}"/>
		<c:set var="poloAtual"  value="${d.polo.descricao}"/>
		<c:set var="cursoAtual" value="${d.curso.descricao}"/>
		
		<c:set var="mudouAno" value="false"/>
			
		<c:if test="${anoAtual != _ano}">
			<c:set var="mudouAno" value="true"/>
			<c:if test="${not status.first}">
				<tr>
					<td>
						${_curso}
					</td>
					<td align="right">
						${totalCurso}
					</td>
				</tr>

				<c:set var="_curso" value="${cursoAtual}"/>
				<c:set var="totalCurso" value="0"/>
				
				<tr class="titulo"> 
					<td align="right" style="background-color:white;">
						<b>Total</b>
					</td>
					<td style="text-align:right;background-color:white;">
						<b>${totalPolo}</b>
					</td>
				</tr>
				<c:set var="totalPolo"  value="0"/>
				<tr class="titulo" align="right">
					<td>Total Anual:</td>
					<td>${totalAno}</td>
				</tr>
				<c:set var="totalAno"  value="0"/>
				<tr>
					<td class="espaco" style="border-left: hidden;border-right: hidden;">
				</td>
				<tr>
			</c:if>
			
			<tr class="titulo" style="background-color: #BBBBBB">
				<td>
					Curso 
				</td>
				<td>
					Quantidade
				</td>
			</tr>
			<tr class="titulo">
				<td colspan="2">
					Ano de Entrada: ${anoAtual}
				</td>
			</tr>
			<c:set var="_ano" value="${anoAtual}"/>
		</c:if>
		
		<c:if test="${poloAtual != _polo}">	
			<c:if test="${not mudouAno}">
				<tr>
					<td>
						${_curso}
					</td>
					<td align="right">
						${totalCurso}
					</td>
				</tr>

				<c:set var="_curso" value="${cursoAtual}"/>
				<c:set var="totalCurso" value="0"/>
				
				<tr class="titulo"> 
					<td align="right" style="background-color:white;">
						<b>Total</b>
					</td>
					<td style="text-align:right;background-color:white;">
						<b>${totalPolo}</b>
					</td>
				</tr>
			</c:if>
			<c:set var="mudouAno" value="false"/>	
			<tr class="titulo">
				<td colspan="2">
					Polo: ${poloAtual}
				</td>
			</tr>
			<c:set var="_polo" value="${poloAtual}"/>
			<c:set var="totalPolo" value="0"/>
		</c:if>
		
		<c:if test="${cursoAtual != _curso}">
				<tr>
					<td>
						${_curso}
					</td>
					<td align="right">
						${totalCurso}
					</td>
				</tr>
			<c:set var="_curso" value="${cursoAtual}"/>
			<c:set var="totalCurso" value="0"/>
		</c:if>
		
		<c:set var="totalAno"   value="${totalAno+1}"/>
		<c:set var="totalPolo"  value="${totalPolo+1}"/>
		<c:set var="totalCurso" value="${totalCurso+1}"/>
		<c:set var="totalGeral" value="${totalGeral+1}"/>	
	</c:forEach>
	
	<tr>
		<td>
			${_curso}
		</td>
		<td align="right">
			${totalCurso}
		</td>
	</tr>
	<tr class="titulo"> 
		<td align="right" style="background-color:white;">
			<b>Total</b>
		</td>
		<td style="text-align:right;background-color:white;">
			<b>${totalPolo}</b>
		</td>
	</tr>
	<tr class="titulo">
		<td align="right">Total Anual:</td>
		<td align="right">${totalAno}</td>
	</tr>
	
</table>
<br/>
<table cellspacing="1" width="100%" style="font-size: 11px;" align="center">
	<tr align="center">
		<td><b>Total de Discentes Encontrados:</b> ${totalGeral}<br/></td>
	</tr>
</table>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
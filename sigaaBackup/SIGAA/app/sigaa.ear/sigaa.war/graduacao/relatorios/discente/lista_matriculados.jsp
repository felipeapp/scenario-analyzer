<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Lista de alunos ativos e matriculados num determinado período</b></caption>
			<tr>
				<th>Ano-Período:</th>
					<td><b><h:outputText value="#{relatorioDiscente.ano}"/> - <h:outputText value="#{relatorioDiscente.periodo}"/></b>
				</td>
			</tr>
	</table>
	<hr>
	<table width="100%" style="font-size: 10px">
		<caption><b>Legenda</b></caption>
			<tr>
				<td><b>Ingresso:</b><i> Período de ingresso do discente</i> </td>
				<td><b>MAT:</b> <i>Nº de componentes em espera/matriculado no presente momento</i></td>
			</tr>
			<tr>
				<td><b>TR:</b> <i>Nº de componentes com trancamento</i></td>
				<td><b>AP:</b> <i>Nº de componentes com aprovação/aproveitamentos</i></td>
			</tr>
			<tr>
				<td><b>RP:</b> <i>Nº de componentes com reprovação</i></td>
				<td><b>Total:</b> <i>Total de matrículas realizadas no período</i></td>
			</tr>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: <h:outputText value="#{relatorioDiscente.numeroRegistosEncontrados}"/></b></caption>
	<c:set var="curso"/>
	<c:set var="matriz"/>
	<c:forEach items="${relatorioDiscente.listaDiscente}" var="linha">
			<c:set var="curso" value="${linha.id_curso}"/>
			<c:set var="matriz" value="${linha.id_matriz_curricular}"/>
		<c:if test="${cursoAnterior != curso || matrizAnterior != matriz}">
			<c:set var="cursoAnterior" value="${curso}"/>
			<c:set var="matrizAnterior" value="${matriz}"/>
			<tr>
				<td colspan="8">
					<br>
					<b>${linha.centro} - ${linha.curso_nome} (${linha.id_curso}) - ${linha.municipio_nome}<br>
					 <i>${linha.turno_desc} - ${linha.modalidade_curso} - ${linha.habilitacao_nome }</i></b>
					<hr>
				</td>
			</tr>
			<tr>
					<td style="text-align: center"><b>Ingresso</b></td>
					<td style="text-align: center"><b>Matrícula</b></td>
					<td style="text-align: left"><b>Nome</b></td>
					<td style="text-align: center"><b>MAT</b></td>
					<td style="text-align: center"><b>TR</b></td>
					<td style="text-align: center"><b>AP</b></td>
					<td style="text-align: center"><b>RP</b></td>
					<td style="text-align: center"><b>Total</b></td>
			<tr>
		</c:if>
		<tr>
			<td style="text-align: center">
				${linha.ano_ingresso}-${linha.periodo_ingresso}
			</td>
			<td style="text-align: center">
				${linha.matricula}
			</td>
			<td>
				${linha.aluno_nome}
				<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
			</td>
			<td align="center">
				${linha.matriculados}
			</td>
			<td align="center">
				${linha.trancado}
			</td>
			<td align="center">
				${linha.aprovacoes}
			</td>
			<td align="center">
				${linha.reprovacoes}
			</td>
			<td align="center">
				${linha.matriculados + linha.trancado +  linha.aprovacoes + linha.reprovacoes}
			</td>

		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

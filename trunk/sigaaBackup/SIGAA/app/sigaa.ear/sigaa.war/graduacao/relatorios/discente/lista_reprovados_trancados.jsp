<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 2px ; border-bottom: 1px dashed #888}
</style>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Lista de alunos reprovados ou trancados num determinado período</b></caption>
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
	<c:set var="cursoLoop"/>
	<c:set var="discenteLoop"/>
	<c:set var="matrizLoop"/>
	<c:forEach items="${relatorioDiscente.listaDiscente}" var="linha">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			<c:set var="discenteAtual" value="${linha.id_discente}"/>
			<c:set var="matrizAtual" value="${linha.id_matriz_curricular}"/>

		<c:if test="${cursoLoop != cursoAtual || matrizLoop != matrizAtual}">
			<c:set var="cursoLoop" value="${cursoAtual}"/>
			<c:set var="matrizLoop" value="${matrizAtual}"/>
			<tr class="curso">
				<td colspan="5">
					<b>${linha.centro}	- ${linha.curso_nome} - ${linha.municipio_nome}<br>
					<i>${linha.turno_desc} - ${linha.modalidade_curso} - ${ empty linha.habilitacao_nome? "MODALIDADE SEM HABILITAÇÃO": linha.habilitacao_nome }</i></b>
				</td>
			</tr>
			<tr class="header">
				<td><b>Componente Curricular</b></td>
				<td align="center" nowrap="nowrap"><b>Média / Faltas</b></td>
				<td align="center"><b>Resultado</b></td>
			<tr>

		</c:if>

		<c:if test="${discenteLoop != discenteAtual}">
			<c:set var="discenteLoop" value="${discenteAtual}"/>
			<tr class="discente">
				<td colspan="3">
					${linha.ano_ingresso}${linha.periodo_ingresso} - ${linha.matricula} - ${linha.aluno_nome}
					<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
				</td>
			</tr>
		</c:if>

		<tr class="componentes">
			<td>
				${linha.componente_codigo} - ${linha.componente_nome} - ${linha.componente_ch}h
			</td>
			<td align="center">
				${linha.media_final} / ${linha.numero_faltas}
			</td>
			<td align="center">
				${linha.situacao_matricula}
			</td>
		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

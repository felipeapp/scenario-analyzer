<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	#relatorio span.semRegistro { color: red;}
	#relatorio tr.itemRel td {padding: 1px 0 0 ;}
	#relatorio tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	#relatorio tr.anosemestre td {padding: 15px 0 0; border-bottom: 1px solid #555; font-size: 13px}
	#relatorio tr.componentecurr td {padding: 35px 0 0; border-bottom: 1px solid #555; font-size: 12px}
	#relatorio tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	#relatorio tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	#relatorio tr.componentes td {padding: 2px ; border-bottom: 1px dashed #888}
	#relatorio tr.total td {padding: 2px ; border-bottom: 1px solid #888; font-size: 12px; font-weight: bold;}
</style>
<f:view>
	<hr>
	<h2><b>Relatório Quantitativo de Solicitação de Trancamento</b></h2>
	<table>
			<tr class="itemRel">
				<td>Ano-Semestre: </td>
				<td><b><h:outputText value="#{relatorioDiscente.filtroAnoPeriodo ? (relatorioDiscente.ano*10)+relatorioDiscente.periodo : 'TODOS'}"/></b>
						</td>
			</tr>
			<tr class="itemRel">
				<td>Motivo do Trancamento: </td>
				<td><b><h:outputText value="#{relatorioDiscente.filtroMotivoTrancamento ? relatorioDiscente.motivoTrancamento.descricao : 'TODOS'}"/></b>
				</td>
			</tr>
			<tr class="itemRel">
				<td>Centro:</td>
				<td><b><h:outputText value="#{relatorioDiscente.filtroCentro ? relatorioDiscente.centro.nome : 'TODOS'}"/></b>
				</td>
			</tr>
			<tr class="itemRel">
				<td >Curso:</td>
				<td><b><h:outputText value="#{relatorioDiscente.filtroCurso ? relatorioDiscente.curso.descricao : 'TODOS'}"/></b>
				</td>
			</tr>
			<tr class="itemRel">
				<td>Matriz Curricular:</td>
				<td><b><h:outputText value="#{relatorioDiscente.filtroMatriz ? relatorioDiscente.matrizCurricular.descricaoMin : 'TODAS'}"/></b>
				</td>
			</tr>

	</table>
	<hr>
	<c:choose>
		<c:when test="${relatorioDiscente.numeroRegistosEncontrados != 0 }">
			<h3><b>Total de Registros: <h:outputText value="#{relatorioDiscente.numeroRegistosEncontrados}"/></b></h3>
		</c:when>
		<c:otherwise>
			<span class="semRegistro">Nenhum registro encontrado</span>
		</c:otherwise>
	</c:choose>

	<table cellspacing="1" width="100%" style="font-size: 10px;">

	<c:set var="matriz"/>
	<c:set var="anoPeriodo"/>
	<c:set var="total" value="0"/>
	<c:forEach items="${relatorioDiscente.listaDiscente}" var="linha">
			<c:set var="matrizAtual" value="${linha.id_matriz_curricular}"/>
			<c:set var="anoPeriodoAtual" value="${(linha.ano * 10) + linha.periodo}"/>
		<c:if test="${ matriz != matrizAtual || anoPeriodo != anoPeriodoAtual }">
			<c:if test="${ not empty matriz }">
				<tr class="total">
					<td align="right">
						Total:
					</td>
					<td>
						${total}
					</td>
				</tr>
				<c:set var="total" value="0"/>
			</c:if>
			<c:set var="matriz" value="${matrizAtual}"/>
			<c:if test="${ anoPeriodo != anoPeriodoAtual}">
				<c:set var="anoPeriodo" value="${anoPeriodoAtual}"/>
				<tr class="anosemestre">
					<td colspan="2">
						<b>${(linha.ano * 10) + linha.periodo }</b>
					</td>
				</tr>
			</c:if>
			<tr class="curso">
				<td colspan="3">
					<b>${linha.centro_sigla} - ${linha.centro_nome} - ${linha.curso_nome} - ${linha.municipio_nome}<br>
					 <i>${linha.turno_sigla} -${linha.modalidade_nome } - ${empty linha.habilitacao? "<i>MODALIDADE SEM HABILITAÇÃO</i>": linha.habilitacao  }</i> </b>
				</td>
			</tr>
			<tr class="header">
				<td><b>Motivo do Trancamento</b></td>
				<td><b>Qtd</b></td>
			<tr>
		</c:if>
		<tr class="componentes">
			<td width="95%" valign="middle">
				${linha.motivo_trancamento }
			</td>
			<td width="5%">
				${linha.qtd}
				<c:set var="total" value="${total+linha.qtd}"/>
			</td>
		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555; font-weight: bold; font-size: 20px; text-align: center; }
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.foot td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee; font-weight: bold; font-size: 13px; }
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>
<f:view>
	<h:outputText value="#{ avaliacaoApresentacaoResumoBean.create }" />
	<hr>
	<table width="100%">
		<caption><b>Relatório de Premiação de Trabalhos do CIC</b></caption>
		<tr>
			<th>Congresso:</th>
			<td>
				<b>${ avaliacaoApresentacaoResumoBean.congresso.descricao }</b>
			</td>
		</tr>
			<tr>
			<th>Centro:</th>
			<td>
				<b>${ avaliacaoApresentacaoResumoBean.unidade.nome != null ? avaliacaoApresentacaoResumoBean.unidade.nome : 'TODOS' }</b>
			</td>
		</tr>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
	<caption><b>Total de Registros: <h:outputText value="#{avaliacaoApresentacaoResumoBean.numeroRegistrosEncontrados}"/></b></caption>
	<c:set var="centroLoop" />
		<tr class="header">
			<td>Classificação</td>
			<td>Cód. Resumo</td>
			<td>Aluno</td>
			<td>Curso</td>
			<td>Média Final</td>
		</tr>
		<c:forEach var="linha" items="#{ avaliacaoApresentacaoResumoBean.lista }" varStatus="status">
			<c:set var="centroAtual" value="${linha.centro}"/>
			<c:if test="${centroAtual != centroLoop}">
				<c:set var="pos" value="1"/>
				<tr class="curso">
					<td colspan="5"> ${ linha.centro } </td>
				</tr>
				<c:set var="centroLoop" value="${centroAtual}"/>
			</c:if>
			
			<tr class="componentes">
				<td> ${ pos }º </td>
				<td> ${ linha.codigo } </td>
				<td> ${ linha.autor } </td>
				<td> ${ linha.curso } </td>
				<td> <ufrn:format type="valor3" valor="${ linha.media_final }" /> </td>
			</tr>
			<c:set var="pos" value="${ pos + 1 }"/>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

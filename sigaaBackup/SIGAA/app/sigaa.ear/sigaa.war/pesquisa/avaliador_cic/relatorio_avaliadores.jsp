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
		<caption><b>Relatório de Avaliadores de Apresentação de Resumo do CIC</b></caption>
		<tr>
			<th>Congresso:</th>
			<td>
				<b>${ avaliacaoApresentacaoResumoBean.congresso.descricao }</b>
			</td>
		</tr>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
	<caption><b>Total de Registros: <h:outputText value="#{avaliacaoApresentacaoResumoBean.numeroRegistrosEncontrados}"/></b></caption>
	<c:set var="centroLoop" />
	<c:set var="deptoLoop" />
		<c:forEach var="linha" items="#{ avaliacaoApresentacaoResumoBean.lista }" varStatus="status">
			<c:set var="centroAtual" value="${linha.centro}"/>
			<c:set var="deptoAtual" value="${linha.depto}"/>
			<c:if test="${centroAtual != centroLoop}">
				<tr class="curso">
					<td> ${ linha.centro } </td>
				</tr>
				<c:set var="centroLoop" value="${centroAtual}"/>
			</c:if>
			<c:if test="${deptoAtual != deptoLoop}">
				<tr class="discente">
					<td> ${ linha.depto }</td>
				</tr>
				<c:set var="deptoLoop" value="${deptoAtual}"/>
			</c:if>
			
			<tr class="componentes">
				<td> ${ linha.siape } - ${ linha.avaliador } </td>
			</tr>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

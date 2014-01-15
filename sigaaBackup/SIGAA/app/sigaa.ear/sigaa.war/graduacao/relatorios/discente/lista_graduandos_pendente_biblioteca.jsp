<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<%@include file="/graduacao/relatorios/cabecalho_curso.jsp"%>

	<table cellspacing="1" class="tabelaRelatorioBorda" width="100%" style="font-size: 10px;">
		<thead>
			<tr>
				<th style="text-align: center;">Matr�cula</th>
				<th style="text-align: left;">Nome</th>
				<th style="text-align: right;">N� de Empr�stimos sem Devolu��o</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${resultado}" var="linha">
			<tr>
				<td style="text-align: center;">${linha.matricula }</td>
				<td style="text-align: left;">
					${linha.nome }
					<c:if test="${not empty linha.municipio_polo}"> - P�LO ${linha.municipio_polo}</c:if>
				</td>
				<td style="text-align: right;">${linha.total_pendente }</td>
			<tr>
		</c:forEach>
		</tbody>
	</table>
	<br/>
	<div align="center">Total de Registros: ${ fn:length(resultado) }</div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
<!--
table.programaRelatorio caption {
	font-size: 1.3em;
	font-weight: bold;
	color: #222;
	font-variant: small-caps;
	letter-spacing: 2px;
	padding: 3px;
	border-bottom: 2px solid #444;
}
table.programaRelatorio th{
	font-weight: bold;
	font-size: 1.2em;
	text-align: left;
	border-bottom: 1px dashed #333;
	padding: 10px 0 3px;
}
table.programaRelatorio td.itemPrograma {
	padding: 8px 10px;
}
-->
</style>
<f:view>
	<table class="visualizacao">
		<tr>
			<th width="30%">Componente Curricular:</th>
			<td>${programaComponente.obj.componenteCurricular.codigo} -
				${programaComponente.obj.componenteCurricular.nome}</td>
		</tr>
		<tr>
			<th>Créditos:</th>
			<td>${programaComponente.obj.componenteCurricular.crTotal} créditos</td>
		</tr>
		<tr>
			<th>Carga Horária:</th>
			<td>${programaComponente.obj.componenteCurricular.chTotal} horas</td>
		</tr>
		<tr>
			<th>Unidade Responsável:</th>
			<td>${programaComponente.obj.componenteCurricular.unidade.nome}</td>
		</tr>
		<tr>
			<th>Tipo do Componente:</th>
			<td>${programaComponente.obj.componenteCurricular.tipoComponente.descricao}</td>
		</tr>
		<tr>
			<th>Ementa:</th>
			<td>
				<c:choose>
					<c:when test="${not empty programaComponente.obj.componenteCurricular.detalhes.ementa}">
						<ufrn:format type="texto" name="programaComponente" property="obj.componenteCurricular.detalhes.ementa.nome_ascii" />
					</c:when>
					<c:otherwise>
						<i> ementa não cadastrada </i>
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
	</table>
	
	<br />
	<table class="programaRelatorio" width="100%;">
		<caption>Dados do Programa</caption>
		<thead>
			<tr>
				<th>Aula</th>
				<th>Conteúdo</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="item" items="#{programaComponente.obj.componenteCurricular.itemPrograma}">
				<tr>
					<td> <c:out value="${item.aula}" /> </td>
					<td style="padding-left: 180px;"> <c:out value="${item.conteudo}" /> </td>
				</tr>
			</c:forEach>
		</tbody>			
	</table>
</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

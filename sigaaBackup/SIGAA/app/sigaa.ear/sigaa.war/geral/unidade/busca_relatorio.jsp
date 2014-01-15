<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
<c:set var="tipoDesc" value="valendo"/>
<c:forEach items="#{unidade.lista}" var="item" varStatus="status">
<c:set var="tipoDesc" value="${item.tipoAcademicaDesc}"/>
</c:forEach>
	<h2 class="tituloTabela"><b>Lista de Unidades Acadêmicas </b></h2>
    <div id="parametrosRelatorio">
		<table>
			<c:if test="${unidade.param == 'nome'}">
				<tr>
					<th>Nome da Unidade:</th>
					<td>
						${unidade.nome}
					</td>
				</tr>	   
			</c:if>
			<c:if test="${unidade.param == 'tipo'}">
				<tr>
					<th>Tipo da Unidade: </th>
				
					<td>
						${tipoDesc}
					</td>		
				</tr>	
			</c:if>
		</table>
	</div>
	
	<br/>
	
	<table class="tabelaRelatorioBorda" width="100%">
		
		<thead>
			<tr>
				<th width="50px"><p align="right">Código</p></th>
				<th>Unidade</th>
				<th>Sigla</th>
				<th>Tipo</th>
			</tr>
		</thead>
		<c:forEach items="#{unidade.lista}" var="item" varStatus="status">
			<tbody>
				<tr>
				<td align="right">${item.codigo}</td>
					<td>${item.nome}</td>
					<td>${item.sigla}</td>
					<td>${item.tipoAcademicaDesc}</td>
				</tr>
			</tbody>
			<c:set var="tipoDesc" value="${item.tipoAcademicaDesc}"/>
		</c:forEach>
	</table>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
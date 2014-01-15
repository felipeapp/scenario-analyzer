<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
.alinharDireita{ 
	text-align:right !important;
}
.alinharEsquerda{ 
	text-align:left !important;
} 
.alinharCentro{ 
	text-align:center !important;
}
.destacado{
	color: red;
}
</style>
<body>
<f:view>
	<h2>Lista de Docentes por Centro e Departamento</h2>
	<table width="100%">
		<c:if test="${relatorioDocente.centro ne null and relatorioDocente.centro.id ne 0}">
			<tr>
				<th>Centro:</th>
				<td><b><h:outputText
					value="#{relatoriodocente.centro.nome }" /></b></td>
			</tr>
			<tr>
				<th>Departamento:</th>
				<td><b>TODOS</b></td>
			</tr>
		</c:if>
		<c:if test="${relatorioDocente.departamento ne null and relatorioDocente.departamento.id ne 0}">
			<tr>
				<th>Centro:</th>
				<td><b><h:outputText
					value="#{relatorioDocente.departamento.gestora.nome }" /></b></td>
				<th>Departamento:</th>
				<td><b><h:outputText
					value="#{relatorioDocente.departamento.nome }" /></b></td>
			</tr>
		</c:if>
	</table>
	<c:set var="centroLoop"/>	
	<c:set var="centroAtual" value="${linha.centro}"/>
	<table class="tabelaRelatorioBorda" width="100%">
	<c:forEach items="${relatorioDocente.listaDocentes}" var="linha">
		<c:if test="${centroLoop != linha.centro}">
			<table width="100%"><tr><td>&nbsp;</td></tr></table>
			<table width="100%" class="tabelaRelatorioBorda">
			<c:set var="centroLoop" value="${linha.centro}"/>
				<tr class="centro">
					<td align="center" colspan="3">
						<b>${linha.centro}</b>
					</td>
				</tr>
			</table>
			<table width="100%" class="tabelaRelatorioBorda">
			<thead>		
			<tr>
				<th class="alinharEsquerda">Docente</th>
				<th class="alinharDireita">Siape</th>
				<th class="alinharEsquerda">Cargo</th>
			</tr>	
			</thead>
		</c:if>
		<tr>
			<td width="50%" class="alinharEsquerda">${linha.docente}</td>
			<td width="10%" class="alinharDireita">${linha.siape}</td>
			<td width="40%" class="alinharEsquerda">${linha.denominacao}</td>
		</tr>
	</c:forEach>
	</table>
	<br>
	<table width="100%" class="tabelaRelatorioBorda">
		<tfoot align="center">
			<tr><td align="center"><b>Total de Registros: </b>${relatorioDocente.numeroListaDocentes}</td></tr>
		</tfoot>
	</table>
</f:view>
</body>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

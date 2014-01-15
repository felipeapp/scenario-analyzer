<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Indentifica��o do Docente</b></caption>
		<tr>
			<th>Nome:</th>
			<td colspan="5"><b><h:outputText
				value="#{classificacaoRelatorio.docenteDetalhes.servidor.pessoa.nome }" /></b></td>
		</tr>
		<tr>
			<th>Matr�cula:</th>
			<td><b><h:outputText value="#{classificacaoRelatorio.docenteDetalhes.servidor.siape }" /></b></td>
			<th>CPF:</th>
			<td colspan="3"><b><h:outputText
				value="#{classificacaoRelatorio.docenteDetalhes.servidor.pessoa.cpf_cnpj }" /></b></td>
		</tr>
		<tr>
			<th>Centro:</th>
			<td colspan="5"><b><h:outputText
				value="#{classificacaoRelatorio.docenteDetalhes.servidor.unidade.unidadeResponsavel.nome}" /></b></td>
		</tr>
		<tr>
			<th>Departamento:</th>
			<td colspan="5"><b><h:outputText
				value="#{classificacaoRelatorio.docenteDetalhes.servidor.unidade.nome}" /></b></td>
		</tr>
		<tr>
			<th>IPI:</th>
			<td><b><h:outputText value="#{classificacaoRelatorio.docenteDetalhes.ipi}" /></b></td>
			<th>FPPI:</th>
			<td colspan="3"><b><h:outputText
				value="#{classificacaoRelatorio.docenteDetalhes.fppi }" /></b></td>
		</tr>
	</table>
	<hr>
	<table width="100%">
		<caption><b>${classificacaoRelatorio.docenteDetalhes.classificacaoRelatorio.relatorioProdutividade.titulo} </b></caption>
		<thead>
			<tr>
				<td width="90%">Descri��o do Item</td>
				<td width="10%">Pontua��o</td>
			<tr>
		</thead>
		<c:forEach items="${classificacaoRelatorio.docenteDetalhes.emissaoRelatorioItemCollection}" var="item">
			<tr>
				<td>${item.grupoItem.descricao}</td>
				<td>${item.pontos}</td>
			</tr>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>

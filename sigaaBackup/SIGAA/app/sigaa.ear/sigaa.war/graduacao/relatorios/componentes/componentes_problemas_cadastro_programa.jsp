<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	table.info {
		margin: 5px auto 15px;
		border: 1px solid #333;
		border-width: 1px 0;
		width: 80%;
	}

	table.info th{ font-weight: bold; }
	table.info th, table.info td{ padding: 3px; }

	table.relatorio {
		border: 1px solid #333;
		border-width: 2px 0;
		width: 100%;
	}
	
	
	table.relatorio th, table.relatorio td{ padding: 2px 3px; }
	table.relatorio thead th { text-align: left;}
	table.tabelaRelatorioBorda { border-top: none;}
	table.tabelaRelatorioBorda thead td { text-align: left;  border: none; }
	table.relatorio tbody td { border-bottom: 1px dashed #555; }
	table.relatorio tfoot td { text-align: center; background: #DDD; border-top: 1px solid #333; font-weight: bold; padding: 3px; }
</style>

<f:view>
	<h2> ${relatorioPorDepartamento.titulo} </h2>
	
	<table class="tabelaRelatorioBorda" width="100%" >
		
		
		<c:set var="_unidade"/>
		<c:forEach items="${componentes}" var="componente" varStatus="loop">
			<c:set var="unidadeAtual" value="${componente.unidade.id}"/>
			
			<c:if test="${_unidade != unidadeAtual}">
				<c:set var="_unidade" value="${unidadeAtual}"/>
					
				
				<thead>
					<tr><td>&nbsp</td></tr>
					<tr><td>&nbsp</td></tr>
					<tr>
						<td colspan="3"><font size="2px"><b>${componente.unidade.nome}</b></font></td>
					</tr>
					<tr><td>&nbsp</td></tr>
					<tr>
						<th> Código </th>
						<th> Nome </th>
						<th> Tipo </th>
					</tr>
				</thead>
			</c:if>
			
			<tr>
				<td> ${componente.codigo}</td>
				<td> ${componente.nome} </td>
				<td> ${componente.tipoComponente} </td>
			</tr>
		
		</c:forEach>
		
		</table>
		<br>
		<table class="tabelaRelatorioBorda" width="100%">
			<tfoot>
				<tr>
					<td colspan="3" align="center"> ${fn:length(componentes)} componentes encontrados </td>
				</tr>			
			</tfoot>
		</table>
	

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
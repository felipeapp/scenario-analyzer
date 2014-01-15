<!-- lista de membros do projeto -->
	<c:if test="${ not empty projeto.subProjetos }">
		<tr> <td colspan="2" style="margin:0; padding: 0;">
		<table class="subFormulario" width="100%">
		<caption>Sub-Projetos</caption>
	        <tbody>
	    	<c:forEach items="${projeto.subProjetos}" var="subprojeto" varStatus="status">
	            <tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	            	<th>Título:</th>
					<td colspan="5">${subprojeto.titulo}</td>
				</tr>
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	            	<th>Área de Conhecimento:</th>
					<td colspan="5">${subprojeto.grandeArea.nome}</td>
				</tr>
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<th>Coordenador:</th>
					<td colspan="5">${subprojeto.coordenador.pessoa.nome}</td>
				</tr>
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td rowspan="2">&nbsp;</td>
	        		<td rowspan="2" style="text-align: right; font-weight: bold">Custeio</td>
	        	</tr>
	        	<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				    <td style="text-align: right; font-weight: bold"> Capital </td>
				    <td style="text-align: right; font-weight: bold"> Taxa de Administração </td>
				    <td style="text-align: right; font-weight: bold"> Overhead da Instituição </td>
				    <td style="text-align: right; font-weight: bold"> Subtotais </td>
				</tr>
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<th>Valores:</th>	
					<td style="text-align: right" width="16%"> <fmt:formatNumber currencySymbol="R$  " value="${ subprojeto.custeio }" type="currency" /></td>
					<td style="text-align: right" width="16%"> <fmt:formatNumber currencySymbol="R$  " value="${ subprojeto.capital }" type="currency" /></td>
					<td style="text-align: right" width="16%"> <fmt:formatNumber currencySymbol="R$  " value="${ subprojeto.taxa }" type="currency" /></td>
					<td style="text-align: right" width="16%"> <fmt:formatNumber currencySymbol="R$  " value="${ subprojeto.overhead }" type="currency" /></td>
					<td style="text-align: right" width="16%"> <fmt:formatNumber currencySymbol="R$  " value="${ subprojeto.total }" type="currency" /></td>
	            </tr>
	    	</c:forEach>
	        </tbody>
	    </table>	    
	    </td></tr>
	    <tr> <td colspan="2" style="margin:0; padding: 0;">
		<table class="subFormulario" width="100%">
			<caption>Total dos Valores Financiados</caption>
			<thead>
	        	<tr>
	        		<th rowspan="2">&nbsp;</th>
	        		<th rowspan="2" style="text-align: right">Custeio</th>
	        	</tr>
	        	<tr>
				    <th style="text-align: right"> Capital </th>
				    <th style="text-align: right"> Taxa de Administração </th>
				    <th style="text-align: right"> Overhead da Instituição </th>
				    <th style="text-align: right"> Subtotais </th>
		       </tr>
	        </thead>
	        <tbody>
	        	<tr>
	        		<th>Totais:</th>
	        		<td style="text-align: right" width="16%"><fmt:formatNumber currencySymbol="R$  " value="${ projeto.totalCusteio }" type="currency" /></td>
	        		<td style="text-align: right" width="16%"><fmt:formatNumber currencySymbol="R$  " value="${ projeto.totalCapital }" type="currency" /></td>
	        		<td style="text-align: right" width="16%"><fmt:formatNumber currencySymbol="R$  " value="${ projeto.totalTaxa }" type="currency" /></td>
	        		<td style="text-align: right" width="16%"><fmt:formatNumber currencySymbol="R$  " value="${ projeto.totalOverhead }" type="currency" /></td>
	        		<td style="text-align: right" width="16%"><fmt:formatNumber currencySymbol="R$  " value="${ projeto.valorTotal }" type="currency" /></td>
	        	</tr>
	        </tbody>
		</table>	    
	    </td></tr>
	</c:if>
	<!-- fim da lista de membros do projeto -->
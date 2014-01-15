<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>


<f:view>
	<h2>Relatório de Residentes no Cadastro Único</h2>

	<div id="parametrosRelatorio">
		<table >
			<tr>
				<th>Ano-Período:</th>
				<td> 
					${ relatoriosSaeMBean.ano } - ${ relatoriosSaeMBean.periodo }
				</td>
			</tr>
		</table>
	</div>
	<br/>
	<table class="tabelaRelatorioBorda" width="100%">	
		<thead>
		<tr>
			<th> Discente </th>
			<th style="text-align: left;"> Status </th>
		</tr>
		</thead>
		<tbody>
		<c:forEach var="item" items="#{relatoriosSaeMBean.listaResidentesCadastroUnico}">
			<tr>
				<td> ${item[0]} </td>										
				<td align="left"> ${item[1]} </td>
			</tr>
		</c:forEach>
		</tbody>
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
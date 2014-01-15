<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
</style>

<f:view>
	<h:outputText value="#{ relatoriosCoordenador.create }" />
	<h2>Relatório de Disciplinas com Ementas</h2>

		<b>Curso: </b> <c:out value="${ relatoriosCoordenador.curso.descricao }"/> <br/>
		<b>Ano/Período: </b><c:out value="${ relatoriosCoordenador.ano }"/>.<c:out value="${ relatoriosCoordenador.periodo }"/> 
		
		<br/>
		<br/>
		
		<table cellspacing="1" width="100%" style="font-size: 10px;" class="tabelaRelatorioBorda">	
			<tr class="header">
				<td><b>Código</b></td>	
				<td><b>Nome / Ementa</b></td>
			</tr>
			<c:forEach var="item" items="#{ relatoriosCoordenador.listaDisciplinasEmenta }" varStatus="status">
				<tr>
					<td>
						<b>${item.codigo}</b>
					</td>
					<td>
						<b>${item.detalhes.nome}</b>
					</td>
					
					<tr>
						<td>
						</td>
						<td>
							${item.detalhes.ementa}
							<br/><br/>
						</td>
					</tr>
				</tr>
			</c:forEach>
				<tr>
					<td colspan="6" style="text-align: center; font-weight: bold;">
						${fn:length(relatoriosCoordenador.listaDisciplinasEmenta)} registro(s) encontrado(s)
					</td>
				</tr>
		</table>
		
		<tfoot>
		</tfoot>
		
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
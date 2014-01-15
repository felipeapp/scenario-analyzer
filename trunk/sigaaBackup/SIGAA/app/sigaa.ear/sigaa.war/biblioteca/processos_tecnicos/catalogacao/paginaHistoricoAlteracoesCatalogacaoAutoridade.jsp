<%@include file="/biblioteca/cabecalho_popup.jsp"%>

<%-- Pagina que mostra o hist�rico de altera��es de um t�tulo ou autoridade na p�gina de cataloga��o  --%>

<h2> Biblioteca &gt;  Hist�rico das �ltimas Altera��es da Autoridade </h2>

<style type="text/css">

.tabelaRelatorioBorda td, td{
	border: 1px solid  #EEEEEE;
}

</style>


		
		
			<c:set var="listaModificacoes" value="${catalogacaoMBean.historicoAlteracaoAutoridade}"></c:set>
			
			
			<c:if test="${  fn:length( listaModificacoes) <= 0 }">
					<table width="90%" style="margin-right: auto; margin-left: auto">
						<tr>
							<td style="text-align: center; font-weight:bold; font-size: 18px">
								A Autoridade n�o possui hist�rio de altera��es ainda.
							</td>
						</tr>
					</table>
			</c:if>
			
			<c:if test="${  fn:length( listaModificacoes) > 0 }">
			
				<table class="tabelaRelatorioBorda" style="width:100%;">
	
					<thead>
						<tr>
							<th style="text-align:center; width: 20%;">Data</th>
							<th style="text-align:left;  width: 30%;">Catalogador</th>
							<th style="text-align:left; width: 50%">Informa��es da Autoridade</th>
						</tr>
					</thead>
					
					<tbody>
						<c:forEach var="informacoesHistorico" items="#{listaModificacoes}" varStatus="status">
							<tr  class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
								<td style="text-align:center; vertical-align: top;"><ufrn:format type="dataHora" valor="${informacoesHistorico[2]}" /> </td>
								<td style="vertical-align: top;">${informacoesHistorico[0]}</td>
								<td>${informacoesHistorico[1]}</td>
							</tr>
						</c:forEach>
					</tbody>
					
				</table>
			
			</c:if>
	


<%@include file="/biblioteca/rodape_popup.jsp"%>
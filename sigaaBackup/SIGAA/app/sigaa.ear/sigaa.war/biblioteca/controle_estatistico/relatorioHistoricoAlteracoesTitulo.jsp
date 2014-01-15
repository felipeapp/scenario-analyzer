<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

	<style>
		.tabelaRelatorioBorda tr{
			text-align:right;
		}
	</style>

	<f:view>

		<h2>Hist�rico de Altera��es de um T�tulo</h2>

		<div id="parametrosRelatorio" style="margin-bottom:10px;">
			<table>
				<tr>
					<th>T�tulo Catalogr�fico:</th><td>${emiteRelatorioHistoricosMBean.tituloCatalografico.formatoReferencia}</td>
				</tr>
				<c:if test="${emiteRelatorioHistoricosMBean.dataInicio != null || emiteRelatorioHistoricosMBean.dataFim != null}">
					<tr>
						<th>Per�odo:</th>
						<td>
							<c:if test="${emiteRelatorioHistoricosMBean.dataInicio != null}">A partir de <ufrn:format type="data" valor="${emiteRelatorioHistoricosMBean.dataInicio}" /></c:if>
							<c:if test="${emiteRelatorioHistoricosMBean.dataFim != null}">at� <ufrn:format type="data" valor="${emiteRelatorioHistoricosMBean.dataFim}" /></c:if>					
						</td>
					</tr>
				</c:if>
			</table>
		</div>

		<style>
		
			.tabelaRelatorioBorda tr td, .tabelaRelatorioBorda thread tr th{
				text-align:left;
				vertical-align:top;
			}
			
		</style>

		<table class="tabelaRelatorioBorda" style="width:100%;">

			<thead>
				<tr>
					<th style="text-align:center;">Data</th>
					<th>Catalogador</th>
					<th>Informa��es do T�tulo</th>
				</tr>
			</thead>
			
			<tbody>
				<c:forEach var="h" items="#{emiteRelatorioHistoricosMBean.historico}">
					<tr>
						<td style="text-align:center;"><ufrn:format type="dataHora" valor="${h[2]}" /></td>
						<td>${h[0]}</td>
						<td>${h[1]}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
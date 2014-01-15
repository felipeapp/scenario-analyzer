<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

	<style>
		.tabelaRelatorioBorda tr{
			text-align:right;
		}
	</style>

	<f:view>

		<h2> Histórico de Alterações de um Material</h2>

		<div id="parametrosRelatorio" style="margin-bottom:10px;">
			<table>
				<tr>
					<th>Material Informacional:</th><td>${emiteRelatorioHistoricosMBean.codigoBarras} - ${emiteRelatorioHistoricosMBean.materialInformacional.tituloCatalografico.formatoReferencia}</td>
				</tr>
				<tr>
					<th>Período:</th>
					<td>
						<c:if test="${emiteRelatorioHistoricosMBean.dataInicio != null}">de <ufrn:format type="data" valor="${emiteRelatorioHistoricosMBean.dataInicio}" /></c:if>
						<c:if test="${emiteRelatorioHistoricosMBean.dataInicio != null}">até <ufrn:format type="data" valor="${emiteRelatorioHistoricosMBean.dataFim}" /></c:if>					
					</td>
				</tr>
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
					<th>Usuário</th>
					<th>Informações do Material</th>
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
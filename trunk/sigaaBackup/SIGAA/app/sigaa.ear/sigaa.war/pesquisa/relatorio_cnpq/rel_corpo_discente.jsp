<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<f:view>
	
		<table width="100%" style="font-size: 11px;">
			<caption><td align="center"><b>${ relatoriosCNPQMBean.nomeRelatorio }</b></td></b>
		</table>
		<br />
		
		<c:if test="${ relatoriosCNPQMBean.exibirTipoBolsa || relatoriosCNPQMBean.exibirEditalPesquisa }">
			<table width="100%">
		  	  <c:if test="${ relatoriosCNPQMBean.exibirTipoBolsa }">
			  	  <tr>		
					<td width="13%"><b>Tipo Bolsa:</b></td> 
					<td>
						<h:outputText value="#{relatoriosCNPQMBean.tipoBolsaPesquisa.descricao}"/>
					</td>
				  </tr>
			  </c:if>
		  	  <c:if test="${ relatoriosCNPQMBean.exibirEditalPesquisa }">
			  	  <tr>		
					<td width="17%"><b>Edital Pesquisa:</b></td> 
					<td>
						<h:outputText value="#{ relatoriosCNPQMBean.editalPesquisa.edital.descricao }"/>
					</td>
				  </tr>
			  </c:if>
			</table>
			<br />
		</c:if>

		<table width="100%" class="tabelaRelatorioBorda" border="1">

			<thead>
				<tr>
					${ relatoriosCNPQMBean.cabecalho }
				</tr>
			</thead>

				<tr style="text-align: right;">
					${ relatoriosCNPQMBean.valores }
				</tr>
				
		</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
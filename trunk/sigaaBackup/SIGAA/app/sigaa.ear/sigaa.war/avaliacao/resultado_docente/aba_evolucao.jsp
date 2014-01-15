<rich:tab label="Gr�fico da Evolu��o da M�dia Geral por Per�odo" id="graphEvolucaoTab">
	<t:panelGrid id="evolucaoContentPanel" columns="2" columnClasses="columnClasses, columnClasses">
		<t:panelGroup>
			<cewolf:chart id="evolucao" type="verticalbar" 
	             title="Evolu��o da M�dia Geral por Per�odo"
	             xaxislabel="Ano-Per�odo"
	             yaxislabel="M�dia Geral" 
	             showlegend="false"> 
				<cewolf:colorpaint color="#D3E1F1"/> 
				<cewolf:data> 
					<cewolf:producer id="dados"> 
						<cewolf:param name="evolucao" value="<%= new Boolean(true)%>"/>
						<cewolf:param name="idServidor" value="${ portalResultadoAvaliacao.servidor.id }"/>
					</cewolf:producer>
				</cewolf:data>
			</cewolf:chart> 
			<cewolf:img chartid="evolucao" renderer="/cewolf" width="450" height="350">
				<cewolf:map tooltipgeneratorid="dados"/> 
			</cewolf:img>
		</t:panelGroup>
		<t:panelGroup>
			<div style="width: 80%; text-align: center">
				<table class="visualizacao" width="100%">
					<caption>M�dia Geral da Avalia��o Institucional por Ano-Per�odo</caption>
						<thead>
							<tr>
								<th width="10%">Ano-Per�odo</th>
								<th width="5%" style="text-align: right;">M�dia Geral<sup>[1]</sup></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="#{portalResultadoAvaliacao.mediasPorAnoPeriodo}" var="linha" varStatus="loop">
								<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
									<td>${ linha.key }</td>
									<td style="text-align: right;"><ufrn:format type="valor" valor="${ linha.value }" /></td>
								</tr>
							</c:forEach>
						</tbody>
				</table>
				<br/><b>[1]</b>: a M�dia Geral no Per�odo corresponde a m�dia das notas da ${portalResultadoAvaliacao.dimensaoMediaGeral}.
			</div>
		</t:panelGroup>
	</t:panelGrid>
</rich:tab>
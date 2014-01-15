<rich:tab label="Gráfico da Evolução da Média Geral por Período" id="graphEvolucaoTab">
	<t:panelGrid id="evolucaoContentPanel" columns="2" columnClasses="columnClasses, columnClasses">
		<t:panelGroup>
			<cewolf:chart id="evolucao" type="verticalbar" 
	             title="Evolução da Média Geral por Período"
	             xaxislabel="Ano-Período"
	             yaxislabel="Média Geral" 
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
					<caption>Média Geral da Avaliação Institucional por Ano-Período</caption>
						<thead>
							<tr>
								<th width="10%">Ano-Período</th>
								<th width="5%" style="text-align: right;">Média Geral<sup>[1]</sup></th>
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
				<br/><b>[1]</b>: a Média Geral no Período corresponde a média das notas da ${portalResultadoAvaliacao.dimensaoMediaGeral}.
			</div>
		</t:panelGroup>
	</t:panelGrid>
</rich:tab>

<tr><td style="height: 20px"></td></tr>
<tr>
	<td colspan="2" class="titulo" style="background-color: #DDDDDD">
		<c:forEach var="r" items="#{graficoBimestralNotas.regraNotas}">
			<c:if test="${r.id == idRegraAtual}">
				<h:outputText value="#{r.titulo}"/>
			</c:if>
		</c:forEach>
	<td>
</tr>

<tr>
	<td colspan="2" class="titulo" style="text-align: center;">Média da Turma: 
		<fmt:formatNumber maxFractionDigits="2" value="${mediaBimestre/contador}"/>  
	<td>
</tr>
<tr>
	<td class="titulo">Médias Maiores que ${graficoBimestralNotas.param.mediaMinimaPassarPorMedia}</td>
	<td style="border: 1px solid;">
		<fmt:formatNumber maxFractionDigits="2" value="${(maiores/contador)*100}"/>%
	<td>
</tr>
<tr>
	<td class="titulo">Médias entre ${graficoBimestralNotas.param.mediaMinimaAprovacao} e 
						${graficoBimestralNotas.param.mediaMinimaPassarPorMedia}</td>
	<td style="border: 1px solid;">
		<fmt:formatNumber maxFractionDigits="2" value="${(iguais/contador)*100}"/>%
	<td>
</tr>
<tr>
	<td class="titulo">Médias Menores que ${graficoBimestralNotas.param.mediaMinimaAprovacao}</td>
	<td style="border: 1px solid;">
		<fmt:formatNumber maxFractionDigits="2" value="${(menores/contador)*100}"/>%
	</td>
</tr>
<tr><td style="height:20px;"></td></tr>
<tr>
	<td colspan="2">
		<fieldset>
			<legend>Detalhes</legend> 
			<div align="center">
				<cewolf:chart id="GraficoBimestralNotas" type="pie"> 
					<cewolf:colorpaint color="#D3E1F1"/> 
					<cewolf:data> 
						<cewolf:producer id="dados"> 
							<cewolf:param name="maiores" value="${maiores}"/>
							<cewolf:param name="menores" value="${menores}"/>
							<cewolf:param name="iguais"  value="${iguais}"/>
							<cewolf:param name="mediaAprovacao"  value="${graficoBimestralNotas.param.mediaMinimaPassarPorMedia}"/>
							<cewolf:param name="mediaMinimaAprovacao"  value="${graficoBimestralNotas.param.mediaMinimaAprovacao}"/>
						</cewolf:producer>
					</cewolf:data> 
				</cewolf:chart> 
				<cewolf:img chartid="GraficoBimestralNotas" renderer="/cewolf" width="620" height="400"/> 
			</div>
		</fieldset>
	</td>
</tr>
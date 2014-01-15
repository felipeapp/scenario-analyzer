<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form>
 <h2><ufrn:subSistema /> > Lista de Avaliações Institucionais Anteriores</h2>
	<div class="descricaoOperacao">
		Caro usuário,<br/>
		Abaixo estão listadas as Avaliações Institucionais realizadas. Selecione a que deseja rever.<br/>
	</div>
			
	<div class="infoAltRem">
		<c:if test="${!calendarioAvaliacaoInstitucionalBean.portalAvaliacaoInstitucional}">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Rever Avaliação Institucional
		</c:if>
	</div>
	<table class="formulario" width="85%">
		<caption>Lista de Avaliações Institucionais</caption>
		<thead>
			<tr>
				<td style="text-align: center">Ano-Período</td>
				<td>Formulário</td>
				<td style="text-align: center">Ensino à Distância</td>
				<td style="text-align: center">Período de Resposta</td>
				<td></td>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{calendarioAvaliacaoInstitucionalBean.allPreenchidosDiscentes}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td style="text-align: center">${item.ano}.${item.periodo}</td>
					<td>${item.formulario.titulo}</td>
					<td style="text-align: center"><ufrn:format valor="${item.formulario.ead}" type="simNao" /></td>
					<td style="text-align: center"><ufrn:format type="data" valor="${item.inicio}" /> à <ufrn:format type="data" valor="${item.fim}"/></td>
					<td width="5%">
						<h:commandLink action="#{avaliacaoInstitucionalAnterior.reverAnterior}" rendered="#{!item.periodoPreenchimento}" id="rever">
							<h:graphicImage value="/img/seta.gif" style="overflow: visible;" title="Rever Avaliação Institucional"/>
							<f:param name="id" value="#{item.id}" />
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="7">
					<h:commandButton action="#{calendarioAvaliacaoInstitucionalBean.cancelar}" value="Cancelar" onclick="#{ confirm }" />
				</td>
			</tr>
		</tfoot>
	</table>
	<br/>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form>
 <h2><ufrn:subSistema /> > Lista de Avalia��es Institucionais Anteriores</h2>
	<div class="descricaoOperacao">
		Caro usu�rio,<br/>
		Abaixo est�o listadas as Avalia��es Institucionais realizadas. Selecione a que deseja rever.<br/>
	</div>
			
	<div class="infoAltRem">
		<c:if test="${!calendarioAvaliacaoInstitucionalBean.portalAvaliacaoInstitucional}">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Rever Avalia��o Institucional
		</c:if>
	</div>
	<table class="formulario" width="85%">
		<caption>Lista de Avalia��es Institucionais</caption>
		<thead>
			<tr>
				<td style="text-align: center">Ano-Per�odo</td>
				<td>Formul�rio</td>
				<td style="text-align: center">Ensino � Dist�ncia</td>
				<td style="text-align: center">Per�odo de Resposta</td>
				<td></td>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{calendarioAvaliacaoInstitucionalBean.allPreenchidosDiscentes}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td style="text-align: center">${item.ano}.${item.periodo}</td>
					<td>${item.formulario.titulo}</td>
					<td style="text-align: center"><ufrn:format valor="${item.formulario.ead}" type="simNao" /></td>
					<td style="text-align: center"><ufrn:format type="data" valor="${item.inicio}" /> � <ufrn:format type="data" valor="${item.fim}"/></td>
					<td width="5%">
						<h:commandLink action="#{avaliacaoInstitucionalAnterior.reverAnterior}" rendered="#{!item.periodoPreenchimento}" id="rever">
							<h:graphicImage value="/img/seta.gif" style="overflow: visible;" title="Rever Avalia��o Institucional"/>
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
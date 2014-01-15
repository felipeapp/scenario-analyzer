<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="portalFamiliar" />
	<h:form>
		
		<h2><ufrn:subSistema /> &gt; Frequências </h2>
		
		<c:set var="totalFaltas" value="#{0}"/>
			
		<a4j:outputPanel rendered="#{fn:length(portalFamiliar.frequencias ) > 0}">
			<table class="listagem" style="width: 300px">
				<thead>
					<tr><th style="text-align:center;">Data</th><th style="text-align:left;">Situação</th></tr>
				</thead>
				<tbody>
					<c:forEach var="item" items="#{ portalFamiliar.frequencias }" varStatus="loop">
						<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td class="first" width="50%" style="text-align:center;border-right: 1px solid #DEDFE3">
								<fmt:formatDate value="${ item.data }" pattern="dd/MM/yyyy"/>
							</td>
							<td width="50%" style="text-align:left;">
								<c:set var="totalFaltas" value="#{ totalFaltas + item.faltas }"/>
								<h:outputText value="Presente" rendered="#{ item.faltas == 0}" />
								<h:outputText value="#{ item.faltas } Falta(s)" rendered="#{ item.faltas > 0 }" />
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</a4j:outputPanel>
		
		<h:outputText value="A frequência ainda não foi lançada." style="color:#C00;text-align:center;display:block;font-weight:bold;" rendered="#{fn:length( portalFamiliar.frequencias ) == 0}" />

		<div class="botoes-show" style="text-align: center;">
			<b>Total de Faltas:</b> <h:outputText value="#{ totalFaltas }" /><br/>
		</div> 
	
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
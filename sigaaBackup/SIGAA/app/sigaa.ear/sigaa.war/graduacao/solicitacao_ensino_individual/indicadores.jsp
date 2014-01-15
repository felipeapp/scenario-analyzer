<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h:outputText value="#{indicadorSolicitacaoEnsinoIndividualMBean.create}"/>

	<h2> Indicadores de solicita��es de ensino individual </h2>
	
	<div class="descricaoOperacao">
		<p>Caro usu�rio,</p>
		<p>
			Abaixo est�o listadas as demandas de solicita��es de ensino individualizado, 
			podendo ser utilizadas como indicadores para a cria��o de turmas regulares, 
		</p>
	</div>
		
	<h:form id="form">
		<table class="listagem">
			<caption class="listagem"> Demanda de solicita��es de ensino individual</caption>
			<thead>
				<tr>
					<th> Componente </th>
					<th style="text-align: right;"> Qtde. de Solicita��es </th>
					<th width="3%"></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${indicadorSolicitacaoEnsinoIndividualMBean.mapResultado}" var="mapa" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}"> 
						<td>${mapa.key}</td>
						<td align="right">${fn:length(mapa.value)}</td>
						<td></td> 
					</tr>
				</c:forEach>	
			</tbody>
		</table>	
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
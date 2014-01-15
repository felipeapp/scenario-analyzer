<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Histórico de Bolsas do Discente na Instituição </h2>
	
	<div class="descricaoOperacao" style="width: 90%">
		<h4> Caro aluno, </h4>
		<p>
			Abaixo estão listadas as bolsas remuneradas, pagas pela instituição, recebidas
			por você durante o período deste vínculo com a mesma.
		</p>
	</div>
	
	<table class="listagem" style="width: 90%">
		<caption>Bolsas encontradas</caption>	
		<thead>
		<tr>
			<th> Tipo da Bolsa </th>
			<th> Unidade </th>
			<th> Início </th>
			<th> Fim </th>
		</tr>
		</thead>
		<c:forEach items="#{relatorioBolsasDiscenteBean.bolsas}" var="_bolsista" varStatus="status">
			<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td> ${ _bolsista.tipoBolsa } </td>
				<td> ${ _bolsista.unidade.codigoNome } </td>
				<td> <h:outputText value="#{_bolsista.dataInicio}"> <f:convertDateTime/> </h:outputText> </td>
				<td> <h:outputText value="#{_bolsista.dataFim}"> <f:convertDateTime/> </h:outputText> </td>
			</tr>		
		</c:forEach>
	</table>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

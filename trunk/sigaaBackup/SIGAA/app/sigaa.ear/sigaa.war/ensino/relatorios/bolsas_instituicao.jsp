<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Hist�rico de Bolsas do Discente na Institui��o </h2>
	
	<div class="descricaoOperacao" style="width: 90%">
		<h4> Caro aluno, </h4>
		<p>
			Abaixo est�o listadas as bolsas remuneradas, pagas pela institui��o, recebidas
			por voc� durante o per�odo deste v�nculo com a mesma.
		</p>
	</div>
	
	<table class="listagem" style="width: 90%">
		<caption>Bolsas encontradas</caption>	
		<thead>
		<tr>
			<th> Tipo da Bolsa </th>
			<th> Unidade </th>
			<th> In�cio </th>
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

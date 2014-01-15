<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/discente/menu_discente.jsp" %>
	<h2><ufrn:subSistema/> &gt; Situa��o da sua solicita��o </h2>
	<div class="descricaoOperacao">
	<p>Caro usu�rio,</p><br/>
	
	<p>
		Por essa p�gina voc� pode acompanhar a situa��o de sua solicita��o de Bolsa Aux�lio,<br/>
		verificando se a mesma encontra-se: <br/>
			<ul>
				<li><b> Em An�lise </b></li> 
				<li><b> Deferida e Contemplada </b></li>
				<li><b> Deferida, em Fila de Espera</b></li>
				<li><b> Indeferida </b></li>
			</ul>

	<p>	Caso ainda n�o tenha comparecido ao Departamento de Estudante com os documentos <br/>
		necess�rios, fa�a isso, pois s� assim sua solicita��o ser� analisada pelo departamento!<br/><br/>
	</p> 
	<p>	O per�odo de in�cio de fim s� ser� exibido para as bolsas PROMISAES, per�odo esse definido pelos gestores da PROAE. </p> 
	</div>

	<h:form id="form">
		<table class="formulario" width="100%">
			<caption> Situa��o da sua solicita��o </caption>
			<thead>
				<tr>
					<th>Bolsa Auxilio</th>
					<th>Situa��o da Bolsa</th>
					<th style="text-align: center;">In�cio Bolsa</th>
					<th style="text-align: center;">Fim Bolsa</th>
					<th style="text-align: center;">Ano-Per�odo</th>
				</tr>
			</thead>
			<c:forEach items="${ bolsaAuxilioMBean.todasSolicitacoesBolsaAuxilioDiscente }" var="linha" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td> ${linha.bolsaAuxilio.tipoBolsaAuxilio.denominacao } </td>
					<td> ${linha.bolsaAuxilio.situacaoBolsa.denominacao } </td>
					
					<c:choose>
						<c:when test="${ linha.bolsaAuxilio.tipoBolsaAuxilio.promisaes }">
							<td style="text-align: center;"> <fmt:formatDate value="${linha.inicioBolsa}" pattern="dd/MM/yyyy"/> </td>
							<td style="text-align: center;"> <fmt:formatDate value="${linha.fimBolsa}" pattern="dd/MM/yyyy"/> </td>
						</c:when>
						<c:otherwise>
							<td style="text-align: center;"> --- </td>
							<td style="text-align: center;"> --- </td>
						</c:otherwise>
					</c:choose>
					<td style="text-align: center;"> ${linha.ano}.${linha.periodo}</td>
				</tr>
				
			</c:forEach>
			
		</table>
		
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/discente/menu_discente.jsp" %>
	<h2><ufrn:subSistema/> &gt; Situação da sua solicitação </h2>
	<div class="descricaoOperacao">
	<p>Caro usuário,</p><br/>
	
	<p>
		Por essa página você pode acompanhar a situação de sua solicitação de Bolsa Auxílio,<br/>
		verificando se a mesma encontra-se: <br/>
			<ul>
				<li><b> Em Análise </b></li> 
				<li><b> Deferida e Contemplada </b></li>
				<li><b> Deferida, em Fila de Espera</b></li>
				<li><b> Indeferida </b></li>
			</ul>

	<p>	Caso ainda não tenha comparecido ao Departamento de Estudante com os documentos <br/>
		necessários, faça isso, pois só assim sua solicitação será analisada pelo departamento!<br/><br/>
	</p> 
	<p>	O período de início de fim só será exibido para as bolsas PROMISAES, período esse definido pelos gestores da PROAE. </p> 
	</div>

	<h:form id="form">
		<table class="formulario" width="100%">
			<caption> Situação da sua solicitação </caption>
			<thead>
				<tr>
					<th>Bolsa Auxilio</th>
					<th>Situação da Bolsa</th>
					<th style="text-align: center;">Início Bolsa</th>
					<th style="text-align: center;">Fim Bolsa</th>
					<th style="text-align: center;">Ano-Período</th>
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
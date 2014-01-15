<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2 class="tituloPagina"> <ufrn:subSistema/> > Acompanhar Bolsa Auxilio </h2>

	<h:form>
	<c:set var="conteudos" value="#{bolsaAuxilioMBean.anoReferencia.calendario}"/>
	<c:if test="${not empty conteudos}">
		<div class="descricaoOperacao">
		<center><b>POR FAVOR, LEIA AS INSTRU��ES ABAIXO </b></center>
			
			<br/>
			
				<table width="350" align="center">
				<caption ><b>PER�ODOS DE DIVULGA��O DO RESULTADO</b></caption>
					<thead>
						<tr>
							<td>Tipo da Bolsa</td>
							<td>&nbsp;</td>
							<td style="text-align: center;">In�cio</td>
							<td>&nbsp;</td>
							<td style="text-align: center;">Fim</td>
							<td>&nbsp;</td>
							<td>Munic�pio</td>
							<td>&nbsp;</td>
							<td>Discentes permitidos</td>
						</tr>
					</thead>
					<c:forEach var="item" items="#{bolsaAuxilioMBean.anoReferencia.calendario}">
						<tr>
							<td nowrap="true">${ item.tipoBolsaAuxilio.denominacao }</td>
							<td>&nbsp;</td>
							<td style="text-align: center;"><fmt:formatDate pattern="dd/MM/yyyy" value="${item.inicioDivulgacaoResultado}"/> </td>
							<td>&nbsp;</td>
							<td style="text-align: center;"><fmt:formatDate pattern="dd/MM/yyyy" value="${item.fimDivulgacaoResultado}"/> </td>
							<td>&nbsp;</td>
							<td nowrap="true">${item.municipio.nome}</td>
							<td>&nbsp;</td>
							<td nowrap="true">
								<c:if test="${item.alunoNovato == true}">Discentes novatos</c:if> <c:if test="${item.alunoVeterano == true}">/</c:if>
								<c:if test="${item.alunoVeterano == true}">Discentes veteranos</c:if>
							</td>
						</tr>
					</c:forEach>		
				</table>
			</div>
		</c:if>	
	
	  <table class="formulario" width="50%">
		
		<caption> Solicita��es Encontradas </caption>
		<thead>
			<tr>
				<th style="text-align: center;">Ano Per�odo Solicita��o</th>
				<th>Tipo Bolsa Aux�lio</th>
				<th>Situa��o Bolsa Aux�lio</th>
			</tr>
		</thead>
	
			<tbody>
				<c:choose>
					<c:when test="${ not empty bolsaAuxilioMBean.bolsas }">
						<c:forEach items="#{bolsaAuxilioMBean.bolsas}" var="bolsaAux" varStatus="loop">
							<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td style="text-align: center;">${bolsaAux.ano}.${bolsaAux.periodo}</td>
								<td>${bolsaAux.bolsaAuxilio.tipoBolsaAuxilio.denominacao}</td>
								<td>${bolsaAux.bolsaAuxilio.situacaoBolsa.denominacao}</td>
							</tr>
						</c:forEach>	
					</c:when>
					<c:otherwise>
						<tr>
							<td colspan="3" align="center">
								N�o foi encontrada nenhuma bolsa / solicita��o.
							</td>
						</tr>
					</c:otherwise>
				</c:choose>
			</tbody>
	  </table>
	  
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
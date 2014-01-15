<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:form>
		<c:set var="perguntas" value="${atendimentoAluno.todasPerguntas}" />
	
		<c:if test="${empty perguntas}">
			<i><center>Não possui perguntas.</center></i>
		</c:if>
		<c:if test="${not empty perguntas}">
			<div class="infoAltRem" style="font-variant: small-caps;">
				<h:graphicImage value="/img/avancar.gif"style="overflow: visible;"/>: Abrir Pergunta
			</div>
					
			<table class="listagem" width="80%">
				<thead>
					<tr>
						<td width="60%">Pergunta</td>
						<td style="text-align:left;">Status</td>
						<td></td>
					</tr>
				</thead>
				<c:forEach items="${perguntas}" var="pergunta" varStatus="loop">
					<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${pergunta.titulo}</td>
						<td align="left">${ pergunta.statusAtendimento.descricao }</td>
						<%-- 
						<td align="right">
							<a href="#" onclick="PainelLerResposta.show(${pergunta.id})"><img src="${ctx}/img/avancar.gif"/></a>
						</td>
						--%>
						<td align="right">
							<a href="javascript:void(0)" onclick="habilitarDetalhes(${pergunta.id});"><img src="${ctx}/img/avancar.gif"/></a>
						</td>
					</tr>
					<tr>
						<td colspan="3" id="linha_${pergunta.id}" class="detalhesDiscente" ></td>	
					</tr>					
				</c:forEach>					
			</table>
		</c:if>
		</h:form>
		
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>		
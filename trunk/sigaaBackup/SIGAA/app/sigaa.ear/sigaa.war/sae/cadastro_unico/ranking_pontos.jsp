<% 
	response.setHeader("Cache-Control", "must-revalidate");
	response.setHeader("Cache-Control", "max-age=1");
	response.setHeader("Cache-Control", "no-cache");
	response.setIntHeader("Expires", 0);
	response.setHeader("Pragma", "no-cache");
%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<f:view>
	
	<h2>Ranking de Pontuação</h2>
	
	<div id="parametrosRelatorio">
		<table >
			<tr>
				<th>Ano-Período:</th>
				<td> 
					${rankingPontuacao.ano}.${rankingPontuacao.periodo}
				</td>
			</tr>
		</table>
	</div>
	<br />
	<table class="tabelaRelatorioBorda" width="100%">
		<thead>
			<tr>
				<th style="text-align: center;">Matrícula</th>
				<th>Nome</th>
				<th style="text-align: right;">Pontos</th>
			</tr>	
		</thead>
		<tbody>
			<c:if test="${not empty rankingPontuacao.resultado}">
				<c:forEach items="#{rankingPontuacao.resultado}" var="adesao">
					<tr>
						<td align = "center">${ adesao.discente.matricula }</td>
						<td>${ adesao.discente.nome }</td>
						<td align="right" width="10%">${adesao.pontuacao}</td>
					</tr>
					<c:set var="total" value="${total+1}" />
				</c:forEach>
			</c:if>
		</tbody>
		<tr>
			<th colspan="3">Total de Alunos Inscritos: ${total}</th>
		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
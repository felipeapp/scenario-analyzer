<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.header td {background-color: #eee; font-weight: bold;}
</style>

<f:view>
	<h2>${relatoriosVestibular.nomeRelatorio }</h2>
<div id="parametrosRelatorio">
<table>
	<tr>
		<th>Processo Seletivo:</th>
		<td>${relatoriosVestibular.obj.nome}</td>
	</tr>
</table>
</div>
<br/>

<table class="tabelaRelatorioBorda" align="center" width="100%">
		<thead>
			<tr>
				<th style="text-align: left;">Pergunta / Resposta</th>				
				<th style="text-align: right;">Total</th>
			</tr>
		</thead>
	
	<c:set var="_pergunta"/>
	<c:forEach items="#{relatoriosVestibular.linhaQuestionarioRespostas}" var="item" varStatus="indice">
	   <c:set var="perguntaAtual" value="${item.pergunta}"/>
		  <c:if test="${_pergunta != perguntaAtual}">
			<tr class="header">
				<td colspan="3" style="font-size: 12px;"><b>${item.ordem}.${item.pergunta}</b></td>
			</tr>
			<c:set var="_pergunta" value="${perguntaAtual}"/>
		  </c:if>
		    <tr>
		    	<td>
			    	<c:choose>
				    	<c:when test="${item.alternativa == 'true'}">
				    		Verdadeiro
				    	</c:when>
				    	<c:when test="${item.alternativa == 'false'}">
				    		Falso
				    	</c:when>
				    	<c:otherwise>
				    		${item.alternativa}
				    	</c:otherwise>
		    	</c:choose>
		    	</td>
				<td style="text-align: right; width: 12%;"><ufrn:format type="valorInt" valor="${item.totalParcial}"/></td> 
			</tr>
	</c:forEach>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
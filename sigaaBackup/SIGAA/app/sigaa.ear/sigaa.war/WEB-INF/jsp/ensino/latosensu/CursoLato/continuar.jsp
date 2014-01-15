<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import=" br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta" %>
<h2>
	<ufrn:subSistema /> &gt; Propostas Cadastradas pelo usuário
</h2>

<br>

<c:if test="${not empty lista}">
	<br/>
	<div class="infoAltRem">
	    <html:img page="/img/seta.gif" style="overflow: visible;"/>
	    : Continuar Preenchimento da Proposta
	    <html:img page="/img/view.gif" style="overflow: visible;"/>
	    : Visualizar Dados da Proposta
	    <html:img page="/img/delete.gif" style="overflow: visible;"/>
	    : Excluir Proposta
	</div>
	<br>

	<table class="listagem">
	<caption>Propostas Cadastradas pelo usuário</caption>
		<thead>
			<tr>
	        	<th>Curso</th>
	       	 	<th>Situação da Proposta</th>
	       		<th>&nbsp;</th>
	        	<th>&nbsp;</th>
	        	<th>&nbsp;</th>
			</tr>
		</thead>

        <tbody>
        <c:forEach items="${lista}" var="curso" varStatus="status">
        <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td> ${curso.descricao} </td>
	        <td> ${curso.propostaCurso.situacaoProposta.descricao} </td>
	        <c:set var="INCOMPLETA" value="<%= String.valueOf(SituacaoProposta.INCOMPLETA) %>" scope="application"></c:set>
	        <c:set var="SUBMETIDA" value="<%= String.valueOf(SituacaoProposta.SUBMETIDA) %>" scope="page"></c:set>
	        <c:set var="ACEITA" value="<%= String.valueOf(SituacaoProposta.ACEITA) %>" scope="page"></c:set>
	        
			<td width="15">
				<c:if test="${ curso.propostaCurso.situacaoProposta.id == INCOMPLETA }">
		        	<html:link action="/ensino/latosensu/criarCurso?dispatch=edit&id=${ curso.id }">
	                	<img src="<%= request.getContextPath() %>/img/seta.gif" alt="Continuar cadastro" title="Continuar cadastro" border="0"/>
	                </html:link>
                </c:if>
	        </td>
            <td width="15">
	        	<html:link action="/ensino/latosensu/criarCurso?dispatch=view&id=${ curso.id }">
                	<img src="<%= request.getContextPath() %>/img/view.gif" alt="Visualizar Proposta" title="Visualizar Proposta" border="0"/>
                </html:link>
            </td>
            <td width="10">
           		<c:if test="${ !(curso.propostaCurso.situacaoProposta.id == SUBMETIDA || curso.propostaCurso.situacaoProposta.id == ACEITA) }">
            		<html:link action="/ensino/latosensu/criarCurso?dispatch=remove&id=${ curso.id }">
                		<img src="<%= request.getContextPath() %>/img/delete.gif" alt="Excluir Proposta" title="Excluir Proposta" border="0"/>
                	</html:link>
            	</c:if>  
            </td>
		</tr>
	    </c:forEach>
	</table>

</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>


<f:view>
	<h2><ufrn:subSistema /> > Localizar Resumos do Seminário de Iniciação à Docência -  SID</h2>

 	<h:messages showDetail="true" showSummary="true" />
 	<c:set var="resumos" value="#{resumoSid.resumos}"/>
 	<c:if test="${empty resumos}">
			<center><i> Nenhum resumo encontrado para as opções de busca! </i></center>
	</c:if>
 	<c:if test="${not empty resumos}">
	<h:form>
	
		<div class="infoAltRem">
		    <h:graphicImage value="/img/check.png" style="overflow: visible;" rendered="#{acesso.monitoria}"/><h:outputText rendered="#{acesso.monitoria}" value=": Enviou Resumo"/>
		    <h:graphicImage value="/img/cross.png" style="overflow: visible;" />: Não Enviou Resumo
	    </div>
		<br/>
		
	
		<table class="listagem">
	    	<caption>Projetos Encontrados</caption>

		 	<tbody>
    	   		<c:forEach items="#{resumoSid.resumos}" var="resumo" varStatus="status">
					<tr>
						<td colspan="4" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
							${ resumo.projetoEnsino.anoTitulo }
						</td>
					</tr>
					
					<c:forEach items="#{resumo.participacoesSid}" var="participacao" varStatus="status">			
        	       		<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
        	       			<td>
        	       			<c:choose>
        	       				<c:when test="${participacao.participou == true}">
        	       					<h:graphicImage value="/img/check.png" style="overflow: visible;" />
        	       				</c:when>
        	       				<c:when test="${participacao.participou == false}">
        	       					<h:graphicImage value="/img/cross.png" style="overflow: visible;" />
        	       				</c:when>
        	       			</c:choose>
        	       			
        	       			</td>
            	        	<%-- <td> <h:selectBooleanCheckbox id="chkParticipouSid" value="#{participacao.participou}" styleClass="noborder"  readonly="true" disabled="true" /> </td>--%>
                	    	<td> ${ participacao.discenteMonitoria.discente.nome } </td>
							<td width="26%"> </td>
	              		</tr>
					</c:forEach>
				</c:forEach>
			</tbody>			
		</table>		
	</h:form>
	</c:if>
</f:view>
	
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
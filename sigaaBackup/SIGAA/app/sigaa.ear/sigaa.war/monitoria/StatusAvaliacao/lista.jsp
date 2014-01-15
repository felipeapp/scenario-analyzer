<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > statusAvaliacao</h2><br>
<h:outputText value="#{statusAvaliacao.create}"/>

	<div class="infoAltRem">
	    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Status de Avaliação
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Status de Avaliação<br/>
	</div>

	<h:form>
	<table class=listagem><tr>
	<caption class="listagem"> Lista de statusAvaliacaos</caption><td>descricao</td>
	<td></td><td></td></tr>
	<c:forEach items="#{statusAvaliacao.all}" var="item">
		<tr>
			<td>${item.descricao}</td>
					<td  width=20>
					
						<h:commandLink title="Alterar" action="#{statusAvaliacao.atualizar}" style="border: 0;">
						   	<f:param name="id" value="#{item.id}"/>				    	
							<h:graphicImage url="/img/alterar.gif"   />
						</h:commandLink>
						
					
					<h:commandLink title="Remover" action="#{statusAvaliacao.preRemover}" style="border: 0;">
					   	<f:param name="id" value="#{util.id}"/>				    	
						<h:graphicImage url="/img/delete.gif"  />
					</h:commandLink>
					
			</td>
		</tr>
	</c:forEach>
	</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
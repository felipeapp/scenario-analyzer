<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages/>
	<h2><ufrn:subSistema /> > Pólo</h2>
	<br>

	<h:outputText value="#{polo.create}"/>

	<div class="infoAltRem">
	    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Pólo
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Pólo<br/>
	</div>


	<table class="listagem">
			<caption class="listagem">Lista de Pólos </caption>

	<c:forEach items="${poloBean.all}" var="polo">
		<tr>
			<td> ${poloBean.endereco} </td>
			<h:form>
				<td>
					<input type="hidden" value="${polo.id}" name="id"/>
					<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{poloBean.atualizar}" style="border: 0;"/>

				</td>
			</h:form>
			<h:form>
				<td  width="25">
					<input type="hidden" value="${polo.id}" name="id"/>
					<h:commandButton image="/img/delete.gif" alt="Remover" action="#{poloBean.preRemover}" style="border: 0;"/>
				</td>
			</h:form>
	</c:forEach>
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

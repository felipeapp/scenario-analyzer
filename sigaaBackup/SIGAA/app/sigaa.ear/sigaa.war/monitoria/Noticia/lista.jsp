<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages/>
	<h2><ufrn:subSistema /> > Not�cias</h2>
	<br>

	<h:outputText value="#{noticiaMonitoria.create}"/>


	<div class="infoAltRem">
	    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Not�cia
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Not�cia<br/>
	</div>

	<h:form>
	<table class="listagem">
		<caption class="listagem">Lista de Not�cias</caption>
		<thead>
			<tr>
				<th> Descri��o </th>
				<th> Validade  </th>
				<th> Publicada </th>
				<th>  </th>
				<th>  </th>
			</tr>
		</thead>
	<tbody>
	<c:forEach items="#{noticiaMonitoria.all}" var="noticia">
		<tr>
			<td> ${noticia.descricao} </td>
			<td> <fmt:formatDate value="${noticia.validade}" pattern="dd/MM/yyyy" var="data"/>${data} </td>
			<td> ${noticia.publicada ? "Sim" : "N�o" }
			<td width="2%">
				
				<h:commandLink title="Alterar" action="#{noticiaMonitoria.atualizar}" style="border: 0;">
				    	<f:param name="id" value="#{noticia.id}"/>
				      	<h:graphicImage url="/img/alterar.gif" />
				</h:commandLink>
			</td>																
			<td width="2%">	
				<h:commandLink title="Remover" action="#{noticiaMonitoria.preRemover}" style="border: 0;">
				    	<f:param name="id" value="#{noticia.id}"/>
				      	<h:graphicImage url="/img/delete.gif" />
				</h:commandLink>															
			</td>
	</c:forEach>
	</tbody>
	</table>
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
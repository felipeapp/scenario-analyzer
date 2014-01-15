<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Tipo Artístico</h2>

	<h:outputText value="#{tipoArtistico.create}" />
<c:set var="lista" value="${tipoArtistico.all}" />
<c:if test="${empty lista}">
	<br />
	<center>
	<span style="color:red;">Nenhum Tipo Artístico Encontrado.</span>
	</center>
</c:if>
<c:if test="${not empty lista}">	
	
	
	<table class=listagem>
		<tr>
			<caption class="listagem">Lista de Tipos Artísticos</caption>
			<td>Descrição</td>
			<td>Contexto</td>
			<td></td>
			<td></td>
		</tr>
		<c:forEach items="${tipoArtistico.all}" var="item">
			<tr>
				<td>${item.descricao}</td>
				<td>${item.contexto}</td>

				<td width=20>
				 <h:form>
				  <input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/alterar.gif" value="Alterar"
						action="#{tipoArtistico.atualizar}" />
				  </h:form>
				 </td>

				<td width=25>
				 <h:form>
				   <input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/delete.gif" alt="Remover"
						action="#{tipoArtistico.remover}" onclick="#{confirmDelete}"/>
				 </h:form>
				</td>

			</tr>
		</c:forEach>
	</table>

</c:if>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

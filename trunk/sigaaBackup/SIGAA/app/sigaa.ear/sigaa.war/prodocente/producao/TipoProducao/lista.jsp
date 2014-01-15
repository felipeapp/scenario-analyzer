<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Tipo de Produção</h2>

	<h:outputText value="#{tipoProducao.create}" />
<c:set var="lista" value="${tipoProducao.all}" />
<c:if test="${empty lista}">
	<br />
	<center>
	<span style="color:red;">Nenhum Tipo Produção Encontrado.</span>
	</center>
</c:if>
<c:if test="${not empty lista}">	
	
	
	<table class=listagem>
		<tr>
			<caption class="listagem">Lista de Tipos de Produção</caption>
			<td>Descrição</td>
			<td></td>
			<td></td>
		</tr>
		<c:forEach items="${tipoProducao.all}" var="item">
			<tr>
				<td>${item.descricao}</td>

				<td width=20>
				 <h:form>
				  <input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/alterar.gif" value="Alterar"
						action="#{tipoProducao.atualizar}" />
				  </h:form>
				 </td>

				<td width=25>
				 <h:form>
				   <input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/delete.gif" alt="Remover"
						action="#{tipoProducao.remover}" onclick="#{confirmDelete}"/>
				 </h:form>
				</td>


			</tr>
		</c:forEach>
	</table>

</c:if>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

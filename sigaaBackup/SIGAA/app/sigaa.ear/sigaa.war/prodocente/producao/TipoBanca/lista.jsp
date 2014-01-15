<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Tipo de Banca</h2>

	<h:outputText value="#{tipoBanca.create}" />
<c:set var="lista" value="${tipoBanca.all}" />
<c:if test="${empty lista}">
	<br />
	<center>
	<span style="color:red;">Nenhum Tipo Banca Encontrado.</span>
	</center>
</c:if>
<c:if test="${not empty lista}">	
	
	
	<table class=listagem>
		<tr>
			<caption class="listagem">Lista de Tipos de Banca</caption>
			<td>Descrição</td>
			<td></td>
			<td></td>
		</tr>
		<c:forEach items="${tipoBanca.all}" var="item">
			<tr>
				<td>${item.descricao}</td>

				<td width=20>
				 <h:form>
				  <input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/alterar.gif" value="Alterar"
						action="#{tipoBanca.atualizar}" />
				  </h:form>
				</td>

				<td width=25>
				 <h:form>
				  <input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/delete.gif" alt="Remover"
						action="#{tipoBanca.remover}" onclick="#{confirmDelete}"/>
				 </h:form>
				</td>

			</tr>
		</c:forEach>
	</table>

</c:if>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

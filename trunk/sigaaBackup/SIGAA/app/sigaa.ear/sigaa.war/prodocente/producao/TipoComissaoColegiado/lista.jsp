<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Tipo de Comissão de Colegiado</h2>

	<h:outputText value="#{tipoComissaoColegiado.create}" />
<c:set var="lista" value="${tipoComissaoColegiado.all}" />
<c:if test="${empty lista}">
	<br />
	<center>
	<span style="color:red;">Nenhum Tipo Comissão Colegiado Encontrado.</span>
	</center>
</c:if>
<c:if test="${not empty lista}">	

	
	<table class=listagem>
		<tr>
			<caption class="listagem">Lista de Tipo de Comissão de Colegiado</caption>
			<td>Descrição</td>
			<td>Natureza</td>
			<td></td>
			<td></td>
		</tr>
		<c:forEach items="${tipoComissaoColegiado.all}" var="item">
			<tr>
				<td>${item.descricao}</td>
				<td>${item.natureza}</td>


				<td width=20>
				 <h:form>
				  <input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/alterar.gif" value="Alterar"
						action="#{tipoComissaoColegiado.atualizar}" />
				  </h:form>
				 </td>

				<td width=25>
				 <h:form>
				   <input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/delete.gif" alt="Remover"
						action="#{tipoComissaoColegiado.remover}" onclick="#{confirmDelete}"/>
				 </h:form>
				</td>

			</tr>
		</c:forEach>
	</table>
</c:if>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

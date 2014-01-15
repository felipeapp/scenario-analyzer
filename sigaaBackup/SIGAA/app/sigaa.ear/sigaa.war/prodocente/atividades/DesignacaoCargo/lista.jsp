<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Cargo</h2>
	<br>
	<h:outputText value="#{designacaoCargo.create}" />
	<table class=listagem>
		<tr>
			<caption class="listagem">Lista de Cargos</caption>
			<td>Descrição</td>
			<td>Gratificada</td>
			<td>Tipo</td>
			<td>Codigo Rhnet</td>
			<td></td>
			<td></td>
		</tr>
		<c:forEach items="${designacaoCargo.allAtividades}" var="item">
			<tr>
				<td>${item.descricao}</td>
				<td>${item.gratificada}</td>
				<td>${item.tipo}</td>
				<td>${item.codigoRhnet}</td>
				<h:form>
					<td width=20><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/alterar.gif" value="Alterar"
						action="#{designacaoCargo.atualizar}" /></td>
				</h:form>
				<h:form>
					<td width=25><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/delete.gif" alt="Remover"
						action="#{designacaoCargo.remover}" onclick="javascript:if(confirm('Deseja realmente REMOVER essa atividade ?')){ return true;} return false; void(0);" /></td>
				</h:form>
			</tr>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

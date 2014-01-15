<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> > Chefia</h2>
	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" /> <h:commandLink action="#{chefia.preCadastrar}" value="Cadastrar Nova Chefia"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Chefia
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Chefia <br/>
		</div>
	</h:form>
	<h:outputText value="#{chefia.create}" />
	<table class="listagem">
		<caption class="listagem">Lista de Chefias</caption>
		<thead>
			
			<td>Publicação</td>
			<td style="text-align: center;">Data do Documento</td>
			<td>Docente</td>
			<td style="text-align: center;">Data da Publicação</td>
			<td style="text-align: center;">Data Final</td>
			<td style="text-align: center;">Remunerado</td>
			<td>Autoridade</td>
			<td>Tipo de Chefia</td>
			<td></td>
			<td></td>
		</thead>
		<c:forEach items="${chefia.all}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.publicacao}</td>
				<td align="center"><ufrn:format valor="${item.dataDocumento}" type="data" /></td>
				<td>${item.servidor.pessoa.nome}</td>
				<td align="center"><ufrn:format valor="${item.dataPublicacao}" type="data" /></td>
				<td align="center"><ufrn:format valor="${item.dataFinal}" type="data" /></td>
				<td align="center">${item.remunerado ?"Sim":"Não"}</td>
				<td>${item.autoridade.pessoa.nome}</td>
				<td>${item.tipoChefia.descricao}</td>
				<td width=20>
					<h:form><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/alterar.gif" alt="Alterar" value="Alterar"
						action="#{chefia.atualizar}" />
					</h:form>
				</td>
				
				<td width=25>
					<h:form><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/delete.gif" alt="Remover"
						action="#{chefia.inativar}" onclick="javascript:if(confirm('Deseja realmente REMOVER essa atividade ?')){ return true;} return false; void(0);" />
					</h:form>
				</td>
			</tr>
		</c:forEach>
	</table>
	<h:outputText value="#{chefia.dropList}" />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
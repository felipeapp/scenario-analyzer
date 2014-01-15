<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Afastamento</h2>
	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/> <h:commandLink action="#{afastamentoProdocente.preCadastrar}" value="Cadastrar Novo Tipo de Afastamento"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Tipo de Afastamento
		    <p><h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Tipo de Afastamento</p><br/>
		</div>
	</h:form>
	<h:outputText value="#{afastamentoProdocente.create}" />
	<table class="listagem" style="width:100%" >
		<caption class="listagem">Lista de Afastamentos</caption>
		<thead>
			<td>Descrição</td>
			<td></td>
			<td></td>
		</thead>
		<c:forEach items="${afastamentoProdocente.allAtivos}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
				<td width=20>
					<h:form><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/alterar.gif" value="Alterar"
						action="#{afastamentoProdocente.atualizar}" />
					</h:form>
				</td>
				<td width=25>
					<h:form><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/delete.gif" alt="Remover"
						action="#{afastamentoProdocente.remover}" onclick="javascript:if(confirm('Deseja realmente REMOVER essa atividade ?')){ return true;} return false; void(0);" />
					</h:form>
				</td>
			</tr>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
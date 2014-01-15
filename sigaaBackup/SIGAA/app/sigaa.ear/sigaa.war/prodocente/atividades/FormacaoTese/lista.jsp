<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2>Formação Tese</h2><br>
<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: <h:commandLink action="#{formacaoTese.preCadastrar}" value="Cadastrar Nova Formação de Tese"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Formação Tese
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Formação Tese<br/>
		</div>
	</h:form>
<h:outputText value="#{formacaoTese.create}"/>
<table class=listagem>
	<caption class="listagem"> Lista de formacaoTeses</caption>
	<thead>
	<td>Descrição</td>
	<td></td>
	<td></td>
	</thead>
<c:forEach items="${formacaoTese.allAtividades}" var="item"  varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
<td>${item.descricao}</td>
<h:form>
<td  width=20>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{formacaoTese.atualizar}"/>
</td>
</h:form>
<h:form>
<td  width=25>
<input type="hidden" value="${item.id}" name="id"/>
<h:commandButton image="/img/delete.gif" alt="Remover" action="#{formacaoTese.remover}" onclick="javascript:if(confirm('Deseja realmente REMOVER essa atividade ?')){ return true;} return false; void(0);" />
</td>
</h:form>
</tr>
</c:forEach>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

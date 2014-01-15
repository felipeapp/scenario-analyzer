<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages/>
	<h2><ufrn:subSistema /> > Listagem de P�los</h2>

	<h:outputText value="#{poloBean.create}"/>

	<div class="infoAltRem">
		<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar P�lo
	    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar P�lo
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover P�lo<br/>
	</div>

	<table class="listagem">
	<caption> P�los Cadastrados </caption>
	<thead>
		<tr>
		<th>P�lo</th>
		<th>Telefone</th>
		<th>Hor�rio</th>
		<th>C�digo</th>
		<th width="25"></th>
		<th width="25"></th>
		<th width="25"></th>
		</tr>
	</thead>

	<tbody>
	<c:forEach items="${poloBean.all}" var="polo" varStatus="loop">
		<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<td> ${polo.descricao}</td>
			<td> ${polo.telefone} </td>
			<td> ${polo.horarioFuncionamento} </td>
			<td> ${polo.codigo} </td>
			<td>
				<h:form>
				<input type="hidden" value="${polo.id}" name="id"/>
				<h:commandButton image="/img/view.gif" title="Visualizar P�lo" value="Ver detalhes" action="#{poloBean.detalhes}" style="border: 0;"/>
				</h:form>
			</td>
			<td>
				<h:form>
				<input type="hidden" value="${polo.id}" name="id"/>
				<h:commandButton image="/img/alterar.gif" title="Alterar P�lo" value="Alterar" action="#{poloBean.atualizar}" style="border: 0;"/>
				</h:form>
			</td>
			<td>
				<h:form>
				<input type="hidden" value="${polo.id}" name="id"/>
				<h:commandButton image="/img/delete.gif"  title="Remover P�lo" alt="Remover" action="#{poloBean.remover}" style="border: 0;"/>
				</h:form>
			</td>
		</tr>
	</c:forEach>
	</tbody>
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

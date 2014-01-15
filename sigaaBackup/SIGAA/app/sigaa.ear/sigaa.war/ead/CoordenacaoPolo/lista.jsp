<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Coordenadores de P�los</h2>
	<br>
	<h:outputText value="#{coordenacaoPolo.create}"/>
	<div class="infoAltRem">
	    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Coordenador
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Coordenador
	    <h:graphicImage value="/img/user.png" style="overflow: visible;"/>: Cadastrar Usu�rio
	</div>
	<table class="listagem">
		<caption class="listagem">Lista de Coordenadores</caption>
		<thead>
			<tr>
				<th>P�lo</th>
				<th>Pessoa</th>
				<th style="text-align: center;" width="8%">In�cio</th>
				<th style="text-align: center;" width="8%">Fim</th>
				<th>Usu�rio</th>
				<th></th>
				<th></th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${coordenacaoPolo.all}" var="c" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td> ${c.polo.descricao} </td>
					<td> ${c.pessoa.nome} </td>
					<td style="text-align: center;"> <fmt:formatDate value="${c.inicio}" pattern="dd/MM/yyyy"/> </td>
					<td style="text-align: center;"> <fmt:formatDate value="${c.fim}" pattern="dd/MM/yyyy"/> </td>
					<td> ${ c.usuario.login } </td>
					<td>
						<h:form>
							<input type="hidden" value="${c.id}" name="id"/>
							<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{coordenacaoPolo.atualizar}" style="border: 0;" title="Alterar Coordenador"/>
						</h:form>
					</td>
					<td  width="25">
						<h:form>
							<input type="hidden" value="${c.id}" name="id"/>
							<h:commandButton image="/img/delete.gif" title="Remover Coordenador" action="#{coordenacaoPolo.preRemover}" style="border: 0;"/>
						</h:form>
					</td>
					<td  width="25">
						<c:if test="${ c.usuario == null }">
							<h:form>
								<input type="hidden" value="${c.id}" name="id" /> 
								<h:commandButton image="/img/user.png" styleClass="noborder" title="Cadastrar Usu�rio" action="#{coordenacaoPolo.formUsuario}" />
							</h:form>
						</c:if>
					</td>
			</c:forEach>
		</tbody>
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
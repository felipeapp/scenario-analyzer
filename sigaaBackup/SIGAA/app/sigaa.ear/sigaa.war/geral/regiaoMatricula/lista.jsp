<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Listagem de Região de Campus para Matrícula</h2>

	<h:outputText value="#{regiaoMatriculaBean.create}"/>

	<div class="infoAltRem">
		<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar
	    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover<br/>
	</div>

	<table class="listagem">
		<caption> Regiões de Campus para Matrícula Cadastradas </caption>
		<thead>
			<tr>
			<th>Nome</th>
			<th>Nível de Ensino</th>
			<th width="25"></th>
			<th width="25"></th>
			<th width="25"></th>
			</tr>
		</thead>
	
		<tbody>
		<c:forEach items="${regiaoMatriculaBean.allByNivelEnsino}" var="item" varStatus="loop">
			<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				<td> ${item.nome} </td>
				<td> ${item.nivelDesc} </td>
				<td>
					<h:form>
					<input type="hidden" value="${item.id}" name="id"/>
					<h:commandButton image="/img/view.gif" title="Visualizar" value="Ver detalhes" action="#{regiaoMatriculaBean.detalhes}" style="border: 0;"/>
					</h:form>
				</td>
				<td>
					<h:form>
					<input type="hidden" value="${item.id}" name="id"/>
					<h:commandButton image="/img/alterar.gif" title="Alterar" value="Alterar" action="#{regiaoMatriculaBean.atualizar}" style="border: 0;"/>
					</h:form>
				</td>
				<td>
					<h:form>
					<input type="hidden" value="${item.id}" name="id"/>
					<h:commandButton image="/img/delete.gif"  title="Remover" alt="Remover" action="#{regiaoMatriculaBean.remover}" onclick="#{confirmDelete}" style="border: 0;"/>
					</h:form>
				</td>
			</tr>
		</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="5" align="center">
					<h:form>
					<h:commandButton value="Cancelar" action="#{regiaoMatriculaBean.cancelar}" onclick="#{confirm}" immediate="true" id="btnCancel"/>
					</h:form>
				</td>
			</tr>
		</tfoot>
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
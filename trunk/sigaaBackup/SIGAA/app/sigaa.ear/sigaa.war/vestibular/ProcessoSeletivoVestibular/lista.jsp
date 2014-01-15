<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Processo Seletivo</h2>

	<center><h:messages />
	<div class="infoAltRem"><h:graphicImage value="/img/alterar.gif"
		style="overflow: visible;" />: Alterar dados do PS <h:graphicImage
		value="/img/delete.gif" style="overflow: visible;" />: Remover PS <br />
	</div>
	</center>

	<h:form>
		<table class=listagem>
			<caption class="listagem">Lista de Processos Seletivos</caption>
			<thead>
				<tr>
					<th width="10%" style="text-align: center">Ano/Período Aplicação</th>
					<th>Nome</th>
					<th>Forma de Ingresso</th>
					<th>Interno/Externo</th>
					<th colspan="2"></th>
				</tr>
			</thead>
			<c:forEach items="#{processoSeletivoVestibular.all}" var="item"
				varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td style="text-align: center">${item.ano}.${item.periodo}</td>
					<td>${item.nome}</td>
					<td>${item.formaIngresso.descricao}</td>
					<td>
						<h:outputText value="Externo" rendered="#{item.processoExterno}" />
						<h:outputText value="Interno" rendered="#{!item.processoExterno}" />
					</td>
					<td width="2%"><h:commandLink title="Alterar"
						action="#{processoSeletivoVestibular.atualizar}"
						style="border: 0;">
						<f:param name="id" value="#{item.id}" />
						<h:graphicImage url="/img/alterar.gif" />
					</h:commandLink></td>
					<td width="2%"><h:commandLink title="Remover"
						action="#{processoSeletivoVestibular.prepareRemover}"
						style="border: 0;">
						<f:param name="id" value="#{item.id}" />
						<h:graphicImage url="/img/delete.gif" />
					</h:commandLink></td>
				</tr>
			</c:forEach>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
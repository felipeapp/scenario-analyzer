<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Lista de Configuração de GRU</h2>
	<h:form>
	<div class="infoAltRem">
		<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>: <h:commandLink action="#{grupoEmissaoGRUMBean.preCadastrar}" value="Cadastrar"/>
		<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
	</div>
	
	<table class="listagem">
		<caption class="listagem">Configuração de GRU Cadastradas</caption>
		<thead>
		<tr>
			<th>Agência</th>
			<th>Código Cedente</th>
			<th>Nº do Convênio</th>
			<th>Código da Gestão</th>
			<th>Unidade Gestora</th>
			<th>Status</th>
			<th width="2%"></th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="#{grupoEmissaoGRUMBean.all}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td valign="top">${item.agencia}</td>
				<td valign="top">${item.codigoCedente}</td>
				<td valign="top">${item.convenio}</td>
				<td valign="top">${item.codigoGestao}</td>
				<td valign="top">${item.codigoUnidadeGestora}</td>
				<td valign="top">
					<h:outputText value="Ativo" rendered="#{ item.ativo }" />
					<h:outputText value="Inativo" rendered="#{ !item.ativo }" />
				</td>
				<td valign="top">
					<h:commandLink title="Alterar" style="border: 0;" action="#{grupoEmissaoGRUMBean.atualizar}" >
						<f:param name="id" value="#{item.id}" />
						<h:graphicImage url="/img/alterar.gif" alt="Alterar" />
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="8" style="text-align: center">
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{grupoEmissaoGRUMBean.cancelar}" id="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
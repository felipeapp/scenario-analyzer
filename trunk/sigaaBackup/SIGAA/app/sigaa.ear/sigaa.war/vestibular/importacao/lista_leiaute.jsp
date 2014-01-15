<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > 
	<h:outputText value="Definição de Leiaute do Arquivo de Importação" />
	</h2>

	<h:form>
		<div class="infoAltRem">
			<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Leiaute
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Leiaute
			<h:graphicImage value="/img/biblioteca/duplicar.png" style="overflow: visible;" />: Duplicar Leiaute
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Inativar Leiaute
		</div>
		<table class="listagem">
		<caption>Lista de Leiaute de Arquivos para Importação (${fn:length(importaAprovadosOutrosVestibularesMBean.listaLeiautes)})</caption>
			<thead>
				<tr>
					<th>Descrição</th>
					<th>Forma de Ingresso</th>
					<th>Ativo</th>
					<th width="2%"></th>
					<th width="2%"></th>
					<th width="2%"></th>
					<th width="2%"></th>
				</tr>
			</thead>
			<c:forEach items="#{importaAprovadosOutrosVestibularesMBean.listaLeiautes}" var="item"
				varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.descricao}</td>
					<td>${item.formaIngresso.descricao}</td>
					<td><ufrn:format type="simNao" valor="${item.ativo}" /></td>
					<td>
						<h:commandLink title="Visualizar Leiaute" style="border: 0;" action="#{importaAprovadosOutrosVestibularesMBean.view}" >
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage url="/img/view.gif" alt="Visualizar Leiaute" />
						</h:commandLink>
					</td>
					<td>
						<h:commandLink title="Alterar Leiaute" style="border: 0;" action="#{importaAprovadosOutrosVestibularesMBean.atualizar}" >
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage url="/img/alterar.gif" alt="Alterar Leiaute" />
						</h:commandLink>
					</td>
					<td>
						<h:commandLink title="Duplicar Leiaute" style="border: 0;" action="#{importaAprovadosOutrosVestibularesMBean.view}" >
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage url="/img/biblioteca/duplicar.png" alt="Duplicar Leiaute" />
						</h:commandLink>
					</td>
					<td>
						<h:commandLink title="Inativar Leiaute" style="border: 0;" action="#{importaAprovadosOutrosVestibularesMBean.inativar}" 
							onclick="#{ confirmDelete }">
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage url="/img/delete.gif" alt="Inativar Leiaute" />
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
			<tfoot>
				<tr>
					<td colspan="7" style="text-align: center;">
						<h:commandButton immediate="true" value="Cancelar" action="#{importaAprovadosOutrosVestibularesMBean.cancelar}" onclick="#{ confirm }"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
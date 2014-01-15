<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:form>
	<h2 class="title"><ufrn:subSistema /> > Itens a serem adicionados ao grupo</h2>
	<h:messages showDetail="true"></h:messages>
		<table class="formulario" width="100%">
			<caption class="formulario">Relatório: ${relatorioAtividades.obj.titulo}</caption>
			<tr>
				<th width="15%">Grupo:</th>
				<td><b>${relatorioAtividades.nomeGrupo}</b></td>
			</tr>
			<tr>
				<th width="15%" nowrap="nowrap">Pontuação Máxima do Grupo:</th>
				<td><b>${relatorioAtividades.pontuacaoMaximaGrupo}</b></td>
			</tr>
		</table>
		<table class="formulario" width="100%">
			<thead style="background: #C8D5EC;	text-align: center; font-weight: normal;">
				<tr>
					<td colspan="2"><h:commandButton id="voltar1" action="#{relatorioAtividades.adicionarItem}"
					value="Adicionar Itens ao Grupo" /> <h:commandButton id="cancelar1" action="#{relatorioAtividades.cancelarAdicionaItem}"
					value="Cancelar"/> </td>
				</tr>
			</thead>
			<tr>
				<td colspan="2" align="center">
				<table class="subFormulario" width="100%">
					<caption class="subFormulario">Itens dispóniveis</caption>
					<tr><td>
						<t:dataTable align="center" width="100%" styleClass="listagem" rowClasses="linhaPar,linhaImpar"
							value="#{relatorioAtividades.itensGrupo}" var="item" rowIndexVar="g" >
							<t:column>
								<f:facet name="header">
									<h:outputText value="" />
								</f:facet>
								<h:selectBooleanCheckbox value="#{item.selecionado}" id="selecionado"/>
							</t:column>
							<t:column>
								<f:facet name="header">
									<h:outputText value="Item" />
								</f:facet>
								<h:outputText value="#{g+1}" />
							</t:column>
							<t:column >
								<f:facet name="header">
									<h:outputText value="Descrição" />
								</f:facet>
								<h:outputText value="#{item.descricao}"/>
							</t:column>
						</t:dataTable></td></tr>
				</table>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton id="voltar2" action="#{relatorioAtividades.adicionarItem}"
						value="Adicionar Itens ao Grupo" /> <h:commandButton id="cancelar2" action="#{relatorioAtividades.cancelarAdicionaItem}"
						value="Cancelar"/> </td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

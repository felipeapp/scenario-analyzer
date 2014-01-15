<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:messages showDetail="true" />
	<h:form>
		<t:dataTable width="100%" align="center" value="#{pontuacaoProducao.itens}" var="grupo">
			<t:column>
				<f:verbatim>
					<h2><b>
				</f:verbatim>
						<h:outputText value="#{grupo.id}"/>
				<f:verbatim>
						&nbsp; - &nbsp;
				</f:verbatim>
						<h:outputText value="#{grupo.descricao}" />
				<f:verbatim>
					</b></h2>
						<b>Pontos Máximos do Item: </b>
				</f:verbatim>
						<h:inputText size="6" value="#{grupo.maximoPontosGrupo}" />
				<f:verbatim> <br><br>
				</f:verbatim>
				<t:dataTable align="center" width="100%" styleClass="listagem" rowClasses="linhaPar,linhaImpar"
					value="#{grupo.itens}" var="item">
					<t:column>
						<f:facet name="header">
							<h:outputText value="Item" />
						</f:facet>
						<h:outputText value="#{item.topico }" />
					</t:column>
					<t:column >
						<f:facet name="header">
							<h:outputText value="Descrição" />
						</f:facet>
						<h:inputTextarea value="#{item.descricao}" rows="2" cols="40" />
					</t:column>
					<t:column>
						<f:facet name="header">
							<h:outputText value="Pontuação" />
						</f:facet>
						<h:inputText size="6" value="#{item.pontuacao}" />
					</t:column>
					<t:column>
						<f:facet name="header">
							<h:outputText value="Pontuação Máxima" />
						</f:facet>
						<h:inputText size="6" value="#{item.pontuacaoMaxima}" />
					</t:column>
					<t:column>
						<f:facet name="header">
							<h:outputText value="Pontuação Mínima" />
						</f:facet>
						<h:inputText size="6" value="#{item.pontuacaoMinima}" />
					</t:column>
				</t:dataTable>
				<f:verbatim>
					<br><br><br>
				</f:verbatim>
			</t:column>

		</t:dataTable>
		<h:commandButton value="Atualizar"
				action="#{pontuacaoProducao.atualizarItens}" />
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

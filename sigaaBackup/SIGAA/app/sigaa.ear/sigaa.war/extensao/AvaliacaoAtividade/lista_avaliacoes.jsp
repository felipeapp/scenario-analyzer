<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> > Avaliar Proposta de A��o de Extens�o</h2>


	<h:form id="formListaAtividadesAvaliacao">

		<div class="infoAltRem"><h:graphicImage value="/img/view.gif"
				style="overflow: visible;" />: Visualizar A��o de Extens�o <h:graphicImage
				value="/img/extensao/document_chart.png" style="overflow: visible;" />:
			Visualizar Avalia��o <h:graphicImage value="/img/seta.gif"
				style="overflow: visible;" /><h:outputText
				value=": Avaliar A��o de Extens�o" />
		</div>				
		<br />

		<table class="formulario" width="100%">
			<caption class="listagem">Selecione uma A��o para realizar
			a avalia��o</caption>

			<tbody>
				<tr>
					<td><t:dataTable id="dtavaliacoes"
						value="#{avaliacaoAtividade.avaliacoes}" var="aval"
						align="center" width="100%" styleClass="listagem"
						rowClasses="linhaPar, linhaImpar">

						<t:column style="text-align: center;" >
							<f:facet name="header">
								<f:verbatim><center>Ano</center></f:verbatim>
							</f:facet>
							<h:outputText value="#{aval.atividade.ano}" />
						</t:column>

						<t:column>
							<f:facet name="header">
								<f:verbatim>T�tulo</f:verbatim>
							</f:facet>
							<h:outputText value="#{aval.atividade.titulo}" />
						</t:column>

						<t:column styleClass="centerAlign">
							<f:facet name="header">
								<f:verbatim>Tipo</f:verbatim>
							</f:facet>
							<h:outputText
								value="#{aval.atividade.tipoAtividadeExtensao.descricao}" />
						</t:column>

						<t:column styleClass="centerAlign">
							<f:facet name="header">
								<f:verbatim>Situa��o</f:verbatim>
							</f:facet>
							<h:outputText value="#{aval.statusAvaliacao.descricao}" />
						</t:column>


						<t:column>
							<h:commandLink title="Visualizar A��o de Extens�o"
								action="#{ atividadeExtensao.view }" immediate="true">
								<f:param name="id" value="#{aval.atividade.id}" />
								<h:graphicImage url="/img/view.gif" />
							</h:commandLink>
						</t:column>


						<t:column>
							<h:commandLink title="Visualiza Avalia��o Completa"
								action="#{ avaliacaoAtividade.view }" immediate="true"
								rendered="#{not empty aval.dataAvaliacao}">
								<f:param name="idAvaliacao" value="#{aval.id}" />
								<h:graphicImage url="/img/extensao/document_chart.png" />
							</h:commandLink>
						</t:column>

						<t:column width="2%">
							<h:commandLink title="Avaliar A��o de Extens�o" rendered="#{ aval.permitirAvaliacaoMembroComite }" 
								action="#{ avaliacaoAtividade.iniciarAvaliacaoParecerista }" immediate="true">
							        <f:param name="idAvaliacao" value="#{aval.id}"/>
					        		<h:graphicImage url="/img/seta.gif" />
							</h:commandLink>
						</t:column>

					</t:dataTable> <c:if test="${empty avaliacaoAtividade.avaliacoes}">
						<center><font color="red">N�o h� avalia��es	pendentes</font></center>
					</c:if></td>
				</tr>

			</tbody>
		</table>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> > Avaliar Proposta de Ação de Extensão</h2>


	<h:form id="formListaAtividadesAvaliacao">

		<div class="infoAltRem"><h:graphicImage value="/img/view.gif"
				style="overflow: visible;" />: Visualizar Ação de Extensão <h:graphicImage
				value="/img/extensao/document_chart.png" style="overflow: visible;" />:
			Visualizar Avaliação <h:graphicImage value="/img/seta.gif"
				style="overflow: visible;" /><h:outputText
				value=": Avaliar Ação de Extensão" />
		</div>				
		<br />


		<table class="formulario" width="100%">
			<caption class="listagem">Selecione uma Ação para realizar
			a avaliação</caption>

			<tbody>
				<tr>
					<td><t:dataTable id="dtavaliacoes"
						value="#{avaliacaoAtividade.avaliacoesMembroComite}" var="aval"
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
								<f:verbatim>Título</f:verbatim>
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
								<f:verbatim>Situação</f:verbatim>
							</f:facet>
							<h:outputText value="#{aval.statusAvaliacao.descricao}" />
						</t:column>


						<t:column>
							<h:commandLink title="Visualizar Ação de Extensão"
								action="#{ atividadeExtensao.view }" immediate="true">
								<f:param name="id" value="#{aval.atividade.id}" />
								<h:graphicImage url="/img/view.gif" />
							</h:commandLink>
						</t:column>


						<t:column>
							<h:commandLink title="Visualiza Avaliação Completa"
								action="#{ avaliacaoAtividade.view }" immediate="true"
								rendered="#{not empty aval.dataAvaliacao}">
								<f:param name="idAvaliacao" value="#{aval.id}" />
								<h:graphicImage url="/img/extensao/document_chart.png" />
							</h:commandLink>
						</t:column>

						<t:column>
							<h:commandLink title="Avaliar Ação de Extensão"	action="#{ avaliacaoAtividade.iniciarAvaliacaoComite }"	
								immediate="true" rendered="#{ aval.permitirAvaliacaoMembroComite }"> <%-- aguardando avaliacao --%>
								<f:param name="idAvaliacao" value="#{aval.id}" />
								<h:graphicImage url="/img/seta.gif" />
							</h:commandLink>
						</t:column>
					</t:dataTable> <c:if test="${empty avaliacaoAtividade.avaliacoesMembroComite}">
						<center><font color="red">Não há avaliações	pendentes</font></center>
					</c:if></td>
				</tr>

			</tbody>
		</table>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
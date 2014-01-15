<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> > Visualizar Avalia��es de A��o de Extens�o</h2>


	<h:form id="formListaAtividadesAvaliacao">

		<div class="infoAltRem">
		 <h:graphicImage value="/img/extensao/document_chart.png" style="overflow: visible;" />: Visualizar Avalia��o
		</div>
		
		<br />


		<table class="formulario" width="100%">
			<caption class="listagem">Lista de Avalia��es da a��o de extens�o selecionada</caption>

			<tbody>
			
				<tr>
				 <td style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">${atividadeExtensao.obj.anoTitulo}</td>
				</tr> 
			
			
			
				<tr>
					<td><t:dataTable id="dtavaliacoes"
						value="#{atividadeExtensao.obj.avaliacoes}" var="avaliac"
						align="center" width="100%" styleClass="listagem"
						rowClasses="linhaPar, linhaImpar">
						<t:column>
							<f:facet name="header">
								<f:verbatim>Tipo Avalia��o</f:verbatim>
							</f:facet>
							<h:outputText value="#{avaliac.tipoAvaliacao.descricao}" />
						</t:column>

						<t:column styleClass="centerAlign">
							<f:facet name="header">
								<f:verbatim>Situa��o</f:verbatim>
							</f:facet>
							<h:outputText value="#{avaliac.statusAvaliacao.descricao}" />
						</t:column>

						<t:column styleClass="centerAlign">
							<f:facet name="header">
								<f:verbatim>Avaliado Em</f:verbatim>
							</f:facet>
							<h:outputText value="#{avaliac.dataAvaliacao}" >
								<f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss"/>
							</h:outputText>
						</t:column>

						<t:column>
							<h:commandLink title="Visualizar Avalia��o Completa"
								action="#{ avaliacaoAtividade.view }" immediate="true"
								rendered="#{not empty avaliac.dataAvaliacao}">
								<f:param name="idAvaliacao" value="#{avaliac.id}" />
								<h:graphicImage url="/img/extensao/document_chart.png" />
							</h:commandLink>
						</t:column>


					</t:dataTable> <c:if test="${empty atividadeExtensao.obj.avaliacoes}">
						<center><font color="red">N�o h� avalia��es	para esta a��o de extens�o</font></center>
					</c:if></td>
				</tr>
			</tbody>
			<tfoot>
			     <tr>
			         <td><input type="button" onclick="javascript:history.back();" value="<< Voltar" /></td>
			     </tr>
			</tfoot>
		</table>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> &gt; Distribui��o de Resumos para Avaliadores de Trabalhos do CIC</h2>
	<h:outputText value="#{avaliacaoResumoBean.create}" />
	<h:form id="form">
		<table class="formulario" width="100%">
			<caption>Filtros para Distribui��o</caption>
			<tbody>
				
				<tr>
					<th>�rea de Conhecimento: </th>
					<td>
						<a4j:region>
							<h:selectOneMenu id="areas" value="#{avaliacaoResumoBean.area.id}" immediate="true"
								valueChangeListener="#{avaliacaoResumoBean.carregaAvaliadores}"	>
									<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
									<f:selectItems value="#{area.allGrandeAreas}"/>
									<a4j:support reRender="avaliadores" event="onchange" />
							</h:selectOneMenu>&nbsp;
							<a4j:status>
								<f:facet name="start">
									<h:graphicImage value="/img/ajax-loader.gif"/>
								</f:facet>
							</a4j:status>
						</a4j:region>
					</td>
				</tr>
			<a4j:region>
				<tr>
					<th>Avaliador: </th>
					<td>
							<h:selectOneMenu id="avaliadores" value="#{avaliacaoResumoBean.avaliador.id}" immediate="true"
								valueChangeListener="#{avaliacaoResumoBean.carregaAvaliacoes}">
									<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
									<f:selectItems value="#{avaliacaoResumoBean.avaliadoresCombo}"/>
									<a4j:support reRender="avaliacoes" event="onchange" />
							</h:selectOneMenu>&nbsp;
							<a4j:status>
								<f:facet name="start">
									<h:graphicImage value="/img/ajax-loader.gif"/>
								</f:facet>
							</a4j:status>
					</td>
				</tr>
				<tr>
					<th>Pendentes:</th>
					<td> <h:selectBooleanCheckbox value="#{avaliacaoResumoBean.pendentes}" id="pendentes" /> </td>
				</tr>
			</a4j:region>
				<tr>
					<td colspan="2">
						<t:dataTable value="#{avaliacaoResumoBean.avaliacoes}" var="av" id="avaliacoes" rowIndexVar="index" width="100%" rowClasses="linhaPar, linhaImpar" styleClass="listagem">
							<t:column width="5%" styleClass="centerAlign">
								<f:facet name="header"><f:verbatim></f:verbatim></f:facet>
								<h:selectBooleanCheckbox value="#{av.selecionado}" />
							</t:column>
						
							<t:column>
								<f:facet name="header">
									<f:verbatim>C�digo</f:verbatim>
								</f:facet>
								<h:outputText value="#{av.resumo.codigo}" />
							</t:column>
							
							<t:column>
								<f:facet name="header">
									<f:verbatim>Autor</f:verbatim>
								</f:facet>
								<h:outputText value="#{av.resumo.autor.nome}" />
							</t:column>
							
							<t:column>
								<f:facet name="header">
									<f:verbatim>Orientador</f:verbatim>
								</f:facet>
								<h:outputText value="#{av.resumo.orientador.nome}" />
							</t:column>
							
							<t:column>
								<f:facet name="header">
									<f:verbatim>Status</f:verbatim>
								</f:facet>
								<h:outputText value="#{av.resumo.statusString}" />
							</t:column>
							
						</t:dataTable>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton value="Gravar Distribui��o" action="#{avaliacaoResumoBean.gravar}" /> <h:commandButton
						value="Cancelar" action="#{avaliacaoResumoBean.cancelar}" onclick="#{confirm}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
		
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
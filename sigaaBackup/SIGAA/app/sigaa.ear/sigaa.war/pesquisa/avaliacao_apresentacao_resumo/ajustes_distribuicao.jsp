<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> &gt; Distribuição de Avaliações de Trabalhos do CIC</h2>
	<h:outputText value="#{avaliacaoApresentacaoResumoBean.create}" />
	<h:form id="form">
		<table class="formulario" width="100%">
			<caption>Filtros para Distribuição</caption>
			<tbody>
				
				<tr>
					<th>Centro/Unidade: </th>
					<td>
						<a4j:region>
							<h:selectOneMenu id="areas" value="#{avaliacaoApresentacaoResumoBean.unidade.id}" immediate="true"
								valueChangeListener="#{avaliacaoApresentacaoResumoBean.carregaAvaliadores}"	>
									<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
									<f:selectItems value="#{siglaUnidadePesquisaMBean.unidadesCombo}"/>
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
							<h:selectOneMenu id="avaliadores" value="#{avaliacaoApresentacaoResumoBean.avaliador.id}" immediate="true"
								valueChangeListener="#{avaliacaoApresentacaoResumoBean.carregaAvaliacoes}">
									<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
									<f:selectItems value="#{avaliacaoApresentacaoResumoBean.avaliadoresCombo}"/>
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
					<td> <h:selectBooleanCheckbox value="#{avaliacaoApresentacaoResumoBean.pendentes}" id="pendentes" /> </td>
				</tr>
			</a4j:region>
				<tr>
					<td colspan="2">
						<t:dataTable value="#{avaliacaoApresentacaoResumoBean.avaliacoes}" var="av" id="avaliacoes" rowIndexVar="index" width="100%" rowClasses="linhaPar, linhaImpar" styleClass="listagem">
							<t:column width="5%" styleClass="centerAlign">
								<f:facet name="header"><f:verbatim></f:verbatim></f:facet>
								<h:selectBooleanCheckbox value="#{av.selecionado}" />
							</t:column>
						
							<t:column>
								<f:facet name="header">
									<f:verbatim>Centro/Unidade</f:verbatim>
								</f:facet>
								<h:outputText value="#{av.resumo.autor.discente.curso.unidade.sigla}" />
							</t:column>
						
							<t:column>
								<f:facet name="header">
									<f:verbatim>Código</f:verbatim>
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
					<td colspan="2"><h:commandButton value="Gravar Distribuição" action="#{avaliacaoApresentacaoResumoBean.gravar}" /> <h:commandButton
						value="Cancelar" action="#{avaliacaoApresentacaoResumoBean.cancelar}" onclick="#{confirm}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
		
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

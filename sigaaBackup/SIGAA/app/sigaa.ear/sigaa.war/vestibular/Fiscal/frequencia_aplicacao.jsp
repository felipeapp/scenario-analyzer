<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
<!--
.left {
	text-align: left;
	border-spacing: 3px;
}

.center {
	text-align: center;
	border-spacing: 3px;
}
.tfoot {
	background-color: #C8D5EC; 
	text-align: center;
}
-->
</style>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> > Registro da frequência do Fiscal em Aplicação de Prova</h2>
	<div class="descricaoOperacao">
			Selecione um processo seletivo e um
			local de aplicação de prova para ter a lista de fiscais. <br>
			Em seguida, registre a frequência dos fiscais e o conceito.<br/>
			Para incrementar em um dia a frequência de todos os fiscais que não foram ausentes à reunião ou à aplicação de provas, utilize o botão "<b>Incrementar a Frequência</b>".
	</div>
	<h:form id="form">
		<table class="formulario" width="75%">
			<caption>Selecione o Processo Seletivo e o Local de Aplicação de Prova</caption>
			<tbody>
				<tr>
					<th width="25%">Processo Seletivo:</th>
					<td width="75%">
						<a4j:region>
							<h:selectOneMenu id="processoSeletivo"
								value="#{assiduidadeFiscal.idProcessoSeletivo}"
								valueChangeListener="#{assiduidadeFiscal.carregaLocalAplicacao}">
								<f:selectItem itemValue="0"	itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
								<a4j:support event="onchange" reRender="form" />
							</h:selectOneMenu>
							<a4j:status>
								<f:facet name="start">
									<h:graphicImage value="/img/indicator.gif" />
								</f:facet>
							</a4j:status>
						</a4j:region>
					</td>
				</tr>
				<tr>
					<th>Local de Aplicação:</th>
					<td width="70%">
						<a4j:region>
							<h:selectOneMenu id="localAplicacao" immediate="true"
								value="#{assiduidadeFiscal.idLocalAplicacao}"
								valueChangeListener="#{assiduidadeFiscal.carregaListaFiscais}">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{assiduidadeFiscal.locaisAplicacao}" />
								<a4j:support event="onchange" reRender="form" />
							</h:selectOneMenu>
							<a4j:status>
								<f:facet name="start">
									<h:graphicImage value="/img/indicator.gif" />
								</f:facet>
							</a4j:status>
						</a4j:region>
					</td>
				</tr>
			</tbody>
		</table>
		<br>
		<div align="center" id="resultadoBusca">
			Total de fiscais titulares: ${fn:length(assiduidadeFiscal.obj)}
			<c:if test="${assiduidadeFiscal.fiscaisReuniaoDataModel != null && not empty assiduidadeFiscal.obj}">
				<a4j:region>
				<h:dataTable value="#{assiduidadeFiscal.fiscaisReuniaoDataModel}" id="listaFiscais"
					columnClasses="center, left,  center" var="item"
					rowClasses="linhaPar,linhaImpar"
					footerClass="tfoot" width="75%">
					<h:column>
						<f:facet name="header">
							<h:outputText value="Presente" />
						</f:facet>
						<h:selectBooleanCheckbox value="#{item.presenteAplicacao}" rendered="#{item.presenteReuniao}">
							<a4j:support event="onclick"  reRender="listaFiscais"/>
						</h:selectBooleanCheckbox>
						<h:outputText rendered="#{not item.presenteReuniao}" value="Ausente à Reunião"/>
					</h:column>
					<h:column>
						<f:facet name="header">
							<h:outputText value="Nome" />
						</f:facet>
						<h:outputText value="#{item.pessoa.nome}"></h:outputText>
					</h:column>
					<h:column>
						<f:facet name="header">
							<h:outputText value="Dias Trabalhados" />
						</f:facet>
							<h:inputText size="5" value="#{item.frequencia}" rendered="#{item.presenteReuniao && item.presenteAplicacao}"
								onkeyup="return formatarInteiro(this)" converter="#{ intConverter }" maxlength="3"
								required="true" styleClass="center"/>
							<h:outputText value="#{item.frequencia}" rendered="#{not item.presenteReuniao || not item.presenteAplicacao}"/>
					</h:column>
					<f:facet name="footer">
						<h:panelGrid columns="3" style="display:inline;">
							<h:commandButton value="Atualizar Frequencias" action="#{assiduidadeFiscal.cadastrar}" />
							<h:commandButton value="Incrementar Frequência dos Fiscais Presentes" action="#{assiduidadeFiscal.incrementarFrequencia}" />
							<h:commandButton value="Cancelar" action="#{assiduidadeFiscal.cancelar}" onclick="#{confirm}"/>
						</h:panelGrid>
					</f:facet>
				</h:dataTable>
				</a4j:region>
			</c:if>
		</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
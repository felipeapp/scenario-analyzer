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
	<h2><ufrn:subSistema /> > Convocar Fiscais Reservas</h2>
	<div class="descricaoOperacao">
			Selecione um processo seletivo e um
			local de aplicação de prova para ter a lista de fiscais. <br>
			Desmarcando a opção <b>RESERVA</b> o fiscal passa a ser titular.<br>
	</div>
	<h:form id="formBusca">
		<table class="formulario" width="80%">
			<caption>Selecione o Processo Seletivo e o Local de Aplicação de Prova</caption>
			<tbody>
				<tr>
					<th width="30%">Processo Seletivo:</th>
					<td width="70%">
						<h:selectOneMenu id="processoSeletivo" immediate="true"
							value="#{assiduidadeFiscal.idProcessoSeletivo}"
							onchange="submit()"
							valueChangeListener="#{assiduidadeFiscal.carregaLocalAplicacao}">
							<f:selectItem itemValue="0"	itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>Local de Aplicação:</th>
					<td width="70%">
						<h:selectOneMenu id="localAplicacao" immediate="true"
							value="#{assiduidadeFiscal.idLocalAplicacao}"
							onchange="submit()"
							valueChangeListener="#{assiduidadeFiscal.carregaListaFiscais}">
							<f:selectItem itemValue="0"	itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{assiduidadeFiscal.locaisAplicacao}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
		</table>
		<br>
		<div align="center">
			Total de fiscais: ${fn:length(assiduidadeFiscal.obj)}
			<c:if
				test="${assiduidadeFiscal.fiscaisReuniaoDataModel != null && not empty assiduidadeFiscal.obj}">
				<h:dataTable value="#{assiduidadeFiscal.fiscaisReuniaoDataModel}"
					columnClasses="left, left, center, center, center, center" var="item"
					rowClasses="linhaPar,linhaImpar" width="80%"
					style="width: 100%;" footerClass="tfoot">
					<h:column>
						<f:facet name="header">
							<h:outputText value="Nome" />
						</f:facet>
						<h:outputText value="#{item.pessoa.nome}"></h:outputText>
					</h:column>
					<h:column>
						<f:facet name="header">
							<h:outputText value="Bairro" />
						</f:facet>
						<h:outputText value="#{item.pessoa.enderecoContato.bairro}"></h:outputText>
					</h:column>
					<h:column>
						<f:facet name="header">
							<h:outputText value="Telefone" />
						</f:facet>
						<h:outputText value="#{item.pessoa.telefone}"></h:outputText>
					</h:column>
					<h:column>
						<f:facet name="header">
							<h:outputText value="Celular" />
						</f:facet>
						<h:outputText value="#{item.pessoa.celular}"></h:outputText>
					</h:column>
					<h:column>
						<f:facet name="header">
							<h:outputText value="Experiência" />
						</f:facet>
						<h:outputText rendered="#{item.novato}" value="Novato"/>
						<h:outputText rendered="#{not item.novato}" value="Experiente"/>
					</h:column>
					<h:column>
						<f:facet name="header">
							<h:outputText value="Reserva" />
						</f:facet>
						<h:selectBooleanCheckbox value="#{item.reserva}"></h:selectBooleanCheckbox>
					</h:column>
					<f:facet name="footer">
						<div style="text-align: center;">
						<h:panelGrid columns="2" style="display:inline;">
							<h:commandButton value="Atualizar" action="#{assiduidadeFiscal.cadastrar}" /> 
							<h:commandButton value="Cancelar" action="#{assiduidadeFiscal.cancelar}" onclick="#{confirm}" immediate="true" />
						</h:panelGrid>
						</div>
					</f:facet>
				</h:dataTable>
			</c:if>
		</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
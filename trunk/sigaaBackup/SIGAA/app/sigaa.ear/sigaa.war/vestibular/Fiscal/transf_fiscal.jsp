<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
<!--
.left {
	text-align: left;
	width: 75%;
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
	<h2><ufrn:subSistema /> > Transferir Fiscais entre Locais de Aplicação de Prova</h2>
	<div class="descricaoOperacao">Selecione um processo seletivo e
		um local de aplicação de prova de origem para ter a lista de fiscais.
		Selecione o local de aplicação de prova de destino e marque quais
		fiscais deverão ser transferidos.<br>
	</div>
	<h:form id="formBusca">
		<table class="formulario" width="90%">
			<caption>Escolha o Processo Seletivo, a Origem e o Destino</caption>
			<tbody>
				<tr>
					<th width="30%">Processo Seletivo:</th>
					<td width="70%">
						<h:selectOneMenu id="processoSeletivo" immediate="true"
							value="#{transferenciaFiscal.idProcessoSeletivo}"
							onchange="submit()"
							valueChangeListener="#{transferenciaFiscal.localProvaListener}">
							<f:selectItem itemValue="0"	itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>Local de Origem:</th>
					<td width="70%">
						<h:selectOneMenu id="localAplicacaoOrigem" immediate="true"
							value="#{transferenciaFiscal.idLocalAplicacaoOrigem}"
							onchange="submit()"
							valueChangeListener="#{transferenciaFiscal.localOrigemListener}">
							<f:selectItem itemValue="0"	itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{transferenciaFiscal.locaisAplicacaoOrigem}" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>Local de Destino:</th>
					<td width="70%">
						<h:selectOneMenu id="localAplicacaoDestino" immediate="true"
							value="#{transferenciaFiscal.idLocalAplicacaoDestino}" >
							<f:selectItem itemValue="0"	itemLabel="--> SELECIONE <--" />
							<f:selectItems value="#{transferenciaFiscal.locaisAplicacaoDestino}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
		</table>
		<br>
		<div align="center">
			Total de fiscais: ${fn:length(transferenciaFiscal.obj)}
			<c:if
				test="${transferenciaFiscal.fiscaisDataModel != null && not empty transferenciaFiscal.obj}">
				<h:dataTable value="#{transferenciaFiscal.fiscaisDataModel}"
					columnClasses="center, left, center, center, center" footerClass="tfoot"
					rowClasses="linhaPar,linhaImpar" var="item" style="width: 90%;">
					<h:column>
						<f:facet name="header">
							<h:outputText value="Transferir" />
						</f:facet>
						<h:selectBooleanCheckbox value="#{item.selecionado}"/>
					</h:column>
					<h:column>
						<f:facet name="header">
							<h:outputText value="Nome" />
						</f:facet>
						<h:outputText value="#{item.objeto.pessoa.nome}"></h:outputText>
					</h:column>
					<h:column>
						<f:facet name="header">
							<h:outputText value="Titularidade" />
						</f:facet>
						<h:outputText rendered="#{item.objeto.reserva}" value="Reserva"/>
						<h:outputText rendered="#{not item.objeto.reserva}" value="Titular"/>
					</h:column>
					<h:column>
						<f:facet name="header">
							<h:outputText value="Experiência" />
						</f:facet>
						<h:outputText rendered="#{item.objeto.novato}" value="Novato"/>
						<h:outputText rendered="#{not item.objeto.novato}" value="Experiente"/>
					</h:column>
					<h:column>
						<f:facet name="header">
							<h:outputText value="Disponibilidade para viajar" />
						</f:facet>
						<h:outputText rendered="#{item.objeto.inscricaoFiscal.disponibilidadeOutrasCidades}" value="Sim"/>
						<h:outputText rendered="#{not item.objeto.inscricaoFiscal.disponibilidadeOutrasCidades}" value="Não"/>
					</h:column>
					<f:facet name="footer">
						<h:panelGrid columns="2" style="display:inline">
							<h:commandButton value="Transferir" action="#{transferenciaFiscal.cadastrar}" /> 
							<h:commandButton value="Cancelar" action="#{transferenciaFiscal.cancelar}" onclick="#{confirm}" immediate="true" />
						</h:panelGrid>
					</f:facet>
				</h:dataTable>
			</c:if>
		</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
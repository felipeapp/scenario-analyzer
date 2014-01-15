<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
<!--
.left {
	text-align: left;
	border-spacing: 3px;
	padding-left: 5px;
}

.right {
	text-align: right;
	border-spacing: 3px;
	padding-right: 5px;
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
	<h2><ufrn:subSistema /> > Marcação de Presença de Fiscal em Reunião</h2>

	<h:form id="formBusca">
		<div class="descricaoOperacao">
			Selecione um processo seletivo e um
			local de aplicação de prova para ter a lista de fiscais. <br>
			Caso o fiscal não tenha se apresentado à reunião de fiscais, desmarque
			o item correspondente. <br>
			<b>OBS:</b> a lista é ordenada por titularidade e depois por nome.
		</div>
		<table class="formulario" width="80%">
			<caption>Selecione o Processo Seletivo e o Local de Aplicação de Prova</caption>
			<tbody>
				<tr>
					<th width="30%">Processo Seletivo:</th>
					<td width="70%"><h:selectOneMenu id="processoSeletivo"
						value="#{assiduidadeFiscal.idProcessoSeletivo}"
						onchange="submit()"
						valueChangeListener="#{assiduidadeFiscal.carregaLocalAplicacao}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<th>Local de Aplicação:</th>
					<td width="70%"><h:selectOneMenu id="localAplicacao"
						value="#{assiduidadeFiscal.idLocalAplicacao}"
						onchange="submit()"
						valueChangeListener="#{assiduidadeFiscal.carregaListaFiscais}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{assiduidadeFiscal.locaisAplicacao}" />
					</h:selectOneMenu></td>
				</tr>
			</tbody>
		</table>
		<br>
		<div align="center">
			Total de fiscais: ${fn:length(assiduidadeFiscal.obj)}
			<c:if
				test="${assiduidadeFiscal.fiscaisReuniaoDataModel != null && not empty assiduidadeFiscal.obj}">
				<h:dataTable value="#{assiduidadeFiscal.fiscaisReuniaoDataModel}"
					columnClasses="center,right,left,center" width="80%"
					rowClasses="linhaPar,linhaImpar" footerClass="tfoot" var="item">
					<h:column>
						<f:facet name="header">
							<h:outputText value="Titularidade" />
						</f:facet>
						<h:outputText rendered="#{item.reserva}" value="Reserva"/>
						<h:outputText rendered="#{not item.reserva}" value="Titular"/>
					</h:column>
					<h:column headerClass="right">
						<f:facet name="header">
							<h:outputText value="Matricula" />
						</f:facet>
						<h:outputText value="#{item.discente.matricula}" />
						<h:outputText value="#{item.servidor.siape}" />
					</h:column>
					<h:column headerClass="left">
						<f:facet name="header">
							<h:outputText value="Nome" />
						</f:facet>
						<h:outputText value="#{item.pessoa.nome}"></h:outputText>
					</h:column>
					<h:column>
						<f:facet name="header">
							<h:outputText value="Presente" />
						</f:facet>
						<h:selectBooleanCheckbox value="#{item.presenteReuniao}"></h:selectBooleanCheckbox>
					</h:column>
					<f:facet name="footer">
						<h:panelGrid columns="2" style="display:inline">
							<h:commandButton value="Atualizar"
								action="#{assiduidadeFiscal.cadastrar}">
							</h:commandButton>
							<h:commandButton value="Cancelar"
								action="#{assiduidadeFiscal.cancelar}" onclick="#{confirm}"
								 />
						</h:panelGrid>
					</f:facet>
				</h:dataTable>
			</c:if>
		</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
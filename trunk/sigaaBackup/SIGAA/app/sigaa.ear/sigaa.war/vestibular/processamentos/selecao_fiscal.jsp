<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
<!--
.left {
	text-align: left;
	border-spacing: 3px;
}

.center {
	width: 15%;
	text-align: center;
	border-spacing: 3px;
}

.right {
	width: 15%;
	text-align: right;
	border-spacing: 3px;
}
-->
</style>

<script type="text/javascript">
function aguarde() {
	document.getElementById("wait").style.display="inline";
	document.getElementById("botoes").style.display="none";
}
</script>

<f:view>
	<h:form id="form">
		<h2><ufrn:subSistema /> > Processamento de Sele��o de
		Fiscais</h2>
		<div class="descricaoOperacao">
		<p>Selecione um Processo Seletivo e
		aguarde o formul�rio ser atualizado. Em seguida, informe a quantidade
		de fiscais a serem selecionados para cada munic�pio. O percentual
		reserva � aplicado em rela��o � quantidade de fiscais titulares a
		selecionar.</p>
		<p>Pelo menos um discente de cada curso ser� selecionado como fiscal, o
		que pode dar uma pequena diferen�a no n�mero total de fiscais.</p>
		<p><b>OBS:</b> ser� utilizado o �ndice acad�mico <b>${processamentoSelecaoFiscal.indiceSelecaoGraduacao.nome} (${processamentoSelecaoFiscal.indiceSelecaoGraduacao.sigla})</b> na sele��o de fiscais de gradua��o.</p>
		</div>
		<table class="formulario" width="75%">
			<caption>Dados do Processamento <h:outputText value="(Processado)" 
					rendered="#{processamentoSelecaoFiscal.obj.processoSeletivoVestibular.selecaoFiscalProcessada}"/>
			</caption>
			<tbody>
				<tr>
					<th class="required">Processo Seletivo:</th>
					<td><h:selectOneMenu
						valueChangeListener="#{processamentoSelecaoFiscal.processoSeletivoListener}"
						value="#{processamentoSelecaoFiscal.idProcessoSeletivo}"
						onchange="submit()">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<td colspan="2">
					<h:dataTable var="cidade"
						value="#{processamentoSelecaoFiscal.fiscaisPorMunicipio}"
						styleClass="listagem" rowClasses="linhaPar,linhaImpar"
						columnClasses="left,right,right,right"
						rendered="#{not empty processamentoSelecaoFiscal.fiscaisPorMunicipio}">
						<f:facet name="caption">
							<f:verbatim>Quantidade por Munic�pio
							<h:outputText rendered="#{processamentoSelecaoFiscal.selecaoProcessada}" value="(processado)"/></f:verbatim>
						</f:facet>
						<h:column headerClass="left">
							<f:facet name="header">
								<f:verbatim>Munic�pio</f:verbatim>
							</f:facet>
							<h:outputText value="#{cidade.nome}" />
						</h:column>
						<h:column headerClass="right">
							<f:facet name="header">
								<f:verbatim><div style="text-align: right">N�m. de Fiscais Titulares</div></f:verbatim>
							</f:facet>
							<h:inputText value="#{cidade.numFiscais}" size="4" maxlength="4"
								onkeyup="return formatarInteiro(this)"
								converter="#{intConverter}"
								rendered="#{not processamentoSelecaoFiscal.selecaoProcessada}">
							</h:inputText>
							<h:outputText styleClass="required" value=" " rendered="#{not processamentoSelecaoFiscal.selecaoProcessada}" />
							<h:outputText value="#{cidade.numFiscais}" rendered="#{processamentoSelecaoFiscal.selecaoProcessada}"/>
						</h:column>
						<h:column headerClass="right">
							<f:facet name="header">
								<f:verbatim><div style="text-align: right">Reserva (%)</div></f:verbatim>
							</f:facet>
							<h:inputText value="#{cidade.percentualReserva}" size="3"
								maxlength="3" onkeyup="return formatarInteiro(this)"
								converter="#{intConverter}" 
								rendered="#{not processamentoSelecaoFiscal.selecaoProcessada}" >
							</h:inputText>
							<h:outputText styleClass="required" value=" " rendered="#{not processamentoSelecaoFiscal.selecaoProcessada}" />
							<h:outputText value="#{cidade.percentualReserva}" rendered="#{processamentoSelecaoFiscal.selecaoProcessada}"/>
						</h:column>
						<h:column headerClass="right">
							<f:facet name="header">
								<f:verbatim><div style="text-align: right">N�m. de<br />Inscritos</div></f:verbatim>
							</f:facet>
							<h:outputText value="#{cidade.numInscritos}" />
						</h:column>
					</h:dataTable></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<div id="wait" style="display: none;"><html:img	page="/img/indicator.gif" align="middle" />Processando...</div>
						<div id="botoes" style="border: 0;">
							<h:commandButton value="Simular o Processamento" id="simular" onclick="aguarde()"  
							action="#{ processamentoSelecaoFiscal.simularProcessamento }" /> 
							<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ processamentoSelecaoFiscal.cancelar }" />
						</div>
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<center><html:img page="/img/required.gif"
			style="vertical-align: top;" /> <span class="fontePequena">
		Campos de preenchimento obrigat�rio. </span> <br />
		<br />
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
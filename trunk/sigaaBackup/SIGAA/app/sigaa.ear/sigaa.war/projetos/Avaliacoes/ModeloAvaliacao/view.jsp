<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> &gt; Visualiza��o do Modelo de Avalia��o</h2>
	<br />
	<h:form>
		<table class="formulario" width="100%">
			<caption>Visualizar Modelo de Question�rio</caption>
			<tr class="linhaImpar">
				<td width="50%"><b><p align="right">T�tulo:</p></b></td>
				<td>
					<h:outputText value="#{modeloAvaliacao.obj.descricao}" />
				</td>
			</tr>
			<tr class="linhaPar">
				<td><b><p align="right">M�dia para Aprova��o:</p></b></td>
				<td>
					<h:outputText value="#{modeloAvaliacao.obj.mediaMinimaAprovacao}" />
				</td>
			</tr>
			<tr class="linhaImpar">
				<td><b><p align="right">M�xima Discrep�ncia:</p></b></td>
				<td>
					<h:outputText value="#{modeloAvaliacao.obj.maximaDiscrepanciaAvaliacoes}" />
				</td>
			</tr>
			<tr class="linhaPar">
				<td><b><p align="right">Tipo:</p></b></td>
				<td>
					<h:outputText value="#{modeloAvaliacao.obj.tipoAvaliacao.descricao}" />
				</td>
			</tr>
			<tr class="linhaImpar">
				<td><b><p align="right">Edital:</p></b></td>
				<td>
					<h:outputText value="#{modeloAvaliacao.obj.edital.descricao}" />
				</td>
			</tr>
			<tr class="linhaPar">
				<td><b><p align="right">Question�rio:</p></b></td>
				<td>
					<h:outputText value="#{modeloAvaliacao.obj.questionario.descricao}" />
				</td>
			</tr>
		</table>
		<br />
		<table class="formulario" width="100%">
			<caption>Question�rio de Avalia��o ${modeloAvaliacao.obj.questionario.descricao}</caption>
			<tr>
				<td>
					<h:dataTable id="dtView" value="#{modeloAvaliacao.obj.questionario.itensAvaliacao}" 
						rowClasses="linhaPar, linhaImpar" var="itemQues" width="100%">
							<h:column>
								<f:facet name="header">
									<h:outputText value="Pergunta" />
								</f:facet>
								<h:outputText value="#{itemQues.pergunta.descricao}" />
							</h:column>
							<h:column>
								<f:facet name="header">
									<h:outputText value="Grupo" />
								</f:facet>
								<h:outputText value="#{itemQues.grupo.descricao}" />
							</h:column>
							<h:column>
							
								<f:facet name="header">
									<h:outputText value="Peso" />
								</f:facet>
								<h:outputText value="#{itemQues.peso}" />
							</h:column>
							<h:column>
								<f:facet name="header">
									<h:outputText value="Nota M�xima" />
								</f:facet>
								<h:outputText value="#{itemQues.notaMaxima}" />
							</h:column>
					</h:dataTable>
				</td>	
			</tr>
		</table>
		<table class="formulario" width="100%">
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="<<Voltar" action="#{modeloAvaliacao.iniciarBuscaModelos}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
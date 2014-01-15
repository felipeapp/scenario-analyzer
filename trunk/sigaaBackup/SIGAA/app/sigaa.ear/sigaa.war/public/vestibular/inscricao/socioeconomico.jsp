<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2 class="tituloPagina">Questionario Socioeconômico</h2>
	<h:form id="form">
		<table class="formulario" width="80%">
			<caption>Questionário Socioeconômico</caption>
			<tbody>
				<tr>
					<td><c:forEach 
						items="#{inscricaoVestibular.obj.respostasCandidatoQSE}"
						var="respostaCandidato">
						<table width="100%" class="subFormulario">
							<caption>${respostaCandidato.questaoSocioEconomico.ordem}
							- ${respostaCandidato.questaoSocioEconomico.pergunta}</caption>
							<tr>
								<td><h:selectOneMenu id="respostaCandidato"
									value="#{respostaCandidato.respostaQSE.id}">
									<f:selectItems
										value="#{respostaCandidato.questaoSocioEconomico.respostasCombo}" />
								</h:selectOneMenu></td>
							</tr>
						</table>
					</c:forEach></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Cancelar" onclick="#{confirm}" id="cancelar" action="#{inscricaoVestibular.cancelar}" immediate="true" />
						<h:commandButton id="submeterQSE" value="#{inscricaoVestibular.submitButton}" action="#{inscricaoVestibular.submeterSocioEconomico}" /> 
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>

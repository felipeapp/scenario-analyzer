<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> &gt; Visualização do Questionário de Avaliação</h2>
	<br />
	<h:form>
		<table class="formulario" width="100%">
				<caption>Questionário de Avaliação</caption>
				<tr>
					<td>
						<h:dataTable id="dtView" rowClasses="linhaPar, linhaImpar" binding="#{questionarioAvaliacao.itensDown}" 
							value="#{questionarioAvaliacao.obj.itensAvaliacao}" var="itemQues" width="100%">
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
									<center><h:outputText value="#{itemQues.peso}" /></center>
								</h:column>
								<h:column>
									<f:facet name="header">
										<h:outputText value="Nota Máxima" />
									</f:facet>
									<center><h:outputText value="#{itemQues.notaMaxima}" /></center>
								</h:column>
						</h:dataTable>
					</td>	
				</tr>
				<tfoot>
					<tr>
						<td>
							<center><h:commandButton value="<<Voltar" action="#{questionarioAvaliacao.iniciarBuscaQuestionarios}" /></center>
						</td>
					</tr>
				</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
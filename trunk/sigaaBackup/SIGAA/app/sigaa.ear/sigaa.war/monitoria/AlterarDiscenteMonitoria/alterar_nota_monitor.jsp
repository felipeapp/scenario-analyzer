<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<h2><ufrn:subSistema /> > Alterar Nota e Classificação</h2>
<br>

<f:view>
<h:form id="form">
	<table class="formulario" width="100%" cellpadding="3">
		<tbody>
			<caption class="listagem"> Alterar Nota e Classificação </caption>

					<tr>
						<td >
							<t:dataTable value="#{alterarDiscenteMonitoria.obj}" 
								var="monitor" rowClasses="linhaPar,linhaImpar" width="100%" id="monitor" rowIndexVar="linha">
			
								<t:column width="20%">
									<f:facet name="header"><f:verbatim>Discente</f:verbatim></f:facet>
									<h:outputText value="#{monitor.discente.matriculaNome}"/>
								</t:column>

                                <t:column width="3%" styleClass="centerAlign">
                                    <f:facet name="header"><f:verbatim>Classificação</f:verbatim></f:facet>
                                    <h:inputText value="#{monitor.classificacao}" size="4" maxlength="3" onkeypress="return(formataInteiro(this, event, 1))" id="txtClassificacao"/>
                                </t:column>
			
								<t:column width="3%" styleClass="centerAlign">
									<f:facet name="header"><f:verbatim>Prova Escrita</f:verbatim></f:facet>
									<h:inputText value="#{monitor.notaProva}" 
									size="4" maxlength="3" onkeypress="return(formataValor(this, event, 1))" onblur="verificaNotaMaiorDez(this)" id="txtProvaEscrita">
												<f:converter converterId="convertNota"/>
									</h:inputText>									
								</t:column>
			
								<t:column width="3%" styleClass="centerAlign">
									<f:facet name="header"><f:verbatim>Nota Final</f:verbatim></f:facet>
									<h:inputText value="#{monitor.nota}" size="4" maxlength="3" onkeypress="return(formataValor(this, event, 1))" onblur="verificaNotaMaiorDez(this)" id="txtNotaFinal">
										<f:converter converterId="convertNota"/>
									</h:inputText>									
								</t:column>
							</t:dataTable>
					</td>
				</tr>
		</tbody>
		<tfoot>
				<tr>
					<td>
					    <h:commandButton value="< Voltar" action="#{comissaoMonitoria.alterarMonitor}" id="BtnVoltar" rendered="#{acesso.monitoria}"/>
						<h:commandButton value="Alterar" action="#{alterarDiscenteMonitoria.alterarNotas}" id="BtnAlterarNota"/>
						<h:commandButton value="Cancelar" action="#{ alterarDiscenteMonitoria.cancelar }" onclick="#{confirm}" id="BtnCancelar"/>
					</td>
				</tr>
		</tfoot>
	</table>
	</h:form>
</f:view>
<br/>
<br/>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
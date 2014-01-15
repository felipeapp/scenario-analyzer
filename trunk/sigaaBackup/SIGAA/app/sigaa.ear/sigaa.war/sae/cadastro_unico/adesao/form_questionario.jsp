<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Cadastro Único de Bolsistas > Questionário</h2>

<f:view>
	<h:form id="form">
	
	<table class="formulario" style="width: 85%;">
		<caption>Questionário Sócio Econômico</caption>
			<tbody>
				<tr>
					<td><%@include file="/geral/questionario/_formulario_respostas.jsp" %></td>
				</tr>
				
				<tr>
					<td class="subFormulario">Itens do conforto familiar</td>
				</tr>
				
				<tr>
					<td>
						<t:newspaperTable value="#{adesaoCadastroUnico.lista}" var="confortoFamiliar" newspaperColumns="2" 
									newspaperOrientation="horizontal" rowClasses="linhaPar, linhaImpar"  width="100%">					
					
							<t:column style="text-align: right;">
			                      <h:outputText value="#{confortoFamiliar.item.item}" />
		                    </t:column>
		                    
		                    <t:column>
								<h:selectOneMenu value="#{confortoFamiliar.quantidade}">
									<f:selectItem itemValue="0" itemLabel="Nenhum" />
									<f:selectItem itemValue="1" itemLabel="1" />
									<f:selectItem itemValue="2" itemLabel="2" />
									<f:selectItem itemValue="3" itemLabel="3" />
								</h:selectOneMenu>
		                    </t:column>
		                </t:newspaperTable>
					</td>				
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td>
						<h:commandButton value="Confirmar Inscrição" action="#{adesaoCadastroUnico.finalizarQuestionario}" 
							onclick="return confirm('Confirma a sua adesão ao Programa de Cadastro Único?');"/> 
						<h:commandButton value="Cancelar" action="#{adesaoCadastroUnico.cancelar}" onclick="#{confirm}" />
					</td>
				</tr>			
			</tfoot>
	</table>
	<br/>
	<div align="center">
		<span class="required" style="font-size: small">&nbsp;</span>
		Campos de preenchimento obrigatório.
	</div>
		
	</h:form>
	
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
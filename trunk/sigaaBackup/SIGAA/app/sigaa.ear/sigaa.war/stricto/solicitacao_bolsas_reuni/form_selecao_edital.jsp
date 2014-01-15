<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="solicitacaoBolsasReuniBean" />
		
	<h2> <ufrn:subSistema /> &gt; Solicitação de Bolsas REUNI de Assistência ao Ensino </h2>
	
	<h:form>
		
		<table class="formulario" width="45%">
			<caption class="formulario">Selecione o Edital</caption>
			
			<tbody>			
					<tr>
						<td class="required" style="width: 50px;">Edital:</td>
						<td>
							<h:selectOneMenu value="#{solicitacaoBolsasReuniBean.edital}" style="width: 350px;" id="idEdital">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
								<f:selectItems value="#{editalBolsasReuniBean.allCombo}" />
							</h:selectOneMenu>
						</td>
					</tr>
				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Consultar" action="#{solicitacaoBolsasReuniBean.selecionarEdital}" id="btConsulta"/>
						<h:commandButton value="Cancelar"  action="#{solicitacaoBolsasReuniBean.cancelar}" immediate="true" onclick="#{confirm}" id="cancelarOperacao"/>
					</td>
				</tr>
			</tfoot>
		</table>
	<br />
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	</h:form>	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	
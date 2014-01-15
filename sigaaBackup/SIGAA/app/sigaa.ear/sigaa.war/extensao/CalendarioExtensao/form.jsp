<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Configurar Calendário de Extensão</h2>
	<h:form>
	
	<table class="formulario" width="70%">
		<caption>Calendário de Extensão</caption>
		<tbody>
			<tr>
				<th class="obrigatorio" width="30%">Ano Referência:</th>
				<td><h:inputText value="#{calendarioExtensao.obj.anoReferencia}" id="anoReferencia" size="4" 
							readonly="#{calendarioExtensao.readOnly}" maxlength="4" onkeyup="formatarInteiro(this)">
						<a4j:support event="onblur" actionListener="#{calendarioExtensao.alterarAno}"
								reRender="periodoEfetivacaoInicial, periodoEfetivacaoFim, cadastrar" />		
					</h:inputText>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<table class="formulario" width="100%">
						<tr>
							<td colspan="2" class="subFormulario">Período de efetivação das bolsas de extensão.</td>
						</tr>
			
						<tr>
							<th style="font-weight: bold">Projetos submetidos em:</th>
							<td>Todos os anos</td>
						</tr>

						<tr>
							<th width="30%">Iniciar Em:</th>
							<td>
				    			 <t:inputCalendar value="#{calendarioExtensao.obj.inicioCadastroBolsa}" id="periodoEfetivacaoInicial" size="10" maxlength="10" 
				    				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
				    				renderAsPopup="true" renderPopupButtonAsImage="true" >
				      				<f:converter converterId="convertData"/>
								</t:inputCalendar> 
							</td>
						</tr>
			
						<tr>
							<th>Finalizar Em:</th>
							<td>
				    			 <t:inputCalendar value="#{calendarioExtensao.obj.fimCadastroBolsa}" id="periodoEfetivacaoFim" size="10" maxlength="10" 
				    				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
				    				renderAsPopup="true" renderPopupButtonAsImage="true" >
				      				<f:converter converterId="convertData"/>
								</t:inputCalendar> 
							</td>
									
						</tr>

					</table>
					</td>
			</tr>
		</tbody>
		
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="#{calendarioExtensao.confirmButton}" action="#{calendarioExtensao.cadastrar}" id="cadastrar" />
					<h:commandButton value="Cancelar" action="#{calendarioExtensao.cancelar}" id="cancelar" onclick="#{confirm}" />
				</td>
			</tr>
		</tfoot>
	</table>
	
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %> 
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
<a4j:keepAlive beanName="calendarioAcoesAssociadas" />
<h:form prependId="false">
	<h2><ufrn:subSistema />&gt; Configurar Calendário de Ações Integradas</h2>
	<table class="formulario" width="70%" id="tabGeral" >
		<caption>Calendário de Ações Integradas</caption>
		<tbody>
			<tr>
				<td colspan="2"  class="subFormulario" >Período de Efetivação das Bolsas de Projetos</td>
			</tr>
			<tr>
				<th width="50%" class= "${ calendarioAcoesAssociadas.altera ? " " : "obrigatorio"}" 
				style="${ calendarioAcoesAssociadas.altera ? "font-weight: bold" : " "}">Ano Referência:</th>
				<td>
					<h:inputText id="anoReferencia"
						value="#{calendarioAcoesAssociadas.obj.anoReferencia}" size="4"
						maxlength="4" readonly="#{calendarioAcoesAssociadas.readOnly}"
						onkeyup="formatarInteiro(this)" rendered="#{!calendarioAcoesAssociadas.altera}"/>
					<h:outputText id="anoReferenciaAlt" 
						value="#{calendarioAcoesAssociadas.obj.anoReferencia}" rendered="#{calendarioAcoesAssociadas.altera}" />
				</td>
			</tr>
				
						
			<tr>
				<th class="obrigatorio">Início do cadastro dos Planos de Trabalho:</th>
				<td>
					<t:inputCalendar id="periodoEfetivacaoInicial"
						value="#{calendarioAcoesAssociadas.obj.inicioCadastroBolsa}"
						renderAsPopup="true" renderPopupButtonAsImage="true"
						popupDateFormat="dd/MM/yyyy" size="10"
						onkeypress="return(formatarMascara(this,event,'##/##/####'))"
						maxlength="10" popupTodayString="Hoje é">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>
				
			<tr>
				<th class="obrigatorio">Fim do cadastro dos Planos de Trabalho:</th>
				<td>
					<t:inputCalendar id="periodoEfetivacaoFim"
						value="#{calendarioAcoesAssociadas.obj.fimCadastroBolsa}"
						renderAsPopup="true" renderPopupButtonAsImage="true"
						popupDateFormat="dd/MM/yyyy" size="10"
						onkeypress="return(formatarMascara(this,event,'##/##/####'))"
						maxlength="10" popupTodayString="Hoje é">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>
				
				
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton id="btnConfirmar" value="#{calendarioAcoesAssociadas.confirmButton}" action="#{calendarioAcoesAssociadas.cadastrar}" />
					<h:commandButton id="btnCancelar" value="Cancelar" action="#{calendarioAcoesAssociadas.cancelar}" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
		
	</table>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
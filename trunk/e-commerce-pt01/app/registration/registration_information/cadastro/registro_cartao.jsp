<%@include file="/include/basic.jsp" %>
<f:view>
<%@include file="/include/head_administracao.jsp" %>


<h:form>
	<ecommerce:Mensagens />
	
	
	<%@include file="/menu.jsp"%>
	

	
	<table class="formulario">
		<caption>Informe as informa��es do cart�o</caption>
		<tbody>
			<tr>
				<th class="obrigatorio">
					Nome do titular:
				</th>
				<td>
					<h:inputText size="50" value="#{registroMBean.creditCard.nomeTitular}" />
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">
					N�mero do cart�o:
				</th>
				<td>
					<h:inputText size="50" value="#{registroMBean.creditCard.numeroCartao}"/>
				</td>
			</tr>
			<tr>
				<th>
					Data de vencimento:
				</th>
				<td>
				<rich:calendar firstWeekDay="0" showWeeksBar="false" 
					enableManualInput="true" datePattern="dd/MM/yyyy" 
					oninputkeyup="formatarData(this, event);" value="#{registroMBean.creditCard.dataVencimento}"/>
				</td>
			</tr>
			
			
				<tr>				<th class="obrigatorio">					D�gito verificador:				</th>				<td>					<h:inputText size="50" value="#{registroMBean.creditCard.dv}"/>				</td>			</tr>
			
			<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Cancelar" action="#{registroMBean.voltar}"/>
					<h:commandButton value="Avan�ar >>" action="#{registroMBean.iniciarCadastroCartao}"/>
				</td>
			</tr>
		</tfoot>
	</table>
	
</h:form>
<%@include file="/include/tail_administracao.jsp"%>
</f:view>

<%@include file="/include/basic.jsp" %>
<f:view>
<%@include file="/include/head_administracao.jsp" %>
<h2>Checkout</h2>
<div class="descricaoOperacao">
	Encerramento de Compras.	
</div>
<h:form>
	<ecommerce:Mensagens />
	<c:if test="${not empty checkoutMBean.valorTotal}">
O total da sua compra � de:		
<h:outputText value="#{checkoutMBean.valorTotal}" >
<f:convertNumber pattern="#,##0.00" />
</h:outputText>	
</c:if>
	<br />
	
	Escolha a forma de pagamento:
	<h:selectOneRadio id="PaymentTypes" title="Escolha a forma de pagamento:" border="1">
		
			<f:selectItem id="p1" itemLabel="Cart�o de Cr�dito" itemValue="1" />
		
			<f:selectItem id="p2" itemLabel="Cart�o de D�bito" itemValue="2" />
		
			<f:selectItem id="p3" itemLabel="Ordem Banc�ria" itemValue="3" />
	</h:selectOneRadio>
	<br/>
	Escolha a forma de confirma��o do Pedido:
	<h:selectOneRadio id="OrdemConfirmation" title="Escolha a confirma��o do pagamento:" border="1">
		
			<f:selectItem id="o1" itemLabel="P�gina Eletr�nica" itemValue="1" />
		
			<f:selectItem id="o2" itemLabel="E-mail" itemValue="2" />
		
			<f:selectItem id="o3" itemLabel="Telefone" itemValue="3" />
	</h:selectOneRadio>
</h:form>
<%@include file="/include/tail_administracao.jsp" %>	
</f:view>

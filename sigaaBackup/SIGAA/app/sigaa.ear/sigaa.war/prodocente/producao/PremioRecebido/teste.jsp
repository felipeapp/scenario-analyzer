<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form styleClass="formulario">
	<input value="13954660" name="id" id="id_teste" type="text"> <br><br>
	<h:commandButton	value="Validar"
						action="#{premioRecebido.preValidar}" /> <h:commandButton
						value="Cancelar" action="#{premioRecebido.cancelar}" />
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
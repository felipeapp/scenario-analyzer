<%@include file="/ava/cabecalho.jsp" %>

<f:view>
	<%@include file="/ava/menu.jsp" %>
	<h:form>
	
<h:messages showDetail="true" />

	<fieldset>
		<legend>COMPROVANTE DE ENVIO</legend>
		<center><h2>Submiss�o da tarefa realizada com sucesso. Comprovante de submiss�o N� <h:outputText value="#{ respostaTarefaTurma.obj.numeroComprovante}" />.</h2></center>
	</fieldset>
	
	</h:form>
	
</f:view>
<%@include file="/ava/rodape.jsp" %>
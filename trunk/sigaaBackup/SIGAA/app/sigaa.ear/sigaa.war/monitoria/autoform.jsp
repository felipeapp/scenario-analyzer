<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<br>
	<br>
	<h2><ufrn:subSistema /> > Cadastro de Projeto de Monitoria</h2>
	<h:form>
		<h:outputText value="#{grupoItemAvaliacao.renderForm}"/>
		 <h:panelGrid id="panelgridtest" binding="#{grupoItemAvaliacao.form.component}"/>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<%@include file="/mobile/commons/cabecalho.jsp"%>
	<f:view>
		
		<center>
			<strong> Menu Principal </strong><br/>
		</center>
		
		<h:form>
			
			<h:commandButton value="Pesquisar Disciplinas" action="#{consultaServPubMobileMBean.iniciarBuscaDisciplinas}" style="width: 150px;  background-color: #EFF3FA"/> <br/>
			<h:commandButton value="Pesquisar Turmas" action="#{consultaServPubMobileMBean.iniciarBuscaTurmas}" style="width: 150px;  background-color: #EFF3FA"/> <br/>
			<h:commandButton value="Pesquisar Cursos" action="#{consultaServPubMobileMBean.iniciarBuscaCursos}" style="width: 150px;  background-color: #EFF3FA"/> <br/>
		
		</h:form>
				
	</f:view>	

<%@include file="/mobile/commons/rodape.jsp"%>

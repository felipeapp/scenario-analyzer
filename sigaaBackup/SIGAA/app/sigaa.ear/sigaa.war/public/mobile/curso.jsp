<%@include file="/mobile/commons/cabecalho.jsp"%>

<f:view>

	<a href="http://wap.ufrn.br/">Menu Principal</a> <br/><br/>

	Pesquisar Cursos: <br/><br/>
	<h:form>

	Nivel:
	<h:selectOneMenu value="#{consultaServPubMobileMBean.nivelEnsino}">
		<f:selectItem itemLabel="Tecnico" itemValue="T" />
		<f:selectItem itemLabel="Graduacao" itemValue="G" />
		<f:selectItem itemLabel="Mestrado" itemValue="E" />
		<f:selectItem itemLabel="Doutorado" itemValue="D" />
	</h:selectOneMenu>

	<div> <br/> </div>
	
	<h:commandButton value="Pesquisar" 
		action="#{consultaServPubMobileMBean.consultarCursos}" 
			style="width: 150px;  background-color: #EFF3FA"/> 
			
	<br/>
	</h:form>


	<c:if test="${not empty consultaServPubMobileMBean.listaCursosCentro}">
		<c:forEach items="${consultaServPubMobileMBean.listaCursosCentro}" var="item">
			
			
			<div>
					<br/>
					<b> ${item.key} </b>
					<hr/> 
				 
						<br/> 
						<c:forEach items="${item.value.cursos}" var="itemValue">
							${itemValue.nome} <br/> <br/>	
						</c:forEach>
			</div>
			
		</c:forEach>
	</c:if>

 	<br/>
 	
	<c:if test="${empty consultaServPubMobileMBean.listaCursosCentro}">
		Nenhum registro
	</c:if>
	
	<br/>
	
</f:view>
<%@include file="/mobile/commons/rodape.jsp"%>
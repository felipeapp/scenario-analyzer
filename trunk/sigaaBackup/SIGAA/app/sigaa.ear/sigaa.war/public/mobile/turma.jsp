<%@include file="/mobile/commons/cabecalho.jsp"%>

<f:view>

	<a href="http://wap.ufrn.br/">Menu Principal</a> <br/><br/>

	Pesquisar Turmas: <br/><br/> 
	<h:form>

	Ano/Semestre: <h:inputText value="#{consultaServPubMobileMBean.ano}" maxlength="4" size="4" />.<h:inputText value="#{consultaServPubMobileMBean.semestre}" maxlength="1" size="2" /> <br/>
	Cod. Disc.: <h:inputText value="#{consultaServPubMobileMBean.codigoDisciplina}" maxlength="10" size="10" /> <br/>

	<h:commandButton value="Pesquisar" 
		action="#{consultaServPubMobileMBean.consultarTurmas}" 
			style="width: 150px;  background-color: #EFF3FA"/> <br/>
	</h:form>

	<c:if test="${not empty consultaServPubMobileMBean.listaTurmas}">
		<hr/>
		
		<c:forEach items="#{consultaServPubMobileMBean.listaTurmas}" var="item">
			${item.turma.disciplina.detalhes.nome_ascii} <br/>
			<strong> Professor: </strong> ${item.docente.pessoa.nome} <br/>
			<strong> Horario: </strong> ${item.turma.descricaoHorario} <br/>
			<strong> Local: </strong> ${item.turma.local} <br/>
			<strong> Turma: </strong> ${item.turma.disciplina.detalhes.codigo} <br/>
			<hr/>
		</c:forEach>
	</c:if>
	
	<br/>
	<c:if test="${empty consultaServPubMobileMBean.listaTurmas}">
		Nenhum registro
	</c:if>
	
	<br/>
	
</f:view>
<%@include file="/mobile/commons/rodape.jsp"%>
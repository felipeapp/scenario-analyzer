<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<a4j:keepAlive beanName="turmaInfantilMBean" />
<f:view>

	<h:form id="form">
	
		<h2> <ufrn:subSistema /> &gt; Lista de Turmas</h2>
		<br/>
		<div class="infoAltRem">
		    <h:graphicImage value="/img/listar.gif" style="overflow: visible;"/>
		    <h:outputText value=": Formulário da Turma" />
		</div>
		<c:if test="${not empty turmaInfantilMBean.resultadosBusca}">
			<table class="listagem">
				<caption>Turmas Encontradas (${fn:length(turmaInfantilMBean.resultadosBusca)})</caption>
				<thead>
					<tr>
						<th>Turma</th>
						<th></th>
						<th></th>
						<th></th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<c:forEach items="#{turmaInfantilMBean.resultadosBusca}" var="turma" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${ turma.descricaoTurmaInfantil }</td>
						<td>
							<h:commandLink title="Formulário da Turma" action="#{ formularioEvolucaoCriancaMBean.iniciarFormularioTurma }" >
						        <f:param name="idTurma" value="#{turma.id}"/>
						        <f:param name="idComponente" value="#{turma.disciplina.id}"/>
					    		<h:graphicImage url="/img/listar.gif" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
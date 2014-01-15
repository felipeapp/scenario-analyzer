<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<f:subview id="menu">
<%@include file="/portais/tutor/menu_tutor.jsp" %>
</f:subview>

<h2> Alunos Orientados </h2>

<c:if test="${usuario.tutor.acessoCompleto}">
	<div class="descricaoOperacao">
		<center>
			<img src="${ctx}/img/icones/page_white_edit.png"> Visualizar Avaliações Realiazadas do Aluno <br>
			<img src="${ctx}/img/seta.gif"> Avaliar o aluno
		</center>
	</div>
</c:if>

<h:outputText value="#{ portalTutor.create }"/>
	<table class="listagem">
		<caption>Alunos Orientados</caption>
		<thead>
		<tr>
			<th>Matrícula</th>
			<th>Nome</th>
			<th></th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${portalTutor.alunosOrientados}" var="aluno" varStatus="status">
		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td width="15%">
				${ aluno.matricula }
			</td>
			<td width="75%">
				${ aluno.pessoa.nome }
			</td>
			<td width="10">
			<h:form>
				<a4j:outputPanel rendered="#{ usuario.tutor.acessoCompleto }">
					<input type="hidden" name="discente" value="${ aluno.id }">
					<h:commandButton image="/img/view.gif" action="#{fichaAvaliacaoEad.fichaDiscente}" title="Ver Ficha de Avaliação"/>
					<h:commandButton image="/img/seta.gif" action="#{fichaAvaliacaoEad.iniciar}" title="Selecionar Discente"/>
				</a4j:outputPanel>
			</h:form>
			</td>
		</tr>
		</c:forEach>
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
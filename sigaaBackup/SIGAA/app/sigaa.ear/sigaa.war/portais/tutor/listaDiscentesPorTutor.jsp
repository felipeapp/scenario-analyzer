<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<f:subview id="menu">
<%@include file="/portais/tutor/menu_tutor.jsp" %>
</f:subview>

<h2> Alunos Orientados </h2>

<div class="descricaoOperacao">
	<center>
		<img src="${ctx}/img/seta.gif"> Logar como discente - <a href="javascript:history.go(-1)">Selecionar outro Tutor</a>
	</center>
</div>

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
		<c:forEach items="${logarComoDiscente.alunosOrientados}" var="aluno" varStatus="status">
		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td width="15%">
				${ aluno.matricula }
			</td>
			<td width="75%">
				${ aluno.pessoa.nome }
			</td>
			<td width="10">
			<h:form>
				<input type="hidden" name="discente" value="${ aluno.id }">
				<h:commandButton image="/img/seta.gif" action="#{logarComoDiscente.logarDiscente}" title="Logar como discente"/>
			</h:form>			
			</td>
		</tr>
		</c:forEach>
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> &gt; Questionários de Avaliação</h2>
	
	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{questionarioAvaliacao.iniciarCadastroQuestionario}" value="Cadastrar Questionário"/>
					<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Questionário
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Questionário
					<h:graphicImage value="/img/delete.gif"style="overflow: visible;"/>: Remover Questionário
				</h:form>
			</div>
	</center>
	<c:if test="${not empty questionarioAvaliacao.questionarios}">
	<table class="listagem" >
		<caption class="listagem">Lista de Questionários de Avaliação</caption>
		<thead>
		<tr>
			<td>Título</td>
			<td></td>
			<td></td>
			<td></td>
		</tr>
		</thead>
		<h:form>
		
		<c:forEach items="#{questionarioAvaliacao.questionarios}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
				<td width="20%" align="right">
					<h:commandLink title="Viusalizar Questionário" action="#{questionarioAvaliacao.viewQuestionario}">
						<f:param name="id" value="#{item.id}" />
						<h:graphicImage url="/img/view.gif"/>
					</h:commandLink>
				</td>
				<td width=20>
					<h:commandLink title="Alterar Questionário" action="#{questionarioAvaliacao.atualizar}">
						<f:param name="id" value="#{item.id}"/>
						<h:graphicImage url="/img/alterar.gif"/>
					</h:commandLink>
				</td>
				<td width=20>
					<h:commandLink title="Remover Questionário" onclick="return confirm('Deseja realmente remover este questionário?');" action="#{questionarioAvaliacao.inativar}">
						<f:param name="id" value="#{item.id}"/>
						<h:graphicImage url="/img/delete.gif"/>
					</h:commandLink>
				</td>
				
			</tr>
		</c:forEach>
		
		</h:form>
	</table>
	</c:if>
	<c:if test="${ empty questionarioAvaliacao.questionarios}">
		<center> Nenhuma Questionário encontrado. </center>
	</c:if>
	
	
	
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

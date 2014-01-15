<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> &gt; Perguntas de Avaliações</h2>
	<a4j:keepAlive beanName="grupoAvaliacao" />
	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{grupoAvaliacao.iniciarCadastroPergunta}" value="Cadastrar Pergunta"/>
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Pergunta
					<h:graphicImage value="/img/delete.gif"style="overflow: visible;"/>: Remover Pergunta
				</h:form>
			</div>
	</center>
	<c:if test="${not empty grupoAvaliacao.perguntas}">
		<table class="listagem">
			<caption class="listagem">Lista de Perguntas de Avaliações</caption>
			<thead>
				<tr>
					<td>Descrição</td>
					<td></td>
					<td></td>

				</tr>
			</thead>
			<h:form>

				<c:forEach items="#{grupoAvaliacao.perguntas}" var="item"
					varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${item.descricao}</td>
						<td width=20>
							<h:commandLink title="Alterar Pergunta"	action="#{grupoAvaliacao.preAlterarPergunta}">
								<f:param name="id" value="#{item.id}" />
								<h:graphicImage url="/img/alterar.gif" />
							</h:commandLink>
						</td>
						<td width=20>
							<h:commandLink title="Remover Pergunta"	action="#{grupoAvaliacao.inativarPergunta}" onclick="return confirm('Deseja realmente remover esta pergunta?');">
								<f:param name="id" value="#{item.id}" />
								<h:graphicImage url="/img/delete.gif" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>

			</h:form>

		</table>
	</c:if>
	<c:if test="${ empty grupoAvaliacao.perguntas}">
		<center> Nenhuma pergunta de avaliação encontrada. </center>
	</c:if>




</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

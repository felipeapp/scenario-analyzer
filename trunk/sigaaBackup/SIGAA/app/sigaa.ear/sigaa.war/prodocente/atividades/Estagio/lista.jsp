<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> &gt; Estágio</h2>


	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;" />: <h:commandLink action="#{estagio.preCadastrar}" value="Cadastrar Novo Estágio"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Estágio
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Estágio <br/>
		</div>
	</h:form>


	<h:outputText value="#{estagio.create}" />
	<table class=listagem width="100%">
		<caption class="listagem">Lista de Estágios</caption>
		<thead>
		<tr>
			<td>Nome do Projeto</td>
			<td>Instituição</td>
			<td>Orientando</td>
			<td>Período Início</td>
			<td>Agência Financiadora</td>
			<td></td>
			<td></td>
		</tr>
		</thead>

		<c:set var="lista" value="#{estagio.allAtividades}" />
		<c:if test="${empty lista}">
			<tr>
			<td colspan="6">
			<br />
			<center>
			<span style="color:red;">Nenhum Estágio Encontrado.</span>
			</center>
			</td>
			</tr>
		</c:if>
		<h:form>
		<c:forEach items="#{lista}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.nomeProjeto}</td>
				<td>${item.instituicao}</td>
				<td>${item.aluno == null ? item.orientando : item.aluno.pessoa.nome}</td>
				<td>
					<ufrn:format type="data" valor="${item.periodoInicio}"/>
				</td>
				<td>${item.entidadeFinanciadora.nome}</td>
				<td width="20">
					<h:commandLink title="Alterar" action="#{estagio.atualizar}">
						<f:param name="id" value="#{item.id}"/>
						<h:graphicImage url="/img/alterar.gif"/>
					</h:commandLink>
				</td>
				<td width="25">
					<h:commandLink title="Remover" action="#{estagio.remover}" onclick="#{confirmDelete}">
						<f:param name="id" value="#{item.id}"/>
						<h:graphicImage url="/img/delete.gif"/>
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
		</h:form>

	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

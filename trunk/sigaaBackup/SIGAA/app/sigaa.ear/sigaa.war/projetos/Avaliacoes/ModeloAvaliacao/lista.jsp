<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> &gt; Modelos de Avaliações</h2>
	<a4j:keepAlive beanName="modeloAvaliacao"></a4j:keepAlive>
	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{modeloAvaliacao.iniciarCadastroModeloAvaliacao}" value="Cadastrar Modelo de Avaliação"/>
					<h:graphicImage value="/img/view.gif" style="overflow: visible;" />Visualizar Modelo de Avaliação
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Modelo de Avaliação
					<h:graphicImage value="/img/delete.gif"style="overflow: visible;"/>: Remover Modelo de Avaliação				
				</h:form>
			</div>
	</center>
	<c:if test="${not empty modeloAvaliacao.modelos}">
	<table class="listagem" >
		<caption class="listagem">Lista de Modelos de Avaliações</caption>
		<thead>
		<tr>
			<td>Descrição</td>
			<td>Tipo</td>
			<td>Edital</td>
			<td>Questionário</td>
			<td></td>
			<td></td>
			<td></td>
		</tr>
		</thead>
		<h:form>
		
		<c:forEach items="#{modeloAvaliacao.modelos}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
				<td>${item.tipoAvaliacao.descricao}</td>
				<td>${item.edital.descricao}</td>
				<td>${item.questionario.descricao}</td>
				<td width="2%">
					<h:commandLink title="Visualizar Modelo de Avaliação" action="#{modeloAvaliacao.viewModelo}">
						<f:param name="id" value="#{item.id}" />
						<h:graphicImage url="/img/view.gif" />
					</h:commandLink>
				</td>
				<td width="2%">
					<h:commandLink title="Alterar Modelo de Avaliação" action="#{modeloAvaliacao.atualizar}">
						<f:param name="id" value="#{item.id}"/>
						<h:graphicImage url="/img/alterar.gif"/>
					</h:commandLink>
				</td>
				<td width="2%">
					<h:commandLink title="Remover Modelo de Avaliação" onclick="return confirm('Deseja realmente remover este modelo de avaliação?');" 
						action="#{modeloAvaliacao.inativar}">
						<f:param name="id" value="#{item.id}"/>
						<h:graphicImage url="/img/delete.gif"/>
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
		
		</h:form>
	</table>
	</c:if>
	<c:if test="${ empty modeloAvaliacao.modelos}">
		<center> Nenhum modelo de avaliação encontrado. </center>
	</c:if>
	
	
	
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

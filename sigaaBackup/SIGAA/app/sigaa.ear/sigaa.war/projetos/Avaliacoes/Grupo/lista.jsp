<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
<a4j:keepAlive beanName="grupoAvaliacao"></a4j:keepAlive>
	<h2><ufrn:subSistema /> &gt; Grupos de Avaliações</h2>
	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{grupoAvaliacao.preCadastrarGrupo}" value="Cadastrar Grupo"/>
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Grupo
					<h:graphicImage value="/img/delete.gif"style="overflow: visible;"/>: Remover Grupo				
				</h:form>
			</div>
	</center>
	<c:if test="${not empty grupoAvaliacao.grupos}">
	<table class="listagem" >
		<caption class="listagem">Lista de Grupos de Avaliações</caption>
		<thead>
		<tr>
			<td>Descrição</td>
			<td></td>
			<td></td>
		</tr>
		</thead>
		<h:form>
		
		<c:forEach items="#{grupoAvaliacao.grupos}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
				<td width="2%">
					<h:commandLink title="Alterar Grupo" action="#{grupoAvaliacao.preAlterarGrupo}">
						<f:param name="id" value="#{item.id}"/>
						<h:graphicImage url="/img/alterar.gif"/>
					</h:commandLink>
				</td>
				<td width="2%">
					<h:commandLink title="Remover Grupo" onclick="return confirm('Deseja realmente remover este grupo?');" action="#{grupoAvaliacao.inativarGrupo}">
						<f:param name="id" value="#{item.id}"/>
						<h:graphicImage url="/img/delete.gif"/>
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
		
		</h:form>
	</table>
	</c:if>
	<c:if test="${ empty grupoAvaliacao.grupos}">
		<center> Nenhum grupo de avaliação encontrado. </center>
	</c:if>
	
	
	
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

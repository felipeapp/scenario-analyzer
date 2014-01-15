<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> &gt; Modelos de Avalia��es</h2>
	<a4j:keepAlive beanName="modeloAvaliacao"></a4j:keepAlive>
	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{modeloAvaliacao.iniciarCadastroModeloAvaliacao}" value="Cadastrar Modelo de Avalia��o"/>
					<h:graphicImage value="/img/view.gif" style="overflow: visible;" />Visualizar Modelo de Avalia��o
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Modelo de Avalia��o
					<h:graphicImage value="/img/delete.gif"style="overflow: visible;"/>: Remover Modelo de Avalia��o				
				</h:form>
			</div>
	</center>
	<c:if test="${not empty modeloAvaliacao.modelos}">
	<table class="listagem" >
		<caption class="listagem">Lista de Modelos de Avalia��es</caption>
		<thead>
		<tr>
			<td>Descri��o</td>
			<td>Tipo</td>
			<td>Edital</td>
			<td>Question�rio</td>
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
					<h:commandLink title="Visualizar Modelo de Avalia��o" action="#{modeloAvaliacao.viewModelo}">
						<f:param name="id" value="#{item.id}" />
						<h:graphicImage url="/img/view.gif" />
					</h:commandLink>
				</td>
				<td width="2%">
					<h:commandLink title="Alterar Modelo de Avalia��o" action="#{modeloAvaliacao.atualizar}">
						<f:param name="id" value="#{item.id}"/>
						<h:graphicImage url="/img/alterar.gif"/>
					</h:commandLink>
				</td>
				<td width="2%">
					<h:commandLink title="Remover Modelo de Avalia��o" onclick="return confirm('Deseja realmente remover este modelo de avalia��o?');" 
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
		<center> Nenhum modelo de avalia��o encontrado. </center>
	</c:if>
	
	
	
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

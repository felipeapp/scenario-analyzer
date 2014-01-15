<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
 .colData{width:55px !important;text-align: center !important;}	
   .colStatus{width:55px !important;text-align: center !important;}	
</style>

<f:view>
		
	<c:if test="${secaoExtraSite.portalDocente}"> 
	<%@include file="/portais/docente/menu_docente.jsp"%>
	</c:if>
	<c:if test="${secaoExtraSite.portalCoordenadorStricto}">
	<%@include file="/stricto/menu_coordenador.jsp"%>
	</c:if>
	<c:if test="${secaoExtraSite.portalCoordenadorGraduacao}">
	<%@include file="/graduacao/menu_coordenador.jsp"%>
	</c:if>
	
	<h2>
		<ufrn:subSistema /> 
		<h:outputText escape="false" rendered="#{not empty secaoExtraSite.curso}" value="&gt; #{secaoExtraSite.curso.descricao}" />
		&gt; Lista de Seções Extras
	</h2>
	<h:outputText value="#{secaoExtraSite.create}" />
	
	<div class="descricaoOperacao">
		<p>Caro Usuário(a),</p>
		<br />
		<p>
			A listagem abaixo exibe todas as notícias publicadas ou não no portal público.
		</p>
	</div>

	<center><h:messages />
	<h:form>
	<div class="infoAltRem">
	   	<h:commandLink styleClass="noborder" action="#{secaoExtraSite.preCadastrar}">
			<h:graphicImage url="/img/adicionar.gif" />
			 Cadastrar
		</h:commandLink>
		<h:graphicImage value="/img/view.gif" style="overflow: visible;" />
			: Visualizar no Portal Público
	   	<h:graphicImage value="/img/alterar.gif"
		style="overflow: visible;"  />: Alterar 
		<h:graphicImage
		value="/img/delete.gif" style="overflow: visible;" />: Remover <br />
	</div>
	</center>
	
		<table class="listagem" style="width: 100%">
			<caption>Lista de Seções Extras</caption>
			<thead>
				<tr>
					<th>Idioma</th>
					<th>Titulo</th>
					<td class="colStatus">Publicada</td>
					<th class="colData">Data</th>
					<th colspan="3" width="1%"></th>
				</tr>
			</thead>
			<c:set var="secoesExtra" value="#{secaoExtraSite.resultadosBusca}" />
			<c:choose>

				<c:when test="${not empty secoesExtra}">
					<c:forEach items="#{secoesExtra}" var="item" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td>${item.locale}</td>
							<td>${item.titulo}</td>
							<td  class="colStatus">
								<h:outputText value="sim" rendered="#{item.publicada}" />
								<h:outputText escape="false" value="<font color='red'>não</font>" rendered="#{!item.publicada}" />
							</td>
							<td class="colData">
								<ufrn:format type="data" valor="${item.dataCadastro}"></ufrn:format>&nbsp;
							</td>
							<td width="20"><h:commandLink styleClass="noborder" rendered="#{empty item.linkExterno}"
								title="Visualizar no Portal Público" action="#{secaoExtraSite.verSecao}" target="_blank">
								<h:graphicImage url="/img/view.gif" />
								<f:param name="id" value="#{item.id}" />
							</h:commandLink></td>
							<td>
								<h:commandLink styleClass="noborder" title="Alterar"
								action="#{secaoExtraSite.atualizar}">
								<h:graphicImage url="/img/alterar.gif" />
								<f:param name="id" value="#{item.id}" />
								</h:commandLink>
							</td>
							<td>
								<h:commandLink styleClass="noborder" title="Remover"
								action="#{secaoExtraSite.remover}" immediate="false" onclick="#{confirmDelete}">
								<h:graphicImage url="/img/delete.gif" />
								<f:param name="id" value="#{item.id}" />
								</h:commandLink>
							</td>
						</tr>
					</c:forEach>
				</c:when>

				<c:otherwise>
					<tr>
						<td colspan="10" align="center">Nenhuma seção cadastrada até o momento</td>
					</tr>
				</c:otherwise>

			</c:choose>
			<tfoot>
				<tr>
					<td colspan="10" align="center"><h:commandButton value="<< Voltar"
						action="#{detalhesSite.listarCursosTecnico}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
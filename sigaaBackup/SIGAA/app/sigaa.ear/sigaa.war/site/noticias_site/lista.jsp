<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
 .colData{width:55px !important;text-align: center !important;}
  .colStatus{width:55px !important;text-align: center !important;}		
</style>
<f:view>
	<c:if test="${noticiaSite.portalDocente}"> 
	<%@include file="/portais/docente/menu_docente.jsp"%>
	</c:if>
	<c:if test="${noticiaSite.portalCoordenadorStricto}">
	<%@include file="/stricto/menu_coordenador.jsp"%>
	</c:if>
	<c:if test="${noticiaSite.portalCoordenadorGraduacao}">
	<%@include file="/graduacao/menu_coordenador.jsp"%>
	</c:if>
	
	<h2>
		<ufrn:subSistema />
		<h:outputText escape="false" rendered="#{not empty noticiaSite.curso}" 
		value="&gt; #{noticiaSite.curso.descricao}" /> &gt; Lista de Not�cias
	</h2>
	<h:outputText value="#{noticiaSite.create}" />
	
	<div class="descricaoOperacao">
		<p>Caro Usu�rio(a),</p>
		<br />
		<p>
			A listagem abaixo exibe todas as not�cias publicadas ou n�o no portal p�blico.
		</p>
	</div>	
	
	
	<h:form id="formListaNoticiaSite">
	
	<center>
	<div class="infoAltRem">
		<h:commandLink styleClass="noborder" id="adicionarNoticia" action="#{noticiaSite.preCadastrar}">
			<h:graphicImage url="/img/adicionar.gif" /> Cadastrar
		</h:commandLink>
		<h:graphicImage value="/img/view.gif" style="overflow: visible;" />
			: Visualizar no Portal P�blico
		<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar 
		<h:graphicImage
		value="/img/delete.gif" style="overflow: visible;" />: Remover <br />
	</div>
	</center>
	
		<table class="listagem" style="width: 100%">
			
			<caption>Lista de Not�cias</caption>
			<thead>
				<tr>
					<td>T�tulo</td>
					<td class="colStatus">Publicada</td>
					<td class="colData">Data</td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</thead>
			
			<c:choose>
				<c:when test="${not empty noticiaSite.resultadosBusca}">
					<c:forEach items="#{noticiaSite.resultadosBusca}" var="item" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td>${item.titulo}</td>
							<td  class="colStatus">
								<h:outputText escape="false" value="<font color='green'>Sim</font>" rendered="#{item.publicada}" />
								<h:outputText escape="false" value="<font color='red'>N�o</font>" rendered="#{!item.publicada}" />
							</td>
							<td class="colData">
								<ufrn:format type="data" valor="${item.dataCadastro}"></ufrn:format>
							</td>
							<td width="20">
							<h:commandLink styleClass="noborder" id="verNoticia"
								title="Visualizar" action="#{noticiaSite.verNoticia}" target="_blank">
								<h:graphicImage url="/img/view.gif" />
								<f:param name="id" value="#{item.id}" />
							</h:commandLink></td>
							<td width="20">
							<h:commandLink styleClass="noborder" id="alterarNoticia"
								title="Alterar" action="#{noticiaSite.atualizar}">
								<h:graphicImage url="/img/alterar.gif" />
								<f:param name="id" value="#{item.id}" />
							</h:commandLink>
							</td>
							<td width="25">
							<h:commandLink styleClass="noborder" id="removerNoticia"
								title="Remover" action="#{noticiaSite.remover}"
								onclick="#{confirmDelete}">
								<h:graphicImage url="/img/delete.gif" />
								<f:param name="id" value="#{item.id}" />
							</h:commandLink></td>
						</tr>
					</c:forEach>
				</c:when>	
				<c:otherwise>
					<tr >
					<td colspan="10" align="center">
						Nenhuma not�cia cadastrada at� o momento...
					</td>
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
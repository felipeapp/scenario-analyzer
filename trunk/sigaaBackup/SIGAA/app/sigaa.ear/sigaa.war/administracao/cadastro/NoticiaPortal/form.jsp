<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> Cadastro de Notícia no Portal</h2>
	<h:outputText value="#{ noticiaPortal.create }"/>
	<c:if test="${ !noticiaPortal.fromMonitoria }">
	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: left; width: 100%">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{noticiaPortal.listar}"/>
			</div>
			</h:form>
	</center>
	</c:if>


	<table class=formulario>
		<h:form>
			<caption class="listagem">Cadastro de Notícias para os Portais</caption>
			<h:inputHidden value="#{noticiaPortal.confirmButton}" />
			<h:inputHidden value="#{noticiaPortal.obj.id}" />
			<tr>
				<th>Título:</th>
				<td><h:inputText value="#{noticiaPortal.obj.titulo}"
					readonly="#{noticiaPortal.readOnly}" size="100"/></td>
			</tr>
			<tr>
				<th>Descrição:</th>
				<td><h:inputTextarea value="#{noticiaPortal.obj.descricao}"
					readonly="#{noticiaPortal.readOnly}" rows="20" cols="100" /></td>
			</tr>
			<c:if test="${ !noticiaPortal.fromMonitoria }">
			<tr>
				<th>Publicada:</th>
				<td><h:selectBooleanCheckbox value="#{noticiaPortal.obj.publicada}"
					readonly="#{noticiaPortal.readOnly}" /></td>
			</tr>
			</c:if>
			<tr>
				<th>Destaque:</th>
				<td><h:selectBooleanCheckbox value="#{noticiaPortal.obj.destaque}"
					readonly="#{noticiaPortal.readOnly}" /></td>
			</tr>


			<tr>
				<th>Localização:</th>
				<td><h:selectOneMenu value="#{noticiaPortal.obj.localizacao}">
						<f:selectItems value="#{noticiaPortal.localizacao}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2>
						<h:inputHidden value="#{ noticiaPortal.fromMonitoria }"/>
					<h:commandButton
						value="#{noticiaPortal.confirmButton}"
						action="#{noticiaPortal.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{noticiaPortal.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br>
	<c:if test="${ !noticiaPortal.fromMonitoria }">
	<a href="lista.jsf">Lista de Notícias </a>
	</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
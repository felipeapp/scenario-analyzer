<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<h2><ufrn:subSistema /> &gt; Cadastrar Gestor de Fórum de Cursos</h2>

<h:outputText value="#{gestorForumCursoBean.create}" />

	<h:form id="formGestorForum">
	
		<table class="formulario" width="100%">
			<caption>Dados do Gestor</caption>
			<tr>
				<th>Curso:</th>
				<td><h:outputText value="#{ gestorForumCursoBean.obj.curso.nomeCompleto }" id="outputNomeCurso"/></td>
			</tr>
			<tr>
				<th>Tipo de Gestor:</th>
				<td>
				<h:selectOneRadio value="#{ gestorForumCursoBean.gestorDocente }" id="tipoBusca">
						<f:selectItem itemValue="true" itemLabel = "Docente" />
						<f:selectItem itemValue="false" itemLabel = "Servidor" />
						<a4j:support event="onclick" reRender="formGestorForum"/>
					</h:selectOneRadio>
				</td>
			</tr>
			<c:if test="${gestorForumCursoBean.gestorDocente}">
			<tr>
				<th>Docente:</th>
				<td>
					<h:inputHidden value="#{gestorForumCursoBean.idDocente}" id="idDocente"/>
					<c:set var="idAjax" value="formGestorForum:idDocente"/>
					<c:set var="nomeAjax" value="gestorForumCursoBean.nomeDocente"/>
					<c:set var="inativos" value="false"/>
					<%@include file="/WEB-INF/jsp/include/ajax/docente_jsf.jsp" %>
				</td>
			</tr>
			</c:if>
			<c:if test="${!gestorForumCursoBean.gestorDocente}">
			<tr>
				<th>Servidor:</th>
				<td>
					<table class="buscaAjax" style="border: solid 1px #CCCCCC;">
					<tr><td>
						<h:inputText value="#{gestorForumCursoBean.nomeDocente}" id="nome" size="70" 
						 onchange="javascript:$('formGestorForum:checkOrientador').checked = checked;"/>
						<h:inputHidden value="#{gestorForumCursoBean.idDocente}" id="idServidor" />
			
						<ajax:autocomplete source="formGestorForum:nome" target="formGestorForum:idServidor"
							baseUrl="/sigaa/ajaxServidor" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
							parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
					</td></tr>	
					</table>
				</td>
			</tr>
			</c:if>
			
			<tfoot>
			<tr><td colspan="2">
  				 <h:inputHidden value="#{ gestorForumCursoBean.obj.id }"/>
  				 <h:inputHidden value="#{ gestorForumCursoBean.obj.curso.id }"/>
			     <h:inputHidden value="#{gestorForumCursoBean.confirmButton}" /> 
			     <h:commandButton value="#{gestorForumCursoBean.confirmButton}" action="#{ gestorForumCursoBean.cadastrar }" id="btnCadastrar"/>
			     <h:commandButton value="Cancelar" action="#{ gestorForumCursoBean.cancelar }" immediate="true" id="btnCancelar"/>
			</td></tr>
			</tfoot>
		</table>

	</h:form>

</f:view>
<br><br>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
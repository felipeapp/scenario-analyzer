<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	
	<h2><ufrn:subSistema /> > Alterar Docentes de Projetos</h2>
	
	<h:messages showDetail="true"></h:messages>

 	<h:form id="form">

	
	<table class="formulario" width="70%">
	<caption>Busca por Docentes</caption>
	<tbody>
	    
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{consultarEquipeDocente.checkBuscaProjeto}" id="selectBuscaProjeto"/>
			</td>
		
			<td width="20%"><label>Título do Projeto: </label></td>
			<td>
				<h:inputText id="tituloProjeto" value="#{consultarEquipeDocente.tituloProjeto }" style="width:90%;" onchange="javascript:$('form:selectBuscaProjeto').checked = true;"/>
			</td>
		</tr>		    

	    <tr>
			<td>
			<h:selectBooleanCheckbox value="#{consultarEquipeDocente.checkBuscaOrientador}" id="selectBuscaOrientador"/>
			</td>
	    	<td> <label>Nome do Docente: </label> </td>
	    	<td> 
	    	
				<h:inputText value="#{consultarEquipeDocente.servidor.pessoa.nome}" id="nome" style="width:90%;" onchange="javascript:$('form:selectBuscaOrientador').checked = true;"/>
				<h:inputHidden value="#{consultarEquipeDocente.servidor.id}" id="idServidor"/>
	
				<ajax:autocomplete source="form:nome" target="form:idServidor"
					baseUrl="/sigaa/ajaxServidor" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=todos,inativos=true"
					parser="new ResponseXmlToHtmlListParser()" />
	
				<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>	    	
	    	
	    	</td>
	    </tr>
	    
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
			<h:commandButton value="Buscar" action="#{ consultarEquipeDocente.localizar }" id="BtnBuscar"/>
			<h:commandButton value="Cancelar" action="#{ consultarEquipeDocente.cancelar }" id="BtnCancelar"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>

	<br/>

	<c:set var="orientadores" value="#{consultarEquipeDocente.orientadores}"/>

	<c:if test="${empty orientadores}">
		<center><i> Nenhum docente localizado. </i></center>
	</c:if>


	<c:if test="${not empty orientadores}">

	<div class="infoAltRem">
	    <h:graphicImage value="/img/monitoria/businessman_view.png" style="overflow: visible;"/>: Visualizar Dados do Docente
	    <h:graphicImage value="/img/monitoria/businessman_refresh.png" style="overflow: visible;" rendered="#{acesso.monitoria}"/><h:outputText value=": Alterar Docente"	rendered="#{acesso.monitoria}"/><br/>
	    <h:graphicImage value="/img/monitoria/businessman_delete.png" style="overflow: visible;" rendered="#{acesso.monitoria}"/><h:outputText value=": Finalizar Docente"	rendered="#{acesso.monitoria}"/>	    
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;" rendered="#{acesso.monitoria}"/><h:outputText value=": Excluir Docente do Projeto"	rendered="#{acesso.monitoria}"/>	    	    
	</div>

	 <table class="listagem">
	    <caption>Orientadores Encontrados (${ fn:length(orientadores) })</caption>

	      <thead>
	      	<tr>
	        	<th>Docente</th>
	        	<th>Projeto</th>
	        	<th>Início</th>
	        	<th>Fim</th>
	        	<th>&nbsp;</th>
	        	
	        </tr>
	 	</thead>
	 	<tbody>

       	<c:forEach items="#{orientadores}" var="orientador" varStatus="status">
               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

                    <td> ${orientador.servidor.siapeNome} </td>
                    <td> <span title="${orientador.projetoEnsino.anoTitulo}">${fn:substring(orientador.projetoEnsino.anoTitulo,0,45)}...</span> </td>
					<td> <fmt:formatDate value="${orientador.dataEntradaProjeto}" pattern="dd/MM/yyyy"/></td>
					<td> <fmt:formatDate value="${orientador.dataSaidaProjeto}" pattern="dd/MM/yyyy"/></td>
					<td width="12%">
					
							<h:commandLink title="Visualizar Dados do Docente" action="#{ consultarEquipeDocente.view }" id="btview">
							       <f:param name="id" value="#{orientador.id}"/>
				                   <h:graphicImage url="/img/monitoria/businessman_view.png" />
							</h:commandLink>							
							
							<h:commandLink  title="Alterar Docente" action="#{alterarEquipeDocente.preAlterarEquipeDocente}" style="border: 0;" rendered="#{ (acesso.monitoria)  }" id="btalterar" immediate="true">
							      <f:param name="id" value="#{orientador.id}"/>
				                  <h:graphicImage url="/img/monitoria/businessman_refresh.png" />
							</h:commandLink>
								
							<h:commandLink  title="Finalizar Docente"  action="#{alterarEquipeDocente.preFinalizarEquipeDocente}" style="border: 0;" rendered="#{ (acesso.monitoria)  }" id="btfinalizar" immediate="true">							
							      <f:param name="id" value="#{orientador.id}"/>
				                  <h:graphicImage url="/img/monitoria/businessman_delete.png" />
							</h:commandLink>								
							
							<h:commandLink  title="Excluir Docente do Projeto" action="#{alterarEquipeDocente.preExcluirEquipeDocente}" style="border: 0;" rendered="#{acesso.monitoria}" id="btexcluir">															
							       <f:param name="id" value="#{orientador.id}"/>
				                   <h:graphicImage url="/img/delete.gif" />
							</h:commandLink>
							
					</td>					

              </tr>
          </c:forEach>
	 	</tbody>
	 </table>
	
	</c:if>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h:outputText value="#{cadMonitor.create}"/>	
		
	<h2><ufrn:subSistema /> > Alterar Situação dos Projetos de Ensino</h2>


	<%@include file="/monitoria/form_busca_projeto.jsp"%>

	<c:set var="projetos" value="#{projetoMonitoria.projetosLocalizados}"/>

	<c:if test="${empty projetos}">
		<center><i> Nenhum Projeto localizado </i></center>
	</c:if>


	<c:if test="${not empty projetos}">

	<h:form>
		 <table class="listagem">
		    <caption>Projetos de Monitoria Encontrados (${ fn:length(projetos) })</caption>
		      <thead>
		      	<tr>
		      		<th style="text-align: center;"> <h:selectBooleanCheckbox styleClass="chkSelecionaTodos" onclick="selecionarTodos();" /> </th>
		        	<th>Ano</th>
		        	<th>Título</th>
		        	<th width="10%">Tipo</th>
		        	<th width="15%">Dimensão</th>		        	
		        	<th>Situação</th>
		        </tr>
		 	</thead>
		 			 	
		 	<tbody>
			<c:set var="unidadeProjeto" value=""/>	 		 
	       	<c:forEach items="#{projetos}" var="projeto" varStatus="status">
	
						<c:if test="${ unidadeProjeto != projeto.unidade.id }">
							<c:set var="unidadeProjeto" value="${ projeto.unidade.id }"/>
							<tr>
								<td colspan="11" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
								${ projeto.unidade.nome } / ${ projeto.unidade.sigla }
								</td>
							</tr>
						</c:if>

		               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						
						<td style="text-align: center;"> <h:selectBooleanCheckbox value="#{projeto.projeto.selecionado}" styleClass="todosChecks" /> </td>
						<td> ${projeto.ano}</td>
	                    <td>
	                    	<h:outputText value="#{projeto.titulo}">
	                    		<f:attribute name="lineWrap" value="90"/>
								<f:converter converterId="convertTexto"/>
	                    	</h:outputText>  
		                     <br/><i><h:outputText value="Coordenador(a): #{projeto.coordenacao.pessoa.nome}" rendered="#{not empty projeto.coordenacao}"/></i>
	                    </td>
	                    <td> ${projeto.tipoProjetoEnsino.sigla} </td> 
	                    <td> ${projeto.projetoAssociado ? 'PROJETO ASSOCIADO' : 'PROJETO ISOLADO'}</td>	                    
						<td> ${projeto.situacaoProjeto.descricao} </td>
	              </tr>
	          </c:forEach>
		 	</tbody>
		 	
			<tfoot>
				<tr>
					<td colspan="6" style="text-align: center;">
						<h:commandButton value="Alterar Status" action="#{alterarStatusProjetoMonitoriaMBean.iniciarAlteracao}" />
			    	</td>
			    </tr>
			</tfoot>
		 	
		 </table>
	</h:form>

	</c:if>

</f:view>

<script type="text/javascript">
	
	function selecionarTodos(){
		var todosSelecionados = document.getElementsByClassName("chkSelecionaTodos")[0];
		var checks = document.getElementsByClassName("todosChecks");
		
		 for (i=0;i<checks.length;i++){
			 checks[i].checked = !checks[i].checked;
		 }
	}
		
</script>	

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
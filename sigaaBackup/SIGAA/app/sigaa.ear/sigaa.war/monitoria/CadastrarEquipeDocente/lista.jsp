<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h2><ufrn:subSistema /> > Cadastrar Docente</h2>

	<%@include file="/monitoria/form_busca_projeto.jsp"%>
 	

	<c:set var="projetos" value="#{projetoMonitoria.projetosLocalizados}"/>

	<c:if test="${empty projetos}">
	<center><i> Nenhum Projeto localizado </i></center>
	</c:if>


	<c:if test="${not empty projetos}">


		<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Projeto de Ensino
		    <h:graphicImage value="/img/monitoria/businessman_add.png" style="overflow: visible;" rendered="#{acesso.monitoria}"/><h:outputText rendered="#{acesso.monitoria}" value=": Adicionar Novos Docentes"/>	    	</div>
		<br/>

	<h:form id="formAdd">
		 <table class="listagem">
		    <caption>Projetos de Ensino Encontrados (${fn:length(projetos)})</caption>
	
		      <thead>
		      	<tr>
		      		<th>Ano</th>
		        	<th>Título</th>
		        	<th>Situação</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        </tr>
		 	</thead>
		 	<tbody>
			<c:set var="unidadeProjeto" value=""/>	 		 	
	       	<c:forEach items="#{projetos}" var="projeto" varStatus="status">
	
				<c:if test="${ unidadeProjeto != projeto.unidade.id }">
						<c:set var="unidadeProjeto" value="${ projeto.unidade.id }"/>
					<tr>
							<td colspan="5" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
								${ projeto.unidade.nome } / ${ projeto.unidade.sigla }
							</td>
					</tr>
					
				</c:if>

		               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

						<td> ${projeto.ano}</td>
	                    <td> ${projeto.titulo}
		                     <br/><i><h:outputText value="Coordenador(a): #{projeto.coordenacao.pessoa.nome}" rendered="#{not empty projeto.coordenacao}"/></i>
	                    </td>
						<td> ${projeto.situacaoProjeto.descricao}</td>
						<td width="2%">
								<h:commandLink  title="Visualizar Projeto de Monitoria"  action="#{ projetoMonitoria.view }" 
									immediate="true" id="btView">
								      <f:param name="id" value="#{projeto.id}"/>
								      <h:graphicImage url="/img/view.gif" />
								</h:commandLink>
						</td>		
						<td width="2%">
								<h:commandLink title="Adicionar Docente" action="#{ alterarEquipeDocente.preNovoEquipeDocente }" 
									id="btAdd" rendered="#{acesso.monitoria}">
								      <f:param name="id" value="#{projeto.id}"/>
								      <h:graphicImage url="/img/monitoria/businessman_add.png" />
								</h:commandLink>
						</td>
	
	              </tr>
	          </c:forEach>
		 	</tbody>
		 </table>
		</h:form>
		
	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
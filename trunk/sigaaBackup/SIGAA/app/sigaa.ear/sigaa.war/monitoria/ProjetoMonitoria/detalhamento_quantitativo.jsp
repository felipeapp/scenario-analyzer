<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>

	<h2> <ufrn:subSistema/> -> Lista de Projetos ->
	<c:if test="${status == null}"> Cadastrados </c:if>
	<c:if test="${status != null}"> ${status.descricao} </c:if>
	</h2>

	<h:form>
	 <table class="listagem">
	    <caption>Projeto de Monitoria Cadastrados</caption>

	      <thead>
	      	<tr>
	        	<th>Projeto</th>
	        	<th>Centro</th>
	        	<th>Descrição</th>
	        	<th>&nbsp;</th>
	        </tr>
	 	</thead>
	 	<tbody>
       	<c:forEach items="#{projetos}" var="projeto" varStatus="status">
               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
                    <td width="40%"> ${projeto.titulo} </td>
					<td align="center"> ${projeto.unidade.sigla} </td>
					<td align="center"> ${projeto.descricao} </td>
					<td>
							<h:commandLink  title="Visualizar Projeto de Monitoria" action="#{ projetoMonitoria.view }">
						    	<f:param name="id" value="#{projeto.id}"/>				    	
								<h:graphicImage url="/img/view.gif"/>
							</h:commandLink>
					</td>
              </tr>
          </c:forEach>
	 	</tbody>
	 </table>
	 </h:form>
	 
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
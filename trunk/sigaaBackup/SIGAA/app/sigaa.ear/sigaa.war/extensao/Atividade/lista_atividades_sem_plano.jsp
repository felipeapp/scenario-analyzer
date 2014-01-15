<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>                                                               
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>

	<c:if test="${!acesso.extensao}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>
	
	
	<h2><ufrn:subSistema /> > Consultar atividades sem planos ou com planos pendentes</h2>
		

 	<h:form id="form">

			<table class="formulario" width="70%">
			<caption>Busca por atividades sem planos ou com planos pendentes</caption>
			<tbody>
				
				<tr>
					<td width="1%"> <h:selectBooleanCheckbox value="#{relatoriosAtividades.checkBuscaTituloAtividade}" id="selectBuscaTitulo" styleClass="noborder"/> </td>
			    	<td width="20%"> <label for="tituloAtividade"> Título da Ação: </label> </td>
			    	<td> <h:inputText id="buscaTitulo" value="#{relatoriosAtividades.tituloAtividade}" size="55" maxlength="55" onfocus="javascript:$('form:selectBuscaTitulo').checked = true;"/></td>
			    </tr>
				
				<tr>
					<td width="1%"><h:selectBooleanCheckbox value="#{relatoriosAtividades.checkBuscaAno}" id="selectBuscaAno" /></td>
			    	<td width="20%"> <label for="anoAcao"> Ano da Ação: </label> </td>
			    	<td> <h:inputText maxlength="4" value="#{relatoriosAtividades.ano}" size="10" onchange="javascript:$('form:selectBuscaAno').checked = true;" onkeyup="return formatarInteiro(this)" onkeypress="javascript:$('form:selectBuscaAno').checked = true;" onfocus="javascript:$('form:selectBuscaAno').checked = true;"/></td>
			    </tr>			    
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
					<h:commandButton value="Buscar" action="#{ relatoriosAtividades.localizarAtividadesSemPlanoTrabalho }"/>
					<h:commandButton value="Cancelar" action="#{ relatoriosAtividades.cancelar }" onclick="#{confirm}"/>
			    	</td>
			    </tr>
			</tfoot>
			</table>

	</h:form>
	
	

	<c:if test="${ not empty relatoriosAtividades.atividadesLocalizadas}">
		
		<br/>

		<div class="infoAltRem">	    
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Detalhes da Ação	    
		</div>
		
		<br />
		
	</c:if>

	

	<c:if test="${not empty relatoriosAtividades.atividadesLocalizadas}">

		<h:form id="formLista">

			 <table class="listagem">
			    <caption>Atividades Encontrados (${ fn:length(relatoriosAtividades.atividadesLocalizadas) })</caption>
		
			      <thead>
			      	<tr>
			        	<th>Código</th>
			        	<th width="30%">Título</th>
			        	<th>Início</th>	
			        	<th>Fim</th>
			        	<th width="20%">Bolsas Solicitadas</th>
			        	<th width="20%">Bolsas Concedidas</th>
			        	<th>Voluntários</th>
			        	<th></th>
			        </tr>
			 	</thead>
			 	<tbody>	
			 	
		       	<c:forEach items="#{relatoriosAtividades.atividadesLocalizadas}" var="atividade" varStatus="status">
		              <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">		                    
		                    <td> ${atividade.codigo} </td>
		                    <td> ${atividade.titulo} </td>
		                    <td> <fmt:formatDate value="${atividade.dataInicio}" pattern="dd/MM/yyyy"/></td>
		                    <td> <fmt:formatDate value="${atividade.dataFim}" pattern="dd/MM/yyyy"/></td>
		                    <td align="center"> ${atividade.bolsasSolicitadas} </td>
		                    <td align="center"> ${atividade.bolsasConcedidas}  </td>
		                    <td align="center"> ${atividade.bolsasSolicitadas - atividade.bolsasConcedidas} </td>
		                    <td>					
								<h:commandLink title="Visualizar Detalhes da Ação" action="#{ atividadeExtensao.view }">
								    <f:param name="id" value="#{atividade.id}"/>
			                   		<h:graphicImage url="/img/view.gif"/>
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
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h:outputText value="#{selecaoDiscenteExtensao.create}"/>	
	
	<%@include file="/portais/discente/menu_discente.jsp" %>
	<h2><ufrn:subSistema /> > Visualizar Resultados de Seleção de Ações de Extensão</h2>

	<c:set var="inscricoes" value="#{selecaoDiscenteExtensao.inscricoes}"/>

	<c:if test="${empty inscricoes}">
	<center><i> Você não participou da seleção de nenhuma atividade de extensão. </i></center>
	</c:if>


	<c:if test="${not empty inscricoes}">
	
	
	<div class="infoAltRem">
	    <h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar detalhes da ação de extensão			    
	</div>
	
	<h:form>
	 <table class="listagem">
	    <caption>Inscrições Encontradas (${ fn:length(inscricoes) })</caption>

	      <thead>
	      	<tr>
	      		<th>Código</th>
	        	<th width="50%">Título</th>
				<th>Tipo</th>	        	
	        	<th>Área</th>
	        	<th>Resultado</th>
	        	<th></th>
	        </tr>
	 	</thead>
	 	<tbody>
       	<c:forEach items="#{inscricoes}" var="insc" varStatus="status">
               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

                    <td> ${insc.atividade.codigo} </td>
                    <td> ${insc.atividade.titulo} </td>
                    <td> ${insc.atividade.tipoAtividadeExtensao.descricao} </td>
                    <td> ${insc.atividade.areaTematicaPrincipal.descricao} </td>
                    <td> ${insc.situacaoDiscenteExtensao.descricao} </td>
                    <td>
						<h:commandLink  title="Ver" action="#{atividadeExtensao.view}" style="border: 0;">
					      <f:param name="id" value="#{insc.atividade.id}"/>
					      <h:graphicImage url="/img/view.gif" />
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
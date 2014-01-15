<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h:outputText value="#{selecaoDiscenteExtensao.create}"/>	
	
	<%@include file="/portais/discente/menu_discente.jsp" %>
	<h2><ufrn:subSistema /> > Visualizar Resultados de Sele��o de A��es de Extens�o</h2>

	<c:set var="inscricoes" value="#{selecaoDiscenteExtensao.inscricoes}"/>

	<c:if test="${empty inscricoes}">
	<center><i> Voc� n�o participou da sele��o de nenhuma atividade de extens�o. </i></center>
	</c:if>


	<c:if test="${not empty inscricoes}">
	
	
	<div class="infoAltRem">
	    <h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar detalhes da a��o de extens�o			    
	</div>
	
	<h:form>
	 <table class="listagem">
	    <caption>Inscri��es Encontradas (${ fn:length(inscricoes) })</caption>

	      <thead>
	      	<tr>
	      		<th>C�digo</th>
	        	<th width="50%">T�tulo</th>
				<th>Tipo</th>	        	
	        	<th>�rea</th>
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
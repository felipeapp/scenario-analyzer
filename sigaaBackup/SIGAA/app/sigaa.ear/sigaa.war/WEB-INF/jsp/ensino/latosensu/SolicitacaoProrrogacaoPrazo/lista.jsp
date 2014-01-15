<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2> <ufrn:subSistema /> > Lista de Situações de Proposta </h2>
<f:view>
<h:form id="form">
  <table class="formulario" width="75%">
	<caption>Busca Situações de Proposta de Curso de Lato Sensu</caption>
	  <tbody>
		<tr>
		  <td> 
		  	<h:selectBooleanCheckbox value="#{situacaoPropostaMBean.descricao}" id="tipoBusca" styleClass="noborder" /> 
		  </td>
		  <td>Descrição:</td>
          <td>
           <h:inputText value="#{situacaoPropostaMBean.obj.descricao}" id="descricao" size="50"
           		onclick="$('form:tipoBusca').checked = !$('form:tipoBusca').checked;"/>
          </td>
		</tr>
		<tr>
		  <td>
			<h:selectBooleanCheckbox value="#{situacaoPropostaMBean.todos}" id="tipoBusca2" styleClass="noborder"/>
		  </td>
		  <td>Todos</td>
        </tr>
      </tbody>
      <tfoot>
        <tr>
		  <td colspan="4" align="center">
			<h:commandButton value="Busca" action="#{situacaoPropostaMBean.buscar}" />
			<h:commandButton value="Cancelar" action="#{situacaoPropostaMBean.cancelar}" onclick="#{confirm}"/>
		  </td>
	    </tr>
      </tfoot>
     </table>

  <br />
  <c:if test="${not empty situacaoPropostaMBean.lista}">

	<div class="infoAltRem">
		<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Situação de Proposta 
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Situação de Proposta 	
	</div>

	<table class="listagem">
	  <caption class="listagem">Listagem das Situações de Proposta </caption>
			<thead>
					<tr>
						<td colspan="5">Descrição</td>
					</tr>
			</thead>
			<c:forEach items="#{situacaoPropostaMBean.lista}" var="lista" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${lista.descricao}</td>
						<td width="2%">
							<h:commandLink title="Alterar" 
								action="#{situacaoPropostaMBean.atualizar}">
								<h:graphicImage url="/img/alterar.gif" />
								<f:param name="id" value="#{lista.id}"/>
							</h:commandLink>
						</td>
						<td width="2%">						
							<h:commandLink title="Remover" action="#{situacaoPropostaMBean.remover}" onclick="#{confirmDelete}">
								<h:graphicImage url="/img/delete.gif"/>
								<f:param name="id" value="#{lista.id}"/>
							</h:commandLink>
						</td>
					</tr>
		    </c:forEach>							
	</table>
  </c:if>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
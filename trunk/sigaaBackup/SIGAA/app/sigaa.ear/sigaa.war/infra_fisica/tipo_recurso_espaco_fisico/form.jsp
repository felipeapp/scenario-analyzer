<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema/> > Cadastro de Tipo de Recurso para Espaço Físico</h2>
<h:form>
   <table class="formulario" width="60%">
	<caption> Cadastro de Tipo de Recurso para Espaço Físico</caption>

    <tr>
	  <th class="obrigatorio">Recurso:</th>
		<td align="left">
			<h:inputText value="#{tipoRecursoEspacoFisicoMBean.obj.denominacao}" maxlength="60" 
				readonly="#{tipoRecursoEspacoFisicoMBean.readOnly}" disabled="#{tipoRecursoEspacoFisicoMBean.readOnly}" id="nome"/>
			<h:inputHidden value="#{tipoRecursoEspacoFisicoMBean.confirmButton}" id="confirmButton" />
			<h:inputHidden value="#{tipoRecursoEspacoFisicoMBean.obj.id}" id="objid" />
		</td>
	</tr>
	<tr>
		<th>Ativo:</th>
		<td> 
			<h:selectBooleanCheckbox value="#{ tipoRecursoEspacoFisicoMBean.obj.ativo }"  readonly="#{tipoRecursoEspacoFisicoMBean.readOnly}" disabled="#{tipoRecursoEspacoFisicoMBean.readOnly}" />
		</td>
	</tr>

	<tfoot>
	<tr>
		<td colspan="2">
			<h:inputHidden value="#{tipoRecursoEspacoFisicoMBean.obj.id}"/>
			<h:commandButton value="#{tipoRecursoEspacoFisicoMBean.confirmButton}" action="#{tipoRecursoEspacoFisicoMBean.cadastrar}"/>
		<c:if test="${tipoRecursoEspacoFisicoMBean.obj.id > 0}">
			<h:commandButton value="<< Voltar" action="#{tipoRecursoEspacoFisicoMBean.listar}" immediate="true"/>
		</c:if>
			<h:commandButton value="Cancelar" action="#{tipoRecursoEspacoFisicoMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
			</td>
		</tr>
	</tfoot>
</table>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
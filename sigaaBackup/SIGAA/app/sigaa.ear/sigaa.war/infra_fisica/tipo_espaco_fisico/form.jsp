<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema/> > Tipo de Espaço Físico</h2>

<h:form>
  <table class="formulario" width="60%">
	<caption> Tipo de Espaço Físico</caption>
		<tr>
			<th class="required"> Denominação: </th>
			<td> 
				<h:inputText value="#{ tipoEspacoFisicoMBean.obj.denominacao }" readonly="#{tipoEspacoFisicoMBean.readOnly}"
				  disabled="#{tipoEspacoFisicoMBean.readOnly}" style="width: 300px;"/>
			</td>
		</tr>
		<tr>
			<th> Reservável: </th>
			<td> 
			   <h:selectOneRadio value="#{tipoEspacoFisicoMBean.obj.reservavel}" disabled="#{tipoEspacoFisicoMBean.readOnly}" id="reserva" >
				 <f:selectItem itemLabel="Sim" itemValue="true" />
				 <f:selectItem itemLabel="Não" itemValue="false" />
			   </h:selectOneRadio>
		</td>
		</tr>
		<tr>
			<th> Espaço Aulas: </th>
			<td> 
			   <h:selectOneRadio value="#{tipoEspacoFisicoMBean.obj.espacoAulas}" disabled="#{tipoEspacoFisicoMBean.readOnly}" id="espaco_aula" >
 				 <f:selectItem itemLabel="Sim" itemValue="true" />
  				 <f:selectItem itemLabel="Não" itemValue="false" />
			   </h:selectOneRadio>
		</td>
		</tr>
	  
	  <tfoot>
		<tr>
			<td colspan="2">
				<h:inputHidden value="#{tipoEspacoFisicoMBean.obj.id}"/>
				<h:commandButton value="#{tipoEspacoFisicoMBean.confirmButton}" action="#{tipoEspacoFisicoMBean.cadastrar}"/>
				<h:commandButton value="Cancelar" action="#{tipoEspacoFisicoMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
			</td>
	   </tr>
	 </tfoot>
  </table>
  
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
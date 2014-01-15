<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema /> > Cadastro de Tipo de Membro do Colegiado</h2>
<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <h:commandLink action="#{tipoMembroColegiado.listar}" value="Listar Tipo de Membro do Colegiado Cadastrados"/>
	  </div>
 </h:form>
 <h:form> 
<table class="formulario" width="50%">
<caption class="listagem">Cadastro de Tipo de Membro do Colegiado</caption>	<h:inputHidden value="#{tipoMembroColegiado.confirmButton}"/> <h:inputHidden value="#{tipoMembroColegiado.obj.id}"/>
<tr>
	<th class="required"> Descrição:</th>
	<td> <h:inputText value="#{tipoMembroColegiado.obj.descricao}" size="70" maxlength="200" readonly="#{tipoMembroColegiado.readOnly}"/></td>
</tr>
<tfoot><tr><td colspan=2>
<h:commandButton value="#{tipoMembroColegiado.confirmButton}" action="#{tipoMembroColegiado.cadastrar}" />
<h:commandButton value="Cancelar" action="#{tipoMembroColegiado.cancelar}" onclick="#{confirm}" immediate="true"/></td>
</tr></tfoot>
</table>
</h:form>
<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	 <br />
	 <center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	 </center>
	 <br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

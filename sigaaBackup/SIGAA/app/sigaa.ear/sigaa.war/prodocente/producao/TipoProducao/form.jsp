<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<f:subview id="menu">
		<%@include file="/portais/docente/menu_docente.jsp"%>
	</f:subview>
<h2>Cadastro de Tipo de Produ��o</h2>
<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <h:commandLink action="#{tipoProducao.listar}" value="Listar Tipos de Produ��o Cadastradas"/>
	  </div>
 </h:form>
<h:messages showDetail="true"></h:messages>
<h:form>
<table class=formulario width="100%">
	<caption class="listagem">Cadastro de Tipo de Produ��o</caption>
	<h:inputHidden value="#{tipoProducao.confirmButton}"/>
	<h:inputHidden value="#{tipoProducao.obj.id}"/>
	<tr>
		<th> Descri��o:</th>
		<td> <h:inputText size="85" maxlength="255" value="#{tipoProducao.obj.descricao}" readonly="#{tipoProducao.readOnly}"/></td>
	</tr>
	<tfoot>
	<tr><td colspan=2>
	<h:commandButton value="#{tipoProducao.confirmButton}" action="#{tipoProducao.cadastrar}" />
	<h:commandButton value="Cancelar" action="#{tipoProducao.cancelar}" /></td>
	</tr>
</tfoot>
</table>
</h:form>
<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	 <br />
	 <center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
	 </center>
	 <br />
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

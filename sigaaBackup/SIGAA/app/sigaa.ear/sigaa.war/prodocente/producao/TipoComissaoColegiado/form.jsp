<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<f:subview id="menu">
		<%@include file="/portais/docente/menu_docente.jsp"%>
	</f:subview>
<h2>Cadastro de Tipo de Comissão de Colegiado</h2>
<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <h:commandLink action="#{tipoComissaoColegiado.listar}" value="Listar Tipo de Comissão de Colegiado Cadastrados"/>
	  </div>
 </h:form>
 <h:form> 
<table class=formulario width="100%">
<caption class="listagem">Cadastro de Tipo de Comissão de Colegiado</caption>
<h:inputHidden value="#{tipoComissaoColegiado.confirmButton}"/>
<h:inputHidden value="#{tipoComissaoColegiado.obj.id}"/>
<tr>
	<th> Descrição:</th>
	<td> <h:inputText value="#{tipoComissaoColegiado.obj.descricao}" size="85" maxlength="255" readonly="#{tipoComissaoColegiado.readOnly}"/></td>
</tr>
<tr>
	<th> Natureza:</th>
	<td> <h:inputText value="#{tipoComissaoColegiado.obj.natureza}" size="85" maxlength="255" readonly="#{tipoComissaoColegiado.readOnly}"/></td>
</tr>
<tfoot><tr><td colspan=2>
<h:commandButton value="#{tipoComissaoColegiado.confirmButton}" action="#{tipoComissaoColegiado.cadastrar}" /> <h:commandButton value="Cancelar" action="#{tipoComissaoColegiado.cancelar}" /></td>
</tr></tfoot>
</table>
</h:form>
<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	 <br />
	 <center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	 </center>
	 <br />
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

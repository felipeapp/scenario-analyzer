<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema /> > Cadastro de Tipo de Instância</h2>
<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <h:commandLink action="#{tipoInstancia.listar}" value="Listar Tipo de Instância Cadastrada"/>
	  </div>
 </h:form>
 <h:form>
<table class=formulario width="50%">
 <caption class="listagem">Cadastro de Tipo de Instância</caption>
<h:inputHidden value="#{tipoInstancia.confirmButton}"/>
 <h:inputHidden value="#{tipoInstancia.obj.id}"/>
<tr>
	<th class="required"> Descrição:</th>
	<td> <h:inputText value="#{tipoInstancia.obj.descricao}" size="70" maxlength="200" readonly="#{tipoInstancia.readOnly}"/></td>
</tr>
<tfoot><tr><td colspan=2>
<h:commandButton value="#{tipoInstancia.confirmButton}" action="#{tipoInstancia.cadastrar}" /> 
<h:commandButton value="Cancelar" action="#{tipoInstancia.cancelar}" onclick="#{confirm}" immediate="true"/></td>
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

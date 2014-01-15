<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Cadastro de Tipo de Evento</h2>
<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <h:commandLink action="#{tipoEvento.listar}" value="Listar Tipo de Evento Cadastrados"/>
	  </div>
 </h:form>
 <h:form>
<table class=formulario width="50%">
 <caption class="listagem">Cadastro de Tipo de Evento</caption>	<h:inputHidden value="#{tipoEvento.confirmButton}"/> <h:inputHidden value="#{tipoEvento.obj.id}"/>
<tr>
	<th class="required"> Descrição:</th>
	<td> <h:inputText value="#{tipoEvento.obj.descricao}" size="70" maxlength="200" readonly="#{tipoEvento.readOnly}"/></td>
</tr>
<tfoot><tr><td colspan=2>
<h:commandButton value="#{tipoEvento.confirmButton}" action="#{tipoEvento.cadastrar}" /> 
<h:commandButton value="Cancelar" action="#{tipoEvento.cancelar}" onclick="#{confirm}" immediate="true"/></td>
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

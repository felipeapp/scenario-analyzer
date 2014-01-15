<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema /> > Cadastro de Tipo de Região</h2>

<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <h:commandLink action="#{tipoRegiao.listar}" value="Listar Tipo de Região Cadastrados"/>
	  </div>
	 </h:form>
	<h:messages showDetail="true"></h:messages>
<h:form>
<table class=formulario width="50%">
	<caption class="listagem">Cadastro de Tipo de Região</caption>
	<h:inputHidden value="#{tipoRegiao.confirmButton}"/> <h:inputHidden value="#{tipoRegiao.obj.id}"/>
	<tr>
		<th class="required"> Descrição:</th>
		<td> <h:inputText value="#{tipoRegiao.obj.descricao}" size="70" maxlength="200" readonly="#{tipoRegiao.readOnly}"/></td>
	</tr>
	<tfoot>
	<tr>
	<td colspan=2>
		<h:commandButton value="#{tipoRegiao.confirmButton}" action="#{tipoRegiao.cadastrar}" /> 
		<h:commandButton value="Cancelar" action="#{tipoRegiao.cancelar}" onclick="#{confirm}" immediate="true"/></td>
	</tr>
	</tfoot>
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

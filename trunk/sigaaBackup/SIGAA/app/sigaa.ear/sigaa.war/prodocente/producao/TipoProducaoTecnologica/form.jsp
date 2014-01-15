<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema /> > Cadastro de Tipo de Produção Tecnológica</h2>
<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <h:commandLink action="#{tipoProducaoTecnologica.listar}" value="Listar Tipos de Produção Tecnológica Cadastrados"/>
	  </div>
	 </h:form>
	<h:messages showDetail="true"></h:messages>

<h:form> 
<table class=formulario width="50%">
<caption class="listagem">Cadastro de Tipo de Produção Tecnológica</caption>	<h:inputHidden value="#{tipoProducaoTecnologica.confirmButton}"/> <h:inputHidden value="#{tipoProducaoTecnologica.obj.id}"/>
<tr>
	<th class="required"> Descrição:</th>
	<td>
	 <h:inputText value="#{tipoProducaoTecnologica.obj.descricao}" size="70" maxlength="200" readonly="#{tipoProducaoTecnologica.readOnly}"/>
	 </td>
</tr>
<tfoot>
<tr><td colspan="2">
<h:commandButton value="#{tipoProducaoTecnologica.confirmButton}" action="#{tipoProducaoTecnologica.cadastrar}" /> 
<h:commandButton value="Cancelar" action="#{tipoProducaoTecnologica.cancelar}" onclick="#{confirm}" immediate="true"/></td>
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

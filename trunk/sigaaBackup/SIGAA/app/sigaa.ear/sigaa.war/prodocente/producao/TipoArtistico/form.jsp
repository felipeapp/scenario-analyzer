<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<f:subview id="menu">
		<%@include file="/portais/docente/menu_docente.jsp"%>
	</f:subview>
<h2>Cadastro de Tipo Artístico</h2>
<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <h:commandLink action="#{tipoComissaoColegiado.listar}" value="Listar Tipo de Banca Cadastradas"/>
	  </div>
 </h:form>
 <h:form>
<table class=formulario width="90%">
 <caption class="listagem">Cadastro de Tipo Artístico</caption>	<h:inputHidden value="#{tipoArtistico.confirmButton}"/> <h:inputHidden value="#{tipoArtistico.obj.id}"/>
<tr>
	<th> Descrição:</th>
	<td> <h:inputText value="#{tipoArtistico.obj.descricao}" size="85" maxlength="255" readonly="#{tipoArtistico.readOnly}"/></td>
</tr>
<tr>
	<th> Contexto:</th>
	<td> <h:inputText value="#{tipoArtistico.obj.contexto}" size="85" maxlength="255" readonly="#{tipoArtistico.readOnly}"/></td>
</tr>
<tfoot><tr><td colspan=2>
<h:commandButton value="#{tipoArtistico.confirmButton}" action="#{tipoArtistico.cadastrar}" /> <h:commandButton value="Cancelar" action="#{tipoArtistico.cancelar}" /></td>
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

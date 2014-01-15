<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<f:subview id="menu">
 <%@include file="/portais/docente/menu_docente.jsp"%>
</f:subview>


<h2><ufrn:subSistema /> > Cadastro de Sub-Tipo Artístico</h2>

	<h:form>
		<div style="text-align:right">
		</div>
	</h:form>

   <h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/> 
		<h:commandLink action="#{subTipoArtistico.listar}"
			value="Listar Sub-Tipos Artistico Cadastrados"/>
	 </div>
    </h:form>
	
	<h:messages showDetail="true"></h:messages>	


<h:form> 
<table class="formulario" width="95%">
<caption class="listagem">Cadastro de Sub-Tipo Artístico</caption>	<h:inputHidden value="#{subTipoArtistico.confirmButton}"/> <h:inputHidden value="#{subTipoArtistico.obj.id}"/>
<tr>
	<th class="required"> Descrição:</th>
	<td> <h:inputText value="#{subTipoArtistico.obj.descricao}" size="60" maxlength="255" readonly="#{subTipoArtistico.readOnly}"/></td>
</tr>
<tr>
	<th> Tipo de Produção :</th>
	<td>
	<h:selectOneMenu value="#{subTipoArtistico.obj.tipoProducao.id}" disabled="#{subTipoArtistico.readOnly}" disabledClass="#{subTipoArtistico.disableClass}">
		<f:selectItem itemValue="0" itemLabel="--SELECIONE--"/>
		<f:selectItems value="#{tipoProducao.allCombo}"/>
	</h:selectOneMenu>
	</td>
</tr>
<tfoot><tr><td colspan=2>
<h:commandButton value="#{subTipoArtistico.confirmButton}" action="#{subTipoArtistico.cadastrar}" /> 
<h:commandButton value="Cancelar" action="#{subTipoArtistico.cancelar}" onclick="#{confirm}" immediate="true"/></td>
</tr></tfoot>
</table>
</h:form>
<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

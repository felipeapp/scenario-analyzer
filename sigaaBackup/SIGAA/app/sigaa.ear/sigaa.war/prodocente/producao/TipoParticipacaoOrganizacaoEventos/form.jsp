<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema /> > Cadastro de Tipo de Participação de Organização em Eventos</h2>
<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <h:commandLink action="#{tipoParticipacaoOrganizacaoEventos.listar}" value="Listar Tipo de Participação de Organização em Eventos Cadastrados"/>
	  </div>
 </h:form>
 <h:form> 
<table class=formulario width="50%">
<caption class="listagem">Cadastro de Tipo de Participação de Organização em Eventos</caption>	<h:inputHidden value="#{tipoParticipacaoOrganizacaoEventos.confirmButton}"/> <h:inputHidden value="#{tipoParticipacaoOrganizacaoEventos.obj.id}"/>
<tr>
	<th class="required"> Descrição:</th>
	<td> <h:inputText value="#{tipoParticipacaoOrganizacaoEventos.obj.descricao}" size="85" maxlength="255" readonly="#{tipoParticipacaoOrganizacaoEventos.readOnly}"/></td>
</tr>
<tfoot>
	<tr>
		<td colspan=2>
			<h:commandButton value="#{tipoParticipacaoOrganizacaoEventos.confirmButton}" action="#{tipoParticipacaoOrganizacaoEventos.cadastrar}" /> 
			<h:commandButton value="Cancelar" action="#{tipoParticipacaoOrganizacaoEventos.cancelar}" onclick="#{confirm}" immediate="true"/>
		</td>
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

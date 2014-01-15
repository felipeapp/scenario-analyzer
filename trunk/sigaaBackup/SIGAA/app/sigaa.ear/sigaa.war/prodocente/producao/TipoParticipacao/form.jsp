<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema /> > Cadastro de Tipo de Participação</h2>
<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <h:commandLink action="#{tipoParticipacao.listar}" value="Listar Tipo de Participação Cadastrados"/>
	  </div>
 </h:form>
 <h:form>
<table class=formulario width="70%">
 <caption class="listagem">Cadastro de Tipo de Participação</caption>	<h:inputHidden value="#{tipoParticipacao.confirmButton}"/>
 <h:inputHidden value="#{tipoParticipacao.obj.id}"/>
	<tr>
		<th class="required"> Tipo de Produção:</th>
		<td>
		<h:selectOneMenu value="#{tipoParticipacao.obj.tipoProducao.id}" style="width:380px" disabled="#{tipoParticipacao.readOnly}" 
			disabledClass="#{tipoParticipacao.disableClass}" readonly="#{tipoParticipacao.readOnly}">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--"/>
				<f:selectItems value="#{tipoProducao.allCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
			<th class="required"> Descrição:</th>
			<td> <h:inputText value="#{tipoParticipacao.obj.descricao}" size="60" maxlength="255" readonly="#{tipoParticipacao.readOnly}"/></td>
	</tr>
  <tfoot>
	<tr>
		<td colspan=2>
			<h:commandButton value="#{tipoParticipacao.confirmButton}" action="#{tipoParticipacao.cadastrar}" />
			<h:commandButton value="Cancelar" action="#{tipoParticipacao.cancelar}" onclick="#{confirm}" immediate="true"/>
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

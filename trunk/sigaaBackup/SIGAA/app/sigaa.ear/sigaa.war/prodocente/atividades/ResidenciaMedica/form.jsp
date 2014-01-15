<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Residência Médica</h2>

	<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
		<h:commandLink action="#{residenciaMedica.listar}" value="Listar Residências Médicas Cadastradas"/>
	 </div>
    </h:form>

	<h:messages showDetail="true"></h:messages>


	<h:form id="form">
	<table class=formulario width="100%">
			<caption class="listagem">Cadastro de Residência Médica</caption>
			<h:inputHidden value="#{residenciaMedica.confirmButton}" />
			<h:inputHidden value="#{residenciaMedica.obj.id}" />

			<tr>
				<th class="required">Programa:</th>
				<td>
					<h:selectOneMenu value="#{residenciaMedica.obj.programaResidenciaMedica.id}" style="width: 80%"
						disabled="#{residenciaMedica.readOnly}" disabledClass="#{residenciaMedica.disableClass}" id="programaResidencia">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE UM PROGRAMA DE RESIDÊNCIA MÉDICA --- " />
						<f:selectItems value="#{programaResidencia.allCombo}" />
					</h:selectOneMenu>
				 </td>
			</tr>
			<tr>
				<th class="required">Ano/Período:</th>
				<td>
					<h:inputText value="#{residenciaMedica.obj.ano}" size="5" maxlength="4" readonly="#{residenciaMedica.readOnly}" id="ano" onkeypress="return ApenasNumeros(event);"  />
					 . <h:inputText value="#{residenciaMedica.obj.semestre}" size="2" maxlength="1" readonly="#{residenciaMedica.readOnly}" id="semestre" onkeypress="return ApenasNumeros(event);" />
				</td>
			</tr>
			<tr>
				<th class="required">Carga Horária Semanal Dispendida:</th>
				<td>
					<h:inputText value="#{residenciaMedica.obj.chSemanal}" size="3" maxlength="2" readonly="#{residenciaMedica.readOnly}" id="chSemanal" style="text-align: right;" onkeypress="return ApenasNumeros(event);" /> horas
				</td>
			</tr>
			<tr>
				<th>Observações:</th>
				<td>
					<h:inputTextarea value="#{residenciaMedica.obj.observacoes}" cols="80" rows="3"
						readonly="#{residenciaMedica.readOnly}" id="observacoes" />
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
					<h:commandButton id="btConfirmarAcao"
						value="#{residenciaMedica.confirmButton}" action="#{residenciaMedica.cadastrar}" />
					<h:commandButton value="Cancelar" id="btCancelar" action="#{residenciaMedica.cancelar}" onclick="#{confirm}" immediate="true" />
				</tr>
			</tfoot>
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
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Eleição</h2>

	<center>
			<h:messages showDetail="true"/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif" style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{eleicao.listar}"/>
			</div>
			</h:form>
	</center>


	<div id="panel">
	<div id="cadastrar">

	<table class="formulario" width="75%">
		<h:form>
			<caption class="listagem">Cadastro de Eleição</caption>
			<h:inputHidden value="#{eleicao.confirmButton}" />
			<h:inputHidden value="#{eleicao.obj.id}" />
			<tr>
				<th>Título:<span class="required">&nbsp;</span></th>
				<td><h:inputText size="30" id="txtTitulo"
					value="#{eleicao.obj.titulo}"
					readonly="#{eleicao.readOnly}" />
				</td>
			</tr>
			<tr>
				<th>Descricão:<span class="required">&nbsp;</span></th>
				<td><h:inputText size="60" maxlength="255" id="txtDescricao"
					value="#{eleicao.obj.descricao}"
					readonly="#{eleicao.readOnly}" />
				</td>
			</tr>
			<tr>
				<th>Centro:<span class="required">&nbsp;</span></th>
				<td>
					<h:selectOneMenu id="centro" value="#{eleicao.obj.centro.id}" style="width: 500px">
							<f:selectItem itemValue="0" itemLabel=" >> SELECIONE UM CENTRO << " />
							<f:selectItems value="#{unidade.allCentroCombo}"/>
						</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>Data de Inicio:<span class="required">&nbsp;</span></th>
				<td><t:inputCalendar value="#{eleicao.obj.dataInicio}"
					size="10" maxlength="10" readonly="#{eleicao.readOnly}" popupDateFormat="dd/MM/yyyy"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataInicio">
					<f:converter converterId="convertData"/> </t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th>Hora de Inicio:<span class="required">&nbsp;</span></th>
				<td>
					<h:inputText id="txtHoraInicio" value="#{eleicao.obj.horaInicio}" size="5" maxlength="5"
						readonly="#{eleicao.readOnly}"
						onkeypress="return(formataHora(this, event))">
						<f:convertDateTime pattern="HH:mm" />
					</h:inputText>
				</td>
			</tr>
			<tr>
				<th>Data de Fim:<span class="required">&nbsp;</span></th>
				<td><t:inputCalendar value="#{eleicao.obj.dataFim}"
					size="10" maxlength="10" readonly="#{eleicao.readOnly}" popupDateFormat="dd/MM/yyyy"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataFim">
					<f:converter converterId="convertData"/> </t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th>Hora de Fim:<span class="required">&nbsp;</span></th>
				<td>
					<h:inputText id="txtHoraFim" value="#{eleicao.obj.horaFim}" size="5" maxlength="5"
						readonly="#{eleicao.readOnly}"
						onkeypress="return(formataHora(this, event))">
						<f:convertDateTime pattern="HH:mm" />
					</h:inputText>
				</td>
			</tr>			
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{eleicao.confirmButton}"
						action="#{eleicao.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{eleicao.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	</div>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
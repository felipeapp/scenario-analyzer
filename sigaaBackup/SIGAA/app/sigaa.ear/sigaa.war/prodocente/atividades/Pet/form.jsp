<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2> <ufrn:subSistema/> > PET</h2>

	<h:messages showDetail="true" />

	<h:form>
	 <div class="infoAltRem" style="width: 95%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <h:commandLink action="#{petBean.listar}" value="Listar PETs Cadastrados"/>
	 </div>
    </h:form>
	<h:form id="form">

	<table class="formulario" width="95%">
			<caption class="listagem">Cadastro de PET</caption>
			<h:inputHidden value="#{petBean.confirmButton}" />
			<h:inputHidden value="#{petBean.obj.id}" />
			<tr>
				<th  width="30%" class="required">Descrição do Grupo:</th>
				<td>
					<h:inputText value="#{petBean.obj.descricao}" maxlength="200" readonly="#{petBean.readOnly}" id="descrica" style="width: 95%;"/>
				</td>
			</tr>
			<tr>
				<th>Curso:</th>
				<td>
					<h:selectOneMenu value="#{petBean.obj.idCurso}" 
						style="width: 95%" disabled="#{petBean.readOnly}" disabledClass="#{petBean.disableClass}"
						id="curso">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{curso.allCursoGraduacaoCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Área de Conhecimento:</th>
				<td>
					<h:selectOneMenu value="#{petBean.obj.idAreaConhecimentoCnpq}" 
						style="width: 95%" disabled="#{petBean.readOnly}" disabledClass="#{petBean.disableClass}"
						id="areaConhecimento">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{area.allAreasCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Data de Início:</th>
				<td><t:inputCalendar value="#{petBean.obj.dataInicio}" size="10" maxlength="10" 
					disabled="#{petBean.readOnly}" popupDateFormat="dd/MM/yyyy"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataInicio">
					<f:converter converterId="convertData"/> </t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th>Data de Término:</th>
				<td><t:inputCalendar value="#{petBean.obj.dataFim}" size="10" maxlength="10" 
					disabled="#{petBean.readOnly}" popupDateFormat="dd/MM/yyyy"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataFim">
					<f:converter converterId="convertData"/> </t:inputCalendar>
				</td>
			</tr>

			<tr>
				<th class="required">Limite de bolsas para o grupo:</th>
				<td>
					<h:inputText value="#{petBean.obj.limiteBolsas}" maxlength="4" readonly="#{petBean.readOnly}" converter="#{intConverter}" 
						id="limiteBolsas" onkeyup="return formatarInteiro(this);" size="10"/>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{petBean.confirmButton}" action="#{petBean.cadastrar}" id="btnCadastrar"/> 
						<h:commandButton value="Cancelar" action="#{petBean.cancelar}" onclick="#{confirm}" immediate="true" id="btnCancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<br>
	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

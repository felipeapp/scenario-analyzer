<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="cotaBolsasMBean"></a4j:keepAlive>
	<h2><ufrn:subSistema /> &gt; Cota de Bolsas</h2>

	<center>
		<div class="infoAltRem" style="text-align: left; width: 90%">
			<h:form id="infoAltRem" style="text-align: center; width: 90%;">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar Cotas de Bolsa Cadastradas" action="#{cotaBolsasMBean.listar}"/>
			</h:form>	
		</div>
	</center>

	<table class=formulario width="90%">
		<h:form id="form">
			<caption class="listagem">Cadastro de Cota de Bolsas</caption>
			<h:inputHidden value="#{cotaBolsasMBean.confirmButton}" />
			<h:inputHidden value="#{cotaBolsasMBean.obj.id}" />
			<tr>
				<th>C�digo:</th>
				<td><h:inputText id="codigo" size="20" maxlength="30"
					readonly="#{cotaBolsasMBean.readOnly}"  value="#{cotaBolsasMBean.obj.codigo}" /></td>
			</tr>
			<tr>
				<th class="obrigatorio">Descri��o:</th>
				<td><h:inputText id="descricao" size="50" maxlength="50"
					readonly="#{cotaBolsasMBean.readOnly}"  value="#{cotaBolsasMBean.obj.descricao}" /></td>
			</tr>
			<tr>
				<th class="obrigatorio">Per�odo de Validade:</th>
				<td> de
					<t:inputCalendar value="#{cotaBolsasMBean.obj.inicio}" size="10" maxlength="10" 
					disabled="#{cotaBolsasMBean.readOnly}" popupDateFormat="dd/MM/yyyy"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="inicioSubmissao">
					<f:converter converterId="convertData"/> </t:inputCalendar> at� 
					<t:inputCalendar value="#{cotaBolsasMBean.obj.fim}" size="10" maxlength="10" 
					disabled="#{cotaBolsasMBean.readOnly}" popupDateFormat="dd/MM/yyyy"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="fimSubmissao">
					<f:converter converterId="convertData"/> </t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">�rg�o Financiador:</th>
				<td>
					<h:selectOneMenu id="entidadeFinanciadora" value="#{cotaBolsasMBean.obj.entidadeFinanciadora.id}" onchange="submit()" 
							readonly="#{cotaBolsasMBean.readOnly}" valueChangeListener="#{cotaBolsasMBean.changeOrgaoFinanciador}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{entidadeFinanciadora.allCombo}"/>
						<a4j:support event="onchange" reRender="modalidades"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<a4j:region rendered="#{ cotaBolsasMBean.exibeModalidade }">
				<tr>
					<th class="obrigatorio">Modalidades:</th>
					<td>
						<h:selectManyCheckbox id="modalidades" value="#{cotaBolsasMBean.selectedItems}" layout="pageDirection" immediate="true">
							<f:selectItems value="#{cotaBolsasMBean.modalidadesCombo}"/>
						</h:selectManyCheckbox>
					</td>
				</tr>
			</a4j:region>
			<tr>
				<th width="50%">Per�odo de Envio de Relat�rios Parciais:</th>
				<td > de <t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
					value="#{cotaBolsasMBean.obj.inicioEnvioRelatorioParcial}" id="inicioEnvioRelatorioParcialBolsa"  readonly="#{cotaBolsasMBean.readOnly}"/> at� <t:inputCalendar
					renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
					value="#{cotaBolsasMBean.obj.fimEnvioRelatorioParcial}" id="fimEnvioRelatorioParcialBolsa" readonly="#{cotaBolsasMBean.readOnly}"/></td>
			</tr>
			<tr>
				<th>Per�odo de Envio de Relat�rios Finais:</th>
				<td> de <t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
					value="#{cotaBolsasMBean.obj.inicioEnvioRelatorioFinal}" id="inicioRelatorioFinalBolsa" readonly="#{cotaBolsasMBean.readOnly}"/> at� <t:inputCalendar
					renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
					value="#{cotaBolsasMBean.obj.fimEnvioRelatorioFinal}" id="fimRelatorioFinalBolsa" readonly="#{cotaBolsasMBean.readOnly}"/></td>
			</tr>
			<tr>
				<th width="50%">Per�odo de cadastro de plano Volunt�rio:</th>
				<td > de <t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
					value="#{cotaBolsasMBean.obj.inicioCadastroPlanoVoluntario}" id="inicioCadastroPlanoVoluntario"  readonly="#{cotaBolsasMBean.readOnly}"/> at� <t:inputCalendar
					renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))"
					value="#{cotaBolsasMBean.obj.fimCadastroPlanoVoluntario}" id="fimCadastroPlanoVoluntario" readonly="#{cotaBolsasMBean.readOnly}"/></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton id="btnCadastrar"
						value="#{cotaBolsasMBean.confirmButton}"
						action="#{cotaBolsasMBean.cadastrar}" /> <h:commandButton id="btnCancelar" immediate="true"
						value="Cancelar" onclick="#{confirm}" action="#{cotaBolsasMBean.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br>
	<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

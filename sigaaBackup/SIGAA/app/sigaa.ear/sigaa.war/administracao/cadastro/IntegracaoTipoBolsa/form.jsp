<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Cadastro do tipo de bolsa</h2>

	<h:form id="form">
		<table class=formulario width="100%">
			<caption class="listagem">Cadastro de Modalidade de Educação</caption>
			<tr>
				<th width="50%" class="obrigatorio">Bolsa ${ configSistema['nomeSigaa'] }:</th>
				<td>
					<h:selectOneMenu value="#{integracaoTipoBolsaMBean.obj.idBolsaSigaa}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItems value="#{tipoBolsaAuxilioMBean.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Bolsa ${ configSistema['nomeSipac'] }:</th>
				<td>
					<h:selectOneMenu value="#{integracaoTipoBolsaMBean.obj.idBolsaSipac}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItems value="#{relatoriosSaeMBean.allBolsaCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Unidade Federativa:</th>
				<td>
					<h:selectOneMenu value="#{integracaoTipoBolsaMBean.obj.uf}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="TODOS" />
						<f:selectItems value="#{integracaoTipoBolsaMBean.allUfCombo}" />
						<a4j:support event="onchange" reRender="municipio" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>Município:</th>
				<td>
					<h:selectOneMenu value="#{integracaoTipoBolsaMBean.obj.municipio}" id="municipio">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="TODOS" />
						<f:selectItems value="#{integracaoTipoBolsaMBean.allMunicipiosUf}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2>
						<h:commandButton value="#{integracaoTipoBolsaMBean.confirmButton}" id="cadastrar" action="#{integracaoTipoBolsaMBean.cadastrar}" /> 
						<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}" action="#{integracaoTipoBolsaMBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

 <br />
 
 <center>
 	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
 	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
 </center>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; <h:outputText value="#{tipoBolsaPesquisa.confirmButton}"/> Tipo de Bolsa de Pesquisa</h2>

	<center>
			<h:form id="formLegenda">
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar Tipos de Bolsa Cadastrados" action="#{tipoBolsaPesquisa.listar}"/>
			</div>
			</h:form>
	</center>

	<table class=formulario width="100%">
		<h:form id="formTipoBolsaPesquisa">
			<caption class="listagem">Cadastro de Tipo de Bolsa de Pesquisa</caption>
			<h:inputHidden value="#{tipoBolsaPesquisa.confirmButton}" />
			<h:inputHidden value="#{tipoBolsaPesquisa.obj.id}" />
			<tr>
				<th class="obrigatorio">Descrição:</th>
				<td><h:inputText id="descricao" size="30" maxlength="30" 
					readonly="#{tipoBolsaPesquisa.readOnly}"  value="#{tipoBolsaPesquisa.obj.descricao}" /></td>
			</tr>
			<tr>
				<th class="obrigatorio">Categoria:</th>
				<td>
					<h:selectOneMenu id="categoria" value="#{tipoBolsaPesquisa.obj.categoria}" readonly="#{tipoBolsaPesquisa.readOnly}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{tipoBolsaPesquisa.categoriasCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Órgão Financiador:</th>
				<td>
					<h:selectOneMenu id="entidadeFinanciadora" value="#{tipoBolsaPesquisa.obj.entidadeFinanciadora.id}" readonly="#{tipoBolsaPesquisa.readOnly}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{entidadeFinanciadora.allCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>Dia Limite para a Indicação: <ufrn:help>Dia Limite para que o discente possa entrar na Folha de pagamento do mês corrente.</ufrn:help></th>
				<td><h:inputText id="dataLimite" size="2" maxlength="2" 
					readonly="#{tipoBolsaPesquisa.readOnly}"  value="#{tipoBolsaPesquisa.obj.diaLimiteIndicacao}" />
				</td>
			</tr>
			<tr>
				<th>Dia Limite para a Finalização: <ufrn:help>Dia Limite para que o discente não entre na Folha de pagamento do mês corrente.</ufrn:help></th>
				<td><h:inputText id="dataLimiteFinalizacao" size="2" maxlength="2" 
					readonly="#{tipoBolsaPesquisa.readOnly}"  value="#{tipoBolsaPesquisa.obj.diaLimiteFinalizacao}" />
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Níveis Permitidos: <ufrn:help>Restringe quais alunos podem ser indicados para este tipo de bolsa.</ufrn:help> </th>
				<td>
					<h:selectManyCheckbox id="cbx_nivel_medio" value="#{tipoBolsaPesquisa.tipoNiveis}" layout="pageDirection"  >
						<f:selectItems id="niveis_permissao" value="#{nivelEnsino.allCombo}" />
					</h:selectManyCheckbox>
				</td>
			</tr>
			<tr>
				<th>Vinculado a cota? <ufrn:help>Indica se os planos de trabalho deste
				tipo de bolsa devem ser vinculados a uma cota de bolsas ou se podem
				ser cadastrados em fluxo contínuo.</ufrn:help> </th>
				<td>
					<h:selectOneRadio id="vinculadoCota" value="#{tipoBolsaPesquisa.obj.vinculadoCota}" readonly="#{tipoBolsaPesquisa.readOnly}">
						<f:selectItems value="#{tipoBolsaPesquisa.simNao}"/>
					</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<th>Necessita Envio de Relatórios? <ufrn:help>Indica se os planos de trabalho deste
				tipo de bolsa devem precisam enviar relatórios parcial e final.</ufrn:help></th>
				<td>
					<h:selectOneRadio id="necessitaRelatorio" value="#{tipoBolsaPesquisa.obj.necessitaRelatorio}" readonly="#{tipoBolsaPesquisa.readOnly}">
						<f:selectItems value="#{tipoBolsaPesquisa.simNao}"/>
					</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario"> Parâmetros do Envio de Relatórios </td>
			</tr>
			<tr>
				<th width="50%">Período de Envio de Relatórios Parciais:</th>
				<td> de 
	    			 <t:inputCalendar value="#{tipoBolsaPesquisa.obj.inicioEnvioRelatorioParcial}" id="inicioEnvioRelatorioParcialBolsa" size="10" maxlength="10" 
	    				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
	    				renderAsPopup="true" renderPopupButtonAsImage="true" readonly="#{tipoBolsaPesquisa.readOnly}">
	      				<f:converter converterId="convertData"/>
					</t:inputCalendar> até 

	    			<t:inputCalendar value="#{tipoBolsaPesquisa.obj.fimEnvioRelatorioParcial}" id="fimEnvioRelatorioParcialBolsa" size="10" maxlength="10" 
	    				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
	    				renderAsPopup="true" renderPopupButtonAsImage="true" readonly="#{tipoBolsaPesquisa.readOnly}">
	      				<f:converter converterId="convertData"/>
					</t:inputCalendar> 
				
				</td>
			</tr>
			<tr>
				<th>Período de Envio de Relatórios Finais:</th>
				<td> de 
	    			<t:inputCalendar value="#{tipoBolsaPesquisa.obj.inicioEnvioRelatorioFinal}" id="inicioRelatorioFinalBolsa" size="10" maxlength="10" 
	    				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
	    				renderAsPopup="true" renderPopupButtonAsImage="true" readonly="#{tipoBolsaPesquisa.readOnly}">
	      				<f:converter converterId="convertData"/>
					</t:inputCalendar> até 

	    			<t:inputCalendar value="#{tipoBolsaPesquisa.obj.fimEnvioRelatorioFinal}" id="fimRelatorioFinalBolsa" size="10" maxlength="10" 
	    				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
	    				renderAsPopup="true" renderPopupButtonAsImage="true" readonly="#{tipoBolsaPesquisa.readOnly}">
	      				<f:converter converterId="convertData"/>
					</t:inputCalendar> 
			</tr>
			<tr>
				<th>Período de Envio de Resumos para Congresso de Iniciação Científica:</th>
				<td> de 
	    			<t:inputCalendar value="#{tipoBolsaPesquisa.obj.inicioEnvioResumoCongresso}" id="inicioResumoCongresso" size="10" maxlength="10" 
	    				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
	    				renderAsPopup="true" renderPopupButtonAsImage="true" readonly="#{tipoBolsaPesquisa.readOnly}">
	      				<f:converter converterId="convertData"/>
					</t:inputCalendar> até 

	    			<t:inputCalendar value="#{tipoBolsaPesquisa.obj.fimEnvioResumoCongresso}" id="fimResumoCongresso" size="10" maxlength="10" 
	    				onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
	    				renderAsPopup="true" renderPopupButtonAsImage="true" readonly="#{tipoBolsaPesquisa.readOnly}">
	      				<f:converter converterId="convertData"/>
					</t:inputCalendar> 
				</td>					
			</tr>
			<c:if test="${not tipoBolsaPesquisa.obj.ativo}">
				<tr>
					<th>Ativo:</th>
					<td>
						<h:selectBooleanCheckbox id="ativo" value="#{tipoBolsaPesquisa.obj.ativo}" disabled="#{tipoBolsaPesquisa.readOnly}"/>
					</td>
				</tr>
			</c:if>
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton id="btnCadastrar"
						value="#{tipoBolsaPesquisa.confirmButton}"
						action="#{tipoBolsaPesquisa.cadastrar}" /> <h:commandButton id="btnCancelar"
						value="Cancelar" onclick="#{confirm}" action="#{tipoBolsaPesquisa.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br>
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

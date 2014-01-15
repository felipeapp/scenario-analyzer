<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="congressoIniciacaoCientifica" />
	<h2><ufrn:subSistema /> &gt; Congresso de Inicia��o Cient�fica</h2>

	<div id="ajuda" class="descricaoOperacao" style="text-align: justify;">
		� poss�vel adicionar restri��es para a submiss�o dos resumos do congresso de Inicia��o Cient�fica, a fim de aceitar resumos para os tipos de bolsas que iniciaram e finalizaram no per�odo informado,  
			caso n�o seja adicionada nenhuma restri��o todos os resumos ser�o aceitos, para o CIC em quest�o. <br />
	</div>

	<center>
		<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
	  			<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Restri��o
			</div>
		</h:form>
	</center>

	<table class=formulario width="80%">
		<h:form>
			<caption class="listagem">Cadastro de Congresso de Inicia��o Cient�fica</caption>
			<h:inputHidden value="#{congressoIniciacaoCientifica.confirmButton}" />
			<h:inputHidden value="#{congressoIniciacaoCientifica.obj.id}" />
			<tr>
				<th width="20%" class="obrigatorio">Edi��o:</th>
				<td><h:inputText size="10" maxlength="10"
					readonly="#{congressoIniciacaoCientifica.readOnly}"  value="#{congressoIniciacaoCientifica.obj.edicao}" /></td>
			</tr>
			<tr>
				<th class="obrigatorio">Ano:</th>
				<td>
					<h:inputText size="4" maxlength="4" readonly="#{congressoIniciacaoCientifica.readOnly}" value="#{congressoIniciacaoCientifica.obj.ano}"
						onkeyup="formatarInteiro(this);"/>
				</td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Per�odo:</th>
				<td>
					<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" 
						value="#{congressoIniciacaoCientifica.obj.inicio}" popupDateFormat="dd/MM/yyyy" size="10" maxlength="10"
						onkeypress="return(formatarMascara(this,event,'##/##/####'))" readonly="#{congressoIniciacaoCientifica.readOnly}"/>
					 a 
					<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" 
						value="#{congressoIniciacaoCientifica.obj.fim}" popupDateFormat="dd/MM/yyyy" size="10" maxlength="10"
						onkeypress="return(formatarMascara(this,event,'##/##/####'))" readonly="#{congressoIniciacaoCientifica.readOnly}"/>
				</td>
			</tr>
						
			<c:if test="${congressoIniciacaoCientifica.obj.id > 0}">
				<tr>
					<th>Ativo:</th>
					<td>
						<h:selectBooleanCheckbox value="#{congressoIniciacaoCientifica.obj.ativo}" disabled="#{congressoIniciacaoCientifica.readOnly}"/>
					</td>
				</tr>
			</c:if>
			
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">
						<caption>Restri��es envio Resumo CIC</caption>

						<tr>
							<th width="20%"> Tipo de Restri��o: </th>
							<td>
								<h:selectOneRadio id="restricao" value="#{ congressoIniciacaoCientifica.restricao.tipoRestricao }" onchange="submit();">
									<f:selectItem itemValue="1" itemLabel="Cota" />
									<f:selectItem itemValue="2" itemLabel="Bolsa de Pesquisa" />
								</h:selectOneRadio>
							</td>
						</tr>

						<c:choose>
							
							<c:when test="${ congressoIniciacaoCientifica.restricao.cota }">
								<tr>
									<th  class="obrigatorio" width="20%"> Cota: </th>
									<td>
										<h:selectOneMenu id="cotaBolsa" value="#{congressoIniciacaoCientifica.restricao.cotaBolsa.id}">
											<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
											<f:selectItems value="#{cotaBolsasMBean.allCombo}" />
										</h:selectOneMenu>
									</td>
								</tr>
							</c:when>
							
							<c:when test="${ not congressoIniciacaoCientifica.restricao.cota }">
								<tr>
									<th class="obrigatorio"> Bolsa de Pesquisa: </th>
									<td>
										<h:selectOneMenu id="tipoBolsaPesquisa" value="#{congressoIniciacaoCientifica.restricao.tipoBolsa.id}">
											<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
											<f:selectItems value="#{tipoBolsaPesquisa.allCombo}" />
										</h:selectOneMenu>
									</td>
								</tr>
								
								<tr>
									<th class="obrigatorio">Per�odo de Execu��o da Bolsa:</th>
									<td>
								    	 <t:inputCalendar value="#{congressoIniciacaoCientifica.restricao.dataInicial}" id="dataInicio" size="10" maxlength="10" 
					   						onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
					   						renderAsPopup="true" renderPopupButtonAsImage="true" readonly="#{congressoIniciacaoCientifica.readOnly}" >
					     						<f:converter converterId="convertData"/>
										</t:inputCalendar> 
										 a 
								    	 <t:inputCalendar value="#{congressoIniciacaoCientifica.restricao.dataFinal}" id="dataFim" size="10" maxlength="10" 
					   						onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
					   						renderAsPopup="true" renderPopupButtonAsImage="true" readonly="#{congressoIniciacaoCientifica.readOnly}" >
					     						<f:converter converterId="convertData"/>
										</t:inputCalendar> 
									</td>
								</tr>
							</c:when>							
						</c:choose>
						
						<tfoot>
							<tr>
								<td colspan="4">
									<h:commandButton value="Adicionar Restri��o" action="#{ congressoIniciacaoCientifica.adicionarRestricao }" />
								</td>
							</tr>
						</tfoot>
					</table>
				</td>
			</tr>
		
			<tr>
				<td colspan="3">

							<rich:dataTable id="_restricao" value="#{ congressoIniciacaoCientifica.obj.restricoes }" 
								var="linha" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
				
								<rich:column>
									<f:facet name="header"><f:verbatim>Cota/Bolsa de Pesquisa</f:verbatim></f:facet>
									<h:outputText value="#{ linha.cotaBolsa.descricao }" rendered="#{ not empty linha.cotaBolsa }"/>
									<h:outputText value="#{ linha.tipoBolsa.descricao }" rendered="#{ not empty linha.tipoBolsa }"/>
								</rich:column>

								<rich:column headerClass="colData">
									<f:facet name="header"><f:verbatim>Data Inicial</f:verbatim></f:facet>
									<h:outputText value="-" rendered="#{ not empty linha.cotaBolsa }"/>
									<h:outputText value="#{ linha.dataInicial }" rendered="#{ not empty linha.tipoBolsa }"/>
								</rich:column>

								<rich:column styleClass="centerAlign">
									<f:facet name="header"><f:verbatim>Data Final</f:verbatim></f:facet>
									<h:outputText value="-" rendered="#{ not empty linha.cotaBolsa }"/>
									<h:outputText value="#{ linha.dataFinal }" rendered="#{ not empty linha.tipoBolsa }"/>
								</rich:column>

								<rich:column width="5%" styleClass="centerAlign">
									<h:commandLink action="#{ congressoIniciacaoCientifica.removerRestricao }" onclick="#{ confirmDelete }" >
										<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover Restri��o"/>
										<f:param name="id" value="#{ linha.id }"/>
										<f:param name="idTipoBolsa" value="#{ not empty linha.tipoBolsa ? linha.tipoBolsa.id : 0 }"/>
										<f:param name="idCotaBolsa" value="#{ not empty linha.cotaBolsa ? linha.cotaBolsa.id : 0 }"/>
									</h:commandLink>
								</rich:column>
								
							</rich:dataTable>
				</td>
			</tr>
			
		<tfoot>
			<tr>
				<td colspan="4">
					<h:commandButton value="#{congressoIniciacaoCientifica.confirmButton}" action="#{congressoIniciacaoCientifica.cadastrar}" /> 
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{congressoIniciacaoCientifica.cancelar}" />
				</td>
			</tr>
		</tfoot>
	</h:form>
</table>
	<br>
	<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

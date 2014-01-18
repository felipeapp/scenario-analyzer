<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<f:subview id="menu">
	<%@include file="/portais/docente/menu_docente.jsp"%>
	</f:subview>
	<h2>
	<a href="${ctx}/prodocente/nova_producao.jsf">
 		<h:graphicImage title="Voltar para Tela de Novas Produções" value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;"/>
 	</a>
	Cadastro de Visita Científica
	</h2>
	<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <h:commandLink action="#{visitaCientifica.listar}" value="Listar Visitas Cientificas Cadastrados"/>
	  </div>
	 </h:form>
	<%-- <h:messages showDetail="true"></h:messages> --%>
	<h:form id="myform">
	
	<table class="formulario" width="100%">
			<caption class="listagem">Cadastro de Visita Científica</caption>
			<h:inputHidden value="#{visitaCientifica.confirmButton}" />
			<h:inputHidden value="#{visitaCientifica.obj.id}" />
			<h:inputHidden value="#{visitaCientifica.obj.validado}" />
			<tr>
	 		<!-- Coluna 1 -->
	  		<td width="22%">
	   		<table id="coluna1" >
					<tr>
						<th>Título:</th>
						<td><h:inputText size="40" maxlength="255"
							value="#{visitaCientifica.obj.titulo}"
							readonly="#{visitaCientifica.readOnly}" /></td>
					</tr>
					<tr>
					<th class="required">Local:</th>
						<td><h:inputText value="#{visitaCientifica.obj.local}" size="40"
						maxlength="60" readonly="#{visitaCientifica.readOnly}" /></td>
					</tr>
					<tr>
						<th>Informação:</th>
						<td><h:inputText size="40" maxlength="255"
							value="#{visitaCientifica.obj.informacao}"
							readonly="#{visitaCientifica.readOnly}" /></td>
					</tr>

					<tr>
					<td colspan="2">
				  	<fieldset class=""><legend>Financiamento de Visita Científica</legend>
				   		<table>

							<tr>
								<th class="required">Entidade:</th>
								<td>
									<h:inputText value="#{visitaCientifica.obj.entidade}" size="30"
										maxlength="50" readonly="#{visitaCientifica.readOnly}" id="entidade" />
								</td>
							</tr>

							<tr>
								<th class="required">Valor:</th>
								<td>
									<h:inputText id="valor" value="#{visitaCientifica.obj.valor}" maxlength="8"  
										style="text-align: right" onkeydown="return(formataValor(this, event, 2))" 
										onfocus="javascript:select()" size="10">
										   <f:converter converterId="convertMoeda"/>
									</h:inputText>	
								</td>										
							</tr>

							<tr>
								<th>Financiamento Externo:</th>
								<td><t:selectBooleanCheckbox
									value="#{visitaCientifica.obj.financiamentoExterno}"
									readonly="#{visitaCientifica.readOnly}" /></td>
							</tr>
					 	</table>
				 	 </fieldset>
				 	</td>
					</tr>

			</table>
	  		</td>
	 		<!-- Fim Coluna 1 -->
	 		<!-- Coluna 2 -->
	  		<td width="70%">
	   		<table id="coluna2">
			<tr>
				<th class="required">Data de Produção:</th>
				<td>
					<t:inputCalendar value="#{visitaCientifica.obj.dataProducao}" popupDateFormat="dd/MM/yyyy" 
						size="10" maxlength="10" readonly="#{visitaCientifica.readOnly}" 
						renderAsPopup="true" renderPopupButtonAsImage="true" id="dataProducao" 
						onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupTodayString="Hoje é">
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
									
				</td>
			</tr>		   		
			<tr>
				<th class="required">Ano de Referência</th>
				<td>
					 <h:selectOneMenu id="anoReferencia" value="#{visitaCientifica.obj.anoReferencia}">
						<f:selectItem itemValue="" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{visitaCientifica.anosCadastrarAnoReferencia}" />
   					</h:selectOneMenu>
				</td>
			</tr>
	   		<tr>
				<th class="required">Período Início:</th>
				<td><t:inputText value="#{visitaCientifica.dataInicio}"
					size="7" maxlength="7" readonly="#{visitaCientifica.readOnly}"
					onkeypress="return(formatarMascara(this,event,'##/####'))" />
					<%-- <f:convertDateTime pattern="MM/yyyy" />
					</t:inputText>--%>
					<span style="font-size:9px; color:#a7a7a7; ">(Mês/Ano)</span>
				</td>
			</tr>
			<tr>
				<th class="required">Período Fim:</th>
				<td><t:inputText value="#{visitaCientifica.dataFim}"
					size="7" maxlength="7" readonly="#{visitaCientifica.readOnly}"
					onkeypress="return(formatarMascara(this,event,'##/####'))" />
					<%-- <f:convertDateTime pattern="MM/yyyy" />
					</t:inputText>--%>
					<span style="font-size:9px; color:#a7a7a7; ">(Mês/Ano)</span>
				</td>
			</tr>

					<tr>
						<th class="required">Âmbito:</th>

						<td><h:selectOneMenu value="#{visitaCientifica.obj.ambito.id}"
							disabled="#{visitaCientifica.readOnly}"
							disabledClass="#{visitaCientifica.disableClass}" style="width:308px;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{tipoRegiao.allCombo}" />
						</h:selectOneMenu></td>
					</tr>
					<tr>
						<th class="required">Área:</th>

						<td>
							<h:selectOneMenu value="#{visitaCientifica.obj.area.id}" style="width:308px;"
								disabled="#{visitaCientifica.readOnly}"
								disabledClass="#{visitaCientifica.disableClass}" id="area" valueChangeListener="#{visitaCientifica.changeArea}">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{area.allCombo}" />
							    <a4j:support event="onchange" reRender="subarea" />
							</h:selectOneMenu>
						</td>
					</tr>
					<tr>
						<th class="required">Sub-Área:</th>

						<td><h:selectOneMenu value="#{visitaCientifica.obj.subArea.id}" style="width:308px;"
							disabled="#{visitaCientifica.readOnly}"
							disabledClass="#{visitaCientifica.disableClass}" id="subarea">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{visitaCientifica.subArea}"/>
						</h:selectOneMenu></td>
					</tr>

					<tr>
						<th class="required">Instituição:</th>
						<td><h:selectOneMenu id="selectInst" value="#{visitaCientifica.obj.ies.id}"
							disabled="#{visitaCientifica.readOnly}" style="width:308px;"
							disabledClass="#{visitaCientifica.disableClass}">
							<f:selectItems value="#{instituicoesEnsino.allCombo}" />
							<a4j:support id="supportInst" onsubmit="javascript:mostrarOutrosInst();" event="onchange" actionListener="#{visitaCientifica.alterarStatusInst}" />							
						</h:selectOneMenu>
						<ufrn:help><i>Caso a instituição visitada não esteja cadastrado no sistema, marque esta opção.</i></ufrn:help></td>
					</tr>
									
					<tr id="inst" style="display: none;">
						<th class="obrigatorio">Instituição Visitada:</th>
						<td>
							<h:inputText value="#{visitaCientifica.obj.instituicaoOutro}" style="width:308px;" />
						</td>
					</tr>

					<tr>
						<th valign="top">Departamento:</th>

						<td><b><h:outputText value="#{visitaCientifica.obj.departamento.nome}" /></b></td>
							
					</tr>
				

			<c:if test="${visitaCientifica.validar}">
			<tr>
				<th>Validar:</th>
				<td><h:selectOneRadio rendered="#{bean.validar}"
					value="#{visitaCientifica.obj.validado}">
					<f:selectItem itemValue="false" itemLabel="Inválido" />
					<f:selectItem itemValue="true" itemLabel="Válido" />
				</h:selectOneRadio></td>
			</tr>
			</c:if>
			</table>
	 	 </td>
	 <!-- Fim Coluna 2  -->

		 </tr>
			<tfoot>
				<tr>
					<td colspan="2">
					<h:commandButton value="Validar" action="#{visitaCientifica.validar}" 
						immediate="true" rendered=""/>
					<h:commandButton value="#{visitaCientifica.confirmButton}"
						action="#{visitaCientifica.cadastrar}" id="cadastrar" />
					<h:commandButton onclick="#{confirm}" id="cancelar"
						value="Cancelar" action="#{visitaCientifica.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
		</h:form>

	<br />
	<center>
		<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br />
	<br />
			
<script>
	mostrarOutrosInst();	
	function mostrarOutrosInst() {
		$('inst').style.display = ($('myform:selectInst').value == 0)? "" : "none";
	}
	
</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

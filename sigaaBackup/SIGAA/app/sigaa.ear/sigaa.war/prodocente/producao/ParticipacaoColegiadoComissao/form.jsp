<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<f:subview id="menu">
 <%@include file="/portais/docente/menu_docente.jsp"%>
</f:subview>
	<h2><a href="${ctx}/prodocente/nova_producao.jsf">
 			<h:graphicImage title="Voltar para Tela de Novas Produções" value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;"/>
 		</a>Cadastro de Participação em Comissão
	</h2>

   <h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <a href="${ctx}/prodocente/producao/ParticipacaoColegiadoComissao/lista.jsf" >Listar Participações em Comissões</a>
	 </div>
    </h:form>

	<h:messages showDetail="true"></h:messages>


	<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	<h:form id="cadastroParticipacaoComissao">
	<table class="formulario" width="40%">
	<caption class="listagem">Cadastro de Participação em Comissão</caption>
	 <h:inputHidden value="#{participacaoColegiadoComissao.confirmButton}" />
	 <h:inputHidden value="#{participacaoColegiadoComissao.obj.id}" />
	 <h:inputHidden value="#{participacaoColegiadoComissao.obj.validado}" />
	 <tr>
	 <!-- Coluna 1 -->
	  <td width="50%">
	   <table id="coluna1" >
		<tr>
			<th class="required">Colegiado/Comissão:</th>
			<td><h:inputText style="width: 300px;" value="#{participacaoColegiadoComissao.obj.comissao}" 
					size="50" maxlength="255" readonly="#{participacaoColegiadoComissao.readOnly}" 
					onkeypress="return ApenasNumero(event);"/>
			</td>
		</tr>
		<tr>
			<th class="required">Natureza:</th>
			<td><h:selectOneMenu style="width: 300px;"
					value="#{participacaoColegiadoComissao.obj.tipoComissaoColegiado.id}"
					disabled="#{participacaoColegiadoComissao.readOnly}"
					disabledClass="#{participacaoColegiadoComissao.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{tipoComissaoColegiado.allCombo}" />
					<a4j:support id="support" event="onchange" reRender="required"
						actionListener="#{participacaoColegiadoComissao.setRequired}" />							
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th class="required">Tipo de Participação:</th>
			<td><h:selectOneMenu style="width: 300px;"
					value="#{participacaoColegiadoComissao.obj.tipoMembroColegiado.id}"
					disabled="#{participacaoColegiadoComissao.readOnly}"
					disabledClass="#{participacaoColegiadoComissao.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{tipoMembroColegiado.allCombo}" />
				</h:selectOneMenu>
			</td>
		</tr>

		<tr>
			<th>Informações Complementares:</th>
			<td>
		    	<h:inputTextarea value="#{participacaoColegiadoComissao.obj.informacao}" cols="37" rows="3" />
			</td>
		</tr>

       </table>
  	</td>
	 <!-- Fim Coluna 1 -->

	 <!-- Coluna 2 -->
	  <td width="50%">
	   <table id="coluna2">
		<tr>
			<th class="required" nowrap="nowrap">Data da publicação:</th>
			<td>
				<t:inputCalendar value="#{participacaoColegiadoComissao.obj.dataProducao}"
					size="10" maxlength="10" readonly="#{participacaoColegiadoComissao.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataProducao" 
					popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))">
					<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>								
		</tr>
		<tr>
			<th class="required">Ano de Referência:</th>
			<td>
				<h:selectOneMenu id="anoReferencia" value="#{participacaoColegiadoComissao.obj.anoReferencia}">
					<f:selectItem itemValue="" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{producao.anosCadastrarAnoReferencia}" />
   				</h:selectOneMenu>
			</td>
		</tr>

		<tr>
			<th class="required">Período Início:  <span style="font-size:9px; color:#a7a7a7; ">(Mês/Ano)</span></th>
			<td><t:inputText id="periodoInicio"	value="#{participacaoColegiadoComissao.obj.periodoInicio}"
					size="7" maxlength="7" readonly="#{participacaoColegiadoComissao.readOnly}"
					onkeypress="return(formatarMascara(this,event,'##/####'))" >
				 	<f:convertDateTime pattern="MM/yyyy" />
				</t:inputText>
			</td>
		</tr>
		<tr>
			<th>Período Fim:
				<a4j:outputPanel ajaxRendered="true"> 
						<h:graphicImage id="required" url="/img/required.gif" 
							rendered="#{participacaoColegiadoComissao.required}"/>
				</a4j:outputPanel>
				<span style="font-size:9px; color:#a7a7a7; ">(Mês/Ano)</span></th>
			<td><t:inputText id="periodoFim" value="#{participacaoColegiadoComissao.obj.periodoFim}" 
					maxlength="7" readonly="#{participacaoColegiadoComissao.readOnly}"
					onkeypress="return(formatarMascara(this,event,'##/####'))" size="7">
					<f:convertDateTime pattern="MM/yyyy" />
			    </t:inputText>
			</td>
		</tr>
		<tr>
			<th>Número de Reuniões:</th>
			<td><h:inputText value="#{participacaoColegiadoComissao.obj.numeroReunioes}"
					size="10" maxlength="6"	readonly="#{participacaoColegiadoComissao.readOnly}" 
					onkeypress="return ApenasNumeros(event);" />
			</td>
		</tr>
		<tr>
			<th>Membro Nato:</th>
			<td><t:selectBooleanCheckbox value="#{participacaoColegiadoComissao.obj.nato}"
					readonly="#{participacaoColegiadoComissao.readOnly}" />
			</td>
		</tr>

      </table>
	  </td>
	 <!-- Fim Coluna 2  -->
	 </tr>
	 <tr>
		<td colspan="3">
			<table id="coluna3">
				<tr>
					<th class="required">Instituição:</th>
					<td><h:selectOneMenu style="width: 580px;" id="sel"
							value="#{participacaoColegiadoComissao.obj.instituicao.id}"
							disabledClass="#{participacaoColegiadoComissao.disableClass}"
							disabled="#{participacaoColegiadoComissao.readOnly}">
							<%--<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />--%>
							<f:selectItems value="#{instituicoesEnsino.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</table>
		</td>
	 </tr>
			<!-- Botões -->
	 <c:if test="${participacaoColegiadoComissao.validar}">
	 <tr>
	 	<th>Validar:</th>
	 	<td>
	 		<h:selectOneRadio rendered="#{bean.validar}" value="#{participacaoColegiadoComissao.obj.validado}">
	 			<f:selectItem itemValue="false" itemLabel="Inválido" />
				<f:selectItem itemValue="true" itemLabel="Válido" />
		    </h:selectOneRadio>
	  	</td>
	 </tr>
	 </c:if>
	<tfoot>
  		<tr>
	   		<td colspan="2">
		   	    <h:commandButton value="Validar" action="#{participacaoColegiadoComissao.validar}" 
		   	   		immediate="true" rendered="#{participacaoColegiadoComissao.validar}"/>
			    <h:commandButton value="#{participacaoColegiadoComissao.confirmButton}" 
			   		action="#{participacaoColegiadoComissao.cadastrar}" 
			   		rendered="#{participacaoColegiadoComissao.cadastroVindoTelaMembroComissao == false}" /> 
			    <h:commandButton value="#{participacaoColegiadoComissao.confirmButton}" 
			   		action="#{participacaoColegiadoComissao.cadastrarVindoTelaMembroComissao}" 
			   		rendered="#{participacaoColegiadoComissao.cadastroVindoTelaMembroComissao == true}" /> 
			    <h:commandButton value="Cancelar" action="#{participacaoColegiadoComissao.cancelar}" 
			   		rendered="#{participacaoColegiadoComissao.cadastroVindoTelaMembroComissao == false}" 
			   		onclick="#{confirm}"/>
				<h:commandButton value="Cancelar" 
					action="#{participacaoColegiadoComissao.cancelarVindoTelaMembroComissao}" 
					rendered="#{participacaoColegiadoComissao.cadastroVindoTelaMembroComissao == true}" 
					onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>
	 <!-- Fim botões -->

	</table>
	</h:form>
<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	<br />
	<center>
		<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br />

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
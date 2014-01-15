<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<f:subview id="menu">
	<%@include file="/portais/docente/menu_docente.jsp"%>
</f:subview>
	<h2>
	<a href="${ctx}/prodocente/nova_producao.jsf">
 	<h:graphicImage title="Voltar para Tela de Novas Produções" value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;"/>
 	</a>
	Cadastro de Participação em Sociedades Científicas e Culturais
	</h2>

   <h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <a href="${ctx}/prodocente/producao/ParticipacaoSociedade/lista.jsf" >Listar Participação em Sociedades Científicas e Culturais Cadastradas</a>
	 </div>
    </h:form>
	<h:messages showDetail="true"></h:messages>


<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	<h:form>
	<table class="formulario" width="100%">
	<caption class="listagem">Cadastro de Participação em Sociedades Científicas e Culturais</caption>
	 <h:inputHidden value="#{participacaoSociedade.confirmButton}" />
	 <h:inputHidden value="#{participacaoSociedade.obj.id}" />
	 <h:inputHidden value="#{participacaoSociedade.obj.validado}" />
	 <tr>
	 <!-- Coluna 1 -->
	  <td width="50%">
	   <table id="coluna1" >

		<tr>
			<th class="required">Nome da Sociedade:</th>
			<td><h:inputText value="#{participacaoSociedade.obj.nomeSociedade}"
				size="50" maxlength="255"
				readonly="#{participacaoSociedade.readOnly}" /></td>
		</tr>
		<tr>
			<th class="required">Âmbito:</th>
				<td><h:selectOneMenu style="width: 220px;" value="#{participacaoSociedade.obj.ambito.id}"
				disabled="#{participacaoSociedade.readOnly}"
				disabledClass="#{participacaoSociedade.disableClass}">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{tipoRegiao.allCombo}" />
			</h:selectOneMenu></td>
		</tr>
		<tr>
			<th class="required">Participação:</th>
				<td><h:selectOneMenu style="width: 220px;"
				value="#{participacaoSociedade.obj.tipoParticipacaoSociedade.id}"
				disabled="#{participacaoSociedade.readOnly}"
				disabledClass="#{participacaoSociedade.disableClass}">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{tipoParticipacaoSociedade.allCombo}" />
		</h:selectOneMenu></td>
		</tr>

		<tr>
			<th class="required">Área:</th>
				<td><h:selectOneMenu style="width: 220px;" value="#{participacaoSociedade.obj.area.id}"
				disabled="#{participacaoSociedade.readOnly}"
				disabledClass="#{participacaoSociedade.disableClass}" id="area" valueChangeListener="#{participacaoSociedade.changeArea}">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{area.allCombo}" />
				<a4j:support event="onchange" reRender="subarea" />
			</h:selectOneMenu></td>
		</tr>
		<tr>
			<th class="required">Sub-Área:</th>
				<td><h:selectOneMenu style="width: 220px;" value="#{participacaoSociedade.obj.subArea.id}"
				disabled="#{participacaoSociedade.readOnly}"
				disabledClass="#{participacaoSociedade.disableClass}" id="subarea">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{participacaoSociedade.subArea}" />
			</h:selectOneMenu></td>
		</tr>

		<tr>
			<th>Informações Complementares:</th>
			<td>
			<h:inputTextarea value="#{participacaoSociedade.obj.informacao}" cols="49" rows="3" />
			</td>
		</tr>

		<tr><td><br /><br /></td></tr>

       </table>
	  </td>
	 <!-- Fim Coluna 1 -->

	 <!-- Coluna 2 -->
	  <td width="30%">
	   <table id="coluna2">

		<tr>
			<th class="required" nowrap="nowrap">Data da publicação:</th>
			<td>
				<t:inputCalendar value="#{participacaoSociedade.obj.dataProducao}"
					size="10" maxlength="10" readonly="#{participacaoSociedade.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataProducao" onkeypress="javascript:formataData(this,event); return ApenasNumeros(event);" />					
			</td>
		</tr>

		<tr>
			<th class="required">Ano de Referência</th>
			<td>
				 <h:selectOneMenu id="anoReferencia" value="#{participacaoSociedade.obj.anoReferencia}">
					<f:selectItem itemValue="" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{producao.anosCadastrarAnoReferencia}" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th class="required">Período Início: <span style="font-size:9px; color:#a7a7a7; ">(Mês/Ano)</span></th>
			<td><t:inputText
				value="#{participacaoSociedade.obj.periodoInicio}" size="7"
				maxlength="7" readonly="#{participacaoSociedade.readOnly}"
				onkeypress="return(formatarMascara(this,event,'##/####'))" >
						 <f:convertDateTime pattern="MM/yyyy" />
				</t:inputText>
			</td>
		</tr>
		<tr>
			<th>Período Fim: <span style="font-size:9px; color:#a7a7a7; ">(Mês/Ano)</span></th>
			<td><t:inputText value="#{participacaoSociedade.obj.periodoFim}"
				size="7" maxlength="7"
				readonly="#{participacaoSociedade.readOnly}" onkeypress="return(formatarMascara(this,event,'##/####'))" >
						 <f:convertDateTime pattern="MM/yyyy" />
			   </t:inputText>
			</td>
		</tr>

		<tr><td></td></tr>

       </table>
	  </td>
	 <!-- Fim Coluna 2  -->
	 </tr>


         <!-- Botoes -->
	 <c:if test="${participacaoSociedade.validar}">
	 <tr>
	  <th>Validar:</th>
	  <td><h:selectOneRadio rendered="#{bean.validar}"
			value="#{participacaoSociedade.obj.validado}">
			<f:selectItem itemValue="false" itemLabel="Inválido" />
			<f:selectItem itemValue="true" itemLabel="Válido" />
		    </h:selectOneRadio>
	  </td>
	 </tr>
	 </c:if>
	 <tfoot>
	  <tr>
	   <td colspan=2>
	   <h:commandButton value="Validar" action="#{participacaoSociedade.validar}" immediate="true" rendered="#{participacaoSociedade.validar}"/>
	   <h:commandButton value="#{participacaoSociedade.confirmButton}"
		action="#{participacaoSociedade.cadastrar}" /> <h:commandButton
		value="Cancelar" action="#{participacaoSociedade.cancelar}" onclick="#{confirm}"/></td>
	  </tr>
	 </tfoot>
	 <!-- Fim botoes -->


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

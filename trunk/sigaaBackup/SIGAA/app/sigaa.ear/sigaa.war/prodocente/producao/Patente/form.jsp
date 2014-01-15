<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<f:subview id="menu">
 <%@include file="/portais/docente/menu_docente.jsp"%>
</f:subview>

	<h2>
	<a href="${ctx}/prodocente/nova_producao.jsf">
 	<h:graphicImage title="Voltar para Tela de Novas Produções" value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;"/>
 	</a>
	Cadastro de Patente
	</h2>

   <h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif" style="overflow: visible;"/>
	  <a href="${ctx}/prodocente/producao/Patente/lista.jsf" > Listar Patentes Cadastradas</a>
	  <h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/> :Adicionar Instituição à Lista
	  <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/> :Remover Instituição da Lista
	 </div>
    </h:form>
	<h:messages showDetail="true"></h:messages>


	<h:form id="formulario">
	<table class="formulario" width="100%">
	<caption class="listagem">Cadastro de Patentes</caption>
	<h:outputText value="#{patente.initMBean}" />
	 <h:inputHidden value="#{patente.confirmButton}" />
	 <h:inputHidden value="#{patente.obj.id}" />
	 <h:inputHidden value="#{patente.obj.validado}" />
	 <tr>
	 <!-- Coluna 1 -->
	  <td width="50%">
	   <table id="coluna1" >

		<tr>
			<th class="obrigatorio">Título:</th>
			<td><h:inputText value="#{patente.obj.titulo}" size="53"
				maxlength="255" readonly="#{patente.readOnly}" /></td>
		</tr>

		<tr>
			<th class="obrigatorio">Local de Registro:</th>
			<td><h:inputText value="#{patente.obj.registroLocal}" size="53"
				maxlength="255" readonly="#{patente.readOnly}" /></td>
		</tr>
		<tr>
			<th class="obrigatorio">Autores:</th>
			<td>
				<h:inputTextarea value="#{patente.obj.autores}" cols="49" rows="3"></h:inputTextarea>
			</td>
		</tr>
		<tr>
			<th>Informações Complementares:</th>
			<td>
			<h:inputTextarea value="#{patente.obj.informacao}" cols="49" rows="3" />
			</td>
		</tr>

		<tr>
			<th class="obrigatorio">Tipo de Participação:</th>
			<td><h:selectOneMenu style="width: 250px;" value="#{patente.obj.tipoParticipacao.id}"
				disabled="#{patente.readOnly}"
			disabledClass="#{patente.disableClass}">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{patente.tipoParticipacao}" />
			</h:selectOneMenu></td>
		</tr>
		<tr>
			<th class="obrigatorio">Área:</th>
			<td><h:selectOneMenu style="width: 250px;" value="#{patente.obj.area.id}"
				disabled="#{patente.readOnly}"
				disabledClass="#{patente.disableClass}" id="area" valueChangeListener="#{patente.changeArea}">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{area.allCombo}" />
				<a4j:support event="onchange" reRender="subarea" />
			</h:selectOneMenu></td>
		</tr>
		<tr>
			<th class="obrigatorio">Sub-Área:</th>
			<td><h:selectOneMenu style="width: 250px;" value="#{patente.obj.subArea.id}"
				disabled="#{patente.readOnly}"
				disabledClass="#{patente.disableClass}" id="subarea">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{patente.subArea}" />
			</h:selectOneMenu>
			</td>
		</tr>
		<tr>
            <th width="1%">Instituição Patrocinadora: &nbsp;&nbsp;</th>
             <td width="99%">
				<h:selectOneMenu style="margin-top:10px; width: 250px;" id="escolher_patrocinadora" 
					disabled="#{patente.readOnly}" disabledClass="#{patente.disableClass}" 
					value="#{patente.idInstituicaoDefault}">
					<f:selectItems value="#{instituicoesEnsino.allCombo}" />
				</h:selectOneMenu>&nbsp;
 			   <h:commandButton action="#{patente.addPatrocinadora}" style="margin-left:3px;" 
 			   		image="/img/adicionar.gif" title="Adicionar instituição à lista de instituições Patrocinadoras Escolhidas" />
				<br />
				<img src="/shared/img/required.gif" style="margin-left:-10px;" />
			    <h:selectManyMenu id="patrocinadora" style="margin-top:5px; height: 80px; width:250px; vertical-align: middle;" 
			    	value="#{patente.idsPatrocinadoraSelecionadaDeletar}">
				 	<f:selectItems value="#{patente.patrocinadorasEscolhidas}" />
			    </h:selectManyMenu>&nbsp;
	            <h:commandButton action="#{patente.deletarPatrocinadora}" 
	            	image="/img/delete.gif" title="Remover instituição da lista de instituições patrocinadoras escolhidas" />
	        </td>
		</tr>
       </table>
	  </td>
	 <!-- Fim Coluna 1 -->

	 <!-- Coluna 2 -->
	  <td width="30%">
	  <table id="coluna2">
	   <tr>
			<th class="obrigatorio" nowrap="nowrap">Data da produção:</th>
			<td>
				<t:inputCalendar value="#{patente.obj.dataProducao}"
					size="10" maxlength="10" readonly="#{patente.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataProducao" onkeypress="javascript:formataData(this,event); return ApenasNumeros(event);" />					
			</td>								
		</tr>
	   
		<tr>
			<th class="obrigatorio">Ano de Referência:</th>
			<td>
				<h:selectOneMenu id="anoReferencia" value="#{patente.obj.anoReferencia}">
					<f:selectItem itemValue="" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{patente.anosCadastrarAnoReferencia}" />
   				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th>Página de Registro:</th>
			<td><h:inputText value="#{patente.obj.registroPagina}" size="10"
				maxlength="6" readonly="#{patente.readOnly}" onkeypress="return ApenasNumeros(event);" />
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">Volume do Registro:</th>
			<td><h:inputText value="#{patente.obj.registroVolume}" size="10"
				maxlength="5" readonly="#{patente.readOnly}" onkeypress="return ApenasNumeros(event);" /></td>
		</tr>
		<tr>
			<th class="obrigatorio">Data do Registro:</th>
			<td> <t:inputCalendar value="#{patente.obj.registroData}"
					size="10" maxlength="10" readonly="#{patente.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataRegistro" onkeypress="javascript:formataData(this,event); return ApenasNumeros(event);" />
			</td>					
		</tr>
		<tr>
			<th class="obrigatorio">Status:</th>
			<td>
			    <h:selectOneMenu style="width: 90px;" value="#{patente.obj.status}" disabled="#{patente.readOnly}"	disabledClass="#{patente.disableClass}" immediate="true">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItem itemValue="1" itemLabel="Em Registro" />
					<f:selectItem itemValue="2" itemLabel="Concedida" />
					<a4j:support reRender="formulario" event="onchange" />
				</h:selectOneMenu>
			</td>
		</tr>

		<a4j:region rendered="#{ patente.obj.status == 1 }" id="registro">

			<tr>
				<th class="obrigatorio">Nº de Registro:</th>
				<td><h:inputText value="#{patente.obj.numeroRegistro}" size="10"
					maxlength="20" readonly="#{patente.readOnly}" />
				</td>
			</tr>
			
		</a4j:region>
		
		<a4j:region rendered="#{ patente.obj.status == 2 }" id="patente">
			<tr>
				<th class="obrigatorio" style="vertical-align: center;">Nº da Patente:</th>
				<td><h:inputText value="#{patente.obj.numeroPatente}" size="10"
					maxlength="255" readonly="#{patente.readOnly}" onkeypress="return ApenasNumeros(event);" />
				</td>
			</tr>
		</a4j:region>
		
		<tr>
			<th>Premiada:</th>
			<td><h:selectBooleanCheckbox value="#{patente.obj.premiada}"
				readonly="#{patente.readOnly}" /></td>
		</tr>

		<!-- Quantitativos -->
		<tr>
		 <td colspan="2">
		  <fieldset class=""><legend>Quantitativos</legend>
		   <table>
		    <tr>
			 <th>Docentes (incluindo você):</th>
			 <td>
			  <h:inputText value="#{patente.obj.numeroDocentes}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Docentes de outros Departamentos:</th>
			 <td>
			  <h:inputText value="#{patente.obj.numeroDocentesOutros}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Estudantes:</th>
			 <td>
			  <h:inputText value="#{patente.obj.numeroEstudantes}" size="7" maxlength="7" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Técnicos/Administrativos:</th>
			 <td>
			  <h:inputText value="#{patente.obj.numeroTecnicos}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Outros:</th>
			 <td>
			  <h:inputText value="#{patente.obj.numeroOutros}" size="7"	maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
		   </table>
		  </fieldset>
		 </td>
		</tr>
	    <!-- Fim Quantitativos  -->

      </table>
	  </td>
	 <!-- Fim Coluna 2  -->
	 </tr>

     <!-- Botões -->
	 <c:if test="${patente.validar}">
	 <tr>
	  <th>Validar:</th>
	  <td><h:selectOneRadio rendered="#{bean.validar}"
			value="#{patente.obj.validado}">
			<f:selectItem itemValue="false" itemLabel="Inválido" />
			<f:selectItem itemValue="true" itemLabel="Válido" />
		    </h:selectOneRadio>
	  </td>
	 </tr>
	 </c:if>
	 <tfoot>
	  <tr>
	   <td colspan="2">
	   <h:commandButton value="Validar" action="#{patente.validar}" immediate="true" rendered="#{patente.validar}"/>
	   <h:commandButton value="#{patente.confirmButton}"
		action="#{patente.cadastrar}" /> <h:commandButton
		value="Cancelar" action="#{patente.cancelar}" onclick="#{confirm}" /></td>
	  </tr>
	 </tfoot>
	 <!-- Fim botões -->

	 </table>
	 </h:form>
	 <br />
	 <center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	 </center>
	 <br />

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<f:subview id="menu">
 <%@include file="/portais/docente/menu_docente.jsp"%>
</f:subview>
	<h2>
	<a href="${ctx}/prodocente/nova_producao.jsf">
 		<h:graphicImage title="Voltar para Tela de Novas Produções" value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;"/>
 	</a>
	Cadastro de Texto Didático ou Discussão
	</h2>

	<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <a href="${ctx}/prodocente/producao/TextoDidatico/lista.jsf" >Listar Textos Didaticos ou Discussão Cadastrados</a>
	 </div>
    </h:form>

	<h:messages showDetail="true"></h:messages>


	<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	<h:form>
	<table class="formulario" width="100%">
	<caption class="listagem">Cadastro de Texto Didático ou Discussão</caption>
	 <h:inputHidden value="#{textoDidatico.confirmButton}" />
	 <h:inputHidden value="#{textoDidatico.obj.id}" />
	 <h:inputHidden value="#{textoDidatico.obj.validado}" />
	 <tr>
	 <!-- Coluna 1 -->
	  <td width="50%">
	   <table id="coluna1" >

		<tr>
			<th class="required">Tipo de Texto:</th>
			<td>
			  <h:selectOneMenu style="width: 317px;"
				value="#{textoDidatico.obj.textoDiscussao}"
				disabled="#{textoDidatico.readOnly}"
				disabledClass="#{textoDidatico.disableClass}"
				onchange="javascript:submit();">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItem itemValue="#{false}" itemLabel="Texto Didático" />
				<f:selectItem itemValue="#{true}" itemLabel="Texto Discussão" />
				</h:selectOneMenu>
			</td>
		</tr>

		<tr>
			<th class="required">Título:</th>
			<td><h:inputText size="50" value="#{textoDidatico.obj.titulo}"
				readonly="#{textoDidatico.readOnly}" /></td>
		</tr>
		<tr>
			<th class="required">Autores:</th>
			<td>
			<h:inputTextarea value="#{textoDidatico.obj.autores}" cols="47" rows="5"></h:inputTextarea>
			</td>
		</tr>
		<tr>
			<th class="required">Local de Publicação:</th>
			<td><h:inputText value="#{textoDidatico.obj.localPublicacao}"
				size="50" maxlength="255" readonly="#{textoDidatico.readOnly}" /></td>
		</tr>
		<tr>
			<th>Informação:</th>
			<td><h:inputText size="50" maxlength="255"
				value="#{textoDidatico.obj.informacao}"
				readonly="#{textoDidatico.readOnly}" /></td>
		</tr>
		<tr>
			<th class="required">Tipo de Participação:</th>
			<td><h:selectOneMenu style="width: 240px;"
				value="#{textoDidatico.obj.tipoParticipacao.id}"
				disabled="#{textoDidatico.readOnly}"
				disabledClass="#{textoDidatico.disableClass}">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{textoDidatico.tipoParticipacao}" />
			</h:selectOneMenu></td>
		</tr>
		<tr>
			<th class="required">Área:</th>
			<td><h:selectOneMenu style="width: 240px;" value="#{textoDidatico.obj.area.id}"
				disabled="#{textoDidatico.readOnly}"
				disabledClass="#{textoDidatico.disableClass}" id="area" valueChangeListener="#{textoDidatico.changeArea}">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{area.allCombo}" />
				<a4j:support event="onchange" reRender="subarea" />
			</h:selectOneMenu></td>
		</tr>
		<tr>
			<th class="required">Sub-Área:</th>
			<td><h:selectOneMenu style="width: 240px;" value="#{textoDidatico.obj.subArea.id}"
				disabled="#{textoDidatico.readOnly}"
				disabledClass="#{textoDidatico.disableClass}" id="subarea">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{textoDidatico.subArea}" />
			</h:selectOneMenu></td>
		</tr>
		<tr>
			<th class="required">Tipo de Instância:</th>
			<td><h:selectOneMenu style="width: 240px;" value="#{textoDidatico.obj.tipoInstancia.id}"
				disabled="#{textoDidatico.readOnly}"
				disabledClass="#{textoDidatico.disableClass}">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{tipoInstancia.allCombo}" />
			</h:selectOneMenu></td>
		</tr>


          </table>
	  </td>
	 <!-- Fim Coluna 1 -->

	 <!-- Coluna 2 -->
	  <td width="30%">
	   <table id="coluna2">

			<tr>
				<th class="required">Data de Produção:</th>
				<td>
					<t:inputCalendar value="#{textoDidatico.obj.dataProducao}"
						size="10" maxlength="10" readonly="#{textoDidatico.readOnly}"
						renderAsPopup="true" renderPopupButtonAsImage="true" id="dataProducao" onkeypress="javascript:formataData(this,event); return ApenasNumeros(event);" />									
				</td>
			</tr>	 

		<tr>
			<th class="required">Ano de Referência:</th>
			<td>
				 <h:selectOneMenu id="anoReferencia" value="#{textoDidatico.obj.anoReferencia}">
					<f:selectItem itemValue="" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{textoDidatico.anosCadastrarAnoReferencia}" />
   				</h:selectOneMenu>
		   </td>
		</tr>
		<tr>
			<th>Página Inicial:</th>
			<td>
				<h:inputText value="#{textoDidatico.obj.paginaInicial}" size="10"
				maxlength="20" readonly="#{textoDidatico.readOnly}" onkeypress="return ApenasNumeros(event);"   />
			</td>
		</tr>
		<tr>
		    <th>Página Final:</th>
		    <td>
				<h:inputText value="#{textoDidatico.obj.paginaFinal}" size="10"
				maxlength="20" readonly="#{textoDidatico.readOnly}" onkeypress="return ApenasNumeros(event);"/>
			</td>
		</tr>
		<tr>
			<th>Destaque:</th>
			<td><h:selectBooleanCheckbox value="#{textoDidatico.obj.destaque}"
				readonly="#{textoDidatico.readOnly}" /></td>
		</tr>
		<tr>
			<th><h:outputText value="Premiada:" rendered="#{textoDidatico.obj.textoDiscussao}" /></th>
			<td><h:selectBooleanCheckbox value="#{textoDidatico.obj.premiada}" rendered="#{textoDidatico.obj.textoDiscussao}"	readonly="#{textoDidatico.readOnly}" /></td>
		</tr>


		<tr>
		<!-- Quantitativos -->
		<tr>
		 <td colspan="2">
		  <fieldset class=""><legend>Quantitativos</legend>
		   <table>
		    <tr>
			 <th>Docentes (incluindo você):</th>
			 <td>
			  <h:inputText value="#{textoDidatico.obj.numeroDocentes}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Docentes de outros Departamentos:</th>
			 <td>
			  <h:inputText value="#{textoDidatico.obj.numeroDocentesOutros}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Estudantes:</th>
			 <td>
			  <h:inputText value="#{textoDidatico.obj.numeroEstudantes}" size="7" maxlength="7" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Técnicos/Administrativos:</th>
			 <td>
			  <h:inputText value="#{textoDidatico.obj.numeroTecnicos}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Outros:</th>
			 <td>
			  <h:inputText value="#{textoDidatico.obj.numeroOutros}" size="7"	maxlength="6" readonly="#{capitulo.readOnly}" />
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


         <!-- Botoes -->
	 <c:if test="${textoDidatico.validar}">
	 <tr>
	  <th>Validar:</th>
	  <td><h:selectOneRadio rendered="#{bean.validar}"
			value="#{textoDidatico.obj.validado}">
			<f:selectItem itemValue="false" itemLabel="Inválido" />
			<f:selectItem itemValue="true" itemLabel="Válido" />
		    </h:selectOneRadio>
	  </td>
	 </tr>
	 </c:if>
	 <tfoot>
	  <tr>
	   <td colspan=2>
	   <h:commandButton value="Validar" action="#{textoDidatico.validar}" immediate="true" rendered="#{textoDidatico.validar}"/>
	   <h:commandButton value="#{textoDidatico.confirmButton}"
		action="#{textoDidatico.cadastrar}" /> <h:commandButton
		value="Cancelar" action="#{textoDidatico.cancelar}" onclick="#{confirm}"/></td>
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

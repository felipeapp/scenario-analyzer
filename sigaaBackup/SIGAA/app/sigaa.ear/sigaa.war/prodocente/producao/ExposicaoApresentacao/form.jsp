<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
<h2>
	 <a href="${ctx}/prodocente/nova_producao.jsf">
 		<h:graphicImage title="Voltar para Tela de Novas Produ��es" value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;"/>
 	</a>
	Cadastro de Exposi��o ou Apresentac�o Artisticas
</h2>

   <h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <a href="${ctx}/prodocente/producao/ExposicaoApresentacao/lista.jsf" >Listar Exposi��o ou Apresentac�o Art�stica Cadastrados</a>
	 </div>
    </h:form>

	<h:messages showDetail="true"></h:messages>


<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	<h:form>
	<table class="formulario" width="100%">
	<caption class="listagem">Cadastro de Exposi��o ou Apresentac�o Art�stica</caption>
	 <h:inputHidden value="#{exposicao.confirmButton}" />
	 <h:inputHidden value="#{exposicao.obj.id}" />
	 <h:inputHidden value="#{exposicao.obj.validado}" />
	 <tr>
	 <!-- Coluna 1 -->
	  <td width="50%">
	   <table id="coluna1" >

		<tr>
			<th class="required">T�tulo:</th>
			<td><h:inputText size="50" value="#{exposicao.obj.titulo}" id="_1"
				readonly="#{exposicao.readOnly}" /></td>
		</tr>
		<tr>
			<th class="required">Local :</th>
			<td><h:inputText value="#{exposicao.obj.local}" size="50" id="_10"
				maxlength="50" readonly="#{exposicao.readOnly}" /></td>
		</tr>
		<tr>
			<th class="required">Autores:</th>
			<td>
				<h:inputTextarea value="#{exposicao.obj.autores}" id="_11" cols="49" rows="5" ></h:inputTextarea>
			</td>
		</tr>
		<tr>
			<th>Informa��es Complementares:</th>
			<td>
			  <h:inputTextarea value="#{exposicao.obj.informacao}" id="_7" cols="49" rows="3" />
			</td>
		</tr>

		<tr>
			<th class="required">�mbito:</th>
			<td><h:selectOneMenu style="width: 250px;" value="#{exposicao.obj.tipoRegiao.id}"
				disabled="#{exposicao.readOnly}"
				disabledClass="#{exposicao.disableClass}">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{tipoRegiao.allCombo}" />
			</h:selectOneMenu></td>
		</tr>
		<tr>
			<th class="required">�rea:</th>
			<td><h:selectOneMenu style="width: 250px;" value="#{exposicao.obj.area.id}"
				disabled="#{exposicao.readOnly}"
				disabledClass="#{exposicao.disableClass}" id="area"  valueChangeListener="#{exposicao.changeArea}">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{area.allCombo}" />
				<a4j:support event="onchange" reRender="subarea" />
				</h:selectOneMenu></td>
		</tr>
		<tr>
			<th class="required">Sub-�rea:</th>
			<td><h:selectOneMenu style="width: 250px;" value="#{exposicao.obj.subArea.id}"
				disabled="#{exposicao.readOnly}"
				disabledClass="#{exposicao.disableClass}" id="subarea">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{exposicao.subArea}" />
			</h:selectOneMenu></td>
		</tr>
			<tr>
			<th class="required">Tipo Art�stico:</th>
			<td>
			<h:selectOneMenu style="width: 250px;" value="#{exposicao.obj.tipoArtistico.id}" id="_61"
				disabled="#{exposicao.readOnly}"
				disabledClass="#{exposicao.disableClass}" >
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{exposicao.tipoArtistico}" />
			</h:selectOneMenu>
			</td>
		</tr>

			<tr>
			<th class="required">Sub-Tipo Art�stico:</th>
			<td>
			<h:selectOneMenu style="width: 250px;" value="#{exposicao.obj.subTipoArtistico.id}" id="_6"
				disabled="#{exposicao.readOnly}"
				disabledClass="#{exposicao.disableClass}" >
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{exposicao.tipo}" />
			</h:selectOneMenu>
			</td>
		</tr>

          </table>
	  </td>
	 <!-- Fim Coluna 1 -->

	 <!-- Coluna 2 -->
	  <td width="30%">
	   <table id="coluna2">

		<tr>
			<th class="required">Data de Produ��o:</th>
			<td>
				<t:inputCalendar value="#{exposicao.obj.dataProducao}"
					size="10" maxlength="10" readonly="#{exposicao.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataProducao" onkeypress="javascript:formataData(this,event); return ApenasNumeros(event);" />									
			</td>
		</tr>

		<tr>
			<th class="required">Ano de Refer�ncia:</th>
			<td>
				<h:selectOneMenu id="anoReferencia" value="#{exposicao.obj.anoReferencia}">
					<f:selectItem itemValue="" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{producao.anosCadastrarAnoReferencia}" />
   				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th class="required">Data inicial: <span style="font-size:9px; color:#a7a7a7; ">(M�s/Ano)</span></th>
			<td><t:inputText onkeypress="return(formatarMascara(this,event,'##/####'))" value="#{exposicao.obj.periodoInicio}"
				size="7" maxlength="7" readonly="#{exposicao.readOnly}" id="_8"	>
					<f:convertDateTime pattern="MM/yyyy" />
				</t:inputText>

			</td>
		</tr>
		<tr>
			<th class="required">Data Final: <span style="font-size:9px; color:#a7a7a7; ">(M�s/Ano)</span></th>
			<td><t:inputText onkeypress="return(formatarMascara(this,event,'##/####'))" value="#{exposicao.obj.periodoFim}"
				size="7" maxlength="7" readonly="#{exposicao.readOnly}" id="_9" >
					<f:convertDateTime pattern="MM/yyyy" />
				</t:inputText>
			</td>
		</tr>

		<tr>
			 <th>Premiada:</th>
			    <td>
				<h:selectBooleanCheckbox value="#{exposicao.obj.premiada}"
					readonly="#{exposicao.readOnly}" />
				</td>
			</tr>


		<!-- Quantitativos -->
		<tr>
		 <td colspan="2">
		  <fieldset class=""><legend>Quantitativos</legend>
		   <table>
		    <tr>
			 <th>Docentes (incluindo voc�):</th>
			 <td>
			  <h:inputText value="#{exposicao.obj.numeroDocentes}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Docentes de outros Departamentos:</th>
			 <td>
			  <h:inputText value="#{exposicao.obj.numeroDocentesOutros}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Estudantes:</th>
			 <td>
			  <h:inputText value="#{exposicao.obj.numeroEstudantes}" size="7" maxlength="7" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>T�cnicos/Administrativos:</th>
			 <td>
			  <h:inputText value="#{exposicao.obj.numeroTecnicos}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Outros:</th>
			 <td>
			  <h:inputText value="#{exposicao.obj.numeroOutros}" size="7"	maxlength="6" readonly="#{capitulo.readOnly}" />
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
	 <c:if test="${exposicao.validar}">
	 <tr>
	  <th>Validar:</th>
	  <td><h:selectOneRadio rendered="#{bean.validar}"
			value="#{exposicao.obj.validado}">
			<f:selectItem itemValue="false" itemLabel="Inv�lido" />
			<f:selectItem itemValue="true" itemLabel="V�lido" />
		    </h:selectOneRadio>
	  </td>
	 </tr>
	 </c:if>
	 <tfoot>
	  <tr>
	   <td colspan=2>
	   <h:commandButton value="Validar" action="#{exposicao.validar}" immediate="true" rendered="#{exposicao.validar}"/>
	   <h:commandButton value="#{exposicao.confirmButton}"
		action="#{exposicao.cadastrar}" /> <h:commandButton
		value="Cancelar" action="#{exposicao.cancelar}" onclick="#{confirm}"/></td>
	  </tr>
	 </tfoot>
	 <!-- Fim botoes -->


	 </table>
	 </h:form>
<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	 <br />
	 <center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
	 </center>
	 <br />



</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

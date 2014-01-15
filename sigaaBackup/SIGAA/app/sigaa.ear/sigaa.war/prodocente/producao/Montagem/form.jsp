<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2>
	<a href="${ctx}/prodocente/nova_producao.jsf">
 	<h:graphicImage title="Voltar para Tela de Novas Produções" value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;"/>
 	</a>
	Cadastro de Montagem
	</h2>

   <h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <a href="${ctx}/prodocente/producao/Montagem/lista.jsf" >Listar Montagens Cadastradas</a>
	 </div>
    </h:form>

	<h:messages showDetail="true"></h:messages>


<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	<h:form>
	<table class="formulario" width="100%">
	<caption class="listagem">Cadastro de Montagens</caption>
	 <h:inputHidden value="#{montagem.confirmButton}" />
	 <h:inputHidden value="#{montagem.obj.id}" />
	 <h:inputHidden value="#{montagem.obj.validado}" />
	 <tr>
	 <!-- Coluna 1 -->
	  <td width="50%">
	   <table id="coluna1" >

		<tr>
			<th class="required">Título:</th>
			<td><h:inputText size="50" value="#{montagem.obj.titulo}" id="_1"
				readonly="#{montagem.readOnly}" /></td>
		</tr>
		<tr>
			<th class="required">Local :</th>
			<td><h:inputText value="#{montagem.obj.local}" size="50" id="_10"
				maxlength="60" readonly="#{montagem.readOnly}" /></td>
		</tr>
		<tr>
			<th class="required">
			  Autores:<br />
			  <span style="color: #c3c3c3; font-size: 10px;"><b>Um por linha</b></span>
			</th>
			<td>
				<h:inputTextarea value="#{montagem.obj.autores}" id="_11" cols="49" rows="3" />
			</td>
		</tr>
		<tr>
			<th>Informações Complementares:</th>
			<td>
				<h:inputTextarea value="#{montagem.obj.informacao}" id="_7" cols="49" rows="3" />
			</td>
		</tr>

		<tr>
			<th  class="required">Tipo de Participação:</th>
			<td><h:selectOneMenu style="width: 280px;" value="#{montagem.obj.tipoParticipacao.id}" id="_2"
					disabled="#{montagem.readOnly}"
					disabledClass="#{montagem.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{montagem.tipoParticipacao}" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th class="required">Âmbito:</th>
			<td><h:selectOneMenu  style="width: 280px;" value="#{montagem.obj.tipoRegiao.id}"
				disabled="#{montagem.readOnly}"
				disabledClass="#{montagem.disableClass}">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{tipoRegiao.allCombo}" />
			</h:selectOneMenu>
		</td>
		</tr>
		<tr>
			<th class="required">Área:</th>
			<td><h:selectOneMenu style="width: 280px;" value="#{montagem.obj.area.id}"
				disabled="#{montagem.readOnly}"
				disabledClass="#{montagem.disableClass}" id="area" valueChangeListener="#{montagem.changeArea}">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{area.allCombo}" />
				<a4j:support event="onchange" reRender="subarea" />
				</h:selectOneMenu></td>
		</tr>
		<tr>
			<th class="required">Sub-Área:</th>
			<td><h:selectOneMenu style="width: 280px;" value="#{montagem.obj.subArea.id}"
				disabled="#{montagem.readOnly}"
				disabledClass="#{montagem.disableClass}" id="subarea">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{montagem.subArea}" />
				</h:selectOneMenu></td>
		</tr>
		<tr>
			<th class="required">Tipo Artístico:</th>
			<td>
				<h:selectOneMenu style="width: 280px;" value="#{montagem.obj.tipoArtistico.id}" id="_6"
					disabled="#{montagem.readOnly}"
					disabledClass="#{montagem.disableClass}" >
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{montagem.tipoArtistico}" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th class="required">Sub-Tipo Artístico:</th>
			<td>
				<h:selectOneMenu style="width: 280px;" value="#{montagem.obj.subTipoArtistico.id}"
					disabled="#{montagem.readOnly}"
					disabledClass="#{montagem.disableClass}" >
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{montagem.tipo}" />
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
			<th class="required">Data de Produção:</th>
			<td>
				<t:inputCalendar value="#{montagem.obj.dataProducao}"
					size="10" maxlength="10" readonly="#{montagem.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataProducao" onkeypress="javascript:formataData(this,event); return ApenasNumeros(event);" />									
			</td>
		</tr>


		<tr>
			<th class="required">Ano de Referência:</th>
			<td>
			<h:selectOneMenu id="anoReferencia" value="#{montagem.obj.anoReferencia}">
				<f:selectItem itemValue="" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{producao.anosCadastrarAnoReferencia}" />
   			</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th class="required">Data inicial: <span style="font-size:9px; color:#a7a7a7; ">(Mês/Ano)</span></th>
			<td>
			<t:inputText onkeypress="return(formatarMascara(this,event,'##/####'))" value="#{montagem.obj.periodoInicio}"
					size="7" maxlength="7" readonly="#{montagem.readOnly}" id="_8"
					 >

			  <f:convertDateTime pattern="MM/yyyy" />
			</t:inputText>

			</td>
		</tr>
		<tr>
			<th class="required">Data Final: <span style="font-size:9px; color:#a7a7a7; ">(Mês/Ano)</span></th>
			<td><t:inputText onkeypress="return(formatarMascara(this,event,'##/####'))" value="#{montagem.obj.periodoFim}"
					size="7" maxlength="7" readonly="#{montagem.readOnly}" id="_9"
					>

				  <f:convertDateTime pattern="MM/yyyy" />
				</t:inputText>
			</td>
		</tr>
		<tr>
			 <th>Premiada:</th>
			    <td>
				<h:selectBooleanCheckbox value="#{montagem.obj.premiada}"
					readonly="#{montagem.readOnly}" />
				</td>
			</tr>



		<!-- Quantitativos -->
		<tr>
		 <td colspan="2">
		  <fieldset class=""><legend>Quantitativos</legend>
		   <table>
		    <tr>
			 <th>Docentes (incluindo você):</th>
			 <td>
			  <h:inputText value="#{montagem.obj.numeroDocentes}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Docentes de outros Departamentos:</th>
			 <td>
			  <h:inputText value="#{montagem.obj.numeroDocentesOutros}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Estudantes:</th>
			 <td>
			  <h:inputText value="#{montagem.obj.numeroEstudantes}" size="7" maxlength="7" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Técnicos/Administrativos:</th>
			 <td>
			  <h:inputText value="#{montagem.obj.numeroTecnicos}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Outros:</th>
			 <td>
			  <h:inputText value="#{montagem.obj.numeroOutros}" size="7"	maxlength="6" readonly="#{capitulo.readOnly}" />
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
	 <c:if test="${montagem.validar}">
	 <tr>
	  <th>Validar:</th>
	  <td><h:selectOneRadio rendered="#{bean.validar}"
			value="#{montagem.obj.validado}">
			<f:selectItem itemValue="false" itemLabel="Inválido" />
			<f:selectItem itemValue="true" itemLabel="Válido" />
		    </h:selectOneRadio>
	  </td>
	 </tr>
	 </c:if>
	 <tfoot>
	  <tr>
	   <td colspan=2>
	   <h:commandButton value="Validar" action="#{montagem.validar}" immediate="true" rendered="#{montagem.validar}"/>
	   <h:commandButton value="#{montagem.confirmButton}"
		action="#{montagem.cadastrar}" /> <h:commandButton
		value="Cancelar" action="#{montagem.cancelar}" onclick="#{confirm}"/></td>
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

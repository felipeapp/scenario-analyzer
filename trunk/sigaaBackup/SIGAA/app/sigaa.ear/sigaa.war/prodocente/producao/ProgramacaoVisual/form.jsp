<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2>
	<a href="${ctx}/prodocente/nova_producao.jsf">
 		<h:graphicImage title="Voltar para Tela de Novas Produções" value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;"/>
 	</a>
	Cadastro de Programações Visuais Cadastradas
	</h2>


   <h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <a href="${ctx}/prodocente/producao/ProgramacaoVisual/lista.jsf" >Listar Programações Visuais Cadastradas</a>
	 </div>
    </h:form>

	<h:messages showDetail="true"></h:messages>


<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	<h:form>
	<table class="formulario" width="100%">
	<caption class="listagem">Cadastro de Programação Visual</caption>
	 <h:inputHidden value="#{programacao.confirmButton}" />
	 <h:inputHidden value="#{ programacao.obj.id }" />
	 <h:inputHidden value="#{ programacao.obj.validado }" />
	 <tr>
	 <!-- Coluna 1 -->
	  <td width="50%">
	   <table id="coluna1" >
		<tr>
			<th class="required">Título:</th>
			<td><h:inputText size="50" value="#{programacao.obj.titulo}" id="_1"
				readonly="#{programacao.readOnly}" /></td>
		</tr>
		<tr>
			<th class="required">Local :</th>
			<td><h:inputText value="#{programacao.obj.local}" size="50" id="_10"
				maxlength="60" readonly="#{programacao.readOnly}" /></td>
		</tr>
		<tr>
			<th class="required">Autores:</th>
			<td>
				<h:inputTextarea value="#{programacao.obj.autores}" cols="49" rows="5" id="_11" />
			</td>
		</tr>
		<tr>
			<th>Informações Complementares:</th>
			<td>
			 <h:inputTextarea value="#{programacao.obj.informacao}" id="_7" cols="49" rows="3" />
			</td>
		</tr>


		<tr>
			<th  class="required">Tipo de Participação:</th>
			<td><h:selectOneMenu style="width: 250px;" value="#{programacao.obj.tipoParticipacao.id}" id="_2"
				disabled="#{programacao.readOnly}"
					disabledClass="#{programacao.disableClass}">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
			<f:selectItems value="#{programacao.tipoParticipacao}" />
			</h:selectOneMenu></td>
		</tr>
			<tr>
			<th class="required">Âmbito:</th>
			<td><h:selectOneMenu style="width: 250px;" value="#{programacao.obj.tipoRegiao.id}"
				disabled="#{programacao.readOnly}"
				disabledClass="#{programacao.disableClass}">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{tipoRegiao.allCombo}" />
			</h:selectOneMenu></td>
		</tr>
		<tr>
			<th class="required">Área:</th>
		<td><h:selectOneMenu style="width: 250px;" value="#{programacao.obj.area.id}"
				disabled="#{programacao.readOnly}"
				disabledClass="#{programacao.disableClass}" id="area" valueChangeListener="#{programacao.changeArea}">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{area.allCombo}" />
				<a4j:support event="onchange" reRender="subarea" />
			</h:selectOneMenu></td>
		</tr>
		<tr>
			<th class="required">Sub-Área:</th>
				<td><h:selectOneMenu style="width: 250px;" value="#{programacao.obj.subArea.id}"
			disabled="#{programacao.readOnly}"
				disabledClass="#{programacao.disableClass}" id="subarea">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{programacao.subArea}" />
			</h:selectOneMenu></td>
		</tr>
		<tr>
			<th class="required">Tipo Artístico:</th>
			<td><h:selectOneMenu style="width: 250px;" value="#{programacao.obj.tipoArtistico.id}" id="_5"
				disabled="#{programacao.readOnly}"
				disabledClass="#{programacao.disableClass}">
				<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{programacao.tipoArtistico}" />
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
				<t:inputCalendar value="#{programacao.obj.dataProducao}"
					size="10" maxlength="10" readonly="#{programacao.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataProducao" onkeypress="javascript:formataData(this,event); return ApenasNumeros(event);" />									
			</td>
		</tr>

		<tr>
			<th class="required">Ano de Referência</th>
			<td>
				 <h:selectOneMenu id="anoReferencia" value="#{programacao.obj.anoReferencia}">
					<f:selectItem itemValue="" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{programacao.anosCadastrarAnoReferencia}" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th class="required">Data inicial:</th>
			<td><t:inputText onkeypress="return(formatarMascara(this,event,'##/####'))" value="#{programacao.obj.periodoInicio}"
				size="7" maxlength="7" readonly="#{programacao.readOnly}" id="_8" >
					 <f:convertDateTime pattern="MM/yyyy" />
				</t:inputText>
				<span style="color: #656565">(mês/ano)</span>
			</td>
		</tr>
		<tr>
			<th class="required">Data Final:</th>
			<td><t:inputText onkeypress="return(formatarMascara(this,event,'##/####'))" value="#{programacao.obj.periodoFim}"
				size="7" maxlength="7" readonly="#{programacao.readOnly}" id="_9" >
				 <f:convertDateTime pattern="MM/yyyy" />
				</t:inputText>
 			   <span style="color: #656565">(mês/ano)</span>
			</td>
		</tr>
		<tr>
			 <th>Premiado:</th>
			    <td>
				<h:selectBooleanCheckbox value="#{programacao.obj.premiada}"
					readonly="#{programacao.readOnly}" />
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
			  <h:inputText value="#{programacao.obj.numeroDocentes}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Docentes de outros Departamentos:</th>
			 <td>
			  <h:inputText value="#{programacao.obj.numeroDocentesOutros}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Estudantes:</th>
			 <td>
			  <h:inputText value="#{programacao.obj.numeroEstudantes}" size="7" maxlength="7" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Técnicos/Administrativos:</th>
			 <td>
			  <h:inputText value="#{programacao.obj.numeroTecnicos}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Outros:</th>
			 <td>
			  <h:inputText value="#{programacao.obj.numeroOutros}" size="7"	maxlength="6" readonly="#{capitulo.readOnly}" />
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
	 <c:if test="${programacao.validar}">
	 <tr>
	  <th>Validar:</th>
	  <td><h:selectOneRadio rendered="#{bean.validar}"
			value="#{programacao.obj.validado}">
			<f:selectItem itemValue="false" itemLabel="Inválido" />
			<f:selectItem itemValue="true" itemLabel="Válido" />
		    </h:selectOneRadio>
	  </td>
	 </tr>
	 </c:if>
	 <tfoot>
	  <tr>
	   <td colspan=2>
	   <h:commandButton value="Validar" action="#{programacao.validar}" immediate="true" rendered="#{programacao.validar}"/>
	   <h:commandButton value="#{programacao.confirmButton}"
		action="#{programacao.cadastrar}" /> <h:commandButton
		value="Cancelar" action="#{programacao.cancelar}" onclick="#{confirm}"/></td>
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

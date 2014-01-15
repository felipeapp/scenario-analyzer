<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<f:subview id="menu">
		<%@include file="/portais/docente/menu_docente.jsp"%>
	</f:subview>
	
	<h2><a href="${ctx}/prodocente/nova_producao.jsf">
			<h:graphicImage title="Voltar para Tela de Novas Produções" value="/img/prodocente/voltarproducoes.gif" 
					style="overflow: visible;" /></a>
		Cadastro de Participação em Evento</h2>

	<h:form>
		<div class="infoAltRem" style="width: 100%">
	 		<h:graphicImage value="/img/listar.gif"style="overflow: visible;" />
	 		<a href="${ctx}/prodocente/producao/PublicacaoEvento/lista.jsf">Listar Participações em Eventos Cadastradas</a>
	 	</div>
	</h:form>

<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	<h:form>
	<table class="formulario" width="100%">
	<caption class="listagem">Cadastro de Participação em Eventos</caption>
	 <h:inputHidden value="#{publicacaoEvento.confirmButton}" />
	 <h:inputHidden value="#{publicacaoEvento.obj.id}" />
	 <h:inputHidden value="#{publicacaoEvento.obj.validado}" />
	 <tr>
	 <!-- Coluna 1 -->
	  <td width="50%">
	   <table id="coluna1">

		<tr>
			<th class="required">Título:</th>
			<td><h:inputText size="50" maxlength="255" value="#{publicacaoEvento.obj.titulo}"
						readonly="#{publicacaoEvento.readOnly}" /></td>
		</tr>
		<tr>
			<th class="required">Nome do Evento:</th>
			<td><h:inputText value="#{publicacaoEvento.obj.nomeEvento}"
						size="50" maxlength="255" readonly="#{publicacaoEvento.readOnly}" /></td>
		</tr>
		<tr>
			<th class="required">Local de Participação:</th>
			<td><h:inputText value="#{publicacaoEvento.obj.localPublicacao}"
						size="50" maxlength="255" readonly="#{publicacaoEvento.readOnly}" /></td>
		</tr>
		<tr>
			<th class="required">Autores:</th>
			<td><h:inputTextarea value="#{publicacaoEvento.obj.autores}" cols="49" rows="5" ></h:inputTextarea></td>
		</tr>
		<tr>
			<th>Organizadores:</th>
			<td><h:inputTextarea value="#{publicacaoEvento.obj.organizadores}" cols="49" rows="5" ></h:inputTextarea></td>
		</tr>
		<tr>
			<th class="required">Natureza:</th>
			<td><h:selectOneMenu style="width: 250px;" value="#{publicacaoEvento.obj.natureza}"
						disabled="#{publicacaoEvento.readOnly}" disabledClass="#{publicacaoEvento.disableClass}">
					<f:selectItem itemValue="" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{publicacaoEvento.allNatureza}" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th>Observações:</th>
			<td><h:inputTextarea value="#{publicacaoEvento.obj.informacao}" cols="49" rows="3" ></h:inputTextarea></td>
		</tr>

		<tr>
			<th class="required">Tipo de Evento:</th>
			<td><h:selectOneMenu style="width: 250px;" value="#{publicacaoEvento.obj.tipoEvento.id}"
						disabled="#{publicacaoEvento.readOnly}"	disabledClass="#{publicacaoEvento.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{tipoEvento.allCombo}" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th class="required">Âmbito:</th>
			<td><h:selectOneMenu style="width: 250px;" value="#{publicacaoEvento.obj.tipoRegiao.id}"
						disabled="#{publicacaoEvento.readOnly}"	disabledClass="#{publicacaoEvento.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{tipoRegiao.allCombo}" />
				</h:selectOneMenu></td>
		</tr>
		<tr>
			<th class="required">Tipo de Participação:</th>
			<td><h:selectOneMenu style="width: 250px;" value="#{publicacaoEvento.obj.tipoParticipacao.id}"
						disabled="#{publicacaoEvento.readOnly}"	disabledClass="#{publicacaoEvento.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{publicacaoEvento.tipoParticipacao}" />
				</h:selectOneMenu></td>
		</tr>
		<tr>
			<th class="required">Área:</th>
			<td><h:selectOneMenu style="width: 250px;" value="#{publicacaoEvento.obj.area.id}" disabled="#{publicacaoEvento.readOnly}"
						disabledClass="#{publicacaoEvento.disableClass}" id="area" valueChangeListener="#{publicacaoEvento.changeArea}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{area.allCombo}" />
			 	   <a4j:support event="onchange" reRender="subarea" />
				</h:selectOneMenu></td>
		</tr>
		<tr>
			<th class="required">Sub-Área:</th>
			<td><h:selectOneMenu style="width: 250px;" value="#{publicacaoEvento.obj.subArea.id}" disabled="#{publicacaoEvento.readOnly}" 
						disabledClass="#{publicacaoEvento.disableClass}" id="subarea" >
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{publicacaoEvento.subArea}" />
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
				<td><t:inputCalendar value="#{publicacaoEvento.obj.dataProducao}" size="10" maxlength="10" 
							readonly="#{publicacaoEvento.readOnly}" renderAsPopup="true" renderPopupButtonAsImage="true" 
							id="dataProducao" onkeypress="javascript:formataData(this,event); return ApenasNumeros(event);" />									
				</td>
			</tr>	   
	   
			<tr>
				<th class="required">Ano de Referência</th>
				<td><h:selectOneMenu id="anoReferencia" value="#{publicacaoEvento.obj.anoReferencia}">
						<f:selectItem itemValue="" itemLabel="--SELECIONE--" />
						<f:selectItems value="#{producao.anosCadastrarAnoReferencia}" />
   					</h:selectOneMenu>
				</td>
			</tr>
		<tr>
			<th>Página Inicial:</th>
			<td><h:inputText value="#{publicacaoEvento.obj.paginaInicial}" size="10" maxlength="255" 
					readonly="#{publicacaoEvento.readOnly}" onkeypress="return ApenasNumeros(event);" />
			</td>
		</tr>
		<tr>
		 <th>Página Final:</th>
		 <td><h:inputText value="#{publicacaoEvento.obj.paginaFinal}" size="10" maxlength="255" 
					readonly="#{publicacaoEvento.readOnly}" onkeypress="return ApenasNumeros(event);" />
		 </td>
		</tr>
		<tr>
			<th>Destaque:</th>
			<td><h:selectBooleanCheckbox value="#{publicacaoEvento.obj.destaque}" readonly="#{publicacaoEvento.readOnly}" /></td>
		</tr>
		<tr>
			<th>Apresentado:</th>
		 	<td><h:selectBooleanCheckbox value="#{publicacaoEvento.obj.apresentado}" readonly="#{publicacaoEvento.readOnly}" /></td>
		</tr>


		<!-- Quantitativos -->
		<tr>
		 <td colspan="2">
		  <fieldset class=""><legend>Quantitativos</legend>
		   <table>
		    <tr>
				<th>Docentes (incluindo você):</th>
				<td><h:inputText value="#{publicacaoEvento.obj.numeroDocentes}" size="7" maxlength="6" 
						readonly="#{capitulo.readOnly}" /></td>
			</tr>
			<tr>
				<th>Docentes de outros Departamentos:</th>
				<td><h:inputText value="#{publicacaoEvento.obj.numeroDocentesOutros}" size="7" maxlength="6" 
							readonly="#{capitulo.readOnly}" /></td>
			</tr>
			<tr>
				<th>Estudantes:</th>
				<td><h:inputText value="#{publicacaoEvento.obj.numeroEstudantes}" size="7" maxlength="7" 
							readonly="#{capitulo.readOnly}" /></td>
			</tr>
			<tr>
				<th>Técnicos/Administrativos:</th>
				<td><h:inputText value="#{publicacaoEvento.obj.numeroTecnicos}" size="7" maxlength="6" 
						readonly="#{capitulo.readOnly}" /></td>
			</tr>
			<tr>
				<th>Outros:</th>
				<td><h:inputText value="#{publicacaoEvento.obj.numeroOutros}" size="7" maxlength="6" 
						readonly="#{capitulo.readOnly}" /></td>
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
	 <c:if test="${publicacaoEvento.validar}">
	 <tr>
	  	<th>Validar:</th>
	  	<td><h:selectOneRadio rendered="#{bean.validar}" value="#{publicacaoEvento.obj.validado}">
			  	<f:selectItem itemValue="false" itemLabel="Inválido" />
				<f:selectItem itemValue="true" itemLabel="Válido" />
		   </h:selectOneRadio>
	 	</td>
	 </tr>
	 </c:if>
	 <tfoot>
	 	<tr>
	  		<td colspan="2">
	   			<h:commandButton value="Validar" action="#{publicacaoEvento.validar}" immediate="true" 
	   					rendered="#{publicacaoEvento.validar}" />
	   			<h:commandButton value="#{publicacaoEvento.confirmButton}" action="#{publicacaoEvento.cadastrar}" /> 
	   			<h:commandButton value="Cancelar" action="#{publicacaoEvento.cancelar}" onclick="#{confirm}"/>
	   		</td>
	 	</tr>
	 </tfoot>
	 <!-- Fim botoes -->

	 </table>
	 </h:form>
<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	 
	 <br />
	 <center>
	 	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;" /> 
	 	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	 </center>
	 <br />

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2>
	<a href="${ctx}/prodocente/nova_producao.jsf">
 	<h:graphicImage title="Voltar para Tela de Novas Produções" value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;"/>
 	</a>
	Cadastro de Capítulos de Livros
	</h2>

   <h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <a href="${ctx}/prodocente/producao/Capitulo/lista.jsf" >Listar Capítulos de Livros Cadastrados</a>
	 </div>
    </h:form>

	<h:messages showDetail="true"></h:messages>

<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	<h:form id="formProducao">
	<table class="formulario" width="100%">
	<caption class="listagem">Cadastrar Capítulo de Livro</caption>
	 <h:inputHidden value="#{capitulo.confirmButton}" />
	 <h:inputHidden value="#{capitulo.obj.id}" />
	 <h:inputHidden value="#{capitulo.obj.validado}" />
	 <tr>
	 <!-- Coluna 1 -->
	  <td width="50%">
	   <table id="coluna1" >

	    <tr>
		 <th class="required">Título do Capítulo:</th>
		 <td>
		    <h:inputText size="50" value="#{capitulo.obj.titulo}" readonly="#{capitulo.readOnly}" />
		 </td>
		</tr>

		<tr>
		 <th class="required">Autores:</th>
		 <td>
		  <h:inputTextarea value="#{capitulo.obj.autores}" style="width: 310px; height: 50px;" readonly="#{capitulo.readOnly}"></h:inputTextarea>
		 </td>
		</tr>

		<tr>
		 <th class="required">Título do Livro:</th>
		 <td>
		  <h:inputText value="#{capitulo.obj.tituloLivro}" size="50" maxlength="255" readonly="#{capitulo.readOnly}" />
		 </td>
		</tr>

		<tr>
		 <th class="required">Editor:</th>
		 <td>
		  <h:inputText value="#{capitulo.obj.editor}" size="50" maxlength="255" readonly="#{capitulo.readOnly}" />
		 </td>
		</tr>

		<tr>
		 <th class="required">Local de Publicação:</th>
		 <td>
		  <h:inputText value="#{capitulo.obj.localPublicacao}" size="40" maxlength="60" readonly="#{capitulo.readOnly}" />
		 </td>
	    </tr>

		<tr>
			<th class="required">Data de Produção:</th>
			<td>
				<t:inputCalendar value="#{capitulo.obj.dataProducao}"
					size="10" maxlength="10" readonly="#{capitulo.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataProducao" onkeypress="javascript:formataData(this,event); return ApenasNumeros(event);" />									
			</td>
		</tr>

		<tr>
		 <th class="required">Ano de Referência:</th>
		 <td>
			 <h:selectOneMenu id="anoReferencia" value="#{capitulo.obj.anoReferencia}">
				<f:selectItem itemValue="" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{producao.anosCadastrarAnoReferencia}" />
   			</h:selectOneMenu>
		 </td>
		</tr>

		<tr>
		 <th class="required">Área:</th>
		 <td>
			  <h:selectOneMenu style="width:220px;" value="#{capitulo.obj.area.id}"
						disabled="#{capitulo.readOnly}"
						disabledClass="#{capitulo.disableClass}" id="area" valueChangeListener="#{capitulo.changeArea}">
						<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
						<f:selectItems value="#{area.allCombo}" />
						<a4j:support event="onchange" reRender="subarea"/>
			 </h:selectOneMenu>
		</td>
		</tr>

		<tr>
		 <th class="required">Sub-área:</th>
		 <td>
		  <h:selectOneMenu style="width:220px;" value="#{capitulo.obj.subArea.id}"
					disabled="#{capitulo.readOnly}" id="subarea"
					disabledClass="#{capitulo.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{capitulo.subArea}" />
		  </h:selectOneMenu>
		 </td>
		</tr>

		<tr>
		 <th  class="required">Publicação:</th>
		 <td>
		  <h:selectOneMenu style="width:220px;" id="tipoRegiao"
					value="#{capitulo.obj.tipoRegiao.id}"
					disabled="#{capitulo.readOnly}"
					disabledClass="#{capitulo.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{tipoRegiao.allCombo}" />
		  </h:selectOneMenu>
		 </td>
	    </tr>

		<tr>
		 <th  class="required">Tipo de Participação:</th>
		 <td>
		  <h:selectOneMenu style="width:220px;" value="#{capitulo.obj.tipoParticipacao.id}"
					disabled="#{capitulo.readOnly}"
					disabledClass="#{capitulo.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{capitulo.tipoParticipacao}" />
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
		 <th>Capítulos do Livro:</th>
		  <td>
		   <h:inputText value="#{capitulo.obj.capitulosLivro}" size="7" maxlength="5" readonly="#{capitulo.readOnly}" />
		  </td>
		</tr>

		<tr>
		 <th>Página Inicial:</th>
		 <td>
		  <h:inputText value="#{capitulo.obj.paginaInicial}" size="7" maxlength="255" readonly="#{capitulo.readOnly}" onkeypress="return ApenasNumeros(event);"/>
		 </td>
		</tr>

		<tr>
		 <th>Página Final:</th>
		 <td>
		  <h:inputText value="#{capitulo.obj.paginaFinal}" size="7" maxlength="255" readonly="#{capitulo.readOnly}" onkeypress="return ApenasNumeros(event);"/>
		 </td>
		</tr>

		<tr>
 		 <th>Publicação Premiada?</th>
		 <td>
		  <h:selectBooleanCheckbox value="#{capitulo.obj.premiada}" readonly="#{capitulo.readOnly}" styleClass="noborder"/>
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
			  <h:inputText value="#{capitulo.obj.numeroDocentes}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Docentes de outros Departamentos:</th>
			 <td>
			  <h:inputText value="#{capitulo.obj.numeroDocentesOutros}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Estudantes:</th>
			 <td>
			  <h:inputText value="#{capitulo.obj.numeroEstudantes}" size="7" maxlength="7" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Técnicos/Administrativos:</th>
			 <td>
			  <h:inputText value="#{capitulo.obj.numeroTecnicos}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Outros:</th>
			 <td>
			  <h:inputText value="#{capitulo.obj.numeroOutros}" size="7"	maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
		   </table>
		  </fieldset>
		 </td>
		</tr>

		 <tr>
		 <td colspan="2">Observações: <br>
		    <h:inputTextarea value="#{capitulo.obj.informacao}" cols="40" rows="3"></h:inputTextarea>
		 </td>
		</tr>

	    <!-- Fim Quantitativos  -->

	   </table>
	  </td>
	 <!-- Fim Coluna 2  -->
	 </tr>

     <!-- Botoes -->
	 <c:if test="${capitulo.validar}">
	 <tr>
	  <th>Validar:</th>
	  <td><h:selectOneRadio rendered="#{bean.validar}"
			value="#{capitulo.obj.validado}">
			<f:selectItem itemValue="false" itemLabel="Inválido" />
			<f:selectItem itemValue="true" itemLabel="Válido" />
		    </h:selectOneRadio>
	  </td>
	 </tr>
	 </c:if>
	 <tfoot>
	  <tr>
	   <td colspan=2>
	   <h:commandButton value="Validar" action="#{capitulo.validar}" immediate="true" rendered="#{capitulo.validar}"/>
	   <h:commandButton value="#{capitulo.confirmButton}"
		action="#{capitulo.cadastrar}" /> <h:commandButton
		value="Cancelar" action="#{capitulo.cancelar}" onclick="#{confirm}" /></td>
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

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<f:subview id="menu">
 <%@include file="/portais/docente/menu_docente.jsp"%>
</f:subview>
	<h2>
	<a href="${ctx}/prodocente/nova_producao.jsf">
 	<h:graphicImage title="Voltar para Tela de Novas Produções" value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;"/>
 	</a>
	Cadastro de Livros
	</h2>


   <h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <a href="${ctx}/prodocente/producao/Livro/lista.jsf" >Listar Livros Cadastrados</a>
	 </div>
    </h:form>
	<h:messages showDetail="true"></h:messages>




<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	<h:form>
	<table class="formulario" width="100%">
	<caption class="listagem">Cadastro de Livros</caption>
	 <h:inputHidden value="#{livro.confirmButton}" />
	 <h:inputHidden value="#{livro.obj.id}" />
	 <h:inputHidden value="#{livro.obj.validado}" />
	 <input type="hidden" name="id" value="${livro.obj.id }">
	 <tr>
	 <!-- Coluna 1 -->
	  <td width="50%">
	   <table id="coluna1" >

       <tr>
		<th class="required">Título:</th>
		<td>
		  <h:inputText size="50" value="#{livro.obj.titulo}" readonly="#{livro.readOnly}" />
		</td>
	   </tr>

	  <tr>
		 <th class="required">Autores:</th>
		 <td>
		  <h:inputTextarea value="#{livro.obj.autores}" style="width: 310px; height: 50px;" readonly="#{livro.readOnly}"></h:inputTextarea>
		 </td>
		</tr>

	   <tr>
		 <th>Organizadores/Editores:</th>
		 <td>
		  <h:inputTextarea value="#{livro.obj.organizadores}" style="width: 310px; height: 50px;" readonly="#{livro.readOnly}"></h:inputTextarea>
		 </td>
		</tr>

	   <tr>
		<th class="required">Local de Publicação:</th>
		<td><h:inputText value="#{livro.obj.localPublicacao}" size="50"
					maxlength="255" readonly="#{livro.readOnly}" /></td>
	   </tr>
	   <tr>
		<th class="required">Editora:</th>
		<td><h:inputText value="#{livro.obj.editora}" size="50"
			maxlength="255" readonly="#{livro.readOnly}" /></td>
	   </tr>

       <tr>
		<th>Observações:</th>
	    <td>
			<h:inputTextarea value="#{livro.obj.informacao}" cols="49" rows="3"></h:inputTextarea>
	    </td>
	   </tr>

	   <tr>
		<th class="required">Publicação:</th>
		<td><h:selectOneMenu style="width: 220px;" value="#{livro.obj.tipoRegiao.id}"
					readonly="#{livro.readOnly}" disabledClass="#{livro.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{tipoRegiao.allCombo}" />
				</h:selectOneMenu>
		</td>
	   </tr>
	   <tr>
		<th class="required">Tipo de Participação:</th>
		<td><h:selectOneMenu style="width: 220px;" value="#{livro.obj.tipoParticipacao.id}"
					readonly="#{livro.readOnly}" disabledClass="#{livro.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{livro.tipoParticipacao}" />
				</h:selectOneMenu>
		</td>
	   </tr>
	   <tr>
		<th class="required">Área:</th>
		<td><h:selectOneMenu style="width: 220px;" value="#{livro.obj.area.id}"
					readonly="#{livro.readOnly}"
					disabledClass="#{livro.disableClass}" id="area"  valueChangeListener="#{livro.changeArea}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{area.allCombo}" />
					<a4j:support event="onchange" reRender="subarea" />
				</h:selectOneMenu>
		</td>
	   </tr>
	   <tr>
		<th class="required">Sub-Área:</th>
		<td><h:selectOneMenu style="width: 220px;" id="subarea" value="#{livro.obj.subArea.id}"
					readonly="#{livro.readOnly}"
					disabledClass="#{livro.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{livro.subArea}" />
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
				<t:inputCalendar value="#{livro.obj.dataProducao}"
					size="10" maxlength="10" readonly="#{livro.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataProducao" onkeypress="javascript:formataData(this,event); return ApenasNumeros(event);" />									
			</td>
		</tr>

		<tr>
		 <th class="required">Ano de Referência:</th>
		 <td>
		 	<h:selectOneMenu id="anoReferencia" value="#{livro.obj.anoReferencia}">
				<f:selectItem itemValue="" itemLabel="--SELECIONE--" />
				<f:selectItems value="#{producao.anosCadastrarAnoReferencia}" />
   			</h:selectOneMenu>
		 </td>
		</tr>
		<tr>
		 <th>Nº Páginas:</th>
		 <td>
		  <h:inputText value="#{livro.obj.quantidadePaginas}" size="10"
			  maxlength="255" readonly="#{livro.readOnly}" onkeypress="return ApenasNumeros(event);"/>
         </td>
        </tr>

		<tr>
		 <th>Destaque:</th>
		 <td>
		       <h:selectBooleanCheckbox value="#{livro.obj.destaque}"
					readonly="#{livro.readOnly}" />
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
			  <h:inputText value="#{livro.obj.numeroDocentes}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Docentes de outros Departamentos:</th>
			 <td>
			  <h:inputText value="#{livro.obj.numeroDocentesOutros}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Estudantes:</th>
			 <td>
			  <h:inputText value="#{livro.obj.numeroEstudantes}" size="7" maxlength="7" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Técnicos/Administrativos:</th>
			 <td>
			  <h:inputText value="#{livro.obj.numeroTecnicos}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" />
			 </td>
			</tr>
			<tr>
			 <th>Outros:</th>
			 <td>
			  <h:inputText value="#{livro.obj.numeroOutros}" size="7"	maxlength="6" readonly="#{capitulo.readOnly}" />
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
	 <c:if test="${livro.validar}">
	 <tr>
	  <th>Validar:</th>
	  <td><h:selectOneRadio rendered="#{bean.validar}"
			value="#{livro.obj.validado}">
			<f:selectItem itemValue="false" itemLabel="Inválido" />
			<f:selectItem itemValue="true" itemLabel="Válido" />
		    </h:selectOneRadio>
	  </td>
	 </tr>
	 </c:if>
	 <tfoot>
	  <tr>
	   <td colspan=2>
	   <h:commandButton value="Validar" action="#{livro.validar}" immediate="true" rendered="#{livro.validar}"/>
	   <h:commandButton value="#{livro.confirmButton}"
		action="#{livro.cadastrar}" /> <h:commandButton
		value="Cancelar" action="#{livro.cancelar}" onclick="#{confirm}"/></td>
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

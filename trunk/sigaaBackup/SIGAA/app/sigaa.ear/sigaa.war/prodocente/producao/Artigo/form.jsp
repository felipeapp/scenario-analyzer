<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages></h:messages>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2>
	  <a href="${ctx}/prodocente/nova_producao.jsf">
	  <h:graphicImage title="Voltar para Tela de Novas Produções" value="/img/prodocente/voltarproducoes.gif"style="overflow: visible;"/>
	  </a>
	  Produção Intectual - Cadastro de Artigos, Periódicos, Jornais e Similares
 	</h2>


    <h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <a href="${ctx}/prodocente/producao/Artigo/lista.jsf" >Listar Artigos, Periódicos, Jornais e Similares Cadastrados</a>
	 </div>
    </h:form>

<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	<h:form>
	<h:inputHidden value="#{artigo.confirmButton}" />
	<h:inputHidden value="#{artigo.obj.id}" />
	<h:inputHidden value="#{artigo.obj.validado}" />
	
	<table class="formulario" width="100%" border="1">
	<caption class="listagem">Cadastrar Artigos, Periódicos, Jornais e Similares</caption>
	<tr>
		<td> 
	</tr>

	<tr>	
	 <!-- Coluna 1 -->
	  <td width="67%">
	   <table id="coluna1" width="100%">
	   
			<tr>
				<th class="required">Data de Produção:</th>
				<td>
					<t:inputCalendar value="#{artigo.obj.dataProducao}"
						size="10" maxlength="10" readonly="#{artigo.readOnly}"
						renderAsPopup="true" renderPopupButtonAsImage="true" id="dataProducao" 
						onkeypress="return formataData(this,event)" popupDateFormat="dd/MM/yyyy"/>									
				</td>
			</tr>
	   
	   
			<tr>
				<th class="required">Ano de Referência:</th>
				<td>
					 <h:selectOneMenu id="anoReferencia" value="#{artigo.obj.anoReferencia}">
						<f:selectItem itemValue="" itemLabel="--SELECIONE--" />
						<f:selectItems value="#{producao.anosCadastrarAnoReferencia}" />
   					 </h:selectOneMenu>
				</td>
			</tr>	   
			<tr>
				<th align="left" class="required" width="25%">Título:</th>
				<td>
					<h:inputTextarea value="#{artigo.obj.titulo}" rows="3" readonly="#{artigo.readOnly}" style="width:95%; "/>
				</td>
			</tr>
			<tr>
				<th class="required">Autores:</th>
				<td>
					<h:inputTextarea id="autores" value="#{artigo.obj.autores}" rows="3" style="width:95%; "/>
				</td>
			</tr>
			<tr>
				<th>Editora:</th>
				<td><h:inputText id="editora" value="#{artigo.obj.editora}" style="width:95%;"
					maxlength="255" readonly="#{artigo.readOnly}" /></td>
			</tr>

			<tr>
				<th class="required">Local de Publicação:</th>
				<td><h:inputText id="localPublicacao"
					value="#{artigo.obj.localPublicacao}" maxlength="60" style="width:95%;"
					readonly="#{artigo.readOnly}" /></td>
			</tr>

			<tr>
				<th>Observações:</th>
				<td><h:inputText id="observacoes" value="#{artigo.obj.informacao}" style="width:95%;"
					readonly="#{artigo.readOnly}" /></td>
			</tr>


			<tr>
				<th class="required">Titulo do Periódico:</th>
				<td><h:inputText value="#{artigo.obj.tituloPeriodico}" style="width:95%;"
					maxlength="255" readonly="#{artigo.readOnly}" id="tituloPeriodico" /></td>
			</tr>
			<tr>
				<th align="left" class="required">Tipo do Periódico:</th>
			     <td><h:selectOneMenu style="width: 70%;" value="#{artigo.obj.tipoPeriodico.id}"
					disabled="#{artigo.readOnly}"
					disabledClass="#{artigo.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{tipoPeriodico.allCombo}" />
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th align="left" class="required">Âmbito:</th>
				<td><h:selectOneMenu style="width: 70%;" value="#{artigo.obj.tipoRegiao.id}"
					disabled="#{artigo.readOnly}"
					disabledClass="#{artigo.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{tipoRegiao.allCombo}" />
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th class="required">Tipo de Participação:</th>
				<td><h:selectOneMenu style="width: 70%;" value="#{artigo.obj.tipoParticipacao.id}"
					disabled="#{artigo.readOnly}"
					disabledClass="#{artigo.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{artigo.tipoParticipacao}" />
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th class="required">Área:</th>
				<td><h:selectOneMenu style="width: 70%;" value="#{artigo.obj.area.id}"
					disabled="#{artigo.readOnly}"
					disabledClass="#{artigo.disableClass}" valueChangeListener="#{artigo.changeArea}" id="area">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{area.allCombo}" />
					<a4j:support event="onchange" reRender="subarea" />
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th class="required">Sub-Área:</th>
				<td><h:selectOneMenu style="width: 70%;" value="#{artigo.obj.subArea.id}"
					disabled="#{artigo.readOnly}"
					disabledClass="#{artigo.disableClass}" id="subarea">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{artigo.subArea}" />
				</h:selectOneMenu></td>
			</tr>

	   </table>
	  </td>

	 <!-- Coluna 2 -->

	 <td width="33%" valign="top">
	  <table id="coluna2" >

			<tr>
				<th>Página Inicial:</th>
				<td>
					<h:inputText value="#{artigo.obj.paginaInicial}" size="5"
					maxlength="10" readonly="#{artigo.readOnly}" />
				</td>
			</tr>

			<tr>
		 	    <th>Página Final:</th>
		 	    <td>
					<h:inputText value="#{artigo.obj.paginaFinal}" size="5"
					maxlength="10" readonly="#{artigo.readOnly}" />
			   </td>
			</tr>
			<tr>
				<th>Volume:</th>
				<td>
					<h:inputText value="#{artigo.obj.volume}" size="5"
					maxlength="20" readonly="#{artigo.readOnly}" />
				</td>
			</tr>
			<tr>
					<th>Número:</th>
				<td>
				    <h:inputText value="#{artigo.obj.numero}" size="6" maxlength="6"
					readonly="#{artigo.readOnly}" onkeyup="return formatarInteiro(this);" />
				</td>
			</tr>

			<tr>
			    <th>ISSN/ISBN:</th>
			    <td>
					<h:inputText value="#{artigo.obj.issn}" size="20"
					maxlength="40" readonly="#{artigo.readOnly}" />
				</td>
			</tr>
			<tr>
					<th>Destaque:</th>
				<td>
					<h:selectBooleanCheckbox value="#{artigo.obj.destaque}"
					readonly="#{artigo.readOnly}" styleClass="noborder"/>
				</td>
			</tr>


			<!-- Quantitativos -->

			<tr>
				<td colspan="2">
				<fieldset class=""><legend>Quantitativos</legend>
				<table >
					<tr title="Docentes Incluindo Você">
						<th >Docentes:</th>
						<td>
							<h:inputText alt="Docentes Incluindo Você" title="Docentes Incluindo Você" value="#{artigo.obj.numeroDocentes}"
							size="7" maxlength="6" readonly="#{artigo.readOnly}" />
						</td>
					</tr>
					<tr>
						<th>Docentes de outros Departamentos:</th>
						<td>
							<h:inputText
							value="#{artigo.obj.numeroDocentesOutros}" size="7" maxlength="6"
							readonly="#{artigo.readOnly}" />
						</td>
				   </tr>
				   <tr>
						<th>Estudantes:</th>
						<td>
							<h:inputText value="#{artigo.obj.numeroEstudantes}" size="7"
							maxlength="7" readonly="#{artigo.readOnly}" />
						</td>
				   </tr>
				   <tr>
				   		<th>Técnicos/Administrativos:</th>
				   		<td>
							<h:inputText value="#{artigo.obj.numeroTecnicos}" size="7"
							maxlength="6" readonly="#{artigo.readOnly}" />
						</td>
				   </tr>
				   <tr>
						<th>Outros:</th>
						<td>
						<h:inputText value="#{artigo.obj.numeroOutros}" size="7"
							maxlength="6" readonly="#{artigo.readOnly}" />
						</td>
					</tr>
				</table>
				</fieldset>
				</td>
			</tr>
			<!-- Fim quantitativos -->

	   </table>
	  </td>
	 </tr>

		<!-- Botoes -->
		<c:if test="${artigo.validar}">
		<tr>
			<th>Validar:</th>
			<td><h:selectOneRadio rendered="#{bean.validar}"
				value="#{artigo.obj.validado}">
				<f:selectItem itemValue="false" itemLabel="Inválido" />
				<f:selectItem itemValue="true" itemLabel="Válido" />
			</h:selectOneRadio></td>
		</tr>
		</c:if>

		<tfoot>
			<tr>
				<td colspan=2>
				<h:commandButton value="Validar" action="#{artigo.validar}" immediate="true" rendered="#{artigo.validar}"/>
				<h:commandButton value="#{artigo.confirmButton}"
					action="#{artigo.cadastrar}" /> <h:commandButton value="Cancelar"
					action="#{artigo.cancelar}" onclick="#{confirm}" immediate="true"/></td>
			</tr>
		</tfoot>
		<!-- Fim Botoes -->


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

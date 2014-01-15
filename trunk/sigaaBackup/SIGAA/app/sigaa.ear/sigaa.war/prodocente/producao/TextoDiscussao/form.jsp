<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<f:subview id="menu">
		<%@include file="/portais/docente/menu_docente.jsp"%>
	</f:subview>
	<h2>
	<a href="${ctx}/prodocente/nova_producao.jsf">
 		<h:graphicImage title="Voltar para Tela de Novas Produções" value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;"/>
 	</a>
	Cadastro de Texto de Discussão
	</h2>

   <h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <a href="${ctx}/prodocente/producao/TextoDiscussao/lista.jsf" >Listar Textos de Discussão Cadastrados</a>
	 </div>
    </h:form>

	<h:messages showDetail="true"></h:messages>

<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	<h:form>
	<table class=formulario width="100%">
			<caption class="listagem">Cadastro de Texto de Discussão</caption>
			<h:inputHidden value="#{textoDiscussao.confirmButton}" />
			<h:inputHidden value="#{textoDiscussao.obj.id}" />
			<h:inputHidden value="#{textoDiscussao.obj.validado}" />
			<tr>
			 <!-- Coluna 1 -->
			  <td width="50%">
			   <table id="coluna1" >

		          <!-- Mário aqui vao so dados da coluna 1 -->
					<tr>
							<th class="required">Título:</th>
							<td><h:inputText size="50" maxlength="255"
								value="#{textoDiscussao.obj.titulo}"
								readonly="#{textoDiscussao.readOnly}" /></td>
						</tr>
						<tr>
							<th class="required">Autores:</th>
							<td>
									<h:inputTextarea value="#{textoDiscussao.obj.autores}" cols="49" rows="3" > </h:inputTextarea>
							</td>
						</tr>

						<tr>
						<th class="required">Local de Publicação:</th>
						<td><h:inputText value="#{textoDiscussao.obj.localPublicacao}"
								size="50" maxlength="255" readonly="#{textoDiscussao.readOnly}" /></td>
						</tr>
						<tr>
							<th>Observações:</th>
							<td>
							 <h:inputTextarea value="#{textoDiscussao.obj.informacao}" cols="49" rows="3" ></h:inputTextarea>
							</td>
						</tr>
						<tr>
							<th class="required">Tipo de Participação:</th>
							<td><h:selectOneMenu
								value="#{textoDiscussao.obj.tipoParticipacao.id}"
								disabled="#{textoDiscussao.readOnly}" style="width:200px"
								disabledClass="#{textoDiscussao.disableClass}">
								<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
								<f:selectItems value="#{textoDiscussao.tipoParticipacao}" />
							</h:selectOneMenu></td>
						</tr>
						<tr>
							<th class="required">Área:</th>

							<td><h:selectOneMenu value="#{textoDiscussao.obj.area.id}"
								disabled="#{textoDiscussao.readOnly}" style="width:200px"
								disabledClass="#{textoDiscussao.disableClass}" id="area" valueChangeListener="#{textoDiscussao.changeArea}">
								<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
								<f:selectItems value="#{area.allCombo}" />
								<a4j:support event="onchange" reRender="subarea" />
							</h:selectOneMenu></td>
						</tr>
						<tr>
							<th class="required">Sub-Área:</th>

							<td><h:selectOneMenu value="#{textoDiscussao.obj.subArea.id}"
								disabled="#{textoDiscussao.readOnly}" style="width:200px"
								disabledClass="#{textoDiscussao.disableClass}" id="subarea">
								<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
								<f:selectItems value="#{textoDiscussao.subArea}" />
							</h:selectOneMenu></td>
						</tr>
					<tr>
							<th class="required" >Ano de Referência:</th>
							<td>
								 <h:selectOneMenu id="anoReferencia" value="#{textoDiscussao.obj.anoReferencia}">
									<f:selectItem itemValue="" itemLabel="--SELECIONE--" />
									<f:selectItems value="#{textoDiscussao.anosCadastrarAnoReferencia}" />
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
						<th>Página Inicial</th>
						<td><h:inputText value="#{textoDiscussao.obj.paginaInicial}" size="6" readonly="#{textoDiscussao.readOnly}"/></td>
					</tr>
					<tr>
						<th>Página Final</th>
						<td><h:inputText value="#{textoDiscussao.obj.paginaFinal}" size="6" readonly="#{textoDiscussao.readOnly}"/></td>
					</tr>

					<tr>
						<th>Destaque:</th>
						<td>
						<p  align="left">
						<h:selectBooleanCheckbox value="#{textoDiscussao.obj.destaque}"
							readonly="#{textoDiscussao.readOnly}"  /> </p></td>

					</tr>

					<!-- Quantitativos -->
							<tr>
							 <td colspan="2">
							  <fieldset class=""><legend>Quantitativos</legend>
							   <table>
							    <tr>
								 <th>Docentes (incluindo você):</th>
								 <td>
								  <h:inputText value="#{textoDiscussao.obj.numeroDocentes}" size="7" maxlength="6" readonly="#{textoDiscussao.readOnly}" />
								 </td>
								</tr>
								<tr>
								 <th>Docentes de outros Departamentos:</th>
								 <td>
								  <h:inputText value="#{textoDiscussao.obj.numeroDocentesOutros}" size="7" maxlength="6" readonly="#{textoDiscussao.readOnly}" />
								 </td>
								</tr>
								<tr>
								 <th>Estudantes:</th>
								 <td>
								  <h:inputText value="#{textoDiscussao.obj.numeroEstudantes}" size="7" maxlength="7" readonly="#{textoDiscussao.readOnly}" />
								 </td>
								</tr>
								<tr>
								 <th>Técnicos/Administrativos:</th>
								 <td>
								  <h:inputText value="#{textoDiscussao.obj.numeroTecnicos}" size="7" maxlength="6" readonly="#{textoDiscussao.readOnly}" />
								 </td>
								</tr>
								<tr>
								 <th>Outros:</th>
								 <td>
								  <h:inputText value="#{textoDiscussao.obj.numeroOutros}" size="7"	maxlength="6" readonly="#{textoDiscussao.readOnly}" />
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
			 <c:if test="${textoDiscussao.validar}">
			 <tr>
			  <th>Validar:</th>
			  <td><h:selectOneRadio rendered="#{bean.validar}"
					value="#{textoDiscussao.obj.validado}">
					<f:selectItem itemValue="false" itemLabel="Inválido" />
					<f:selectItem itemValue="true" itemLabel="Válido" />
				    </h:selectOneRadio>
			  </td>
			 </tr>
			 </c:if>
			 <tfoot>
			  <tr>
			   <td colspan=2>
			   <h:commandButton value="Validar" action="#{textoDiscussao.validar}" immediate="true" rendered="#{textoDiscussao.validar}"/>
			   <h:commandButton value="#{textoDiscussao.confirmButton}"
				action="#{textoDiscussao.cadastrar}" /> <h:commandButton
				value="Cancelar" action="#{textoDiscussao.cancelar}" /></td>
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

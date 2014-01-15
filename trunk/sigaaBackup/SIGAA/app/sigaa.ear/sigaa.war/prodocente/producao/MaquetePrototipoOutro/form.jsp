<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<f:subview id="menu">
		<%@include file="/portais/docente/menu_docente.jsp"%>
	</f:subview>
	<h2>
	<a href="${ctx}/prodocente/nova_producao.jsf">
 		<h:graphicImage title="Voltar para Tela de Novas Produções" value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;"/>
 	</a>
	Cadastro de Maquetes, Prototipos, Softwares e Outros
	</h2>

	<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <a href="${ctx}/prodocente/producao/MaquetePrototipoOutro/lista.jsf" >Listar Maquetes, Prototipos, Softwares e Outros Cadastradas</a>
	 </div>
    </h:form>
	<h:form>
	<table class="formulario" width="100%">
	<caption class="listagem">Cadastro de Maquetes, Prototipos, Softwares e Outros</caption>
	 <h:inputHidden value="#{maquetePrototipoOutro.confirmButton}" />
	 <h:inputHidden value="#{maquetePrototipoOutro.obj.id}" />
	 <h:inputHidden value="#{maquetePrototipoOutro.obj.validado}" />
	 <tr>
	 <!-- Coluna 1 -->
	  <td width="50%">
			<table id="coluna1" >

			<tr>
				<th class="required">Título:</th>
				<td>
			 	<h:inputText size="50"
					value="#{maquetePrototipoOutro.obj.titulo}" id="comp3"
					readonly="#{maquetePrototipoOutro.readOnly}" />
				</td>
			</tr>

			<tr>
			 <th class="required">Local:</th>
			 <td>
			    <h:inputText
						value="#{maquetePrototipoOutro.obj.localPublicacao}" size="50" id="comp4"
						maxlength="40" readonly="#{maquetePrototipoOutro.readOnly}" />
			 </td>
			</tr>

			<tr>
				<th>Autores:</th>
				<td>
					<h:inputTextarea value="#{maquetePrototipoOutro.obj.autores}" cols="49" rows="3" id="comp5"/>
				</td>
			</tr>
			<tr>
				<th>Informações Complementares:</th>
				<td>
					<h:inputTextarea value="#{maquetePrototipoOutro.obj.informacao}" cols="49" rows="3" id="comp6"/>
				</td>
			</tr>


			<tr>
				<th class="required">Área:</th>
				<td><h:selectOneMenu style="width: 220px;" value="#{maquetePrototipoOutro.obj.area.id}"
					disabled="#{maquetePrototipoOutro.readOnly}" 
					disabledClass="#{maquetePrototipoOutro.disableClass}" id="area" valueChangeListener="#{maquetePrototipoOutro.changeArea}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{area.allCombo}" />
					<a4j:support event="onchange" reRender="subarea" />
					</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Sub-Área:</th>
				<td><h:selectOneMenu style="width: 220px;" value="#{maquetePrototipoOutro.obj.subArea.id}"
					disabled="#{maquetePrototipoOutro.readOnly}" id="subarea"
					disabledClass="#{maquetePrototipoOutro.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{maquetePrototipoOutro.subArea}" />
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th class="required">Tipo de Participação:</th>
				<td><h:selectOneMenu style="width: 220px;"
					value="#{maquetePrototipoOutro.obj.tipoParticipacao.id}"
					disabled="#{maquetePrototipoOutro.readOnly}" id="comp7"
					disabledClass="#{maquetePrototipoOutro.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{maquetePrototipoOutro.tipoParticipacaoSelect}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Âmbito:</th>
				<td><h:selectOneMenu style="width: 220px;"
				value="#{maquetePrototipoOutro.obj.tipoRegiao.id}"
					disabled="#{maquetePrototipoOutro.readOnly}" id="comp8"
					disabledClass="#{maquetePrototipoOutro.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{tipoRegiao.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Tipo de Produção Tecnológica:</th>
				<td><h:selectOneMenu style="width: 220px;" id="comp9"
					value="#{maquetePrototipoOutro.obj.tipoProducaoTecnologica.id}"
					disabled="#{maquetePrototipoOutro.readOnly}"
					disabledClass="#{maquetePrototipoOutro.disableClass}">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{tipoProducaoTecnologica.allCombo}" />
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
						<t:inputCalendar value="#{maquetePrototipoOutro.obj.dataProducao}"
							size="10" maxlength="10" readonly="#{maquetePrototipoOutro.readOnly}"
							renderAsPopup="true" renderPopupButtonAsImage="true" id="dataProducao" onkeypress="javascript:formataData(this,event); return ApenasNumeros(event);" />									
					</td>
				</tr>
				<tr>
					<th class="required">Ano de Referência:</th>
					<td>
						 <h:selectOneMenu id="anoReferencia" value="#{maquetePrototipoOutro.obj.anoReferencia}">
							<f:selectItem itemValue="" itemLabel="--SELECIONE--" />
							<f:selectItems value="#{producao.anosCadastrarAnoReferencia}" />
   						</h:selectOneMenu>
				    </td>
				</tr>

				<tr>
					<th>Premiada:</th>
					<td>
					 <h:selectBooleanCheckbox
							value="#{maquetePrototipoOutro.obj.premiada}" id="comp11"
							readonly="#{maquetePrototipoOutro.readOnly}" />
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
					  <h:inputText value="#{maquetePrototipoOutro.obj.numeroDocentes}" size="7" maxlength="6" readonly="#{maquetePrototipoOutro.readOnly}" id="comp12"/>
					 </td>
					</tr>
					<tr>
					 <th>Docentes de outros Departamentos:</th>
					 <td>
					  <h:inputText value="#{maquetePrototipoOutro.obj.numeroDocentesOutros}" size="7" maxlength="6" readonly="#{maquetePrototipoOutro.readOnly}" id="comp13"/>
					 </td>
					</tr>
					<tr>
					 <th>Estudantes:</th>
					 <td>
					  <h:inputText value="#{maquetePrototipoOutro.obj.numeroEstudantes}" size="7" maxlength="7" readonly="#{maquetePrototipoOutro.readOnly}" id="comp14"/>
					 </td>
					</tr>
					<tr>
					 <th>Técnicos/Administrativos:</th>
					 <td>
					  <h:inputText value="#{maquetePrototipoOutro.obj.numeroTecnicos}" size="7" maxlength="6" readonly="#{maquetePrototipoOutro.readOnly}" id="comp15"/>
					 </td>
					</tr>
					<tr>
					 <th>Outros:</th>
					 <td>
					  <h:inputText value="#{maquetePrototipoOutro.obj.numeroOutros}" size="7"	maxlength="6" readonly="#{capitulo.readOnly}" id="comp16"/>
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
	 <c:if test="${maquetePrototipoOutro.validar}">
	 <tr>
	  <th>Validar:</th>
	  <td><h:selectOneRadio rendered="#{bean.validar}" id="comp20"
			value="#{maquetePrototipoOutro.obj.validado}">
			<f:selectItem itemValue="false" itemLabel="Inválido" />
			<f:selectItem itemValue="true" itemLabel="Válido" />
		    </h:selectOneRadio>
	  </td>
	 </tr>
	 </c:if>
	 <tfoot>
	  <tr>
	   <td colspan=2>
	   <h:commandButton value="Validar" action="#{maquetePrototipoOutro.validar}" immediate="true" rendered="#{maquetePrototipoOutro.validar}" id="validar"/>
	   <h:commandButton value="#{maquetePrototipoOutro.confirmButton}" id="cadastrar"
		action="#{maquetePrototipoOutro.cadastrar}" /> <h:commandButton
		value="Cancelar" action="#{maquetePrototipoOutro.cancelar}" onclick="#{confirm}" id="cancelar"/></td>
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
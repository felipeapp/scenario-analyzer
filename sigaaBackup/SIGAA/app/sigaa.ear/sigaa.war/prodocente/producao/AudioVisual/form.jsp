<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<f:subview id="menu">
<%@include file="/portais/docente/menu_docente.jsp"%>
</f:subview>
	<h2>
	  <a href="${ctx}/prodocente/nova_producao.jsf">
	  <h:graphicImage title="Voltar para Tela de Novas Produções" value="/img/prodocente/voltarproducoes.gif"style="overflow: visible;"/>
	  </a>
	  Cadastro de Audio Visual
	</h2>

    <h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <a href="${ctx}/prodocente/producao/AudioVisual/lista.jsf" >Listar Apresentações Audio Visuais Cadastradas</a>
	 </div>
    </h:form>

<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx -->
	<h:form id="form">
	<table class="formulario" width="100%">
	<caption class="listagem">Cadastro de Apresentações Audio Visuais</caption>
	 <h:inputHidden value="#{audioVisual.confirmButton}" id="strConfirmButton"/>
	 <h:inputHidden value="#{audioVisual.obj.id}" id="idObj" />
	 <h:inputHidden value="#{audioVisual.obj.validado}" id="validado" />

	 <tr>
	 <!-- Coluna 1 -->
	  <td width="50%">
	   <table id="coluna1" >


			<tr>
				<th class="required">Título:</th>
				<td><h:inputText size="50" value="#{audioVisual.obj.titulo}"
					readonly="#{audioVisual.readOnly}" id="titulo" /></td>
			</tr>
			<tr>
				<th class="required">Autores:</th>
				<td>
				 <h:inputTextarea value="#{audioVisual.obj.autores}" cols="49" rows="3" id="autores"></h:inputTextarea>
				</td>
			</tr>


			<tr>
				<th class="required">Local:</th>
				<td><h:inputText size="50" value="#{audioVisual.obj.local}"
					readonly="#{audioVisual.readOnly}" id="local"/></td>
			</tr>


			<tr>
				<th>Veículo:</th>
				<td><h:inputText value="#{audioVisual.obj.veiculo}" size="50"
					maxlength="255" readonly="#{audioVisual.readOnly}" id="veiculo"/></td>
			</tr>
			<tr>
				<th>Informações Complementares:</th>
				<td>
				 <h:inputTextarea value="#{audioVisual.obj.informacao}" cols="49" rows="3" id="informacao"></h:inputTextarea>
				</td>
			</tr>

			<tr>
				<th class="required">Tipo de Participação:</th>
				<td><h:selectOneMenu style="width: 220px;" value="#{audioVisual.obj.tipoParticipacao.id}"
					disabled="#{audioVisual.readOnly}"
					disabledClass="#{audioVisual.disableClass}" id="tipoParticipacao">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{audioVisual.tipoParticipacao}" />
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th class="required">Âmbito:</th>

				<td><h:selectOneMenu style="width: 220px;" value="#{audioVisual.obj.tipoRegiao.id}"
					disabled="#{audioVisual.readOnly}"
					disabledClass="#{audioVisual.disableClass}" id="tipoRegiao">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{tipoRegiao.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Tipo Artístico:</th>

				<td><h:selectOneMenu style="width: 220px;" value="#{audioVisual.obj.tipoArtistico.id}"
					disabled="#{audioVisual.readOnly}"
					disabledClass="#{audioVisual.disableClass}" id="tipoArtistico">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{audioVisual.tipoArtistico}" />
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th class="required">Classificação:</th>

				<td><h:selectOneMenu style="width: 220px;" value="#{audioVisual.obj.subTipoArtistico.id}"
					disabled="#{audioVisual.readOnly}"
					disabledClass="#{audioVisual.disableClass}" id="subTipoArtistico">
					<f:selectItem itemValue="0" itemLabel="--SELECIONE--" />
					<f:selectItems value="#{audioVisual.tipo}" />
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
				<t:inputCalendar value="#{audioVisual.obj.dataProducao}"
					size="10" maxlength="10" readonly="#{audioVisual.readOnly}"
					renderAsPopup="true" renderPopupButtonAsImage="true" id="dataProducao" onkeypress="javascript:formataData(this,event); return ApenasNumeros(event);" />									
			</td>
		</tr>


			<tr>
				<th class="required">Ano de Referência:</th>
				<td>
					<h:selectOneMenu id="anoReferencia" value="#{audioVisual.obj.anoReferencia}">
						<f:selectItem itemValue="" itemLabel="--SELECIONE--" />
						<f:selectItems value="#{producao.anosCadastrarAnoReferencia}" />
   					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>Duração:</th>
				<td><h:inputText value="#{audioVisual.obj.duracaoDivulgacao}"
					size="6" maxlength="7" readonly="#{audioVisual.readOnly}" id="duracaoDivulgacao"/>
					<span style="color: #c3c3c3; font-size: 10px;"><b>Em minutos</b></span>
				</td>
			</tr>
			<tr>
			 <th>Divulgado:</th>
			    <td>
				<h:selectBooleanCheckbox value="#{audioVisual.obj.divulgado}"
					readonly="#{audioVisual.readOnly}" id="divulgado"/>
				</td>
			</tr>
			<tr>
			 <th>Premiado:</th>
			    <td>
				<h:selectBooleanCheckbox value="#{audioVisual.obj.premiada}"
					readonly="#{audioVisual.readOnly}" id="premiado"/>
				</td>
			</tr>


		<!-- Quantitativos -->
		<tr>
		 <td colspan="2">
		  <fieldset class="formulario"><legend>Quantitativos</legend>
		   <table>
		    <tr>
			 <th>Docentes (incluindo você):</th>
			 <td>
			  <h:inputText value="#{audioVisual.obj.numeroDocentes}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" id="numeroDocentes"/>
			 </td>
			</tr>
			<tr>
			 <th>Docentes de outros Departamentos:</th>
			 <td>
			  <h:inputText value="#{audioVisual.obj.numeroDocentesOutros}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" id="numeroDocentesOutros"/>
			 </td>
			</tr>
			<tr>
			 <th>Estudantes:</th>
			 <td>
			  <h:inputText value="#{audioVisual.obj.numeroEstudantes}" size="7" maxlength="7" readonly="#{capitulo.readOnly}" id="numeroEstudantes"/>
			 </td>
			</tr>
			<tr>
			 <th>Técnicos/Administrativos:</th>
			 <td>
			  <h:inputText value="#{audioVisual.obj.numeroTecnicos}" size="7" maxlength="6" readonly="#{capitulo.readOnly}" id="numeroTecnicos"/>
			 </td>
			</tr>
			<tr>
			 <th>Outros:</th>
			 <td>
			  <h:inputText value="#{audioVisual.obj.numeroOutros}" size="7"	maxlength="6" readonly="#{capitulo.readOnly}" id="numeroOutros"/>
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
	 <c:if test="${audioVisual.validar}">
	 <tr>
	  <th>Validar:</th>
	  <td><h:selectOneRadio rendered="#{bean.validar}"
			value="#{audioVisual.obj.validado}" id="radiobtnValidado">
			<f:selectItem itemValue="false" itemLabel="Inválido" />
			<f:selectItem itemValue="true" itemLabel="Válido" />
		    </h:selectOneRadio>
	  </td>
	 </tr>
	 </c:if>
	 <tfoot>
	  <tr>
	   <td colspan=2>
	   <h:commandButton value="Validar" action="#{audioVisual.validar}" immediate="true" rendered="#{audioVisual.validar}" id="btnValidar"/>
	   <h:commandButton value="#{audioVisual.confirmButton}"
		action="#{audioVisual.cadastrar}" id="btnCadastrar"/> <h:commandButton
		value="Cancelar" action="#{audioVisual.cancelar}" onclick="#{confirm}" id="btnCancelar"/></td>
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
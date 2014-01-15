<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2 class="title"> <ufrn:subSistema /> &gt; Identificação de Coordenador</h2>

	<h:form id="form">
		<h:inputHidden value="#{coordenacaoCurso.confirmButton}" />
		<h:inputHidden value="#{coordenacaoCurso.obj.id}" />

		<table class="formulario">
			<caption class="formulario"> <h:outputText id="rotuloFormulario" value="#{coordenacaoCurso.rotuloFormulario}"/> </caption>
			<tr>
				<th width="20%" class="required">Coordenador:</th>
				<td>
					<h:inputHidden id="idServidor" value="#{coordenacaoCurso.obj.servidor.id}"/>
					<h:inputText id="nomeServidor" value="#{coordenacaoCurso.obj.servidor.pessoa.nome}" size="70" onkeyup="CAPS(this)"
						disabled="#{coordenacaoCurso.readOnly}" readonly="#{coordenacaoCurso.readOnly}" />

					<c:if test="${acesso.complexoHospitalar}">
						<ajax:autocomplete source="form:nomeServidor" target="form:idServidor"
							baseUrl="/sigaa/ajaxServidor" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="tipo=todos,situacao=ativo"
							parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
					</c:if>
					<c:if test="${not acesso.complexoHospitalar}">
						<ajax:autocomplete source="form:nomeServidor" target="form:idServidor"
							baseUrl="/sigaa/ajaxDocente" className="autocomplete"
							indicator="indicatorDocente" minimumCharacters="3" parameters="tipo=ufrn,situacao=ativo"
							parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicatorDocente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
					</c:if>
				</td>
			</tr>
			<ufrn:subSistema teste="graduacao">
				<tr>
				<th class="required">Curso:</th>
				<td>
					<a4j:region>
					<h:selectOneMenu id="curso" value="#{coordenacaoCurso.obj.curso.id}" style="width: 80%"
						valueChangeListener="#{coordenacaoCurso.carregarDadosContato}" >
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{ cursoGrad.allCombo }"/>
						<a4j:support event="onchange" reRender="paginaOficialCoordenacao, emailContato, telefoneContato" />
					</h:selectOneMenu>
					<a4j:status>
		                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
		            </a4j:status>
					</a4j:region>
				</td>
				</tr>
			</ufrn:subSistema>
			<ufrn:subSistema teste="stricto">
				<tr>
				<th class="required">Programa:</th>
				<td>
					<h:selectOneMenu id="programa" value="#{coordenacaoCurso.obj.unidade.id}" style="width: 80%">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{ unidade.allProgramaPosCombo }"/>
					</h:selectOneMenu>
				</td>
				</tr>
			</ufrn:subSistema>
			<ufrn:subSistema teste="lato">
				<tr>
				<th class="required">Curso:</th>
				<td>
					<h:selectOneMenu id="cursoLato" value="#{coordenacaoCurso.obj.curso.id}" style="width: 95%">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{ lato.allCombo }"/>
					</h:selectOneMenu>
				</td>
				</tr>
			</ufrn:subSistema>
			<ufrn:subSistema teste="tecnico">
				<tr>
				<th class="required">Curso:</th>
				<td>
					<h:selectOneMenu id="cursoTecnico" value="#{coordenacaoCurso.obj.curso.id}" style="width: 80%">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{ cursoTecnicoMBean.allUnidadeCombo }"/>
					</h:selectOneMenu>
				</td>
				</tr>
			</ufrn:subSistema>
			<ufrn:subSistema teste="residencia">
				<tr>
				<th class="required">Programa de Residência:</th>
				<td>
					<h:selectOneMenu id="programaResidencia" value="#{coordenacaoCurso.obj.unidade.id}" style="width: 80%">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{ unidade.allProgramaResidenciaCombo }"/>
					</h:selectOneMenu>
				</td>
				</tr>
			</ufrn:subSistema>
			<tr>
				<th class="required"> Função: </th>
				<td>
					<h:selectOneMenu id="selectCargo" value="#{coordenacaoCurso.obj.cargoAcademico.id}" style="width: 45%">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{ cargoAcademico.coordenadorEViceCombo }"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required"> Início do Mandato: </th>
				<td><t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" id="inicioMandato" 
					maxlength="10" popupDateFormat="dd/MM/yyyy" value="#{coordenacaoCurso.obj.dataInicioMandato}"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))" >
					<f:converter converterId="convertData"/> </t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th class="required"> Fim do Mandato: </th>
				<td><t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" id="fimMandato" 
					maxlength="10" popupDateFormat="dd/MM/yyyy" value="#{coordenacaoCurso.obj.dataFimMandato}"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))" >
					<f:converter converterId="convertData"/> </t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th width="20%">Página oficial da coordenação:</th>
				<td>
					<h:inputText value="#{coordenacaoCurso.obj.paginaOficialCoordenacao}" size="60" maxlength="150" id="paginaOficialCoordenacao"/>
				</td>
			</tr>
			<tr>
				<th width="20%">E-mail:</th>
				<td>
					<h:inputText value="#{coordenacaoCurso.obj.emailContato}" id="emailContato" maxlength="50"/>
				</td>
			</tr>
			<tr>
				<th width="20%">Telefone/ramal 1:</th>
				<td>
					 <h:inputText value="#{coordenacaoCurso.obj.telefoneContato1}" onkeyup="return formatarInteiro(this);" 
					 	maxlength="8" size="8" id="telefoneContato1" /> /
					 <h:inputText value="#{coordenacaoCurso.obj.ramalTelefone1}" onkeyup="return formatarInteiro(this);" 
					 	 maxlength="5" size="5" id="ramalTelefone1" /> (ramal)
				</td>
			</tr>
			<tr>
				<th width="20%">Telefone/ramal 2:</th>
				<td>
					<h:inputText value="#{coordenacaoCurso.obj.telefoneContato2}" onkeyup="return formatarInteiro(this);" 
					 	maxlength="8" size="8" id="telefoneContato2" /> /
					 <h:inputText value="#{coordenacaoCurso.obj.ramalTelefone2}" onkeyup="return formatarInteiro(this);" 
					 	 maxlength="5" size="5" id="ramalTelefone2" /> (ramal)
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="<< Voltar" id="btnVoltar" action="#{coordenacaoCurso.voltar}" rendered="#{coordenacaoCurso.obj.id != 0}" />
						<h:commandButton value="#{coordenacaoCurso.confirmButton}" action="#{coordenacaoCurso.cadastrar}"  id="cadastrar" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{coordenacaoCurso.cancelar}" immediate="true" id="cancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

	<script type="text/javascript">$('form:nomeServidor').focus();</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

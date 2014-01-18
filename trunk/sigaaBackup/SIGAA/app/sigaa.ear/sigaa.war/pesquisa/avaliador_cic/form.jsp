<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2 class="title"><ufrn:subSistema /> &gt; Avaliadores do CIC</h2>

	<h:form id="form">
		<table class="formulario" width="100%">
			<caption class="formulario">Dados do Avaliador</caption>
			
			<tr>
				<th class="obrigatorio">Tipo de Usuário:</th>
				<td>
					<h:selectOneRadio id="tipoUsuario" value="#{avaliadorCIC.obj.tipoUsuario}" >
						<f:selectItems value="#{avaliadorCIC.comboStatusPessoa }" />
						<a4j:support event="onclick" reRender="form" />
					</h:selectOneRadio>
				</td>
			</tr>
								
			<tr>
				<th class="obrigatorio">Congresso:</th>
				<td>
					<h:selectOneMenu id="congresso" value="#{avaliadorCIC.obj.congresso.id}" readonly="#{avaliadorCIC.readOnly}">
						<f:selectItems value="#{avaliadorCIC.allCongressosCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<t:htmlTag value="tr" rendered="#{avaliadorCIC.obj.usuarioDocente}" id="docenteAuto">
				<th class="obrigatorio">Docente: </th>
				<td>
					<h:inputHidden id="idServidor" value="#{avaliadorCIC.obj.docente.id}"/>
					<h:inputText id="nomeServidor" value="#{avaliadorCIC.obj.docente.pessoa.nome}" size="70" onkeyup="CAPS(this);" readonly="#{avaliadorCIC.readOnly}"/>
					<ajax:autocomplete source="form:nomeServidor" target="form:idServidor"
						baseUrl="/sigaa/ajaxDocente" className="autocomplete"
						indicator="indicatorDocente" minimumCharacters="3" parameters="tipo=ufrn,situacao=ativo"
						parser="new ResponseXmlToHtmlListParser()" />
					<span id="indicatorDocente" style="display:none; "> 
					<img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> 
					</span>
				</td>
			</t:htmlTag>
			
			<t:htmlTag value="tr" rendered="#{ avaliadorCIC.obj.usuarioDiscente  }" id="discenteAuto">
				<th class="obrigatorio">Discente:</th>
				<td>
					<h:inputHidden id="idDiscente" value="#{ avaliadorCIC.obj.discente.id }"></h:inputHidden>
					<h:inputText id="nomeDiscente" value="#{ avaliadorCIC.obj.discente.pessoa.nome}"	 onkeyup="CAPS(this);" size="70" />

					<ajax:autocomplete source="form:nomeDiscente" target="form:idDiscente" baseUrl="/sigaa/ajaxDiscente"
							className="autocomplete" indicator="indicatorDiscente" minimumCharacters="3" 
							parameters="nivel=ufrn"	parser="new ResponseXmlToHtmlListParser()" />
					<span id="indicatorDiscente" style="display: none;"> <img src="/sigaa/img/indicator.gif" /> </span>
					<ufrn:help img="/img/ajuda.gif">Apenas os Discentes Ativos da ${ configSistema['siglaInstituicao'] } serão listados</ufrn:help>
				</td>
			</t:htmlTag>
			
			<tr>
				<th>Área de Conhecimento: </th>
				<td>
					<h:selectOneMenu id="area" value="#{ avaliadorCIC.obj.area.id }" readonly="#{avaliadorCIC.readOnly}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{area.allGrandeAreas}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Tipo de Avaliador:</th>
				<td align="left"> 
					<h:selectBooleanCheckbox value="#{avaliadorCIC.obj.avaliadorResumo}" readonly="#{avaliadorCIC.readOnly}"/> 
					Avaliador de Resumo
					&nbsp;&nbsp;&nbsp;&nbsp;
				 	<h:selectBooleanCheckbox value="#{avaliadorCIC.obj.avaliadorApresentacao}" readonly="#{avaliadorCIC.readOnly}"/> 
				 	Avaliador de Apresentação 
				</td>
			</tr>			
			
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton value="#{avaliadorCIC.confirmButton}"
						action="#{avaliadorCIC.cadastrar}" /> <h:commandButton value="Cancelar"
						action="#{avaliadorCIC.cancelarListar}" onclick="#{confirm}"/></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>

	<script type="text/javascript">$('form:congresso').focus();</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

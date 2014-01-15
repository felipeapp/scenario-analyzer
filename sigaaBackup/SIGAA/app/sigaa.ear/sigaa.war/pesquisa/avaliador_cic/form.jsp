<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2 class="title"><ufrn:subSistema /> &gt; Avaliadores do CIC</h2>

	<center>
			<h:form>
			<div class="infoAltRem" style="text-align: left; width: 100%">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{avaliadorCIC.listar}"/>
			</div>
			</h:form>
	</center>

	<h:form id="form">
		<h:inputHidden value="#{avaliadorCIC.confirmButton}" />
		<h:inputHidden value="#{avaliadorCIC.obj.id}" />

		<table class="formulario" width="100%">
			<caption class="formulario">Dados do Avaliador</caption>
			<tr>
				<th class="required">Congresso:</th>
				<td>
					<h:selectOneMenu id="congresso" value="#{avaliadorCIC.obj.congresso.id}" readonly="#{avaliadorCIC.readOnly}">
						<f:selectItems value="#{avaliadorCIC.allCongressosCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Docente: </th>
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
			</tr>
			<tr>
				<th class="required">Área de Conhecimento: </th>
				<td>
					<h:selectOneMenu id="area" value="#{avaliadorCIC.obj.area.id}" readonly="#{avaliadorCIC.readOnly}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{area.allGrandeAreas}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td colspan="2" align="center"> 
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
						action="#{avaliadorCIC.cancelar}" onclick="#{confirm}"/></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>

	<script type="text/javascript">$('form:congresso').focus();</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

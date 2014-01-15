<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2 class="title"><ufrn:subSistema /> &gt; Colaboradores Voluntários</h2>

	<h:form id="form">
		<h:inputHidden value="#{colaboradorVoluntario.confirmButton}" />
		<h:inputHidden value="#{colaboradorVoluntario.obj.id}" />

		<table class="formulario">
			<caption class="formulario">Dados do Colaborador Voluntário</caption>
			<tr>
				<th class="required">Servidor: </th>
				<td>
					<h:inputHidden id="idServidor" value="#{colaboradorVoluntario.obj.servidor.id}"/>
					<h:inputText id="nomeServidor" value="#{colaboradorVoluntario.obj.servidor.pessoa.nome}" size="70" onkeyup="CAPS(this);" readonly="#{colaboradorVoluntario.readOnly}"/>
					<ajax:autocomplete source="form:nomeServidor" target="form:idServidor"
						baseUrl="/sigaa/ajaxDocente" className="autocomplete"
						indicator="indicatorDocente" minimumCharacters="3" parameters="tipo=ufrn,situacao=inativo"
						parser="new ResponseXmlToHtmlListParser()" />
					<span id="indicatorDocente" style="display:none; "> 
					<img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> 
					</span>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton value="#{colaboradorVoluntario.confirmButton}"
						action="#{colaboradorVoluntario.cadastrar}" /> <h:commandButton value="Cancelar"
						action="#{colaboradorVoluntario.cancelar}" onclick="#{confirm}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>

	<script type="text/javascript">$('form:nomeServidor').focus();</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

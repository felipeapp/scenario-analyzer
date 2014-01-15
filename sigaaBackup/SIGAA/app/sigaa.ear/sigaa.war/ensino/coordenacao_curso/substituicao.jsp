<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2 class="title"><ufrn:subSistema /> &gt; Substituição de Coordenadores</h2>

	<h:form id="form">
		<h:inputHidden value="#{coordenacaoCurso.confirmButton}" />
		<h:inputHidden value="#{coordenacaoCurso.obj.id}" />

		<table class="formulario" width="90%">
		<caption class="formulario">Dados da Substituição</caption>
			<tr>
				<ufrn:subSistema teste="graduacao">
					<th>Curso:</th>
					<td><h:outputText value="#{ coordenacaoCurso.coordenadorAntigo.curso.descricaoCompleta }"/> </td>
				</ufrn:subSistema>
				<ufrn:subSistema teste="stricto">
					<th>Programa:</th>
					<td><h:outputText value="#{ coordenacaoCurso.coordenadorAntigo.unidade.nome }"/> </td>
				</ufrn:subSistema>
				<ufrn:subSistema teste="lato">
					<th>Curso:</th>
					<td><h:outputText value="#{ coordenacaoCurso.coordenadorAntigo.curso.descricaoCompleta }"/> </td>
				</ufrn:subSistema>
			</tr>
			<tr>
				<th><b>Coordenador Atual</b>:</th>
				<td>
					<h:outputText value="#{ coordenacaoCurso.coordenadorAntigo.servidor.nome }"/>
					<i><small>(<ufrn:format type="data" name="coordenacaoCurso" property="coordenadorAntigo.dataInicioMandato"/> a <ufrn:format type="data" name="coordenacaoCurso" property="coordenadorAntigo.dataFimMandato"/>) </small></i>
				</td>
			</tr>
			<tr>
				<th class="required">Novo Coordenador:</th>
				<td>
					<h:inputHidden id="idServidor" value="#{coordenacaoCurso.obj.servidor.id}"/>
					<h:inputText id="nomeServidor" value="#{coordenacaoCurso.obj.servidor.pessoa.nome}" size="70" onkeyup="CAPS(this)"
						disabled="#{coordenacaoCurso.readOnly}" readonly="#{coordenacaoCurso.readOnly}" />

					<ajax:autocomplete source="form:nomeServidor" target="form:idServidor"
							baseUrl="/sigaa/ajaxDocente" className="autocomplete"
							indicator="indicatorDocente" minimumCharacters="3" parameters="tipo=ufrn,situacao=ativo"
							parser="new ResponseXmlToHtmlListParser()" />

					<span id="indicatorDocente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
				</td>
			</tr>
			<tr>
				<th class="required"> Início do Mandato: </th>
				<td>
					<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" popupDateFormat="dd/MM/yyyy"
							value="#{coordenacaoCurso.obj.dataInicioMandato}" id="inicioMandato" onkeypress="return formataData(this, event)" maxlength="10">
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th class="required"> Fim do Mandato: </th>
				<td>
					<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" popupDateFormat="dd/MM/yyyy"
							value="#{coordenacaoCurso.obj.dataFimMandato}" id="fimMandato" onkeypress="return formataData(this, event)" maxlength="10">
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{coordenacaoCurso.confirmButton}" id="btnCadastrar" action="#{coordenacaoCurso.cadastrar}" />
						<h:commandButton value="<< Voltar" id="btnVoltar" action="#{coordenacaoCurso.voltar}" />
					 	<h:commandButton value="Cancelar" onclick="#{confirm}" id="btnCancelar" action="#{coordenacaoCurso.cancelar}" />
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

	<script type="text/javascript">$('form:nomeUsuario').focus();</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

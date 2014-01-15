<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<style>

	table.formulario tr.matriculaCompulsoria td {
		background: #EEE;
		text-align: center;
		font-style: italic;
	}
</style>

<f:view>
	<%@include file="/lato/menu_coordenador.jsp" %>
	<h2> <ufrn:subSistema /> &gt; ${registroAtividade.descricaoOperacao} &gt; Registro da Atividade </h2>

	<c:set var="discente" value="#{registroAtividade.obj.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>

	<h:form id="form">
	<table class="formulario" style="width: 90%">
		<caption> Informe os detalhes do registro da atividade </caption>
		<tbody>
		<tr>
			<th style="font-weight: bold;"> Atividade: </th>
			<td> ${registroAtividade.obj.componente.codigoNome} </td>
		</tr>
		<tr>
			<th style="font-weight: bold;"> Tipo da Atividade: </th>
			<td> ${registroAtividade.obj.componente.tipoAtividade.descricao} </td>
		</tr>

		<c:if test="${ registroAtividade.matricula || registroAtividade.validacao }">
			<tr>
				<th class="required"> Ano-Período: </th>
				<td>
					<h:inputText id="ano" size="4" maxlength="4" value="#{registroAtividade.obj.ano}" onkeyup="return(formatarInteiro(this))"/> 
					- <h:inputText id="periodo" size="1" maxlength="1" value="#{registroAtividade.obj.periodo}" onkeyup="return(formatarInteiro(this))"/>
				</td>
			</tr>
			<tr>
				<th class="required"> Data de Início: </th>
				<td>
					<h:selectOneMenu id="mesInicio" value="#{registroAtividade.obj.mes}">
						<f:selectItems value="#{registroAtividade.meses}"/>
					</h:selectOneMenu>&nbsp;/&nbsp;
					<h:selectOneMenu id="anoInicio" value="#{registroAtividade.obj.anoInicio}">
						<f:selectItems value="#{registroAtividade.anos}"/>
					</h:selectOneMenu>
				</td>
			</tr>
		</c:if>

		<c:if test="${ registroAtividade.consolidacao }">
			<tr>
				<th> Ano Período: </th>
				<td> ${registroAtividade.obj.ano} . ${registroAtividade.obj.periodo} </td>
			</tr>
			<tr>
				<th> Data de Início: </th>
				<td>
				<ufrn:format type="mes" valor="${registroAtividade.obj.mes - 1}" /> / ${registroAtividade.obj.anoInicio}
				</td>
			</tr>
		</c:if>
		<tr style="${registroAtividade.idOrientador > 0 ? '' : 'display: none;'}" id="orientadorSetado">
			<th class="rotulo"> Orientador: </th>
			<td>
				<h:outputText value="#{registroAtividade.nomeOrientador}"/>
				<a href="#" onclick="habilitaSelecaoOrientador()">Alterar</a>
			</td>
		</tr>
		<tr style="${registroAtividade.idOrientador > 0 ? 'display: none;' : ''}" id="escolheOrientador">
			<th class="required"> Orientador: </th>
			<td>
				<input type="hidden" id="buscaAjaxDocente">
				<table class="buscaAjax" >
					<tr class="titulo">
						<td width="5%">
							<input type="radio" name="tipoAjaxDocente_1" onclick="buscarDocentePor('buscaAjaxDocenteUFRN');resetNome();"
								value="internoLato" id="buscaAjaxDocenteUFRN" class="noborder">
						</td>
						<td width="20%" align="left">
							<label onclick="buscarDocentePor('buscaAjaxDocenteUFRN')">Docente Interno</label>
						</td>
						<td width="5%">
							<input type="radio" name="tipoAjaxDocente_1" onclick="buscarDocentePor('buscaAjaxDocenteExterno');resetNome();"
								value="externoLato" id="buscaAjaxDocenteExterno" class="noborder">
						</td>
						<td align="left">
							<label onclick="buscarDocentePor('buscaAjaxDocenteExterno')">Somente externos</label>
						</td>
					</tr>
					<tr>
						<td colspan="6" style="height: 25px">
							<h:inputHidden value="#{registroAtividade.idOrientador}" id="idDocente"/>
							<h:inputText id="nomeDocente" value="#{registroAtividade.nomeOrientador}" size="70" maxlength="80"/>
							
							<ajax:autocomplete source="form:nomeDocente" target="form:idDocente"
								baseUrl="/sigaa/ajaxDocente" className="autocomplete"
								indicator="indicatorDocente" minimumCharacters="3" parameters="tipo={buscaAjaxDocente}"
								parser="new ResponseXmlToHtmlListParser()" />
							<span id="indicatorDocente" style="display:none;">
							<img src="/sigaa/img/indicator.gif" /></span>
						</td>
					</tr>
				</table>
				
				<script type="text/javascript">
					function resetNome(){
						$('form:nomeDocente').value = '';
					}	
				
					function buscarDocentePor(radio) {
						$('buscaAjaxDocente').value = $(radio).value;
						marcaCheckBox(radio);
						$('form:nomeDocente').focus();
					}
					buscarDocentePor('buscaAjaxDocenteUFRN');
					<c:if test="${not empty registroAtividade.obj.registroAtividade && not empty registroAtividade.obj.registroAtividade.orientador &&
							empty registroAtividade.obj.registroAtividade.orientador.orientador}">
							buscarDocentePor('buscaAjaxDocenteExterno');
					</c:if>
				</script>
			</td>
		</tr>

		<c:if test="${ registroAtividade.consolidacao || registroAtividade.validacao }">
			<tr>
				<td colspan="2" class="subFormulario" style="text-align: center;"> Resultado </td>
			</tr>

			<tr>
				<th class="required"> Data Final: </th>
				<td>
					<h:selectOneMenu id="mesFim" value="#{registroAtividade.obj.mesFim}">
						<f:selectItems value="#{registroAtividade.meses}"/>
					</h:selectOneMenu>&nbsp;/&nbsp;
					<h:selectOneMenu id="anoFim" value="#{registroAtividade.obj.anoFim}">
						<f:selectItems value="#{registroAtividade.anos}"/>
					</h:selectOneMenu>
				</td>
			</tr>

			<c:if test="${ registroAtividade.obj.registroAtividade.atividade.necessitaMediaFinal }">
				<tr>
				<c:if test="${registroAtividade.nota}">
					<th class="required"> Nota Final: </th>
					<td>
						<h:inputText value="#{registroAtividade.obj.mediaFinal}" size="4" maxlength="4" id="campoNota" 
							onkeypress="return(formataValor(this, event, 1))" onblur="verificaNotaMaiorDez(this)">
							<f:converter converterId="convertNota"/>
						</h:inputText>
					</td>
				</c:if>
				<c:if test="${not registroAtividade.nota}">
					<th class="required"> Conceito: </th>
					<td>
						<h:selectOneMenu value="#{registroAtividade.obj.conceito}" id="conceito">
							<f:selectItem itemLabel="--" itemValue=""/>
							<f:selectItems value="#{conceitoNota.orderedCombo}" />
						</h:selectOneMenu>
					</td>
				</c:if>
				</tr>
			</c:if>

			<c:if test="${ !registroAtividade.obj.registroAtividade.atividade.necessitaMediaFinal }">
				<tr>
					<th> Situação: </th>
					<td style="padding: 8px;">
						<h:selectOneMenu id="idSituacaoMatricula" style="width: 180px" value="#{registroAtividade.obj.situacaoMatricula.id}">
							<f:selectItems value="#{registroAtividade.resultados}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
		</c:if>

		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton id="btnVoltar" value="<< Voltar" action="#{registroAtividade.buscarDiscente}"/>
					<h:commandButton id="btnCancelar" value="Cancelar" onclick="#{confirm}" action="#{registroAtividade.cancelar}"/>
					<h:commandButton id="btnAvancar" value="Próximo Passo >>" action="#{registroAtividade.verConfirmacao}" />
				</td>
			</tr>
		</tfoot>
	</table>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</h:form>
</f:view>

<script>
function habilitaSelecaoOrientador() {
	$('orientadorSetado').style.display = 'none';
	$('escolheOrientador').style.display = '';		
}
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
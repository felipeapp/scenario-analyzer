<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h2> <ufrn:subSistema /> &gt; Declaração de turmas ministradas</h2>
<h:form id="form">
<table align="center" class="formulario">
	<caption class="listagem">Dados da Declaração</caption>
	<tr>
		<th class="required">Docente: </th>
		<td>
			<h:inputHidden id="idServidor" value="#{declaracaoTecnico.docente.id}"/>
			<h:inputText id="nomeServidor" value="#{declaracaoTecnico.docente.pessoa.nome}" size="70" onkeyup="CAPS(this);"/>
			<ajax:autocomplete source="form:nomeServidor" target="form:idServidor"
				baseUrl="/sigaa/ajaxDocente" className="autocomplete"
				indicator="indicatorDocente" minimumCharacters="3" parameters="tipo=unidade,inativos=true"
				parser="new ResponseXmlToHtmlListParser()" />
			<span id="indicatorDocente" style="display:none; "> 
			<img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> 
			</span>
		</td>
	</tr>
	<tr>
		<th class="required">Ano-Período Inicial: </th>
		<td>
			<h:inputText id="ano" value="#{declaracaoTecnico.ano}" size="4" maxlength="4"/> -
			<h:inputText id="periodo" value="#{declaracaoTecnico.periodo}" size="1" maxlength="1"/>
		</td>
	</tr>
	<tr>
		<th class="required">Ano-Período Final: </th>
		<td>
			<h:inputText id="anoFim" value="#{declaracaoTecnico.anoFim}" size="4" maxlength="4"/> -
			<h:inputText id="periodoFim" value="#{declaracaoTecnico.periodoFim}" size="1" maxlength="1"/>
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton id="emitir" value="Gerar Relatório"
				action="#{declaracaoTecnico.emitirDeclaracao}"/> <h:commandButton
				value="Cancelar" action="#{declaracaoTecnico.cancelar}" onclick="#{confirm}" id="cancelar" />
			</td>
		</tr>
	</tfoot>
</table>
</h:form>
<br />
	<center><h:graphicImage url="/img/required.gif"
		style="vertical-align: top;" /> <span class="fontePequena">
	Campos de preenchimento obrigatório. </span></center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
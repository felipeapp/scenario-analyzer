<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<f:view>
<f:subview id="menu">
	<%@include file="/portais/docente/menu_docente.jsp"%>
</f:subview>
<h2><ufrn:subSistema /> &gt; Relatório de Produtividade do Docente </h2>

<div class="descricaoOperacao">
	<h3>Caro docente,</h3>
	<p>
		O relatório de produtividade é emitido com base no ano de referência informado. Caso possua um vínculo de <strong>chefia</strong>, e esteja
		utilizando esse vínculo no momento, você poderá visualizar o relatório de outros docentes lotados na unidade a qual chefia.
	</p>
</div>

<h:form id="form">
<h:outputText value="#{ relatorioAtividades.create }"/>
<table align="center" class="formulario" width="70%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th class="obrigatorio">Docente:</th>
		<td>
		<c:if test="${!(relatorioAtividades.acessoPrivilegiado) and !acesso.chefeDepartamento and acesso.docenteUFRN}">
			${ usuario.pessoa.nome }
		</c:if>
		<c:if test="${relatorioAtividades.acessoPrivilegiado or acesso.chefeDepartamento}">
			<h:inputHidden id="id" value="#{relatorioAtividades.docenteRelatorio.id}"></h:inputHidden>
			<h:inputText id="nome" value="#{relatorioAtividades.docenteRelatorio.pessoa.nome}" size="80" />
			<ajax:autocomplete
					source="form:nome" target="form:id"
					baseUrl="/sigaa/ajaxServidor" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=${relatorioAtividades.escopoBuscaServidores},inativos=true"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
		</c:if>
		</td>
	</tr>
	<tr>
		<th>Relatório: </th>
		<td>
			<h:selectOneMenu id="idRelatorio" value="#{relatorioAtividades.idRelatorio}" disabled="true">
				<f:selectItems value="#{relatorioAtividades.allCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th class="obrigatorio">Ano:</th>
		<td>
			 <h:inputText value="#{relatorioAtividades.anoVigencia}" size="4" maxlength="4" onkeyup="formatarInteiro(this)"/>
		 </td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
					action="#{relatorioAtividades.montarRelatorio}"/> <h:commandButton
					value="Cancelar" action="#{relatorioAtividades.cancelar}" id="cancelar" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>
</table>
</h:form>
<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
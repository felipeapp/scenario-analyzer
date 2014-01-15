<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
<!--
.center {
	text-align: center;
	border-spacing: 3px;
}

.left {
	text-align: left;
	border-spacing: 3px;
}
-->
</style>

<script type="text/javascript">
JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<script type="text/javascript">
J = jQuery.noConflict();
function viewFoto(id) {
	var linha = 'linha_'+ id;
	if ( J('#'+linha).css('display') == 'none' ) {
		if (/msie/.test( navigator.userAgent.toLowerCase() ))
			J('#'+linha).css('display', 'inline-block');
		else
			J('#'+linha).css('display', 'table-cell');			
	} else {
		J('#'+linha).css('display', 'none');		
	}
}
</script>

<f:view>
	<h2><ufrn:subSistema /> > Seleção Manual de Fiscais</h2>
	<h:form id="formBusca">
		<div class="descricaoOperacao">Selecione o Processo Seletivo e
		digite o nome do fiscal para realizar a busca. Em seguida, marque
		quais fiscais serão selecionados manualmente e clique no botão
		"Selecionar para Fiscal"</div>
		<table class="formulario">
			<caption>Escolha o Processo Seletivo</caption>
			<tbody>
				<tr>
					<th width="35%" class="obrigatorio">Processo Seletivo:</th>
					<td width="65%"><h:selectOneMenu id="processoSeletivo"
						immediate="true" value="#{selecaoManualFiscal.idProcessoSeletivo}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<th class="obrigatorio">Nome:</th>
					<td><h:inputText value="#{selecaoManualFiscal.nome}" size="30"
						maxlength="50" /></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" align="center"><h:commandButton value="Buscar"
						action="#{selecaoManualFiscal.buscar}"></h:commandButton> <h:commandButton
						value="Cancelar" onclick="#{confirm}"
						action="#{selecaoManualFiscal.cancelar}" id="cancelar" /></td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
		<br />
		<h:panelGroup id="lista">
			<div align="center"><c:if
				test="${not empty selecaoManualFiscal.listaInscricoesFiscal}">
				<div class="infoAltRem">
					<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Ver a Foto 3x4
				</div>
				<table class="listagem">
				<caption>Inscrições de Fiscais Encontradas: ${fn:length(selecaoManualFiscal.listaInscricoesFiscal)}</caption>
					<thead>
						<tr>
							<td align="center">Selecionar</td>
							<td align="center">CPF</td>
							<td align="center">Nome</td>
							<td align="center">Experiência</td>
							<td align="center">Curso/Unidade</td>
							<td></td>
						</tr>
					</thead>
					<c:forEach items="#{selecaoManualFiscal.listaInscricoesFiscal}"
						var="item" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td align="center"><h:selectBooleanCheckbox
								value="#{item.selecionado}" /></td>
							<td align="right"><ufrn:format type="cpf_cnpj"
								valor="${item.objeto.pessoa.cpf_cnpj}" /></td>
							<td align="left"><h:outputText
								value="#{item.objeto.pessoa.nome}" /></td>
							<td align="center"><h:outputText
								rendered="#{item.objeto.novato}" value="Novato" /> <h:outputText
								rendered="#{not item.objeto.novato}" value="Experiente" /></td>
							<td align="left"><h:outputText
								rendered="#{not empty item.objeto.discente}"
								value="#{item.objeto.discente.curso.descricao}" /> <h:outputText
								rendered="#{not empty item.objeto.servidor}"
								value="#{item.objeto.servidor.unidade.nome}" /></td>
							<td>
								<a href="javascript: void(0);" onclick="viewFoto(${item.objeto.id});" >
									<h:graphicImage value="/img/view.gif" style="overflow: visible;" title="Ver a Foto 3x4" />
								</a>
							</td>
						</tr>
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}"> 
							<td colspan="6" id="linha_${item.objeto.id}" style="display: none;">
								<c:if test="${not empty item.objeto.idFoto}">
									<img src="${ctx}/verFoto?idArquivo=${item.objeto.idFoto}&key=${ sf:generateArquivoKey(item.objeto.idFoto) }" style="width: 150px; height: 200px"/>
								</c:if>
							</td>				
						</tr>
					</c:forEach>
				</table>
				<table class="formulario" width="100%">
					<tfoot>
						<tr>
							<td align="center"><h:commandButton
								value="Selecionar para Fiscal"
								action="#{selecaoManualFiscal.cadastrar}"></h:commandButton> <h:commandButton
								value="Cancelar" action="#{selecaoManualFiscal.cancelar}"
								onclick="#{confirm}" immediate="true" /></td>
						</tr>
					</tfoot>
				</table>
			</c:if></div>
		</h:panelGroup>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
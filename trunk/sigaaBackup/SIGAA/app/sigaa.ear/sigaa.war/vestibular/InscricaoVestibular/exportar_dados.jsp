<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Exportar 
		<c:if test="${not exportarDadosCandidatoBean.selecionaFaixaInscricao}">Dados
		</c:if>
		<c:if test="${exportarDadosCandidatoBean.selecionaFaixaInscricao}">Fotos
		</c:if>
		 dos Candidatos</h2>

	<div class="descricaoOperacao">Este formulário permite exportar os dados das inscrições dos candidatos ao Vestibular.<br/>
	Os dados são exportados com a codificação de caracteres <b>ISO-8859-15</b>. 
	</div>
	<h:form id="form">
		<a4j:keepAlive beanName="exportarDadosCandidatoBean"></a4j:keepAlive>
		<table class="formulario" width="80%">
			<caption>Informe os Parâmetros</caption>
			<tr>
				<th class="obrigatorio">Processo Seletivo:</th>
				<td>
					<a4j:region>
					<h:selectOneMenu id="processoSeletivo"
						value="#{exportarDadosCandidatoBean.obj.processoSeletivo.id}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
						<a4j:support event="onchange" reRender="faixaInscricao" action="#{exportarDadosCandidatoBean.selecionaProcessoSeletivo}" />
					</h:selectOneMenu>
					</a4j:region>
				</td>
			</tr>
			<c:if test="${exportarDadosCandidatoBean.selecionaFaixaInscricao}">
				<tr>
					<th class="obrigatorio">Faixa de Inscrição:</th>
					<td>
						<h:selectOneMenu id="faixaInscricao"
							value="#{exportarDadosCandidatoBean.faixaInscricao}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{exportarDadosCandidatoBean.faixaInscricaoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<tr>
				<th>
					<h:selectBooleanCheckbox value="#{exportarDadosCandidatoBean.exportarSomenteValidados}" id="exportarSomenteValidados"/>
				</th>
				<td>
					Exportar somente inscrições validadas.
				</td>
			</tr>
			<c:if test="${exportarDadosCandidatoBean.exportarDadosPessoais}">
				<tr>
					<th valign="top">Formato do Arquivo:</th>
					<td>
						<h:selectOneMenu id="formatoArquivo"
							value="#{exportarDadosCandidatoBean.formatoArquivo}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{exportarDadosCandidatoBean.formatoArquivoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Exportar" action="#{exportarDadosCandidatoBean.exportar}" id="exportar"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{exportarDadosCandidatoBean.cancelar}" id="cancela" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
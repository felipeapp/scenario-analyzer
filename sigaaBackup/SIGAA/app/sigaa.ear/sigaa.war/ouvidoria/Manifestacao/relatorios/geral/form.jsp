<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="relatoriosOuvidoria" />

<f:view>
<h2><ufrn:subSistema/> > Relatórios Geral de Manifestações</h2>

	<h:form id="formulario">
		<table class="formulario" style="width:70%;">
			<caption> Informe os critérios de busca</caption>
			<tbody>
				<tr>
					<td width="10%" style="text-align: right;">
						<h:selectBooleanCheckbox value="#{relatoriosOuvidoria.checkOrigem}" styleClass="noborder" id="checkOrigem" />
					</td>
					<th style="text-align: left" nowrap="nowrap"> 
						<label for="checkOrigem"	onclick="$('formulario:checkOrigem').checked = !$('formulario:checkOrigem').checked;">
							Origem da Manifestação:</label></th>
					<td>
						<h:selectOneMenu id="selectOrigem" value="#{relatoriosOuvidoria.origemManifestacao.id }" onchange="$('formulario:checkOrigem').checked = true">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
							<f:selectItems value="#{origemManifestacao.allOrigensCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td style="text-align: right;">
						<h:selectBooleanCheckbox value="#{relatoriosOuvidoria.checkCategoriaAssunto}" styleClass="noborder" id="checkCategoriaAssunto" />
					</td>
					<th nowrap="nowrap" style="text-align: left"> 
						<h:outputText>Categoria do Assunto:</h:outputText>
					</th>
					<td>
						<h:selectOneMenu id="selectCategoria" value="#{relatoriosOuvidoria.categoriaAssuntoManifestacao.id }" onchange="$('formulario:checkCategoriaAssunto').checked = true; submit()">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
							<f:selectItems value="#{categoriaAssuntoManifestacao.allCategoriasAtivasCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td style="text-align: right;">
						<h:selectBooleanCheckbox value="#{relatoriosOuvidoria.checkAssunto}" styleClass="noborder" id="checkAssunto" />
					</td>
					<th nowrap="nowrap" style="text-align: left"> 
						<h:outputText>Assunto da Manifestação:</h:outputText>
					</th>
					<td>
						<h:selectOneMenu id="selectAssunto" value="#{relatoriosOuvidoria.assuntoManifestacao.id }" onchange="$('formulario:checkAssunto').checked = true">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
							<f:selectItems value="#{relatoriosOuvidoria.allAssuntosByCategoriaCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td style="text-align: right;">
						<h:selectBooleanCheckbox value="#{relatoriosOuvidoria.checkCategoriaSolicitante}" styleClass="noborder" id="checkCategoriaSolicitante" />
					</td>
					<th nowrap="nowrap" style="text-align: left"> 
						<h:outputText>Categoria do Solicitante:</h:outputText>
					</th>
					<td>
						<h:selectOneMenu value="#{relatoriosOuvidoria.categoriaSolicitante.id }" onchange="$('formulario:checkCategoriaSolicitante').checked = true">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
							<f:selectItems value="#{categoriaSolicitante.allCategoriasCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td style="text-align: right;">
						<h:selectBooleanCheckbox value="#{relatoriosOuvidoria.checkStatusManifestacao}" styleClass="noborder" id="checkStatusManifestacao" />
					</td>
					<th nowrap="nowrap" style="text-align: left"> 
						<h:outputText>Status da Manifestação:</h:outputText>
					</th>
					<td>
						<h:selectOneMenu value="#{relatoriosOuvidoria.statusManifestacao.id }" onchange="$('formulario:checkStatusManifestacao').checked = true">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
							<f:selectItems value="#{statusManifestacao.allStatusManifestacaoCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td style="text-align: right;">
						<h:selectBooleanCheckbox value="#{relatoriosOuvidoria.checkUnidadeResponsavel}" styleClass="noborder" id="checkUnidadeResponsavel" />
					</td>
					<th nowrap="nowrap" style="text-align: left"> 
						<h:outputText>Unidade Responsável:</h:outputText>
					</th>
					<td>
						<h:selectOneMenu value="#{relatoriosOuvidoria.unidade.id }" onchange="$('formulario:checkUnidadeResponsavel').checked = true">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
							<f:selectItems value="#{relatoriosOuvidoria.allUnidadesComManifestacoesCombo }"/>
						</h:selectOneMenu>
						<ufrn:help img="/img/ajuda.gif">Apenas unidades que já tiveram manifestações encaminhadas serão listadas aqui.</ufrn:help>
					</td>
				</tr>
				<tr>
					<td style="text-align: right;">
						<h:selectBooleanCheckbox value="#{relatoriosOuvidoria.checkPeriodo}" styleClass="noborder" id="checkPeriodo" />
					</td>
					<th style="text-align: left" width="130px"> 
						<h:outputText>Período de Cadastro:</h:outputText>
					</th>
					<td> 
						De: <t:inputCalendar value="#{relatoriosOuvidoria.dataInicial }" renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return formataData(this,event)"
									popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é" id="Data_Inicial" size="10" maxlength="10" onchange="$('formulario:checkPeriodo').checked = true" />
						a: <t:inputCalendar value="#{relatoriosOuvidoria.dataFinal }" renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return formataData(this,event)"
									popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é" id="Data_Final" size="10" maxlength="10" onchange="$('formulario:checkPeriodo').checked = true" />
					</td>
				</tr>
				<tr>
					<td></td>
					<td colspan="2">
						<h:selectOneRadio value="#{relatoriosOuvidoria.somenteNaoRespondidas }">
							<f:selectItem itemLabel="Todas as Manifestações" itemValue="false" />
							<f:selectItem itemLabel="Somente Manifestações Não Respondidas" itemValue="true" />
						</h:selectOneRadio>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton action="#{relatoriosOuvidoria.gerarRelatorioGeralManifestacoes }" value="Gerar Relatório" id="buscar" />
						<h:commandButton action="#{relatoriosOuvidoria.cancelar }" value="Cancelar" id="cancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
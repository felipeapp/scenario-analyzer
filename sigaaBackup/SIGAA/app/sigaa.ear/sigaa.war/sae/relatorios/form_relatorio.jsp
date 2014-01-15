<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2>
		<ufrn:subSistema/> > Relatórios de Bolsa Auxílio
	</h2>

	<div class="descricaoOperacao">
		<p>Caro Usuário,</p>
		<p>Esta tela permite gerar um relatório de acordo com as opções de filtro abaixo.</p>
	</div>

	<h:form id="form">
		<table class="formulario" style="width: 95%">
			<caption>Informe os critérios para a emissão do relatório</caption>

			<tr>
				<th class="required">Ano.Período: </th>
				<td><h:inputText id="ano" value="#{relatoriosSaeMBean.ano}"
					size="5" maxlength="4" onkeyup="return formatarInteiro(this);" />.<h:inputText
					id="semestre" value="#{relatoriosSaeMBean.periodo}" size="2"
					maxlength="1" onkeyup="return formatarInteiro(this);" /></td>
			</tr>

			<c:if test="${!relatoriosSaeMBean.relatorioContempladosDeferidos}">
			<tr>
				<th class="required">Formato do Relatório:</th>
				<td>
					<h:selectOneRadio value="#{relatoriosSaeMBean.formato}">
						<f:selectItem itemValue="pdf" itemLabel="PDF" />
						<f:selectItem itemValue="xls" itemLabel="XLS (Excel)" />
						<f:selectItem itemValue="html" itemLabel="HTML" />
					</h:selectOneRadio>
				</td>
			</tr>
			</c:if>
			
			<c:if test="${relatoriosSaeMBean.relatorioTodosAlunosAlimentacao == true}">
				<tr>
					<th>Centro: </th>
					<td>
						<h:selectOneMenu id="centro" value="#{relatoriosSaeMBean.idCentro}" style="width: 500px">
							<f:selectItem itemValue="0" itemLabel=">> SELECIONE UM CENTRO <<"  />
							<f:selectItems value="#{unidade.allCentroCombo}"/>
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			
			<c:if test="${relatoriosSaeMBean.relatorioBairro == true}">
			<tr>
				<th>UF: </th>
				<td>
					<h:selectOneMenu value="#{relatoriosSaeMBean.idUF}" id="ufEnd" 
							valueChangeListener="#{relatoriosSaeMBean.carregarMunicipios }">
							<f:selectItems value="#{unidadeFederativa.allCombo}" />
							<a4j:support event="onchange" reRender="endMunicipio" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th>Município: </th>
				<td>
					<h:selectOneMenu value="#{relatoriosSaeMBean.idMunicipio}" id="endMunicipio">
						<f:selectItems value="#{relatoriosSaeMBean.municipiosEndereco}" />
					</h:selectOneMenu>
				</td>
			</tr>
			</c:if>
			
			<tfoot>
				<tr>
					
					<c:if test="${relatoriosSaeMBean.relatorioTodosAlunosAlimentacao == true}">
						<td colspan="2">
							<h:commandButton action="#{relatoriosSaeMBean.gerarRelatorioTodosAlunosComBolsa}" value="Emitir Relatório" />
							<h:commandButton action="#{relatoriosSaeMBean.cancelar}" value="Cancelar" onclick="#{confirm}" />
						</td>
					</c:if>
					
					<c:if test="${relatoriosSaeMBean.relatorioContempladosDeferidos == true}">
						<td colspan="2">
							<h:commandButton action="#{relatoriosSaeMBean.gerarRelatorioContempladosDeferidos}" value="Emitir Relatório" />
							<h:commandButton action="#{relatoriosSaeMBean.cancelar}" value="Cancelar" onclick="#{confirm}" />
						</td>
					</c:if>

					<c:if test="${relatoriosSaeMBean.relatorioEscolaridadePai == true}">
						<td colspan="2">
							<h:commandButton action="#{relatoriosSaeMBean.gerarRelatorioEscolaridadePai}" value="Emitir Relatório" />
							<h:commandButton action="#{relatoriosSaeMBean.cancelar}" value="Cancelar" onclick="#{confirm}" /> 
						</td>
					</c:if>
					
					<c:if test="${relatoriosSaeMBean.relatorioBairro == true}">
						<td colspan="2">
							<h:commandButton action="#{relatoriosSaeMBean.gerarRelatorioBairro}" value="Emitir Relatório" />
							<h:commandButton action="#{relatoriosSaeMBean.cancelar}" value="Cancelar" onclick="#{confirm}" /> 
						</td>
					</c:if>
					
					<c:if test="${relatoriosSaeMBean.relatorioProfissao == true}">
						<td colspan="2">
							<h:commandButton action="#{relatoriosSaeMBean.gerarRelatorioProfissaoPai}" value="Emitir Relatório" />
							<h:commandButton action="#{relatoriosSaeMBean.cancelar}" value="Cancelar" onclick="#{confirm}" /> 
						</td>
					</c:if>
					
					<c:if test="${relatoriosSaeMBean.relatorioAlimentacaoResidencia == true}">
						<td colspan="2">
							<h:commandButton action="#{relatoriosSaeMBean.gerarRelatorioAlimentacaoResidencia}" value="Emitir Relatório" />
							<h:commandButton action="#{relatoriosSaeMBean.cancelar}" value="Cancelar" onclick="#{confirm}" /> 
						</td>
					</c:if>

				</tr>
			</tfoot>

		</table>
	</h:form>
	
	<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
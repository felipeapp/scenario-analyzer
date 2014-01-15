<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Exporta��o de Dados da Avalia��o Institucional</h2>

	<h:form id="form">
		<a4j:keepAlive beanName="exportaAvaliacaoInstitucional"></a4j:keepAlive>
		<table align="center" class="formulario" width="80%">
			<caption>Informe os Par�metros para Exportar os Dados</caption>
			<tr>
				<th width="20%" class="obrigatorio">Formul�rio:</th>
				<td>
					<h:selectOneMenu id="anoPeriodo" value="#{exportaAvaliacaoInstitucional.calendario.id}" onchange="submit();" 
						valueChangeListener="#{exportaAvaliacaoInstitucional.formularioListener}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{exportaAvaliacaoInstitucional.calendariosAvaliacaoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<c:if test="${exportaAvaliacaoInstitucional.exportarDiscente}">
				<tr>
					<th>
						<h:selectBooleanCheckbox value="#{exportaAvaliacaoInstitucional.somenteDadosFiltrados}" id="exportarDadosFiltrados"/>
					</th>
					<td>
						Exportar Somente os Dados N�o Processados
						<ufrn:help>Se marcado, ser�o exibidos somente os dados que foram exclu�dos do processamento das m�dias, segundo pelos atributos abaixo informados.</ufrn:help>
					</td>
				</tr>
				<c:if test="${exportaAvaliacaoInstitucional.obj.id != 0}">
					<tr>
						<td colspan="2" class="subFormulario">Detalhes do Processamento:</td>
					</tr>
					<tr>
						<td colspan="2">
							<table class="listagem" width="100%">
								<thead></thead>
								<tbody>
								<tr>
									<th><b>Data do Processamento:</b></th>
									<td><ufrn:format type="data" valor="${exportaAvaliacaoInstitucional.obj.fimProcessamento}" /></td>
								</tr>
								<tr>
									<th><b>Consulta do resultado para docentes:</b></th>
									<td><ufrn:format type="simnao" valor="${exportaAvaliacaoInstitucional.obj.consultaDocenteLiberada}" /> Liberada</td>
								</tr>
								<tr>
									<th><b>N�mero M�nimo de Avalia��es por Docente:</b></th>
									<td>${exportaAvaliacaoInstitucional.obj.numMinAvaliacoes}</td>
								</tr>
								<tr>
									<th><b>Excluir Avalia��es de Docentes com Reprova��es por Falta:</th>
									<td><ufrn:format type="simnao" valor="${exportaAvaliacaoInstitucional.obj.excluirRepovacoesFalta}" /></td>
								</tr>
								<tr>
									<th><b>Perguntas que Determinam se a Avalia��o � V�lida:</b></th>
									<td>
										<c:forEach items="#{exportaAvaliacaoInstitucional.obj.perguntaDeterminanteExclusaoAvaliacao }" var="pergunta" varStatus="status">
											${pergunta.grupo.titulo } - ${pergunta.descricao}<br/>
										</c:forEach>
									</td>
								</tr>
								</tbody>
							</table>
						</td>
					</tr>
				</c:if>
			</c:if>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Exportar Dados" action="#{exportaAvaliacaoInstitucional.exportarDados}" id="exportarDados"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{exportaAvaliacaoInstitucional.cancelar}" id="cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

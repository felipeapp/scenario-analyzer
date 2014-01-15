<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="relatorioTurma"></a4j:keepAlive>
	<h:messages showDetail="true"></h:messages>
	<h2> <ufrn:subSistema/> > Relatório de Ocupação de Vagas de Turmas</h2>
	<h:form id="form">
	<h:inputHidden value="#{relatorioTurma.nivel}"/>
	<h:outputText value="#{relatorioTurma.create}" />
		<table class="formulario">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				<tr>
					<th class="obrigatorio">Ano-Período:</th>
					<td>
						<h:inputText value="#{relatorioTurma.ano}" size="4" maxlength="4" id="ano" onkeyup="formatarInteiro(this)"/>
						- <h:inputText value="#{relatorioTurma.periodo}" size="1" maxlength="1" id="periodo" onkeyup="formatarInteiro(this)"/>						
					</td>
				</tr>
				<c:if test="${acesso.ppg || acesso.dae}">
					<tr>
						<th> <label>Departamento:</label> </th>
						<td>
							<h:selectOneMenu id="unidades" value="#{relatorioTurma.departamento.id}">
								<f:selectItem itemValue="0" itemLabel="--TODOS--" />
								<c:if test="${acesso.ppg}">
									<f:selectItems value="#{unidade.allProgramaPosCombo}" />
								</c:if>
								<c:if test="${acesso.dae}">
									<f:selectItems value="#{unidade.allDeptosEscolasCoordCursosCombo}" />
								</c:if> 
							</h:selectOneMenu>
						</td>
					</tr>
				</c:if>
				<tr>
					<th>Situação:</th>
					<td>
						<h:selectOneMenu id="situacao" value="#{relatorioTurma.situacaoTurma.id}">
							<f:selectItem itemValue="0" itemLabel="TODOS" />
							<f:selectItems value="#{situacaoTurma.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{relatorioTurma.gerarRelatorioOcupacaoVagas}" id="btnGerar"/> 
						<h:commandButton value="Cancelar" action="#{relatorioTurma.cancelar}" id="btnCancelar"/>
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
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
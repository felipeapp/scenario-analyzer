<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<a4j:keepAlive beanName="relatoriosCoordenador" />

<f:view>
	<div class="descricaoOperacao">
		<p>Caro(a) Usuário(a),</p>
		<p>Este relatório tem como finalidade listar todos os componentes de um currículo, 
			mostrando suas expressões de equivalência e componentes equivalentes.</p>
	</div>

	<h:outputText value="#{ relatoriosCoordenador.create }" />
	<h2> <ufrn:subSistema /> &gt; ${relatoriosCoordenador.tituloRelatorio } </h2>
		<h:form id="anoPeriodoForm">
			<h:inputHidden value="#{relatoriosCoordenador.tipoRelatorio}"/>
			<table align="center" class="formulario" width="50%" >
				<caption class="listagem">Dados do Relatório</caption>
				<tbody>
				<c:if test="${relatoriosCoordenador.exibeCurso}">
					<a4j:region>
						<tr>
							<th class="required">Curso:</th>
							<td>
								<c:if test="${(acesso.coordenadorCursoGrad || acesso.secretarioGraduacao) }">
									${relatoriosCoordenador.cursoAtualCoordenacao.nomeCompleto} 
								</c:if>
								<c:if test="${acesso.dae or acesso.secretarioCentro}">
									<h:selectOneMenu id="curso" value="#{relatoriosCoordenador.curso.id }" immediate="true" onchange="submit()" valueChangeListener="#{relatoriosCoordenador.carregarMatrizes}">
										<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
										<f:selectItems value="#{relatoriosCoordenador.cursos}" />
									</h:selectOneMenu>
								</c:if>
							</td>
						</tr>
					</a4j:region>
					<a4j:region>
						<tr>
							<th nowrap="nowrap" class="required">Matriz Curricular:</th>
							<td>
								<h:selectOneMenu id="matrizCurricular" value="#{relatoriosCoordenador.matriz.id }" immediate="true" onchange="submit()" valueChangeListener="#{relatoriosCoordenador.carregarCurriculos}">
									<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
									<f:selectItems value="#{relatoriosCoordenador.matrizesCurriculares}" />
								</h:selectOneMenu>
							</td>
						</tr>
					</a4j:region>
					<tr>
						<th class="required">Currículo:</th>
						<td>
							<h:selectOneMenu id="curriculo" value="#{relatoriosCoordenador.curriculo.id }" immediate="true" onchange="submit()">
								<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
								<f:selectItems value="#{relatoriosCoordenador.curriculos}" />
							</h:selectOneMenu>
						</td>
					</tr>
				</c:if>
				</tbody>
				<tfoot>
				<tr>
					<td colspan="3" align="center">
						<h:commandButton value="Gerar Relatório" action="#{relatoriosCoordenador.gerarRelatorioEquivalencias}" id="gerarRelatorio" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{relatoriosCoordenador.cancelar}" id="cancelarRelatorio" />
					</td>
				</tr>
				</tfoot>
			</table>
			<br>
			<center><html:img page="/img/required.gif" style="vertical-align: top;" /> 
				<span class="fontePequena">	Campos de preenchimento obrigatório. </span> 
			</center>
		</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
<!--
.left {
	text-align: left;
	border-spacing: 3px;
}

.center {
	text-align: center;
	border-spacing: 3px;
}
-->
</style>
<f:view>
	<h2><ufrn:subSistema /> &gt; Convocação de Candidatos para Vagas Remanescentes &gt; Quadro de Vagas Remanescentes</h2>
	<div class="descricaoOperacao">
	<p>Caro Usuário,</p>
	<p>No quadro abaixo é exibida quantidade candidatos serão convocados para cada matriz curricular, podendo ser alterado conforme a necessidade.</p>
	</div>
	<h:form id="formulario">
		<table class="formulario" width="100%">
			<caption>Informe as Quantidades de Candidatos a Convocar</caption>
			<tbody>
				<tr>
					<th width="30%" class="rotulo">Processo Seletivo:</th>
					<td><h:outputText id="psVest" value="#{convocacaoVagasRemanescentesVestibularMBean.obj.processoSeletivo.nome}"/></td>
				</tr>
				<tr>
					<th class="rotulo"> Estratégia de convocação:</th>
					<td>
						<h:outputText value="#{convocacaoVagasRemanescentesVestibularMBean.obj.processoSeletivo.estrategiaConvocacao.class.simpleName}"/>
						<h:outputText value="Não definido" rendered="#{ empty convocacaoVagasRemanescentesVestibularMBean.obj.processoSeletivo.estrategiaConvocacao.class.simpleName}"/>
					</td>
				</tr>
				<tr>
					<th class="rotulo">Descrição:</th>
					<td><h:outputText id="descricao" value="#{convocacaoVagasRemanescentesVestibularMBean.obj.descricao}"/></td>
				</tr>
				<c:if test="${ convocacaoVagasRemanescentesVestibularMBean.obj.processoSeletivo.entradaDoisPeriodos }">
					<tr>
						<th class="rotulo">Semestre a Convocar:</th>
						<td>
							<h:outputText value="#{ convocacaoVagasRemanescentesVestibularMBean.obj.semestreConvocacao }"/>
						</td>
					</tr>
				</c:if>
				<tr>
					<th class="rotulo">Data da Convocação:</th>
					<td>
						<h:outputText value="#{convocacaoVagasRemanescentesVestibularMBean.obj.dataConvocacao}"/> 
					</td>
				</tr>
				<tr>
					<th class="rotulo">Percentual Adicional de Vagas:</th>
					<td>
						<h:outputText value="#{convocacaoVagasRemanescentesVestibularMBean.obj.percentualAdicionalVagas}%" /> 
					</td>
				</tr>
				<tr>
					<td colspan="2" class="subFormulario">Matrizes Curriculares com Vagas Remanescentes
					</td>
				</tr>
				<tr>
					<td colspan="2" style="text-align: center;">
					<table class="formulario" width="100%">
						<thead>
							<tr>
								<th rowspan="2" style="text-align: left;">Curso / Matriz Curricular</th>
								<c:if test="${ convocacaoVagasRemanescentesVestibularMBean.distancia }">
									<th rowspan="2">Pólo</th>
								</c:if>
								<th rowspan="2" style="text-align: left;">Vagas<br/>Ofertadas</th>
								<th rowspan="2" style="text-align: center;" width="7%">Quant. a<br/>Convocar</th>
								<th colspan="${ fn:length(convocacaoVagasRemanescentesVestibularMBean.gruposCotas) }" 
									style="text-align: center;">Quantidade por Grupo de Cotas
								</th>
							</tr>
							<tr>
								<c:forEach items="#{ convocacaoVagasRemanescentesVestibularMBean.gruposCotas }" var="grupo">
									<th style="text-align: center;" width="7%">
										<h:outputText value="#{ grupo.descricao }" title="#{ grupo }" />
									</th>
								</c:forEach>
							</tr>
						</thead>
						<tbody>
							<c:set var="_municipio" value="" />
							<c:set var="parImpar" value="0" />
							<c:forEach items="#{ convocacaoVagasRemanescentesVestibularMBean.listaOfertaVagasCurso }" var="item" varStatus="status">
								<c:if test="${ item.curso.municipio.id != _municipio }">
									<tr>
										<td colspan="${ fn:length(convocacaoVagasRemanescentesVestibularMBean.gruposCotas) + (convocacaoVagasRemanescentesVestibularMBean.distancia ? 5 : 4)}" class="subFormulario">
									 		${ item.curso.municipio.nome } 
								 		</td>
							 		</tr>
							 		<c:set var="parImpar" value="0" />
									<c:set var="_municipio" value="${ item.curso.municipio.id }" />
								</c:if>
								<tr class="${parImpar % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td style="text-align: left;">
										${ item.matrizCurricular.curso.nome }
										<h:outputText value=" - #{ item.matrizCurricular.habilitacao.nome}" rendered="#{ not empty item.matrizCurricular.habilitacao.nome }" />
										<h:outputText value=" - #{ item.matrizCurricular.enfase.nome}" rendered="#{ not empty item.matrizCurricular.enfase.nome }" />
										<h:outputText value=" - #{ item.matrizCurricular.grauAcademico.descricao}" />
										<h:outputText value=" - #{ item.matrizCurricular.turno.sigla}" />
									</td>
									<c:if test="${ convocacaoVagasRemanescentesVestibularMBean.distancia }">
										<td>
											${ item.polo.descricao }
										</td>
									</c:if>
									<td style="text-align: right;">
										<h:outputText  value="#{item.totalVagas}"/>
									</td>
									<td style="text-align: right;">
										<h:inputText size="3" value="#{item.totalVagasOciosas}"
											onkeyup="return formatarInteiro(this)" maxlength="3"
											converter="#{intConverter}"  style="text-align:right;"
											required="true" 
											title="Quantidade de Candidatos a Convocar">
										</h:inputText>
									</td>
									<!-- o forEach aninhado abaixo garante a mesma ordem do cabeçalho do formulário -->
									<c:forEach items="#{ convocacaoVagasRemanescentesVestibularMBean.gruposCotas }" var="grupo">
										<c:set var="temCota" value="false" />
										<c:forEach items="#{ item.cotas }" var="cota">
											<c:if test="${ grupo.id == cota.grupoCota.id}">
												<td style="text-align: right;">
													<h:inputText size="3" value="#{cota.totalVagasOciosas}"
														onkeyup="return formatarInteiro(this)" maxlength="3"
														converter="#{intConverter}"  style="text-align:right;"
														title="#{ grupo.descricao }">
													</h:inputText>
												</td>
												<c:set var="temCota" value="true" />
											</c:if>
										</c:forEach>
										<c:if test="${ !temCota }">
											<td style="text-align: right;">0</td>
										</c:if>
									</c:forEach>
								</tr>
								<c:set var="parImpar" value="${ parImpar + 1 }" />
							</c:forEach>
						</tbody>
					</table>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton id="voltar" value="<< Voltar" action="#{convocacaoVagasRemanescentesVestibularMBean.telaFormulario}" />
					<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}" action="#{convocacaoVagasRemanescentesVestibularMBean.cancelar}" />
					<a4j:commandButton id="btnBuscar" value="Próximo Passo >>" action="#{convocacaoVagasRemanescentesVestibularMBean.buscarConvocacoes}"
					onclick="this.disabled=true; this.value='Aguarde. Isto poderá demorar alguns minutos...';$('formulario:indicador').style.visibility = 'visible';"/>
					<h:graphicImage value="/img/indicator.gif" id="indicador" style="visibility:hidden;"/>
				</td>
			</tr>
		</tfoot>
		</table>
		<br/>
		<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena">
		Campos de preenchimento obrigatório. </span> <br>
		<br>
		</center>

	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>